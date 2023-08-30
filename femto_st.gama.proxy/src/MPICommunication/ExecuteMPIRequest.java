package MPICommunication;

import ummisco.gama.dev.utils.DEBUG;

/**
 * 
 * 
 * @author Lucas Grosjean
 *
 */
public class ExecuteMPIRequest implements Runnable 
{
	MPIRequest request;
	
	static
	{
		DEBUG.OFF();
	}
	
	public ExecuteMPIRequest(MPIRequest request) 
	{
		this.request = request;
	}
	
	void test()
	{
	}

	@Override
	public void run() 
	{
		DEBUG.OUT("START OF " + request);
		
		switch (request.requestType) 
		{
			default -> test();
		}
		
		DEBUG.OUT("END OF " + request);
		Thread.currentThread().interrupt();
		return;
	}
}
