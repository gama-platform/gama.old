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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.*;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import com.jogamp.opengl.util.gl2.GLUT;

import msi.gama.common.util.ImageUtils;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.file.GamaFile;
import msi.gama.util.file.GamaSVGFile;
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.files.GLModel;
import ummisco.gama.opengl.files.ModelLoaderOBJ;
import ummisco.gama.opengl.jts.JTSDrawer;

public class GeometryCache {
	
	//FIXME: Need to see why it's not working with sync=true
	boolean sync = false;
	private  final Map<String, Integer> GEOMETRIES;
	private  GeometryAsyncBuilder BUILDER =null ;
	
	 Integer openNestedGLListIndex;
	private  GLUT myGlut = new GLUT();
	private JOGLRenderer renderer;
	JTSDrawer drawer;
	
	public GeometryCache(GL2 gl,JOGLRenderer renderer){
		  if(sync){
		    	GEOMETRIES = new HashMap<String, Integer>();//(100, 0.75f, 1)	
		    }
		    else{
		    	GEOMETRIES = new ConcurrentHashMap(100, 0.75f, 1);
		    	BUILDER = new GeometryAsyncBuilder(gl);
		    }
		this.renderer=renderer;
		drawer = new JTSDrawer(renderer);
	}
	// Assumes the texture has been created. But it may be processed at the time
	// of the call, so we wait for its availability.
	public  Integer getListIndex(final GL gl, final String file) {
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

	public  void initializeStaticGeometry(GL2 gl,final GamaFile file) {
	    if ( contains(file.getFile().getAbsolutePath().toString()) ) { return; }
	    if(sync){
	    	gl.getContext().makeCurrent();
	    	Integer index = buildList(gl, file);
			if ( index != null ) {
				GEOMETRIES.put(file.getFile().getAbsolutePath().toString(), index);
			}
	    }else{
	    	BuildingTask task = new BuildingTask(null, file);
			BUILDER.tasks.offer(task);	
	    }
	    
		
	}

	/**
	 * @param image
	 * @return
	 */
	public  boolean contains(final String file) {
		return GEOMETRIES.containsKey(file);
	}

	public  Integer buildList(final GL gl, final GamaFile string) {
		Integer index = null;
		if ( string.getExtension().equals("obj")) {	
			String obj = string.getFile().toString();
			String fmtl = string.getFile().getAbsolutePath().replaceAll(".obj", ".mtl");
			GLModel asset3Dmodel = ModelLoaderOBJ.LoadModel(obj, fmtl, (GL2) gl);
			index = ((GL2) gl).glGenLists(1);
			((GL2) gl).glNewList(index, GL2.GL_COMPILE);
				asset3Dmodel.draw((GL2) gl);		
			((GL2) gl).glEndList();
		}
		if ( string.getExtension().equals("svg")) {	
			GamaSVGFile svg = (GamaSVGFile) string;
			GamaShape g = (GamaShape) svg.getGeometry(null);	
			index = ((GL2) gl).glGenLists(1);
			((GL2) gl).glNewList(index, GL2.GL_COMPILE);
			Color c = new Color(0,0,0);
			drawer.DrawTesselatedPolygon((Polygon) g.getInnerGeometry(), 1, c, 1);	
			((GL2) gl).glEndList();			
		}
		if(index!=null){
		  ((GL2) gl).glCallList(index);
		}
		return index;
	}


	public  class GeometryAsyncBuilder implements Runnable {

		final LinkedBlockingQueue<GLTask> tasks = new LinkedBlockingQueue<GLTask>();
		final Thread loadingThread;
		protected GL2 gl;

		public GeometryAsyncBuilder(GL2 gl) {
			this.gl = gl;
			loadingThread = new Thread(this, "Geometry cache building thread");
			loadingThread.start();
		}

		@Override
		public void run() {
			final ArrayList<GLTask> copy = new ArrayList();
			while (true) {
				tasks.drainTo(copy);
				try {
					gl.getContext().makeCurrent();
					for ( GLTask currentTask : copy ) {
						currentTask.runIn(gl);
					}

				} finally {
					    if(gl.getContext().isCurrent()){
					    	gl.getContext().release();	
					    }
						
			}}
		}

	}

	protected interface GLTask {

		abstract void runIn(GL gl);
	}

	protected class DestroyingTask implements GLTask {

		final int[] geometryIds;

		DestroyingTask(final int[] geometryIds) {
			this.geometryIds = geometryIds;
		}

		@Override
		public void runIn(final GL gl) {
			//gl.glDeleteTextures(geometryIds.length, geometryIds, 0);
		}

	}

	protected  class BuildingTask implements GLTask {

		protected final GamaFile string;
		BuildingTask(final GL2 gl, final GamaFile file) {
			this.string = file;
		}

		@Override
		public void runIn(final GL gl) {
			Integer index = buildList(gl, string);
			if ( index != null ) {
				GEOMETRIES.put(string.getFile().getAbsolutePath().toString(), index);
			}
		}
	}
}
