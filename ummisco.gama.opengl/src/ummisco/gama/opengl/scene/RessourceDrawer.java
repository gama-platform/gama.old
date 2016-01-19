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

import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.jts.*;
import ummisco.gama.opengl.utils.*;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLContext;
import com.vividsolutions.jts.geom.*;

/**
 * 
 * The class GeometryDrawer.
 * 
 * @author drogoul
 * @since 4 mai 2013
 * 
 */
public class RessourceDrawer extends ObjectDrawer<RessourceObject> {

	JTSDrawer jtsDrawer;

	public RessourceDrawer(final JOGLRenderer r) {
		super(r);
		jtsDrawer = new JTSDrawer(r);
	}



	@Override
	protected void _draw(GL2 gl, RessourceObject object) {
		jtsDrawer.drawGeometryCached(object.file);	
	}
}