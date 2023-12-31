/*******************************************************************************************************
 *
 * ChartLayerStatement.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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
						type = { IType.LIST, IType.FLOAT, IType.INT, IType.STRING },
						optional = true,
						doc = @doc ("change the default common x series labels (replace x value or categories) for an other value (string or numerical).")),
				@facet (
						name = IKeyword.Y_LABELS,
						type = { IType.LIST, IType.FLOAT, IType.INT, IType.STRING },
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
						values = { IKeyword.LINE, /* IKeyword.WHISKER, */ IKeyword.AREA, IKeyword.BAR, IKeyword.DOT,
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
						doc = @doc ("the tick unit for the y-axis (distance between horizontal lines and values on the left of the axis).")),
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
						name = ChartLayerStatement.LABELBACKGROUNDCOLOR,
						type = IType.COLOR,
						optional = true,
						doc = @doc ("Color of the label background (for Pie chart)")),
				@facet (
						name = ChartLayerStatement.LABELTEXTCOLOR,
						type = IType.COLOR,
						optional = true,
						doc = @doc ("Color of the label text (for Pie chart)")),
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
						name = IKeyword.TRANSPARENCY,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("the transparency level of the layer (between 0 -- opaque -- and 1 -- fully transparent)")),
				@facet (
						name = IKeyword.VISIBLE,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Defines whether this layer is visible or not")),
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
		see = { IKeyword.DISPLAY, IKeyword.AGENTS, IKeyword.EVENT, "graphics", IKeyword.GRID_LAYER,
				IKeyword.IMAGE_LAYER, IKeyword.OVERLAY, IKeyword.QUADTREE, IKeyword.SPECIES_LAYER, IKeyword.TEXT })
public class ChartLayerStatement extends AbstractLayerStatement {

	/** The Constant XRANGE. */
	public static final String XRANGE = "x_range";

	/** The Constant YRANGE. */
	public static final String YRANGE = "y_range";

	/** The Constant Y2RANGE. */
	public static final String Y2RANGE = "y2_range";

	/** The Constant XLABEL. */
	public static final String XLABEL = "x_label";

	/** The Constant YLABEL. */
	public static final String YLABEL = "y_label";

	/** The Constant Y2LABEL. */
	public static final String Y2LABEL = "y2_label";

	/** The Constant MEMORIZE. */
	public static final String MEMORIZE = "memorize";

	/** The Constant SERIES_LABEL_POSITION. */
	public static final String SERIES_LABEL_POSITION = "series_label_position";

	/** The Constant X_LOGSCALE. */
	public static final String X_LOGSCALE = "x_log_scale";

	/** The Constant Y_LOGSCALE. */
	public static final String Y_LOGSCALE = "y_log_scale";

	/** The Constant Y2_LOGSCALE. */
	public static final String Y2_LOGSCALE = "y2_log_scale";

	/** The Constant YTICKUNIT. */
	public static final String YTICKUNIT = "y_tick_unit";

	/** The Constant Y2TICKUNIT. */
	public static final String Y2TICKUNIT = "y2_tick_unit";

	/** The Constant XTICKUNIT. */
	public static final String XTICKUNIT = "x_tick_unit";

	/** The Constant XTICKLINEVISIBLE. */
	public static final String XTICKLINEVISIBLE = "x_tick_line_visible";

	/** The Constant YTICKLINEVISIBLE. */
	public static final String YTICKLINEVISIBLE = "y_tick_line_visible";

	/** The Constant TICKLINECOLOR. */
	public static final String TICKLINECOLOR = "tick_line_color";

	/** The Constant TITLEVISIBLE. */
	public static final String TITLEVISIBLE = "title_visible";

	/** The Constant XTICKVALUEVISIBLE. */
	public static final String XTICKVALUEVISIBLE = "x_tick_values_visible";

	/** The Constant YTICKVALUEVISIBLE. */
	public static final String YTICKVALUEVISIBLE = "y_tick_values_visible";

	/** The Constant TICKFONTFACE. */
	public static final String TICKFONTFACE = "tick_font";

	/** The Constant TICKFONTSIZE. */
	public static final String TICKFONTSIZE = "tick_font_size";

	/** The Constant TICKFONTSTYLE. */
	public static final String TICKFONTSTYLE = "tick_font_style";

	/** The Constant LABELTEXTCOLOR. */
	public static final String LABELTEXTCOLOR = "label_text_color";

	/** The Constant LABELBACKGROUNDCOLOR. */
	public static final String LABELBACKGROUNDCOLOR = "label_background_color";

	/** The Constant LABELFONTFACE. */
	public static final String LABELFONTFACE = "label_font";

	/** The Constant LABELFONTSIZE. */
	public static final String LABELFONTSIZE = "label_font_size";

	/** The Constant LABELFONTSTYLE. */
	public static final String LABELFONTSTYLE = "label_font_style";

	/** The Constant LEGENDFONTFACE. */
	public static final String LEGENDFONTFACE = "legend_font";

	/** The Constant LEGENDFONTSIZE. */
	public static final String LEGENDFONTSIZE = "legend_font_size";

	/** The Constant LEGENDFONTSTYLE. */
	public static final String LEGENDFONTSTYLE = "legend_font_style";

	/** The Constant TITLEFONTFACE. */
	public static final String TITLEFONTFACE = "title_font";

	/** The Constant TITLEFONTSIZE. */
	public static final String TITLEFONTSIZE = "title_font_size";

	/** The Constant TITLEFONTSTYLE. */
	public static final String TITLEFONTSTYLE = "title_font_style";

	/** The Constant CHARTDATASET. */
	public static final String CHARTDATASET = "chart_dataset_transfer";

	/**
	 * The Class DataDeclarationSequence.
	 */
	public static class DataDeclarationSequence extends AbstractStatementSequence {

		/**
		 * Instantiates a new data declaration sequence.
		 *
		 * @param desc
		 *            the desc
		 */
		public DataDeclarationSequence(final IDescription desc) {
			super(desc);
		}

		// shouldn't have to do that but I don't know how to get the "chart
		// statement" inside the "data statement" declaration otherwise...

	}

	/** The chartoutput. */
	private ChartOutput chartOutput = null;

	// private HashMap<String,Object> chartParameters=new
	// HashMap<String,Object>();

	/** The last values. */
	final Map<String, Double> lastValues = new LinkedHashMap<>();

	/** The data declaration. */
	DataDeclarationSequence dataDeclaration = new DataDeclarationSequence(getDescription());

	/**
	 * Gets the output.
	 *
	 * @return the output
	 */
	public ChartOutput getOutput() { return chartOutput; }

	/**
	 * Instantiates a new chart layer statement.
	 *
	 * @param desc
	 *            the desc
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public ChartLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> commands) {
		dataDeclaration.setChildren(commands);
	}

	/**
	 * Gets the chart.
	 *
	 * @return the chart
	 */
	public JFreeChart getChart() {
		// should be changed, used in LayerSideControls to open an editor...
		return chartOutput.getJFChart();
	}

	// What can not change at eery step
	@Override
	public boolean _init(final IScope scope) throws GamaRuntimeException {
		setName(getChartName(scope));
		lastValues.clear();

		IExpression string1 = getFacet(IKeyword.TYPE);

		chartOutput = ChartJFreeChartOutput.createChartOutput(scope, getName(), string1);

		string1 = getFacet(IKeyword.STYLE);
		if (string1 != null) { chartOutput.setStyle(scope, Cast.asString(scope, string1.value(scope))); }

		string1 = getFacet(IKeyword.REVERSE_AXIS);
		if (string1 != null) { chartOutput.setReverseAxis(scope, Cast.asBool(scope, string1.value(scope))); }
		string1 = getFacet(ChartLayerStatement.X_LOGSCALE);
		if (string1 != null) { chartOutput.setX_LogScale(scope, Cast.asBool(scope, string1.value(scope))); }
		string1 = getFacet(ChartLayerStatement.Y_LOGSCALE);
		if (string1 != null) { chartOutput.setY_LogScale(scope, Cast.asBool(scope, string1.value(scope))); }

		string1 = getFacet(ChartLayerStatement.Y2_LOGSCALE);
		if (string1 != null) { chartOutput.setY2_LogScale(scope, Cast.asBool(scope, string1.value(scope))); }

		chartOutput.createChart(scope);
		updateValues(scope);

		//
		boolean memorize = GamaPreferences.Displays.CHART_MEMORIZE.getValue();
		final IExpression face = getFacet(MEMORIZE);
		if (face != null) { memorize = Cast.asBool(scope, face.value(scope)); }

		chartOutput.initChart(scope, getName());
		final boolean isBatch = scope.getExperiment().getSpecies().isBatch();
		final boolean isPermanent = getDisplayOutput().isPermanent();
		final boolean isBatchAndPermanent = isBatch && isPermanent;
		/** The chartdataset. */
		ChartDataSet chartdataset = new ChartDataSet(memorize, isBatchAndPermanent);
		chartOutput.setChartdataset(chartdataset);
		chartOutput.initdataset();

		IExpression expr = getFacet(IKeyword.X_SERIE);
		if (expr != null) {
			final IExpression expval = getFacet(IKeyword.X_SERIE).resolveAgainst(scope);
			chartdataset.setXSource(scope, expval);
			chartOutput.setUseXSource(scope, expval);
		}

		expr = getFacet(IKeyword.X_LABELS);
		if (expr != null) {
			final IExpression expval = getFacet(IKeyword.X_LABELS).resolveAgainst(scope);
			chartdataset.setXLabels(scope, expval);
			chartOutput.setUseXLabels(scope, expval);
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
			chartOutput.setUseYLabels(scope, expval);
		}
		scope.addVarWithValue(ChartLayerStatement.CHARTDATASET, chartdataset);
		for (final IStatement s : dataDeclaration.getCommands()) { scope.execute(s); }
		chartdataset = (ChartDataSet) scope.getVarValue(ChartLayerStatement.CHARTDATASET);
		chartOutput.initChart_post_data_init(scope);
		chartOutput.updateOutput(scope);

		return true;
	}

	/**
	 * Gets the chart name.
	 *
	 * @param scope
	 *            the scope
	 * @return the chart name
	 */
	private String getChartName(final IScope scope) {
		return Cast.asString(scope, getFacetValue(scope, IKeyword.NAME));
	}

	/**
	 * Update values.
	 *
	 * @param scope
	 *            the scope
	 * @return true, if successful
	 */
	// what can be updated at each step
	public boolean updateValues(final IScope scope) {

		IExpression string1 = getFacet(ChartLayerStatement.XLABEL);
		if (string1 != null) { chartOutput.setXLabel(scope, Cast.asString(scope, string1.value(scope))); }

		string1 = getFacet(ChartLayerStatement.YLABEL);
		if (string1 != null) { chartOutput.setYLabel(scope, Cast.asString(scope, string1.value(scope))); }

		string1 = getFacet(ChartLayerStatement.Y2LABEL);
		if (string1 != null) { chartOutput.setY2Label(scope, Cast.asString(scope, string1.value(scope))); }

		string1 = getFacet(ChartLayerStatement.SERIES_LABEL_POSITION);
		if (string1 != null) { chartOutput.setSeriesLabelPosition(scope, Cast.asString(scope, string1.value(scope))); }

		IExpression expr = getFacet(XRANGE);
		if (expr != null) {
			final Object range = expr.value(scope);

			if (range instanceof Number) {
				chartOutput.setXRangeInterval(scope, ((Number) range).doubleValue());
			} else if (range instanceof GamaPoint) {
				chartOutput.setXRangeMinMax(scope, ((GamaPoint) range).getX(), ((GamaPoint) range).getY());
			} else if (range instanceof IList) {
				chartOutput.setXRangeMinMax(scope, Cast.asFloat(scope, ((IList<?>) range).get(0)),
						Cast.asFloat(scope, ((IList<?>) range).get(1)));
			}
		}

		expr = getFacet(YRANGE);
		if (expr != null) {
			final Object range = expr.value(scope);

			if (range instanceof Number) {
				chartOutput.setYRangeInterval(scope, ((Number) range).doubleValue());
			} else if (range instanceof GamaPoint) {
				chartOutput.setYRangeMinMax(scope, ((GamaPoint) range).getX(), ((GamaPoint) range).getY());
			} else if (range instanceof IList) {
				chartOutput.setYRangeMinMax(scope, Cast.asFloat(scope, ((IList<?>) range).get(0)),
						Cast.asFloat(scope, ((IList<?>) range).get(1)));
			}
		}
		expr = getFacet(Y2RANGE);
		if (expr != null) {
			final Object range = expr.value(scope);

			if (range instanceof Number) {
				chartOutput.setY2RangeInterval(scope, ((Number) range).doubleValue());
			} else if (range instanceof GamaPoint) {
				chartOutput.setY2RangeMinMax(scope, ((GamaPoint) range).getX(), ((GamaPoint) range).getY());
			} else if (range instanceof IList) {
				chartOutput.setY2RangeMinMax(scope, Cast.asFloat(scope, ((IList<?>) range).get(0)),
						Cast.asFloat(scope, ((IList<?>) range).get(1)));
			}
		}
		IExpression expr2 = getFacet(XTICKUNIT);
		if (expr2 != null) {
			final Object range = expr2.value(scope);

			if (range instanceof Number) {
				final double r = ((Number) range).doubleValue();
				chartOutput.setXTickUnit(scope, r);
			}
		}

		expr2 = getFacet(YTICKUNIT);
		if (expr2 != null) {
			final Object range = expr2.value(scope);

			if (range instanceof Number) {
				final double r = ((Number) range).doubleValue();
				chartOutput.setYTickUnit(scope, r);
			}
		}
		expr2 = getFacet(Y2TICKUNIT);
		if (expr2 != null) {
			final Object range = expr2.value(scope);

			if (range instanceof Number) {
				final double r = ((Number) range).doubleValue();
				chartOutput.setY2TickUnit(scope, r);
			}
		}
		expr2 = getFacet(IKeyword.GAP);
		if (expr2 != null) {
			final Double range = Cast.asFloat(scope, expr2.value(scope));
			chartOutput.setGap(scope, range);
		}
		// ((BarRenderer) plot.getRenderer()).setItemMargin(gap);

		GamaColor colorvalue = GamaColor.get(Color.black);
		IExpression color = getFacet(IKeyword.AXES);
		if (color != null) { colorvalue = Cast.asColor(scope, color.value(scope)); }
		chartOutput.setAxesColorValue(scope, colorvalue);

		colorvalue = GamaColor.get(Color.black);
		color = getFacet(ChartLayerStatement.TICKLINECOLOR);
		if (color != null) { colorvalue = Cast.asColor(scope, color.value(scope)); }
		chartOutput.setTickColorValue(scope, colorvalue);

		string1 = getFacet(ChartLayerStatement.XTICKVALUEVISIBLE);
		if (string1 != null) { chartOutput.setXTickValueVisible(scope, Cast.asBool(scope, string1.value(scope))); }
		string1 = getFacet(ChartLayerStatement.YTICKVALUEVISIBLE);
		if (string1 != null) { chartOutput.setYTickValueVisible(scope, Cast.asBool(scope, string1.value(scope))); }
		string1 = getFacet(ChartLayerStatement.TITLEVISIBLE);
		if (string1 != null) { chartOutput.setTitleVisible(scope, Cast.asBool(scope, string1.value(scope))); }

		string1 = getFacet(ChartLayerStatement.XTICKLINEVISIBLE);
		if (string1 != null) { chartOutput.setXTickLineVisible(scope, Cast.asBool(scope, string1.value(scope))); }
		string1 = getFacet(ChartLayerStatement.YTICKLINEVISIBLE);
		if (string1 != null) { chartOutput.setYTickLineVisible(scope, Cast.asBool(scope, string1.value(scope))); }
		color = getFacet(IKeyword.COLOR);
		if (color != null) { colorvalue = Cast.asColor(scope, color.value(scope)); }
		chartOutput.setColorValue(scope, colorvalue);
		colorvalue = GamaColor.get(Color.white);
		color = getFacet(IKeyword.BACKGROUND);
		if (color != null) { colorvalue = Cast.asColor(scope, color.value(scope)); }
		chartOutput.setBackgroundColorValue(scope, colorvalue);

		color = getFacet(LABELTEXTCOLOR);
		if (color != null) {
			colorvalue = Cast.asColor(scope, color.value(scope));
			chartOutput.setLabelTextColorValue(scope, colorvalue);
		}

		color = getFacet(LABELBACKGROUNDCOLOR);
		if (color != null) {
			colorvalue = Cast.asColor(scope, color.value(scope));
			chartOutput.setLabelBackgroundColorValue(scope, colorvalue);
		}

		color = getFacet(IKeyword.BACKGROUND);
		if (color != null) {
			colorvalue = Cast.asColor(scope, color.value(scope));
			chartOutput.setBackgroundColorValue(scope, colorvalue);
		}
		GamaFont font = null;
		IExpression face = getFacet(TICKFONTFACE);
		if (face != null) {
			if (face.getGamlType() == Types.STRING) {
				chartOutput.setTickFontFace(scope, Cast.asString(scope, face.value(scope)));
			} else {
				font = (GamaFont) Types.FONT.cast(scope, face.value(scope), null, false);
				if (font != null) {
					chartOutput.setTickFontFace(scope, font.getFontName());
					chartOutput.setTickFontSize(scope, font.getSize());
					chartOutput.setTickFontStyle(scope, font.getStyle());
				}
			}
		}

		face = getFacet(LABELFONTFACE);
		if (face != null) {
			if (face.getGamlType() == Types.STRING) {
				chartOutput.setLabelFontFace(scope, Cast.asString(scope, face.value(scope)));
			} else {
				font = (GamaFont) Types.FONT.cast(scope, face.value(scope), null, false);
				if (font != null) {
					chartOutput.setLabelFontFace(scope, font.getFontName());
					chartOutput.setLabelFontSize(scope, font.getSize());
					chartOutput.setLabelFontStyle(scope, font.getStyle());
				}
			}
		}

		face = getFacet(LEGENDFONTFACE);
		if (face != null) {
			if (face.getGamlType() == Types.STRING) {
				chartOutput.setLegendFontFace(scope, Cast.asString(scope, face.value(scope)));
			} else {
				font = (GamaFont) Types.FONT.cast(scope, face.value(scope), null, false);
				if (font != null) {
					chartOutput.setLegendFontFace(scope, font.getFontName());
					chartOutput.setLegendFontSize(scope, font.getSize());
					chartOutput.setLegendFontStyle(scope, font.getStyle());
				}
			}
		}

		face = getFacet(TITLEFONTFACE);
		if (face != null) {
			if (face.getGamlType() == Types.STRING) {
				chartOutput.setTitleFontFace(scope, Cast.asString(scope, face.value(scope)));
			} else {
				font = (GamaFont) Types.FONT.cast(scope, face.value(scope), null, false);
				if (font != null) {
					chartOutput.setTitleFontFace(scope, font.getFontName());
					chartOutput.setTitleFontSize(scope, font.getSize());
					chartOutput.setTitleFontStyle(scope, font.getStyle());
				}
			}
		}

		face = getFacet(TICKFONTSIZE);
		if (face != null) { chartOutput.setTickFontSize(scope, Cast.asInt(scope, face.value(scope))); }
		face = getFacet(LABELFONTSIZE);
		if (face != null) { chartOutput.setLabelFontSize(scope, Cast.asInt(scope, face.value(scope))); }
		face = getFacet(LEGENDFONTSIZE);
		if (face != null) { chartOutput.setLegendFontSize(scope, Cast.asInt(scope, face.value(scope))); }
		face = getFacet(TITLEFONTSIZE);
		if (face != null) { chartOutput.setTitleFontSize(scope, Cast.asInt(scope, face.value(scope))); }
		face = getFacet(TICKFONTSTYLE);
		if (face != null) { chartOutput.setTickFontStyle(scope, toFontStyle(getLiteral(TICKFONTSTYLE))); }
		face = getFacet(LABELFONTSTYLE);
		if (face != null) { chartOutput.setLabelFontStyle(scope, toFontStyle(getLiteral(LABELFONTSTYLE))); }
		face = getFacet(LEGENDFONTSTYLE);
		if (face != null) { chartOutput.setLegendFontStyle(scope, toFontStyle(getLiteral(LEGENDFONTSTYLE))); }
		face = getFacet(TITLEFONTSTYLE);
		if (face != null) { chartOutput.setTitleFontStyle(scope, toFontStyle(getLiteral(TITLEFONTSTYLE))); }

		return true;
	}

	/**
	 * To font style.
	 *
	 * @param style
	 *            the style
	 * @return the int
	 */
	int toFontStyle(final String style) {
		if ("bold".equals(style)) return Font.BOLD;
		if ("italic".equals(style)) return Font.ITALIC;
		return Font.PLAIN;
	}

	@Override
	public boolean _step(final IScope scope) throws GamaRuntimeException {
		updateValues(scope);

		chartOutput.step(scope);

		return true;
	}

	@Override
	public LayerType getType(final LayeredDisplayOutput output) {
		return LayerType.CHART;
	}

	@Override
	public void dispose() {
		if (chartOutput != null) { chartOutput.dispose(null); }
		chartOutput = null;
		// chart = null;
		super.dispose();
	}

	/**
	 * Save history.
	 */
	public void saveHistory() {
		if (!chartOutput.getChartdataset().keepsHistory()) return;
		final IScope scope = getDisplayOutput().getScope().copy("in save");
		if (scope == null) return;
		try {
			chartOutput.getChartdataset().saveHistory(scope, getName() + "_cycle_" + scope.getClock().getCycle());
		} catch (final Exception e) {
			e.printStackTrace();
			return;
		} finally {
			GAMA.releaseScope(scope);
		}
	}

	/**
	 * Keeps history.
	 *
	 * @return true, if successful
	 */
	public boolean keepsHistory() {
		return chartOutput.getChartdataset().keepHistory;
	}

}
