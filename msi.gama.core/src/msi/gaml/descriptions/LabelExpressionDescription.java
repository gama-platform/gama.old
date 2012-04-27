/**
 * Created by drogoul, 31 mars 2012
 * 
 */
package msi.gaml.descriptions;

import msi.gama.common.util.StringUtils;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.*;
import msi.gaml.types.*;

/**
 * The class LabelExpressionDescription.
 * 
 * @author drogoul
 * @since 31 mars 2012
 * 
 */
public class LabelExpressionDescription implements IExpressionDescription {

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
		public IType type() {
			return Types.get(IType.STRING);
		}

	}

	IExpression expression;
	Object ast;

	// static WeakHashMap<String, StringConstantExpression> cache = new WeakHashMap();

	public LabelExpressionDescription(final String label) {
		// StringConstantExpression existing = cache.get(label);
		// if ( existing == null ) {
		// existing = new StringConstantExpression(label);
		// cache.put(label, existing);
		// }
		expression = new StringConstantExpression(label);
	}

	@Override
	public IExpressionDescription compileAsLabel() {
		return this;
	}

	@Override
	public String toString() {
		return expression.literalValue();
	}

	@Override
	public void setExpression(final IExpression expr) {
		// We remove the description from the cache as its expression may be anything (and not only
		// a StringConstantExpression)
		// if ( expr != expression ) {
		// cache.remove(expression.literalValue());
		expression = expr;
		// }
	}

	@Override
	public IExpression compile(final IDescription context, final IExpressionFactory factory) {
		return expression;
	}

	@Override
	public Object getAst() {
		return ast;
	}

	@Override
	public IExpression getExpression() {
		return expression;
	}

	@Override
	public void dispose() {}

	@Override
	public boolean equalsString(final String o) {
		return expression.literalValue() == null ? o == null : expression.literalValue().equals(o);
	}

	/**
	 * @param ast
	 */
	public void setAst(final Object ast) {
		this.ast = ast;
	}

}
