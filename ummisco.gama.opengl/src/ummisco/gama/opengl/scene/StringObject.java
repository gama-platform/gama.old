/*********************************************************************************************
 *
 *
 * 'StringObject.java', in plugin 'msi.gama.jogl2', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.opengl.scene;

import java.awt.*;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.util.GamaColor;
import msi.gaml.statements.draw.*;

public class StringObject extends AbstractObject {

	public static GamaColor defaultTextColor = GamaColor.getInt(Color.black.getRGB());
	public final String string;

	public StringObject(final String string, final DrawingAttributes attributes, final LayerObject layer) {
		super(attributes, layer);
		this.string = string;
	}

	public StringObject(final String string, final GamaPoint location, final LayerObject layer) {
		this(string, new TextDrawingAttributes(null, null, location, defaultTextColor, null, false), layer);
	}

	public Font getFont() {
		if ( !(attributes instanceof TextDrawingAttributes) ) { return null; }
		return ((TextDrawingAttributes) attributes).font;
	}

	public boolean iisInPerspective() {
		if ( !(attributes instanceof TextDrawingAttributes) ) { return false; }
		return ((TextDrawingAttributes) attributes).perspective;
	}

}
