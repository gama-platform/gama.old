/*********************************************************************************************
 *
 * 'FPSDrawer.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
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

		if (renderer.data.isShowfps()) {
			gl.glDisable(GL.GL_BLEND);
			computeFrameRate();
			renderer.setCurrentColor(Color.black);
			gl.glRasterPos3d(-renderer.getWidth() / 10d, renderer.getHeight() / 10d, 0);
			gl.glPushMatrix();
			gl.glScaled(8.0d, 8.0d, 8.0d);
			final GLUT glut = renderer.getGlut();
			glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, "fps : " + fps);
			gl.glEnable(GL.GL_BLEND);
			gl.glPopMatrix();
		}

	}

}
