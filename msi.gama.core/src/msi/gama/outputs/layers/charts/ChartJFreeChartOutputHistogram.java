package msi.gama.outputs.layers.charts;

import java.awt.Color;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.AbstractCategoryItemRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.DefaultIntervalCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.outputs.layers.charts.ChartJFreeChartOutputScatter.myXYErrorRenderer;
import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;

public class ChartJFreeChartOutputHistogram extends ChartJFreeChartOutput {

	public ChartJFreeChartOutputHistogram(IScope scope, String name,
			IExpression typeexp) {
		super(scope, name, typeexp);
		// TODO Auto-generated constructor stub

		jfreedataset.add(0,new DefaultCategoryDataset());
		
		if ( style.equals(IKeyword.THREE_D) ) {
			chart =
				ChartFactory.createBarChart3D(getName(), null, null, null, PlotOrientation.VERTICAL, true,
					true, false);
		} else if ( style.equals(IKeyword.STACK) ) {
			chart =
				ChartFactory.createStackedBarChart(getName(), null, null, null, PlotOrientation.VERTICAL, true,
					true, false);
		} else {
			chart =
				ChartFactory.createBarChart(getName(), null, null, null, PlotOrientation.VERTICAL, true, true,
					false);
		}		
	}
	public void initdataset()
	{
		super.initdataset();
		if (getType()==ChartOutput.HISTOGRAM_CHART)
		{
			chartdataset.setCommonXSeries(true);
			chartdataset.setByCategory(true);
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
			case ChartDataSource.DATA_TYPE_LIST_DOUBLE_3:
			case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_3:
			{
				source.setCumulative(scope,false);
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
	
	

	Dataset createDataset(IScope scope)
	{
		return new DefaultCategoryDataset();
	}

	protected AbstractRenderer createRenderer(IScope scope,String serieid)
	{
		String style=this.getChartdataset().getDataSeries(scope, serieid).getStyle(scope);
		AbstractRenderer newr=new BarRenderer();
		switch (style)
		{
		case IKeyword.STACK:
		{
			newr=new StackedBarRenderer();
			break;
		
		}
		case IKeyword.THREE_D:
		{
			newr=new BarRenderer3D();
			break;
		
		}
		case IKeyword.WHISKER:
		case IKeyword.AREA:
		case IKeyword.BAR:
		case IKeyword.STEP:
		case IKeyword.RING:
		case IKeyword.EXPLODED:
		default: 
		{
			newr=new BarRenderer();
			break;
		
		}
		}
		return newr;
	}

	protected void resetRenderer(IScope scope,String serieid)
	{
		AbstractCategoryItemRenderer newr=(AbstractCategoryItemRenderer)this.getOrCreateRenderer(scope, serieid);

//		newr.setSeriesStroke(0, new BasicStroke(0));
		ChartDataSeries myserie=this.getChartdataset().getDataSeries(scope, serieid);

	}
	

	protected void clearDataSet(IScope scope) {
		// TODO Auto-generated method stub
		super.clearDataSet(scope);
        CategoryPlot plot = (CategoryPlot)this.chart.getPlot();
        for (int i=plot.getDatasetCount()-1; i>=1; i--)
        {
        	plot.setDataset(i, null);
        	plot.setRenderer(i, null);
        }
		((DefaultCategoryDataset)jfreedataset.get(0)).clear();
		jfreedataset.clear();
		jfreedataset.add(0,new DefaultCategoryDataset());
		plot.setDataset((DefaultCategoryDataset)jfreedataset.get(0));
    	plot.setRenderer(0, null);
		IdPosition.clear();
		nbseries=0;
	}

	
	protected void createNewSerie(IScope scope, String serieid) {
		ChartDataSeries dataserie=chartdataset.getDataSeries(scope,serieid);
//		final XYIntervalSeries serie = new XYIntervalSeries(dataserie.getSerieLegend(scope), false, true);		
        CategoryPlot plot = (CategoryPlot)this.chart.getPlot();
		
        DefaultCategoryDataset firstdataset=(DefaultCategoryDataset)plot.getDataset();
		
		if (nbseries==0 )
		{
			plot.setDataset(0, firstdataset);
			
		}
		else
		{

			DefaultCategoryDataset newdataset=new DefaultCategoryDataset();
			jfreedataset.add(newdataset);
			plot.setDataset(jfreedataset.size()-1, newdataset);
			
		}
		nbseries++;
    	plot.setRenderer(jfreedataset.size()-1, (CategoryItemRenderer)getOrCreateRenderer(scope,serieid));
		IdPosition.put(serieid, jfreedataset.size()-1);
		System.out.println("new serie"+serieid+" at "+IdPosition.get(serieid)+" fdsize "+plot.getCategories().size()+" jfds "+jfreedataset.size()+" datasc "+plot.getDatasetCount()+" nbse "+nbseries);
		// TODO Auto-generated method stub		
	}


	
	protected void resetSerie(IScope scope, String serieid) {
		// TODO Auto-generated method stub
		
		ChartDataSeries dataserie=chartdataset.getDataSeries(scope,serieid);
		DefaultCategoryDataset serie=((DefaultCategoryDataset) jfreedataset.get(IdPosition.get(dataserie.getSerieId(scope))));
		serie.clear();
		ArrayList<String> CValues=dataserie.getCValues(scope);
		ArrayList<Double> YValues=dataserie.getYValues(scope);
		ArrayList<Double> SValues=dataserie.getSValues(scope);
		
		if (CValues.size()>0)
		{
			// TODO Hack to speed up, change!!!
			final CategoryAxis domainAxis = (CategoryAxis) ((CategoryPlot)this.chart.getPlot()).getDomainAxis();
			final NumberAxis rangeAxis = (NumberAxis) ((CategoryPlot)this.chart.getPlot()).getRangeAxis();
			rangeAxis.setAutoRange(false);
		for(int i=0; i<CValues.size(); i++)
		{
				serie.addValue(YValues.get(i), serieid, CValues.get(i)); 
		}
		rangeAxis.setAutoRange(true);
		}
		if (SValues.size()>0)
		{
			//what to do with Z values??
			
		}

		this.resetRenderer(scope, serieid); 
				
	}
	protected void initRenderer(IScope scope) {
		// TODO Auto-generated method stub
        CategoryPlot plot = (CategoryPlot)this.chart.getPlot();
        defaultrenderer = new BarRenderer();
        plot.setRenderer((BarRenderer)defaultrenderer);
		
		
	}

}
