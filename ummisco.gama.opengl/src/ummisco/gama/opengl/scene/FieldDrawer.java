/*******************************************************************************************************
 *
 * ummisco.gama.opengl.scene.FieldDrawer.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Locale;

import com.google.common.base.MoreObjects;
import com.google.common.primitives.Doubles;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;

import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.file.GamaImageFile;
import ummisco.gama.opengl.OpenGL;

/**
 *
 * The class DEMDrawer.
 *
 * @author grignard
 * @since 15 mai 2013
 *
 */
public class FieldDrawer extends ObjectDrawer<FieldObject> {

	// Working copies of coordinate sequences to limit excessive garbage
	final ICoordinates threePoints = GeometryUtils.GEOMETRY_FACTORY.getCoordinateSequenceFactory().create(3, 3);
	final ICoordinates fivePoints = GeometryUtils.GEOMETRY_FACTORY.getCoordinateSequenceFactory().create(5, 3);
	final ICoordinates fourPoints = GeometryUtils.GEOMETRY_FACTORY.getCoordinateSequenceFactory().create(4, 3);

	public FieldDrawer(final OpenGL gl) {
		super(gl);
	}

	@Override
	protected void _draw(final FieldObject demObj) {
		try {
			gl.pushMatrix();
			if (demObj.getObject() == null) {
				drawFromImage(demObj);
				return;
			}
			final double altFactor = MoreObjects.firstNonNull(demObj.getAttributes().getHeight(), 1.0);
			final double maxZ = Doubles.max(demObj.getObject());
			if (demObj.getAttributes().grayScaled) {
				gl.disableTextures();
			}
			if (demObj.getAttributes().triangulated) {
				drawAsTriangles(demObj, altFactor, maxZ);
			} else {
				drawAsRectangles(demObj, altFactor, maxZ);
			}
			if (demObj.getAttributes().withText && demObj.getObject() != null) {
				drawLabels(demObj, altFactor);
			}
		} finally {
			gl.popMatrix();
		}
	}

	public void drawLabels(final FieldObject demObj, final double altFactor) {
		final GamaPoint cellDim = demObj.getAttributes().getCellSize();
		final double columns = Math.floor(gl.getWorldWidth() / cellDim.x);
		final double rows = Math.floor(gl.getWorldHeight() / cellDim.y);
		// Draw gridvalue as text inside each cell
		gl.setCurrentColor(Color.black);
		final String[] strings = new String[demObj.getObject().length];
		final double[] coords = new double[strings.length * 3];
		for (int i = 0, c = 0; i < columns; i++) {
			final double stepX = i * cellDim.x;
			for (int j = 0; j < rows; j++, c += 3) {
				final double stepY = j * cellDim.y;
				final double gridValue = demObj.getObject()[(int) (j * columns + i)];
				strings[(int) (j * columns + i)] = String.format(Locale.US, "%.2f", gridValue);
				coords[c] = stepX + cellDim.x / 2;
				coords[c + 1] = -(stepY + cellDim.y / 2);
				coords[c + 2] = gridValue * altFactor + 1;
			}
		}
		gl.rasterText(strings, GLUT.BITMAP_TIMES_ROMAN_10, coords);

	}

	public void drawAsTriangles(final FieldObject demObj, final double altFactor, final double maxZ) {
		final GamaPoint cellDim = demObj.getAttributes().getCellSize();
		final double columns = Math.floor(gl.getWorldWidth() / cellDim.x);
		final double rows = Math.floor(gl.getWorldHeight() / cellDim.y);
		for (int i = 0; i < columns; i++) {
			final double x1 = i * cellDim.x;
			final double x2 = x1 + cellDim.x;
			for (int j = 0; j < rows; j++) {
				final double y1 = -j * cellDim.y;
				final double y2 = y1 - cellDim.y;
				double z1 = 0d, z2 = 0d, z3 = 0d, z4 = 0d;
				final double[] values = demObj.getObject();
				if (values != null) {
					z1 = Math.min(maxZ, Math.abs(values[(int) (j * columns + i)]));
					if (i < columns - 1 && j < rows - 1) {
						z2 = Math.min(maxZ, Math.abs(values[(int) ((j + 1) * columns + i)]));
						z3 = Math.min(maxZ, Math.abs(values[(int) ((j + 1) * columns + (i + 1))]));
						z4 = Math.min(maxZ, Math.abs(values[(int) (j * columns + (i + 1))]));
					} else if (j == (int) rows - 1 && i < columns - 1) {// Last rows
						z2 = z1;
						z3 = z4 = Math.min(maxZ, Math.abs(values[(int) (j * columns + (i + 1))]));
					} else if (i == (int) columns - 1 && j < rows - 1) {// Last cols
						z2 = z3 = Math.min(maxZ, Math.abs(values[(int) ((j + 1) * columns + i)]));
						z4 = z1;
					} else if (i == (int) columns - 1 && j == (int) rows - 1) { // last cell
						z2 = z3 = z4 = z1;
					}
				}
				fourPoints.setTo(x1, y1, z1 * altFactor, x1, y2, z2 * altFactor, x2, y1, z4 * altFactor, x2, y2,
						z3 * altFactor);
				gl.setNormal(fourPoints, true);
				final Color lineColor = demObj.getAttributes().getBorder();
				if (lineColor != null) {
					drawTriangleLines(fourPoints, lineColor);
				} else {
					if (demObj.getAttributes().grayScaled) {
						drawGrayScaledTriangle(maxZ, fourPoints);

					} else {
						drawTexturedTriangle(1 / columns, 1 / rows, i, j, fourPoints);
					}
				}
			}
		}
	}

	public void drawTexturedTriangle(final double w, final double h, final int i, final int j,
			final ICoordinates vertices) {
		final double xt = w * i, yt = h * j, xt2 = xt + w, yt2 = yt + h;
		final double[] texCoords3 = { xt2, yt, xt, yt2, xt, yt };
		threePoints.setTo(vertices.at(2), vertices.at(1), vertices.at(0));
		gl.setNormal(threePoints, true);
		gl.drawVertices(GL.GL_TRIANGLES, threePoints, 3, true, texCoords3);
		texCoords3[0] = xt;
		texCoords3[1] = yt2;
		texCoords3[2] = xt2;
		texCoords3[3] = yt;
		texCoords3[4] = xt2;
		texCoords3[5] = yt2;
		threePoints.setTo(vertices.at(1), vertices.at(2), vertices.at(3));
		gl.setNormal(threePoints, false);
		gl.drawVertices(GL.GL_TRIANGLES, threePoints, 3, false, texCoords3);
	}

	public void drawGrayScaledTriangle(final double maxZ, final ICoordinates vertices) {
		threePoints.setTo(vertices.at(2), vertices.at(1), vertices.at(0));
		gl.setCurrentColor(threePoints.averageZ() / maxZ);
		gl.drawSimpleShape(threePoints, 3, true, true, true, null);
		threePoints.setTo(vertices.at(1), vertices.at(2), vertices.at(3));
		gl.setCurrentColor(threePoints.averageZ() / maxZ);
		gl.drawSimpleShape(threePoints, 3, true, false, true, null);
	}

	public void drawTriangleLines(final ICoordinates vertices, final Color lineColor) {
		gl.setCurrentColor(lineColor);
		gl.drawClosedLine(vertices, -1);
	}

	public void drawAsRectangles(final FieldObject demObj, final double altFactor, final double maxZ) {
		final GamaPoint cellDim = demObj.getAttributes().getCellSize();
		final double columns = Math.floor(gl.getWorldWidth() / cellDim.x);
		final double rows = Math.floor(gl.getWorldHeight() / cellDim.y);
		for (int i = 0; i < columns; i++) {
			final double x1 = i * cellDim.x, x2 = x1 + cellDim.x;
			final double[] values = demObj.getObject();
			for (int j = 0; j < rows; j++) {
				final double y1 = -j * cellDim.y, y2 = y1 - cellDim.y;
				final double zValue = Math.min(Math.abs(values[(int) (j * columns + i)]), maxZ);
				final double scaledZ = zValue * altFactor;
				// Explicitly create a ring
				fivePoints.setTo(x1, y1, scaledZ, x2, y1, scaledZ, x2, y2, scaledZ, x1, y2, scaledZ, x1, y1, scaledZ);
				final Color lineColor = demObj.getAttributes().getBorder();
				if (lineColor != null) {
					gl.setCurrentColor(lineColor);
					gl.drawClosedLine(fivePoints, 4);
				} else {
					gl.setNormal(fivePoints, true);
					// _normal(fivePoints, true);
					if (demObj.getAttributes().grayScaled) {
						drawGrayScaledCell(maxZ, zValue, fivePoints);
					} else {
						drawTexturedCell(1 / columns, 1 / rows, i, j, fivePoints);
					}
				}
			}
		}
	}

	public void drawGrayScaledCell(final double maxZ, final double zValue, final ICoordinates vertices) {
		gl.setCurrentColor(zValue / maxZ);
		gl.drawSimpleShape(vertices, 4, true, true, false, null);
	}

	public void drawTexturedCell(final double w, final double h, final int i, final int j,
			final ICoordinates vertices) {
		final double[] texCoords = { w * i, h * j, w * (i + 1), h * j, w * (i + 1), h * (j + 1), w * i, h * (j + 1) };
		gl.drawVertices(GL2.GL_QUADS, vertices, 4, true, texCoords);
	}

	protected void drawFromImage(final FieldObject demObj) {
		int rows, cols;
		// final double vx, vy;
		double ts, tt, tw, th;
		// Not y-flipped
		final BufferedImage dem = getDirectImage(demObj, 1);
		rows = dem.getHeight() - 1;
		cols = dem.getWidth() - 1;
		ts = 1.0f / cols;
		tt = 1.0f / rows;
		final double altFactor = MoreObjects.firstNonNull(demObj.getAttributes().getHeight(), 1.0);
		final double centerX = gl.getWorldWidth() / 2;
		final double centerY = gl.getWorldHeight() / 2;
		tw = 2 * centerX / cols;
		th = 2 * centerY / rows;
		gl.pushMatrix();
		gl.translateBy(centerX, -centerY, 0);
		final double[] texCoords = new double[4 * (cols + 1)];
		final double[] vertices = new double[6 * (cols + 1)];
		final ICoordinates coords = ICoordinates.ofLength(2 * (cols + 1));
		gl.outputNormal(0, 0, 1);
		for (int y = 0; y < rows; y++) {
			for (int x = 0, i = 0, j = 0; x <= cols; x++, i += 4, j += 6) {
				vertices[j] = tw * x - centerX;
				vertices[j + 1] = th * y - centerY;
				vertices[j + 2] = (dem.getRGB(cols - x, rows - y) & 255) * altFactor;
				vertices[j + 3] = vertices[j];
				vertices[j + 4] = vertices[j + 1] + th;
				vertices[j + 5] = (dem.getRGB(cols - x, rows - (y + 1)) & 255) * altFactor;
				texCoords[i] = 1.0f - ts * x;
				texCoords[i + 1] = 1.0f - tt * y;
				texCoords[i + 2] = texCoords[i];
				texCoords[i + 3] = texCoords[i + 1] - tt;

			}
			gl.drawVertices(GL2.GL_QUAD_STRIP, coords.setTo(vertices), -1, true, texCoords);
		}
		gl.popMatrix();

	}

	public BufferedImage getDirectImage(final FieldObject object, final int order) {
		final List<?> textures = object.getAttributes().getTextures();
		if (textures == null || textures.size() > order + 1) { return null; }
		final Object t = textures.get(order);
		if (t instanceof BufferedImage) { return (BufferedImage) t; }
		if (t instanceof GamaImageFile) { return ((GamaImageFile) t).getImage(null, true); }
		return null;
	}

}