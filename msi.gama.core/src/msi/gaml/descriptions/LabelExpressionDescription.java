/**
 * Created by drogoul, 31 mars 2012
 * 
 */
package msi.gaml.descriptions;

import java.util.*;
import msi.gama.common.util.StringUtils;
import msi.gama.runtime.IScope;
import msi.gaml.expressions.*;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.types.*;
import org.eclipse.emf.ecore.EObject;

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
			return getName();
		}

		@Override
		public boolean isConst() {
			return true;
		}

		@Override
		public String toGaml() {
			return StringUtils.toGamlString(getName());
		}

		@Override
		public String getDocumentation() {
			return "Constant string: " + getName();
		}

		@Override
		public String getTitle() {
			return "constant string '" + getName() + "'";
		}

	}

	final String value;

	private LabelExpressionDescription(final String label) {
		super((IExpression) null);
		value = StringUtils.unescapeJava(label);
	}

	@Override
	public IExpressionDescription cleanCopy() {
		// TODO Check that it does not cause any problem
		return this;
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
	public boolean isConstant() {
		return true;
	}

	@Override
	public void setTarget(final EObject newTarget) {
		super.setTarget(newTarget);
		if ( getExpression() != null ) {
			DescriptionFactory.setGamlDescription(newTarget, getExpression());
		}

	}

	@Override
	public Set<String> getStrings(final IDescription context, final boolean skills) {
		// Assuming of the form [aaa, bbb]
		Set<String> result = new HashSet();
		StringBuilder b = new StringBuilder();
		for ( char c : value.toCharArray() ) {
			switch (c) {
				case '[':
				case ' ':
					break;
				case ']':
				case ',': {
					result.add(b.toString());
					b.setLength(0);
					break;
				}
				default:
					b.append(c);
			}
		}
		return result;
	}

	public static IExpressionDescription create(final String s) {
		return new LabelExpressionDescription(s);
	}

}
