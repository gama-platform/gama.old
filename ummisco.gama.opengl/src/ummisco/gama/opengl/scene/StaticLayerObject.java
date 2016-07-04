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

import com.jogamp.opengl.GL;
import com.vividsolutions.jts.geom.Geometry;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gaml.types.GamaGeometryType;
import ummisco.gama.opengl.Abstract3DRenderer;

public class StaticLayerObject extends LayerObject {

	static Geometry NULL_GEOM = GamaGeometryType.buildRectangle(0, 0, new GamaPoint(0, 0)).getInnerGeometry();
	static final GamaPoint WORLD_OFFSET = new GamaPoint();
	static final GamaPoint WORLD_SCALE = new GamaPoint(1, 1, 1);
	static final Double WORLD_ALPHA = 1d;

	public StaticLayerObject(final Abstract3DRenderer renderer) {
		super(renderer, null);
	}

	@Override
	public boolean isStatic() {
		return true;
	}

	@Override
	public void clear(final GL gl) {
	}

}