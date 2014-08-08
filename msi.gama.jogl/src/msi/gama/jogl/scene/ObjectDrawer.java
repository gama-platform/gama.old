/*********************************************************************************************
 * 
 * 
 * 'ObjectDrawer.java', in plugin 'msi.gama.jogl', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.jogl.scene;

import javax.media.opengl.GL;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import com.sun.opengl.util.GLUT;

public abstract class ObjectDrawer<T extends AbstractObject> {

	final JOGLAWTGLRenderer renderer;
	final GLUT glut = new GLUT();

	public ObjectDrawer(final JOGLAWTGLRenderer r) {
		renderer = r;
	}

	// Better to subclass _draw than this one
	void draw(final T object) {
		renderer.gl.glPushMatrix();
		if ( renderer.getZFighting() ) {
			SetPolygonOffset(object);
		}
		_draw(object);
		renderer.gl.glPopMatrix();
	}

	void SetPolygonOffset(final T object) {
		if ( !object.fill ) {
			renderer.gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
			renderer.gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
			renderer.gl.glEnable(GL.GL_POLYGON_OFFSET_LINE);
			renderer.gl.glPolygonOffset(0.0f, -(object.getZ_fighting_id().floatValue() + 0.1f));
		} else {
			renderer.gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
			renderer.gl.glDisable(GL.GL_POLYGON_OFFSET_LINE);
			renderer.gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
			renderer.gl.glPolygonOffset(1, -object.getZ_fighting_id().floatValue());
		}
	}

	protected abstract void _draw(T object);

	public void dispose() {}

	public GL getGL() {
		return renderer.gl;
	}

	public JOGLAWTGLRenderer getRenderer() {
		return renderer;
	}

}
