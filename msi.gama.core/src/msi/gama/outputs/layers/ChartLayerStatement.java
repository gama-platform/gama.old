/*********************************************************************************************
 *
 *
 * 'ChartLayerStatement.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.outputs.layers;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.*;
import org.jfree.data.statistics.*;
import org.jfree.data.xy.*;
import org.jfree.ui.RectangleInsets;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.FileUtils;
import msi.gama.kernel.experiment.BatchAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.layers.ChartDataListStatement.ChartDataList;
import msi.gama.outputs.layers.ChartDataStatement.ChartData;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.*;
import msi.gaml.operators.Random;
import msi.gaml.operators.fastmaths.CmnFastMath;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 9 nov. 2009
 *
 * @todo Description
 *
 */
@symbol(name = IKeyword.CHART, kind = ISymbolKind.LAYER, with_sequence = true)
@inside(symbols = IKeyword.DISPLAY)
@facets(value = {
	/* @facet(name = ISymbol.VALUE, type = TypeManager.STRING, optional = true), */
	@facet(name = ChartLayerStatement.XRANGE,
		type = { IType.FLOAT, IType.INT, IType.POINT },
		optional = true,
		doc = @doc("range of the x-axis. Can be a number (which will set the axis total range) or a point (which will set the min and max of the axis).")),
	@facet(name = ChartLayerStatement.YRANGE,
		type = { IType.FLOAT, IType.INT, IType.POINT },
		optional = true,
		doc = @doc("range of the y-axis. Can be a number (which will set the axis total range) or a point (which will set the min and max of the axis).")),
	@facet(name = IKeyword.POSITION,
		type = IType.POINT,
		optional = true,
		doc = @doc("position of the upper-left corner of the layer. Note that if coordinates are in [0,1[, the position is relative to the size of the environment (e.g. {0.5,0.5} refers to the middle of the display) whereas it is absolute when coordinates are greter than 1. The position can only be a 3D point {0.5, 0.5, 0.5}, the last coordinate specifying the elevation of the layer.")),
	@facet(name = IKeyword.SIZE,
		type = IType.POINT,
		optional = true,
		doc = @doc("extent of the layer in the screen from its position. Coordinates in [0,1[ are treated as percentages of the total surface, while coordinates > 1 are treated as absolute sizes in model units (i.e. considering the model occupies the entire view). Like in 'position', an elevation can be provided with the z coordinate, allowing to scale the layer in the 3 directions ")),
	@facet(name = IKeyword.BACKGROUND, type = IType.COLOR, optional = true, doc = @doc("the background color")),
	@facet(name = IKeyword.TIMEXSERIES,
		type = IType.LIST,
		optional = true,
		doc = @doc("for series charts, change the default time serie (simulation cycle) for an other value.")),
	@facet(name = IKeyword.AXES, type = IType.COLOR, optional = true, doc = @doc("the axis color")),
	@facet(name = IKeyword.TYPE,
		type = IType.ID,
		values = { IKeyword.XY, IKeyword.SCATTER, IKeyword.HISTOGRAM, IKeyword.SERIES, IKeyword.PIE,
			IKeyword.BOX_WHISKER },
		optional = true,
		doc = @doc("the type of chart. It could be histogram, series, xy, pie or box whisker. The difference between series and xy is that the former adds an implicit x-axis that refers to the numbers of cycles, while the latter considers the first declaration of data to be its x-axis.")),
	@facet(name = IKeyword.STYLE,
		type = IType.ID,
		values = { IKeyword.EXPLODED, IKeyword.THREE_D, IKeyword.STACK, IKeyword.BAR },
		optional = true),
	@facet(name = IKeyword.TRANSPARENCY, type = IType.FLOAT, optional = true, doc = @doc("the style of the chart")),
	@facet(name = IKeyword.GAP, type = IType.FLOAT, optional = true),
	@facet(name = ChartLayerStatement.YTICKUNIT,
		type = IType.FLOAT,
		optional = true,
		doc = @doc("the tick unit for the x-axis (distance between vertical lines and values bellow the axis).")),
	@facet(name = ChartLayerStatement.XTICKUNIT,
		type = IType.FLOAT,
		optional = true,
		doc = @doc("the tick unit for the y-axis (distance between horyzontal lines and values on the left of the axis).")),
	@facet(name = IKeyword.NAME,
		type = IType.LABEL,
		optional = true,
		doc = @doc("the human readable title of the chart layer")),
	@facet(name = IKeyword.COLOR, type = IType.COLOR, optional = true),
	@facet(name = ChartLayerStatement.TICKFONTFACE, type = IType.STRING, optional = true),
	@facet(name = ChartLayerStatement.TICKFONTSIZE, type = IType.INT, optional = true),
	@facet(name = ChartLayerStatement.TICKFONTSTYLE,
		type = IType.ID,
		values = { "plain", "bold", "italic" },
		optional = true,
		doc = @doc("the style used to display ticks")),
	@facet(name = ChartLayerStatement.LABELFONTFACE, type = IType.STRING, optional = true),
	@facet(name = ChartLayerStatement.LABELFONTSIZE, type = IType.INT, optional = true),
	@facet(name = ChartLayerStatement.LABELFONTSTYLE,
		type = IType.ID,
		values = { "plain", "bold", "italic" },
		optional = true,
		doc = @doc("the style used to display labels")),
	@facet(name = ChartLayerStatement.LEGENDFONTFACE, type = IType.STRING, optional = true),
	@facet(name = ChartLayerStatement.LEGENDFONTSIZE, type = IType.INT, optional = true),
	@facet(name = ChartLayerStatement.LEGENDFONTSTYLE,
		type = IType.ID,
		values = { "plain", "bold", "italic" },
		optional = true,
		doc = @doc("the style used to display legend")),
	@facet(name = ChartLayerStatement.TITLEFONTFACE, type = IType.STRING, optional = true),
	@facet(name = ChartLayerStatement.TITLEFONTSIZE, type = IType.INT, optional = true),
	@facet(name = ChartLayerStatement.TITLEFONTSTYLE,
		type = IType.ID,
		values = { "plain", "bold", "italic" },
		optional = true,
		doc = @doc("the style used to display titles")), },

	omissible = IKeyword.NAME)
@doc(
	value = "`" + IKeyword.CHART +
		"` allows modeler to display a chart: this enables to display specific values of the model at each iteration. GAMA can display various chart types: time series (series), pie charts (pie) and histograms (histogram).",
	usages = { @usage(value = "The general syntax is:",
		examples = { @example(value = "display chart_display {", isExecutable = false),
			@example(value = "   chart \"chart name\" type: series [additional options] {", isExecutable = false),
			@example(value = "      [Set of data, datalists statements]", isExecutable = false),
			@example(value = "   }", isExecutable = false), @example(value = "}", isExecutable = false) }) },
	see = { IKeyword.DISPLAY, IKeyword.AGENTS, IKeyword.EVENT, "graphics", IKeyword.GRID_POPULATION, IKeyword.IMAGE,
		IKeyword.OVERLAY, IKeyword.QUADTREE, IKeyword.POPULATION, IKeyword.TEXT })
public class ChartLayerStatement extends AbstractLayerStatement {

	public static final String XRANGE = "x_range";
	public static final String YRANGE = "y_range";

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

	public class DataDeclarationSequence extends AbstractStatementSequence {

		public DataDeclarationSequence(final IDescription desc) {
			super(desc);
		}

		// We create the variable in which the datas will be accumulated
		@Override
		public void enterScope(final IScope scope) {
			super.enterScope(scope);
			scope.addVarWithValue(ChartDataStatement.DATAS, new ArrayList());
			scope.addVarWithValue(ChartDataListStatement.DATALISTS, new ArrayList());
		}

		// We save the datas once the computation is finished
		@Override
		public void leaveScope(final IScope scope) {
			datas = (List<ChartData>) scope.getVarValue(ChartDataStatement.DATAS);
			datalists = (List<ChartDataList>) scope.getVarValue(ChartDataListStatement.DATALISTS);
			super.leaveScope(scope);
		}

	}

	private static final int SERIES_CHART = 0;
	private static final int HISTOGRAM_CHART = 1;
	private static final int PIE_CHART = 2;
	private static final int XY_CHART = 3;
	private static final int BOX_WHISKER_CHART = 4;
	private static final int SCATTER_CHART = 5;

	private int type = SERIES_CHART;
	private String style = IKeyword.DEFAULT;
	private JFreeChart chart = null;
	private StringBuilder history;
	private static String chartFolder = "charts";
	private String tickFontFace = Font.SANS_SERIF;
	private int tickFontSize = 10;
	private int tickFontStyle = Font.PLAIN;
	private String labelFontFace = Font.SANS_SERIF;
	private int labelFontSize = 12;
	private int labelFontStyle = Font.BOLD;
	private String legendFontFace = Font.SANS_SERIF;
	private int legendFontSize = 10;
	private int legendFontStyle = Font.ITALIC;
	private String titleFontFace = Font.SERIF;
	private int titleFontSize = 14;
	private int titleFontStyle = Font.BOLD;
	private GamaColor backgroundColor = null, axesColor = null;
	private final Map<String, Integer> expressions_index = new HashMap();
	private Dataset dataset;
	private boolean exploded;
	static String xAxisName = "'time'";
	List<ChartData> datas;
	List<ChartData> datasfromlists;
	List<ChartDataList> datalists;
	final Map<String, Double> lastValues;
	Long lastComputeCycle;
	ChartDataStatement timeSeriesXData = null;
	DataDeclarationSequence dataDeclaration = new DataDeclarationSequence(null);

	public JFreeChart getChart() {
		return chart;
	}

	public ChartLayerStatement(/* final ISymbol context, */final IDescription desc) throws GamaRuntimeException {
		super(desc);
		axesColor = new GamaColor(Color.black);
		lastValues = new LinkedHashMap();
		lastComputeCycle = 0l;
	}

	Font getLabelFont() {
		return new Font(labelFontFace, labelFontStyle, labelFontSize);
	}

	Font getTickFont() {
		return new Font(tickFontFace, tickFontStyle, tickFontSize);
	}

	Font getLegendFont() {
		return new Font(legendFontFace, legendFontStyle, legendFontSize);
	}

	Font getTitleFont() {
		return new Font(titleFontFace, titleFontStyle, titleFontSize);
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		dataDeclaration.setChildren(commands);
	}

	void createSeries(final IScope scope, final boolean isTimeSeries) throws GamaRuntimeException {
		final XYPlot plot = (XYPlot) chart.getPlot();
		final NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		domainAxis.setTickLabelFont(getTickFont());
		domainAxis.setLabelFont(getLabelFont());
		if ( isTimeSeries ) {
			domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			if ( timeSeriesXData == null ) {
				timeSeriesXData = (ChartDataStatement) DescriptionFactory.create(IKeyword.DATA, description,
					IKeyword.LEGEND, xAxisName, IKeyword.VALUE, SimulationAgent.CYCLE).compile();
				if ( getFacet(IKeyword.TIMEXSERIES) != null ) {
					timeSeriesXData.getDescription().getFacets().get(IKeyword.VALUE)
						.setExpression(getFacet(IKeyword.TIMEXSERIES));
				}
			}

			// FIXME: datas can NOT contain timeSeriesXData (a ChartDataStatement and not a ChartData)
			if ( !datas.contains(timeSeriesXData) ) {
				datas.add(0, timeSeriesXData.createData(scope));
			}
		}
		IExpression expr = getFacet(XRANGE);
		IExpression expr2 = getFacet(XTICKUNIT);
		if ( expr != null ) {
			Object range = expr.value(scope);
			// Double range = Cast.asFloat(scope, expr.value(scope));

			if ( range instanceof Number ) {
				double r = ((Number) range).doubleValue();
				if ( r > 0 ) {
					domainAxis.setFixedAutoRange(r);
					domainAxis.setAutoRangeMinimumSize(r);
				}
				domainAxis.setAutoRangeIncludesZero(false);
			} else if ( range instanceof GamaPoint ) {
				domainAxis.setRange(((GamaPoint) range).getX(), ((GamaPoint) range).getY());
			}
		}
		if ( expr2 != null ) {
			Object range = expr2.value(scope);
			// Double range = Cast.asFloat(scope, expr.value(scope));

			if ( range instanceof Number ) {
				double r = ((Number) range).doubleValue();
				if ( r > 0 ) {
					domainAxis.setTickUnit(new NumberTickUnit(r));
				}
			}
		}
		if ( datas.size() > 0 ) {
			domainAxis.setLabel(datas.get(0).getName());
		}
		final NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
		yAxis.setTickLabelFont(getTickFont());
		yAxis.setLabelFont(getLabelFont());
		expr = getFacet(YRANGE);
		expr2 = getFacet(YTICKUNIT);
		if ( expr != null ) {
			Object range = expr.value(scope);
			// Double range = Cast.asFloat(scope, expr.value(scope));

			if ( range instanceof Number ) {
				double r = ((Number) range).doubleValue();
				if ( r > 0 ) {
					yAxis.setFixedAutoRange(r);
					yAxis.setAutoRangeMinimumSize(r);
				}
				yAxis.setAutoRangeIncludesZero(false);
			} else if ( range instanceof GamaPoint ) {
				yAxis.setRange(((GamaPoint) range).getX(), ((GamaPoint) range).getY());
			}
		}
		if ( expr2 != null ) {
			Object range = expr2.value(scope);
			// Double range = Cast.asFloat(scope, expr.value(scope));

			if ( range instanceof Number ) {
				double r = ((Number) range).doubleValue();
				if ( r > 0 ) {
					yAxis.setTickUnit(new NumberTickUnit(r));
				}
			}
		}
		if ( datas.size() == 2 ) {
			yAxis.setLabel(datas.get(1).getName());
			chart.removeLegend();
		}
		final LegendTitle ll = chart.getLegend();
		if ( ll != null ) {
			ll.setItemFont(getLegendFont());
		}

		for ( int i = 0; i < datas.size(); i++ ) {
			ChartData e = datas.get(i);

			final String legend = e.getName();
			if ( i != 0 | !isTimeSeries ) { // the first data is the domain

				XYDataset data = plot.getDataset(i);
				XYSeries serie = new XYSeries(0, false, false);
				if ( type == SERIES_CHART || type == XY_CHART ) {
					dataset = new DefaultTableXYDataset();
					// final XYSeries nserie = new XYSeries(serie.getKey(), false, false);
					final XYSeries nserie = new XYSeries(e.getName(), false, false);
					((DefaultTableXYDataset) dataset).addSeries(nserie);
				}
				if ( type == SCATTER_CHART ) {
					dataset = new XYSeriesCollection();
					final XYSeries nserie = new XYSeries(e.getName(), false, true);
					// final XYSeries nserie = new XYSeries(serie.getKey(), false, true);
					((XYSeriesCollection) dataset).addSeries(nserie);
				}

				// dataset = new DefaultTableXYDataset();

				// final XYSeries serie = new XYSeries(legend, false, false);
				// final XYSeries serie = new XYSeries(legend, false, true);
				// ((DefaultTableXYDataset) dataset).addSeries(serie);
				expressions_index.put(legend, i);
				plot.setRenderer(i, (XYItemRenderer) e.getRenderer(), false);
				// final Color c = e.getColor();
				// ((XYLineAndShapeRenderer) plot.getRenderer(i)).setSeriesPaint(0, c);
				// TODO Control this with a facet
				// ((XYLineAndShapeRenderer) plot.getRenderer(i)).setBaseShapesFilled(false);
				// TODO Control this with a facet
				// ((XYLineAndShapeRenderer) plot.getRenderer(i)).setSeriesShapesVisible(0, false);
				// if (type==SERIES_CHART)
				// plot.setDataset(i-1, (DefaultTableXYDataset) dataset);
				// else
				plot.setDataset(i, (XYDataset) dataset);
			}
			history.append(legend);
			history.append(',');

		}
		if ( history.length() > 0 ) {
			history.deleteCharAt(history.length() - 1);
		}
		history.append(Strings.LN);

	}

	/**
	 * create dataset for box_whisker chart
	 * @return A sample dataset.
	 */
	private BoxAndWhiskerCategoryDataset createWhisker(final IScope scope) {

		final CategoryPlot plot = (CategoryPlot) chart.getPlot();
		// final int seriesCount = 1;
		final int categoryCount = 3;
		final int entityCount = 2;

		final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
		for ( int i = 0; i < datas.size(); i++ ) {
			// ChartData e = datas.get(i);
			for ( int j = 0; j < categoryCount; j++ ) {
				final List list = new ArrayList();
				// add some values...
				for ( int k = 0; k < entityCount; k++ ) {
					// list.add(new Double(k*2));
					// list.add(new Double(k*3));
					final double value1 = 10.0 + scope.getRandom().next() * 3;
					list.add(new Double(value1));
					final double value2 = 11.25 + scope.getRandom().next(); // concentrate values in the middle
					list.add(new Double(value2));
				}
				dataset.add(list, "Series " + i, " Type " + j);

				history.append("Series " + i);
				history.append(',');
			}
		}
		history.deleteCharAt(history.length() - 1);
		history.append(Strings.LN);
		plot.setDataset(dataset);
		chart.removeLegend();
		final CategoryAxis axis = plot.getDomainAxis();
		axis.setTickLabelFont(getTickFont());
		axis.setLabelFont(getLabelFont());
		// ((BarRenderer3D) plot.getRenderer()).setItemMargin(0.1);
		axis.setCategoryMargin(0.1);
		axis.setUpperMargin(0.05);
		axis.setLowerMargin(0.05);
		return dataset;
	}

	private void createData(final IScope scope) throws GamaRuntimeException {
		// Normally initialize the datas
		dataDeclaration.executeOn(scope);
		switch (type) {
			case SERIES_CHART: {
				createSeries(scope, true);
				break;
			}
			case PIE_CHART: {
				createSlices(scope);
				break;
			}
			case BOX_WHISKER_CHART: {
				createWhisker(scope);
				break;
			}
			case HISTOGRAM_CHART: {
				createBars(scope);
				break;
			}
			case SCATTER_CHART: {
				createSeries(scope, false);
				break;
			}
			case XY_CHART:
				createSeries(scope, false);
				break;
		}
	}

	private void createSlices(final IScope scope) throws GamaRuntimeException {
		int i = 0;
		dataset = new DefaultPieDataset();
		final PiePlot plot = (PiePlot) chart.getPlot();
		for ( final ChartData e : datas ) {
			final String legend = e.getName();
			((DefaultPieDataset) dataset).insertValue(i++, legend, null);
			history.append(legend);
			history.append(',');
		}
		if ( history.length() > 0 ) {
			history.deleteCharAt(history.length() - 1);
		}
		history.append(Strings.LN);
		plot.setDataset((DefaultPieDataset) dataset);
		i = 0;
		for ( final ChartData e : datas ) {
			plot.setSectionPaint(i++, e.getColor());
		}
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} = {1} ({2})"));
		if ( exploded ) {
			for ( final Object c : ((DefaultPieDataset) dataset).getKeys() ) {
				plot.setExplodePercent((Comparable) c, 0.20);
			}
		}
		plot.setSectionOutlinesVisible(false);
		plot.setLabelFont(getLabelFont());
		plot.setNoDataMessage("No data available yet");
		plot.setCircular(true);
		plot.setLabelGap(0.02);
		plot.setInteriorGap(0);
	}

	class CustomRenderer extends BarRenderer {

		public CustomRenderer() {}

		@Override
		public Paint getItemPaint(final int row, final int column) {
			return datas.get(column).getColor();
		}
	}

	private void createBars(final IScope scope) {
		final CategoryPlot plot = (CategoryPlot) chart.getPlot();
		BarRenderer renderer = new CustomRenderer();
		plot.setRenderer(renderer);

		dataset = new DefaultCategoryDataset();
		int i = 0;
		for ( final ChartData e : datas ) {
			// String legend = e.getName();
			// ((DefaultCategoryDataset) dataset).setValue(0d, new Integer(0), legend/* , legend */);

			final String legend = e.getName();
			if ( !CategoryItemRenderer.class.isInstance(e.getRenderer()) ) {
				e.renderer = new CustomRenderer();
			}
			plot.setRenderer(i, (CategoryItemRenderer) e.getRenderer(), false);
			final Color c = e.getColor();
			plot.getRenderer(i).setSeriesPaint(i, c);
			// plot.setDataset(i, (DefaultCategoryDataset) dataset);
			i++;
			history.append(legend);
			history.append(',');

		}
		if ( history.length() > 0 ) {
			history.deleteCharAt(history.length() - 1);
		}
		history.append(Strings.LN);
		plot.setDataset((DefaultCategoryDataset) dataset);

		chart.removeLegend();
		final NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
		yAxis.setTickLabelFont(getTickFont());
		yAxis.setLabelFont(getLabelFont());
		IExpression expr = getFacet(YRANGE);
		IExpression expr2 = getFacet(YTICKUNIT);
		if ( expr != null ) {
			Object range = expr.value(scope);
			// Double range = Cast.asFloat(scope, expr.value(scope));

			if ( range instanceof Number ) {
				double r = ((Number) range).doubleValue();
				if ( r > 0 ) {
					yAxis.setFixedAutoRange(r);
					yAxis.setAutoRangeMinimumSize(r);
				}
				// yAxis.setAutoRangeIncludesZero(false);
			} else if ( range instanceof GamaPoint ) {
				yAxis.setRange(((GamaPoint) range).getX(), ((GamaPoint) range).getY());
			}
		}
		if ( expr2 != null ) {
			Object range = expr2.value(scope);
			// Double range = Cast.asFloat(scope, expr.value(scope));

			if ( range instanceof Number ) {
				double r = ((Number) range).doubleValue();
				if ( r > 0 ) {
					yAxis.setTickUnit(new NumberTickUnit(r));
				}
			}
		}

		final CategoryAxis axis = plot.getDomainAxis();
		Double gap = Cast.asFloat(scope, getFacetValue(scope, IKeyword.GAP, 0.01));
		// ((BarRenderer) plot.getRenderer()).setItemMargin(gap);
		renderer.setMaximumBarWidth(1 - gap);
		axis.setCategoryMargin(gap);
		axis.setUpperMargin(gap);
		axis.setLowerMargin(gap);

	}

	private void createChart(final IScope scope) {
		switch (type) {
			case SERIES_CHART: {
				chart = ChartFactory.createXYLineChart(getName(), "time", "", null, PlotOrientation.VERTICAL, true,
					false, false);
				break;
			}
			case PIE_CHART: {
				if ( style.equals(IKeyword.THREE_D) ) {
					chart = ChartFactory.createPieChart3D(getName(), null, false, true, false);
				} else if ( style.equals(IKeyword.RING) ) {
					chart = ChartFactory.createRingChart(getName(), null, false, true, false);
				} else if ( style.equals(IKeyword.EXPLODED) ) {
					chart = ChartFactory.createPieChart(getName(), null, false, true, false);
					exploded = true;
				} else {
					chart = ChartFactory.createPieChart(getName(), null, false, true, false);
				}
				break;
			}
			case HISTOGRAM_CHART: {
				if ( style.equals(IKeyword.THREE_D) ) {
					chart = ChartFactory.createBarChart3D(getName(), null, null, null, PlotOrientation.VERTICAL, true,
						true, false);
				} else if ( style.equals(IKeyword.STACK) ) {
					chart = ChartFactory.createStackedBarChart(getName(), null, null, null, PlotOrientation.VERTICAL,
						true, true, false);
				} else {
					chart = ChartFactory.createBarChart(getName(), null, null, null, PlotOrientation.VERTICAL, true,
						true, false);
				}
				break;
			}
			case XY_CHART:
				chart = ChartFactory.createXYLineChart(getName(), "", "", null, PlotOrientation.VERTICAL, true, false,
					false);
				break;
			case SCATTER_CHART:
				chart = ChartFactory.createXYLineChart(getName(), "", "", null, PlotOrientation.VERTICAL, true, false,
					false);
				break;
			case BOX_WHISKER_CHART: {
				chart = ChartFactory.createBoxAndWhiskerChart(getName(), "Time", "Value",
					(BoxAndWhiskerCategoryDataset) dataset, true);
				chart.setBackgroundPaint(new Color(249, 231, 236));

				break;
			}
		}
		Plot plot = chart.getPlot();
		chart.getTitle().setFont(getTitleFont());
		if ( backgroundColor == null ) {
			plot.setBackgroundPaint(null);
			chart.setBackgroundPaint(null);
			chart.setBorderPaint(null);
			if ( chart.getLegend() != null ) {
				chart.getLegend().setBackgroundPaint(null);
			}
		} else {
			Color bg = backgroundColor;
			chart.setBackgroundPaint(bg);
			plot.setBackgroundPaint(bg);
			chart.setBorderPaint(bg);
			if ( chart.getLegend() != null ) {
				chart.getLegend().setBackgroundPaint(bg);
			}
		}
		// chart.getLegend().setItemPaint(axesColor);
		// chart.getLegend().setBackgroundPaint(null);

		if ( plot instanceof CategoryPlot ) {
			final CategoryPlot pp = (CategoryPlot) chart.getPlot();
			pp.setDomainGridlinePaint(axesColor);
			pp.setRangeGridlinePaint(axesColor);
			// plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
			// plot.setDomainCrosshairVisible(true);
			pp.setRangeCrosshairVisible(true);
		} else if ( plot instanceof XYPlot ) {
			final XYPlot pp = (XYPlot) chart.getPlot();
			pp.setDomainGridlinePaint(axesColor);
			pp.setRangeGridlinePaint(axesColor);
			pp.setDomainCrosshairPaint(axesColor);
			pp.setRangeCrosshairPaint(axesColor);
			pp.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
			pp.setDomainCrosshairVisible(true);
			pp.setRangeCrosshairVisible(true);
		}
	}

	@Override
	public boolean _init(final IScope scope) throws GamaRuntimeException {
		history = new StringBuilder(500);
		lastValues.clear();;
		IExpression string1 = getFacet(IKeyword.TYPE);
		if ( string1 != null ) {
			String t = Cast.asString(scope, string1.value(scope));
			type = IKeyword.SERIES.equals(t) ? SERIES_CHART
				: IKeyword.HISTOGRAM.equals(t) ? HISTOGRAM_CHART
					: IKeyword.PIE.equals(t) ? PIE_CHART : IKeyword.BOX_WHISKER.equals(t) ? BOX_WHISKER_CHART
						: IKeyword.SCATTER.equals(t) ? SCATTER_CHART : XY_CHART;

		}
		IExpression color = getFacet(IKeyword.AXES);
		if ( color != null ) {
			axesColor = Cast.asColor(scope, color.value(scope));
		}
		IExpression color1 = getFacet(IKeyword.BACKGROUND);
		if ( color1 != null ) {
			backgroundColor = Cast.asColor(scope, color1.value(scope));
		}
		IExpression string = getFacet(IKeyword.STYLE);
		if ( string != null ) {
			style = Cast.asString(scope, string.value(scope));
			// TODO Verifier style;
		}
		IExpression face = getFacet(ChartLayerStatement.TICKFONTFACE);
		if ( face != null ) {
			tickFontFace = Cast.asString(scope, face.value(scope));
		}
		face = getFacet(ChartLayerStatement.LABELFONTFACE);
		if ( face != null ) {
			labelFontFace = Cast.asString(scope, face.value(scope));
		}
		face = getFacet(ChartLayerStatement.LEGENDFONTFACE);
		if ( face != null ) {
			legendFontFace = Cast.asString(scope, face.value(scope));
		}
		face = getFacet(ChartLayerStatement.TITLEFONTFACE);
		if ( face != null ) {
			titleFontFace = Cast.asString(scope, face.value(scope));
		}
		face = getFacet(ChartLayerStatement.TICKFONTSIZE);
		if ( face != null ) {
			tickFontSize = Cast.asInt(scope, face.value(scope));
		}
		face = getFacet(ChartLayerStatement.LABELFONTSIZE);
		if ( face != null ) {
			labelFontSize = Cast.asInt(scope, face.value(scope));
		}
		face = getFacet(ChartLayerStatement.LEGENDFONTSIZE);
		if ( face != null ) {
			legendFontSize = Cast.asInt(scope, face.value(scope));
		}
		face = getFacet(ChartLayerStatement.TITLEFONTSIZE);
		if ( face != null ) {
			titleFontSize = Cast.asInt(scope, face.value(scope));
		}
		face = getFacet(ChartLayerStatement.TICKFONTSTYLE);
		if ( face != null ) {
			tickFontStyle = toFontStyle(Cast.asString(scope, face.value(scope)));
		}
		face = getFacet(ChartLayerStatement.LABELFONTSTYLE);
		if ( face != null ) {
			labelFontStyle = toFontStyle(Cast.asString(scope, face.value(scope)));
		}
		face = getFacet(ChartLayerStatement.LEGENDFONTSTYLE);
		if ( face != null ) {
			legendFontStyle = toFontStyle(Cast.asString(scope, face.value(scope)));
		}
		face = getFacet(ChartLayerStatement.TITLEFONTSTYLE);
		if ( face != null ) {
			titleFontStyle = toFontStyle(Cast.asString(scope, face.value(scope)));
		}
		createChart(scope);
		createData(scope);
		// dataswithoutlists = datas;
		updateseries(scope);
		chart.setNotify(false);
		return true;
	}

	int toFontStyle(final String style) {
		if ( style.equals("bold") ) { return Font.BOLD; }
		if ( style.equals("italic") ) { return Font.ITALIC; }
		return Font.PLAIN;
	}

	public void updateseries(final IScope scope) throws GamaRuntimeException {
		// datas=dataswithoutlists;
		datasfromlists = new ArrayList<ChartData>();
		for ( int dl = 0; dl < datalists.size(); dl++ ) {
			ChartDataList datalist = datalists.get(dl);

			Object val = datalist.valuelistexp.resolveAgainst(scope).value(scope);
			if ( !(val instanceof GamaList) ) {
				// scope.getGui().debug("chart list with no list...");
				return;
			}
			List<List> values = Cast.asList(scope, val);
			if ( datalist.doreverse ) {
				List tempvalues = Cast.asList(scope, val);
				values = new ArrayList<List>();
				if ( tempvalues.get(0) instanceof GamaList ) {
					IList nval = Cast.asList(scope, tempvalues.get(0));
					for ( int j = 0; j < nval.size(); j++ ) {
						List nl = new ArrayList();
						nl.add(nval.get(j));
						values.add(nl);
					}
				} else {
					// scope.getGui().debug("Reverse series but not list of list..." + tempvalues);
					return;

				}
				if ( tempvalues.size() > 1 ) {
					for ( int i = 1; i < tempvalues.size(); i++ ) {
						if ( tempvalues.get(i) instanceof GamaList ) {
							IList nval = Cast.asList(scope, tempvalues.get(i));
							for ( int j = 0; j < nval.size(); j++ ) {
								// Cast.asList(scope, values.get(j)).add(nval.get(j));

								values.get(j).add(nval.get(j));

							}
						} else {
							// scope.getGui().debug("Reverse series but not list of list..." + tempvalues);
							return;

						}
					}
				}

				// scope.getGui().debug("New Values"+values);
			}

			List defaultnames = new ArrayList<String>();
			List defaultcolors = new ArrayList<GamaColor>();
			for ( int i = 0; i < values.size() + 1; i++ ) {
				defaultnames.add("data" + i);
				// defaultcolors.add(GamaColor.array[i]);
				if ( i < 10 ) {

					if ( i == 0 ) {
						defaultcolors.add(Cast.asColor(scope, Color.CYAN));
					}
					if ( i == 1 ) {
						defaultcolors.add(Cast.asColor(scope, Color.RED));
					}
					if ( i == 2 ) {
						defaultcolors.add(Cast.asColor(scope, Color.YELLOW));
					}
					if ( i == 3 ) {
						defaultcolors.add(Cast.asColor(scope, Color.GREEN));
					}
					if ( i == 4 ) {
						defaultcolors.add(Cast.asColor(scope, Color.BLUE));
					}
					if ( i == 5 ) {
						defaultcolors.add(Cast.asColor(scope, Color.PINK));
					}
					if ( i == 6 ) {
						defaultcolors.add(Cast.asColor(scope, Color.MAGENTA));
					}
					if ( i == 7 ) {
						defaultcolors.add(Cast.asColor(scope, Color.ORANGE));
					}
					if ( i == 8 ) {
						defaultcolors.add(Cast.asColor(scope, Color.LIGHT_GRAY));
					}
					if ( i == 9 ) {
						defaultcolors.add(Cast.asColor(scope, Color.DARK_GRAY));
					}
				}
				if ( i >= 10 ) {
					if ( i < GamaColor.colors.size() ) {
						defaultcolors.add(GamaColor.int_colors.values()[i]);
					} else {
						defaultcolors.add(GamaColor.getInt(Random.opRnd(scope, 10000)));
					}
				}

			}

			if ( datalist.colorlistexp != null ) {
				Object valcol = datalist.colorlistexp.resolveAgainst(scope).value(scope);
				if ( valcol instanceof GamaList ) {
					for ( int c = 0; c < ((GamaList) valcol).size(); c++ ) {
						// if ( type == SERIES_CHART)
						// defaultcolors.set(c+1, Cast.asColor(scope, ((GamaList)valcol).get(c)));
						// else
						defaultcolors.set(c, Cast.asColor(scope, ((GamaList) valcol).get(c)));

					}

				}
			}

			boolean dynamicseriesnames = false;
			List<String> seriesnames = new ArrayList();

			if ( datalist.legendlistexp != null ) {
				Object valc = datalist.legendlistexp.resolveAgainst(scope).value(scope);

				if ( valc instanceof GamaList ) {
					dynamicseriesnames = true;
					seriesnames = (GamaList) valc;
					for ( int i = 0; i < CmnFastMath.min(values.size(), seriesnames.size()); i++ ) {
						defaultnames.set(i, seriesnames.get(i) + "(" + i + ")");
						if ( type == SERIES_CHART && ((XYPlot) chart.getPlot()).getDataset(i + 1) != null ) {
							if ( ((DefaultTableXYDataset) ((XYPlot) chart.getPlot()).getDataset(i + 1))
								.getSeriesCount() > 0 ) {
								((DefaultTableXYDataset) ((XYPlot) chart.getPlot()).getDataset(i + 1)).getSeries(0)
									.setKey(seriesnames.get(i) + "(" + i + ")");
							}

						}
					}
					if ( values.size() > seriesnames.size() ) {
						for ( int i = seriesnames.size(); i < values.size(); i++ ) {
							defaultnames.set(i, "(" + i + ")");
						}
					}
				} else {
					for ( int i = values.size(); i < values.size(); i++ ) {
						defaultnames.set(i, "(" + i + ")");
					}
				}
			}

			int nbseries = values.size();
			// if ( type==SERIES_CHART ) nbseries++;
			// ChartData first=datas.get(0);
			if ( type == HISTOGRAM_CHART ) {
				((DefaultCategoryDataset) dataset).clear();
			}
			if ( type == PIE_CHART ) {
				((DefaultPieDataset) dataset).clear();
			}
			if ( nbseries > datalist.previoussize ) {

				for ( int i = datalist.previoussize; i < nbseries; i++ ) {
					AbstractRenderer r;
					try {
						r = datalist.renderer.getClass().newInstance();
						if ( XYLineAndShapeRenderer.class.isAssignableFrom(r.getClass()) ) {
							((XYLineAndShapeRenderer) r).setBaseShapesFilled(
								((XYLineAndShapeRenderer) datalist.renderer).getBaseShapesFilled());
							((XYLineAndShapeRenderer) r).setBaseShapesVisible(
								((XYLineAndShapeRenderer) datalist.renderer).getBaseShapesVisible());
							((XYLineAndShapeRenderer) r).setSeriesLinesVisible(0,
								((XYLineAndShapeRenderer) datalist.renderer).getSeriesLinesVisible(0));
						}
						ChartData newdata;
						newdata =
							ChartDataListStatement.newChartData(scope, r, Cast.asString(scope, defaultnames.get(i)),
								Cast.asColor(scope, defaultcolors.get(i)), values.get(i));

						datas.add(newdata);
						datasfromlists.add(newdata);

						if ( type == SERIES_CHART || type == XY_CHART || type == SCATTER_CHART ) {
							final XYPlot plot = (XYPlot) chart.getPlot();
							final String legend = newdata.getName();
							// if (dataset==null)
							// dataset = new XYDataset();
							if ( type == SERIES_CHART || type == XY_CHART ) {
								dataset = new DefaultTableXYDataset();
								final XYSeries serie = new XYSeries(legend, false, false);
								((DefaultTableXYDataset) dataset).addSeries(serie);

							} else {
								dataset = new XYSeriesCollection();
								final XYSeries serie = new XYSeries(legend, false, true);
								((XYSeriesCollection) dataset).addSeries(serie);

							}
							expressions_index.put(legend, datas.size() - 1);
							plot.setRenderer(datas.size() - 1, (XYItemRenderer) newdata.getRenderer(), false);
							final Color c = newdata.getColor();
							plot.getRenderer(datas.size() - 1).setSeriesPaint(0, c);
							// if ((i>0)||(type==XY_CHART))
							plot.setDataset(datas.size() - 1, (XYDataset) dataset);
							history.append(legend);
							history.append(',');
						}

						if ( type == HISTOGRAM_CHART ) {
							final CategoryPlot plot = (CategoryPlot) chart.getPlot();
							int l = 0;
							for ( final ChartData e : datas ) {
								// String legend = e.getName();
								// ((DefaultCategoryDataset) dataset).setValue(0d, new Integer(0), legend/* , legend
								// */);

								final String legend = e.getName();
								if ( !CategoryItemRenderer.class.isInstance(e.getRenderer()) ) {
									e.renderer = new BarRenderer();
								}
								plot.setRenderer(l, (CategoryItemRenderer) e.getRenderer(), false);
								final Color c = e.getColor();
								plot.getRenderer(l).setSeriesPaint(0, c);
								// plot.setDataset(i, (DefaultCategoryDataset) dataset);
								// }
								l++;
								history.append(legend);
								history.append(',');

							}
							if ( history.length() > 0 ) {
								history.deleteCharAt(history.length() - 1);
							}
							history.append(Strings.LN);
							// plot.setDataset((DefaultCategoryDataset) dataset);

						}
						if ( type == PIE_CHART ) {
							int l = 0;
							// dataset = new DefaultPieDataset();
							final PiePlot plot = (PiePlot) chart.getPlot();
							for ( final ChartData e : datas ) {
								final String legend = e.getName();
								((DefaultPieDataset) dataset).insertValue(l++, legend, null);
								history.append(legend);
								history.append(',');
							}
							if ( history.length() > 0 ) {
								history.deleteCharAt(history.length() - 1);
							}
							history.append(Strings.LN);
							history.append(Strings.LN);
							// plot.setDataset((DefaultPieDataset) dataset);
							l = 0;
							for ( final ChartData e : datas ) {
								plot.setSectionPaint(l++, e.getColor());
							}
							plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} = {1} ({2})"));
							if ( exploded ) {
								for ( final Object c : ((DefaultPieDataset) dataset).getKeys() ) {
									plot.setExplodePercent((Comparable) c, 0.20);
								}
							}
						}

					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				datalist.previoussize = nbseries;
			}

			boolean dynamiccategorynames = false;
			List<String> categorynames = new ArrayList<String>();
			/*
			 * if (datalist.categlistexp!=null)
			 * {
			 * Object valc=datalist.categlistexp.resolveAgainst(scope).value(scope);
			 * if ((valc instanceof GamaList))
			 * {
			 * dynamiccategorynames=true;
			 * categorynames=(GamaList)valc;
			 * }
			 *
			 * if (type==HISTOGRAM_CHART)
			 * {
			 * for ( int i=0; i<values.size(); i++ ) {
			 * GamaList x = new GamaList();
			 * Object obj = values.get(i);
			 * if ( obj instanceof GamaList ) {
			 * x = (GamaList) obj;
			 * // clearvalues=true;
			 * if (dynamiccategorynames)
			 * {
			 * for (int j=0;j<x.length(scope);j++)
			 * if (j<categorynames.size())
			 * {
			 * ((DefaultCategoryDataset) dataset).setValue(Cast.asFloat(scope,
			 * x.get(j)).doubleValue(),(String)defaultnames.get(i),categorynames.get(j).toString()+"("+j+")");
			 * }
			 * else
			 * ((DefaultCategoryDataset) dataset).setValue(Cast.asFloat(scope,
			 * x.get(j)).doubleValue(),(String)defaultnames.get(i),"("+j+")");
			 * }
			 * else
			 * {
			 * for (int j=0;j<x.length(scope);j++)
			 * ((DefaultCategoryDataset) dataset).setValue(Cast.asFloat(scope,
			 * x.get(j)).doubleValue(),(String)defaultnames.get(i), new Integer(j));
			 *
			 * }
			 * } else {
			 * ((DefaultCategoryDataset) dataset).setValue(Cast.asFloat(scope, obj).doubleValue(), new
			 * Integer(0),(String)defaultnames.get(i));
			 * }
			 * }
			 * }
			 * }
			 */
			if ( chart.getLegend() == null ) {
				chart.addLegend(new LegendTitle(chart.getPlot()));
			}
			// LegendTitle legend = chart.getLegend();
			// scope.getGui().debug("dyncateg:"+defaultnames);
			// scope.getGui().debug("legend:"+legend);
			for ( int i = 0; i < nbseries; i++ ) {
				ChartData first = datas.get(i);
				if ( type == SERIES_CHART ) {
					first = datas.get(i + 1);
				}
				first.lastvalue = values.get(i);

			}

		}
	}

	public void clearvalues(final IScope scope) {
		// DefaultCategoryDataset=new DefaultCategoryDataset();
		if ( type == PIE_CHART ) {
			((DefaultPieDataset) dataset).clear();
		} else {
			if ( dataset != null ) {
				((DefaultCategoryDataset) dataset).clear();
			}
			// if (chart.getLegend()!=null) chart.removeLegend();
			for ( int dl = 0; dl < datalists.size(); dl++ ) {
				ChartDataList datalist = datalists.get(dl);
				// datalist.previoussize=0;

				// GamaList defaultnames =new GamaList<String>(Types.STRING);

				boolean dynamicseriesnames = false;

				if ( datalist.legendlistexp != null ) {
					Object valc = datalist.legendlistexp.resolveAgainst(scope).value(scope);

					if ( valc instanceof GamaList ) {
						dynamicseriesnames = true;
						GamaList seriesnames = (GamaList) valc;
						for ( int i = 0; i < CmnFastMath.min(datas.size(), seriesnames.size()); i++ ) {
							datas.get(i).setName(seriesnames.get(i) + "(" + i + ")");
						}
					}
				}

				boolean dynamiccategorynames = false;
				List<String> categorynames = new ArrayList<String>();

				if ( datalist.categlistexp != null ) {
					Object valc = datalist.categlistexp.resolveAgainst(scope).value(scope);
					if ( valc instanceof GamaList ) {
						dynamiccategorynames = true;
						categorynames = (GamaList) valc;
					}

				}
				// if (dynamiccategorynames)
				// {
				// ((DefaultCategoryDataset) dataset).clear();
				// }

				if ( dataset != null ) {
					for ( final ChartData d : datas ) {
						List x = new ArrayList();
						Object obj = d.getValue(scope);
						if ( obj instanceof GamaList ) {
							x = (GamaList) obj;
							// clearvalues=true;
							if ( dynamiccategorynames ) {
								for ( int j = 0; j < x.size(); j++ ) {
									if ( j < categorynames.size() ) {

										((DefaultCategoryDataset) dataset).setValue(
											Cast.asFloat(scope, x.get(j)).doubleValue(), d.getName(),
											categorynames.get(j).toString() + "(" + j + ")");
									} else {
										((DefaultCategoryDataset) dataset).setValue(
											Cast.asFloat(scope, x.get(j)).doubleValue(), d.getName(), "(" + j + ")");
									}
								}
							} else {
								for ( int j = 0; j < x.size(); j++ ) {
									((DefaultCategoryDataset) dataset).setValue(
										Cast.asFloat(scope, x.get(j)).doubleValue(), d.getName(), Integer.valueOf(j));
								}

							}
						} else {
							((DefaultCategoryDataset) dataset).setValue(Cast.asFloat(scope, obj).doubleValue(),
								new Integer(0), d.getName());
						}

						switch (type) {
							case PIE_CHART: {
								// ((DefaultPieDataset) dataset).setValue(s, n);
								break;
							}
							case HISTOGRAM_CHART: {
								// scope.getGui().debug("ChartLayerStatement._step row " + ((DefaultCategoryDataset)
								// dataset).getRowCount() +
								// " col " + ((DefaultCategoryDataset) dataset).getColumnCount());
								// ((DefaultCategoryDataset) dataset).setValue(n, new Integer(0), s);
								break;
							}
						}

						history.append(0);
						history.append(',');
					}
				}
			}

		}

		if ( chart.getLegend() == null ) {
			// LegendTitle nouvleg=new LegendTitle(chart.getPlot());
			// chart.addLegend(nouvleg);
			// nouvleg.

		}

	}

	@Override
	public boolean _step(final IScope scope) throws GamaRuntimeException {
		lastComputeCycle = (long) scope.getClock().getCycle();
		if ( datalists.size() > 0 ) {
			updateseries(scope);
		}

		if ( type == XY_CHART || type == SERIES_CHART || type == SCATTER_CHART ) {
			computeSeries(scope, lastComputeCycle);
			return true;

		}

		/*
		 * switch (type) {
		 * case XY_CHART:
		 * computeSeries(scope, lastComputeCycle);
		 * case SERIES_CHART:
		 * computeSeries(scope, lastComputeCycle);
		 * return true;
		 * }
		 */
		boolean clearvalues = false;
		int cpt = 0;
		for ( final ChartData d : datas ) {
			List x = new ArrayList();
			Object obj = d.getValue(scope);
			if ( obj instanceof GamaList ) {
				x = (GamaList) obj;
				clearvalues = true;
				if ( type != XY_CHART ) {
					for ( int j = 0; j < x.size(); j++ ) {
						lastValues.put(d.getName(), Cast.asFloat(scope, x.get(j)));
					}
				}
			} else {
				x.add(obj);
				// if ( type != XY_CHART || type != SCATTER_CHART ) {
				lastValues.put(d.getName(), Cast.asFloat(scope, x.get(x.size() - 1)));
				// }
			}
		}
		if ( clearvalues ) {
			clearvalues(scope);
		} else {
			for ( final Map.Entry<String, Double> d : lastValues.entrySet() ) {
				String s = d.getKey();
				final double n = d.getValue();
				if ( !(d instanceof GamaList) ) {
					switch (type) {
						case PIE_CHART: {
							((DefaultPieDataset) dataset).setValue(s, n);
							break;
						}
						case HISTOGRAM_CHART: {
							// ((DefaultCategoryDataset) dataset).setValue(Cast.asFloat(scope,
							// x.get(j)).doubleValue(),d.getName(),"("+j+")");
							// scope.getGui().debug("ChartLayerStatement._step row " + ((DefaultCategoryDataset)
							// dataset).getRowCount() +
							// " col " + ((DefaultCategoryDataset) dataset).getColumnCount());
							((DefaultCategoryDataset) dataset).setValue(n, Integer.valueOf(cpt), s/* , s */);
							((CategoryPlot) chart.getPlot()).getRenderer().setSeriesPaint(cpt, datas.get(cpt).color);
							cpt++;
							break;
						}
					}
				}
				history.append(n);
				history.append(',');
			}
		}

		history.deleteCharAt(history.length() - 1);
		history.append(Strings.LN);
		return true;
	}

	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 * @param cycle
	 */
	private void computeSeries(final IScope scope, final long cycle) throws GamaRuntimeException {
		if ( datas.isEmpty() ) { return; }
		List x = new ArrayList();
		Object obj = datas.get(0).getValue(scope);
		if ( type == SERIES_CHART && scope.getAgentScope() instanceof BatchAgent ) {
			// if (BatchAgent.class.isAssignableFrom(scope.getClass()))
			obj = ((BatchAgent) scope.getAgentScope()).getRunNumber();
		}

		boolean cumulative = false;
		if ( obj instanceof GamaList ) {
			x = (GamaList) obj;
		} else {
			x.add(obj);
		}
		for ( int i = 0; i < x.size(); i++ ) {
			history.append(x.get(i));
			history.append(',');
		}
		if ( !(type == SERIES_CHART & datas.size() < 2) ) {
			for ( int i = 0; i < datas.size(); i++ ) {
				if ( !datasfromlists.contains(datas.get(i)) ) {
					if ( type == SERIES_CHART & i == 0 ) {
						i++;
					}
					XYPlot plot = (XYPlot) chart.getPlot();

					final NumberAxis domainAxis = (NumberAxis) ((XYPlot) this.chart.getPlot()).getDomainAxis();
					final NumberAxis rangeAxis = (NumberAxis) ((XYPlot) this.chart.getPlot()).getRangeAxis();
					boolean domainauto = false;
					if ( domainAxis.isAutoRange() ) {
						domainauto = true;
						domainAxis.setAutoRange(false);

					}
					boolean rangeauto = false;
					if ( rangeAxis.isAutoRange() ) {
						rangeauto = true;
						rangeAxis.setAutoRange(false);

					}

					// DefaultTableXYDataset data = (DefaultTableXYDataset) plot.getDataset(i);
					XYDataset data = plot.getDataset(i);
					XYSeries serie = new XYSeries(0, false, false);
					if ( type == SERIES_CHART || type == XY_CHART ) {
						serie = ((DefaultTableXYDataset) data).getSeries(0);
					}
					if ( type == SCATTER_CHART ) {
						serie = ((XYSeriesCollection) data).getSeries(0);
					}
					List n = new ArrayList();
					Object o = datas.get(i).getValue(scope);
					if ( o instanceof GamaList ) {
						n = (GamaList) o;
					} else {
						cumulative = true;
						n.add(o);
					}
					if ( !cumulative ) {
						if ( type == SERIES_CHART || type == XY_CHART ) {
							final XYSeries nserie = new XYSeries(serie.getKey(), false, false);
							((DefaultTableXYDataset) data).removeSeries(0);
							((DefaultTableXYDataset) data).addSeries(nserie);
							serie = nserie;
							// serie.clear();
						}
						if ( type == SCATTER_CHART ) {
							final XYSeries nserie = new XYSeries(serie.getKey(), false, true);
							((XYSeriesCollection) data).removeSeries(0);
							((XYSeriesCollection) data).addSeries(nserie);
							serie = nserie;
							// serie.clear();
						}

					}
					// java.lang.System.out.println("gr"+n);
					for ( int j = 0; j < n.size(); j++ ) {
						if ( type == SERIES_CHART ) {
							double d2 = Cast.asFloat(scope, n.get(j));
							double d1;
							if ( cumulative ) {
								d1 = Cast.asFloat(scope, x.get(j));
							} else {
								d1 = Cast.asFloat(scope, j);
							}
							serie.addOrUpdate(d1, d2);
						} else if ( type == XY_CHART || type == SCATTER_CHART ) {
							try {
								IList<Double> list = GamaListType.staticCast(scope, n.get(j), Types.FLOAT, false);
								double d1 = list.get(0);
								double d2 = list.get(1);
								if ( cumulative ) {
									serie.addOrUpdate(d1, d2);
								} else {
									serie.addOrUpdate(d1, d2);

								}
							} catch (IndexOutOfBoundsException e) {
								// GamaRuntimeException g = GamaRuntimeException.create(e,scope);
								// g.addContext("each point value should be a gama-point or a 2-float list, value here: "+(Cast.asList(scope, n.get(j))));
								GamaRuntimeException g = GamaRuntimeException
									.error("each point value should be a gama-point or a 2-float list, value here: " +
										Cast.asList(scope, n.get(j)), scope);
								GAMA.reportAndThrowIfNeeded(scope, g, true);
								// TODO Auto-generated catch block
							}
						}
						history.append(n.get(j));
						history.append(',');
					}

					if ( domainauto == true ) {
						domainAxis.setAutoRange(true);

					}
					if ( rangeauto == true ) {
						rangeAxis.setAutoRange(true);

					}
				}

			}
		}
		history.deleteCharAt(history.length() - 1);
		history.append(Strings.LN);

	}

	@Override
	public short getType() {
		return ILayerStatement.CHART;
	}

	@Override
	public void dispose() {
		chart = null;
		super.dispose();
	}

	public void saveHistory() {
		IScope scope = output.getScope().copy();
		if ( scope == null ) { return; }
		try {
			Files.newFolder(scope, chartFolder);
			String file = chartFolder + "/" + "chart_" + getName() + ".csv";
			BufferedWriter bw;
			file = FileUtils.constructAbsoluteFilePath(scope, file, false);
			bw = new BufferedWriter(new FileWriter(file));
			bw.append(history);
			bw.close();
		} catch (final Exception e) {
			e.printStackTrace();
			return;
		} finally {
			GAMA.releaseScope(scope);
		}
	}

}
