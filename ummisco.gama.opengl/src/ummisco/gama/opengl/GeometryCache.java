/*********************************************************************************************
 *
 * 'GeometryCache.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jogamp.opengl.GL2;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.file.GamaGeometryFile;
import ummisco.gama.opengl.files.GamaObjFile;
import ummisco.gama.opengl.jts.JTSDrawer;

public class GeometryCache {

	private final Map<String, Integer> cache;

	public GeometryCache() {
		cache = new ConcurrentHashMap<>(100, 0.75f, 4);

		// drawer = new GeometryDrawer(renderer);
	}

	public Integer get(final GL2 gl, final JOGLRenderer renderer, final GamaGeometryFile file) {
		Integer index = cache.get(file.getPath(renderer.getSurface().getScope()));
		if (index == null) {
			try {
				index = buildList(gl, renderer, file);
				cache.put(file.getPath(renderer.getSurface().getScope()), index);
			} catch (final FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return index;
	}

	private Integer buildList(final GL2 gl, final JOGLRenderer renderer, final GamaGeometryFile file)
			throws FileNotFoundException {
		final String extension = file.getExtension(renderer.getSurface().getScope());
		// We generate the list first
		final Integer index = gl.glGenLists(1);
		gl.glNewList(index, GL2.GL_COMPILE);
		// We push the matrix
		gl.glPushMatrix();
		// We draw the file in the list
		if (extension.equals("obj")) {
			((GamaObjFile) file).drawToOpenGL(gl, renderer);
		} else {
			final IDisplaySurface surface = renderer.getSurface();
			final IShape shape = file.getGeometry(surface.getScope());
			if (shape == null) {
				return index;
			}
			final Geometry g = shape.getInnerGeometry();
			drawSimpleGeometry(gl, renderer, g);
		}
		// We then pop the matrix
		gl.glPopMatrix();
		// And close the list,
		gl.glEndList();
		// Before returning its index
		return index;
	}

	private final Color defaultColor = new Color(0, 0, 0);

	void drawSimpleGeometry(final GL2 gl, final JOGLRenderer renderer, final Geometry g) {
		if (g.getNumGeometries() > 1) {
			for (int i = 0; i < g.getNumGeometries(); i++) {
				final Geometry jts = g.getGeometryN(i);
				drawSimpleGeometry(gl, renderer, jts);
			}
		} else {
			final JTSDrawer drawer = renderer.getJTSDrawer();
			if (g instanceof Polygon) {
				drawer.drawTesselatedPolygon(gl, (Polygon) g, 1, defaultColor, 1);
			} else if (g instanceof LineString) {
				drawer.drawLineString(gl, (LineString) g, 0, JOGLRenderer.getLineWidth(), defaultColor, 1);
			}
		}
	}

	/**
	 * @param gl
	 */
	public void dispose(final GL2 gl) {
		for (final Integer i : cache.values()) {
			gl.glDeleteLists(i, 1);
		}
		cache.clear();
	}

}
