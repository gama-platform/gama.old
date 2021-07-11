/*******************************************************************************************************
 *
 * ummisco.gama.opengl.scene.StringObject.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.opengl.scene.text;

import msi.gaml.statements.draw.TextDrawingAttributes;
import ummisco.gama.opengl.scene.AbstractObject;
import ummisco.gama.opengl.scene.AbstractObject.DrawerType;

public class StringObject extends AbstractObject<String, TextDrawingAttributes> {

	public StringObject(final String string, final TextDrawingAttributes attributes) {
		super(string, attributes, DrawerType.STRING);
	}

}
