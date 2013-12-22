package msi.gama.jogl.scene;

import static javax.media.opengl.GL.GL_COMPILE;
import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.*;
import javax.media.opengl.GL;
import msi.gama.jogl.utils.*;

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

	public void draw(final boolean picking, final JOGLAWTGLRenderer renderer) {

		if ( picking ) {
			if ( renderer.colorPicking ) {
				drawer.getGL().glDisable(GL.GL_DITHER);
				drawer.getGL().glDisable(GL.GL_LIGHTING);
				drawer.getGL().glDisable(GL.GL_TEXTURE);

				int viewport[] = new int[4];
				drawer.getGL().glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

				FloatBuffer pixels = FloatBuffer.allocate(4);
				drawer.getGL().glReadPixels(drawer.renderer.camera.getLastMousePressedPosition().x,
					viewport[3] - drawer.renderer.camera.getLastMousePressedPosition().y, 1, 1, GL.GL_RGBA,
					GL.GL_FLOAT, pixels);

				drawer.getGL().glEnable(GL.GL_DITHER);
				drawer.getGL().glEnable(GL.GL_LIGHTING);
				drawer.getGL().glEnable(GL.GL_TEXTURE);

				Color index = new Color(pixels.get(0), pixels.get(1), pixels.get(2));
				System.out.println("color picked " + index.toString());
			} else {
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
		} else if ( drawAsVBO ) {
			if ( vah == null ) {
				vah = new VertexArrayHandler(renderer.gl, renderer.glu, renderer);
				vah.buildVertexArray((List<GeometryObject>) objects);
			} else {
				vah.loadCollada(null);
			}

		} else {
			for ( final T object : objects ) {
				object.draw(drawer, picking);
			}
		}
	}

	public void dispose() {
		drawer.dispose();
	}

}