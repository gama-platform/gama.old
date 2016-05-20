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
import org.jfree.chart.axis.AxisLocation;
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
import org.jfree.chart.renderer.GrayPaintScale;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer;
import org.jfree.chart.renderer.xy.StackedXYAreaRenderer2;
import org.jfree.chart.renderer.xy.XYAreaRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.renderer.xy.XYBoxAndWhiskerRenderer;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.statistics.BoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.MatrixSeries;
import org.jfree.data.xy.MatrixSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYIntervalDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ShapeUtilities;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaColor;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;

public class ChartJFreeChartOutputHeatmap extends ChartJFreeChartOutput {


	public ChartJFreeChartOutputHeatmap(IScope scope, String name,
			IExpression typeexp) {
		super(scope, name, typeexp);
		// TODO Auto-generated constructor stub




	}
	public void createChart(IScope scope)
	{
		super.createChart(scope);

		jfreedataset.add(0,new MatrixSeriesCollection());
		PlotOrientation orientation=PlotOrientation.VERTICAL;
		if (reverse_axes)
			orientation=PlotOrientation.HORIZONTAL;		
	
			chart =
					ChartFactory.createXYLineChart(getName(), "", "", (MatrixSeriesCollection)(jfreedataset.get(0)), orientation, true, false,
							false);

	}

	public void setDefaultPropertiesFromType(IScope scope, ChartDataSource source, Object o, int type_val) {
		// TODO Auto-generated method stub

		switch (type_val)
		{
		case ChartDataSource.DATA_TYPE_LIST_DOUBLE_N:
		case ChartDataSource.DATA_TYPE_LIST_DOUBLE_3:
		case ChartDataSource.DATA_TYPE_LIST_DOUBLE_12:
		{
			source.setCumulative(scope,false);				
			source.setCumulativeY(scope,true);				
			source.setUseSize(scope,true);				
			
		}
		case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_N:
		case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_12:
		case ChartDataSource.DATA_TYPE_LIST_POINT:
		case ChartDataSource.DATA_TYPE_MATRIX_DOUBLE:
		case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_3:
		default:
		{
			source.setCumulative(scope,false);				
			source.setUseSize(scope,true);				
		}
		}

	}


	public void initdataset()
	{
		super.initdataset();
		chartdataset.setCommonXSeries(true);
		chartdataset.setCommonYSeries(true);
		chartdataset.setByCategory(false);
		chartdataset.forceNoXAccumulate=true;
		chartdataset.forceNoYAccumulate=true;
	}

	Dataset createDataset(IScope scope)
	{
		return new MatrixSeriesCollection();
	}

	protected AbstractRenderer createRenderer(IScope scope,String serieid)
	{
		String style=this.getChartdataset().getDataSeries(scope, serieid).getStyle(scope);
		AbstractRenderer newr=new XYBlockRenderer();
		switch (style)
		{
		case IKeyword.SPLINE:
		case IKeyword.STEP:
		case IKeyword.DOT:
		case IKeyword.WHISKER:
		case IKeyword.AREA:
		case IKeyword.BAR:
		case IKeyword.STACK:
		case IKeyword.RING:
		case IKeyword.EXPLODED:
		case IKeyword.THREE_D:
		default: 
		{
			newr=new XYBlockRenderer();
			break;

		}
		}
		return newr;
	}

	protected static final LookupPaintScale createLUT(final int ncol, float vmin, float vmax, Color start, Color med, Color end) {
		final float[][] colors = new float[][]  { 
				{start.getRed()/255f, start.getGreen()/255f, start.getBlue()/255f, start.getAlpha()/255f},
				{med.getRed()/255f, med.getGreen()/255f, med.getBlue()/255f, med.getAlpha()/255f},
				{end.getRed()/255f, end.getGreen()/255f, end.getBlue()/255f, end.getAlpha()/255f}
		};
		final float[] limits = new float[] {0, 0.5f, 1};
		final LookupPaintScale lut = new LookupPaintScale(vmin, vmax, med);
		float val;
		float r, g, b, a;
		for (int j = 0; j < ncol; j++) {			
			val = (float)j/(float)(ncol-0.99f);
			int i = 0;
			for (i = 0; i < limits.length; i++) {
				if (val < limits[i]) {
					break;
				}
			}
			i = i - 1;
			r = colors[i][0] + (val-limits[i])/(limits[i+1]-limits[i])*(colors[i+1][0]-colors[i][0]); 
			g = colors[i][1] + (val-limits[i])/(limits[i+1]-limits[i])*(colors[i+1][1]-colors[i][1]); 
			b = colors[i][2] + (val-limits[i])/(limits[i+1]-limits[i])*(colors[i+1][2]-colors[i][2]); 
			a = colors[i][3] + (val-limits[i])/(limits[i+1]-limits[i])*(colors[i+1][3]-colors[i][3]); 
			lut.add(val*(vmax-vmin)+vmin, new Color(r, g, b,a));
		}
		return lut;
	}	
	
	protected static final LookupPaintScale createLUT(final int ncol, float vmin, float vmax, Color start, Color end) {
		final float[][] colors = new float[][]  { 
				{start.getRed()/255f, start.getGreen()/255f, start.getBlue()/255f, start.getAlpha()/255f},
				{end.getRed()/255f, end.getGreen()/255f, end.getBlue()/255f, end.getAlpha()/255f}
		};
		final float[] limits = new float[] {0, 1};
		final LookupPaintScale lut = new LookupPaintScale(vmin,vmax, start);
		float val;
		float r, g, b, a;
		for (int j = 0; j < ncol; j++) {			
			val = (float)j/(float)(ncol-0.99f);
			int i = 0;
			r = colors[i][0] + (val-limits[i])/(limits[i+1]-limits[i])*(colors[i+1][0]-colors[i][0]); 
			g = colors[i][1] + (val-limits[i])/(limits[i+1]-limits[i])*(colors[i+1][1]-colors[i][1]); 
			b = colors[i][2] + (val-limits[i])/(limits[i+1]-limits[i])*(colors[i+1][2]-colors[i][2]); 
			a = colors[i][3] + (val-limits[i])/(limits[i+1]-limits[i])*(colors[i+1][3]-colors[i][3]); 
			lut.add((val*(vmax-vmin)+vmin), new Color(r, g, b,a));
		}
		return lut;
	}	
	
	protected void resetRenderer(IScope scope,String serieid)
	{
		XYBlockRenderer newr=(XYBlockRenderer)this.getOrCreateRenderer(scope, serieid);

		//		newr.setSeriesStroke(0, new BasicStroke(0));
		ChartDataSeries myserie=this.getChartdataset().getDataSeries(scope, serieid);
		
		if (myserie.getMycolor()!=null)
		{
			newr.setSeriesPaint(0,myserie.getMycolor());
		}
		if (myserie.getSValues(scope).size()>0)
		{
			double maxval=Collections.max(myserie.getSValues(scope));
			double minval=Collections.min(myserie.getSValues(scope));
			Color cdeb=new Color(0,0,0,0);
			if (myserie.getMyMincolor()!=null)
				cdeb=myserie.getMyMincolor();
			Color cend=new Color(0.9f,0.9f,0.9f,1.0f);
			if (myserie.getMycolor()!=null)
				cend=myserie.getMycolor();
			
			LookupPaintScale paintscale=createLUT(100,(float)minval,(float)maxval,cdeb, cend);
			if (myserie.getMyMedcolor()!=null)
				paintscale=createLUT(100,(float)minval,(float)maxval,cdeb, myserie.getMyMedcolor(),cend);
			
			newr.setPaintScale(paintscale);

		     NumberAxis scaleAxis = new NumberAxis(myserie.getName());
		        scaleAxis.setAxisLinePaint(this.axesColor);
		        scaleAxis.setTickMarkPaint(this.axesColor);
		        scaleAxis.setTickLabelFont(this.getTickFont());
		        scaleAxis.setRange(minval, maxval);
		        scaleAxis.setAxisLinePaint(axesColor);
		        scaleAxis.setLabelFont(getLabelFont());
				if (textColor!=null)
				{
					scaleAxis.setLabelPaint(textColor);				
					scaleAxis.setTickLabelPaint(textColor);				
				}

				PaintScaleLegend legend = new PaintScaleLegend(paintscale,
	                scaleAxis);
	        legend.setAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
	        legend.setAxisOffset(5.0);
//	        legend.setMargin(new RectangleInsets(5, 5, 5, 5));
//	        legend.setFrame(new BlockBorder(Color.red));
//	        legend.setPadding(new RectangleInsets(10, 10, 10, 10));
//	        legend.setStripWidth(10);
	        legend.setPosition(RectangleEdge.RIGHT);
	        legend.setBackgroundPaint(this.backgroundColor);
//	        ArrayList<PaintScaleLegend> caxe=new ArrayList<PaintScaleLegend>();
//	        caxe.add(legend);
//	        chart.setSubtitles(caxe);
	        if (!this.series_label_position.equals("none"))
	        	chart.addSubtitle(legend);	
			
		}
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
		((MatrixSeriesCollection)jfreedataset.get(0)).removeAllSeries();
		jfreedataset.clear();
		jfreedataset.add(0,new MatrixSeriesCollection());
		plot.setDataset((MatrixSeriesCollection)jfreedataset.get(0));
		plot.setRenderer(0, null);
		
		IdPosition.clear();
	}


	protected void createNewSerie(IScope scope, String serieid) {
		
		ChartDataSeries dataserie=chartdataset.getDataSeries(scope,serieid);
		final MatrixSeries serie = new MatrixSeries((String)dataserie.getSerieLegend(scope), Math.max(1,this.getChartdataset().getYSeriesValues().size()), Math.max(1,this.getChartdataset().getXSeriesValues().size()));		
		XYPlot plot = (XYPlot)this.chart.getPlot();

		MatrixSeriesCollection firstdataset=(MatrixSeriesCollection)plot.getDataset();

		if (!IdPosition.containsKey(serieid))
		{
		
		if (firstdataset.getSeriesCount()==0)
		{
			firstdataset.addSeries(serie);
			plot.setDataset(0, firstdataset);

		}
		else
		{

			MatrixSeriesCollection newdataset=new MatrixSeriesCollection();
			newdataset.addSeries(serie);			
			jfreedataset.add(newdataset);
			plot.setDataset(jfreedataset.size()-1, newdataset);

		}
		plot.setRenderer(jfreedataset.size()-1, (XYItemRenderer)getOrCreateRenderer(scope,serieid));
		IdPosition.put(serieid, jfreedataset.size()-1);
//		System.out.println("new serie"+serieid+" at "+IdPosition.get(serieid)+" fdsize "+plot.getSeriesCount()+" jfds "+jfreedataset.size()+" datasc "+plot.getDatasetCount());
		// TODO Auto-generated method stub		

		}
	}

	public void preResetSeries(IScope scope) {
		this.clearDataSet(scope);
        ArrayList<PaintScaleLegend> caxe=new ArrayList<PaintScaleLegend>();
        chart.setSubtitles(caxe);
		
	}

	protected void resetSerie(IScope scope, String serieid) {
		// TODO Auto-generated method stub
		this.createNewSerie(scope, serieid);
		ChartDataSeries dataserie=chartdataset.getDataSeries(scope,serieid);
		MatrixSeries serie=((MatrixSeriesCollection) jfreedataset.get(IdPosition.get(dataserie.getSerieId(scope)))).getSeries(0);
		ArrayList<Double> XValues=dataserie.getXValues(scope);
		ArrayList<Double> YValues=dataserie.getYValues(scope);
		ArrayList<Double> SValues=dataserie.getSValues(scope);
		
		if (XValues.size()>0)
		{
			final NumberAxis domainAxis = (NumberAxis) ((XYPlot)this.chart.getPlot()).getDomainAxis();
			final NumberAxis rangeAxis = (NumberAxis) ((XYPlot)this.chart.getPlot()).getRangeAxis();
			domainAxis.setAutoRange(false);
			rangeAxis.setAutoRange(false);
			for(int i=0; i<XValues.size(); i++)
			{
				serie.update(YValues.get(i).intValue(), XValues.get(i).intValue(), SValues.get(i).doubleValue());
			}
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
	


	protected void initRenderer(IScope scope) {
		// TODO Auto-generated method stub
		XYPlot plot = (XYPlot)this.chart.getPlot();
		defaultrenderer = new XYBlockRenderer();
		plot.setRenderer((XYBlockRenderer)defaultrenderer);


	}
	
	public void setUseXSource(IScope scope, IExpression expval) {
		// if there is something to do to use custom X axis
		
	}

	public void setUseXLabels(IScope scope, IExpression expval) {
		// if there is something to do to use custom X axis
		final XYPlot pp = (XYPlot) chart.getPlot();
		
		((NumberAxis)pp.getDomainAxis()).setNumberFormatOverride(new NumberFormat(){

			    @Override
			    public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
			    	int ind=chartdataset.XSeriesValues.indexOf(number);
			    	if (ind>=0)
			    	{
			            return new StringBuffer(""+chartdataset.Xcategories.get(ind) );			    		
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

	public void setUseYLabels(IScope scope, IExpression expval) {
		// if there is something to do to use custom X axis
		final XYPlot pp = (XYPlot) chart.getPlot();
		
		((NumberAxis)pp.getRangeAxis()).setNumberFormatOverride(new NumberFormat(){

			    @Override
			    public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
			    	int ind=chartdataset.YSeriesValues.indexOf(number);
			    	if (ind>=0)
			    	{
			            return new StringBuffer(""+chartdataset.Ycategories.get(ind) );			    		
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
		pp.setDomainCrosshairVisible(false);
		pp.setRangeCrosshairVisible(false);
		pp.setRangeGridlinesVisible(false);
		pp.setDomainGridlinesVisible(false);

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
