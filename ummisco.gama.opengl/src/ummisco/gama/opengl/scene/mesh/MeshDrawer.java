/*******************************************************************************************************
 *
 * ummisco.gama.opengl.scene.FieldDrawer.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.scene.mesh;

import static com.jogamp.common.nio.Buffers.newDirectDoubleBuffer;
import static com.jogamp.common.nio.Buffers.newDirectIntBuffer;

import java.awt.Color;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.Locale;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.fixedfunc.GLPointerFunc;
import com.jogamp.opengl.util.gl2.GLUT;

import msi.gama.common.geometry.ICoordinates;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.matrix.IField;
import msi.gaml.statements.draw.IMeshColorProvider;
import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.scene.ObjectDrawer;

/**
 *
 * The class FieldDrawer.
 *
 * @author grignard + A. Drogoul 2021
 * @since 15 mai 2013
 *
 */
public class MeshDrawer extends ObjectDrawer<MeshObject> {

	// ARRAYS
	// The attribute holding the data
	private double[] data;
	// The attribute holding the position of the vertex indices (in case of no_data)
	private int[] realIndexes;

	// BUFFERS
	// The buffers for vertices, normals, textures, colors, line colors
	private DoubleBuffer vertexBuffer, normalBuffer, texBuffer, colorBuffer, lineColorBuffer;
	// The buffer holding the indices to the points to draw
	private IntBuffer indexBuffer;

	// The number of columns and rows of the data
	private int cols, rows;
	// The widht and height of each cell in world coordinates; the minimal and maximal values found in the data
	private double cx, cy, min, max;
	// The value representing the absence of data
	private double noData;

	// FLAGS
	// Flags indicating if the data is to be drawn in wireframe, in grayscale, as triangles and with the value
	private boolean triangles, withText;
	// Flags indicating what to output: textures, colors, lines ?
	private boolean outputsTextures, outputsColors, outputsLines;

	// COLORS
	// An array holding the three components of the line color
	private double[] lineColor;
	// An array used for the transfer of colors from the color provider
	double[] rgb = new double[3];
	// The provider of color for the vertices
	private IMeshColorProvider fill;
	// Alpha
//	private final double layerAlpha;

	// NORMALS
	// The normals used for quads drawing
	final double[] quadNormals = { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 };
	// The temporary coordinate sequence used to hold vertices to compute normals
	final ICoordinates surface = ICoordinates.ofLength(9);
	// The temporary transfer value for the normal
	final GamaPoint normal = new GamaPoint();

	public MeshDrawer(final OpenGL gl) {
		super(gl);
//		layerAlpha = gl.getCurrentObjectAlpha();
	}

	@Override
	protected void _draw(final MeshObject object) {

		var attributes = object.getAttributes();

		var minMax = object.getObject().getMinMax(null);
		max = minMax[1];
		min = minMax[0];
		data = smooth(object.getObject().getMatrix(), attributes.getSmooth());
		this.cols = (int) attributes.getXYDimension().x;
		this.rows = (int) attributes.getXYDimension().y;
		boolean grayscale = attributes.isGrayscaled();
		Color line = attributes.getBorder();
		this.fill = attributes.getColorProvider();
		if (line == null) {
			lineColor = fill != null ? fill.getColor(0, data[0], min, max, null) : new double[] { 0, 0, 0 };
		} else {
			lineColor = new double[] { line.getRed() / 255d, line.getGreen() / 255d, line.getBlue() / 255d };
		}
		outputsTextures = gl.isTextured() && !grayscale;
		outputsColors = (fill != null || grayscale) && !gl.isWireframe();
		outputsLines = gl.isWireframe() || line != null;
		noData = attributes.getNoDataValue();
		if (noData == IField.NO_NO_DATA) { noData = object.getObject().getNoData(null); }

		this.cx = attributes.getCellSize().x;
		this.cy = attributes.getCellSize().y;

		this.withText = attributes.isWithText();
		this.triangles = attributes.isTriangulated();

		initializeBuffers();
		fillBuffers();
		finalizeBuffers();
		gl.pushMatrix();
		try {
			applyTranslation(object);
			if (object.getAttributes().getScale() != null) {
				double zScale = object.getAttributes().getScale();
				gl.scaleBy(1, 1, zScale);
			}
			drawField();
		} finally {
			gl.popMatrix();
		}
	}

	private void initializeBuffers() {
		final var length = cols * rows;
		int previous = realIndexes == null ? 0 : realIndexes.length;
		if (length > previous) {
			realIndexes = new int[length];
			final var lengthM1 = (cols - 1) * (rows - 1);
			vertexBuffer = newDirectDoubleBuffer(triangles ? length * 3 : lengthM1 * 12);
			normalBuffer = newDirectDoubleBuffer(triangles ? length * 3 : lengthM1 * 12);
			indexBuffer = newDirectIntBuffer(lengthM1 * 6);
			if (outputsLines) { lineColorBuffer = newDirectDoubleBuffer(triangles ? length * 3 : lengthM1 * 12); }
			if (outputsTextures) { texBuffer = newDirectDoubleBuffer(triangles ? length * 2 : lengthM1 * 8); }
			if (outputsColors) { colorBuffer = newDirectDoubleBuffer(triangles ? length * 3 : lengthM1 * 12); }
		} else {
			vertexBuffer.clear();
			normalBuffer.clear();
			indexBuffer.clear();
			if (lineColorBuffer != null) { lineColorBuffer.clear(); }
			if (texBuffer != null) { texBuffer.clear(); }
			if (colorBuffer != null) { colorBuffer.clear(); }
		}

	}

	public void fillBuffers() {
		if (triangles) {
			var realIndex = 0;
			for (var j = 0; j < rows; j++) {
				var y = j * cy;
				for (var i = 0; i < cols; i++) {
					var x = i * cx;
					var index = j * cols + i;
					var z = data[index];
					realIndexes[index] = z == noData ? -1 : realIndex++;
					if (z == noData) { continue; }
					vertexBuffer.put(x).put(-y).put(z);
					colorize(z, i, j);
					surface.setTo(x - cx, y - cy, get(data, i - 1, j - 1), x, y - cy, get(data, i, j - 1), x + cx,
							y - cy, get(data, i + 1, j - 1), x + cx, y, get(data, i + 1, j), x + cx, y + cy,
							get(data, i + 1, j + 1), x, y + cy, get(data, i, j + 1), x - cx, y + cy,
							get(data, i - 1, j + 1), x - cx, y, get(data, i - 1, j), x - cx, y - cy,
							get(data, i - 1, j - 1)).getNormal(true, 1, normal);
					normalBuffer.put(normal.x).put(normal.y).put(normal.z);
					if (j > 0 && i > 0) {
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
		} else {
			int index = 0;
			for (var i = 0; i < cols - 1; ++i) {
				double x1 = i * cx, x2 = (i + 1) * cx;
				for (var j = 0; j < rows - 1; ++j) {
					double y1 = -j * cy, y2 = -(j + 1) * cy, z = data[j * cols + i];
					if (z == noData) { continue; }
					vertexBuffer.put(new double[] { x1, y1, z, x2, y1, z, x2, y2, z, x1, y2, z });
					colorize(z, i, j);
					colorize(z, i + 1, j);
					colorize(z, i + 1, j + 1);
					colorize(z, i, j + 1);
					normalBuffer.put(quadNormals);
					indexBuffer.put(index).put(index + 1).put(index + 3);
					indexBuffer.put(index + 1).put(index + 2).put(index + 3);
					index += 4;
				}
			}
		}
	}

	private void colorize(final double z, final int x, final int y) {
		// Outputs either a texture coordinate or the color of the vertex
		if (outputsTextures) { texBuffer.put((double) x / cols).put((double) y / rows); }
		if (outputsColors) { colorBuffer.put(fill.getColor(y * cols + x, z, min, max, rgb)); }
		// If the line color is specified, outputs it
		if (outputsLines) { lineColorBuffer.put(lineColor); }
	}

	private void finalizeBuffers() {
		if (outputsTextures) { texBuffer.flip(); }
		if (outputsColors) { colorBuffer.flip(); }
		if (outputsLines) { lineColorBuffer.flip(); }
		vertexBuffer.flip();
		indexBuffer.flip();
		normalBuffer.flip();
	}

	public void drawFieldFallback() {
		if (vertexBuffer.limit() == 0) return;
		final var ogl = gl.getGL();
		// Forcing alpha
		ogl.glBlendColor(0.0f, 0.0f, 0.0f, (float) gl.getCurrentObjectAlpha());
		ogl.glBlendFunc(GL2ES2.GL_CONSTANT_ALPHA, GL2ES2.GL_ONE_MINUS_CONSTANT_ALPHA);
		gl.beginDrawing(GL.GL_TRIANGLES);
		for (var index = 0; index < indexBuffer.limit(); index++) {
			var i = indexBuffer.get(index);
			int one = i * 3, two = i * 3 + 1, three = i * 3 + 2;
			if (!gl.isWireframe() && outputsColors) {
				// TODO Bug when using a gradient: some color components are outside the range
				gl.setCurrentColor(colorBuffer.get(one), colorBuffer.get(two), colorBuffer.get(three), 1);
			}
			if (outputsTextures) { gl.outputTexCoord(texBuffer.get(i * 2), texBuffer.get(i * 2 + 1)); }
			gl.outputNormal(normalBuffer.get(one), normalBuffer.get(two), normalBuffer.get(three));
			ogl.glVertex3d(vertexBuffer.get(one), vertexBuffer.get(two), vertexBuffer.get(three));
		}
		if (outputsLines) {
			ogl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
			for (var index = 0; index < indexBuffer.limit(); index++) {
				var i = indexBuffer.get(index);
				gl.setCurrentColor(lineColorBuffer.get(i * 3), lineColorBuffer.get(i * 3 + 1),
						lineColorBuffer.get(i + 1), 1);
				gl.outputVertex(vertexBuffer.get(i * 3), vertexBuffer.get(i * 3 + 1), vertexBuffer.get(i * 3 + 2));
			}
			ogl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
		}
		gl.endDrawing();
		ogl.glBlendColor(0.0f, 0.0f, 0.0f, 0.0f);
		ogl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
	}

	public void drawField() {
		// AD - See issue #3125
		if (true || gl.isRenderingKeystone()) {
			drawFieldFallback();
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
			if (outputsColors) { ogl.glColorPointer(3, GL2GL3.GL_DOUBLE, 0, colorBuffer); }

			if (!gl.isWireframe()) {
				ogl.glDrawElements(GL.GL_TRIANGLES, indexBuffer.limit(), GL.GL_UNSIGNED_INT, indexBuffer);
			}
			if (outputsLines) {
				if (!outputsColors) { gl.enable(GLPointerFunc.GL_COLOR_ARRAY); }
				ogl.glColorPointer(3, GL2GL3.GL_DOUBLE, 0, lineColorBuffer);
				ogl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
				ogl.glDrawElements(GL.GL_TRIANGLES, indexBuffer.limit(), GL.GL_UNSIGNED_INT, indexBuffer);
				ogl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
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
		if (withText) { drawLabels(gl); }

	}

	public void drawLabels(final OpenGL gl) {
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
		final var previous = gl.setLighting(false);
		for (var i = 0; i < strings.length; i++) {
			gl.getGL().glRasterPos3d(coords[i * 3], coords[i * 3 + 1], coords[i * 3 + 2] + gl.getCurrentZTranslation());
			gl.getGlut().glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, strings[i]);
		}
		gl.setLighting(previous);
		gl.exitRasterTextMode();

	}

	double get(final double[] data, final int x0, final int y0) {
		var x = x0 < 0 ? 0 : x0 > cols - 1 ? cols - 1 : x0;
		var y = y0 < 0 ? 0 : y0 > rows - 1 ? rows - 1 : y0;
		return data[y * cols + x];
	}

	double[] smooth(final double[] data, final int passes) {
		if (passes == 0) return data;
		var input = data;
		var output = data.clone();
		for (var i = 0; i < passes; i++) {
			for (var y = 0; y < rows; ++y) {
				for (var x = 0; x < cols; ++x) {
					double z00 = get(input, x - 1, y - 1), z01 = get(input, x - 1, y - 1),
							z02 = get(input, x + 1, y - 1), z03 = get(input, x - 1, y), z = get(input, x, y),
							z05 = get(input, x + 1, y), z06 = get(input, x - 1, y + 1), z07 = get(input, x, y + 1),
							z08 = get(input, x + 1, y + 1);
					if (z00 == noData || z01 == noData || z02 == noData || z03 == noData || z == noData || z05 == noData
							|| z06 == noData || z07 == noData || z08 == noData) {
						continue;
					}
					// Sample a 3x3 filtering grid based on surrounding neighbors
					output[x + y * cols] = (z00 + z01 + z02 + z03 + z + z05 + z06 + z07 + z08) / 9d;
				}
			}
			input = output;
		}
		return output;
	}

	@Override
	public void dispose() {}
}