/*********************************************************************************************
 *
 *
 * 'AssertStatement.java', in plugin 'irit.gaml.extensions.test', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package irit.gaml.extensions.test.statements;

import java.util.Collection;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;

@symbol(name = { "assert" }, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = true)
@facets(value = {
	@facet(name = IKeyword.VALUE,
		type = IType.NONE,
		optional = false,
		doc = @doc("the value that is evaluated and compared to other facets")),
	@facet(name = IKeyword.EQUALS,
		type = IType.NONE,
		optional = true,
		doc = @doc("an expression, assert tests whether the value is equals to this expression")),
	@facet(name = IKeyword.ISNOT,
		type = IType.NONE,
		optional = true,
		doc = @doc("an expression, assert tests whether the value is not equals to this expression")),
	@facet(name = IKeyword.RAISES,
		type = IType.ID,
		optional = true,
		doc = @doc("\"error\" or \"warning\", used in testing what raises the evaluation of the value: expression")) },
	omissible = IKeyword.VALUE)
@inside(symbols = { "test" })
@doc(value = "Allows to check whether the evaluation of a given expression fulfils a given condition. If it is not fulfilled, an exception is raised.",
	usages = {
		@usage(value = "if the equals: facet is used, the equality between the evaluation of expressions in the value: and in the equals: facets is tested",
			examples = @example("assert (2+2) equals: 4;")),
		@usage(value = "if the is_not: facet is used, the inequality between the evaluation of expressions in the value: and in the equals: facets is tested",
			examples = @example("assert self is_not: nil;")),
		@usage(value = "if the raises: facet is used with either \"warning\" or \"error\", the statement tests whether the evaluation of the value: expression raises an error (resp. a warning)",
			examples = { @example("int z <- 0;"), @example("assert (3/z) raises: \"error\";") }) },
	see = { "test", "setup" })
public class AssertStatement extends AbstractStatement {

	StatementDescription setUpStatement;
	IExpression value;
	IExpression equals;
	IExpression isnot;
	IExpression raises;

	public AssertStatement(final IDescription desc) {
		super(desc);
		setName("assert");

		Collection<StatementDescription> statements = desc.getSpeciesContext().getBehaviors();
		for ( StatementDescription s : statements ) {
			if ( "setup".equals(s.getName()) ) {
				setUpStatement = s;
			}
		}

		value = getFacet(IKeyword.VALUE);
		if ( getFacet(IKeyword.EQUALS) != null ) {
			equals = getFacet(IKeyword.EQUALS);
		}
		if ( getFacet(IKeyword.ISNOT) != null ) {
			isnot = getFacet(IKeyword.ISNOT);
		}
		if ( getFacet(IKeyword.RAISES) != null ) {
			raises = getFacet(IKeyword.RAISES);
		}
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {

		if ( equals != null ) {
			if ( value.value(scope) != null ) {
				if ( !value.value(scope).equals(equals.value(scope)) ) { throw GamaRuntimeException.error(
					"Assert equals ERROR : " + value.serialize(false) + " is not equals to " + equals.value(scope),
					scope); }
				return null;
			} else {
				if ( equals.value(scope) != null ) { throw GamaRuntimeException.error(
					"Assert equals ERROR : " + value.serialize(false) + " is not equals to " + equals.value(scope),
					scope); }
				return null;
			}
		}

		if ( isnot != null ) {
			if ( value.value(scope).equals(isnot.value(scope)) ) { throw GamaRuntimeException.error(
				"Assert is_not ERROR: " + value.serialize(false) + " is equals to " + isnot.value(scope), scope); }
			return null;
		}

		if ( raises != null ) {
			// System.out.println(raises.value(scope));
			try {
				value.value(scope);
			} catch (GamaRuntimeException e) {
				boolean isWarning = e.isWarning() && !scope.getExperiment().getWarningsAsErrors();

				if ( isWarning && IKeyword.ERROR.equals(raises.getName()) ) { throw GamaRuntimeException
					.error("Assert raises ERROR: " + value.serialize(false) +
						" does not raise an error. It raises a warning.", scope); }
				if ( !isWarning && IKeyword.WARNING_TEST.equals(raises.getName()) ) { throw GamaRuntimeException
					.error("Assert raises ERROR: " + value.serialize(false) +
						" does not raise a warning. It raises an error.", scope); }
				if ( !IKeyword.ERROR.equals(raises.getName()) && !IKeyword.WARNING_TEST.equals(raises.getName()) ) { throw GamaRuntimeException
					.error("Assert raises ERROR: " + value.serialize(false) + "does not raise a " + raises.getName() +
						", it raises " + (isWarning ? "a warning." : "an error."), scope); }
				return null;
			} catch (Exception e) {
				if ( !IKeyword.ERROR.equals(raises.getName()) ) { throw GamaRuntimeException.error(
					"Assert raises ERROR: " + value.serialize(false) + " raises an error that is not managed by GAMA.",
					scope); }
				return null;
			}
			if ( IKeyword.ERROR.equals(raises.getName()) || IKeyword.WARNING_TEST.equals(raises.getName()) ) { throw GamaRuntimeException
				.error("Assert raises ERROR: " + value.serialize(false) + " does not raise anything.", scope); }
			return null;
		}

		// Case where there no equals, is_not or raises
		// the value is thus evaluated as a boolean and tested
		if ( !Cast.asBool(scope, getFacet(IKeyword.VALUE).value(scope)) ) { throw GamaRuntimeException.error(
			"Assert ERROR: " + value.serialize(false) + " is false", scope); }
		return null;
	}
}
