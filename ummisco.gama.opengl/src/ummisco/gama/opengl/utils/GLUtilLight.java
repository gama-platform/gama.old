/*********************************************************************************************
 *
 * 'GLUtilLight.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.utils;

import java.awt.Color;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.util.gl2.GLUT;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.math.Vector3D;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.LayeredDisplayData;
import msi.gama.outputs.LightPropertiesStructure;
import msi.gama.util.GamaColor;
import msi.gaml.operators.Maths;
import ummisco.gama.opengl.Abstract3DRenderer;

public class GLUtilLight {

	public static final int fogMode[] = { GL2.GL_EXP, GL2.GL_EXP2, GL2.GL_LINEAR };

	public static void setAmbiantLight(final GL2 gl, final Color ambientLightValue) {
		final float[] lightAmbientValue = { ambientLightValue.getRed() / 255.0f, ambientLightValue.getGreen() / 255.0f,
				ambientLightValue.getBlue() / 255.0f, 1.0f };
		gl.glLightfv(GLLightingFunc.GL_LIGHT0, GLLightingFunc.GL_AMBIENT, lightAmbientValue, 0);
	}

	public static void InitializeLighting(final GL2 gl, final LayeredDisplayData data, final boolean modernRenderer) {

		// ambient
		setAmbiantLight(gl, data.getAmbientLightColor());
		// deactivate diffuse light for the light0
		data.setLightActive(0, true);
		data.setLightType(0, "direction");
		data.setDiffuseLightColor(0, new GamaColor(0, 0, 0, 0));

		// default value for diffuse light
		boolean useDefaultValueForLight1 = true;
		for (final LightPropertiesStructure lightProp : data.getDiffuseLights()) {
			if (lightProp.id == 1) {
				useDefaultValueForLight1 = false;
			}
		}
		if (useDefaultValueForLight1) {
			data.setLightActive(1, true);
			// directional light
			data.setLightType(1, "direction");
			data.setLightDirection(1, new GamaPoint(0.5, 0.5, -1, 0));
			// white color (the default value depends on the type of renderer used)
			if (modernRenderer) {
				data.setDiffuseLightColor(1, new GamaColor(255, 255, 255, 255));
			} else {
				data.setDiffuseLightColor(1, new GamaColor(127, 127, 127, 255));
			}
		}

		// set material properties which will be assigned by glColor
		gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_AMBIENT_AND_DIFFUSE);
		gl.glLightModelf(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_TRUE);
		// enable color tracking
		gl.glEnable(GLLightingFunc.GL_COLOR_MATERIAL);
	}

	public static void NotifyOpenGLTranslation(final GL2 gl, final double[] translation,
			final List<LightPropertiesStructure> lightPropertiesList, final LayeredDisplayData data) {
		for (final LightPropertiesStructure lightProperties : lightPropertiesList) {
			if (lightProperties.active) {
				final float[] position = new float[] { (float) lightProperties.position.x,
						(float) lightProperties.position.y, (float) lightProperties.position.z };
				final double[] newPos = new double[] { position[0] - translation[0], position[1] - translation[1],
						position[2] - translation[2] };
				gl.glLightfv(lightProperties.id, GLLightingFunc.GL_POSITION,
						new float[] { (float) newPos[0], (float) newPos[1], (float) newPos[2] }, 0);
				data.setLightPosition(lightProperties.id, new GamaPoint(newPos[0], newPos[1], newPos[2]));
			}
		}
	}

	public static void NotifyOpenGLRotation(final GL2 gl, final double angle, final Coordinate axis,
			final LayeredDisplayData data) {
		final List<LightPropertiesStructure> lightPropertiesList = data.getDiffuseLights();
		for (final LightPropertiesStructure lightProperties : lightPropertiesList) {
			if (lightProperties.active) {
				final Coordinate normalized = Vector3D.normalize(axis);
				// update the position
				// a, b, c are the normalized composants of the axis.
				final double a = normalized.x;
				final double b = normalized.y;
				final double c = normalized.z;
				// x, y, z are the initial position of the light.
				double x = lightProperties.position.x;
				double y = lightProperties.position.y;
				double z = lightProperties.position.z;
				if (lightProperties.type == LightPropertiesStructure.TYPE.POINT) {
					final double resultX = x * (a * a + (1 - a * a) * Maths.cos(angle))
							+ y * (a * b * (1 - Maths.cos(angle) - c * Maths.sin(angle)))
							+ z * (a * c * (1 - Maths.cos(angle) + b * Maths.sin(angle)));
					final double resultY = x * (a * b * (1 - Maths.cos(angle) + c * Maths.sin(angle)))
							+ y * (b * b + (1 - b * b) * Maths.cos(angle))
							+ z * (b * c * (1 - Maths.cos(angle)) - a * Maths.sin(angle));
					final double resultZ = x * (a * c * (1 - Maths.cos(angle)) - b * Maths.sin(angle))
							+ y * (b * c * (1 - Maths.cos(angle)) + a * Maths.sin(angle))
							+ z * (c * c + (1 - c * c) * Maths.cos(angle));
					gl.glLightfv(GL2.GL_LIGHT0 + lightProperties.id, GL2.GL_POSITION,
							new float[] { (float) resultX, (float) resultY, (float) resultZ }, 0);
					lightProperties.position = new GamaPoint(resultX, resultY, resultZ);
				}
				// update the direction
				// x, y, z are the initial position of the light.
				x = lightProperties.direction.x;
				y = -lightProperties.direction.y;
				z = lightProperties.direction.z;
				if (lightProperties.type == LightPropertiesStructure.TYPE.POINT) {
					final double resultX = x * (a * a + (1 - a * a) * Maths.cos(angle))
							+ y * (a * b * (1 - Maths.cos(angle) - c * Maths.sin(angle)))
							+ z * (a * c * (1 - Maths.cos(angle) + b * Maths.sin(angle)));
					final double resultY = x * (a * b * (1 - Maths.cos(angle) + c * Maths.sin(angle)))
							+ y * (b * b + (1 - b * b) * Maths.cos(angle))
							+ z * (b * c * (1 - Maths.cos(angle)) - a * Maths.sin(angle));
					final double resultZ = x * (a * c * (1 - Maths.cos(angle)) - b * Maths.sin(angle))
							+ y * (b * c * (1 - Maths.cos(angle)) + a * Maths.sin(angle))
							+ z * (c * c + (1 - c * c) * Maths.cos(angle));
					gl.glLightfv(GL2.GL_LIGHT0 + lightProperties.id, GL2.GL_SPOT_DIRECTION,
							new float[] { (float) resultX, (float) resultY, (float) resultZ }, 0);
					data.setLightDirection(lightProperties.id, new GamaPoint(resultX, -resultY, resultZ));
				}
			}
		}
	}

	public static void UpdateDiffuseLightValue(final GL2 gl, final Abstract3DRenderer renderer) {
		final List<LightPropertiesStructure> lightPropertiesList = renderer.data.getDiffuseLights();
		final double size = renderer.getMaxEnvDim() / 20;
		final double worldWidth = renderer.data.getEnvWidth();
		final double worldHeight = renderer.data.getEnvHeight();
		for (final LightPropertiesStructure lightProperties : lightPropertiesList) {
			if (lightProperties.active) {
				gl.glEnable(GL2.GL_LIGHT0 + lightProperties.id);
				// GET AND SET ALL THE PROPERTIES OF THE LIGHT
				// Get the type of light (direction / point / spot)
				final LightPropertiesStructure.TYPE type = lightProperties.type;
				// Get and set the color (the diffuse color)
				final float[] diffuseColor =
						{ lightProperties.color.getRed() / 255.0f, lightProperties.color.getGreen() / 255.0f,
								lightProperties.color.getBlue() / 255.0f, lightProperties.color.getAlpha() / 255.0f };
				gl.glLightfv(GL2.GL_LIGHT0 + lightProperties.id, GL2.GL_DIFFUSE, diffuseColor, 0);
				// Get and set the position
				// the 4th value of the position determines weather of not the
				// distance object-light has to be computed
				float[] lightPosition;
				if (type == LightPropertiesStructure.TYPE.DIRECTION) {
					lightPosition = new float[] { -(float) lightProperties.direction.x,
							(float) lightProperties.direction.y, -(float) lightProperties.direction.z, 0 };
				} else {
					lightPosition = new float[] { (float) lightProperties.position.x,
							-(float) lightProperties.position.y, (float) lightProperties.position.z, 1 };
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
							-(float) lightProperties.direction.y, (float) lightProperties.direction.z };
					gl.glLightfv(GL2.GL_LIGHT0 + lightProperties.id, GL2.GL_SPOT_DIRECTION, spotLight, 0);
					final float spotAngle = lightProperties.spotAngle;
					gl.glLightf(GL2.GL_LIGHT0 + lightProperties.id, GL2.GL_SPOT_CUTOFF, spotAngle);
				}

				// DRAW THE LIGHT IF NEEDED
				if (lightProperties.drawLight && lightProperties.id != 0) {
					// disable the lighting during the time the light is drawn
					gl.glDisable(GL2.GL_LIGHTING);

					// save the current color to re-set it at the end of this
					// part
					final Color currentColor = renderer.getCurrentColor();
					// change the current color to the light color (the
					// representation of the color will have the same color as
					// the light in itself)
					renderer.setCurrentColor(gl, lightProperties.color);
					final GLUT glut = new GLUT();
					final double x = lightProperties.direction.x;
					final double y = -lightProperties.direction.y;
					final double z = lightProperties.direction.z;
					final double zNorm = z / Math.sqrt(x * x + y * y + z * z);
					final double xNorm = x / Math.sqrt(x * x + y * y + z * z);
					final double yNorm = y / Math.sqrt(x * x + y * y + z * z);
					if (type == LightPropertiesStructure.TYPE.POINT) {
						gl.glPushMatrix();
						gl.glTranslated(lightPosition[0], lightPosition[1], lightPosition[2]);
						glut.glutSolidSphere(size, 16, 16);
						gl.glPopMatrix();
					} else if (type == LightPropertiesStructure.TYPE.SPOT) {
						gl.glPushMatrix();
						gl.glTranslated(lightPosition[0], lightPosition[1], lightPosition[2]);

						final double baseSize = Math.sin(Math.toRadians(lightProperties.spotAngle)) * size;

						// see :
						// http://opengl.developpez.com/tutoriels/opengl-tutorial/17-les-rotations-quaternions/
						// init vector : {0,0,-1}
						// dest vector : {x,y,z}
						// compute angle
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
					} else {
						// draw direction light : a line and an sphere at the end of the line.
						final int maxI = 3;
						final int maxJ = 3;
						for (int i = 0; i < maxI; i++) {
							for (int j = 0; j < maxJ; j++) {
								final double[] beginPoint =
										new double[] { i * worldWidth / maxI, -j * worldHeight / maxJ, size * 10 };
								final double[] endPoint = new double[] { i * worldWidth / maxI + xNorm * size * 3,
										-(j * worldHeight / maxJ) + yNorm * size * 3, size * 10 + zNorm * size * 3 };
								// draw the lines
								gl.glBegin(GL2.GL_LINES);
								// gl.glLineWidth((float) (size / 10));
								gl.glVertex3d(beginPoint[0], beginPoint[1], beginPoint[2]);
								gl.glVertex3d(endPoint[0], endPoint[1], endPoint[2]);
								gl.glEnd();
								// draw the small sphere
								gl.glPushMatrix();
								gl.glTranslated(endPoint[0], endPoint[1], endPoint[2]);
								glut.glutSolidSphere(size / 5, 16, 16);
								gl.glPopMatrix();
							}
						}
					}
					renderer.setCurrentColor(gl, currentColor);

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

}
