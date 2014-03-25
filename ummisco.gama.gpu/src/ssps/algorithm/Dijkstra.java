/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ssps.algorithm;

import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.CL_DEVICE_TYPE_ALL;
import static org.jocl.CL.CL_MEM_COPY_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_ONLY;
import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clCreateCommandQueue;
import static org.jocl.CL.clCreateContext;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clEnqueueReadBuffer;
import static org.jocl.CL.clGetDeviceIDs;
import static org.jocl.CL.clGetPlatformIDs;
import static org.jocl.CL.clReleaseCommandQueue;
import static org.jocl.CL.clReleaseContext;
import static org.jocl.CL.clReleaseKernel;
import static org.jocl.CL.clReleaseMemObject;
import static org.jocl.CL.clReleaseProgram;
import static org.jocl.CL.clSetKernelArg;
import ssps.graph.Graph;
import ssps.util.P;

import java.io.BufferedReader;
import java.io.FileInputStream;
//import com.sun.org.apache.xpath.internal.operations.Bool;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_platform_id;
import org.jocl.cl_program;

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

	public Dijkstra() {
		V = new int[P.V_MAX];
		E = new int[P.E_MAX];
		W = new float[P.E_MAX];
		M = new int[P.V_MAX];
		D = new float[P.V_MAX];
	}

	public Dijkstra(int[] V, int[] E, float[] W, int[] M, float[] D) {
		this.V = V;
		this.E = E;
		this.W = W;
		this.M = M;
		this.D = D;
	}

	public void init(Graph graph) {
		this.V = graph.getVertex_list();
		this.E = graph.getEdge_list();
		this.W = graph.getWeight_list();
		for (int i = 0; i < P.V_MAX; i++) {
			this.M[i] = 0;
			this.D[i] = (float) P.INFINI;
		}
	}

	public void initDijkstraCPU(Graph G, int source, int[] V, int[] E,
			float[] W, int[] M, float[] D) {
		init(G);
		int nb_V = G.getVertex_count();
		int nb_E = G.getEdge_count();
		for (int i = 0; i < nb_V; i++) {
			V[i] = this.V[i];
			M[i] = 0;
			D[i] = this.D[i];
		}
		for (int i = 0; i < nb_E; i++) {
			E[i] = this.E[i];
			W[i] = this.W[i];
		}
		M[source] = 1;
		D[source] = (float) 0;
		int start = V[source];
		int end = V[source + 1];
		for (int j = start; j < end; j++) {
			D[E[j]] = W[j];
		}
	}

	public void initDijkstraGPU(Graph G, int source, int[] V, int[] E,
			float[] W, int[] M, float[] C, float[] U) {
		init(G);
		int nb_V = G.getVertex_count();
		int nb_E = G.getEdge_count();
		for (int i = 0; i < nb_V; i++) {
			V[i] = this.V[i];
			M[i] = 0;
			C[i] = (float) P.INFINI;
			U[i] = (float) P.INFINI;
		}
		for (int i = 0; i < nb_E; i++) {
			E[i] = this.E[i];
			W[i] = this.W[i];
		}
		M[source] = 1;
		C[source] = 0;
		U[source] = 0;
		/*
		 * int start = V[source]; int end = V[source + 1]; for (int j = start; j
		 * < end; j++) { D[E[j]] = W[j]; }
		 */
	}

	private boolean isTrue(int N, int[] M) {
		int i = 0;
		while (i < N) {
			if (M[i] == 0)
				return false;
			i++;
		}
		return true;
	}

	private boolean isEmpty(int N, int[] M) {
		int i = 0;
		while (i < N) {
			if (M[i] == 1)
				return false;
			i++;
		}
		return true;
	}

	private int searchMinNonMask(Graph graph, float[] D, int[] M) {
		int nb_node = graph.getVertex_count();
		int min_index = -1;
		float min = (float) Float.MAX_VALUE;
		for (int i = 0; i < nb_node; i++) {
			if (min > D[i] && M[i] == 0) {
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
	public float[] searchSPCPU(Graph graph, int source_index) {
		int nb_v = graph.getVertex_count();
		int nb_e = graph.getEdge_count();
		int[] V = new int[P.V_MAX];
		int[] E = new int[P.E_MAX];
		float[] W = new float[P.E_MAX];
		int[] M = new int[P.V_MAX];
		float[] D = new float[P.V_MAX];
		initDijkstraCPU(graph, source_index, V, E, W, M, D);

		while (!isTrue(nb_v, M)) {
			int u = searchMinNonMask(graph, D, M);
			if (u == -1)
				break;
			M[u] = 1;
			int start = V[u];
			int end;
			if (u + 1 < nb_v)
				end = V[u + 1];
			else
				end = nb_e;
			for (int j = start; j < end; j++) {
				float temp = D[u] + W[j];
				int v = E[j];
				if (D[v] > temp) {
					D[v] = temp;
					M[v] = 0;
				}
			}
		}
		System.out.println("Result CPU: " + java.util.Arrays.toString(D));
		return D;
	}

	/**
	 * @param graph
	 * @param source_index
	 */
	public float[] searchSPGPU(Graph graph, int source_index) {
		int n = P.E_MAX;
		int nb_v = graph.getVertex_count();
		int nb_e = graph.getEdge_count();

		int[] VPtr = graph.getVertex_list();
		int[] EPtr = graph.getEdge_list();
		float[] WPtr = graph.getWeight_list();
		int[] MPtr = new int[P.V_MAX];
		float[] CPtr = new float[P.V_MAX];
		float[] UPtr = new float[P.V_MAX];

		Pointer pV = Pointer.to(VPtr);
		Pointer pE = Pointer.to(EPtr);
		Pointer pW = Pointer.to(WPtr);
		Pointer pM = Pointer.to(MPtr);
		Pointer pC = Pointer.to(CPtr);
		Pointer pU = Pointer.to(UPtr);

		// The platform, device type and device number
		// that will be used
		final int platformIndex = 0;
		final long deviceType = CL_DEVICE_TYPE_ALL;
		final int deviceIndex = 0;

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
		for(int i = 0; i<devices.length;i++ )
		System.out.println("pouet "+devices[i] );
		
		cl_device_id device = devices[1];//deviceIndex];

		// Create a context for the selected device
		cl_context context = clCreateContext(contextProperties, 1,
				new cl_device_id[] { device }, null, null, null);

		// Create a command-queue for the selected device
		cl_command_queue commandQueue = clCreateCommandQueue(context, device,
				0, null);

		// Allocate the memory objects for the input- and output data
		cl_mem memObjects[] = new cl_mem[6];
		memObjects[0] = clCreateBuffer(context, CL_MEM_READ_ONLY
				| CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * P.V_MAX, pV, null);

		memObjects[1] = clCreateBuffer(context, CL_MEM_READ_ONLY
				| CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * P.E_MAX, pE, null);

		memObjects[2] = clCreateBuffer(context, CL_MEM_READ_ONLY
				| CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * P.E_MAX, pW, null);

		memObjects[3] = clCreateBuffer(context, CL_MEM_READ_WRITE
				| CL_MEM_COPY_HOST_PTR, Sizeof.cl_int * P.V_MAX, pM, null);

		memObjects[4] = clCreateBuffer(context, CL_MEM_READ_WRITE
				| CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * P.V_MAX, pC, null);

		memObjects[5] = clCreateBuffer(context, CL_MEM_READ_WRITE
				| CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * P.V_MAX, pU, null);

		// Create the program from the source code
		String source = readFile("/tmp/Kernel.cl");
		cl_program program = clCreateProgramWithSource(context, 1,
				new String[] { source }, null, null);

		// Build the program
		clBuildProgram(program, 0, null, null, null, null);

		// Create the kernel1
		cl_kernel kernel1 = clCreateKernel(program, "OCL_SSSP_KERNEL1", null);
		clSetKernelArg(kernel1, 0, Sizeof.cl_mem, Pointer.to(memObjects[0]));
		clSetKernelArg(kernel1, 1, Sizeof.cl_mem, Pointer.to(memObjects[1]));
		clSetKernelArg(kernel1, 2, Sizeof.cl_mem, Pointer.to(memObjects[2]));
		clSetKernelArg(kernel1, 3, Sizeof.cl_mem, Pointer.to(memObjects[3]));
		clSetKernelArg(kernel1, 4, Sizeof.cl_mem, Pointer.to(memObjects[4]));
		clSetKernelArg(kernel1, 5, Sizeof.cl_mem, Pointer.to(memObjects[5]));
		clSetKernelArg(kernel1, 6, Sizeof.cl_int,
				Pointer.to(new int[] { nb_v }));
		clSetKernelArg(kernel1, 7, Sizeof.cl_int,
				Pointer.to(new int[] { nb_e }));

		// Create the kernel2
		cl_kernel kernel2 = clCreateKernel(program, "OCL_SSSP_KERNEL2", null);
		clSetKernelArg(kernel2, 0, Sizeof.cl_mem, Pointer.to(memObjects[0]));
		clSetKernelArg(kernel2, 1, Sizeof.cl_mem, Pointer.to(memObjects[1]));
		clSetKernelArg(kernel2, 2, Sizeof.cl_mem, Pointer.to(memObjects[2]));
		clSetKernelArg(kernel2, 3, Sizeof.cl_mem, Pointer.to(memObjects[3]));
		clSetKernelArg(kernel2, 4, Sizeof.cl_mem, Pointer.to(memObjects[4]));
		clSetKernelArg(kernel2, 5, Sizeof.cl_mem, Pointer.to(memObjects[5]));
		clSetKernelArg(kernel2, 6, Sizeof.cl_int,
				Pointer.to(new int[] { nb_v }));

		// Create the kernel3
		cl_kernel kernel3 = clCreateKernel(program, "OCL_SSSP_INIT", null);
		clSetKernelArg(kernel3, 0, Sizeof.cl_mem, Pointer.to(memObjects[0]));
		clSetKernelArg(kernel3, 1, Sizeof.cl_mem, Pointer.to(memObjects[1]));
		clSetKernelArg(kernel3, 2, Sizeof.cl_mem, Pointer.to(memObjects[2]));
		clSetKernelArg(kernel3, 3, Sizeof.cl_mem, Pointer.to(memObjects[3]));
		clSetKernelArg(kernel3, 4, Sizeof.cl_mem, Pointer.to(memObjects[4]));
		clSetKernelArg(kernel3, 5, Sizeof.cl_mem, Pointer.to(memObjects[5]));
		clSetKernelArg(kernel3, 6, Sizeof.cl_int,
				Pointer.to(new int[] { nb_v }));
		clSetKernelArg(kernel3, 7, Sizeof.cl_int,
				Pointer.to(new int[] { source_index }));

		// Set the work-item dimensions
		long global_work_size[] = new long[] { n };
		long local_work_size[] = new long[] { 1 };

		// Execute the kerne3
		clEnqueueNDRangeKernel(commandQueue, kernel3, 1, null,
				global_work_size, local_work_size, 0, null, null);
		// Read the output data
		clEnqueueReadBuffer(commandQueue, memObjects[3], CL_TRUE, 0, P.V_MAX
				* Sizeof.cl_int, pM, 0, null, null);
		clEnqueueReadBuffer(commandQueue, memObjects[4], CL_TRUE, 0, P.V_MAX
				* Sizeof.cl_float, pC, 0, null, null);
		clEnqueueReadBuffer(commandQueue, memObjects[5], CL_TRUE, 0, P.V_MAX
				* Sizeof.cl_float, pU, 0, null, null);

		while (!isEmpty(nb_v, MPtr)) {
			// Execute the kernel
			clEnqueueNDRangeKernel(commandQueue, kernel1, 1, null,
					global_work_size, local_work_size, 0, null, null);

			// Execute the kernel
			clEnqueueNDRangeKernel(commandQueue, kernel2, 1, null,
					global_work_size, local_work_size, 0, null, null);
			// Read the output data
			clEnqueueReadBuffer(commandQueue, memObjects[3], CL_TRUE, 0,
					P.V_MAX * Sizeof.cl_float, pM, 0, null, null);
			clEnqueueReadBuffer(commandQueue, memObjects[4], CL_TRUE, 0,
					P.V_MAX * Sizeof.cl_float, pC, 0, null, null);
			clEnqueueReadBuffer(commandQueue, memObjects[5], CL_TRUE, 0,
					P.V_MAX * Sizeof.cl_float, pU, 0, null, null);
		}

		// Release kernel, program, and memory objects
		clReleaseMemObject(memObjects[0]);
		clReleaseMemObject(memObjects[1]);
		clReleaseMemObject(memObjects[2]);
		clReleaseMemObject(memObjects[3]);
		clReleaseMemObject(memObjects[4]);
		clReleaseMemObject(memObjects[5]);
		clReleaseKernel(kernel1);
		clReleaseKernel(kernel2);
		clReleaseKernel(kernel3);
		clReleaseProgram(program);
		clReleaseCommandQueue(commandQueue);
		clReleaseContext(context);

		return CPtr;
	}

	private static String readFile(String fileName) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(fileName)));
			StringBuffer sb = new StringBuffer();
			String line = null;
			while (true) {
				line = br.readLine();
				if (line == null) {
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
}