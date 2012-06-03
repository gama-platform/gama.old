package msi.gaml.statements;

import msi.gaml.descriptions.IDescription;

public class PertinenceStatement extends AbstractStatementSequence {

	// protected List<? extends ISymbol> commands;
	protected double pertinenceValue;

	public PertinenceStatement(final IDescription desc) {
		super(desc);
	}

	// @Override
	// public void setChildren(final List<? extends ISymbol> commands) {
	// this.commands = commands;
	// }

}
