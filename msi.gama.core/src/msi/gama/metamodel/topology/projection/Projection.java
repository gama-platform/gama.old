/*********************************************************************************************
 *
 * 'Projection.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.metamodel.topology.projection;

import org.geotools.geometry.jts.DefaultCoordinateSequenceTransformer;
import org.geotools.geometry.jts.GeometryCoordinateSequenceTransformer;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;

import msi.gama.common.util.GeometryUtils;
import msi.gama.runtime.IScope;

public class Projection implements IProjection {

	private final ProjectionFactory factory;
	private GeometryCoordinateSequenceTransformer transformer, inverseTransformer;
	CoordinateReferenceSystem initialCRS;
	Envelope projectedEnv;
	final IProjection referenceProjection;

	Projection(final IProjection world, final ProjectionFactory fact) {
		referenceProjection = world;
		factory = fact;
	}

	Projection(final IScope scope, final IProjection world, final CoordinateReferenceSystem crs, final Envelope env,
			final ProjectionFactory fact) {
		this.factory = fact;
		this.referenceProjection = world;
		initialCRS = crs;
		if (env != null) {
			if (CRS.getProjectedCRS(initialCRS) == null) {
				createTransformation(computeProjection(scope));
			}
			// We project the envelope and we use it for initializing the translations
			projectedEnv = transform(env);
			// createTranslations(projectedEnv.getMinX(), projectedEnv.getHeight(), projectedEnv.getMinY());
		}
	}

	@Override
	public void createTransformation(final MathTransform t) {
		if (t != null) {
			transformer = new GeometryCoordinateSequenceTransformer(new DefaultCoordinateSequenceTransformer(
					GeometryUtils.GEOMETRY_FACTORY.getCoordinateSequenceFactory()));
			// TODO see ConcatenatedTransformDirect2D
			transformer.setMathTransform(t);
			try {
				inverseTransformer = new GeometryCoordinateSequenceTransformer(new DefaultCoordinateSequenceTransformer(
						GeometryUtils.GEOMETRY_FACTORY.getCoordinateSequenceFactory()));
				inverseTransformer.setMathTransform(t.inverse());
			} catch (final NoninvertibleTransformException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Geometry transform(final Geometry g) {
		// Remove uselessly complicated multigeometries
		if (g instanceof GeometryCollection && g.getNumGeometries() == 1) { return transform(g.getGeometryN(0)); }
		Geometry geom = GeometryUtils.GEOMETRY_FACTORY.createGeometry(g);
		if (transformer != null) {
			try {
				geom = transformer.transform(g);
			} catch (final TransformException e) {
				e.printStackTrace();
			}
		}
		translate(geom);
		return geom;
	}

	public Geometry transform(final Geometry g, final boolean translate) {
		Geometry geom = GeometryUtils.GEOMETRY_FACTORY.createGeometry(g);
		if (transformer != null) {
			try {
				geom = transformer.transform(g);
			} catch (final TransformException e) {
				e.printStackTrace();
			}
		}
		if (translate)
			translate(geom);
		return geom;
	}

	Envelope transform(final Envelope g, final boolean translate) {
		if (transformer == null) { return g; }
		return transform(JTS.toGeometry(g), translate).getEnvelopeInternal();
	}

	Envelope transform(final Envelope g) {
		if (transformer == null) { return g; }
		return transform(JTS.toGeometry(g)).getEnvelopeInternal();
	}

	@Override
	public Geometry inverseTransform(final Geometry g) {
		Geometry geom = GeometryUtils.GEOMETRY_FACTORY.createGeometry(g);
		inverseTranslate(geom);
		if (inverseTransformer != null) {
			try {
				geom = inverseTransformer.transform(geom);
			} catch (final TransformException e) {
				e.printStackTrace();
			}
		}
		return geom;
	}

	MathTransform computeProjection(final IScope scope) {
		MathTransform crsTransformation = null;
		// ProjectionFactory.computeTargetCRS(longitude, latitude);
		try {
			crsTransformation = CRS.findMathTransform(initialCRS, getTargetCRS(scope), true);
		} catch (final FactoryException e) {
			e.printStackTrace();
			return null;
		}
		return crsTransformation;
	}

	@Override
	public CoordinateReferenceSystem getInitialCRS(final IScope scope) {
		return initialCRS;
	}

	@Override
	public Envelope getProjectedEnvelope() {
		return projectedEnv;
	}

	/**
	 * Method getTargetCRS()
	 * 
	 * @see msi.gama.metamodel.topology.projection.IProjection#getTargetCRS()
	 */
	@Override
	public CoordinateReferenceSystem getTargetCRS(final IScope scope) {
		if (referenceProjection != null) { return referenceProjection.getTargetCRS(scope); }
		return factory.getTargetCRS(scope);
	}

	/**
	 * Method translate()
	 * 
	 * @see msi.gama.metamodel.topology.projection.IProjection#translate(com.vividsolutions.jts.geom.Geometry)
	 */
	@Override
	public void translate(final Geometry geom) {
		if (referenceProjection != null) {
			referenceProjection.translate(geom);
		}
	}

	/**
	 * Method inverseTranslate()
	 * 
	 * @see msi.gama.metamodel.topology.projection.IProjection#inverseTranslate(com.vividsolutions.jts.geom.Geometry)
	 */
	@Override
	public void inverseTranslate(final Geometry geom) {
		if (referenceProjection != null) {
			referenceProjection.inverseTranslate(geom);
		}
	}

}
