/*******************************************************************************************************
 *
 * AttributeHolder.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gaml.compilation.ISymbol;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;

/**
 * A class that facilitates the development of classes holding attributes declared in symbols' facets
 *
 * @author drogoul
 *
 */
public abstract class AttributeHolder {

//	static {
//		DEBUG.OFF();
//	}

	/** The attributes. */
	final Map<String, Attribute<?>> attributes = new HashMap<>(10);

	/** The symbol. */
	final ISymbol symbol;

	/**
	 * The Interface Attribute.
	 *
	 * @param <V>
	 *            the value type
	 */
	public interface Attribute<V> extends IExpression {

		/**
		 * Refresh.
		 *
		 * @param scope
		 *            the scope
		 */
		void refresh(final String name, final IScope scope);

		/**
		 * Gets the.
		 *
		 * @return the v
		 */
		V get();
	}

	/**
	 * The Interface IExpressionWrapper.
	 *
	 * @param <V>
	 *            the value type
	 */
	public interface IExpressionWrapper<V> {

		/**
		 * Value.
		 *
		 * @param scope
		 *            the scope
		 * @param facet
		 *            the facet
		 * @return the v
		 */
		V value(IScope scope, IExpression facet);
	}

	/**
	 * The Class ConstantAttribute.
	 *
	 * @param <V>
	 *            the value type
	 */
	public static class ConstantAttribute<V> implements Attribute<V> {

		/** The value. */
		private final V value;

		/**
		 * Instantiates a new constant attribute.
		 *
		 * @param value
		 *            the value
		 */
		public ConstantAttribute(final V value) {
			this.value = value;
		}

		/**
		 * Refresh.
		 *
		 * @param scope
		 *            the scope
		 */
		@Override
		public void refresh(final String name, final IScope scope) {}

		@Override
		public V value(final IScope scope) {
			return value;
		}

		@Override
		public V get() {
			return value;
		}

	}

	/**
	 * The Class ExpressionAttribute.
	 *
	 * @param <T>
	 *            the generic type
	 * @param <V>
	 *            the value type
	 */
	static class ExpressionAttribute<T extends IType<V>, V> implements Attribute<V> {

		/** The expression. */
		final IExpression expression;

		/** The return type. */
		final T returnType;

		/** The value. */
		private volatile V value;

		/**
		 * Instantiates a new expression attribute.
		 *
		 * @param type
		 *            the type
		 * @param ev
		 *            the ev
		 * @param init
		 *            the init
		 */
		public ExpressionAttribute(final T type, final IExpression ev, final V init) {
			expression = ev;
			returnType = type;
			value = init;
		}

		@Override
		public V value(final IScope scope) {
			return returnType.cast(scope, expression.value(scope), null, false);
		}

		/**
		 * Refresh.
		 *
		 * @param scope
		 *            the scope
		 */
		@Override
		public void refresh(final String name, final IScope scope) {
			value = value(scope);
		}

		@Override
		public V get() {
			return value;
		}

	}

	/**
	 * The Class ExpressionEvaluator.
	 *
	 * @param <V>
	 *            the value type
	 */
	static class ExpressionEvaluator<V> implements Attribute<V> {

		/** The evaluator. */
		final IExpressionWrapper<V> evaluator;

		/** The facet. */
		final IExpression facet;

		/** The value. */
		private V value;

		/**
		 * Instantiates a new expression evaluator.
		 *
		 * @param ev
		 *            the ev
		 * @param expression
		 *            the expression
		 */
		public ExpressionEvaluator(final IExpressionWrapper<V> ev, final IExpression expression) {
			evaluator = ev;
			facet = expression;
		}

		@Override
		public V value(final IScope scope) {
			return evaluator.value(scope, facet);
		}

		/**
		 * Refresh.
		 *
		 * @param scope
		 *            the scope
		 */
		@Override
		public void refresh(final String name, final IScope scope) {
			value = value(scope);
		}

		@Override
		public V get() {
			return value;
		}

	}

	/**
	 * Refresh.
	 *
	 * @param scope
	 *            the scope
	 * @return the attribute holder
	 */
	public AttributeHolder refresh(final IScope scope) {
		attributes.forEach((name, attribute) -> { attribute.refresh(name, scope); });
		return this;
	}

	/**
	 * Instantiates a new attribute holder.
	 *
	 * @param symbol
	 *            the symbol
	 */
	public AttributeHolder(final ISymbol symbol) {
		this.symbol = symbol;
	}

	/**
	 * Creates the.
	 *
	 * @param <V>
	 *            the value type
	 * @param facet
	 *            the facet
	 * @param def
	 *            the def
	 * @return the attribute
	 */
	protected <V> Attribute<V> create(final String facet, final V def) {
		final Attribute<V> result = new ConstantAttribute<>(def);
		attributes.put(facet, result);
		return result;
	}

	/**
	 * Creates the.
	 *
	 * @param <T>
	 *            the generic type
	 * @param <V>
	 *            the value type
	 * @param facet
	 *            the facet
	 * @param type
	 *            the type
	 * @param def
	 *            the def
	 * @return the attribute
	 */
	protected <T extends IType<V>, V> Attribute<V> create(final String facet, final T type, final V def) {
		final IExpression exp = symbol.getFacet(facet);
		return create(facet, exp, type, def, e -> type.cast(null, e.getConstValue(), null, true));
	}

	/**
	 * Creates the.
	 *
	 * @param <T>
	 *            the generic type
	 * @param <V>
	 *            the value type
	 * @param facet
	 *            the facet
	 * @param exp
	 *            the exp
	 * @param type
	 *            the type
	 * @param def
	 *            the def
	 * @param constCaster
	 *            the const caster
	 * @return the attribute
	 */
	protected <T extends IType<V>, V> Attribute<V> create(final String facet, final IExpression exp, final T type,
			final V def, final Function<IExpression, V> constCaster) {
		Attribute<V> result;
		// AD 10/12/19 Changed because it was creating problems with constant
		// boolean values meant to indicate the presence or absence of the property
		// see #2902 and #2913
		if (exp == null || exp.isConst() && exp.isContextIndependant() && exp.getGamlType() != Types.BOOL) {
			result = new ConstantAttribute<>(exp == null ? def : constCaster.apply(exp));
		} else {
			result = new ExpressionAttribute<>(type, exp, def);
		}
		attributes.put(facet, result);
		return result;

	}

	/**
	 * Creates the.
	 *
	 * @param <T>
	 *            the generic type
	 * @param <V>
	 *            the value type
	 * @param facet
	 *            the facet
	 * @param ev
	 *            the ev
	 * @param type
	 *            the type
	 * @param def
	 *            the def
	 * @param constCaster
	 *            the const caster
	 * @return the attribute
	 */
	protected <T extends IType<V>, V> Attribute<V> create(final String facet, final IExpressionWrapper<V> ev,
			final T type, final V def, final Function<IExpression, V> constCaster) {
		final IExpression exp = symbol.getFacet(facet);
		Attribute<V> result;
		// AD 10/12/19 Changed because it was creating problems with constant
		// boolean values meant to indicate the presence or absence of the property
		// see #2902 and #2913
		if (exp == null || exp.isConst() && exp.isContextIndependant() && exp.getGamlType() != Types.BOOL) {
			result = new ConstantAttribute<>(exp == null ? def : constCaster != null ? constCaster.apply(exp) : def);
		} else {
			result = new ExpressionEvaluator<>(ev, exp);
		}
		attributes.put(facet, result);
		return result;
	}

}
