/*******************************************************************************************************
 *
 * ExperimentBackwardAgent.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.experiment;

import java.util.ArrayList;
import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.RandomUtils;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.ExperimentPlan;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.kernel.simulation.SimulationPopulation;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.experiment;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.serializer.implementations.SerialisedSimulationRecorder;

/**
 * The Class ExperimentBackwardAgent.
 */
@experiment (IKeyword.RECORD)
@doc ("A type of gui experiment that records its previous states and allows the user to step backward")
public class ExperimentBackwardAgent extends ExperimentAgent {

	static {
		DEBUG.ON();
	}

	/**
	 * The Class ObsoleteExperimentBackwardAgent.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 7 août 2023
	 */
	@experiment (IKeyword.MEMORIZE)
	@doc ("A type of gui experiment that records its previous states and allows the user to step backward. This keyword is deprecated: 'record' should be used instead")
	public static class ObsoleteExperimentBackwardAgent extends ExperimentBackwardAgent {

		/**
		 * Instantiates a new obsolete experiment backward agent.
		 *
		 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
		 * @param s
		 *            the s
		 * @param index
		 *            the index
		 * @throws GamaRuntimeException
		 *             the gama runtime exception
		 * @date 7 août 2023
		 */
		public ObsoleteExperimentBackwardAgent(final IPopulation<? extends IAgent> s, final int index)
				throws GamaRuntimeException {
			super(s, index);
		}

	}

	/** The conf. */
	SerialisedSimulationRecorder recorder;

	/** The compressExpr. */
	final IExpression formatExpr, compressExpr;

	/** The compress. */
	boolean compress;

	/** The format. */
	String format;

	static {
		DEBUG.ON();
	}

	/**
	 * Instantiates a new experiment backward agent.
	 *
	 * @param s
	 *            the s
	 * @param index
	 *            the index
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public ExperimentBackwardAgent(final IPopulation<? extends IAgent> s, final int index) throws GamaRuntimeException {
		super(s, index);
		IExperimentPlan species = getSpecies();
		formatExpr = species.getFacet("format");
		compressExpr = species.getFacet("compress");
	}

	/**
	 * Redefinition of the callback method
	 *
	 * @see msi.gama.metamodel.agent.GamlAgent#_init_(msi.gama.runtime.IScope)
	 */
	@Override
	public Object _init_(final IScope scope) {
		super._init_(scope);
		compress = compressExpr == null ? false : Cast.asBool(scope, compressExpr.value(scope));
		format = formatExpr == null ? "binary" : formatExpr.literalValue();
		recorder = new SerialisedSimulationRecorder(format, compress);
		recorder.record(getSimulation());
		return this;
	}

	@Override
	public boolean step(final IScope scope) {
		// Do a normal step
		final boolean result = super.step(scope);
		// Save simulation state in the history
		SimulationPopulation sp = getSimulationPopulation();
		if (sp != null) {
			List<SimulationAgent> sims = new ArrayList<>(sp);
			for (SimulationAgent sim : sims) { recorder.record(sim); }
		}
		return result;
	}

	@Override
	public boolean backward(final IScope scope) {
		final boolean result = true;
		try {
			GAMA.runAndUpdateAll(() -> {
				for (SimulationAgent sim : getSimulationPopulation()) {
					if (recorder.canStepBack(sim)) {
						recorder.restore(sim);
						if (!((ExperimentPlan) this.getSpecies()).keepsSeed()) {
							sim.setRandomGenerator(
									new RandomUtils(super.random.next(), sim.getRandomGenerator().getRngName()));
						}
					}

				}
			});
		} finally {
			informStatus();
			scope.getGui().updateExperimentState(scope);
		}
		return result;
	}

	@Override
	public boolean canStepBack() {
		// DEBUG.OUT("Can step back avec cycle = "
		// + (getSimulation() == null ? "nil" : String.valueOf(getSimulation().getClock().getCycle())));
		return getSimulation() != null && getSimulation().getClock().getCycle() > 0;
	}

	@Override
	public boolean isRecord() { return true; }

}
