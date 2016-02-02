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

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.jogamp.opengl.GL2;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.GAMA;
import msi.gama.util.GamaColor;
import msi.gama.util.file.GamaGeometryFile;
import msi.gaml.statements.draw.DrawingData.DrawingAttributes;
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.files.GamaObjFile;

public class GeometryCache {

	private final Map<String, Integer> cache;
	GeometryDrawer drawer;
	static GamaColor defautColor = new GamaColor(0, 0, 0, 255);

	// JTSDrawer drawer;

	public GeometryCache(final JOGLRenderer renderer) {
		cache = new ConcurrentHashMap<String, Integer>(100, 0.75f, 4);
		drawer = new GeometryDrawer(renderer);
	}

	public Integer get(final GL2 gl, final GamaGeometryFile file) {
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

	private Integer buildList(final GL2 gl, final GamaGeometryFile file) throws FileNotFoundException {
		String extension = file.getExtension();
		// We generate the list first
		Integer index = gl.glGenLists(1);
		gl.glNewList(index, GL2.GL_COMPILE);
		// We push the matrix
		gl.glPushMatrix();
		// We draw the file in the list
		if ( extension.equals("obj") ) {
			((GamaObjFile) file).drawToOpenGL(gl);
		} else {
			GamaShape g = (GamaShape) file.getGeometry(GAMA.getRuntimeScope());
			DrawingAttributes attributes = new DrawingAttributes(new GamaPoint(0, 0, 0), defautColor, defautColor);
			attributes.type = g.getGeometricalType();
			attributes.empty = false;
			GeometryObject object = new GeometryObject(g.getInnerGeometry(), attributes, null);
			drawer.draw(gl, object);

			//
			//
			// object.draw(gl, new GeometryDrawer(), false);
			//
			// if ( g.getInnerGeometry().getNumGeometries() > 1 ) {
			// for ( int i = 0; i < g.getInnerGeometry().getNumGeometries(); i++ ) {
			// drawer.drawTesselatedPolygon(gl, (Polygon) g.getInnerGeometry().getGeometryN(i), 1, c, 1);
			// }
			// } else {
			// drawer.drawTesselatedPolygon(gl, (Polygon) g.getInnerGeometry(), 1, c, 1);
			// }
		}
		// We then pop the matrix
		gl.glPopMatrix();
		// And close the list,
		gl.glEndList();
		// Before returning its index
		return index;
	}
}
