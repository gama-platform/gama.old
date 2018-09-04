/*******************************************************************************************************
 *
 * ummisco.gama.opengl.scene.GeometryObject.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.opengl.scene;

import com.vividsolutions.jts.geom.Geometry;

import msi.gama.common.geometry.GeometryUtils;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gaml.statements.draw.FileDrawingAttributes;

public class GeometryObject extends AbstractObject<Geometry, FileDrawingAttributes> {

	public GeometryObject(final Geometry geometry, final FileDrawingAttributes attributes) {
		super(geometry, attributes);
	}

	@Override
	public DrawerType getDrawerType() {
		return DrawerType.GEOMETRY;
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
			GeometryUtils.getContourCoordinates(getObject()).getCenter(p);
		} else {
			GeometryUtils.getContourCoordinates(getObject()).getCenter(p);
			p.negate();
			p.add(explicitLocation);
		}
	}

	@Override
	public void getTranslationForScalingInto(final GamaPoint p) {
		GeometryUtils.getContourCoordinates(getObject()).getCenter(p);
	}

}
