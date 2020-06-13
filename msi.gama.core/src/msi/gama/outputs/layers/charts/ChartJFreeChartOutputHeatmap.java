/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.charts.ChartJFreeChartOutputHeatmap.java, in plugin msi.gama.core, is part of the source code
 * of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers.charts;

import java.awt.Color;
import java.awt.Point;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.LogarithmicAxis;
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
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.MatrixSeries;
import org.jfree.data.xy.MatrixSeriesCollection;
import org.jfree.data.xy.XYDataset;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.IScope;
import msi.gaml.expressions.IExpression;

public class ChartJFreeChartOutputHeatmap extends ChartJFreeChartOutput {

	public ChartJFreeChartOutputHeatmap(final IScope scope, final String name, final IExpression typeexp) {
		super(scope, name, typeexp);
		// TODO Auto-generated constructor stub

	}

	@Override
	public void createChart(final IScope scope) {
		super.createChart(scope);

		jfreedataset.add(0, new MatrixSeriesCollection());
		PlotOrientation orientation = PlotOrientation.VERTICAL;
		if (reverse_axes) {
			orientation = PlotOrientation.HORIZONTAL;
		}

		chart = ChartFactory.createXYLineChart(getName(), "", "", (MatrixSeriesCollection) jfreedataset.get(0),
				orientation, true, false, false);

	}

	@Override
	public void setDefaultPropertiesFromType(final IScope scope, final ChartDataSource source, final int type_val) {
		// TODO Auto-generated method stub

		switch (type_val) {
			case ChartDataSource.DATA_TYPE_LIST_DOUBLE_N:
			case ChartDataSource.DATA_TYPE_LIST_DOUBLE_3:
			case ChartDataSource.DATA_TYPE_LIST_DOUBLE_12: {
				source.setCumulative(scope, false);
				source.setCumulativeY(scope, true);
				source.setUseSize(scope, true);
				break;
			}
			case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_N:
			case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_12:
			case ChartDataSource.DATA_TYPE_LIST_POINT:
			case ChartDataSource.DATA_TYPE_MATRIX_DOUBLE:
			case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_3:
			default: {
				source.setCumulative(scope, false);
				source.setUseSize(scope, true);
			}
		}

	}

	@Override
	public void initdataset() {
		super.initdataset();
		chartdataset.setCommonXSeries(true);
		chartdataset.setCommonYSeries(true);
		chartdataset.setByCategory(false);
		chartdataset.forceNoXAccumulate = true;
		chartdataset.forceNoYAccumulate = true;
	}

	@Override
	protected AbstractRenderer createRenderer(final IScope scope, final String serieid) {
		final String style = this.getChartdataset().getDataSeries(scope, serieid).getStyle(scope);
		AbstractRenderer newr;
		switch (style) {
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
			default: {
				newr = new XYBlockRenderer();
				break;

			}
		}
		return newr;
	}

	protected static final LookupPaintScale createLUT(final int ncol, final float vmin, final float vmax,
			final Color start, final Color med, final Color end) {
		final float[][] colors = new float[][] {
				{ start.getRed() / 255f, start.getGreen() / 255f, start.getBlue() / 255f, start.getAlpha() / 255f },
				{ med.getRed() / 255f, med.getGreen() / 255f, med.getBlue() / 255f, med.getAlpha() / 255f },
				{ end.getRed() / 255f, end.getGreen() / 255f, end.getBlue() / 255f, end.getAlpha() / 255f } };
		final float[] limits = new float[] { 0, 0.5f, 1 };
		final LookupPaintScale lut = new LookupPaintScale(vmin, vmax, med);
		float val;
		float r, g, b, a;
		for (int j = 0; j < ncol; j++) {
			val = j / (ncol - 0.99f);
			int i = 0;
			for (i = 0; i < limits.length; i++) {
				if (val < limits[i]) {
					break;
				}
			}
			i = i - 1;
			r = colors[i][0] + (val - limits[i]) / (limits[i + 1] - limits[i]) * (colors[i + 1][0] - colors[i][0]);
			g = colors[i][1] + (val - limits[i]) / (limits[i + 1] - limits[i]) * (colors[i + 1][1] - colors[i][1]);
			b = colors[i][2] + (val - limits[i]) / (limits[i + 1] - limits[i]) * (colors[i + 1][2] - colors[i][2]);
			a = colors[i][3] + (val - limits[i]) / (limits[i + 1] - limits[i]) * (colors[i + 1][3] - colors[i][3]);
			lut.add(val * (vmax - vmin) + vmin, new Color(r, g, b, a));
		}
		return lut;
	}

	protected static final LookupPaintScale createLUT(final int ncol, final float vmin, final float vmax,
			final Color start, final Color end) {
		final float[][] colors = new float[][] {
				{ start.getRed() / 255f, start.getGreen() / 255f, start.getBlue() / 255f, start.getAlpha() / 255f },
				{ end.getRed() / 255f, end.getGreen() / 255f, end.getBlue() / 255f, end.getAlpha() / 255f } };
		final float[] limits = new float[] { 0, 1 };
		final LookupPaintScale lut = new LookupPaintScale(vmin, vmax, start);
		float val;
		float r, g, b, a;
		for (int j = 0; j < ncol; j++) {
			val = j / (ncol - 0.99f);
			final int i = 0;
			r = colors[i][0] + (val - limits[i]) / (limits[i + 1] - limits[i]) * (colors[i + 1][0] - colors[i][0]);
			g = colors[i][1] + (val - limits[i]) / (limits[i + 1] - limits[i]) * (colors[i + 1][1] - colors[i][1]);
			b = colors[i][2] + (val - limits[i]) / (limits[i + 1] - limits[i]) * (colors[i + 1][2] - colors[i][2]);
			a = colors[i][3] + (val - limits[i]) / (limits[i + 1] - limits[i]) * (colors[i + 1][3] - colors[i][3]);
			lut.add(val * (vmax - vmin) + vmin, new Color(r, g, b, a));
		}
		return lut;
	}

	protected void resetRenderer(final IScope scope, final String serieid) {
		final XYBlockRenderer newr = (XYBlockRenderer) this.getOrCreateRenderer(scope, serieid);

		// newr.setSeriesStroke(0, new BasicStroke(0));
		final ChartDataSeries myserie = this.getChartdataset().getDataSeries(scope, serieid);

		if (myserie.getMycolor() != null) {
			newr.setSeriesPaint(0, myserie.getMycolor());
		}
		if (myserie.getSValues(scope).size() > 0) {
			final double maxval = Collections.max(myserie.getSValues(scope));
			final double minval = Collections.min(myserie.getSValues(scope));
			Color cdeb = new Color(0, 0, 0, 0);
			if (myserie.getMyMincolor() != null) {
				cdeb = myserie.getMyMincolor();
			}
			Color cend = new Color(0.9f, 0.9f, 0.9f, 1.0f);
			if (myserie.getMycolor() != null) {
				cend = myserie.getMycolor();
			}

			LookupPaintScale paintscale = createLUT(100, (float) minval, (float) maxval, cdeb, cend);
			if (myserie.getMyMedcolor() != null) {
				paintscale = createLUT(100, (float) minval, (float) maxval, cdeb, myserie.getMyMedcolor(), cend);
			}

			newr.setPaintScale(paintscale);

			final NumberAxis scaleAxis = new NumberAxis(myserie.getName());
			scaleAxis.setAxisLinePaint(this.axesColor);
			scaleAxis.setTickMarkPaint(this.axesColor);
			scaleAxis.setTickLabelFont(this.getTickFont());
			scaleAxis.setRange(minval, maxval);
			scaleAxis.setAxisLinePaint(axesColor);
			scaleAxis.setLabelFont(getLabelFont());
			if (textColor != null) {
				scaleAxis.setLabelPaint(textColor);
				scaleAxis.setTickLabelPaint(textColor);
			}
			if (!this.getXTickValueVisible(scope)) {
				scaleAxis.setTickMarksVisible(false);
				scaleAxis.setTickLabelsVisible(false);

			}

			final PaintScaleLegend legend = new PaintScaleLegend(paintscale, scaleAxis);
			legend.setAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
			legend.setAxisOffset(5.0);
			// legend.setMargin(new RectangleInsets(5, 5, 5, 5));
			// legend.setFrame(new BlockBorder(Color.red));
			// legend.setPadding(new RectangleInsets(10, 10, 10, 10));
			// legend.setStripWidth(10);
			legend.setPosition(RectangleEdge.RIGHT);
			legend.setBackgroundPaint(this.backgroundColor);
			// ArrayList<PaintScaleLegend> caxe=new
			// ArrayList<PaintScaleLegend>();
			// caxe.add(legend);
			// chart.setSubtitles(caxe);
			if (!this.series_label_position.equals("none")) {
				chart.addSubtitle(legend);
			}

		}
	}

	@Override
	protected void clearDataSet(final IScope scope) {
		// TODO Auto-generated method stub
		super.clearDataSet(scope);
		final XYPlot plot = (XYPlot) this.chart.getPlot();
		for (int i = plot.getDatasetCount() - 1; i >= 1; i--) {
			plot.setDataset(i, null);
			plot.setRenderer(i, null);
		}
		((MatrixSeriesCollection) jfreedataset.get(0)).removeAllSeries();
		jfreedataset.clear();
		jfreedataset.add(0, new MatrixSeriesCollection());
		plot.setDataset((MatrixSeriesCollection) jfreedataset.get(0));
		plot.setRenderer(0, null);

		IdPosition.clear();
	}

	@Override
	protected void createNewSerie(final IScope scope, final String serieid) {

		final ChartDataSeries dataserie = chartdataset.getDataSeries(scope, serieid);
		final MatrixSeries serie = new MatrixSeries((String) dataserie.getSerieLegend(scope),
				Math.max(1, this.getChartdataset().getYSeriesValues().size()),
				Math.max(1, this.getChartdataset().getXSeriesValues().size()));
		final XYPlot plot = (XYPlot) this.chart.getPlot();

		final MatrixSeriesCollection firstdataset = (MatrixSeriesCollection) plot.getDataset();

		if (!IdPosition.containsKey(serieid)) {

			if (firstdataset.getSeriesCount() == 0) {
				firstdataset.addSeries(serie);
				plot.setDataset(0, firstdataset);

			} else {

				final MatrixSeriesCollection newdataset = new MatrixSeriesCollection();
				newdataset.addSeries(serie);
				jfreedataset.add(newdataset);
				plot.setDataset(jfreedataset.size() - 1, newdataset);

			}
			plot.setRenderer(jfreedataset.size() - 1, (XYItemRenderer) getOrCreateRenderer(scope, serieid));
			IdPosition.put(serieid, jfreedataset.size() - 1);
			// DEBUG.LOG("new serie"+serieid+" at
			// "+IdPosition.get(serieid)+" fdsize "+plot.getSeriesCount()+" jfds
			// "+jfreedataset.size()+" datasc "+plot.getDatasetCount());
			// TODO Auto-generated method stub

		}
	}

	@Override
	public void preResetSeries(final IScope scope) {
		this.clearDataSet(scope);
		final ArrayList<PaintScaleLegend> caxe = new ArrayList<>();
		chart.setSubtitles(caxe);

	}

	@Override
	protected void resetSerie(final IScope scope, final String serieid) {
		// TODO Auto-generated method stub
		this.createNewSerie(scope, serieid);
		final ChartDataSeries dataserie = chartdataset.getDataSeries(scope, serieid);
		final MatrixSeries serie =
				((MatrixSeriesCollection) jfreedataset.get(IdPosition.get(dataserie.getSerieId(scope)))).getSeries(0);
		final ArrayList<Double> XValues = dataserie.getXValues(scope);
		final ArrayList<Double> YValues = dataserie.getYValues(scope);
		final ArrayList<Double> SValues = dataserie.getSValues(scope);
		final NumberAxis domainAxis = (NumberAxis) ((XYPlot) this.chart.getPlot()).getDomainAxis();
		if (XValues.size() == 0) {
			if (!usexrangeinterval && !usexrangeminmax) {
				domainAxis.setAutoRange(false);
				domainAxis.setRange(-0.5, XValues.size() + 0.5);
			}

		}
		final NumberAxis rangeAxis = (NumberAxis) ((XYPlot) this.chart.getPlot()).getRangeAxis();
		if (YValues.size() == 0) {
			if (!useyrangeinterval && !useyrangeminmax) {
				rangeAxis.setAutoRange(false);
				rangeAxis.setRange(-0.5, YValues.size() + 0.5);
			}

		}
		// final NumberAxis domainAxis = (NumberAxis) ((XYPlot) this.chart.getPlot()).getDomainAxis();
		// final NumberAxis rangeAxis = (NumberAxis) ((XYPlot) this.chart.getPlot()).getRangeAxis();

		if (XValues.size() > 0) {
			domainAxis.setAutoRange(false);
			rangeAxis.setAutoRange(false);
			domainAxis.setTickLabelsVisible(this.getXTickValueVisible(scope));
			domainAxis.setTickMarksVisible(this.getXTickValueVisible(scope));
			rangeAxis.setTickLabelsVisible(this.getYTickValueVisible(scope));
			rangeAxis.setTickMarksVisible(this.getYTickValueVisible(scope));
			for (int i = 0; i < XValues.size(); i++) {

				if (XValues.get(i) > domainAxis.getUpperBound()) {
					if (!usexrangeinterval && !usexrangeminmax) {
						domainAxis.setAutoRange(false);
						domainAxis.setRange(-0.5, YValues.get(i) + 0.5);
					}

				}
				if (YValues.get(i) > rangeAxis.getUpperBound()) {
					if (!useyrangeinterval && !useyrangeminmax) {
						rangeAxis.setAutoRange(false);
						rangeAxis.setRange(-0.5, YValues.get(i) + 0.5);
					}

				}

				serie.update(YValues.get(i).intValue(), XValues.get(i).intValue(), SValues.get(i).doubleValue());
			}
		}
		this.resetRenderer(scope, serieid);

	}

	@Override
	public void resetAxes(final IScope scope) {
		NumberAxis domainAxis = (NumberAxis) ((XYPlot) this.chart.getPlot()).getDomainAxis();
		NumberAxis rangeAxis = (NumberAxis) ((XYPlot) this.chart.getPlot()).getRangeAxis();

		if (getX_LogScale(scope)) {
			final LogarithmicAxis logAxis = new LogarithmicAxis(domainAxis.getLabel());
			logAxis.setAllowNegativesFlag(true);
			((XYPlot) this.chart.getPlot()).setDomainAxis(logAxis);
			domainAxis = logAxis;
		}
		if (getY_LogScale(scope)) {
			final LogarithmicAxis logAxis = new LogarithmicAxis(rangeAxis.getLabel());
			logAxis.setAllowNegativesFlag(true);
			((XYPlot) this.chart.getPlot()).setRangeAxis(logAxis);
			rangeAxis = logAxis;
		}
		if (!usexrangeinterval && !usexrangeminmax) {
			// domainAxis.setAutoRangeMinimumSize(0.5);
			// domainAxis.setAutoRange(true);
		}

		if (this.usexrangeinterval) {
			domainAxis.setFixedAutoRange(xrangeinterval);
			domainAxis.setAutoRangeMinimumSize(xrangeinterval);
			domainAxis.setAutoRange(true);
		}
		if (this.usexrangeminmax) {
			domainAxis.setRange(xrangemin, xrangemax);
		}

		if (!useyrangeinterval && !useyrangeminmax) {
			// rangeAxis.setAutoRangeMinimumSize(0.5);
			// rangeAxis.setAutoRange(true);
		}

		if (this.useyrangeinterval) {
			rangeAxis.setFixedAutoRange(yrangeinterval);
			rangeAxis.setAutoRangeMinimumSize(yrangeinterval);
			rangeAxis.setAutoRange(true);
		}
		if (this.useyrangeminmax) {
			rangeAxis.setRange(yrangemin, yrangemax);
		}
		if (this.series_label_position.equals("none")) {
			if (this.chart.getLegend() != null) {
				this.chart.getLegend().setVisible(false);
			}
		}
		if (!this.getXTickLineVisible(scope)) {
			((XYPlot) this.chart.getPlot()).setDomainGridlinesVisible(false);

		}
		if (!this.getYTickLineVisible(scope)) {
			((XYPlot) this.chart.getPlot()).setRangeGridlinesVisible(false);

		}

	}

	@Override
	protected void initRenderer(final IScope scope) {
		// TODO Auto-generated method stub
		final XYPlot plot = (XYPlot) this.chart.getPlot();
		defaultrenderer = new XYBlockRenderer();
		plot.setRenderer((XYBlockRenderer) defaultrenderer);

	}

	@Override
	public void setUseXSource(final IScope scope, final IExpression expval) {
		// if there is something to do to use custom X axis

	}

	@Override
	public void setUseXLabels(final IScope scope, final IExpression expval) {
		// if there is something to do to use custom X axis
		final XYPlot pp = (XYPlot) chart.getPlot();

		((NumberAxis) pp.getDomainAxis()).setNumberFormatOverride(new NumberFormat() {

			@Override
			public StringBuffer format(final double number, final StringBuffer toAppendTo, final FieldPosition pos) {
				final int ind = chartdataset.XSeriesValues.indexOf(number);
				if (ind >= 0) { return new StringBuffer("" + chartdataset.Xcategories.get(ind)); }
				return new StringBuffer("");

			}

			@Override
			public StringBuffer format(final long number, final StringBuffer toAppendTo, final FieldPosition pos) {
				return new StringBuffer("n" + number);
				// return new StringBuffer(String.format("%s", number));
			}

			@Override
			public Number parse(final String source, final ParsePosition parsePosition) {
				return null;
			}
		});

	}

	@Override
	public void setUseYLabels(final IScope scope, final IExpression expval) {
		// if there is something to do to use custom X axis
		final XYPlot pp = (XYPlot) chart.getPlot();

		((NumberAxis) pp.getRangeAxis()).setNumberFormatOverride(new NumberFormat() {

			@Override
			public StringBuffer format(final double number, final StringBuffer toAppendTo, final FieldPosition pos) {
				final int ind = chartdataset.YSeriesValues.indexOf(number);
				if (ind >= 0) { return new StringBuffer("" + chartdataset.Ycategories.get(ind)); }
				return new StringBuffer("");

			}

			@Override
			public StringBuffer format(final long number, final StringBuffer toAppendTo, final FieldPosition pos) {
				return new StringBuffer("n" + number);
				// return new StringBuffer(String.format("%s", number));
			}

			@Override
			public Number parse(final String source, final ParsePosition parsePosition) {
				return null;
			}
		});

	}

	@Override
	public void initChart(final IScope scope, final String chartname) {
		super.initChart(scope, chartname);

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
		if (textColor != null) {
			pp.getDomainAxis().setLabelPaint(textColor);
			pp.getDomainAxis().setTickLabelPaint(textColor);
		}
		if (xtickunit > 0) {
			((NumberAxis) pp.getDomainAxis()).setTickUnit(new NumberTickUnit(xtickunit));
		}

		pp.getRangeAxis().setAxisLinePaint(axesColor);
		pp.getRangeAxis().setLabelFont(getLabelFont());
		pp.getRangeAxis().setTickLabelFont(getTickFont());
		if (textColor != null) {
			pp.getRangeAxis().setLabelPaint(textColor);
			pp.getRangeAxis().setTickLabelPaint(textColor);
		}
		if (ytickunit > 0) {
			((NumberAxis) pp.getRangeAxis()).setTickUnit(new NumberTickUnit(ytickunit));
		}

		// resetAutorange(scope);

		if (xlabel != null && !xlabel.isEmpty()) {
			pp.getDomainAxis().setLabel(xlabel);
		}
		if (ylabel != null && !ylabel.isEmpty()) {
			pp.getRangeAxis().setLabel(ylabel);
		}

	}

	@Override
	public void getModelCoordinatesInfo(final int xOnScreen, final int yOnScreen, final IDisplaySurface g,
			final Point positionInPixels, final StringBuilder sb) {
		final int x = xOnScreen - positionInPixels.x;
		final int y = yOnScreen - positionInPixels.y;
		final ChartEntity entity = info.getEntityCollection().getEntity(x, y);
		// getChart().handleClick(x, y, info);
		if (entity instanceof XYItemEntity) {
			final XYDataset data = ((XYItemEntity) entity).getDataset();
			final int index = ((XYItemEntity) entity).getItem();
			final int series = ((XYItemEntity) entity).getSeriesIndex();
			final double xx = data.getXValue(series, index);
			final double yy = data.getYValue(series, index);
			final XYPlot plot = (XYPlot) getJFChart().getPlot();
			final ValueAxis xAxis = plot.getDomainAxis(series);
			final ValueAxis yAxis = plot.getRangeAxis(series);
			final boolean xInt = xx % 1 == 0;
			final boolean yInt = yy % 1 == 0;
			String xTitle = xAxis.getLabel();
			if (StringUtils.isBlank(xTitle)) {
				xTitle = "X";
			}
			String yTitle = yAxis.getLabel();
			if (StringUtils.isBlank(yTitle)) {
				yTitle = "Y";
			}
			sb.append(xTitle).append(" ").append(xInt ? (int) xx : String.format("%.2f", xx));
			sb.append(" | ").append(yTitle).append(" ").append(yInt ? (int) yy : String.format("%.2f", yy));
			return;
		} else if (entity instanceof PieSectionEntity) {
			final String title = ((PieSectionEntity) entity).getSectionKey().toString();
			final PieDataset data = ((PieSectionEntity) entity).getDataset();
			final int index = ((PieSectionEntity) entity).getSectionIndex();
			final double xx = data.getValue(index).doubleValue();
			final boolean xInt = xx % 1 == 0;
			sb.append(title).append(" ").append(xInt ? (int) xx : String.format("%.2f", xx));
			return;
		} else if (entity instanceof CategoryItemEntity) {
			final Comparable<?> columnKey = ((CategoryItemEntity) entity).getColumnKey();
			final String title = columnKey.toString();
			final CategoryDataset data = ((CategoryItemEntity) entity).getDataset();
			final Comparable<?> rowKey = ((CategoryItemEntity) entity).getRowKey();
			final double xx = data.getValue(rowKey, columnKey).doubleValue();
			final boolean xInt = xx % 1 == 0;
			sb.append(title).append(" ").append(xInt ? (int) xx : String.format("%.2f", xx));
			return;
		}
	}

}
