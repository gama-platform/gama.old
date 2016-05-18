package msi.gama.outputs.layers.charts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
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
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
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

public class ChartJFreeChartOutputHistogram extends ChartJFreeChartOutput {

	boolean useSubAxis=false;
	boolean useMainAxisLabel=true;
	
	public ChartJFreeChartOutputHistogram(IScope scope, String name,
			IExpression typeexp) {
		super(scope, name, typeexp);
		// TODO Auto-generated constructor stub

	}
	
	public void createChart(IScope scope)
	{
		super.createChart(scope);
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
		case IKeyword.DOT:
		{
			newr=new ScatterRenderer();
			break;		
		}			
		case IKeyword.AREA:
		{
			newr=new StackedAreaRenderer();
			break;		
		}			
		case IKeyword.LINE:
		{
			newr=new StatisticalLineAndShapeRenderer();
			break;		
		}
		case IKeyword.STEP:
		{
			newr=new LevelRenderer();
			break;		
		}			
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
//		if (serieid!=this.getChartdataset().series.keySet().iterator().next())
//			newr=(AbstractCategoryItemRenderer)this.getOrCreateRenderer(scope, serieid);
		
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
			newr.setSeriesPaint(myrow,myserie.getMycolor());
		}

		if (this.series_label_position.equals("onchart"))
		{
//			((BarRenderer)newr).setBaseItemLabelGenerator(new LabelGenerator());
			newr.setBaseItemLabelGenerator(new LabelGenerator());
	        ItemLabelPosition itemlabelposition = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER);
	        newr.setBasePositiveItemLabelPosition(itemlabelposition);
	        newr.setBaseNegativeItemLabelPosition(itemlabelposition);
	        newr.setBaseItemLabelsVisible(true);
		}

		if (newr instanceof BarRenderer)
		{
			if (gap>=0)
	        {
	            ((BarRenderer)newr).setMaximumBarWidth(1 - gap);
	        	
	        }
			
		}
		}
		
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
		final NumberAxis rangeAxis = (NumberAxis) ((CategoryPlot)this.chart.getPlot()).getRangeAxis();

		if (!useyrangeinterval && !useyrangeminmax)
		{
			rangeAxis.setAutoRange(true);
		}
			
		if (this.useyrangeinterval)
		{
			rangeAxis.setFixedAutoRange(yrangeinterval);
			rangeAxis.setAutoRangeMinimumSize(yrangeinterval);
			rangeAxis.setAutoRange(true);
			
		}
		if (this.useyrangeminmax)
		{
			rangeAxis.setRange(yrangemin, yrangemax);
			
		}

		resetDomainAxis(scope);
		CategoryAxis domainAxis = (CategoryAxis) ((CategoryPlot)this.chart.getPlot()).getDomainAxis();

		if (this.useSubAxis)
		{
			for (String serieid:chartdataset.getDataSeriesIds(scope))
			{
				((SubCategoryAxis) domainAxis).addSubCategory(serieid);			
			}
			
		}
		
	}

	
	public void resetDomainAxis(IScope scope) {
		// TODO Auto-generated method stub
		final CategoryPlot pp = (CategoryPlot) chart.getPlot();
		if (this.useSubAxis)
		{
		SubCategoryAxis newAxis=new SubCategoryAxis(pp.getDomainAxis().getLabel());
		pp.setDomainAxis(newAxis);
		}
		
		pp.getDomainAxis().setAxisLinePaint(axesColor);
		pp.getDomainAxis().setTickLabelFont(getTickFont());
		pp.getDomainAxis().setLabelFont(getLabelFont());
		if (textColor!=null)
		{
			pp.getDomainAxis().setLabelPaint(textColor);				
			pp.getDomainAxis().setTickLabelPaint(textColor);				
		}

        if (gap>0)
        {

		pp.getDomainAxis().setCategoryMargin(gap);
		pp.getDomainAxis().setUpperMargin(gap);
		pp.getDomainAxis().setLowerMargin(gap);
        }

		if (this.useSubAxis && !this.useMainAxisLabel)
		{
			pp.getDomainAxis().setTickLabelsVisible(false);
//			pp.getDomainAxis().setTickLabelPaint(this.backgroundColor);
	//		pp.getDomainAxis().setLabelFont(new Font(labelFontFace, labelFontStyle, 1));
		}

		
		
	}
	public void initChart(IScope scope, String chartname)
	{
		super.initChart(scope, chartname);
		final CategoryPlot pp = (CategoryPlot) chart.getPlot();

		
		


	}
	public void initChart_post_data_init(IScope scope) {
		// TODO Auto-generated method stub
		super.initChart_post_data_init(scope);
		final CategoryPlot pp = (CategoryPlot) chart.getPlot();

		String sty=getStyle();
		this.useSubAxis=false;
		switch (sty)
		{
			case (IKeyword.STACK):
			{				
				if (this.series_label_position.equals("xaxis"))
				{
					this.series_label_position="default";
				}
				if (this.series_label_position.equals("default"))
				{
					this.series_label_position="legend";					
				}
				break;
			}
			default:
			{
				if (this.series_label_position.equals("default"))
				{
					if (this.getChartdataset().getSources().size()>0)
					{
						ChartDataSource onesource=this.getChartdataset().getSources().get(0);
						if (onesource.isCumulative)
						{
							this.series_label_position="legend";
						}
						else
						{
							this.series_label_position="xaxis";	
							useMainAxisLabel=false;
						}
						
					}
					else
					{
						this.series_label_position="legend";
						
					}
				}
				break;
			}
		}
		if (this.series_label_position.equals("xaxis"))
		{
			this.useSubAxis=true;
		}

		if (!this.series_label_position.equals("legend"))
		{
			chart.getLegend().setVisible(false);
			// legend is useless, but I find it nice anyway... Could put back...
		}
		this.resetDomainAxis(scope);
		
		pp.setDomainGridlinePaint(axesColor);
		pp.setRangeGridlinePaint(axesColor);
		pp.setRangeCrosshairVisible(true);
		
		
		pp.getRangeAxis().setAxisLinePaint(axesColor);
		pp.getRangeAxis().setLabelFont(getLabelFont());
		pp.getRangeAxis().setTickLabelFont(getTickFont());
		if (textColor!=null)
		{
			pp.getRangeAxis().setLabelPaint(textColor);				
			pp.getRangeAxis().setTickLabelPaint(textColor);				
		}
		if ( ytickunit > 0 ) {
			((NumberAxis)pp.getRangeAxis()).setTickUnit(new NumberTickUnit(ytickunit));
		}


		if (ylabel!=null && ylabel!="")
		{
			pp.getRangeAxis().setLabel(ylabel);				
		}
		if (this.series_label_position.equals("yaxis"))
		{
			pp.getRangeAxis().setLabel(this.getChartdataset().getDataSeriesIds(scope).iterator().next());
			chart.getLegend().setVisible(false);
		}
		
		if (xlabel!=null && xlabel!="")
		{
			pp.getDomainAxis().setLabel(xlabel);				
		}
		
	}
	
	protected void initRenderer(IScope scope) {
		// TODO Auto-generated method stub
//        CategoryPlot plot = (CategoryPlot)this.chart.getPlot();
 //       defaultrenderer = new BarRenderer();
  //      plot.setRenderer((BarRenderer)defaultrenderer);
		
		
	}

	
	public String getModelCoordinatesInfo(final int xOnScreen, final int yOnScreen, final IDisplaySurface g, Point positionInPixels) {
		int x = xOnScreen - positionInPixels.x;
		int y = yOnScreen - positionInPixels.y;
		ChartEntity entity = info.getEntityCollection().getEntity(x, y);
		// getChart().handleClick(x, y, info);
		if ( entity instanceof XYItemEntity ) {
			XYDataset data = ((XYItemEntity) entity).getDataset();
			int index = ((XYItemEntity) entity).getItem();
			int series = ((XYItemEntity) entity).getSeriesIndex();
			double xx = data.getXValue(series, index);
			double yy = data.getYValue(series, index);
			XYPlot plot = (XYPlot) getJFChart().getPlot();
			ValueAxis xAxis = plot.getDomainAxis(series);
			ValueAxis yAxis = plot.getRangeAxis(series);
			boolean xInt = xx % 1 == 0;
			boolean yInt = yy % 1 == 0;
			String xTitle = xAxis.getLabel();
			if ( StringUtils.isBlank(xTitle) ) {
				xTitle = "X";
			}
			String yTitle = yAxis.getLabel();
			if ( StringUtils.isBlank(yTitle) ) {
				yTitle = "Y";
			}
			StringBuilder sb = new StringBuilder();
			sb.append(xTitle).append(" ").append(xInt ? (int) xx : String.format("%.2f", xx));
			sb.append(" | ").append(yTitle).append(" ").append(yInt ? (int) yy : String.format("%.2f", yy));
			return sb.toString();
		} else if ( entity instanceof PieSectionEntity ) {
			String title = ((PieSectionEntity) entity).getSectionKey().toString();
			PieDataset data = ((PieSectionEntity) entity).getDataset();
			int index = ((PieSectionEntity) entity).getSectionIndex();
			double xx = data.getValue(index).doubleValue();
			StringBuilder sb = new StringBuilder();
			boolean xInt = xx % 1 == 0;
			sb.append(title).append(" ").append(xInt ? (int) xx : String.format("%.2f", xx));
			return sb.toString();
		} else if ( entity instanceof CategoryItemEntity ) {
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
		return "";
	}
	
	
}
