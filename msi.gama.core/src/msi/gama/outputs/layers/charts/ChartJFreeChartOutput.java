package msi.gama.outputs.layers.charts;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.ui.RectangleInsets;

public class ChartJFreeChartOutput extends ChartOutput {


	
	
	 ArrayList<Dataset> jfreedataset=new ArrayList<Dataset>();
	 JFreeChart chart = null;
	 AbstractRenderer defaultrenderer;
	 HashMap<String,Integer> IdPosition=new HashMap<String,Integer>(); //serie id-nb for arraylists/table requirements

		public ChartJFreeChartOutput(final IScope scope,String name,IExpression typeexp)
		{
			super(scope, name,typeexp);
		}
	 
	 
	public static ChartJFreeChartOutput createChartOutput(final IScope scope,String name,IExpression typeexp)
	{
		ChartJFreeChartOutput newChart;
		int type=SERIES_CHART;

		IExpression string1 = typeexp;
		if ( string1 != null ) {
			String t = Cast.asString(scope, string1.value(scope));
			type =
					IKeyword.SERIES.equals(t) ? SERIES_CHART : IKeyword.HISTOGRAM.equals(t) ? HISTOGRAM_CHART
							: IKeyword.PIE.equals(t) ? PIE_CHART : IKeyword.BOX_WHISKER.equals(t) ? BOX_WHISKER_CHART
									: IKeyword.SCATTER.equals(t) ? SCATTER_CHART : XY_CHART;

		}
		

		switch (type) {
		case SERIES_CHART: {
			newChart = new ChartJFreeChartOutputScatter(scope,name,typeexp);
			break;
		}
		case PIE_CHART: {
			newChart = new ChartJFreeChartOutputPie(scope,name,typeexp);
			break;
		}
		case HISTOGRAM_CHART: {
			newChart = new ChartJFreeChartOutputHistogram(scope,name,typeexp);
			break;
		}
		case XY_CHART:
			newChart = new ChartJFreeChartOutputScatter(scope,name,typeexp);
			break;
		case SCATTER_CHART:
			newChart = new ChartJFreeChartOutputScatter(scope,name,typeexp);
			break;
		case BOX_WHISKER_CHART: {
			newChart = new ChartJFreeChartOutputHistogram(scope,name,typeexp);
			break;
		}
		default:
		{
			newChart = new ChartJFreeChartOutputScatter(scope,name,typeexp);			
		}
		}
		return newChart;
	}

	

	public BufferedImage getImage(int sizex, int sizey)
	{
		return chart.createBufferedImage(sizex, sizey);
	}
	
	
	public void initChart(IScope scope, String chartname)
	{
		super.initChart(scope,chartname);
		
		//not yet updated, move to subclasses

		initRenderer(scope);
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
	

	
	protected void initRenderer(IScope scope) {
		// TODO Auto-generated method stub
		
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
	
	protected void updateImage(IScope scope) {
		// TODO Auto-generated method stub
	}

	public JFreeChart getJFChart()
	{
		return chart;
	}
	
	
	
}
