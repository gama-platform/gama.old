/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.architecture.finite_state_machine;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.*;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.architecture.reflex.ReflexArchitecture;
import msi.gaml.commands.ICommand;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 12 sept. 2010
 * 
 * @todo Description
 * 
 */
@vars({ @var(name = IKeyword.STATE, type = IType.STRING_STR),
	@var(name = IKeyword.STATES, type = IType.LIST_STR, constant = true) })
@skill(IKeyword.FSM)
public class FsmArchitecture extends ReflexArchitecture {

	protected Map<String, FsmStateCommand> states = new HashMap();
	protected GamaList<String> stateNames;
	private FsmStateCommand initialState;
	private FsmStateCommand finalState;

	@Override
	public void verifyBehaviors(final ISpecies context) {
		super.verifyBehaviors(context);
		// hasBehavior = hasBehavior || states.size() > 0;
		for ( final FsmStateCommand s : states.values() ) {
			if ( s.isInitial() ) {
				if ( initialState == null ) {
					initialState = s;
				} else {
					initialState.error("Only one initial state allowed", FsmStateCommand.INITIAL);
					s.error("Only one initial state allowed", FsmStateCommand.INITIAL);
				}
			}
			if ( s.isFinal() ) {
				if ( finalState == null ) {
					finalState = s;
				} else {
					finalState.error("Only one final state allowed", FsmStateCommand.FINAL);
					s.error("Only one final state allowed", FsmStateCommand.FINAL);
				}
			}
		}
		if ( initialState == null ) {
			context.error("No initial state defined");
		}
		context.getVar(IKeyword.STATE).setValue(initialState.getName());
		stateNames = new GamaList(states.keySet());
	}

	@getter(var = IKeyword.STATES, initializer = true)
	public GamaList getStateNames(final IAgent agent) {
		return stateNames;
	}

	@setter(IKeyword.STATES)
	public void setStateNames(final IAgent agent, final GamaList list) {

	}

	@getter(var = IKeyword.STATE)
	public String getStateName(final IAgent agent) {
		FsmStateCommand currentState = (FsmStateCommand) agent.getAttribute(IKeyword.CURRENT_STATE);
		if ( currentState == null ) { return null; }
		return currentState.getName();
	}

	public FsmStateCommand getState(final String stateName) {
		return states.get(stateName);
	}

	@setter(IKeyword.STATE)
	public void setStateName(final IAgent agent, final String stateName) {
		if ( stateName != null && states.containsKey(stateName) ) {
			setCurrentState(agent, states.get(stateName));
		}
	}

	@Override
	public void addBehavior(final ICommand c) {
		if ( c instanceof FsmStateCommand ) {
			FsmStateCommand state = (FsmStateCommand) c;
			states.put(state.getName(), state);
		} else {
			super.addBehavior(c);
		}
	}

	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		// if ( !hasBehavior ) { return null; }
		super.executeOn(scope);
		IGamlAgent agent = getCurrentAgent(scope);
		if ( agent.dead() ) { return null; }
		FsmStateCommand currentState = (FsmStateCommand) agent.getAttribute(IKeyword.CURRENT_STATE);
		if ( currentState == null ) { return null; }
		return currentState.executeOn(scope);
	}

	public void setCurrentState(final IAgent agent, final FsmStateCommand state) {
		FsmStateCommand currentState = (FsmStateCommand) agent.getAttribute(IKeyword.CURRENT_STATE);
		if ( currentState == state ) { return; }
		if ( currentState != null && currentState.hasExitActions() ) {
			agent.setAttribute(IKeyword.STATE_TO_EXIT, currentState);
		}
		agent.setAttribute(IKeyword.ENTER, true);
		agent.setAttribute(IKeyword.CURRENT_STATE, state);
	}
}
