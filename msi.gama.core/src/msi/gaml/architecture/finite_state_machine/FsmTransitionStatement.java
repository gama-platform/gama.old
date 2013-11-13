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
@inside(symbols = { FsmStateStatement.STATE })
@facets(value = { @facet(name = IKeyword.WHEN, type = IType.BOOL, optional = true),
	@facet(name = FsmTransitionStatement.TO, type = IType.ID, optional = false) }, omissible = IKeyword.WHEN)
@validator(TransitionValidator.class)
public class FsmTransitionStatement extends AbstractStatementSequence {

	// TODO En faire une sous classe de if ?

	

	public static class TransitionValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription desc) {
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
