/*******************************************************************************************************
 *
 * WorldProjection.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.topology.projection;

import javax.measure.UnitConverter;

import org.locationtech.jts.geom.CoordinateFilter;
import org.locationtech.jts.geom.Geometry;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.runtime.IScope;

/**
 * The Class WorldProjection.
 */
public class WorldProjection extends Projection {

	/** The unit converter. */
	public UnitConverter unitConverter;

	/** The inverse unit converter. */
	public UnitConverter inverseUnitConverter;

	/**
	 * Instantiates a new world projection.
	 *
	 * @param scope
	 *            the scope
	 * @param crs
	 *            the crs
	 * @param env
	 *            the env
	 * @param fact
	 *            the fact
	 */
	public WorldProjection(final IScope scope, final CoordinateReferenceSystem crs, final Envelope3D env,
			final ProjectionFactory fact) {
		super(scope, null, crs, env, fact);
	}

	@Override
	public void translate(final Geometry geom) {
		if (projectedEnv != null) {
			geom.apply((CoordinateFilter) coord -> {
				coord.x -= projectedEnv.getMinX();
				coord.y = -coord.y + projectedEnv.getHeight() + projectedEnv.getMinY();

			});
			geom.geometryChanged();
		}
	}

	@Override
	public void inverseTranslate(final Geometry geom) {
		if (projectedEnv != null) {
			geom.apply((CoordinateFilter) coord -> {
				coord.x += projectedEnv.getMinX();
				coord.y = -coord.y + projectedEnv.getHeight() + projectedEnv.getMinY();
			});
			geom.geometryChanged();
		}
	}

	@Override
	public void convertUnit(final Geometry geom) {
		if (unitConverter != null) {
			geom.apply((CoordinateFilter) coord -> {
				coord.x = unitConverter.convert(coord.x);
				coord.y = unitConverter.convert(coord.y);
				coord.z = unitConverter.convert(coord.z);
			});
			geom.geometryChanged();
		}
	}

	@Override
	public void inverseConvertUnit(final Geometry geom) {
		if (inverseUnitConverter != null) {
			geom.apply((CoordinateFilter) coord -> {
				coord.x = inverseUnitConverter.convert(coord.x);
				coord.y = inverseUnitConverter.convert(coord.y);
				coord.z = inverseUnitConverter.convert(coord.z);
			});
			geom.geometryChanged();
		}
	}

	/**
	 * Update translations.
	 *
	 * @param env
	 *            the env
	 */
	public void updateTranslations(final Envelope3D env) {
		if (env != null) { projectedEnv = env; }
	}

	/**
	 * Update unit.
	 *
	 * @param uc
	 *            the unit converter
	 */
	public void updateUnit(final UnitConverter uc) {
		if (uc != null) {
			this.unitConverter = uc;
			this.inverseUnitConverter = uc.inverse();
		}
	}

}