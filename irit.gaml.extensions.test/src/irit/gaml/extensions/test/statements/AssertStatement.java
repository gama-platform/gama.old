package irit.gaml.extensions.test.statements;

import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;

@symbol(name = { "assert" }, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = true)
@facets(value = {
	@facet(name = IKeyword.VALUE, type = IType.NONE, optional = false),
	@facet(name = "equals", type = IType.NONE, optional = true) },
 omissible = IKeyword.VALUE)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.SPECIES, ISymbolKind.MODEL })
public class AssertStatement extends AbstractStatement {
	StatementDescription setUpStatement;
	IExpression value;
	IExpression equals;

	public AssertStatement(final IDescription desc) {
		super(desc);
		setName("assert");

		List<IDescription> statements = desc.getSpeciesContext().getChildren();
		for ( IDescription s : statements ) {
			if ( s.getName().equals("setUp") ) {
				setUpStatement = (StatementDescription) s;
			}
		}
		
		value = getFacet("value");
		if ( getFacet("equals") != null ) {
			equals = getFacet("equals");
		}
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		String valueStr = getFacet("value").literalValue();
		String equalsStr = getFacet("equals").literalValue();
		System.out.println(valueStr + "  " + equalsStr);
		
		if ( getFacet("equals") != null ) {
			if(!value.value(scope).equals(equals.value(scope))){
				throw GamaRuntimeException.error("Assert ERROR: " + valueStr + " is not equals to " + equalsStr);
			}
		}

		return null;
	}

}
