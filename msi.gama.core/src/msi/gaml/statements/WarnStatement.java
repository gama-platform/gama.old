/*******************************************************************************************************
 *
 * msi.gaml.statements.WarnStatement.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 * 
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
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

@symbol(name = IKeyword.WARNING, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false,
concept = { IConcept.SYSTEM })
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER })
@facets(
	value = { @facet(name = IKeyword.MESSAGE,
		type = IType.STRING,
		optional = false,
		doc = @doc("the message to display as a warning.") ) },
	omissible = IKeyword.MESSAGE)
@doc(value = "The statement makes the agent output an arbitrary message in the error view as a warning.",
	usages = {
		@usage(value = "Emmitting a warning", examples = { @example("warn 'This is a warning from ' + self;") }) })
public class WarnStatement extends AbstractStatement {

	final IExpression message;

	public WarnStatement(final IDescription desc) {
		super(desc);
		message = getFacet(IKeyword.MESSAGE);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		IAgent agent = scope.getAgent();
		String mes = null;
		if ( agent != null && !agent.dead() ) {
			mes = Cast.asString(scope, message.value(scope));
			GAMA.reportError(scope, GamaRuntimeException.warning(mes, scope), false);
		}
		return mes;
	}

	// @Override
	// public IType getType() {
	// return Types.STRING;
	// }

}
