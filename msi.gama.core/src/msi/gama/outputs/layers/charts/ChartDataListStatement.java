/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.charts.ChartDataListStatement.java, in plugin msi.gama.core, is part of the source code of
 * the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers.charts;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;

@symbol (
		name = "datalist",
		kind = ISymbolKind.SINGLE_STATEMENT,
		with_sequence = false,
		concept = { IConcept.CHART },
		doc = @doc ("add a list of series to a chart. The number of series can be dynamic (the size of the list changes each step). See Ant Foraging (Charts) model in ChartTest for examples."))
@doc ("add a list of series to a chart. The number of series can be dynamic (the size of the list changes each step). See Ant Foraging (Charts) model in ChartTest for examples.")
@inside (
		symbols = IKeyword.CHART,
		kinds = ISymbolKind.SEQUENCE_STATEMENT)
@facets (
		value = { @facet (
				name = IKeyword.VALUE,
				type = IType.LIST,
				optional = false,
				doc = @doc ("the values to display. Has to be a matrix, a list or a List of List. Each element can be a number (series/histogram) or a list with two values (XY chart)")),
				@facet (
						name = ChartDataStatement.YERR_VALUES,
						type = IType.LIST,
						optional = true,
						doc = @doc ("the Y Error bar values to display. Has to be a List. Each element can be a number or a list with two values (low and high value)")),
				@facet (
						name = ChartDataStatement.XERR_VALUES,
						type = IType.LIST,
						optional = true,
						doc = @doc ("the X Error bar values to display. Has to be a List. Each element can be a number or a list with two values (low and high value)")),
				@facet (
						name = ChartDataStatement.USE_SECOND_Y_AXIS,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Use second y axis for this serie")),
				@facet (
						name = ChartDataStatement.YMINMAX_VALUES,
						type = IType.LIST,
						optional = true,
						doc = @doc ("the Y MinMax bar values to display (BW charts). Has to be a List. Each element can be a number or a list with two values (low and high value)")),
				@facet (
						name = ChartDataStatement.MARKERSIZE,
						type = IType.LIST,
						optional = true,
						doc = @doc ("the marker sizes to display. Can be a list of numbers (same size for each marker of the series) or a list of list (different sizes by point)")),
				@facet (
						name = IKeyword.LEGEND,
						type = IType.LIST,
						optional = true,
						doc = @doc ("the name of the series: a list of strings (can be a variable with dynamic names)")),
				@facet (
						name = ChartDataStatement.MARKER,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("marker visible or not")),
				@facet (
						name = ChartDataStatement.MARKERSHAPE,
						type = IType.ID,
						values = { ChartDataStatement.MARKER_EMPTY, ChartDataStatement.MARKER_SQUARE,
								ChartDataStatement.MARKER_CIRCLE, ChartDataStatement.MARKER_UP_TRIANGLE,
								ChartDataStatement.MARKER_DIAMOND, ChartDataStatement.MARKER_HOR_RECTANGLE,
								ChartDataStatement.MARKER_DOWN_TRIANGLE, ChartDataStatement.MARKER_HOR_ELLIPSE,
								ChartDataStatement.MARKER_RIGHT_TRIANGLE, ChartDataStatement.MARKER_VERT_RECTANGLE,
								ChartDataStatement.MARKER_LEFT_TRIANGLE },
						optional = true,
						doc = @doc ("Shape of the marker. Same one for all series.")),
				@facet (
						name = ChartDataStatement.CUMUL_VALUES,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Force to replace values at each step (false) or accumulate with previous steps (true)")),
				@facet (
						name = ChartDataStatement.LINE_VISIBLE,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Line visible or not (same for all series)")),
				@facet (
						name = ChartDataStatement.FILL,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Marker filled (true) or not (false), same for all series.")),
				@facet (
						name = IKeyword.COLOR,
						type = IType.LIST,
						optional = true,
						doc = @doc ("list of colors, for heatmaps can be a list of [minColor,maxColor] or [minColor,medColor,maxColor]")),
				@facet (
						name = ChartDataStatement.THICKNESS,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("The thickness of the lines to draw")),
				@facet (
						name = IKeyword.STYLE,
						type = IType.ID,
						values = { IKeyword.LINE, IKeyword.WHISKER, IKeyword.AREA, IKeyword.BAR, IKeyword.DOT,
								IKeyword.STEP, IKeyword.SPLINE, IKeyword.STACK, IKeyword.THREE_D, IKeyword.RING,
								IKeyword.EXPLODED },
						optional = true,
						doc = @doc ("Style for the serie (if not the default one sepecified on chart statement)")) },
		omissible = IKeyword.LEGEND)
public class ChartDataListStatement extends AbstractStatement {

	public ChartDataListStatement(final IDescription desc) {
		super(desc);
	}

	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 */

	public ChartDataSourceList createDataSource(final IScope scope, final ChartDataSet graphdataset)
			throws GamaRuntimeException {

		final ChartDataSourceList data = new ChartDataSourceList();

		// IExpression string1 = getFacet(IKeyword.TYPE);

		data.setDataset(scope, graphdataset);

		String stval = getLiteral(IKeyword.STYLE);
		if (stval != null) {
			data.setStyle(scope, stval);
		}

		IExpression expval = getFacet(IKeyword.LEGEND).resolveAgainst(scope);
		data.setNameExp(scope, expval);

		expval = getFacet(IKeyword.VALUE).resolveAgainst(scope);
		data.setValueExp(scope, expval);

		expval = getFacet(ChartDataStatement.YERR_VALUES);
		if (expval != null) {
			expval = expval.resolveAgainst(scope);
			data.setYErrValueExp(scope, expval);

		}

		expval = getFacet(ChartDataStatement.XERR_VALUES);
		if (expval != null) {
			expval = expval.resolveAgainst(scope);
			data.setXErrValueExp(scope, expval);

		}

		expval = getFacet(ChartDataStatement.YMINMAX_VALUES);
		if (expval != null) {
			expval = expval.resolveAgainst(scope);
			data.setYMinMaxValueExp(scope, expval);

		}

		expval = getFacet(IKeyword.COLOR);
		if (expval != null) {
			expval = expval.resolveAgainst(scope);
			data.setColorExp(scope, expval);

		}
		expval = getFacet(ChartDataStatement.THICKNESS);
		if (expval != null) {
			expval = expval.resolveAgainst(scope);
			data.setLineThickness(Cast.asFloat(scope, expval.value(scope)));
		}
		boolean boolval = getFacetValue(scope, ChartDataStatement.MARKER, true);
		data.setMarkerBool(scope, boolval);
		boolval = getFacetValue(scope, ChartDataStatement.USE_SECOND_Y_AXIS, false);
		data.setUseSecondYAxis(scope, boolval);

		boolval = getFacetValue(scope, ChartDataStatement.LINE_VISIBLE, true);
		data.setShowLine(scope, boolval);
		boolval = getFacetValue(scope, ChartDataStatement.FILL, true);
		data.setFillMarker(scope, boolval);

		stval = getFacetValue(scope, ChartDataStatement.MARKERSHAPE, null);
		data.setMarkerShape(scope, stval);

		// should allow different marker shapes in a list (with Gama Shapes)
		/*
		 * expval = getFacetValue(scope, ChartDataStatement.MARKERSHAPE, null); if (expval!=null) {
		 * expval=expval.resolveAgainst(scope); data.setMarkerShapeExp(scope, expval);
		 *
		 * }
		 */

		final Object forcecumul = getFacetValue(scope, ChartDataStatement.CUMUL_VALUES, null);
		if (forcecumul != null) {
			data.setCumulative(scope, Cast.asBool(scope, forcecumul));
			data.setForceCumulative(scope, true);
		}

		data.createInitialSeries(scope);

		expval = getFacet(ChartDataStatement.MARKERSIZE);
		if (expval != null) {
			data.setUseSize(true);
			expval = expval.resolveAgainst(scope);
			data.setMarkerSize(scope, expval);

		}

		return data;
	}

	@Override
	protected Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		final ChartDataSet graphdataset = (ChartDataSet) scope.getVarValue(ChartLayerStatement.CHARTDATASET);
		final ChartDataSourceList data = createDataSource(scope, graphdataset);
		graphdataset.addDataSource(data);
		return data;
	}

}