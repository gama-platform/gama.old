/*********************************************************************************************
 *
 * 'FieldDrawer.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Locale;

import com.google.common.base.Objects;
import com.google.common.primitives.Doubles;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES3;
import com.jogamp.opengl.util.gl2.GLUT;

import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.metamodel.shape.GamaPoint;
import ummisco.gama.opengl.JOGLRenderer;

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

	public FieldDrawer(final JOGLRenderer r) {
		super(r);
	}

	@Override
	protected void _draw(final GL2 gl, final FieldObject demObj) {
		try {
			gl.glPushMatrix();
			if (demObj.values == null) {
				drawFromImage(demObj, gl);
				return;
			}
			final double altFactor = Objects.firstNonNull(demObj.getHeight(), 1.0);
			final double maxZ = Doubles.max(demObj.values);
			if (demObj.isGrayScaled()) {
				gl.glDisable(GL.GL_TEXTURE_2D);
			}
			if (demObj.isTriangulated()) {
				drawAsTriangles(gl, demObj, altFactor, maxZ);
			} else {
				drawAsRectangles(gl, demObj, altFactor, maxZ);
			}
			if (demObj.isShowText() && demObj.values != null) {
				drawLabels(gl, demObj, altFactor);
			}
		} finally {
			gl.glPopMatrix();
		}
	}

	public void drawLabels(final GL2 gl, final FieldObject demObj, final double altFactor) {
		final GamaPoint cellDim = demObj.getCellSize();
		final double columns = renderer.getEnvWidth() / cellDim.x;
		final double rows = renderer.getEnvHeight() / cellDim.y;
		// Draw gridvalue as text inside each cell
		gl.glDisable(GL.GL_BLEND);
		renderer.setCurrentColor(Color.black);
		for (int i = 0; i < columns; i++) {
			final double stepX = i * cellDim.x;/// textureWidth * columns;
			for (int j = 0; j < rows; j++) {
				final double stepY = j * cellDim.y;/// textureHeight * rows;
				final double gridValue = demObj.values[(int) (j * columns + i)];
				gl.glRasterPos3d(stepX + cellDim.x / 2, -(stepY + cellDim.y / 2), gridValue * altFactor + 1);
				gl.glPushMatrix();
				gl.glScaled(8.0d, 8.0d, 8.0d);
				renderer.getGlut().glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10,
						String.format(Locale.US, "%.2f", gridValue));
				gl.glPopMatrix();
			}
		}
		gl.glEnable(GL.GL_BLEND);
	}

	public void drawAsTriangles(final GL2 gl, final FieldObject demObj, final double altFactor, final double maxZ) {
		final GamaPoint cellDim = demObj.getCellSize();
		final double columns = renderer.getEnvWidth() / cellDim.x;
		final double rows = renderer.getEnvHeight() / cellDim.y;
		for (int i = 0; i < columns; i++) {
			final double x1 = i * cellDim.x;
			final double x2 = x1 + cellDim.x;
			for (int j = 0; j < rows; j++) {
				final double y1 = -j * cellDim.y;
				final double y2 = y1 - cellDim.y;
				double z1 = 0d, z2 = 0d, z3 = 0d, z4 = 0d;
				if (demObj.values != null) {
					z1 = Math.min(maxZ, Math.abs(demObj.values[(int) (j * columns + i)]));
					if (i < columns - 1 && j < rows - 1) {
						z2 = Math.min(maxZ, Math.abs(demObj.values[(int) ((j + 1) * columns + i)]));
						z3 = Math.min(maxZ, Math.abs(demObj.values[(int) ((j + 1) * columns + (i + 1))]));
						z4 = Math.min(maxZ, Math.abs(demObj.values[(int) (j * columns + (i + 1))]));
					} else if (j == (int) rows - 1 && i < columns - 1) {// Last rows
						z2 = z1;
						z3 = z4 = Math.min(maxZ, Math.abs(demObj.values[(int) (j * columns + (i + 1))]));
					} else if (i == (int) columns - 1 && j < rows - 1) {// Last cols
						z2 = z3 = Math.min(maxZ, Math.abs(demObj.values[(int) ((j + 1) * columns + i)]));
						z4 = z1;
					} else if (i == (int) columns - 1 && j == (int) rows - 1) { // last cell
						z2 = z3 = z4 = z1;
					}
				}
				fourPoints.replaceWith(x1, y1, z1 * altFactor, x1, y2, z2 * altFactor, x2, y1, z4 * altFactor, x2, y2,
						z3 * altFactor);
				_normal(fourPoints, true);
				final Color lineColor = demObj.getBorder();
				if (lineColor != null) {
					drawTriangleLines(fourPoints, lineColor);
				} else {
					if (demObj.isGrayScaled()) {
						drawGrayScaledTriangle(maxZ, fourPoints);

					} else {
						drawTexturedTriangle(gl, 1 / columns, 1 / rows, i, j, fourPoints);
					}
				}
			}
		}
	}

	public void drawTexturedTriangle(final GL2 gl, final double w, final double h, final int i, final int j,
			final ICoordinates vertices) {
		final double xt = w * i, yt = h * j, xt2 = xt + w, yt2 = yt + h;
		final double[] texCoords3 = { xt2, yt, xt, yt2, xt, yt };
		gl.glBegin(GL.GL_TRIANGLES);
		threePoints.replaceWith(vertices.at(2), vertices.at(1), vertices.at(0));
		_normal(threePoints, true);
		threePoints.visit((x, y, z, index) -> {
			gl.glTexCoord2d(texCoords3[index * 2], texCoords3[index * 2 + 1]);
			gl.glVertex3d(x, y, z);
		}, 3, true);
		final double[] texCoords4 = { xt, yt2, xt2, yt, xt2, yt2 };
		threePoints.replaceWith(vertices.at(1), vertices.at(2), vertices.at(3));
		_normal(threePoints, false);
		threePoints.visit((x, y, z, index) -> {
			gl.glTexCoord2d(texCoords4[index * 2], texCoords4[index * 2 + 1]);
			gl.glVertex3d(x, y, z);
		}, 3, false);
		gl.glEnd();
	}

	public void drawGrayScaledTriangle(final double maxZ, final ICoordinates vertices) {
		threePoints.replaceWith(vertices.at(2), vertices.at(1), vertices.at(0));
		renderer.setCurrentColor(threePoints.averageZ() / maxZ);
		_shape(threePoints, 3, true, true, true, null);
		threePoints.replaceWith(vertices.at(1), vertices.at(2), vertices.at(3));
		renderer.setCurrentColor(threePoints.averageZ() / maxZ);
		_shape(threePoints, 3, true, false, true, null);
	}

	public void drawTriangleLines(final ICoordinates vertices, final Color lineColor) {
		renderer.setCurrentColor(lineColor);
		_line(vertices, -1, true);
	}

	public void drawAsRectangles(final GL2 gl, final FieldObject demObj, final double altFactor, final double maxZ) {
		final GamaPoint cellDim = demObj.getCellSize();
		final double columns = renderer.getEnvWidth() / cellDim.x;
		final double rows = renderer.getEnvHeight() / cellDim.y;
		for (int i = 0; i < columns; i++) {
			final double x1 = i * cellDim.x, x2 = x1 + cellDim.x;
			for (int j = 0; j < rows; j++) {
				final double y1 = -j * cellDim.y, y2 = y1 - cellDim.y;
				final double zValue = Math.min(Math.abs(demObj.values[(int) (j * columns + i)]), maxZ);
				final double scaledZ = zValue * altFactor;
				// Explicitly create a ring
				fivePoints.replaceWith(x1, y1, scaledZ, x2, y1, scaledZ, x2, y2, scaledZ, x1, y2, scaledZ, x1, y1,
						scaledZ);
				final Color lineColor = demObj.getBorder();
				if (lineColor != null) {
					renderer.setCurrentColor(lineColor);
					_line(fivePoints, 4, true);
				} else {
					_normal(fivePoints, true);
					if (demObj.isGrayScaled()) {
						drawGrayScaledCell(maxZ, zValue, fivePoints);
					} else {
						drawTexturedCell(gl, 1 / columns, 1 / rows, i, j, fivePoints);
					}
				}
			}
		}
	}

	public void drawGrayScaledCell(final double maxZ, final double zValue, final ICoordinates vertices) {
		renderer.setCurrentColor(zValue / maxZ);
		_shape(vertices, 4, true, true, false, null);
	}

	public void drawTexturedCell(final GL2 gl, final double w, final double h, final int i, final int j,
			final ICoordinates vertices) {
		final double[] texCoords = { w * i, h * j, w * (i + 1), h * j, w * (i + 1), h * (j + 1), w * i, h * (j + 1) };
		gl.glBegin(GL2ES3.GL_QUADS);
		vertices.visit((x, y, z, index) -> {
			gl.glTexCoord2d(texCoords[index * 2], texCoords[index * 2 + 1]);
			gl.glVertex3d(x, y, z);
		}, 4, true);
		gl.glEnd();
	}

	protected void drawFromImage(final FieldObject demObj, final GL2 gl) {
		int rows, cols;
		int x, y;
		double vx, vy, s, t;
		double ts, tt, tw, th;
		final BufferedImage dem = demObj.getDirectImage(1);
		rows = dem.getHeight() - 1;
		cols = dem.getWidth() - 1;
		ts = 1.0f / cols;
		tt = 1.0f / rows;
		final double altFactor = Objects.firstNonNull(demObj.getHeight(), 1.0);
		tw = renderer.getEnvWidth() / cols;
		th = renderer.getEnvHeight() / rows;
		gl.glPushMatrix();
		gl.glTranslated(renderer.getEnvWidth() / 2, -renderer.getEnvHeight() / 2, 0);
		gl.glNormal3d(0f, 0f, 1f);

		for (y = 0; y < rows; y++) {
			gl.glBegin(GL2.GL_QUAD_STRIP);
			for (x = 0; x <= cols; x++) {
				vx = tw * x - renderer.getEnvWidth() / 2.0f;
				vy = th * y - renderer.getEnvHeight() / 2.0f;
				s = 1.0f - ts * x;
				t = 1.0f - tt * y;
				final double alt1 = (dem.getRGB(cols - x, y) & 255) * altFactor;
				final double alt2 = (dem.getRGB(cols - x, y + 1) & 255) * altFactor;
				gl.glTexCoord2d(s, t);
				gl.glVertex3d(vx, vy, alt1);
				gl.glTexCoord2d(s, t - tt);
				gl.glVertex3d(vx, vy + th, alt2);

			}
			gl.glEnd();
		}
		gl.glPopMatrix();

	}

}