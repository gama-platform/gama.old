/*********************************************************************************************
 *
 * 'SimpleLayer.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.webgl;

import java.util.List;

import msi.gama.metamodel.shape.GamaPoint;
import ummisco.gama.modernOpenGL.DrawingEntity;

/**
 * A simplified representation of a LayerObject
 * 
 * @author drogoul
 *
 */
public class SimpleLayer {

	final GamaPoint offset;
	final GamaPoint scale;
	final Double alpha;
	final List<DrawingEntity> objects;

	public SimpleLayer(final GamaPoint offset, final GamaPoint scale, final Double alpha,
			final List<DrawingEntity> objects) {
		this.offset = offset;
		this.scale = scale;
		this.alpha = alpha;
		this.objects = objects;
	}
}
