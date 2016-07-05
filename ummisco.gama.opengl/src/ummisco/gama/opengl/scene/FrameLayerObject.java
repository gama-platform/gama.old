package ummisco.gama.opengl.scene;

import java.awt.Color;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaColor;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.Abstract3DRenderer;
import ummisco.gama.opengl.JOGLRenderer;

public class FrameLayerObject extends StaticLayerObject {

	private final double startTime;
	private int frameCount = 0;
	private double currentTime = 0;
	private double previousTime = 0;
	public float fps = 00.00f;

	public FrameLayerObject(final Abstract3DRenderer renderer) {
		super(renderer);
		startTime = System.currentTimeMillis();
		setAlpha(WORLD_ALPHA);
		setOffset(WORLD_OFFSET);
		setScale(WORLD_SCALE);
	}

	public void computeFrameRate() {
		frameCount++;
		currentTime = System.currentTimeMillis() - startTime;
		final int timeInterval = (int) (currentTime - previousTime);
		if (timeInterval > 1000) {
			fps = frameCount / (timeInterval / 1000.0f);
			previousTime = currentTime;
			frameCount = 0;
		}
	}

	@Override
	public void drawWithoutShader(final GL2 gl, final JOGLRenderer renderer) {
		gl.glDisable(GL2.GL_LIGHTING);
		if (currentList.isEmpty()) {
			final double w = renderer.data.getEnvWidth();
			final double h = renderer.data.getEnvHeight();
			// add the world
			final GamaColor c = new GamaColor(150, 150, 150, 255);
			final IShape g = GamaGeometryType.buildRectangle(w, h, new GamaPoint(w / 2, h / 2));
			currentList.add(new GeometryObject(g, c, false, IShape.Type.POLYGON, this));
		}
		super.drawWithoutShader(gl, renderer);

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