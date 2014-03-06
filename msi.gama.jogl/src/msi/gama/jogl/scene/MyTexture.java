package msi.gama.jogl.scene;

import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import com.sun.opengl.util.texture.Texture;

public class MyTexture {

	private final Texture texture;
	private final boolean isDynamic;

	public MyTexture(final Texture texture, final boolean isDynamic) {
		super();
		this.texture = texture;
		this.isDynamic = isDynamic;
	}

	public void bindTo(final JOGLAWTGLRenderer renderer) {
		renderer.getContext().makeCurrent();
		texture.enable();
		texture.bind();
		// renderer.getContext().release();
	}

	public void unbindFrom(final JOGLAWTGLRenderer renderer) {
		// renderer.getContext().makeCurrent();
		texture.disable();
	}

	public boolean isDynamic() {
		return isDynamic;
	}

	public Texture getTexture() {
		return texture;
	}

	public void dispose() {
		texture.dispose();
	}

}
