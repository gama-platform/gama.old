/*********************************************************************************************
 *
 * 'DrawingData.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.statements.draw;

import java.awt.Color;
import java.util.List;

import msi.gama.common.geometry.Rotation3D;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaFont;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMaterial;
import msi.gama.util.GamaPair;
import msi.gama.util.IList;
import msi.gaml.operators.Cast;
import msi.gaml.operators.IUnits;
import msi.gaml.types.GamaFontType;
import msi.gaml.types.GamaListType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Class DrawingData. This class contains a number of attributes to help draw geometries, pictures, files and text.
 * These attributes are supplied either by the draw statement or by the layer
 *
 * @author drogoul
 * @since 28 janv. 2016
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class DrawingData extends AttributeHolder {

	static final GamaColor DEFAULT_BORDER_COLOR = new GamaColor(Color.BLACK);

	final Attribute<ILocation> size;
	final Attribute<Double> depth;
	final Attribute<GamaPair<Double, GamaPoint>> rotation;
	final Attribute<ILocation> location;
	final Attribute<ILocation> anchor;
	final Attribute<Boolean> empty;
	final Attribute<GamaColor> border;
	private final Attribute<IList<GamaColor>> colors;
	final Attribute<GamaFont> font;
	final Attribute<IList> texture;
	final Attribute<GamaMaterial> material;
	final Attribute<Boolean> perspective;
	final Attribute<Double> lineWidth;
	final Attribute<Boolean> lighting;

	public DrawingData(final DrawStatement symbol) {
		super(symbol);

		this.size = create(IKeyword.SIZE, (scope, exp) -> {
			if (exp.getGamlType().isNumber()) {
				final double val = Cast.asFloat(scope, exp.value(scope));
				// We do not consider the z ordinate -- see Issue #1539
				return new GamaPoint(val, val, 0);
			} else {
				return (GamaPoint) exp.value(scope);
			}
		}, Types.POINT, null);
		this.lighting = create(IKeyword.LIGHTED, Types.BOOL, true);
		this.depth = create(IKeyword.DEPTH, Types.FLOAT, null);
		this.rotation = create(IKeyword.ROTATE, (scope, exp) -> {
			if (exp.getGamlType().getGamlType() == Types.PAIR) {
				final GamaPair currentRotation = Cast.asPair(scope, exp.value(scope), true);
				currentRotation.key = Cast.asFloat(scope, currentRotation.key);
				return currentRotation;
			} else {
				final GamaPair currentRotation =
						new GamaPair(scope, exp.value(scope), Rotation3D.PLUS_K, Types.FLOAT, Types.POINT);
				currentRotation.key = Cast.asFloat(scope, currentRotation.key);
				return currentRotation;
			}
		}, Types.PAIR, null);
		this.anchor = create(IKeyword.ANCHOR, (scope, exp) -> {
			final GamaPoint p = (GamaPoint) exp.value(scope);
			p.x = Math.min(1d, Math.max(p.x, 0d));
			p.y = Math.min(1d, Math.max(p.y, 0d));
			return p;
		}, Types.POINT, IUnits.bottom_left);
		this.location = create(IKeyword.AT, Types.POINT, null);
		this.empty = create(IKeyword.EMPTY, Types.BOOL, false);
		this.border = create(IKeyword.BORDER, (scope, exp) -> {
			if (exp.getGamlType() == Types.BOOL) {
				final boolean hasBorder = Cast.asBool(scope, exp.value(scope));
				if (hasBorder) { return DEFAULT_BORDER_COLOR; }
				return null;
			} else {
				return (GamaColor) exp.value(scope);
			}
		}, Types.COLOR, null);
		this.colors = create(IKeyword.COLOR, (scope, exp) -> {
			switch (exp.getGamlType().id()) {
				case IType.COLOR:
					final GamaColor currentColor = (GamaColor) exp.value(scope);
					return GamaListFactory.createWithoutCasting(Types.COLOR, currentColor);
				case IType.LIST:
					return (IList) exp.value(scope);
				default:
					return null;
			}

		}, Types.LIST, null);
		this.font = create(IKeyword.FONT, Types.FONT, GamaFontType.DEFAULT_DISPLAY_FONT.getValue());
		this.texture = create(IKeyword.TEXTURE, (scope, exp) -> {
			if (exp.getGamlType().getGamlType() == Types.LIST) {
				return GamaListType.staticCast(scope, exp.value(scope), Types.STRING, false);
			} else {
				return GamaListFactory.createWithoutCasting(Types.NO_TYPE, exp.value(scope));
			}
		}, Types.LIST, null);
		this.material = create(IKeyword.MATERIAL, Types.MATERIAL, null);
		this.perspective = create(IKeyword.PERSPECTIVE, Types.BOOL, true);
		this.lineWidth = create(IKeyword.WIDTH, Types.FLOAT, GamaPreferences.Displays.CORE_LINE_WIDTH.getValue());

	}

	public GamaColor getCurrentColor() {
		if (colors.get() == null || colors.get().isEmpty()) { return null; }
		return colors.get().get(0);
	}

	public List<GamaColor> getColors() {
		if (colors.get() == null || colors.get().isEmpty() || colors.get().size() == 1) { return null; }
		return colors.get();
	}

	public GamaPoint getLocation() {
		return location.get() == null ? null : location.get().toGamaPoint();
	}

	public GamaPoint getAnchor() {
		return anchor.get() == null ? null : anchor.get().toGamaPoint();
	}

}
