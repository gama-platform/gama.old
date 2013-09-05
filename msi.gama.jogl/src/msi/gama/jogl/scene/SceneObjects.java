package msi.gama.jogl.scene;

import static javax.media.opengl.GL.GL_COMPILE;

import java.awt.Color;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.*;

import javax.media.opengl.GL;

import com.sun.opengl.util.BufferUtil;

import msi.gama.jogl.utils.JOGLAWTGLRenderer;
import msi.gama.jogl.utils.VertexArrayHandler;

public class SceneObjects<T extends AbstractObject> implements Iterable<T> {

	public static class Static<T extends AbstractObject> extends SceneObjects<T> {

		Static(final ObjectDrawer<T> drawer, final boolean asList, final boolean asVBO) {
			super(drawer, asList, asVBO);
		}

		@Override
		public void add(final T object) {
			super.add(object);
			if ( openGLListIndex != null ) {
				drawer.renderer.gl.glDeleteLists(openGLListIndex, 1);
				openGLListIndex = null;
			}
		}

		@Override
		public void clear(final JOGLAWTGLRenderer renderer) {}
	}

	final ObjectDrawer<T> drawer;
	final List<T> objects = new ArrayList();
	Integer openGLListIndex;
	final boolean drawAsList;
	boolean drawAsVBO;
	VertexArrayHandler vah = null;

	SceneObjects(final ObjectDrawer<T> drawer, final boolean asList, final boolean asVBO) {
		this.drawer = drawer;
		drawAsList = asList;
		drawAsVBO = asVBO;
	}

	@Override
	public Iterator<T> iterator() {
		return objects.iterator();
	}

	protected void clearObjects() {
		objects.clear();
	}

	public void clear(final JOGLAWTGLRenderer renderer) {
		clearObjects();
		if ( openGLListIndex != null ) {
			renderer.getContext().makeCurrent();
			renderer.gl.glDeleteLists(openGLListIndex, 1);
			// renderer.getContext().release();
			openGLListIndex = null;
		}
	}

	public Integer getIndexInOpenGLList() {
		return openGLListIndex;
	}

	public void setIndexInOpenGLList(final Integer index) {
		this.openGLListIndex = index;
	}

	public List<T> getObjects() {
		return objects;
	}

	public void add(final T object) {
		objects.add(object);
	}

	public void draw(final boolean picking, JOGLAWTGLRenderer renderer) {
		
		
		if ( picking ) {
			if(renderer.colorPicking){
				drawer.getGL().glDisable(GL.GL_DITHER);
				drawer.getGL().glDisable(GL.GL_LIGHTING);
				drawer.getGL().glDisable(GL.GL_TEXTURE);
				/*drawer.getGL().glColor3f(1.0f,0,0);*/
				//http://elect86.wordpress.com/2013/02/04/jogl-color-picking/
				//drawer.getGL().glDrawBuffer(GL.GL_BACK);
		
	            /*for ( final T object : objects ) {
	            	System.out.println("object.index " + object.index);
	            	Color index = new Color(object.index);
	            	drawer.getGL().glColor3f(index.getRed() / 255.0f, index.getGreen() / 255.0f, index.getBlue() / 255.0f);
	            	System.out.println("index getRed : "+index.getRed() / 255.0f+
	            			" index getGreen : "+index.getGreen() / 255.0f+
	            			"index getBlue : "+index.getBlue() / 255.0f);
					object.draw(drawer, picking);
				}*/
	            
	            int viewport[] = new int[4];
	            drawer.getGL().glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

	            FloatBuffer pixels = FloatBuffer.allocate(4);
	            //drawer.getGL().glReadBuffer(GL.GL_BACK);
	            drawer.getGL().glReadPixels(drawer.renderer.camera.getLastxPressed(),viewport[3]-drawer.renderer.camera.getLastyPressed(),1,1,
	            GL.GL_RGBA,GL.GL_FLOAT,pixels);
	            
		        drawer.getGL().glEnable(GL.GL_DITHER);
	            drawer.getGL().glEnable(GL.GL_LIGHTING);
				drawer.getGL().glEnable(GL.GL_TEXTURE);
	            
	            
	            
	            Color index = new Color(pixels.get(0), pixels.get(1), pixels.get(2));	    
	            System.out.println("color picked " + index.toString());
	            //renderer.colorPicking=false;
			}
			else{
				drawer.getGL().glPushMatrix();
				drawer.getGL().glInitNames();
				drawer.getGL().glPushName(0);
				for ( final T object : objects ) {
					object.draw(drawer, picking);
				}
				drawer.getGL().glPopName();
				drawer.getGL().glPopMatrix();
			}
		} else if ( drawAsList ) {
			if ( openGLListIndex == null ) {

				openGLListIndex = drawer.getGL().glGenLists(1);
				drawer.getGL().glNewList(openGLListIndex, GL_COMPILE);
				for ( final T object : objects ) {
					object.draw(drawer, picking);
				}
				drawer.getGL().glEndList();
			}
			drawer.getGL().glCallList(openGLListIndex);
		}else if(drawAsVBO){
				if(vah == null)
				{
					vah= new VertexArrayHandler(renderer.gl, renderer.glu,renderer);
					vah.buildVertexArray((List<GeometryObject>) objects);
				}
				else
				{
					//vah.loadCollada();
//					vah.createVBOs();
				}
			
		}else {
			for ( final T object : objects ) {
				object.draw(drawer, picking);
			}
		}
	}

	public void dispose() {
		drawer.dispose();
		// if ( openGLListIndex != null ) {
		// drawer.getGL().glDeleteLists(openGLListIndex, 1);
		// }
	}

}