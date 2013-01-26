package msi.gaml.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.*;
import msi.gaml.types.IType;

@facets(value = { @facet(name = IKeyword.EQUATION_LEFT, type = IType.NONE_STR, optional = false),
	@facet(name = IKeyword.EQUATION_RIGHT, type = IType.FLOAT_STR, optional = false) }, omissible = IKeyword.EQUATION_RIGHT)
@symbol(name = { IKeyword.EQUATION }, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@inside(symbols = IKeyword.SOLVE)
public class EquationStatement extends AbstractStatement {

	final IExpression function, expression;
	IVarExpression var;
	int order;

	public EquationStatement(final IDescription desc) {
		super(desc);
		function = getFacet(IKeyword.EQUATION_LEFT);
		var = (IVarExpression) ((AbstractBinaryOperator) function).left();
		expression = getFacet(IKeyword.EQUATION_RIGHT);
	}

	@Override
	protected Double privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		Double result = (Double) expression.value(scope);
		var.setVal(scope, result, false);
		return result;
	}

	public int getOrder() {
		if ( function.getName().equals("diff") ) { return 1; }
		if ( function.getName().equals("diff2") ) { return 2; }
		return 0;
	}

	// placeholders

	@operator("diff")
	static Double diff(final IScope scope, final Double left, final Double right) {
		return Double.NaN;
	}

	@operator("diff2")
	static Double diff2(final IScope scope, final Double left, final Double right) {
		return Double.NaN;
	}

}
