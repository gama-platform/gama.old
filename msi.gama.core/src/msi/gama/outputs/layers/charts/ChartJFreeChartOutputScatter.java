package msi.gama.outputs.layers.charts;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

public class ChartJFreeChartOutputScatter extends ChartJFreeChartOutput {

	public class myXYErrorRenderer extends XYErrorRenderer{
		
		ChartJFreeChartOutputScatter myoutput;
		String myid;
		boolean useSize;
		AffineTransform transform = new AffineTransform();
		
		public boolean isUseSize() {
			return useSize;
		}

		public void setUseSize(IScope scope, boolean useSize) {
			this.useSize = useSize;

		}

		public void setMyid(String myid) {
			this.myid = myid;
		}

		private static final long serialVersionUID = 1L;

			public void setOutput(ChartJFreeChartOutput output)
			{
				myoutput=(ChartJFreeChartOutputScatter) output;
			}
		
			@Override
	        public Shape getItemShape(int row, int col) {
	            if (isUseSize()) {
	            	transform.setToScale(myoutput.getScale(myid, col), myoutput.getScale(myid, col));
	                return transform.createTransformedShape(super.getItemShape(row, col));
	            } else {
	                return super.getItemShape(row, col);
	            }
	        }
	    }
	
	double getScale(String serie, int col)
	{
		if (MarkerScale.containsKey(serie))
		{
			return MarkerScale.get(serie).get(col);
		}
		else
		{
			return 1;
		}
	}
	
	HashMap<String,ArrayList<Double>> MarkerScale=new HashMap<String,ArrayList<Double>>();
	
	public ChartJFreeChartOutputScatter(IScope scope, String name,
			IExpression typeexp) {
		super(scope, name, typeexp);
		// TODO Auto-generated constructor stub
		
		jfreedataset.add(0,new XYIntervalSeriesCollection());
		
   		switch (type) {
		case SERIES_CHART: {
			chart =
				ChartFactory.createXYLineChart(name, "time", "", (XYIntervalSeriesCollection)(jfreedataset.get(0)), PlotOrientation.VERTICAL, true, false,
					false);
			break;
		}

		case XY_CHART:
			chart =
				ChartFactory.createXYLineChart(getName(), "", "", (XYIntervalSeriesCollection)jfreedataset.get(0), PlotOrientation.VERTICAL, true, false,
					false);
			break;
		case SCATTER_CHART:
			chart =
			ChartFactory.createXYLineChart(getName(), "", "", (XYIntervalSeriesCollection)jfreedataset.get(0), PlotOrientation.VERTICAL, true, false,
				false);
			break;
		case BOX_WHISKER_CHART: {
			chart =
				ChartFactory.createBoxAndWhiskerChart(getName(), "Time", "Value",
					(BoxAndWhiskerCategoryDataset) jfreedataset.get(0), true);
			chart.setBackgroundPaint(new Color(249, 231, 236));

			break;
		}    		
		
	}
		
   		
	}

	public void setDefaultPropertiesFromType(IScope scope, ChartDataSource source, Object o, int type_val) {
		// TODO Auto-generated method stub

		switch (type_val)
		{
			case ChartDataSource.DATA_TYPE_LIST_DOUBLE_N:
			case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_N:
			case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_12:
			case ChartDataSource.DATA_TYPE_LIST_POINT:
			case ChartDataSource.DATA_TYPE_MATRIX_DOUBLE:
			{
				source.setCumulative(scope,false);
				source.setUseSize(scope,false);				
				break;				
			}
			case ChartDataSource.DATA_TYPE_LIST_DOUBLE_3:
			{
				source.setCumulative(scope,true);
				source.setUseSize(scope,true);				
				break;				
				
			}
			default:
			{
				source.setCumulative(scope,true);				
				source.setUseSize(scope,false);				
			}
		}
			
		

		
	}
	
	
	public void initdataset()
	{
		super.initdataset();
		if (getType()==ChartOutput.SERIES_CHART)
		{
			chartdataset.setCommonXSeries(true);
			chartdataset.setByCategory(false);
		}
		if (getType()==ChartOutput.XY_CHART)
		{
			chartdataset.setCommonXSeries(false);
			chartdataset.setByCategory(false);			
		}
		if (getType()==ChartOutput.SCATTER_CHART)
		{
			chartdataset.setCommonXSeries(false);
			chartdataset.setByCategory(false);			
		}
	}
	
	Dataset createDataset(IScope scope)
	{
		return new XYIntervalSeriesCollection();
	}

	protected AbstractRenderer createRenderer(IScope scope,String serieid)
	{
		myXYErrorRenderer newr=new myXYErrorRenderer();
		newr.setMyid(serieid);
		newr.setOutput(this);
		return newr;
	}

	

	protected void clearDataSet(IScope scope) {
		// TODO Auto-generated method stub
		super.clearDataSet(scope);
        XYPlot plot = (XYPlot)this.chart.getPlot();
        for (int i=plot.getSeriesCount()-1; i>=1; i--)
        {
        	plot.setDataset(i, null);
        	plot.setRenderer(i, null);
        }
		((XYIntervalSeriesCollection)jfreedataset.get(0)).removeAllSeries();
    	plot.setRenderer(0, null);
		IdPosition.clear();
	}

	
	protected void createNewSerie(IScope scope, String serieid) {
		ChartDataSeries dataserie=chartdataset.getDataSeries(scope,serieid);
		final XYIntervalSeries serie = new XYIntervalSeries(dataserie.getSerieLegend(scope), false, true);		
        XYPlot plot = (XYPlot)this.chart.getPlot();
		
        XYIntervalSeriesCollection newdataset=(XYIntervalSeriesCollection)createDataset(scope);
		XYIntervalSeriesCollection firstdataset=(XYIntervalSeriesCollection)plot.getDataset();
		
		firstdataset.addSeries(serie);
		newdataset.addSeries(serie);
		jfreedataset.add(newdataset);
		plot.setDataset(jfreedataset.size()-1, newdataset);
    	plot.setRenderer(jfreedataset.size()-1, (XYErrorRenderer)getOrCreateRenderer(scope,serieid));
		IdPosition.put(serieid, plot.getSeriesCount()-1);
		System.out.println("new serie"+serieid+" at "+IdPosition.get(serieid)+" fdsize "+firstdataset.getSeriesCount());
		// TODO Auto-generated method stub		
	}


	
	protected void resetSerie(IScope scope, String serieid) {
		// TODO Auto-generated method stub
		
		ChartDataSeries dataserie=chartdataset.getDataSeries(scope,serieid);
		XYIntervalSeries serie=((XYIntervalSeriesCollection) jfreedataset.get(IdPosition.get(dataserie.getSerieId(scope)))).getSeries(0);
		serie.clear();
		ArrayList<Double> XValues=dataserie.getXValues(scope);
		ArrayList<Double> YValues=dataserie.getYValues(scope);
		ArrayList<Double> SValues=dataserie.getSValues(scope);
		if (XValues.size()>0)
		for(int i=0; i<XValues.size(); i++)
		{
				serie.add(XValues.get(i),XValues.get(i),XValues.get(i),YValues.get(i),YValues.get(i),YValues.get(i));			
		}
		if (SValues.size()>0)
		{
			MarkerScale.remove(serieid);
			ArrayList<Double> nscale=(ArrayList<Double>) SValues.clone();
			MarkerScale.put(serieid, nscale);
			
		}
				
	}
	public void setSerieMarkerShape(IScope scope, String serieid, String markershape) {
		XYErrorRenderer serierenderer=(XYErrorRenderer) getOrCreateRenderer(scope,serieid);
		if ( markershape != null ) {
		if (markershape.equals(ChartDataStatement.MARKER_EMPTY))
		{
			serierenderer.setSeriesShapesVisible(0, false);			
		}
		else
		{
			Shape myshape=defaultmarkers[0];
			if ( markershape.equals(ChartDataStatement.MARKER_CIRCLE) ) {
				myshape=defaultmarkers[1];
			} else if ( markershape.equals(ChartDataStatement.MARKER_UP_TRIANGLE) ) {
				myshape=defaultmarkers[2];
			} else if ( markershape.equals(ChartDataStatement.MARKER_DIAMOND) ) {
				myshape=defaultmarkers[3];
			} else if ( markershape.equals(ChartDataStatement.MARKER_HOR_RECTANGLE) ) {
				myshape=defaultmarkers[4];
			} else if ( markershape.equals(ChartDataStatement.MARKER_DOWN_TRIANGLE) ) {
				myshape=defaultmarkers[5];
			} else if ( markershape.equals(ChartDataStatement.MARKER_HOR_ELLIPSE) ) {
				myshape=defaultmarkers[6];
			} else if ( markershape.equals(ChartDataStatement.MARKER_RIGHT_TRIANGLE) ) {
				myshape=defaultmarkers[7];
			} else if ( markershape.equals(ChartDataStatement.MARKER_VERT_RECTANGLE) ) {
				myshape=defaultmarkers[8];
			} else if ( markershape.equals(ChartDataStatement.MARKER_LEFT_TRIANGLE) ) {
				myshape=defaultmarkers[9];
			} 
			serierenderer.setSeriesShape(0, myshape);
			
		}
		}
			
		
	}	
	
	public void setUseSize(IScope scope, String name, boolean b) {
		// TODO Auto-generated method stub
		myXYErrorRenderer serierenderer=(myXYErrorRenderer) getOrCreateRenderer(scope,name);
		serierenderer.setUseSize(scope,b);
	}
	
	
	protected void initRenderer(IScope scope) {
		// TODO Auto-generated method stub
        XYPlot plot = (XYPlot)this.chart.getPlot();
        defaultrenderer = new myXYErrorRenderer();
        plot.setRenderer((myXYErrorRenderer)defaultrenderer);
		
		
	}


}
