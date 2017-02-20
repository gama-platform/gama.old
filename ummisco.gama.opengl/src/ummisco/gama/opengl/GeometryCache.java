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

import static msi.gama.common.geometry.GeometryUtils.getTypeOf;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jogamp.opengl.GL2;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFilter;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.shape.IShape.Type;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.util.file.GamaGeometryFile;
import ummisco.gama.opengl.files.GamaObjFile;
import ummisco.gama.opengl.scene.OpenGL;
import ummisco.gama.opengl.scene.ResourceObject;

public class GeometryCache {

	public static class BuiltInGeometry {
		Integer bottom, top, faces;

		public static BuiltInGeometry assemble() {
			return new BuiltInGeometry(null, null, null);
		}

		public BuiltInGeometry top(final Integer top) {
			this.top = top;
			return this;
		}

		public BuiltInGeometry faces(final Integer faces) {
			this.faces = faces;
			return this;
		}

		public BuiltInGeometry bottom(final Integer bottom) {
			this.bottom = bottom;
			return this;
		}

		private BuiltInGeometry(final Integer bottom, final Integer top, final Integer faces) {
			super();
			this.bottom = bottom;
			this.top = top;
			this.faces = faces;
		}

		public void draw(final OpenGL gl) {
			if (bottom != null)
				gl.drawList(bottom);
			if (top != null)
				gl.drawList(top);
			gl.enableAlternateTexture();
			if (faces != null)
				gl.drawList(faces);
		}
	}

	private final Map<IShape.Type, BuiltInGeometry> shapeCache = new ConcurrentHashMap<>(100, 0.75f, 4);
	private final Map<String, Integer> fileCache = new ConcurrentHashMap<>(100, 0.75f, 4);
	private final Map<String, ResourceObject> geometriesToProcess = new ConcurrentHashMap<>();
	private final Cache<String, Envelope3D> envelopes;
	private final IScope scope;
	private final Consumer<Geometry> drawer;

	public GeometryCache(final JOGLRenderer renderer) {
		this.scope = renderer.getSurface().getScope().copy("Geometry cache");
		this.drawer = g -> renderer.getGeometryDrawer().drawGeometry(g, true, null, 0, getTypeOf(g));
		envelopes = CacheBuilder.newBuilder().build();
	}

	public Integer get(final OpenGL gl, final GamaGeometryFile file) {
		final String path = file.getPath(scope);
		Integer index = fileCache.get(path);
		if (index == null) {
			index = buildList(gl, file);
			fileCache.put(path, index);
		}
		return index;
	}

	public BuiltInGeometry get(final OpenGL gl, final IShape.Type id) {
		final BuiltInGeometry index = shapeCache.get(id);
		return index;
	}

	private Integer buildList(final OpenGL gl, final GamaGeometryFile file) {
		// We generate the list first

		final Integer index = gl.compileAsList(() -> {
			// We draw the file in the list

			if (file instanceof GamaObjFile) {
				final GamaObjFile f = (GamaObjFile) file;
				f.loadObject(scope, true);
				f.drawToOpenGL(gl);
			} else {
				final IShape shape = file.getGeometry(scope);
				if (shape == null) { return; }
				try {
					drawSimpleGeometry(gl, shape.getInnerGeometry());
				} catch (final ExecutionException e) {
					e.printStackTrace();
				}
			}
		});

		return index;
	}

	void drawSimpleGeometry(final OpenGL gl, final Geometry geom) throws ExecutionException {
		geom.apply((GeometryFilter) (g) -> drawer.accept(g));
	}

	public void dispose(final GL2 gl) {
		for (final Integer i : fileCache.values()) {
			gl.glDeleteLists(i, 1);
		}
		fileCache.clear();
		GAMA.releaseScope(scope);
	}

	public void processUnloadedGeometries(final OpenGL gl) {
		for (final ResourceObject object : geometriesToProcess.values()) {
			get(gl, object.getFile());
		}
		geometriesToProcess.clear();
	}

	public void saveGeometryToProcess(final ResourceObject object) {
		final String path = object.getFile().getPath(scope);
		if (!geometriesToProcess.containsKey(path))
			geometriesToProcess.put(path, object);
	}

	public Envelope3D getEnvelope(final GamaGeometryFile file) {
		try {
			return envelopes.get(file.getPath(scope), () -> file.computeEnvelope(scope));
		} catch (final ExecutionException e) {
			return new Envelope3D();
		}
	}

	public void put(final Type key, final BuiltInGeometry value) {
		shapeCache.put(key, value);
	}

}
