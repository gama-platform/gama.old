package msi.gaml.statements;

import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;

@symbol(name = IKeyword.CHOOSE, kind = ISymbolKind.SEQUENCE_STATEMENT)
public class PertinenceChooseStatement extends PertinenceStatement {

	private IStatement mostPertinentCommand;

	public PertinenceChooseStatement(final IDescription desc) {
		super(desc);
	}

	@Override
	public Double computePertinence(final IScope scope) throws GamaRuntimeException {
		pertinence = 0;
		List<IStatement> pertinentsCommands = new GamaList<IStatement>();
		for ( ISymbol c : commands ) {
			if ( !(c instanceof IStatement) ) {
				continue;
			}
			IStatement command = (IStatement) c;
			Double p = command.computePertinence(scope);
			if ( p > pertinence ) {
				pertinentsCommands = new GamaList<IStatement>();
				pertinentsCommands.add(command);
				pertinence = p;
			} else if ( p == pertinence ) {
				pertinentsCommands.add(command);
			}
		}
		if ( !pertinentsCommands.isEmpty() ) {
			mostPertinentCommand =
				pertinentsCommands.get(GAMA.getRandom().between(0, pertinentsCommands.size() - 1));
		}
		return pertinence;
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		computePertinence(scope);
		if ( pertinence > 0 ) { return mostPertinentCommand.executeOn(scope); }
		return null;
	}
}