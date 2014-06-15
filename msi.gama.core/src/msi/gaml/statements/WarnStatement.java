/*********************************************************************************************
 * 
 * 
 * 'WarnStatement.java', in plugin 'msi.gama.core', is part of the source code of the
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
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
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

@symbol(name = IKeyword.WARNING, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER })
@facets(value = { @facet(name = IKeyword.MESSAGE,
	type = IType.STRING,
	optional = false,
	doc = @doc("the message to display as a warning.")) }, omissible = IKeyword.MESSAGE)
@doc(value = "The statement makes the agent output an arbitrary message in the error view as a warning.",
	usages = { @usage(examples = { @example("warn 'This is a warning from ' + self;") }) })
public class WarnStatement extends AbstractStatement {

	final IExpression message;

	public WarnStatement(final IDescription desc) {
		super(desc);
		message = getFacet(IKeyword.MESSAGE);
	}

	@Override
	public Object privateExecuteIn(final IScope stack) throws GamaRuntimeException {
		IAgent agent = stack.getAgentScope();
		String mes = null;
		if ( agent != null && !agent.dead() ) {
			mes = Cast.asString(stack, message.value(stack));
			GAMA.reportError(stack, GamaRuntimeException.warning(mes), false);
		}
		return mes;
	}

	// @Override
	// public IType getType() {
	// return Types.get(IType.STRING);
	// }

}
