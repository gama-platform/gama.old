/*
 * SVGLoader.java
 *
 *
 * The Salamander Project - 2D and 3D graphics libraries in Java Copyright (C) 2004 Mark McKay
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Mark McKay can be contacted at mark@kitfox.com. Salamander and other projects can be found at http://www.kitfox.com
 *
 * Created on February 18, 2004, 5:09 PM
 */

package msi.gama.util.file.svgsalamander_copy;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class SVGLoader extends DefaultHandler {
	final HashMap<String, Class<? extends SVGElement>> nodeClasses = new HashMap<>();
	final LinkedList<SVGElement> buildStack = new LinkedList<>();

	final SVGLoaderHelper helper;

	/**
	 * The diagram that represents the base of this SVG document we're loading. Will be augmented to include node
	 * indexing info and other useful stuff.
	 */
	final SVGDiagram diagram;

	// Used to keep track of document elements that are not part of the SVG namespace
	int skipNonSVGTagDepth = 0;
	int indent = 0;

	public SVGLoader(final URI xmlBase, final SVGUniverse universe) {

		diagram = new SVGDiagram(xmlBase, universe);

		nodeClasses.put("circle", Circle.class);
		nodeClasses.put("ellipse", Ellipse.class);
		nodeClasses.put("g", Group.class);
		nodeClasses.put("line", Line.class);
		nodeClasses.put("path", Path.class);
		nodeClasses.put("polygon", Polygon.class);
		nodeClasses.put("polyline", Polyline.class);
		nodeClasses.put("rect", Rect.class);
		nodeClasses.put("shape", ShapeElement.class);
		nodeClasses.put("stop", Stop.class);
		nodeClasses.put("svg", SVGRoot.class);

		helper = new SVGLoaderHelper(xmlBase, universe, diagram);
	}

	@Override
	public void startElement(final String namespaceURI, final String tagName, final String qName,
			final Attributes attrs) throws SAXException {
		String sName = tagName;

		indent++;

		if (skipNonSVGTagDepth != 0 || !namespaceURI.equals("") && !namespaceURI.equals(SVGElement.SVG_NS)) {
			skipNonSVGTagDepth++;
			return;
		}

		sName = sName.toLowerCase();

		final Object obj = nodeClasses.get(sName);
		if (obj == null) { return; }

		try {
			final Class<?> cls = (Class<?>) obj;
			final SVGElement svgEle = (SVGElement) cls.newInstance();

			SVGElement parent = null;
			if (buildStack.size() != 0) {
				parent = buildStack.getLast();
			}
			svgEle.loaderStartElement(helper, attrs, parent);

			buildStack.addLast(svgEle);
		} catch (final Exception e) {
			e.printStackTrace();
			throw new SAXException(e);
		}

	}

	@Override
	public void endElement(final String namespaceURI, final String tagName, final String qName) throws SAXException {
		indent--;

		if (skipNonSVGTagDepth != 0) {
			skipNonSVGTagDepth--;
			return;
		}

		final String sName = tagName.toLowerCase();

		final Object obj = nodeClasses.get(sName);
		if (obj == null) { return; }

		try {
			final SVGElement svgEle = buildStack.removeLast();

			svgEle.loaderEndElement(helper);

			SVGElement parent = null;
			if (buildStack.size() != 0) {
				parent = buildStack.getLast();
				// else loadRoot = (SVGElement)svgEle;
			}

			if (parent != null) {
				parent.loaderAddChild(helper, svgEle);
			} else {
				diagram.setRoot((SVGRoot) svgEle);
			}

		} catch (final Exception e) {
			e.printStackTrace();
			throw new SAXException(e);
		}
	}

	@Override
	public void characters(final char buf[], final int offset, final int len) throws SAXException {
		if (skipNonSVGTagDepth != 0) { return; }

		if (buildStack.size() != 0) {
			final SVGElement parent = buildStack.getLast();
			final String s = new String(buf, offset, len);
			parent.loaderAddText(helper, s);
		}
	}

	@Override
	public void processingInstruction(final String target, final String data) throws SAXException {}

	public SVGDiagram getLoadedDiagram() {
		return diagram;
	}
}
