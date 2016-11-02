/*********************************************************************************************
 *
 * 'WorldProjection.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.metamodel.topology.projection;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class WorldProjection extends Projection {

	public CoordinateFilter gisToAbsoluteTranslation, absoluteToGisTranslation;

	public WorldProjection(final CoordinateReferenceSystem crs, final Envelope env, final ProjectionFactory fact) {
		super(null, crs, env, fact);
		// referenceProjection = this;
		/*
		 * Remove the translation: this one is computed only when the world
		 * agent geometry is modified. if ( env != null ) {
		 * createTranslations(projectedEnv.getMinX(), projectedEnv.getHeight(),
		 * projectedEnv.getMinY()); }
		 */
	}

	@Override
	public void translate(final Geometry geom) {
		if (gisToAbsoluteTranslation != null) {
			geom.apply(gisToAbsoluteTranslation);
		}
	}

	@Override
	public void inverseTranslate(final Geometry geom) {
		if (absoluteToGisTranslation != null) {
			geom.apply(absoluteToGisTranslation);
		}
	}

	public void updateTranslations(final Envelope env) {
		if (env != null) {
			projectedEnv = env;
		}
		createTranslations(projectedEnv.getMinX(), projectedEnv.getHeight(), projectedEnv.getMinY());
	}

	public void createTranslations(final double minX, final double height, final double minY) {
		// if (gisToAbsoluteTranslation != null && absoluteToGisTranslation !=
		// null) {
		// return;
		// }
		gisToAbsoluteTranslation = new CoordinateFilter() {

			@Override
			public void filter(final Coordinate coord) {
				coord.x -= minX;
				coord.y = -coord.y + height + minY;
			}
		};
		absoluteToGisTranslation = new CoordinateFilter() {

			@Override
			public void filter(final Coordinate coord) {
				coord.x += minX;
				coord.y = -coord.y + height + minY;
			}
		};
	}

}