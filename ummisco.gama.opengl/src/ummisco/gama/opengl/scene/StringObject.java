/*********************************************************************************************
 *
 * 'StringObject.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.Color;
import java.awt.Font;

import msi.gama.util.GamaColor;
import msi.gaml.statements.draw.DrawingAttributes;
import msi.gaml.statements.draw.TextDrawingAttributes;

public class StringObject extends AbstractObject {

	public static GamaColor defaultTextColor = GamaColor.getInt(Color.black.getRGB());
	public final String string;

	public StringObject(final String string, final DrawingAttributes attributes) {
		super(attributes);
		this.string = string;
	}

	public Font getFont() {
		if (!(attributes instanceof TextDrawingAttributes)) { return null; }
		return ((TextDrawingAttributes) attributes).font;
	}

	public boolean iisInPerspective() {
		if (!(attributes instanceof TextDrawingAttributes)) { return false; }
		return ((TextDrawingAttributes) attributes).perspective;
	}

	@Override
	public DrawerType getDrawerType() {
		return DrawerType.STRING;
	}

}
