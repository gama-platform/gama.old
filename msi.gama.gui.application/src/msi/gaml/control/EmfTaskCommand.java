/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gaml.control;

import java.util.List;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.*;
import msi.gaml.commands.AbstractCommandSequence;

/**
 * The Class Task. Represents a task of the EMF model, a sequence of assignements and primitives
 */
@facets({ @facet(name = ISymbol.NAME, type = IType.ID, optional = false) })
@symbol(name = EmfTaskCommand.TASK, kind = ISymbolKind.BEHAVIOR)
@inside(symbols = ISpecies.EMF)
public class EmfTaskCommand extends AbstractCommandSequence {

	/**
	 * The Class IntReturnValue.
	 */
	public static class IntReturnValue {

		/** The i. */
		private int i;

		/**
		 * Instantiates a new int return value.
		 * 
		 * @param j the j
		 */
		private IntReturnValue(final int j) {
			i = j;
		}

	}

	static final String INTERRUPTIBLE = "interruptible";

	static final String RUN = "run";

	static final String PRIORITY = "priority";

	static final String WEIGHT = "weight";

	static final String TASK = "task";

	static final String END = "end";

	static final String MAX = "max";

	static final String MIN = "min";

	private EmfPriorityCommand priority = null;

	private EmfDurationCommand duration = null;

	private EmfConditionsCommand conditions = null;

	private EmfEndCommand end = null;

	public EmfTaskCommand(final IDescription desc) {
		super(desc);
		setName(getLiteral(ISymbol.NAME));
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		for ( ISymbol c : commands ) {
			if ( c instanceof EmfPriorityCommand ) {
				priority = (EmfPriorityCommand) c;
			} else if ( c instanceof EmfDurationCommand ) {
				duration = (EmfDurationCommand) c;
			} else if ( c instanceof EmfEndCommand ) {
				end = (EmfEndCommand) c;
			} else if ( c instanceof EmfConditionsCommand ) {
				conditions = (EmfConditionsCommand) c;
			}
		}
		commands.remove(priority);
		commands.remove(duration);
		commands.remove(end);
		commands.remove(conditions);
		super.setChildren(commands);
	}

	public boolean canStopOn(final IScope scope) throws GamaRuntimeException {
		IAgent ag = scope.getAgentScope();
		double activityLevel = (Double) ag.getAttribute(EmfBehavior.ACTIVITY_LEVEL);
		int ag_duration = (Integer) ag.getAttribute(EmfBehavior.DURATION);
		boolean isInterruptible = (Boolean) ag.getAttribute(INTERRUPTIBLE);
		IExpression durationMin = duration == null ? null : duration.getFacet(EmfTaskCommand.MIN);
		return (activityLevel <= 0.0 || durationMin == null || Cast.asFloat(scope,
			durationMin.value(scope)) <= ag_duration) &&
			isInterruptible;
	}

	/**
	 * @throws GamaRuntimeException Compute priority.
	 * 
	 * @param agent the agent
	 * 
	 * @return the float
	 */
	public double computePriorityOn(final IScope scope) throws GamaRuntimeException {
		return priority == null ? 1.0d : Cast.asFloat(scope, priority.getFacet(ISymbol.VALUE)
			.value(scope));
	}

	/**
	 * @throws GamaRuntimeException This implementation of run only executes one action at a time.
	 * 
	 * @param ag the ag
	 * 
	 * @return the command status
	 * 
	 * @throws GamlException
	 */

	@Override
	public Object privateExecuteIn(final IScope agent) throws GamaRuntimeException {
		final ExecutionStatus actionStatus = recursiveExecuteOn(agent);
		double al =
			Cast.asFloat(agent, agent.getAgentScope().getAttribute(EmfBehavior.ACTIVITY_LEVEL));
		al = --al < 0 ? 0 : al;
		agent.getAgentScope().setAttribute(EmfBehavior.ACTIVITY_LEVEL, al);
		return actionStatus;
	}

	/**
	 * @throws GamaRuntimeException Halt.
	 * 
	 * @param ag the ag
	 * 
	 * @throws GamlException *
	 * @see msi.gaml.commands.Sequence#deactivate()
	 */

	public void haltOn(final IScope scope) throws GamaRuntimeException {
		IAgent agent = scope.getAgentScope();
		if ( end != null &&
			!isNormallyFinished(
				ExecutionStatus.valueOf(Cast.asInt(scope, agent.getAttribute(EmfBehavior.STATUS))),
				Cast.asInt(scope, agent.getAttribute(EmfBehavior.EXECUTION_POINT))) ) {
			end.executeOn(scope);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gaml.commands.SequenceCommand#shouldStopOn(msi.gama.metamodel.agent
	 * .interfaces.BasicEntity)
	 */

	public boolean shouldStopOn(final IScope scope) throws GamaRuntimeException {
		IAgent agent = scope.getAgentScope();
		final boolean sequenceIsFinished =
			Cast.asInt(scope, agent.getAttribute(EmfBehavior.EXECUTION_POINT)) >= commands.length;
		if ( sequenceIsFinished ) { return true; }
		boolean whileIsTrue;
		boolean untilIsFalse;
		boolean durationIsOk;

		if ( conditions == null ) {
			whileIsTrue = true;
			untilIsFalse = true;
		} else {
			IExpression whileExpr = conditions.getFacet(ISymbol.WHILE);
			IExpression untilExpr = conditions.getFacet(ISymbol.UNTIL);
			whileIsTrue = whileExpr == null || Cast.asBool(scope, whileExpr.value(scope));
			untilIsFalse = untilExpr == null || !Cast.asBool(scope, untilExpr.value(scope));
		}
		if ( duration == null ) {
			durationIsOk = true;
		} else {
			IExpression durationMax = duration.getFacet(EmfTaskCommand.MAX);
			durationIsOk =
				durationMax == null ||
					Cast.asFloat(scope, durationMax.value(scope)) >= Cast.asFloat(scope,
						agent.getAttribute(EmfBehavior.DURATION));
		}
		return (Cast.asFloat(scope, agent.getAttribute(EmfBehavior.ACTIVITY_LEVEL)) <= 0.0 || canStopOn(scope) &&
			!(whileIsTrue && untilIsFalse && durationIsOk)) &&
			Cast.asBool(scope, agent.getAttribute(INTERRUPTIBLE));
	}

	/**
	 * @throws GamaRuntimeException Run action on.
	 * 
	 * @param agent the agent
	 * @param m the m
	 * 
	 * @return the command status
	 */
	private ExecutionStatus runActionOn(final IScope scope, final IntReturnValue m)
		throws GamaRuntimeException {
		if ( m.i >= commands.length ) { return ExecutionStatus.end; }
		// OutputManager.debug("==> Running " + actions.get(m.i) + " on " + agent);
		ICommand c = commands[m.i];
		c.executeOn(scope);
		final ExecutionStatus status = scope.getStatus();
		final boolean loop = c.getDescription().getKeyword().equals(ISymbol.REPEAT);
		switch (status) {
			case skipped:
			case success:
			case end:
				m.i++;
			break;
			case failure:
				m.i += commands.length;
			break;
			case running:
				if ( !loop ) {
					m.i++;
				}
			break;
		}
		return status;
	}

	/**
	 * Checks if is normally finished.
	 * 
	 * @param actionStatus the action status
	 * @param executionPoint the execution point
	 * 
	 * @return true, if is normally finished
	 */
	private boolean isNormallyFinished(final ExecutionStatus actionStatus, final int executionPoint) {
		final boolean isEndedByAction = actionStatus == ExecutionStatus.end;
		final boolean hasReachedLastAction =
			(actionStatus == ExecutionStatus.success || actionStatus == ExecutionStatus.skipped) &&
				executionPoint == commands.length;
		return isEndedByAction || hasReachedLastAction;
	}

	/**
	 * @throws GamaRuntimeException recursive run of actions.
	 * 
	 * @param agent the agent
	 * 
	 * @return the command status
	 * 
	 * @throws GamlException
	 */
	private ExecutionStatus recursiveExecuteOn(final IScope scope) throws GamaRuntimeException {
		IAgent agent = scope.getAgentScope();
		final IntReturnValue m =
			new IntReturnValue(Cast.asInt(scope, agent.getAttribute(EmfBehavior.EXECUTION_POINT)));
		final ExecutionStatus actionStatus = runActionOn(scope, m);
		agent.setAttribute(EmfBehavior.EXECUTION_POINT, m.i);
		if ( actionStatus == ExecutionStatus.skipped &&
			Cast.asInt(scope, agent.getAttribute(EmfBehavior.EXECUTION_POINT)) < commands.length ) {
			recursiveExecuteOn(scope);
		} else {
			agent.setAttribute(EmfBehavior.DURATION,
				(Integer) agent.getAttribute(EmfBehavior.DURATION) +
					scope.getSimulationScope().getScheduler().getStep());
			if ( actionStatus == ExecutionStatus.failure || actionStatus == ExecutionStatus.end ) {
				agent.setAttribute(EmfBehavior.ACTIVITY_LEVEL, 0d);
			}
		}
		return actionStatus;
	}
}