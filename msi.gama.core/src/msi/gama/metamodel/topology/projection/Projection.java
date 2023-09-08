/*******************************************************************************************************
 *
 * Projection.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.metamodel.topology.projection;

import org.geotools.geometry.jts.DefaultCoordinateSequenceTransformer;
import org.geotools.geometry.jts.GeometryCoordinateSequenceTransformer;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.geometry.GeometryUtils;
import msi.gama.runtime.IScope;

/**
 * The Class Projection.
 */
public class Projection implements IProjection {

	/** The factory. */
	private final ProjectionFactory factory;
	
	/** The inverse transformer. */
	private GeometryCoordinateSequenceTransformer transformer, inverseTransformer;
	
	/** The initial CRS. */
	CoordinateReferenceSystem initialCRS;
	
	/** The projected env. */
	Envelope3D projectedEnv;
	
	/** The reference projection. */
	final IProjection referenceProjection;

	/**
	 * Instantiates a new projection.
	 *
	 * @param world the world
	 * @param fact the fact
	 */
	Projection(final IProjection world, final ProjectionFactory fact) {
		referenceProjection = world;
		factory = fact;
	}

	/**
	 * Instantiates a new projection.
	 *
	 * @param scope the scope
	 * @param world the world
	 * @param crs the crs
	 * @param env the env
	 * @param fact the fact
	 */
	Projection(final IScope scope, final IProjection world, final CoordinateReferenceSystem crs, final Envelope3D env,
			final ProjectionFactory fact) {
		this.factory = fact;
		this.referenceProjection = world;
		initialCRS = crs;
		if (env != null) {
			if (initialCRS != null && !initialCRS.equals(getTargetCRS(scope))) {
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
		if (g instanceof GeometryCollection && g.getNumGeometries() == 1) return transform(g.getGeometryN(0));
		return transform(g, true);
	}

	/**
	 * Transform.
	 *
	 * @param g the g
	 * @param translate the translate
	 * @return the geometry
	 */
	public Geometry transform(final Geometry g, final boolean translate) {
		Geometry geom = GeometryUtils.GEOMETRY_FACTORY.createGeometry(g);
		if (transformer != null) {
			try {
				geom = transformer.transform(geom);
			} catch (final TransformException e) {
				e.printStackTrace();
			}
		}
		if (translate) {
			translate(geom);
			convertUnit(geom);
		}
		return geom;
	}

	/**
	 * Transform.
	 *
	 * @param g the g
	 * @return the envelope 3 D
	 */
	Envelope3D transform(final Envelope3D g) {
		if (transformer == null) return g;
		return Envelope3D.of(transform(JTS.toGeometry(g)).getEnvelopeInternal());
	}

	@Override
	public Geometry inverseTransform(final Geometry g) {
		Geometry geom = GeometryUtils.GEOMETRY_FACTORY.createGeometry(g);
		inverseConvertUnit(geom);
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

	/**
	 * Compute projection.
	 *
	 * @param scope the scope
	 * @return the math transform
	 */
	MathTransform computeProjection(final IScope scope) {
		MathTransform crsTransformation = null;
		if (initialCRS == null) return null;
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
	public Envelope3D getProjectedEnvelope() {
		return projectedEnv;
	}

	/**
	 * Method getTargetCRS()
	 *
	 * @see msi.gama.metamodel.topology.projection.IProjection#getTargetCRS()
	 */
	@Override
	public CoordinateReferenceSystem getTargetCRS(final IScope scope) {
		if (referenceProjection != null) return referenceProjection.getTargetCRS(scope);
		return factory.getTargetCRS(scope);
	}

	/**
	 * Method translate()
	 *
	 * @see msi.gama.metamodel.topology.projection.IProjection#translate(org.locationtech.jts.geom.Geometry)
	 */
	@Override
	public void translate(final Geometry geom) {
		if (referenceProjection != null) { referenceProjection.translate(geom); }
	}

	/**
	 * Method inverseTranslate()
	 *
	 * @see msi.gama.metamodel.topology.projection.IProjection#inverseTranslate(org.locationtech.jts.geom.Geometry)
	 */
	@Override
	public void inverseTranslate(final Geometry geom) {
		if (referenceProjection != null) { referenceProjection.inverseTranslate(geom); }
	}

	@Override
	public void convertUnit(final Geometry geom) {
		if (referenceProjection != null) { referenceProjection.convertUnit(geom); }

	}

	@Override
	public void inverseConvertUnit(final Geometry geom) {
		if (referenceProjection != null) { referenceProjection.inverseConvertUnit(geom); }

	}

}
