package ummisco.gama.opengl.utils;

import java.awt.Color;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;

import ummisco.gama.opengl.JOGLRenderer;

public class FPSDrawer {

	private int frameCount;
	private final double startTime;
	private double currentTime;
	private double previousTime;
	private double fps;

	public FPSDrawer() {
		startTime = System.currentTimeMillis();
	}

	public void computeFrameRate() {
		frameCount++;
		currentTime = System.currentTimeMillis() - startTime;
		final int timeInterval = (int) (currentTime - previousTime);
		if (timeInterval > 1000) {
			fps = frameCount / (timeInterval / 1000d);
			previousTime = currentTime;
			frameCount = 0;
		}
	}

	public void draw(final GL2 gl, final JOGLRenderer renderer) {
		gl.glDisable(GL2.GL_LIGHTING);
		if (renderer.data.isShowfps()) {
			computeFrameRate();
			gl.glDisable(GL.GL_BLEND);
			renderer.setCurrentColor(gl, Color.black);
			gl.glRasterPos3d(-renderer.getWidth() / 10d, renderer.getHeight() / 10d, 0);
			gl.glScaled(8.0d, 8.0d, 8.0d);
			final GLUT glut = new GLUT();
			glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, "fps : " + fps);
			gl.glScaled(0.125d, 0.125d, 0.125d);
			gl.glEnable(GL.GL_BLEND);
		}
		gl.glEnable(GL2.GL_LIGHTING);
	}

}
