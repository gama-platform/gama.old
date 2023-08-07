/*******************************************************************************************************
 *
 * XStreamImplementation.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.implementations;

import com.thoughtworks.xstream.XStream;

import msi.gama.common.util.RandomUtils;
import msi.gama.kernel.experiment.ExperimentPlan;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.SavedAgent;
import msi.gama.outputs.IOutputManager;
import msi.gama.runtime.IScope;
import ummisco.gama.serializer.factory.StreamConverter;
import ummisco.gama.serializer.gamaType.converters.ConverterScope;
import ummisco.gama.serializer.gaml.ReverseOperators;

/**
 * The Class XStreamImplementation.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 7 août 2023
 */
public class XStreamImplementation extends SerialisationImplementation {

	@Override
	public void restore(final SimulationAgent currentSimAgt) {
		IScope scope = currentSimAgt.getScope();
		current = current.getParent();
		byte[] data = current.getData();

		if (data != null) {
			final String previousState = new String(data);
			final XStream xstream = StreamConverter.loadAndBuild(scope, ConverterScope.class);
			// get the previous state
			final SavedAgent agt = (SavedAgent) xstream.fromXML(previousState);
			// Update of the simulation
			currentSimAgt.updateWith(scope, agt);
			// useful to recreate the random generator
			final int rngUsage = currentSimAgt.getRandomGenerator().getUsage();
			final String rngName = currentSimAgt.getRandomGenerator().getRngName();
			final Double rngSeed = currentSimAgt.getRandomGenerator().getSeed();

			final IOutputManager outputs = currentSimAgt.getOutputManager();
			if (outputs != null) { outputs.step(scope); }

			// Recreate the random generator and set it to the same state as the saved one
			if (((ExperimentPlan) scope.getExperiment().getSpecies()).keepsSeed()) {
				currentSimAgt.setRandomGenerator(new RandomUtils(rngSeed, rngName));
				currentSimAgt.getRandomGenerator().setUsage(rngUsage);
			} else {
				currentSimAgt.setRandomGenerator(
						new RandomUtils(scope.getExperiment().getRandomGenerator().next(), rngName));
			}
		}
	}

	@Override
	public void save(final SimulationAgent sim) {
		final String state = ReverseOperators.serializeAgent(sim.getScope(), sim);
		if (current == null) {
			current = history.setRoot(state.getBytes());
		} else {
			current = current.addChild(state.getBytes());
		}
	}

}
