/*******************************************************************************************************
 *
 * msi.gaml.statements.draw.TextDrawingAttributes.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8)
 *
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements.draw;

import msi.gama.common.geometry.AxisAngle;
import msi.gama.common.geometry.Scaling3D;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape.Type;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaFont;

@SuppressWarnings ({ "rawtypes", "unchecked" })
public class TextDrawingAttributes extends DrawingAttributes implements Cloneable {

	public final GamaFont font;
	public final boolean perspective;
	public final GamaPoint anchor;

	public TextDrawingAttributes(final Scaling3D size, final AxisAngle rotation, final GamaPoint location,
			final GamaPoint anchor, final GamaColor color, final GamaFont font, final Boolean perspective) {
		super(size, rotation, location, color, null, null);
		setType(Type.POLYGON);
		this.font = font;
		this.anchor = anchor;
		this.perspective = perspective == null || perspective.booleanValue();
	}

	public TextDrawingAttributes copyTranslatedBy(final GamaPoint p) {
		return new TextDrawingAttributes(getSize(), getRotation(), getLocation().plus(p), getAnchor(), getColor(), font,
				perspective);
	}

	@Override
	public GamaPoint getAnchor() {
		return anchor;
	}

}