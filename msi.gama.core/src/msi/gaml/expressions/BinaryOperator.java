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

import gnu.trove.set.hash.THashSet;
import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
import msi.gaml.descriptions.*;
import msi.gaml.operators.Cast;

/**
 * The Class BinaryOperator.
 */
public class BinaryOperator extends NAryOperator {

	public static IExpression
		create(final OperatorProto proto, final IDescription context, final IExpression ... child) {
		BinaryOperator u = new BinaryOperator(proto, context, child);
		if ( u.isConst() ) {
			IExpression e = GAML.getExpressionFactory().createConst(u.value(null), u.getType());
			// System.out.println("				==== Simplification of " + u.toGaml() + " into " + e.toGaml());
		}
		return u;
	}

	public BinaryOperator(final OperatorProto proto, final IDescription context, final IExpression ... args) {
		super(proto, args);
		prototype.verifyExpectedTypes(context, exprs[1].getType());
	}

	static Set<String> symbols = new THashSet(Arrays.asList("=", "+", "-", "/", "*", "^", "<", ">", "<=", ">=", "?",
		"!=", ":", ".", "where", "select", "collect", "first_with", "last_with", "overlapping", "at_distance", "in",
		"inside", "among", "contains", "contains_any", "contains_all", "min_of", "max_of", "with_max_of",
		"with_min_of", "of_species", "of_generic_species", "sort_by", "accumulate", "or", "and", "at", "is",
		"group_by", "index_of", "last_index_of", "index_by", "count", "sort", "::", "as_map"));

	@Override
	public String toGaml() {
		StringBuilder sb = new StringBuilder();
		String name = getName();
		if ( name.equals("internal_at") ) {
			// '[' and ']' included
			sb.append(exprs[0].toGaml()).append(exprs[1].toGaml());
		} else if ( symbols.contains(name) ) {
			parenthesize(sb, exprs[0]);
			sb.append(' ').append(name).append(' ');
			parenthesize(sb, exprs[1]);
		} else if ( name.equals(IKeyword.AS) ) {
			// Special case for the "as" operator
			sb.append(exprs[1].toGaml()).append("(").append(exprs[0].toGaml()).append(")");
		} else {
			sb.append(name);
			parenthesize(sb, exprs[0], exprs[1]);
		}
		return sb.toString();
	}

	@Override
	public boolean shouldBeParenthesized() {
		String s = getName();
		if ( s.equals(".") || s.equals(":") ) { return false; }
		return symbols.contains(getName());
	}

	@Override
	public Object value(final IScope scope) throws GamaRuntimeException {
		Object leftVal = null, rightVal = null;
		try {
			leftVal = exprs[0].value(scope);
			rightVal = prototype.lazy ? exprs[1] : exprs[1].value(scope);
			final Object result = prototype.helper.run(scope, leftVal, rightVal);
			return result;
		} catch (final RuntimeException ex) {
			final GamaRuntimeException e1 = GamaRuntimeException.create(ex);
			e1.addContext("when applying the " + literalValue() + " operator on " + Cast.toGaml(leftVal) + " and " +
				Cast.toGaml(rightVal));
			throw e1;
		}
	}

	@Override
	public BinaryOperator copy() {
		return new BinaryOperator(prototype, null, exprs);
	}

	public static class BinaryVarOperator extends BinaryOperator implements IVarExpression.Agent {

		public BinaryVarOperator(final OperatorProto proto, final IDescription context, final IExpression ... args) {
			super(proto, context, args);
		}

		@Override
		public void setVal(final IScope scope, final Object v, final boolean create) throws GamaRuntimeException {
			final IAgent agent = Cast.asAgent(scope, exprs[0].value(scope));
			if ( agent == null || agent.dead() ) { return; }
			scope.setAgentVarValue(agent, exprs[1].literalValue(), v);
		}

		@Override
		public boolean isNotModifiable() {
			return ((IVarExpression) exprs[1]).isNotModifiable();
		}

		@Override
		public String toGaml() {
			return exprs[0].toGaml() + "." + exprs[1].toGaml();
		}

		//
		@Override
		public BinaryVarOperator copy() {
			return new BinaryVarOperator(prototype, null, exprs);
		}
	}

}
