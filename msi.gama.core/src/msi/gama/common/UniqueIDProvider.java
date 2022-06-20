package msi.gama.common;

/**
 * Class that provide an uniqueID for agent
 */
public class UniqueIDProvider extends UniqueIDProviderService 
{

	int uniqueID;
	
	public UniqueIDProvider()
	{
		System.out.println("UniqueIDProvider");
		this.uniqueID = 0;
	}
	
	@Override
	public int register() 
	{
		increment();
		return uniqueID;
	}

	/**
	 * 
	 * Increment the uniqueID and prefix it by the mpi rank of the current processor node
	 */
	public void increment() 
	{
		uniqueID++;
	}
}
