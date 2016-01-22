/*********************************************************************************************
 *
 *
 * 'StringDrawer.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import com.jogamp.opengl.*;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;
import ummisco.gama.opengl.JOGLRenderer;

/**
 *
 * The class StringDrawer.
 *
 * @author drogoul
 * @since 4 mai 2013
 *
 */

public class StringDrawer extends ObjectDrawer<StringObject> {

	// Setting it to true requires that the ModelScene handles strings outside a list (see ModelScene)
	static final boolean USE_VERTEX_ARRAYS = true;

	public StringDrawer(final JOGLRenderer r) {
		super(r);

	}

	@Override
	public void draw(final GL2 gl, final StringObject object) {
		_draw(gl, object);
	}

	@Override
	protected void _draw(final GL2 gl, final StringObject s) {
		TextRenderer r = renderer.get(s.font, s.size, s.style);
		if ( r == null ) { return; }
		float x = (float) ((float) s.location.x * s.getScale().x + s.getOffset().x);
		float y = (float) ((float) s.location.y * s.getScale().y - s.getOffset().y);
		float z = (float) ((float) s.location.z * s.getScale().z + s.getOffset().z);
		// GL2 gl = GLContext.getCurrentGL().getGL2();
		if ( s.bitmap == true ) {

			gl.glPushMatrix();

			r.setColor(s.getColor());
			r.begin3DRendering();
			r.draw3D(s.string, x, y, z, (float) (1f / renderer.getyRatioBetweenPixelsAndModelUnits()));
			r.flush();
			r.end3DRendering();
			gl.glPopMatrix();

		} else {
			gl.glPushMatrix();
			gl.glDisable(GLLightingFunc.GL_LIGHTING);

			gl.glDisable(GL.GL_BLEND);

			gl.glColor4d(s.getColor().getRed() / 255.0, s.getColor().getGreen() / 255.0, s.getColor().getBlue() / 255.0,
				s.getColor().getAlpha() / 255.0 * s.getAlpha());
			gl.glRasterPos3d(x, y, z);

			glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_24, s.string);
			// FIXME We go back to the white ??
			gl.glColor4d(1, 1, 1, 1);
			//
			gl.glEnable(GL.GL_BLEND);
			gl.glEnable(GLLightingFunc.GL_LIGHTING);
			gl.glPopMatrix();
		}

	}

	@Override
	public void dispose() {
		// cache.clear();
	}
}