/*********************************************************************************************
 *
 *
 * 'BinaryOperator.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.expressions;

import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.operators.Cast;

/**
 * The Class BinaryOperator.
 */
public class BinaryOperator extends NAryOperator {

	public static IExpression create(final OperatorProto proto, final IDescription context,
			final IExpression... child) {
		final BinaryOperator u = new BinaryOperator(proto, context, child);
		if (u.isConst() && GamaPreferences.CONSTANT_OPTIMIZATION.getValue()) {
			final IExpression e = GAML.getExpressionFactory().createConst(u.value(null), u.getType(),
					u.serialize(false));
			// System.out.println(" ==== Simplification of " + u.toGaml() + "
			// into " + e.toGaml());
			return e;
		}
		return u;
	}

	public BinaryOperator(final OperatorProto proto, final IDescription context, final IExpression... args) {
		super(proto, args);
		prototype.verifyExpectedTypes(context, exprs[1].getType());
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		final StringBuilder sb = new StringBuilder();
		final String name = getName();
		if (name.equals("internal_at")) {
			// '[' and ']' included
			sb.append(exprs[0].serialize(includingBuiltIn)).append(exprs[1].serialize(includingBuiltIn));
		} else if (OperatorProto.binaries.contains(name)) {
			parenthesize(sb, exprs[0]);
			sb.append(' ').append(name).append(' ');
			parenthesize(sb, exprs[1]);
		} else if (name.equals(IKeyword.AS)) {
			// Special case for the "as" operator
			sb.append(exprs[1].serialize(false)).append("(").append(exprs[0].serialize(includingBuiltIn)).append(")");
		} else {
			sb.append(name);
			parenthesize(sb, exprs[0], exprs[1]);
		}
		return sb.toString();
	}

	@Override
	public boolean shouldBeParenthesized() {
		final String s = getName();
		if (s.equals(".") || s.equals(":")) {
			return false;
		}
		return OperatorProto.binaries.contains(getName());
	}

	@Override
	public Object value(final IScope scope) throws GamaRuntimeException {
		Object leftVal = null, rightVal = null;
		try {
			leftVal = prototype.lazy[0] ? exprs[0] : exprs[0].value(scope);
			rightVal = prototype.lazy[1] ? exprs[1] : exprs[1].value(scope);
			final Object result = prototype.helper.run(scope, leftVal, rightVal);
			return result;
		} catch (final GamaRuntimeException ge) {
			throw ge;
		} catch (final RuntimeException ex) {
			// System.out.println(ex + " when applying the " + literalValue() +
			// " operator on " + Cast.toGaml(leftVal)
			// + " and " + Cast.toGaml(rightVal));
			final GamaRuntimeException e1 = GamaRuntimeException.create(ex, scope);
			e1.addContext("when applying the " + literalValue() + " operator on " + Cast.toGaml(leftVal) + " and "
					+ Cast.toGaml(rightVal));
			throw e1;
		}
	}

	@Override
	public BinaryOperator copy() {
		return new BinaryOperator(prototype, null, exprs);
	}

	public static class BinaryVarOperator extends BinaryOperator implements IVarExpression.Agent {

		public BinaryVarOperator(final OperatorProto proto, final IDescription context, final IExpression... args) {
			super(proto, context, args);
		}

		@Override
		public void setVal(final IScope scope, final Object v, final boolean create) throws GamaRuntimeException {
			final IAgent agent = Cast.asAgent(scope, exprs[0].value(scope));
			if (agent == null || agent.dead()) {
				return;
			}
			scope.setAgentVarValue(agent, exprs[1].literalValue(), v);
		}

		@Override
		public IExpression getOwner() {
			return exprs[0];
		}

		@Override
		public VariableExpression getVar() {
			return (VariableExpression) exprs[1];
		}

		@Override
		public boolean isNotModifiable() {
			return ((IVarExpression) exprs[1]).isNotModifiable();
		}

		@Override
		public String serialize(final boolean includingBuiltIn) {
			final StringBuilder sb = new StringBuilder();
			parenthesize(sb, exprs[0]);
			sb.append('.');
			sb.append(exprs[1].serialize(includingBuiltIn));
			return sb.toString();
			// return exprs[0].serialize(includingBuiltIn) + "." +
			// exprs[1].serialize(includingBuiltIn);
		}

		//
		@Override
		public BinaryVarOperator copy() {
			return new BinaryVarOperator(prototype, null, exprs);
		}
	}

}
