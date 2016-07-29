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
