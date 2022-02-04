/*******************************************************************************************************
 *
 * AbstractExperimentScheduler.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import msi.gama.common.interfaces.IStepable;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * The Class AbstractExperimentScheduler.
 */
public abstract class AbstractExperimentScheduler implements IExperimentScheduler {

	/** The to step. */
	protected final Map<IStepable, IScope> toStep = Collections.synchronizedMap(new LinkedHashMap<>());
	/** The to stop. */
	protected final Set<IStepable> toStop = new CopyOnWriteArraySet<>();

	/**
	 * Dispose.
	 */
	@Override
	public void dispose() {
		toStop.clear();
		toStep.clear();
	}

	/**
	 * Schedule.
	 *
	 * @param stepable
	 *            the stepable
	 * @param scope
	 *            the scope
	 */
	@Override
	public void schedule(final IStepable stepable, final IScope scope) {
		toStep.put(stepable, scope);
		try {
			if (!scope.init(stepable).passed()) {
				toStop.add(stepable);
				scope.setInterrupted();
			}
		} catch (final Throwable e) {
			if (scope != null && scope.interrupted()) {
				toStop.add(stepable);
			} else if (!(e instanceof GamaRuntimeException)) {
				GAMA.reportError(scope, GamaRuntimeException.create(e, scope), true);
			}
		}

	}

	/**
	 * Step.
	 *
	 * @throws InterruptedException
	 */
	protected void step() {
		try {
			toStep.forEach((stepable, scope) -> {
				if (!scope.step(stepable).passed()) {
					toStop.add(stepable);
					scope.setInterrupted();
				}
			});
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		// Cleaning
		if (toStop.isEmpty()) return;
		for (final IStepable s : toStop) { toStep.remove(s); }
		if (toStep.isEmpty()) { this.pause(); }
		toStop.clear();
	}

}
