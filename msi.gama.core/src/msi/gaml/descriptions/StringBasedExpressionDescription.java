/*******************************************************************************************************
 *
 * msi.gaml.descriptions.StringBasedExpressionDescription.java, in plugin msi.gama.core, is part of the source code of
 * the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.descriptions;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.StringUtils;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * The class StringBasedExpressionDescription.
 *
 * @author drogoul
 * @since 31 mars 2012
 *
 */
@SuppressWarnings ({ "rawtypes" })
public class StringBasedExpressionDescription extends BasicExpressionDescription {

	String string;

	private StringBasedExpressionDescription(final String s) {
		super((EObject) null);
		string = s;
	}

	// @Override
	// public String toString() {
	// return string;
	// }

	@Override
	public String toOwnString() {
		return string;
	}

	@Override
	public IExpressionDescription compileAsLabel() {
		return LabelExpressionDescription.create(string);
	}

	@Override
	public Set<String> getStrings(final IDescription context, final boolean skills) {
		// Assuming of the form [aaa, bbb]
		final Set<String> result = new HashSet<>();
		final StringBuilder b = new StringBuilder();
		for (final char c : string.toCharArray()) {
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

	@Override
	public IExpressionDescription cleanCopy() {
		final IExpressionDescription copy = new StringBasedExpressionDescription(string);
		copy.setTarget(target);
		return copy;
	}

	public static IExpressionDescription create(final String string) {
		if (string == null) { return null; }
		final String s = string.trim();
		if (s.equals(IKeyword.NULL)) { return ConstantExpressionDescription.create((Object) null); }
		if (s.equals(IKeyword.FALSE)) { return ConstantExpressionDescription.create(false); }
		if (s.equals(IKeyword.TRUE)) { return ConstantExpressionDescription.create(true); }
		if (StringUtils.isGamaString(s)) { return LabelExpressionDescription.create(StringUtils.toJavaString(s)); }
		return new StringBasedExpressionDescription(string);
	}

	@Override
	public IType<?> getDenotedType(final IDescription context) {
		IType type = context.getTypeNamed(string);
		if (type == Types.NO_TYPE) {
			type = super.getDenotedType(context);
		}
		return type;
	}

}
