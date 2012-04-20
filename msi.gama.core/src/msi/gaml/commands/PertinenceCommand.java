package msi.gaml.commands;

import java.util.List;

import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;

public class PertinenceCommand  extends AbstractCommandSequence {
	
	protected List<? extends ISymbol> commands;
	protected double pertinence;
	
	public PertinenceCommand(final IDescription desc) {
		super(desc);
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		this.commands = commands;
	}

}
