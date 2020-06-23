/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.charts.ChartLayerStatement.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers.charts;

import java.awt.Color;
import java.awt.Font;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jfree.chart.JFreeChart;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.LayeredDisplayOutput;
import msi.gama.outputs.layers.AbstractLayerStatement;
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
import msi.gama.util.GamaFont;
import msi.gama.util.IList;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

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
		value = { @facet (
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
						name = ChartLayerStatement.Y2RANGE,
						type = { IType.FLOAT, IType.INT, IType.POINT, IType.LIST },
						optional = true,
						doc = @doc ("range of the second y-axis. Can be a number (which will set the axis total range) or a point (which will set the min and max of the axis).")),
				@facet (
						name = IKeyword.POSITION,
						type = IType.POINT,
						optional = true,
						doc = @doc ("position of the upper-left corner of the layer. Note that if coordinates are in [0,1[, the position is relative to the size of the environment (e.g. {0.5,0.5} refers to the middle of the display) whereas it is absolute when coordinates are greater than 1 for x and y. The z-ordinate can only be defined between 0 and 1. The position can only be a 3D point {0.5, 0.5, 0.5}, the last coordinate specifying the elevation of the layer.")),
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
				@facet (
						name = IKeyword.Y_LABELS,
						type = { IType.LIST, IType.FLOAT, IType.INT, IType.LABEL },
						optional = true,
						doc = @doc ("for heatmaps/3d charts, change the default y serie for an other value (string or numerical in a list or cumulative).")),
				@facet (
						name = ChartLayerStatement.X_LOGSCALE,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("use Log Scale for X axis")),
				@facet (
						name = ChartLayerStatement.Y_LOGSCALE,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("use Log Scale for Y axis")),
				@facet (
						name = ChartLayerStatement.Y2_LOGSCALE,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("use Log Scale for second Y axis")),
				@facet (
						name = IKeyword.AXES,
						type = IType.COLOR,
						optional = true,
						doc = @doc ("the axis color")),
				@facet (
						name = ChartLayerStatement.XTICKVALUEVISIBLE,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("X tick values visible")),
				@facet (
						name = ChartLayerStatement.YTICKVALUEVISIBLE,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Y tick values visible")),
				@facet (
						name = ChartLayerStatement.TITLEVISIBLE,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("chart title visible")),
				@facet (
						name = ChartLayerStatement.XTICKLINEVISIBLE,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("X tick line visible")),
				@facet (
						name = ChartLayerStatement.YTICKLINEVISIBLE,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Y tick line visible")),
				@facet (
						name = ChartLayerStatement.TICKLINECOLOR,
						type = IType.COLOR,
						optional = true,
						doc = @doc ("the tick lines color")),
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
						name = ChartLayerStatement.Y2TICKUNIT,
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
						type = IType.STRING,
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
						name = ChartLayerStatement.Y2LABEL,
						type = IType.STRING,
						optional = true,
						doc = @doc ("the title for the second Y axis")),
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
						type = { IType.STRING, IType.FONT },
						optional = true,
						doc = @doc ("Tick font face. Either the name of a font face or a font. When used for a series chart, it will set the font of values on the axes, but When used with a pie, it will modify the font of messages associated to each pie section.")),
				@facet (
						name = ChartLayerStatement.TICKFONTSIZE,
						type = IType.INT,
						optional = true,
						doc = @doc (
								deprecated = "Use a font in tick_font instead",
								value = "Tick font size")),
				@facet (
						name = ChartLayerStatement.TICKFONTSTYLE,
						type = IType.ID,
						values = { "plain", "bold", "italic" },
						optional = true,
						doc = @doc (
								deprecated = "Use a font in tick_font instead",
								value = "the style used to display ticks")),
				@facet (
						name = ChartLayerStatement.LABELFONTFACE,
						type = { IType.STRING, IType.FONT },
						optional = true,
						doc = @doc ("Label font face. Either the name of a font face or a font")),
				@facet (
						name = ChartLayerStatement.LABELFONTSIZE,
						type = IType.INT,
						optional = true,
						doc = @doc (
								deprecated = "Use a font in label_font instead",
								value = "Label font size")),
				@facet (
						name = ChartLayerStatement.LABELFONTSTYLE,
						type = IType.ID,
						values = { "plain", "bold", "italic" },
						optional = true,
						doc = @doc (
								deprecated = "Use a font in label_font instead",
								value = "the style used to display labels")),
				@facet (
						name = ChartLayerStatement.LEGENDFONTFACE,
						type = { IType.STRING, IType.FONT },
						optional = true,
						doc = @doc ("Legend font face. Either the name of a font face or a font")),
				@facet (
						name = ChartLayerStatement.LEGENDFONTSIZE,
						type = IType.INT,
						optional = true,
						doc = @doc (
								deprecated = "Use a font in legend_font instead",
								value = "Legend font size")),
				@facet (
						name = ChartLayerStatement.LEGENDFONTSTYLE,
						type = IType.ID,
						values = { "plain", "bold", "italic" },
						optional = true,
						doc = @doc (
								deprecated = "Use a font in legend_font instead",
								value = "the style used to display legend")),
				@facet (
						name = ChartLayerStatement.TITLEFONTFACE,
						type = { IType.STRING, IType.FONT },
						optional = true,
						doc = @doc ("Title font face. Either the name of a font face or a font")),
				@facet (
						name = ChartLayerStatement.TITLEFONTSIZE,
						type = IType.INT,
						optional = true,
						doc = @doc (
								deprecated = "Use a font in title_font instead",
								value = "Title font size")),
				@facet (
						name = ChartLayerStatement.TITLEFONTSTYLE,
						type = IType.ID,
						values = { "plain", "bold", "italic" },
						optional = true,
						doc = @doc (
								deprecated = "Use a font in title_font instead",
								value = "the style used to display titles")), },

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
	public static final String Y2RANGE = "y2_range";

	public static final String XLABEL = "x_label";
	public static final String YLABEL = "y_label";
	public static final String Y2LABEL = "y2_label";
	public static final String MEMORIZE = "memorize";

	public static final String SERIES_LABEL_POSITION = "series_label_position";

	public static final String X_LOGSCALE = "x_log_scale";
	public static final String Y_LOGSCALE = "y_log_scale";
	public static final String Y2_LOGSCALE = "y2_log_scale";

	public static final String YTICKUNIT = "y_tick_unit";
	public static final String Y2TICKUNIT = "y_tick_unit";
	public static final String XTICKUNIT = "x_tick_unit";

	public static final String XTICKLINEVISIBLE = "x_tick_line_visible";
	public static final String YTICKLINEVISIBLE = "y_tick_line_visible";
	public static final String TICKLINECOLOR = "tick_line_color";

	public static final String TITLEVISIBLE = "title_visible";
	public static final String XTICKVALUEVISIBLE = "x_tick_values_visible";
	public static final String YTICKVALUEVISIBLE = "y_tick_values_visible";

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

	ChartDataSet chartdataset;

	public class DataDeclarationSequence extends AbstractStatementSequence {

		public DataDeclarationSequence(final IDescription desc) {
			super(desc);
		}

		// shouldn't have to do that but I don't know how to get the "chart
		// statement" inside the "data statement" declaration otherwise...

	}

	private ChartOutput chartoutput = null;

	// private HashMap<String,Object> chartParameters=new
	// HashMap<String,Object>();

	final Map<String, Double> lastValues;
	DataDeclarationSequence dataDeclaration = new DataDeclarationSequence(getDescription());

	public ChartOutput getOutput() {
		return chartoutput;
	}

	public ChartLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		lastValues = new LinkedHashMap<>();
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

	// What can not change at eery step
	@Override
	public boolean _init(final IScope scope) throws GamaRuntimeException {
		setName(getChartName(scope));
		lastValues.clear();

		IExpression string1 = getFacet(IKeyword.TYPE);

		chartoutput = ChartJFreeChartOutput.createChartOutput(scope, getName(), string1);

		string1 = getFacet(IKeyword.STYLE);
		if (string1 != null) {
			chartoutput.setStyle(scope, Cast.asString(scope, string1.value(scope)));
		}

		string1 = getFacet(IKeyword.REVERSE_AXIS);
		if (string1 != null) {
			chartoutput.setReverseAxis(scope, Cast.asBool(scope, string1.value(scope)));
		}
		string1 = getFacet(ChartLayerStatement.X_LOGSCALE);
		if (string1 != null) {
			chartoutput.setX_LogScale(scope, Cast.asBool(scope, string1.value(scope)));
		}
		string1 = getFacet(ChartLayerStatement.Y_LOGSCALE);
		if (string1 != null) {
			chartoutput.setY_LogScale(scope, Cast.asBool(scope, string1.value(scope)));
		}

		string1 = getFacet(ChartLayerStatement.Y2_LOGSCALE);
		if (string1 != null) {
			chartoutput.setY2_LogScale(scope, Cast.asBool(scope, string1.value(scope)));
		}

		chartoutput.createChart(scope);
		updateValues(scope);

		//
		boolean memorize = GamaPreferences.Displays.CHART_MEMORIZE.getValue();
		final IExpression face = getFacet(MEMORIZE);
		if (face != null) {
			memorize = Cast.asBool(scope, face.value(scope));
		}

		chartoutput.initChart(scope, getName());
		final boolean isBatch = scope.getExperiment().getSpecies().isBatch();
		final boolean isPermanent = getDisplayOutput().isPermanent();
		final boolean isBatchAndPermanent = isBatch && isPermanent;
		chartdataset = new ChartDataSet(memorize, isBatchAndPermanent);
		chartoutput.setChartdataset(chartdataset);
		chartoutput.initdataset();

		IExpression expr = getFacet(IKeyword.X_SERIE);
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
		scope.addVarWithValue(ChartLayerStatement.CHARTDATASET, chartdataset);
		for (final IStatement s : dataDeclaration.getCommands()) {
			scope.execute(s);
		}
		chartdataset = (ChartDataSet) scope.getVarValue(ChartLayerStatement.CHARTDATASET);
		chartoutput.initChart_post_data_init(scope);
		chartoutput.updateOutput(scope);

		return true;
	}

	private String getChartName(final IScope scope) {
		return Cast.asString(scope, getFacetValue(scope, IKeyword.NAME));
	}

	// what can be updated at each step
	public boolean updateValues(final IScope scope) {

		IExpression string1 = getFacet(ChartLayerStatement.XLABEL);
		if (string1 != null) {
			chartoutput.setXLabel(scope, Cast.asString(scope, string1.value(scope)));
		}

		string1 = getFacet(ChartLayerStatement.YLABEL);
		if (string1 != null) {
			chartoutput.setYLabel(scope, Cast.asString(scope, string1.value(scope)));
		}

		string1 = getFacet(ChartLayerStatement.Y2LABEL);
		if (string1 != null) {
			chartoutput.setY2Label(scope, Cast.asString(scope, string1.value(scope)));
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
			} else if (range instanceof IList) {
				chartoutput.setXRangeMinMax(scope, Cast.asFloat(scope, ((IList<?>) range).get(0)),
						Cast.asFloat(scope, ((IList<?>) range).get(1)));
			}
		}

		expr = getFacet(YRANGE);
		if (expr != null) {
			final Object range = expr.value(scope);

			if (range instanceof Number) {
				chartoutput.setYRangeInterval(scope, ((Number) range).doubleValue());
			} else if (range instanceof GamaPoint) {
				chartoutput.setYRangeMinMax(scope, ((GamaPoint) range).getX(), ((GamaPoint) range).getY());
			} else if (range instanceof IList) {
				chartoutput.setYRangeMinMax(scope, Cast.asFloat(scope, ((IList<?>) range).get(0)),
						Cast.asFloat(scope, ((IList<?>) range).get(1)));
			}
		}
		expr = getFacet(Y2RANGE);
		if (expr != null) {
			final Object range = expr.value(scope);

			if (range instanceof Number) {
				chartoutput.setY2RangeInterval(scope, ((Number) range).doubleValue());
			} else if (range instanceof GamaPoint) {
				chartoutput.setY2RangeMinMax(scope, ((GamaPoint) range).getX(), ((GamaPoint) range).getY());
			} else if (range instanceof IList) {
				chartoutput.setY2RangeMinMax(scope, Cast.asFloat(scope, ((IList<?>) range).get(0)),
						Cast.asFloat(scope, ((IList<?>) range).get(1)));
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
		expr2 = getFacet(Y2TICKUNIT);
		if (expr2 != null) {
			final Object range = expr2.value(scope);

			if (range instanceof Number) {
				final double r = ((Number) range).doubleValue();
				chartoutput.setY2TickUnit(scope, r);
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
		color = getFacet(ChartLayerStatement.TICKLINECOLOR);
		if (color != null) {
			colorvalue = Cast.asColor(scope, color.value(scope));
		}
		chartoutput.setTickColorValue(scope, colorvalue);

		string1 = getFacet(ChartLayerStatement.XTICKVALUEVISIBLE);
		if (string1 != null) {
			chartoutput.setXTickValueVisible(scope, Cast.asBool(scope, string1.value(scope)));
		}
		string1 = getFacet(ChartLayerStatement.YTICKVALUEVISIBLE);
		if (string1 != null) {
			chartoutput.setYTickValueVisible(scope, Cast.asBool(scope, string1.value(scope)));
		}
		string1 = getFacet(ChartLayerStatement.TITLEVISIBLE);
		if (string1 != null) {
			chartoutput.setTitleVisible(scope, Cast.asBool(scope, string1.value(scope)));
		}

		string1 = getFacet(ChartLayerStatement.XTICKLINEVISIBLE);
		if (string1 != null) {
			chartoutput.setXTickLineVisible(scope, Cast.asBool(scope, string1.value(scope)));
		}
		string1 = getFacet(ChartLayerStatement.YTICKLINEVISIBLE);
		if (string1 != null) {
			chartoutput.setYTickLineVisible(scope, Cast.asBool(scope, string1.value(scope)));
		}
		color = getFacet(IKeyword.COLOR);
		if (color != null) {
			colorvalue = Cast.asColor(scope, color.value(scope));
		}
		chartoutput.setColorValue(scope, colorvalue);
		colorvalue = new GamaColor(Color.white);
		color = getFacet(IKeyword.BACKGROUND);
		if (color != null) {
			colorvalue = Cast.asColor(scope, color.value(scope));
		}
		chartoutput.setBackgroundColorValue(scope, colorvalue);

		GamaFont font = null;
		IExpression face = getFacet(TICKFONTFACE);
		if (face != null) {
			if (face.getGamlType() == Types.STRING) {
				chartoutput.setTickFontFace(scope, Cast.asString(scope, face.value(scope)));
			} else {
				font = (GamaFont) Types.FONT.cast(scope, face.value(scope), null, false);
				if (font != null) {
					chartoutput.setTickFontFace(scope, font.getFontName());
					chartoutput.setTickFontSize(scope, font.getSize());
					chartoutput.setTickFontStyle(scope, font.getStyle());
				}
			}
		}

		face = getFacet(LABELFONTFACE);
		if (face != null) {
			if (face.getGamlType() == Types.STRING) {
				chartoutput.setLabelFontFace(scope, Cast.asString(scope, face.value(scope)));
			} else {
				font = (GamaFont) Types.FONT.cast(scope, face.value(scope), null, false);
				if (font != null) {
					chartoutput.setLabelFontFace(scope, font.getFontName());
					chartoutput.setLabelFontSize(scope, font.getSize());
					chartoutput.setLabelFontStyle(scope, font.getStyle());
				}
			}
		}

		face = getFacet(LEGENDFONTFACE);
		if (face != null) {
			if (face.getGamlType() == Types.STRING) {
				chartoutput.setLegendFontFace(scope, Cast.asString(scope, face.value(scope)));
			} else {
				font = (GamaFont) Types.FONT.cast(scope, face.value(scope), null, false);
				if (font != null) {
					chartoutput.setLegendFontFace(scope, font.getFontName());
					chartoutput.setLegendFontSize(scope, font.getSize());
					chartoutput.setLegendFontStyle(scope, font.getStyle());
				}
			}
		}

		face = getFacet(TITLEFONTFACE);
		if (face != null) {
			if (face.getGamlType() == Types.STRING) {
				chartoutput.setTitleFontFace(scope, Cast.asString(scope, face.value(scope)));
			} else {
				font = (GamaFont) Types.FONT.cast(scope, face.value(scope), null, false);
				if (font != null) {
					chartoutput.setTitleFontFace(scope, font.getFontName());
					chartoutput.setTitleFontSize(scope, font.getSize());
					chartoutput.setTitleFontStyle(scope, font.getStyle());
				}
			}
		}

		face = getFacet(TICKFONTSIZE);
		if (face != null) {
			chartoutput.setTickFontSize(scope, Cast.asInt(scope, face.value(scope)).intValue());
		}
		face = getFacet(LABELFONTSIZE);
		if (face != null) {
			chartoutput.setLabelFontSize(scope, Cast.asInt(scope, face.value(scope)).intValue());
		}
		face = getFacet(LEGENDFONTSIZE);
		if (face != null) {
			chartoutput.setLegendFontSize(scope, Cast.asInt(scope, face.value(scope)).intValue());
		}
		face = getFacet(TITLEFONTSIZE);
		if (face != null) {
			chartoutput.setTitleFontSize(scope, Cast.asInt(scope, face.value(scope)).intValue());
		}
		face = getFacet(TICKFONTSTYLE);
		if (face != null) {
			chartoutput.setTickFontStyle(scope, toFontStyle(getLiteral(TICKFONTSTYLE)));
		}
		face = getFacet(LABELFONTSTYLE);
		if (face != null) {
			chartoutput.setLabelFontStyle(scope, toFontStyle(getLiteral(LABELFONTSTYLE)));
		}
		face = getFacet(LEGENDFONTSTYLE);
		if (face != null) {
			chartoutput.setLegendFontStyle(scope, toFontStyle(getLiteral(LEGENDFONTSTYLE)));
		}
		face = getFacet(TITLEFONTSTYLE);
		if (face != null) {
			chartoutput.setTitleFontStyle(scope, toFontStyle(getLiteral(TITLEFONTSTYLE)));
		}

		return true;
	}

	int toFontStyle(final String style) {
		if (style.equals("bold")) { return Font.BOLD; }
		if (style.equals("italic")) { return Font.ITALIC; }
		return Font.PLAIN;
	}

	@Override
	public boolean _step(final IScope scope) throws GamaRuntimeException {
		updateValues(scope);

		chartoutput.step(scope);

		return true;
	}

	@Override
	public LayerType getType(final LayeredDisplayOutput output) {
		return LayerType.CHART;
	}

	@Override
	public void dispose() {
		if (chartoutput != null) {
			chartoutput.dispose(null);
		}
		chartoutput = null;
		// chart = null;
		super.dispose();
	}

	public void saveHistory() {
		if (!getDataSet().keepsHistory()) { return; }
		final IScope scope = this.getDisplayOutput().getScope().copy("in save");
		if (scope == null) { return; }
		try {
			this.getDataSet().saveHistory(scope, this.getName() + "_cycle_" + scope.getClock().getCycle());
		} catch (final Exception e) {
			e.printStackTrace();
			return;
		} finally {
			GAMA.releaseScope(scope);
		}
	}

}
