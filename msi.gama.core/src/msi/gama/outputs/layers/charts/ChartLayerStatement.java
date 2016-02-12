/*********************************************************************************************
 * 
 * 
 * 'ChartLayerStatement.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.outputs.layers.charts;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.FileUtils;
import msi.gama.kernel.experiment.BatchAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.layers.AbstractLayerStatement;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
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
import org.jfree.chart.renderer.category.*;
import org.jfree.chart.renderer.xy.*;
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
	@facet(name = ChartLayerStatement.XRANGE,
		type = { IType.FLOAT, IType.INT, IType.POINT },
		optional = true,
		doc = @doc("range of the x-axis. Can be a number (which will set the axis total range) or a point (which will set the min and max of the axis).")),
	@facet(name = ChartLayerStatement.YRANGE,
		type = { IType.FLOAT, IType.INT, IType.POINT },
		optional = true,
		doc = @doc("range of the y-axis. Can be a number (which will set the axis total range) or a point (which will set the min and max of the axis).")),
	@facet(name = IKeyword.POSITION,
		type = IType.POINT,
		optional = true,
		doc = @doc("position of the upper-left corner of the layer. Note that if coordinates are in [0,1[, the position is relative to the size of the environment (e.g. {0.5,0.5} refers to the middle of the display) whereas it is absolute when coordinates are greter than 1. The position can only be a 3D point {0.5, 0.5, 0.5}, the last coordinate specifying the elevation of the layer.")),
	@facet(name = IKeyword.SIZE,
		type = IType.POINT,
		optional = true,
		doc = @doc("the layer resize factor: {1,1} refers to the original size whereas {0.5,0.5} divides by 2 the height and the width of the layer. In case of a 3D layer, a 3D point can be used (note that {1,1} is equivalent to {1,1,0}, so a resize of a layer containing 3D objects with a 2D points will remove the elevation)")),
	@facet(name = IKeyword.BACKGROUND, type = IType.COLOR, optional = true, doc = @doc("the background color")),
	@facet(name = IKeyword.TIMEXSERIES,
		type = IType.LIST,
		optional = true,
		doc = @doc("for series charts, change the default time serie (simulation cycle) for an other value.")),
	@facet(name = IKeyword.AXES, type = IType.COLOR, optional = true, doc = @doc("the axis color")),
	@facet(name = IKeyword.TYPE,
		type = IType.ID,
		values = { IKeyword.XY, IKeyword.SCATTER, IKeyword.HISTOGRAM, IKeyword.SERIES, IKeyword.PIE,
			IKeyword.BOX_WHISKER },
		optional = true,
		doc = @doc("the type of chart. It could be histogram, series, xy, pie or box whisker. The difference between series and xy is that the former adds an implicit x-axis that refers to the numbers of cycles, while the latter considers the first declaration of data to be its x-axis.")),
	@facet(name = IKeyword.STYLE, type = IType.ID, values = { IKeyword.LINE, IKeyword.WHISKER, IKeyword.AREA,
			IKeyword.BAR, IKeyword.DOT, IKeyword.STEP, IKeyword.SPLINE, IKeyword.STACK, IKeyword.THREE_D, IKeyword.RING,
			IKeyword.EXPLODED }, optional = true),
	@facet(name = IKeyword.TRANSPARENCY, type = IType.FLOAT, optional = true, doc = @doc("the style of the chart")),
	@facet(name = IKeyword.GAP, type = IType.FLOAT, optional = true),
	@facet(name = ChartLayerStatement.YTICKUNIT,
		type = IType.FLOAT,
		optional = true,
		doc = @doc("the tick unit for the x-axis (distance between vertical lines and values bellow the axis).")),
	@facet(name = ChartLayerStatement.XTICKUNIT,
		type = IType.FLOAT,
		optional = true,
		doc = @doc("the tick unit for the y-axis (distance between horyzontal lines and values on the left of the axis).")),
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false, doc = @doc("the identifier of the chart layer")),
	@facet(name = IKeyword.COLOR, type = IType.COLOR, optional = true),
	@facet(name = ChartLayerStatement.TICKFONTFACE, type = IType.STRING, optional = true),
	@facet(name = ChartLayerStatement.TICKFONTSIZE, type = IType.INT, optional = true),
	@facet(name = ChartLayerStatement.TICKFONTSTYLE,
		type = IType.ID,
		values = { "plain", "bold", "italic" },
		optional = true,
		doc = @doc("the style used to display ticks")),
	@facet(name = ChartLayerStatement.LABELFONTFACE, type = IType.STRING, optional = true),
	@facet(name = ChartLayerStatement.LABELFONTSIZE, type = IType.INT, optional = true),
	@facet(name = ChartLayerStatement.LABELFONTSTYLE,
		type = IType.ID,
		values = { "plain", "bold", "italic" },
		optional = true,
		doc = @doc("the style used to display labels")),
	@facet(name = ChartLayerStatement.LEGENDFONTFACE, type = IType.STRING, optional = true),
	@facet(name = ChartLayerStatement.LEGENDFONTSIZE, type = IType.INT, optional = true),
	@facet(name = ChartLayerStatement.LEGENDFONTSTYLE,
		type = IType.ID,
		values = { "plain", "bold", "italic" },
		optional = true,
		doc = @doc("the style used to display legend")),
	@facet(name = ChartLayerStatement.TITLEFONTFACE, type = IType.STRING, optional = true),
	@facet(name = ChartLayerStatement.TITLEFONTSIZE, type = IType.INT, optional = true),
	@facet(name = ChartLayerStatement.TITLEFONTSTYLE,
		type = IType.ID,
		values = { "plain", "bold", "italic" },
		optional = true,
		doc = @doc("the style used to display titles")), },

	omissible = IKeyword.NAME)
@doc(value = "`" +
	IKeyword.CHART +
	"` allows modeler to display a chart: this enables to display specific values of the model at each iteration. GAMA can display various chart types: time series (series), pie charts (pie) and histograms (histogram).",
	usages = { @usage(value = "The general syntax is:", examples = {
		@example(value = "display chart_display {", isExecutable = false),
		@example(value = "   chart \"chart name\" type: series [additional options] {", isExecutable = false),
		@example(value = "      [Set of data, datalists statements]", isExecutable = false),
		@example(value = "   }", isExecutable = false), @example(value = "}", isExecutable = false) }) },
	see = { IKeyword.DISPLAY, IKeyword.AGENTS, IKeyword.EVENT, "graphics", IKeyword.GRID_POPULATION, IKeyword.IMAGE,
		IKeyword.OVERLAY, IKeyword.QUADTREE, IKeyword.POPULATION, IKeyword.TEXT })
public class ChartLayerStatement extends AbstractLayerStatement {

	public static final String XRANGE = "x_range";
	public static final String YRANGE = "y_range";

	public static final String YTICKUNIT = "y_tick_unit";
	public static final String XTICKUNIT = "x_tick_unit";

	public static final String TICKFONTFACE = "tick_font";
	public static final String TICKFONTSIZE = "tick_font_size";
	public static final String TICKFONTSTYLE = "tick_font_style";

	public static final String LABELFONTFACE = "label_font";
	public static final String LABELFONTSIZE = "label_font_size";
	public static final String LABELFONTSTYLE = "label_font_style";

	public static final String LEGENDFONTFACE = "legend_font";
	public static final String LEGENDFONTSIZE = "legend_font_size";
	public static final String LEGENDFONTSTYLE = "legend_font_style";

	public static final String TITLEFONTFACE = "title_font";
	public static final String TITLEFONTSIZE = "title_font_size";
	public static final String TITLEFONTSTYLE = "title_font_style";

	public static final String CHARTDATASET = "chart_dataset_transfer";
	
	
	private ChartDataSet chartdataset;
	
	public class DataDeclarationSequence extends AbstractStatementSequence {

		public DataDeclarationSequence(final IDescription desc) {
			super(desc);
		}

		//shouldn't have to do that but I don't know how to get the "chart statement" inside the "data statement" declaration otherwise...
		
		// We create the variable in which the datas will be accumulated
		@Override
		public void enterScope(final IScope scope) {
			super.enterScope(scope);
			
			scope.addVarWithValue(ChartLayerStatement.CHARTDATASET, chartdataset);
			
		}

		// We save the datas once the computation is finished
		@Override
		public void leaveScope(final IScope scope) {
			chartdataset=(ChartDataSet) scope.getVarValue(ChartLayerStatement.CHARTDATASET);
//			System.out.println("DELETE ME, I am in chartlayerstatement top");
			
			super.leaveScope(scope);
		}

	}

	 static final int SERIES_CHART = 0;
	 static final int HISTOGRAM_CHART = 1;
	 static final int PIE_CHART = 2;
	 static final int XY_CHART = 3;
	 static final int BOX_WHISKER_CHART = 4;
	static final int SCATTER_CHART = 5;

	private ChartOutput chartoutput=null;
	
//	private HashMap<String,Object> chartParameters=new HashMap<String,Object>();
	
	static String xAxisName = "'time'";
	final Map<String, Double> lastValues;
	Long lastComputeCycle;
	ChartDataStatement timeSeriesXData = null;
	DataDeclarationSequence dataDeclaration = new DataDeclarationSequence(null);


	public ChartOutput getOutput() {
		return chartoutput;
	}

	public ChartLayerStatement(/* final ISymbol context, */final IDescription desc) throws GamaRuntimeException {
		super(desc);
		lastValues = new LinkedHashMap();
		lastComputeCycle = 0l;
	}



	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		dataDeclaration.setChildren(commands);
	}
	
	public JFreeChart getChart()
	{
		//should be changed, used in LayerSideControls to open an editor...
		return getDataSet().getOutput().getJFChart();
	}

	public ChartDataSet getDataSet()
	{
		return chartdataset;
	}
	


	@Override
	public boolean _init(final IScope scope) throws GamaRuntimeException {
		lastValues.clear();;

//		chartParameters.clear();
		
		IExpression string1 = getFacet(IKeyword.TYPE);
//		chartParameters.put(IKeyword.TYPE, string1);

		chartoutput = ChartJFreeChartOutput.createChartOutput(scope,getName(),string1);
		
		GamaColor colorvalue=new GamaColor(Color.black);
		IExpression color = getFacet(IKeyword.AXES);
//		chartParameters.put(IKeyword.AXES, color);
		if ( color != null ) {
			colorvalue = Cast.asColor(scope, color.value(scope));
		}
		chartoutput.setAxesColorValue(scope,colorvalue);

//TOCHANGE		
		/*
		IExpression color1 = getFacet(IKeyword.BACKGROUND);
		chartParameters.put(IKeyword.BACKGROUND, color1);
		IExpression string = getFacet(IKeyword.STYLE);
		chartParameters.put(IKeyword.STYLE, string);
		IExpression face = getFacet(ChartLayerStatement.TICKFONTFACE);
		chartParameters.put(ChartLayerStatement.TICKFONTFACE, face);
		face = getFacet(ChartLayerStatement.LABELFONTFACE);
		chartParameters.put(ChartLayerStatement.LABELFONTFACE, face);
		face = getFacet(ChartLayerStatement.LEGENDFONTFACE);
		chartParameters.put(ChartLayerStatement.LEGENDFONTFACE, face);
		face = getFacet(ChartLayerStatement.TITLEFONTFACE);
		chartParameters.put(ChartLayerStatement.TITLEFONTFACE, face);
		face = getFacet(ChartLayerStatement.TICKFONTSIZE);
		chartParameters.put(ChartLayerStatement.TICKFONTSIZE, face);
		face = getFacet(ChartLayerStatement.LABELFONTSIZE);
		chartParameters.put(ChartLayerStatement.LABELFONTSIZE, face);
		face = getFacet(ChartLayerStatement.LEGENDFONTSIZE);
		chartParameters.put(ChartLayerStatement.LEGENDFONTSIZE, face);
		face = getFacet(ChartLayerStatement.TITLEFONTSIZE);
		chartParameters.put(ChartLayerStatement.TITLEFONTSIZE, face);
		face = getFacet(ChartLayerStatement.TICKFONTSTYLE);
		chartParameters.put(ChartLayerStatement.TICKFONTSTYLE, face);
		face = getFacet(ChartLayerStatement.LABELFONTSTYLE);
		chartParameters.put(ChartLayerStatement.LABELFONTSTYLE, face);
		face = getFacet(ChartLayerStatement.LEGENDFONTSTYLE);
		chartParameters.put(ChartLayerStatement.LEGENDFONTSTYLE, face);
		face = getFacet(ChartLayerStatement.TITLEFONTSTYLE);
		chartParameters.put(ChartLayerStatement.TITLEFONTSTYLE, face);
*/
		
		
		chartoutput.initChart(scope,getName());
		
		chartdataset=new ChartDataSet();
	    chartoutput.setChartdataset(chartdataset);
	    chartoutput.initdataset();
		
		dataDeclaration.executeOn(scope);
				
		chartoutput.updateOutput(scope);

		return true;
	}




	@Override
	public boolean _step(final IScope scope) throws GamaRuntimeException {
		chartoutput.step(scope);
		
		return true;
	}


	@Override
	public short getType() {
		return ILayerStatement.CHART;
	}

	@Override
	public void dispose() {
//		chart = null;
		super.dispose();
	}

	public void saveHistory() {
		//TODO!!
		
//		IScope scope = output.getScope().copy();
//		if ( scope == null ) { return; }
//		try {
//			Files.newFolder(scope, chartFolder);
//			String file = chartFolder + "/" + "chart_" + getName() + ".csv";
//			BufferedWriter bw;
//			file = FileUtils.constructAbsoluteFilePath(scope, file, false);
//			bw = new BufferedWriter(new FileWriter(file));
//			bw.append(history);
//			bw.close();
//		} catch (final Exception e) {
//			e.printStackTrace();
//			return;
//		} finally {
//			GAMA.releaseScope(scope);
//		}
	}

}
