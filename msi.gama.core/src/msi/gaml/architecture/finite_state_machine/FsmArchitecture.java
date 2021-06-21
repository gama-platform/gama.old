/*******************************************************************************************************
 *
 * msi.gaml.architecture.finite_state_machine.FsmArchitecture.java, in plugin msi.gama.core, is part of the source code
 * of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.architecture.finite_state_machine;

import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gaml.architecture.reflex.ReflexArchitecture;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 12 sept. 2010
 *
 * @todo Description
 *
 */
@vars ({ @variable (
		name = IKeyword.STATE,
		type = IType.STRING,
		doc = @doc ("Returns the name of the current state of the agent")),
		@variable (
				name = IKeyword.STATES,
				type = IType.LIST,
				constant = true,
				doc = @doc ("Returns the list of all the states defined in the species")) })
@skill (
		name = IKeyword.FSM,
		concept = { IConcept.BEHAVIOR, IConcept.ARCHITECTURE },
		doc = @doc ("The Finite State Machine architecture allows to program agents using a finite set of states and conditional transitions between them"))
public class FsmArchitecture extends ReflexArchitecture {

	protected final Map<String, FsmStateStatement> states = GamaMapFactory.createUnordered();
	protected FsmStateStatement initialState;

	@Override
	protected void clearBehaviors() {
		super.clearBehaviors();
		states.clear();
	}

	@Override
	public void verifyBehaviors(final ISpecies context) {
		super.verifyBehaviors(context);
		for (final FsmStateStatement s : states.values()) {
			if (s.isInitial()) { initialState = s; }
		}
		if (initialState != null) { context.getVar(IKeyword.STATE).setValue(null, initialState.getName()); }
	}

	@getter (
			value = IKeyword.STATES,
			initializer = true)
	public IList<String> getStateNames(final IAgent agent) {
		return GamaListFactory.wrap(Types.STRING, states.keySet());
	}

	@setter (IKeyword.STATES)
	public void setStateNames(final IAgent agent, final IList<String> list) {}

	@getter (IKeyword.STATE)
	public String getStateName(final IAgent agent) {
		final FsmStateStatement currentState = (FsmStateStatement) agent.getAttribute(IKeyword.CURRENT_STATE);
		if (currentState == null) return null;
		return currentState.getName();
	}

	public FsmStateStatement getState(final String stateName) {
		return states.get(stateName);
	}

	@setter (IKeyword.STATE)
	public void setStateName(final IAgent agent, final String stateName) {
		if (stateName != null && states.containsKey(stateName)) { setCurrentState(agent, states.get(stateName)); }
	}

	@Override
	public void addBehavior(final IStatement c) {
		if (c instanceof FsmStateStatement) {
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
		if (scope.interrupted()) return null;
		final FsmStateStatement currentState = (FsmStateStatement) agent.getAttribute(IKeyword.CURRENT_STATE);
		if (currentState == null) return null;
		return scope.execute(currentState).getValue();
	}

	public void setCurrentState(final IAgent agent, final FsmStateStatement state) {
		final FsmStateStatement currentState = (FsmStateStatement) agent.getAttribute(IKeyword.CURRENT_STATE);
		if (currentState == state) return;
		// if ( currentState != null && currentState.hasExitActions() ) {
		// agent.setAttribute(IKeyword.STATE_TO_EXIT, currentState);
		// }
		agent.setAttribute(IKeyword.ENTER, true);
		agent.setAttribute(IKeyword.CURRENT_STATE, state);
	}

	/***
	 * What happens when the agent dies: calls the exit statement of the current state if it exists (see Issue #2865)
	 */
	@Override
	public boolean abort(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		if (scope.interrupted() || agent == null) return true;
		final FsmStateStatement currentState = (FsmStateStatement) agent.getAttribute(IKeyword.CURRENT_STATE);
		if (currentState == null) return true;
		currentState.haltOn(scope);
		// and we return the regular abort behavior
		return super.abort(scope);
	}
}
