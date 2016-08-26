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

import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import gnu.trove.set.hash.THashSet;
import msi.gama.common.util.StringUtils;
import msi.gama.precompiler.GamlProperties;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The class LabelExpressionDescription.
 *
 * @author drogoul
 * @since 31 mars 2012
 *
 */
public class LabelExpressionDescription extends BasicExpressionDescription implements IExpression {

	@Override
	public IType getDenotedType(final IDescription context) {
		return context.getTypeNamed(value);
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
		if (expression == null) {
			expression = this;
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
	public void setTarget(final EObject newTarget) {
		super.setTarget(newTarget);
	}

	@Override
	public Set<String> getStrings(final IDescription context, final boolean skills) {
		// Assuming of the form [aaa, bbb]
		final Set<String> result = new THashSet();
		final StringBuilder b = new StringBuilder();
		for (final char c : value.toCharArray()) {
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

	@Override
	public String getDocumentation() {
		return "Constant string: " + getName();
	}

	@Override
	public String getTitle() {
		return "constant string '" + getName() + "'";
	}

	@Override
	public String getDefiningPlugin() {
		return null;
	}

	@Override
	public String getName() {
		return value;
	}

	@Override
	public void setName(final String newName) {
	}

	@Override
	public IType getType() {
		return Types.STRING;
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
	public String literalValue() {
		return value;
	}

	@Override
	public IExpression resolveAgainst(final IScope scope) {
		return this;
	}

	@Override
	public boolean shouldBeParenthesized() {
		return false;
	}

	@Override
	public void collectMetaInformation(final GamlProperties meta) {
	}

}
