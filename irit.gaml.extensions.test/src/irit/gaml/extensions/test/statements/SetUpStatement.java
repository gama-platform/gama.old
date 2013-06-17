package irit.gaml.extensions.test.statements;

import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.statements.AbstractStatementSequence;

@symbol(name = { "setUp" }, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.SPECIES, ISymbolKind.MODEL })
public class SetUpStatement extends AbstractStatementSequence {
	public SetUpStatement(final IDescription desc) {
		super(desc);
		setName("setUp");
	}
	
	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		return super.privateExecuteIn(scope); 
	}
}
