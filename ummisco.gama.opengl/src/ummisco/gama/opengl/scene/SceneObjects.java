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

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.collect.Iterables;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import ummisco.gama.opengl.JOGLRenderer;

public class SceneObjects<T extends AbstractObject> implements ISceneObjects<T> {

	public static class Static<T extends AbstractObject> extends SceneObjects<T> {

		Static(final ObjectDrawer<T> drawer) {
			super(drawer);
		}

		@Override
		public void add(final T object) {
			super.add(object);
			openGLListIndex = null;
		}

		@Override
		public void clear(final GL gl, final int traceSize, final boolean fading) {
		}
	}

	final ObjectDrawer<T> drawer;
	final LinkedList<List<T>> objects = new LinkedList();
	List<T> currentList;
	Integer openGLListIndex;
	boolean isFading;

	SceneObjects(final ObjectDrawer<T> drawer) {
		this.drawer = drawer;
		currentList = newCurrentList();
		objects.add(currentList);
	}

	private List newCurrentList() {
		return new CopyOnWriteArrayList();
	}

	@Override
	public void clear(final GL gl, final int sizeLimit, final boolean fading) {
		isFading = fading;
		if (sizeLimit == 0) {
			objects.clear();
		} else {
			final int size = objects.size();
			for (int i = 0, n = size - sizeLimit; i < n; i++) {
				objects.poll();
			}
		}
		currentList = newCurrentList();
		objects.offer(currentList);
		final Integer index = openGLListIndex;
		if (index != null) {
			gl.getGL2().glDeleteLists(index, 1);
			openGLListIndex = null;
		}
	}

	@Override
	public void add(final T object) {
		currentList.add(object);
	}

	@Override
	public void remove(final T object) {
		currentList.remove(object);
	}

	@Override
	public Iterable<T> getObjects() {
		return Iterables.concat(objects);
	}

	private void drawPicking(final GL2 gl, final JOGLRenderer renderer) {
		gl.glPushMatrix();
		gl.glInitNames();
		gl.glPushName(0);
		double alpha = 0d;
		final int size = objects.size();
		final double delta = size == 0 ? 0 : 1d / size;
		for (final List<T> list : objects) {
			alpha = alpha + delta;
			for (final T object : list) {
				if (isFading) {
					final double originalAlpha = object.getAlpha();
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

	@Override
	public void draw(final GL2 gl, final boolean picking) {
		final JOGLRenderer renderer = drawer.getRenderer();
		if (objects.size() == 0) {
			return;
		}
		renderer.setCurrentColor(gl, Color.white);
		// GLUtilGLContext.SetCurrentColor(gl, 1.0f, 1.0f, 1.0f);
		if (picking) {
			drawPicking(gl, renderer);
			return;
		}

		Integer index = openGLListIndex;
		if (index == null) {
			index = gl.glGenLists(1);
			gl.glNewList(index, GL2.GL_COMPILE);
			double alpha = 0d;
			final int size = objects.size();
			final double delta = size == 0 ? 0 : 1d / size;
			for (final List<T> list : objects) {
				alpha = alpha + delta;
				for (final T object : list) {
					if (isFading) {
						final double originalAlpha = object.getAlpha();
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

	}

	@Override
	public void preload(final GL2 gl) {
		final JOGLRenderer renderer = drawer.getRenderer();
		if (objects.size() == 0) {
			return;
		}
		for (final T object : objects.get(0)) {
			object.preload(gl, renderer);
		}
	}

}