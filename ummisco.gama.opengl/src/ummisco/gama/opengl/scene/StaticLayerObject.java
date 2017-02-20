/*********************************************************************************************
 *
 * 'StaticLayerObject.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.util.List;

import ummisco.gama.opengl.Abstract3DRenderer;

public class StaticLayerObject extends LayerObject {

	static abstract class World extends StaticLayerObject {

		public World(final Abstract3DRenderer renderer) {
			super(renderer);
		}

		@Override
		public boolean isLightInteraction() {
			return false;
		}

		@Override
		public void draw(final OpenGL gl) {
			if (renderer.getPickingState().isPicking())
				return;
			gl.disableLighting();
			if (currentList.isEmpty()) {
				fillWithObjects(currentList);
			}
			super.draw(gl);
			gl.enableLighting();

		}

		@Override
		protected boolean isPickable() {
			return false;
		}

		abstract void fillWithObjects(List<AbstractObject> currentList);
	}

	public StaticLayerObject(final Abstract3DRenderer renderer) {
		super(renderer, null);
	}

	@Override
	public boolean isStatic() {
		return true;
	}

	@Override
	public void clear(final OpenGL gl) {}

}