/*******************************************************************************************************
 *
 * StringBasedExpressionDescription.java, in msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2024 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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

	/** The string. */
	String string;

	/**
	 * Instantiates a new string based expression description.
	 *
	 * @param s
	 *            the s
	 */
	private StringBasedExpressionDescription(final String s) {
		super((EObject) null);
		string = s;
	}

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

	/**
	 * Creates the.
	 *
	 * @param string
	 *            the string
	 * @return the i expression description
	 */
	public static IExpressionDescription create(final String string) {
		if (string == null) return null;
		final String s = string.trim();
		if (IKeyword.NULL.equals(s)) return ConstantExpressionDescription.create((Object) null);
		if (IKeyword.FALSE.equals(s)) return ConstantExpressionDescription.create(false);
		if (IKeyword.TRUE.equals(s)) return ConstantExpressionDescription.create(true);
		if (StringUtils.isGamaString(s)) return LabelExpressionDescription.create(StringUtils.toJavaString(s));
		return new StringBasedExpressionDescription(string);
	}

	@Override
	public IType<?> getDenotedType(final IDescription context) {
		if ("0".equals(string)) return Types.NO_TYPE;
		IType type = context.getTypeNamed(string);
		if (type == Types.NO_TYPE) { type = super.getDenotedType(context); }
		return type;
	}

}
