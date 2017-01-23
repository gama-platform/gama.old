/*********************************************************************************************
 *
 * 'GeometryCache.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl;

import static msi.gama.common.util.GeometryUtils.getTypeOf;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

import com.jogamp.opengl.GL2;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFilter;

import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.file.GamaGeometryFile;
import ummisco.gama.opengl.files.GamaObjFile;
import ummisco.gama.opengl.scene.GeometryDrawer;

public class GeometryCache {

	private final Map<String, Integer> cache = new ConcurrentHashMap<>(100, 0.75f, 4);
	private final CopyOnWriteArrayList<GamaGeometryFile> geometriesToProcess = new CopyOnWriteArrayList<>();

	public GeometryCache() {}

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
			if (shape == null) { return index; }
			try {
				drawSimpleGeometry(gl, renderer, shape.getInnerGeometry());
			} catch (final ExecutionException e) {
				e.printStackTrace();
			}
		}
		// We then pop the matrix
		gl.glPopMatrix();
		// And close the list,
		gl.glEndList();
		// Before returning its index
		return index;
	}

	void drawSimpleGeometry(final GL2 gl, final JOGLRenderer renderer, final Geometry geom) throws ExecutionException {
		renderer.setCurrentColor(GamaPreferences.CORE_COLOR.getValue());
		final GeometryDrawer drawer = renderer.getGeometryDrawer();
		geom.apply((GeometryFilter) (g) -> drawer.drawGeometry(g, true, null, 0, null, getTypeOf(geom)));
	}

	public void dispose(final GL2 gl) {
		for (final Integer i : cache.values()) {
			gl.glDeleteLists(i, 1);
		}
		cache.clear();
	}

	public void processUnloadedGeometries(final GL2 gl, final JOGLRenderer renderer) {
		for (final GamaGeometryFile file : geometriesToProcess) {
			get(gl, renderer, file);
		}
		geometriesToProcess.clear();
	}

	public void saveGeometryToProcess(final GamaGeometryFile file) {
		if (!geometriesToProcess.contains(file))
			geometriesToProcess.add(file);
	}

}
