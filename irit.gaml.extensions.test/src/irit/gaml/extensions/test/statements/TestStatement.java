package irit.gaml.extensions.test.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;

@symbol(name = { "test" }, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true, unique_name = true)
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT, ISymbolKind.MODEL })
@facets(value = { @facet(name = IKeyword.NAME, type = IType.ID, optional = true) }, omissible = IKeyword.NAME)
public class TestStatement extends AbstractStatementSequence {

	public TestStatement(final IDescription desc){
		super(desc);	
		if ( hasFacet(IKeyword.NAME) ) {
			setName("test"+getLiteral(IKeyword.NAME));
		}
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		for(IStatement s : scope.getAgentScope().getSpecies().getBehaviors()){
			if("setUp".equals(s.getName())){
				s.executeOn(scope);
			}
		}		
		return super.privateExecuteIn(scope); 
	}

}