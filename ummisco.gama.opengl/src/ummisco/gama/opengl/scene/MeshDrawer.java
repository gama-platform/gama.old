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
package ummisco.gama.opengl.scene;

import static com.jogamp.common.nio.Buffers.newDirectDoubleBuffer;

import java.awt.Color;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.Locale;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;

import msi.gama.common.geometry.ICoordinates;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.matrix.IField;
import msi.gaml.statements.draw.MeshDrawingAttributes;
import ummisco.gama.opengl.OpenGL;

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
			if (object.getAttributes().getScale() != null) {
				double zScale = object.getAttributes().getScale();
				gl.scaleBy(1, 1, zScale);
			}

			FieldMeshDrawer hmap = new FieldMeshDrawer(object);
			hmap.drawOn(gl);
		} finally {
			gl.popMatrix();
		}
	}

	private class FieldMeshDrawer {
		final double[] data;
		private DoubleBuffer vertexBuffer, normalBuffer, texBuffer, colorBuffer, lineColorBuffer;
		private final int cols, rows;
		private final double cx, cy, minHeight, maxHeight;
		private IntBuffer indexBuffer;
		private final boolean wireframe, grayscale, triangles, withText;
		private final Color line, fill;
		private final boolean outputsTextures;
		private final boolean outputsColors;
		private final boolean outputsLines;
		private Double noData;
		double[] normals = { 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1 };

		public FieldMeshDrawer(final MeshObject object) {
			MeshDrawingAttributes attributes = object.getAttributes();
			this.cols = (int) attributes.getXYDimension().x;
			this.rows = (int) attributes.getXYDimension().y;
			data = clone(cols, rows, object.getObject(), true, attributes.isSmooth());
			noData = attributes.getNoDataValue();
			if (noData == null) { noData = object.getObject().getNoData(null); }
			double[] minMax = object.getObject().getMinMax(null);
			maxHeight = minMax[1];
			minHeight = minMax[0];
			this.cx = attributes.getCellSize().x;
			this.cy = attributes.getCellSize().y;
			this.wireframe = attributes.isEmpty();
			this.grayscale = attributes.isGrayscaled();
			this.withText = attributes.isWithText();
			this.line = attributes.getBorder();
			this.fill = attributes.getColor();
			this.triangles = attributes.isTriangulated();

			outputsTextures = attributes.isTextured() && !grayscale && !wireframe;
			outputsColors = (fill != null || grayscale) && !wireframe;
			outputsLines = wireframe || line != null;

			initializeBuffers();
			fillBuffers();
			closeBuffers();
		}

		private void initializeBuffers() {
			final int length = cols * rows;
			final int lengthM1 = (cols - 1) * (rows - 1);
			vertexBuffer = newDirectDoubleBuffer(triangles ? length * 3 : lengthM1 * 12);
			normalBuffer = newDirectDoubleBuffer(triangles ? length * 3 : lengthM1 * 12);
			if (triangles) { indexBuffer = Buffers.newDirectIntBuffer(lengthM1 * 6); }
			if (outputsLines) {
				lineColorBuffer = Buffers.newDirectDoubleBuffer(triangles ? length * 3 : lengthM1 * 12);
			}
			if (outputsTextures) { texBuffer = newDirectDoubleBuffer(triangles ? length * 2 : lengthM1 * 8); }
			if (outputsColors) { colorBuffer = Buffers.newDirectDoubleBuffer(triangles ? length * 3 : lengthM1 * 12); }
		}

		private void colorize(final double z, final int x, final int y) {
			// Outputs either a texture coordinate or the color of the vertex
			if (outputsTextures) {
				texBuffer.put((double) x / cols);
				texBuffer.put((double) y / rows);
			}
			if (outputsColors) {
				double d = (z - minHeight) / (maxHeight - minHeight);
				if (fill != null && !grayscale) {
					colorBuffer.put(d * fill.getRed() / 255d);
					colorBuffer.put(d * fill.getGreen() / 255d);
					colorBuffer.put(d * fill.getBlue() / 255d);
				} else {
					colorBuffer.put(d);
					colorBuffer.put(d);
					colorBuffer.put(d);
				}
			}
			// If the line color is specified, outputs it
			if (outputsLines) {
				Color c = line == null ? fill == null ? Color.black : fill : line;
				lineColorBuffer.put(c.getRed() / 255d);
				lineColorBuffer.put(c.getGreen() / 255d);
				lineColorBuffer.put(c.getBlue() / 255d);
			}

		}

		public void fillBuffers() {
			if (triangles) {
				int[] normalCount = new int[data.length];
				int[] ix = new int[3];
				ICoordinates surface = ICoordinates.ofLength(3);
				GamaPoint normal = new GamaPoint();

				for (int x = 0; x < cols; ++x) {
					double x1 = x * cx;
					for (int y = 0; y < rows; ++y) {
						double z = data[y * cols + x];
						// if (IntervalSize.isZeroWidth(min, max))

						// Outputs the 3 ordinates of the vertex
						vertexBuffer.put(x1);

						vertexBuffer.put(-y * cy);
						vertexBuffer.put(z);
						colorize(z, x, y);
						// Builds the index buffer: references vertices in the vertex buffer to avoid duplications
						if (x == 0 || y == 0 || z == noData) { continue; }
						buildIndexesAndNormals(normalCount, ix, normal, surface, x, y);
					}
				}
				normalize(normalCount);
			} else {
				for (int x = 0; x < cols - 1; ++x) {
					double x1 = x * cx, x2 = (x + 1) * cx;
					for (int y = 0; y < rows - 1; ++y) {
						double y1 = -y * cy, y2 = -(y + 1) * cy, z = data[y * cols + x];
						if (z == noData) { continue; }
						vertexBuffer.put(new double[] { x1, y1, z, x2, y1, z, x2, y2, z, x1, y2, z });
						colorize(z, x, y);
						colorize(z, x + 1, y);
						colorize(z, x + 1, y + 1);
						colorize(z, x, y + 1);
						normalBuffer.put(normals);
					}
				}
			}
		}

		private void closeBuffers() {
			if (outputsTextures) { texBuffer = texBuffer.flip(); }
			if (outputsColors) { colorBuffer = colorBuffer.flip(); }
			if (outputsLines) { lineColorBuffer = lineColorBuffer.flip(); }
			vertexBuffer = vertexBuffer.flip();
			if (triangles) { indexBuffer = indexBuffer.flip(); }
			// AD No need to flip the buffer because triangulation always uses indexed put
			if (!triangles) { normalBuffer = normalBuffer.flip(); }
		}

		private void buildIndexesAndNormals(final int[] normalCount, final int[] ix, final GamaPoint normal,
				final ICoordinates surface, final int x, final int y) {
			// Shared between triangles
			indexBuffer.put(ix[0] = y + x * rows);
			indexBuffer.put(ix[1] = ix[0] - 1);
			indexBuffer.put(ix[2] = ix[0] - rows);
			// Normals are computed for each triplet (triangle) or rectangle

			surface.setTo(vertexBuffer.get(ix[0] * 3), vertexBuffer.get(ix[0] * 3 + 1), vertexBuffer.get(ix[0] * 3 + 2),
					vertexBuffer.get(ix[1] * 3), vertexBuffer.get(ix[1] * 3 + 1), vertexBuffer.get(ix[1] * 3 + 2),
					vertexBuffer.get(ix[2] * 3), vertexBuffer.get(ix[2] * 3 + 1), vertexBuffer.get(ix[2] * 3 + 2))
					.getNormal(false, 1, normal);

			for (int i : ix) {
				int i3 = i * 3;
				normalBuffer.put(i3, normalBuffer.get(i3) + normal.x);
				normalBuffer.put(i3 + 1, normalBuffer.get(i3 + 1) + normal.y);
				normalBuffer.put(i3 + 2, normalBuffer.get(i3 + 2) + normal.z);
				normalCount[i]++;
			}

			indexBuffer.put(ix[0] = ix[2] - 1);
			indexBuffer.put(ix[2]);
			indexBuffer.put(ix[1]);
			surface.setTo(vertexBuffer.get(ix[0] * 3), vertexBuffer.get(ix[0] * 3 + 1), vertexBuffer.get(ix[0] * 3 + 2),
					vertexBuffer.get(ix[2] * 2), vertexBuffer.get(ix[2] * 3 + 1), vertexBuffer.get(ix[2] * 3 + 2),
					vertexBuffer.get(ix[1] * 3), vertexBuffer.get(ix[1] * 3 + 1), vertexBuffer.get(ix[1] * 3 + 2))
					.getNormal(false, 1, normal);
			for (int i : ix) {
				int i3 = i * 3;
				normalBuffer.put(i3, normalBuffer.get(i3) + normal.x);
				normalBuffer.put(i3 + 1, normalBuffer.get(i3 + 1) + normal.y);
				normalBuffer.put(i3 + 2, normalBuffer.get(i3 + 2) + normal.z);
				normalCount[i]++;
			}
		}

		// Rescan the normals and perform the average function on them
		private void normalize(final int[] normalCount) {
			for (int i = 0, i2 = 0; i < normalCount.length && i2 < normalBuffer.limit() - 2; i++, i2 += 3) {
				normalBuffer.put(i2, normalBuffer.get(i2) / normalCount[i]);
				normalBuffer.put(i2 + 1, normalBuffer.get(i2 + 1) / normalCount[i]);
				normalBuffer.put(i2 + 2, normalBuffer.get(i2 + 2) / normalCount[i]);
			}
		}

		public void drawOn(final OpenGL openGL) {
			if (vertexBuffer.limit() == 0) return;
			final GL2 gl = openGL.getGL();
			openGL.enable(GL2.GL_VERTEX_ARRAY);
			openGL.enable(GL2.GL_NORMAL_ARRAY);
			if (outputsTextures) {
				openGL.enable(GL2.GL_TEXTURE_COORD_ARRAY);
			} else {
				gl.glDisable(GL.GL_TEXTURE_2D);
			}
			if (outputsColors) { openGL.enable(GL2.GL_COLOR_ARRAY); }
			try {
				gl.glVertexPointer(3, GL2.GL_DOUBLE, 0, vertexBuffer);
				gl.glNormalPointer(GL2.GL_DOUBLE, 0, normalBuffer);

				if (outputsTextures) { gl.glTexCoordPointer(2, GL2.GL_DOUBLE, 0, texBuffer); }
				if (outputsColors) { gl.glColorPointer(3, GL2.GL_DOUBLE, 0, colorBuffer); }

				if (!wireframe) {
					if (triangles) {
						gl.glDrawElements(GL.GL_TRIANGLES, indexBuffer.limit(), GL2.GL_UNSIGNED_INT, indexBuffer);
					} else {
						// AD Warning. GL_QUADS have been deprecated and removed from OpenGL 4.0
						gl.glDrawArrays(GL2.GL_QUADS, 0, vertexBuffer.limit() / 3);
					}
				}
				if (outputsLines) {
					if (!outputsColors) { openGL.enable(GL2.GL_COLOR_ARRAY); }
					gl.glColorPointer(3, GL2.GL_DOUBLE, 0, lineColorBuffer);
					gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
					if (triangles) {
						gl.glDrawElements(GL.GL_TRIANGLES, indexBuffer.limit(), GL2.GL_UNSIGNED_INT, indexBuffer);
					} else {
						// AD Warning. GL_QUADS have been deprecated and removed from OpenGL 4.0
						gl.glDrawArrays(GL2.GL_QUADS, 0, vertexBuffer.limit() / 3);
					}
					gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
				}
			} finally {
				openGL.disable(GL2.GL_VERTEX_ARRAY);
				openGL.disable(GL2.GL_NORMAL_ARRAY);
				if (outputsTextures) { openGL.disable(GL2.GL_TEXTURE_COORD_ARRAY); }
				if (outputsColors || outputsLines) { openGL.disable(GL2.GL_COLOR_ARRAY); }

			}
			if (withText) { drawLabels(openGL); }

		}

		public void drawLabels(final OpenGL gl) {
			// Draw gridvalue as text inside each cell
			gl.setCurrentColor(Color.black);
			final String[] strings = new String[data.length];
			final double[] coords = new double[strings.length * 3];
			for (int i = 0, c = 0; i < cols; i++) {
				final double stepX = i * cx;
				for (int j = 0; j < rows; j++, c += 3) {
					final double stepY = j * cy;
					final double gridValue = data[j * cols + i];
					strings[j * cols + i] = String.format(Locale.US, "%.2f", gridValue);
					coords[c] = stepX + cx / 2;
					coords[c + 1] = -(stepY + cy / 2);
					coords[c + 2] = gridValue;
				}
			}
			gl.beginRasterTextMode();
			final boolean previous = gl.setLighting(false);
			for (int i = 0; i < strings.length; i++) {
				gl.getGL().glRasterPos3d(coords[i * 3], coords[i * 3 + 1],
						coords[i * 3 + 2] + gl.getCurrentZTranslation());
				gl.getGlut().glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, strings[i]);
			}
			gl.setLighting(previous);
			gl.exitRasterTextMode();

		}

		double[] clone(final int width, final int height, final IField field, final boolean smoothEdges,
				final boolean smooth) {
			double[] data = field.getMatrix();

			if (!smooth) return data;
			double[] result = data.clone();
			// Temporary values for traversing single dimensional arrays
			int x = 0;
			int z = 0;
			long widthClamp = smoothEdges ? width : width - 1;
			long heightClamp = smoothEdges ? height : height - 1;
			int bounds = width * height;
			for (z = smoothEdges ? 0 : 1; z < heightClamp; ++z) {
				for (x = smoothEdges ? 0 : 1; x < widthClamp; ++x) {
					// Sample a 3x3 filtering grid based on surrounding neighbors

					double value = 0.0f;
					double cellAverage = 1.0f;

					// Sample top row
					if (x - 1 + (z - 1) * width >= 0 && x - 1 + (z - 1) * width < bounds) {
						value += data[x - 1 + (z - 1) * width];
						++cellAverage;
					}
					if (x - 0 + (z - 1) * width >= 0 && x - 0 + (z - 1) * width < bounds) {
						value += data[x + (z - 1) * width];
						++cellAverage;
					}
					if (x + 1 + (z - 1) * width >= 0 && x + 1 + (z - 1) * width < bounds) {
						value += data[x + 1 + (z - 1) * width];
						++cellAverage;
					}
					// Sample middle row
					if (x - 1 + (z - 0) * width >= 0 && x - 1 + (z - 0) * width < bounds) {
						value += data[x - 1 + z * width];
						++cellAverage;
					}
					// Sample center point (will always be in bounds)
					value += data[x + z * width];

					if (x + 1 + (z - 0) * width >= 0 && x + 1 + (z - 0) * width < bounds) {
						value += data[x + 1 + z * width];
						++cellAverage;
					}
					// Sample bottom row
					if (x - 1 + (z + 1) * width >= 0 && x - 1 + (z + 1) * width < bounds) {
						value += data[x - 1 + (z + 1) * width];
						++cellAverage;
					}
					if (x - 0 + (z + 1) * width >= 0 && x - 0 + (z + 1) * width < bounds) {
						value += data[x + (z + 1) * width];
						++cellAverage;
					}
					if (x + 1 + (z + 1) * width >= 0 && x + 1 + (z + 1) * width < bounds) {
						value += data[x + 1 + (z + 1) * width];
						++cellAverage;
					}
					result[x + z * width] = value / cellAverage;
				}
			}
			return result;
		}
	}

	@Override
	public void dispose() {}
}