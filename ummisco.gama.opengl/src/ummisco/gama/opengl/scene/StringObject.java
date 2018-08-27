/*******************************************************************************************************
 *
 * ummisco.gama.opengl.scene.StringObject.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.Font;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gaml.statements.draw.TextDrawingAttributes;

public class StringObject extends AbstractObject<String, TextDrawingAttributes> {

	public StringObject(final String string, final TextDrawingAttributes attributes) {
		super(string, attributes);
	}

	@Override
	public DrawerType getDrawerType() {
		return DrawerType.STRING;
	}

}
