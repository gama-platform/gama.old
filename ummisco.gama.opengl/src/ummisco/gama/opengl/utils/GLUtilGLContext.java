package ummisco.gama.opengl.utils;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLContext;

public class GLUtilGLContext {

	static Map<GL2, float[]> colors = new ConcurrentHashMap();

	// static float[] CurrentColor = new float[4];

	public static float[] GetCurrentColor() {
		return colors.get(GLContext.getCurrentGL());
	}

	public static void SetCurrentColor(final GL2 gl, final float... newColor) {
		System.out.println("Setting " + Arrays.toString(newColor) + " for GL " + gl.getContext().getHandle());
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
}
