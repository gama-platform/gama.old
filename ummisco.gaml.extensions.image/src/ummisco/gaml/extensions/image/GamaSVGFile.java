/*******************************************************************************************************
 *
 * GamaSVGFile.java, in ummisco.gaml.extensions.image, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gaml.extensions.image;

import static java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_COLOR_RENDERING;
import static java.awt.RenderingHints.KEY_DITHERING;
import static java.awt.RenderingHints.KEY_FRACTIONALMETRICS;
import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.KEY_RENDERING;
import static java.awt.RenderingHints.KEY_STROKE_CONTROL;
import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_COLOR_RENDER_QUALITY;
import static java.awt.RenderingHints.VALUE_DITHER_DISABLE;
import static java.awt.RenderingHints.VALUE_FRACTIONALMETRICS_ON;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC;
import static java.awt.RenderingHints.VALUE_RENDER_QUALITY;
import static java.awt.RenderingHints.VALUE_STROKE_PURE;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
import static org.apache.batik.util.XMLResourceDescriptor.getXMLParserClassName;

import java.awt.Dimension;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.imageio.ImageIO;

import org.apache.batik.anim.dom.AbstractSVGAnimatedLength;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.anim.dom.SVGGraphicsElement;
import org.apache.batik.anim.dom.SVGOMAnimatedTransformList.BaseSVGTransformList;
import org.apache.batik.anim.dom.SVGOMCircleElement;
import org.apache.batik.anim.dom.SVGOMDocument;
import org.apache.batik.anim.dom.SVGOMEllipseElement;
import org.apache.batik.anim.dom.SVGOMLineElement;
import org.apache.batik.anim.dom.SVGOMRectElement;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.gvt.renderer.StaticRenderer;
import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.parser.AWTPolygonProducer;
import org.apache.batik.parser.AWTPolylineProducer;
import org.apache.batik.parser.ParseException;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.locationtech.jts.awt.ShapeReader;
import org.locationtech.jts.geom.Geometry;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.GamlAnnotations.no_test;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.file.GamaGeometryFile;
import msi.gama.util.file.IGamaFile;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Class GamaSVGFile. Only loads vector shapes right now (and none of the associated elements: textures, colors, fonts,
 * etc.)
 *
 * @author drogoul
 * @since 30 déc. 2013
 *
 */
@file (
		name = "svg",
		extensions = "svg",
		buffer_type = IType.LIST,
		buffer_content = IType.GEOMETRY,
		buffer_index = IType.INT,
		concept = { IConcept.SVG },
		doc = @doc ("Represents 2D geometries described in a SVG file. The internal representation is a list of geometries"))
public class GamaSVGFile extends GamaGeometryFile {

	/** The Constant SVG_FACTORY. */
	static final SAXSVGDocumentFactory SVG_FACTORY = new SAXSVGDocumentFactory(getXMLParserClassName());

	/**
	 * The Class CustomTranscoder.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 16 juil. 2023
	 */
	public static final class CustomTranscoder extends PNGTranscoder {
		@Override
		protected ImageRenderer createRenderer() {
			ImageRenderer renderer = new StaticRenderer();
			RenderingHints renderHints = renderer.getRenderingHints();
			renderHints.add(new RenderingHints(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_OFF));
			renderHints.add(new RenderingHints(KEY_RENDERING, VALUE_RENDER_QUALITY));
			renderHints.add(new RenderingHints(KEY_DITHERING, VALUE_DITHER_DISABLE));
			renderHints.add(new RenderingHints(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC));
			renderHints.add(new RenderingHints(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY));
			renderHints.add(new RenderingHints(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON));
			renderHints.add(new RenderingHints(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_QUALITY));
			renderHints.add(new RenderingHints(KEY_STROKE_CONTROL, VALUE_STROKE_PURE));
			renderHints.add(new RenderingHints(KEY_FRACTIONALMETRICS, VALUE_FRACTIONALMETRICS_ON));
			renderer.setRenderingHints(renderHints);
			return renderer;
		}
	}

	/** The Constant PNG_TRANSCODER. */
	static final PNGTranscoder PNG_TRANSCODER = new CustomTranscoder();

	static {
		DEBUG.OFF();
	}

	/**
	 * Instantiates a new gama SVG file.
	 *
	 * @param scope
	 *            the scope
	 * @param pathName
	 *            the path name
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@doc (
			value = "This file constructor allows to read a svg file",
			examples = { @example (
					value = "file f <-svg_file(\"file.svg\");",
					isExecutable = false) })
	public GamaSVGFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
	}

	@Override
	protected IShape buildGeometry(final IScope scope) {
		return GamaGeometryType.geometriesToGeometry(scope, getBuffer());
	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		// TODO are there attributes ?
		return GamaListFactory.create(Types.STRING);
	}

	/** The document. */
	private SVGOMDocument document;

	/**
	 * Gets the root.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @return the root
	 * @date 21 juil. 2023
	 */
	private SVGOMDocument getDocument(final IScope scope) {
		if (document == null) {
			File f = getFile(scope);
			try {
				FileInputStream is = new FileInputStream(f);
				document = (SVGOMDocument) SVG_FACTORY.createSVGDocument(f.toURI().toString(), is);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return document;
	}

	/**
	 * Parses the group.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param el
	 *            the el
	 * @param shapes
	 *            the shapes
	 * @param transform
	 *            the transform
	 * @throws IOException
	 * @throws ParseException
	 * @date 16 juil. 2023
	 */
	private void parseGroup(final Element g, final AffineTransform transform) throws ParseException, IOException {
		AffineTransform current = computeTransform(g, transform);
		NodeList nl = g.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element el) {
				String tag = el.getTagName();
				if ("g".equals(tag)) { parseGroup(el, current); }
				if ("path".equals(tag)) { parsePath(el, current); }
				if ("rect".equals(tag)) { parseRect(el, current); }
				if ("circle".equals(tag)) { parseCircle(el, current); }
				if ("ellipse".equals(tag)) { parseEllipse(el, current); }
				if ("line".equals(tag)) { parseLine(el, current); }
				if ("polyline".equals(tag)) { parsePolyline(el, current); }
				if ("polygon".equals(tag)) { parsePolygon(el, current); }
			}
		}
	}

	/**
	 * Parses the rect.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param el
	 *            the el
	 * @param shapes
	 *            the shapes
	 * @param transform
	 *            the transform
	 * @date 18 juil. 2023
	 */
	private void parseRect(final Element el, final AffineTransform transform) {
		SVGOMRectElement re = (SVGOMRectElement) el;
		AbstractSVGAnimatedLength _x = (AbstractSVGAnimatedLength) re.getX();
		float x = _x.getCheckedValue();
		AbstractSVGAnimatedLength _y = (AbstractSVGAnimatedLength) re.getY();
		float y = _y.getCheckedValue();
		AbstractSVGAnimatedLength _width = (AbstractSVGAnimatedLength) re.getWidth();
		float w = _width.getCheckedValue();
		AbstractSVGAnimatedLength _height = (AbstractSVGAnimatedLength) re.getHeight();
		float h = _height.getCheckedValue();
		AbstractSVGAnimatedLength _rx = (AbstractSVGAnimatedLength) re.getRx();
		float rx = _rx.getCheckedValue();
		if (rx > w / 2) { rx = w / 2; }
		AbstractSVGAnimatedLength _ry = (AbstractSVGAnimatedLength) re.getRy();
		float ry = _ry.getCheckedValue();
		if (ry > h / 2) { ry = h / 2; }
		AffineTransform current = computeTransform(el, transform);
		if (rx == 0 || ry == 0) {
			addShape(new Rectangle2D.Float(x, y, w, h), current);
		} else {
			addShape(new RoundRectangle2D.Float(x, y, w, h, rx * 2, ry * 2), current);
		}
	}

	/**
	 * Parses the circle.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param el
	 *            the el
	 * @param shapes
	 *            the shapes
	 * @param current
	 *            the current
	 * @date 18 juil. 2023
	 */
	private void parseCircle(final Element el, final AffineTransform transform) {
		SVGOMCircleElement ce = (SVGOMCircleElement) el;
		AbstractSVGAnimatedLength _cx = (AbstractSVGAnimatedLength) ce.getCx();
		float cx = _cx.getCheckedValue();
		AbstractSVGAnimatedLength _cy = (AbstractSVGAnimatedLength) ce.getCy();
		float cy = _cy.getCheckedValue();
		AbstractSVGAnimatedLength _r = (AbstractSVGAnimatedLength) ce.getR();
		float r = _r.getCheckedValue();
		float x = cx - r;
		float y = cy - r;
		float w = r * 2;
		addShape(new Ellipse2D.Float(x, y, w, w), computeTransform(el, transform));
	}

	/**
	 * Parses the line.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param el
	 *            the el
	 * @param shapes
	 *            the shapes
	 * @param current
	 *            the current
	 * @date 18 juil. 2023
	 */
	private void parseLine(final Element el, final AffineTransform transform) {
		SVGOMLineElement le = (SVGOMLineElement) el;
		AbstractSVGAnimatedLength _x1 = (AbstractSVGAnimatedLength) le.getX1();
		float x1 = _x1.getCheckedValue();
		AbstractSVGAnimatedLength _y1 = (AbstractSVGAnimatedLength) le.getY1();
		float y1 = _y1.getCheckedValue();
		AbstractSVGAnimatedLength _x2 = (AbstractSVGAnimatedLength) le.getX2();
		float x2 = _x2.getCheckedValue();
		AbstractSVGAnimatedLength _y2 = (AbstractSVGAnimatedLength) le.getY2();
		float y2 = _y2.getCheckedValue();
		addShape(new Line2D.Float(x1, y1, x2, y2), computeTransform(el, transform));
	}

	/**
	 * Parses the ellipse.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param el
	 *            the el
	 * @param shapes
	 *            the shapes
	 * @param current
	 *            the current
	 * @date 18 juil. 2023
	 */
	private void parseEllipse(final Element el, final AffineTransform transform) {
		SVGOMEllipseElement ee = (SVGOMEllipseElement) el;
		AbstractSVGAnimatedLength _cx = (AbstractSVGAnimatedLength) ee.getCx();
		float cx = _cx.getCheckedValue();
		AbstractSVGAnimatedLength _cy = (AbstractSVGAnimatedLength) ee.getCy();
		float cy = _cy.getCheckedValue();
		AbstractSVGAnimatedLength _rx = (AbstractSVGAnimatedLength) ee.getRx();
		float rx = _rx.getCheckedValue();
		AbstractSVGAnimatedLength _ry = (AbstractSVGAnimatedLength) ee.getRy();
		float ry = _ry.getCheckedValue();
		addShape(new Ellipse2D.Float(cx - rx, cy - ry, rx * 2, ry * 2), computeTransform(el, transform));
	}

	/**
	 * Parses the polyline.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param el
	 *            the el
	 * @param shapes
	 *            the shapes
	 * @param transform
	 *            the transform
	 * @throws IOException
	 * @throws ParseException
	 * @date 18 juil. 2023
	 */
	private void parsePolyline(final Element el, final AffineTransform transform) throws ParseException, IOException {
		addShape(AWTPolylineProducer.createShape(new StringReader(el.getAttribute("points")), getWindindRule(el)),
				computeTransform(el, transform));
	}

	/**
	 * Parses the polygon.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param el
	 *            the el
	 * @param shapes
	 *            the shapes
	 * @param transform
	 *            the transform
	 * @throws IOException
	 * @throws ParseException
	 * @date 18 juil. 2023
	 */
	private void parsePolygon(final Element el, final AffineTransform transform) throws ParseException, IOException {
		addShape(AWTPolygonProducer.createShape(new StringReader(el.getAttribute("points")), getWindindRule(el)),
				computeTransform(el, transform));
	}

	/**
	 * Parses the path.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param el
	 *            the el
	 * @param shapes
	 *            the shapes
	 * @param transform
	 *            the transform
	 * @throws IOException
	 * @throws ParseException
	 * @date 16 juil. 2023
	 */
	private void parsePath(final Element el, final AffineTransform transform) throws ParseException, IOException {
		addShape(AWTPathProducer.createShape(new StringReader(el.getAttribute("d")), getWindindRule(el)),
				computeTransform(el, transform));
	}

	/**
	 * Gets the windind rule.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param el
	 *            the el
	 * @return the windind rule
	 * @date 18 juil. 2023
	 */
	private int getWindindRule(final Element el) {
		Value v = CSSUtilities.getComputedStyle(el, SVGCSSEngine.FILL_RULE_INDEX);
		return v != null && v.getStringValue().charAt(0) == 'n' ? Path2D.WIND_NON_ZERO : Path2D.WIND_EVEN_ODD;
	}

	/**
	 * Apply transform.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param g
	 *            the g
	 * @param transform
	 *            the transform
	 * @date 16 juil. 2023
	 */
	private AffineTransform computeTransform(final Element el, final AffineTransform transform) {
		if (el instanceof SVGGraphicsElement g) {
			AffineTransform result = new AffineTransform(transform);
			result.concatenate(((BaseSVGTransformList) g.getTransform().getBaseVal()).getAffineTransform());
			return result;
		}
		return transform;
	}

	/**
	 * Adds the shape.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param shape
	 *            the shape
	 * @param current
	 *            the current
	 * @param shapes
	 *            the shapes
	 * @date 18 juil. 2023
	 */
	private void addShape(final Shape shape, final AffineTransform current) {
		PathIterator it = shape.getPathIterator(current, 1.0);
		Geometry g = ShapeReader.read(it, GeometryUtils.GEOMETRY_FACTORY);
		g = GeometryUtils.cleanGeometry(g);
		GamaShape gs = new GamaShape(g);
		DEBUG.OUT("Adding shape with type " + gs.getInnerGeometry().getGeometryType() + " envelope "
				+ gs.getEnvelope().getWidth() + " " + gs.getEnvelope().getHeight() + " at " + gs.getEnvelope().getMinX()
				+ " " + gs.getEnvelope().getMinY());
		getBuffer().add(gs);
	}

	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) return;
		setBuffer(GamaListFactory.create());
		try {
			parseGroup(getDocument(scope).getDocumentElement(), new AffineTransform());
			Envelope3D env = Envelope3D.of(getBuffer());
			DEBUG.OUT("Total resulting envelope " + env.getWidth() + " " + env.getHeight() + " at " + env.getMinX()
					+ " " + env.getMinY());
			for (IShape s : getBuffer()) { s.setLocation(s.getLocation().minus(env.getMinX(), env.getMinY(), 0)); }
		} catch (final Exception e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	/**
	 * Gets the image.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param b
	 *            the b
	 * @return the image
	 * @date 16 juil. 2023
	 */
	public BufferedImage getImage(final IScope scope, final boolean useCache) {
		Dimension dim = correctSize(scope);
		return getImage(scope, useCache, dim.width, dim.height);
	}

	/**
	 * Gets the image.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param useCache
	 *            the use cache
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return the image
	 * @date 21 juil. 2023
	 */
	public BufferedImage getImage(final IScope scope, final boolean useCache, final int x, final int y) {

		if (useCache) {
			String key = getPath(scope) + x + "x" + y;
			BufferedImage im = ImageCache.getInstance().getImage(key);
			if (im == null) {
				im = getImage(scope, false, x, y);
				ImageCache.getInstance().forceCacheImage(im, key);
			}
			return im;
		}
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
			SVGOMDocument svg = getDocument(scope);
			TranscoderInput input = new TranscoderInput(svg);
			input.setURI(svg.getDocumentURI());
			PNG_TRANSCODER.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, (float) y);
			PNG_TRANSCODER.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, (float) x);
			// Create the transcoder output.
			TranscoderOutput output = new TranscoderOutput(outputStream);
			DEBUG.OUT("Retrieving image at " + x + " " + y);
			// Save the image.
			PNG_TRANSCODER.transcode(input, output);
			outputStream.flush();
			// Convert the byte stream into an image.
			byte[] imgData = outputStream.toByteArray();
			return ImageIO.read(new ByteArrayInputStream(imgData));
		} catch (IOException | TranscoderException e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

	/**
	 * Correct size of.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param name
	 *            the name
	 * @param svg
	 *            the svg
	 * @return the dimension
	 * @date 16 juil. 2023
	 */
	private Dimension correctSize(final IScope scope) {
		Element node = getDocument(scope).getDocumentElement();
		String nativeWidthStr = node.getAttribute("width");
		String nativeHeightStr = node.getAttribute("height");
		int nativeWidth = -1;
		int nativeHeight = -1;
		nativeWidthStr = stripOffPx(nativeWidthStr);
		nativeHeightStr = stripOffPx(nativeHeightStr);
		float floatWidth = nativeWidthStr.isBlank() ? 0f : Float.parseFloat(nativeWidthStr);
		float floatHeight = nativeHeightStr.isBlank() ? 0f : Float.parseFloat(nativeHeightStr);
		nativeWidth = Math.round(floatWidth);
		nativeHeight = Math.round(floatHeight);
		if (nativeWidth == 0 && nativeHeight == 0) {
			Envelope3D env = Envelope3D.of(getBuffer());
			nativeWidth = (int) env.getWidth();
			nativeHeight = (int) env.getHeight();
		}
		node.setAttribute("width", String.valueOf(nativeWidth));
		node.setAttribute("height", String.valueOf(nativeHeight));
		return new Dimension(nativeWidth, nativeHeight);
	}

	/**
	 * Strip off px.
	 *
	 * @param dimensionString
	 *            the dimension string
	 * @return the string
	 */
	private static String stripOffPx(final String dimensionString) {
		if (dimensionString.endsWith("px")) return dimensionString.substring(0, dimensionString.length() - 2);
		return dimensionString;
	}

	/**
	 * Image.
	 *
	 * @param w
	 *            the w
	 * @param h
	 *            the h
	 * @param type
	 *            the type
	 * @return the gama image
	 */
	@operator (
			can_be_const = true,
			value = "image")
	@doc ("Builds a new image from the specified file, p	assing the width and height in parameter ")
	@no_test
	public static GamaImage image(final IScope scope, final IGamaFile file, final int w, final int h) {
		if (file instanceof GamaSVGFile svg)
			return GamaImage.from(svg.getImage(scope, true, w, h), true, file.getPath(scope) + w + "x" + h);
		if (file instanceof GamaImageFile f) return ImageOperators.with_size(scope,
				GamaImage.from(f.getImage(scope, true), true, f.getOriginalPath()), w, h);
		return null;
	}

}
