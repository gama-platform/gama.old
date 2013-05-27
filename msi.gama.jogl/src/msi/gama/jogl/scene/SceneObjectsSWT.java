package msi.gama.jogl.scene;

import static javax.media.opengl.GL.GL_COMPILE;
import java.util.*;
import msi.gama.jogl.utils.JOGLSWTGLRenderer;

public class SceneObjectsSWT<T extends AbstractObjectSWT> implements Iterable<T> {

	public static class Static<T extends AbstractObjectSWT> extends SceneObjectsSWT<T> {

		Static(ObjectDrawerSWT<T> drawer, boolean asList) {
			super(drawer, asList);
		}

		@Override
		public void add(T object) {
			if ( openGLListIndex != null ) { return; }
			super.add(object);
		}

		@Override
		public void clear(JOGLSWTGLRenderer renderer) {}
	}

	final ObjectDrawerSWT<T> drawer;
	final List<T> objects = new ArrayList();
	Integer openGLListIndex;
	final boolean drawAsList;

	public SceneObjectsSWT(ObjectDrawerSWT<T> drawer, boolean asList) {
		this.drawer = drawer;
		drawAsList = asList;
	}

	@Override
	public Iterator<T> iterator() {
		return objects.iterator();
	}

	protected void clearObjects() {
		objects.clear();
	}

	public void clear(JOGLSWTGLRenderer renderer) {
		clearObjects();
		if ( openGLListIndex != null ) {
			renderer.gl.glDeleteLists(openGLListIndex, 1);
			openGLListIndex = null;
		}
	}

	public Integer getIndexInOpenGLList() {
		return openGLListIndex;
	}

	public void setIndexInOpenGLList(Integer index) {
		this.openGLListIndex = index;
	}

	public List<T> getObjects() {
		return objects;
	}

	public void add(T object) {
		objects.add(object);
	}

	public void draw(boolean picking) {
		if ( picking ) {
			drawer.getGL().glPushMatrix();
			drawer.getGL().glInitNames();
			drawer.getGL().glPushName(0);
			for ( T object : objects ) {
				object.draw(drawer, picking);
			}
			drawer.getGL().glPopName();
			drawer.getGL().glPopMatrix();
		} else if ( drawAsList ) {
			if ( openGLListIndex == null ) {
				openGLListIndex = drawer.getGL().glGenLists(1);
				drawer.getGL().glNewList(openGLListIndex, GL_COMPILE);
				for ( T object : objects ) {
					object.draw(drawer, picking);
				}
				drawer.getGL().glEndList();
			}
			drawer.getGL().glCallList(openGLListIndex);
		} else {
			for ( T object : objects ) {
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