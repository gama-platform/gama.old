package proxyExperiment;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.population.IPopulationFactory;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.experiment;
import msi.gama.runtime.exceptions.GamaRuntimeException;

@experiment (IKeyword.PROXY)
@doc("Proxy experiment")
public class ProxyExperiment extends ExperimentAgent
{

	public ProxyExperiment(IPopulation<? extends IAgent> s, int index) throws GamaRuntimeException 
	{
		super(s, index);
		setPopulationFactory(initializePopulationFactory());
	}
	
	protected IPopulationFactory initializePopulationFactory() {
		System.out.println("initializePopulationFactory distribution Experiment");
		return new ProxyPopulationFactory();
	}
}
