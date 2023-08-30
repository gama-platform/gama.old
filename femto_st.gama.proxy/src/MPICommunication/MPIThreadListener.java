package MPICommunication;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicBoolean;

import MPISkill.IMPISkill;
import mpi.MPI;
import mpi.MPIException;
import mpi.Status;
import ummisco.gama.dev.utils.DEBUG;

public class MPIThreadListener extends Thread 
{
	static
	{
		DEBUG.OFF();
	}
	
    private final AtomicBoolean running = new AtomicBoolean(false);
	
	public MPIThreadListener()
	{
		DEBUG.OUT("INIT MPIThreadListener");
	}
	
	@Override
	public void run()
	{
		DEBUG.OUT("run begin");
		running.set(true);
		while(running.get())
		{
			try {

				DEBUG.OUT("WAITING FOR REQUEST");
				byte[] arr = new byte[4];
				Status st = MPI.COMM_WORLD.recv(arr, 1, MPI.INT, MPI.ANY_SOURCE, IMPISkill.REQUEST_TAG); // receive the type of request
				
				DEBUG.OUT("request st source= " + st.getSource());
				DEBUG.OUT("request st tag = " + st.getTag());
				
				int requestType = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getInt();
				DEBUG.OUT("requestType = " + requestType);
				
				MPIRequest request = new MPIRequest(requestType, st.getSource());
				
				DEBUG.OUT("starting thread with request " + request.toString());
				
				Thread th = new Thread(new ExecuteMPIRequest(request)); // exec the request in a thread
				th.start();
				
			} catch (MPIException e) 
			{
				DEBUG.OUT("MPIException MPIThreadListener run " + e);
			}
		}
	}
	
	@Override
	public void interrupt() 
	{
        running.set(false);
    }
}
