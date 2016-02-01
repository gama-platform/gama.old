/*********************************************************************************************
 *
 *
 * 'GeometryDrawer.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import com.jogamp.opengl.GL2;
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.jts.JTSDrawer;

/**
 *
 * The class GeometryDrawer.
 *
 * @author drogoul
 * @since 4 mai 2013
 *
 */
public class ResourceDrawer extends ObjectDrawer<ResourceObject> {

	JTSDrawer jtsDrawer;

	public ResourceDrawer(final JOGLRenderer r) {
		super(r);
		jtsDrawer = new JTSDrawer(r);
	}

	@Override
	protected void _draw(final GL2 gl, final ResourceObject object) {
		jtsDrawer.drawGeometryCached(gl, object.file);
	}
}