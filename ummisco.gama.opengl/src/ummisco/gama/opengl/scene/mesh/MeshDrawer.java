/*******************************************************************************************************
 *
 * MeshDrawer.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.scene.mesh;

import java.awt.Color;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.Locale;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.fixedfunc.GLPointerFunc;
import com.jogamp.opengl.util.gl2.GLUT;

import msi.gama.common.geometry.ICoordinates;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.layers.MeshLayerData;
import msi.gama.util.matrix.IField;
import msi.gaml.statements.draw.IMeshColorProvider;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.scene.ObjectDrawer;

/**
 *
 * The class MeshDrawer.
 *
 * @author grignard + A. Drogoul 2021
 * @since 15 mai 2013
 *
 */
public class MeshDrawer extends ObjectDrawer<MeshObject> {

	static {
		DEBUG.ON();
	}

	/** The Constant BLACK. */
	static final double[] BLACK = { 0, 0, 0, 1 };

	/** The Constant TRANSPARENT. */
	static final double[] TRANSPARENT = { 0, 0, 0, 0 };

	// ARRAYS
	/** The attribute holding the data */
	// private double[] data;

	/** The attribute holding the position of the vertex indices (in case of no_data) */
	private int[] realIndexes;

	// BUFFERS
	/** The buffers for vertices, normals, textures, colors, line colors */
	private DoubleBuffer vertexBuffer, normalBuffer, texBuffer, colorBuffer, lineColorBuffer;

	/** The buffer holding the indices to the vertices to draw */
	private IntBuffer indexBuffer;

	/** The widht and height of each cell in world coordinates; the minimal and maximal values found in the data **/
	private double cx, cy;

	/** above: value representing the minimal value to draw **/
	private double above;

	// FLAGS
	/** Flags indicating if the data is to be drawn using triangles or rectangles and with or w/o the value */
	private boolean triangles;

	/** Flags indicating what to output: textures, colors, lines ? */
	private boolean outputsTextures, outputsColors, outputsLines, useFillForLines;

	// COLORS
	/** An array holding the 4 components of the line color */
	private double[] lineColor;

	/** An array used for the transfer of colors from the color provider */
	double[] rgb = new double[4];

	/** An array holding the min and max value found in data. */
	double[] minMax = new double[2];

	/** The provider of color for the vertices */
	private IMeshColorProvider colorProvider;

	/** The normals used for drawing quads when the mesh is drawn using rectangles */
	final static double[] quadNormals = { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 };

	/** A temporary coordinate sequence used to hold vertices in order to compute normals of triangles */
	final ICoordinates surface = ICoordinates.ofLength(9);

	/** A temporary transfer value for the normal */
	final GamaPoint normal = new GamaPoint();

	/**
	 * Instantiates a new mesh drawer.
	 *
	 * @param gl
	 *            the instance of OpenGL to which this drawer belongs
	 */
	public MeshDrawer(final OpenGL gl) {
		super(gl);
	}

	@Override
	public void dispose() {
		surface.setTo(0);
		colorProvider = null;
		// data = null;
		realIndexes = null;
		vertexBuffer = normalBuffer = texBuffer = colorBuffer = lineColorBuffer = null;
		indexBuffer = null;
	}

	@Override
	protected void _draw(final MeshObject object) {
		var attributes = object.getAttributes();
		int cols = (int) attributes.getXYDimension().x;
		int rows = (int) attributes.getXYDimension().y;
		boolean grayscale = attributes.isGrayscaled();
		Color line = attributes.getBorder();
		useFillForLines = line == null && gl.isWireframe() && colorProvider != null;
		this.colorProvider = attributes.getColorProvider();
		this.lineColor = line != null
				? new double[] { line.getRed() / 255d, line.getGreen() / 255d, line.getBlue() / 255d, 1 } : BLACK;
		outputsTextures = gl.isTextured() && !grayscale;
		outputsColors = (colorProvider != null || grayscale) && !gl.isWireframe();
		outputsLines = gl.isWireframe() || line != null;
		// noData: value representing the absence of value
		double noData = attributes.getNoDataValue();
		if (noData == IField.NO_NO_DATA) { noData = object.getObject().getNoData(null); }
		above = attributes.getAbove();

		this.cy = this.gl.getWorldHeight() / (rows - 1d);
		this.cx = this.gl.getWorldWidth() / (cols - 1d);

		boolean withText = attributes.isWithText();
		this.triangles = attributes.isTriangulated();

		double[] data = attributes.getSmoothProvider().smooth(cols, rows, object.getObject().getMatrix(), noData,
				attributes.getSmooth());
		getMinMax(data, noData, minMax);
		initializeBuffers(cols, rows);
		fillBuffers(data, cols, rows, noData);
		finalizeBuffers();
		gl.pushMatrix();
		try {
			applyTranslation(object);
			if (object.getAttributes().getScale() != null) {
				double zScale = object.getAttributes().getScale();
				gl.scaleBy(1, 1, zScale);
			}
			drawField(cols, rows);
			if (withText) { drawLabels(data, cols, rows); }
		} finally {
			gl.popMatrix();
		}
	}

	/**
	 * Gets the min max.
	 *
	 * @param data
	 *            the data
	 * @param result
	 *            the result
	 * @return the min max
	 */
	public double[] getMinMax(final double[] data, final double noData, final double[] result) {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (double f : data) {
			if (f == noData || f < above) { continue; }
			if (f > max) {
				max = f;
			} else if (f < min) { min = f; }
		}
		if (result == null) return new double[] { min, max };
		result[0] = min;
		result[1] = max;
		return result;
	}

	/**
	 * Initialize buffers.
	 */
	private void initializeBuffers(final int cols, final int rows) {
		final var length = cols * rows;
		int previous = realIndexes == null ? 0 : realIndexes.length;
		if (length > previous) {
			realIndexes = new int[length];
			final var lengthM1 = (cols - 1) * (rows - 1);
			int colors = triangles ? length * 4 : lengthM1 * 16;
			int points = triangles ? length * 3 : lengthM1 * 12;
			int textures = triangles ? length * 2 : lengthM1 * 8;
			vertexBuffer = Buffers.newDirectDoubleBuffer(points);
			normalBuffer = Buffers.newDirectDoubleBuffer(points);
			indexBuffer = Buffers.newDirectIntBuffer(lengthM1 * 6);
			// AD : fix for #3299. outputsLines and outputsColors can change overtime and it is necessary to maintain
			// the buffers if the size doesnt change
			lineColorBuffer = Buffers.newDirectDoubleBuffer(colors);
			texBuffer = Buffers.newDirectDoubleBuffer(textures);
			colorBuffer = Buffers.newDirectDoubleBuffer(colors);
		} else {
			vertexBuffer.clear();
			normalBuffer.clear();
			indexBuffer.clear();
			lineColorBuffer.clear();
			texBuffer.clear();
			colorBuffer.clear();
		}

	}

	/**
	 * Fill buffers.
	 */
	public void fillBuffers(final double[] data, final int cols, final int rows, final double noData) {
		if (triangles) {
			if (noData == IField.NO_NO_DATA) {
				fillBuffersWithTrianglesSimplified(data, cols, rows);
			} else {
				fillBuffersWithTriangles(data, cols, rows, noData);
			}
		} else {
			fillBuffersWithRectangles(data, cols, rows, noData);
		}
	}

	/**
	 * Fill buffers with rectangles.
	 */
	private void fillBuffersWithRectangles(final double[] data, final int cols, final int rows, final double noData) {
		int index = 0;
		for (var i = 0; i < cols - 1; ++i) {
			double x1 = i * cx;
			double x2 = (i + 1) * cx;
			for (var j = 0; j < rows - 1; ++j) {
				double y1 = -j * cy;
				double y2 = -(j + 1) * cy;
				double z = data[j * cols + i];
				if (z == noData) { continue; }
				vertexBuffer.put(new double[] { x1, y1, z, x2, y1, z, x2, y2, z, x1, y2, z });
				setColor(cols, rows, z, i, j);
				setColor(cols, rows, z, i + 1, j);
				setColor(cols, rows, z, i + 1, j + 1);
				setColor(cols, rows, z, i, j + 1);
				normalBuffer.put(quadNormals);
				indexBuffer.put(index).put(index + 1).put(index + 3);
				indexBuffer.put(index + 1).put(index + 2).put(index + 3);
				index += 4;
			}
		}
	}

	/**
	 * Fill buffers with triangles.
	 */
	private void fillBuffersWithTriangles(final double[] data, final int cols, final int rows, final double noData) {
		var realIndex = 0;
		for (var j = 0; j < rows; j++) {
			var y = j * cy;
			for (var i = 0; i < cols; i++) {
				var x = i * cx;
				var index = j * cols + i;
				var z = get(cols, rows, data, i, j);
				realIndexes[index] = z == noData ? -1 : realIndex++;
				if (z == noData) { continue; }
				vertexBuffer.put(x).put(-y).put(z);
				setColor(cols, rows, z, i, j);
				setNormal(data, cols, rows, i, j);
			}
		}
		for (var j = 1; j < rows; j++) {
			for (var i = 1; i < cols; i++) {
				var index = j * cols + i;
				var current = realIndexes[index];
				var minus1 = realIndexes[index - 1];
				var minusCols = realIndexes[index - cols];
				var minusColsAnd1 = realIndexes[index - cols - 1];
				if (minus1 == -1 || minusCols == -1 || minusColsAnd1 == -1) { continue; }
				indexBuffer.put(current).put(minus1).put(minusCols);
				indexBuffer.put(minusColsAnd1).put(minusCols).put(minus1);

			}
		}

	}

	/**
	 * Fill buffers with triangles.
	 */
	private void fillBuffersWithTrianglesSimplified(final double[] data, final int cols, final int rows) {
		for (var j = 0; j < rows; j++) {
			for (var i = 0; i < cols; i++) {
				var z = get(cols, rows, data, i, j);
				vertexBuffer.put(i * cx).put(-j * cy).put(z);
				setColor(cols, rows, z, i, j);
				setNormal(data, cols, rows, i, j);
			}
		}
		// Different loop for building the indexes
		for (var j = 0; j < rows - 1; j++) {
			for (var i = 0; i < cols - 1; i++) {
				var index = j * cols + i;
				indexBuffer.put(index).put(index + 1).put(index + cols);
				indexBuffer.put(index + 1).put(index + cols + 1).put(index + cols);
			}
		}

	}

	/**
	 * Compute normal.
	 *
	 * @param cols
	 *            the cols
	 * @param rows
	 *            the rows
	 * @param i
	 *            the i
	 * @param j
	 *            the j
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	private void setNormal(final double[] data, final int cols, final int rows, final int i, final int j) {
		final double x = i * cx;
		final double y = j * cy;
		surface.setTo(x - cx, y - cy, get(cols, rows, data, i - 1, j - 1), x, y - cy, get(cols, rows, data, i, j - 1),
				x + cx, y - cy, get(cols, rows, data, i + 1, j - 1), x + cx, y, get(cols, rows, data, i + 1, j), x + cx,
				y + cy, get(cols, rows, data, i + 1, j + 1), x, y + cy, get(cols, rows, data, i, j + 1), x - cx, y + cy,
				get(cols, rows, data, i - 1, j + 1), x - cx, y, get(cols, rows, data, i - 1, j), x - cx, y - cy,
				get(cols, rows, data, i - 1, j - 1)).getNormal(true, 1, normal);
		normalBuffer.put(normal.x).put(normal.y).put(normal.z);
	}

	/**
	 * Colorize.
	 *
	 * @param z
	 *            the z
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 */
	private void setColor(final int cols, final int rows, final double z, final int x0, final int y0) {
		var x = x0 < 0 ? 0 : x0 > cols - 1 ? cols - 1 : x0;
		var y = y0 < 0 ? 0 : y0 > rows - 1 ? rows - 1 : y0;
		// Outputs either a texture coordinate or the color of the vertex or both
		if (outputsTextures) { texBuffer.put((double) x / (double) (cols - 1)).put((double) y / (double) (rows - 1)); }
		if (outputsColors) {
			if (above != MeshLayerData.ABOVE && z < above) {
				colorBuffer.put(TRANSPARENT, 0, 4);
			} else {
				colorBuffer.put(colorProvider.getColor(y * cols + x, z, minMax[0], minMax[1], rgb), 0, 4);
			}
		}
		// If the line color is specified, outputs it
		if (outputsLines) {
			if (useFillForLines) {
				lineColorBuffer.put(colorProvider.getColor(y * cols + x, z, minMax[0], minMax[1], rgb), 0, 4);
			} else {
				lineColorBuffer.put(lineColor);
			}
		}
	}

	/**
	 * Finalize buffers.
	 */
	private void finalizeBuffers() {
		if (outputsTextures) { texBuffer.flip(); }
		if (outputsColors) { colorBuffer.flip(); }
		if (outputsLines) { lineColorBuffer.flip(); }
		vertexBuffer.flip();
		indexBuffer.flip();
		normalBuffer.flip();
	}

	/**
	 * Draw field fallback.
	 */
	public void drawFieldFallback(final int cols, final int rows) {
		if (vertexBuffer.limit() == 0) return;
		final var ogl = gl.getGL();
		// Forcing alpha
		ogl.glBlendColor(0.0f, 0.0f, 0.0f, (float) gl.getCurrentObjectAlpha());
		ogl.glBlendFunc(GL2ES2.GL_CONSTANT_ALPHA, GL2ES2.GL_ONE_MINUS_CONSTANT_ALPHA);
		gl.beginDrawing(GL.GL_TRIANGLES);
		for (var index = 0; index < indexBuffer.limit(); index++) {
			var i = indexBuffer.get(index);
			int one = i * 3, two = one + 1, three = one + 2, four = one + 3;
			if (!gl.isWireframe() && outputsColors) {
				// TODO Bug when using a gradient: some color components are outside the range
				try {
					gl.setCurrentColor(colorBuffer.get(one), colorBuffer.get(two), colorBuffer.get(three),
							colorBuffer.get(four));
				} catch (IllegalArgumentException e) {
					DEBUG.OUT("Problem with following colors: " + colorBuffer.get(one) + " " + colorBuffer.get(two)
							+ " " + colorBuffer.get(three));
				}
			}
			if (outputsTextures) { gl.outputTexCoord(texBuffer.get(i * 2), texBuffer.get(i * 2 + 1)); }
			gl.outputNormal(normalBuffer.get(one), normalBuffer.get(two), normalBuffer.get(three));
			ogl.glVertex3d(vertexBuffer.get(one), vertexBuffer.get(two), vertexBuffer.get(three));
		}
		if (outputsLines) {
			boolean previous = gl.setObjectWireframe(true);
			for (var index = 0; index < indexBuffer.limit(); index++) {
				var i = indexBuffer.get(index);
				gl.setCurrentColor(lineColorBuffer.get(i * 3), lineColorBuffer.get(i * 3 + 1),
						lineColorBuffer.get(i * 3 + 2), lineColorBuffer.get(i * 3 + 4));
				gl.outputVertex(vertexBuffer.get(i * 3), vertexBuffer.get(i * 3 + 1), vertexBuffer.get(i * 3 + 2));
			}
			gl.setObjectWireframe(previous);
		}
		gl.endDrawing();
		ogl.glBlendColor(0.0f, 0.0f, 0.0f, 0.0f);
		ogl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
	}

	/**
	 * Draw field.
	 */
	public void drawField(final int cols, final int rows) {
		// AD - See issue #3125
		if (gl.isRenderingKeystone()) {
			drawFieldFallback(cols, rows);
			return;
		}
		if (vertexBuffer.limit() == 0) return;
		final var ogl = gl.getGL();
		// Forcing alpha
		ogl.glBlendColor(0.0f, 0.0f, 0.0f, (float) gl.getCurrentObjectAlpha());
		ogl.glBlendFunc(GL2ES2.GL_CONSTANT_ALPHA, GL2ES2.GL_ONE_MINUS_CONSTANT_ALPHA);

		gl.enable(GLPointerFunc.GL_VERTEX_ARRAY);
		gl.enable(GLPointerFunc.GL_NORMAL_ARRAY);
		if (outputsTextures) {
			gl.enable(GLPointerFunc.GL_TEXTURE_COORD_ARRAY);
		} else {
			ogl.glDisable(GL.GL_TEXTURE_2D);
		}
		if (outputsColors) { gl.enable(GLPointerFunc.GL_COLOR_ARRAY); }
		try {
			ogl.glVertexPointer(3, GL2GL3.GL_DOUBLE, 0, vertexBuffer);
			ogl.glNormalPointer(GL2GL3.GL_DOUBLE, 0, normalBuffer);

			if (outputsTextures) { ogl.glTexCoordPointer(2, GL2GL3.GL_DOUBLE, 0, texBuffer); }
			if (outputsColors) { ogl.glColorPointer(4, GL2GL3.GL_DOUBLE, 0, colorBuffer); }

			if (!gl.isWireframe()) {
				ogl.glDrawElements(GL.GL_TRIANGLES, indexBuffer.limit(), GL.GL_UNSIGNED_INT, indexBuffer);
			}
			if (outputsLines) {
				if (!outputsColors) { gl.enable(GLPointerFunc.GL_COLOR_ARRAY); }
				ogl.glColorPointer(4, GL2GL3.GL_DOUBLE, 0, lineColorBuffer);
				boolean previous = gl.setObjectWireframe(true);
				ogl.glDrawElements(GL.GL_TRIANGLES, indexBuffer.limit(), GL.GL_UNSIGNED_INT, indexBuffer);
				gl.setObjectWireframe(previous);
			}

		} finally {
			gl.disable(GLPointerFunc.GL_NORMAL_ARRAY);
			if (outputsTextures) { gl.disable(GLPointerFunc.GL_TEXTURE_COORD_ARRAY); }
			if (outputsColors || outputsLines) { gl.disable(GLPointerFunc.GL_COLOR_ARRAY); }
			gl.disable(GLPointerFunc.GL_VERTEX_ARRAY);
			// Putting back alpha to normal
			ogl.glBlendColor(0.0f, 0.0f, 0.0f, 0.0f);
			ogl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		}

	}

	/**
	 * Draw labels.
	 *
	 * @param gl
	 *            the gl
	 */
	public void drawLabels(final double[] data, final int cols, final int rows) {
		// Draw gridvalue as text inside each cell
		gl.setCurrentColor(Color.black);
		final var strings = new String[data.length];
		final var coords = new double[strings.length * 3];
		for (int i = 0, c = 0; i < cols; i++) {
			final var stepX = i * cx;
			for (var j = 0; j < rows; j++, c += 3) {
				final var stepY = j * cy;
				final var gridValue = data[j * cols + i];
				strings[j * cols + i] = String.format(Locale.US, "%.2f", gridValue);
				coords[c] = stepX + cx / 2;
				coords[c + 1] = -(stepY + cy / 2);
				coords[c + 2] = gridValue;
			}
		}
		gl.beginRasterTextMode();
		final var previous = gl.setObjectLighting(false);
		for (var i = 0; i < strings.length; i++) {
			gl.getGL().glRasterPos3d(coords[i * 3], coords[i * 3 + 1], coords[i * 3 + 2] + gl.getCurrentZTranslation());
			gl.getGlut().glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, strings[i]);
		}
		gl.setObjectLighting(previous);
		gl.exitRasterTextMode();

	}

	/**
	 * Gets the.
	 *
	 * @param data
	 *            the data
	 * @param x0
	 *            the x 0
	 * @param y0
	 *            the y 0
	 * @return the double
	 */
	double get(final int cols, final int rows, final double[] data, final int x0, final int y0) {
		var x = x0 < 0 ? 0 : x0 > cols - 1 ? cols - 1 : x0;
		var y = y0 < 0 ? 0 : y0 > rows - 1 ? rows - 1 : y0;
		return data[y * cols + x];
	}

}