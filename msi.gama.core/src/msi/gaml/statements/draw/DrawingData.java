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
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaFont;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMaterial;
import msi.gama.util.GamaPair;
import msi.gama.util.IList;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.IUnits;
import msi.gaml.types.GamaBoolType;
import msi.gaml.types.GamaColorType;
import msi.gaml.types.GamaFloatType;
import msi.gaml.types.GamaFontType;
import msi.gaml.types.GamaListType;
import msi.gaml.types.GamaMaterialType;
import msi.gaml.types.GamaPairType;
import msi.gaml.types.GamaPointType;
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
public class DrawingData {

	@FunctionalInterface
	interface Evaluator<V> {
		V value(IScope scope);

		default V getConstValue() {
			try {
				return value(null);
			} catch (final RuntimeException e) {
				return null;
			}
		}
	}

	abstract class Attribute<T extends IType, V> implements Evaluator<V> {
		V value;

		void refresh(final IScope scope) {
			value = value(scope);
		}
	}

	class ConstantAttribute<T extends IType, V> extends Attribute<T, V> {

		ConstantAttribute(final V value) {
			this.value = value;
		}

		@Override
		void refresh(final IScope scope) {}

		@Override
		public V value(final IScope scope) {
			return value;
		}

	}

	class ExpressionAttribute<T extends IType, V> extends Attribute<T, V> {
		final Evaluator<V> evaluator;

		public ExpressionAttribute(final Evaluator<V> ev) {
			evaluator = ev;
		}

		@Override
		public V value(final IScope scope) {
			return evaluator.value(scope);
		}
	}

	<T extends IType<V>, V> Attribute create(final IExpression exp, final T type, final V def) {
		return create(exp, (scope) -> type.cast(scope, exp.value(scope), null, true), type, def);
	}

	<T extends IType<V>, V> Attribute create(final IExpression exp, final Evaluator ev, final T type, final V def) {
		if (exp != null
				&& exp.isConst()) { return new ConstantAttribute(type.cast(null, ev.getConstValue(), null, true)); }
		if (exp == null) { return new ConstantAttribute(def); }
		return new ExpressionAttribute(ev);
	}

	static final GamaColor DEFAULT_BORDER_COLOR = new GamaColor(Color.BLACK);

	final Attribute<GamaPointType, GamaPoint> size;
	final Attribute<GamaFloatType, Double> depth;
	final Attribute<GamaPairType, GamaPair<Double, GamaPoint>> rotation;
	final Attribute<GamaPointType, GamaPoint> location;
	final Attribute<GamaPointType, GamaPoint> anchor;
	final Attribute<GamaBoolType, Boolean> empty;
	final Attribute<GamaColorType, GamaColor> border;
	private final Attribute<GamaListType, IList<GamaColor>> colors;
	final Attribute<GamaFontType, GamaFont> font;
	final Attribute<GamaListType, IList> texture;
	final Attribute<GamaMaterialType, GamaMaterial> material;
	final Attribute<GamaBoolType, Boolean> perspective;
	final Attribute<GamaFloatType, Double> lineWidth;
	final Attribute<GamaBoolType, Boolean> lighting;

	final Attribute[] ATTRIBUTES;

	public DrawingData(final IExpression sizeExp, final IExpression depthExp, final IExpression rotationExp,
			final IExpression locationExp, final IExpression anchorExp, final IExpression emptyExp,
			final IExpression borderExp, final IExpression colorExp, final IExpression fontExp,
			final IExpression textureExp, final IExpression materialExp, final IExpression perspectiveExp,
			final IExpression lineWidthExp, final IExpression lightingExp) {
		this.size = create(sizeExp, (scope) -> {
			if (sizeExp.getGamlType().isNumber()) {
				final double val = Cast.asFloat(scope, sizeExp.value(scope));
				// We do not consider the z ordinate -- see Issue #1539
				return new GamaPoint(val, val, 0);
			} else {
				return (GamaPoint) sizeExp.value(scope);
			}
		}, Types.POINT, null);
		this.lighting = create(lightingExp, Types.BOOL, true);
		this.depth = create(depthExp, Types.FLOAT, null);
		this.rotation = create(rotationExp, (scope) -> {
			if (rotationExp.getGamlType().getGamlType() == Types.PAIR) {
				final GamaPair currentRotation = Cast.asPair(scope, rotationExp.value(scope), true);
				currentRotation.key = Cast.asFloat(scope, currentRotation.key);
				return currentRotation;
			} else {
				final GamaPair currentRotation =
						new GamaPair(scope, rotationExp.value(scope), Rotation3D.PLUS_K, Types.FLOAT, Types.POINT);
				currentRotation.key = Cast.asFloat(scope, currentRotation.key);
				return currentRotation;
			}
		}, Types.PAIR, null);
		this.anchor = create(anchorExp, (scope) -> {
			final GamaPoint p = (GamaPoint) anchorExp.value(scope);
			p.x = Math.min(1d, Math.max(p.x, 0d));
			p.y = Math.min(1d, Math.max(p.y, 0d));
			return p;
		}, Types.POINT, IUnits.bottom_left);
		this.location = create(locationExp, Types.POINT, null);
		this.empty = create(emptyExp, Types.BOOL, false);
		this.border = create(borderExp, (scope) -> {
			if (borderExp.getGamlType() == Types.BOOL) {
				final boolean hasBorder = Cast.asBool(scope, borderExp.value(scope));
				if (hasBorder) { return DEFAULT_BORDER_COLOR; }
				return null;
			} else {
				return borderExp.value(scope);
			}
		}, Types.COLOR, null);
		this.colors = create(colorExp, (scope) -> {
			switch (colorExp.getGamlType().id()) {
				case IType.COLOR:
					final GamaColor currentColor = (GamaColor) colorExp.value(scope);
					return GamaListFactory.createWithoutCasting(Types.COLOR, currentColor);
				case IType.LIST:
					return (IList) colorExp.value(scope);
				default:
					return null;
			}

		}, Types.LIST, null);
		this.font = create(fontExp, Types.FONT, GamaFontType.DEFAULT_DISPLAY_FONT.getValue());
		this.texture = create(textureExp, (scope) -> {
			if (textureExp.getGamlType().getGamlType() == Types.LIST) {
				return GamaListType.staticCast(scope, textureExp.value(scope), Types.STRING, false);
			} else {
				return GamaListFactory.createWithoutCasting(Types.NO_TYPE, textureExp.value(scope));
			}
		}, Types.LIST, null);
		this.material = create(materialExp, Types.MATERIAL, null);
		this.perspective = create(perspectiveExp, Types.BOOL, true);
		this.lineWidth = create(lineWidthExp, Types.FLOAT, GamaPreferences.Displays.CORE_LINE_WIDTH.getValue());
		ATTRIBUTES = new Attribute[] { size, location, anchor, depth, colors, rotation, empty, border, font, texture,
				material, perspective, lineWidth, lighting };
	}

	public DrawingData computeAttributes(final IScope scope) {
		for (final Attribute a : ATTRIBUTES) {
			a.refresh(scope);
		}
		return this;
	}

	public GamaColor getCurrentColor() {
		if (colors.value == null || colors.value.isEmpty()) { return null; }
		return colors.value.get(0);
	}

	public List<GamaColor> getColors() {
		if (colors.value == null || colors.value.isEmpty() || colors.value.size() == 1) { return null; }
		return colors.value;
	}

}
