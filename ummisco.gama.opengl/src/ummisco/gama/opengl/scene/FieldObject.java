/*******************************************************************************************************
 *
 * ummisco.gama.opengl.scene.FieldObject.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.opengl.scene;

import msi.gaml.statements.draw.FieldDrawingAttributes;

public class FieldObject extends AbstractObject<double[], FieldDrawingAttributes> {

	public FieldObject(final double[] dem, final FieldDrawingAttributes attributes) {
		super(dem, attributes);
	}

	@Override
	public DrawerType getDrawerType() {
		return DrawerType.FIELD;
	}

}
