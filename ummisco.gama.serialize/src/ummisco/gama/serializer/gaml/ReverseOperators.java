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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.FileUtils;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.SavedAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.runtime.IScope;
import msi.gama.util.file.GamaSavedSimulationFile;
import ummisco.gama.serializer.factory.StreamConverter;
import ummisco.gama.serializer.gamaType.converters.ConverterScope;

public class ReverseOperators {

	@operator(value = "serialize")
	@doc(value = "It serializes any object, i.e. transform it into a string.") 
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
	@doc(value = "")
	public static String serializeAgent(final IScope scope, final IAgent agent) {
		return StreamConverter.convertObjectToStream(scope, new SavedAgent(scope, agent));
	}

	@operator(value = "restoreSimulationFromFile")
	@doc(value = "restoreSimulationFromFile")
	public static int unSerializeSimulationFromFile(final IScope scope, final GamaSavedSimulationFile file) {
		return unSerializeSimulationFromXML(scope, file.getBuffer().get(0));
	}

	@operator(value = "restoreSimulation")
	@doc(value="restoreSimulation")
	public static int unSerializeSimulationFromXML(final IScope scope, final String simul) {
		final ConverterScope cScope = new ConverterScope(scope);
		final XStream xstream = StreamConverter.loadAndBuild(cScope);

		final SavedAgent saveAgt = (SavedAgent) xstream.fromXML(simul);
		final ExperimentAgent expAgt = (ExperimentAgent) scope.getExperiment();
		final SimulationAgent simAgent = expAgt.getSimulation();

		simAgent.updateWith(scope, saveAgt);

		return 1;
	}

	@operator(value = "saveAgent")
	@doc(value = "")
	public static int saveAgent(final IScope scope, final IAgent agent, final String pathname) {
		final String path = FileUtils.constructAbsoluteFilePath(scope, pathname, false);

		final String serializedAgent = serializeAgent(scope, agent);

		final ExperimentAgent expAgt = (ExperimentAgent) scope.getExperiment();
		final SimulationAgent simAgt = expAgt.getSimulation();
		int savedCycle = simAgt.getClock().getCycle();
		String savedModel = expAgt.getModel().getFilePath();
		String savedExperiment = (String) expAgt.getSpecies().getFacet(IKeyword.NAME).value(scope);
		
		FileWriter fw = null;
		try {
			final File f = new File(path);
			if (!f.exists()) {
				f.createNewFile();
			}
			fw = new FileWriter(f);
			
			// Write the Metadata
			fw.write(savedModel + System.lineSeparator());
			fw.write(savedExperiment + System.lineSeparator());
			fw.write(savedCycle + System.lineSeparator());
		
			// Write the serializedAgent
			fw.write(serializedAgent);
			fw.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}

		return 0;
	}

	@operator(value = "saveSimulation")
	@doc(value = "")
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
		return StreamConverter.convertNetworkStreamToObject(scope, s);
	}
	// END TODO

}