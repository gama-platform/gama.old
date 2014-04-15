/*********************************************************************************************
 * 
 *
 * 'FsmTransitionStatement.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.architecture.finite_state_machine;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.architecture.finite_state_machine.FsmTransitionStatement.TransitionValidator;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;

@symbol(name = FsmTransitionStatement.TRANSITION, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
@inside(kinds = { ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.BEHAVIOR })
@facets(value = { @facet(name = IKeyword.WHEN, type = IType.BOOL, optional = true),
	@facet(name = FsmTransitionStatement.TO, type = IType.ID, optional = false) }, omissible = IKeyword.WHEN)
@validator(TransitionValidator.class)
public class FsmTransitionStatement extends AbstractStatementSequence {

	private static final List<String> states = Arrays.asList(FsmStateStatement.STATE, IKeyword.USER_PANEL);

	public static class TransitionValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription desc) {
			IDescription sup = desc.getEnclosingDescription();
			String keyword = sup.getKeyword();
			if ( !states.contains(keyword) ) {
				desc.error("Transitions cannot be declared inside  " + keyword, IGamlIssue.WRONG_PARENT);
				return;
			}
			final String behavior = desc.getFacets().getLabel(TO);
			final SpeciesDescription sd = desc.getSpeciesContext();
			if ( !sd.hasBehavior(behavior) ) {
				desc.error("Behavior " + behavior + " does not exist in " + sd.getName(), IGamlIssue.UNKNOWN_BEHAVIOR,
					TO, behavior, sd.getName());
			}
		}

	}

	final IExpression when;

	/** Constant field TRANSITION. */
	public static final String TRANSITION = "transition";

	protected static final String TO = "to";

	public FsmTransitionStatement(final IDescription desc) {
		super(desc);
		String stateName = getLiteral(TO);
		setName(stateName);
		if ( getFacet(IKeyword.WHEN) != null ) {
			when = getFacet(IKeyword.WHEN);
		} else {
			when = new ConstantExpression(true);
		}
	}

	public boolean evaluatesTrueOn(final IScope scope) throws GamaRuntimeException {
		return Cast.asBool(scope, when.value(scope));
		// Normally, the agent is still in the "currentState" scope.
	}

}
