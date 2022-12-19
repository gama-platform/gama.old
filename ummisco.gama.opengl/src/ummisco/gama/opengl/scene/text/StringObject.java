/*******************************************************************************************************
 *
 * StringObject.java, in ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.opengl.scene.text;

import msi.gaml.statements.draw.DrawingAttributes.DrawerType;
import msi.gaml.statements.draw.TextDrawingAttributes;
import ummisco.gama.opengl.scene.AbstractObject;

/**
 * The Class StringObject.
 */
public class StringObject extends AbstractObject<String, TextDrawingAttributes> {

	/**
	 * Instantiates a new string object.
	 *
	 * @param string the string
	 * @param attributes the attributes
	 */
	public StringObject(final String string, final TextDrawingAttributes attributes) {
		super(string, attributes, DrawerType.STRING);
	}

}
