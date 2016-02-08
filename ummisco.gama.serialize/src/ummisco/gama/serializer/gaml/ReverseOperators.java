package ummisco.gama.serializer.gaml;

import java.io.*;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import msi.gama.common.util.FileUtils;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.simulation.*;
import msi.gama.metamodel.agent.SavedAgent;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.IScope;
import ummisco.gama.serializer.gamaType.converters.*;

public class ReverseOperators {

	@operator(value = "serializeSimulation")
	@doc("")
	public static String serializeSimulation(final IScope scope, final int i) {
		XStream xstream = new XStream(new DomDriver());
		xstream.registerConverter(new LogConverter());
		xstream.registerConverter(new GamaBasicTypeConverter(scope));
		xstream.registerConverter(new GamaShapeFileConverter(scope));
		xstream.registerConverter(new GamaAgentConverter(scope));
		xstream.registerConverter(new GamaListConverter(scope));
		xstream.registerConverter(new GamaMapConverter(scope));
		// xstream.registerConverter(new GamaShapeConverter());
		// xstream.registerConverter(new GamaScopeConverter());
		// xstream.registerConverter(new GamaPointConverter());
		// xstream.registerConverter(new GamaPairConverter());
		// xstream.registerConverter(new GamaSimulationAgentConverter());

		ExperimentAgent expAgt = (ExperimentAgent) scope.getExperiment();
		SimulationAgent simAgt = expAgt.getSimulation();

		System.out.println("**** TODO list = Reducer for any kind of file");
		System.out.println("**** TODO list = Handle the random generator too");

		return xstream.toXML(new SavedAgent(scope, simAgt));
	}

	@operator(value = "unSerializeSimulation")
	@doc("")
	public static int unSerializeSimulation(final IScope scope, final String simul) {
		XStream xstream = new XStream(new DomDriver());
		xstream.registerConverter(new LogConverter());
		xstream.registerConverter(new GamaBasicTypeConverter(scope));
		xstream.registerConverter(new GamaShapeFileConverter(scope));
		xstream.registerConverter(new GamaAgentConverter(scope));
		xstream.registerConverter(new GamaListConverter(scope));
		xstream.registerConverter(new GamaMapConverter(scope));
		// xstream.registerConverter(new GamaShapeConverter());
		// xstream.registerConverter(new GamaScopeConverter());
		// xstream.registerConverter(new GamaPointConverter());
		// xstream.registerConverter(new GamaPairConverter());
		// xstream.registerConverter(new GamaSimulationAgentConverter());

		SavedAgent agt = (SavedAgent) xstream.fromXML(simul);

		ExperimentAgent exp = (ExperimentAgent) scope.getExperiment();
		exp.getSimulation().dispose();

		SimulationPopulation simPop = exp.getSimulationPopulation();
		agt.restoreTo(scope, simPop);

		return 1;
	}

	@operator(value = "saveSimulation")
	@doc("")
	public static int saveSimulation(final IScope scope, final String pathname) {
		String path = FileUtils.constructAbsoluteFilePath(scope, pathname, false);
		// checkValidity(scope);

		String simulation = serializeSimulation(scope, 0);

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

}
