package femto_st.gama.mpi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import mpi.MPI;
import msi.gama.common.UniqueIDProviderService;
import ummisco.gama.dev.utils.FLAGS;

/**
 * The Class Activator for UniqueIDProviderMPI class.
 */
public class UniqueIDProviderActivator implements BundleActivator {
	@Override
	public void start(final BundleContext context) throws Exception 
	{
		// todo load library without mpirun 
		// or get mpiRank 
		
		//final String[] arg = {};
		//MPI.InitThread(arg, MPI.THREAD_MULTIPLE);
		//UniqueIDProviderService.getInstance().initMPI(MPI.COMM_WORLD.getRank());
		
		//UniqueIDProviderService.getInstance().initMPI(888);
	}

	@Override
	public void stop(final BundleContext context) throws Exception {}

}
