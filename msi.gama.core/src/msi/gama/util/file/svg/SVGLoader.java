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

package msi.gama.util.file.svg;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ummisco.gama.dev.utils.DEBUG;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class SVGLoader extends DefaultHandler {
	final HashMap<String, Class<? extends SVGElement>> nodeClasses = new HashMap<>();
	final LinkedList<SVGElement> buildStack = new LinkedList<>();

	final HashSet<String> ignoreClasses = new HashSet<>();

	final SVGLoaderHelper helper;

	/**
	 * The diagram that represents the base of this SVG document we're loading. Will be augmented to include node
	 * indexing info and other useful stuff.
	 */
	final SVGDiagram diagram;

	// SVGElement loadRoot;

	// Used to keep track of document elements that are not part of the SVG namespace
	int skipNonSVGTagDepth = 0;
	int indent = 0;

	final boolean verbose;

	/** Creates a new instance of SVGLoader */
	public SVGLoader(final URI xmlBase, final SVGUniverse universe) {
		this(xmlBase, universe, false);
	}

	public SVGLoader(final URI xmlBase, final SVGUniverse universe, final boolean verbose) {
		this.verbose = verbose;

		diagram = new SVGDiagram(xmlBase, universe);

		// Compile a list of important builder classes
		nodeClasses.put("a", A.class);
		// nodeClasses.put("animate", Animate.class);
		// nodeClasses.put("animatecolor", AnimateColor.class);
		// nodeClasses.put("animatemotion", AnimateMotion.class);
		// nodeClasses.put("animatetransform", AnimateTransform.class);
		nodeClasses.put("circle", Circle.class);
		nodeClasses.put("clippath", ClipPath.class);
		nodeClasses.put("desc", Desc.class);
		nodeClasses.put("ellipse", Ellipse.class);
		nodeClasses.put("filter", Filter.class);
		nodeClasses.put("g", Group.class);
		nodeClasses.put("hkern", Hkern.class);
		nodeClasses.put("line", Line.class);
		nodeClasses.put("metadata", Metadata.class);
		nodeClasses.put("path", Path.class);
		nodeClasses.put("polygon", Polygon.class);
		nodeClasses.put("polyline", Polyline.class);
		nodeClasses.put("rect", Rect.class);

		nodeClasses.put("shape", ShapeElement.class);
		nodeClasses.put("stop", Stop.class);
		nodeClasses.put("svg", SVGRoot.class);
		nodeClasses.put("symbol", Symbol.class);
		nodeClasses.put("use", Use.class);

		ignoreClasses.add("midpointstop");
		ignoreClasses.add("lineargradient");
		ignoreClasses.add("radialgradient");
		ignoreClasses.add("pattern");
		ignoreClasses.add("image");
		ignoreClasses.add("text");
		ignoreClasses.add("tspan");
		ignoreClasses.add("glyph");
		ignoreClasses.add("missing-glyph");
		ignoreClasses.add("font-face");
		ignoreClasses.add("font");
		ignoreClasses.add("title");
		ignoreClasses.add("style");
		ignoreClasses.add("defs");
		ignoreClasses.add("animate");
		ignoreClasses.add("animatecolor");
		ignoreClasses.add("animatemotion");
		ignoreClasses.add("animatetransform");
		ignoreClasses.add("set");

		// attribClasses.put("clip-path", StyleUrl.class);
		// attribClasses.put("color", StyleColor.class);

		helper = new SVGLoaderHelper(xmlBase, universe, diagram);
	}

	private String printIndent(final int indent, final String indentStrn) {
		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < indent; i++) {
			sb.append(indentStrn);
		}
		return sb.toString();
	}

	@Override
	public void startDocument() throws SAXException {
		// DEBUG.ERR("Start doc");

		// buildStack.clear();
	}

	@Override
	public void endDocument() throws SAXException {
		// DEBUG.ERR("End doc");
	}

	@Override
	public void startElement(final String namespaceURI, final String tagName, final String qName,
			final Attributes attrs) throws SAXException {
		String sName = tagName;
		if (verbose) {
			DEBUG.ERR(printIndent(indent, " ") + "Starting parse of tag " + sName + ": " + namespaceURI);
		}
		indent++;

		if (skipNonSVGTagDepth != 0 || !namespaceURI.equals("") && !namespaceURI.equals(SVGElement.SVG_NS)) {
			skipNonSVGTagDepth++;
			return;
		}

		sName = sName.toLowerCase();

		// javax.swing.JOptionPane.showMessageDialog(null, sName);

		final Object obj = nodeClasses.get(sName);
		if (obj == null) {
			if (!ignoreClasses.contains(sName)) {
				DEBUG.ERR("SVGLoader: Could not identify tag '" + sName + "'");
			}
			return;
		}

		// Debug info tag depth
		// for (int i = 0; i < buildStack.size(); i++) System.err.print(" ");
		// DEBUG.ERR("+" + sName);

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
		if (verbose) {
			DEBUG.ERR(printIndent(indent, " ") + "Ending parse of tag " + tagName + ": " + namespaceURI);
		}

		if (skipNonSVGTagDepth != 0) {
			skipNonSVGTagDepth--;
			return;
		}

		final String sName = tagName.toLowerCase();

		final Object obj = nodeClasses.get(sName);
		if (obj == null) { return; }

		// Debug info tag depth
		// for (int i = 0; i < buildStack.size(); i++) System.err.print(" ");
		// DEBUG.ERR("-" + sName);

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
	public void processingInstruction(final String target, final String data) throws SAXException {
		// Check for external style sheet
	}

	// public SVGElement getLoadRoot() { return loadRoot; }
	public SVGDiagram getLoadedDiagram() {
		return diagram;
	}
}
