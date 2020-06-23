/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.OverlayStatement.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.IOverlayProvider;
import msi.gama.common.interfaces.IUpdaterMessage;
import msi.gama.common.interfaces.IUpdaterTarget;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.layers.OverlayStatement.OverlayInfo;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.IList;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

@symbol (
		name = IKeyword.OVERLAY,
		kind = ISymbolKind.LAYER,
		with_sequence = true,
		unique_in_context = true,
		concept = { IConcept.DISPLAY })
@inside (
		symbols = IKeyword.DISPLAY)
@facets (
		value = { @facet (
				name = IKeyword.ROUNDED,
				type = IType.BOOL,
				optional = true,
				doc = @doc ("Whether or not the rectangular shape of the overlay should be rounded. True by default")),
				@facet (
						name = IKeyword.BORDER,
						type = IType.COLOR,
						optional = true,
						doc = @doc ("Color to apply to the border of the rectangular shape of the overlay. Nil by default")),
				@facet (
						name = IKeyword.POSITION,
						type = IType.POINT,
						optional = true,
						doc = @doc ("position of the upper-left corner of the layer. Note that if coordinates are in [0,1[, the position is relative to the size of the environment (e.g. {0.5,0.5} refers to the middle of the display) whereas it is absolute when coordinates are greater than 1 for x and y. The z-ordinate can only be defined between 0 and 1. The position can only be a 3D point {0.5, 0.5, 0.5}, the last coordinate specifying the elevation of the layer.")),
				@facet (
						name = IKeyword.SIZE,
						type = IType.POINT,
						optional = true,
						doc = @doc ("extent of the layer in the view from its position. Coordinates in [0,1[ are treated as percentages of the total surface of the view, while coordinates > 1 are treated as absolute sizes in model units (i.e. considering the model occupies the entire view). Unlike  'position', no elevation can be provided with the z coordinate ")),
				@facet (
						name = IKeyword.TRANSPARENCY,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("the transparency rate of the overlay (between 0 -- opaque and 1 -- fully transparent) when it is displayed inside the view. The bottom overlay will remain at 0.75")),
				@facet (
						name = IKeyword.LEFT,
						type = IType.NONE,
						optional = true,
						doc = @doc ("an expression that will be evaluated and displayed in the left section of the bottom overlay")),
				@facet (
						name = IKeyword.RIGHT,
						type = IType.NONE,
						optional = true,
						doc = @doc ("an expression that will be evaluated and displayed in the right section of the bottom overlay")),
				@facet (
						name = IKeyword.CENTER,
						type = IType.NONE,
						optional = true,
						doc = @doc ("an expression that will be evaluated and displayed in the center section of the bottom overlay")),
				@facet (
						name = IKeyword.BACKGROUND,
						type = IType.COLOR,
						optional = true,
						doc = @doc ("the background color of the overlay displayed inside the view (the bottom overlay remains black)")),
				@facet (
						name = IKeyword.COLOR,
						type = { IType.LIST, IType.COLOR },
						of = IType.COLOR,
						optional = true,
						doc = @doc ("the color(s) used to display the expressions given in the 'left', 'center' and 'right' facets")) })
// ,omissible = IKeyword.LEFT)
@doc (
		value = "`" + IKeyword.OVERLAY
				+ "` allows the modeler to display a line to the already existing bottom overlay, where the results of 'left', 'center' and 'right' facets, when they are defined, are displayed with the corresponding color if defined.",
		usages = { @usage (
				value = "To display information in the bottom overlay, the syntax is:",
				examples = { @example (
						value = "overlay \"Cycle: \" + (cycle) center: \"Duration: \" + total_duration + \"ms\" right: \"Model time: \" + as_date(time,\"\") color: [#yellow, #orange, #yellow];",
						isExecutable = false) }) },
		see = { IKeyword.DISPLAY, IKeyword.AGENTS, IKeyword.CHART, IKeyword.EVENT, "graphics", IKeyword.GRID_POPULATION,
				IKeyword.IMAGE, IKeyword.POPULATION })
public class OverlayStatement extends GraphicLayerStatement implements IOverlayProvider<OverlayInfo> {

	final IExpression left, right, center, color;
	String leftValue, rightValue, centerValue;
	List<int[]> constantColors;
	IUpdaterTarget<OverlayInfo> overlay;

	public static class OverlayInfo implements IUpdaterMessage {

		public String[] infos;

		public java.util.List<int[]> colors;

		OverlayInfo(final String[] infos, final java.util.List<int[]> colors) {
			this.infos = infos;
			this.colors = colors;
		}

	}

	public OverlayStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		left = getFacet(IKeyword.LEFT);
		right = getFacet(IKeyword.RIGHT);
		center = getFacet(IKeyword.CENTER);
		color = getFacet(IKeyword.COLOR);

		if (color != null && color.isConst()) {
			constantColors = computeColors(null);
		}
	}

	private List<int[]> computeColors(final IScope scope) {
		if (constantColors != null) { return constantColors; }
		if (color == null) { return null; }
		if (color.getGamlType().id() == IType.LIST) {
			final IList<?> list = Cast.asList(scope, color.value(scope));
			final List<int[]> result = new ArrayList<>();
			int i = 0;
			for (final Object o : list) {
				final int[] rgb = computeColor(scope, o);
				result.add(rgb);
				if (++i > 2) {
					break;
				}
			}
			return result;
		} else {
			final int[] rgb = computeColor(scope, color.value(scope));
			return Arrays.asList(rgb, rgb, rgb);
		}
	}

	private static int[] computeColor(final IScope scope, final Object color) {
		final GamaColor c = Cast.asColor(scope, color);
		final int[] rgb = new int[] { c.red(), c.green(), c.blue() };
		return rgb;
	}

	@Override
	public LayerType getType(final LayeredDisplayOutput output) {
		return LayerType.OVERLAY;
	}

	@Override
	protected boolean _step(final IScope scope) {
		if (overlay == null) { return true; }
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
	public void setTarget(final IUpdaterTarget<OverlayInfo> overlay, final IDisplaySurface surface) {
		this.overlay = overlay;
		_step(surface.getScope());
	}

	@Override
	public boolean isToCreate() {
		return !aspect.isEmpty();
	}

	/**
	 * @return
	 */
	public boolean hasInfo() {
		return left != null || right != null || center != null;
	}

}
