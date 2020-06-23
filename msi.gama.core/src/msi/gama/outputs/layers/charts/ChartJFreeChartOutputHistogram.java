/*******************************************************************************************************
 *
 * msi.gama.outputs.layers.charts.ChartJFreeChartOutputHistogram.java, in plugin msi.gama.core, is part of the source
 * code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs.layers.charts;

import java.awt.Point;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.SubCategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.AbstractCategoryItemRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.GradientBarPainter;
import org.jfree.chart.renderer.category.LevelRenderer;
import org.jfree.chart.renderer.category.ScatterRenderer;
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.category.StatisticalLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.GradientXYBarPainter;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.XYDataset;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;

public class ChartJFreeChartOutputHistogram extends ChartJFreeChartOutput {

	boolean useSubAxis = false;
	boolean useMainAxisLabel = true;

	public static void enableFlatLook(final boolean flat) {
		if (flat) {
			BarRenderer.setDefaultBarPainter(new StandardBarPainter());
			BarRenderer.setDefaultShadowsVisible(false);
			XYBarRenderer.setDefaultBarPainter(new StandardXYBarPainter());
			XYBarRenderer.setDefaultShadowsVisible(false);
			StackedBarRenderer.setDefaultBarPainter(new StandardBarPainter());
			StackedBarRenderer.setDefaultShadowsVisible(false);
		} else {
			BarRenderer.setDefaultBarPainter(new GradientBarPainter());
			BarRenderer.setDefaultShadowsVisible(true);
			XYBarRenderer.setDefaultBarPainter(new GradientXYBarPainter());
			XYBarRenderer.setDefaultShadowsVisible(true);
			StackedBarRenderer.setDefaultBarPainter(new GradientBarPainter());
			StackedBarRenderer.setDefaultShadowsVisible(true);
		}
	}

	static {
		enableFlatLook(GamaPreferences.Displays.CHART_FLAT.getValue());
		GamaPreferences.Displays.CHART_FLAT.onChange(newValue -> enableFlatLook(newValue));
	}

	public ChartJFreeChartOutputHistogram(final IScope scope, final String name, final IExpression typeexp) {
		super(scope, name, typeexp);
		// TODO Auto-generated constructor stub

	}

	@Override
	public void createChart(final IScope scope) {
		super.createChart(scope);
		jfreedataset.add(0, new DefaultCategoryDataset());
		PlotOrientation orientation = PlotOrientation.VERTICAL;
		if (reverse_axes) {
			orientation = PlotOrientation.HORIZONTAL;
		}
		if (style.equals(IKeyword.THREE_D)) {
			chart = ChartFactory.createBarChart(getName(), null, null, null, orientation, true, true, false);
		} else if (style.equals(IKeyword.STACK)) {
			chart = ChartFactory.createStackedBarChart(getName(), null, null, null, orientation, true, true, false);
		} else {
			chart = ChartFactory.createBarChart(getName(), null, null, null, orientation, true, true, false);

		}

	}

	@Override
	public void initdataset() {
		super.initdataset();
		if (getType() == ChartOutput.HISTOGRAM_CHART) {
			chartdataset.setCommonXSeries(true);
			chartdataset.setByCategory(true);
		}
	}

	@Override
	public void setDefaultPropertiesFromType(final IScope scope, final ChartDataSource source, final int type_val) {
		// TODO Auto-generated method stub

		switch (type_val) {
			case ChartDataSource.DATA_TYPE_LIST_DOUBLE_N:
			case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_N:
			case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_12:
			case ChartDataSource.DATA_TYPE_LIST_POINT:
			case ChartDataSource.DATA_TYPE_MATRIX_DOUBLE:
			case ChartDataSource.DATA_TYPE_LIST_DOUBLE_3:
			case ChartDataSource.DATA_TYPE_LIST_LIST_DOUBLE_3: {
				source.setCumulative(scope, false);
				source.setUseSize(scope, false);
				break;

			}
			default: {
				source.setCumulative(scope, false); // never cumulative by default
				source.setUseSize(scope, false);
			}
		}

	}

	static class LabelGenerator extends StandardCategoryItemLabelGenerator {
		/**
		 * Generates an item label.
		 *
		 * @param dataset
		 *            the dataset.
		 * @param series
		 *            the series index.
		 * @param category
		 *            the category index.
		 *
		 * @return the label.
		 */
		@Override
		public String generateLabel(final CategoryDataset dataset, final int series, final int category) {
			return dataset.getRowKey(series).toString();
		}
	}

	@Override
	protected AbstractRenderer createRenderer(final IScope scope, final String serieid) {
		final String style = this.getChartdataset().getDataSeries(scope, serieid).getStyle(scope);
		AbstractRenderer newr = new BarRenderer();
		switch (style) {
			case IKeyword.STACK: {
				newr = new StackedBarRenderer();
				break;

			}

			case IKeyword.DOT: {
				newr = new ScatterRenderer();
				break;
			}
			case IKeyword.AREA: {
				newr = new StackedAreaRenderer();
				break;
			}
			case IKeyword.LINE: {
				newr = new StatisticalLineAndShapeRenderer();
				break;
			}
			case IKeyword.STEP: {
				newr = new LevelRenderer();
				break;
			}
			case IKeyword.RING:
			case IKeyword.EXPLODED:
			case IKeyword.THREE_D:
			default: {
				newr = new BarRenderer();
				break;

			}
		}
		return newr;
	}

	protected void resetRenderer(final IScope scope, final String serieid) {
		// AbstractCategoryItemRenderer
		// newr=(AbstractCategoryItemRenderer)this.getOrCreateRenderer(scope,
		// serieid);
		final CategoryPlot plot = (CategoryPlot) this.chart.getPlot();
		final AbstractCategoryItemRenderer newr = (AbstractCategoryItemRenderer) plot.getRenderer();
		// if
		// (serieid!=this.getChartdataset().series.keySet().iterator().next())
		// newr=(AbstractCategoryItemRenderer)this.getOrCreateRenderer(scope,
		// serieid);

		final ChartDataSeries myserie = this.getChartdataset().getDataSeries(scope, serieid);
		if (!IdPosition.containsKey(serieid)) {
			// DEBUG.LOG("pb!!!");
		} else {
			final int myrow = IdPosition.get(serieid);
			if (myserie.getMycolor() != null) {
				newr.setSeriesPaint(myrow, myserie.getMycolor());
			}

			if (this.series_label_position.equals("onchart")) {
				// ((BarRenderer)newr).setBaseItemLabelGenerator(new
				// LabelGenerator());
				newr.setDefaultItemLabelGenerator(new LabelGenerator());
				final ItemLabelPosition itemlabelposition =
						new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER);
				newr.setDefaultPositiveItemLabelPosition(itemlabelposition);
				newr.setDefaultNegativeItemLabelPosition(itemlabelposition);
				newr.setDefaultItemLabelsVisible(true);
			}

			if (newr instanceof BarRenderer) {
				if (gap >= 0) {
					((BarRenderer) newr).setMaximumBarWidth(1 - gap);

				}

			}
		}

	}

	@Override
	protected void clearDataSet(final IScope scope) {
		// TODO Auto-generated method stub
		super.clearDataSet(scope);
		final CategoryPlot plot = (CategoryPlot) this.chart.getPlot();
		for (int i = plot.getDatasetCount() - 1; i >= 1; i--) {
			plot.setDataset(i, null);
			plot.setRenderer(i, null);
		}
		((DefaultCategoryDataset) jfreedataset.get(0)).clear();
		jfreedataset.clear();
		jfreedataset.add(0, new DefaultCategoryDataset());
		plot.setDataset((DefaultCategoryDataset) jfreedataset.get(0));
		plot.setRenderer(0, null);
		IdPosition.clear();
		nbseries = 0;
	}

	@Override
	protected void createNewSerie(final IScope scope, final String serieid) {
		// final ChartDataSeries dataserie = chartdataset.getDataSeries(scope,
		// serieid);
		// final XYIntervalSeries serie = new
		// XYIntervalSeries(dataserie.getSerieLegend(scope), false, true);
		if(!IdPosition.containsKey(serieid)) {

			final CategoryPlot plot = (CategoryPlot) this.chart.getPlot();
	
			final DefaultCategoryDataset firstdataset = (DefaultCategoryDataset) plot.getDataset();
	
			if (nbseries == 0) {
				plot.setDataset(0, firstdataset);
				plot.setRenderer(nbseries, (CategoryItemRenderer) getOrCreateRenderer(scope, serieid));
			} else {
	
				// DefaultCategoryDataset newdataset=new DefaultCategoryDataset();
				// jfreedataset.add(newdataset);
				// plot.setDataset(jfreedataset.size()-1, newdataset);
				// plot.setDataset(nbseries, firstdataset);
			}
		
			nbseries++;
			// plot.setRenderer(nbseries-1,
			// (CategoryItemRenderer)getOrCreateRenderer(scope,serieid));
			IdPosition.put(serieid, nbseries - 1);
		}

		// DEBUG.LOG("new serie"+serieid+" at
		// "+IdPosition.get(serieid)+" fdsize "+plot.getCategories().size()+"
		// jfds "+jfreedataset.size()+" datasc "+plot.getDatasetCount()+" nbse
		// "+nbseries);
		// TODO Auto-generated method stub
	}

	@Override
	public void removeSerie(final IScope scope, final String serieid) {
		// TODO Auto-generated method stub
		super.removeSerie(scope, serieid);
		this.clearDataSet(scope);
	}

	@Override
	protected void resetSerie(final IScope scope, final String serieid) {
		// TODO Auto-generated method stub

		final ChartDataSeries dataserie = chartdataset.getDataSeries(scope, serieid);
		// DefaultCategoryDataset serie=((DefaultCategoryDataset)
		// jfreedataset.get(IdPosition.get(dataserie.getSerieId(scope))));
		final DefaultCategoryDataset serie = (DefaultCategoryDataset) jfreedataset.get(0);
		if (serie.getRowKeys().contains(serieid)) {
			serie.removeRow(serieid);
		}
		final ArrayList<String> CValues = dataserie.getCValues(scope);
		final ArrayList<Double> YValues = dataserie.getYValues(scope);
		final ArrayList<Double> SValues = dataserie.getSValues(scope);
		if (CValues.size() > 0) {
			// TODO Hack to speed up, change!!!
			// final CategoryAxis domainAxis = ((CategoryPlot)
			// this.chart.getPlot()).getDomainAxis();
			final NumberAxis rangeAxis = (NumberAxis) ((CategoryPlot) this.chart.getPlot()).getRangeAxis();
			rangeAxis.setAutoRange(false);
			for (int i = 0; i < CValues.size(); i++) {
				if (getY_LogScale(scope)) {
					final double val = YValues.get(i);
					if (val <= 0) {
						throw GamaRuntimeException.warning("Log scale with <=0 value:" + val, scope);
					} else {
						serie.addValue(YValues.get(i), serieid, CValues.get(i));
					}

				} else {
					serie.addValue(YValues.get(i), serieid, CValues.get(i));

				}
				// ((ExtendedCategoryAxis)domainAxis).addSubLabel(CValues.get(i),
				// serieid);;
			}
		}
		if (SValues.size() > 0) {
			// what to do with Z values??

		}

		this.resetRenderer(scope, serieid);

	}

	@Override
	public void resetAxes(final IScope scope) {
		final CategoryPlot pp = (CategoryPlot) this.chart.getPlot();
		NumberAxis rangeAxis = (NumberAxis) ((CategoryPlot) this.chart.getPlot()).getRangeAxis();
		if (getY_LogScale(scope)) {
			final LogarithmicAxis logAxis = new LogarithmicAxis(rangeAxis.getLabel());
			logAxis.setAllowNegativesFlag(true);
			((CategoryPlot) this.chart.getPlot()).setRangeAxis(logAxis);
			rangeAxis = logAxis;
		}

		if (!useyrangeinterval && !useyrangeminmax) {
			rangeAxis.setAutoRange(true);
		}

		if (this.useyrangeinterval) {
			rangeAxis.setFixedAutoRange(yrangeinterval);
			rangeAxis.setAutoRangeMinimumSize(yrangeinterval);
			rangeAxis.setAutoRange(true);

		}
		if (this.useyrangeminmax) {
			rangeAxis.setRange(yrangemin, yrangemax);

		}

		resetDomainAxis(scope);

		final CategoryAxis domainAxis = ((CategoryPlot) this.chart.getPlot()).getDomainAxis();

		pp.setDomainGridlinePaint(axesColor);
		pp.setRangeGridlinePaint(axesColor);
		pp.setRangeCrosshairVisible(true);

		pp.getRangeAxis().setAxisLinePaint(axesColor);
		pp.getRangeAxis().setLabelFont(getLabelFont());
		pp.getRangeAxis().setTickLabelFont(getTickFont());
		if (textColor != null) {
			pp.getRangeAxis().setLabelPaint(textColor);
			pp.getRangeAxis().setTickLabelPaint(textColor);
		}
		if (getYTickUnit(scope) > 0) {
			((NumberAxis) pp.getRangeAxis()).setTickUnit(new NumberTickUnit(getYTickUnit(scope)));
		}

		if (getYLabel(scope) != null && !getYLabel(scope).isEmpty()) {
			pp.getRangeAxis().setLabel(getYLabel(scope));
		}
		if (this.series_label_position.equals("yaxis")) {
			pp.getRangeAxis().setLabel(this.getChartdataset().getDataSeriesIds(scope).iterator().next());
			chart.getLegend().setVisible(false);
		}

		if (getXLabel(scope) != null && !getXLabel(scope).isEmpty()) {
			pp.getDomainAxis().setLabel(getXLabel(scope));
		}

		if (this.useSubAxis) {
			for (final String serieid : chartdataset.getDataSeriesIds(scope)) {
				((SubCategoryAxis) domainAxis).addSubCategory(serieid);
			}

		}
		if (!this.getYTickLineVisible(scope)) {
			pp.setDomainGridlinesVisible(false);
		}

		if (!this.getYTickLineVisible(scope)) {
			pp.setRangeCrosshairVisible(false);

		}

		if (!this.getYTickValueVisible(scope)) {
			pp.getRangeAxis().setTickMarksVisible(false);
			pp.getRangeAxis().setTickLabelsVisible(false);

		}

	}

	public void resetDomainAxis(final IScope scope) {
		// TODO Auto-generated method stub
		final CategoryPlot pp = (CategoryPlot) chart.getPlot();
		if (this.useSubAxis) {
			final SubCategoryAxis newAxis = new SubCategoryAxis(pp.getDomainAxis().getLabel());
			pp.setDomainAxis(newAxis);
		}

		pp.getDomainAxis().setAxisLinePaint(axesColor);
		pp.getDomainAxis().setTickLabelFont(getTickFont());
		pp.getDomainAxis().setLabelFont(getLabelFont());
		if (textColor != null) {
			pp.getDomainAxis().setLabelPaint(textColor);
			pp.getDomainAxis().setTickLabelPaint(textColor);
			if (this.series_label_position.equals("xaxis")) {
				((SubCategoryAxis)pp.getDomainAxis()).setSubLabelPaint(textColor);
			}			
		}

		if (gap > 0) {

			pp.getDomainAxis().setCategoryMargin(gap);
			pp.getDomainAxis().setUpperMargin(gap);
			pp.getDomainAxis().setLowerMargin(gap);
		}

		if (this.useSubAxis && !this.useMainAxisLabel) {
			pp.getDomainAxis().setTickLabelsVisible(false);
			// pp.getDomainAxis().setTickLabelPaint(this.backgroundColor);
			// pp.getDomainAxis().setLabelFont(new Font(labelFontFace,
			// labelFontStyle, 1));
		}
		if (!this.getYTickLineVisible(scope)) {
			pp.setDomainGridlinesVisible(false);
		}

		if (!this.getYTickLineVisible(scope)) {
			pp.setRangeCrosshairVisible(false);

		}

		if (!this.getYTickValueVisible(scope)) {
			pp.getRangeAxis().setTickMarksVisible(false);
			pp.getRangeAxis().setTickLabelsVisible(false);

		}
		if (!this.getXTickValueVisible(scope)) {
			pp.getDomainAxis().setTickMarksVisible(false);
			pp.getDomainAxis().setTickLabelsVisible(false);

		}

	}
	//
	// @Override
	// public void initChart(final IScope scope, final String chartname) {
	// super.initChart(scope, chartname);
	// // final CategoryPlot pp = (CategoryPlot) chart.getPlot();
	//
	// }

	@Override
	public void initChart_post_data_init(final IScope scope) {
		// TODO Auto-generated method stub
		super.initChart_post_data_init(scope);
		final CategoryPlot pp = (CategoryPlot) chart.getPlot();

		final String sty = getStyle();
		this.useSubAxis = false;
		switch (sty) {
			case IKeyword.STACK: {
				if (this.series_label_position.equals("xaxis")) {
					this.series_label_position = "default";
				}
				if (this.series_label_position.equals("default")) {
					this.series_label_position = "legend";
				}
				break;
			}
			default: {
				if (this.series_label_position.equals("default")) {
					if (this.getChartdataset().getSources().size() > 0) {
						final ChartDataSource onesource = this.getChartdataset().getSources().get(0);
						if (onesource.isCumulative) {
							this.series_label_position = "legend";
						} else {
							this.series_label_position = "xaxis";
							useMainAxisLabel = false;
						}

					} else {
						this.series_label_position = "legend";

					}
				}
				break;
			}
		}
		if (this.series_label_position.equals("xaxis")) {
			this.useSubAxis = true;
		}

		if (!this.series_label_position.equals("legend")) {
			chart.getLegend().setVisible(false);
			// legend is useless, but I find it nice anyway... Could put back...
		}
		this.resetDomainAxis(scope);

		pp.setDomainGridlinePaint(axesColor);
		pp.setRangeGridlinePaint(axesColor);
		if (!this.getXTickLineVisible(scope)) {
			pp.setDomainGridlinesVisible(false);
		}
		if (!this.getYTickLineVisible(scope)) {
			pp.setRangeGridlinesVisible(false);
		}
		pp.setRangeCrosshairVisible(true);
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

		if (ylabel != null && !ylabel.isEmpty()) {
			pp.getRangeAxis().setLabel(ylabel);
		}
		if (this.series_label_position.equals("yaxis")) {
			pp.getRangeAxis().setLabel(this.getChartdataset().getDataSeriesIds(scope).iterator().next());
			chart.getLegend().setVisible(false);
		}

		if (xlabel != null && !xlabel.isEmpty()) {
			pp.getDomainAxis().setLabel(xlabel);
		}
		if (textColor != null) {
			pp.getDomainAxis().setLabelPaint(textColor);
			pp.getDomainAxis().setTickLabelPaint(textColor);
			if (this.series_label_position.equals("xaxis")) {
				((SubCategoryAxis)pp.getDomainAxis()).setSubLabelPaint(textColor);
			}			
		}
	}

	@Override
	protected void initRenderer(final IScope scope) {
		// final CategoryPlot pp = (CategoryPlot) chart.getPlot();
		// final BarRenderer renderer = (BarRenderer) pp.getRenderer();

		// TODO Auto-generated method stub
		// CategoryPlot plot = (CategoryPlot)this.chart.getPlot();
		// defaultrenderer = new BarRenderer();
		// plot.setRenderer((BarRenderer)defaultrenderer);

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
