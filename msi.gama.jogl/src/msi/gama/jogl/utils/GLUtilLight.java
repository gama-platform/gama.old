package msi.gama.jogl.utils;

import static javax.media.opengl.GL.GL_AMBIENT_AND_DIFFUSE;
import static javax.media.opengl.GL.GL_COLOR_MATERIAL;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_FRONT;
import static javax.media.opengl.GL.GL_POSITION;
import static javax.media.opengl.GL.GL_TRIANGLES;

import java.awt.Color;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import msi.gama.metamodel.shape.GamaPoint;

public class GLUtilLight {
	
	private static float light0Position[] = new float[4];
	private static float light1Position[] = new float[4];
	
	public static final int fogMode[] = { GL.GL_EXP, GL.GL_EXP2, GL.GL_LINEAR };

	/**
	 * 
	 * @param gl
	 * @param r red
	 * @param g green
	 * @param b blue
	 * @param a-alfa s cooficient of transparency
	 */
	public static void createAmbientLight(final GL gl, final float r, final float g, final float b, final float a) {
		gl.glEnable(GL.GL_LIGHTING);
		float colors[] = { r, g, b, a };
		gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, colors, 0);
	}

	/**
	 * 
	 * @param gl
	 * @param colors
	 * @param position
	 * @param n_ofLight
	 */
	public static void createDiffuseLight(final GL gl, final float colors[], final float position[], final int n_ofLight) {
		switch (n_ofLight) {
			case 0: {
				gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, colors, 0);
				gl.glEnable(GL.GL_LIGHT0);
			}
				break;
			case 1: {
				gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, colors, 0);
				gl.glEnable(GL.GL_LIGHT1);
			}
				break;
			case 2: {
				gl.glLightfv(GL.GL_LIGHT2, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT2, GL.GL_DIFFUSE, colors, 0);
				gl.glEnable(GL.GL_LIGHT2);
			}
				break;
			case 3: {
				gl.glLightfv(GL.GL_LIGHT3, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT3, GL.GL_DIFFUSE, colors, 0);
				gl.glEnable(GL.GL_LIGHT3);
			}
				break;
			case 4: {
				gl.glLightfv(GL.GL_LIGHT4, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT4, GL.GL_DIFFUSE, colors, 0);
				gl.glEnable(GL.GL_LIGHT4);
			}
				break;
			case 5: {
				gl.glLightfv(GL.GL_LIGHT5, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT5, GL.GL_DIFFUSE, colors, 0);
				gl.glEnable(GL.GL_LIGHT5);
			}
				break;
			case 6: {
				gl.glLightfv(GL.GL_LIGHT6, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT6, GL.GL_DIFFUSE, colors, 0);
				gl.glEnable(GL.GL_LIGHT6);
			}
				break;
			case 7: {
				gl.glLightfv(GL.GL_LIGHT7, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT7, GL.GL_DIFFUSE, colors, 0);
				gl.glEnable(GL.GL_LIGHT7);
			}
				break;
			default:
				System.out.println("Error:openGL has avialable only 8 lights, the first id light0.");
		}

	}// end of create Diffuse Light

	/**
	 * Default color-white and position 100 in each direction
	 * @param gl
	 * @param colors
	 * @param position
	 * @param n_ofLight
	 */
	public static void createDiffuseLight(final GL gl, final int n_ofLight, final float pos) {
		float colors[] = { 1, 1, 1, 1 };
		float position[] = { pos, pos, pos, 1 };

		switch (n_ofLight) {
			case 0: {
				gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, colors, 0);
				gl.glEnable(GL.GL_LIGHT0);
			}
				break;
			case 1: {
				gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, colors, 0);
				gl.glEnable(GL.GL_LIGHT1);
			}
				break;
			case 2: {
				gl.glLightfv(GL.GL_LIGHT2, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT2, GL.GL_DIFFUSE, colors, 0);
				gl.glEnable(GL.GL_LIGHT2);
			}
				break;
			case 3: {
				gl.glLightfv(GL.GL_LIGHT3, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT3, GL.GL_DIFFUSE, colors, 0);
				gl.glEnable(GL.GL_LIGHT3);
			}
				break;
			case 4: {
				gl.glLightfv(GL.GL_LIGHT4, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT4, GL.GL_DIFFUSE, colors, 0);
				gl.glEnable(GL.GL_LIGHT4);
			}
				break;
			case 5: {
				gl.glLightfv(GL.GL_LIGHT5, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT5, GL.GL_DIFFUSE, colors, 0);
				gl.glEnable(GL.GL_LIGHT5);
			}
				break;
			case 6: {
				gl.glLightfv(GL.GL_LIGHT6, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT6, GL.GL_DIFFUSE, colors, 0);
				gl.glEnable(GL.GL_LIGHT6);
			}
				break;
			case 7: {
				gl.glLightfv(GL.GL_LIGHT7, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT7, GL.GL_DIFFUSE, colors, 0);
				gl.glEnable(GL.GL_LIGHT7);
			}
				break;
			default:
				System.out.println("Error:openGL has avialable only 8 lights, the first id light0.");
		}
	}// end of create Diffuse Light

	public static void DrawColorTriangle(final GL gl, final float x, final float y, final float z, final float alpha,
		final float size) {
		// ----- Render a triangle -----
		gl.glTranslatef(x, y, z); // translate left and into the screen
		gl.glBegin(GL_TRIANGLES); // draw using triangles
		gl.glNormal3f(0.0f, 0.0f, -1.0f);
		gl.glColor4f(1.0f, 0.0f, 0.0f, alpha); // Red
		gl.glVertex3f(0.0f, size, 0.0f);
		gl.glColor4f(0.0f, 1.0f, 0.0f, alpha); // Green
		gl.glVertex3f(-size, -size, 0.0f);
		gl.glColor4f(0.0f, 0.0f, 1.0f, alpha); // Blue
		gl.glVertex3f(size, -size, 0.0f);
		gl.glEnd();
		gl.glTranslatef(-x, -y, -z); // retranslate right and into the screen
	}

	/**
	 * 
	 * @param gl
	 * @param isSoft if is false then surfaces wont flash
	 * @param alfa - determines black-white-shade color of light R=G=B=alfa
	 * @param size takse vaules from 0 to 128 and determines size of flash when soft body
	 */
	public static void createSoftMaterial(final GL gl, final boolean isSoft, float alfa, int size) {
		if ( isSoft ) {
			float cooficientColor[] = { alfa, alfa, alfa, 1 };
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, cooficientColor, 0);
			gl.glMateriali(GL.GL_FRONT, GL.GL_SHININESS, size);

		} else {
			alfa = 0;
			size = 0;
			float cooficientColor[] = { alfa, alfa, alfa, 1 };
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, cooficientColor, 0);
			gl.glMateriali(GL.GL_FRONT, GL.GL_SHININESS, size);
		}
	}

	/**
	 * 
	 * @param gl
	 * @param disp_color dispersion color
	 * @param flash_color
	 * @param position
	 * @param direction
	 * @param size a width of light
	 * @param alfa a blank cooficient
	 * @param n_ofLight
	 */
	public static void createDirectionLight(final GL gl, final float disp_color[], final float flash_color[],
		final float position[], final float direction[], final float size, final float alfa, final int n_ofLight) {
		switch (n_ofLight) {
			case 0: {
				gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, disp_color, 0);
				gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, flash_color, 0);

				gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPOT_DIRECTION, direction, 0);
				gl.glLightf(GL.GL_LIGHT0, GL.GL_SPOT_CUTOFF, size);
				gl.glLightf(GL.GL_LIGHT0, GL.GL_SPOT_EXPONENT, alfa);
				gl.glEnable(GL.GL_LIGHT0);
			}
				break;
			case 1: {
				gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, disp_color, 0);
				gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPECULAR, flash_color, 0);

				gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPOT_DIRECTION, direction, 0);
				gl.glLightf(GL.GL_LIGHT1, GL.GL_SPOT_CUTOFF, size);
				gl.glLightf(GL.GL_LIGHT1, GL.GL_SPOT_EXPONENT, alfa);
				gl.glEnable(GL.GL_LIGHT1);
			}
				break;
			case 2: {
				gl.glLightfv(GL.GL_LIGHT2, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT2, GL.GL_DIFFUSE, disp_color, 0);
				gl.glLightfv(GL.GL_LIGHT2, GL.GL_SPECULAR, flash_color, 0);

				gl.glLightfv(GL.GL_LIGHT2, GL.GL_SPOT_DIRECTION, direction, 0);
				gl.glLightf(GL.GL_LIGHT2, GL.GL_SPOT_CUTOFF, size);
				gl.glLightf(GL.GL_LIGHT2, GL.GL_SPOT_EXPONENT, alfa);
				gl.glEnable(GL.GL_LIGHT2);
			}
				break;
			case 3: {
				gl.glLightfv(GL.GL_LIGHT3, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT3, GL.GL_DIFFUSE, disp_color, 0);
				gl.glLightfv(GL.GL_LIGHT3, GL.GL_SPECULAR, flash_color, 0);

				gl.glLightfv(GL.GL_LIGHT3, GL.GL_SPOT_DIRECTION, direction, 0);
				gl.glLightf(GL.GL_LIGHT3, GL.GL_SPOT_CUTOFF, size);
				gl.glLightf(GL.GL_LIGHT3, GL.GL_SPOT_EXPONENT, alfa);
				gl.glEnable(GL.GL_LIGHT3);
			}
				break;
			case 4: {
				gl.glLightfv(GL.GL_LIGHT4, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT4, GL.GL_DIFFUSE, disp_color, 0);
				gl.glLightfv(GL.GL_LIGHT4, GL.GL_SPECULAR, flash_color, 0);

				gl.glLightfv(GL.GL_LIGHT4, GL.GL_SPOT_DIRECTION, direction, 0);
				gl.glLightf(GL.GL_LIGHT4, GL.GL_SPOT_CUTOFF, size);
				gl.glLightf(GL.GL_LIGHT4, GL.GL_SPOT_EXPONENT, alfa);
				gl.glEnable(GL.GL_LIGHT4);
			}
				break;
			case 5: {
				gl.glLightfv(GL.GL_LIGHT5, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT5, GL.GL_DIFFUSE, disp_color, 0);
				gl.glLightfv(GL.GL_LIGHT5, GL.GL_SPECULAR, flash_color, 0);

				gl.glLightfv(GL.GL_LIGHT5, GL.GL_SPOT_DIRECTION, direction, 0);
				gl.glLightf(GL.GL_LIGHT5, GL.GL_SPOT_CUTOFF, size);
				gl.glLightf(GL.GL_LIGHT5, GL.GL_SPOT_EXPONENT, alfa);
				gl.glEnable(GL.GL_LIGHT5);
			}
				break;
			case 6: {
				gl.glLightfv(GL.GL_LIGHT6, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT6, GL.GL_DIFFUSE, disp_color, 0);
				gl.glLightfv(GL.GL_LIGHT6, GL.GL_SPECULAR, flash_color, 0);

				gl.glLightfv(GL.GL_LIGHT6, GL.GL_SPOT_DIRECTION, direction, 0);
				gl.glLightf(GL.GL_LIGHT6, GL.GL_SPOT_CUTOFF, size);
				gl.glLightf(GL.GL_LIGHT6, GL.GL_SPOT_EXPONENT, alfa);
				gl.glEnable(GL.GL_LIGHT6);
			}
				break;
			case 7: {
				gl.glLightfv(GL.GL_LIGHT7, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT7, GL.GL_DIFFUSE, disp_color, 0);
				gl.glLightfv(GL.GL_LIGHT7, GL.GL_SPECULAR, flash_color, 0);

				gl.glLightfv(GL.GL_LIGHT7, GL.GL_SPOT_DIRECTION, direction, 0);
				gl.glLightf(GL.GL_LIGHT7, GL.GL_SPOT_CUTOFF, size);
				gl.glLightf(GL.GL_LIGHT7, GL.GL_SPOT_EXPONENT, alfa);
				gl.glEnable(GL.GL_LIGHT7);
			}
				break;
			default:
				System.out.println("Error:openGL has avialable only 8 lights, the first id light0.");
		}

	}// END LIGHT DIRECTION

	/**
	 * A shorter version method which create DirectionalLight. Size of bundle light has 30,
	 * alfa is 1 and disp&flash color are white
	 * @param gl
	 * @param position
	 * @param direction
	 * @param n_ofLight
	 */
	public static void createDirectionLight(final GL gl, final float position[], final float direction[],
		final int n_ofLight) {
		float disp_color[] = { 1, 1, 1, 1 };
		float flash_color[] = { 1, 1, 1, 1 };
		float size = 30;
		float alfa = 1;
		switch (n_ofLight) {
			case 0: {
				gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, disp_color, 0);
				gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR, flash_color, 0);

				gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPOT_DIRECTION, direction, 0);
				gl.glLightf(GL.GL_LIGHT0, GL.GL_SPOT_CUTOFF, size);
				gl.glLightf(GL.GL_LIGHT0, GL.GL_SPOT_EXPONENT, alfa);
				gl.glEnable(GL.GL_LIGHT0);
			}
				break;
			case 1: {
				gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, disp_color, 0);
				gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPECULAR, flash_color, 0);

				gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPOT_DIRECTION, direction, 0);
				gl.glLightf(GL.GL_LIGHT1, GL.GL_SPOT_CUTOFF, size);
				gl.glLightf(GL.GL_LIGHT1, GL.GL_SPOT_EXPONENT, alfa);
				gl.glEnable(GL.GL_LIGHT1);
			}
				break;
			case 2: {
				gl.glLightfv(GL.GL_LIGHT2, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT2, GL.GL_DIFFUSE, disp_color, 0);
				gl.glLightfv(GL.GL_LIGHT2, GL.GL_SPECULAR, flash_color, 0);

				gl.glLightfv(GL.GL_LIGHT2, GL.GL_SPOT_DIRECTION, direction, 0);
				gl.glLightf(GL.GL_LIGHT2, GL.GL_SPOT_CUTOFF, size);
				gl.glLightf(GL.GL_LIGHT2, GL.GL_SPOT_EXPONENT, alfa);
				gl.glEnable(GL.GL_LIGHT2);
			}
				break;
			case 3: {
				gl.glLightfv(GL.GL_LIGHT3, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT3, GL.GL_DIFFUSE, disp_color, 0);
				gl.glLightfv(GL.GL_LIGHT3, GL.GL_SPECULAR, flash_color, 0);

				gl.glLightfv(GL.GL_LIGHT3, GL.GL_SPOT_DIRECTION, direction, 0);
				gl.glLightf(GL.GL_LIGHT3, GL.GL_SPOT_CUTOFF, size);
				gl.glLightf(GL.GL_LIGHT3, GL.GL_SPOT_EXPONENT, alfa);
				gl.glEnable(GL.GL_LIGHT3);
			}
				break;
			case 4: {
				gl.glLightfv(GL.GL_LIGHT4, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT4, GL.GL_DIFFUSE, disp_color, 0);
				gl.glLightfv(GL.GL_LIGHT4, GL.GL_SPECULAR, flash_color, 0);

				gl.glLightfv(GL.GL_LIGHT4, GL.GL_SPOT_DIRECTION, direction, 0);
				gl.glLightf(GL.GL_LIGHT4, GL.GL_SPOT_CUTOFF, size);
				gl.glLightf(GL.GL_LIGHT4, GL.GL_SPOT_EXPONENT, alfa);
				gl.glEnable(GL.GL_LIGHT4);
			}
				break;
			case 5: {
				gl.glLightfv(GL.GL_LIGHT5, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT5, GL.GL_DIFFUSE, disp_color, 0);
				gl.glLightfv(GL.GL_LIGHT5, GL.GL_SPECULAR, flash_color, 0);

				gl.glLightfv(GL.GL_LIGHT5, GL.GL_SPOT_DIRECTION, direction, 0);
				gl.glLightf(GL.GL_LIGHT5, GL.GL_SPOT_CUTOFF, size);
				gl.glLightf(GL.GL_LIGHT5, GL.GL_SPOT_EXPONENT, alfa);
				gl.glEnable(GL.GL_LIGHT5);
			}
				break;
			case 6: {
				gl.glLightfv(GL.GL_LIGHT6, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT6, GL.GL_DIFFUSE, disp_color, 0);
				gl.glLightfv(GL.GL_LIGHT6, GL.GL_SPECULAR, flash_color, 0);

				gl.glLightfv(GL.GL_LIGHT6, GL.GL_SPOT_DIRECTION, direction, 0);
				gl.glLightf(GL.GL_LIGHT6, GL.GL_SPOT_CUTOFF, size);
				gl.glLightf(GL.GL_LIGHT6, GL.GL_SPOT_EXPONENT, alfa);
				gl.glEnable(GL.GL_LIGHT6);
			}
				break;
			case 7: {
				gl.glLightfv(GL.GL_LIGHT7, GL.GL_POSITION, position, 0);
				gl.glLightfv(GL.GL_LIGHT7, GL.GL_DIFFUSE, disp_color, 0);
				gl.glLightfv(GL.GL_LIGHT7, GL.GL_SPECULAR, flash_color, 0);

				gl.glLightfv(GL.GL_LIGHT7, GL.GL_SPOT_DIRECTION, direction, 0);
				gl.glLightf(GL.GL_LIGHT7, GL.GL_SPOT_CUTOFF, size);
				gl.glLightf(GL.GL_LIGHT7, GL.GL_SPOT_EXPONENT, alfa);
				gl.glEnable(GL.GL_LIGHT7);
			}
				break;
			default:
				System.out.println("Error:openGL has avialable only 8 lights, the first id light0.");
		}

	}// END LIGHT DIRECTION

	/**
	 * Creates fog on the scene.
	 * @param gl - GL object
	 * @param color - fog color
	 * @param start - begining of the fog
	 * @param end - end distance of the fog
	 * @param mode - set type of fog param GL_EXP (the default), GL_EXP2, or GL_LINEAR
	 * @param fog_hint hint specifies whether fog calculations are done per pixel (GL_NICEST) or per vertex
	 *            (GL_FASTEST).
	 */
	public static void enableFog(final GL gl, final float color[], final float start, final float end, final int mode,
		final int fog_hint, final float density) {
		gl.glEnable(GL.GL_FOG);
		gl.glFogfv(GL.GL_FOG_COLOR, color, 0);
		gl.glFogf(GL.GL_FOG_DENSITY, density);
		gl.glFogf(GL.GL_FOG_START, start);
		gl.glFogf(GL.GL_FOG_END, end);
		gl.glFogf(GL.GL_FOG_MODE, mode);
		gl.glHint(GL.GL_FOG_HINT, fog_hint);
	}

	public static void enableSmooth(final GL gl) {
		gl.glShadeModel(GL.GL_SMOOTH);
	}

	public static void enableFlat(final GL gl) {
		gl.glShadeModel(GL.GL_FLAT);
	}

	public static void enableBlend(final GL gl) {
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
	}

	public static void enableColorMaterial(final GL gl) {
		gl.glEnable(GL.GL_COLOR_MATERIAL);
		gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT_AND_DIFFUSE);
	}

	public static void enableDepthTest(final GL gl) {
		// the depth buffer & enable the depth testing
		gl.glClearDepth(1.0f);
		gl.glEnable(GL_DEPTH_TEST); // enables depth testing
		gl.glDepthFunc(GL.GL_LEQUAL); // the type of depth test to do
	}

	public static void enableLighting(final GL gl) {
		gl.glEnable(GL.GL_LIGHTING);
	}

	public static void disableFog(final GL gl) {
		gl.glDisable(GL.GL_FOG);
	}

	public static void disableLight(final GL gl) {
		gl.glDisable(GL.GL_LIGHTING);
	}

	public static void disableBlend(final GL gl) {
		gl.glDisable(GL.GL_BLEND);
	}

	public static void disableColorMaterial(final GL gl) {
		gl.glDisable(GL.GL_COLOR_MATERIAL);
	}

	public static void disableDepthTest(final GL gl) {
		gl.glDisable(GL.GL_DEPTH_TEST);
	}

	public static void disableSoftMaterial(final GL gl) {
		float cooficientColor[] = { 0, 0, 0, 1 };
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, cooficientColor, 0);
		gl.glMateriali(GL.GL_FRONT, GL.GL_SHININESS, 0);

	}

	public static void setAmbiantLight(final GL gl, final Color ambientLightValue) {
		float[] lightAmbientValue =
			{ (float) ambientLightValue.getRed() / 255, (float) ambientLightValue.getGreen() / 255,
				(float) ambientLightValue.getBlue() / 255, 1.0f };
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, lightAmbientValue, 0);
	}

	public static void setDiffuseLight(final GL gl, final Color ambientLightValue, final GamaPoint pos) {
		// Diffuse light 0
		float[] light1DiffuseValue =
			{ (float) ambientLightValue.getRed() / 255, (float) ambientLightValue.getGreen() / 255,
				(float) ambientLightValue.getBlue() / 255, 1.0f };;
		// Diffuse light location xyz (directed light)
		float light1Position[] = { (float) pos.getX(), (float) pos.getY(), (float) pos.getZ() };
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, light1DiffuseValue, 0);
		gl.glLightfv(GL.GL_LIGHT1, GL_POSITION, light1Position, 0);
	}

	public static void DrawDiffuseLight0(float[] light0Position, final GL gl, final GLU glu, final double radius) {
		gl.glTranslatef(light0Position[0], light0Position[1], light0Position[2]);
		gl.glColor3f(1.0f, 1.0f, 0.0f);
		DrawSphere(gl, glu, radius);
		gl.glTranslatef(-light0Position[0], -light0Position[1], -light0Position[2]);
	}
	
	public static void DrawDiffuseLights(final GL gl, final GLU glu, final double radius) {
		DrawLight0(gl, glu, radius);
		DrawLight1(gl, glu, radius);
	}

	public static void DrawLight0(final GL gl, final GLU glu, final double radius) {
		gl.glTranslatef(light0Position[0], light0Position[1], light0Position[2]);
		gl.glColor3f(1.0f, 1.0f, 0.0f);
		DrawSphere(gl, glu, radius);
		gl.glTranslatef(-light0Position[0], -light0Position[1], -light0Position[2]);
	}

	public static void DrawLight1(final GL gl, final GLU glu, final double radius) {
		gl.glTranslatef(light1Position[0], light1Position[1], light1Position[2]);
		gl.glColor3f(1.0f, 1.0f, 0.0f);
		DrawSphere(gl, glu, radius);
		gl.glTranslatef(-light1Position[0], -light1Position[1], -light1Position[2]);
	}

	public static void DrawSphere(final GL gl, final GLU glu, final double radius) {
		// Draw sphere (possible styles: FILL, LINE, POINT).
		GLUquadric earth = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(earth, GLU.GLU_FILL);
		glu.gluQuadricNormals(earth, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(earth, GLU.GLU_OUTSIDE);
		final int slices = 16;
		final int stacks = 16;
		glu.gluSphere(earth, radius, slices, stacks);
		glu.gluDeleteQuadric(earth);
	}

	public static void InitializeLighting(final GL gl, final GLU glu, final float widthEnv, final float heightEnv,
		final Color ambientLightValue, final Color diffuseLightValue) {

		// ambient
		float[] lightAmbientValue =
			{ (float) ambientLightValue.getRed() / 255, (float) ambientLightValue.getGreen() / 255,
				(float) ambientLightValue.getBlue() / 255, 1.0f };
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, lightAmbientValue, 0);

		// Diffuse
		float[] lightDiffuseValue =
			{ (float) diffuseLightValue.getRed() / 255, (float) diffuseLightValue.getGreen() / 255,
				(float) diffuseLightValue.getBlue() / 255, 1.0f };

		boolean use2light = false;
		//use Two lights
		if(use2light){
			light0Position[0] = widthEnv * 2;
			light0Position[1] = -heightEnv / 2;
			light0Position[2] = 2 * widthEnv;
			light0Position[3] = 0.0f;
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, lightDiffuseValue, 0);
			gl.glLightfv(GL.GL_LIGHT0, GL_POSITION, light0Position, 0);
	
			light1Position[0] = -widthEnv;
			light1Position[1] = -heightEnv / 2;
			light1Position[2] = 2 * widthEnv;
			light1Position[3] = 0.0f;
			gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, lightDiffuseValue, 0);
			gl.glLightfv(GL.GL_LIGHT1, GL_POSITION, light1Position, 0);
			
			gl.glEnable(GL.GL_LIGHT0); // Enable Light-0
			gl.glEnable(GL.GL_LIGHT1); // Enable Light-1
		}
		else{
			light0Position[0] = widthEnv / 2;
			light0Position[1] = -heightEnv / 2;
			light0Position[2] = 2 * widthEnv;
			light0Position[3] = 0.0f;
			gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, lightDiffuseValue, 0);
			gl.glLightfv(GL.GL_LIGHT0, GL_POSITION, light0Position, 0);
			gl.glEnable(GL.GL_LIGHT0);
		}

		// Specular
		float specularMean = 0.1f;
		float[] lightSpecularValue = { specularMean, specularMean, specularMean, 1f };

		//gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPECULAR, lightSpecularValue, 0);

		

		// enable color tracking
		gl.glEnable(GL_COLOR_MATERIAL);
		// set material properties which will be assigned by glColor
		gl.glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);

		float[] rgba = { 0.2f, 0.2f, 0.2f, 1f };
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, rgba, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, rgba, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, rgba, 0);
		gl.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, 0.5f);
	}

	public static void SetAmbiantLightFromValue(final GL gl, final GLU glu, final Color c) {
		float[] lightAmbientValue =
			{ (float) c.getRed() / 255, (float) c.getGreen() / 255, (float) c.getBlue() / 255, 1.0f };
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, lightAmbientValue, 0);
	}

	public static void UpdateAmbiantLightValue(final GL gl, final GLU glu, final Color ambiantLightValue) {

		float[] lightAmbientValue =
			{ (float) ambiantLightValue.getRed() / 255, (float) ambiantLightValue.getGreen() / 255,
				(float) ambiantLightValue.getBlue() / 255, 1.0f };
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, lightAmbientValue, 0);
	}

	public static void UpdateDiffuseLightValue(final GL gl, final GLU glu, final Color diffuseLightValue) {

		float[] lightDiffuseValue =
			{ (float) diffuseLightValue.getRed() / 255, (float) diffuseLightValue.getGreen() / 255,
				(float) diffuseLightValue.getBlue() / 255, 1.0f };
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, lightDiffuseValue, 0);
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, lightDiffuseValue, 0);
	}
	
	public static void UpdateDiffuseLightPosition(final GL gl, final GLU glu, float[] lightDiffusePos) {

		gl.glLightfv(GL.GL_LIGHT0, GL_POSITION, lightDiffusePos, 0);

	}

	public static void DrawLight(final GL gl, final GLU glu) {
		gl.glTranslated(light1Position[0], -light1Position[1], light1Position[2]);
		gl.glColor4f(1.0f, 1.0f, 0.0f, 1.0f);
		GLUquadric quad = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(quad, GLU.GLU_FILL);
		glu.gluQuadricNormals(quad, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(quad, GLU.GLU_OUTSIDE);
		final int slices = 16;
		final int stacks = 16;
		glu.gluSphere(quad, 1.0f, slices, stacks);
		glu.gluDeleteQuadric(quad);
		gl.glTranslated(-light1Position[0], light1Position[1], -light1Position[2]);
	}

	public static void InitializeLighting2(final GL gl) {
		// Prepare light parameters.
		float SHINE_ALL_DIRECTIONS = 1;
		float[] lightPos = { 0, 0, -10, SHINE_ALL_DIRECTIONS };
		float[] lightColorAmbient = { 1f, 1f, 1f, 1f };
		float[] lightColorSpecular = { 0.8f, 0.8f, 0.8f, 1f };

		// Set light parameters.
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, lightPos, 0);
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, lightColorAmbient, 0);
		gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPECULAR, lightColorSpecular, 0);

		// Enable lighting in GL.
		gl.glEnable(GL.GL_LIGHT1);
		gl.glEnable(GL.GL_LIGHTING);

		// Set material properties.
		float[] rgba = { 1f, 1f, 1f };
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, rgba, 0);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, rgba, 0);
		gl.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, 0.5f);
	}

	public static void setPointSize(final GL gl, final float size, final boolean smooth) {
		gl.glPointSize(size);
		if ( smooth ) {
			gl.glEnable(GL.GL_POINT_SMOOTH);
		} else {
			gl.glDisable(GL.GL_POINT_SMOOTH);
		}
	}

	public static void setLineWidth(final GL gl, final float size, final boolean smooth) {
		gl.glLineWidth(size);
		if ( smooth ) {
			gl.glEnable(GL.GL_LINE_SMOOTH);
		} else {
			gl.glDisable(GL.GL_LINE_SMOOTH);
		}
	}

	/**
	 * Set the shade model: type == 1 equals GL_SMOOTH and type == 2 equals GL_FLAT
	 * default model is SMOOTH.
	 * @param gl
	 * @param type - 1 or 2.
	 */
	public static void setShadeMode(final GL gl, final int type) {
		switch (type) {
			case 1:
				gl.glShadeModel(GL.GL_SMOOTH);
				break;
			case 2:
				gl.glShadeModel(GL.GL_FLAT);
				break;
			default:
				gl.glShadeModel(GL.GL_SMOOTH);
		}

	}

	public static void drawCircle(final GL gl, final double size, int n_vertexs) {
		if ( n_vertexs < 3 ) {
			n_vertexs = 3;
		}
		gl.glPushMatrix();
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glVertex3d(0, 0, 0);
		double angle = 2 * Math.PI / n_vertexs;
		for ( int i = 0; i < n_vertexs; i++ ) {
			gl.glVertex3d(size * Math.cos(i * angle), size * Math.sin(angle * i), 0);
		}
		gl.glVertex3d(size, 0, 0);
		gl.glEnd();
		gl.glPopMatrix();
	}

	public static void drawEmptyCircle(final GL gl, final double size, int n_vertexs) {
		if ( n_vertexs < 3 ) {
			n_vertexs = 3;
		}
		gl.glPushMatrix();
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glNormal3d(0, 0, 1);

		double angle = 2 * Math.PI / n_vertexs;
		for ( int i = 0; i < n_vertexs; i++ ) {
			gl.glVertex3d(size * Math.cos(i * angle), size * Math.sin(angle * i), 0);
		}

		gl.glEnd();
		gl.glPopMatrix();
	}

}
