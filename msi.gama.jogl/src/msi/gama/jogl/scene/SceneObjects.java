/*********************************************************************************************
 * 
 *
 * 'SceneObjects.java', in plugin 'msi.gama.jogl', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.jogl.scene;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.util.*;
import javax.media.opengl.GL;
import msi.gama.jogl.utils.*;
import com.google.common.collect.Iterables;

public class SceneObjects<T extends AbstractObject> {

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
		public void clear(final int traceSize, final boolean fading) {}
	}

	final ObjectDrawer<T> drawer;
	final LinkedList<List<T>> objects = new LinkedList();
	List<T> currentList;
	Integer openGLListIndex;
	final boolean drawAsList;
	boolean isFading;
	boolean drawAsVBO;
	VertexArrayHandler vah = null;

	SceneObjects(final ObjectDrawer<T> drawer, final boolean asList, final boolean asVBO) {
		this.drawer = drawer;
		drawAsList = asList;
		drawAsVBO = asVBO;
		currentList = new ArrayList();
		objects.add(currentList);
	}

	protected void clearObjects(final int sizeLimit) {
		while (objects.size() > sizeLimit) {
			objects.removeFirst();
		}
		currentList = new ArrayList();
		objects.addLast(currentList);
	}

	public void clear(final int sizeLimit, final boolean fading) {
		isFading = fading;
		clearObjects(sizeLimit);
		if ( openGLListIndex != null ) {
			drawer.getRenderer().getContext().makeCurrent();
			drawer.getRenderer().gl.glDeleteLists(openGLListIndex, 1);
			openGLListIndex = null;
		}
	}

	public Integer getIndexInOpenGLList() {
		return openGLListIndex;
	}

	public void setIndexInOpenGLList(final Integer index) {
		this.openGLListIndex = index;
	}

	public void add(final T object) {
		currentList.add(object);
	}

	public Iterable<T> getObjects() {
		return Iterables.concat(objects);
	}

	public void draw(final boolean picking) {
		JOGLAWTGLRenderer renderer = drawer.getRenderer();
		GL gl = drawer.getGL();
		gl.glColor3d(1.0,1.0,1.0);
		if ( picking ) {
			if ( renderer.colorPicking ) {
				gl.glDisable(GL.GL_DITHER);
				gl.glDisable(GL.GL_LIGHTING);
				gl.glDisable(GL.GL_TEXTURE);

				int viewport[] = new int[4];
				gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

				FloatBuffer pixels = FloatBuffer.allocate(4);
				gl.glReadPixels(renderer.camera.getLastMousePressedPosition().x,
					viewport[3] - renderer.camera.getLastMousePressedPosition().y, 1, 1, GL.GL_RGBA, GL.GL_FLOAT,
					pixels);

				gl.glEnable(GL.GL_DITHER);
				gl.glEnable(GL.GL_LIGHTING);
				gl.glEnable(GL.GL_TEXTURE);

				Color index = new Color(pixels.get(0), pixels.get(1), pixels.get(2));
				// System.out.println("color picked " + index.toString());
			} else {
				gl.glPushMatrix();
				gl.glInitNames();
				gl.glPushName(0);
				double alpha = 0d;
				int size = objects.size();
				double delta = size == 0 ? 0 : 1d / size;
				for ( final List<T> list : objects ) {
					alpha = alpha + delta;
					for ( T object : list ) {
						if ( isFading ) {
							double originalAlpha = object.getAlpha();
							object.setAlpha(originalAlpha * alpha);
							object.draw(drawer, picking);
							object.setAlpha(originalAlpha);
						} else {
							object.draw(drawer, picking);
						}
					}
				}

				gl.glPopName();
				gl.glPopMatrix();
			}
		} else if ( drawAsList ) {
			if ( openGLListIndex == null ) {

				openGLListIndex = gl.glGenLists(1);
				gl.glNewList(openGLListIndex, GL.GL_COMPILE);
				double alpha = 0d;
				int size = objects.size();
				double delta = size == 0 ? 0 : 1d / size;
				for ( final List<T> list : objects ) {
					alpha = alpha + delta;
					for ( T object : list ) {
						if ( isFading ) {
							double originalAlpha = object.getAlpha();
							object.setAlpha(originalAlpha * alpha);
							object.draw(drawer, picking);
							object.setAlpha(originalAlpha);
						} else {
							object.draw(drawer, picking);
						}
					}
				}
				gl.glEndList();
			}
			gl.glCallList(openGLListIndex);
		} else if ( drawAsVBO ) {
			if ( vah == null ) {
				vah = new VertexArrayHandler(gl, renderer.glu, renderer);
				vah.buildVertexArray(getObjects());
			} else {
				vah.loadCollada(null);
			}

		} else {
			for ( final T object : getObjects() ) {
				object.draw(drawer, picking);
			}
		}
	}

	public void dispose() {
		drawer.dispose();
	}

}