package ummisco.gama.opengl.scene.text;

import static com.jogamp.common.nio.Buffers.newDirectDoubleBuffer;
import static com.jogamp.opengl.glu.GLU.GLU_TESS_WINDING_NONZERO;
import static com.jogamp.opengl.glu.GLU.GLU_TESS_WINDING_ODD;
import static com.jogamp.opengl.glu.GLU.GLU_TESS_WINDING_RULE;
import static java.awt.geom.PathIterator.WIND_EVEN_ODD;

import java.awt.Color;
import java.awt.geom.PathIterator;
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

import msi.gama.common.geometry.ICoordinates;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.GamaColor;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.opengl.OpenGL;

/**
 * A class that provides a compact way of processing and dislaying strings
 *
 * @author Alexis Drogoul
 *
 */
class TextDrawerHelper {

	static {
		DEBUG.OFF();
	}
	// private final int RESTART = Integer.MIN_VALUE;
	private static int BYTES_PER_DOUBLE = Double.SIZE / Byte.SIZE;
	private static final int BUFFER_SIZE = 1000000;
	private FaceBuffer faceBuffer;
	private SideBuffer sideBuffer;
	final GLU glu;
	final GLUtessellator tobj;
	int windingRule;
	boolean wireframe, textured;
	Color border;
	double width, height, depth;

	TextDrawerHelper(final OpenGL openGL) {
		glu = GLU.createGLU(openGL.getGL());
		tobj = GLU.gluNewTess();
	}

	void clear() {
		if (faceBuffer != null) { faceBuffer.clear(); }
		if (sideBuffer != null) { sideBuffer.clear(); }
	}

	void prepare() {
		if (!wireframe && faceBuffer == null) { faceBuffer = new FaceBuffer(); }
		if (sideBuffer == null) { sideBuffer = new SideBuffer(); }
		if (faceBuffer != null) { faceBuffer.prepare(); }
		if (sideBuffer != null) { sideBuffer.prepare(); }
	}

	void flip() {
		if (sideBuffer != null) { sideBuffer.flip(); }
		if (faceBuffer != null) { faceBuffer.flip(); }
	}

	public void resetWith(final Double depth, final boolean wireframe, final GamaColor border,
			final boolean withTexture, final double width, final double height) {
		clear();
		this.depth = depth;
		this.wireframe = wireframe;
		this.border = border;
		this.textured = withTexture;
		this.width = width;
		this.height = height;
		prepare();
	}

	void process(final PathIterator pi) {
		windingRule = pi.getWindingRule();
		if (!wireframe) {
			GLU.gluTessProperty(tobj, GLU_TESS_WINDING_RULE,
					windingRule == WIND_EVEN_ODD ? GLU_TESS_WINDING_ODD : GLU_TESS_WINDING_NONZERO);
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
		flip();
	}

	void drawOn(final OpenGL openGL) {
		final var gl = openGL.getGL();
		Color previous = null;
		try {
			if (!wireframe) {
				// Solid case
				faceBuffer.drawOn(openGL, depth == 0);
				if (depth > 0) {
					openGL.translateBy(0, 0, depth);
					faceBuffer.drawOn(openGL, true);
					openGL.translateBy(0, 0, -depth);
					sideBuffer.drawOn(openGL);
				}
				if (border != null) {
					previous = openGL.getCurrentColor();
					openGL.setCurrentColor(border);
					openGL.translateBy(0, 0, depth + 5 * openGL.getCurrentZIncrement());
					sideBuffer.drawBorderOn(openGL);
					openGL.translateBy(0, 0, -depth - 5 * openGL.getCurrentZIncrement());
				}
			} else {
				// Wireframe case
				if (border != null) {
					previous = openGL.getCurrentColor();
					openGL.setCurrentColor(border);
				}
				if (depth == 0) {
					// We use the quads side buffer to render only the top (lines)
					sideBuffer.drawBorderOn(openGL);
				} else {
					gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
					sideBuffer.drawOn(openGL);
					gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
				}
			}
		} finally {
			if (previous != null) { openGL.setCurrentColor(previous); }
		}
	}

	private class SideBuffer {
		double previousX = Double.MIN_VALUE, previousY = Double.MIN_VALUE;
		ICoordinates temp = ICoordinates.ofLength(4);
		GamaPoint normal = new GamaPoint();
		int currentIndex = -1;
		int[] indices = new int[1000]; // Indices of the "move_to" or "close"
		private DoubleBuffer normalBuffer;
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
			if (normalBuffer != null) { normalBuffer.flip(); }
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
					addNormal(normal.x, normal.y, normal.z, normal.x, normal.y, normal.z);
				}
				// And we store the upper face
				quadsBuffer.put(x).put(y).put(depth);
			}
			previousX = x;
			previousY = y;
		}

		public void addNormal(final double... ordinates) {
			normalBuffer.put(ordinates);
		}

		public void clear() {
			if (normalBuffer != null) { normalBuffer.clear(); }
			quadsBuffer.clear();
			bottomIndices.clear();

		}

		public void prepare() {
			currentIndex = -1;
			normalBuffer = depth > 0 ? newDirectDoubleBuffer(BUFFER_SIZE) : null;
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

		public void fixedPipelineFallbackForBorders(final OpenGL openGL) {
			var stride = depth == 0 ? 3 : 6;
			var i = -1;
			while (i < currentIndex) {
				var begin = indices[++i];
				var end = indices[++i];
				openGL.beginDrawing(GL.GL_LINE_LOOP);
				for (var index = begin; index < end; index += stride) {
					openGL.outputVertex(quadsBuffer.get(index), quadsBuffer.get(index + 1), quadsBuffer.get(index + 2));
				}
				openGL.endDrawing();
			}

		}

		public void drawOn(final OpenGL openGL) {
			if (quadsBuffer.limit() == 0) return;
			// AD - See issue #3125
			if (openGL.isRenderingKeystone()) {
				fixedPipelineFallback(openGL);
				return;
			}
			openGL.enable(GLPointerFunc.GL_VERTEX_ARRAY);
			var gl = openGL.getGL();
			openGL.enable(GLPointerFunc.GL_NORMAL_ARRAY);
			gl.glNormalPointer(GL2GL3.GL_DOUBLE, 0, normalBuffer);
			gl.glVertexPointer(3, GL2GL3.GL_DOUBLE, 0, quadsBuffer);
			for (var i = 0; i < currentIndex; i++) {
				gl.getGL().glDrawArrays(GL2.GL_QUAD_STRIP, indices[i] / 3, (indices[i + 1] - indices[i]) / 3);
			}
			openGL.disable(GLPointerFunc.GL_NORMAL_ARRAY);
			openGL.disable(GLPointerFunc.GL_VERTEX_ARRAY);
		}

		public void drawBorderOn(final OpenGL openGL) {
			if (openGL.isRenderingKeystone()) {
				fixedPipelineFallbackForBorders(openGL);
				return;
			}
			openGL.enable(GLPointerFunc.GL_VERTEX_ARRAY);
			var gl = openGL.getGL();
			// To draw the border, we provide a stride of 0 (= 3*BYTES_PER_DOUBLE) if depth = 0, as the bottom
			// coordinates are contiguous, or 6*BYTES_PER_DOUBLE to take the upper coordinates into account
			gl.glVertexPointer(3, GL2GL3.GL_DOUBLE, depth == 0 ? 0 : 6 * BYTES_PER_DOUBLE, quadsBuffer);
			// We use the sides buffer to draw only the top contours. Depending on whether or not there is a
			// depth, we rely on the indices of the different contours as either every 3 ordinates (if depth ==
			// 0), or every 6 ordinates to account for the added 'z = depth' coordinates.
			for (var i = 0; i < currentIndex; i++) {
				gl.glDrawArrays(GL.GL_LINE_LOOP, indices[i] / (depth == 0 ? 3 : 6),
						(indices[i + 1] - indices[i]) / (depth == 0 ? 3 : 6));
			}
			openGL.disable(GLPointerFunc.GL_VERTEX_ARRAY);
		}
	}

	private class FaceBuffer extends GLUtessellatorCallbackAdapter {
		DoubleBuffer current = newDirectDoubleBuffer(BUFFER_SIZE);
		DoubleBuffer texture;

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

		public void clear() {
			current.clear();
			if (texture != null) { texture = null; }
		}

		public void prepare() {
			if (textured && texture == null) { texture = newDirectDoubleBuffer(BUFFER_SIZE * 2 / 3); }
		}

		public void fixedPipelineFallback(final OpenGL openGL, final boolean up) {
			openGL.beginDrawing(GL.GL_TRIANGLES);
			openGL.outputNormal(0, 0, up ? 1 : -1);
			for (var i = 0; i < current.limit(); i += 3) {
				if (textured) { openGL.outputTexCoord(texture.get(2 * i / 3), texture.get(2 * i / 3 + 1)); }
				openGL.getGL().glVertex3d(current.get(i), current.get(i + 1), current.get(i + 2));
			}
			openGL.endDrawing();
		}

		void drawOn(final OpenGL gl, final boolean up) {
			if (current.limit() == 0) return;
			if (gl.isRenderingKeystone()) {
				fixedPipelineFallback(gl, up);
				return;
			}
			gl.enable(GLPointerFunc.GL_VERTEX_ARRAY);
			gl.outputNormal(0, 0, up ? 1 : -1);
			if (textured) {
				gl.enable(GLPointerFunc.GL_TEXTURE_COORD_ARRAY);
				gl.getGL().glTexCoordPointer(2, GL2GL3.GL_DOUBLE, 0, texture);
			}
			gl.getGL().glVertexPointer(3, GL2GL3.GL_DOUBLE, 0, current);
			gl.getGL().glDrawArrays(GL.GL_TRIANGLES, 0, current.limit() / 3);
			if (textured) { gl.disable(GLPointerFunc.GL_TEXTURE_COORD_ARRAY); }
			gl.disable(GLPointerFunc.GL_VERTEX_ARRAY);
		}

		@Override
		public void vertex(final Object data) {
			var d = (double[]) data;
			if (textured) { texture.put(d[0] / width).put(d[1] / height); }
			current.put(d[0]).put(d[1]).put(d[2]);
		}

		@Override
		public void error(final int errnum) {
			DEBUG.OUT("Error in tesselation: " + glu.gluErrorString(errnum));
		}

		@Override
		public void combine(final double[] coords, final Object[] data, final float[] weight, final Object[] outData) {
			outData[0] = data[0];
		}

	}

}