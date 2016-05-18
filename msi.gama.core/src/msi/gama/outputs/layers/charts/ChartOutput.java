package msi.gama.outputs.layers.charts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

import org.apache.commons.lang.StringUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.Dataset;

public abstract class ChartOutput {

	 static final int SERIES_CHART = 0;
	 static final int HISTOGRAM_CHART = 1;
	 static final int PIE_CHART = 2;
	 static final int XY_CHART = 3;
	 static final int BOX_WHISKER_CHART = 4;
	static final int SCATTER_CHART = 5;
	 static final int RADAR_CHART = 6;
	 static final int HEATMAP_CHART = 7;

	LinkedHashMap<String,Integer> serieLastUpdate=new LinkedHashMap<String,Integer>();
	
	public int lastUpdateCycle=-1;
	
	String chname="";	
	String xlabel=null;
	String ylabel=null;
	ChartDataSet chartdataset;
	int type = SERIES_CHART;

	ChartOutput chartOutput = null;
	 GamaColor backgroundColor = null, axesColor = null, textColor=null;

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
	 
	 String series_label_position="default";
	String style = IKeyword.DEFAULT;
	 
	 double gap=-1; //only used in bar charts? copied the code, don't understand how to use it...
	 
	 double xrangeinterval,xrangemin,xrangemax;
	 boolean usexrangeinterval=false, usexrangeminmax=false;
	 double yrangeinterval,yrangemin,yrangemax;
	 boolean useyrangeinterval=false, useyrangeminmax=false;

	double xtickunit=-1;
	double ytickunit=-1;

	//copy from previous dataLayerStatement
	
	 StringBuilder history;
	 static String chartFolder = "charts";

	 final Map<String, Integer> expressions_index = new HashMap();
	static String xAxisName = "'time'";
	
	
//	HashMap<String,Object> chartParameters=new HashMap<String,Object>();
	
	public abstract BufferedImage getImage(IScope scope, int sizex, int sizey);

	public ChartOutput(final IScope scope, String name,IExpression typeexp)
	{
			String t = Cast.asString(scope, typeexp.value(scope));
			type =
					IKeyword.SERIES.equals(t) ? SERIES_CHART : IKeyword.HISTOGRAM.equals(t) ? HISTOGRAM_CHART
							: IKeyword.RADAR.equals(t) ? RADAR_CHART
							: IKeyword.PIE.equals(t) ? PIE_CHART : IKeyword.BOX_WHISKER.equals(t) ? BOX_WHISKER_CHART
									: IKeyword.SCATTER.equals(t) ? SCATTER_CHART : XY_CHART;
			axesColor=new GamaColor(Color.black);
	}
	
	 public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public int getChartCycle(IScope scope)
	{
		return scope.getClock().getCycle();
	}
	
	public void step(IScope scope)
	{
		chartdataset.updatedataset(scope,getChartCycle(scope));
	}

	public void initdataset()
	{
		
	}
	
	public void updateOutput(IScope scope)
	{
		if (chartdataset.doResetAll(scope,lastUpdateCycle))
		{
			clearDataSet(scope);
			for (String serieid:chartdataset.getDataSeriesIds(scope))
			{
				createNewSerie(scope,serieid);
			}
			for (String serieid:chartdataset.getDataSeriesIds(scope))
			{
				this.resetSerie(scope,serieid);
			}
			resetAxes(scope);

		}
		else
		{
			LinkedHashMap<String,Integer> toremove=chartdataset.getSerieRemovalDate();
			for (String serieid:toremove.keySet())
			{
				if (toremove.get(serieid)>=lastUpdateCycle)
				{
					removeSerie(scope,serieid);
					toremove.put(serieid,toremove.get(serieid)-1);
				}
			}
			LinkedHashMap<String,Integer> toadd=chartdataset.getSerieCreationDate();
			for (String serieid:toadd.keySet())
			{
				if (toadd.get(serieid)>=lastUpdateCycle)
				{
					createNewSerie(scope,serieid);
					toadd.put(serieid,toadd.get(serieid)-1);
				}
			}
			for (String serieid:chartdataset.getDataSeriesIds(scope))
			{
				this.resetSerie(scope,serieid);
			}
			resetAxes(scope);

			
		}
		updateImage(scope);		
		
		lastUpdateCycle=scope.getClock().getCycle();
		System.out.println("output last update:"+lastUpdateCycle);
	}
	
	public void resetAxes(IScope scope) {
		// Update axes
		
	}

	public void removeSerie(IScope scope, String serieid) {
		// TODO Auto-generated method stub
		
	}

	protected void updateImage(IScope scope) {
		// TODO Auto-generated method stub
		
		
	}

	protected void resetSerie(IScope scope, String serieid) {
		// TODO Auto-generated method stub
		
	}

	protected void clearDataSet(IScope scope) {
		// TODO Auto-generated method stub
		
	}

	protected void createNewSerie(IScope scope, String serieid) {
		// TODO Auto-generated method stub
		
	}
	
	public void setUseXSource(IScope scope, IExpression expval) {
		// if there is something to do to use custom X axis
		
		
	}
	public void setUseXLabels(IScope scope, IExpression expval) {
		// if there is something to do to use custom X axis
		
		
	}
	public void setUseYSource(IScope scope, IExpression expval) {
		// if there is something to do to use custom X axis
		
		
	}
	public void setUseYLabels(IScope scope, IExpression expval) {
		// if there is something to do to use custom X axis
		
		
	}

	int toFontStyle(final String style) {
		if ( style.equals("bold") ) { return Font.BOLD; }
		if ( style.equals("italic") ) { return Font.ITALIC; }
		return Font.PLAIN;
	}
	
	public  void initChart(IScope scope, String chartname)
	{
		history = new StringBuilder(500);
		chname=chartname;
		
	}

	public  void createChart(IScope scope)
	{
		
	}
	
	
	 public ChartDataSet getChartdataset() {
		return chartdataset;
	}

	public void setChartdataset(ChartDataSet chartdataset) {
		this.chartdataset = chartdataset;
		chartdataset.setOutput(this);
	}	
	public String getName()
	{
		return chname;
	}

	public String getStyle()
	{
		return style;
	}

	public void setAxesColorValue(IScope scope, GamaColor color) {
			axesColor = color;
		
	}
	
	public void setBackgroundColorValue(IScope scope, GamaColor color) {
		backgroundColor = color;
	
}

	public void setColorValue(IScope scope, GamaColor color) {
		textColor = color;
	
}

	public void setTickFontFace(IScope scope, String value) {
	if ( value != null ) {
		tickFontFace = value;
	}
	}
	public void setLabelFontFace(IScope scope, String value) {
	if ( value != null ) {
		labelFontFace =  value;
	}
	}
	public void setLegendFontFace(IScope scope, String value) {
	if ( value != null ) {
		legendFontFace = value;
	}
	}
	public void setTitleFontFace(IScope scope, String value) {
	if ( value != null ) {
		titleFontFace =  value;
	}
	}
	public void setTickFontSize(IScope scope, int value) {
		tickFontSize = value;
	}
	public void setLabelFontSize(IScope scope, int value) {
		labelFontSize =  value;
	}
	public void setLegendFontSize(IScope scope, int value) {
		legendFontSize =  value;
	}
	public void setTitleFontSize(IScope scope, int value) {
		titleFontSize =  value;
	}
	public void setTickFontStyle(IScope scope, String value) {
	if ( value != null ) {
		tickFontStyle = toFontStyle( value);
	}
	}
	public void setLabelFontStyle(IScope scope, String value) {
	if ( value != null ) {
		labelFontStyle = toFontStyle( value);
	}
	}
	public void setLegendFontStyle(IScope scope, String value) {
	if ( value != null ) {
		legendFontStyle = toFontStyle( value);
	}
	}
	public void setTitleFontStyle(IScope scope, String value) {
	if ( value != null ) {
		titleFontStyle = toFontStyle( value);
	}
	}

	public void setXLabel(IScope scope, String asString) {
		// TODO Auto-generated method stub
		xlabel=asString;
		
	}
	public void setYLabel(IScope scope, String asString) {
		// TODO Auto-generated method stub
		ylabel=asString;
		
	}
	
	public void setXRangeInterval(IScope scope, double doubleValue) {
		// TODO Auto-generated method stub
		this.usexrangeinterval=true;
		this.xrangeinterval=doubleValue;
		
	}
	
	public void setXRangeMinMax(IScope scope, double minValue, double maxValue) {
		// TODO Auto-generated method stub
		this.usexrangeminmax=true;
		this.xrangemin=minValue;
		this.xrangemax=maxValue;
		
	}

	public void setYRangeInterval(IScope scope, double doubleValue) {
		// TODO Auto-generated method stub
		this.useyrangeinterval=true;
		this.yrangeinterval=doubleValue;
		
	}
	
	public void setYRangeMinMax(IScope scope, double minValue, double maxValue) {
		// TODO Auto-generated method stub
		this.useyrangeminmax=true;
		this.yrangemin=minValue;
		this.yrangemax=maxValue;
		
	}
	public void setXTickUnit(IScope scope, double r) {
		this.xtickunit=r;
		// TODO Auto-generated method stub
		
	}

	public void setYTickUnit(IScope scope, double r) {
		this.ytickunit=r;
		// TODO Auto-generated method stub
		
	}
	public void setGap(IScope scope, double range) {
		// TODO Auto-generated method stub
		this.gap=range;
	}

	public JFreeChart getJFChart()
	{
		return null;
	}

	public void setSerieMarkerShape(IScope scope, String serieid, String markershape) {
		// TODO Auto-generated method stub
		
	}

	public void setDefaultPropertiesFromType(IScope scope, ChartDataSource source, Object o, int type_val) {
		// TODO Auto-generated method stub
		
	}



	public void setUseSize(IScope scope, String name, boolean b) {
		// TODO Auto-generated method stub
		
	}

	public void setSeriesLabelPosition(IScope scope, String asString) {
		// TODO Auto-generated method stub
		series_label_position=asString;
		
	}

	public void setStyle(IScope scope, String asString) {
		// TODO Auto-generated method stub
		style=asString;
		
	}

	public void initChart_post_data_init(IScope scope) {
		// TODO Auto-generated method stub
		
	}

	public String getModelCoordinatesInfo(final int xOnScreen, final int yOnScreen, final IDisplaySurface g, Point positionInPixels) {
		return "";
	}




}
