/*******************************************************************************************************
 *
 * ConfigHelper.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.kabeja.tools;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class ConfigHelper {

	/** The Constant JAVA_14_SAX_DRIVER. */
	public static final String JAVA_14_SAX_DRIVER = "org.apache.crimson.parser.XMLReaderImpl";

	/** The Constant JAVA_15_SAX_DRIVER. */
	public static final String JAVA_15_SAX_DRIVER = "com.sun.org.apache.xerces.internal.parsers.SAXParser";

	/**
	 * Gets the SAXSD driver.
	 *
	 * @return the SAXSD driver
	 */
	public static String getSAXSDDriver() {
		// check for version 1.4 and above
		// String ver = System.getProperty("java.version");
		String parser = null;

		try {
			parser = SAXParserFactory.newInstance().newSAXParser().getXMLReader().getClass().getName();

			// XMLReader r = XMLReaderFactory.createXMLReader(parser);
		} catch (SAXException | ParserConfigurationException e) {
			e.printStackTrace();
		}

		// if (ver.startsWith("1.2") || ver.startsWith("1.3")) {
		// parser = System.getProperty("org.xml.sax.driver");
		// } else if (ver.startsWith("1.4")) {
		// // jdk 1.4 uses crimson
		// parser = JAVA_14_SAX_DRIVER;
		// } else if (ver.startsWith("1.5")) {
		// parser = JAVA_15_SAX_DRIVER;
		// }
		return parser;
	}
}
