package msi.gama.outputs.layers.charts;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

import org.jfree.chart.JFreeChart;
import org.jfree.data.general.Dataset;

public abstract class ChartOutput {

	 static final int SERIES_CHART = 0;
	 static final int HISTOGRAM_CHART = 1;
	 static final int PIE_CHART = 2;
	 static final int XY_CHART = 3;
	 static final int BOX_WHISKER_CHART = 4;
	static final int SCATTER_CHART = 5;

	LinkedHashMap<String,Integer> serieLastUpdate=new LinkedHashMap<String,Integer>();
	
	public int lastUpdateCycle=-1;
	
	String chname="";	
	ChartDataSet chartdataset;
	int type = SERIES_CHART;

	//copy from previous dataLayerStatement
	
	String style = IKeyword.DEFAULT;
	 ChartOutput chartOutput = null;
	 StringBuilder history;
	 static String chartFolder = "charts";
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
	 GamaColor backgroundColor = null, axesColor = null;

	 final Map<String, Integer> expressions_index = new HashMap();
	 boolean exploded=false;
	static String xAxisName = "'time'";
	
	
//	HashMap<String,Object> chartParameters=new HashMap<String,Object>();
	
	public abstract BufferedImage getImage(int sizex, int sizey);

	public ChartOutput(final IScope scope, String name,IExpression typeexp)
	{
			String t = Cast.asString(scope, typeexp.value(scope));
			type =
					IKeyword.SERIES.equals(t) ? SERIES_CHART : IKeyword.HISTOGRAM.equals(t) ? HISTOGRAM_CHART
							: IKeyword.PIE.equals(t) ? PIE_CHART : IKeyword.BOX_WHISKER.equals(t) ? BOX_WHISKER_CHART
									: IKeyword.SCATTER.equals(t) ? SCATTER_CHART : XY_CHART;
			axesColor=new GamaColor(Color.black);
	}
	
	
//	public ChartOutput(final IScope scope, String name,HashMap<String,Object> params)
//	{
//
//		axesColor = new GamaColor(Color.black);
//		
//		chartParameters=params;
//		IExpression string1 = (IExpression) chartParameters.get(IKeyword.TYPE);
//		if ( string1 != null ) {
//			String t = Cast.asString(scope, string1.value(scope));
//			type =
//					IKeyword.SERIES.equals(t) ? SERIES_CHART : IKeyword.HISTOGRAM.equals(t) ? HISTOGRAM_CHART
//							: IKeyword.PIE.equals(t) ? PIE_CHART : IKeyword.BOX_WHISKER.equals(t) ? BOX_WHISKER_CHART
//									: IKeyword.SCATTER.equals(t) ? SCATTER_CHART : XY_CHART;
//
//		}
//		IExpression color = (IExpression) chartParameters.get(IKeyword.AXES);
//		if ( color != null ) {
//			axesColor = Cast.asColor(scope, color.value(scope));
//		}
//		IExpression color1 = (IExpression) chartParameters.get(IKeyword.BACKGROUND);
//		if ( color1 != null ) {
//			backgroundColor = Cast.asColor(scope, color1.value(scope));
//		}
//		IExpression string = (IExpression) chartParameters.get(IKeyword.STYLE);
//		if ( string != null ) {
//			style = Cast.asString(scope, string.value(scope));
//			// TODO Verifier style;
//		}
//		IExpression face = (IExpression) chartParameters.get(ChartLayerStatement.TICKFONTFACE);
//		if ( face != null ) {
//			tickFontFace = Cast.asString(scope, face.value(scope));
//		}
//		face = (IExpression) chartParameters.get(ChartLayerStatement.LABELFONTFACE);
//		if ( face != null ) {
//			labelFontFace = Cast.asString(scope, face.value(scope));
//		}
//		face = (IExpression) chartParameters.get(ChartLayerStatement.LEGENDFONTFACE);
//		if ( face != null ) {
//			legendFontFace = Cast.asString(scope, face.value(scope));
//		}
//		face = (IExpression) chartParameters.get(ChartLayerStatement.TITLEFONTFACE);
//		if ( face != null ) {
//			titleFontFace = Cast.asString(scope, face.value(scope));
//		}
//		face = (IExpression) chartParameters.get(ChartLayerStatement.TICKFONTSIZE);
//		if ( face != null ) {
//			tickFontSize = Cast.asInt(scope, face.value(scope));
//		}
//		face = (IExpression) chartParameters.get(ChartLayerStatement.LABELFONTSIZE);
//		if ( face != null ) {
//			labelFontSize = Cast.asInt(scope, face.value(scope));
//		}
//		face = (IExpression) chartParameters.get(ChartLayerStatement.LEGENDFONTSIZE);
//		if ( face != null ) {
//			legendFontSize = Cast.asInt(scope, face.value(scope));
//		}
//		face = (IExpression) chartParameters.get(ChartLayerStatement.TITLEFONTSIZE);
//		if ( face != null ) {
//			titleFontSize = Cast.asInt(scope, face.value(scope));
//		}
//		face = (IExpression) chartParameters.get(ChartLayerStatement.TICKFONTSTYLE);
//		if ( face != null ) {
//			tickFontStyle = toFontStyle(Cast.asString(scope, face.value(scope)));
//		}
//		face = (IExpression) chartParameters.get(ChartLayerStatement.LABELFONTSTYLE);
//		if ( face != null ) {
//			labelFontStyle = toFontStyle(Cast.asString(scope, face.value(scope)));
//		}
//		face = (IExpression) chartParameters.get(ChartLayerStatement.LEGENDFONTSTYLE);
//		if ( face != null ) {
//			legendFontStyle = toFontStyle(Cast.asString(scope, face.value(scope)));
//		}
//		face = (IExpression) chartParameters.get(ChartLayerStatement.TITLEFONTSTYLE);
//		if ( face != null ) {
//			titleFontStyle = toFontStyle(Cast.asString(scope, face.value(scope)));
//		}
//		chartParameters.put(ChartLayerStatement.TITLEFONTSTYLE, face);
//	}
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
		updateOutput(scope);
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
		}
		else
		{
			LinkedHashMap<String,Integer> toremove=chartdataset.getSerieRemovalDate();
			for (String serieid:toremove.keySet())
			{
				if (toremove.get(serieid)>lastUpdateCycle)
					removeSerie(scope,serieid);
			}
			LinkedHashMap<String,Integer> toadd=chartdataset.getSerieCreationDate();
			for (String serieid:toadd.keySet())
			{
				if (toadd.get(serieid)>lastUpdateCycle)
				{
					createNewSerie(scope,serieid);					
				}
			}
			for (String serieid:chartdataset.getDataSeriesIds(scope))
			{
				this.resetSerie(scope,serieid);
			}
			
		}
		updateImage(scope);		
		
		lastUpdateCycle=scope.getClock().getCycle();
		System.out.println("output last update:"+lastUpdateCycle);
	}
	
	private void removeSerie(IScope scope, String serieid) {
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

	public void setAxesColorValue(IScope scope, GamaColor color) {
			axesColor = color;
		
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

}
