package msi.gaml.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

@facets(value = { @facet(name = IKeyword.EQUATION_LEFT, type = IType.NONE_STR, optional = false),
	@facet(name = IKeyword.EQUATION_RIGHT, type = IType.FLOAT_STR, optional = false) })
@symbol(name = { IKeyword.EQUATION }, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@inside(kinds = { ISymbolKind.SPECIES }, symbols = IKeyword.SOLVER)
public class EquationStatement extends AbstractStatement {

	final IExpression function, expression;

	public EquationStatement(final IDescription desc) {
		super(desc);
		function = getFacet(IKeyword.EQUATION_LEFT);
		expression = getFacet(IKeyword.EQUATION_RIGHT);
	}

	@Override
	protected Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		return null;
	}

}
