/*******************************************************************************************************
 *
 * ThreadSkill.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.skills;

import java.util.concurrent.atomic.AtomicBoolean;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.AbstractAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;

/**
 * ThreadSkill : This class is intended to define the minimal set of behaviours required from an agent that is able to
 * run an action in a thread. Each member that has a meaning in GAML is annotated with the respective tags (vars,
 * getter, setter, init, action & args)
 *
 * @author taillandier 30 August 2022
 * @revised AD 25 September 2022 regarding the unit of the interval, the names and the management of multiple threads
 */

@doc ("The thread skill is intended to define the minimal set of behaviours required for agents that are able to run an action in a thread")
@skill (
		name = IKeyword.THREAD_SKILL,
		concept = { IConcept.SKILL, IConcept.SYSTEM })
public class ThreadSkill extends Skill {

	/** The Constant ACTION_NAME. */
	private static final String ACTION_NAME = "thread_action";

	/** The Constant START_THREAD. */
	private static final String START_THREAD = "start_thread";

	/** The Constant END_THREAD. */
	private static final String END_THREAD = "end_thread";

	/** The Constant THREAD_MEMORY. */
	private static final String THREAD_MEMORY = "%%thread_memory%%";

	/**
	 * primStartThread
	 *
	 * @param scope
	 *            the scope
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = START_THREAD,
			args = { @arg (
					name = "continuous",
					type = IType.BOOL,
					optional = true,
					doc = @doc ("if the thread should run continuously or just once")),
					@arg (
							name = "interval",
							type = IType.FLOAT,
							optional = true,
							doc = @doc ("Interval of machine time between two executions of the action. Default unit is in seconds, use explicit units to specify another, like 10 #ms")) },

			doc = @doc (
					examples = { @example ("do start_thread continuous: true;") },
					returns = "true if the thread was well created and started, false otherwise",
					value = "Start a new thread that will run the runnable_action action (continuously by default)."))
	public Boolean primStartThread(final IScope scope) throws GamaRuntimeException {
		Boolean continuous = !scope.hasArg("continuous") ? true : (Boolean) scope.getArg("continuous", IType.BOOL);
		Double interval = !scope.hasArg("interval") ? 0.1 : scope.getFloatArg("interval");
		ControlSubThread currentThread = (ControlSubThread) scope.getAgent().getAttribute(THREAD_MEMORY);
		if (currentThread == null) {
			currentThread = new ControlSubThread(scope.getAgent(), continuous, (int) (interval * 1000));
			scope.getAgent().setAttribute(THREAD_MEMORY, currentThread);
			currentThread.start();
		}
		return true;
	}

	/**
	 * Prim end thread.
	 *
	 * @param scope
	 *            the scope
	 * @return the boolean
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = END_THREAD,
			doc = @doc (
					examples = { @example ("do end_thread;") },
					returns = "true if the thread was well stopped, false otherwise",
					value = "End the current thread."))
	public Boolean primEndThread(final IScope scope) throws GamaRuntimeException {
		ControlSubThread currentThread = (ControlSubThread) scope.getAgent().getAttribute(THREAD_MEMORY);
		if (currentThread != null) {
			currentThread.stop();
			scope.getAgent().setAttribute(THREAD_MEMORY, null);
			return true;
		}
		return false;
	}

	/**
	 * Prim die.
	 *
	 * @param scope
	 *            the scope
	 * @return the object
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = "die",
			doc = @doc ("Kills the agent and disposes of it. Once dead, the agent cannot behave anymore"))
	public Object primDie(final IScope scope) throws GamaRuntimeException {
		primEndThread(scope);
		((AbstractAgent) scope.getAgent()).primDie(scope);
		return null;
	}

	/**
	 * primExternalFactorOnRemainingTime
	 *
	 * @param scope
	 *            the scope
	 * @return the double
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = ACTION_NAME)
	public Object primExternalFactorOnRemainingTime(final IScope scope) throws GamaRuntimeException {
		return null;
	}

	/**
	 * The Class ControlSubThread.
	 */
	public class ControlSubThread implements Runnable {

		/** The worker. */
		private Thread worker;

		/** The running. */
		private final AtomicBoolean running = new AtomicBoolean(false);

		/** The interval. */
		private final int interval;

		/** The agent. */
		private final IAgent agent;

		/** The continuous. */
		private final Boolean continuous;

		/**
		 * Instantiates a new control sub thread.
		 *
		 * @param ag
		 *            the ag
		 * @param cont
		 *            the cont
		 * @param sleepInterval
		 *            the sleep interval
		 */
		public ControlSubThread(final IAgent ag, final Boolean cont, final int sleepInterval) {
			interval = sleepInterval;
			agent = ag;
			continuous = cont;
		}

		/**
		 * Start.
		 */
		public void start() {
			worker = new Thread(this);
			worker.start();
		}

		/**
		 * Stop.
		 */
		public void stop() {
			running.set(false);
			Thread.currentThread().interrupt();

		}

		@Override
		public void run() {
			if (continuous) {
				running.set(true);
				while (running.get() && !agent.dead() && !agent.getScope().interrupted()) {
					try {
						Thread.sleep(interval);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						System.out.println("Thread was interrupted, Failed to complete operation");
					}

					ISpecies context = agent.getSpecies();
					IStatement action = context.getAction(ACTION_NAME);
					agent.getScope().copy("ThreadScope").execute(action, agent, null);
				}
			} else {
				ISpecies context = agent.getSpecies();
				IStatement action = context.getAction(ACTION_NAME);
				agent.getScope().copy("ThreadScope").execute(action, agent, null);
			}
		}

	}

}
