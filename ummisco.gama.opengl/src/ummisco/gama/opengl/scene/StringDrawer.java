/*******************************************************************************************************
 *
 * ummisco.gama.opengl.scene.StringDrawer.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.scene;

import static java.awt.geom.PathIterator.WIND_EVEN_ODD;
import static msi.gama.common.geometry.GeometryUtils.GEOMETRY_FACTORY;

import java.awt.Color;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.time.Duration;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUtessellator;
import com.jogamp.opengl.util.gl2.GLUT;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gaml.statements.draw.TextDrawingAttributes;
import msi.gaml.types.GamaFontType;
import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.Tesselator;
import ummisco.gama.ui.utils.PlatformHelper;

/**
 *
 * The class StringDrawer.
 *
 * @author drogoul
 * @since 4 mai 2013
 *
 */

public class StringDrawer extends ObjectDrawer<StringObject> {
	protected final float textSizeMultiplier = 2f;

	LoadingCache<Font, TextRenderer> textRendererCache = CacheBuilder.newBuilder()
			.expireAfterWrite(Duration.ofMinutes(2)).build(new CacheLoader<Font, TextRenderer>() {

				@Override
				public TextRenderer load(final Font font) throws Exception {
					return new TextRenderer(gl, font);
				}
			});

	public StringDrawer(final OpenGL gl) {
		super(gl);
	}

	@Override
	protected void _draw(final StringObject s) {
		TextDrawingAttributes attributes = s.getAttributes();
		try {
			gl.pushMatrix();
			final AxisAngle rotation = attributes.getRotation();
			final GamaPoint p = attributes.getLocation();
			if (rotation != null) {
				gl.translateBy(p.x, p.y, p.z);
				final GamaPoint axis = rotation.getAxis();
				// AD Change to a negative rotation to fix Issue #1514
				gl.rotateBy(-rotation.getAngle(), axis.x, axis.y, axis.z);
				// Voids the location so as to make only one translation
				p.setLocation(0, 0, 0);
			}
			if (attributes.isPerspective()) {
				Font font = attributes.getFont();
				final int fontSize = PlatformHelper.scaleToHiDPI(Math.round(font.getSize()));
				if (fontSize != font.getSize()) { font = font.deriveFont((float) fontSize); }
				TextRenderer t;
				try {
					t = textRendererCache.get(font);
				} catch (ExecutionException e) {
					return;
				}
				final float ratio = (float) gl.getRatios().y;
				final float scale = 1f / ratio;
				final Rectangle2D bounds = t.getBounds(s.getObject());
				GamaPoint anchor = attributes.getAnchor();
				final double curX = p.x - bounds.getWidth() * scale * anchor.x;
				final double curY = p.y + bounds.getY() * scale * anchor.y;
				final double curZ = p.z /* + gl.getCurrentZTranslation() */;
				t.draw(s.getObject(), curX, curY, curZ, scale, attributes.getPrecision(), attributes.getDepth(),
						attributes.isEmpty(), attributes.getBorder());
			} else {
				int fontToUse = GLUT.BITMAP_HELVETICA_18;
				final Font f = s.getAttributes().getFont();
				if (f != null) {
					if (f.getSize() < 10) {
						fontToUse = GLUT.BITMAP_HELVETICA_10;
					} else if (f.getSize() < 16) { fontToUse = GLUT.BITMAP_HELVETICA_12; }
				}
				gl.rasterText(s.getObject(), fontToUse, p.x, p.y, p.z);
			}
		} finally {
			gl.popMatrix();
		}
	}

	@Override
	public void dispose() {
		textRendererCache.invalidateAll();
	}

	/**
	 * This class renders a Font into OpenGL
	 *
	 * @author Davide Raccagni - Erik Tollerud 2004
	 * @adaptation Alexis Drogoul 2021
	 */
	private class TextRenderer implements Tesselator {
		final ICoordinates _quadvertices = GEOMETRY_FACTORY.COORDINATES_FACTORY.create(5, 3);
		Color previous = null;
		private final Font font;
		private final OpenGL gl;
		private double width, height;
		private final GLU glu;
		private final GLUtessellator tobj;
		final FontRenderContext context = new FontRenderContext(new AffineTransform(), true, true);
		final AffineTransform negateY = AffineTransform.getScaleInstance(1.0, -1.0);

		double[] lastCoord = new double[3];
		double[] firstCoord = new double[3];
		double[] coords = new double[6];

		/**
		 * Intstantiates a new TextRenderer3D initially rendering in the specified font.
		 *
		 * @param font
		 *            - the initial font for this TextRenderer3D
		 */
		public TextRenderer(final OpenGL gl, final Font font) {
			this.gl = gl;
			this.font = font == null ? GamaFontType.DEFAULT_DISPLAY_FONT.getValue() : font;
			glu = GLU.createGLU(gl.getGL());
			tobj = glu.gluNewTess();
			glu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, this);
			glu.gluTessCallback(tobj, GLU.GLU_TESS_END, this);
			glu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, this);
			glu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, this);
			glu.gluTessCallback(tobj, GLU.GLU_TESS_COMBINE, this);
		}

		public void draw(final String str, final double xOff, final double yOff, final double zOff,
				final double scaleFactor, final double precision, final double depth, final boolean wireframe,
				final Color border) {

			gl.getGL().glPushAttrib(GL2.GL_TRANSFORM_BIT);
			gl.push(GL2.GL_MODELVIEW);
			gl.translateBy(xOff, yOff, zOff);
			gl.scaleBy(scaleFactor, scaleFactor, scaleFactor);
			this.draw(str, precision, depth <= 0 ? 0f : depth, wireframe, border);
			gl.popMatrix();
			gl.getGL().glPopAttrib();
		}

		/**
		 * Renders a string into the specified GL object, starting at the (0,0,0) point in OpenGL coordinates.
		 *
		 * @param str
		 *            the string to render.
		 * @param glu
		 *            a GLU instance to use for the text rendering (provided to prevent continuous re-instantiation of a
		 *            GLU object)
		 * @param gl
		 *            the OpenGL context in which to render the text.
		 */
		public void draw(final String str, final double precision, final double depth, final boolean wireframe,
				final Color border) {
			GlyphVector gv = font.createGlyphVector(context, str);
			GeneralPath gp = (GeneralPath) gv.getOutline();
			Rectangle2D bounds = gp.getBounds2D();
			width = bounds.getWidth();
			height = bounds.getHeight();
			PathIterator pi = gp.getPathIterator(negateY, precision);
			// The normal is decided partly by the depth. If it is null, the "back face" will actually be the "front
			// face"
			gl.outputNormal(0, 0, depth == 0d ? 1.0 : -1.0);
			tesselateFace(pi, wireframe, 0.0, null);
			if (depth > 0d) {
				pi = gp.getPathIterator(negateY, precision);
				gl.outputNormal(0, 0, 1.0d);
				tesselateFace(pi, wireframe, depth, null);
				pi = gp.getPathIterator(negateY, precision);
				drawSides(pi, wireframe, depth, border);
			}
			if (!wireframe && border != null) {
				pi = gp.getPathIterator(negateY, precision);
				// We draw the border at the "altitude" of depth
				tesselateFace(pi, true, depth, border);
			}
		}

		/**
		 * Get the bounding box for the supplied string with the current font, etc.
		 *
		 * @param str
		 * @return
		 */
		public Rectangle2D getBounds(final String str) {
			GlyphVector gv = font.createGlyphVector(context, str);
			GeneralPath gp = (GeneralPath) gv.getOutline();
			return gp.getBounds2D();
		}

		// construct the sides of each glyph by walking around and extending each vertex
		// out to the depth of the extrusion
		private void drawSides(final PathIterator pi, final boolean wireframe, final double depth, final Color border) {
			while (!pi.isDone()) {
				switch (pi.currentSegment(coords)) {
					case PathIterator.SEG_MOVETO:
						gl.begin(GL2.GL_QUADS);
						lastCoord[0] = coords[0];
						lastCoord[1] = coords[1];
						firstCoord[0] = coords[0];
						firstCoord[1] = coords[1];
						break;
					case PathIterator.SEG_LINETO:
						_quadvertices.setTo(lastCoord[0], lastCoord[1], 0, lastCoord[0], lastCoord[1], depth, coords[0],
								coords[1], depth, coords[0], coords[1], 0, lastCoord[0], lastCoord[1], 0);
						gl.setNormal(_quadvertices, true);
						gl.drawSimpleShape(_quadvertices, 4, !wireframe, true, false, /* border */ null);
						// dont use border now for the sides
						lastCoord[0] = coords[0];
						lastCoord[1] = coords[1];
						break;
					case PathIterator.SEG_CLOSE:
						_quadvertices.setTo(lastCoord[0], lastCoord[1], 0, lastCoord[0], lastCoord[1], depth,
								firstCoord[0], firstCoord[1], depth, firstCoord[0], firstCoord[1], 0, lastCoord[0],
								lastCoord[1], 0);
						gl.setNormal(_quadvertices, true);
						gl.drawSimpleShape(_quadvertices, 4, !wireframe, true, false, /* border */null);
						// dont use border now for the sides
						break;
				}
				pi.next();
			}
		}

		// routine that tesselates the current set of glyphs
		private void tesselateFace(final PathIterator pi, final boolean wireframe, final double tessZ,
				final Color border) {

			if (border != null) {
				previous = gl.getCurrentColor();
				gl.setCurrentColor(border);
			}
			// glu.gluTessNormal(tobj, 0.0, 0.0, -1.0);
			glu.gluTessProperty(tobj, GLU.GLU_TESS_WINDING_RULE,
					pi.getWindingRule() == WIND_EVEN_ODD ? GLU.GLU_TESS_WINDING_ODD : GLU.GLU_TESS_WINDING_NONZERO);
			glu.gluTessProperty(tobj, GLU.GLU_TESS_BOUNDARY_ONLY, wireframe ? GL2.GL_TRUE : GL2.GL_FALSE);
			glu.gluTessBeginPolygon(tobj, (double[]) null);

			while (!pi.isDone()) {
				double[] coords = new double[3];
				coords[2] = tessZ;
				switch (pi.currentSegment(coords)) {
					case PathIterator.SEG_MOVETO:
						glu.gluTessBeginContour(tobj);//$FALL-THROUGH$
					case PathIterator.SEG_LINETO:
						glu.gluTessVertex(tobj, coords, 0, coords);
						break;
					case PathIterator.SEG_CLOSE:
						glu.gluTessEndContour(tobj);
						break;
				}
				pi.next();
			}
			glu.gluTessEndPolygon(tobj);
			if (previous != null) {
				gl.setCurrentColor(previous);
				previous = null;
			}
		}

		@Override
		public void drawVertex(final int i, final double x, final double y, final double z) {
			if (gl.isTextured()) { gl.outputTexCoord(x / width, y / height); }
			gl.outputVertex(x, y, z);
		}

		@Override
		public void endDrawing() {
			gl.end();
		}

		@Override
		public void beginDrawing(final int type) {
			gl.begin(type);
		}

	}

}