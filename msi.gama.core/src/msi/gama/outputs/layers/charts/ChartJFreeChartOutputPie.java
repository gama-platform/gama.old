package msi.gama.outputs.layers.charts;

import java.util.ArrayList;
import java.util.HashMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.chart.renderer.category.AbstractCategoryItemRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DefaultPieDataset;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;

public class ChartJFreeChartOutputPie extends ChartJFreeChartOutput {

	public ChartJFreeChartOutputPie(IScope scope, String name,
			IExpression typeexp) {
		super(scope, name, typeexp);
		// TODO Auto-generated constructor stubs
		


	}
	
	public void createChart(IScope scope)
	{
		super.createChart(scope);
		if ( style.equals(IKeyword.THREE_D) ) {
			chart = ChartFactory.createPieChart3D(getName(), null, false, true, false);
		} else if ( style.equals(IKeyword.RING) ) {
			chart = ChartFactory.createRingChart(getName(), null, false, true, false);
		} else if ( style.equals(IKeyword.EXPLODED) ) {
			chart = ChartFactory.createPieChart(getName(), null, false, true, false);
		} else {
			chart = ChartFactory.createPieChart(getName(), null, false, true, false);
		}
	}
	public void initdataset()
	{
		super.initdataset();
		if (getType()==ChartOutput.PIE_CHART)
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
			default:
			{
				source.setCumulative(scope,false); // never cumulative by default				
				source.setUseSize(scope,false);				
			}
		}
			
		

		
	}
	
	

	Dataset createDataset(IScope scope)
	{
		return new DefaultPieDataset();
	}

	public void initChart(IScope scope, String chartname)
	{
		super.initChart(scope,chartname);

		final PiePlot pp = (PiePlot) chart.getPlot();
		pp.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} = {1} ({2})"));
		if (axesColor!=null)
		{
			pp.setLabelLinkPaint(axesColor);
		}
		pp.setLabelFont(getTickFont());
		if (textColor!=null)
		{
//			pp.setLabelPaint(textColor);				
			//not for Pie since the label background is always yellow for now...
		}

	}

	
	protected AbstractRenderer createRenderer(IScope scope,String serieid)
	{

		String style=this.getChartdataset().getDataSeries(scope, serieid).getStyle(scope);
		AbstractRenderer newr=new DefaultPolarItemRenderer();
		switch (style)
		{
		case IKeyword.STACK:
		case IKeyword.THREE_D:
		case IKeyword.WHISKER:
		case IKeyword.AREA:
		case IKeyword.BAR:
		case IKeyword.STEP:
		case IKeyword.RING:
		case IKeyword.EXPLODED:
		default: 
		{
			newr=new DefaultPolarItemRenderer(); // useless, piechart doesn't use renderers...
			break;
		
		}
		}
		return newr;
	}

	protected void resetRenderer(IScope scope,String serieid)
	{
		ChartDataSeries myserie=this.getChartdataset().getDataSeries(scope, serieid);
		int myrow=IdPosition.get(serieid);
		if (myserie.getMycolor()!=null)
		{
			((PiePlot)this.getJFChart().getPlot()).setSectionPaint(serieid,myserie.getMycolor());
		}
		
	}
	

	protected void clearDataSet(IScope scope) {
		// TODO Auto-generated method stub
		super.clearDataSet(scope);
        PiePlot plot = (PiePlot)this.chart.getPlot();
		jfreedataset.clear();
		jfreedataset.add(0,new DefaultPieDataset());
		plot.setDataset((DefaultPieDataset)jfreedataset.get(0));
		IdPosition.clear();
		nbseries=0;
	}

	
	protected void createNewSerie(IScope scope, String serieid) {
		ChartDataSeries dataserie=chartdataset.getDataSeries(scope,serieid);
        PiePlot plot = (PiePlot)this.chart.getPlot();
		
        DefaultPieDataset firstdataset=(DefaultPieDataset)plot.getDataset();
		
		nbseries++;
		IdPosition.put(serieid, nbseries-1);
		if ( getStyle().equals(IKeyword.EXPLODED) ) {
				plot.setExplodePercent(serieid, 0.20);
		}
		
//		System.out.println("new serie"+serieid+" at "+IdPosition.get(serieid)+" jfds "+jfreedataset.size()+" datasc "+" nbse "+nbseries);
	}


	
	protected void resetSerie(IScope scope, String serieid) {
		// TODO Auto-generated method stub
		
		ChartDataSeries dataserie=chartdataset.getDataSeries(scope,serieid);
		DefaultPieDataset serie=((DefaultPieDataset) jfreedataset.get(0));
		ArrayList<Double> YValues=dataserie.getYValues(scope);
		
		if (YValues.size()>0)
		{
			// TODO Hack to speed up, change!!!
				serie.setValue(serieid,YValues.get(YValues.size()-1)); 
		}
		this.resetRenderer(scope, serieid); 

				
	}
	protected void initRenderer(IScope scope) {
		// TODO Auto-generated method stub
		
		
	}

}
