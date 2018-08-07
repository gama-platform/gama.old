package msi.gaml.statements.draw;

import java.util.HashMap;
import java.util.Map;

import msi.gama.runtime.IScope;
import msi.gaml.compilation.ISymbol;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

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

		public void refresh(final IScope scope);

		public V get();
	}

	public interface IExpressionWrapper<V> {
		public V value(IScope scope, IExpression facet);
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

	class ExpressionAttribute<V> implements Attribute<V> {
		final IExpression expression;
		private V value;

		public ExpressionAttribute(final IExpression ev) {
			expression = ev;
		}

		@Override
		public V value(final IScope scope) {
			return (V) expression.value(scope);
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
		return create(facet, exp, type, def);
	}

	protected <T extends IType<V>, V> Attribute<V> create(final String facet, final IExpression exp, final T type,
			final V def) {
		Attribute<V> result;
		if (exp != null) {
			if (exp.isConst()) {
				result = new ConstantAttribute<>(type.cast(null, exp.getConstValue(), null, true));
			} else {
				result = new ExpressionAttribute<>(exp);
			}
		} else {
			result = new ConstantAttribute<>(def);
		}
		attributes.put(facet, result);
		return result;

	}

	protected <T extends IType<V>, V> Attribute<V> create(final String facet, final IExpressionWrapper<V> ev,
			final T type, final V def) {
		final IExpression exp = symbol.getFacet(facet);
		Attribute<V> result;
		if (exp == null) {
			result = new ConstantAttribute<>(def);
		} else {
			result = new ExpressionEvaluator<>(ev, exp);
		}
		attributes.put(facet, result);
		return result;
	}

}
