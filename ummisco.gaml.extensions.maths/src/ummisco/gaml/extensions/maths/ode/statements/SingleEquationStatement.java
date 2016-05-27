/*********************************************************************************************
 *
 *
 * 'SingleEquationStatement.java', in plugin 'ummisco.gaml.extensions.maths', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gaml.extensions.maths.ode.statements;

import static msi.gama.common.interfaces.IKeyword.DIF2;
import static msi.gama.common.interfaces.IKeyword.DIFF;
import static msi.gama.common.interfaces.IKeyword.EQUATION;
import static msi.gama.common.interfaces.IKeyword.EQUATION_LEFT;
import static msi.gama.common.interfaces.IKeyword.EQUATION_OP;
import static msi.gama.common.interfaces.IKeyword.EQUATION_RIGHT;
import static msi.gama.common.interfaces.IKeyword.SOLVE;
import static msi.gama.common.interfaces.IKeyword.ZERO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.serializer;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.SymbolDescription;
import msi.gaml.descriptions.SymbolSerializer;
import msi.gaml.expressions.AbstractNAryOperator;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IOperator;
import msi.gaml.expressions.IVarExpression;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gaml.extensions.maths.ode.statements.SingleEquationStatement.SIngleEquationSerializer;
import ummisco.gaml.extensions.maths.ode.statements.SingleEquationStatement.SingleEquationValidator;

/**
 *
 * The class SingleEquationStatement. Implements an Equation in the form
 * function(n, t) = expression; The left function is only here as a placeholder
 * for enabling a simpler syntax and grabbing the variable as its left member.
 * 
 * @comment later, the order will be used as it will require a different
 *          integrator to solve the equation. For the moment, it is just here to
 *          show how to compute it from the function used
 *
 * @author Alexis Drogoul, Huynh Quang Nghi
 * @since 26 janv. 2013
 *
 */

@symbol(name = { EQUATION_OP }, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false, concept = {
		IConcept.EQUATION, IConcept.MATH })
@facets(value = {
		@facet(name = EQUATION_LEFT, type = IType.NONE, optional = false, doc = @doc("the left part of the equation (it should be a variable or a call to the diff() or diff2() operators) ")),
		@facet(name = EQUATION_RIGHT, type = IType.FLOAT, optional = false, doc = @doc("the right part of the equation (it is mandatory that it can be evaluated as a float")) }, omissible = EQUATION_RIGHT)
@inside(symbols = EQUATION)
@validator(SingleEquationValidator.class)
@serializer(SIngleEquationSerializer.class)
@doc(value = "Allows to implement an equation in the form function(n, t) = expression. The left function is only here as a placeholder for enabling a simpler syntax and grabbing the variable as its left member.", usages = {
		@usage(value = "The syntax of the = statement is a bit different from the other statements. It has to be used as follows (in an equation):", examples = {
				@example(value = "float t;", isExecutable = false), @example(value = "float S;", isExecutable = false),
				@example(value = "float I;", isExecutable = false),
				@example(value = "equation SI { ", isExecutable = false),
				@example(value = "   diff(S,t) = (- 0.3 * S * I / 100);", isExecutable = false),
				@example(value = "   diff(I,t) = (0.3 * S * I / 100);", isExecutable = false),
				@example(value = "} ", isExecutable = false) }) }, see = { EQUATION, SOLVE })
public class SingleEquationStatement extends AbstractStatement {

	public static final Map<String, Integer> orderNames = new TOrderedHashMap();
	static {
		orderNames.put(ZERO, 0);
		orderNames.put(DIFF, 1);
		orderNames.put(DIF2, 2);
	}

	public static class SIngleEquationSerializer extends SymbolSerializer {

		@Override
		protected void serialize(final SymbolDescription desc, final StringBuilder sb, final boolean includingBuiltIn) {
			sb.append(desc.getFacets().get(LEFT).serialize(includingBuiltIn)).append(" = ")
					.append(desc.getFacets().get(RIGHT).serialize(includingBuiltIn)).append(";");
		}
	}

	public static class SingleEquationValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 *
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription d) {

			final IExpressionDescription fDesc = d.getFacets().get(EQUATION_LEFT);
			final IExpression func = fDesc.getExpression();
			final IExpressionDescription eDesc = d.getFacets().get(EQUATION_RIGHT);
			final IExpression expr = eDesc.getExpression();
			final boolean isFunction = func instanceof IOperator && orderNames.containsKey(func.getName());
			if (!isFunction) {
				d.error("The left-hand member of an equation should be a variable or a call to the diff() or diff2() operators",
						IGamlIssue.UNKNOWN_BINARY, fDesc.getTarget());
				return;
			}

			final IType type = ((IOperator) func).arg(0).getType();
			if (!type.isTranslatableInto(Types.FLOAT)) {
				d.error("The variable of the left-hand member of an equation is expected to be of type float",
						IGamlIssue.WRONG_TYPE, fDesc.getTarget());
				return;
			}

			if (!expr.getType().isTranslatableInto(Types.FLOAT)) {
				d.error("The right-hand member of an equation is expected to be of type float", IGamlIssue.WRONG_TYPE,
						eDesc.getTarget());
			}
		}
	}

	private IExpression function, expression;
	private final List<IExpression> var = new ArrayList<IExpression>();
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

	public List<IExpression> getVars() {
		return var;
	}

	public IExpression getVarTime() {
		return var_t;
	}

	public IExpression getVar(final int index) {
		return var.get(index);
	}

	public void setVar(final int index, final IVarExpression v) {
		this.var.set(index, v);
	}

	public void setVar_t(final IVarExpression vt) {
		this.var_t = vt;
	}

	public SingleEquationStatement(final IDescription desc) {
		super(desc);
		function = getFacet(EQUATION_LEFT);
		expression = getFacet(EQUATION_RIGHT);
	}

	public void establishVar() {
		if (getOrder() == 0) {
			return;
		}
		int i = 0;
		for (i = 0; i < ((AbstractNAryOperator) function).numArg() - 1; i++) {
			final IExpression tmp = ((AbstractNAryOperator) function).arg(i);
			var.add(i, tmp);
		}
		var_t = ((AbstractNAryOperator) function).arg(i);
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
		final Double result = (Double) expression.value(scope);

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

	@operator(value = DIFF, concept = { IConcept.EQUATION,
			IConcept.MATH }, doc = @doc("A placeholder function for expressing equations"))
	public static Double diff(final IScope scope, final Double var, final Double time) {
		return Double.NaN;
	}

	@operator(value = DIF2, concept = { IConcept.EQUATION,
			IConcept.MATH }, doc = @doc("A placeholder function for expressing equations"))
	public static Double diff2(final IScope scope, final Double var, final Double time) {
		return Double.NaN;
	}

	/**
	 * Placeholder for zero-order equations. The expression on the right allows
	 * to pass the variable directly (maybe useful one day).
	 * 
	 * @param scope
	 * @param var
	 * @param time
	 * @return
	 */
	@operator(value = ZERO, concept = { IConcept.EQUATION,
			IConcept.MATH }, doc = @doc("An internal placeholder function"))
	public static Double f(final IScope scope, final IExpression var) {
		return Double.NaN;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return function.toString() + " = " + expression.toString();
	}

}
