/*********************************************************************************************
 *
 * 'ChartLayerStatement.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.outputs.layers.charts;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jfree.chart.JFreeChart;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.layers.AbstractLayerStatement;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaList;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 9 nov. 2009
 * 
 * @todo Description
 * 
 */
@symbol (
		name = IKeyword.CHART,
		kind = ISymbolKind.LAYER,
		with_sequence = true,
		concept = { IConcept.CHART, IConcept.DISPLAY })
@inside (
		symbols = IKeyword.DISPLAY)
@facets (
		value = {
				/*
				 * @facet(name = ISymbol.VALUE, type = TypeManager.STRING, optional = true),
				 */
				@facet (
						name = ChartLayerStatement.XRANGE,
						type = { IType.FLOAT, IType.INT, IType.POINT, IType.LIST },
						optional = true,
						doc = @doc ("range of the x-axis. Can be a number (which will set the axis total range) or a point (which will set the min and max of the axis).")),
				@facet (
						name = ChartLayerStatement.YRANGE,
						type = { IType.FLOAT, IType.INT, IType.POINT, IType.LIST },
						optional = true,
						doc = @doc ("range of the y-axis. Can be a number (which will set the axis total range) or a point (which will set the min and max of the axis).")),
				@facet (
						name = IKeyword.POSITION,
						type = IType.POINT,
						optional = true,
						doc = @doc ("position of the upper-left corner of the layer. Note that if coordinates are in [0,1[, the position is relative to the size of the environment (e.g. {0.5,0.5} refers to the middle of the display) whereas it is absolute when coordinates are greter than 1. The position can only be a 3D point {0.5, 0.5, 0.5}, the last coordinate specifying the elevation of the layer.")),
				@facet (
						name = IKeyword.SIZE,
						type = IType.POINT,
						optional = true,
						doc = @doc ("the layer resize factor: {1,1} refers to the original size whereas {0.5,0.5} divides by 2 the height and the width of the layer. In case of a 3D layer, a 3D point can be used (note that {1,1} is equivalent to {1,1,0}, so a resize of a layer containing 3D objects with a 2D points will remove the elevation)")),
				@facet (
						name = IKeyword.REVERSE_AXIS,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("reverse X and Y axis (for example to get horizental bar charts")),
				@facet (
						name = IKeyword.BACKGROUND,
						type = IType.COLOR,
						optional = true,
						doc = @doc ("the background color")),
				@facet (
						name = IKeyword.X_SERIE,
						type = { IType.LIST, IType.FLOAT, IType.INT },
						optional = true,
						doc = @doc ("for series charts, change the default common x serie (simulation cycle) for an other value (list or numerical).")),
				@facet (
						name = IKeyword.X_LABELS,
						type = { IType.LIST, IType.FLOAT, IType.INT, IType.LABEL },
						optional = true,
						doc = @doc ("change the default common x series labels (replace x value or categories) for an other value (string or numerical).")),
				// @facet(name = IKeyword.Y_SERIES,
				// type = {IType.LIST, IType.FLOAT, IType.INT},
				// optional = true,
				// doc = @doc("change the default common y serie for an other value
				// (list or numerical).")),
				@facet (
						name = IKeyword.Y_LABELS,
						type = { IType.LIST, IType.FLOAT, IType.INT, IType.LABEL },
						optional = true,
						doc = @doc ("for heatmaps/3d charts, change the default y serie for an other value (string or numerical in a list or cumulative).")),
				@facet (
						name = IKeyword.AXES,
						type = IType.COLOR,
						optional = true,
						doc = @doc ("the axis color")),
				@facet (
						name = IKeyword.TYPE,
						type = IType.ID,
						values = { IKeyword.XY, IKeyword.SCATTER, IKeyword.HISTOGRAM, IKeyword.SERIES, IKeyword.PIE,
								IKeyword.RADAR, IKeyword.HEATMAP, IKeyword.BOX_WHISKER },
						optional = true,
						doc = @doc ("the type of chart. It could be histogram, series, xy, pie, radar, heatmap or box whisker. The difference between series and xy is that the former adds an implicit x-axis that refers to the numbers of cycles, while the latter considers the first declaration of data to be its x-axis.")),
				@facet (
						name = IKeyword.STYLE,
						type = IType.ID,
						values = { IKeyword.LINE, IKeyword.WHISKER, IKeyword.AREA, IKeyword.BAR, IKeyword.DOT,
								IKeyword.STEP, IKeyword.SPLINE, IKeyword.STACK, IKeyword.THREE_D, IKeyword.RING,
								IKeyword.EXPLODED, IKeyword.DEFAULT },
						doc = @doc ("The sub-style style, also default style for the series."),
						optional = true),
				// @facet(name = IKeyword.TRANSPARENCY, type = IType.FLOAT, optional =
				// true, doc = @doc("the style of the chart")),
				// unused?
				@facet (
						name = IKeyword.GAP,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("minimum gap between bars (in proportion)")),
				@facet (
						name = ChartLayerStatement.YTICKUNIT,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("the tick unit for the x-axis (distance between vertical lines and values bellow the axis).")),
				@facet (
						name = ChartLayerStatement.XTICKUNIT,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("the tick unit for the y-axis (distance between horyzontal lines and values on the left of the axis).")),
				@facet (
						name = IKeyword.NAME,
						type = IType.LABEL,
						optional = false,
						doc = @doc ("the identifier of the chart layer")),
				@facet (
						name = ChartLayerStatement.XLABEL,
						type = IType.STRING,
						optional = true,
						doc = @doc ("the title for the X axis")),
				@facet (
						name = ChartLayerStatement.YLABEL,
						type = IType.STRING,
						optional = true,
						doc = @doc ("the title for the Y axis")),
				@facet (
						name = IKeyword.COLOR,
						type = IType.COLOR,
						optional = true,
						doc = @doc ("Text color")),
				@facet (
						name = ChartLayerStatement.SERIES_LABEL_POSITION,
						type = IType.ID,
						values = { "default", "none", "legend", "onchart", "yaxis", "xaxis" },
						optional = true,
						doc = @doc ("Position of the Series names: default (best guess), none, legend, onchart, xaxis (for category plots) or yaxis (uses the first serie name).")),
				@facet (
						name = ChartLayerStatement.MEMORIZE,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Whether or not to keep the values in memory (in order to produce a csv file, for instance). The default value, true, can also be changed in the preferences")),
				@facet (
						name = ChartLayerStatement.TICKFONTFACE,
						type = IType.STRING,
						optional = true,
						doc = @doc ("Tick font face")),
				@facet (
						name = ChartLayerStatement.TICKFONTSIZE,
						type = IType.INT,
						optional = true,
						doc = @doc ("Tick font size")),
				@facet (
						name = ChartLayerStatement.TICKFONTSTYLE,
						type = IType.ID,
						values = { "plain", "bold", "italic" },
						optional = true,
						doc = @doc ("the style used to display ticks")),
				@facet (
						name = ChartLayerStatement.LABELFONTFACE,
						type = IType.STRING,
						optional = true,
						doc = @doc ("Label font face")),
				@facet (
						name = ChartLayerStatement.LABELFONTSIZE,
						type = IType.INT,
						optional = true,
						doc = @doc ("Label font size")),
				@facet (
						name = ChartLayerStatement.LABELFONTSTYLE,
						type = IType.ID,
						values = { "plain", "bold", "italic" },
						optional = true,
						doc = @doc ("the style used to display labels")),
				@facet (
						name = ChartLayerStatement.LEGENDFONTFACE,
						type = IType.STRING,
						optional = true,
						doc = @doc ("Legend font face")),
				@facet (
						name = ChartLayerStatement.LEGENDFONTSIZE,
						type = IType.INT,
						optional = true,
						doc = @doc ("Legend font size")),
				@facet (
						name = ChartLayerStatement.LEGENDFONTSTYLE,
						type = IType.ID,
						values = { "plain", "bold", "italic" },
						optional = true,
						doc = @doc ("the style used to display legend")),
				@facet (
						name = ChartLayerStatement.TITLEFONTFACE,
						type = IType.STRING,
						optional = true,
						doc = @doc ("Title font face")),
				@facet (
						name = ChartLayerStatement.TITLEFONTSIZE,
						type = IType.INT,
						optional = true,
						doc = @doc ("Title font size")),
				@facet (
						name = ChartLayerStatement.TITLEFONTSTYLE,
						type = IType.ID,
						values = { "plain", "bold", "italic" },
						optional = true,
						doc = @doc ("the style used to display titles")), },

		omissible = IKeyword.NAME)
@doc (
		value = "`" + IKeyword.CHART
				+ "` allows modeler to display a chart: this enables to display specific values of the model at each iteration. GAMA can display various chart types: time series (series), pie charts (pie) and histograms (histogram).",
		usages = { @usage (
				value = "The general syntax is:",
				examples = { @example (
						value = "display chart_display {",
						isExecutable = false),
						@example (
								value = "   chart \"chart name\" type: series [additional options] {",
								isExecutable = false),
						@example (
								value = "      [Set of data, datalists statements]",
								isExecutable = false),
						@example (
								value = "   }",
								isExecutable = false),
						@example (
								value = "}",
								isExecutable = false) }) },
		see = { IKeyword.DISPLAY, IKeyword.AGENTS, IKeyword.EVENT, "graphics", IKeyword.GRID_POPULATION, IKeyword.IMAGE,
				IKeyword.OVERLAY, IKeyword.QUADTREE, IKeyword.POPULATION, IKeyword.TEXT })
public class ChartLayerStatement extends AbstractLayerStatement {

	public static final String XRANGE = "x_range";
	public static final String YRANGE = "y_range";

	public static final String XLABEL = "x_label";
	public static final String YLABEL = "y_label";
	public static final String MEMORIZE = "memorize";

	public static final String SERIES_LABEL_POSITION = "series_label_position";

	public static final String YTICKUNIT = "y_tick_unit";
	public static final String XTICKUNIT = "x_tick_unit";

	public static final String TICKFONTFACE = "tick_font";
	public static final String TICKFONTSIZE = "tick_font_size";
	public static final String TICKFONTSTYLE = "tick_font_style";

	public static final String LABELFONTFACE = "label_font";
	public static final String LABELFONTSIZE = "label_font_size";
	public static final String LABELFONTSTYLE = "label_font_style";

	public static final String LEGENDFONTFACE = "legend_font";
	public static final String LEGENDFONTSIZE = "legend_font_size";
	public static final String LEGENDFONTSTYLE = "legend_font_style";

	public static final String TITLEFONTFACE = "title_font";
	public static final String TITLEFONTSIZE = "title_font_size";
	public static final String TITLEFONTSTYLE = "title_font_style";

	public static final String CHARTDATASET = "chart_dataset_transfer";

	private ChartDataSet chartdataset;

	public class DataDeclarationSequence extends AbstractStatementSequence {

		public DataDeclarationSequence(final IDescription desc) {
			super(desc);
		}

		// shouldn't have to do that but I don't know how to get the "chart
		// statement" inside the "data statement" declaration otherwise...

		// We create the variable in which the datas will be accumulated
		@Override
		public void enterScope(final IScope scope) {
			super.enterScope(scope);

			scope.addVarWithValue(ChartLayerStatement.CHARTDATASET, chartdataset);

		}

		// We save the datas once the computation is finished
		@Override
		public void leaveScope(final IScope scope) {
			chartdataset = (ChartDataSet) scope.getVarValue(ChartLayerStatement.CHARTDATASET);
			// System.out.println("DELETE ME, I am in chartlayerstatement top");

			super.leaveScope(scope);
		}

	}

	static final int SERIES_CHART = 0;
	static final int HISTOGRAM_CHART = 1;
	static final int PIE_CHART = 2;
	static final int XY_CHART = 3;
	static final int BOX_WHISKER_CHART = 4;
	static final int SCATTER_CHART = 5;

	private ChartOutput chartoutput = null;

	// private HashMap<String,Object> chartParameters=new
	// HashMap<String,Object>();

	static String xAxisName = "'time'";
	final Map<String, Double> lastValues;
	Long lastComputeCycle;
	ChartDataStatement timeSeriesXData = null;
	DataDeclarationSequence dataDeclaration = new DataDeclarationSequence(null);

	public ChartOutput getOutput() {
		return chartoutput;
	}

	public ChartLayerStatement(/* final ISymbol context, */final IDescription desc) throws GamaRuntimeException {
		super(desc);
		lastValues = new LinkedHashMap<>();
		lastComputeCycle = 0l;
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> commands) {
		dataDeclaration.setChildren(commands);
	}

	public JFreeChart getChart() {
		// should be changed, used in LayerSideControls to open an editor...
		return getDataSet().getOutput().getJFChart();
	}

	public ChartDataSet getDataSet() {
		return chartdataset;
	}

	@Override
	public boolean _init(final IScope scope) throws GamaRuntimeException {
		lastValues.clear();

		// chartParameters.clear();

		IExpression string1 = getFacet(IKeyword.TYPE);
		// chartParameters.put(IKeyword.TYPE, string1);

		chartoutput = ChartJFreeChartOutput.createChartOutput(scope, getName(), string1);

		string1 = getFacet(IKeyword.STYLE);
		if (string1 != null) {
			chartoutput.setStyle(scope, Cast.asString(scope, string1.value(scope)));
		}

		string1 = getFacet(IKeyword.REVERSE_AXIS);
		if (string1 != null) {
			chartoutput.setReverseAxis(scope, Cast.asBool(scope, string1.value(scope)));
		}

		chartoutput.createChart(scope);

		string1 = getFacet(ChartLayerStatement.XLABEL);
		if (string1 != null) {
			chartoutput.setXLabel(scope, Cast.asString(scope, string1.value(scope)));
		}

		string1 = getFacet(ChartLayerStatement.YLABEL);
		if (string1 != null) {
			chartoutput.setYLabel(scope, Cast.asString(scope, string1.value(scope)));
		}

		string1 = getFacet(ChartLayerStatement.SERIES_LABEL_POSITION);
		if (string1 != null) {
			chartoutput.setSeriesLabelPosition(scope, Cast.asString(scope, string1.value(scope)));
		}

		IExpression expr = getFacet(XRANGE);
		if (expr != null) {
			final Object range = expr.value(scope);

			if (range instanceof Number) {
				chartoutput.setXRangeInterval(scope, ((Number) range).doubleValue());
			} else if (range instanceof GamaPoint) {
				chartoutput.setXRangeMinMax(scope, ((GamaPoint) range).getX(), ((GamaPoint) range).getY());
			} else if (range instanceof GamaList) {
				chartoutput.setXRangeMinMax(scope, Cast.asFloat(scope, ((GamaList<?>) range).get(0)),
						Cast.asFloat(scope, ((GamaList<?>) range).get(1)));
			}
		}

		expr = getFacet(YRANGE);
		if (expr != null) {
			final Object range = expr.value(scope);

			if (range instanceof Number) {
				chartoutput.setYRangeInterval(scope, ((Number) range).doubleValue());
			} else if (range instanceof GamaPoint) {
				chartoutput.setYRangeMinMax(scope, ((GamaPoint) range).getX(), ((GamaPoint) range).getY());
			} else if (range instanceof GamaList) {
				chartoutput.setYRangeMinMax(scope, Cast.asFloat(scope, ((GamaList<?>) range).get(0)),
						Cast.asFloat(scope, ((GamaList<?>) range).get(1)));
			}
		}

		IExpression expr2 = getFacet(XTICKUNIT);
		if (expr2 != null) {
			final Object range = expr2.value(scope);

			if (range instanceof Number) {
				final double r = ((Number) range).doubleValue();
				chartoutput.setXTickUnit(scope, r);
			}
		}

		expr2 = getFacet(YTICKUNIT);
		if (expr2 != null) {
			final Object range = expr2.value(scope);

			if (range instanceof Number) {
				final double r = ((Number) range).doubleValue();
				chartoutput.setYTickUnit(scope, r);
			}
		}
		expr2 = getFacet(IKeyword.GAP);
		if (expr2 != null) {
			final Double range = Cast.asFloat(scope, expr2.value(scope));
			chartoutput.setGap(scope, range);
		}
		// ((BarRenderer) plot.getRenderer()).setItemMargin(gap);

		GamaColor colorvalue = new GamaColor(Color.black);
		IExpression color = getFacet(IKeyword.AXES);
		if (color != null) {
			colorvalue = Cast.asColor(scope, color.value(scope));
		}
		chartoutput.setAxesColorValue(scope, colorvalue);

		colorvalue = new GamaColor(Color.black);
		color = getFacet(IKeyword.COLOR);
		if (color != null) {
			colorvalue = Cast.asColor(scope, color.value(scope));
			chartoutput.setColorValue(scope, colorvalue);
		}

		colorvalue = new GamaColor(Color.white);
		color = getFacet(IKeyword.BACKGROUND);
		if (color != null) {
			colorvalue = Cast.asColor(scope, color.value(scope));
			chartoutput.setBackgroundColorValue(scope, colorvalue);
		}

		IExpression face = getFacet(ChartLayerStatement.TICKFONTFACE);
		if (face != null) {
			chartoutput.setTickFontFace(scope, Cast.asString(scope, face));
		}
		face = getFacet(ChartLayerStatement.LABELFONTFACE);
		if (face != null) {
			chartoutput.setLabelFontFace(scope, Cast.asString(scope, face));
		}
		face = getFacet(ChartLayerStatement.LEGENDFONTFACE);
		if (face != null) {
			chartoutput.setLegendFontFace(scope, Cast.asString(scope, face));
		}
		face = getFacet(ChartLayerStatement.TITLEFONTFACE);
		if (face != null) {
			chartoutput.setTitleFontFace(scope, Cast.asString(scope, face));
		}
		face = getFacet(ChartLayerStatement.TICKFONTSIZE);
		if (face != null) {
			chartoutput.setTickFontSize(scope, Cast.asInt(scope, face.value(scope)).intValue());
		}
		face = getFacet(ChartLayerStatement.LABELFONTSIZE);
		if (face != null) {
			chartoutput.setLabelFontSize(scope, Cast.asInt(scope, face.value(scope)).intValue());
		}
		face = getFacet(ChartLayerStatement.LEGENDFONTSIZE);
		if (face != null) {
			chartoutput.setLegendFontSize(scope, Cast.asInt(scope, face.value(scope)).intValue());
		}
		face = getFacet(ChartLayerStatement.TITLEFONTSIZE);
		if (face != null) {
			chartoutput.setTitleFontSize(scope, Cast.asInt(scope, face.value(scope)).intValue());
		}
		face = getFacet(ChartLayerStatement.TICKFONTSTYLE);
		if (face != null) {
			chartoutput.setTickFontStyle(scope, getLiteral(ChartLayerStatement.TICKFONTSTYLE));
		}
		face = getFacet(ChartLayerStatement.LABELFONTSTYLE);
		if (face != null) {
			chartoutput.setLabelFontStyle(scope, getLiteral(ChartLayerStatement.LABELFONTSTYLE));
		}
		face = getFacet(ChartLayerStatement.LEGENDFONTSTYLE);
		if (face != null) {
			chartoutput.setLegendFontStyle(scope, getLiteral(ChartLayerStatement.LEGENDFONTSTYLE));
		}
		face = getFacet(TITLEFONTSTYLE);
		if (face != null) {
			chartoutput.setTitleFontStyle(scope, getLiteral(TITLEFONTSTYLE));
		}
		boolean memorize = GamaPreferences.Displays.CHART_MEMORIZE.getValue();
		face = getFacet(MEMORIZE);
		if (face != null) {
			memorize = Cast.asBool(scope, face.value(scope));
		}

		chartoutput.initChart(scope, getName());

		chartdataset = new ChartDataSet(memorize);
		chartoutput.setChartdataset(chartdataset);
		chartoutput.initdataset();

		expr = getFacet(IKeyword.X_SERIE);
		if (expr != null) {
			final IExpression expval = getFacet(IKeyword.X_SERIE).resolveAgainst(scope);
			chartdataset.setXSource(scope, expval);
			chartoutput.setUseXSource(scope, expval);
		}

		expr = getFacet(IKeyword.X_LABELS);
		if (expr != null) {
			final IExpression expval = getFacet(IKeyword.X_LABELS).resolveAgainst(scope);
			chartdataset.setXLabels(scope, expval);
			chartoutput.setUseXLabels(scope, expval);
		}

		/*
		 * expr = getFacet(IKeyword.Y_SERIE); if (expr!=null) { IExpression expval =
		 * getFacet(IKeyword.Y_SERIE).resolveAgainst(scope); chartdataset.setYSource(scope,expval);
		 * chartoutput.setUseYSource(scope,expval); }
		 */
		// will be added with 3d charts

		expr = getFacet(IKeyword.Y_LABELS);
		if (expr != null) {
			final IExpression expval = getFacet(IKeyword.Y_LABELS).resolveAgainst(scope);
			chartdataset.setYLabels(scope, expval);
			chartoutput.setUseYLabels(scope, expval);
		}

		dataDeclaration.executeOn(scope);

		chartoutput.initChart_post_data_init(scope);
		chartoutput.updateOutput(scope);

		return true;
	}

	@Override
	public boolean _step(final IScope scope) throws GamaRuntimeException {
		chartoutput.step(scope);

		return true;
	}

	@Override
	public short getType() {
		return ILayerStatement.CHART;
	}

	@Override
	public void dispose() {
		// chart = null;
		super.dispose();
	}

	public void saveHistory() {
		if (!getDataSet().keepsHistory())
			return;
		final IScope scope = this.getDisplayOutput().getScope().copy("Save");
		if (scope == null) { return; }
		try {
			this.getDataSet().saveHistory(scope, this.getName());
		} catch (final Exception e) {
			e.printStackTrace();
			return;
		} finally {
			GAMA.releaseScope(scope);
		}
	}

}
