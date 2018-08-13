package ummisco.gama.opengl.renderer.helpers;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.swt.GLCanvas;

import msi.gama.outputs.LayeredDisplayData;
import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.renderer.JOGLRenderer;
import ummisco.gama.opengl.view.SWTOpenGLDisplaySurface;

public abstract class AbstractRendererHelper {

	private final JOGLRenderer renderer;

	public AbstractRendererHelper(final JOGLRenderer renderer) {
		this.renderer = renderer;
	}

	protected JOGLRenderer getRenderer() {
		return renderer;
	}

	protected LayeredDisplayData getData() {
		return renderer.data;
	}

	protected GL2 getGL() {
		return renderer.getOpenGLHelper().getGL();
	}

	protected OpenGL getOpenGL() {
		return renderer.getOpenGLHelper();
	}

	protected GLCanvas getCanvas() {
		return renderer.getCanvas();
	}

	protected SWTOpenGLDisplaySurface getSurface() {
		return renderer.getSurface();
	}

	protected double getMaxEnvDim() {
		return renderer.getMaxEnvDim();
	}

	public abstract void initialize();

}
