/*********************************************************************************************
 *
 *
 * 'DEMDrawer.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Locale;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.utils.*;

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
		if ( demObj.values == null ) {
			drawFromImage(demObj, gl);
			return;
		}
		double cellWidth = demObj.getCellSize().x;
		double cellHeight = demObj.getCellSize().y;
		// Get Environment Properties
		double columns = renderer.data.getEnvWidth() / cellWidth;
		double rows = renderer.data.getEnvHeight() / cellHeight;
		double envWidthStep = 1 / columns;
		double envHeightStep = 1 / rows;

		// Get Texture Properties
		Texture curTexture = demObj.getTexture(gl, renderer, 0);
		if ( curTexture == null ) { return; }

		// FIXME: Need to set it dynamicly
		double altFactor = demObj.getZFactor();
		double maxZ = GetMaxValue(demObj.values);

		double x1, x2, y1, y2;
		double zValue = 0d;
		double zValScaled = 0d;
		double stepX, stepY;

		if ( !demObj.isGrayScaled() ) {
			curTexture.enable(gl);
			curTexture.bind(gl);
		}

		gl.glColor4d(1.0d, 1.0d, 1.0d, demObj.getAlpha());

		// Draw Grid with square
		// if texture draw with color coming from the texture and z according to gridvalue
		// else draw the grid with color according the gridValue in gray value
		// if ( !isInitialized() && demObj.isTextured ) {
		// setInitialized(true);
		// }
		if ( !demObj.isTriangulated() ) {
			for ( int i = 0; i < columns; i++ ) {
				x1 = i / columns * columns;
				x2 = (i + 1) / columns * columns;
				for ( int j = 0; j < rows; j++ ) {
					y1 = j / rows * rows;
					y2 = (j + 1) / rows * rows;
					if ( demObj.values != null ) {
						zValue = demObj.values[(int) (j * columns + i)];
						zValScaled = zValue * altFactor;
					}
					Color lineColor = demObj.getBorder();
					if ( lineColor != null ) {
						gl.glColor3d(lineColor.getRed() / 255.0f, lineColor.getGreen() / 255.0f,
							lineColor.getBlue() / 255.0f);
						gl.glBegin(GL.GL_LINE_STRIP);
						gl.glVertex3d(x1 * cellWidth, -y1 * cellHeight, zValScaled);
						gl.glVertex3d(x2 * cellWidth, -y1 * cellHeight, zValScaled);
						gl.glVertex3d(x2 * cellWidth, -y2 * cellHeight, zValScaled);
						gl.glVertex3d(x1 * cellWidth, -y2 * cellHeight, zValScaled);
						gl.glVertex3d(x1 * cellWidth, -y1 * cellHeight, zValScaled);
						gl.glEnd();
					} else {
						if ( demObj.isGrayScaled() ) {
							gl.glColor3d(zValue / maxZ, zValue / maxZ, zValue / maxZ);
							gl.glBegin(GL2ES3.GL_QUADS);
							gl.glVertex3d(x1 * cellWidth, -y1 * cellHeight, zValScaled);
							gl.glVertex3d(x2 * cellWidth, -y1 * cellHeight, zValScaled);
							gl.glVertex3d(x2 * cellWidth, -y2 * cellHeight, zValScaled);
							gl.glVertex3d(x1 * cellWidth, -y2 * cellHeight, zValScaled);
							gl.glEnd();

						} else {
							gl.glBegin(GL2ES3.GL_QUADS);
							gl.glTexCoord2d(envWidthStep * i, envHeightStep * j);
							gl.glVertex3d(x1 * cellWidth, -y1 * cellHeight, zValScaled);
							gl.glTexCoord2d(envWidthStep * (i + 1), envHeightStep * j);
							gl.glVertex3d(x2 * cellWidth, -y1 * cellHeight, zValScaled);
							gl.glTexCoord2d(envWidthStep * (i + 1), envHeightStep * (j + 1));
							gl.glVertex3d(x2 * cellWidth, -y2 * cellHeight, zValScaled);
							gl.glTexCoord2d(envWidthStep * i, envHeightStep * (j + 1));
							gl.glVertex3d(x1 * cellWidth, -y2 * cellHeight, zValScaled);
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
			for ( int i = 0; i < columns; i++ ) {
				x1 = i / columns * columns;
				x2 = (i + 1) / columns * columns;
				for ( int j = 0; j < rows; j++ ) {
					y1 = j / rows * rows;
					y2 = (j + 1) / rows * rows;
					if ( demObj.values != null ) {
						zValue = demObj.values[(int) (j * columns + i)];
						if ( i < columns - 1 && j < rows - 1 ) {
							z1 = zValue;
							z2 = demObj.values[(int) ((j + 1) * columns + i)];
							z3 = demObj.values[(int) ((j + 1) * columns + (i + 1))];
							z4 = demObj.values[(int) (j * columns + (i + 1))];
						}

						// Last rows
						if ( j == rows - 1 && i < columns - 1 ) {
							z1 = zValue;
							z4 = demObj.values[(int) (j * columns + (i + 1))];
							z2 = z1;
							z3 = z4;
						}
						// Last cols
						if ( i == columns - 1 && j < rows - 1 ) {
							z1 = zValue;
							z2 = demObj.values[(int) ((j + 1) * columns + i)];
							z3 = z2;
							z4 = z1;
						}

						// last cell
						if ( i == columns - 1 && j == rows - 1 ) {
							z1 = zValue;
							z2 = z1;
							z3 = z1;
							z4 = z1;
						}

					}

					// Compute normal
					if ( renderer.getComputeNormal() ) {
						Vertex[] vertices = new Vertex[4];
						for ( int i1 = 0; i1 < 4; i1++ ) {
							vertices[i1] = new Vertex();
						}
						vertices[0].x = x1 * cellWidth;
						vertices[0].y = -y1 * cellHeight;
						vertices[0].z = z1 * altFactor;

						vertices[1].x = x1 * cellWidth;
						vertices[1].y = -y2 * cellHeight;
						vertices[1].z = z1 * altFactor;

						vertices[2].x = x2 * cellWidth;
						vertices[2].y = -y1 * cellHeight;
						vertices[2].z = z4 * altFactor;

						vertices[3].x = x2 * cellWidth;
						vertices[3].y = -y2 * cellHeight;
						vertices[3].z = z3 * altFactor;
						double[] normal = GLUtilNormal.CalculateNormal(vertices[2], vertices[1], vertices[0]);
						gl.glNormal3dv(normal, 0);
						// GLUtilNormal.HandleNormal(vertices, null, 0,-1, renderer);
					}
					Color lineColor = demObj.getBorder();
					if ( lineColor != null ) {
						gl.glColor3d(lineColor.getRed() / 255.0f, lineColor.getGreen() / 255.0f,
							lineColor.getBlue() / 255.0f);
						gl.glBegin(GL.GL_LINE_STRIP);
						gl.glVertex3d(x1 * cellWidth, -y1 * cellHeight, z1 * altFactor);
						gl.glVertex3d(x1 * cellWidth, -y2 * cellHeight, z2 * altFactor);
						gl.glVertex3d(x2 * cellWidth, -y1 * cellHeight, z4 * altFactor);
						gl.glVertex3d(x2 * cellWidth, -y2 * cellHeight, z3 * altFactor);
						gl.glVertex3d(x1 * cellWidth, -y1 * cellHeight, z1 * altFactor);
						gl.glEnd();
					} else {
						if ( demObj.isGrayScaled() ) {
							gl.glColor3d(zValue / maxZ, zValue / maxZ, zValue / maxZ);
							gl.glBegin(GL.GL_TRIANGLE_STRIP);
							gl.glVertex3d(x1 * cellWidth, -y1 * cellHeight, z1 * altFactor);
							gl.glVertex3d(x1 * cellWidth, -y2 * cellHeight, z2 * altFactor);
							gl.glVertex3d(x2 * cellWidth, -y1 * cellHeight, z4 * altFactor);
							gl.glVertex3d(x2 * cellWidth, -y2 * cellHeight, z3 * altFactor);
							gl.glEnd();

						} else {
							gl.glBegin(GL.GL_TRIANGLE_STRIP);
							gl.glTexCoord2d(envWidthStep * i, envHeightStep * j);
							gl.glVertex3d(x1 * cellWidth, -y1 * cellHeight, z1 * altFactor);
							gl.glTexCoord2d(envWidthStep * i, envHeightStep * (j + 1));
							gl.glVertex3d(x1 * cellWidth, -y2 * cellHeight, z2 * altFactor);
							gl.glTexCoord2d(envWidthStep * (i + 1), envHeightStep * j);
							gl.glVertex3d(x2 * cellWidth, -y1 * cellHeight, z4 * altFactor);
							gl.glTexCoord2d(envWidthStep * (i + 1), envHeightStep * (j + 1));
							gl.glVertex3d(x2 * cellWidth, -y2 * cellHeight, z3 * altFactor);
							gl.glEnd();
						}
					}
				}
			}
		}

		if ( demObj.isShowText() && demObj.values != null ) {
			// Draw gridvalue as text inside each cell
			gl.glDisable(GL.GL_BLEND);
			gl.glColor4d(0.0, 0.0, 0.0, 1.0d);
			for ( int i = 0; i < columns; i++ ) {
				stepX = i * cellWidth;/// textureWidth * columns;
				for ( int j = 0; j < rows; j++ ) {
					stepY = j * cellHeight;/// textureHeight * rows;
					double gridValue = demObj.values[(int) (j * columns + i)];
					gl.glRasterPos3d(stepX + cellWidth / 2, -(stepY + cellHeight / 2), gridValue * altFactor);
					gl.glPushMatrix();
					gl.glScaled(8.0d, 8.0d, 8.0d);
					glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, String.format(Locale.US, "%.2f", gridValue));
					gl.glPopMatrix();
				}
			}
			gl.glEnable(GL.GL_BLEND);
		}
		if ( !demObj.isGrayScaled() ) {
			curTexture.disable(gl);
		}

	}

	private static double GetMaxValue(final double[] gridValue) {
		double maxValue = 0.0;
		if ( gridValue != null ) {
			for ( double element : gridValue ) {
				if ( element > maxValue ) {
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

		BufferedImage dem = demObj.getDirectImage(1);
		Texture curTexture = demObj.getTexture(gl, renderer, 0);
		if ( curTexture == null ) { return; }
		// Enable the texture

		curTexture.enable(gl);
		curTexture.bind(gl);
		rows = dem.getHeight() - 1;
		cols = dem.getWidth() - 1;
		ts = 1.0f / cols;
		tt = 1.0f / rows;

		// FIXME/ need to set w and h dynamicly
		float w = (float) renderer.data.getEnvWidth();
		float h = (float) renderer.data.getEnvHeight();
		float altFactor = (float) demObj.getZFactor();

		tw = w / cols;
		th = h / rows;
		gl.glPushMatrix();
		gl.glTranslated(w / 2, -h / 2, 0);

		gl.glNormal3f(0.0f, 1.0f, 0.0f);

		for ( y = 0; y < rows; y++ ) {
			gl.glBegin(GL2.GL_QUAD_STRIP);
			for ( x = 0; x <= cols; x++ ) {
				vx = tw * x - w / 2.0f;
				vy = th * y - h / 2.0f;
				s = 1.0f - ts * x;
				t = 1.0f - tt * y;

				float alt1 = (dem.getRGB(cols - x, y) & 255) * altFactor;
				float alt2 = (dem.getRGB(cols - x, y + 1) & 255) * altFactor;

				boolean isTextured = true;
				if ( isTextured ) {
					gl.glTexCoord2f(s, t);
					gl.glVertex3f(vx, vy, alt1);
					gl.glTexCoord2f(s, t - tt);
					gl.glVertex3f(vx, vy + th, alt2);
				} else {
					float color = dem.getRGB(cols - x, y) & 255;
					color = color / 255.0f;
					gl.glColor3f(color, color, color);
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