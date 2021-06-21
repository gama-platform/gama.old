/*******************************************************************************************************
 *
 * ummisco.gama.opengl.scene.GeometryObject.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.scene;

import org.locationtech.jts.geom.Geometry;

import msi.gama.common.geometry.GeometryUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gaml.statements.draw.DrawingAttributes;

public class GeometryObject extends AbstractObject<Geometry, DrawingAttributes> {

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
					case CIRCLE:
						break;
					case GRIDLINE:
						break;
					case LINEARRING:
						break;
					case LINECYLINDER:
						break;
					case LINESTRING:
						break;
					case MULTILINESTRING:
						break;
					case MULTIPOINT:
						break;
					case MULTIPOLYGON:
						break;
					case NULL:
						break;
					case PLAN:
						break;
					case POINT:
						break;
					case POLYGON:
						break;
					case POLYHEDRON:
						break;
					case POLYPLAN:
						break;
					case ROUNDED:
						break;
					case SQUARE:
						break;
					case TEAPOT:
						break;
					case THREED_FILE:
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
