/*********************************************************************************************
 *
 * 'ObjectDrawer.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2GL3;

import ummisco.gama.opengl.JOGLRenderer;

public abstract class ObjectDrawer<T extends AbstractObject> {

	final JOGLRenderer renderer;

	public ObjectDrawer(final JOGLRenderer r) {
		renderer = r;
	}

	// Better to subclass _draw than this one
	void draw(final GL2 gl, final T object) {
		gl.glPushMatrix();
		if (renderer.data.isZ_fighting()) {
			setPolygonOffset(object, gl);
		}
		_draw(gl, object);
		gl.glPopMatrix();
	}

	void setPolygonOffset(final T object, final GL2 gl) {
		if (!object.isFilled() || renderer.data.isTriangulation()) {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
			gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
			gl.glEnable(GL2GL3.GL_POLYGON_OFFSET_LINE);
			gl.glPolygonOffset(0.0f, -((float) object.getZ_fighting_id() + 0.1f));
		} else {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
			gl.glDisable(GL2GL3.GL_POLYGON_OFFSET_LINE);
			gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
			gl.glPolygonOffset(1, (float) -object.getZ_fighting_id());
		}
	}

	protected abstract void _draw(GL2 gl, T object);

	public void dispose() {
	}

	public JOGLRenderer getRenderer() {
		return renderer;
	}

}
