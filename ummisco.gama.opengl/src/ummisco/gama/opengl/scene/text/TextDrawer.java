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
import java.nio.IntBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.fixedfunc.GLPointerFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUtessellator;
import com.jogamp.opengl.glu.GLUtessellatorCallbackAdapter;
import com.jogamp.opengl.util.gl2.GLUT;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gaml.statements.draw.TextDrawingAttributes;
import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.scene.ObjectDrawer;
import ummisco.gama.ui.utils.PlatformHelper;

/**
 *
 * The class StringDrawer.
 *
 * @author drogoul
 * @since 4 mai 2013
 *
 */

public class TextDrawer extends ObjectDrawer<StringObject> {
	final static AffineTransform AT = AffineTransform.getScaleInstance(1.0, -1.0);
	final FontRenderContext context = new FontRenderContext(new AffineTransform(), true, true);
	private static final int BUFFER_SIZE = 1000000;
	final GLUtessellator tobj = GLU.gluNewTess();
	private final FaceBuffer faceBuffer = new FaceBuffer();
	private final SideBuffer sideBuffer = new SideBuffer();
	Color border;
	double width, height, depth;

	public TextDrawer(final OpenGL gl) {
		super(gl);
	}

	@Override
	protected void _draw(final StringObject s) {
		TextDrawingAttributes attributes = s.getAttributes();

		if (!attributes.isPerspective()) {
			drawBitmap(s.getObject(), attributes);
		} else {
			Font font = attributes.getFont();
			final int fontSize = PlatformHelper.scaleToHiDPI(Math.round(font.getSize()));
			if (fontSize != font.getSize()) { font = font.deriveFont((float) fontSize); }
			Shape shape = font.createGlyphVector(context, s.getObject()).getOutline();
			final Rectangle2D bounds = shape.getBounds2D();
			this.depth = attributes.getDepth();
			this.border = attributes.getBorder();
			this.width = bounds.getWidth();
			this.height = bounds.getHeight();
			faceBuffer.prepare();
			sideBuffer.prepare();
			process(shape.getPathIterator(AT, attributes.getPrecision()));
			drawOn(attributes, bounds.getY());
		}

	}

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
					sideBuffer.beginNewContour();
					sideBuffer.addContourVertex0(x0, y0);
					break;
				case PathIterator.SEG_LINETO:
					// If we are solid, we pass the coordinates to the tesselation algorithm
					if (!wireframe) { GLU.gluTessVertex(tobj, coords, 0, coords); }
					// We also pass the coordinates to the side buffer, which will decide whether to create depth
					// level coordinates and compute the normal vector associated with this vertex
					sideBuffer.addContourVertex0(coords[0], coords[1]);
					break;
				case PathIterator.SEG_CLOSE:
					if (!wireframe) { GLU.gluTessEndContour(tobj); }
					// We close the contour by adding it explicitly to the sideBuffer in order to close the loop
					sideBuffer.addContourVertex0(x0, y0);
					sideBuffer.endContour();
			}
			pi.next();
		}
		if (!wireframe) { GLU.gluTessEndPolygon(tobj); }
		sideBuffer.flip();
		faceBuffer.flip();
	}

	void drawOn(final TextDrawingAttributes attributes, final double y) {
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
				faceBuffer.drawOn(depth == 0);
				if (depth > 0) {
					gl.translateBy(0, 0, depth);
					faceBuffer.drawOn(true);
					gl.translateBy(0, 0, -depth);
					sideBuffer.drawOn();
				}
				if (border != null) {
					previous = gl.getCurrentColor();
					gl.setCurrentColor(border);
					gl.translateBy(0, 0, depth + 5 * gl.getCurrentZIncrement());
					sideBuffer.drawBorderOn();
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
					sideBuffer.drawBorderOn();
				} else {
					// final var ogl = gl.getGL();
					// Normally already set by beginObject() in OpenGL.
					// ogl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
					sideBuffer.drawOn();
					// ogl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
				}
			}
		} finally {
			if (previous != null) { gl.setCurrentColor(previous); }
			gl.popMatrix();
		}
	}

	@Override
	public void dispose() {}

	private class SideBuffer {
		double previousX = Double.MIN_VALUE, previousY = Double.MIN_VALUE;
		ICoordinates temp = ICoordinates.ofLength(4);
		GamaPoint normal = new GamaPoint();
		int currentIndex = -1;
		int[] indices = new int[1000]; // Indices of the "move_to" or "close"
		private final DoubleBuffer normalBuffer = newDirectDoubleBuffer(BUFFER_SIZE);
		private final DoubleBuffer quadsBuffer = newDirectDoubleBuffer(BUFFER_SIZE); // Contains the sides
		private final IntBuffer bottomIndices = Buffers.newDirectIntBuffer(BUFFER_SIZE / 2);

		public void endContour() {
			indices[++currentIndex] = quadsBuffer.position();
		}

		public void beginNewContour() {
			indices[++currentIndex] = quadsBuffer.position();
		}

		public void flip() {
			quadsBuffer.flip();
			normalBuffer.flip();
		}

		/**
		 * Add a point at altitude zero: creates automatically a point at altitude "depth" if depth > 0 in the quads
		 *
		 * @param x
		 *            and y represent the new vertex to be added
		 *
		 */
		public void addContourVertex0(final double x, final double y) {
			quadsBuffer.put(x).put(y).put(0);
			if (depth > 0) {
				// If depth > 0, then we will build side faces, and we need to calculate their normal
				if (previousX > Double.MIN_VALUE) {
					temp.setTo(previousX, previousY, 0, previousX, previousY, depth, x, y, 0, previousX, previousY, 0);
					temp.getNormal(true, 1, normal);
					// We add two normal vectors as the vertex buffer will be filled by 2 coordinates
					normalBuffer.put(new double[] { normal.x, normal.y, normal.z, normal.x, normal.y, normal.z });
				}
				// And we store the upper face
				quadsBuffer.put(x).put(y).put(depth);
			}
			previousX = x;
			previousY = y;
		}

		public void prepare() {
			normalBuffer.clear();
			quadsBuffer.clear();
			bottomIndices.clear();
			currentIndex = -1;
		}

		public void fixedPipelineFallback(final OpenGL openGL) {
			var i = -1;
			while (i < currentIndex) {
				var begin = indices[++i];
				var end = indices[++i];
				openGL.beginDrawing(GL2.GL_QUAD_STRIP);
				for (var index = begin; index < end; index += 3) {
					openGL.outputNormal(normalBuffer.get(index), normalBuffer.get(index + 1),
							normalBuffer.get(index + 2));
					openGL.outputVertex(quadsBuffer.get(index), quadsBuffer.get(index + 1), quadsBuffer.get(index + 2));
				}
				openGL.endDrawing();
			}

		}

		public void fixedPipelineFallbackForBorders() {
			var stride = depth == 0 ? 3 : 6;
			var i = -1;
			while (i < currentIndex) {
				var begin = indices[++i];
				var end = indices[++i];
				gl.beginDrawing(GL.GL_LINE_LOOP);
				for (var index = begin; index < end; index += stride) {
					gl.outputVertex(quadsBuffer.get(index), quadsBuffer.get(index + 1), quadsBuffer.get(index + 2));
				}
				gl.endDrawing();
			}

		}

		public void drawOn() {
			if (quadsBuffer.limit() == 0) return;
			// AD - See issue #3125
			if (gl.isRenderingKeystone()) {
				fixedPipelineFallback(gl);
				return;
			}
			var ogl = gl.getGL();
			gl.enable(GLPointerFunc.GL_VERTEX_ARRAY);
			gl.enable(GLPointerFunc.GL_NORMAL_ARRAY);
			ogl.glNormalPointer(GL2GL3.GL_DOUBLE, 0, normalBuffer);
			ogl.glVertexPointer(3, GL2GL3.GL_DOUBLE, 0, quadsBuffer);
			for (var i = 0; i < currentIndex; i++) {
				ogl.getGL().glDrawArrays(GL2.GL_QUAD_STRIP, indices[i] / 3, (indices[i + 1] - indices[i]) / 3);
			}
			gl.disable(GLPointerFunc.GL_NORMAL_ARRAY);
			gl.disable(GLPointerFunc.GL_VERTEX_ARRAY);
		}

		public void drawBorderOn() {
			if (gl.isRenderingKeystone()) {
				fixedPipelineFallbackForBorders();
				return;
			}
			gl.enable(GLPointerFunc.GL_VERTEX_ARRAY);
			var olg = gl.getGL();
			// To draw the border, we provide a stride of 0 (= 3*BYTES_PER_DOUBLE) if depth = 0, as the bottom
			// coordinates are contiguous, or 6*BYTES_PER_DOUBLE to take the upper coordinates into account
			olg.glVertexPointer(3, GL2GL3.GL_DOUBLE, depth == 0 ? 0 : 6 * Double.SIZE / Byte.SIZE, quadsBuffer);
			// We use the sides buffer to draw only the top contours. Depending on whether or not there is a
			// depth, we rely on the indices of the different contours as either every 3 ordinates (if depth ==
			// 0), or every 6 ordinates to account for the added 'z = depth' coordinates.
			for (var i = 0; i < currentIndex; i++) {
				olg.glDrawArrays(GL.GL_LINE_LOOP, indices[i] / (depth == 0 ? 3 : 6),
						(indices[i + 1] - indices[i]) / (depth == 0 ? 3 : 6));
			}
			gl.disable(GLPointerFunc.GL_VERTEX_ARRAY);
		}
	}

	private class FaceBuffer extends GLUtessellatorCallbackAdapter {
		final DoubleBuffer current = newDirectDoubleBuffer(BUFFER_SIZE);
		final DoubleBuffer texture = newDirectDoubleBuffer(BUFFER_SIZE * 2 / 3);

		FaceBuffer() {
			GLU.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, this);
			GLU.gluTessCallback(tobj, GLU.GLU_TESS_END, this);
			GLU.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, this);
			GLU.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, this);
			GLU.gluTessCallback(tobj, GLU.GLU_TESS_COMBINE, this);
			GLU.gluTessCallback(tobj, GLU.GLU_TESS_EDGE_FLAG, this);
		}

		public void flip() {
			current.flip();
			if (texture != null) { texture.flip(); }
		}

		public void prepare() {
			current.clear();
			texture.clear();
		}

		public void fixedPipelineFallback(final boolean up) {
			gl.beginDrawing(GL.GL_TRIANGLES);
			gl.outputNormal(0, 0, up ? 1 : -1);
			for (var i = 0; i < current.limit(); i += 3) {
				if (gl.isTextured()) { gl.outputTexCoord(texture.get(2 * i / 3), texture.get(2 * i / 3 + 1)); }
				gl.getGL().glVertex3d(current.get(i), current.get(i + 1), current.get(i + 2));
			}
			gl.endDrawing();
		}

		void drawOn(final boolean up) {
			if (current.limit() == 0) return;
			if (gl.isRenderingKeystone()) {
				fixedPipelineFallback(up);
				return;
			}
			gl.enable(GLPointerFunc.GL_VERTEX_ARRAY);
			gl.outputNormal(0, 0, up ? 1 : -1);
			if (gl.isTextured()) {
				gl.enable(GLPointerFunc.GL_TEXTURE_COORD_ARRAY);
				gl.getGL().glTexCoordPointer(2, GL2GL3.GL_DOUBLE, 0, texture);
			}
			gl.getGL().glVertexPointer(3, GL2GL3.GL_DOUBLE, 0, current);
			gl.getGL().glDrawArrays(GL.GL_TRIANGLES, 0, current.limit() / 3);
			if (gl.isTextured()) { gl.disable(GLPointerFunc.GL_TEXTURE_COORD_ARRAY); }
			gl.disable(GLPointerFunc.GL_VERTEX_ARRAY);
		}

		@Override
		public void vertex(final Object data) {
			var d = (double[]) data;
			if (gl.isTextured()) { texture.put(d[0] / width).put(d[1] / height); }
			current.put(d[0]).put(d[1]).put(d[2]);
		}

		@Override
		public void combine(final double[] coords, final Object[] data, final float[] weight, final Object[] outData) {
			outData[0] = data[0];
		}

	}

}