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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
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
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.combination;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.architecture.finite_state_machine.FsmStateStatement.StateValidator;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.*;
import msi.gaml.operators.Cast;
import msi.gaml.statements.*;
import msi.gaml.types.IType;

/**
 * The Class FsmStateStatement.
 * 
 * @author drogoul
 */

@symbol(name = FsmStateStatement.STATE, kind = ISymbolKind.BEHAVIOR, with_sequence = true, unique_name = true)
@inside(symbols = IKeyword.FSM, kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT, ISymbolKind.MODEL })
@facets(value = { @facet(name = FsmStateStatement.INITIAL, type = IType.BOOL, optional = true),
	@facet(name = FsmStateStatement.FINAL, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.NAME, type = IType.ID, optional = false) }, combinations = {
	@combination({ IKeyword.NAME, FsmStateStatement.FINAL }), @combination({ IKeyword.NAME }),
	@combination({ IKeyword.NAME, FsmStateStatement.INITIAL }) }, omissible = IKeyword.NAME)
@validator(StateValidator.class)
public class FsmStateStatement extends AbstractStatementSequence {

	

	public static class StateValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription description) {
			if ( !Assert.nameIsValid(description) ) { return; }
			Facets ff = description.getFacets();
			IExpression expr = ff.getExpr(INITIAL);
			if ( IExpressionFactory.TRUE_EXPR.equals(expr) ) {
				assertNoOther(description, INITIAL);
			} else {
				expr = ff.getExpr(FINAL);
				if ( IExpressionFactory.TRUE_EXPR.equals(expr) ) {
					assertNoOther(description, FINAL);
				} else {
					assertAtLeastOne(description, INITIAL);
				}
			}
		}

		private void assertNoOther(final IDescription desc, final String facet) {
			final IDescription sd = desc.getEnclosingDescription();
			if ( sd == null ) { return; }
			for ( final IDescription child : sd.getChildren() ) {
				if ( child.equals(desc) || !child.getKeyword().equals(STATE) ) {
					continue;
				}
				IExpression expr = child.getFacets().getExpr(facet);
				if ( IExpressionFactory.TRUE_EXPR.equals(expr) ) {
					final String error = "Only one " + facet + " state is allowed.";
					child.error(error, IGamlIssue.DUPLICATE_DEFINITION, facet, TRUE);
				}
			}
		}

		private void assertAtLeastOne(final IDescription desc, final String facet) {
			final IDescription sd = desc.getEnclosingDescription();
			if ( sd == null ) { return; }
			for ( final IDescription child : sd.getChildren() ) {
				if ( child.getKeyword().equals(STATE) ) {
					final IExpression expr = child.getFacets().getExpr(facet);
					if ( expr == null ) {
						continue;
					}
					if ( IExpressionFactory.TRUE_EXPR.equals(expr) ) { return; }
				}
			}
			final String error = "No " + facet + " state defined";
			sd.error(error, IGamlIssue.MISSING_DEFINITION, sd.getUnderlyingElement(null), desc.getKeyword(), facet,
				TRUE);
		}
	}

	public static final String STATE_MEMORY = "state_memory";

	public static final String INITIAL = "initial";
	public static final String FINAL = "final";
	protected static final String STATE = "state";
	public static final String ENTER = "enter";
	public static final String EXIT = "exit";
	private FsmEnterStatement enterActions = null;
	private FsmExitStatement exitActions = null;
	List<FsmTransitionStatement> transitions = new ArrayList();
	private int transitionsSize;
	boolean isInitial;
	boolean isFinal;

	public FsmStateStatement(final IDescription desc) {
		super(desc);
		setName(getLiteral(IKeyword.NAME)); // A VOIR
		isInitial = Cast.asBool(null, getLiteral(FsmStateStatement.INITIAL));
		isFinal = Cast.asBool(null, getLiteral(FsmStateStatement.FINAL));
	}

	@Override
	public void setChildren(final List<? extends ISymbol> children) {
		for ( final ISymbol c : children ) {
			if ( c instanceof FsmEnterStatement ) {
				enterActions = (FsmEnterStatement) c;
			} else if ( c instanceof FsmExitStatement ) {
				exitActions = (FsmExitStatement) c;
			} else if ( c instanceof FsmTransitionStatement ) {
				transitions.add((FsmTransitionStatement) c);
			}
		}
		children.remove(enterActions);
		children.remove(exitActions);
		children.removeAll(transitions);
		transitionsSize = transitions.size();
		super.setChildren(children);
	}

	protected boolean beginExecution(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = scope.getAgentScope();
		if ( scope.interrupted() ) { return false; }
		Map<String, Object> memory = (Map) agent.getAttribute(STATE_MEMORY);
		if ( memory == null ) {
			memory = new HashMap();
			agent.setAttribute(STATE_MEMORY, memory);
		}
		for ( final Map.Entry<String, Object> entry : memory.entrySet() ) {
			scope.addVarWithValue(entry.getKey(), entry.getValue());
		}
		final Boolean enter = (Boolean) agent.getAttribute(ENTER);
		if ( enter ) {
			final FsmStateStatement stateToExit = (FsmStateStatement) agent.getAttribute(IKeyword.STATE_TO_EXIT);
			if ( stateToExit != null ) {
				stateToExit.haltOn(scope);
			}
			if ( agent.dead() ) { return false; }
			memory.clear();
			scope.removeAllVars();
			if ( enterActions != null ) {
				enterActions.executeOn(scope);
			}
			agent.setAttribute(IKeyword.STATE_TO_EXIT, null);
			agent.setAttribute(ENTER, false);
		}
		if ( agent.dead() ) { return false; }
		return true;
	}

	protected Object bodyExecution(final IScope scope) throws GamaRuntimeException {
		return super.privateExecuteIn(scope);
	}

	protected String evaluateTransitions(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = scope.getAgentScope();
		for ( int i = 0; i < transitionsSize; i++ ) {
			final FsmTransitionStatement transition = transitions.get(i);

			if ( /* agent.isEnabled(futureState) && */transition.evaluatesTrueOn(scope) ) {
				final String futureState = transition.getName();
				transition.executeOn(scope);
				scope.setAgentVarValue(agent, STATE, futureState);
				return futureState;
			}
		}
		if ( !agent.dead() ) {
			scope.saveAllVarValuesIn((Map) agent.getAttribute(STATE_MEMORY));
		}
		return name;

	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		if ( !beginExecution(scope) ) { return null; }
		bodyExecution(scope);
		return evaluateTransitions(scope);
	}

	public void haltOn(final IScope scope) throws GamaRuntimeException {
		if ( exitActions != null ) {
			exitActions.executeOn(scope);
		}
	}

	public FsmExitStatement getExitStatement() {
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
