package irit.gaml.extensions.test.statements;

import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.statements.AbstractStatementSequence;

@symbol(name = { "setup" }, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT, ISymbolKind.MODEL })
public class SetUpStatement extends AbstractStatementSequence {
	public SetUpStatement(final IDescription desc) {
		super(desc);
		setName("setup");
	}

    @Override
    public Object executeOn(final IScope scope) throws GamaRuntimeException {
        // does nothing when called « normally »		
    	return null;
    }

    public Object setup(final IScope scope) throws GamaRuntimeException {
            // calls the « normal » execution defined in the superclass
            return super.executeOn(scope);
    }	
	
//	@Override
//	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
//		return super.privateExecuteIn(scope); 
//	}
}
