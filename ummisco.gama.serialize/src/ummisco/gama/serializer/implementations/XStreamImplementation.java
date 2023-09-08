/*******************************************************************************************************
 *
 * XStreamImplementation.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
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
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.SavedAgent;
import msi.gama.outputs.IOutputManager;
import msi.gama.runtime.IScope;
import ummisco.gama.serializer.factory.StreamConverter;
import ummisco.gama.serializer.gamaType.converters.ConverterScope;

/**
 * The Class XStreamImplementation.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 7 août 2023
 */
public class XStreamImplementation extends AbstractSerialisationProcessor<SavedAgent> {

	/**
	 * Restore.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param sim
	 *            the current sim agt
	 * @param agt
	 *            the agt
	 * @date 7 août 2023
	 */
	@Override
	public void restoreFromSerialisedForm(final IAgent agent, final SavedAgent agt) {
		IScope scope = agent.getScope();
		// Update of the simulation
		agent.updateWith(scope, agt);
		// useful to recreate the random generator
		if (agent instanceof SimulationAgent sim) {
			final int rngUsage = sim.getRandomGenerator().getUsage();
			final String rngName = sim.getRandomGenerator().getRngName();
			final Double rngSeed = sim.getRandomGenerator().getSeed();

			final IOutputManager outputs = sim.getOutputManager();
			if (outputs != null) { outputs.step(scope); }

			// Recreate the random generator and set it to the same state as the saved one
			if (((ExperimentPlan) scope.getExperiment().getSpecies()).keepsSeed()) {
				sim.setRandomGenerator(new RandomUtils(rngSeed, rngName));
				sim.getRandomGenerator().setUsage(rngUsage);
			} else {
				sim.setRandomGenerator(new RandomUtils(scope.getExperiment().getRandomGenerator().next(), rngName));
			}
		}
	}

	@Override
	protected SavedAgent encodeToSerialisedForm(final IAgent agent) {
		IScope scope = agent.getScope();
		SavedAgent sa = new SavedAgent(scope, agent);
		if (agent instanceof SimulationAgent sim) {
			sa.put(HEADER_KEY, new SerialisedSimulationHeader(sim));
			if (sim.serializeHistory()) { sa.put(HISTORY_KEY, sim.getHistory()); }
		}
		return sa;
	}

	/**
	 * Write.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param objectToSerialise
	 *            the object to serialise
	 * @return the byte[]
	 * @date 8 août 2023
	 */
	@Override
	public byte[] write(final IScope scope, final SavedAgent objectToSerialise) {
		String s = StreamConverter.convertObjectToStream(scope, objectToSerialise);
		return s.getBytes();
	}

	/**
	 * Read.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param data
	 *            the data
	 * @return the saved agent
	 * @date 8 août 2023
	 */
	@Override
	public SavedAgent read(final IScope scope, final byte[] data) {
		final String previousState = new String(data);
		final XStream xstream = StreamConverter.loadAndBuild(scope, ConverterScope.class);
		return (SavedAgent) xstream.fromXML(previousState);
	}

	@Override
	public String getFormat() { return XML_FORMAT; }

	@Override
	public byte getFormatIdentifier() { return 3; }

	@Override
	public void prettyPrint() {

	}

}
