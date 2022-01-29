/*******************************************************************************************************
 *
 * FilterExample.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.kabeja.parser.dxf.filter;

import java.util.HashMap;
import java.util.Map;

import msi.gama.ext.kabeja.parser.DXFParser;
import msi.gama.ext.kabeja.parser.ParserBuilder;

/**
 * The Class FilterExample.
 */
public class FilterExample {
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		try {
			DXFParser parser = (DXFParser) ParserBuilder.createDefaultParser();

			// test
			DXFStreamFilter filter = new DXFStreamLayerFilter();
			Map<String, String> p = new HashMap<>();
			p.put("layers.include", args[0]);
			filter.setProperties(p);
			parser.addDXFStreamFilter(filter);
			parser.parse(args[1]);

			// DXFDocument doc = parser.getDocument();

			// do something with the doc
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
