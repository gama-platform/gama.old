package msi.gama.jogl.scene;

import javax.media.opengl.GL;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;

import static javax.media.opengl.GL.*;
import java.awt.Font;
import java.awt.image.*;
import java.io.File;
import java.util.*;
import javax.media.opengl.*;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import msi.gama.jogl.utils.*;
import msi.gama.jogl.utils.JTSGeometryOpenGLDrawer.JTSDrawer;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.*;
import com.vividsolutions.jts.geom.*;


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
			renderer.gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
			renderer.gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
			renderer.gl.glEnable(GL2GL3.GL_POLYGON_OFFSET_LINE);
			renderer.gl.glPolygonOffset(0.0f, -(object.getZ_fighting_id().floatValue() + 0.1f));
		} else {
			renderer.gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_FILL);
			renderer.gl.glDisable(GL2GL3.GL_POLYGON_OFFSET_LINE);
			renderer.gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
			renderer.gl.glPolygonOffset(1, -object.getZ_fighting_id().floatValue());
		}
	}

	protected abstract void _draw(T object);

	public void dispose() {}

	public GL2 getGL() {
		return renderer.gl;
	}

	public JOGLAWTGLRenderer getRenderer() {
		return renderer;
	}

}
