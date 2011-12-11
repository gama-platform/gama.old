/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.control;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.util.ExecutionStatus;
import msi.gaml.agents.IGamlAgent;

/**
 * Written by drogoul Modified on 12 sept. 2010
 * 
 * @todo Description
 * 
 */

@skill(ISpecies.EMF)
public class EmfBehavior extends ReflexControl {

	public static final String						TASK			= "task";

	public static final String						ACTIVITY_LEVEL	= "activity_level";

	public static final String						DURATION		= "duration";

	public static final String						EXECUTION_POINT	= "execution_point";

	public static final String						STATUS			= "status";

	public static final String						TASK_MEMORY		= "task_memory";

	protected final HashMap<String, EmfTaskCommand>	tasks			=
																		new HashMap<String, EmfTaskCommand>();
	protected EmfTaskCommand						currentTask		= null;

	@Override
	public void addBehavior(final ICommand c) {
		if ( c instanceof EmfTaskCommand ) {
			EmfTaskCommand task = (EmfTaskCommand) c;
			tasks.put(task.getName(), task);
		} else {
			super.addBehavior(c);
		}
	}

	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		super.executeOn(scope);
		IGamlAgent agent = getCurrentAgent(scope);
		if ( agent.dead() ) { return null; }
		currentTask = (EmfTaskCommand) agent.getAttribute(TASK);
		if ( currentTask != null ) {
			if ( currentTask.shouldStopOn(scope) ) {
				stopCurrentTask(agent, scope);
			} else if ( !currentTask.canStopOn(scope) ) {
				executeCurrentTask(agent, scope);
				return null;
			}
		}
		selectActivableTask(agent, scope);
		if ( currentTask != null ) {
			executeCurrentTask(agent, scope);
		}
		return null;
	}

	private EmfTaskCommand selectActivableTask(final IGamlAgent agent, final IScope scope)
		throws GamaRuntimeException {
		EmfTaskCommand selected = currentTask;
		double maxVal = selected == null ? 0f : (Double) agent.getAttribute(ACTIVITY_LEVEL);
		for ( final EmfTaskCommand t : tasks.values() ) {
			if ( t != currentTask /* && agent.isEnabled(t.getName()) */) {
				final double p = t.computePriorityOn(scope);
				if ( p > maxVal ) {
					maxVal = p;
					selected = t;
				}
			}
		}
		if ( selected != null && selected != currentTask ) {
			if ( currentTask != null ) {
				stopCurrentTask(agent, scope);
			}
			currentTask = selected;
			agent.setAttribute(TASK, selected);
			agent.setAttribute(ACTIVITY_LEVEL, maxVal);
			agent.setAttribute(DURATION, 0);
			agent.setAttribute(EXECUTION_POINT, 0);
		}
		return selected;
	}

	private void stopCurrentTask(final IGamlAgent agent, final IScope scope)
		throws GamaRuntimeException {
		if ( agent.dead() ) { return; }
		currentTask.haltOn(scope);
		((Map) agent.getAttribute(TASK_MEMORY)).clear();
		currentTask = null;
		agent.setAttribute(ACTIVITY_LEVEL, 0d);
		agent.setAttribute(DURATION, 0);
		agent.setAttribute(EXECUTION_POINT, 0);
	}

	private Object executeCurrentTask(final IAgent agent, final IScope scope)
		throws GamaRuntimeException {
		if ( agent.dead() ) { return null; }
		Map<String, Object> memory = (Map) agent.getAttribute(TASK_MEMORY);
		if ( memory == null ) {
			memory = new HashMap();
			agent.setAttribute(TASK_MEMORY, memory);
		}

		for ( Map.Entry<String, Object> entry : memory.entrySet() ) {
			scope.addVarWithValue(entry.getKey(), entry.getValue());
		}
		Object result = currentTask.executeOn(scope);
		ExecutionStatus actionStatus = scope.getStatus();
		agent.setAttribute(STATUS, ExecutionStatus.intValueOf(actionStatus));
		if ( actionStatus != ExecutionStatus.end && actionStatus != ExecutionStatus.failure ) {
			scope.saveAllVarValuesIn((Map) agent.getAttribute(TASK_MEMORY));
		}
		return result;
	}

	// @Override
	// public void verifyBehaviors(final IExecutionContext context) throws GamlException {
	// super.verifyBehaviors(context);
	// hasBehavior = hasBehavior || tasks.size() > 0;
	// }
}
