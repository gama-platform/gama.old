package msi.gama.metamodel.topology.projection;


import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import org.locationtech.jts.geom.CoordinateFilter;
import org.locationtech.jts.geom.Geometry;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.runtime.IScope;

public class SimpleScalingProjection implements IProjection{

	public CoordinateFilter scaling, inverseScaling;

	@Override
	public void createTransformation(MathTransform t) {
		
	}
	
	public SimpleScalingProjection(Double scale) {
		if (scale != null) {
			createScalingTransformations(scale);
		}
		
	}

	@Override
	public Geometry transform(Geometry geom) {
		if (scaling != null) {
			geom.apply(scaling);
			geom.geometryChanged();
		}
		return geom;
	}

	@Override
	public Geometry inverseTransform(Geometry geom) {
		if (inverseScaling != null) {
			geom.apply(inverseScaling);
			geom.geometryChanged();
		}
		return geom;
	}
	
	public void createScalingTransformations(final Double scale) {
		scaling = coord -> {
			coord.x *= scale;
			coord.y *= scale;
			coord.z *= scale;
		};
		inverseScaling = coord -> {
			coord.x /= scale;
			coord.y /= scale;
			coord.z /= scale;
		};
	}

	@Override
	public CoordinateReferenceSystem getInitialCRS(IScope scope) {
		return null;
	}

	@Override
	public CoordinateReferenceSystem getTargetCRS(IScope scope) {
		return null;
	}

	@Override
	public Envelope3D getProjectedEnvelope() {
		return null;
	}

	@Override
	public void translate(Geometry geom) {
		
	}

	@Override
	public void inverseTranslate(Geometry geom) {
		
	}

	@Override
	public void convertUnit(Geometry geom) {
	}

	@Override
	public void inverseConvertUnit(Geometry geom) {
		
	}

}
