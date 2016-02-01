/**
 * Created by drogoul, 28 janv. 2016
 *
 */
package msi.gaml.statements.draw;

import java.awt.Color;
import java.util.*;
import msi.gama.common.GamaPreferences;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.IScope;
import msi.gama.util.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;

/**
 * Class DrawingData. This class contains a number of attributes to help draw geometries, pictures, files and text. These attributes are supplied either by the draw statement or by the layer
 *
 * @author drogoul
 * @since 28 janv. 2016
 *
 */
public class DrawingData {

	static final GamaPoint DEFAULT_AXIS = new GamaPoint(0, 0, 1);
	static final GamaColor DEFAULT_BORDER_COLOR = new GamaColor(Color.BLACK);

	final IExpression sizeExp;
	final IExpression depthExp;
	final IExpression rotationExp;
	final IExpression locationExp;
	final IExpression emptyExp;
	final IExpression borderExp;
	final IExpression colorExp;
	final IExpression fontExp;
	final IExpression textureExp;
	final IExpression bitmapExp;

	ILocation constantSIze;
	Double constantDepth;
	GamaPair<Double, GamaPoint> constantRotation;
	ILocation constantLocation;
	Boolean constantEmpty;
	GamaColor constantBorder;
	GamaColor constantColor;
	GamaFont constantFont;
	IList constantTextures;
	Boolean constantBitmap;
	Boolean hasBorder;
	Boolean hasColor;

	public DrawingData(final IExpression sizeExp, final IExpression depthExp, final IExpression rotationExp,
		final IExpression locationExp, final IExpression emptyExp, final IExpression borderExp,
		final IExpression colorExp, final IExpression fontExp, final IExpression textureExp,
		final IExpression bitmapExp) {
		this.sizeExp = sizeExp;
		this.depthExp = depthExp;
		this.rotationExp = rotationExp;
		this.locationExp = locationExp;
		this.emptyExp = emptyExp;
		this.borderExp = borderExp;
		this.colorExp = colorExp;
		this.fontExp = fontExp;
		this.textureExp = textureExp;
		this.bitmapExp = bitmapExp;
		initializeConstants();
	}

	private void initializeConstants() {
		/* BITMAP */
		if ( bitmapExp != null && bitmapExp.isConst() ) {
			constantBitmap = Cast.asBool(null, bitmapExp.value(null));
		} else if ( bitmapExp == null ) {
			constantBitmap = true;
		}
		/* SIZE */
		if ( sizeExp != null && sizeExp.isConst() ) {
			constantSIze = Cast.asPoint(null, sizeExp.value(null));
		}
		/* DEPTH */
		if ( depthExp != null && depthExp.isConst() ) {
			constantDepth = Cast.asFloat(null, depthExp.value(null));
		}
		/* ROTATION */
		if ( rotationExp != null && rotationExp.isConst() ) {
			if ( rotationExp.getType().getType() == Types.PAIR ) {
				constantRotation = Cast.asPair(null, rotationExp.value(null), true);
				constantRotation.key = Cast.asFloat(null, constantRotation.key);
			} else {
				constantRotation = new GamaPair(rotationExp.value(null), DEFAULT_AXIS, Types.FLOAT, Types.POINT);
				constantRotation.key = Cast.asFloat(null, constantRotation.key);
			}
		}
		/* LOCATION */
		if ( locationExp != null && locationExp.isConst() ) {
			constantLocation = Cast.asPoint(null, locationExp.value(null));
		}
		/* EMPTY */
		if ( emptyExp != null && emptyExp.isConst() ) {
			constantEmpty = Cast.asBool(null, emptyExp.value(null));
		} else if ( emptyExp == null ) {
			constantEmpty = false;
		}

		/* BORDER */
		if ( borderExp != null && borderExp.isConst() ) {
			if ( borderExp.getType() == Types.BOOL ) {
				hasBorder = Cast.asBool(null, borderExp.value(null));
				if ( hasBorder ) {
					constantBorder = DEFAULT_BORDER_COLOR;
				}
			} else {
				hasBorder = true;
				constantBorder = Cast.asColor(null, borderExp.value(null));
			}
		} else if ( borderExp == null ) {
			hasBorder = true;
			// AD commented in order to allow for the color to be chosen when computing the border color
			// constantBorder = DEFAULT_BORDER_COLOR;
		} else {
			hasBorder = true;
		}

		/* COLOR */
		if ( colorExp != null && colorExp.isConst() ) {
			hasColor = true;
			constantColor = Cast.asColor(null, colorExp.value(null));
		} else if ( colorExp == null ) {
			hasColor = false;
		} else {
			hasColor = true;
		}

		/* FONT */
		if ( fontExp != null && fontExp.isConst() ) {
			constantFont = GamaFontType.staticCast(null, fontExp.value(null), true);
		}

		/* TEXTURES */
		if ( textureExp != null && textureExp.isConst() ) {
			if ( textureExp.getType().getType() == Types.LIST ) {
				constantTextures = Cast.asList(null, textureExp.value(null));
			} else {
				constantTextures = GamaListFactory.createWithoutCasting(Types.NO_TYPE, textureExp.value(null));
			}
		}
	}

	public DrawingAttributes computeAttributes(final IScope scope) {
		ILocation currentSize = null;
		Double currentDepth = null;
		GamaPair<Double, GamaPoint> currentRotation = null;
		ILocation currentLocation = null;
		Boolean currentEmpty;
		GamaColor currentBorder = null;
		GamaColor currentColor;
		GamaFont currentFont;
		IList currentTextures = null;
		Boolean currentBitmap;

		/* BITMAP */
		if ( constantBitmap != null ) {
			currentBitmap = constantBitmap;
		} else {
			currentBitmap = Cast.asBool(scope, bitmapExp.value(scope));
		}
		/* SIZE */
		if ( constantSIze != null ) {
			currentSize = constantSIze;
		} else {
			if ( sizeExp != null ) {
				currentSize = Cast.asPoint(scope, sizeExp.value(scope));
			}
		}
		/* DEPTH */
		if ( constantDepth != null ) {
			currentDepth = constantDepth;
		} else {
			if ( depthExp != null ) {
				currentDepth = Cast.asFloat(scope, depthExp.value(scope));
			}
		}

		/* ROTATION */
		if ( constantRotation != null ) {
			currentRotation = constantRotation;
		} else {
			if ( rotationExp != null ) {
				if ( rotationExp.getType().getType() == Types.PAIR ) {
					currentRotation = Cast.asPair(scope, rotationExp.value(scope), true);
					currentRotation.key = Cast.asFloat(scope, currentRotation.key);
				} else {
					currentRotation = new GamaPair(rotationExp.value(scope), DEFAULT_AXIS, Types.FLOAT, Types.POINT);
					currentRotation.key = Cast.asFloat(scope, currentRotation.key);
				}
			}
		}
		/* LOCATION */
		if ( constantLocation != null ) {
			currentLocation = constantLocation;
		} else {
			if ( locationExp != null ) {
				currentLocation = Cast.asPoint(scope, locationExp.value(scope));
			}
			// else {
			// currentLocation = scope.getAgentScope().getLocation();
			// }
		}
		/* EMPTY */
		if ( constantEmpty != null ) {
			currentEmpty = constantEmpty;
		} else {
			currentEmpty = Cast.asBool(scope, emptyExp.value(scope));
		}

		/* COLOR */
		if ( constantColor != null ) {
			currentColor = constantColor;
		} else {
			if ( colorExp != null ) {
				currentColor = Cast.asColor(scope, colorExp.value(scope));
			} else {
				currentColor = new GamaColor(GamaPreferences.CORE_COLOR.getValue());
			}
		}

		/* BORDER */
		if ( hasBorder || currentEmpty ) {
			if ( constantBorder != null ) {
				currentBorder = constantBorder;
			} else {
				if ( borderExp != null && borderExp.getType() != Types.BOOL ) {
					currentBorder = Cast.asColor(scope, borderExp.value(scope));
				} else {
					currentBorder = currentColor;
				}
			}
		}

		/* FONT */
		if ( constantFont != null ) {
			currentFont = constantFont;
		} else {
			if ( fontExp != null ) {
				currentFont = GamaFontType.staticCast(scope, fontExp.value(scope), true);
			} else {
				currentFont = GamaFontType.DEFAULT_DISPLAY_FONT.getValue();
			}
		}

		/* TEXTURES */
		if ( constantTextures != null ) {
			currentTextures = constantTextures;
		} else {
			if ( textureExp != null ) {
				if ( textureExp.getType().getType() == Types.LIST ) {
					constantTextures = GamaListType.staticCast(scope, textureExp.value(scope), Types.STRING, false);
				} else {
					constantTextures = GamaListFactory.createWithoutCasting(Types.NO_TYPE, textureExp.value(scope));
				}
			}
		}

		return new DrawingAttributes(currentSize, currentDepth, currentRotation, currentLocation, currentEmpty,
			currentBorder, hasBorder, currentColor, currentFont, currentTextures, currentBitmap, hasColor,
			scope.getAgentScope());
	}

	public static class DrawingAttributes {

		public GamaPoint size;
		public Double depth = 0.0;
		public final GamaPair<Double, GamaPoint> rotation;
		public GamaPoint location;
		public Boolean empty;
		public GamaColor border;
		public final Boolean hasBorder;
		public final Boolean hasColor;
		public GamaColor color;
		public final GamaFont font;
		public final List textures;
		public Boolean bitmap = true;
		public IAgent agent;
		public IShape.Type type;
		public String speciesName = null;
		public Boolean isDynamic = false;

		public DrawingAttributes(final ILocation size, final Double depth, final GamaPair<Double, GamaPoint> rotation,
			final ILocation location, final Boolean empty, final GamaColor border, final Boolean hasBorder,
			final GamaColor color, final GamaFont font, final List textures, final Boolean bitmap,
			final Boolean hasColor, final IAgent agent) {
			this.size = size == null ? null : new GamaPoint(size);
			this.depth = depth == null ? 0.0 : depth;
			this.rotation = rotation;
			// To make sure no side effect can happen
			this.location = location == null ? null : new GamaPoint(location);
			this.empty = empty;
			this.border = border == null && empty ? color : border;
			this.hasBorder = hasBorder || empty;
			this.color = color;
			this.font = font;
			this.textures = textures == null ? null : new ArrayList(textures);
			this.bitmap = bitmap;
			this.hasColor = hasColor;
			this.agent = agent;
		}

		public DrawingAttributes(final GamaPoint location) {
			this(location, null, null);
		}

		public DrawingAttributes(final GamaPoint location, final GamaColor color, final GamaColor border) {
			this.location = location;
			this.size = null;
			this.rotation = null;
			this.empty = color == null;
			this.border = border;
			this.hasBorder = border != null;
			this.color = color;
			this.font = null;
			this.textures = null;
			this.hasColor = color != null;
			this.agent = null;
		}

		public DrawingAttributes copy() {
			return new DrawingAttributes(size, depth, rotation, location, empty, border, hasBorder, color, font,
				textures, bitmap, hasColor, agent);
		}

		public void setShapeType(final IShape.Type type) {
			this.type = type;
		}

		public void setSpeciesName(final String name) {
			speciesName = name;
		}

		public void setDynamic(final Boolean b) {
			isDynamic = b;
		}

		/**
		 * @param attribute
		 */
		public void setDepthIfAbsent(final Double d) {
			if ( depth != 0.0 ) { return; }
			depth = d == null ? 0.0 : d;
		}

		/**
		 * @param gamaPoint
		 */
		public void setLocationIfAbsent(final GamaPoint point) {
			if ( location == null ) {
				location = point;
			}
		}

	}

}
