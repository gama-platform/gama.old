package msi.gaml.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;

@symbol(name = { IKeyword.DEFAULT }, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true, unique_in_context = true)
@inside(symbols = IKeyword.SWITCH)
public class MatchDefaultStatement extends MatchStatement {

	public MatchDefaultStatement(final IDescription desc) {
		super(desc);
	}

	@Override
	public boolean matches(final IScope scope, final Object switchValue)
		throws GamaRuntimeException {
		return false;
	}

}