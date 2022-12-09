/*******************************************************************************************************
 *
 * StaticLayerObject.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.scene.layers;

import java.util.List;

import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;
import ummisco.gama.opengl.scene.AbstractObject;

/**
 * The Class StaticLayerObject.
 */
public class StaticLayerObject extends LayerObject {

	/**
	 * The Class World.
	 */
	public static abstract class World extends StaticLayerObject {

		/**
		 * Instantiates a new world.
		 *
		 * @param renderer
		 *            the renderer
		 */
		public World(final IOpenGLRenderer renderer) {
			super(renderer);
		}

		@Override
		public boolean canSplit() {
			return false;
		}

		@Override
		public void computeScale(final Trace list) {}

		@Override
		public void computeOffset(final Trace list) {}

		@Override
		public boolean isLightInteraction() { return false; }

		@Override
		public void draw(final OpenGL gl) {
			if (currentList.isEmpty()) { fillWithObjects(currentList); }
			gl.suspendZTranslation();
			final boolean previous = gl.setDisplayLighting(false);
			super.draw(gl);
			gl.setDisplayLighting(previous);
			gl.resumeZTranslation();

		}

		/**
		 * Fill with objects.
		 *
		 * @param list
		 *            the list
		 */
		public abstract void fillWithObjects(List<AbstractObject<?, ?>> list);
	}

	/**
	 * Instantiates a new static layer object.
	 *
	 * @param renderer
	 *            the renderer
	 */
	public StaticLayerObject(final IOpenGLRenderer renderer) {
		super(renderer, null);
	}

	@Override
	public boolean isStatic() { return true; }

	@Override
	public void clear(final OpenGL gl) {}

	@Override
	public boolean isPickable() { return false; }

}