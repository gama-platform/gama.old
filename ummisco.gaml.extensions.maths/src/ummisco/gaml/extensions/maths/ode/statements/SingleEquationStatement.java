package ummisco.gaml.extensions.maths.ode.statements;

import static msi.gama.common.interfaces.IKeyword.*;
import java.util.*;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.*;
import ummisco.gaml.extensions.maths.ode.statements.SingleEquationStatement.SingleEquationValidator;

@facets(value = { @facet(name = EQUATION_LEFT, type = IType.NONE, optional = false),
	@facet(name = EQUATION_RIGHT, type = IType.FLOAT, optional = false) }, omissible = EQUATION_RIGHT)
@symbol(name = { EQUATION_OP }, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@inside(symbols = EQUATION)
/**
 * 
 * The class SingleEquationStatement. 
 * Implements an Equation in the form function(n, t) = expression;
 * The left function is only here as a placeholder for enabling a simpler syntax and grabbing the variable as its left member.
 * @comment later, the order will be used as it will require a different integrator to solve the equation. For the moment, it is just 
 * here to show how to compute it from the function used
 *
 * @author Alexis Drogoul, Huynh Quang Nghi
 * @since 26 janv. 2013
 *
 */
@validator(SingleEquationValidator.class)
public class SingleEquationStatement extends AbstractStatement {

	public static final Map<String, Integer> orderNames = new LinkedHashMap();
	static {
		orderNames.put(ZERO, 0);
		orderNames.put(DIFF, 1);
		orderNames.put(DIF2, 2);
	}

	public static class SingleEquationValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 * 
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription d) {

			IExpressionDescription fDesc = d.getFacets().get(EQUATION_LEFT);
			IExpression func = fDesc.getExpression();
			IExpressionDescription eDesc = d.getFacets().get(EQUATION_RIGHT);
			IExpression expr = eDesc.getExpression();
			boolean isFunction = func instanceof IOperator && orderNames.containsKey(func.getName());
			if ( !isFunction ) {
				d.error(
					"The left-hand member of an equation should be a variable or a call to the diff() or diff2() operators",
					IGamlIssue.UNKNOWN_BINARY, fDesc.getTarget());
				return;
			}

			IType type = ((IOperator) func).arg(0).getType();
			if ( !type.isTranslatableInto(Types.get(IType.FLOAT)) ) {
				d.error("The variable of the left-hand member of an equation is expected to be of type float",
					IGamlIssue.WRONG_TYPE, fDesc.getTarget());
				return;
			}

			if ( !expr.getType().isTranslatableInto(Types.get(IType.FLOAT)) ) {
				d.error("The right-hand member of an equation is expected to be of type float", IGamlIssue.WRONG_TYPE,
					eDesc.getTarget());
			}
		}
	}

	private IExpression function, expression;
	private final IList<IExpression> var = new GamaList<IExpression>();
	private IExpression var_t;

	int order;

	public IExpression getFunction() {
		return function;
	}

	public void setFunction(final IExpression function) {
		this.function = function;
	}

	public IExpression getExpression() {
		return expression;
	}

	public void setExpression(final IExpression expression) {
		this.expression = expression;
	}

	public IList<IExpression> getVars() {
		return var;
	}

	public IExpression getVar(final int index) {
		return var.get(index);
	}

	public void setVar(final int index, final IVarExpression v) {
		this.var.set(index, v);
	}

	public IExpression getVar_t() {
		return var_t;
	}

	public void setVar_t(final IVarExpression vt) {
		this.var_t = vt;
	}

	public SingleEquationStatement(final IDescription desc) {
		super(desc);
		function = getFacet(EQUATION_LEFT);
		expression = getFacet(EQUATION_RIGHT);
	}

	public void etablishVar() {
		if ( getOrder() == 0 ) { return; }
		for ( int i = 0; i < ((AbstractNAryOperator) function).numArg(); i++ ) {
			IExpression tmp = ((AbstractNAryOperator) function).arg(i);
			if ( tmp.getName().equals("t") ) {
				var_t = tmp;
			} else {
				var.add(i, tmp);
			}
		}
	}

	/**
	 * This method is normally called by the system of equations to which this
	 * equation belongs. It simply computes the expression that represents the
	 * equation and returns it. The storage of the new values is realized in
	 * SystemOfEquationsStatement, in order not to generate side effects (e.g.
	 * the value of a shared variable changing between the integrations of two
	 * equations)
	 * 
	 * @see msi.gaml.statements.AbstractStatement#privateExecuteIn(msi.gama.runtime.IScope)
	 */
	@Override
	protected Double privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		Double result = (Double) expression.value(scope);

		return result;
	}

	@Override
	public Object executeOn(final IScope scope) throws GamaRuntimeException {
		return expression.value(scope);// super.executeOn(scope);
	}

	public int getOrder() {
		return orderNames.get(function.getName());
	}

	// Placeholders operators that are (normally) never called.
	// FIXME Can probably be replaced by actions, so that they do not pollute
	// the whole scope of
	// GAMA operators.
	// TODO And maybe they can do something useful, like gathering the order, or
	// the var or var_t,
	// whenever they are called.

	@operator(DIFF)
	public static Double diff(final IScope scope, final Double var, final Double time) {
		return Double.NaN;
	}

	@operator(DIF2)
	public static Double diff2(final IScope scope, final Double var, final Double time) {
		return Double.NaN;
	}

	/**
	 * Placeholder for zero-order equations. The expression on the right allows to pass the variable directly (maybe
	 * useful one day).
	 * @param scope
	 * @param var
	 * @param time
	 * @return
	 */
	@operator(ZERO)
	public static Double f(final IScope scope, final IExpression var) {
		return Double.NaN;
	}

}
