/*********************************************************************************************
 *
 *
 * 'TextLayerStatement.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.outputs.layers;

import static msi.gama.common.interfaces.IKeyword.STYLE;
import java.awt.Color;
import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.*;
import msi.gaml.operators.Cast;
import msi.gaml.types.*;

@symbol(name = IKeyword.TEXT, kind = ISymbolKind.LAYER, with_sequence = false)
@inside(symbols = IKeyword.DISPLAY)
@facets(
	value = { @facet(name = IKeyword.VALUE, type = IType.STRING, optional = true, internal = true),
		@facet(name = IKeyword.POSITION,
			type = IType.POINT,
			optional = true,
			doc = @doc("position of the upper-left corner of the layer. Note that if coordinates are in [0,1[, the position is relative to the size of the environment (e.g. {0.5,0.5} refers to the middle of the display) whereas it is absolute when coordinates are greter than 1. The position can only be a 3D point {0.5, 0.5, 0.5}, the last coordinate specifying the elevation of the layer.") ),
		@facet(name = IKeyword.SIZE,
			type = { IType.INT, IType.FLOAT, IType.POINT },
			optional = true,
			doc = @doc("extent of the layer in the screen from its position. Coordinates in [0,1[ are treated as percentages of the total surface, while coordinates > 1 are treated as absolute sizes in model units (i.e. considering the model occupies the entire view). Like in 'position', an elevation can be provided with the z coordinate, allowing to scale the layer in the 3 directions ") ),
		@facet(name = IKeyword.TRANSPARENCY,
			type = IType.FLOAT,
			optional = true,
			doc = @doc("the transparency rate of the layer (between 0 and 1, 1 means no transparency)") ),
		// 10/04/13 Name is not a constant ID anymore but can represent the text to display.

		@facet(name = STYLE,
			type = IType.ID,
			values = { "plain", "bold", "italic" },
			optional = true,
			doc = @doc(
				deprecated = "Use the 'font' operator together with the #bold, #italic and #plain constant styles",
				value = "the style (bold, italic...) udes to display the text") ),
		@facet(name = IKeyword.NAME, type = IType.STRING, optional = false, doc = @doc("the string to display") ),
		@facet(name = IKeyword.FONT,
			type = { IType.FONT, IType.STRING },
			optional = true,
			doc = @doc("the font used for the text") ),
		@facet(name = IKeyword.COLOR,
			type = IType.COLOR,
			optional = true,
			doc = @doc("the color used to display the text") ),
		@facet(name = IKeyword.REFRESH,
			type = IType.BOOL,
			optional = true,
			doc = @doc("(openGL only) specify whether the display of the text is refreshed. (true by default, useful in case of text that is not been modified over simulation)") ) },
	omissible = IKeyword.NAME)
@doc(deprecated = "Use a 'graphics' type of layer instead, with a `draw text ` statement inside",
	value = "`" + IKeyword.TEXT +
		"` allows the modeler to display a string (that can change at each step) in a given position of the display.",
	usages = {
		@usage(value = "The general syntax is:",
			examples = { @example(value = "display my_display {", isExecutable = false),
				@example(value = "   text expression [additional options];", isExecutable = false),
				@example(value = "}", isExecutable = false) }),
		@usage(value = "For instance, in a segregation model, `agents` will only display unhappy agents:",
			examples = { @example(value = "display Segregation {", isExecutable = false),
				@example(
					value = "   text 'Carrying ants : ' + (int(ant as list count(each.has_food)) + int(ant as list count(each.state = 'followingRoad'))) position: {0.5,0.03} color: rgb('black') size: {1,0.02};",
					isExecutable = false),
				@example(value = "}", isExecutable = false) }) },
	see = { IKeyword.DISPLAY, IKeyword.AGENTS, IKeyword.CHART, IKeyword.EVENT, "graphics", IKeyword.GRID_POPULATION,
		IKeyword.IMAGE, IKeyword.OVERLAY, IKeyword.QUADTREE, IKeyword.POPULATION })
@Deprecated
public class TextLayerStatement extends AbstractLayerStatement {

	public static final Map<String, Integer> CONSTANTS = new HashMap();

	static {
		CONSTANTS.put("plain", 0);
		CONSTANTS.put("bold", 1);
		CONSTANTS.put("italic", 2);
	}

	private final IExpression color;
	private final IExpression font;
	private IExpression text;
	private final IExpression style;
	String constantText = null;
	Color constantColor = null;
	GamaFont constantFont = null;
	// private Integer constantStyle = null;
	private String currentText = null;
	private Color currentColor = null;
	private GamaFont currentFont = null;

	// private Integer currentStyle = null;

	public TextLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		IExpression c = getFacet(IKeyword.COLOR);
		color = c == null ? new ConstantExpression(Cast.asColor(null, "black")) : c;
		c = getFacet(IKeyword.FONT);
		font = c == null ? new ConstantExpression(Types.FONT.getDefault(), Types.FONT) : c;
		// If 'value:' is not defined, we take the name as the text to display.
		text = getFacet(IKeyword.VALUE, IKeyword.NAME);
		style = getFacet(IKeyword.STYLE);
		if ( getFacet(IKeyword.SIZE) == null ) {
			IExpression f = font;
			IExpressionFactory ef = GAML.getExpressionFactory();
			IExpression s = ef.createOperator("as", desc, desc.getUnderlyingElement(IKeyword.FONT), f,
				ef.createTypeExpression(Types.INT));
			IExpression exp = ef.createOperator("*", desc, null, s, new PixelUnitExpression("px", null));
			getBox().setSize(null, exp);

		}
		// style = c == null ? new ConstantExpression("plain") : c;
	}

	public String getText() {
		return currentText;
	}

	public Color getColor() {
		return currentColor;
	}

	@Override
	public short getType() {
		return ILayerStatement.TEXT;
	}

	public GamaFont getFont() {
		return currentFont;
	}

	@Override
	public boolean _step(final IScope scope) {
		currentText = constantText == null ? Cast.asString(scope, text.value(scope)) : constantText;
		currentColor = constantColor == null ? Cast.asColor(scope, color.value(scope)) : constantColor;
		currentFont = constantFont == null ? GamaFontType.staticCast(scope, font.value(scope), false) : constantFont;
		if ( style != null ) {
			int currentStyle = CONSTANTS.get(Cast.asString(scope, style.value(scope)));
			if ( currentFont.getStyle() != currentStyle ) {
				currentFont = new GamaFont(currentFont.deriveFont(currentStyle));
			}
		}
		return true;
	}

	@Override
	public boolean _init(final IScope scope) {
		if ( text.isConst() && constantText == null ) {
			constantText = Cast.asString(scope, text.value(scope));
			currentText = constantText;
		}
		if ( font.isConst() && constantFont == null ) {
			constantFont = GamaFontType.staticCast(scope, font.value(scope), false);
			currentFont = constantFont;

		}
		if ( color.isConst() && constantColor == null ) {
			constantColor = Cast.asColor(scope, color.value(scope));
			currentColor = constantColor;
		}

		return true;
	}

	public void setTextExpr(final IExpression text) {
		this.text = text;
		constantText = null;
	}

	public void setColor(final Color currentColor) {
		constantColor = currentColor;
	}

	public void setFont(final GamaFont string) {
		constantFont = string;
	}

	public IExpression getTextExpr() {
		return text;
	}

	public Integer getStyle() {
		return currentFont.getStyle();
	}
}
