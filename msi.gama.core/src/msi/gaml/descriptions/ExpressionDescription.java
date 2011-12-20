/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.descriptions;

import java.util.*;
import msi.gama.common.util.StringUtils;

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
			addAll(StringUtils.tokenize(expr));
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
		return StringUtils.toJavaString(string);
	}

}
