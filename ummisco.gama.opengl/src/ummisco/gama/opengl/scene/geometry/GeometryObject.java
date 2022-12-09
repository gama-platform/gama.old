/*******************************************************************************************************
 *
 * GeometryObject.java, in ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.scene.geometry;

import org.locationtech.jts.geom.Geometry;

import msi.gama.common.geometry.GeometryUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gaml.statements.draw.DrawingAttributes;
import msi.gaml.statements.draw.DrawingAttributes.DrawerType;
import ummisco.gama.opengl.scene.AbstractObject;

/**
 * The Class GeometryObject.
 */
public class GeometryObject extends AbstractObject<Geometry, DrawingAttributes> {

	/**
	 * Instantiates a new geometry object.
	 *
	 * @param geometry
	 *            the geometry
	 * @param attributes
	 *            the attributes
	 */
	public GeometryObject(final Geometry geometry, final DrawingAttributes attributes) {
		super(geometry, attributes, DrawerType.GEOMETRY);
	}

	@Override
	public void getTranslationInto(final GamaPoint p) {
		final GamaPoint explicitLocation = getAttributes().getLocation();
		if (explicitLocation == null) {
			p.setLocation(0, 0, 0);
		} else {
			GeometryUtils.getContourCoordinates(getObject()).getCenter(p);
			p.negate();
			p.add(explicitLocation);
		}
	}

	@Override
	public void getTranslationForRotationInto(final GamaPoint p) {
		final GamaPoint explicitLocation = getAttributes().getLocation();
		if (explicitLocation == null) {
			// System.out.println(GeometryUtils.getContourCoordinates(getObject()).getEnvelope());
			GeometryUtils.getContourCoordinates(getObject()).getCenter(p);
			Double depth = getAttributes().getDepth();
			if (depth != null) {
				switch (getAttributes().type) {
					case SPHERE:
						p.z += depth;
						break;
					case CYLINDER:
					case PYRAMID:
					case CONE:
					case BOX:
					case CUBE:
						p.z += depth / 2;
						break;
					default:
						break;
				}
			}
		} else {
			p.setLocation(explicitLocation);
		}
	}

	@Override
	public void getTranslationForScalingInto(final GamaPoint p) {
		GeometryUtils.getContourCoordinates(getObject()).getCenter(p);
	}

}
