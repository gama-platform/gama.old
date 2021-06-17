/*******************************************************************************************************
 *
 * msi.gaml.statements.draw.TextDrawingAttributes.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
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

	private GamaFont font;
	public Boolean perspective;
	public GamaPoint anchor;
	public Double precision;

	public TextDrawingAttributes(final Scaling3D size, final AxisAngle rotation, final GamaPoint location,
			final GamaColor color) {
		super(size, rotation, location, color, null, null);
		setType(Type.POLYGON);
	}

	public void setPerspective(final Boolean perspective) {
		this.perspective = perspective;
	}

	@Override
	public GamaColor getColor() {
		if (isEmpty() && fill == null && border != null) return border;
		return super.getColor();
	}

	public void setAnchor(final GamaPoint anchor) {
		this.anchor = anchor;
	}

	@Override
	public GamaPoint getAnchor() {
		if (anchor == null) return super.getAnchor();
		return anchor;
	}

	public GamaFont getFont() {
		return font;
	}

	public void setFont(final GamaFont font) {
		this.font = font;
	}

	public boolean isPerspective() {
		return perspective == null ? true : perspective;
	}

	@Override
	public Double getDepth() {
		return depth == null ? 0d : depth;
	}

	public TextDrawingAttributes copyTranslatedBy(final GamaPoint p) {
		try {
			TextDrawingAttributes clone = (TextDrawingAttributes) this.clone();
			clone.setLocation(getLocation().plus(p));
			return clone;
		} catch (CloneNotSupportedException e) {}
		return new TextDrawingAttributes(getSize(), getRotation(), getLocation().plus(p), getColor());
	}

	public void setPrecision(final Double prec) {
		precision = Math.min(1, Math.max(0, prec));
	}

	public Double getPrecision() {
		return precision;
	}

}