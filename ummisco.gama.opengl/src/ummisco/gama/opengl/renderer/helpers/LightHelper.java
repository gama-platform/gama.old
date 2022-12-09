/*******************************************************************************************************
 *
 * LightHelper.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.renderer.helpers;

import static com.jogamp.opengl.fixedfunc.GLLightingFunc.GL_LIGHT0;

import java.awt.Color;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES1;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.util.gl2.GLUT;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.layers.properties.ILightDefinition;
import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;

/**
 * The Class LightHelper.
 */
public class LightHelper extends AbstractRendererHelper {

	/**
	 * Instantiates a new light helper.
	 *
	 * @param renderer
	 *            the renderer
	 */
	public LightHelper(final IOpenGLRenderer renderer) {
		super(renderer);
	}

	/**
	 * Sets the ambiant light.
	 *
	 * @param gl
	 *            the gl
	 * @param intensity
	 *            the ambient light value
	 */
	public void setAmbientLight(final ILightDefinition light) {
		Color c = !light.isActive() ? Color.black : light.getIntensity();
		final float[] array = { c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 1.0f };
		getGL().glLightModelfv(GL2ES1.GL_LIGHT_MODEL_AMBIENT, array, 0);
	}

	@Override
	public void initialize() {
		// ambient
		setAmbientLight(data.getLights().get(ILightDefinition.ambient));
		// set material properties which will be assigned by glColor
		getGL().glColorMaterial(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_AMBIENT_AND_DIFFUSE);
		getGL().glLightModelf(GL2ES1.GL_LIGHT_MODEL_TWO_SIDE, GL.GL_TRUE);
		getGL().glEnable(GLLightingFunc.GL_COLOR_MATERIAL);
	}

	/**
	 * Update diffuse light value.
	 *
	 * @param openGL
	 *            the open GL
	 */
	public void updateDiffuseLightValue(final OpenGL openGL) {
		setAmbientLight(data.getLights().get(ILightDefinition.ambient));
		final GL2 gl = getGL();
		final double size = getMaxEnvDim() / 20;
		final double worldWidth = getRenderer().getEnvWidth();
		final double worldHeight = getRenderer().getEnvHeight();
		getData().getLights().forEach((name, light) -> {
			if (ILightDefinition.ambient.equals(name)) return;
			int id = GL_LIGHT0 + light.getId();
			if (light.isActive()) {
				String type = light.getType();
				Color c = light.getIntensity();
				gl.glEnable(id);
				final float[] color =
						{ c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, c.getAlpha() / 255.0f };
				openGL.getGL().glLightfv(id, GLLightingFunc.GL_DIFFUSE, color, 0);
				float[] lightPosition;
				if (ILightDefinition.direction.equals(type)) {
					GamaPoint p = light.getDirection();
					lightPosition = new float[] { -(float) p.x, (float) p.y, -(float) p.z, 0 };
				} else {
					GamaPoint p = light.getLocation();
					lightPosition = new float[] { (float) p.x, -(float) p.y, (float) p.z, 1 };
				}
				gl.glLightfv(id, GLLightingFunc.GL_POSITION, lightPosition, 0);
				// Get and set the attenuation (if it is not a direction light)
				if (!ILightDefinition.direction.equals(type)) {
					final double l = light.getLinearAttenuation();
					final double q = light.getQuadraticAttenuation();
					gl.glLightf(id, GLLightingFunc.GL_LINEAR_ATTENUATION, (float) l);
					gl.glLightf(id, GLLightingFunc.GL_QUADRATIC_ATTENUATION, (float) q);
				}
				// Get and set spot properties (if the light is a spot light)
				if (ILightDefinition.spot.equals(type)) {
					GamaPoint p = light.getDirection();
					float[] spotLight = { (float) p.x, -(float) p.y, (float) p.z, 0 };
					gl.glLightfv(id, GLLightingFunc.GL_SPOT_DIRECTION, spotLight, 0);
					final double spotAngle = light.getAngle();
					gl.glLightf(id, GLLightingFunc.GL_SPOT_CUTOFF, (float) spotAngle);
				}
				if (light.isDrawing()) {
					// disable the lighting during the time the light is drawn
					final boolean previous = openGL.setObjectLighting(false);
					drawLight(openGL, size, worldWidth, worldHeight, light, lightPosition);
					openGL.setObjectLighting(previous);
				}
			} else {
				gl.glDisable(id);
			}
		});

	}

	/**
	 * Draw light.
	 *
	 * @param openGL
	 *            the open GL
	 * @param size
	 *            the size
	 * @param worldWidth
	 *            the world width
	 * @param worldHeight
	 *            the world height
	 * @param light
	 *            the light properties
	 * @param pos
	 *            the light position
	 */
	private void drawLight(final OpenGL openGL, final double size, final double worldWidth, final double worldHeight,
			final ILightDefinition light, final float[] pos) {

		// save the current color to re-set it at the end of this
		// part
		final Color currentColor = openGL.swapCurrentColor(light.getIntensity());
		// change the current color to the light color (the
		// representation of the color will have the same color as
		// the light in itself)
		final GLUT glut = new GLUT();
		GamaPoint dir = light.getDirection();
		GamaPoint dirNorm = dir.normalized();
		final String type = light.getType();
		if (type == ILightDefinition.point) {
			openGL.pushMatrix();
			openGL.translateBy(pos[0], pos[1], pos[2]);
			glut.glutSolidSphere(size, 16, 16);
			openGL.popMatrix();
		} else if (type == ILightDefinition.spot) {
			openGL.pushMatrix();
			openGL.translateBy(pos[0], pos[1], pos[2]);
			final double baseSize = Math.sin(Math.toRadians(light.getAngle())) * size;
			// compute angle
			int flag = 1;
			if (dirNorm.z < 0) { flag = -1; }
			final double cosAngle = flag * dirNorm.z;
			// compute axis : dest vect init
			GamaPoint axis = new GamaPoint(0, 0, -1);
			final double angle = Math.acos(flag * cosAngle);
			axis = GamaPoint.cross(axis, dir);
			openGL.rotateBy(Math.toDegrees(-angle) + 180, axis.x, axis.y, axis.z);
			openGL.translateBy(0, 0, -size);
			glut.glutSolidCone(baseSize, size, 16, 16);
			openGL.popMatrix();
		} else {
			// draw direction light : a line and an sphere at the end of the line.
			final int maxI = 3;
			final int maxJ = 3;
			for (int i = 0; i < maxI; i++) {
				for (int j = 0; j < maxJ; j++) {
					final double[] beginPoint = { i * worldWidth / maxI, -j * worldHeight / maxJ, size * 10 };
					final double[] endPoint = { i * worldWidth / maxI + dirNorm.x * size * 3,
							-(j * worldHeight / maxJ) - dirNorm.y * size * 3, size * 10 + dirNorm.z * size * 3 };
					// draw the lines
					openGL.beginDrawing(GL.GL_LINES);
					openGL.drawVertex(0, beginPoint[0], beginPoint[1], beginPoint[2]);
					openGL.drawVertex(0, endPoint[0], endPoint[1], endPoint[2]);
					openGL.endDrawing();
					// draw the small sphere
					openGL.pushMatrix();
					openGL.translateBy(endPoint[0], endPoint[1], endPoint[2]);
					glut.glutSolidSphere(size / 5, 16, 16);
					openGL.popMatrix();
				}
			}
		}
		openGL.setCurrentColor(currentColor);

	}

	/**
	 * Draw.
	 */
	public void draw() {
		if (isActive()) {
			final OpenGL openGL = getOpenGL();
			openGL.pushMatrix();
			updateDiffuseLightValue(openGL);
			openGL.popMatrix();
		}
	}

	/**
	 * Checks if is active.
	 *
	 * @return true, if is active
	 */
	public boolean isActive() { return getData().isLightOn(); }

}
