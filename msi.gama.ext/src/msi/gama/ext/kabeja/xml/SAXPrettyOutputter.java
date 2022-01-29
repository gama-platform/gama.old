/*******************************************************************************************************
 *
 * SAXPrettyOutputter.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.kabeja.xml;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * <p>
 * This outputs a SAXStream to an OutputStream with the given encoding or otherwise with the default encoding (utf-8).
 * </p>
 * <p>
 * <b>Note: </b> Not all features are implemented, so if you use this with other SAXStreams others then the
 * Kabeja-SAXStream you will get broken XML-Documents.
 * </p>
 *
 * @author <a href="mailto:simon.mieth@gmx.de">Simon Mieth</a>
 *
 */
public class SAXPrettyOutputter extends AbstractSAXSerializer implements SAXSerializer {

	/** The Constant DEFAULT_ENCODING. */
	public static final String DEFAULT_ENCODING = "UTF-8";

	/** The Constant SUFFIX. */
	public static final String SUFFIX = "svg";

	/** The Constant SUFFIX_GZIP. */
	public static final String SUFFIX_GZIP = "svgz";

	/** The Constant MIMETYPE. */
	public static final String MIMETYPE = "text/svg";

	/** The Constant PROPERTY_ENCODING. */
	public static final String PROPERTY_ENCODING = "encoding";

	/** The Constant PROPERTY_GZIP. */
	public static final String PROPERTY_GZIP = "gzip";

	/** The out. */
	private OutputStreamWriter out;

	/** The encoding. */
	private String encoding;

	/** The dtd. */
	private String dtd;

	/** The indent. */
	private int indent = 0;

	/** The parent. */
	private boolean parent = false;

	/** The text content list. */
	private final ArrayList<Boolean> textContentList = new ArrayList<>();

	/** The rootxmlns. */
	protected HashMap<String, String> rootxmlns = new HashMap<>();

	/** The gzip. */
	protected boolean gzip = false;

	/**
	 * Instantiates a new SAX pretty outputter.
	 *
	 * @param output
	 *            the output
	 * @param encoding
	 *            the encoding
	 */
	public SAXPrettyOutputter(final OutputStream output, final String encoding) {
		this.encoding = encoding;
		this.setOutput(output);
	}

	/**
	 *
	 */
	public SAXPrettyOutputter(final OutputStream out) {
		this(out, DEFAULT_ENCODING);
	}

	/**
	 * Instantiates a new SAX pretty outputter.
	 */
	public SAXPrettyOutputter() {
		this.encoding = DEFAULT_ENCODING;
	}

	@Override
	public void characters(final char[] ch, final int start, final int length) throws SAXException {
		try {
			if (length > 0) {
				if (parent) {
					this.out.write(">");
					parent = false;
				}

				char[] enc = encodeXML(new String(ch, 0, length)).toCharArray();
				this.out.write(enc, start, enc.length);

				// textNode in this context
				textContentList.set(textContentList.size() - 1, true);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void endDocument() throws SAXException {
		try {
			this.out.flush();
			this.out.close();

			textContentList.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
		try {
			if (parent) {
				this.out.write("/>");
			} else {
				// check for textNodes in this context
				Boolean b = textContentList.remove(textContentList.size() - 1);

				if (b.booleanValue()) {} else {
					// there was no textNode we can create a new line
					this.out.write('\n');
					indentOutput(indent);
				}
				this.out.write("</" + qName + ">");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		indent--;
		parent = false;
	}

	@Override
	public void endPrefixMapping(final String prefix) throws SAXException {}

	@Override
	public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {}

	@Override
	public void processingInstruction(final String target, final String data) throws SAXException {}

	@Override
	public void setDocumentLocator(final Locator locator) {}

	@Override
	public void skippedEntity(final String name) throws SAXException {}

	@Override
	public void startDocument() throws SAXException {
		indent = 0;

		try {
			this.out.write("<?xml version=\"1.0\" encoding=\"" + encoding + "\" ?>");

			if (this.dtd != null) { this.out.write("\n<!DOCTYPE " + dtd + ">"); }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void startElement(final String namespaceURI, final String localName, final String qName,
			final Attributes atts) throws SAXException {
		this.indent++;

		try {
			if (this.parent) {
				// we are nested
				this.out.write(">");
			} else {
				this.parent = true;
			}

			// first create a new line
			this.out.write('\n');

			// indent the line
			this.indentOutput(indent);

			// the element
			this.out.write("<" + qName);

			int attrCount = atts.getLength();

			for (int i = 0; i < attrCount; i++) {
				// we need a white space between the
				// attributes
				this.indentOutput(1);

				// String uri = atts.getURI(i);
				String qname = atts.getQName(i);

				// if (uri.length() > 0) {
				// String prefix = qname.substring(0, qname.indexOf(':'));
				// out
				// .write(" xmlns:" + prefix + "=\"" + uri
				// + "\" ");
				// }
				String value = atts.getValue(i);
				if (value == null) { value = ""; }
				this.out.write(qname + "=\"" + encodeXML(atts.getValue(i)) + "\"");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// no text in this context now
		this.textContentList.add(false);
	}

	@Override
	public void startPrefixMapping(final String prefix, final String uri) throws SAXException {}

	/**
	 * Indent the output
	 *
	 * @param indentSize
	 */
	private void indentOutput(final int indentSize) {
		try {
			for (int i = 0; i < indentSize; i++) { this.out.write(' '); }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Encode XML.
	 *
	 * @param text
	 *            the text
	 * @return the string
	 */
	public static String encodeXML(final String text) {
		int length = text.length();
		StringBuilder work = new StringBuilder(length);

		for (int i = 0; i < length; i++) {
			char c = text.charAt(i);

			switch (c) {
				case '&':
					work.append("&amp;");
					break;
				case '<':
					work.append("&lt;");
					break;
				case '>':
					work.append("&gt;");
					break;
				default:
					if (!Character.isIdentifierIgnorable(c)) { work.append(c); }
					break;
			}
		}

		return work.toString();
	}

	/**
	 * Sets the dtd.
	 *
	 * @param dtd
	 *            the new dtd
	 */
	public void setDTD(final String dtd) { this.dtd = dtd; }

	/**
	 * Query XMLNS.
	 *
	 * @param atts
	 *            the atts
	 */
	protected void queryXMLNS(final Attributes atts) {
		for (int i = 0; i < atts.getLength(); i++) {
			String qname = atts.getQName(i);

			if (qname.startsWith("xmlns:")) {
				String prefix = atts.getLocalName(i);
				String uri = atts.getValue(i);
				rootxmlns.put(uri, prefix);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.kabeja.xml.SAXSerializer#getMimeType()
	 */
	@Override
	public String getMimeType() { return MIMETYPE; }

	/*
	 * (non-Javadoc)
	 *
	 * @see org.kabeja.xml.SAXSerializer#getSuffix()
	 */
	@Override
	public String getSuffix() {
		if (gzip) return SUFFIX_GZIP;
		return SUFFIX;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.kabeja.xml.SAXSerializer#setOutput(java.io.OutputStream)
	 */
	@Override
	public void setOutput(final OutputStream out) {
		OutputStream bout = null;

		try {
			if (gzip) {
				bout = new BufferedOutputStream(new GZIPOutputStream(out));
			} else {
				bout = new BufferedOutputStream(out);
			}

			this.out = new OutputStreamWriter(bout, this.encoding);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.kabeja.xml.SAXSerializer#setProperties(java.util.Map)
	 */
	@Override
	public void setProperties(final Map properties) {
		this.properties = properties;

		if (properties.containsKey(PROPERTY_ENCODING)) { this.encoding = (String) properties.get(PROPERTY_ENCODING); }

		if (properties.containsKey(PROPERTY_GZIP)) {
			this.gzip = Boolean.getBoolean((String) properties.get(PROPERTY_GZIP));
		}
	}
}
