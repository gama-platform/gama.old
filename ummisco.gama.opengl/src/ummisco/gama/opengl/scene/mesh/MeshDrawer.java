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
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL2ES3;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.fixedfunc.GLPointerFunc;
import com.jogamp.opengl.util.gl2.GLUT;

import msi.gama.common.geometry.ICoordinates;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.matrix.IField;
import msi.gaml.statements.draw.IMeshColorProvider;
import msi.gaml.statements.draw.MeshDrawingAttributes;
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

	public MeshDrawer(final OpenGL gl) {
		super(gl);
	}

	@Override
	protected void _draw(final MeshObject object) {
		try {
			gl.pushMatrix();
			applyTranslation(object);
			if (object.getAttributes().getScale() != null) {
				double zScale = object.getAttributes().getScale();
				gl.scaleBy(1, 1, zScale);
			}

			var hmap = new FieldMeshDrawer(object);
			hmap.drawOn(gl);
		} finally {
			gl.popMatrix();
		}
	}

	private class FieldMeshDrawer {
		// ARRAYS
		// The attribute holding the data
		private final double[] data;
		// The attribute holding the position of the vertex indices (in case of no_data)
		private final int[] realIndexes;

		// BUFFERS
		// The buffers for vertices, normals, textures, colors, line colors and the normals to display (if debugging)
		private DoubleBuffer vertexBuffer, normalBuffer, texBuffer, colorBuffer, lineColorBuffer, displayNormalBuffer;
		// The buffer holding the indices to the points to draw
		private IntBuffer indexBuffer;

		// The number of columns and rows of the data
		private final int cols, rows;
		// The widht and height of each cell in world coordinates; the minimal and maximal values found in the data
		private final double cx, cy, min, max;
		// The value representing the absence of data
		private double noData;

		// FLAGS
		// Flags indicating if the data is to be drawn in wireframe, in grayscale, as triangles and with the value
		private final boolean wireframe, grayscale, triangles, withText;
		// Flags indicating what to output: textures, colors, lines, normals ?
		private final boolean outputsTextures, outputsColors, outputsLines, outputsNormals = false;

		// COLORS
		// An array holding the three components of the line color
		private final double[] lineColor;
		// An array used for the transfer of colors from the color provider
		double[] rgb = new double[3];
		// The provider of color for the vertices
		private final IMeshColorProvider fill;

		// NORMALS
		// The normals used for quads drawing
		final double[] quadNormals = { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 };
		// The temporary coordinate sequence used to hold vertices to compute normals
		final ICoordinates surface = ICoordinates.ofLength(9);
		// The temporary transfer value for the normal
		final GamaPoint normal = new GamaPoint();

		public FieldMeshDrawer(final MeshObject object) {
			var attributes = object.getAttributes();
			this.cols = (int) attributes.getXYDimension().x;
			this.rows = (int) attributes.getXYDimension().y;

			data = smooth(object.getObject().getMatrix(), attributes.getSmooth());
			noData = attributes.getNoDataValue();
			if (noData == IField.NO_NO_DATA) { noData = object.getObject().getNoData(null); }
			realIndexes = new int[cols * rows];

			var minMax = object.getObject().getMinMax(null);
			max = minMax[1];
			min = minMax[0];
			this.cx = attributes.getCellSize().x;
			this.cy = attributes.getCellSize().y;
			this.wireframe = attributes.isEmpty();
			this.grayscale = attributes.isGrayscaled();
			this.withText = attributes.isWithText();
			Color line = attributes.getBorder();
			this.fill = attributes.getColorProvider();
			if (line == null) {
				lineColor = fill != null ? fill.getColor(0, data[0], min, max, null) : new double[] { 0, 0, 0 };
			} else {
				lineColor = new double[] { line.getRed() / 255d, line.getGreen() / 255d, line.getBlue() / 255d };
			}
			this.triangles = attributes.isTriangulated();
			outputsTextures = attributes.isTextured() && !grayscale && !wireframe;
			outputsColors = (fill != null || grayscale) && !wireframe;
			outputsLines = wireframe || line != null;

			initializeBuffers();
			fillBuffers();
			finalizeBuffers();
		}

		private void initializeBuffers() {
			final var length = cols * rows;
			final var lengthM1 = (cols - 1) * (rows - 1);
			vertexBuffer = newDirectDoubleBuffer(triangles ? length * 3 : lengthM1 * 12);
			normalBuffer = newDirectDoubleBuffer(triangles ? length * 3 : lengthM1 * 12);
			if (triangles && outputsNormals) { displayNormalBuffer = newDirectDoubleBuffer(length * 6); }
			if (triangles) { indexBuffer = newDirectIntBuffer(lengthM1 * 6); }
			if (outputsLines) { lineColorBuffer = newDirectDoubleBuffer(triangles ? length * 3 : lengthM1 * 12); }
			if (outputsTextures) { texBuffer = newDirectDoubleBuffer(triangles ? length * 2 : lengthM1 * 8); }
			if (outputsColors) { colorBuffer = newDirectDoubleBuffer(triangles ? length * 3 : lengthM1 * 12); }
		}

		private void colorize(final double z, final int x, final int y) {
			// Outputs either a texture coordinate or the color of the vertex
			if (outputsTextures) { texBuffer.put((double) x / cols).put((double) y / rows); }
			if (outputsColors) { colorBuffer.put(fill.getColor(y * cols + x, z, min, max, rgb)); }
			// If the line color is specified, outputs it
			if (outputsLines) { lineColorBuffer.put(lineColor); }
		}

		double get(final int x0, final int y0) {
			var x = x0 < 0 ? 0 : x0 > cols - 1 ? cols - 1 : x0;
			var y = y0 < 0 ? 0 : y0 > rows - 1 ? rows - 1 : y0;
			return data[y * cols + x];
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
						if (z == noData) {
							realIndexes[index] = -1;
							continue;
						} else {
							realIndexes[index] = realIndex++;
						}
						vertexBuffer.put(x).put(-y).put(z);
						colorize(z, i, j);
						surface.setTo(x - cx, y - cy, get(i - 1, j - 1), x, y - cy, get(i, j - 1), x + cx, y - cy,
								get(i + 1, j - 1), x + cx, y, get(i + 1, j), x + cx, y + cy, get(i + 1, j + 1), x,
								y + cy, get(i, j + 1), x - cx, y + cy, get(i - 1, j + 1), x - cx, y, get(i - 1, j),
								x - cx, y - cy, get(i - 1, j - 1)).getNormal(true, 1, normal);
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
				for (var x = 0; x < cols - 1; ++x) {
					double x1 = x * cx, x2 = (x + 1) * cx;
					for (var y = 0; y < rows - 1; ++y) {
						double y1 = -y * cy, y2 = -(y + 1) * cy, z = data[y * cols + x];
						if (z == noData) { continue; }
						vertexBuffer.put(new double[] { x1, y1, z, x2, y1, z, x2, y2, z, x1, y2, z });
						colorize(z, x, y);
						colorize(z, x + 1, y);
						colorize(z, x + 1, y + 1);
						colorize(z, x, y + 1);
						normalBuffer.put(quadNormals);
					}
				}
			}
		}

		private void finalizeBuffers() {
			if (outputsTextures) { texBuffer = texBuffer.flip(); }
			if (outputsColors) { colorBuffer = colorBuffer.flip(); }
			if (outputsLines) { lineColorBuffer = lineColorBuffer.flip(); }
			vertexBuffer = vertexBuffer.flip();
			if (triangles) { indexBuffer = indexBuffer.flip(); }
			normalBuffer = normalBuffer.flip();
			if (triangles && outputsNormals) {
				for (var i = 0; i < vertexBuffer.limit(); i += 3) {
					displayNormalBuffer.put(vertexBuffer.get(i)).put(vertexBuffer.get(i + 1))
							.put(vertexBuffer.get(i + 2));
					displayNormalBuffer.put(vertexBuffer.get(i) - 2 * normalBuffer.get(i))
							.put(vertexBuffer.get(i + 1) - 2 * normalBuffer.get(i + 1))
							.put(vertexBuffer.get(i + 2) - 2 * normalBuffer.get(i + 2));
				}
				displayNormalBuffer.flip();
			}
		}

		public void fixedPipelineFallback(final OpenGL openGL) {
			if (vertexBuffer.limit() == 0) return;
			final var gl = openGL.getGL();
			// Forcing alpha
			gl.glBlendColor(0.0f, 0.0f, 0.0f, (float) openGL.getCurrentObjectAlpha());
			gl.glBlendFunc(GL2ES2.GL_CONSTANT_ALPHA, GL2ES2.GL_ONE_MINUS_CONSTANT_ALPHA);
			if (triangles) {
				openGL.beginDrawing(GL.GL_TRIANGLES);
				for (var index = 0; index < indexBuffer.limit(); index++) {
					var i = indexBuffer.get(index);
					// System.out.println("Index " + index + " -> " + i);
					if (!wireframe && outputsColors) {
						openGL.setCurrentColor(colorBuffer.get(i * 3), colorBuffer.get(i * 3 + 1),
								colorBuffer.get(i * 3 + 2), 1);
					}
					if (outputsTextures) { openGL.outputTexCoord(texBuffer.get(i * 2), texBuffer.get(i * 2 + 1)); }
					openGL.outputNormal(normalBuffer.get(i * 3), normalBuffer.get(i * 3 + 1),
							normalBuffer.get(i * 3 + 2));
					// System.out.println("Point: " + vertexBuffer.get(i) + " " + vertexBuffer.get(i + 1) + " "
					// + vertexBuffer.get(i + 2));
					gl.glVertex3d(vertexBuffer.get(i * 3), vertexBuffer.get(i * 3 + 1), vertexBuffer.get(i * 3 + 2));
				}
				if (outputsLines) {
					gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
					for (var index = 0; index < indexBuffer.limit(); index++) {
						var i = indexBuffer.get(index);
						openGL.setCurrentColor(lineColorBuffer.get(i * 3), lineColorBuffer.get(i * 3 + 1),
								lineColorBuffer.get(i + 1), 1);
						openGL.outputVertex(vertexBuffer.get(i * 3), vertexBuffer.get(i * 3 + 1),
								vertexBuffer.get(i * 3 + 2));
					}
					gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
				}
			} else {
				openGL.beginDrawing(GL2ES3.GL_QUADS);
				for (var i = 0; i < vertexBuffer.limit(); i += 3) {
					if (!wireframe && outputsColors) {
						openGL.setCurrentColor(colorBuffer.get(i), colorBuffer.get(i + 1), colorBuffer.get(i + 2), 1);
					}
					if (outputsTextures) {
						openGL.outputTexCoord(texBuffer.get(2 * i / 3), texBuffer.get(2 * i / 3 + 1));
					}
					openGL.outputNormal(normalBuffer.get(i), normalBuffer.get(i + 1), normalBuffer.get(i + 2));
					gl.glVertex3d(vertexBuffer.get(i), vertexBuffer.get(i + 1), vertexBuffer.get(i + 2));
				}
				if (outputsLines) {
					gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
					gl.glColorPointer(3, GL2GL3.GL_DOUBLE, 0, lineColorBuffer);
					for (var i = 0; i < vertexBuffer.limit(); i += 3) {
						openGL.setCurrentColor(lineColorBuffer.get(i), lineColorBuffer.get(i + 1),
								lineColorBuffer.get(i + 2), 1);
						openGL.outputVertex(vertexBuffer.get(i), vertexBuffer.get(i + 1), vertexBuffer.get(i + 2));
					}
					gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
				}
			}
			openGL.endDrawing();
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		}

		public void drawOn(final OpenGL openGL) {
			// AD - See issue #3125
			if (openGL.isRenderingKeystone()) {
				fixedPipelineFallback(openGL);
				return;
			}
			if (vertexBuffer.limit() == 0) return;
			final var gl = openGL.getGL();
			// Forcing alpha
			gl.glBlendColor(0.0f, 0.0f, 0.0f, (float) openGL.getCurrentObjectAlpha());
			gl.glBlendFunc(GL2ES2.GL_CONSTANT_ALPHA, GL2ES2.GL_ONE_MINUS_CONSTANT_ALPHA);

			openGL.enable(GLPointerFunc.GL_VERTEX_ARRAY);
			openGL.enable(GLPointerFunc.GL_NORMAL_ARRAY);
			if (outputsTextures) {
				openGL.enable(GLPointerFunc.GL_TEXTURE_COORD_ARRAY);
			} else {
				gl.glDisable(GL.GL_TEXTURE_2D);
			}
			if (outputsColors) { openGL.enable(GLPointerFunc.GL_COLOR_ARRAY); }
			try {
				gl.glVertexPointer(3, GL2GL3.GL_DOUBLE, 0, vertexBuffer);
				gl.glNormalPointer(GL2GL3.GL_DOUBLE, 0, normalBuffer);

				if (outputsTextures) { gl.glTexCoordPointer(2, GL2GL3.GL_DOUBLE, 0, texBuffer); }
				if (outputsColors) { gl.glColorPointer(3, GL2GL3.GL_DOUBLE, 0, colorBuffer); }

				if (!wireframe) {
					if (triangles) {
						gl.glDrawElements(GL.GL_TRIANGLES, indexBuffer.limit(), GL.GL_UNSIGNED_INT, indexBuffer);
					} else {
						// AD Warning. GL_QUADS have been deprecated and removed from OpenGL 4.0
						gl.glDrawArrays(GL2ES3.GL_QUADS, 0, vertexBuffer.limit() / 3);
					}
				}
				if (outputsLines) {
					if (!outputsColors) { openGL.enable(GLPointerFunc.GL_COLOR_ARRAY); }
					gl.glColorPointer(3, GL2GL3.GL_DOUBLE, 0, lineColorBuffer);
					gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
					if (triangles) {
						gl.glDrawElements(GL.GL_TRIANGLES, indexBuffer.limit(), GL.GL_UNSIGNED_INT, indexBuffer);
					} else {
						// AD Warning. GL_QUADS have been deprecated and removed from OpenGL 4.0
						gl.glDrawArrays(GL2ES3.GL_QUADS, 0, vertexBuffer.limit() / 3);
					}
					gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
				}

			} finally {
				openGL.disable(GLPointerFunc.GL_NORMAL_ARRAY);
				if (outputsTextures) { openGL.disable(GLPointerFunc.GL_TEXTURE_COORD_ARRAY); }
				if (outputsColors || outputsLines) { openGL.disable(GLPointerFunc.GL_COLOR_ARRAY); }

				if (triangles && outputsNormals) {
					openGL.setCurrentColor(Color.white);
					openGL.setLineWidth(3);
					gl.glVertexPointer(3, GL2GL3.GL_DOUBLE, 0, displayNormalBuffer);
					gl.glDrawArrays(GL.GL_LINES, 0, displayNormalBuffer.limit() / 3);
				}
				openGL.disable(GLPointerFunc.GL_VERTEX_ARRAY);
				// Putting back alpha to normal
				gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

			}
			if (withText) { drawLabels(openGL); }

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
				gl.getGL().glRasterPos3d(coords[i * 3], coords[i * 3 + 1],
						coords[i * 3 + 2] + gl.getCurrentZTranslation());
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
						// Sample a 3x3 filtering grid based on surrounding neighbors
						var value = get(input, x - 1, y - 1) + get(input, x, y - 1) + get(input, x + 1, y - 1)
								+ get(input, x - 1, y) + get(input, x, y) + get(input, x + 1, y)
								+ get(input, x - 1, y + 1) + get(input, x, y + 1) + get(input, x + 1, y + 1);
						output[x + y * cols] = value / 9d;
					}
				}
				input = output;
			}
			return output;
		}

	}

	@Override
	public void dispose() {}
}