/*********************************************************************************************
 *
 * 'ChartOutput.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.outputs.layers.charts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jfree.chart.JFreeChart;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

public abstract class ChartOutput {

	static final int SERIES_CHART = 0;
	static final int HISTOGRAM_CHART = 1;
	static final int PIE_CHART = 2;
	static final int XY_CHART = 3;
	static final int BOX_WHISKER_CHART = 4;
	static final int SCATTER_CHART = 5;
	static final int RADAR_CHART = 6;
	static final int HEATMAP_CHART = 7;

	LinkedHashMap<String, Integer> serieLastUpdate = new LinkedHashMap<String, Integer>();

	public int lastUpdateCycle = -1;
	public boolean ismyfirststep = true;

	String chname = "";
	String xlabel = null;
	String ylabel = null;
	ChartDataSet chartdataset;
	int type = SERIES_CHART;
	boolean reverse_axes = false;

	ChartOutput chartOutput = null;
	Color backgroundColor = GamaColor.WHITE;
	Color axesColor = null;
	Color textColor = null;

	String tickFontFace = Font.SANS_SERIF;
	int tickFontSize = 10;
	int tickFontStyle = Font.PLAIN;
	String labelFontFace = Font.SANS_SERIF;
	int labelFontSize = 12;
	int labelFontStyle = Font.BOLD;
	String legendFontFace = Font.SANS_SERIF;
	int legendFontSize = 10;
	int legendFontStyle = Font.ITALIC;
	String titleFontFace = Font.SERIF;
	int titleFontSize = 14;
	int titleFontStyle = Font.BOLD;

	String series_label_position = "default";
	String style = IKeyword.DEFAULT;

	double gap = -1; // only used in bar charts? copied the code, don't
						// understand how to use it...

	double xrangeinterval, xrangemin, xrangemax;
	boolean usexrangeinterval = false, usexrangeminmax = false;

	double yrangeinterval, yrangemin, yrangemax;
	boolean useyrangeinterval = false, useyrangeminmax = false;

	double xtickunit = -1;
	double ytickunit = -1;

	// copy from previous dataLayerStatement

	static String chartFolder = "charts";

	final Map<String, Integer> expressions_index = new HashMap<>();
	static String xAxisName = "'time'";

	// HashMap<String,Object> chartParameters=new HashMap<String,Object>();

	public abstract BufferedImage getImage(IScope scope, int sizex, int sizey, boolean antiAlias);

	public ChartOutput(final IScope scope, final String name, final IExpression typeexp) {
		final String t = typeexp == null ? IKeyword.SERIES : Cast.asString(scope, typeexp.value(scope));
		type = IKeyword.SERIES.equals(t) ? SERIES_CHART
				: IKeyword.HISTOGRAM.equals(t) ? HISTOGRAM_CHART
						: IKeyword.RADAR.equals(t) ? RADAR_CHART
								: IKeyword.PIE.equals(t) ? PIE_CHART : IKeyword.BOX_WHISKER.equals(t)
										? BOX_WHISKER_CHART : IKeyword.SCATTER.equals(t) ? SCATTER_CHART : XY_CHART;
		axesColor = new GamaColor(Color.black);
	}

	public int getType() {
		return type;
	}

	public void setType(final int type) {
		this.type = type;
	}

	public int getChartCycle(final IScope scope) {
		if (ismyfirststep) {
			ismyfirststep = false;
			return 0;
		}
		return scope.getClock().getCycle() + 1;
	}

	public void step(final IScope scope) {
		chartdataset.updatedataset(scope, getChartCycle(scope));
	}

	public void initdataset() {

	}

	public void updateOutput(final IScope scope) {
		if (chartdataset.doResetAll(scope, lastUpdateCycle)) {
			clearDataSet(scope);
			for (final String serieid : chartdataset.getDataSeriesIds(scope)) {
				createNewSerie(scope, serieid);
			}
			preResetSeries(scope);
			for (final String serieid : chartdataset.getDataSeriesIds(scope)) {
				this.resetSerie(scope, serieid);
			}
			resetAxes(scope);

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
			for (final String serieid : chartdataset.getDataSeriesIds(scope)) {
				this.resetSerie(scope, serieid);
			}
			resetAxes(scope);

		}
		updateImage(scope);

		lastUpdateCycle = scope.getClock().getCycle();
		// System.out.println("output last update:" + lastUpdateCycle);
	}

	public void preResetSeries(final IScope scope) {

	}

	public void resetAxes(final IScope scope) {
		// Update axes

	}

	public void removeSerie(final IScope scope, final String serieid) {
		// TODO Auto-generated method stub

	}

	protected void updateImage(final IScope scope) {
		// TODO Auto-generated method stub

	}

	protected void resetSerie(final IScope scope, final String serieid) {
		// TODO Auto-generated method stub

	}

	protected void clearDataSet(final IScope scope) {
		// TODO Auto-generated method stub

	}

	protected void createNewSerie(final IScope scope, final String serieid) {
		// TODO Auto-generated method stub

	}

	public void setUseXSource(final IScope scope, final IExpression expval) {
		// if there is something to do to use custom X axis

	}

	public void setUseXLabels(final IScope scope, final IExpression expval) {
		// if there is something to do to use custom X axis

	}

	public void setUseYSource(final IScope scope, final IExpression expval) {
		// if there is something to do to use custom X axis

	}

	public void setUseYLabels(final IScope scope, final IExpression expval) {
		// if there is something to do to use custom X axis

	}

	int toFontStyle(final String style) {
		if (style.equals("bold")) { return Font.BOLD; }
		if (style.equals("italic")) { return Font.ITALIC; }
		return Font.PLAIN;
	}

	public void initChart(final IScope scope, final String chartname) {
		chname = chartname;

	}

	public void createChart(final IScope scope) {

	}

	public ChartDataSet getChartdataset() {
		return chartdataset;
	}

	public void setChartdataset(final ChartDataSet chartdataset) {
		this.chartdataset = chartdataset;
		chartdataset.setOutput(this);
	}

	public String getName() {
		return chname;
	}

	public String getStyle() {
		return style;
	}

	public void setAxesColorValue(final IScope scope, final GamaColor color) {
		axesColor = color;

	}
	public final Color getAxesColorValue(final IScope scope) {
		return axesColor;

	}

	public void setBackgroundColorValue(final IScope scope, final GamaColor color) {
		backgroundColor = color;

	}

	public void setColorValue(final IScope scope, final GamaColor color) {
		textColor = color;

	}

	public void setTickFontFace(final IScope scope, final String value) {
		if (value != null) {
			tickFontFace = value;
		}
	}

	public String getTickFontFace(final IScope scope) {
			return tickFontFace;
	}

	public void setLabelFontFace(final IScope scope, final String value) {
		if (value != null) {
			labelFontFace = value;
		}
	}

	public String getLabelFontFace(final IScope scope) {
		return	labelFontFace;
	}

	public void setLegendFontFace(final IScope scope, final String value) {
		if (value != null) {
			legendFontFace = value;
		}
	}
	public String getLegendFontFace(final IScope scope) {
		return	legendFontFace;
	}

	public void setTitleFontFace(final IScope scope, final String value) {
		if (value != null) {
			titleFontFace = value;
		}
	}

	public String getTitleFontFace(final IScope scope) {
		return	titleFontFace;
	}

	public void setTickFontSize(final IScope scope, final int value) {
		tickFontSize = value;
	}

	public int getTickFontSize(final IScope scope) {
		return  tickFontSize;
	}

	public void setLabelFontSize(final IScope scope, final int value) {
		labelFontSize = value;
	}

	public int getLabelFontSize(final IScope scope) {
		return labelFontSize;
	}

	public void setLegendFontSize(final IScope scope, final int value) {
		legendFontSize = value;
	}

	public int getLegendFontSize(final IScope scope) {
		return legendFontSize;
	}

	public void setTitleFontSize(final IScope scope, final int value) {
		titleFontSize = value;
	}

	public int getTitleFontSize(final IScope scope) {
		return  titleFontSize;
	}

	public void setTickFontStyle(final IScope scope, final String value) {
		if (value != null) {
			tickFontStyle = toFontStyle(value);
		}
	}

	public int getTickFontStyle(final IScope scope) {
			return tickFontStyle;
	}

	public void setLabelFontStyle(final IScope scope, final String value) {
		if (value != null) {
			labelFontStyle = toFontStyle(value);
		}
	}

	public int getLabelFontStyle(final IScope scope) {
			return labelFontStyle;
	}

	public void setLegendFontStyle(final IScope scope, final String value) {
		if (value != null) {
			legendFontStyle = toFontStyle(value);
		}
	}

	public int getLegendFontStyle(final IScope scope) {
			return legendFontStyle;
	}

	public void setTitleFontStyle(final IScope scope, final String value) {
		if (value != null) {
			titleFontStyle = toFontStyle(value);
		}
	}

	public int getTitleFontStyle(final IScope scope) {
			return titleFontStyle;
	}

	public void setXLabel(final IScope scope, final String asString) {
		// TODO Auto-generated method stub
		xlabel = asString;

	}

	public String getXLabel(final IScope scope) {
		// TODO Auto-generated method stub
		return xlabel;

	}

	public void setYLabel(final IScope scope, final String asString) {
		// TODO Auto-generated method stub
		ylabel = asString;

	}

	public String getYLabel(final IScope scope) {
		// TODO Auto-generated method stub
		return ylabel;

	}
	public boolean getUseXRangeInterval(final IScope scope) {
		return usexrangeinterval;
	}

	public void setUseXRangeInterval(final IScope scope, boolean usexrangeinterval) {
		this.usexrangeinterval = usexrangeinterval;
	}

	public boolean getUseXRangeMinMax(final IScope scope ) {
		return usexrangeminmax;
	}

	public void setUseXRangeMinMax(final IScope scope, boolean usexrangeminmax) {
		this.usexrangeminmax = usexrangeminmax;
	}

	public boolean getUseYRangeInterval(final IScope scope ) {
		return useyrangeinterval;
	}

	public void setUseYRangeInterval(final IScope scope, boolean useyrangeinterval) {
		this.useyrangeinterval = useyrangeinterval;
	}

	public boolean getUseYRangeMinMax(final IScope scope ) {
		return useyrangeminmax;
	}

	public void setUseYRangeMinMax(final IScope scope, boolean useyrangeminmax) {
		this.useyrangeminmax = useyrangeminmax;
	}

	public void setXRangeInterval(final IScope scope, final double doubleValue) {
		// TODO Auto-generated method stub
		this.usexrangeinterval = true;
		this.xrangeinterval = doubleValue;

	}

	public double getXRangeInterval(final IScope scope) {
		// TODO Auto-generated method stub
		return xrangeinterval;

	}

	public void setXRangeMinMax(final IScope scope, final double minValue, final double maxValue) {
		// TODO Auto-generated method stub
		this.usexrangeminmax = true;
		this.xrangemin = minValue;
		this.xrangemax = maxValue;

	}

	public double[] getXRangeMinMax(final IScope scope) {
		// TODO Auto-generated method stub
		double[] res={xrangemin,xrangemax};
		return res;

	}
	public double getXRangeMin(final IScope scope) {
		// TODO Auto-generated method stub
		return xrangemin;

	}

	public double getXRangeMax(final IScope scope) {
		// TODO Auto-generated method stub
		return xrangemax;

	}

	public double getYRangeInterval(final IScope scope) {
		return this.yrangeinterval;
	}

	public void setYRangeInterval(final IScope scope, final double doubleValue) {
		// TODO Auto-generated method stub
		this.useyrangeinterval = true;
		this.yrangeinterval = doubleValue;

	}

	public void setYRangeMinMax(final IScope scope, final double minValue, final double maxValue) {
		// TODO Auto-generated method stub
		this.useyrangeminmax = true;
		this.yrangemin = minValue;
		this.yrangemax = maxValue;

	}
	public double[] getYRangeMinMax(final IScope scope) {
		// TODO Auto-generated method stub
		double[] res={yrangemin,yrangemax};
		return res;

	}

	public double getYRangeMin(final IScope scope) {
		// TODO Auto-generated method stub
		return yrangemin;

	}

	public double getYRangeMax(final IScope scope) {
		// TODO Auto-generated method stub
		return yrangemax;

	}


	public void setXTickUnit(final IScope scope, final double r) {
		this.xtickunit = r;
		// TODO Auto-generated method stub

	}

	public double getXTickUnit(final IScope scope) {
		return xtickunit;

	}

	public void setYTickUnit(final IScope scope, final double r) {
		this.ytickunit = r;
		// TODO Auto-generated method stub

	}

	public double getYTickUnit(final IScope scope) {
		return ytickunit;
		// TODO Auto-generated method stub

	}

	public void setGap(final IScope scope, final double range) {
		// TODO Auto-generated method stub
		this.gap = range;
	}

	public double getGap(final IScope scope) {
		// TODO Auto-generated method stub
		return this.gap;
	}

	public JFreeChart getJFChart() {
		return null;
	}

	public void setSerieMarkerShape(final IScope scope, final String serieid, final String markershape) {
		// TODO Auto-generated method stub

	}

	public void setDefaultPropertiesFromType(final IScope scope, final ChartDataSource source, final int type_val) {
		// TODO Auto-generated method stub

	}

	public void setUseSize(final IScope scope, final String name, final boolean b) {
		// TODO Auto-generated method stub

	}

	public void setSeriesLabelPosition(final IScope scope, final String asString) {
		// TODO Auto-generated method stub
		series_label_position = asString;

	}

	public void setStyle(final IScope scope, final String asString) {
		// TODO Auto-generated method stub
		style = asString;

	}

	public void initChart_post_data_init(final IScope scope) {
		// TODO Auto-generated method stub

	}

	public void getModelCoordinatesInfo(final int xOnScreen, final int yOnScreen, final IDisplaySurface g,
			final Point positionInPixels, final StringBuilder sb) {}

	public void setReverseAxis(final IScope scope, final Boolean asBool) {
		// TODO Auto-generated method stub
		reverse_axes = asBool;
	}

}
