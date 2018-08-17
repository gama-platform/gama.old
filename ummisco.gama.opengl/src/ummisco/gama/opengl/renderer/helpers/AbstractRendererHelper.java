package ummisco.gama.opengl.renderer.helpers;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.swt.GLCanvas;

import msi.gama.outputs.LayeredDisplayData;
import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;
import ummisco.gama.opengl.view.SWTOpenGLDisplaySurface;

public abstract class AbstractRendererHelper {

	private final IOpenGLRenderer renderer;

	public AbstractRendererHelper(final IOpenGLRenderer renderer) {
		this.renderer = renderer;
	}

	protected IOpenGLRenderer getRenderer() {
		return renderer;
	}

	protected LayeredDisplayData getData() {
		return renderer.getData();
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

	public double getMaxEnvDim() {
		return renderer.getMaxEnvDim();
	}

	public abstract void initialize();

}
