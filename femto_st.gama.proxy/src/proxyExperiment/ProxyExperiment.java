package proxyExperiment;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.population.IPopulationFactory;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.experiment;
import msi.gama.runtime.exceptions.GamaRuntimeException;
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
		DEBUG.OFF();
	}
	
	public ProxyExperiment(IPopulation<? extends IAgent> s, int index) throws GamaRuntimeException 
	{
		super(s, index);
		setPopulationFactory(initializePopulationFactory());
	}
	
	@Override
	protected IPopulationFactory initializePopulationFactory() 
	{
		DEBUG.OUT("initializePopulationFactory");
		return new ProxyPopulationFactory();
	}
}
