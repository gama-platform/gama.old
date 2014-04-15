/*********************************************************************************************
 * 
 * 
 * 'PertinenceChooseStatement.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.statements;

import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;

@symbol(name = IKeyword.CHOOSE, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
public class PertinenceChooseStatement extends PertinenceStatement {

	private IStatement mostPertinentCommand;

	public PertinenceChooseStatement(final IDescription desc) {
		super(desc);
	}

	@Override
	public Double computePertinence(final IScope scope) throws GamaRuntimeException {
		pertinenceValue = 0;
		List<IStatement> pertinentsCommands = new GamaList<IStatement>();
		for ( ISymbol c : commands ) {
			if ( !(c instanceof IStatement) ) {
				continue;
			}
			IStatement command = (IStatement) c;
			Double p = command.computePertinence(scope);
			if ( p > pertinenceValue ) {
				pertinentsCommands = new GamaList<IStatement>();
				pertinentsCommands.add(command);
				pertinenceValue = p;
			} else if ( p == pertinenceValue ) {
				pertinentsCommands.add(command);
			}
		}
		if ( !pertinentsCommands.isEmpty() ) {
			mostPertinentCommand = pertinentsCommands.get(scope.getRandom().between(0, pertinentsCommands.size() - 1));
		}
		return pertinenceValue;
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		computePertinence(scope);
		if ( pertinenceValue > 0 ) { return mostPertinentCommand.executeOn(scope); }
		return null;
	}
}