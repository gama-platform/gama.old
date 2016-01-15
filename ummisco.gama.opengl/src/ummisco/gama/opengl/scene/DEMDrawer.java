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

import java.awt.image.BufferedImage;
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
public class DEMDrawer extends ObjectDrawer<DEMObject> {

	// private boolean initialized;
	final GLUT glut = new GLUT();

	public DEMDrawer(final JOGLRenderer r) {
		super(r);
	}

	@Override
	protected void _draw(final GL2 gl, final DEMObject demObj) {
		// GL2 gl = GLContext.getCurrentGL().getGL2();
		if ( demObj.fromImage ) {
			drawFromImage(demObj, gl);
			return;
		}

		// Get Environment Properties
		double envWidth = demObj.envelope.getWidth() / demObj.cellSize;
		double envHeight = demObj.envelope.getHeight() / demObj.cellSize;
		double envWidthStep = 1 / envWidth;
		double envHeightStep = 1 / envHeight;

		// Get Texture Properties
		double textureWidth = demObj.textureImage.getWidth();
		double textureHeight = demObj.textureImage.getHeight();
		double textureWidthInEnvironment = envWidth / textureWidth;
		double textureHeightInEnvironment = envHeight / textureHeight;

		// FIXME: Need to set it dynamicly
		double altFactor = demObj.envelope.getDepth();
		double maxZ = GetMaxValue(demObj.dem);

		double x1, x2, y1, y2;
		Double zValue = 0.0;
		double stepX, stepY;

		Texture curTexture = demObj.getTexture(gl, renderer);
		if ( curTexture == null ) { return; }

		if ( !demObj.isGrayScaled ) {
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
		if ( !demObj.isTriangulated ) {
			for ( int i = 0; i < envWidth; i++ ) {
				x1 = i / envWidth * envWidth;
				x2 = (i + 1) / envWidth * envWidth;
				for ( int j = 0; j < envHeight; j++ ) {
					y1 = j / envHeight * envHeight;
					y2 = (j + 1) / envHeight * envHeight;
					if ( demObj.dem != null ) {
						zValue = demObj.dem[(int) (j * envWidth + i)];
					}
					if ( demObj.lineColor != null ) {
						gl.glColor3d(demObj.lineColor.getRed() / 255.0f, demObj.lineColor.getGreen() / 255.0f,
							demObj.lineColor.getBlue() / 255.0f);
						gl.glBegin(GL.GL_LINE_STRIP);
						gl.glVertex3d(x1 * demObj.cellSize, -y1 * demObj.cellSize, zValue * altFactor);
						gl.glVertex3d(x2 * demObj.cellSize, -y1 * demObj.cellSize, zValue * altFactor);
						gl.glVertex3d(x2 * demObj.cellSize, -y2 * demObj.cellSize, zValue * altFactor);
						gl.glVertex3d(x1 * demObj.cellSize, -y2 * demObj.cellSize, zValue * altFactor);
						gl.glVertex3d(x1 * demObj.cellSize, -y1 * demObj.cellSize, zValue * altFactor);
						gl.glEnd();
					} else {
						if ( demObj.isGrayScaled ) {
							gl.glColor3d(zValue / maxZ, zValue / maxZ, zValue / maxZ);
							gl.glBegin(GL2ES3.GL_QUADS);
							gl.glVertex3d(x1 * demObj.cellSize, -y1 * demObj.cellSize, zValue * altFactor);
							gl.glVertex3d(x2 * demObj.cellSize, -y1 * demObj.cellSize, zValue * altFactor);
							gl.glVertex3d(x2 * demObj.cellSize, -y2 * demObj.cellSize, zValue * altFactor);
							gl.glVertex3d(x1 * demObj.cellSize, -y2 * demObj.cellSize, zValue * altFactor);
							gl.glEnd();

						} else {
							gl.glBegin(GL2ES3.GL_QUADS);
							gl.glTexCoord2d(envWidthStep * i, envHeightStep * j);
							gl.glVertex3d(x1 * demObj.cellSize, -y1 * demObj.cellSize, zValue * altFactor);
							gl.glTexCoord2d(envWidthStep * (i + 1), envHeightStep * j);
							gl.glVertex3d(x2 * demObj.cellSize, -y1 * demObj.cellSize, zValue * altFactor);
							gl.glTexCoord2d(envWidthStep * (i + 1), envHeightStep * (j + 1));
							gl.glVertex3d(x2 * demObj.cellSize, -y2 * demObj.cellSize, zValue * altFactor);
							gl.glTexCoord2d(envWidthStep * i, envHeightStep * (j + 1));
							gl.glVertex3d(x1 * demObj.cellSize, -y2 * demObj.cellSize, zValue * altFactor);
							gl.glEnd();
						}
					}
				}
			}
		}

		Double z1 = 0.0;
		Double z2 = 0.0;
		Double z3 = 0.0;
		Double z4 = 0.0;

		if ( demObj.isTriangulated ) {
			for ( int i = 0; i < envWidth; i++ ) {
				x1 = i / envWidth * envWidth;
				x2 = (i + 1) / envWidth * envWidth;
				for ( int j = 0; j < envHeight; j++ ) {
					y1 = j / envHeight * envHeight;
					y2 = (j + 1) / envHeight * envHeight;
					if ( demObj.dem != null ) {
						zValue = demObj.dem[(int) (j * envWidth + i)];

						if ( i < envWidth - 1 && j < envHeight - 1 ) {
							z1 = demObj.dem[(int) (j * envWidth + i)];
							z2 = demObj.dem[(int) ((j + 1) * envWidth + i)];
							z3 = demObj.dem[(int) ((j + 1) * envWidth + (i + 1))];
							z4 = demObj.dem[(int) (j * envWidth + (i + 1))];
						}

						// Last rows
						if ( j == envHeight - 1 && i < envWidth - 1 ) {
							z1 = demObj.dem[(int) (j * envWidth + i)];
							z4 = demObj.dem[(int) (j * envWidth + (i + 1))];
							z2 = z1;
							z3 = z4;
						}
						// Last cols
						if ( i == envWidth - 1 && j < envHeight - 1 ) {
							z1 = demObj.dem[(int) (j * envWidth + i)];
							z2 = demObj.dem[(int) ((j + 1) * envWidth + i)];
							z3 = z2;
							z4 = z1;
						}

						// last cell
						if ( i == envWidth - 1 && j == envHeight - 1 ) {
							z1 = demObj.dem[(int) (j * envWidth + i)];
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
						vertices[0].x = x1 * demObj.cellSize;
						vertices[0].y = -y1 * demObj.cellSize;
						vertices[0].z = z1 * altFactor;

						vertices[1].x = x1 * demObj.cellSize;
						vertices[1].y = -y2 * demObj.cellSize;
						vertices[1].z = z1 * altFactor;

						vertices[2].x = x2 * demObj.cellSize;
						vertices[2].y = -y1 * demObj.cellSize;
						vertices[2].z = z4 * altFactor;

						vertices[3].x = x2 * demObj.cellSize;
						vertices[3].y = -y2 * demObj.cellSize;
						vertices[3].z = z3 * altFactor;
						double[] normal = GLUtilNormal.CalculateNormal(vertices[2], vertices[1], vertices[0]);
						gl.glNormal3dv(normal, 0);
						// GLUtilNormal.HandleNormal(vertices, null, 0,-1, renderer);
					}
					if ( demObj.lineColor != null ) {
						gl.glColor3d(demObj.lineColor.getRed() / 255.0f, demObj.lineColor.getGreen() / 255.0f,
							demObj.lineColor.getBlue() / 255.0f);
						gl.glBegin(GL.GL_LINE_STRIP);
						gl.glVertex3d(x1 * demObj.cellSize, -y1 * demObj.cellSize, z1 * altFactor);
						gl.glVertex3d(x1 * demObj.cellSize, -y2 * demObj.cellSize, z2 * altFactor);
						gl.glVertex3d(x2 * demObj.cellSize, -y1 * demObj.cellSize, z4 * altFactor);
						gl.glVertex3d(x2 * demObj.cellSize, -y2 * demObj.cellSize, z3 * altFactor);
						gl.glVertex3d(x1 * demObj.cellSize, -y1 * demObj.cellSize, z1 * altFactor);
						gl.glEnd();
					} else {
						if ( demObj.isGrayScaled ) {
							gl.glColor3d(zValue / maxZ, zValue / maxZ, zValue / maxZ);
							gl.glBegin(GL.GL_TRIANGLE_STRIP);
							gl.glVertex3d(x1 * demObj.cellSize, -y1 * demObj.cellSize, z1 * altFactor);
							gl.glVertex3d(x1 * demObj.cellSize, -y2 * demObj.cellSize, z2 * altFactor);
							gl.glVertex3d(x2 * demObj.cellSize, -y1 * demObj.cellSize, z4 * altFactor);
							gl.glVertex3d(x2 * demObj.cellSize, -y2 * demObj.cellSize, z3 * altFactor);
							gl.glEnd();

						} else {
							gl.glBegin(GL.GL_TRIANGLE_STRIP);
							gl.glTexCoord2d(envWidthStep * i, envHeightStep * j);
							gl.glVertex3d(x1 * demObj.cellSize, -y1 * demObj.cellSize, z1 * altFactor);
							gl.glTexCoord2d(envWidthStep * i, envHeightStep * (j + 1));
							gl.glVertex3d(x1 * demObj.cellSize, -y2 * demObj.cellSize, z2 * altFactor);
							gl.glTexCoord2d(envWidthStep * (i + 1), envHeightStep * j);
							gl.glVertex3d(x2 * demObj.cellSize, -y1 * demObj.cellSize, z4 * altFactor);
							gl.glTexCoord2d(envWidthStep * (i + 1), envHeightStep * (j + 1));
							gl.glVertex3d(x2 * demObj.cellSize, -y2 * demObj.cellSize, z3 * altFactor);
							gl.glEnd();
						}
					}
				}
			}
		}

		if ( demObj.isShowText ) {
			// Draw gridvalue as text inside each cell
			Double gridValue = 0.0;
			gl.glDisable(GL.GL_BLEND);
			gl.glColor4d(0.0, 0.0, 0.0, 1.0d);
			for ( int i = 0; i < textureWidth; i++ ) {
				stepX = i / textureWidth * envWidth;
				for ( int j = 0; j < textureHeight; j++ ) {
					stepY = j / textureHeight * envHeight;
					if ( demObj.dem != null ) {
						gridValue = demObj.dem[(int) (j * textureWidth + i)];
					}
					gl.glRasterPos3d(stepX + textureWidthInEnvironment / 2, -(stepY + textureHeightInEnvironment / 2),
						gridValue * altFactor);
					gl.glScaled(8.0d, 8.0d, 8.0d);
					glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, gridValue.toString());
					gl.glScaled(0.125d, 0.125d, 0.125d);
				}
			}
			gl.glEnable(GL.GL_BLEND);
		}
		if ( !demObj.isGrayScaled ) {
			curTexture.disable(gl);
		}

	}

	private double GetMaxValue(final double[] gridValue) {
		double maxValue = 0.0;
		if ( gridValue == null ) { return maxValue; }
		for ( int i = 0; i < gridValue.length; i++ ) {
			if ( gridValue[i] > maxValue ) {
				maxValue = gridValue[i];
			}
		}
		return maxValue;
	}

	protected void drawFromImage(final DEMObject demObj, final GL2 gl) {

		int rows, cols;
		int x, y;
		float vx, vy, s, t;
		float ts, tt, tw, th;

		BufferedImage dem = demObj.demImg;
		Texture curTexture = demObj.getTexture(gl, renderer);
		if ( curTexture == null ) { return; }
		// Enable the texture

		curTexture.enable(gl);
		curTexture.bind(gl);
		rows = dem.getHeight() - 1;
		cols = dem.getWidth() - 1;
		ts = 1.0f / cols;
		tt = 1.0f / rows;

		// FIXME/ need to set w and h dynamicly
		float w = (float) demObj.envelope.getWidth();
		float h = (float) demObj.envelope.getHeight();
		float altFactor = (float) demObj.envelope.getDepth();

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

	// public boolean isInitialized() {
	// return initialized;
	// }
	//
	// public void setInitialized(final boolean initialized) {
	// this.initialized = initialized;
	// }

}