/*********************************************************************************************
 *
 *
 * 'MyTexture.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.*;
import com.jogamp.opengl.GL2;
import com.vividsolutions.jts.geom.Polygon;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.util.file.*;
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.files.GamaObjFile;
import ummisco.gama.opengl.jts.JTSDrawer;

public class GeometryCache {

	boolean sync = false;
	private final Map<String, Integer> cache;

	Integer openNestedGLListIndex;
	JTSDrawer drawer;

	public GeometryCache(final JOGLRenderer renderer) {
		cache = new HashMap<String, Integer>(100, 0.75f);
		drawer = new JTSDrawer(renderer);
	}

	public Integer get(final GL2 gl, final GamaFile file) {
		Integer index = cache.get(file.getPath());
		if ( index == null ) {
			try {
				index = buildList(gl, file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			cache.put(file.getPath(), index);
		}
		return index;
	}

	private Integer buildList(final GL2 gl, final GamaFile file) throws FileNotFoundException {
		String extension = file.getExtension();
		// We generate the list first
		Integer index = gl.glGenLists(1);
		gl.glNewList(index, GL2.GL_COMPILE);
		// We push the matrix
		gl.glPushMatrix();
		// We draw the file in the list
		if ( extension.equals("obj") ) {
			((GamaObjFile) file).drawToOpenGL(gl);
		} else if ( extension.equals("svg") ) {
			GamaSVGFile svg = (GamaSVGFile) file;
			GamaShape g = (GamaShape) svg.getGeometry(null);
			Color c = new Color(0, 0, 0);
			if ( g.getInnerGeometry().getNumGeometries() > 1 ) {
				for ( int i = 0; i < g.getInnerGeometry().getNumGeometries(); i++ ) {
					drawer.drawTesselatedPolygon(gl, (Polygon) g.getInnerGeometry().getGeometryN(i), 1, c, 1);
				}
			} else {
				drawer.drawTesselatedPolygon(gl, (Polygon) g.getInnerGeometry(), 1, c, 1);
			}
		}
		// We then pop the matrix
		gl.glPopMatrix();
		// And close the list,
		gl.glEndList();
		// Before returning its index
		return index;
	}
}
