/*********************************************************************************************
 *
 *
 * 'OverlayStatement.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.outputs.layers;

import java.awt.Color;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.outputs.layers.OverlayStatement.OverlayInfo;
import msi.gama.precompiler.*;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

@symbol(name = IKeyword.OVERLAY, kind = ISymbolKind.LAYER, with_sequence = true, unique_in_context = true, concept = { IConcept.DISPLAY })
@inside(symbols = IKeyword.DISPLAY)
@facets(value = {
	@facet(name = IKeyword.ROUNDED,
		type = IType.BOOL,
		optional = true,
		doc = @doc("Whether or not the rectangular shape of the overlay should be rounded. True by default")),
	@facet(name = IKeyword.BORDER,
	type = IType.COLOR,
	optional = true,
	doc = @doc("Color to apply to the border of the rectangular shape of the overlay. Nil by default")),
	@facet(name = IKeyword.POSITION,
	type = IType.POINT,
	optional = true,
	doc = @doc("position of the upper-left corner of the overlay. Note that if coordinates are in [0,1[, the position is relative to the size of the view (e.g. {0.5,0.5} refers to the middle of the view) whereas it is absolute when coordinates are greater than 1. When the position is a 3D point {0.5, 0.5, 0.5}, the last coordinate specifies the elevation of the layer.")),
	@facet(name = IKeyword.SIZE,
	type = IType.POINT,
	optional = true,
	doc = @doc("extent of the layer in the view from its position. Coordinates in [0,1[ are treated as percentages of the total surface of the view, while coordinates > 1 are treated as absolute sizes in model units (i.e. considering the model occupies the entire view). Unlike  'position', no elevation can be provided with the z coordinate ")),
	@facet(name = IKeyword.TRANSPARENCY,
	type = IType.FLOAT,
	optional = true,
	doc = @doc("the transparency rate of the overlay (between 0 and 1, 1 means no transparency) when it is displayed inside the view. The bottom overlay will remain at 0.75")),
	@facet(name = IKeyword.LEFT,
	type = IType.NONE,
	optional = true,
	doc = @doc("an expression that will be evaluated and displayed in the left section of the bottom overlay")),
	@facet(name = IKeyword.RIGHT,
	type = IType.NONE,
	optional = true,
	doc = @doc("an expression that will be evaluated and displayed in the right section of the bottom overlay")),
	@facet(name = IKeyword.CENTER,
	type = IType.NONE,
	optional = true,
	doc = @doc("an expression that will be evaluated and displayed in the center section of the bottom overlay")),
	@facet(name = IKeyword.BACKGROUND,
	type = IType.COLOR,
	optional = true,
	doc = @doc("the background color of the overlay displayed inside the view (the bottom overlay remains black)")),
	@facet(name = IKeyword.COLOR,
	type = { IType.LIST, IType.COLOR },
	of = IType.COLOR,
	optional = true,
	doc = @doc("the color(s) used to display the expressions given in the 'left', 'center' and 'right' facets")) })
// ,omissible = IKeyword.LEFT)
@doc(
	value = "`" + IKeyword.OVERLAY +
	"` allows the modeler to display a line to the already existing bottom overlay, where the results of 'left', 'center' and 'right' facets, when they are defined, are displayed with the corresponding color if defined.",
	usages = { @usage(value = "To display information in the bottom overlay, the syntax is:",
	examples = { @example(
		value = "overlay \"Cycle: \" + (cycle) center: \"Duration: \" + total_duration + \"ms\" right: \"Model time: \" + as_date(time,\"\") color: [#yellow, #orange, #yellow];",
		isExecutable = false) }) },
	see = { IKeyword.DISPLAY, IKeyword.AGENTS, IKeyword.CHART, IKeyword.EVENT, "graphics", IKeyword.GRID_POPULATION,
		IKeyword.IMAGE, IKeyword.POPULATION })
public class OverlayStatement extends GraphicLayerStatement implements IOverlayProvider<OverlayInfo> {

	final IExpression left, right, center, round;
	final IExpression color, background, border;
	String leftValue, rightValue, centerValue;
	Color bgColor, borderColor;
	boolean rounded;
	List<int[]> constantColors;
	IUpdaterTarget<OverlayInfo> overlay;

	public static class OverlayInfo implements IUpdaterMessage {

		public String[] infos;

		public java.util.List<int[]> colors;

		OverlayInfo(final String[] infos, final java.util.List<int[]> colors) {
			this.infos = infos;
			this.colors = colors;
		}

		@Override
		public boolean isEmpty() {
			return infos == null;
		}
	}

	public OverlayStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		left = getFacet(IKeyword.LEFT);
		right = getFacet(IKeyword.RIGHT);
		center = getFacet(IKeyword.CENTER);
		color = getFacet(IKeyword.COLOR);
		background = getFacet(IKeyword.BACKGROUND);
		border = getFacet(IKeyword.BORDER);
		round = getFacet(IKeyword.ROUNDED);

		if ( color != null && color.isConst() ) {
			constantColors = computeColors(null);
		}
	}

	private List<int[]> computeColors(final IScope scope) {
		if ( constantColors != null ) { return constantColors; }
		if ( color == null ) { return null; }
		if ( color.getType().id() == IType.LIST ) {
			IList list = Cast.asList(scope, color.value(scope));
			List<int[]> result = new ArrayList();
			int i = 0;
			for ( Object o : list ) {
				int[] rgb = computeColor(scope, o);
				result.add(rgb);
				if ( ++i > 2 ) {
					break;
				}
			}
			return result;
		} else {
			int[] rgb = computeColor(scope, color.value(scope));
			return Arrays.asList(rgb, rgb, rgb);
		}
	}

	private int[] computeColor(final IScope scope, final Object color) {
		GamaColor c = Cast.asColor(scope, color);
		int[] rgb = new int[] { c.red(), c.green(), c.blue() };
		return rgb;
	}

	@Override
	public short getType() {
		return ILayerStatement.OVERLAY;
	}

	@Override
	protected boolean _step(final IScope scope) {
		rounded = round == null ? true : Cast.asBool(scope, round.value(scope));
		borderColor = border == null ? null : Cast.asColor(scope, border.value(scope));
		bgColor = background == null ? Color.black : Cast.asColor(scope, background.value(scope));
		if ( overlay == null ) { return true; }
		leftValue = left == null ? null : Cast.asString(scope, left.value(scope));
		rightValue = right == null ? null : Cast.asString(scope, right.value(scope));
		centerValue = center == null ? null : Cast.asString(scope, center.value(scope));
		overlay.updateWith(new OverlayInfo(getValues(), computeColors(scope)));
		return true;
	}

	private String[] getValues() {
		return new String[] { leftValue, centerValue, rightValue };
	}

	@Override
	public void setTarget(final IUpdaterTarget overlay, final IDisplaySurface surface) {
		this.overlay = overlay;
		_step(surface.getDisplayScope());
	}

	/**
	 * @return the background color, preaffected by the transparency
	 */
	public Color getBackgroundColor() {
		return new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(),
			(int) (getBox().getTransparency() * 255));
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public boolean isRounded() {
		return rounded;
	}

	/**
	 * @return
	 */
	public boolean hasInfo() {
		return left != null || right != null || center != null;
	}

}
