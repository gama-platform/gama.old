/*******************************************************************************************************
 *
 * BinaryOperator.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.expressions.operators;

import java.util.Arrays;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.util.StringUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.GAML;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IVarExpression;
import msi.gaml.expressions.types.TypeExpression;
import msi.gaml.expressions.variables.VariableExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

/**
 * The Class BinaryOperator.
 */
public class BinaryOperator extends AbstractNAryOperator {

	/**
	 * Creates the.
	 *
	 * @param proto
	 *            the proto
	 * @param context
	 *            the context
	 * @param child
	 *            the child
	 * @return the i expression
	 */
	public static IExpression create(final OperatorProto proto, final IDescription context,
			final IExpression... child) {
		final BinaryOperator u = new BinaryOperator(proto, context, child);
		if (u.isConst() && GamaPreferences.External.CONSTANT_OPTIMIZATION.getValue()) return GAML.getExpressionFactory()
				.createConst(u.getConstValue(), u.getGamlType(), u.serializeToGaml(false));
		return u;
	}

	@Override
	public Doc getDocumentation() {
		if (IKeyword.AS.equals(this.getName()) && exprs[1] instanceof TypeExpression) {
			IType t = exprs[1].getDenotedType();
			Doc doc = findDocOnType(t);
			if (doc == null) { doc = findDocOnType(t.getGamlType()); }
			if (doc != null) return doc;
		}
		return super.getDocumentation();
	}

	/**
	 * Find doc on type.
	 *
	 * @param type
	 *            the type
	 * @return the doc
	 */
	private Doc findDocOnType(final IType type) {
		Class<? extends IType> clazz = type.getClass();
		doc doc = null;
		java.lang.reflect.Method m = null;
		try {
			m = clazz.getDeclaredMethod("cast", IScope.class, Object.class, Object.class, boolean.class);
			if (m != null) { doc = m.getAnnotation(doc.class); }
		} catch (NoSuchMethodException | SecurityException e) {}
		if (doc == null) {
			try {
				m = clazz.getDeclaredMethod("cast", IScope.class, Object.class, Object.class, IType.class, IType.class,
						boolean.class);
				if (m != null) { doc = m.getAnnotation(doc.class); }
			} catch (NoSuchMethodException | SecurityException e) {}
		}
		if (doc != null) {
			Doc documentation = new RegularDoc(new StringBuilder(200));
			String s = doc.value();
			if (s != null && !s.isEmpty()) { documentation.append(s).append("<br/>"); }
			usage[] usages = doc.usages();
			for (usage u : usages) { documentation.append(u.value()).append("<br/>"); }
			s = doc.deprecated();
			if (s != null && !s.isEmpty()) {
				documentation.append("<b>Deprecated</b>: ").append("<i>").append(s).append("</i><br/>");
			}
			return documentation;
		}
		return null;
	}

	/**
	 * Instantiates a new binary operator.
	 *
	 * @param proto
	 *            the proto
	 * @param context
	 *            the context
	 * @param args
	 *            the args
	 */
	public BinaryOperator(final OperatorProto proto, final IDescription context, final IExpression... args) {
		super(proto, args);
		prototype.verifyExpectedTypes(context, exprs[1].getGamlType());
	}

	@Override
	public String serializeToGaml(final boolean includingBuiltIn) {
		final StringBuilder sb = new StringBuilder();
		final String name = getName();
		if ("internal_at".equals(name)) {
			// '[' and ']' included
			sb.append(exprs[0].serializeToGaml(includingBuiltIn)).append(exprs[1].serializeToGaml(includingBuiltIn));
		} else if (OperatorProto.binaries.contains(name)) {
			parenthesize(sb, exprs[0]);
			sb.append(' ').append(name).append(' ');
			parenthesize(sb, exprs[1]);
		} else if (IKeyword.AS.equals(name)) {
			// Special case for the "as" operator
			sb.append(exprs[1].serializeToGaml(false)).append("(").append(exprs[0].serializeToGaml(includingBuiltIn))
					.append(")");
		} else {
			sb.append(name);
			parenthesize(sb, exprs[0], exprs[1]);
		}
		return sb.toString();
	}

	@Override
	public boolean shouldBeParenthesized() {
		final String s = getName();
		if (".".equals(s) || ":".equals(s)) return false;
		return OperatorProto.binaries.contains(getName());
	}

	@Override
	public Object _value(final IScope scope) throws GamaRuntimeException {
		Object leftVal = null, rightVal = null;
		try {
			leftVal = prototype.getLazyness()[0] ? exprs[0] : exprs[0].value(scope);
			rightVal = prototype.getLazyness()[1] ? exprs[1] : exprs[1].value(scope);
			return prototype.getHelper().get(scope, leftVal, rightVal);
		} catch (final GamaRuntimeException ge) {
			throw ge;
		} catch (final Throwable ex) {
			final GamaRuntimeException e1 = GamaRuntimeException.create(ex, scope);
			e1.addContext("when applying the " + literalValue() + " operator on " + StringUtils.toGaml(leftVal, false)
					+ " and " + StringUtils.toGaml(rightVal, false));
			throw e1;
		}
	}

	@Override
	public BinaryOperator copy() {
		if (exprs == null) return new BinaryOperator(prototype, null);
		return new BinaryOperator(prototype, null, Arrays.copyOf(exprs, exprs.length));
	}

	/**
	 * The Class BinaryVarOperator.
	 */
	public static class BinaryVarOperator extends BinaryOperator implements IVarExpression.Agent {

		/** The definition description. */
		IDescription definitionDescription;

		/**
		 * Instantiates a new binary var operator.
		 *
		 * @param proto
		 *            the proto
		 * @param context
		 *            the context
		 * @param target
		 *            the target
		 * @param var
		 *            the var
		 */
		public BinaryVarOperator(final OperatorProto proto, final IDescription context, final IExpression target,
				final IVarExpression var) {
			super(proto, context, target, var);
			definitionDescription = context;
		}

		@Override
		public void setVal(final IScope scope, final Object v, final boolean create) throws GamaRuntimeException {
			final IAgent agent = Cast.asAgent(scope, exprs[0].value(scope));
			if (agent == null || agent.dead()) return;
			scope.setAgentVarValue(agent, exprs[1].literalValue(), v);
		}

		@Override
		public IExpression getOwner() { return exprs[0]; }

		@Override
		public VariableExpression getVar() { return (VariableExpression) exprs[1]; }

		@Override
		public IDescription getDefinitionDescription() { return definitionDescription; }

		@Override
		public boolean isNotModifiable() { return ((IVarExpression) exprs[1]).isNotModifiable(); }

		@Override
		public String serializeToGaml(final boolean includingBuiltIn) {
			final StringBuilder sb = new StringBuilder();
			parenthesize(sb, exprs[0]);
			sb.append('.');
			sb.append(exprs[1].serializeToGaml(includingBuiltIn));
			return sb.toString();
		}

		@Override
		public boolean isContextIndependant() { return false; }

		@Override
		public boolean isAllowedInParameters() { return true; }

		@Override
		public BinaryVarOperator copy() {
			return new BinaryVarOperator(prototype, null, exprs[0], (IVarExpression) exprs[1]);
		}
	}

}
