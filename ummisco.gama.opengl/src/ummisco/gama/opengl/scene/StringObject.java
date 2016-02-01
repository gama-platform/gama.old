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
import msi.gaml.statements.draw.DrawingData.DrawingAttributes;

public class StringObject extends AbstractObject {

	public final String string;

	public StringObject(final String string, final DrawingAttributes attributes, final LayerObject layer) {
		super(attributes, layer);
		this.string = string;
	}

	public StringObject(final String string, final GamaPoint location, final LayerObject layer) {
		this(string, new DrawingAttributes(location), layer);
		attributes.perspective = false;
		attributes.color = GamaColor.getInt(Color.black.getRGB());
	}

	public Font getFont() {
		if ( attributes == null ) { return null; }
		return attributes.font;
	}

	public boolean iisInPerspective() {
		return attributes != null && attributes.perspective;
	}

	@Override
	public GamaPoint getLocation() {
		return attributes.location;
	}

}
