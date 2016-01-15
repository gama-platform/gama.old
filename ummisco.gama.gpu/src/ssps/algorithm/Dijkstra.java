/*********************************************************************************************
 *
 *
 * 'Dijkstra.java', in plugin 'ummisco.gama.gpu', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ssps.algorithm;

import static org.jocl.CL.*;
// import com.sun.org.apache.xpath.internal.operations.Bool;
import java.io.*;
import java.net.URL;
import org.jocl.*;
import ssps.graph.Graph;

/**
 *
 * @author mrkhai
 */
public class Dijkstra {

	int V[];
	int E[];
	float W[];
	int M[];
	float D[];
	int P[];
	int V_MAX;
	int E_MAX;
	cl_program program;
	cl_context context;
	cl_device_id device;
	String source;

	public Dijkstra() {
		initGPUFile();
	}

	public Dijkstra(final Graph G) {
		this.V = G.getVertex_list();
		this.E = G.getEdge_list();
		this.W = G.getWeight_list();
		V_MAX = V.length;
		E_MAX = E.length;
		M = new int[V_MAX];
		D = new float[V_MAX];
		P = new int[V_MAX];
		initGPUFile();
	}

	public void init(final Graph G) {
		this.V = G.getVertex_list();
		this.E = G.getEdge_list();
		this.W = G.getWeight_list();
		V_MAX = V.length;
		E_MAX = E.length;
		M = new int[V_MAX];
		D = new float[V_MAX];
		P = new int[V_MAX];
	}

	public Dijkstra(final int[] V, final int[] E, final float[] W) {
		this.V = V;
		this.E = E;
		this.W = W;
		V_MAX = V.length;
		E_MAX = E.length;
		M = new int[V_MAX];
		D = new float[V_MAX];
		P = new int[V_MAX];
		initGPUFile();
	}

	public void initDijkstraCPU(final int source) {
		for ( int i = 0; i < V_MAX; i++ ) {
			M[i] = 0;
			D[i] = Float.POSITIVE_INFINITY;
			P[i] = -1;
		}
		M[source] = 1;
		D[source] = 0;
		P[source] = source;
		int start = V[source];
		int end;
		if ( source + 1 < V_MAX ) {
			end = V[source + 1];
		} else {
			end = E_MAX;
		}
		for ( int j = start; j < end; j++ ) {
			int p = E[j];
			D[p] = W[j];
			P[p] = source;
		}
	}

	public void initDijkstraGPU(final int source, final float[] C, final float[] U) {
		for ( int i = 0; i < V_MAX; i++ ) {
			M[i] = 0;
			C[i] = Float.POSITIVE_INFINITY;
			U[i] = Float.POSITIVE_INFINITY;
			P[i] = -1;
		}
		M[source] = 1;
		C[source] = 0;
		U[source] = 0;
		P[source] = source;
	}

	private boolean isEnd(final int N, final int[] M) {
		for ( int i = 0; i < N; i++ ) {
			if ( M[i] == 0 ) { return false; }
		}
		return true;
	}

	private boolean isEmpty(final int N, final int[] M) {
		int i = 0;
		while (i < N) {
			if ( M[i] == 1 ) { return false; }
			i++;
		}
		return true;
	}

	private int searchMinNonMask(final float[] D, final int[] M) {
		int min_index = -1;
		float min = Float.MAX_VALUE;
		for ( int i = 0; i < V_MAX; i++ ) {
			if ( min > D[i] && M[i] == 0 ) {
				min_index = i;
				min = D[i];
			}
		}
		return min_index;
	}

	/**
	 * @param graph
	 * @param source_index
	 */
	public int[] searchSPCPU(final Graph graph, final int source_index) {
		V_MAX = graph.getVertex_count();
		E_MAX = graph.getEdge_count();
		initDijkstraCPU(source_index);

		while (!isEnd(V_MAX, M)) {
			int u = searchMinNonMask(D, M);
			if ( u == -1 ) {
				break;
			}
			M[u] = 1;
			int start = V[u];
			int end;
			if ( u + 1 < V_MAX ) {
				end = V[u + 1];
			} else {
				end = E_MAX;
			}
			for ( int j = start; j < end; j++ ) {
				float temp = D[u] + W[j];
				int v = E[j];
				if ( D[v] > temp ) {
					D[v] = temp;
					// M[v] = 0;
					P[v] = u;
				}
			}
		}
		return P;
	}

	public void initGPUFile() {
		source = "";
		try {
			URL url = new URL("platform:/plugin/ummisco.gama.gpu/kernel/Kernel.cl");
			source = readFile(url.openConnection().getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initGPU() {
		// The platform, device type and device number
		// that will be used
		final int platformIndex = 0;
		final long deviceType = CL_DEVICE_TYPE_ALL;
		// final int deviceIndex = 0;

		// Enable exceptions and subsequently omit error checks in this sample
		CL.setExceptionsEnabled(true);

		// Obtain the number of platforms
		int numPlatformsArray[] = new int[1];
		clGetPlatformIDs(0, null, numPlatformsArray);
		int numPlatforms = numPlatformsArray[0];

		// Obtain a platform ID
		cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
		clGetPlatformIDs(platforms.length, platforms, null);
		cl_platform_id platform = platforms[platformIndex];

		// Initialize the context properties
		cl_context_properties contextProperties = new cl_context_properties();
		contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

		// Obtain the number of devices for the platform
		int numDevicesArray[] = new int[1];
		clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
		int numDevices = numDevicesArray[0];

		// Obtain a device ID
		cl_device_id devices[] = new cl_device_id[numDevices];
		clGetDeviceIDs(platform, deviceType, numDevices, devices, null);

		device = devices[1];// deviceIndex];
		// Create a context for the selected device
		context = clCreateContext(contextProperties, 1, new cl_device_id[] { device }, null, null, null);

		// Create the program from the source code

		program = clCreateProgramWithSource(context, 1, new String[] { source }, null, null);

		// Build the program
		clBuildProgram(program, 0, null, null, null, null);

	}

	/**
	 * @param graph
	 * @param source_index
	 */
	public int[] searchSPGPU(final Graph graph, final int source_index) {
		initGPU();
		V_MAX = graph.getVertex_count();
		E_MAX = graph.getEdge_count();

		float[] CPtr = new float[V_MAX];
		float[] UPtr = new float[V_MAX];

		Pointer pV = Pointer.to(V);
		Pointer pE = Pointer.to(E);
		Pointer pW = Pointer.to(W);
		Pointer pM = Pointer.to(M);
		Pointer pP = Pointer.to(P);
		Pointer pC = Pointer.to(CPtr);
		Pointer pU = Pointer.to(UPtr);

		// Create a command-queue for the selected device
		cl_command_queue commandQueue = clCreateCommandQueue(context, device, 0, null);

		// Allocate the memory objects for the input- and output data
		cl_mem memObjects[] = new cl_mem[7];
		memObjects[0] =
			clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * V_MAX, pV, null);

		memObjects[1] =
			clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * E_MAX, pE, null);

		memObjects[2] =
			clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * E_MAX, pW, null);

		memObjects[3] =
			clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * V_MAX, pM, null);

		memObjects[4] =
			clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * V_MAX, pP, null);

		memObjects[5] =
			clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * V_MAX, pC, null);

		memObjects[6] =
			clCreateBuffer(context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * V_MAX, pU, null);

		// Create the kernel1
		cl_kernel kernel1 = clCreateKernel(program, "OCL_SSSP_KERNEL1", null);
		clSetKernelArg(kernel1, 0, Sizeof.cl_mem, Pointer.to(memObjects[0]));
		clSetKernelArg(kernel1, 1, Sizeof.cl_mem, Pointer.to(memObjects[1]));
		clSetKernelArg(kernel1, 2, Sizeof.cl_mem, Pointer.to(memObjects[2]));
		clSetKernelArg(kernel1, 3, Sizeof.cl_mem, Pointer.to(memObjects[3]));
		clSetKernelArg(kernel1, 4, Sizeof.cl_mem, Pointer.to(memObjects[4]));
		clSetKernelArg(kernel1, 5, Sizeof.cl_mem, Pointer.to(memObjects[5]));
		clSetKernelArg(kernel1, 6, Sizeof.cl_mem, Pointer.to(memObjects[6]));
		clSetKernelArg(kernel1, 7, Sizeof.cl_int, Pointer.to(new int[] { V_MAX }));
		clSetKernelArg(kernel1, 8, Sizeof.cl_int, Pointer.to(new int[] { E_MAX }));

		// Create the kernel2
		cl_kernel kernel2 = clCreateKernel(program, "OCL_SSSP_KERNEL2", null);
		clSetKernelArg(kernel2, 0, Sizeof.cl_mem, Pointer.to(memObjects[0]));
		clSetKernelArg(kernel2, 1, Sizeof.cl_mem, Pointer.to(memObjects[1]));
		clSetKernelArg(kernel2, 2, Sizeof.cl_mem, Pointer.to(memObjects[2]));
		clSetKernelArg(kernel2, 3, Sizeof.cl_mem, Pointer.to(memObjects[3]));
		clSetKernelArg(kernel2, 4, Sizeof.cl_mem, Pointer.to(memObjects[5]));
		clSetKernelArg(kernel2, 5, Sizeof.cl_mem, Pointer.to(memObjects[6]));
		clSetKernelArg(kernel2, 6, Sizeof.cl_int, Pointer.to(new int[] { V_MAX }));

		// Create the kernel3
		cl_kernel kernel3 = clCreateKernel(program, "OCL_SSSP_INIT", null);
		clSetKernelArg(kernel3, 0, Sizeof.cl_mem, Pointer.to(memObjects[0]));
		clSetKernelArg(kernel3, 1, Sizeof.cl_mem, Pointer.to(memObjects[1]));
		clSetKernelArg(kernel3, 2, Sizeof.cl_mem, Pointer.to(memObjects[2]));
		clSetKernelArg(kernel3, 3, Sizeof.cl_mem, Pointer.to(memObjects[3]));
		clSetKernelArg(kernel3, 4, Sizeof.cl_mem, Pointer.to(memObjects[4]));
		clSetKernelArg(kernel3, 5, Sizeof.cl_mem, Pointer.to(memObjects[5]));
		clSetKernelArg(kernel3, 6, Sizeof.cl_mem, Pointer.to(memObjects[6]));
		clSetKernelArg(kernel3, 7, Sizeof.cl_int, Pointer.to(new int[] { V_MAX }));
		clSetKernelArg(kernel3, 8, Sizeof.cl_int, Pointer.to(new int[] { source_index }));

		// Set the work-item dimensions
		long global_work_size[] = new long[] { E_MAX };
		long local_work_size[] = new long[] { 1 };

		// Execute the kerne3
		clEnqueueNDRangeKernel(commandQueue, kernel3, 1, null, global_work_size, local_work_size, 0, null, null);
		// Read the output data
		clEnqueueReadBuffer(commandQueue, memObjects[3], CL_TRUE, 0, V_MAX * Sizeof.cl_int, pM, 0, null, null);
		clEnqueueReadBuffer(commandQueue, memObjects[4], CL_TRUE, 0, V_MAX * Sizeof.cl_int, pP, 0, null, null);
		clEnqueueReadBuffer(commandQueue, memObjects[5], CL_TRUE, 0, V_MAX * Sizeof.cl_float, pC, 0, null, null);
		clEnqueueReadBuffer(commandQueue, memObjects[6], CL_TRUE, 0, V_MAX * Sizeof.cl_float, pU, 0, null, null);

		while (!isEmpty(V_MAX, M)) {
			// Execute the kernel
			clEnqueueNDRangeKernel(commandQueue, kernel1, 1, null, global_work_size, local_work_size, 0, null, null);

			// Execute the kernel
			clEnqueueNDRangeKernel(commandQueue, kernel2, 1, null, global_work_size, local_work_size, 0, null, null);
			// Read the output data
			clEnqueueReadBuffer(commandQueue, memObjects[3], CL_TRUE, 0, V_MAX * Sizeof.cl_float, pM, 0, null, null);
			clEnqueueReadBuffer(commandQueue, memObjects[4], CL_TRUE, 0, V_MAX * Sizeof.cl_float, pP, 0, null, null);
			clEnqueueReadBuffer(commandQueue, memObjects[5], CL_TRUE, 0, V_MAX * Sizeof.cl_float, pC, 0, null, null);
			clEnqueueReadBuffer(commandQueue, memObjects[6], CL_TRUE, 0, V_MAX * Sizeof.cl_float, pU, 0, null, null);
		}

		// Release kernel, program, and memory objects
		clReleaseMemObject(memObjects[0]);
		clReleaseMemObject(memObjects[1]);
		clReleaseMemObject(memObjects[2]);
		clReleaseMemObject(memObjects[3]);
		clReleaseMemObject(memObjects[4]);
		clReleaseMemObject(memObjects[5]);
		clReleaseMemObject(memObjects[6]);
		clReleaseKernel(kernel1);
		clReleaseKernel(kernel2);
		clReleaseKernel(kernel3);
		clReleaseProgram(program);
		clReleaseCommandQueue(commandQueue);
		clReleaseContext(context);

		return P;
	}

	private static String readFile(final InputStream stream) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			StringBuffer sb = new StringBuffer();
			String line = null;
			while (true) {
				line = br.readLine();
				if ( line == null ) {
					break;
				}
				sb.append(line).append("\n");
			}
			br.close();
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public float[] getD() {
		return D;
	}

	public int[] getP() {
		return P;
	}

}