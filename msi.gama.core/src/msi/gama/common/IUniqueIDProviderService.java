package msi.gama.common;

public interface IUniqueIDProviderService 
{
	void initMPI(int mpiRank);
	UniqueID register();
}
