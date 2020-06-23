/*******************************************************************************************************
 *
 * msi.gaml.statements.ReturnStatement.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 * 
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 6 févr. 2010
 *
 * @todo Description
 *
 */

@symbol(name = IKeyword.RETURN, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false, unique_in_context = true, concept = { IConcept.ACTION })
@inside(symbols = IKeyword.ACTION, kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets(
	value = { @facet(name = IKeyword.VALUE,
		type = IType.NONE,
		optional = true,
		doc = @doc("an expression that is returned") ) },
	omissible = IKeyword.VALUE)
@doc(
	value = "Allows to immediately stop and tell which value to return from the evaluation of the surrounding action or top-level statement (reflex, init, etc.). Usually used within the declaration of an action. For more details about actions, see the following [Section161 section].",
	usages = {
		@usage(value = "Example:",
			examples = { @example(value = "string foo {", isExecutable = false),
				@example(value = "     return \"foo\";", isExecutable = false),
				@example(value = "}", isExecutable = false), @example(value = "", isExecutable = false),
				@example(value = "reflex {", isExecutable = false),
				@example(value = "    string foo_result <- foo(); 	// foos_result is now equals to \"foo\"",
					isExecutable = false),
				@example(value = "}", isExecutable = false) }),
		@usage(
			value = "In the specific case one wants an agent to ask another agent to execute a statement with a return, it can be done similarly to:",
			examples = { @example(value = "// In Species A:", isExecutable = false),
				@example(value = "string foo_different {", isExecutable = false),
				@example(value = "     return \"foo_not_same\";", isExecutable = false),
				@example(value = "}", isExecutable = false), @example(value = "/// ....", isExecutable = false),
				@example(value = "// In Species B:", isExecutable = false),
				@example(value = "reflex writing {", isExecutable = false),
				@example(
					value = "    string temp <- some_agent_A.foo_different []; 	// temp is now equals to \"foo_not_same\" ",
					isExecutable = false),
				@example(value = "}", isExecutable = false), }) })
public class ReturnStatement extends AbstractStatement {

	final IExpression value;

	public ReturnStatement(final IDescription desc) {
		super(desc);
		value = getFacet(IKeyword.VALUE);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		final Object result = value == null ? null : value.value(scope);
		scope.interruptAction();
		return result;
	}
	//
	// @Override
	// public IType getType() {
	// return value == null ? Types.NO_TYPE : value.getType();
	// }
	//
	// @Override
	// public IType getContentType() {
	// return value == null ? Types.NO_TYPE : value.getContentType();
	// }
	//
	// @Override
	// public IType getKeyType() {
	// return value == null ? Types.NO_TYPE : value.getKeyType();
	// }

}
