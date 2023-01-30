package femto_st.gama.mpi;

import mpi.*;
import java.util.List;

/**
 * 
 * 
 * Class designed to make some MPI functions easier to use in MPI context
 *
 */
public class MPIFunctionWrapper 
{
	
	/**
	 * 
	 * Wrapper for  MPI.COMM_WORLD.gatherv from root perspective where we dont know the size of all the element to be received
	 */
	/*public Object[] wrapperGatherVRoot(Object[] dataFromRoot) throws MPIException
	{

		int totalSize = 0;
        int numberOfProcess = MPI.COMM_WORLD.getSize(); // number of process;

        int[] sizeGatherIn = new int[1]; // Buffer to receive all the size from others process
        int[] sizeGatherOut = new int[numberOfProcess]; // Buffer to receive all the size from others process
        
        int[] displ = new int[numberOfProcess]; // displacements buffer => displ[i] = starting index where to write the data from process i in buffer dataBufferOut
        Byte[] dataBufferOut; // Buffer to receive all data in root
        
        sizeGatherIn[0] = dataFromRoot.length;
        
		
        MPI.COMM_WORLD.gather(sizeGatherIn, 1, MPI.INT, sizeGatherOut, 1, MPI.INT, 0); // receive size from all process
        for (int i = 0; i < sizeGatherOut.length; i++) 
        {
            totalSize += sizeGatherOut[i];
        }
        System.out.println("total number of int to receive = " + totalSize);

        dataBufferOut = new byte[totalSize]; // buffer to receive all the data
        displ[0] = 0; // process 0 can write starting at index 0

        int displIndex = 0;
        for (int processIndex = 1; processIndex < displ.length; processIndex++) 
        {
            for (int j = 0; j < processIndex; j++) 
            {
                displIndex += sizeGatherOut[j];
            }
            System.out.println("Starting index for process " + processIndex +" = " + displIndex);
            displ[processIndex] = displIndex;

            displIndex = 0;
        }

        MPI.COMM_WORLD.gatherv(dataFromRoot, dataFromRoot.length, MPI.INT, dataBufferOut, sizeGatherOut, displ, MPI.BYTE, 0); // receive data from all process
        
        for (int dataIndex = 0; dataIndex < dataBufferOut.length; dataIndex++) {
            System.out.println("dataBuffer[" + dataIndex + "] = " + dataBufferOut[dataIndex]);
        }
        
        return dataBufferOut;
	}*/
}
