/*********************************************************************************************
 *
 *
 * 'SceneObjects.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.nio.FloatBuffer;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import com.google.common.collect.Iterables;
import com.jogamp.opengl.*;
import com.jogamp.opengl.fixedfunc.GLLightingFunc;
import ummisco.gama.opengl.JOGLRenderer;
import ummisco.gama.opengl.utils.VertexArrayHandler;

public class SceneObjects<T extends AbstractObject> implements ISceneObjects<T> {

	public static class Static<T extends AbstractObject> extends SceneObjects<T> {

		Static(final ObjectDrawer<T> drawer, final boolean asList, final boolean asVBO) {
			super(drawer, asList, asVBO);
		}

		@Override
		public void add(final T object) {
			super.add(object);
			openGLListIndex = null;
		}

		@Override
		public void clear(final GL gl, final int traceSize, final boolean fading) {}
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
		currentList = newCurrentList();
		objects.add(currentList);
	}

	private List newCurrentList() {
		return new CopyOnWriteArrayList();
	}

	@Override
	public void clear(final GL gl, final int sizeLimit, final boolean fading) {
		isFading = fading;
		if ( sizeLimit == 0 ) {
			objects.clear();
		} else {
			int size = objects.size();
			for ( int i = 0, n = size - sizeLimit; i < n; i++ ) {
				objects.poll();
			}
		}
		currentList = newCurrentList();
		objects.offer(currentList);
		Integer index = openGLListIndex;
		if ( index != null ) {
			gl.getGL2().glDeleteLists(index, 1);
			openGLListIndex = null;
		}
	}

	@Override
	public void add(final T object) {
		currentList.add(object);
	}

	@Override
	public Iterable<T> getObjects() {
		return Iterables.concat(objects);
	}

	private void drawPicking(final GL2 gl, final JOGLRenderer renderer) {
		if ( renderer.colorPicking ) {
			gl.glDisable(GL.GL_DITHER);
			gl.glDisable(GLLightingFunc.GL_LIGHTING);
			gl.glDisable(GL.GL_TEXTURE);

			int viewport[] = new int[4];
			gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);

			FloatBuffer pixels = FloatBuffer.allocate(4);
			gl.glReadPixels(renderer.camera.getLastMousePressedPosition().x,
				viewport[3] - renderer.camera.getLastMousePressedPosition().y, 1, 1, GL.GL_RGBA, GL.GL_FLOAT, pixels);

			gl.glEnable(GL.GL_DITHER);
			gl.glEnable(GLLightingFunc.GL_LIGHTING);
			gl.glEnable(GL.GL_TEXTURE);

			// Color index = new Color(pixels.get(0), pixels.get(1), pixels.get(2));
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
						object.draw(gl, drawer, true);
						object.setAlpha(originalAlpha);
					} else {
						object.draw(gl, drawer, true);
					}
				}
			}

			gl.glPopName();
			gl.glPopMatrix();
		}

	}

	@Override
	public void draw(final GL2 gl, final boolean picking) {
		JOGLRenderer renderer = drawer.getRenderer();
		if ( objects.size() == 0 ) { return; }
		gl.glColor3d(1.0, 1.0, 1.0);
		if ( picking ) {
			drawPicking(gl, renderer);
			return;
		}
		if ( drawAsList ) {
			// for ( T object : objects.get(0) ) {
			// object.preload(gl, renderer);
			// }
			Integer index = openGLListIndex;
			if ( index == null ) {
				index = gl.glGenLists(1);
				gl.glNewList(index, GL2.GL_COMPILE);
				double alpha = 0d;
				int size = objects.size();
				double delta = size == 0 ? 0 : 1d / size;
				for ( final List<T> list : objects ) {
					alpha = alpha + delta;
					for ( T object : list ) {
						if ( isFading ) {
							double originalAlpha = object.getAlpha();
							object.setAlpha(originalAlpha * alpha);
							object.draw(gl, drawer, picking);
							object.setAlpha(originalAlpha);
						} else {
							object.draw(gl, drawer, picking);
						}
					}
				}
				gl.glEndList();
			}
			gl.glCallList(index);
			openGLListIndex = index;
		} else {
			for ( final T object : getObjects() ) {
				object.draw(gl, drawer, picking);
			}
		}
	}

	@Override
	public void preload(final GL2 gl) {
		JOGLRenderer renderer = drawer.getRenderer();
		if ( objects.size() == 0 ) { return; }
		for ( T object : objects.get(0) ) {
			object.preload(gl, renderer);
		}
	}

}