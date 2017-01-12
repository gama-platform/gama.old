/*********************************************************************************************
 *
 * 'StringDrawer.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.Font;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
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

	public StringDrawer(final JOGLRenderer r) {
		super(r);
	}

	@Override
	protected void _draw(final GL2 gl, final StringObject s) {

		final float x = (float) s.getLocation().x;
		final float y = (float) s.getLocation().y;
		final float z = (float) s.getLocation().z;

		if (s.getFont() != null && s.iisInPerspective()) {
			final float scale = 1f / (float) (renderer.getViewHeight() / renderer.getEnvHeight());
			final Font f = s.getFont();
			final TextRenderer r = renderer.getTextRendererFor(f);
			if (r == null) { return; }
			r.begin3DRendering();
			r.draw3D(s.string, x, y, z, scale);
			r.flush();
			r.end3DRendering();
		} else {
			int fontToUse = GLUT.BITMAP_HELVETICA_18;
			final Font f = s.getFont();
			if (f != null) {
				if (f.getSize() < 10) {
					fontToUse = GLUT.BITMAP_HELVETICA_10;
				} else if (f.getSize() < 16) {
					fontToUse = GLUT.BITMAP_HELVETICA_12;
				}
			}
			gl.glPushMatrix();
			gl.glDisable(GLLightingFunc.GL_LIGHTING);
			gl.glDisable(GL.GL_BLEND);
			gl.glRasterPos3d(x, y, z);
			renderer.getGlut().glutBitmapString(fontToUse, s.string);
			gl.glEnable(GL.GL_BLEND);
			gl.glEnable(GLLightingFunc.GL_LIGHTING);
			gl.glPopMatrix();
		}
	}

}