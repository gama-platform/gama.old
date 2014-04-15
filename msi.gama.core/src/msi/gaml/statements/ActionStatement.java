/*********************************************************************************************
 * 
 * 
 * 'ActionStatement.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.statements;

import gnu.trove.set.hash.TLinkedHashSet;
import java.util.Set;
import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
import msi.gaml.statements.ActionStatement.ActionValidator;
import msi.gaml.types.*;

/**
 * The Class ActionCommand.
 * 
 * @author drogoul
 */
@symbol(name = IKeyword.ACTION, kind = ISymbolKind.ACTION, with_sequence = true, with_args = true, unique_name = true)
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT, ISymbolKind.MODEL })
@facets(value = { @facet(name = IKeyword.NAME, type = IType.ID, optional = false),
	@facet(name = IKeyword.TYPE, type = IType.TYPE_ID, optional = true),
	@facet(name = IKeyword.OF, type = IType.TYPE_ID, optional = true),
	@facet(name = IKeyword.INDEX, type = IType.TYPE_ID, optional = true),
	@facet(name = IKeyword.VIRTUAL, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.PERTINENCE, type = IType.FLOAT, optional = true) }, omissible = IKeyword.NAME)
@validator(ActionValidator.class)
public class ActionStatement extends AbstractStatementSequence implements IStatement.WithArgs {

	public static class ActionValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription description) {
			if ( Assert.nameIsValid(description) ) {
				assertReturnedValueIsOk((StatementDescription) description);
			}

		}

		private void assertReturnedValueIsOk(final StatementDescription cd) {
			final Set<StatementDescription> returns = new TLinkedHashSet();
			cd.collectChildren(RETURN, returns);
			final IType at = cd.getType();
			if ( at == Types.NO_TYPE ) { return; }
			// Primitives dont need to be ckecked
			// if ( cd.getKeyword().equals(PRIMITIVE) ) { return; }
			if ( returns.isEmpty() ) {
				cd.error("Action " + cd.getName() + " must return a result of type " + at, IGamlIssue.MISSING_RETURN);
				return;
			}
			for ( final StatementDescription ret : returns ) {
				final IExpression ie = ret.getFacets().getExpr(VALUE);
				if ( ie == null ) {
					continue;
				}
				if ( ie.equals(IExpressionFactory.NIL_EXPR) ) {
					if ( at.getDefault() != null ) {
						ret.error("'nil' is not an acceptable return value. A valid " + at + " is expected instead.",
							IGamlIssue.WRONG_TYPE, VALUE);
					} else {
						continue;
					}
				} else {
					final IType rt = ie.getType();
					if ( !rt.isTranslatableInto(at) ) {
						ret.error("Action " + cd.getName() + " must return a result of type " + at + " (and not " + rt +
							")", IGamlIssue.SHOULD_CAST, VALUE, at.toString());
					}
				}
			}
			// FIXME This assertion is still simple (i.e. the tree is not verified to ensure that every
			// branch returns something)
		}

	}

	Arguments actualArgs;
	Arguments formalArgs = new Arguments();

	/**
	 * The Constructor.
	 * 
	 * @param actionDesc the action desc
	 * @param sim the sim
	 */
	public ActionStatement(final IDescription desc) {
		super(desc);
		if ( hasFacet(IKeyword.NAME) ) {
			name = getLiteral(IKeyword.NAME);
		}
		if ( hasFacet(IKeyword.PERTINENCE) ) {
			pertinence = getFacet(IKeyword.PERTINENCE);
		}
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		scope.stackArguments(actualArgs);
		final Object result = super.privateExecuteIn(scope);
		return result;
	}

	@Override
	public void leaveScope(final IScope scope) {
		// Clears any _action_halted status present
		scope.popAction();
		super.leaveScope(scope);
	}

	@Override
	public void setRuntimeArgs(final Arguments args) {
		actualArgs = new Arguments(args);
		actualArgs.complementWith(formalArgs);
	}

	@Override
	public void setFormalArgs(final Arguments args) {
		formalArgs.putAll(args);
	}
}
