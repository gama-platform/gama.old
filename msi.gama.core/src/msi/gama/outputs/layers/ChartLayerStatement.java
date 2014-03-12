/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.outputs.layers;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.layers.ChartDataStatement.ChartData;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.LabelExpressionDescription;
import msi.gaml.expressions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.*;
import msi.gaml.operators.Random;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.*;
import org.jfree.data.statistics.*;
import org.jfree.data.xy.*;
import org.jfree.ui.RectangleInsets;

/**
 * Written by drogoul Modified on 9 nov. 2009
 * 
 * @todo Description
 * 
 */
@symbol(name = IKeyword.CHART, kind = ISymbolKind.LAYER, with_sequence = true)
@inside(symbols = IKeyword.DISPLAY)
@facets(value = {
	/* @facet(name = ISymbol.VALUE, type = TypeManager.STRING, optional = true), */
	@facet(name = ChartLayerStatement.XRANGE, type = { IType.FLOAT, IType.INT, IType.POINT }, optional = true),
	@facet(name = ChartLayerStatement.YRANGE, type = { IType.FLOAT, IType.INT, IType.POINT }, optional = true),
	@facet(name = IKeyword.POSITION, type = IType.POINT, optional = true),
	@facet(name = IKeyword.SIZE, type = IType.POINT, optional = true),
	@facet(name = IKeyword.BACKGROUND, type = IType.COLOR, optional = true),
	@facet(name = IKeyword.TIMEXSERIES, type = IType.LIST, optional = true),
	@facet(name = IKeyword.AXES, type = IType.COLOR, optional = true),
	@facet(name = IKeyword.TYPE, type = IType.ID, values = { IKeyword.XY, IKeyword.HISTOGRAM, IKeyword.SERIES,
		IKeyword.PIE, IKeyword.BOX_WHISKER }, optional = true),
	@facet(name = IKeyword.STYLE, type = IType.ID, values = { IKeyword.EXPLODED, IKeyword.THREE_D, IKeyword.STACK,
		IKeyword.BAR }, optional = true), @facet(name = IKeyword.TRANSPARENCY, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.GAP, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false),
	@facet(name = IKeyword.FONT, type = IType.ID, optional = true),
	@facet(name = IKeyword.COLOR, type = IType.COLOR, optional = true) }, omissible = IKeyword.NAME)
public class ChartLayerStatement extends AbstractLayerStatement {

	public static final String XRANGE = "x_range";
	public static final String YRANGE = "y_range";

	public class DataDeclarationSequence extends AbstractStatementSequence {

		public DataDeclarationSequence(final IDescription desc) {
			super(desc);
		}

		// We create the variable in which the datas will be accumulated
		@Override
		public void enterScope(final IScope scope) {
			super.enterScope(scope);
			scope.addVarWithValue(ChartDataStatement.DATAS, new ArrayList());
			scope.addVarWithValue(ChartDataListStatement.DATALIST, LabelExpressionDescription.create("idontknowwhattouse").getExpression()); //to change with something temp...
			scope.addVarWithValue(ChartDataListStatement.UPDATEDATA, new Boolean(false));
			scope.addVarWithValue(ChartDataListStatement.REVERSEDATA, new Boolean(false));
		}

		// We save the datas once the computation is finished
		@Override
		public void leaveScope(final IScope scope) {
			datas = (List<ChartData>) scope.getVarValue(ChartDataStatement.DATAS);
			updateseries = (Boolean) scope.getVarValue(ChartDataListStatement.UPDATEDATA);
			reverseseries = (Boolean) scope.getVarValue(ChartDataListStatement.REVERSEDATA);
			listvalue = (IExpression) scope.getVarValue(ChartDataListStatement.DATALIST);
			categoryvalue = (IExpression) scope.getVarValue(ChartDataListStatement.CATEGNAMES);
			serievalue = (IExpression) scope.getVarValue(ChartDataListStatement.SERIESNAMES);
			super.leaveScope(scope);
		}

	}

	private static final int SERIES_CHART = 0;
	private static final int HISTOGRAM_CHART = 1;
	private static final int PIE_CHART = 2;
	private static final int XY_CHART = 3;
	private static final int BOX_WHISKER_CHART = 4;
	private static final String nl = java.lang.System.getProperty("line.separator");
	private int type = SERIES_CHART;
	private String style = IKeyword.DEFAULT;
	private JFreeChart chart = null;
	private StringBuilder history;
	private static String chartFolder = "charts";
	private GamaColor backgroundColor = null, axesColor = null;
	private final Map<String, Integer> expressions_index = new HashMap();
	private Dataset dataset;
	private boolean exploded;
	public boolean updateseries=false;
	public boolean reverseseries=false;
	IExpression listvalue;
	IExpression categoryvalue=null;
	IExpression serievalue=null;

	static String xAxisName = "'time'";
	List<ChartData> datas;
	final Map<String, Double> lastValues;
	Long lastComputeCycle;
	ChartDataStatement timeSeriesXData = null;
	DataDeclarationSequence dataDeclaration = new DataDeclarationSequence(null);

	public JFreeChart getChart() {
		return chart;
	}

	public ChartLayerStatement(/* final ISymbol context, */final IDescription desc) throws GamaRuntimeException {
		super(desc);
		axesColor = Cast.asColor(null, "black");
		lastValues = new HashMap();
		lastComputeCycle = 0l;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		dataDeclaration.setChildren(commands);
	}

	void createSeries(final IScope scope, final boolean isTimeSeries) throws GamaRuntimeException {
		final XYPlot plot = (XYPlot) chart.getPlot();
		final NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		if ( isTimeSeries ) {
			domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			if ( timeSeriesXData == null ) {

				timeSeriesXData =
					(ChartDataStatement) DescriptionFactory.create(IKeyword.DATA, description, IKeyword.LEGEND,
						xAxisName, IKeyword.VALUE, SimulationAgent.CYCLE).compile();
				if ( getFacet(IKeyword.TIMEXSERIES) != null ) {
					timeSeriesXData.getDescription().getFacets().get(IKeyword.VALUE)
						.setExpression(getFacet(IKeyword.TIMEXSERIES));
				}
			}

			if ( !datas.contains(timeSeriesXData) ) {
				datas.add(0, timeSeriesXData.createData(scope));
			}
		}
		IExpression expr = getFacet(XRANGE);
		if ( expr != null ) {
			Object range = expr.value(scope);
			// Double range = Cast.asFloat(scope, expr.value(scope));

			if ( range instanceof Number ) {
				double r = ((Number) range).doubleValue();
				if ( r > 0 ) {
					domainAxis.setFixedAutoRange(r);
					domainAxis.setAutoRangeMinimumSize(r);
				}
				domainAxis.setAutoRangeIncludesZero(false);
			} else if ( range instanceof GamaPoint ) {
				domainAxis.setRange(((GamaPoint) range).getX(), ((GamaPoint) range).getY());
			}
		}
		domainAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 10));
		domainAxis.setLabel(datas.get(0).getName());
		final NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
		expr = getFacet(YRANGE);
		if ( expr != null ) {
			Object range = expr.value(scope);
			// Double range = Cast.asFloat(scope, expr.value(scope));

			if ( range instanceof Number ) {
				double r = ((Number) range).doubleValue();
				if ( r > 0 ) {
					yAxis.setFixedAutoRange(r);
					yAxis.setAutoRangeMinimumSize(r);
				}
				yAxis.setAutoRangeIncludesZero(false);
			} else if ( range instanceof GamaPoint ) {
				yAxis.setRange(((GamaPoint) range).getX(), ((GamaPoint) range).getY());
			}
		}
		if ( datas.size() == 2 ) {
			yAxis.setLabelFont(new Font("SansSerif", Font.BOLD, 10));
			yAxis.setLabel(datas.get(1).getName());
			chart.removeLegend();
		}
		final LegendTitle ll = chart.getLegend();
		if ( ll != null ) {
			ll.setItemFont(new Font("SansSerif", Font.PLAIN, 10));
		}

		for ( int i = 0; i < datas.size(); i++ ) {
			ChartData e = datas.get(i);

			final String legend = e.getName();
			if ( i != 0 | !isTimeSeries) { // the first data is the domain
				dataset = new DefaultTableXYDataset();
				final XYSeries serie = new XYSeries(legend, false, false);
				((DefaultTableXYDataset) dataset).addSeries(serie);
				expressions_index.put(legend, i);
				plot.setRenderer(i, (XYItemRenderer) e.getRenderer(), false);
				// final Color c = e.getColor();
				// ((XYLineAndShapeRenderer) plot.getRenderer(i)).setSeriesPaint(0, c);
				// TODO Control this with a facet
				// ((XYLineAndShapeRenderer) plot.getRenderer(i)).setBaseShapesFilled(false);
				// TODO Control this with a facet
				// ((XYLineAndShapeRenderer) plot.getRenderer(i)).setSeriesShapesVisible(0, false);
				plot.setDataset(i, (DefaultTableXYDataset) dataset);
			}
			history.append(legend);
			history.append(',');

		}

		history.deleteCharAt(history.length() - 1);
		history.append(nl);

	}

	/**
	 * create dataset for box_whisker chart
	 * @return A sample dataset.
	 */
	private BoxAndWhiskerCategoryDataset createWhisker(final IScope scope) {

		final CategoryPlot plot = (CategoryPlot) chart.getPlot();
		// final int seriesCount = 1;
		final int categoryCount = 3;
		final int entityCount = 2;

		final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
		for ( int i = 0; i < datas.size(); i++ ) {
			// ChartData e = datas.get(i);
			for ( int j = 0; j < categoryCount; j++ ) {
				final List list = new ArrayList();
				// add some values...
				for ( int k = 0; k < entityCount; k++ ) {
					// list.add(new Double(k*2));
					// list.add(new Double(k*3));
					final double value1 = 10.0 + Math.random() * 3;
					list.add(new Double(value1));
					final double value2 = 11.25 + Math.random(); // concentrate values in the middle
					list.add(new Double(value2));
				}
				dataset.add(list, "Series " + i, " Type " + j);

				history.append("Series " + i);
				history.append(',');
			}
		}
		history.deleteCharAt(history.length() - 1);
		history.append(nl);
		plot.setDataset(dataset);
		chart.removeLegend();
		final CategoryAxis axis = plot.getDomainAxis();
		// ((BarRenderer3D) plot.getRenderer()).setItemMargin(0.1);
		axis.setCategoryMargin(0.1);
		axis.setUpperMargin(0.05);
		axis.setLowerMargin(0.05);
		return dataset;
	}

	private void createData(final IScope scope) throws GamaRuntimeException {
		// Normally initialize the datas
		dataDeclaration.executeOn(scope);
		switch (type) {
			case SERIES_CHART: {
				createSeries(scope, true);
				break;
			}
			case PIE_CHART: {
				createSlices(scope);
				break;
			}
			case BOX_WHISKER_CHART: {
				createWhisker(scope);
				break;
			}
			case HISTOGRAM_CHART: {
				createBars(scope);
				break;
			}
			case XY_CHART:
				createSeries(scope, false);
				break;
		}
	}

	private void createSlices(final IScope scope) throws GamaRuntimeException {
		int i = 0;
		dataset = new DefaultPieDataset();
		final PiePlot plot = (PiePlot) chart.getPlot();
		for ( final ChartData e : datas ) {
			final String legend = e.getName();
			((DefaultPieDataset) dataset).insertValue(i++, legend, null);
			history.append(legend);
			history.append(',');
		}
		history.deleteCharAt(history.length() - 1);
		history.append(nl);
		plot.setDataset((DefaultPieDataset) dataset);
		i = 0;
		for ( final ChartData e : datas ) {
			plot.setSectionPaint(i++, e.getColor());
		}
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} = {1} ({2})"));
		if ( exploded ) {
			for ( final Object c : ((DefaultPieDataset) dataset).getKeys() ) {
				plot.setExplodePercent((Comparable) c, 0.20);
			}
		}
		plot.setSectionOutlinesVisible(false);
		plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 10));
		plot.setNoDataMessage("No data available yet");
		plot.setCircular(true);
		plot.setLabelGap(0.02);
		plot.setInteriorGap(0);
	}

	class CustomRenderer extends BarRenderer {

		public CustomRenderer() {}

		@Override
		public Paint getItemPaint(final int row, final int column) {
			return datas.get(column).getColor();
		}
	}

	private void createBars(final IScope scope) {
		final CategoryPlot plot = (CategoryPlot) chart.getPlot();
		BarRenderer renderer = new CustomRenderer();
		plot.setRenderer(renderer);
		dataset = new DefaultCategoryDataset();
//		dataset=new CategoryTableXYDataset();
/*
		for ( int i = 0; i < datas.size(); i++ ) {
			ChartData e = datas.get(i);

			final String legend = e.getName();
			if ( i != 0 ) { // the first data is the domain
				dataset = new DefaultTableXYDataset();
				final XYSeries serie = new XYSeries(legend, false, false);
				((DefaultTableXYDataset) dataset).addSeries(serie);
				expressions_index.put(legend, i);
				plot.setRenderer(i, (XYItemRenderer) e.getRenderer(), false);
				final Color c = e.getColor();
				plot.getRenderer(i).setSeriesPaint(0, c);
				plot.setDataset(i, (DefaultTableXYDataset) dataset);
			}
			history.append(legend);
			history.append(',');

		}
*/		
		int i=0;
		for ( final ChartData e : datas ) {
//			String legend = e.getName();
//			((DefaultCategoryDataset) dataset).setValue(0d, new Integer(0), legend/* , legend */);
			
			final String legend = e.getName();
//			if ( i != 0 ) { // the first data is the domain
//				dataset = new DefaultCategoryDataset();
				if (!CategoryItemRenderer.class.isInstance(e.getRenderer()))
					e.renderer=new BarRenderer();
				plot.setRenderer(i, (CategoryItemRenderer)e.getRenderer(), false);
				final Color c = e.getColor();
				plot.getRenderer(i).setSeriesPaint(0, c);
				plot.setDataset(i, (DefaultCategoryDataset) dataset);
//			}
			i++;
			history.append(legend);
			history.append(',');
		
		}
		history.deleteCharAt(history.length() - 1);
		history.append(nl);
		plot.setDataset((DefaultCategoryDataset) dataset);

		chart.removeLegend();
		final CategoryAxis axis = plot.getDomainAxis();
		Double gap = Cast.asFloat(scope, getFacetValue(scope, IKeyword.GAP, 0.01));
		// ((BarRenderer) plot.getRenderer()).setItemMargin(gap);
		renderer.setMaximumBarWidth(1 - gap);
		axis.setCategoryMargin(gap);
		axis.setUpperMargin(gap);
		axis.setLowerMargin(gap);

	}

	private void createChart(final IScope scope) {
		switch (type) {
			case SERIES_CHART: {
				chart =
					ChartFactory.createXYLineChart(getName(), "time", "", null, PlotOrientation.VERTICAL, true, false,
						false);
				break;
			}
			case PIE_CHART: {
				if ( style.equals(IKeyword.THREE_D) ) {
					chart = ChartFactory.createPieChart3D(getName(), null, false, true, false);
				} else if ( style.equals(IKeyword.RING) ) {
					chart = ChartFactory.createRingChart(getName(), null, false, true, false);
				} else if ( style.equals(IKeyword.EXPLODED) ) {
					chart = ChartFactory.createPieChart(getName(), null, false, true, false);
					exploded = true;
				} else {
					chart = ChartFactory.createPieChart(getName(), null, false, true, false);
				}
				break;
			}
			case HISTOGRAM_CHART: {
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
				break;
			}
			case XY_CHART:
				chart =
					ChartFactory.createXYLineChart(getName(), "", "", null, PlotOrientation.VERTICAL, true, false,
						false);
				break;
			case BOX_WHISKER_CHART: {
				chart =
					ChartFactory.createBoxAndWhiskerChart(getName(), "Time", "Value",
						(BoxAndWhiskerCategoryDataset) dataset, true);
				chart.setBackgroundPaint(new Color(249, 231, 236));

				break;
			}
		}
		Plot plot = chart.getPlot();
		chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 12));
		if ( backgroundColor == null ) {
			plot.setBackgroundPaint(null);
			chart.setBackgroundPaint(null);
			chart.setBorderPaint(null);
			if ( chart.getLegend() != null ) {
				chart.getLegend().setBackgroundPaint(null);
			}
		} else {
			Color bg = backgroundColor;
			chart.setBackgroundPaint(bg);
			plot.setBackgroundPaint(bg);
			chart.setBorderPaint(bg);
			if ( chart.getLegend() != null ) {
				chart.getLegend().setBackgroundPaint(bg);
			}
		}
		// chart.getLegend().setItemPaint(axesColor);
		// chart.getLegend().setBackgroundPaint(null);

		if ( plot instanceof CategoryPlot ) {
			final CategoryPlot pp = (CategoryPlot) chart.getPlot();
			pp.setDomainGridlinePaint(axesColor);
			pp.setRangeGridlinePaint(axesColor);
			// plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
			// plot.setDomainCrosshairVisible(true);
			pp.setRangeCrosshairVisible(true);
		} else if ( plot instanceof XYPlot ) {
			final XYPlot pp = (XYPlot) chart.getPlot();
			pp.setDomainGridlinePaint(axesColor);
			pp.setRangeGridlinePaint(axesColor);
			pp.setDomainCrosshairPaint(axesColor);
			pp.setRangeCrosshairPaint(axesColor);
			pp.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
			pp.setDomainCrosshairVisible(true);
			pp.setRangeCrosshairVisible(true);
		}
	}

	@Override
	public boolean _init(final IScope scope) throws GamaRuntimeException {
		history = new StringBuilder(500);
		IExpression string1 = getFacet(IKeyword.TYPE);
		if ( string1 != null ) {
			String t = Cast.asString(scope, string1.value(scope));
			type =
				IKeyword.SERIES.equals(t) ? SERIES_CHART : IKeyword.HISTOGRAM.equals(t) ? HISTOGRAM_CHART
					: IKeyword.PIE.equals(t) ? PIE_CHART : IKeyword.BOX_WHISKER.equals(t) ? BOX_WHISKER_CHART
						: XY_CHART;

		}
		IExpression color = getFacet(IKeyword.AXES);
		if ( color != null ) {
			axesColor = Cast.asColor(scope, color.value(scope));
		}
		IExpression color1 = getFacet(IKeyword.BACKGROUND);
		if ( color1 != null ) {
			backgroundColor = Cast.asColor(scope, color1.value(scope));
		}
		IExpression string = getFacet(IKeyword.STYLE);
		if ( string != null ) {
			style = Cast.asString(scope, string.value(scope));
			// TODO Verifier style;
		}
		createChart(scope);
		createData(scope);
		chart.setNotify(false);
		return true;
	}

	public void updateseries(final IScope scope) throws GamaRuntimeException {
		Object val=listvalue.resolveAgainst(scope).value(scope);
		if (!(val instanceof GamaList))
		{
			GuiUtils.debug("chart list with no list...");
			return;
		}
		GamaList<GamaList> values = (GamaList)Cast.asList(scope,val);
		if (reverseseries)
		{
			IList tempvalues = Cast.asList(scope,val);
			values=new GamaList<GamaList>();
			if (tempvalues.get(0) instanceof GamaList)
			{
				IList nval=Cast.asList(scope, tempvalues.get(0));
				for (int j=0; j<nval.size(); j++)
				{
					GamaList nl=new GamaList();
					nl.add(nval.get(j));
					values.add(nl);					
				}
			}
			else
			{
				GuiUtils.debug("Reverse series but not list of list..."+tempvalues);
				return;
				
			}
			if (tempvalues.size()>1)
			for (int i=1; i<tempvalues.size(); i++)
			{
				if (tempvalues.get(i) instanceof GamaList)
				{
					IList nval=Cast.asList(scope, tempvalues.get(i));
					for (int j=0; j<nval.size(); j++)
					{
//						Cast.asList(scope, values.get(j)).add(nval.get(j));	

						values.get(j).add(nval.get(j));					


					}
				}
				else
				{
					GuiUtils.debug("Reverse series but not list of list..."+tempvalues);
					return;
					
				}
			}
			
//			GuiUtils.debug("New Values"+values);
		}
		GamaList defaultnames=new GamaList<String>();
		GamaList defaultcolors=new GamaList<GamaColor>();
		for (int i=0; i<values.size();i++)
		{
			defaultnames.add("data"+i);
			if (i<10)
			{
				if (i==0) defaultcolors.add((GamaColor)Cast.asColor(scope,GamaColor.CYAN));
				if (i==1) defaultcolors.add((GamaColor)Cast.asColor(scope,GamaColor.RED));
				if (i==2) defaultcolors.add((GamaColor)Cast.asColor(scope,GamaColor.YELLOW));
				if (i==3) defaultcolors.add((GamaColor)Cast.asColor(scope,GamaColor.GREEN));
				if (i==4) defaultcolors.add((GamaColor)Cast.asColor(scope,GamaColor.BLUE));
				if (i==5) defaultcolors.add((GamaColor)Cast.asColor(scope,GamaColor.PINK));
				if (i==6) defaultcolors.add((GamaColor)Cast.asColor(scope,GamaColor.MAGENTA));
				if (i==7) defaultcolors.add((GamaColor)Cast.asColor(scope,GamaColor.ORANGE));
				if (i==8) defaultcolors.add((GamaColor)Cast.asColor(scope,GamaColor.LIGHT_GRAY));
				if (i==9) defaultcolors.add((GamaColor)Cast.asColor(scope,GamaColor.DARK_GRAY));
			}
			if (i>=10)
			if (i<GamaColor.colors.size())
				defaultcolors.add(GamaColor.int_colors.values().toArray()[i]);
			else
				defaultcolors.add(GamaColor.getInt(Random.opRnd(scope, 10000)));				
			
		}		
		boolean dynamicseriesnames=false;
		GamaList seriesnames=new GamaList<String>();
		
		if (serievalue!=null)
		{
		Object valc=serievalue.resolveAgainst(scope).value(scope);
		
		if ((valc instanceof GamaList))
		{
			dynamicseriesnames=true;
			seriesnames=(GamaList)valc;
			for (int i=0; i<Math.min(values.size(),seriesnames.size());i++)
			{
				defaultnames.set(i,seriesnames.get(i)+"("+i+")");
			}/*
			if (datas.size()>1)
			for (int i=datas.size()-1;i>0; i--)
			{
				String sname=((ChartData)datas.get(i)).getName();
				if (i<10) sname=sname.substring(0, sname.length()-3);
				else sname=sname.substring(0, sname.length()-4);
				if (!seriesnames.contains(sname)) 
					{
					GuiUtils.debug("remove:"+sname);
					datas.remove(i);
					}
			}*/
		}
		}

		int nbseries=values.size();
		if ( type==SERIES_CHART ) nbseries++;
		
		ChartData first=datas.get(0);
		if ((datas.size()<nbseries)
			|(datas.size()>1))
			{
//				clearvalues(scope);
				datas.clear();
				if ( type==SERIES_CHART ) {
					final XYPlot plot = (XYPlot) chart.getPlot();
					final NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
					domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
					if ( timeSeriesXData == null ) {

						timeSeriesXData =
							(ChartDataStatement) DescriptionFactory.create(IKeyword.DATA, description, IKeyword.LEGEND,
								xAxisName, IKeyword.VALUE, SimulationAgent.CYCLE).compile();
						if ( getFacet(IKeyword.TIMEXSERIES) != null ) {
							timeSeriesXData.getDescription().getFacets().get(IKeyword.VALUE)
								.setExpression(getFacet(IKeyword.TIMEXSERIES));
						}
						// else {
						// timeSeriesXData.getDescription().getFacets().get(IKeyword.VALUE)
						// .setExpression(new ConstantExpression(null) {
						//
						// @Override
						// public Integer value(final IScope scope) {
						// return scope.getClock().getCycle();
						// }
						//
						// @Override
						// public boolean isConst() {
						// return false;
						// }
						// });
						// }

					}

					if ( !datas.contains(timeSeriesXData) ) {
						datas.add(0, timeSeriesXData.createData(scope));
					}
				}	
		for (int i=datas.size(); i<nbseries;i++)
		{
			AbstractRenderer r;
			try {
				r = first.renderer.getClass().newInstance();
				ChartData newdata;
				if ( type==SERIES_CHART )
					newdata=ChartDataListStatement.newChartData(scope,r,Cast.asString(scope, defaultnames.get(i-1)),Cast.asColor(scope,defaultcolors.get(i-1)),values.get(i-1));
				else
					newdata=ChartDataListStatement.newChartData(scope,r,Cast.asString(scope, defaultnames.get(i)),Cast.asColor(scope,defaultcolors.get(i)),values.get(i));
					
				datas.add(newdata);

				
				if ((type==SERIES_CHART)|(type==XY_CHART))
				{
				final XYPlot plot = (XYPlot) chart.getPlot();
				final String legend = newdata.getName();
				dataset = new DefaultTableXYDataset();
				final XYSeries serie = new XYSeries(legend, false, false);
				((DefaultTableXYDataset) dataset).addSeries(serie);
				expressions_index.put(legend, i);
				plot.setRenderer(i, (XYItemRenderer) newdata.getRenderer(), false);
				final Color c = newdata.getColor();
				plot.getRenderer(i).setSeriesPaint(0, c);
				plot.setDataset(i, (DefaultTableXYDataset) dataset);
			history.append(legend);
			history.append(',');
				}
			
			
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



			
		}
			}
		if (chart.getLegend()==null)	chart.addLegend(new LegendTitle(chart.getPlot()));
		LegendTitle legend = chart.getLegend();
		GuiUtils.debug("dyncateg:"+defaultnames);
		GuiUtils.debug("legend:"+legend);		
		for (int i=0; i<values.size(); i++)
		{
			first=datas.get(i);
			if (type==SERIES_CHART) first=datas.get(i+1);
			first.lastvalue=values.get(i);
						
		}
		
		
	}
	
	public void clearvalues(final IScope scope) {
		boolean dynamiccategorynames=false;
		GamaList categorynames=new GamaList<String>();
		
		if (categoryvalue!=null)
		{
		Object valc=categoryvalue.resolveAgainst(scope).value(scope);
		if ((valc instanceof GamaList))
		{
			dynamiccategorynames=true;
			categorynames=(GamaList)valc;
		}

		
		}
//		if (dynamiccategorynames)
		{
			((DefaultCategoryDataset) dataset).clear();
		}
		for ( final ChartData d : datas ) {
			GamaList x = new GamaList();
			Object obj = d.getValue(scope);
			if ( obj instanceof GamaList ) {
				x = (GamaList) obj;
//				clearvalues=true;
				if (dynamiccategorynames)
				{
				for (int j=0;j<x.length(scope);j++)
					if (j<categorynames.size())
				{
					((DefaultCategoryDataset) dataset).setValue(Cast.asFloat(scope, x.get(j)).doubleValue(),d.getName(),categorynames.get(j).toString()+"("+j+")"/* , s */);						
				}
					else
						((DefaultCategoryDataset) dataset).setValue(Cast.asFloat(scope, x.get(j)).doubleValue(),d.getName(),"("+j+")"/* , s */);
				}
				else
				{
					for (int j=0;j<x.length(scope);j++)
					((DefaultCategoryDataset) dataset).setValue(Cast.asFloat(scope, x.get(j)).doubleValue(),d.getName(), new Integer(j)/* , s */);
					
				}
			} else {
				((DefaultCategoryDataset) dataset).setValue(Cast.asFloat(scope, obj).doubleValue(), new Integer(0),d.getName()/* , s */);
			}
			
			switch (type) {
				case PIE_CHART: {
//					((DefaultPieDataset) dataset).setValue(s, n);
					break;
				}
				case HISTOGRAM_CHART: {
					// GuiUtils.debug("ChartLayerStatement._step row " + ((DefaultCategoryDataset)
					// dataset).getRowCount() +
					// " col " + ((DefaultCategoryDataset) dataset).getColumnCount());
//					((DefaultCategoryDataset) dataset).setValue(n, new Integer(0), s/* , s */);
					break;
				}
			}
			history.append(0);
			history.append(',');
		}
		
		
	}
	
	
	@Override
	public boolean _step(final IScope scope) throws GamaRuntimeException {
		lastComputeCycle = (long) scope.getClock().getCycle();
		if (updateseries) updateseries(scope);


		
			
		switch (type) {
		case XY_CHART:
			computeSeries(scope, lastComputeCycle);
		case SERIES_CHART:
			computeSeries(scope, lastComputeCycle);
			return true;
	}
		
		boolean clearvalues=false;

		for ( final ChartData d : datas ) {
			GamaList x = new GamaList();
			Object obj = d.getValue(scope);
			if ( obj instanceof GamaList ) {
				x = (GamaList) obj;
				clearvalues=true;
				if ((type!=XY_CHART))
				for (int j=0;j<x.length(scope);j++)
				lastValues.put(d.getName(), Double.parseDouble("" + x.get(j)));
			} else {
				x.add(obj);
				if ((type!=XY_CHART))
				lastValues.put(d.getName(), Double.parseDouble("" + x.get(x.size() - 1)));
			}
		}
		if (clearvalues)
		{
			clearvalues(scope);
		}
		else
		for ( final Map.Entry<String, Double> d : lastValues.entrySet() ) {
			String s = d.getKey();
			final double n = d.getValue();
			switch (type) {
				case PIE_CHART: {
					((DefaultPieDataset) dataset).setValue(s, n);
					break;
				}
				case HISTOGRAM_CHART: {
					// GuiUtils.debug("ChartLayerStatement._step row " + ((DefaultCategoryDataset)
					// dataset).getRowCount() +
					// " col " + ((DefaultCategoryDataset) dataset).getColumnCount());
					((DefaultCategoryDataset) dataset).setValue(n, new Integer(0), s/* , s */);
					break;
				}
			}
			history.append(n);
			history.append(',');
		}
		

	
		
		
		history.deleteCharAt(history.length() - 1);
		history.append(nl);
		return true;
	}

	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 * @param cycle
	 */
	private void computeSeries(final IScope scope, final long cycle) throws GamaRuntimeException {
		if ( datas.isEmpty() ) { return; }
		GamaList x = new GamaList();
		Object obj = datas.get(0).getValue(scope);
		if ( obj instanceof GamaList ) {
			x = (GamaList) obj;
		} else {
			x.add(obj);
		}
		for ( int i = 0; i < x.size(); i++ ) {
			history.append(x.get(i));
			history.append(',');
		}
		if (!((type==SERIES_CHART)&(datas.size()<2)))
		for ( int i = 0; i < datas.size(); i++ ) {
			if ((type==SERIES_CHART)&(i==0)) i++;
			XYPlot plot = (XYPlot) chart.getPlot();
			DefaultTableXYDataset data = (DefaultTableXYDataset) plot.getDataset(i);
			XYSeries serie = data.getSeries(0);
			GamaList n = new GamaList();
			Object o = datas.get(i).getValue(scope);
			if ( o instanceof GamaList ) {
				n = (GamaList) o;
			} else {
				n.add(o);
			}
			if ((type==XY_CHART))
				serie.clear();
			java.lang.System.out.println("gr"+n);
			for ( int j = 0; j < n.size(); j++ ) {
				if ((type==SERIES_CHART))
				serie.addOrUpdate(Double.parseDouble("" + j), Double.parseDouble("" + n.get(j)));
				if ((type==XY_CHART))
					serie.addOrUpdate(Double.parseDouble("" + ((GamaList)n.get(j)).get(0)), Double.parseDouble("" + ((GamaList)n.get(j)).get(1)));
				history.append(n.get(j));
				history.append(',');
			}
		}
		history.deleteCharAt(history.length() - 1);
		history.append(nl);

	}

	@Override
	public short getType() {
		return ILayerStatement.CHART;
	}

	@Override
	public void dispose() {
		chart = null;
		super.dispose();
	}

	public void saveHistory() {
		IScope scope = GAMA.obtainNewScope();
		if ( scope == null ) { return; }
		try {
			Files.newFolder(scope, chartFolder);
			String file = chartFolder + "/" + "chart_" + getName() + ".csv";
			BufferedWriter bw;
			file = scope.getSimulationScope().getModel().getRelativeFilePath(file, false);
			bw = new BufferedWriter(new FileWriter(file));
			bw.append(history);
			bw.close();
		} catch (final Exception e) {
			e.printStackTrace();
			return;
		} finally {
			GAMA.releaseScope(scope);
		}
	}

}
