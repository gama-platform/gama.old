package ummisco.gama.opengl.scene;

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
import com.jogamp.opengl.GL2;
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
class StringDrawerHelper {

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

	StringDrawerHelper(final OpenGL openGL) {
		glu = GLU.createGLU(openGL.getGL());
		tobj = glu.gluNewTess();
	}

	void clear() {
		if (faceBuffer != null) { faceBuffer.clear(); }
		if (sideBuffer != null) { sideBuffer.clear(); }
	}

	void prepare() {
		if (!wireframe) { if (faceBuffer == null) { faceBuffer = new FaceBuffer(); } }
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
			glu.gluTessProperty(tobj, GLU_TESS_WINDING_RULE,
					windingRule == WIND_EVEN_ODD ? GLU_TESS_WINDING_ODD : GLU_TESS_WINDING_NONZERO);
			glu.gluTessNormal(tobj, 0, 0, -1);
			glu.gluTessBeginPolygon(tobj, (double[]) null);
		}
		double x0 = 0, y0 = 0;
		while (!pi.isDone()) {
			final double[] coords = new double[6];
			switch (pi.currentSegment(coords)) {
				case PathIterator.SEG_MOVETO:
					// We begin a new contour within the global polygon
					// If we are solid, we pass the information to the tesselation algorithm
					if (!wireframe) {
						glu.gluTessBeginContour(tobj);
						glu.gluTessVertex(tobj, coords, 0, coords);
					}
					x0 = coords[0];
					y0 = coords[1];
					sideBuffer.beginNewContour();
					sideBuffer.addContourVertex0(x0, y0);
					break;
				case PathIterator.SEG_LINETO:
					// If we are solid, we pass the coordinates to the tesselation algorithm
					if (!wireframe) { glu.gluTessVertex(tobj, coords, 0, coords); }
					// We also pass the coordinates to the side buffer, which will decide whether to create depth
					// level coordinates and compute the normal vector associated with this vertex
					sideBuffer.addContourVertex0(coords[0], coords[1]);
					break;
				case PathIterator.SEG_CLOSE:
					if (!wireframe) { glu.gluTessEndContour(tobj); }
					// We close the contour by adding it explicitly to the sideBuffer in order to close the loop
					sideBuffer.addContourVertex0(x0, y0);
					sideBuffer.endContour();
			}
			pi.next();
		}
		if (!wireframe) { glu.gluTessEndPolygon(tobj); }
		flip();
	}

	void drawOn(final OpenGL openGL) {
		// openGL.enable(GL3.GL_PRIMITIVE_RESTART);
		// openGL.getGL().glPrimitiveRestartIndex(RESTART);
		final GL2 gl = openGL.getGL();
		openGL.enable(GL2.GL_VERTEX_ARRAY);
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
					// To draw the border, we provide a stride of 0 (= 3*BYTES_PER_DOUBLE) if depth = 0, as the bottom
					// coordinates are contiguous, or 6*BYTES_PER_DOUBLE to take the upper coordinates into account
					gl.glVertexPointer(3, GL2.GL_DOUBLE, depth == 0 ? 0 : 6 * BYTES_PER_DOUBLE, sideBuffer.quadsBuffer);
					// We use the sides buffer to draw only the top contours. Depending on whether or not there is a
					// depth, we rely on the indices of the different contours as either every 3 ordinates (if depth ==
					// 0), or every 6 ordinates to account for the added 'z = depth' coordinates.
					// gl.glDrawArrays(GL2.GL_LINE_LOOP, 0, sideBuffer.quadsBuffer.limit() / (depth == 0 ? 3 : 6));
					// TODO AD Try using glDrawELements instead, with indices and the RESTART constant.
					// gl.glVertexPointer(3, GL2.GL_DOUBLE, 0, sideBuffer.quadsBuffer);
					// gl.glDrawElements(GL2.GL_LINE_LOOP, sideBuffer.bottomIndices.limit(), GL2.GL_UNSIGNED_INT,
					// sideBuffer.bottomIndices);

					for (int i = 0; i < sideBuffer.currentIndex; i++) {
						gl.glDrawArrays(GL2.GL_LINE_LOOP, sideBuffer.indices[i] / (depth == 0 ? 3 : 6),
								(sideBuffer.indices[i + 1] - sideBuffer.indices[i]) / (depth == 0 ? 3 : 6));
					}
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
					gl.glVertexPointer(3, GL2.GL_DOUBLE, 0, sideBuffer.quadsBuffer);
					for (int i = 0; i < sideBuffer.currentIndex; i++) {
						gl.glDrawArrays(GL2.GL_LINE_LOOP, sideBuffer.indices[i] / 3,
								(sideBuffer.indices[i + 1] - sideBuffer.indices[i]) / 3);
					}
				} else {
					gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
					sideBuffer.drawOn(openGL);
					gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
				}
			}
		} finally {
			openGL.disable(GL2.GL_VERTEX_ARRAY);
			// openGL.disable(GL3.GL_PRIMITIVE_RESTART);
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
		// private int bottomIndex = 0;
		private final IntBuffer bottomIndices = Buffers.newDirectIntBuffer(BUFFER_SIZE / 2);

		public void endContour() {
			// bottomIndices.put(RESTART);
			indices[++currentIndex] = quadsBuffer.position();
		}

		public void beginNewContour() {
			indices[++currentIndex] = quadsBuffer.position();
		}

		public void flip() {
			// bottomIndices.flip();
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
			// We store the bottom face in any case
			// bottomIndices.put(bottomIndex++);
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
				// bottomIndex++;
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
			// bottomIndex = 0;
			normalBuffer = depth > 0 ? newDirectDoubleBuffer(BUFFER_SIZE) : null;
		}

		public void drawOn(final OpenGL openGL) {
			if (quadsBuffer.limit() == 0) return;
			GL2 gl = openGL.getGL();
			openGL.enable(GL2.GL_NORMAL_ARRAY);
			gl.glNormalPointer(GL2.GL_DOUBLE, 0, normalBuffer);
			gl.glVertexPointer(3, GL2.GL_DOUBLE, 0, quadsBuffer);
			for (int i = 0; i < currentIndex; i++) {
				gl.getGL().glDrawArrays(GL2.GL_QUAD_STRIP, indices[i] / 3, (indices[i + 1] - indices[i]) / 3);
			}
			openGL.disable(GL2.GL_NORMAL_ARRAY);
		}
	}

	private class FaceBuffer extends GLUtessellatorCallbackAdapter {
		DoubleBuffer current = newDirectDoubleBuffer(BUFFER_SIZE);
		DoubleBuffer texture;

		FaceBuffer() {
			glu.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, this);
			glu.gluTessCallback(tobj, GLU.GLU_TESS_END, this);
			glu.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, this);
			glu.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, this);
			glu.gluTessCallback(tobj, GLU.GLU_TESS_COMBINE, this);
			glu.gluTessCallback(tobj, GLU.GLU_TESS_EDGE_FLAG, this);
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

		void drawOn(final OpenGL gl, final boolean up) {
			if (current.limit() == 0) return;
			gl.outputNormal(0, 0, up ? 1 : -1);
			if (textured) {
				gl.enable(GL2.GL_TEXTURE_COORD_ARRAY);
				gl.getGL().glTexCoordPointer(2, GL2.GL_DOUBLE, 0, texture);
			}
			gl.getGL().glVertexPointer(3, GL2.GL_DOUBLE, 0, current);
			gl.getGL().glDrawArrays(GL2.GL_TRIANGLES, 0, current.limit() / 3);
			if (textured) { gl.disable(GL2.GL_TEXTURE_COORD_ARRAY); }
		}

		@Override
		public void vertex(final Object data) {
			double[] d = (double[]) data;
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