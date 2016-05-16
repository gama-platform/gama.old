package msi.gama.outputs.layers.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYBoxAndWhiskerRenderer;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLine3DRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYIntervalDataItem;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ShapeUtilities;

import msi.gama.common.interfaces.IDisplaySurface;
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




	}
	public void createChart(IScope scope)
	{
		super.createChart(scope);

		jfreedataset.add(0,new XYIntervalSeriesCollection());
	
		switch (type) {
		case SERIES_CHART: {
			chart =
					ChartFactory.createXYLineChart(getName(), "", "", (XYIntervalSeriesCollection)(jfreedataset.get(0)), PlotOrientation.VERTICAL, true, false,
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
		String style=this.getChartdataset().getDataSeries(scope, serieid).getStyle(scope);
		AbstractRenderer newr=new myXYErrorRenderer();
		switch (style)
		{
		case IKeyword.SPLINE:
		{
			newr=new XYSplineRenderer();
			break;		
		}
		case IKeyword.STEP:
		{
			newr=new XYStepRenderer();
			break;		
		}
		case IKeyword.DOT:
		{
			newr = new XYShapeRenderer();
			break;		
		}
		case IKeyword.WHISKER:
		{
			newr = new XYBoxAndWhiskerRenderer();
			break;		
		}
		case IKeyword.AREA:
		{
			newr = new XYAreaRenderer();
			break;		
		}
		case IKeyword.BAR:
		{
			newr = new XYBarRenderer();
			break;		
		}
		case IKeyword.STACK:
		case IKeyword.RING:
		case IKeyword.EXPLODED:
		case IKeyword.THREE_D:
		{
			newr = new XYLine3DRenderer();
			break;		
		}
		default: 
		{
			newr=new myXYErrorRenderer();
			((myXYErrorRenderer)newr).setMyid(serieid);
			((myXYErrorRenderer)newr).setOutput(this);
			break;

		}
		}
		return newr;
	}

	protected void resetRenderer(IScope scope,String serieid)
	{
		AbstractXYItemRenderer newr=(AbstractXYItemRenderer)this.getOrCreateRenderer(scope, serieid);

		//		newr.setSeriesStroke(0, new BasicStroke(0));
		ChartDataSeries myserie=this.getChartdataset().getDataSeries(scope, serieid);

		if (newr instanceof XYLineAndShapeRenderer)
		{
			((XYLineAndShapeRenderer)newr).setSeriesLinesVisible(0,myserie.getMysource().showLine);
			((XYLineAndShapeRenderer)newr).setSeriesShapesFilled(0,myserie.getMysource().fillMarker);
			((XYLineAndShapeRenderer)newr).setSeriesShapesVisible(0,myserie.getMysource().useMarker);

		}
		if (myserie.getMycolor()!=null)
		{
			newr.setSeriesPaint(0,myserie.getMycolor());
		}

		if (newr instanceof myXYErrorRenderer)
		{
			((myXYErrorRenderer)newr).setDrawYError(false);
			((myXYErrorRenderer)newr).setDrawXError(false);
			if (myserie.isUseYErrValues()) ((myXYErrorRenderer)newr).setDrawYError(true);
			if (myserie.isUseXErrValues()) ((myXYErrorRenderer)newr).setDrawXError(true);
			if (myserie.getMysource().isUseSize()) ((myXYErrorRenderer)newr).setUseSize(scope, true);
		}

		if (myserie.getMysource().getUniqueMarkerName()!=null) setSerieMarkerShape(scope,myserie.getName(),myserie.getMysource().getUniqueMarkerName());

	}


	protected void clearDataSet(IScope scope) {
		// TODO Auto-generated method stub
		super.clearDataSet(scope);
		XYPlot plot = (XYPlot)this.chart.getPlot();
		for (int i=plot.getDatasetCount()-1; i>=1; i--)
		{
			plot.setDataset(i, null);
			plot.setRenderer(i, null);
		}
		((XYIntervalSeriesCollection)jfreedataset.get(0)).removeAllSeries();
		jfreedataset.clear();
		jfreedataset.add(0,new XYIntervalSeriesCollection());
		plot.setDataset((XYIntervalSeriesCollection)jfreedataset.get(0));
		plot.setRenderer(0, null);
		IdPosition.clear();
	}


	protected void createNewSerie(IScope scope, String serieid) {
		ChartDataSeries dataserie=chartdataset.getDataSeries(scope,serieid);
		final XYIntervalSeries serie = new XYIntervalSeries(dataserie.getSerieLegend(scope), false, true);		
		XYPlot plot = (XYPlot)this.chart.getPlot();

		XYIntervalSeriesCollection firstdataset=(XYIntervalSeriesCollection)plot.getDataset();

		if (firstdataset.getSeriesCount()==0)
		{
			firstdataset.addSeries(serie);
			plot.setDataset(0, firstdataset);

		}
		else
		{

			XYIntervalSeriesCollection newdataset=new XYIntervalSeriesCollection();
			newdataset.addSeries(serie);			
			jfreedataset.add(newdataset);
			plot.setDataset(jfreedataset.size()-1, newdataset);

		}
		plot.setRenderer(jfreedataset.size()-1, (XYItemRenderer)getOrCreateRenderer(scope,serieid));
		IdPosition.put(serieid, jfreedataset.size()-1);
//		System.out.println("new serie"+serieid+" at "+IdPosition.get(serieid)+" fdsize "+plot.getSeriesCount()+" jfds "+jfreedataset.size()+" datasc "+plot.getDatasetCount());
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
		{
			final NumberAxis domainAxis = (NumberAxis) ((XYPlot)this.chart.getPlot()).getDomainAxis();
			final NumberAxis rangeAxis = (NumberAxis) ((XYPlot)this.chart.getPlot()).getRangeAxis();
			domainAxis.setAutoRange(false);
			rangeAxis.setAutoRange(false);
			//			domainAxis.setRange(Math.min((double)(Collections.min(XValues)),0), Math.max(Collections.max(XValues),Collections.min(XValues)+1));
			//			rangeAxis.setRange(Math.min((double)(Collections.min(YValues)),0), Math.max(Collections.max(YValues),Collections.min(YValues)+1));
			XYIntervalDataItem newval;
			for(int i=0; i<XValues.size(); i++)
			{
				if (dataserie.isUseYErrValues())
				{
					if (dataserie.isUseXErrValues())
					{
						newval=new XYIntervalDataItem(XValues.get(i),dataserie.xerrvaluesmin.get(i),dataserie.xerrvaluesmax.get(i),YValues.get(i),dataserie.yerrvaluesmin.get(i),dataserie.yerrvaluesmax.get(i));			
						//				serie.add(XValues.get(i),dataserie.xerrvaluesmin.get(i),dataserie.xerrvaluesmax.get(i),YValues.get(i),dataserie.yerrvaluesmin.get(i),dataserie.yerrvaluesmax.get(i));			
					}
					else
					{
						newval=new XYIntervalDataItem(XValues.get(i),XValues.get(i),XValues.get(i),YValues.get(i),dataserie.yerrvaluesmin.get(i),dataserie.yerrvaluesmax.get(i));			
						//			serie.add(XValues.get(i),XValues.get(i),XValues.get(i),YValues.get(i),dataserie.yerrvaluesmin.get(i),dataserie.yerrvaluesmax.get(i));			
					}

				}
				else
				{
					if (dataserie.isUseXErrValues())
					{
						newval=new XYIntervalDataItem(XValues.get(i),dataserie.xerrvaluesmin.get(i),dataserie.xerrvaluesmax.get(i),YValues.get(i),YValues.get(i),YValues.get(i));			
						//				serie.add(XValues.get(i),dataserie.xerrvaluesmin.get(i),dataserie.xerrvaluesmax.get(i),YValues.get(i),YValues.get(i),YValues.get(i));			
					}
					else
					{
						newval=new XYIntervalDataItem(XValues.get(i),XValues.get(i),XValues.get(i),YValues.get(i),YValues.get(i),YValues.get(i));			
						//				serie.add(XValues.get(i),XValues.get(i),XValues.get(i),YValues.get(i),YValues.get(i),YValues.get(i));			
					}

				}
				serie.add(newval, false);
			}
//			domainAxis.setAutoRange(true);
//			rangeAxis.setAutoRange(true);
		}
//		resetAutorange(scope);
		if (SValues.size()>0)
		{
			MarkerScale.remove(serieid);
			ArrayList<Double> nscale=(ArrayList<Double>) SValues.clone();
			MarkerScale.put(serieid, nscale);

		}

		this.resetRenderer(scope, serieid); 

	}
	
	public void resetAxes(IScope scope)
	{
		final NumberAxis domainAxis = (NumberAxis) ((XYPlot)this.chart.getPlot()).getDomainAxis();
		final NumberAxis rangeAxis = (NumberAxis) ((XYPlot)this.chart.getPlot()).getRangeAxis();

		if (!usexrangeinterval && !usexrangeminmax)
		{
			domainAxis.setAutoRange(true);
		}
			
		if (this.usexrangeinterval)
		{
			domainAxis.setFixedAutoRange(xrangeinterval);
			domainAxis.setAutoRangeMinimumSize(xrangeinterval);
			domainAxis.setAutoRange(true);
			
		}
		if (this.usexrangeminmax)
		{
			domainAxis.setRange(xrangemin, xrangemax);
			
		}

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
	
	}
	
	public void setSerieMarkerShape(IScope scope, String serieid, String markershape) {
		AbstractXYItemRenderer newr=(AbstractXYItemRenderer)this.getOrCreateRenderer(scope, serieid);
		if (newr instanceof XYLineAndShapeRenderer)
		{
			XYLineAndShapeRenderer serierenderer=(XYLineAndShapeRenderer) getOrCreateRenderer(scope,serieid);
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


	}	

	public void setUseSize(IScope scope, String name, boolean b) {
		// TODO Auto-generated method stub
		AbstractXYItemRenderer newr=(AbstractXYItemRenderer)this.getOrCreateRenderer(scope, name);
		if (newr instanceof myXYErrorRenderer)
		{
			myXYErrorRenderer serierenderer=(myXYErrorRenderer) getOrCreateRenderer(scope,name);
			serierenderer.setUseSize(scope,b);

		}

	}


	protected void initRenderer(IScope scope) {
		// TODO Auto-generated method stub
		XYPlot plot = (XYPlot)this.chart.getPlot();
		defaultrenderer = new myXYErrorRenderer();
		plot.setRenderer((myXYErrorRenderer)defaultrenderer);


	}
	
	public void setUseXSource(IScope scope, IExpression expval) {
		// if there is something to do to use custom X axis
		final XYPlot pp = (XYPlot) chart.getPlot();
		
			((NumberAxis)pp.getDomainAxis()).setNumberFormatOverride(new NumberFormat(){

			    @Override
			    public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
			    	int ind=chartdataset.XSeriesValues.indexOf(number);
			    	if (ind>=0)
			    	{
			            return new StringBuffer(""+chartdataset.categories.get(ind) );			    		
			    	}
			    	else
			    	{
				    	return new StringBuffer("");
			    		
			    	}

			    }

			    @Override
			    public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
		            return new StringBuffer("n"+number);
//			        return new StringBuffer(String.format("%s", number));
			    }

			    @Override
			    public Number parse(String source, ParsePosition parsePosition) {
			        return null;
			    }
			});
			
		
	}

	public void initChart(IScope scope, String chartname)
	{
		super.initChart(scope,chartname);

		final XYPlot pp = (XYPlot) chart.getPlot();
		pp.setDomainGridlinePaint(axesColor);
		pp.setRangeGridlinePaint(axesColor);
		pp.setDomainCrosshairPaint(axesColor);
		pp.setRangeCrosshairPaint(axesColor);
		pp.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		pp.setDomainCrosshairVisible(true);
		pp.setRangeCrosshairVisible(true);

		pp.getDomainAxis().setAxisLinePaint(axesColor);
		pp.getDomainAxis().setTickLabelFont(getTickFont());
		pp.getDomainAxis().setLabelFont(getLabelFont());
		if (textColor!=null)
		{
			pp.getDomainAxis().setLabelPaint(textColor);				
			pp.getDomainAxis().setTickLabelPaint(textColor);				
		}
		if ( xtickunit > 0 ) {
			((NumberAxis)pp.getDomainAxis()).setTickUnit(new NumberTickUnit(xtickunit));
		}

		
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

//		resetAutorange(scope);

		if (getType()==ChartOutput.SERIES_CHART)
		{
			if (xlabel==null) xlabel="time";
		}
		if (getType()==ChartOutput.XY_CHART)
		{
		}
		if (getType()==ChartOutput.SCATTER_CHART)
		{
		}
		if (xlabel!=null && xlabel!="")
		{
			pp.getDomainAxis().setLabel(xlabel);				
		}
		if (ylabel!=null && ylabel!="")
		{
			pp.getRangeAxis().setLabel(ylabel);				
		}
		

		
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
