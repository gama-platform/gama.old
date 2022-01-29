/*******************************************************************************************************
 *
 * AggregatorGenerator.java, in msi.gama.ext, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.ext.kabeja.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import msi.gama.ext.kabeja.dxf.DXFDocument;

/**
 * The Class AggregatorGenerator.
 */
public class AggregatorGenerator extends AbstractSAXFilter implements SAXGenerator {

	/** The Constant ROOT_ELEMENT. */
	public final static String ROOT_ELEMENT = "aggregate";

	/** The Constant NAMESPACE. */
	public final static String NAMESPACE = "http://kabeja.org/aggregate";

	/** The generators. */
	protected List<SAXGenerator> generators = new ArrayList<>();

	/** The doc. */
	protected DXFDocument doc;

	@Override
	public void generate(final DXFDocument doc, final ContentHandler handler, final Map context) throws SAXException {
		this.setContentHandler(handler);
		this.doc = doc;

		try {
			handler.startDocument();

			String raw = NAMESPACE + ":" + ROOT_ELEMENT;
			handler.startElement(NAMESPACE, raw, ROOT_ELEMENT, new AttributesImpl());
			doGenerate();
			handler.endElement(NAMESPACE, raw, ROOT_ELEMENT);
			handler.endDocument();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Do generate.
	 *
	 * @throws SAXException
	 *             the SAX exception
	 */
	protected void doGenerate() throws SAXException {
		Iterator i = this.generators.iterator();

		while (i.hasNext()) {
			SAXGenerator generator = (SAXGenerator) i.next();
			generator.generate(this.doc, this, null);
		}
	}

	@Override
	public void endDocument() throws SAXException {
		// ignore
	}

	@Override
	public void startDocument() throws SAXException {
		// ignore
	}

	/**
	 * Adds the SAX generator.
	 *
	 * @param generator
	 *            the generator
	 */
	public void addSAXGenerator(final SAXGenerator generator) {
		this.generators.add(generator);
	}

	@Override
	public Map getProperties() { return this.properties; }
}
