/*******************************************************************************************************
 *
 * GamaShapeFactory.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.shape;

import org.locationtech.jts.geom.Geometry;

import msi.gama.common.geometry.Envelope3D;

/**
 * A factory for creating GamaShape objects. Replaces the constuctors of GamaShape, all deprecated. They can be created
 * from JTS Geometries, IShapes, Envelope3D. Any further transformations, like translation, rotation, scaling, ... must
 * be taken care of using the GamaShape.withXXX (withRotation, withScaling, etc.) methods.
 *
 * So for instance, g = new GamaShape(previousShape, newGeometry, translation, rotation) now should be written :
 * 
 * g =
 * GamaShapeFactory.createFrom(newGeometry).withAttributesOf(previousShape).withTranslation(translation).withRotation(rotration);
 *
 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
 * @date 17 sept. 2023
 */
public class GamaShapeFactory {

	/**
	 * Creates a new GamaShape object from a JTS geometry
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param geom
	 *            the geom
	 * @return the gama shape
	 * @date 17 sept. 2023
	 */
	@SuppressWarnings ("deprecation")
	public static GamaShape createFrom(final Geometry geom) {
		return new GamaShape(geom);
	}

	/**
	 * Creates an empty shape.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 17 sept. 2023
	 */
	public static GamaShape create() {
		return createFrom((Geometry) null);
	}

	/**
	 * Creates a new GamaShape object from an Envelope3D
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param env
	 *            the env
	 * @return the gama shape
	 * @date 17 sept. 2023
	 */
	public static GamaShape createFrom(final Envelope3D env) {
		return createFrom(env == null ? Envelope3D.EMPTY.toGeometry() : env.toGeometry());
	}

	/**
	 * Creates a new GamaShape object from an existing IShape. The inner geometry is used to intialise the shape and
	 * their attributes are mixed.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param source
	 *            the source
	 * @return the gama shape
	 * @date 17 sept. 2023
	 */
	public static GamaShape createFrom(final IShape source) {
		if (source == null) return create();
		return createFrom(source.getInnerGeometry().copy()).withAttributesOf(source);
	}

}
