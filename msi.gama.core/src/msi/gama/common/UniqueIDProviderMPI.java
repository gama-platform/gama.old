package msi.gama.common;

/**
 * Class that provide an uniqueID for agent in MPI context (uniqueID is prefixed by the mpi rank ofthe current processor node)
 */
public class UniqueIDProviderMPI extends UniqueIDProviderService
{
	
	int mpiRank = 0;
	int uniqueID = 0;
	
	public UniqueIDProviderMPI(int mpiRank)
	{
		this.uniqueID = 1;
		this.mpiRank = mpiRank; 
		System.out.println("MPIRANK PROVIDER INIT");
	}
	
	@Override
	public UniqueID register() 
	{
		System.out.println("MPI REGISTER");
		increment();
		return new UniqueID(mpiRank, uniqueID);
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
