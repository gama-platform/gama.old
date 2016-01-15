/*********************************************************************************************
 *
 *
 * 'LabelExpressionDescription.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.descriptions;

import java.util.*;
import org.eclipse.emf.ecore.EObject;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
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

	static Map<String, StringConstantExpression> cache = new THashMap();

	@Override
	public IType getDenotedType(final IDescription context) {
		return context.getTypeNamed(value);
	}

	static class StringConstantExpression extends AbstractExpression {

		StringConstantExpression(final String constant) {
			setName(constant);
			type = Types.STRING;
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
		public String serialize(final boolean includingBuiltIn) {
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

		/**
		 * Method collectPlugins()
		 * @see msi.gaml.descriptions.IGamlDescription#collectPlugins(java.util.Set)
		 */
		@Override
		public void collectPlugins(final Set<String> plugins) {}

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
	public String serialize(final boolean includingBuiltIn) {
		return value;
		// return StringUtils.toGamlString(value);
	}

	@Override
	public IExpression getExpression() {
		if ( expression == null ) {
			expression = new StringConstantExpression(value);
		}
		return expression;
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
		// if ( getExpression() != null ) {
		// DescriptionFactory.setGamlDocumentation(newTarget, getExpression());
		// }

	}

	@Override
	public Set<String> getStrings(final IDescription context, final boolean skills) {
		// Assuming of the form [aaa, bbb]
		Set<String> result = new THashSet();
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
