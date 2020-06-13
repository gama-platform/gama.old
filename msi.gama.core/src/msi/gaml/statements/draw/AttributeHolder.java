/*******************************************************************************************************
 *
 * msi.gaml.statements.draw.AttributeHolder.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import msi.gama.runtime.IScope;
import msi.gaml.compilation.ISymbol;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * A class that facilitates the development of classes holding attributes declared in symbols' facets
 *
 * @author drogoul
 *
 */
public abstract class AttributeHolder {

	final Map<String, Attribute<?>> attributes = new HashMap<>(10);
	final ISymbol symbol;

	public interface Attribute<V> extends IExpression {

		void refresh(final IScope scope);

		V get();
	}

	public interface IExpressionWrapper<V> {
		V value(IScope scope, IExpression facet);
	}

	public class ConstantAttribute<V> implements Attribute<V> {
		private final V value;

		public ConstantAttribute(final V value) {
			this.value = value;
		}

		@Override
		public void refresh(final IScope scope) {}

		@Override
		public V value(final IScope scope) {
			return value;
		}

		@Override
		public V get() {
			return value;
		}

	}

	class ExpressionAttribute<T extends IType<V>, V> implements Attribute<V> {
		final IExpression expression;
		final T returnType;
		private V value;

		public ExpressionAttribute(final T type, final IExpression ev) {
			expression = ev;
			returnType = type;
		}

		@Override
		public V value(final IScope scope) {
			return returnType.cast(scope, expression.value(scope), null, false);
		}

		@Override
		public void refresh(final IScope scope) {
			value = value(scope);
		}

		@Override
		public V get() {
			return value;
		}

	}

	class ExpressionEvaluator<V> implements Attribute<V> {
		final IExpressionWrapper<V> evaluator;
		final IExpression facet;
		private V value;

		public ExpressionEvaluator(final IExpressionWrapper<V> ev, final IExpression expression) {
			evaluator = ev;
			facet = expression;
		}

		@Override
		public V value(final IScope scope) {
			return evaluator.value(scope, facet);
		}

		@Override
		public void refresh(final IScope scope) {
			value = value(scope);
		}

		@Override
		public V get() {
			return value;
		}

	}

	public AttributeHolder refresh(final IScope scope) {
		attributes.forEach((name, attribute) -> attribute.refresh(scope));
		return this;
	}

	public AttributeHolder(final ISymbol symbol) {
		this.symbol = symbol;
	}

	protected <V> Attribute<V> create(final String facet, final V def) {
		final Attribute<V> result = new ConstantAttribute<>(def);
		attributes.put(facet, result);
		return result;
	}

	protected <T extends IType<V>, V> Attribute<V> create(final String facet, final T type, final V def) {
		final IExpression exp = symbol.getFacet(facet);
		return create(facet, exp, type, def, (e) -> type.cast(null, e.getConstValue(), null, true));
	}

	protected <T extends IType<V>, V> Attribute<V> create(final String facet, final IExpression exp, final T type,
			final V def, final Function<IExpression, V> constCaster) {
		Attribute<V> result;
		// AD 10/12/19 Changed because it was creating problems with constant
		// boolean values meant to indicate the presence or absence of the property
		// see #2902 and #2913
		if (exp == null || exp.isConst() && exp.isContextIndependant() && exp.getGamlType() != Types.BOOL) {
			result = new ConstantAttribute<>(exp == null ? def : constCaster.apply(exp));
		} else {
			result = new ExpressionAttribute<>(type, exp);
		}
		attributes.put(facet, result);
		return result;

	}

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
