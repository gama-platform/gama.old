/*********************************************************************************************
 *
 *
 * 'GLUtilLight.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.utils;

import java.awt.Color;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.outputs.LightPropertiesStructure;
import msi.gama.util.GamaColor;
import ummisco.gama.opengl.JOGLRenderer;

public class GLUtilLight {

	public static final int fogMode[] = { GL2.GL_EXP, GL2.GL_EXP2, GL2.GL_LINEAR };

	/**
	 * Creates fog on the scene.
	 * 
	 * @param gl
	 *            - GL object
	 * @param color
	 *            - fog color
	 * @param start
	 *            - begining of the fog
	 * @param end
	 *            - end distance of the fog
	 * @param mode
	 *            - set type of fog param GL_EXP (the default), GL_EXP2, or
	 *            GL_LINEAR
	 * @param fog_hint
	 *            hint specifies whether fog calculations are done per pixel
	 *            (GL_NICEST) or per vertex (GL_FASTEST).
	 */
	// public static void enableFog(final GL2 gl, final float color[], final
	// float start, final float end, final int mode,
	// final int fog_hint, final float density) {
	// gl.glEnable(GL2ES1.GL_FOG);
	// gl.glFogfv(GL2ES1.GL_FOG_COLOR, color, 0);
	// gl.glFogf(GL2ES1.GL_FOG_DENSITY, density);
	// gl.glFogf(GL2ES1.GL_FOG_START, start);
	// gl.glFogf(GL2ES1.GL_FOG_END, end);
	// gl.glFogf(GL2ES1.GL_FOG_MODE, mode);
	// gl.glHint(GL2ES1.GL_FOG_HINT, fog_hint);
	// }

	public static void enableSmooth(final GL2 gl) {
		gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
	}

	// public static void enableFlat(final GL2 gl) {
	// gl.glShadeModel(GLLightingFunc.GL_FLAT);
	// }
	//
	// public static void enableBlend(final GL2 gl) {
	// gl.glEnable(GL.GL_BLEND);
	// gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
	// }
	//
	// public static void enableColorMaterial(final GL2 gl) {
	// gl.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);
	// gl.glColorMaterial(GL.GL_FRONT_AND_BACK,
	// GLLightingFunc.GL_AMBIENT_AND_DIFFUSE);
	// }

	public static void enableDepthTest(final GL2 gl) {
		// the depth buffer & enable the depth testing
		gl.glClearDepth(1.0f);
		gl.glEnable(GL.GL_DEPTH_TEST); // enables depth testing
		gl.glDepthFunc(GL.GL_LEQUAL); // the type of depth test to do
	}

	// public static void enableLighting(final GL2 gl) {
	// gl.glEnable(GLLightingFunc.GL_LIGHTING);
	// }

	// public static void disableFog(final GL2 gl) {
	// gl.glDisable(GL2ES1.GL_FOG);
	// }

	// public static void disableLight(final GL2 gl) {
	// gl.glDisable(GLLightingFunc.GL_LIGHTING);
	// }
	//
	// public static void disableBlend(final GL2 gl) {
	// gl.glDisable(GL.GL_BLEND);
	// }
	//
	// public static void disableColorMaterial(final GL2 gl) {
	// gl.glDisable(GLLightingFunc.GL_COLOR_MATERIAL);
	// }
	//
	// public static void disableDepthTest(final GL2 gl) {
	// gl.glDisable(GL.GL_DEPTH_TEST);
	// }

	// public static void disableSoftMaterial(final GL2 gl) {
	// final float cooficientColor[] = { 0, 0, 0, 1 };
	// gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_SPECULAR, cooficientColor,
	// 0);
	// gl.glMateriali(GL.GL_FRONT, GLLightingFunc.GL_SHININESS, 0);
	//
	// }

	public static void setAmbiantLight(final GL2 gl, final Color ambientLightValue) {
		final float[] lightAmbientValue = { ambientLightValue.getRed() / 255.0f, ambientLightValue.getGreen() / 255.0f,
				ambientLightValue.getBlue() / 255.0f, 1.0f };
		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_AMBIENT, lightAmbientValue, 0);
	}

	public static void setDiffuseLight(final GL2 gl, final Color diffuseLight, final GamaPoint pos) {
		// This method is deprecated !! Use "light" statement instead.
		// change the diffusion light value for GL_LIGHT1
//		final float[] light1DiffuseValue = { diffuseLight.getRed() / 255.0f, diffuseLight.getGreen() / 255.0f,
//				diffuseLight.getBlue() / 255.0f, 1.0f };
		
//		// Diffuse light location xyz (directed light)
//		final float light1Position[] = { (float) pos.getX(), (float) pos.getY(), (float) pos.getZ() };
//		gl.glLightfv(GLLightingFunc.GL_LIGHT1, GLLightingFunc.GL_DIFFUSE, light1DiffuseValue, 0);
//		gl.glLightfv(GLLightingFunc.GL_LIGHT1, GLLightingFunc.GL_POSITION, light1Position, 0);
	}

	// public static void DrawDiffuseLight0(final float[] light0Position, final
	// GL2 gl, final GLU glu, final double radius,
	// final Color diffuseLightValue) {
	// gl.glTranslatef(light0Position[0], light0Position[1], light0Position[2]);
	// GLUtilGLContext.SetCurrentColor(gl, (float) (diffuseLightValue.getRed() /
	// 255.0),
	// (float) (diffuseLightValue.getGreen() / 255.0), (float)
	// (diffuseLightValue.getBlue() / 255.0));
	// DrawSphere(gl, glu, radius);
	// gl.glTranslatef(-light0Position[0], -light0Position[1],
	// -light0Position[2]);
	// }

	public static void DrawSphere(final GL gl, final GLU glu, final double radius) {
		// Draw sphere (possible styles: FILL, LINE, POINT).
		final GLUquadric earth = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(earth, GLU.GLU_FILL);
		glu.gluQuadricNormals(earth, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(earth, GLU.GLU_OUTSIDE);
		final int slices = 16;
		final int stacks = 16;
		glu.gluSphere(earth, radius, slices, stacks);
		glu.gluDeleteQuadric(earth);
	}

	public static void InitializeLighting(final GL2 gl, LayeredDisplayData data) {

		// ambient
		Color ambientLightValue = data.getAmbientLightColor();
		final float[] lightAmbientValue = { ambientLightValue.getRed() / 255.0f, ambientLightValue.getGreen() / 255.0f,
				ambientLightValue.getBlue() / 255.0f, 1.0f };
		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_AMBIENT, lightAmbientValue, 0);
		// deactivate diffuse light for the light0
		data.setLightActive(0, true);
		data.setLightType(0, "direction");
		data.setDiffuseLightColor(0, new GamaColor(0,0,0,0));
		
		// default value for diffuse light
		boolean useDefaultValueForLight1 = true;
		for (LightPropertiesStructure lightProp : data.getDiffuseLights()) {
			if (lightProp.id == 1) {
				useDefaultValueForLight1 = false;
			}
		}
		if (useDefaultValueForLight1)
		{
			data.setLightActive(1, true);
			// directional light
			data.setLightType(1, "direction");
			data.setLightDirection(1, new GamaPoint(0.5,0.5,-1,0) );
			// white color
			data.setDiffuseLightColor(1, new GamaColor(255,255,255,255));
		}

		// set material properties which will be assigned by glColor
		gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_AMBIENT_AND_DIFFUSE);
		gl.glLightModelf(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_TRUE);
		// enable color tracking
		gl.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);

		// FIXME: Arno 02/03/2014 glMaterial is deprecated
		// http://www.felixgers.de/teaching/jogl/glColorMaterial.html
		/*
		 * float[] rgba = { 0.5f, 0.5f, 0.5f, 1.0f };
		 * gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_AMBIENT, rgba, 0);
		 * gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, rgba, 0);
		 * gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, rgba, 0);
		 * gl.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, 0.5f);
		 */
	}

	public static void UpdateAmbiantLightValue(final GL2 gl, final GLU glu, final Color ambiantLightValue) {

		final float[] lightAmbientValue = { ambiantLightValue.getRed() / 255.0f, ambiantLightValue.getGreen() / 255.0f,
				ambiantLightValue.getBlue() / 255.0f, 1.0f };
		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_AMBIENT, lightAmbientValue, 0);
	}

	public static void TranslateAllLights(final GL2 gl, final float[] translation) {
		for (int id = 0; id < 8; id++) {
			final int lightId = GLLightingFunc.GL_LIGHT0 + id;
			if (gl.glIsEnabled(lightId)) {
				final float[] position = new float[3];
				gl.glGetLightfv(lightId, GLLightingFunc.GL_POSITION, position, 0);
				gl.glLightfv(lightId, GLLightingFunc.GL_POSITION, new float[] { position[0] - translation[0],
						position[1] - translation[1], position[2] - translation[2] }, 0);
			}
		}
	}

	public static void UpdateDiffuseLightValue(final GL2 gl, final List<LightPropertiesStructure> lightPropertiesList,
			final double size) {
		for (final LightPropertiesStructure lightProperties : lightPropertiesList) {
			if (lightProperties.active) {
				gl.glEnable(GL2.GL_LIGHT0 + lightProperties.id);
				// GET AND SET ALL THE PROPERTIES OF THE LIGHT
				// Get the type of light (direction / point / spot)
				final LightPropertiesStructure.TYPE type = lightProperties.type;
				// Get and set the color (the diffuse color)
				final float[] diffuseColor = { lightProperties.color.getRed() / 255.0f,
						lightProperties.color.getGreen() / 255.0f, lightProperties.color.getBlue() / 255.0f,
						lightProperties.color.getAlpha() / 255.0f };
				gl.glLightfv(GL2.GL_LIGHT0 + lightProperties.id, GL2.GL_DIFFUSE, diffuseColor, 0);
				// Get and set the position
				// the 4th value of the position determines weather of not the
				// distance object-light has to be computed
				float[] lightPosition;
				if (type == LightPropertiesStructure.TYPE.DIRECTION) {
					lightPosition = new float[] { -(float) lightProperties.direction.x,
							-JOGLRenderer.Y_FLAG * (float) lightProperties.direction.y,
							-(float) lightProperties.direction.z, 0 };
				} else {
					lightPosition = new float[] { (float) lightProperties.position.x,
							JOGLRenderer.Y_FLAG * (float) lightProperties.position.y,
							(float) lightProperties.position.z, 1 };
				}
				gl.glLightfv(GL2.GL_LIGHT0 + lightProperties.id, GL2.GL_POSITION, lightPosition, 0);
				// Get and set the attenuation (if it is not a direction light)
				if (type != LightPropertiesStructure.TYPE.DIRECTION) {
					final float linearAttenuation = lightProperties.linearAttenuation;
					final float quadraticAttenuation = lightProperties.quadraticAttenuation;
					gl.glLightf(GL2.GL_LIGHT0 + lightProperties.id, GL2.GL_LINEAR_ATTENUATION, linearAttenuation);
					gl.glLightf(GL2.GL_LIGHT0 + lightProperties.id, GL2.GL_QUADRATIC_ATTENUATION, quadraticAttenuation);
				}
				// Get and set spot properties (if the light is a spot light)
				if (type == LightPropertiesStructure.TYPE.SPOT) {
					final float[] spotLight = { (float) lightProperties.direction.x,
							JOGLRenderer.Y_FLAG * (float) lightProperties.direction.y,
							(float) lightProperties.direction.z };
					gl.glLightfv(GL2.GL_LIGHT0 + lightProperties.id, GL2.GL_SPOT_DIRECTION, spotLight, 0);
					final float spotAngle = lightProperties.spotAngle;
					gl.glLightf(GL2.GL_LIGHT0 + lightProperties.id, GL2.GL_SPOT_CUTOFF, spotAngle);
				}

				// DRAW THE LIGHT IF NEEDED
				if (lightProperties.drawLight) {
					// disable the lighting during the time the light is drawn
					gl.glDisable(GL2.GL_LIGHTING);

					// save the current color to re-set it at the end of this
					// part
					final float[] currentColor = GLUtilGLContext.GetCurrentColor();
					// change the current color to the light color (the
					// representation of the color will have the same color as
					// the light in itself)
					GLUtilGLContext.SetCurrentColor(gl, diffuseColor);
					final GLUT glut = new GLUT();
					if (type == LightPropertiesStructure.TYPE.POINT) {
						gl.glPushMatrix();
						gl.glTranslated(lightPosition[0], lightPosition[1], lightPosition[2]);
						glut.glutSolidSphere(size, 16, 16);
						gl.glPopMatrix();
					} else if (type == LightPropertiesStructure.TYPE.SPOT) {
						gl.glPushMatrix();
						gl.glTranslated(lightPosition[0], lightPosition[1], lightPosition[2]);

						final double baseSize = Math.sin(Math.toRadians(lightProperties.spotAngle) / 2) * size;

						final double x = lightProperties.direction.x;
						final double y = lightProperties.direction.y * JOGLRenderer.Y_FLAG;
						final double z = lightProperties.direction.z;

						// see :
						// http://opengl.developpez.com/tutoriels/opengl-tutorial/17-les-rotations-quaternions/
						// init vector : {0,0,-1}
						// dest vector : {x,y,z}
						// compute angle
						final double xNorm = x / Math.sqrt(x * x + y * y + z * z);
						final double yNorm = y / Math.sqrt(x * x + y * y + z * z);
						final double zNorm = z / Math.sqrt(x * x + y * y + z * z);
						int flag = 1;
						if (zNorm < 0) {
							flag = -1;
						}
						final double cosAngle = flag * zNorm;

						// compute axis : dest vect init
						double[] axis = new double[3];

						final double angle = Math.acos(flag * cosAngle);
						axis = CrossProduct(new double[] { 0, 0, -1 }, new double[] { x, y, z });

						// apply the rotation
						gl.glRotated(Math.toDegrees(-angle) + 180, axis[0], axis[1], axis[2]);

						// translate to put the summit of the cone as rotation
						// point.
						gl.glTranslated(0, 0, -size);

						glut.glutSolidCone(baseSize, size, 16, 16);
						gl.glPopMatrix();
					}
					GLUtilGLContext.SetCurrentColor(gl, currentColor);

					gl.glEnable(GL2.GL_LIGHTING);
				}
			} else {
				gl.glDisable(GL2.GL_LIGHT0 + lightProperties.id);
			}
		}
	}

	public static double[] CrossProduct(final double[] vect1, final double[] vect2) {
		final double[] result = new double[3];
		result[0] = vect1[1] * vect2[2] - vect1[2] * vect2[1];
		result[1] = vect1[2] * vect2[0] - vect1[0] * vect2[2];
		result[2] = vect1[0] * vect2[1] - vect1[1] * vect2[0];
		return result;
	}

	public static void setLineWidth(final GL gl, final float size, final boolean smooth) {
		// smooth should be set to false always, as it creates jagged lines
		gl.glLineWidth(size);
		if (smooth) {
			gl.glEnable(GL.GL_LINE_SMOOTH);
			// gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_NICEST);
		} else {
			gl.glDisable(GL.GL_LINE_SMOOTH);
		}
	}

	/**
	 * Set the shade model: type == 1 equals GL_SMOOTH and type == 2 equals
	 * GL_FLAT default model is SMOOTH.
	 * 
	 * @param gl
	 * @param type
	 *            - 1 or 2.
	 */
	// public static void setShadeMode(final GL2 gl, final int type) {
	// switch (type) {
	// case 1:
	// gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
	// break;
	// case 2:
	// gl.glShadeModel(GLLightingFunc.GL_FLAT);
	// break;
	// default:
	// gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
	// }
	//
	// }

	// public static void drawCircle(final GL2 gl, final double size, int
	// n_vertexs) {
	// if ( n_vertexs < 3 ) {
	// n_vertexs = 3;
	// }
	// gl.glPushMatrix();
	// gl.glBegin(GL.GL_TRIANGLE_FAN);
	// gl.glVertex3d(0, 0, 0);
	// double angle = 2 * CmnFastMath.PI / n_vertexs;
	// for ( int i = 0; i < n_vertexs; i++ ) {
	// gl.glVertex3d(size * FastMath.cos(i * angle), size * FastMath.sin(angle *
	// i), 0);
	// }
	// gl.glVertex3d(size, 0, 0);
	// gl.glEnd();
	// gl.glPopMatrix();
	// }

	// public static void drawEmptyCircle(final GL2 gl, final double size, int
	// n_vertexs) {
	// if ( n_vertexs < 3 ) {
	// n_vertexs = 3;
	// }
	// gl.glPushMatrix();
	// gl.glBegin(GL.GL_LINE_LOOP);
	// gl.glNormal3d(0, 0, 1);
	//
	// double angle = 2 * CmnFastMath.PI / n_vertexs;
	// for ( int i = 0; i < n_vertexs; i++ ) {
	// gl.glVertex3d(size * FastMath.cos(i * angle), size * FastMath.sin(angle *
	// i), 0);
	// }
	//
	// gl.glEnd();
	// gl.glPopMatrix();
	// }

}
