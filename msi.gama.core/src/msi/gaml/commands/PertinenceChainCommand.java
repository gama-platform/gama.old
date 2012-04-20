package msi.gaml.commands;

import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;

@symbol(name = IKeyword.CHAIN, kind = ISymbolKind.SEQUENCE_COMMAND)
public class PertinenceChainCommand extends PertinenceCommand {

	private List<ICommand> pertinentsCommands;

	public PertinenceChainCommand(final IDescription desc) {
		super(desc);
	}

	@Override
	public Double computePertinence(final IScope scope) throws GamaRuntimeException {
		pertinence = 0;
		pertinentsCommands = new GamaList<ICommand>();
		for ( ISymbol c : commands ) {
			if ( !(c instanceof ICommand) ) {
				continue;
			}
			ICommand command = (ICommand) c;
			Double p = 0.0;
			p = command.computePertinence(scope);
			if ( p > 0.0 ) {
				pertinentsCommands.add(command);
			}
			if ( p > pertinence ) {
				pertinence = p;
			}
		}
		return pertinence;
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		computePertinence(scope);
		Object result = null;
		for ( ICommand c : pertinentsCommands ) {
			result = c.executeOn(scope);
		}
		return result;
	}
}