/*********************************************************************************************
 * 
 *
 * 'ChartDataListStatement.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.outputs.layers.charts;

import java.util.ArrayList;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.IValue;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Random;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;
import msi.gaml.variables.IVariable;

import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.xy.*;

@symbol(name = "datalist", kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false, concept = { IConcept.CHART }, doc = @doc("add a list of series to a chart. The number of series can be dynamic (the size of the list changes each step). See Ant Foraging (Charts) model in ChartTest for examples."))
@inside(symbols = IKeyword.CHART, kinds = ISymbolKind.SEQUENCE_STATEMENT)
@facets(value = {
	@facet(name = IKeyword.VALUE, type = IType.LIST, optional = false, doc = @doc("the values to display. Has to be a List of List. Each element can be a number (series/histogram) or a list with two values (XY chart)")),
	@facet(name = ChartDataStatement.YERR_VALUES, type = IType.LIST, optional = true, doc = @doc("the Y Error bar values to display. Has to be a List. Each element can be a number or a list with two values (low and high value)")),
	@facet(name = ChartDataStatement.XERR_VALUES, type = IType.LIST, optional = true, doc = @doc("the X Error bar values to display. Has to be a List. Each element can be a number or a list with two values (low and high value)")),
	@facet(name = ChartDataStatement.YMINMAX_VALUES, type = IType.LIST, optional = true, doc = @doc("the Y MinMax bar values to display (BW charts). Has to be a List. Each element can be a number or a list with two values (low and high value)")),
	@facet(name = ChartDataStatement.MARKERSIZE, type = IType.LIST, optional = true, doc = @doc("the Y Error bar values to display. Has to be a List. Each element can be a number or a list with one value for each serie element")),
//	@facet(name = IKeyword.NAME, type =  IType.LIST, optional = true, doc = @doc("the name of the series: a list of strings (can be a variable with dynamic names)")),
	@facet(name = IKeyword.LEGEND, type =  IType.LIST, optional = true, doc = @doc("the name of the series: a list of strings (can be a variable with dynamic names)")),
	@facet(name = ChartDataListStatement.CATEGNAMES, type =  IType.LIST, optional = true, doc = @doc("the name of categories (can be a variable with dynamic names)")),
	@facet(name = ChartDataListStatement.REVERSECATEG, type =  IType.BOOL, optional = true, doc = @doc("reverse the order of series/categories ([[1,2],[3,4],[5,6]] --> [[1,3,5],[2,4,6]]. May be useful when it is easier to construct one list over the other.")),
	@facet(name = ChartDataStatement.MARKER, type = IType.BOOL, optional = true),
	@facet(name = ChartDataStatement.MARKERSHAPE, type = IType.ID, values = { ChartDataStatement.MARKER_EMPTY,
			ChartDataStatement.MARKER_SQUARE, ChartDataStatement.MARKER_CIRCLE, ChartDataStatement.MARKER_UP_TRIANGLE,
			ChartDataStatement.MARKER_DIAMOND, ChartDataStatement.MARKER_HOR_RECTANGLE,
			ChartDataStatement.MARKER_DOWN_TRIANGLE, ChartDataStatement.MARKER_HOR_ELLIPSE,
			ChartDataStatement.MARKER_RIGHT_TRIANGLE, ChartDataStatement.MARKER_VERT_RECTANGLE,
			ChartDataStatement.MARKER_LEFT_TRIANGLE }, optional = true),
	@facet(name = ChartDataStatement.CUMUL_VALUES, type = IType.BOOL, optional = true),
	@facet(name = ChartDataStatement.LINE_VISIBLE, type = IType.BOOL, optional = true),
	@facet(name = ChartDataStatement.FILL, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.COLOR, type =  IType.LIST, optional = true, doc = @doc("list of colors")),
	@facet(name = IKeyword.STYLE, type = IType.ID, values = { IKeyword.LINE, IKeyword.WHISKER, IKeyword.AREA,
		IKeyword.BAR, IKeyword.DOT, IKeyword.STEP, IKeyword.SPLINE, IKeyword.STACK, IKeyword.THREE_D, IKeyword.RING,
		IKeyword.EXPLODED }, optional = true, doc = @doc("series style")) }, omissible = IKeyword.LEGEND)
public class ChartDataListStatement extends AbstractStatement {	

	public static final String DATALISTS = "datalist";
	public static final String CATEGNAMES = "categoriesnames";
	public static final String REVERSECATEG= "inverse_series_categories";
//	protected int dataNumber = 0;
	
	public static class ChartDataList {

		IExpression colorlistexp;
		IExpression valuelistexp;
		IExpression legendlistexp;
		IExpression categlistexp;
		boolean doreverse;
		AbstractRenderer renderer;
		Object lastvalue;
		String name;
		int previoussize=0;


	}


	public ChartDataListStatement(final IDescription desc) {
		super(desc);
	}

	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 */
	
/*	
	public  ChartDataList createData(final IScope scope) throws GamaRuntimeException {
		ChartDataList datalist=new ChartDataList();
		

		IExpression valexp=getFacet(IKeyword.VALUE);
		datalist.valuelistexp=valexp;
		Boolean reverse= Cast.asBool(scope, getFacetValue(scope, "inverse_series_categories",false));
		datalist.doreverse=reverse;
		
		IExpression categexp=getFacet(ChartDataListStatement.CATEGNAMES);
		datalist.categlistexp=categexp;

		IExpression colorexp=getFacet(IKeyword.COLOR);
		datalist.colorlistexp=colorexp;

		IExpression serexp=getFacet(IKeyword.LEGEND);
		datalist.legendlistexp=serexp;
		
		
		if (categexp!=null)
		{
//			scope.addVarWithValue(ChartDataListStatement.CATEGNAMES, categexp);			
		}
		if (serexp!=null)
		{
//			scope.addVarWithValue(ChartDataListStatement.SERIESNAMES, serexp);
		}


		boolean showMarkers = getFacetValue(scope, ChartDataStatement.MARKER, true);
		boolean showLine = getFacetValue(scope, ChartDataStatement.LINE_VISIBLE, true);
		boolean fillMarkers = getFacetValue(scope, ChartDataStatement.FILL, true);
		String style = getLiteral(IKeyword.STYLE);
		if ( style == null ) {
			style = IKeyword.LINE;
		}
			AbstractRenderer r = null;
			if ( style.equals(IKeyword.LINE) ) {
				r = new XYLineAndShapeRenderer(true, showMarkers);
				((XYLineAndShapeRenderer) r).setBaseShapesFilled(fillMarkers);
				((XYLineAndShapeRenderer) r).setSeriesLinesVisible(0, showLine);
			} else if ( style.equals(IKeyword.AREA) ) {
				r = new XYAreaRenderer();
			} else if ( style.equals(IKeyword.WHISKER) ) {
				r = new BoxAndWhiskerRenderer();
			} else if ( style.equals(IKeyword.BAR) ) {
				r = new BarRenderer();
			} else if ( style.equals(IKeyword.DOT) ) {
				r = new XYDotRenderer();
			} else if ( style.equals(IKeyword.SPLINE) ) {
				r = new XYSplineRenderer();
			} else if ( style.equals(IKeyword.STEP) ) {
				r = new XYStepRenderer();
			} else if ( style.equals(IKeyword.AREA_STACK) ) {
				r = new StackedXYAreaRenderer2();
			} else if ( style.equals(IKeyword.STACK) ) {
				r = new StackedBarRenderer();
			}
		datalist.renderer=r;
			
		return datalist;
	}
*/
	public ChartDataSourceList createDataSource(final IScope scope, ChartDataSet graphdataset) throws GamaRuntimeException {
		
		
		ChartDataSourceList data = new ChartDataSourceList();

		IExpression string1 = getFacet(IKeyword.TYPE);
		

		data.setDataset(scope, graphdataset);
		
		String stval = getLiteral(IKeyword.STYLE);
		if ( stval != null ) {
			data.setStyle(scope,stval);
		}

		
		
		IExpression expval = getFacet(IKeyword.LEGEND).resolveAgainst(scope);
		data.setNameExp(scope,expval);
		
		
		expval = getFacet(IKeyword.VALUE).resolveAgainst(scope);
		data.setValueExp(scope,expval);

		expval = getFacet(ChartDataStatement.YERR_VALUES);
		if (expval!=null)
		{
			expval=expval.resolveAgainst(scope);
			data.setYErrValueExp(scope, expval);
			
		}

		expval = getFacet(ChartDataStatement.XERR_VALUES);
		if (expval!=null)
		{
			expval=expval.resolveAgainst(scope);
			data.setXErrValueExp(scope, expval);
			
		}

		expval = getFacet(ChartDataStatement.YMINMAX_VALUES);
		if (expval!=null)
		{
			expval=expval.resolveAgainst(scope);
			data.setYMinMaxValueExp(scope, expval);
			
		}

		expval = getFacet(IKeyword.COLOR);
		if (expval!=null)
		{
			expval=expval.resolveAgainst(scope);
			data.setColorExp(scope, expval);
			
		}
		boolean boolval = getFacetValue(scope, ChartDataStatement.MARKER, true);
		data.setMarkerBool(scope,boolval);

		boolval = getFacetValue(scope, ChartDataStatement.LINE_VISIBLE, true);
		data.setShowLine(scope,boolval);
		boolval = getFacetValue(scope, ChartDataStatement.FILL, true);
		data.setFillMarker(scope,boolval);
		

		stval = getFacetValue(scope, ChartDataStatement.MARKERSHAPE, null);
		data.setMarkerShape(scope,stval);

		//should allow different marker shapes in a list (with Gama Shapes)
		/*
		expval = getFacetValue(scope, ChartDataStatement.MARKERSHAPE, null);
		if (expval!=null)
		{
			expval=expval.resolveAgainst(scope);
			data.setMarkerShapeExp(scope, expval);
			
		}
	*/
		
		Object forcecumul= getFacetValue(scope, ChartDataStatement.CUMUL_VALUES,null);
		if (forcecumul!=null)
		{
			data.setCumulative(scope,Cast.asBool(scope, forcecumul));
			data.setForceCumulative(scope,true);			
		}
		
		data.createInitialSeries(scope);

		expval = getFacet(ChartDataStatement.MARKERSIZE);
		if (expval!=null)
		{
			data.setUseSize(true);
			expval=expval.resolveAgainst(scope);
			data.setMarkerSize(scope, expval);
			
		}

		
		
		return data;
	}
	
	

	@Override
	protected Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		ChartDataSet graphdataset=(ChartDataSet) scope.getVarValue(ChartLayerStatement.CHARTDATASET);
		ChartDataSourceList data = createDataSource(scope,graphdataset);
		graphdataset.addDataSource(data);
		return data;
	}

}