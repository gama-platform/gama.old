/**
 * Created by drogoul, 28 janv. 2016
 *
 */
package msi.gaml.statements.draw;

import java.awt.Color;

import msi.gama.common.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaFont;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMaterial;
import msi.gama.util.GamaPair;
import msi.gama.util.IList;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.GamaFontType;
import msi.gaml.types.GamaListType;
import msi.gaml.types.GamaMaterialType;
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
	final IExpression materialExp;
	final IExpression perspectiveExp;

	ILocation constantSIze;
	Double constantDepth;
	GamaPair<Double, GamaPoint> constantRotation;
	ILocation constantLocation;
	Boolean constantEmpty;
	GamaColor constantBorder;
	// GamaColor constantColor;
	GamaFont constantFont;
	IList constantTextures;
	GamaMaterial constantMaterial;
	Boolean constantperspective;
	Boolean hasBorder;
	Boolean hasColor;

	ILocation currentSize = null;
	Double currentDepth = null;
	GamaPair<Double, GamaPoint> currentRotation = null;
	ILocation currentLocation = null;
	Boolean currentEmpty;
	GamaColor currentBorder = null;
	GamaColor currentColor;
	IList<GamaColor> currentColors;
	GamaFont currentFont;
	IList currentTextures = null;
	GamaMaterial currentMaterial = null;
	Boolean currentperspective;

	public DrawingData(final IExpression sizeExp, final IExpression depthExp, final IExpression rotationExp,
			final IExpression locationExp, final IExpression emptyExp, final IExpression borderExp,
			final IExpression colorExp, final IExpression fontExp, final IExpression textureExp,
			final IExpression materialExp, final IExpression perspectiveExp) {
		this.sizeExp = sizeExp;
		this.depthExp = depthExp;
		this.rotationExp = rotationExp;
		this.locationExp = locationExp;
		this.emptyExp = emptyExp;
		this.borderExp = borderExp;
		this.colorExp = colorExp;
		this.fontExp = fontExp;
		this.textureExp = textureExp;
		this.materialExp = materialExp;
		this.perspectiveExp = perspectiveExp;
		initializeConstants();
	}

	private void initializeConstants() {
		/* perspective */
		if (perspectiveExp != null && perspectiveExp.isConst()) {
			constantperspective = Cast.asBool(null, perspectiveExp.value(null));
		} else if (perspectiveExp == null) {
			constantperspective = true;
		}
		/* SIZE */
		if (sizeExp != null && sizeExp.isConst()) {
			constantSIze = Cast.asPoint(null, sizeExp.value(null));
		}
		/* DEPTH */
		if (depthExp != null && depthExp.isConst()) {
			constantDepth = Cast.asFloat(null, depthExp.value(null));
		}
		/* ROTATION */
		if (rotationExp != null && rotationExp.isConst()) {
			if (rotationExp.getType().getType() == Types.PAIR) {
				constantRotation = Cast.asPair(null, rotationExp.value(null), true);
				constantRotation.key = Cast.asFloat(null, constantRotation.key);
			} else {
				constantRotation = new GamaPair(rotationExp.value(null), DEFAULT_AXIS, Types.FLOAT, Types.POINT);
				constantRotation.key = Cast.asFloat(null, constantRotation.key);
			}
		}
		/* LOCATION */
		if (locationExp != null && locationExp.isConst()) {
			constantLocation = Cast.asPoint(null, locationExp.value(null));
		}
		/* EMPTY */
		if (emptyExp != null && emptyExp.isConst()) {
			constantEmpty = Cast.asBool(null, emptyExp.value(null));
		} else if (emptyExp == null) {
			constantEmpty = false;
		}

		/* BORDER */
		if (borderExp != null && borderExp.isConst()) {
			if (borderExp.getType() == Types.BOOL) {
				hasBorder = Cast.asBool(null, borderExp.value(null));
				if (hasBorder) {
					constantBorder = DEFAULT_BORDER_COLOR;
				}
			} else {
				hasBorder = true;
				constantBorder = Cast.asColor(null, borderExp.value(null));
			}
		} else if (borderExp == null) {
			hasBorder = false;
			// AD commented in order to allow for the color to be chosen when
			// computing the border color
			// constantBorder = DEFAULT_BORDER_COLOR;
		} else {
			hasBorder = true;
		}

		/* COLOR */
		hasColor = colorExp != null;
		// if (colorExp != null && colorExp.isConst()) {
		// hasColor = true;
		// // constantColor = Cast.asColor(null, colorExp.value(null));
		// } else if (colorExp == null) {
		// hasColor = false;
		// } else {
		// hasColor = true;
		// }

		/* FONT */
		if (fontExp != null && fontExp.isConst()) {
			constantFont = GamaFontType.staticCast(null, fontExp.value(null), true);
		}

		/* TEXTURES */
		if (textureExp != null && textureExp.isConst()) {
			if (textureExp.getType().getType() == Types.LIST) {
				constantTextures = Cast.asList(null, textureExp.value(null));
			} else {
				constantTextures = GamaListFactory.createWithoutCasting(Types.NO_TYPE, textureExp.value(null));
			}
		}

		/* MATERIAL */
		if (materialExp != null && materialExp.isConst()) {
			constantMaterial = GamaMaterialType.staticCast(null, materialExp.value(null), true);
		}
	}

	public void computeAttributes(final IScope scope) {

		/* perspective */
		if (constantperspective != null) {
			currentperspective = constantperspective;
		} else {
			currentperspective = Cast.asBool(scope, perspectiveExp.value(scope));
		}
		/* SIZE */
		if (constantSIze != null) {
			currentSize = constantSIze;
		} else {
			if (sizeExp != null) {
				if (sizeExp.getType().isNumber()) {
					final double val = Cast.asFloat(scope, sizeExp.value(scope));
					// We do not consider the z ordinate -- see Issue #1539
					currentSize = new GamaPoint(val, val, 0);
				} else {
					currentSize = Cast.asPoint(scope, sizeExp.value(scope));
				}
			}
		}
		/* DEPTH */
		if (constantDepth != null) {
			currentDepth = constantDepth;
		} else {
			if (depthExp != null) {
				currentDepth = Cast.asFloat(scope, depthExp.value(scope));
			}
		}

		/* ROTATION */
		if (constantRotation != null) {
			currentRotation = constantRotation;
		} else {
			if (rotationExp != null) {
				if (rotationExp.getType().getType() == Types.PAIR) {
					currentRotation = Cast.asPair(scope, rotationExp.value(scope), true);
					currentRotation.key = Cast.asFloat(scope, currentRotation.key);
				} else {
					currentRotation =
							new GamaPair(scope, rotationExp.value(scope), DEFAULT_AXIS, Types.FLOAT, Types.POINT);
					currentRotation.key = Cast.asFloat(scope, currentRotation.key);
				}
			}
		}
		/* LOCATION */
		if (constantLocation != null) {
			currentLocation = constantLocation;
		} else {
			if (locationExp != null) {
				currentLocation = Cast.asPoint(scope, locationExp.value(scope));
			}
			// else {
			// currentLocation = scope.getAgentScope().getLocation();
			// }
		}
		/* EMPTY */
		if (constantEmpty != null) {
			currentEmpty = constantEmpty;
		} else {
			currentEmpty = Cast.asBool(scope, emptyExp.value(scope));
		}

		/* COLOR */
		if (colorExp != null) {
			switch (colorExp.getType().id()) {
				case IType.COLOR:
					currentColor = (GamaColor) colorExp.value(scope);
					currentColors = GamaListFactory.createWithoutCasting(Types.COLOR, currentColor);
					break;
				case IType.LIST:
					currentColors = (IList) colorExp.value(scope);
					if (!currentColors.isEmpty()) {
						currentColor = currentColors.get(0);
					}
					break;
				default:
					currentColor = new GamaColor(GamaPreferences.CORE_COLOR.getValue());
			}

		} else {
			currentColor = new GamaColor(GamaPreferences.CORE_COLOR.getValue());
		}

		// }

		/* BORDER */
		if (hasBorder || currentEmpty) {
			if (constantBorder != null) {
				currentBorder = constantBorder;
			} else {
				if (borderExp != null && borderExp.getType() != Types.BOOL) {
					currentBorder = Cast.asColor(scope, borderExp.value(scope));
				} else {
					currentBorder = currentColor;
				}
			}
		}

		/* FONT */
		if (constantFont != null) {
			currentFont = constantFont;
		} else {
			if (fontExp != null) {
				currentFont = GamaFontType.staticCast(scope, fontExp.value(scope), true);
			} else {
				currentFont = GamaFontType.DEFAULT_DISPLAY_FONT.getValue();
			}
		}

		/* TEXTURES */
		if (constantTextures != null) {
			currentTextures = constantTextures;
		} else {
			if (textureExp != null) {
				if (textureExp.getType().getType() == Types.LIST) {
					currentTextures = GamaListType.staticCast(scope, textureExp.value(scope), Types.STRING, false);
				} else {
					currentTextures = GamaListFactory.createWithoutCasting(Types.NO_TYPE, textureExp.value(scope));
				}
			}
		}

		/* MATERIAL */
		if (constantMaterial != null) {
			currentMaterial = constantMaterial;
		} else {
			if (materialExp != null) {
				currentMaterial = GamaMaterialType.staticCast(scope, materialExp.value(scope), true);
			} else {
				currentMaterial = null;
			}
		}

	}

}
