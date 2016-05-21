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
import ummisco.gama.serializer.gamaType.converters.*;

public class ReverseOperators {

	public static XStream newXStream(ConverterScope cs) {
		// TODO check whether a BinaryStreamDriver could not be better ... 
		XStream xstream = new XStream(new DomDriver());
		xstream.registerConverter(new LogConverter());
		xstream.registerConverter(new GamaBasicTypeConverter(cs));
		xstream.registerConverter(new GamaAgentConverter(cs));
		xstream.registerConverter(new GamaListConverter(cs));
		xstream.registerConverter(new GamaMapConverter(cs));
		xstream.registerConverter(new SavedAgentConverter(cs));
		xstream.registerConverter(new GamaPairConverter());
		xstream.registerConverter(new GamaMatrixConverter(cs));
		xstream.registerConverter(new GamaGraphConverter(cs));		
		xstream.registerConverter(new GamaFileConverter(cs));
		xstream.registerConverter(new GamaPopulationConverter(cs));
		xstream.registerConverter(new GamaSpeciesConverter(cs));		
		//		xstream.registerConverter(new GamaShapeFileConverter(cs));

		return xstream;
	}
	
	@operator(value = "serializeAgent")
	@doc("")
//	public static String serializeSimulation(final IScope scope, final int i) {
	public static String serializeAgent(final IScope scope, final IAgent agent) {

	XStream xstream = newXStream(new ConverterScope(scope));

	//	ExperimentAgent expAgt = (ExperimentAgent) scope.getExperiment();
	//	SimulationAgent simAgt = expAgt.getSimulation();

		System.out.println("**** TODO list = Problème dans les displays");
//		System.out.println("**** TODO list = Get an agent as parameter");
		System.out.println("**** TODO list = Reducer for any kind of file");
		System.out.println("**** TODO list = Case of multi-simulation ?");
		System.out.println("**** TODO list Improvment = simplify GamaShape");		

		return xstream.toXML(new SavedAgent(scope, agent));
	}

	@operator(value = "serialize")
	@doc("")
	public static String serialize(final IScope scope, final Object o) {
		System.out.println("**** Serialize Object ****");	

		XStream xstream = newXStream(new ConverterScope(scope));
		return xstream.toXML(o);
	}	
	
	@operator(value = "unserialize")
	@doc("")
	public static Object unserialize(final IScope scope, final String s) {
		System.out.println("**** unSerialize Object ****");	
		ConverterScope cScope = new ConverterScope(scope);
		XStream xstream = newXStream(cScope);

		Object agt = xstream.fromXML(s);
		
		return agt;
	}		
	
	@operator(value = "unSerializeSimulation")
	@doc("")
	public static int unSerializeSimulation(final IScope scope, final String simul) {
		ConverterScope cScope = new ConverterScope(scope);
		XStream xstream = newXStream(cScope);

		SavedAgent agt = (SavedAgent) xstream.fromXML(simul);

		ExperimentAgent exp = (ExperimentAgent) scope.getExperiment();
		exp.getSimulation().dispose();

		SimulationPopulation simPop = exp.getSimulationPopulation();
		agt.restoreTo(scope, simPop);
		
		SimulationAgent simAgt = exp.getSimulation();
		cScope.setSimulationAgent(simAgt);
		
		agt = (SavedAgent) xstream.fromXML(simul);

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
