/*********************************************************************************************
 *
 * 'ReverseOperators.java, in plugin ummisco.gama.serialize, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.serializer.gaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;

import msi.gama.common.util.FileUtils;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.SavedAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.runtime.IScope;
import ummisco.gama.serializer.factory.StreamConverter;
import ummisco.gama.serializer.gamaType.converters.ConverterScope;

public class ReverseOperators {

	@operator(value = "serialize")
	@doc(value = "It serializes any object, i.e. transform it into a string.") //, deprecated = "Still in alpha version, do not use it.")
	public static String serialize(final IScope scope, final Object o) {
		System.out.println("**** Serialize Object ****");
		return StreamConverter.convertObjectToStream(scope, o);
	}

	@operator(value = "unserialize")
	@doc(value = "", deprecated = "Still in alpha version, do not use it.")
	public static Object unserialize(final IScope scope, final String s) {
		System.out.println("**** unSerialize Object ****");
		return StreamConverter.convertStreamToObject(scope, s);
	}

	@operator(value = "serializeAgent")
	@doc(value = "")//, deprecated = "Still in alpha version, do not use it.")
	public static String serializeAgent(final IScope scope, final IAgent agent) {

		System.out.println("**** TODO list = Problème dans les displays");
		System.out.println("**** TODO list = Reducer for any kind of file");
		System.out.println("**** TODO list = Case of multi-simulation ?");
		System.out.println("**** TODO list Improvment = simplify GamaShape");

		return StreamConverter.convertObjectToStream(scope, new SavedAgent(scope, agent));
	}

	@operator(value = "unSerializeSimulationFromFile")
	@doc(value = "")//, deprecated = "Still in alpha version, do not use it.")
	public static int unSerializeSimulationFromFile(final IScope scope, final String pathname) {
		final ConverterScope cScope = new ConverterScope(scope);
		final XStream xstream = StreamConverter.loadAndBuild(cScope);

		BufferedReader br = null;
		String stringFile;

		final String absolute_pathname = FileUtils.constructAbsoluteFilePath(scope, pathname, false);

		try {
			br = new BufferedReader(new FileReader(absolute_pathname));
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
		try {
			final StringBuilder sb = new StringBuilder();
			String line = null;
			try {
				line = br.readLine();
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				try {
					line = br.readLine();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
			stringFile = sb.toString();
		} finally {
			try {
				br.close();
			} catch (final IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		final SavedAgent saveAgt = (SavedAgent) xstream.fromXML(stringFile);
		final ExperimentAgent expAgt = (ExperimentAgent) scope.getExperiment();
		final SimulationAgent simAgent = expAgt.getSimulation();

		simAgent.updateWith(scope, saveAgt);

		return 1;
	}

	@operator(value = "unSerializeSimulation")
	@doc(value="unSerializeSimulation")//, deprecated ="Still in alpha version, do not use it.")
	// TODO: to check ...
	public static int unSerializeSimulationFromXML(final IScope scope, final String simul) {
		final ConverterScope cScope = new ConverterScope(scope);
		final XStream xstream = StreamConverter.loadAndBuild(cScope);

		final SavedAgent saveAgt = (SavedAgent) xstream.fromXML(simul);
		final ExperimentAgent expAgt = (ExperimentAgent) scope.getExperiment();
		final SimulationAgent simAgent = expAgt.getSimulation();

		simAgent.updateWith(scope, saveAgt);

		// exp.getSimulation().dispose();

		// SavedAgent agt = (SavedAgent) xstream.fromXML(simul);
		// SimulationAgent simAgt = (SimulationAgent) xstream.fromXML(simul);

		// SimulationPopulation simPop = exp.getSimulationPopulation();
		// agt.restoreTo(scope, simPop);

		// SimulationAgent simAgt = exp.getSimulation();
		// cScope.setSimulationAgent(simAgt);

		// agt = (SavedAgent) xstream.fromXML(simul);
		// simAgt = (SimulationAgent) xstream.fromXML(simul);

		return 1;
	}

	@operator(value = "saveAgent")
	@doc(value = "", deprecated = "Still in alpha version, do not use it.")
	public static int saveAgent(final IScope scope, final IAgent agent, final String pathname) {
		final String path = FileUtils.constructAbsoluteFilePath(scope, pathname, false);

		final String simulation = serializeAgent(scope, agent);

		FileWriter fw = null;
		try {
			final File f = new File(path);
			if (!f.exists()) {
				f.createNewFile();
			}
			fw = new FileWriter(f);
			fw.write(simulation);
			fw.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		return 0;
	}

	@operator(value = "saveSimulation")
	@doc(value = "")//, deprecated = "Still in alpha version, do not use it.")
	public static int saveSimulation(final IScope scope, final String pathname) {
		final ExperimentAgent expAgt = (ExperimentAgent) scope.getExperiment();
		final SimulationAgent simAgt = expAgt.getSimulation();

		return saveAgent(scope, simAgt, pathname);
	}

	// TODO to remove when possible
	@operator(value = "serializeNetwork")
	@doc(value = "[For network purpose] It serializes any object, i.e. transform it into a string.", deprecated = "Still in alpha version, do not use it.")
	public static String serializeNetwork(final IScope scope, final Object o) {
		System.out.println("**** Serialize Object ****");
		return StreamConverter.convertNetworkObjectToStream(scope, o);
	}

	@operator(value = "unserializeNetwork")
	@doc(value = "[For network purpose]", deprecated = "Still in alpha version, do not use it.")
	public static Object unserializeNetwork(final IScope scope, final String s) {
		System.out.println("**** unSerialize Object ****");
		return StreamConverter.convertStreamToObject(scope, s);
	}
	// END TODO

}