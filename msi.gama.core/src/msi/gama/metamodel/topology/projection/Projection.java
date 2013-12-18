/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.metamodel.topology.projection;

import msi.gama.common.util.GeometryUtils;
import org.geotools.geometry.jts.*;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.*;
import com.vividsolutions.jts.geom.*;

public class Projection implements IProjection {

	private GeometryCoordinateSequenceTransformer transformer, inverseTransformer;
	CoordinateReferenceSystem initialCRS;
	Envelope projectedEnv;
	IProjection referenceProjection;

	Projection() {}

	Projection(final IProjection world, final CoordinateReferenceSystem crs, final Envelope env) {
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
		Geometry geom = GeometryUtils.factory.createGeometry(g);
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

	Envelope transform(final Envelope g) {
		if ( transformer == null ) { return g; }
		return transform(JTS.toGeometry(g)).getEnvelopeInternal();
	}

	@Override
	public Geometry inverseTransform(final Geometry g) {
		Geometry geom = GeometryUtils.factory.createGeometry(g);
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
			crsTransformation = CRS.findMathTransform(initialCRS, getTargetCRS());
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
		return ProjectionFactory.getTargetCRS();
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
