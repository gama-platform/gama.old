package ummisco.gama.communicator;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import msi.gama.runtime.IScope;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.SavedAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;

public class ReverseOperators {
	@operator(value = "serializeSimulation")
	@doc("")
	public static String serializeSimulation(IScope scope, int i) {
		XStream xstream = new XStream(new DomDriver());
	//	xstream.registerConverter(new GamaAgentConverter());
	//	xstream.registerConverter(new GamaScopeConverter());
	//	xstream.registerConverter(new GamaPointConverter());
	//	xstream.registerConverter(new GamaPairConverter());
	//	xstream.registerConverter(new GamaMapConverter());
	//	xstream.registerConverter(new GamaSimulationAgentConverter());
		
		ExperimentAgent expAgt = (ExperimentAgent) scope.getExperiment();
		SimulationAgent simAgt = expAgt.getSimulation();
		
		return (String) xstream.toXML(new SavedAgent(scope,simAgt));
	}
	
	@operator(value = "unSerializeSimulation")
	@doc("")
	public static int unSerializeSimulation(IScope scope, String simul) {
		XStream xstream = new XStream(new DomDriver());
	//	xstream.registerConverter(new GamaAgentConverter());
	//	xstream.registerConverter(new GamaScopeConverter());
	//	xstream.registerConverter(new GamaPointConverter());
	//	xstream.registerConverter(new GamaPairConverter());
	//	xstream.registerConverter(new GamaMapConverter());
	//	xstream.registerConverter(new GamaSimulationAgentConverter());
		
		SavedAgent agt = (SavedAgent) xstream.fromXML(simul);

		return 1;
	}
	
}
