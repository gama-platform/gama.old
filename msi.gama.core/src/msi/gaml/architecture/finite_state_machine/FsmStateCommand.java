/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
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
import msi.gama.common.interfaces.*;

import msi.gama.metamodel.agent.IGamlAgent;
import msi.gama.precompiler.*;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

import msi.gaml.commands.AbstractCommandSequence;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

/**
 * The Class StateCommand.
 * 
 * @author drogoul
 */

@symbol(name = FsmStateCommand.STATE, kind = ISymbolKind.BEHAVIOR)
@inside(symbols = IKeyword.FSM, kinds = { ISymbolKind.SPECIES })
@facets(value = { @facet(name = FsmStateCommand.INITIAL, type = IType.BOOL_STR, optional = true),
	@facet(name = FsmStateCommand.FINAL, type = IType.BOOL_STR, optional = true),
	@facet(name = IKeyword.NAME, type = IType.ID, optional = false) }, combinations = {
	@combination({ IKeyword.NAME, FsmStateCommand.FINAL }), @combination({ IKeyword.NAME }),
	@combination({ IKeyword.NAME, FsmStateCommand.INITIAL }) })
public class FsmStateCommand extends AbstractCommandSequence {

	public static final String		STATE_MEMORY	= "state_memory";

	protected static final String	INITIAL			= "initial";
	protected static final String	FINAL			= "final";
	protected static final String	STATE			= "state";
	static final String				ENTER			= "enter";
	static final String				EXIT			= "exit";
	private FsmEnterCommand			enterActions	= null;
	private FsmExitCommand			exitActions		= null;
	List<FsmTransitionCommand>		transitions		= new ArrayList();
	private int						transitionsSize;
	boolean							isInitial;
	boolean							isFinal;

	public FsmStateCommand(final IDescription desc) {
		super(desc);
		setName(getLiteral(IKeyword.NAME)); // A VOIR
		isInitial = Cast.asBool(null, getLiteral(FsmStateCommand.INITIAL));
		isFinal = Cast.asBool(null, getLiteral(FsmStateCommand.FINAL));
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		for ( ISymbol c : commands ) {
			if ( c instanceof FsmEnterCommand ) {
				enterActions = (FsmEnterCommand) c;
			} else if ( c instanceof FsmExitCommand ) {
				exitActions = (FsmExitCommand) c;
			} else if ( c instanceof FsmTransitionCommand ) {
				transitions.add((FsmTransitionCommand) c);
			}
		}
		commands.remove(enterActions);
		commands.remove(exitActions);
		commands.removeAll(transitions);
		transitionsSize = transitions.size();
		super.setChildren(commands);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		IGamlAgent agent = (IGamlAgent) scope.getAgentScope();
		if ( agent.dead() ) { return null; }
		Map<String, Object> memory = (Map) agent.getAttribute(STATE_MEMORY);
		if ( memory == null ) {
			memory = new HashMap();
			agent.setAttribute(STATE_MEMORY, memory);
		}
		for ( Map.Entry<String, Object> entry : memory.entrySet() ) {
			scope.addVarWithValue(entry.getKey(), entry.getValue());
		}
		Boolean enter = (Boolean) agent.getAttribute(ENTER);
		if ( enter ) {
			FsmStateCommand stateToExit =
				(FsmStateCommand) agent.getAttribute(IKeyword.STATE_TO_EXIT);
			if ( stateToExit != null ) {
				stateToExit.haltOn(scope);
			}
			if ( agent.dead() ) { return null; }
			memory.clear();
			scope.removeAllVars();
			if ( enterActions != null ) {
				enterActions.executeOn(scope);
			}
			agent.setAttribute(IKeyword.STATE_TO_EXIT, null);
			agent.setAttribute(ENTER, false);
		}
		if ( agent.dead() ) { return null; }
		super.privateExecuteIn(scope);

		for ( int i = 0; i < transitionsSize; i++ ) {
			final FsmTransitionCommand transition = transitions.get(i);

			if ( /* agent.isEnabled(futureState) && */transition.evaluatesTrueOn(scope) ) {
				final String futureState = transition.getName();
				transition.executeOn(scope);
				scope.setAgentVarValue(agent, STATE, futureState);
				return futureState;
			}
		}
		scope.saveAllVarValuesIn(memory);
		return name;
	}

	public void haltOn(final IScope scope) throws GamaRuntimeException {
		if ( exitActions != null ) {
			exitActions.executeOn(scope);
		}
	}

	public FsmExitCommand getExitCommand() {
		return exitActions;
	}

	public boolean hasExitActions() {
		return exitActions != null;
	}

	public boolean isInitial() {
		return isInitial;
	}

	public boolean isFinal() {
		return isFinal;
	}

}
