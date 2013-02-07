/**
 * Created by drogoul, 31 mars 2012
 * 
 */
package msi.gaml.descriptions;

import java.util.*;
import msi.gama.common.util.StringUtils;
import msi.gama.runtime.IScope;
import msi.gaml.expressions.*;
import msi.gaml.types.*;

/**
 * The class LabelExpressionDescription.
 * 
 * @author drogoul
 * @since 31 mars 2012
 * 
 */
public class LabelExpressionDescription extends BasicExpressionDescription {

	static Map<String, StringConstantExpression> cache = new HashMap();

	StringConstantExpression get(final String s) {
		StringConstantExpression sc = cache.get(s);
		if ( sc == null ) {
			sc = new StringConstantExpression(s);
			cache.put(s, sc);
		}
		return sc;
	}

	class StringConstantExpression extends AbstractExpression {

		StringConstantExpression(final String constant) {
			setName(constant);
			type = Types.get(IType.STRING);
			contentType = Types.get(IType.STRING);
		}

		@Override
		public Object value(final IScope scope) {
			return name;
		}

		@Override
		public boolean isConst() {
			return true;
		}

		@Override
		public String toGaml() {
			return StringUtils.toGamlString(name);
		}

		@Override
		public String getDocumentation() {
			return "Constant string: " + name;
		}

		@Override
		public String getTitle() {
			return "Constant string: " + name;
		}

	}

	final String value;

	public LabelExpressionDescription(final String label) {
		super((IExpression) null);
		value = label;
	}

	@Override
	public IExpressionDescription compileAsLabel() {
		return this;
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public IExpression getExpression() {
		return expression == null ? get(value) : expression;
	}

	@Override
	public IExpression compile(final IDescription context) {
		return getExpression();
	}

	@Override
	public boolean equalsString(final String o) {
		return value.equals(o);
	}

	@Override
	public boolean isString() {
		return true;
	}

}
