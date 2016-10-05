package ummisco.gama.opengl.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLContext;

import msi.gama.outputs.LayeredDisplayData;

public class GLUtilGLContext {

	static Map<GL2, float[]> colors = new ConcurrentHashMap<>();

	// static float[] CurrentColor = new float[4];

	public static float[] GetCurrentColor() {
		return colors.get(GLContext.getCurrentGL());
	}

	public static void SetCurrentColor(final GL2 gl, final float... newColor) {
		if (newColor == null) {
			return;
		}
		final float[] color = new float[4];
		if (newColor.length == 1) {
			color[0] = newColor[0];
			color[1] = newColor[0];
			color[2] = newColor[0];
			color[3] = 1;
		} else if (newColor.length >= 3) {
			color[0] = newColor[0];
			color[1] = newColor[1];
			color[2] = newColor[2];
			color[3] = 1;
			if (newColor.length == 4) {
				color[3] = newColor[3];
			}
		} else {
			return;
		}
		colors.put(gl, color);
		gl.glColor4f(color[0], color[1], color[2], color[3]);
	}

	public static void TranslateContext(final GL2 gl, final double[] loc, final LayeredDisplayData data) {
		// loc in opengl coordinates (y already multiplied by Y_FLAG)
		gl.glTranslated(loc[0], loc[1], loc[2]);
		// float[] locArr = new float[] {(float) loc.x, (float)
		// (JOGLRenderer.Y_FLAG * loc.y), (float) loc.z};
		// FIXME (julien 18/05/16) : does not work for draw shape with facet
		// rotate. Decomment the following line should work, but it does not...
		// GLUtilLight.NotifyOpenGLTranslation(gl, new double[] {loc[0],
		// JOGLRenderer.Y_FLAG * loc[1], loc[2]}, data.getDiffuseLights(),
		// data);
	}

	public static void RotateContext(final GL2 gl, final double[] axis, final double angle,
			final LayeredDisplayData data) {
		// loc in opengl coordinates (y already multiplied by Y_FLAG)
		gl.glRotated(angle, axis[0], axis[1], axis[2]);
		// float[] locArr = new float[] {(float) loc.x, (float)
		// (JOGLRenderer.Y_FLAG * loc.y), (float) loc.z};
		// FIXME (julien 18/05/16) : does not work for draw shape with facet
		// rotate. Decomment the following line should work, but it does not...
		GLUtilLight.NotifyOpenGLRotation(gl, data.getDiffuseLights(), angle, axis, data);
	}
}
