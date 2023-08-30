package proxyExperiment;

import java.io.FileNotFoundException;

import MPICommunication.MPIThreadListener;
import endActionProxy.EndStepActionProxy;
import mpi.MPI;
import mpi.MPIException;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.headless.job.IExperimentJob;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.population.IPopulationFactory;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.experiment;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import proxyPopulation.ProxyPopulationFactory;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Experiment that create a ProxyAgent with every Agent of the simulation
 * Those ProxyAgent will control the of these Agent's attribute from other Agent in the simulation
 * 
 * @author Lucas Grosjean
 *
 */
@experiment (IKeyword.PROXY)
@doc("Proxy experiment")
public class ProxyExperiment extends ExperimentAgent
{
	static
	{
		DEBUG.ON();
	}
	
	MPIThreadListener listeningThread;
	
	public ProxyExperiment(IPopulation<? extends IAgent> s, int index) throws GamaRuntimeException 
	{
		super(s, index);
		setPopulationFactory(initializePopulationFactory());
		//initEndActionProxy();
    	
		if(GAMA.isInHeadLessMode())
		{
			
			try {
				String[] args = {};
				MPI.InitThread(args, MPI.THREAD_MULTIPLE);
				
				DEBUG.REGISTER_LOG_WRITER(new IExperimentJob.DebugStream(MPI.COMM_WORLD.getRank()));
				DEBUG.OUT("HEADLESS MODE DETECTED ");
		    	DEBUG.OUT("************* MPI Init");
		    	
		    	initEndActionProxy();
				
			} catch (MPIException e) {
		    	DEBUG.OUT("MPI Init Error" + e);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}

		
	}
	
	@Override
	public void dispose() {
    	DEBUG.OUT("************* disposing ProxyExperiment");
		super.dispose();
		if(GAMA.isInHeadLessMode())
		{
			try {
		    	DEBUG.OUT("************* MPI Finalize");
				MPI.Finalize();
		    } catch (final MPIException e) {
		    	DEBUG.OUT("MPI Finalize Error" + e);
		    }
		}
		DEBUG.UNREGISTER_LOG_WRITER();
	}
	
	private void initEndActionProxy() 
	{
		this.executer.insertEndAction(new EndStepActionProxy());
	}
	
	@Override
	protected IPopulationFactory initializePopulationFactory() 
	{
		DEBUG.OUT("initializePopulationFactory");
		return new ProxyPopulationFactory();
	}
}
