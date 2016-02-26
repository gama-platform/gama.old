/*********************************************************************************************
 *
 *
 * 'FsmArchitecture.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.architecture.finite_state_machine;

import java.util.Map;
import gnu.trove.map.hash.THashMap;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.architecture.reflex.ReflexArchitecture;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IStatement;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 12 sept. 2010
 *
 * @todo Description
 *
 */
@vars({
	@var(name = IKeyword.STATE, type = IType.STRING, doc = @doc("Returns the current state in which the agent is") ),
	@var(name = IKeyword.STATES,
		type = IType.LIST,
		constant = true,
		doc = @doc("Returns the list of all possible states the agents can be in") ) })
@skill(name = IKeyword.FSM, concept = { IConcept.FSM, IConcept.BEHAVIOR, IConcept.SKILL },
	doc = @doc("The Finite State Machine architecture, that allows to program agents using a finite set of states and conditional transitions between them") )
public class FsmArchitecture extends ReflexArchitecture {

	protected final Map<String, FsmStateStatement> states = new THashMap();
	protected FsmStateStatement initialState;

	@Override
	protected void clearBehaviors() {
		super.clearBehaviors();
		states.clear();
	}

	@Override
	public void verifyBehaviors(final ISpecies context) {
		super.verifyBehaviors(context);
		for ( final FsmStateStatement s : states.values() ) {
			if ( s.isInitial() ) {
				initialState = s;
			}
		}
		if ( initialState != null ) {
			context.getVar(IKeyword.STATE).setValue(null, initialState.getName());
		}
	}

	@getter(value = IKeyword.STATES, initializer = true)
	public IList getStateNames(final IAgent agent) {
		return GamaListFactory.createWithoutCasting(Types.STRING, states.keySet());
	}

	@setter(IKeyword.STATES)
	public void setStateNames(final IAgent agent, final IList list) {}

	@getter(IKeyword.STATE)
	public String getStateName(final IAgent agent) {
		final FsmStateStatement currentState = (FsmStateStatement) agent.getAttribute(IKeyword.CURRENT_STATE);
		if ( currentState == null ) { return null; }
		return currentState.getName();
	}

	public FsmStateStatement getState(final String stateName) {
		return states.get(stateName);
	}

	@setter(IKeyword.STATE)
	public void setStateName(final IAgent agent, final String stateName) {
		if ( stateName != null && states.containsKey(stateName) ) {
			setCurrentState(agent, states.get(stateName));
		}
	}

	@Override
	public void addBehavior(final IStatement c) {
		if ( c instanceof FsmStateStatement ) {
			final FsmStateStatement state = (FsmStateStatement) c;
			states.put(state.getName(), state);
		} else {
			super.addBehavior(c);
		}
	}

	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		super.executeOn(scope);
		return executeCurrentState(scope);
	}

	protected Object executeCurrentState(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		if ( scope.interrupted() ) { return null; }
		final FsmStateStatement currentState = (FsmStateStatement) agent.getAttribute(IKeyword.CURRENT_STATE);
		if ( currentState == null ) { return null; }
		return currentState.executeOn(scope);
	}

	public void setCurrentState(final IAgent agent, final FsmStateStatement state) {
		final FsmStateStatement currentState = (FsmStateStatement) agent.getAttribute(IKeyword.CURRENT_STATE);
		if ( currentState == state ) { return; }
		// if ( currentState != null && currentState.hasExitActions() ) {
		// agent.setAttribute(IKeyword.STATE_TO_EXIT, currentState);
		// }
		agent.setAttribute(IKeyword.ENTER, true);
		agent.setAttribute(IKeyword.CURRENT_STATE, state);
	}
}
