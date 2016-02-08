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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.jogamp.opengl.GL2;
import com.vividsolutions.jts.geom.*;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.runtime.*;
import msi.gama.runtime.GAMA.InScope;
import msi.gama.util.file.GamaGeometryFile;
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.files.GamaObjFile;
import ummisco.gama.opengl.jts.JTSDrawer;

public class GeometryCache {

	private final Map<String, Integer> cache;
	JOGLRenderer renderer;

	JTSDrawer drawer;

	public GeometryCache(final JOGLRenderer renderer) {
		cache = new ConcurrentHashMap<String, Integer>(100, 0.75f, 4);
		this.renderer = renderer;
		drawer = new JTSDrawer(renderer);
		// drawer = new GeometryDrawer(renderer);
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
			GamaShape g = GAMA.run(new InScope<GamaShape>() {

				@Override
				public GamaShape run(final IScope scope) {
					return (GamaShape) file.getGeometry(scope);
				}

			});
			// DrawingAttributes attributes =
			// new ShapeDrawingAttributes(new GamaPoint(0, 0, 0), defautColor, defautColor, g.getGeometricalType());
			// GeometryObject object = new GeometryObject(g.getInnerGeometry(), attributes, null);
			// drawer.draw(gl, object);

			//
			//
			// object.draw(gl, new GeometryDrawer(), false);
			//
			Color c = new Color(0, 0, 0);
			if ( g.getInnerGeometry().getNumGeometries() > 1 ) {
				for ( int i = 0; i < g.getInnerGeometry().getNumGeometries(); i++ ) {
					Geometry jts = g.getInnerGeometry().getGeometryN(i);
					if ( jts instanceof Polygon ) {
						drawer.drawTesselatedPolygon(gl, (Polygon) jts, 1, c, 1);
					}
				}
			} else {
				if ( g.getInnerGeometry() instanceof Polygon ) {
					drawer.drawTesselatedPolygon(gl, (Polygon) g.getInnerGeometry(), 1, c, 1);
				}
			}
		}
		// We then pop the matrix
		gl.glPopMatrix();
		// And close the list,
		gl.glEndList();
		// Before returning its index
		return index;
	}

	private final Color defaultColor = new Color(0, 0, 0);

	void drawSimpleGeometry(final GL2 gl, final Geometry g) {
		if ( g.getNumGeometries() > 1 ) {
			for ( int i = 0; i < g.getNumGeometries(); i++ ) {
				Geometry jts = g.getGeometryN(i);
				drawSimpleGeometry(gl, jts);
			}
		} else {
			if ( g instanceof Polygon ) {
				drawer.drawTesselatedPolygon(gl, (Polygon) g, 1, defaultColor, 1);
			} else if ( g instanceof LineString ) {
				drawer.drawLineString(gl, (LineString) g, 0, renderer.getLineWidth(), defaultColor, 1);
			}
		}
	}

}
