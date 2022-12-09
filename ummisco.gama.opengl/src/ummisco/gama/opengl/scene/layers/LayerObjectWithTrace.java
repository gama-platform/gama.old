/*******************************************************************************************************
 *
 * LayerObjectWithTrace.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.scene.layers;

import java.util.LinkedList;

import msi.gama.common.interfaces.ILayer;
import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;

/**
 * Class LayerObject.
 *
 * @author drogoul
 * @since 3 mars 2014
 *
 */
@SuppressWarnings ({ "rawtypes", "unchecked" })
public class LayerObjectWithTrace extends LayerObject {

	/** The traces. */
	protected final LinkedList<Trace> traces = new LinkedList();

	/**
	 * Instantiates a new layer object.
	 *
	 * @param renderer2
	 *            the renderer 2
	 * @param layer
	 *            the layer
	 */
	public LayerObjectWithTrace(final IOpenGLRenderer renderer2, final ILayer layer) {
		super(renderer2, layer);
		traces.add(currentList);
	}

	/**
	 * Draw all objects.
	 *
	 * @param gl
	 *            the gl
	 * @param picking
	 *            the picking
	 */
	@Override
	protected void drawAllObjects(final OpenGL gl, final boolean picking) {
		double delta = 0;
		if (isFading) {
			final int size = traces.size();
			delta = size == 0 ? 0 : 1d / size;
		}
		double alpha = 0d;
		for (final Trace list : traces) {
			alpha = delta == 0d ? this.alpha : this.alpha * (alpha + delta);
			drawObjects(gl, list, alpha, picking);
		}
	}

	/**
	 * Clear.
	 *
	 * @param gl
	 *            the gl
	 */
	@Override
	public void clear(final OpenGL gl) {
		final int sizeLimit = getTrace();
		isFading = getFading();
		final int size = traces.size();
		for (int i = 0, n = size - sizeLimit; i < n; i++) { traces.poll(); }
		currentList = new Trace();
		traces.offer(currentList);
		final Integer index = openGLListIndex;
		if (index != null) {
			gl.deleteList(index);
			openGLListIndex = null;
		}

	}

	/**
	 * Checks for trace.
	 *
	 * @return true, if successful
	 */
	@Override
	public boolean hasTrace() {
		return getTrace() > 0;
	}

	@Override
	protected int getTrace() {
		if (layer == null) return 0;
		final Integer trace = layer.getData().getTrace();
		return trace == null ? 0 : trace;
	}

	/**
	 * Number of traces.
	 *
	 * @return the int
	 */
	@Override
	public int numberOfActualTraces() {
		return traces.size();
	}

}
