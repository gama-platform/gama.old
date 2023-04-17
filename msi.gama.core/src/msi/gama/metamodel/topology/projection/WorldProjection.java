/*******************************************************************************************************
 *
 * WorldProjection.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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

/**
 * The Class WorldProjection.
 */
public class WorldProjection extends Projection {

	/** The absolute to gis translation. */
	public CoordinateFilter gisToAbsoluteTranslation, absoluteToGisTranslation;
	
	/** The meter to other unit. */
	public CoordinateFilter otherUnitToMeter, meterToOtherUnit;

	/**
	 * Instantiates a new world projection.
	 *
	 * @param scope the scope
	 * @param crs the crs
	 * @param env the env
	 * @param fact the fact
	 */
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

	/**
	 * Update translations.
	 *
	 * @param env the env
	 */
	public void updateTranslations(final Envelope3D env) {
		if (env != null) {
			projectedEnv = env;
		}
		createTranslations(projectedEnv.getMinX(), projectedEnv.getHeight(), projectedEnv.getMinY());
	}
	
	/**
	 * Update unit.
	 *
	 * @param unitConverter the unit converter
	 */
	public void updateUnit(final UnitConverter unitConverter) {
		if (unitConverter != null)
			createUnitTransformations(unitConverter);
	}

	/**
	 * Creates the translations.
	 *
	 * @param minX the min X
	 * @param height the height
	 * @param minY the min Y
	 */
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
	
	/**
	 * Creates the unit transformations.
	 *
	 * @param unitConverter the unit converter
	 */
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