/*******************************************************************************************************
 *
 * LegacyTextDrawer.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.scene.text;

import static com.jogamp.common.nio.Buffers.newDirectDoubleBuffer;
import static com.jogamp.opengl.glu.GLU.GLU_TESS_WINDING_NONZERO;
import static com.jogamp.opengl.glu.GLU.GLU_TESS_WINDING_ODD;
import static com.jogamp.opengl.glu.GLU.GLU_TESS_WINDING_RULE;
import static java.awt.geom.PathIterator.WIND_EVEN_ODD;

import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.nio.DoubleBuffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUtessellator;
import com.jogamp.opengl.util.gl2.GLUT;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gaml.statements.draw.TextDrawingAttributes;
import ummisco.gama.opengl.ITesselator;
import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.scene.ObjectDrawer;
import ummisco.gama.ui.utils.DPIHelper;

/**
 *
 * The class StringDrawer.
 *
 * @author drogoul
 * @since 4 mai 2013
 *
 */

public class LegacyTextDrawer extends ObjectDrawer<StringObject> implements ITesselator {

	/** The temp. */
	// Utilities
	private final ICoordinates temp = ICoordinates.ofLength(4);

	/** The normal. */
	private final GamaPoint normal = new GamaPoint();

	/** The tobj. */
	private final GLUtessellator tobj = GLU.gluNewTess();

	/** The previous Y. */
	private double previousX = Double.MIN_VALUE, previousY = Double.MIN_VALUE;

	/** The current index. */
	private int currentIndex = -1;

	/** The Constant BUFFER_SIZE. */
	// Constants
	private static final int BUFFER_SIZE = 1000000;

	/** The Constant AT. */
	private final static AffineTransform AT = AffineTransform.getScaleInstance(1.0, -1.0);

	/** The Constant context. */
	private static final FontRenderContext context = new FontRenderContext(new AffineTransform(), true, true);

	/** The face vertex buffer. */
	// Buffers
	private final DoubleBuffer faceVertexBuffer = newDirectDoubleBuffer(BUFFER_SIZE);

	/** The face texture buffer. */
	private final DoubleBuffer faceTextureBuffer = newDirectDoubleBuffer(BUFFER_SIZE * 2 / 3);

	/** The indices. */
	private final int[] indices = new int[1000]; // Indices of the "move_to" or "close"

	/** The side normal buffer. */
	private final DoubleBuffer sideNormalBuffer = newDirectDoubleBuffer(BUFFER_SIZE);

	/** The side quads buffer. */
	private final DoubleBuffer sideQuadsBuffer = newDirectDoubleBuffer(BUFFER_SIZE); // Contains the sides

	/** The border. */
	// Properties
	private Color border;

	/** The depth. */
	private double width, height, depth;

	/**
	 * Instantiates a new legacy text drawer.
	 *
	 * @param gl
	 *            the gl
	 */
	public LegacyTextDrawer(final OpenGL gl) {
		super(gl);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, this);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_END, this);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, this);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, this);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_COMBINE, this);
		GLU.gluTessCallback(tobj, GLU.GLU_TESS_EDGE_FLAG, this);
	}

	@Override
	protected void _draw(final StringObject s) {
		TextDrawingAttributes attributes = s.getAttributes();
		if (!attributes.isPerspective()) {
			drawBitmap(s.getObject(), attributes);
		} else {
			Font font = attributes.getFont();
			final int fontSize = DPIHelper.autoScaleUp(gl.getRenderer().getCanvas().getMonitor(), font.getSize());
			if (fontSize != font.getSize()) { font = font.deriveFont((float) fontSize); }
			Shape shape = font.createGlyphVector(context, s.getObject()).getOutline();
			final Rectangle2D bounds = shape.getBounds2D();
			this.depth = attributes.getDepth();
			this.border = attributes.getBorder();
			this.width = bounds.getWidth();
			this.height = bounds.getHeight();
			faceVertexBuffer.clear();
			faceTextureBuffer.clear();
			sideNormalBuffer.clear();
			sideQuadsBuffer.clear();
			currentIndex = -1;
			process(shape.getPathIterator(AT, attributes.getPrecision()));
			drawText(attributes, bounds.getY());
		}

	}

	/**
	 * Draw bitmap.
	 *
	 * @param object
	 *            the object
	 * @param attributes
	 *            the attributes
	 */
	private void drawBitmap(final String object, final TextDrawingAttributes attributes) {
		int fontToUse = GLUT.BITMAP_HELVETICA_18;
		final Font f = attributes.getFont();
		if (f != null) {
			if (f.getSize() < 10) {
				fontToUse = GLUT.BITMAP_HELVETICA_10;
			} else if (f.getSize() < 16) { fontToUse = GLUT.BITMAP_HELVETICA_12; }
		}
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

		gl.rasterText(object, fontToUse, p.x, p.y, p.z);
		gl.popMatrix();
	}

	/**
	 * Process.
	 *
	 * @param pi
	 *            the pi
	 */
	void process(final PathIterator pi) {
		boolean wireframe = gl.isWireframe();
		if (!wireframe) {
			GLU.gluTessProperty(tobj, GLU_TESS_WINDING_RULE,
					pi.getWindingRule() == WIND_EVEN_ODD ? GLU_TESS_WINDING_ODD : GLU_TESS_WINDING_NONZERO);
			GLU.gluTessNormal(tobj, 0, 0, -1);
			GLU.gluTessBeginPolygon(tobj, (double[]) null);
		}
		double x0 = 0, y0 = 0;
		while (!pi.isDone()) {
			final var coords = new double[6];
			switch (pi.currentSegment(coords)) {
				case PathIterator.SEG_MOVETO:
					// We begin a new contour within the global polygon
					// If we are solid, we pass the information to the tesselation algorithm
					if (!wireframe) {
						GLU.gluTessBeginContour(tobj);
						GLU.gluTessVertex(tobj, coords, 0, coords);
					}
					x0 = coords[0];
					y0 = coords[1];
					beginNewContour();
					addContourVertex0(x0, y0);
					break;
				case PathIterator.SEG_LINETO:
					// If we are solid, we pass the coordinates to the tesselation algorithm
					if (!wireframe) { GLU.gluTessVertex(tobj, coords, 0, coords); }
					// We also pass the coordinates to the side buffer, which will decide whether to create depth
					// level coordinates and compute the normal vector associated with this vertex
					addContourVertex0(coords[0], coords[1]);
					break;
				case PathIterator.SEG_CLOSE:
					if (!wireframe) { GLU.gluTessEndContour(tobj); }
					// We close the contour by adding it explicitly to the sideBuffer in order to close the loop
					addContourVertex0(x0, y0);
					endContour();
			}
			pi.next();
		}
		if (!wireframe) { GLU.gluTessEndPolygon(tobj); }
		sideQuadsBuffer.flip();
		sideNormalBuffer.flip();
		faceVertexBuffer.flip();
		if (faceTextureBuffer != null) { faceTextureBuffer.flip(); }
	}

	/**
	 * Draw text.
	 *
	 * @param attributes
	 *            the attributes
	 * @param y
	 *            the y
	 */
	private void drawText(final TextDrawingAttributes attributes, final double y) {
		final AxisAngle rotation = attributes.getRotation();
		final GamaPoint p = attributes.getLocation();

		Color previous = null;
		gl.pushMatrix();
		try {
			GamaPoint anchor = attributes.getAnchor();
			if (rotation != null) {
				gl.translateBy(p.x, p.y, p.z);
				final GamaPoint axis = rotation.getAxis();
				// AD Change to a negative rotation to fix Issue #1514
				gl.rotateBy(-rotation.getAngle(), axis.x, axis.y, axis.z);
				// Voids the location so as to make only one translation
				p.setLocation(0, 0, 0);
			}
			final float scale = 1f / (float) gl.getRatios().y;
			gl.translateBy(p.x - width * scale * anchor.x, p.y + y * scale * anchor.y, p.z);
			gl.scaleBy(scale, scale, scale);
			if (!gl.isWireframe()) {
				// Solid case
				drawFace(depth == 0);
				if (depth > 0) {
					gl.translateBy(0, 0, depth);
					drawFace(true);
					gl.translateBy(0, 0, -depth);
					drawSide();
				}
				if (border != null) {
					previous = gl.getCurrentColor();
					gl.setCurrentColor(border);
					gl.translateBy(0, 0, depth + 5 * gl.getCurrentZIncrement());
					drawBorder();
					gl.translateBy(0, 0, -depth - 5 * gl.getCurrentZIncrement());
				}
			} else {
				// Wireframe case
				if (border != null) {
					previous = gl.getCurrentColor();
					gl.setCurrentColor(border);
				}
				if (depth == 0) {
					// We use the quads side buffer to render only the top (lines)
					drawBorder();
				} else {
					drawSide();
				}
			}
		} finally {
			if (previous != null) { gl.setCurrentColor(previous); }
			gl.popMatrix();
		}
	}

	/**
	 * End contour.
	 */
	private void endContour() {
		indices[++currentIndex] = sideQuadsBuffer.position();
	}

	/**
	 * Begin new contour.
	 */
	private void beginNewContour() {
		indices[++currentIndex] = sideQuadsBuffer.position();
	}

	/**
	 * Add a point at altitude zero: creates automatically a point at altitude "depth" if depth > 0 in the quads
	 *
	 * @param x
	 *            and y represent the new vertex to be added
	 *
	 */
	private void addContourVertex0(final double x, final double y) {
		sideQuadsBuffer.put(x).put(y).put(0);
		if (depth > 0) {
			// If depth > 0, then we will build side faces, and we need to calculate their normal
			if (previousX > Double.MIN_VALUE) {
				temp.setTo(previousX, previousY, 0, previousX, previousY, depth, x, y, 0, previousX, previousY, 0);
				temp.getNormal(true, 1, normal);
				// We add two normal vectors as the vertex buffer will be filled by 2 coordinates
				sideNormalBuffer.put(new double[] { normal.x, normal.y, normal.z, normal.x, normal.y, normal.z });
			}
			// And we store the upper face
			sideQuadsBuffer.put(x).put(y).put(depth);
		}
		previousX = x;
		previousY = y;
	}

	@Override
	public void dispose() {}

	/**
	 * Draw side.
	 */
	private void drawSide() {
		if (sideQuadsBuffer.limit() == 0) return;
		var i = -1;
		while (i < currentIndex) {
			var begin = indices[++i];
			var end = indices[++i];
			gl.beginDrawing(GL2.GL_QUAD_STRIP);
			for (var index = begin; index < end; index += 3) {
				gl.outputNormal(sideNormalBuffer.get(index), sideNormalBuffer.get(index + 1),
						sideNormalBuffer.get(index + 2));
				gl.outputVertex(sideQuadsBuffer.get(index), sideQuadsBuffer.get(index + 1),
						sideQuadsBuffer.get(index + 2));
			}
			gl.endDrawing();
		}
	}

	/**
	 * Draw border.
	 */
	private void drawBorder() {
		var stride = depth == 0 ? 3 : 6;
		var i = -1;
		while (i < currentIndex) {
			var begin = indices[++i];
			var end = indices[++i];
			gl.beginDrawing(GL.GL_LINE_LOOP);
			for (var index = begin; index < end; index += stride) {
				gl.outputVertex(sideQuadsBuffer.get(index), sideQuadsBuffer.get(index + 1),
						sideQuadsBuffer.get(index + 2));
			}
			gl.endDrawing();
		}
	}

	/**
	 * Draw face.
	 *
	 * @param up
	 *            the up
	 */
	private void drawFace(final boolean up) {
		gl.beginDrawing(GL.GL_TRIANGLES);
		gl.outputNormal(0, 0, up ? 1 : -1);
		for (var i = 0; i < faceVertexBuffer.limit(); i += 3) {
			if (gl.isTextured()) {
				gl.outputTexCoord(faceTextureBuffer.get(2 * i / 3), faceTextureBuffer.get(2 * i / 3 + 1));
			}
			gl.getGL().glVertex3d(faceVertexBuffer.get(i), faceVertexBuffer.get(i + 1), faceVertexBuffer.get(i + 2));
		}
		gl.endDrawing();
	}

	@Override
	public void drawVertex(final int i, final double x, final double y, final double z) {
		if (gl.isTextured()) { faceTextureBuffer.put(x / width).put(y / height); }
		faceVertexBuffer.put(x).put(y).put(z);
	}

	@Override
	public void combine(final double[] coords, final Object[] data, final float[] weight, final Object[] outData) {
		outData[0] = data[0];
	}

}