/**
 * Created by drogoul, 16 mars 2014
 * 
 */
package msi.gama.jogl.scene;

import static javax.media.opengl.GL.*;
import java.awt.Font;
import java.util.*;
import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.j2d.TextRenderer;

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

	public StringDrawer(final JOGLAWTGLRenderer r) {
		super(r);
	}

	@Override
	public void draw(final StringObject object) {
		_draw(object);
	}

	@Override
	protected void _draw(final StringObject s) {
		float x = (float) ((float) s.location.x * s.getScale().x + s.getOffset().x);
		float y = (float) ((float) s.location.y * s.getScale().y - s.getOffset().y);
		float z = (float) ((float) s.location.z * s.getScale().z + s.getOffset().z);

		if ( s.bitmap == true ) {
			renderer.gl.glPushMatrix();
			TextRenderer r = get(s.font, s.size, s.style);
			r.setColor(s.getColor());
			r.begin3DRendering();
			r.draw3D(s.string, x, y, z, (float) (renderer.displaySurface.getEnvHeight() / renderer.getHeight()));
			r.end3DRendering();
			renderer.gl.glPopMatrix();
		} else {
			renderer.gl.glPushMatrix();
			renderer.gl.glDisable(GL_LIGHTING);

			renderer.gl.glDisable(GL_BLEND);
		
			renderer.gl.glColor4d(s.getColor().getRed() / 255.0, s.getColor().getGreen() / 255.0, s.getColor()
				.getBlue() / 255.0, s.getColor().getAlpha() / 255.0 * s.getAlpha());
			renderer.gl.glRasterPos3d(x, y, z);

			glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_10, s.string);
			// FIXME We go back to the white ??
			renderer.gl.glColor4d(1, 1, 1, 1);
			//
			renderer.gl.glEnable(GL_BLEND);
			renderer.gl.glEnable(GL_LIGHTING);
			renderer.gl.glPopMatrix();
		}

	}

	@Override
	public void dispose() {
		cache.clear();
	}
}