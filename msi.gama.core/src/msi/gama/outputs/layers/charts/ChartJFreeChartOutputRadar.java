package msi.gama.outputs.layers.charts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ExtendedCategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.SubCategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.AbstractCategoryItemLabelGenerator;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.AbstractCategoryItemRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LevelRenderer;
import org.jfree.chart.renderer.category.ScatterRenderer;
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StatisticalLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.DefaultIntervalCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.jfree.ui.TextAnchor;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.outputs.layers.charts.ChartJFreeChartOutputScatter.myXYErrorRenderer;
import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;

public class ChartJFreeChartOutputRadar extends ChartJFreeChartOutput {

	
	public ChartJFreeChartOutputRadar(IScope scope, String name,
			IExpression typeexp) {
		super(scope, name, typeexp);
		// TODO Auto-generated constructor stub

	}
	
	public void createChart(IScope scope)
	{
		super.createChart(scope);
		SpiderWebPlot plot = new SpiderWebPlot((CategoryDataset) createDataset(scope));
		chart = new JFreeChart(getName(), null, plot, true);
				
	}
	
	public void initdataset()
	{
		super.initdataset();
		chartdataset.setCommonXSeries(true);
		chartdataset.setByCategory(true);
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
		
	protected AbstractRenderer createRenderer(IScope scope,String serieid)
	{
		return null;
	}

	protected void resetRenderer(IScope scope,String serieid)
	{
        SpiderWebPlot plot = (SpiderWebPlot)this.chart.getPlot();
		
		ChartDataSeries myserie=this.getChartdataset().getDataSeries(scope, serieid);
		if (!IdPosition.containsKey(serieid))
		{
		System.out.println("pb!!!");	
		}
		else
		{
		int myrow=IdPosition.get(serieid);
		if (myserie.getMycolor()!=null)
		{
			plot.setSeriesPaint(myrow,myserie.getMycolor());
		}

		if (this.series_label_position.equals("onchart"))
		{
////			newr.setBaseItemLabelGenerator(new LabelGenerator());
//	        ItemLabelPosition itemlabelposition = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER);
//	        newr.setBasePositiveItemLabelPosition(itemlabelposition);
//	        newr.setBaseNegativeItemLabelPosition(itemlabelposition);
//	        newr.setBaseItemLabelsVisible(true);
		}

		}
		
	}
	

	protected void clearDataSet(IScope scope) {
		// TODO Auto-generated method stub
		super.clearDataSet(scope);
        SpiderWebPlot plot = (SpiderWebPlot)this.chart.getPlot();
        for (int i=plot.getDataset().getRowCount()-1; i>=1; i--)
        {
//        	plot.setDataset(i, null);
 //       	plot.setRenderer(i, null);
        }
        if (jfreedataset.size()>0)
        	((DefaultCategoryDataset)jfreedataset.get(0)).clear();
		jfreedataset.clear();
		jfreedataset.add(0,new DefaultCategoryDataset());
		plot.setDataset((DefaultCategoryDataset)jfreedataset.get(0));
		IdPosition.clear();
		nbseries=0;
	}

	
	protected void createNewSerie(IScope scope, String serieid) {
		ChartDataSeries dataserie=chartdataset.getDataSeries(scope,serieid);
//		final XYIntervalSeries serie = new XYIntervalSeries(dataserie.getSerieLegend(scope), false, true);		
        SpiderWebPlot plot = (SpiderWebPlot)this.chart.getPlot();
		
        DefaultCategoryDataset firstdataset=(DefaultCategoryDataset)plot.getDataset();
		
		if (nbseries==0 )
		{
			plot.setDataset(firstdataset);
			
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

		
//		System.out.println("new serie"+serieid+" at "+IdPosition.get(serieid)+" fdsize "+plot.getCategories().size()+" jfds "+jfreedataset.size()+" datasc "+plot.getDatasetCount()+" nbse "+nbseries);
		// TODO Auto-generated method stub		
	}


	public void removeSerie(IScope scope, String serieid) {
		// TODO Auto-generated method stub
		super.removeSerie(scope, serieid);
		this.clearDataSet(scope);
	}
	
	
	protected void resetSerie(IScope scope, String serieid) {
		// TODO Auto-generated method stub
		
        SpiderWebPlot plot = (SpiderWebPlot)this.chart.getPlot();
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
		for(int i=0; i<CValues.size(); i++)
		{
				serie.addValue(YValues.get(i), serieid, CValues.get(i)); 
//				((ExtendedCategoryAxis)domainAxis).addSubLabel(CValues.get(i), serieid);;
		}
		}
		if (SValues.size()>0)
		{
			//what to do with Z values??
			
		}

		this.resetRenderer(scope, serieid); 
				
	}

	public void resetAxes(IScope scope)
	{
		
	}

	
	private void resetDomainAxis(IScope scope) {
		// TODO Auto-generated method stub
		final SpiderWebPlot pp = (SpiderWebPlot) chart.getPlot();
		
	}
	public void initChart(IScope scope, String chartname)
	{
		super.initChart(scope, chartname);
		final SpiderWebPlot pp = (SpiderWebPlot) chart.getPlot();

	}

	public void initChart_post_data_init(IScope scope) {
		// TODO Auto-generated method stub
		super.initChart_post_data_init(scope);
		final SpiderWebPlot pp = (SpiderWebPlot) chart.getPlot();

		String sty=getStyle();
//		this.useSubAxis=false;
		switch (sty)
		{
			default:
			{
				if (this.series_label_position.equals("default"))
				{
					this.series_label_position="legend";
				}
				break;
			}
		}
		if (this.series_label_position.equals("xaxis"))
		{
//			this.useSubAxis=true;
		}

		if (!this.series_label_position.equals("legend"))
		{
			chart.getLegend().setVisible(false);
			// legend is useless, but I find it nice anyway... Could put back...
		}
		this.resetDomainAxis(scope);
		
		pp.setAxisLinePaint(axesColor);
				
		pp.setLabelFont(getLabelFont());
		if (textColor!=null)
		{
			pp.setLabelPaint(textColor);				
		}


		if (ylabel!=null && ylabel!="")
		{
		}
		if (this.series_label_position.equals("yaxis"))
		{
//			pp.getRangeAxis().setLabel(this.getChartdataset().getDataSeriesIds(scope).iterator().next());
			chart.getLegend().setVisible(false);
		}
		chart.getLegend().setVisible(true);
		
		if (xlabel!=null && xlabel!="")
		{
	//		pp.getDomainAxis().setLabel(xlabel);				
		}
		
	}
	
	protected void initRenderer(IScope scope) {
		
		
	}

	public String getModelCoordinatesInfo(final int xOnScreen, final int yOnScreen, final IDisplaySurface g, Point positionInPixels) 
	{
		int x = xOnScreen - positionInPixels.x;
		int y = yOnScreen - positionInPixels.y;
		ChartEntity entity = info.getEntityCollection().getEntity(x, y);
		// getChart().handleClick(x, y, info);

		Comparable columnKey = ((CategoryItemEntity) entity).getColumnKey();
		String title = columnKey.toString();
		CategoryDataset data = ((CategoryItemEntity) entity).getDataset();
		Comparable rowKey = ((CategoryItemEntity) entity).getRowKey();
		double xx = data.getValue(rowKey, columnKey).doubleValue();
		StringBuilder sb = new StringBuilder();
		boolean xInt = xx % 1 == 0;
		sb.append(title).append(" ").append(xInt ? (int) xx : String.format("%.2f", xx));
		return sb.toString();
		
	}
	
	
}
