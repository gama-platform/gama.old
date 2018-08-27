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

import msi.gaml.statements.draw.FileDrawingAttributes;

public class GeometryObject extends AbstractObject<Geometry, FileDrawingAttributes> {

	public GeometryObject(final Geometry geometry, final FileDrawingAttributes attributes) {
		super(geometry, attributes);
	}

	@Override
	public DrawerType getDrawerType() {
		return DrawerType.GEOMETRY;
	}

}
