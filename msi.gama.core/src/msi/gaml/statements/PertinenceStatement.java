package msi.gaml.statements;

import java.util.List;

import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;

public class PertinenceStatement  extends AbstractStatementSequence {
	
	protected List<? extends ISymbol> commands;
	protected double pertinence;
	
	public PertinenceStatement(final IDescription desc) {
		super(desc);
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		this.commands = commands;
	}

}
