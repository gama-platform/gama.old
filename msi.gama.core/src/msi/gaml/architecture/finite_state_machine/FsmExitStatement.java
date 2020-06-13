/*******************************************************************************************************
 *
 * msi.gaml.architecture.finite_state_machine.FsmExitStatement.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.architecture.finite_state_machine;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gaml.descriptions.IDescription;
import msi.gaml.statements.AbstractStatementSequence;

@symbol(name = FsmStateStatement.EXIT,
	kind = ISymbolKind.SEQUENCE_STATEMENT,
	with_sequence = true,
	with_scope = false,
	unique_in_context = true)
@inside(symbols = { FsmStateStatement.STATE })
@doc(value = "In an FSM architecture, `" + FsmStateStatement.EXIT +
	"` introduces a sequence of statements to execute right before exiting the state.",
	usages = { @usage(value = "In the following example, at the state it leaves the state s_init, he will display the message 'EXIT from s_init':",
		examples = { @example(value = "	state s_init initial: true {", isExecutable = false),
			@example(value = "		write state;", isExecutable = false),
			@example(value = "		transition to: s1 when: (cycle > 2) {", isExecutable = false),
			@example(value = "			write \"transition s_init -> s1\";", isExecutable = false),
			@example(value = "		}", isExecutable = false), @example(value = "		exit {", isExecutable = false),
			@example(value = "			write \"EXIT from \"+state;", isExecutable = false),
			@example(value = "		}", isExecutable = false), @example(value = "	}", isExecutable = false) }) },
	see = { FsmStateStatement.ENTER, FsmStateStatement.STATE, FsmTransitionStatement.TRANSITION })
public class FsmExitStatement extends AbstractStatementSequence {

	public FsmExitStatement(final IDescription desc) {
		super(desc);
	}

	@Override
	public void leaveScope(final IScope scope) {
		// no scope

		// TODO : do the contrary in the future : have the no_scope property looked at by the scope
		// itself
	}

	@Override
	public void enterScope(final IScope scope) {
		// no scope
	}
}