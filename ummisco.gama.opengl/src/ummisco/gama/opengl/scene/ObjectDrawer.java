/*********************************************************************************************
 * 
 * 
 * 'ObjectDrawer.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import ummisco.gama.opengl.JOGLRenderer;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.gl2.GLUT;

public abstract class ObjectDrawer<T extends AbstractObject> {

	final JOGLRenderer renderer;
	final GLUT glut = new GLUT();

	public ObjectDrawer(final JOGLRenderer r) {
		renderer = r;
	}

	// Better to subclass _draw than this one
	void draw(final GL2 gl, final T object) {
		// GL2 gl = GLContext.getCurrentGL().getGL2();
		gl.glPushMatrix();
		if ( renderer.data.isZ_fighting() ) {
			SetPolygonOffset(object, gl);
		}
		_draw(gl, object);
		gl.glPopMatrix();
	}

	void SetPolygonOffset(final T object, final GL2 gl) {
		if ( !object.fill ) {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
			gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
			gl.glEnable(GL2GL3.GL_POLYGON_OFFSET_LINE);
			gl.glPolygonOffset(0.0f, -(object.getZ_fighting_id().floatValue() + 0.1f));
		} else {
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
			gl.glDisable(GL2GL3.GL_POLYGON_OFFSET_LINE);
			gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
			gl.glPolygonOffset(1, -object.getZ_fighting_id().floatValue());
		}
	}

	protected abstract void _draw(GL2 gl, T object);

	public void dispose() {}

	public JOGLRenderer getRenderer() {
		return renderer;
	}

}
