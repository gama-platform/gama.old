/*******************************************************************************************************
 *
 * msi.gaml.statements.TryStatement.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 * 
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.statements;

import com.google.common.collect.Iterables;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.annotations.serializer;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.SymbolDescription;
import msi.gaml.descriptions.SymbolSerializer.StatementSerializer;
import msi.gaml.operators.Strings;
import msi.gaml.statements.TryStatement.IfSerializer;

/**
 * IfPrototype.
 * 
 * @author drogoul 14 nov. 07
 */
@symbol (
		name = IKeyword.TRY,
		kind = ISymbolKind.SEQUENCE_STATEMENT,
		with_sequence = true,
		concept = { IConcept.ACTION })
@inside (
		kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER })
@doc (
		value = "Allows the agent to execute a sequence of statements and to catch any runtime error that might happen in a subsequent `catch` block, either to ignore it (not a good idea, usually) or to safely stop the model",
		usages = { @usage (
				value = "The generic syntax is:",
				examples = { @example (
						value = "try {",
						isExecutable = false),
						@example (
								value = "    [statements]",
								isExecutable = false),
						@example (
								value = "}",
								isExecutable = false) }),
				@usage (
						value = "Optionally, the statements to execute when a runtime error happens in the block can be defined in a following statement 'catch'. The syntax then becomes:",
						examples = { @example (
								value = "try {",
								isExecutable = false),
								@example (
										value = "    [statements]",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false),
								@example (
										value = "catch {",
										isExecutable = false),
								@example (
										value = "    [statements]",
										isExecutable = false),
								@example (
										value = "}",
										isExecutable = false) }), })
@serializer (IfSerializer.class)
public class TryStatement extends AbstractStatementSequence {

	public static class IfSerializer extends StatementSerializer {

		@Override
		protected void serializeChildren(final SymbolDescription desc, final StringBuilder sb,
				final boolean includingBuiltIn) {
			sb.append(' ').append('{').append(Strings.LN);
			final String[] catchString = new String[] { null };
			desc.visitChildren(s -> {
				if (s.getKeyword().equals(IKeyword.CATCH)) {
					catchString[0] = s.serialize(false) + Strings.LN;
				} else {
					serializeChild(s, sb, includingBuiltIn);
				}
				return true;
			});

			sb.append('}');
			if (catchString[0] != null) {
				sb.append(catchString[0]);
			} else {
				sb.append(Strings.LN);
			}

		}

	}

	public IStatement catchStatement;

	/**
	 * The Constructor.
	 * 
	 * @param sim
	 *            the sim
	 */
	public TryStatement(final IDescription desc) {
		super(desc);
		setName(IKeyword.CATCH);

	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> commands) {
		for (final ISymbol c : commands) {
			if (c instanceof CatchStatement) {
				catchStatement = (IStatement) c;
			}
		}
		super.setChildren(Iterables.filter(commands, each -> each != catchStatement));
	}

	@Override
	public Object privateExecuteIn(final IScope scope) {
		Object result = null;
		try {
			scope.enableTryMode();
			result = super.privateExecuteIn(scope);
		} catch (final GamaRuntimeException e) {
			scope.disableTryMode();
			if (catchStatement != null) { return scope.execute(catchStatement).getValue(); }
		} finally {
			scope.disableTryMode();
		}
		return result;

	}
}