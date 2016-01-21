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
import java.util.*;
import java.util.concurrent.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.gl2.GLUT;
import com.vividsolutions.jts.geom.Polygon;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.util.file.*;
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.files.*;
import ummisco.gama.opengl.jts.JTSDrawer;

public class GeometryCache {

	// FIXME: Need to see why it's not working with sync=true
	boolean sync = false;
	private final Map<String, Integer> GEOMETRIES;
	private GeometryAsyncBuilder BUILDER = null;

	Integer openNestedGLListIndex;
	private final GLUT myGlut = new GLUT();
	private final JOGLRenderer renderer;
	JTSDrawer drawer;

	public GeometryCache(final JOGLRenderer renderer) {
		if ( sync ) {
			GEOMETRIES = new HashMap<String, Integer>();// (100, 0.75f, 1)
		} else {
			GEOMETRIES = new ConcurrentHashMap(100, 0.75f, 1);
			BUILDER = new GeometryAsyncBuilder();
		}
		this.renderer = renderer;
		drawer = new JTSDrawer(renderer);
	}

	// Assumes the texture has been created. But it may be processed at the time
	// of the call, so we wait for its availability.
	public Integer getListIndex(final GL gl, final String file) {
		if ( file == null ) { return null; }
		Integer index = GEOMETRIES.get(file);
		while (index == null) {
			index = GEOMETRIES.get(file);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return index;
	}

	public void initializeStaticGeometry(final GL2 gl, final GamaFile file) {

		if ( contains(file.getFile().getAbsolutePath().toString()) ) { return; }
		if ( sync ) {
			Integer index = buildList(gl, file);
			if ( index != null ) {
				GEOMETRIES.put(file.getFile().getAbsolutePath().toString(), index);
				System.out.println("sync" + GEOMETRIES.size());
			}
		} else {
			BuildingTask task = new BuildingTask(gl, file);
			BUILDER.tasks.offer(task);
		}
	}

	/**
	 * @param image
	 * @return
	 */
	public boolean contains(final String file) {
		return GEOMETRIES.containsKey(file);
	}

	public Integer buildList(final GL2 gl, final GamaFile string) {
		Integer index = null;
		if ( string.getExtension().equals("obj") ) {
			String obj = string.getFile().toString();
			String fmtl = string.getFile().getAbsolutePath().replaceAll(".obj", ".mtl");
			GLModel asset3Dmodel = ModelLoaderOBJ.LoadModel(obj, fmtl, gl);
			index = gl.glGenLists(1);
			gl.glNewList(index, GL2.GL_COMPILE);
			asset3Dmodel.draw(gl);
			gl.glEndList();
		}
		if ( string.getExtension().equals("svg") ) {
			GamaSVGFile svg = (GamaSVGFile) string;
			GamaShape g = (GamaShape) svg.getGeometry(null);
			index = gl.glGenLists(1);
			gl.glNewList(index, GL2.GL_COMPILE);
			Color c = new Color(0, 0, 0);
			if ( g.getInnerGeometry().getNumGeometries() > 1 ) {
				for ( int i = 0; i < g.getInnerGeometry().getNumGeometries(); i++ ) {
					drawer.DrawTesselatedPolygon((Polygon) g.getInnerGeometry().getGeometryN(i), 1, c, 1);
				}
			} else {
				drawer.DrawTesselatedPolygon((Polygon) g.getInnerGeometry(), 1, c, 1);
			}
			gl.glEndList();
		}
		if ( index != null ) {
			gl.glCallList(index);
		}
		return index;
	}

	class GeometryAsyncBuilder implements Runnable {

		final LinkedBlockingQueue<GLTask> tasks = new LinkedBlockingQueue<GLTask>();
		final Thread loadingThread;

		public GeometryAsyncBuilder() {
			loadingThread = new Thread(this, "Geometry cache building thread");
			loadingThread.start();
		}

		@Override
		public void run() {
			final ArrayList<GLTask> copy = new ArrayList();
			while (true) {
				tasks.drainTo(copy);
				for ( GLTask currentTask : copy ) {
					currentTask.run();
				}
				copy.clear();
			}
		}
	}

	abstract class GLTask {

		GL2 gl;

		GLTask(final GL2 gl) {
			this.gl = gl;
		}

		abstract void run();
	}

	class DestroyingTask extends GLTask {

		final int[] geometryIds;

		DestroyingTask(final GL2 gl, final int[] geometryIds) {
			super(gl);
			this.geometryIds = geometryIds;
		}

		@Override
		public void run() {
			// Detruire les listes et vider la map
		}

	}

	class BuildingTask extends GLTask {

		protected final GamaFile string;

		BuildingTask(final GL2 gl, final GamaFile file) {
			super(gl);
			this.string = file;
		}

		@Override
		public void run() {
			gl.getContext().makeCurrent();
			Integer index = GeometryCache.this.buildList(gl, string);
			if ( index != null ) {
				GEOMETRIES.put(string.getFile().getAbsolutePath().toString(), index);
			}
			gl.getContext().release();
		}
	}
}
