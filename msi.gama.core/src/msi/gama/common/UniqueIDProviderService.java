package msi.gama.common;

//import femto_st.gama.mpi.UniqueIDProviderServiceMPI;

/**
 * Singleton class that provide an uniqueID for agent based on an instance (MPI or not)
 */
public class UniqueIDProviderService implements IUniqueIDProviderService
{
	int uniqueID;
	static UniqueIDProviderService instance;
	
	public static IUniqueIDProviderService getInstance()
    {
        if (instance == null)
        {        	
        	instance = new UniqueIDProvider();
        }
  
        return instance;
    }
	
	@Override
	public synchronized UniqueID register() 
	{
		return instance.register();
	}
	
	@Override
	public void initMPI(int mpiRank) 
	{
		System.out.println("INIT mpi rank uniqueID provider = "+mpiRank);
		instance = new UniqueIDProviderMPI(mpiRank);
	}

}
