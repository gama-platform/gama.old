/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.common.util;

import java.text.*;
import java.util.*;
import java.util.regex.*;
import msi.gama.common.interfaces.IValue;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.util.GamaList;
import msi.gaml.expressions.IExpression;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * The class StringUtils.
 * 
 * @author drogoul
 * @since 13 d�c. 2011
 * 
 */
public class StringUtils {

	final static String strings = "'(?:[^\\\\']+|\\\\.)*'"; // Old: '[^'\\\r\n]*(?:\\.[^'\\\r\n]*)*'
	final static String operators = "::|<>|!=|>=|<=|//";
	public final static String ponctuation = "\\p{Punct}";
	public final static String literals = "\\w+\\$\\w+|\\#\\w+|\\d+\\.\\d+|\\w+\\.\\w+|\\w+";
	final static String regex = strings + "|" + literals + "|" + operators + "|" + ponctuation;
	final static Pattern p = Pattern.compile(regex);

	static public String toGamlString(final String s) {
		if ( s == null ) { return null; }
		StringBuilder sb = new StringBuilder(s.length());
		sb.append('\'');
		sb.append(StringEscapeUtils.escapeJavaScript(s));
		sb.append('\'');
		return sb.toString();
	}

	static public String toJavaString(final String s) {
		if ( s == null ) { return null; }
		String t = s.trim();
		if ( !isGamaString(t) ) { return s; }
		if ( t.length() >= 2 ) { return t.substring(1, t.length() - 1); }
		return s;
	}

	public static List<String> tokenize(final String expression) {
		if ( expression == null ) { return Collections.EMPTY_LIST; }
		final List<String> tokens = new ArrayList<String>();
		final Matcher m = p.matcher(expression);
		while (m.find()) {
			tokens.add(expression.substring(m.start(), m.end()));
		}
		return tokens;
	}

	/**
	 * Unescape java.
	 * 
	 * @param str the str
	 * 
	 * @return the string
	 */
	static public String unescapeJava(final String str) {
		if ( str == null ) { return null; }
		StringBuilder writer = new StringBuilder(str.length());
		unescapeJava(writer, str);
		String result = writer.toString();
		writer.setLength(0);
		return result;

	}

	// private static final StringBuilder writer = new StringBuilder();
	// private static final StringBuilder unicode = new StringBuilder(4);

	/**
	 * Unescape java.
	 * 
	 * @param out the out
	 * @param str the str
	 */
	static private void unescapeJava(final StringBuilder writer, final String str) {
		if ( str == null ) { return; }
		final int sz = str.length();

		boolean hadSlash = false;
		boolean inUnicode = false;
		for ( int i = 0; i < sz; i++ ) {
			final char ch = str.charAt(i);
			if ( inUnicode ) {
				// if in unicode, then we're reading unicode
				// values in somehow
				StringBuilder unicode = new StringBuilder(4);
				unicode.append(ch);
				if ( unicode.length() == 4 ) {
					// digits
					// which represents our unicode character
					final int value = Integer.parseInt(unicode.toString(), 16);
					writer.append((char) value);
					unicode.setLength(0);
					inUnicode = false;
					hadSlash = false;
				}
				continue;
			}
			if ( hadSlash ) {
				// handle an escaped value
				hadSlash = false;
				switch (ch) {
					case '\\':
						writer.append('\\');
						break;
					case '\'':
						writer.append('\'');
						break;
					case '\"':
						writer.append('"');
						break;
					case 'r':
						writer.append('\r');
						break;
					case 'f':
						writer.append('\f');
						break;
					case 't':
						writer.append('\t');
						break;
					case 'n':
						writer.append('\n');
						break;
					case 'b':
						writer.append('\b');
						break;
					case 'u': {
						// uh-oh, we're in unicode country....
						inUnicode = true;
						break;
					}
					default:
						writer.append(ch);
						break;
				}
				continue;
			} else if ( ch == '\\' ) {
				hadSlash = true;
				continue;
			}
			writer.append(ch);
		}
		if ( hadSlash ) {
			// string, let's output it anyway.
			writer.append('\\');
		}
	}

	/**
	 * Gets the time in string.
	 * 
	 * @return the time in string
	 */
	public static String getTimeInString() {
		final Calendar cal = Calendar.getInstance();
		final SimpleDateFormat sdf = new SimpleDateFormat("_yyyy_MM_dd_HH_mm_ss");
		return sdf.format(cal.getTime());
	}

	static public boolean isGamaString(final String s) {
		if ( s == null ) { return false; }
		int n = s.length();
		if ( n == 0 || n == 1 ) { return false; }
		if ( s.charAt(0) != '\'' ) { return false; }
		if ( s.charAt(n - 1) != '\'' ) { return false; }
		return true;
	}

	public static final DecimalFormat DEFAULT_DECIMAL_FORMAT;
	public static final DecimalFormatSymbols SYMBOLS;

	static {
		SYMBOLS = new DecimalFormatSymbols();
		SYMBOLS.setDecimalSeparator('.');
		DEFAULT_DECIMAL_FORMAT = new DecimalFormat("##0.0################", SYMBOLS);
	}

	public static String toGaml(final Object val) {
		if ( val == null ) { return "nil"; }
		if ( val instanceof IExpression ) { return ((IExpression) val).toGaml(); }
		if ( val instanceof IPopulation ) { return ((IPopulation) val).getSpecies().getName(); }
		if ( val instanceof IValue ) { return ((IValue) val).toGaml(); }
		if ( val instanceof String ) { return toGamlString((String) val); }
		if ( val instanceof Double ) { return DEFAULT_DECIMAL_FORMAT.format(val); }
		if ( val instanceof Collection ) { return new GamaList((Collection) val).toGaml(); }
		return String.valueOf(val);
	}

}
