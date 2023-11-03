/*******************************************************************************************************
 *
 * AbstractExperimentController.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

import msi.gama.runtime.IScope;
import msi.gama.runtime.server.GamaServerExperimentConfiguration;

/**
 * The Class AbstractExperimentController.
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 24 oct. 2023
 */
public abstract class AbstractExperimentController implements IExperimentController {

	/** The scope. */
	protected IScope scope;

	/** The disposing. */
	protected boolean disposing;

	/** The server configuration. */
	protected GamaServerExperimentConfiguration serverConfiguration;

	/**
	 * Alive. Flag indicating that the scheduler is running (it should be alive unless the application is shutting down)
	 */
	protected volatile boolean experimentAlive = true;

	/**
	 * Paused. Flag indicating that the experiment is set to pause (used in stepping the experiment)
	 **/
	protected volatile boolean paused = true;

	/** AcceptingCommands. A flag indicating that the command thread is accepting commands */
	protected volatile boolean acceptingCommands = true;

	/** The lock. Used to pause the experiment */
	protected final Semaphore lock = new Semaphore(1);

	/** The experiment. */
	protected IExperimentPlan experiment;

	/** The commands. */
	protected volatile ArrayBlockingQueue<ExperimentCommand> commands = new ArrayBlockingQueue<>(10);

	/** The command thread. */
	protected Thread commandThread = new Thread(() -> {
		while (acceptingCommands) {
			try {
				processUserCommand(commands.take());
			} catch (final Exception e) {}
		}
	}, "Front end controller");

	@Override
	public IExperimentPlan getExperiment() { return experiment; }

	/**
	 * Sets the experiment.
	 *
	 * @param exp
	 *            the new experiment
	 */
	public void setExperiment(final IExperimentPlan exp) { this.experiment = exp; }

	/**
	 * Offer.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param command
	 *            the command
	 * @date 24 oct. 2023
	 */
	private void offer(final ExperimentCommand command) {
		if (experiment == null || isDisposing()) return;
		commands.offer(command);
	}

	/**
	 * Process user command.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param command
	 *            the command
	 * @date 24 oct. 2023
	 */
	protected abstract void processUserCommand(final ExperimentCommand command);

	/**
	 * Synchronous step.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 25 oct. 2023
	 */
	protected void synchronousStep() {
		processUserCommand(ExperimentCommand._STEP);

	}

	/**
	 * Synchronous step back.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 25 oct. 2023
	 */
	protected void synchronousStepBack() {
		processUserCommand(ExperimentCommand._BACK);
	}

	/**
	 * Synchronous start.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 25 oct. 2023
	 */
	protected void synchronousStart() {
		processUserCommand(ExperimentCommand._START);
	}

	/**
	 * Synchronous reload.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 25 oct. 2023
	 */
	protected void synchronousReload() {
		processUserCommand(ExperimentCommand._RELOAD);
	}

	/**
	 * Asynchronous pause.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 25 oct. 2023
	 */
	protected void asynchronousPause() {
		offer(ExperimentCommand._PAUSE);
	}

	/**
	 * Synchronous pause.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 25 oct. 2023
	 */
	protected void synchronousPause() {
		processUserCommand(ExperimentCommand._PAUSE);
	}

	/**
	 * Asynchronous step.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 25 oct. 2023
	 */
	protected void asynchronousStep() {
		offer(ExperimentCommand._STEP);
	}

	/**
	 * Asynchronous step back.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 25 oct. 2023
	 */
	protected void asynchronousStepBack() {
		offer(ExperimentCommand._BACK);
	}

	/**
	 * Asynchronous reload.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 25 oct. 2023
	 */
	protected void asynchronousReload() {
		offer(ExperimentCommand._RELOAD);
	}

	/**
	 * Synchronous open.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 25 oct. 2023
	 */
	protected void synchronousOpen() {
		processUserCommand(ExperimentCommand._OPEN);
	}

	/**
	 * Asynchronous start.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 25 oct. 2023
	 */
	protected void asynchronousStart() {
		offer(ExperimentCommand._START);
	}

	/**
	 * Asynchronous open.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 25 oct. 2023
	 */
	protected void asynchronousOpen() {
		offer(ExperimentCommand._OPEN);
	}

	@Override
	public boolean isDisposing() { return disposing; }

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	protected IScope getScope() { return scope == null ? experiment.getExperimentScope() : scope; }

	/**
	 * Process open.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param andWait
	 *            the and wait
	 * @date 23 oct. 2023
	 */
	@Override
	public void processOpen(final boolean andWait) {
		if (andWait) {
			synchronousOpen();
		} else {
			asynchronousOpen();
		}
	}

	/**
	 * Process pause.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param andWait
	 *            the and wait
	 * @date 23 oct. 2023
	 */
	@Override
	public void processPause(final boolean andWait) {
		if (andWait) {
			synchronousPause();
		} else {
			asynchronousPause();
		}
	}

	/**
	 * Process reload.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param andWait
	 *            the and wait
	 * @date 23 oct. 2023
	 */
	@Override
	public void processReload(final boolean andWait) {
		if (andWait) {
			synchronousReload();
		} else {
			asynchronousReload();
		}
	}

	/**
	 * Process step.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param andWait
	 *            the and wait
	 * @date 23 oct. 2023
	 */
	@Override
	public void processStep(final boolean andWait) {
		if (andWait) {
			synchronousStep();
		} else {
			asynchronousStep();
		}
	}

	/**
	 * Process back.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param andWait
	 *            the and wait
	 * @date 23 oct. 2023
	 */
	@Override
	public void processBack(final boolean andWait) {
		if (andWait) {
			synchronousStepBack();
		} else {
			asynchronousStepBack();
		}
	}

	/**
	 * Process start.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param andWait
	 *            the and wait
	 * @date 24 oct. 2023
	 */
	@Override
	public void processStart(final boolean andWait) {
		if (andWait) {
			synchronousStart();
		} else {
			asynchronousStart();
		}
	}

}
