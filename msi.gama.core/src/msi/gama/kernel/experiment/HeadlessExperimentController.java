/*******************************************************************************************************
 *
 * HeadlessExperimentController.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * The Class HeadlessExperimentController.
 */
public class HeadlessExperimentController implements IExperimentController {

	/** The experiment. */
	private final IExperimentPlan experiment;

	/**
	 * Instantiates a new headless experiment controller.
	 *
	 * @param experiment
	 *            the experiment.
	 */
	public HeadlessExperimentController(final IExperimentPlan experiment) {
		this.experiment = experiment;
	}

	@Override
	public IExperimentPlan getExperiment() { return experiment; }

	@Override
	public void close() {
		experiment.dispose(); // will call own dispose() later
	}

	@Override
	public void schedule(final ExperimentAgent agent) {
		IScope scope = agent.getScope();
		try {
			if (!scope.init(agent).passed()) { scope.setInterrupted(); }
		} catch (final Throwable e) {
			if (!(e instanceof GamaRuntimeException)) {
				GAMA.reportError(scope, GamaRuntimeException.create(e, scope), true);
			}
		}

	}

}
