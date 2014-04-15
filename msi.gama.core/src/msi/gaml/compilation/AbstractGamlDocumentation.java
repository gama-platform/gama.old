/*********************************************************************************************
 * 
 *
 * 'AbstractGamlDocumentation.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.compilation;

import java.nio.charset.Charset;
import java.util.*;

/**
 * Class AbstractGamlDocumentation.
 * 
 * @author drogoul
 * @since 13 avr. 2014
 * 
 */
public class AbstractGamlDocumentation {

	private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

	private static final List<byte[]> main = new ArrayList();
	private static final List<byte[]> deprecated = new ArrayList();

	public AbstractGamlDocumentation() {}

	protected final void AS() {
		S((String[]) null);
	}

	protected final void S(final String ... strings) {
		if ( strings == null || strings.length == 0 ) {
			main.add(null);
			deprecated.add(null);
			return;
		}
		if ( strings.length == 1 || strings[1] == null ) {
			main.add(encode(strings[0]));
			deprecated.add(null);
			return;
		}
		main.add(encode(strings[0]));
		deprecated.add(encode(strings[1]));
	}

	public static byte[] encode(final String string) {
		if ( string == null ) { return null; }
		return string.getBytes(UTF8_CHARSET);
	}

	public static String decode(final byte[] bytes) {
		if ( bytes == null ) { return null; }
		return new String(bytes, UTF8_CHARSET);
	}

	public static String getMain(final int doc) {
		if ( doc == -1 ) { return null; }
		return decode(main.get(doc));
	}

	public static String getDeprecated(final int doc) {
		if ( doc == -1 ) { return null; }
		return decode(deprecated.get(doc));
	}

	public static int getCount() {
		return main.size();
	}

	/**
	 * @param doc
	 * @return
	 */
	public static byte[] getRawMain(final int doc) {
		if ( doc == -1 ) { return null; }
		return main.get(doc);
	}

}
