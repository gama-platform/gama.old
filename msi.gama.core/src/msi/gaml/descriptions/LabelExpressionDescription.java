/**
 * Created by drogoul, 31 mars 2012
 * 
 */
package msi.gaml.descriptions;

import msi.gama.common.util.StringUtils;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.*;
import org.eclipse.emf.common.notify.*;

/**
 * The class LabelExpressionDescription.
 * 
 * @author drogoul
 * @since 31 mars 2012
 * 
 */
public class LabelExpressionDescription extends BasicExpressionDescription {

	static class StringConstantExpression implements IExpression {

		String value;

		StringConstantExpression(final String constant) {
			value = constant;
		}

		@Override
		public Object value(final IScope scope) throws GamaRuntimeException {
			return value;
		}

		@Override
		public boolean isConst() {
			return true;
		}

		@Override
		public String toGaml() {
			return StringUtils.toGamlString(value);
		}

		@Override
		public String literalValue() {
			return value;
		}

		@Override
		public IType getContentType() {
			return Types.get(IType.STRING);
		}

		@Override
		public IType getType() {
			return Types.get(IType.STRING);
		}

		/**
		 * @see msi.gaml.expressions.IExpression#getDocumentation()
		 */
		@Override
		public String getDocumentation() {
			return "Constant string: " + value;
		}

		@Override
		public String getName() {
			return value;
		}

		/**
		 * @see msi.gaml.descriptions.IGamlDescription#dispose()
		 */
		@Override
		public void dispose() {}

		/**
		 * @see msi.gaml.descriptions.IGamlDescription#getTitle()
		 */
		@Override
		public String getTitle() {
			return "Constant string: " + value;
		}

		/**
		 * @see org.eclipse.emf.common.notify.Adapter#notifyChanged(org.eclipse.emf.common.notify.Notification)
		 */
		@Override
		public void notifyChanged(final Notification notification) {}

		/**
		 * @see org.eclipse.emf.common.notify.Adapter#getTarget()
		 */
		@Override
		public Notifier getTarget() {
			return null;
		}

		/**
		 * @see org.eclipse.emf.common.notify.Adapter#setTarget(org.eclipse.emf.common.notify.Notifier)
		 */
		@Override
		public void setTarget(final Notifier newTarget) {}

		/**
		 * @see org.eclipse.emf.common.notify.Adapter#isAdapterForType(java.lang.Object)
		 */
		@Override
		public boolean isAdapterForType(final Object type) {
			return false;
		}

		@Override
		public void unsetTarget(final Notifier oldTarget) {}

	}

	public LabelExpressionDescription(final String label) {
		super(new StringConstantExpression(label));
	}

	@Override
	public IExpressionDescription compileAsLabel() {
		return this;
	}

	@Override
	public String toString() {
		return expression.literalValue();
	}

}
