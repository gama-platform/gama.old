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
import ummisco.gama.serializer.factory.StreamConverter;
import ummisco.gama.serializer.gamaType.converters.ConverterScope;

/**
 * The Class XStreamImplementation.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 7 août 2023
 */
public class XStreamImplementation extends AbstractSerialisationImplementation<SavedAgent> {

	/**
	 * Instantiates a new x stream implementation.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param zip
	 *            the zip
	 * @param save
	 *            the save
	 * @date 7 août 2023
	 */
	public XStreamImplementation(final boolean zip, final boolean save) {
		super(zip, save);
	}

	/**
	 * Restore.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param currentSimAgt
	 *            the current sim agt
	 * @param agt
	 *            the agt
	 * @date 7 août 2023
	 */
	@Override
	public void restoreFromSerialisedForm(final SimulationAgent currentSimAgt, final SavedAgent agt) {
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
			currentSimAgt
					.setRandomGenerator(new RandomUtils(scope.getExperiment().getRandomGenerator().next(), rngName));
		}
	}

	@Override
	protected SavedAgent encodeToSerialisedForm(final SimulationAgent sim) {
		return new SavedAgent(scope, sim);
	}

	@Override
	protected byte[] write(final SavedAgent objectToSerialise) {
		String s = StreamConverter.convertObjectToStream(scope, objectToSerialise);
		return s.getBytes();
	}

	@Override
	protected SavedAgent read(final byte[] data) {
		final String previousState = new String(data);
		final XStream xstream = StreamConverter.loadAndBuild(scope, ConverterScope.class);
		return (SavedAgent) xstream.fromXML(previousState);
	}

	@Override
	protected String getFormat() { return "xml"; }

}
