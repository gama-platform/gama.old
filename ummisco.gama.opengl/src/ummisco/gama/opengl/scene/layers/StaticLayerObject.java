/*********************************************************************************************
 *
 * 'StaticLayerObject.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene.layers;

import java.util.List;

import ummisco.gama.opengl.OpenGL;
import ummisco.gama.opengl.renderer.IOpenGLRenderer;
import ummisco.gama.opengl.scene.AbstractObject;

public class StaticLayerObject extends LayerObject {

	public static abstract class World extends StaticLayerObject {

		public World(final IOpenGLRenderer renderer) {
			super(renderer);
		}

		@Override
		public boolean canSplit() {
			return false;
		}

		@Override
		public boolean isLightInteraction() {
			return false;
		}

		@Override
		public void draw(final OpenGL gl) {
			if (renderer.getPickingHelper().isPicking()) { return; }

			if (currentList.isEmpty()) {
				fillWithObjects(currentList);
			}

			gl.suspendZTranslation();
			final boolean previous = gl.setLighting(false);
			super.draw(gl);
			gl.setLighting(previous);
			gl.resumeZTranslation();

		}

		@Override
		protected boolean isPickable() {
			return false;
		}

		public abstract void fillWithObjects(List<AbstractObject> currentList);
	}

	public StaticLayerObject(final IOpenGLRenderer renderer) {
		super(renderer, null);
	}

	@Override
	public boolean isStatic() {
		return true;
	}

	@Override
	public void clear(final OpenGL gl) {}

}