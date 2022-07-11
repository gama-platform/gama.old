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
		final String[] arg = {};
		MPI.InitThread(arg, MPI.THREAD_MULTIPLE); // does not work with GUI : UnsatisfiedLinkError
		UniqueIDProviderService.getInstance().initMPI(MPI.COMM_WORLD.getRank());
		
		// todo find a way to start gama with GUI 
	}

	@Override
	public void stop(final BundleContext context) throws Exception {}

}
