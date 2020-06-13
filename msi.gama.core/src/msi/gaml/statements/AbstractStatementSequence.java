/*******************************************************************************************************
 *
 * msi.gaml.statements.AbstractStatementSequence.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements;

import com.google.common.collect.FluentIterable;

import msi.gama.runtime.IScope;
import msi.gama.runtime.ExecutionResult;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import one.util.streamex.StreamEx;

public class AbstractStatementSequence extends AbstractStatement {

	protected IStatement[] commands;
	final boolean isTopLevel;

	public AbstractStatementSequence(final IDescription desc) {
		super(desc);
		isTopLevel = desc != null && desc.getMeta().isTopLevel();
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> commands) {
		this.commands = FluentIterable.from(commands).filter(IStatement.class).toArray(IStatement.class);
	}

	public boolean isEmpty() {
		return commands.length == 0;
	}

	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		enterScope(scope);
		try {
			return super.executeOn(scope);
		} finally {
			leaveScope(scope);
		}
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		Object lastResult = null;
		for (final IStatement command : commands) {
			final ExecutionResult result = scope.execute(command);
			if (!result.passed()) { return lastResult; }
			lastResult = result.getValue();
		}
		return lastResult;
	}

	public void leaveScope(final IScope scope) {
		// Clears any action_halted status in case we are a top-level behavior
		// (reflex, init, state, etc.)
		if (isTopLevel) {
			scope.popAction();
		}
		scope.pop(this);
	}

	public void enterScope(final IScope scope) {
		scope.push(this);
	}

	public IStatement[] getCommands() {
		return commands;
	}

}