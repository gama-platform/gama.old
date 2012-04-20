package msi.gaml.commands;

import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.ISymbolKind;
import msi.gaml.descriptions.IDescription;

@symbol(name = IKeyword.CHOOSE, kind = ISymbolKind.SEQUENCE_COMMAND)
public class PertinenceChooseCommand extends PertinenceCommand {
	
	private ICommand mostPertinentCommand;
	public PertinenceChooseCommand(final IDescription desc) {
		super(desc);
	}

	@Override
	public Double computePertinence(final IScope scope) throws GamaRuntimeException {
		pertinence = 0;
		List<ICommand> pertinentsCommands = new GamaList<ICommand>();
		for ( ISymbol c : commands ) {
			if (!(c instanceof ICommand))
				continue;
			ICommand command = (ICommand)c;
			Double p = command.computePertinence(scope);
			if (p > pertinence ) {
				pertinentsCommands = new GamaList<ICommand>();
				pertinentsCommands.add(command);
				pertinence = p;
			}
			else if (p == pertinence) {
				pertinentsCommands.add(command);
			}
		}
		if (! pertinentsCommands.isEmpty())
			mostPertinentCommand = pertinentsCommands.get(GAMA.getRandom().between(0, pertinentsCommands.size() - 1));
		return pertinence;
	}
	
	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		computePertinence(scope);
		if (pertinence > 0)
			return mostPertinentCommand.executeOn(scope);
		return null;
	}
}