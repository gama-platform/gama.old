/*******************************************************************************************************
 *
 * msi.gama.metamodel.topology.projection.WorldProjection.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.metamodel.topology.projection;


import org.opengis.referencing.crs.CoordinateReferenceSystem;

import javax.measure.UnitConverter;

import org.locationtech.jts.geom.CoordinateFilter;
import org.locationtech.jts.geom.Geometry;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.runtime.IScope;

public class WorldProjection extends Projection {

	public CoordinateFilter gisToAbsoluteTranslation, absoluteToGisTranslation;
	
	public CoordinateFilter otherUnitToMeter, meterToOtherUnit;

	public WorldProjection(final IScope scope, final CoordinateReferenceSystem crs, final Envelope3D env,
			final ProjectionFactory fact) {
		super(scope, null, crs, env, fact);
		// referenceProjection = this;
		/*
		 * Remove the translation: this one is computed only when the world agent geometry is modified. if ( env != null
		 * ) { createTranslations(projectedEnv.getMinX(), projectedEnv.getHeight(), projectedEnv.getMinY()); }
		 */
	}

	@Override
	public void translate(final Geometry geom) {
		if (gisToAbsoluteTranslation != null) {
			geom.apply(gisToAbsoluteTranslation);
			geom.geometryChanged();
		}
	}

	@Override
	public void inverseTranslate(final Geometry geom) {
		if (absoluteToGisTranslation != null) {
			geom.apply(absoluteToGisTranslation);
			geom.geometryChanged();
		}
	}
	

	@Override
	public void convertUnit(Geometry geom) {
		if (otherUnitToMeter != null) {
			geom.apply(otherUnitToMeter);
			geom.geometryChanged();
		}
		
	}

	@Override
	public void inverseConvertUnit(Geometry geom) {
		if (meterToOtherUnit != null) {
			geom.apply(meterToOtherUnit);
			geom.geometryChanged();
		}
	}

	public void updateTranslations(final Envelope3D env) {
		if (env != null) {
			projectedEnv = env;
		}
		createTranslations(projectedEnv.getMinX(), projectedEnv.getHeight(), projectedEnv.getMinY());
	}
	
	public void updateUnit(final UnitConverter unitConverter) {
		if (unitConverter != null)
			createUnitTransformations(unitConverter);
	}

	public void createTranslations(final double minX, final double height, final double minY) {
		gisToAbsoluteTranslation = coord -> {
			coord.x -= minX;
			coord.y = -coord.y + height + minY;
		};
		absoluteToGisTranslation = coord -> {
			coord.x += minX;
			coord.y = -coord.y + height + minY;
		};
	}
	public void createUnitTransformations(final UnitConverter unitConverter) {
		otherUnitToMeter = coord -> {
			coord.x = unitConverter.convert(coord.x);
			coord.y = unitConverter.convert(coord.y);
			coord.z = unitConverter.convert(coord.z);
		};
		meterToOtherUnit = coord -> {
			coord.x = unitConverter.inverse().convert(coord.x);
			coord.y = unitConverter.inverse().convert(coord.y);
			coord.z = unitConverter.inverse().convert(coord.z);
		};
	}
	
	

}