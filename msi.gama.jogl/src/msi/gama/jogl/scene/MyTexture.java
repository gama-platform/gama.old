/*********************************************************************************************
 * 
 *
 * 'MyTexture.java', in plugin 'msi.gama.jogl', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.jogl.scene;

import static javax.media.opengl.GL.GL_TEXTURE_2D;
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
		// renderer.getContext().makeCurrent();
		renderer.gl.glEnable(GL_TEXTURE_2D);
		texture.enable();
		texture.bind();
		// renderer.getContext().release();
	}

	public void unbindFrom(final JOGLAWTGLRenderer renderer) {
		// renderer.getContext().makeCurrent();
		renderer.gl.glDisable(GL_TEXTURE_2D);
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
