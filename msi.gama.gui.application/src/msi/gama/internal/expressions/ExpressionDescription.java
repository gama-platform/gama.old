/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.internal.expressions;

import java.util.*;
import msi.gama.internal.types.GamaStringType;

public class ExpressionDescription extends ArrayList<String> implements CharSequence {

	private String string;
	public int length;

	public ExpressionDescription(final String expr) {
		this(expr, true);
	}

	public ExpressionDescription(final String str, final boolean tokenize) {
		String expr = str == null ? "" : str;
		setString(expr);
		if ( tokenize ) {
			addAll(GamaStringType.tokenize(expr));
		} else {
			add(expr);
		}
	}

	public ExpressionDescription(final List<String> tokens) {
		addAll(tokens);
		setString(subConcatenation(0, tokens.size()));

	}

	@Override
	public int length() {
		return length;
	}

	@Override
	public char charAt(final int index) {
		return string.charAt(index);
	}

	@Override
	public CharSequence subSequence(final int start, final int end) {
		return string.subSequence(start, end);
	}

	public String subConcatenation(final int start, final int end) {
		StringBuilder sb = new StringBuilder();
		for ( int i = start; i < end; i++ ) {
			sb.append(get(i)).append(" ");
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return string;
	}

	@Override
	public boolean equals(final Object c) {
		if ( c == null ) { return false; }
		if ( c instanceof String ) { return c.equals(getString()) || c.equals(getStringAsLabel()); }
		if ( c instanceof ExpressionDescription ) { return ((ExpressionDescription) c).getString()
			.equals(getString()); }
		return false;
	}

	public void setString(final String string) {
		this.string = string;
		length = string.length();
	}

	public String getString() {
		return string;
	}

	public String getStringAsLabel() {
		return GamaStringType.toJavaString(string);
	}

}
