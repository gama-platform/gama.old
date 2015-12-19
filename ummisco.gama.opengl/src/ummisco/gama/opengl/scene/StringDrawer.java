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

import java.awt.Font;
import java.util.*;
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
	static final boolean USE_VERTEX_ARRAYS = false;
	Map<String, Map<Integer, Map<Integer, TextRenderer>>> cache = new LinkedHashMap();

	TextRenderer get(final String font, final int size, final int style) {
		Map<Integer, Map<Integer, TextRenderer>> map1 = cache.get(font);
		if ( map1 == null ) {
			map1 = new HashMap();
			cache.put(font, map1);
		}
		Map<Integer, TextRenderer> map2 = map1.get(size);
		if ( map2 == null ) {
			map2 = new HashMap();
			map1.put(size, map2);
		}
		TextRenderer r = map2.get(style);
		if ( r == null ) {
			r = new TextRenderer(new Font(font, style, size), true, true, null, true);
			r.setSmoothing(true);
			r.setUseVertexArrays(USE_VERTEX_ARRAYS);
			map2.put(style, r);
		}
		return r;
	}

	public StringDrawer(final JOGLRenderer r) {
		super(r);
	}

	@Override
	public void draw(final GL2 gl, final StringObject object) {
		_draw(gl, object);
	}

	@Override
	protected void _draw(final GL2 gl, final StringObject s) {
		float x = (float) ((float) s.location.x * s.getScale().x + s.getOffset().x);
		float y = (float) ((float) s.location.y * s.getScale().y - s.getOffset().y);
		float z = (float) ((float) s.location.z * s.getScale().z + s.getOffset().z);
		// GL2 gl = GLContext.getCurrentGL().getGL2();
		if ( s.bitmap == true ) {
			gl.glPushMatrix();
			TextRenderer r = get(s.font, s.size, s.style);
			r.setColor(s.getColor());
			r.begin3DRendering();
			r.draw3D(s.string, x, y, z, (float) (renderer.data.getEnvHeight() /
				(renderer.getHeight() * renderer.displaySurface.getZoomLevel())));
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
		cache.clear();
	}
}