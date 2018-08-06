package msi.gaml.statements.draw;

import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

public class AttributeHolder {

	@FunctionalInterface
	protected interface Evaluator<V> {
		V value(IScope scope);

		default V getConstValue() {
			try {
				return value(null);
			} catch (final RuntimeException e) {
				return null;
			}
		}
	}

	public abstract class Attribute<T extends IType<?>, V> implements Evaluator<V> {
		public V value;

		public void refresh(final IScope scope) {
			value = value(scope);
		}
	}

	class ConstantAttribute<T extends IType<?>, V> extends Attribute<T, V> {

		ConstantAttribute(final V value) {
			this.value = value;
		}

		@Override
		public void refresh(final IScope scope) {}

		@Override
		public V value(final IScope scope) {
			return value;
		}

	}

	class ExpressionAttribute<T extends IType<?>, V> extends Attribute<T, V> {
		final Evaluator<V> evaluator;

		public ExpressionAttribute(final Evaluator<V> ev) {
			evaluator = ev;
		}

		@Override
		public V value(final IScope scope) {
			return evaluator.value(scope);
		}
	}

	protected <T extends IType<V>, V> Attribute<T, V> create(final IExpression exp, final T type, final V def) {
		return create(exp, (scope) -> type.cast(scope, exp.value(scope), null, true), type, def);
	}

	protected <T extends IType<V>, V> Attribute<T, V> create(final IExpression exp, final Evaluator<V> ev, final T type,
			final V def) {
		if (exp != null
				&& exp.isConst()) { return new ConstantAttribute<>(type.cast(null, ev.getConstValue(), null, true)); }
		if (exp == null) { return new ConstantAttribute<>(def); }
		return new ExpressionAttribute<>(ev);
	}

}
