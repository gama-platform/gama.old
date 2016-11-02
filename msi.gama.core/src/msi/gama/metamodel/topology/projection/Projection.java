/*********************************************************************************************
 *
 * 'Projection.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.metamodel.topology.projection;

import msi.gama.common.util.GeometryUtils;
import org.geotools.geometry.jts.*;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.*;
import com.vividsolutions.jts.geom.*;

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

	Projection(final IProjection world, final CoordinateReferenceSystem crs, final Envelope env,
		final ProjectionFactory fact) {
		this.factory = fact;
		this.referenceProjection = world;
		initialCRS = crs;
		if ( env != null ) {
			if ( CRS.getProjectedCRS(initialCRS) == null ) {
				createTransformation(computeProjection());
			}
			// We project the envelope and we use it for initializing the translations
			projectedEnv = transform(env);
			// createTranslations(projectedEnv.getMinX(), projectedEnv.getHeight(), projectedEnv.getMinY());
		}
	}

	@Override
	public void createTransformation(final MathTransform t) {
		if ( t != null ) {
			transformer = new GeometryCoordinateSequenceTransformer();
			// TODO see ConcatenatedTransformDirect2D
			transformer.setMathTransform(t);
			try {
				inverseTransformer = new GeometryCoordinateSequenceTransformer();
				inverseTransformer.setMathTransform(t.inverse());
			} catch (NoninvertibleTransformException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Geometry transform(final Geometry g) {
		Geometry geom = GeometryUtils.FACTORY.createGeometry(g);
		if ( transformer != null ) {
			try {
				geom = transformer.transform(g);
			} catch (TransformException e) {
				e.printStackTrace();
			}
		}
		translate(geom);
		return geom;
	}
	
	public Geometry transform(final Geometry g, final boolean translate) {
		Geometry geom = GeometryUtils.FACTORY.createGeometry(g);
		if ( transformer != null ) {
			try {
				geom = transformer.transform(g);
			} catch (TransformException e) {
				e.printStackTrace();
			}
		}
		if(translate) translate(geom);
		return geom;
	}
	
	Envelope transform(final Envelope g, final boolean translate) {
		if ( transformer == null ) { return g; }
		return transform(JTS.toGeometry(g), translate).getEnvelopeInternal();
	}

	Envelope transform(final Envelope g) {
		if ( transformer == null ) { return g; }
		return transform(JTS.toGeometry(g)).getEnvelopeInternal();
	}

	@Override
	public Geometry inverseTransform(final Geometry g) {
		Geometry geom = GeometryUtils.FACTORY.createGeometry(g);
		inverseTranslate(geom);
		if ( inverseTransformer != null ) {
			try {
				geom = inverseTransformer.transform(geom);
			} catch (TransformException e) {
				e.printStackTrace();
			}
		}
		return geom;
	}

	MathTransform computeProjection() {
		MathTransform crsTransformation = null;
		// ProjectionFactory.computeTargetCRS(longitude, latitude);
		try {
			crsTransformation = CRS.findMathTransform(initialCRS, getTargetCRS(), true);
		} catch (FactoryException e) {
			e.printStackTrace();
			return null;
		}
		return crsTransformation;
	}

	@Override
	public CoordinateReferenceSystem getInitialCRS() {
		return initialCRS;
	}

	@Override
	public Envelope getProjectedEnvelope() {
		return projectedEnv;
	}

	/**
	 * Method getTargetCRS()
	 * @see msi.gama.metamodel.topology.projection.IProjection#getTargetCRS()
	 */
	@Override
	public CoordinateReferenceSystem getTargetCRS() {
		if ( referenceProjection != null ) { return referenceProjection.getTargetCRS(); }
		return factory.getTargetCRS();
	}

	/**
	 * Method translate()
	 * @see msi.gama.metamodel.topology.projection.IProjection#translate(com.vividsolutions.jts.geom.Geometry)
	 */
	@Override
	public void translate(final Geometry geom) {
		if ( referenceProjection != null ) {
			referenceProjection.translate(geom);
		}
	}

	/**
	 * Method inverseTranslate()
	 * @see msi.gama.metamodel.topology.projection.IProjection#inverseTranslate(com.vividsolutions.jts.geom.Geometry)
	 */
	@Override
	public void inverseTranslate(final Geometry geom) {
		if ( referenceProjection != null ) {
			referenceProjection.inverseTranslate(geom);
		}
	}

}
