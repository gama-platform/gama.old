/*********************************************************************************************
 *
 *
 * 'StaticLayerObject.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.util.List;

import com.jogamp.opengl.GL2;

import ummisco.gama.opengl.Abstract3DRenderer;

public class StaticLayerObject extends LayerObject {

	static abstract class World extends StaticLayerObject {

		public World(final Abstract3DRenderer renderer) {
			super(renderer);
		}

		@Override
		public void drawWithoutShader(final GL2 gl) {
			gl.glDisable(GL2.GL_LIGHTING);
			if (currentList.isEmpty()) {
				fillWithObjects(currentList);
			}
			gl.glEnable(GL2.GL_LIGHTING);
			super.drawWithoutShader(gl);
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
	public void clear(final GL2 gl) {
	}

}