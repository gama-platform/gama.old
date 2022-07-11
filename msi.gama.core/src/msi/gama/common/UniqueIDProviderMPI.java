package msi.gama.common;

/**
 * Class that provide an uniqueID for agent in MPI context (uniqueID is prefixed by the mpi rank ofthe current processor node)
 */
public class UniqueIDProviderMPI extends UniqueIDProviderService
{
	
	int mpiRank;
	int uniqueID;
	int uniqueID_entity;
	
	public UniqueIDProviderMPI(int mpiRank)
	{
		this.uniqueID = 0;
		this.mpiRank = mpiRank + 1; // prefixing when mpiRank = 0 dont work when we cast the id to int afterwards -> 0XXXX = XXXX
		System.out.println("MPIRANK PROVIDER INIT");
	}
	
	@Override
	public int register() 
	{
		System.out.println("MPI REGISTER");
		increment();
		return uniqueID_entity;
	}

	/**
	 * 
	 * Increment the uniqueID and prefix it by the mpi rank of the current processor node
	 */
	public void increment() 
	{
		uniqueID++;
		System.out.println("uniqueID MPI = "+uniqueID);
		System.out.println("uniqueID_entity = "+uniqueID_entity);
		System.out.println("uniqueID_entity mpiRank = "+mpiRank);
		
		String rankString = Integer.toString(mpiRank);
        String idString = Integer.toString(uniqueID);
 
        String uniqueIDMPI = rankString + idString;
        uniqueID_entity = Integer.parseInt(uniqueIDMPI);
	}
}
