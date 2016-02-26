package msi.gama.outputs.layers.charts;

import java.awt.Color;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.AbstractCategoryItemLabelGenerator;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
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
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.DefaultIntervalCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.jfree.ui.TextAnchor;

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
				source.setUseSize(scope,false);				
				break;				
				
			}
			default:
			{
				source.setCumulative(scope,false); // never cumulative by default				
				source.setUseSize(scope,false);				
			}
		}
			
		

		
	}
	
	

	Dataset createDataset(IScope scope)
	{
		return new DefaultCategoryDataset();
	}
	
  static class LabelGenerator extends StandardCategoryItemLabelGenerator
  		implements CategoryItemLabelGenerator
  {
    /**
     * Generates an item label.
     * 
     * @param dataset  the dataset.
     * @param series  the series index.
     * @param category  the category index.
     * 
     * @return the label.
     */
      public String generateLabel(final CategoryDataset dataset, 
                                    final int series, 
                                      final int category) {
        return dataset.getRowKey(series).toString();
    }
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
//		AbstractCategoryItemRenderer newr=(AbstractCategoryItemRenderer)this.getOrCreateRenderer(scope, serieid);
        CategoryPlot plot = (CategoryPlot)this.chart.getPlot();
		AbstractCategoryItemRenderer newr=(AbstractCategoryItemRenderer)plot.getRenderer();
		
		ChartDataSeries myserie=this.getChartdataset().getDataSeries(scope, serieid);
		int myrow=IdPosition.get(serieid);
		if (myserie.getMycolor()!=null)
		{
			newr.setSeriesPaint(myrow,myserie.getMycolor());
		}

		((BarRenderer)newr).setBaseItemLabelGenerator(new LabelGenerator());
        ItemLabelPosition itemlabelposition = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER);
        newr.setBasePositiveItemLabelPosition(itemlabelposition);
        newr.setBaseNegativeItemLabelPosition(itemlabelposition);
        newr.setBaseItemLabelsVisible(true);

		
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
	    	plot.setRenderer(nbseries, (CategoryItemRenderer)getOrCreateRenderer(scope,serieid));
			
		}
		else
		{

//			DefaultCategoryDataset newdataset=new DefaultCategoryDataset();
//			jfreedataset.add(newdataset);
//			plot.setDataset(jfreedataset.size()-1, newdataset);
//			plot.setDataset(nbseries, firstdataset);
			
		}
		nbseries++;
//    	plot.setRenderer(nbseries-1, (CategoryItemRenderer)getOrCreateRenderer(scope,serieid));
		IdPosition.put(serieid, nbseries-1);
		System.out.println("new serie"+serieid+" at "+IdPosition.get(serieid)+" fdsize "+plot.getCategories().size()+" jfds "+jfreedataset.size()+" datasc "+plot.getDatasetCount()+" nbse "+nbseries);
		// TODO Auto-generated method stub		
	}


	
	protected void resetSerie(IScope scope, String serieid) {
		// TODO Auto-generated method stub
		
		ChartDataSeries dataserie=chartdataset.getDataSeries(scope,serieid);
//		DefaultCategoryDataset serie=((DefaultCategoryDataset) jfreedataset.get(IdPosition.get(dataserie.getSerieId(scope))));
		DefaultCategoryDataset serie=((DefaultCategoryDataset) jfreedataset.get(0));
		if (serie.getRowKeys().contains(serieid))
			serie.removeRow(serieid);
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
	
	public void initChart(IScope scope, String chartname)
	{
		super.initChart(scope, chartname);
		final CategoryPlot pp = (CategoryPlot) chart.getPlot();
		pp.setDomainGridlinePaint(axesColor);
		pp.setRangeGridlinePaint(axesColor);
		pp.setRangeCrosshairVisible(true);
		
	}
	
	protected void initRenderer(IScope scope) {
		// TODO Auto-generated method stub
//        CategoryPlot plot = (CategoryPlot)this.chart.getPlot();
 //       defaultrenderer = new BarRenderer();
  //      plot.setRenderer((BarRenderer)defaultrenderer);
		
		
	}

}
