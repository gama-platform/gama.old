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

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES3;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;

import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.utils.GLUtilNormal;
import ummisco.gama.opengl.utils.Vertex;

/**
 *
 * The class DEMDrawer.
 *
 * @author grignard
 * @since 15 mai 2013
 *
 */
public class FieldDrawer extends ObjectDrawer<FieldObject> {

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
			final double cellWidth = demObj.getCellSize().x;
			final double cellHeight = demObj.getCellSize().y;
			// Get Environment Properties
			final double columns = renderer.data.getEnvWidth() / cellWidth;
			final double rows = renderer.data.getEnvHeight() / cellHeight;
			final double envWidthStep = 1 / columns;
			final double envHeightStep = 1 / rows;

			// Get Texture Properties
			final Texture curTexture = demObj.getTexture(gl, renderer, 0);
			if (curTexture == null) { return; }

			// FIXME: Need to set it dynamicly
			final double altFactor = demObj.getZFactor();
			final double maxZ = GetMaxValue(demObj.values);

			double x1, x2, y1, y2;
			double zValue = 0d;
			double zValScaled = 0d;
			double stepX, stepY;

			if (!demObj.isGrayScaled()) {
				curTexture.enable(gl);
				curTexture.bind(gl);
			}
			renderer.setCurrentColor(gl, Color.white, demObj.getAlpha());
			// GLUtilGLContext.SetCurrentColor(gl, 1.0f, 1.0f, 1.0f,
			// demObj.getAlpha().floatValue());

			// Draw Grid with square
			// if texture draw with color coming from the texture and z
			// according to
			// gridvalue
			// else draw the grid with color according the gridValue in gray
			// value
			// if ( !isInitialized() && demObj.isTextured ) {
			// setInitialized(true);
			// }
			if (!demObj.isTriangulated()) {
				for (int i = 0; i < columns; i++) {
					x1 = i / columns * columns;
					x2 = (i + 1) / columns * columns;
					for (int j = 0; j < rows; j++) {
						y1 = j / rows * rows;
						y2 = (j + 1) / rows * rows;
						if (demObj.values != null) {
							zValue = demObj.values[(int) (j * columns + i)];
							zValScaled = zValue * altFactor;
						}
						final Color lineColor = demObj.getBorder();
						if (lineColor != null) {
							renderer.setCurrentColor(gl, lineColor);
							// GLUtilGLContext.SetCurrentColor(gl, new float[] {
							// lineColor.getRed() / 255.0f,
							// lineColor.getGreen() / 255.0f,
							// lineColor.getBlue() /
							// 255.0f });
							gl.glBegin(GL.GL_LINE_STRIP);
							gl.glVertex3d(x1 * cellWidth, JOGLRenderer.Y_FLAG * y1 * cellHeight, zValScaled);
							gl.glVertex3d(x2 * cellWidth, JOGLRenderer.Y_FLAG * y1 * cellHeight, zValScaled);
							gl.glVertex3d(x2 * cellWidth, JOGLRenderer.Y_FLAG * y2 * cellHeight, zValScaled);
							gl.glVertex3d(x1 * cellWidth, JOGLRenderer.Y_FLAG * y2 * cellHeight, zValScaled);
							gl.glVertex3d(x1 * cellWidth, JOGLRenderer.Y_FLAG * y1 * cellHeight, zValScaled);
							gl.glEnd();
						} else {
							if (demObj.isGrayScaled()) {
								renderer.setCurrentColor(gl, zValue / maxZ);
								// GLUtilGLContext.SetCurrentColor(gl, new
								// float[] {
								// (float) (zValue / maxZ),
								// (float) (zValue / maxZ), (float) (zValue /
								// maxZ)
								// });
								gl.glBegin(GL2ES3.GL_QUADS);
								gl.glVertex3d(x1 * cellWidth, JOGLRenderer.Y_FLAG * y1 * cellHeight, zValScaled);
								gl.glVertex3d(x2 * cellWidth, JOGLRenderer.Y_FLAG * y1 * cellHeight, zValScaled);
								gl.glVertex3d(x2 * cellWidth, JOGLRenderer.Y_FLAG * y2 * cellHeight, zValScaled);
								gl.glVertex3d(x1 * cellWidth, JOGLRenderer.Y_FLAG * y2 * cellHeight, zValScaled);
								gl.glEnd();

							} else {
								gl.glBegin(GL2ES3.GL_QUADS);
								gl.glTexCoord2d(envWidthStep * i, envHeightStep * j);
								gl.glVertex3d(x1 * cellWidth, JOGLRenderer.Y_FLAG * y1 * cellHeight, zValScaled);
								gl.glTexCoord2d(envWidthStep * (i + 1), envHeightStep * j);
								gl.glVertex3d(x2 * cellWidth, JOGLRenderer.Y_FLAG * y1 * cellHeight, zValScaled);
								gl.glTexCoord2d(envWidthStep * (i + 1), envHeightStep * (j + 1));
								gl.glVertex3d(x2 * cellWidth, JOGLRenderer.Y_FLAG * y2 * cellHeight, zValScaled);
								gl.glTexCoord2d(envWidthStep * i, envHeightStep * (j + 1));
								gl.glVertex3d(x1 * cellWidth, JOGLRenderer.Y_FLAG * y2 * cellHeight, zValScaled);
								gl.glEnd();
							}
						}
					}
				}
			} else {
				double z1 = 0d;
				double z2 = 0d;
				double z3 = 0d;
				double z4 = 0d;
				for (int i = 0; i < columns; i++) {
					x1 = i / columns * columns;
					x2 = (i + 1) / columns * columns;
					for (int j = 0; j < rows; j++) {
						y1 = j / rows * rows;
						y2 = (j + 1) / rows * rows;
						if (demObj.values != null) {
							zValue = demObj.values[(int) (j * columns + i)];
							if (i < columns - 1 && j < rows - 1) {
								z1 = zValue;
								z2 = demObj.values[(int) ((j + 1) * columns + i)];
								z3 = demObj.values[(int) ((j + 1) * columns + (i + 1))];
								z4 = demObj.values[(int) (j * columns + (i + 1))];
							}

							// Last rows
							if (j == (int) rows - 1 && i < columns - 1) {
								z1 = zValue;
								z4 = demObj.values[(int) (j * columns + (i + 1))];
								z2 = z1;
								z3 = z4;
							}
							// Last cols
							if (i == (int) columns - 1 && j < rows - 1) {
								z1 = zValue;
								z2 = demObj.values[(int) ((j + 1) * columns + i)];
								z3 = z2;
								z4 = z1;
							}

							// last cell
							if (i == (int) columns - 1 && j == (int) rows - 1) {
								z1 = zValue;
								z2 = z1;
								z3 = z1;
								z4 = z1;
							}

						}

						// Compute normal

						final Vertex[] vertices = new Vertex[4];
						for (int i1 = 0; i1 < 4; i1++) {
							vertices[i1] = new Vertex();
						}
						vertices[0].x = x1 * cellWidth;
						vertices[0].y = JOGLRenderer.Y_FLAG * y1 * cellHeight;
						vertices[0].z = z1 * altFactor;

						vertices[1].x = x1 * cellWidth;
						vertices[1].y = JOGLRenderer.Y_FLAG * y2 * cellHeight;
						vertices[1].z = z1 * altFactor;

						vertices[2].x = x2 * cellWidth;
						vertices[2].y = JOGLRenderer.Y_FLAG * y1 * cellHeight;
						vertices[2].z = z4 * altFactor;

						vertices[3].x = x2 * cellWidth;
						vertices[3].y = JOGLRenderer.Y_FLAG * y2 * cellHeight;
						vertices[3].z = z3 * altFactor;
						GLUtilNormal.HandleNormal(vertices, 1, renderer);
						// GLUtilNormal.HandleNormal(vertices, null, 0,-1,
						// renderer);
						final Color lineColor = demObj.getBorder();
						if (lineColor != null) {
							renderer.setCurrentColor(gl, lineColor);
							// GLUtilGLContext.SetCurrentColor(gl, new float[] {
							// lineColor.getRed() / 255.0f,
							// lineColor.getGreen() / 255.0f,
							// lineColor.getBlue() /
							// 255.0f });
							gl.glBegin(GL.GL_LINE_STRIP);
							gl.glVertex3d(x1 * cellWidth, JOGLRenderer.Y_FLAG * y1 * cellHeight, z1 * altFactor);
							gl.glVertex3d(x1 * cellWidth, JOGLRenderer.Y_FLAG * y2 * cellHeight, z2 * altFactor);
							gl.glVertex3d(x2 * cellWidth, JOGLRenderer.Y_FLAG * y1 * cellHeight, z4 * altFactor);
							gl.glVertex3d(x2 * cellWidth, JOGLRenderer.Y_FLAG * y2 * cellHeight, z3 * altFactor);
							gl.glVertex3d(x1 * cellWidth, JOGLRenderer.Y_FLAG * y1 * cellHeight, z1 * altFactor);
							gl.glEnd();
						} else {
							if (demObj.isGrayScaled()) {
								renderer.setCurrentColor(gl, zValue / maxZ);
								// GLUtilGLContext.SetCurrentColor(gl, new
								// float[] {
								// (float) (zValue / maxZ),
								// (float) (zValue / maxZ), (float) (zValue /
								// maxZ)
								// });
								gl.glBegin(GL.GL_TRIANGLE_STRIP);
								gl.glVertex3d(x1 * cellWidth, JOGLRenderer.Y_FLAG * y1 * cellHeight, z1 * altFactor);
								gl.glVertex3d(x1 * cellWidth, JOGLRenderer.Y_FLAG * y2 * cellHeight, z2 * altFactor);
								gl.glVertex3d(x2 * cellWidth, JOGLRenderer.Y_FLAG * y1 * cellHeight, z4 * altFactor);
								gl.glVertex3d(x2 * cellWidth, JOGLRenderer.Y_FLAG * y2 * cellHeight, z3 * altFactor);
								gl.glEnd();

							} else {
								gl.glBegin(GL.GL_TRIANGLE_STRIP);
								gl.glTexCoord2d(envWidthStep * i, envHeightStep * j);
								gl.glVertex3d(x1 * cellWidth, JOGLRenderer.Y_FLAG * y1 * cellHeight, z1 * altFactor);
								gl.glTexCoord2d(envWidthStep * i, envHeightStep * (j + 1));
								gl.glVertex3d(x1 * cellWidth, JOGLRenderer.Y_FLAG * y2 * cellHeight, z2 * altFactor);
								gl.glTexCoord2d(envWidthStep * (i + 1), envHeightStep * j);
								gl.glVertex3d(x2 * cellWidth, JOGLRenderer.Y_FLAG * y1 * cellHeight, z4 * altFactor);
								gl.glTexCoord2d(envWidthStep * (i + 1), envHeightStep * (j + 1));
								gl.glVertex3d(x2 * cellWidth, JOGLRenderer.Y_FLAG * y2 * cellHeight, z3 * altFactor);
								gl.glEnd();
							}
						}
					}
				}
			}

			if (demObj.isShowText() && demObj.values != null) {
				// Draw gridvalue as text inside each cell
				gl.glDisable(GL.GL_BLEND);
				renderer.setCurrentColor(gl, Color.black);
				// GLUtilGLContext.SetCurrentColor(gl, new float[] { 0.0f, 0.0f,
				// 0.0f, 1.0f });
				for (int i = 0; i < columns; i++) {
					stepX = i * cellWidth;/// textureWidth * columns;
					for (int j = 0; j < rows; j++) {
						stepY = j * cellHeight;/// textureHeight * rows;
						final double gridValue = demObj.values[(int) (j * columns + i)];
						gl.glRasterPos3d(stepX + cellWidth / 2, -(stepY + cellHeight / 2), gridValue * altFactor);
						gl.glPushMatrix();
						gl.glScaled(8.0d, 8.0d, 8.0d);
						renderer.getGlut().glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10,
								String.format(Locale.US, "%.2f", gridValue));
						gl.glPopMatrix();
					}
				}
				gl.glEnable(GL.GL_BLEND);
			}
			if (!demObj.isGrayScaled()) {
				curTexture.disable(gl);
			}

		} finally {
			gl.glPopMatrix();
		}
	}

	private static double GetMaxValue(final double[] gridValue) {
		double maxValue = 0.0;
		if (gridValue != null) {
			for (final double element : gridValue) {
				if (element > maxValue) {
					maxValue = element;
				}
			}
		}
		return maxValue;
	}

	protected void drawFromImage(final FieldObject demObj, final GL2 gl) {

		int rows, cols;
		int x, y;
		float vx, vy, s, t;
		float ts, tt, tw, th;

		final BufferedImage dem = demObj.getDirectImage(1);
		final Texture curTexture = demObj.getTexture(gl, renderer, 0);
		if (curTexture == null) { return; }
		// Enable the texture

		curTexture.enable(gl);
		curTexture.bind(gl);
		rows = dem.getHeight() - 1;
		cols = dem.getWidth() - 1;
		ts = 1.0f / cols;
		tt = 1.0f / rows;

		// FIXME/ need to set w and h dynamicly
		final float w = (float) renderer.data.getEnvWidth();
		final float h = (float) renderer.data.getEnvHeight();
		final float altFactor = (float) demObj.getZFactor();

		tw = w / cols;
		th = h / rows;
		gl.glPushMatrix();
		gl.glTranslated(w / 2, -h / 2, 0);

		gl.glNormal3f(0.0f, 1.0f, 0.0f);

		for (y = 0; y < rows; y++) {
			gl.glBegin(GL2.GL_QUAD_STRIP);
			for (x = 0; x <= cols; x++) {
				vx = tw * x - w / 2.0f;
				vy = th * y - h / 2.0f;
				s = 1.0f - ts * x;
				t = 1.0f - tt * y;

				final float alt1 = (dem.getRGB(cols - x, y) & 255) * altFactor;
				final float alt2 = (dem.getRGB(cols - x, y + 1) & 255) * altFactor;

				final boolean isTextured = true;
				if (isTextured) {
					gl.glTexCoord2f(s, t);
					gl.glVertex3f(vx, vy, alt1);
					gl.glTexCoord2f(s, t - tt);
					gl.glVertex3f(vx, vy + th, alt2);
				} else {
					float color = dem.getRGB(cols - x, y) & 255;
					color = color / 255.0f;
					renderer.setCurrentColor(gl, color);
					// GLUtilGLContext.SetCurrentColor(gl, color, color, color);
					gl.glVertex3f(vx, vy, alt1);
					gl.glVertex3f(vx, vy + th, alt2);
				}
			}
			gl.glEnd();
		}
		gl.glPopMatrix();
		// gl.glTranslated(-w / 2, h / 2, 0);

		// FIXME: Add disable texture?
		curTexture.disable(gl);

	}

}