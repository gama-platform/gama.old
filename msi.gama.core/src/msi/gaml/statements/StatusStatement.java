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
import msi.gama.common.util.AbstractGui;
import msi.gama.metamodel.agent.IAgent;
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

@symbol(name = IKeyword.STATUS, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER })
@facets(value = {
	@facet(name = IKeyword.COLOR, type = IType.COLOR, optional = true),
	@facet(name = IKeyword.MESSAGE,
		type = IType.NONE,
		optional = false,
		doc = @doc("Allows to display a necessarily short message in the status box in the upper left corner. No formatting characters (carriage returns, tabs, or Unicode characters) should be used, but a background color can be specified. The message will remain in place until it is replaced by another one or by nil, in which case the standard status (number of cycles) will be displayed again")) },
	omissible = IKeyword.MESSAGE)
@doc(value = "The statement makes the agent output an arbitrary message in the status box.",
	usages = { @usage(value = "Outputting a message",
		examples = { @example("status ('This is my status ' + self) color: °yellow;") }) })
public class StatusStatement extends AbstractStatement {

	@Override
	public String getTrace(final IScope scope) {
		// We dont trace status statements
		return "";
	}

	final IExpression message;
	final IExpression color;

	public StatusStatement(final IDescription desc) {
		super(desc);
		message = getFacet(IKeyword.MESSAGE);
		color = getFacet(IKeyword.COLOR);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		IAgent agent = scope.getAgentScope();
		String mes = null;
		if ( agent != null && !agent.dead() ) {
			Object o = message.value(scope);
			String msg = o == null ? null : Cast.asString(scope, o);
			scope.getGui().setStatus(msg, color == null ? null : Cast.asColor(scope, color.value(scope)));
		}
		return mes;
	}
}
