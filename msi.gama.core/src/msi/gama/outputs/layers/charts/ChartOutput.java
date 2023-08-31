/*******************************************************************************************************
 *
 * ChartOutput.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers.charts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;

import org.jfree.chart.JFreeChart;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

/**
 * The Class ChartOutput.
 */
public abstract class ChartOutput {

	/** The Constant SERIES_CHART. */
	static final int SERIES_CHART = 0;

	/** The Constant HISTOGRAM_CHART. */
	static final int HISTOGRAM_CHART = 1;

	/** The Constant PIE_CHART. */
	static final int PIE_CHART = 2;

	/** The Constant XY_CHART. */
	static final int XY_CHART = 3;

	/** The Constant BOX_WHISKER_CHART. */
	static final int BOX_WHISKER_CHART = 4;

	/** The Constant SCATTER_CHART. */
	static final int SCATTER_CHART = 5;

	/** The Constant RADAR_CHART. */
	static final int RADAR_CHART = 6;

	/** The Constant HEATMAP_CHART. */
	static final int HEATMAP_CHART = 7;

	/** The last update cycle. */
	public int lastUpdateCycle = -1;

	/** The ismyfirststep. */
	public boolean ismyfirststep = true;

	/** The chname. */
	String chname = "";

	/** The xlabel. */
	String xlabel = null;

	/** The ylabel. */
	String ylabel = null;

	/** The y 2 label. */
	String y2label = null;

	/** The chartdataset. */
	ChartDataSet chartdataset;

	/** The type. */
	int type = SERIES_CHART;

	/** The reverse axes. */
	boolean reverse_axes = false;

	/** The x logscale. */
	boolean x_logscale = false;

	/** The y logscale. */
	boolean y_logscale = false;

	/** The y 2 logscale. */
	boolean y2_logscale = false;

	/** The use second y axis. */
	boolean use_second_y_axis = false;

	/** The title visible. */
	boolean title_visible = true;

	/** The x tick value visible. */
	boolean x_tick_value_visible = true;

	/** The y tick value visible. */
	boolean y_tick_value_visible = true;

	/** The x tick line visible. */
	boolean x_tick_line_visible = true;

	/** The y tick line visible. */
	boolean y_tick_line_visible = true;

	/** The background color. */
	Color backgroundColor = GamaColor.WHITE;

	/** The axes color. */
	Color axesColor = null;

	/** The background color. */
	Color labelBackgroundColor = null;

	/** The background color. */
	Color labelTextColor = null;

	/** The text color. */
	Color textColor = null;

	/** The tick color. */
	Color tickColor = null;

	/** The tick font face. */
	String tickFontFace = Font.SANS_SERIF;

	/** The tick font size. */
	int tickFontSize = 10;

	/** The tick font style. */
	int tickFontStyle = Font.PLAIN;

	/** The label font face. */
	String labelFontFace = Font.SANS_SERIF;

	/** The label font size. */
	int labelFontSize = 12;

	/** The label font style. */
	int labelFontStyle = Font.BOLD;

	/** The legend font face. */
	String legendFontFace = Font.SANS_SERIF;

	/** The legend font size. */
	int legendFontSize = 10;

	/** The legend font style. */
	int legendFontStyle = Font.ITALIC;

	/** The title font face. */
	String titleFontFace = Font.SERIF;

	/** The title font size. */
	int titleFontSize = 14;

	/** The title font style. */
	int titleFontStyle = Font.BOLD;

	/** The series label position. */
	String series_label_position = "default";

	/** The style. */
	String style = IKeyword.DEFAULT;

	/** The gap. */
	double gap = -1; // only used in bar charts? copied the code, don't
						// understand how to use it...

	/** The xrangemax. */
	double xrangeinterval, xrangemin, xrangemax;

	/** The usexrangeminmax. */
	boolean usexrangeinterval = false, usexrangeminmax = false;

	/** The yrangemax. */
	double yrangeinterval, yrangemin, yrangemax;

	/** The useyrangeminmax. */
	boolean useyrangeinterval = false, useyrangeminmax = false;

	/** The y 2 rangemax. */
	double y2rangeinterval, y2rangemin, y2rangemax;

	/** The usey 2 rangeminmax. */
	boolean usey2rangeinterval = false, usey2rangeminmax = false;

	/** The xtickunit. */
	double xtickunit = -1;

	/** The ytickunit. */
	double ytickunit = -1;

	/** The y 2 tickunit. */
	double y2tickunit = -1;

	// copy from previous dataLayerStatement

	// static String chartFolder = "charts";

	// final Map<String, Integer> expressions_index = new HashMap<>();
	// static String xAxisName = "'time'";

	// HashMap<String,Object> chartParameters=new HashMap<String,Object>();

	/**
	 * Gets the image.
	 *
	 * @param sizeX
	 *            the size X
	 * @param sizeY
	 *            the size Y
	 * @param antiAlias
	 *            the anti alias
	 * @return the image
	 */
	public abstract BufferedImage getImage(final int sizeX, final int sizeY, final boolean antiAlias);

	/**
	 * Instantiates a new chart output.
	 *
	 * @param scope
	 *            the scope
	 * @param name
	 *            the name
	 * @param typeexp
	 *            the typeexp
	 */
	public ChartOutput(final IScope scope, final String name, final IExpression typeexp) {
		final String t = typeexp == null ? IKeyword.SERIES : Cast.asString(scope, typeexp.value(scope));
		type = IKeyword.SERIES.equals(t) ? SERIES_CHART : IKeyword.HISTOGRAM.equals(t) ? HISTOGRAM_CHART
				: IKeyword.RADAR.equals(t) ? RADAR_CHART : IKeyword.PIE.equals(t) ? PIE_CHART
				: IKeyword.BOX_WHISKER.equals(t) ? BOX_WHISKER_CHART : IKeyword.SCATTER.equals(t) ? SCATTER_CHART
				: XY_CHART;
		axesColor = GamaColor.get(Color.black);
	}

	/**
	 * Gets the chart cycle.
	 *
	 * @param scope
	 *            the scope
	 * @return the chart cycle
	 */
	public int getChartCycle(final IScope scope) {
		if (ismyfirststep) {
			ismyfirststep = false;
			return 0;
		}
		return scope.getClock().getCycle() + 1;
	}

	/**
	 * Step.
	 *
	 * @param scope
	 *            the scope
	 */
	public void step(final IScope scope) {
		chartdataset.updatedataset(scope, getChartCycle(scope));
		updateOutput(scope);
	}

	/**
	 * Initdataset.
	 */
	public void initdataset() {

	}

	/**
	 * Update output.
	 *
	 * @param scope
	 *            the scope
	 */
	public void updateOutput(final IScope scope) {
		if (chartdataset.doResetAll(scope, lastUpdateCycle)) {
			clearDataSet(scope);
			for (final String serieid : chartdataset.getDataSeriesIds(scope)) { createNewSerie(scope, serieid); }
			preResetSeries(scope);
			for (final String serieid : chartdataset.getDataSeriesIds(scope)) { this.resetSerie(scope, serieid); }

		} else {
			final LinkedHashMap<String, Integer> toremove = chartdataset.getSerieRemovalDate();
			for (final String serieid : toremove.keySet()) {
				if (toremove.get(serieid) >= lastUpdateCycle) {
					removeSerie(scope, serieid);
					toremove.put(serieid, toremove.get(serieid) - 1);
				}
			}
			final LinkedHashMap<String, Integer> toadd = chartdataset.getSerieCreationDate();
			for (final String serieid : toadd.keySet()) {
				if (toadd.get(serieid) >= lastUpdateCycle) {
					createNewSerie(scope, serieid);
					toadd.put(serieid, toadd.get(serieid) - 1);
				}
			}
			preResetSeries(scope);
			for (final String serieid : chartdataset.getDataSeriesIds(scope)) { this.resetSerie(scope, serieid); }

		}
		resetAxes(scope);

		lastUpdateCycle = scope.getClock().getCycle();
		// DEBUG.LOG("output last update:" + lastUpdateCycle);
	}

	/**
	 * Pre reset series.
	 *
	 * @param scope
	 *            the scope
	 */
	public void preResetSeries(final IScope scope) {

	}

	/**
	 * Reset axes.
	 *
	 * @param scope
	 *            the scope
	 */
	public void resetAxes(final IScope scope) {
		// Update axes

	}

	/**
	 * Removes the serie.
	 *
	 * @param scope
	 *            the scope
	 * @param serieid
	 *            the serieid
	 */
	public void removeSerie(final IScope scope, final String serieid) {

	}

	/**
	 * Reset serie.
	 *
	 * @param scope
	 *            the scope
	 * @param serieid
	 *            the serieid
	 */
	protected void resetSerie(final IScope scope, final String serieid) {

	}

	/**
	 * Clear data set.
	 *
	 * @param scope
	 *            the scope
	 */
	protected void clearDataSet(final IScope scope) {

	}

	/**
	 * Creates the new serie.
	 *
	 * @param scope
	 *            the scope
	 * @param serieid
	 *            the serieid
	 */
	protected void createNewSerie(final IScope scope, final String serieid) {

	}

	/**
	 * Sets the use X source.
	 *
	 * @param scope
	 *            the scope
	 * @param expval
	 *            the expval
	 */
	public void setUseXSource(final IScope scope, final IExpression expval) {
		// if there is something to do to use custom X axis
	}

	/**
	 * Sets the use X labels.
	 *
	 * @param scope
	 *            the scope
	 * @param expval
	 *            the expval
	 */
	public void setUseXLabels(final IScope scope, final IExpression expval) {
		// if there is something to do to use custom X axis
	}

	/**
	 * Sets the use Y labels.
	 *
	 * @param scope
	 *            the scope
	 * @param expval
	 *            the expval
	 */
	public void setUseYLabels(final IScope scope, final IExpression expval) {
		// if there is something to do to use custom X axis
	}

	/**
	 * Inits the chart.
	 *
	 * @param scope
	 *            the scope
	 * @param chartname
	 *            the chartname
	 */
	public void initChart(final IScope scope, final String chartname) {
		chname = chartname;

	}

	/**
	 * Creates the chart.
	 *
	 * @param scope
	 *            the scope
	 */
	public void createChart(final IScope scope) {}

	/**
	 * Gets the chartdataset.
	 *
	 * @return the chartdataset
	 */
	public ChartDataSet getChartdataset() { return chartdataset; }

	/**
	 * Sets the chartdataset.
	 *
	 * @param chartdataset
	 *            the new chartdataset
	 */
	public void setChartdataset(final ChartDataSet chartdataset) {
		this.chartdataset = chartdataset;
		chartdataset.setOutput(this);
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() { return chname; }

	/**
	 * Gets the style.
	 *
	 * @return the style
	 */
	public String getStyle() { return style; }

	/**
	 * Sets the axes color value.
	 *
	 * @param scope
	 *            the scope
	 * @param color
	 *            the color
	 */
	public void setAxesColorValue(final IScope scope, final GamaColor color) {
		axesColor = color;

	}

	/**
	 * Sets the tick color value.
	 *
	 * @param scope
	 *            the scope
	 * @param color
	 *            the color
	 */
	public void setTickColorValue(final IScope scope, final GamaColor color) {
		tickColor = color;

	}

	/**
	 * Sets the background color value.
	 *
	 * @param scope
	 *            the scope
	 * @param color
	 *            the color
	 */
	public void setBackgroundColorValue(final IScope scope, final GamaColor color) {
		backgroundColor = color;

	}

	/**
	 * Sets the label text color value.
	 *
	 * @param scope
	 *            the scope
	 * @param color
	 *            the color
	 */
	public void setLabelTextColorValue(final IScope scope, final GamaColor color) {
		labelTextColor = color;
	}

	/**
	 * Sets the label background color value.
	 *
	 * @param scope
	 *            the scope
	 * @param color
	 *            the color
	 */
	public void setLabelBackgroundColorValue(final IScope scope, final GamaColor color) {
		labelBackgroundColor = color;
	}

	/**
	 * Sets the color value.
	 *
	 * @param scope
	 *            the scope
	 * @param color
	 *            the color
	 */
	public void setColorValue(final IScope scope, final GamaColor color) {
		textColor = color;

	}

	/**
	 * Sets the tick font face.
	 *
	 * @param scope
	 *            the scope
	 * @param value
	 *            the value
	 */
	public void setTickFontFace(final IScope scope, final String value) {
		if (value != null) { tickFontFace = value; }
	}

	/**
	 * Sets the label font face.
	 *
	 * @param scope
	 *            the scope
	 * @param value
	 *            the value
	 */
	public void setLabelFontFace(final IScope scope, final String value) {
		if (value != null) { labelFontFace = value; }
	}

	/**
	 * Sets the legend font face.
	 *
	 * @param scope
	 *            the scope
	 * @param value
	 *            the value
	 */
	public void setLegendFontFace(final IScope scope, final String value) {
		if (value != null) { legendFontFace = value; }
	}

	/**
	 * Sets the title font face.
	 *
	 * @param scope
	 *            the scope
	 * @param value
	 *            the value
	 */
	public void setTitleFontFace(final IScope scope, final String value) {
		if (value != null) { titleFontFace = value; }
	}

	/**
	 * Sets the tick font size.
	 *
	 * @param scope
	 *            the scope
	 * @param value
	 *            the value
	 */
	public void setTickFontSize(final IScope scope, final int value) {
		tickFontSize = value;
	}

	/**
	 * Sets the label font size.
	 *
	 * @param scope
	 *            the scope
	 * @param value
	 *            the value
	 */
	public void setLabelFontSize(final IScope scope, final int value) {
		labelFontSize = value;
	}

	/**
	 * Sets the legend font size.
	 *
	 * @param scope
	 *            the scope
	 * @param value
	 *            the value
	 */
	public void setLegendFontSize(final IScope scope, final int value) {
		legendFontSize = value;
	}

	/**
	 * Sets the title font size.
	 *
	 * @param scope
	 *            the scope
	 * @param value
	 *            the value
	 */
	public void setTitleFontSize(final IScope scope, final int value) {
		titleFontSize = value;
	}

	/**
	 * Sets the tick font style.
	 *
	 * @param scope
	 *            the scope
	 * @param value
	 *            the value
	 */
	public void setTickFontStyle(final IScope scope, final int value) {
		tickFontStyle = value;
	}

	/**
	 * Sets the label font style.
	 *
	 * @param scope
	 *            the scope
	 * @param value
	 *            the value
	 */
	public void setLabelFontStyle(final IScope scope, final int value) {
		labelFontStyle = value;
	}

	/**
	 * Sets the legend font style.
	 *
	 * @param scope
	 *            the scope
	 * @param value
	 *            the value
	 */
	public void setLegendFontStyle(final IScope scope, final int value) {
		legendFontStyle = value;
	}

	/**
	 * Sets the title font style.
	 *
	 * @param scope
	 *            the scope
	 * @param value
	 *            the value
	 */
	public void setTitleFontStyle(final IScope scope, final int value) {
		titleFontStyle = value;
	}

	/**
	 * Sets the X label.
	 *
	 * @param scope
	 *            the scope
	 * @param asString
	 *            the as string
	 */
	public void setXLabel(final IScope scope, final String asString) {
		xlabel = asString;

	}

	/**
	 * Gets the x label.
	 *
	 * @param scope
	 *            the scope
	 * @return the x label
	 */
	public String getXLabel(final IScope scope) {
		return xlabel;

	}

	/**
	 * Sets the Y label.
	 *
	 * @param scope
	 *            the scope
	 * @param asString
	 *            the as string
	 */
	public void setYLabel(final IScope scope, final String asString) {
		ylabel = asString;

	}

	/**
	 * Gets the y label.
	 *
	 * @param scope
	 *            the scope
	 * @return the y label
	 */
	public String getYLabel(final IScope scope) {
		return ylabel;

	}

	/**
	 * Sets the Y 2 label.
	 *
	 * @param scope
	 *            the scope
	 * @param asString
	 *            the as string
	 */
	public void setY2Label(final IScope scope, final String asString) {
		y2label = asString;

	}

	/**
	 * Gets the y 2 label.
	 *
	 * @param scope
	 *            the scope
	 * @return the y 2 label
	 */
	public String getY2Label(final IScope scope) {
		return y2label;

	}

	/**
	 * Gets the use X range interval.
	 *
	 * @param scope
	 *            the scope
	 * @return the use X range interval
	 */
	public boolean getUseXRangeInterval(final IScope scope) {
		return usexrangeinterval;
	}

	/**
	 * Gets the use X range min max.
	 *
	 * @param scope
	 *            the scope
	 * @return the use X range min max
	 */
	public boolean getUseXRangeMinMax(final IScope scope) {
		return usexrangeminmax;
	}

	/**
	 * Gets the use Y range interval.
	 *
	 * @param scope
	 *            the scope
	 * @return the use Y range interval
	 */
	public boolean getUseYRangeInterval(final IScope scope) {
		return useyrangeinterval;
	}

	/**
	 * Gets the use Y range min max.
	 *
	 * @param scope
	 *            the scope
	 * @return the use Y range min max
	 */
	public boolean getUseYRangeMinMax(final IScope scope) {
		return useyrangeminmax;
	}

	/**
	 * Gets the use Y 2 range interval.
	 *
	 * @param scope
	 *            the scope
	 * @return the use Y 2 range interval
	 */
	public boolean getUseY2RangeInterval(final IScope scope) {
		return usey2rangeinterval;
	}

	/**
	 * Gets the use Y 2 range min max.
	 *
	 * @param scope
	 *            the scope
	 * @return the use Y 2 range min max
	 */
	public boolean getUseY2RangeMinMax(final IScope scope) {
		return usey2rangeminmax;
	}

	/**
	 * Sets the X range interval.
	 *
	 * @param scope
	 *            the scope
	 * @param doubleValue
	 *            the double value
	 */
	public void setXRangeInterval(final IScope scope, final double doubleValue) {
		this.usexrangeinterval = true;
		this.xrangeinterval = doubleValue;

	}

	/**
	 * Gets the x range interval.
	 *
	 * @param scope
	 *            the scope
	 * @return the x range interval
	 */
	public double getXRangeInterval(final IScope scope) {
		return xrangeinterval;

	}

	/**
	 * Sets the X range min max.
	 *
	 * @param scope
	 *            the scope
	 * @param minValue
	 *            the min value
	 * @param maxValue
	 *            the max value
	 */
	public void setXRangeMinMax(final IScope scope, final double minValue, final double maxValue) {
		this.usexrangeminmax = true;
		this.xrangemin = minValue;
		this.xrangemax = maxValue;

	}

	/**
	 * Gets the x range min.
	 *
	 * @param scope
	 *            the scope
	 * @return the x range min
	 */
	public double getXRangeMin(final IScope scope) {
		return xrangemin;

	}

	/**
	 * Gets the x range max.
	 *
	 * @param scope
	 *            the scope
	 * @return the x range max
	 */
	public double getXRangeMax(final IScope scope) {
		return xrangemax;

	}

	/**
	 * Gets the y range interval.
	 *
	 * @param scope
	 *            the scope
	 * @return the y range interval
	 */
	public double getYRangeInterval(final IScope scope) {
		return this.yrangeinterval;
	}

	/**
	 * Sets the Y range interval.
	 *
	 * @param scope
	 *            the scope
	 * @param doubleValue
	 *            the double value
	 */
	public void setYRangeInterval(final IScope scope, final double doubleValue) {
		this.useyrangeinterval = true;
		this.yrangeinterval = doubleValue;

	}

	/**
	 * Sets the Y range min max.
	 *
	 * @param scope
	 *            the scope
	 * @param minValue
	 *            the min value
	 * @param maxValue
	 *            the max value
	 */
	public void setYRangeMinMax(final IScope scope, final double minValue, final double maxValue) {
		this.useyrangeminmax = true;
		this.yrangemin = minValue;
		this.yrangemax = maxValue;

	}

	/**
	 * Gets the y range min.
	 *
	 * @param scope
	 *            the scope
	 * @return the y range min
	 */
	public double getYRangeMin(final IScope scope) {
		return yrangemin;

	}

	/**
	 * Gets the y range max.
	 *
	 * @param scope
	 *            the scope
	 * @return the y range max
	 */
	public double getYRangeMax(final IScope scope) {
		return yrangemax;

	}

	/**
	 * Gets the y 2 range interval.
	 *
	 * @param scope
	 *            the scope
	 * @return the y 2 range interval
	 */
	public double getY2RangeInterval(final IScope scope) {
		return this.y2rangeinterval;
	}

	/**
	 * Sets the Y 2 range interval.
	 *
	 * @param scope
	 *            the scope
	 * @param doubleValue
	 *            the double value
	 */
	public void setY2RangeInterval(final IScope scope, final double doubleValue) {
		this.usey2rangeinterval = true;
		this.y2rangeinterval = doubleValue;

	}

	/**
	 * Sets the Y 2 range min max.
	 *
	 * @param scope
	 *            the scope
	 * @param minValue
	 *            the min value
	 * @param maxValue
	 *            the max value
	 */
	public void setY2RangeMinMax(final IScope scope, final double minValue, final double maxValue) {
		this.usey2rangeminmax = true;
		this.y2rangemin = minValue;
		this.y2rangemax = maxValue;

	}

	/**
	 * Gets the y 2 range min.
	 *
	 * @param scope
	 *            the scope
	 * @return the y 2 range min
	 */
	public double getY2RangeMin(final IScope scope) {
		return y2rangemin;

	}

	/**
	 * Gets the y 2 range max.
	 *
	 * @param scope
	 *            the scope
	 * @return the y 2 range max
	 */
	public double getY2RangeMax(final IScope scope) {
		return y2rangemax;

	}

	/**
	 * Sets the X tick unit.
	 *
	 * @param scope
	 *            the scope
	 * @param r
	 *            the r
	 */
	public void setXTickUnit(final IScope scope, final double r) {
		this.xtickunit = r;

	}

	/**
	 * Gets the x tick unit.
	 *
	 * @param scope
	 *            the scope
	 * @return the x tick unit
	 */
	public double getXTickUnit(final IScope scope) {
		return xtickunit;

	}

	/**
	 * Sets the Y tick unit.
	 *
	 * @param scope
	 *            the scope
	 * @param r
	 *            the r
	 */
	public void setYTickUnit(final IScope scope, final double r) {
		this.ytickunit = r;

	}

	/**
	 * Gets the y tick unit.
	 *
	 * @param scope
	 *            the scope
	 * @return the y tick unit
	 */
	public double getYTickUnit(final IScope scope) {
		return ytickunit;

	}

	/**
	 * Sets the Y 2 tick unit.
	 *
	 * @param scope
	 *            the scope
	 * @param r
	 *            the r
	 */
	public void setY2TickUnit(final IScope scope, final double r) {
		this.y2tickunit = r;

	}

	/**
	 * Gets the y 2 tick unit.
	 *
	 * @param scope
	 *            the scope
	 * @return the y 2 tick unit
	 */
	public double getY2TickUnit(final IScope scope) {
		return y2tickunit;

	}

	/**
	 * Sets the gap.
	 *
	 * @param scope
	 *            the scope
	 * @param range
	 *            the range
	 */
	public void setGap(final IScope scope, final double range) {
		this.gap = range;
	}

	/**
	 * Gets the JF chart.
	 *
	 * @return the JF chart
	 */
	public JFreeChart getJFChart() { return null; }

	/**
	 * Sets the serie marker shape.
	 *
	 * @param scope
	 *            the scope
	 * @param serieid
	 *            the serieid
	 * @param markershape
	 *            the markershape
	 */
	public void setSerieMarkerShape(final IScope scope, final String serieid, final String markershape) {

	}

	/**
	 * Sets the default properties from type.
	 *
	 * @param scope
	 *            the scope
	 * @param source
	 *            the source
	 * @param type_val
	 *            the type val
	 */
	public void setDefaultPropertiesFromType(final IScope scope, final ChartDataSource source, final int type_val) {

	}

	/**
	 * Sets the use size.
	 *
	 * @param scope
	 *            the scope
	 * @param name
	 *            the name
	 * @param b
	 *            the b
	 */
	public void setUseSize(final IScope scope, final String name, final boolean b) {

	}

	/**
	 * Sets the series label position.
	 *
	 * @param scope
	 *            the scope
	 * @param asString
	 *            the as string
	 */
	public void setSeriesLabelPosition(final IScope scope, final String asString) {
		series_label_position = asString;

	}

	/**
	 * Sets the style.
	 *
	 * @param scope
	 *            the scope
	 * @param asString
	 *            the as string
	 */
	public void setStyle(final IScope scope, final String asString) {
		style = asString;

	}

	/**
	 * Inits the chart post data init.
	 *
	 * @param scope
	 *            the scope
	 */
	public void initChart_post_data_init(final IScope scope) {

	}

	/**
	 * Gets the model coordinates info.
	 *
	 * @param xOnScreen
	 *            the x on screen
	 * @param yOnScreen
	 *            the y on screen
	 * @param g
	 *            the g
	 * @param positionInPixels
	 *            the position in pixels
	 * @param sb
	 *            the sb
	 * @return the model coordinates info
	 */
	public void getModelCoordinatesInfo(final int xOnScreen, final int yOnScreen, final IDisplaySurface g,
			final Point positionInPixels, final StringBuilder sb) {}

	/**
	 * Sets the reverse axis.
	 *
	 * @param scope
	 *            the scope
	 * @param asBool
	 *            the as bool
	 */
	public void setReverseAxis(final IScope scope, final Boolean asBool) {
		reverse_axes = asBool;
	}

	/**
	 * Sets the X log scale.
	 *
	 * @param scope
	 *            the scope
	 * @param asBool
	 *            the as bool
	 */
	public void setX_LogScale(final IScope scope, final Boolean asBool) {
		x_logscale = asBool;
	}

	/**
	 * Sets the Y log scale.
	 *
	 * @param scope
	 *            the scope
	 * @param asBool
	 *            the as bool
	 */
	public void setY_LogScale(final IScope scope, final Boolean asBool) {
		y_logscale = asBool;
	}

	/**
	 * Gets the x log scale.
	 *
	 * @param scope
	 *            the scope
	 * @return the x log scale
	 */
	public boolean getX_LogScale(final IScope scope) {
		return x_logscale;
	}

	/**
	 * Gets the y log scale.
	 *
	 * @param scope
	 *            the scope
	 * @return the y log scale
	 */
	public boolean getY_LogScale(final IScope scope) {
		return y_logscale;
	}

	/**
	 * Sets the Y 2 log scale.
	 *
	 * @param scope
	 *            the scope
	 * @param asBool
	 *            the as bool
	 */
	public void setY2_LogScale(final IScope scope, final Boolean asBool) {
		y2_logscale = asBool;
	}

	/**
	 * Gets the y 2 log scale.
	 *
	 * @param scope
	 *            the scope
	 * @return the y 2 log scale
	 */
	public boolean getY2_LogScale(final IScope scope) {

		return y2_logscale;
	}

	/**
	 * Sets the use second Y axis.
	 *
	 * @param scope
	 *            the scope
	 * @param asBool
	 *            the as bool
	 */
	public void setUseSecondYAxis(final IScope scope, final Boolean asBool) {

		use_second_y_axis = asBool;
	}

	/**
	 * Gets the use second Y axis.
	 *
	 * @param scope
	 *            the scope
	 * @return the use second Y axis
	 */
	public boolean getUseSecondYAxis(final IScope scope) {

		return use_second_y_axis;
		// return false;
	}

	/**
	 * Sets the X tick value visible.
	 *
	 * @param scope
	 *            the scope
	 * @param asBool
	 *            the as bool
	 */
	public void setXTickValueVisible(final IScope scope, final Boolean asBool) {

		x_tick_value_visible = asBool;
	}

	/**
	 * Gets the x tick value visible.
	 *
	 * @param scope
	 *            the scope
	 * @return the x tick value visible
	 */
	public boolean getXTickValueVisible(final IScope scope) {

		return x_tick_value_visible;
		// return false;
	}

	/**
	 * Sets the Y tick value visible.
	 *
	 * @param scope
	 *            the scope
	 * @param asBool
	 *            the as bool
	 */
	public void setYTickValueVisible(final IScope scope, final Boolean asBool) {

		y_tick_value_visible = asBool;
	}

	/**
	 * Gets the y tick value visible.
	 *
	 * @param scope
	 *            the scope
	 * @return the y tick value visible
	 */
	public boolean getYTickValueVisible(final IScope scope) {

		return y_tick_value_visible;
		// return false;
	}

	/**
	 * Sets the title visible.
	 *
	 * @param scope
	 *            the scope
	 * @param asBool
	 *            the as bool
	 */
	public void setTitleVisible(final IScope scope, final Boolean asBool) {

		title_visible = asBool;
	}

	/**
	 * Gets the title visible.
	 *
	 * @param scope
	 *            the scope
	 * @return the title visible
	 */
	public boolean getTitleVisible(final IScope scope) {

		return title_visible;
		// return false;
	}

	/**
	 * Sets the X tick line visible.
	 *
	 * @param scope
	 *            the scope
	 * @param asBool
	 *            the as bool
	 */
	public void setXTickLineVisible(final IScope scope, final Boolean asBool) {

		x_tick_line_visible = asBool;
	}

	/**
	 * Gets the x tick line visible.
	 *
	 * @param scope
	 *            the scope
	 * @return the x tick line visible
	 */
	public boolean getXTickLineVisible(final IScope scope) {

		return x_tick_line_visible;
		// return false;
	}

	/**
	 * Sets the Y tick line visible.
	 *
	 * @param scope
	 *            the scope
	 * @param asBool
	 *            the as bool
	 */
	public void setYTickLineVisible(final IScope scope, final Boolean asBool) {

		y_tick_line_visible = asBool;
	}

	/**
	 * Gets the y tick line visible.
	 *
	 * @param scope
	 *            the scope
	 * @return the y tick line visible
	 */
	public boolean getYTickLineVisible(final IScope scope) {

		return y_tick_line_visible;
		// return false;
	}

	/**
	 * Dispose.
	 *
	 * @param scope
	 *            the scope
	 */
	public void dispose(final IScope scope) {}

}
