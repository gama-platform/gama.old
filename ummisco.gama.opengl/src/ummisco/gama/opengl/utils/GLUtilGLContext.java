package ummisco.gama.opengl.utils;

import com.jogamp.opengl.GL2;

public class GLUtilGLContext {
	
	static float[] CurrentColor = new float[4];
	
	public static float[] GetCurrentColor() {
		return CurrentColor.clone();
	}
	
	public static void SetCurrentColor(final GL2 gl, final float[] color) {
		if (color.length == 1) {
			CurrentColor[0] = color[0];
			CurrentColor[1] = color[0];
			CurrentColor[2] = color[0];
			CurrentColor[3] = 1;
		}
		else if (color.length >= 3) {
			CurrentColor[0] = color[0];
			CurrentColor[1] = color[1];
			CurrentColor[2] = color[2];
			CurrentColor[3] = 1;
			if (color.length == 4) {
				CurrentColor[3] = color[3];
			}
		}
		else {
			return;
		}
		
		gl.glColor4f(CurrentColor[0], CurrentColor[1],
				CurrentColor[2], CurrentColor[3]);
	}
}
