package ummisco.gama.webgl;

import java.util.List;

import msi.gama.metamodel.shape.GamaPoint;

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
	final List<SimpleGeometryObject> objects;

	public SimpleLayer(final GamaPoint offset, final GamaPoint scale, final Double alpha,
			final List<SimpleGeometryObject> objects) {
		this.offset = offset;
		this.scale = scale;
		this.alpha = alpha;
		this.objects = objects;
	}
}
