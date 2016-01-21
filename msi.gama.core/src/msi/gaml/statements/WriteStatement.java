/*********************************************************************************************
 *
 *
 * 'WriteStatement.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.*;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 6 févr. 2010
 *
 * @todo Description
 *
 */

@symbol(name = IKeyword.WRITE, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER })
@facets(
	value = {
		@facet(name = IKeyword.COLOR,
			type = IType.COLOR,
			optional = true,
			doc = @doc("The color with wich the message will be displayed. Note that different simulations will have different (default) colors to use for this purpose if this facet is not specified") ),
		@facet(name = IKeyword.MESSAGE,
			type = IType.NONE,
			optional = false,
			doc = @doc("the message to display. Modelers can add some formatting characters to the message (carriage returns, tabs, or Unicode characters), which will be used accordingly in the console.") ), },
	omissible = IKeyword.MESSAGE)
@doc(value = "The statement makes the agent output an arbitrary message in the console.",
	usages = {
		@usage(value = "Outputting a message", examples = { @example("write 'This is a message from ' + self;") }) })
public class WriteStatement extends AbstractStatement {

	@Override
	public String getTrace(final IScope scope) {
		// We dont trace write statements
		return "";
	}

	final IExpression message;
	final IExpression color;

	public WriteStatement(final IDescription desc) {
		super(desc);
		message = getFacet(IKeyword.MESSAGE);
		color = getFacet(IKeyword.COLOR);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		IAgent agent = scope.getAgentScope();
		String mes = null;
		if ( agent != null && !agent.dead() ) {
			mes = Cast.asString(scope, message.value(scope));
			if ( mes == null ) {
				mes = "nil";
			}
			GamaColor rgb = null;
			if ( color != null ) {
				rgb = (GamaColor) color.value(scope);
			}
			scope.getGui().informConsole(mes, scope.getRoot(), rgb);
		}
		return mes;
	}

	@operator(value = "sample",
		doc = {
			@doc("Returns a string containing the GAML code of the expression passed in parameter, followed by the result of its evaluation") },
		category = { IOperatorCategory.STRING })
	public static String sample(final IScope scope, final IExpression expr) {
		return sample(scope, expr == null ? "nil" : expr.serialize(false), expr);
	}

	@operator(value = "sample",
		doc = @doc("Returns a string containing the string passed in parameter, followed by the result of the evaluation of the expression") ,
		category = { IOperatorCategory.STRING })
	public static String sample(final IScope scope, final String text, final IExpression expr) {
		return text == null ? "" : text.trim() + " -: " + (expr == null ? "nil" : Cast.toGaml(expr.value(scope)));
	}

}
