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

package msi.gama.ext.svgsalamander;

import static msi.gama.ext.svgsalamander.SVGElement.SVG_NS;

import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Mark McKay
 * @author <a href="mailto:mark@kitfox.com">Mark McKay</a>
 */
public class SVGLoader extends DefaultHandler {

	final LinkedList<IShapeElement> buildStack = new LinkedList<>();
	SVGRoot root;

	// Used to keep track of document elements that are not part of the SVG namespace
	int skipNonSVGTagDepth = 0;
	int indent = 0;

	@Override
	public void startElement(final String namespaceURI, final String tagName, final String qName,
			final Attributes attrs) throws SAXException {
		indent++;
		if (skipNonSVGTagDepth != 0 || !namespaceURI.equals("") && !namespaceURI.equals(SVG_NS)) {
			skipNonSVGTagDepth++;
			return;
		}
		IShapeElement svgEle = null;
		switch (tagName.toLowerCase()) {
			case "circle":
				svgEle = new ShapeElement<Ellipse2D.Float>() {

					@Override
					public void build() throws SVGException {
						float cx = 0f;
						float cy = 0f;
						float r = 0f;
						shape = new Ellipse2D.Float();
						final StyleAttribute sty = new StyleAttribute();
						if (getPres(sty.setName("cx"))) { cx = sty.getFloatValueWithUnits(); }
						if (getPres(sty.setName("cy"))) { cy = sty.getFloatValueWithUnits(); }
						if (getPres(sty.setName("r"))) { r = sty.getFloatValueWithUnits(); }
						shape.setFrame(cx - r, cy - r, r * 2f, r * 2f);
					}

				};
				break;
			case "line":
				svgEle = new ShapeElement<Line2D.Float>() {
					@Override
					public void build() throws SVGException {
						float x1 = 0f;
						float y1 = 0f;
						float x2 = 0f;
						float y2 = 0f;
						final StyleAttribute sty = new StyleAttribute();
						if (getPres(sty.setName("x1"))) { x1 = sty.getFloatValueWithUnits(); }
						if (getPres(sty.setName("y1"))) { y1 = sty.getFloatValueWithUnits(); }
						if (getPres(sty.setName("x2"))) { x2 = sty.getFloatValueWithUnits(); }
						if (getPres(sty.setName("y2"))) { y2 = sty.getFloatValueWithUnits(); }
						shape = new Line2D.Float(x1, y1, x2, y2);
					}
				};
				break;
			case "ellipse":
				svgEle = new ShapeElement<Ellipse2D.Float>() {

					@Override
					public void build() throws SVGException {
						float cx = 0f, cy = 0f, rx = 0f, ry = 0f;
						shape = new Ellipse2D.Float();
						final StyleAttribute sty = new StyleAttribute();
						if (getPres(sty.setName("cx"))) { cx = sty.getFloatValueWithUnits(); }
						if (getPres(sty.setName("cy"))) { cy = sty.getFloatValueWithUnits(); }
						if (getPres(sty.setName("rx"))) { rx = sty.getFloatValueWithUnits(); }
						if (getPres(sty.setName("ry"))) { ry = sty.getFloatValueWithUnits(); }
						shape.setFrame(cx - rx, cy - ry, rx * 2f, ry * 2f);
					}
				};
				break;
			case "polygon":
				svgEle = new ShapeElement<GeneralPath>() {

					@Override
					public void build() throws SVGException {
						int fillRule = GeneralPath.WIND_NON_ZERO;
						String pointsStrn = "";
						final StyleAttribute sty = new StyleAttribute();
						if (getPres(sty.setName("points"))) { pointsStrn = sty.getStringValue(); }
						final String fillRuleStrn =
								getStyle(sty.setName("fill-rule")) ? sty.getStringValue() : "nonzero";
						fillRule =
								fillRuleStrn.equals("evenodd") ? GeneralPath.WIND_EVEN_ODD : GeneralPath.WIND_NON_ZERO;
						final float[] points = XMLParseUtil.parseFloatList(pointsStrn);
						shape = new GeneralPath(fillRule, points.length / 2);
						shape.moveTo(points[0], points[1]);
						for (int i = 2; i < points.length; i += 2) {
							shape.lineTo(points[i], points[i + 1]);
						}
						shape.closePath();
					}
				};
				break;
			case "polyline":
				svgEle = new ShapeElement<GeneralPath>() {

					@Override
					public void build() throws SVGException {
						int fillRule = GeneralPath.WIND_NON_ZERO;
						String pointsStrn = "";
						final StyleAttribute sty = new StyleAttribute();
						if (getPres(sty.setName("points"))) { pointsStrn = sty.getStringValue(); }
						final String fillRuleStrn =
								getStyle(sty.setName("fill-rule")) ? sty.getStringValue() : "nonzero";
						fillRule =
								fillRuleStrn.equals("evenodd") ? GeneralPath.WIND_EVEN_ODD : GeneralPath.WIND_NON_ZERO;
						final float[] points = XMLParseUtil.parseFloatList(pointsStrn);
						shape = new GeneralPath(fillRule, points.length / 2);
						shape.moveTo(points[0], points[1]);
						for (int i = 2; i < points.length; i += 2) {
							shape.lineTo(points[i], points[i + 1]);
						}
					}
				};
				break;
			case "rect":
				svgEle = new ShapeElement<RectangularShape>() {

					@Override
					public void build() throws SVGException {
						float x = 0f;
						float y = 0f;
						float width = 0f;
						float height = 0f;
						float rx = 0f;
						float ry = 0f;
						final StyleAttribute sty = new StyleAttribute();
						if (getPres(sty.setName("x"))) { x = sty.getFloatValueWithUnits(); }
						if (getPres(sty.setName("y"))) { y = sty.getFloatValueWithUnits(); }
						if (getPres(sty.setName("width"))) { width = sty.getFloatValueWithUnits(); }
						if (getPres(sty.setName("height"))) { height = sty.getFloatValueWithUnits(); }
						boolean rxSet = false;
						if (getPres(sty.setName("rx"))) {
							rx = sty.getFloatValueWithUnits();
							rxSet = true;
						}
						boolean rySet = false;
						if (getPres(sty.setName("ry"))) {
							ry = sty.getFloatValueWithUnits();
							rySet = true;
						}
						if (!rxSet) { rx = ry; }
						if (!rySet) { ry = rx; }
						if (rx == 0f && ry == 0f) {
							shape = new Rectangle2D.Float(x, y, width, height);
						} else {
							shape = new RoundRectangle2D.Float(x, y, width, height, rx * 2, ry * 2);
						}
					}
				};
				break;
			case "stop":
				svgEle = new SVGElement() {

					@Override
					public void loaderBuild() throws SVGException {
						float offset = 0f;
						final StyleAttribute sty = new StyleAttribute();
						if (getPres(sty.setName("offset"))) {
							offset = sty.getFloatValue();
							final String units = sty.getUnits();
							if (units != null && units.equals("%")) { offset /= 100f; }
							if (offset > 1) { offset = 1; }
							if (offset < 0) { offset = 0; }
						}
					}
				};
				break;
			case "svg":
				svgEle = new SVGRoot();
				break;
			case "path":
				svgEle = new Path();
				break;
			case "shape":
				svgEle = new ShapeElement() {

					@Override
					protected void build() throws SVGException {}

				};
				break;
			case "g":
				svgEle = new Group();
				break;

		}
		if (svgEle == null) return;
		try {
			IShapeElement parent = null;
			if (buildStack.size() != 0) { parent = buildStack.getLast(); }
			svgEle.loaderStartElement(attrs, parent);
			buildStack.addLast(svgEle);
		} catch (final Exception e) {
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
		try {
			final IShapeElement svgEle = buildStack.removeLast();
			svgEle.loaderBuild();
			IShapeElement parent = buildStack.size() != 0 ? buildStack.getLast() : null;
			if (parent != null) {
				parent.loaderAddChild(svgEle);
			} else {
				root = (SVGRoot) svgEle;
			}
		} catch (final Exception e) {
			throw new SAXException(e);
		}
	}

	public SVGRoot getRoot() {
		return root;
	}
}
