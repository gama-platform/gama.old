package msi.gama.jogl.scene;

import static javax.media.opengl.GL.GL_COMPILE;
import java.util.*;

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
			drawer.getGL().glPushMatrix();
			drawer.getGL().glInitNames();
			drawer.getGL().glPushName(0);
			for ( final T object : objects ) {
				object.draw(drawer, picking);
			}
			drawer.getGL().glPopName();
			drawer.getGL().glPopMatrix();
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
					vah.createVBOs();
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