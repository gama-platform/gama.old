package ummisco.gama.serializer.gaml;

import java.io.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import msi.gama.common.util.FileUtils;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.simulation.*;
import msi.gama.metamodel.agent.AbstractAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.SavedAgent;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.IScope;
import ummisco.gama.serializer.factory.StreamConverter;
import ummisco.gama.serializer.gamaType.converters.*;

public class ReverseOperators {

	@operator(value = "serialize")
	@doc("It serializes any object, i.e. transform it into a string.")
	public static String serialize(final IScope scope, final Object o) {
		System.out.println("**** Serialize Object ****");	
		return StreamConverter.convertObjectToStream(scope, o);
	}	
	
	@operator(value = "unserialize")
	@doc("")
	public static Object unserialize(final IScope scope, final String s) {
		System.out.println("**** unSerialize Object ****");	
		return StreamConverter.convertStreamToObject(scope, s);
	}
	

	
	@operator(value = "serializeAgent")
	@doc("")
	public static String serializeAgent(final IScope scope, final IAgent agent) {

		System.out.println("**** TODO list = Problème dans les displays");
		System.out.println("**** TODO list = Reducer for any kind of file");
		System.out.println("**** TODO list = Case of multi-simulation ?");
		System.out.println("**** TODO list Improvment = simplify GamaShape");		

		return StreamConverter.convertObjectToStream(scope, new SavedAgent(scope, agent));		
	}		
	
	@operator(value = "unSerializeSimulation")
	@doc("")
	public static int unSerializeSimulation(final IScope scope, final String simul) {
		ConverterScope cScope = new ConverterScope(scope);
		XStream xstream = StreamConverter.loadAndBuild(cScope);
		
		ExperimentAgent exp = (ExperimentAgent) scope.getExperiment();
		exp.getSimulation().dispose();		
		
//		SavedAgent agt = (SavedAgent) xstream.fromXML(simul);
		SimulationAgent simAgt = (SimulationAgent) xstream.fromXML(simul);



//		SimulationPopulation simPop = exp.getSimulationPopulation();
//		agt.restoreTo(scope, simPop);
		
//		SimulationAgent simAgt = exp.getSimulation();
//		cScope.setSimulationAgent(simAgt);
		
//		agt = (SavedAgent) xstream.fromXML(simul);
//		simAgt = (SimulationAgent) xstream.fromXML(simul);

		return 1;
	}

	@operator(value = "saveAgent")
	@doc("")
	public static int saveAgent(final IScope scope, final IAgent agent, final String pathname) {
		String path = FileUtils.constructAbsoluteFilePath(scope, pathname, false);

		String simulation = serializeAgent(scope, agent);

		FileWriter fw = null;
		try {
			File f = new File(path);
			if ( !f.exists() ) {
				f.createNewFile();
			}
			fw = new FileWriter(f);
			fw.write(simulation);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return 0;
	}
	
	@operator(value = "saveSimulation")
	@doc("")
	public static int saveSimulation(final IScope scope, final String pathname) {
		ExperimentAgent expAgt = (ExperimentAgent) scope.getExperiment();
		SimulationAgent simAgt = expAgt.getSimulation();

		return saveAgent(scope, simAgt, pathname);
	}	

}
