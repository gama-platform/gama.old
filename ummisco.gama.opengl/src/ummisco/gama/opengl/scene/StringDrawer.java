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

import com.jogamp.opengl.util.gl2.GLUT;

import msi.gama.metamodel.shape.GamaPoint;
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
	protected void _draw(final StringObject s) {
		final GamaPoint p = s.getLocation();
		if (s.getFont() != null && s.iisInPerspective()) {
			final Font f = s.getFont();
			gl.perspectiveText(s.string, f, p.x, p.y, p.z);
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
			gl.rasterText(s.string, fontToUse, p.x, p.y, p.z);
		}
	}

}