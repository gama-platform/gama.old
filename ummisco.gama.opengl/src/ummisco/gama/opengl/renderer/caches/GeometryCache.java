/*******************************************************************************************************
 *
 * GeometryCache.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.renderer.caches;

import static com.google.common.cache.CacheBuilder.newBuilder;
import static java.util.concurrent.TimeUnit.MINUTES;
import static msi.gama.common.geometry.GeometryUtils.getTypeOf;
import static msi.gama.metamodel.shape.IShape.Type.CIRCLE;
import static msi.gama.metamodel.shape.IShape.Type.CONE;
import static msi.gama.metamodel.shape.IShape.Type.CUBE;
import static msi.gama.metamodel.shape.IShape.Type.CYLINDER;
import static msi.gama.metamodel.shape.IShape.Type.POINT;
import static msi.gama.metamodel.shape.IShape.Type.PYRAMID;
import static msi.gama.metamodel.shape.IShape.Type.SPHERE;
import static msi.gama.metamodel.shape.IShape.Type.SQUARE;

import java.nio.DoubleBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFilter;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2GL3;
import com.jogamp.opengl.fixedfunc.GLPointerFunc;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.ICoordinates;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.shape.IShape.Type;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.util.file.GamaGeometryFile;
import msi.gama.util.file.GamaObjFile;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.files.ObjFileDrawer;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;

/**
 * The Class GeometryCache.
 */
public class GeometryCache {

	static {
		DEBUG.ON();
	}

	/** The Constant PI_2. */
	private static final double PI_2 = 2f * Math.PI;

	/** The round rect. */
	static double roundRect[] = { .92, 0, .933892, .001215, .947362, .004825, .96, .010718, .971423, .018716, .981284,
			.028577, .989282, .04, .995175, .052638, .998785, .066108, 1, .08, 1, .92, .998785, .933892, .995175,
			.947362, .989282, .96, .981284, .971423, .971423, .981284, .96, .989282, .947362, .995175, .933892, .998785,
			.92, 1, .08, 1, .066108, .998785, .052638, .995175, .04, .989282, .028577, .981284, .018716, .971423,
			.010718, .96, .004825, .947362, .001215, .933892, 0, .92, 0, .08, .001215, .066108, .004825, .052638,
			.010718, .04, .018716, .028577, .028577, .018716, .04, .010718, .052638, .004825, .066108, .001215, .08,
			0 };

	/** The db. */
	static DoubleBuffer db = Buffers.newDirectDoubleBuffer(roundRect.length).put(roundRect).rewind();

	/**
	 * The Class BuiltInGeometry.
	 */
	public static class BuiltInGeometry {

		/** The faces. */
		Integer bottom, top, faces;

		/**
		 * Assemble.
		 *
		 * @return the built in geometry
		 */
		public static BuiltInGeometry assemble() {
			return new BuiltInGeometry(null, null, null);
		}

		/**
		 * Top.
		 *
		 * @param top
		 *            the top
		 * @return the built in geometry
		 */
		public BuiltInGeometry top(final Integer top) {
			this.top = top;
			return this;
		}

		/**
		 * Faces.
		 *
		 * @param faces
		 *            the faces
		 * @return the built in geometry
		 */
		public BuiltInGeometry faces(final Integer faces) {
			this.faces = faces;
			return this;
		}

		/**
		 * Bottom.
		 *
		 * @param bottom
		 *            the bottom
		 * @return the built in geometry
		 */
		public BuiltInGeometry bottom(final Integer bottom) {
			this.bottom = bottom;
			return this;
		}

		/**
		 * Instantiates a new built in geometry.
		 *
		 * @param bottom
		 *            the bottom
		 * @param top
		 *            the top
		 * @param faces
		 *            the faces
		 */
		private BuiltInGeometry(final Integer bottom, final Integer top, final Integer faces) {
			this.bottom = bottom;
			this.top = top;
			this.faces = faces;
		}

		/**
		 * Draw.
		 *
		 * @param gl
		 *            the gl
		 */
		public void draw(final OpenGL gl) {
			if (bottom != null) { gl.drawList(bottom); }
			if (top != null) { gl.drawList(top); }
			gl.enableAlternateTexture();
			if (faces != null) { gl.drawList(faces); }
		}
	}

	/** The built in cache. */
	private final Cache<IShape.Type, BuiltInGeometry> builtInCache;

	/** The file cache. */
	private final LoadingCache<String, Integer> fileCache;

	/** The file map. */
	private final Map<String, GamaGeometryFile> fileMap = new ConcurrentHashMap<>();

	/** The geometries to process. */
	private final Map<String, GamaGeometryFile> geometriesToProcess = new ConcurrentHashMap<>();

	/** The envelopes. */
	private final Cache<String, Envelope3D> envelopes;

	/** The scope. */
	private final IScope scope;

	/** The drawer. */
	private final Consumer<Geometry> drawer;

	/**
	 * Instantiates a new geometry cache.
	 *
	 * @param renderer
	 *            the renderer
	 */
	public GeometryCache(final IOpenGLRenderer renderer) {
		this.scope = renderer.getSurface().getScope().copy("in opengl geometry cache");
		this.drawer = g -> renderer.getOpenGLHelper().getGeometryDrawer().drawGeometry(g, null, 0, getTypeOf(g));
		envelopes = newBuilder().expireAfterAccess(10, MINUTES).build();
		builtInCache = newBuilder().concurrencyLevel(2).initialCapacity(10).build();
		fileCache = newBuilder().expireAfterAccess(10, MINUTES).initialCapacity(10).removalListener(notif -> {
			if (renderer.isDisposed()) return;
			renderer.getOpenGLHelper().getGL().glDeleteLists((Integer) notif.getValue(), 1);

		}).build(new CacheLoader<String, Integer>() {

			@Override
			public Integer load(final String file) {
				return buildList(renderer.getOpenGLHelper(), file);
			}
		});
	}

	/**
	 * Gets the.
	 *
	 * @param file
	 *            the file
	 * @return the integer
	 */
	public Integer get(final GamaGeometryFile file) {
		return fileCache.getUnchecked(file.getPath(scope));
	}

	/**
	 * Gets the.
	 *
	 * @param id
	 *            the id
	 * @return the built in geometry
	 */
	public BuiltInGeometry get(final IShape.Type id) {
		return builtInCache.getIfPresent(id);
	}

	/**
	 * Builds the list.
	 *
	 * @param gl
	 *            the gl
	 * @param name
	 *            the name
	 * @return the integer
	 */
	Integer buildList(final OpenGL gl, final String name) {
		// DEBUG.OUT("Bulding OpenGL list for " + name);
		final GamaGeometryFile file = fileMap.get(name);
		// We generate the list first

		return gl.compileAsList(() -> {
			// We draw the file in the list
			if (file instanceof GamaObjFile f) {
				f.loadObject(scope, true);
				ObjFileDrawer.drawToOpenGL(f, gl);
			} else {
				final IShape shape = file.getGeometry(scope);
				if (shape == null) return;
				try {
					drawSimpleGeometry(gl, shape.getInnerGeometry());
				} catch (final ExecutionException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Draw simple geometry.
	 *
	 * @param gl
	 *            the gl
	 * @param geom
	 *            the geom
	 * @throws ExecutionException
	 *             the execution exception
	 */
	void drawSimpleGeometry(final OpenGL gl, final Geometry geom) throws ExecutionException {
		geom.apply((GeometryFilter) g -> drawer.accept(g));
	}

	/**
	 * Dispose.
	 */
	public void dispose() {
		fileMap.clear();
		GAMA.releaseScope(scope);
	}

	/**
	 * Process unloaded.
	 */
	public void processUnloaded() {
		for (final GamaGeometryFile object : geometriesToProcess.values()) { get(object); }
		geometriesToProcess.clear();
	}

	/**
	 * Process.
	 *
	 * @param file
	 *            the file
	 */
	public void process(final GamaGeometryFile file) {
		if (file == null) return;
		final String path = file.getPath(scope);
		if (fileCache.getIfPresent(path) != null) return;
		fileMap.putIfAbsent(path, file);
		if (!geometriesToProcess.containsKey(path)) { geometriesToProcess.put(path, file); }
	}

	/**
	 * Gets the envelope.
	 *
	 * @param file
	 *            the file
	 * @return the envelope
	 */
	public Envelope3D getEnvelope(final GamaGeometryFile file) {
		try {
			return envelopes.get(file.getPath(scope), () -> file.computeEnvelope(scope));
		} catch (final ExecutionException e) {
			return Envelope3D.EMPTY;
		}
	}

	/**
	 * Put.
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void put(final Type key, final BuiltInGeometry value) {
		builtInCache.put(key, value);
	}

	/**
	 * Initialize.
	 *
	 * @param gl
	 *            the gl
	 */
	public void initialize(final OpenGL gl) {
		final int slices = GamaPreferences.Displays.DISPLAY_SLICE_NUMBER.getValue();
		final int stacks = slices;
		put(SPHERE, BuiltInGeometry.assemble().faces(gl.compileAsList(() -> {
			gl.translateBy(0d, 0d, 1d);
			drawSphere(gl, 1.0, slices, stacks);
			gl.translateBy(0, 0, -1d);
		})));
		put(CYLINDER,
				BuiltInGeometry.assemble().bottom(gl.compileAsList(() -> { drawDisk(gl, 0d, 1d, slices, slices / 3); }))
						.top(gl.compileAsList(() -> {
							gl.translateBy(0d, 0d, 1d);
							drawDisk(gl, 0d, 1d, slices, slices / 3);
							gl.translateBy(0d, 0d, -1d);
						})).faces(gl.compileAsList(() -> { drawCylinder(gl, 1.0d, 1.0d, 1.0d, slices, stacks); })));
		put(CONE,
				BuiltInGeometry.assemble().bottom(gl.compileAsList(() -> { drawDisk(gl, 0d, 1d, slices, slices / 3); }))
						.faces(gl.compileAsList(() -> {
							drawCylinder(gl, 1.0, 0.0, 1.0, slices, stacks);
						})));
		final ICoordinates baseVertices = ICoordinates.ofLength(5);
		final ICoordinates faceVertices = ICoordinates.ofLength(5);
		baseVertices.setTo(-0.5, 0.5, 0, 0.5, 0.5, 0, 0.5, -0.5, 0, -0.5, -0.5, 0, -0.5, 0.5, 0);

		put(CUBE, BuiltInGeometry.assemble().bottom(gl.compileAsList(() -> {
			gl.drawSimpleShape(baseVertices, 4, false, true, null);
		})).top(gl.compileAsList(() -> {
			baseVertices.translateBy(0, 0, 1);
			gl.drawSimpleShape(baseVertices, 4, true, true, null);
			baseVertices.translateBy(0, 0, -1);
		})).faces(gl.compileAsList(() -> {
			baseVertices.visit((pj, pk) -> {
				faceVertices.setTo(pk.x, pk.y, pk.z, pk.x, pk.y, pk.z + 1, pj.x, pj.y, pj.z + 1, pj.x, pj.y, pj.z, pk.x,
						pk.y, pk.z);
				gl.drawSimpleShape(faceVertices, 4, true, true, null);
			});
		})));
		put(POINT, BuiltInGeometry.assemble().faces(gl.compileAsList(() -> { drawSphere(gl, 1.0, 5, 5); })));

		put(IShape.Type.ROUNDED, BuiltInGeometry.assemble().bottom(gl.compileAsList(() -> {
			// Align with SQUARE; see issue #3542
			gl.translateBy(-.5d, -.5d);
			drawRoundedRectangle(gl.getGL());
			gl.translateBy(.5d, .5d);
		})));
		put(SQUARE, BuiltInGeometry.assemble().bottom(gl.compileAsList(() -> {
			gl.drawSimpleShape(baseVertices, 4, true, true, null);
		})));
		put(CIRCLE, BuiltInGeometry.assemble().bottom(gl.compileAsList(() -> { drawDisk(gl, 0.0, 1.0, slices, 1); })));
		final ICoordinates triangleVertices = ICoordinates.ofLength(4);
		final ICoordinates vertices = ICoordinates.ofLength(5);
		vertices.setTo(-0.5, -0.5, 0, -0.5, 0.5, 0, 0.5, 0.5, 0, 0.5, -0.5, 0, -0.5, -0.5, 0);
		put(PYRAMID, BuiltInGeometry.assemble().bottom(gl.compileAsList(() -> {
			gl.drawSimpleShape(vertices, 4, false, true, null);
		})).faces(gl.compileAsList(() -> {
			final GamaPoint top = new GamaPoint(0, 0, 1);
			vertices.visit((pj, pk) -> {
				triangleVertices.setTo(pj.x, pj.y, pj.z, top.x, top.y, top.z, pk.x, pk.y, pk.z, pj.x, pj.y, pj.z);
				gl.drawSimpleShape(triangleVertices, 3, true, true, null);

			});
		})));

	}

	/**
	 * Draw rounded rectangle.
	 *
	 * @param gl
	 *            the gl
	 */
	public void drawRoundedRectangle(final GL2 gl) {
		gl.glEnableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
		gl.glVertexPointer(2, GL2GL3.GL_DOUBLE, 0, db);
		gl.glDrawArrays(GL.GL_TRIANGLE_FAN, 0, 40);
		gl.glDisableClientState(GLPointerFunc.GL_VERTEX_ARRAY);
	}

	/**
	 * Draw disk.
	 *
	 * @param gl
	 *            the gl
	 * @param inner
	 *            the inner
	 * @param outer
	 *            the outer
	 * @param slices
	 *            the slices
	 * @param loops
	 *            the loops
	 */
	public void drawDisk(final OpenGL gl, final double inner, final double outer, final int slices, final int loops) {
		double da, dr;
		/* Normal vectors */
		gl.outputNormal(0.0, 0.0, +1.0);
		da = PI_2 / slices;
		dr = (outer - inner) / loops;

		final double dtc = 2.0f * outer;
		double sa, ca;
		double r1 = inner;
		int l;
		gl.getGL().glFrontFace(GL.GL_CCW);
		for (l = 0; l < loops; l++) {
			final double r2 = r1 + dr;
			int s;
			gl.beginDrawing(GL2.GL_QUAD_STRIP);
			for (s = 0; s <= slices; s++) {
				double a;
				if (s == slices) {
					a = 0.0f;
				} else {
					a = s * da;
				}
				sa = Math.sin(a);
				ca = Math.cos(a);
				gl.outputTexCoord(0.5f + sa * r2 / dtc, 0.5f + ca * r2 / dtc);
				gl.outputVertex(r2 * sa, r2 * ca, 0);
				gl.outputTexCoord(0.5f + sa * r1 / dtc, 0.5f + ca * r1 / dtc);
				gl.outputVertex(r1 * sa, r1 * ca, 0);
			}
			gl.endDrawing();
			r1 = r2;
		}
		gl.getGL().glFrontFace(GL.GL_CW);
	}

	/**
	 * Draw sphere.
	 *
	 * @param gl
	 *            the gl
	 * @param radius
	 *            the radius
	 * @param slices
	 *            the slices
	 * @param stacks
	 *            the stacks
	 */
	public void drawSphere(final OpenGL gl, final double radius, final int slices, final int stacks) {
		double rho, drho, theta, dtheta;
		double x, y, z;
		double s, t, ds, dt;
		int i, j, imin, imax;
		drho = Math.PI / stacks;
		dtheta = PI_2 / slices;
		ds = 1.0f / slices;
		dt = 1.0f / stacks;
		t = 1.0f; // because loop now runs from 0
		imin = 0;
		imax = stacks;
		gl.getGL().glFrontFace(GL.GL_CCW);
		// draw intermediate stacks as quad strips
		for (i = imin; i < imax; i++) {
			rho = i * drho;
			gl.beginDrawing(GL2.GL_QUAD_STRIP);
			s = 0.0f;
			for (j = 0; j <= slices; j++) {
				theta = j == slices ? 0.0f : j * dtheta;
				x = -Math.sin(theta) * Math.sin(rho);
				y = Math.cos(theta) * Math.sin(rho);
				z = Math.cos(rho);
				gl.outputNormal(x, y, z);
				gl.outputTexCoord(s, t);
				gl.outputVertex(x * radius, y * radius, z * radius);
				x = -Math.sin(theta) * Math.sin(rho + drho);
				y = Math.cos(theta) * Math.sin(rho + drho);
				z = Math.cos(rho + drho);
				gl.outputNormal(x, y, z);
				gl.outputTexCoord(s, t - dt);
				s += ds;
				gl.outputVertex(x * radius, y * radius, z * radius);
			}
			gl.endDrawing();
			t -= dt;
		}
		gl.getGL().glFrontFace(GL.GL_CW);
	}

	/**
	 * Draw cylinder.
	 *
	 * @param gl
	 *            the gl
	 * @param base
	 *            the base
	 * @param top
	 *            the top
	 * @param height
	 *            the height
	 * @param slices
	 *            the slices
	 * @param stacks
	 *            the stacks
	 */
	public void drawCylinder(final OpenGL gl, final double base, final double top, final double height,
			final int slices, final int stacks) {

		double da, r, dr, dz;
		double x, y, z, nz;
		int i, j;
		da = PI_2 / slices;
		dr = (top - base) / stacks;
		dz = height / stacks;
		nz = (base - top) / height;

		final double ds = 1.0f / slices;
		final double dt = 1.0f / stacks;
		float t = 0.0f;
		z = 0.0f;
		r = base;
		gl.getGL().glFrontFace(GL.GL_CCW);
		for (j = 0; j < stacks; j++) {
			float s = 0.0f;
			gl.beginDrawing(GL2.GL_QUAD_STRIP);
			for (i = 0; i <= slices; i++) {
				if (i == slices) {
					x = Math.sin(0.0f);
					y = Math.cos(0.0f);
				} else {
					x = Math.sin(i * da);
					y = Math.cos(i * da);
				}
				gl.outputNormal(x, y, nz);
				gl.outputTexCoord(s, t);
				gl.outputVertex(x * r, y * r, z);
				gl.outputNormal(x, y, nz);
				gl.outputTexCoord(s, t + dt);
				gl.outputVertex(x * (r + dr), y * (r + dr), z + dz);

				s += ds;
			} // for slices
			gl.endDrawing();
			r += dr;
			t += dt;
			z += dz;
		} // for stacks
		gl.getGL().glFrontFace(GL.GL_CW);
	}

}
