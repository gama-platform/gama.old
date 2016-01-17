/*********************************************************************************************
 * 
 * 
 * 'ChartDataStatement.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.outputs.layers.charts;

import java.awt.Shape;
import java.util.ArrayList;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaPoint;
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
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;

import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.*;
import org.jfree.chart.renderer.xy.*;

@symbol(name = IKeyword.DATA, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@inside(symbols = IKeyword.CHART, kinds = ISymbolKind.SEQUENCE_STATEMENT)
@facets(value = {
	@facet(name = IKeyword.VALUE, type = { IType.FLOAT, IType.POINT, IType.LIST }, optional = false),
	@facet(name = IKeyword.NAME, type = IType.ID, optional = true),
	@facet(name = IKeyword.LEGEND, type = IType.STRING, optional = true),
	@facet(name = IKeyword.COLOR, type = IType.COLOR, optional = true),
	@facet(name = ChartDataStatement.LINE_VISIBLE, type = IType.BOOL, optional = true),
	@facet(name = ChartDataStatement.MARKER, type = IType.BOOL, optional = true),
	@facet(name = ChartDataStatement.MARKERSHAPE, type = IType.ID, values = { ChartDataStatement.MARKER_EMPTY,
		ChartDataStatement.MARKER_SQUARE, ChartDataStatement.MARKER_CIRCLE, ChartDataStatement.MARKER_UP_TRIANGLE,
		ChartDataStatement.MARKER_DIAMOND, ChartDataStatement.MARKER_HOR_RECTANGLE,
		ChartDataStatement.MARKER_DOWN_TRIANGLE, ChartDataStatement.MARKER_HOR_ELLIPSE,
		ChartDataStatement.MARKER_RIGHT_TRIANGLE, ChartDataStatement.MARKER_VERT_RECTANGLE,
		ChartDataStatement.MARKER_LEFT_TRIANGLE }, optional = true),
	@facet(name = ChartDataStatement.FILL, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.STYLE, type = IType.ID, values = { IKeyword.LINE, IKeyword.WHISKER, IKeyword.AREA,
		IKeyword.BAR, IKeyword.DOT, IKeyword.STEP, IKeyword.SPLINE, IKeyword.STACK, IKeyword.THREE_D, IKeyword.RING,
		IKeyword.EXPLODED }, optional = true) }, omissible = IKeyword.LEGEND)
public class ChartDataStatement extends AbstractStatement {

	public static final String MARKER = "marker";
	public static final String MARKERSHAPE = "marker_shape";
	public static final String FILL = "fill";
	public static final String LINE_VISIBLE = "line_visible";
	public static final String MARKER_EMPTY = "marker_empty";
	public static final String MARKER_SQUARE = "marker_sqaure";
	public static final String MARKER_CIRCLE = "marker_square";
	public static final String MARKER_UP_TRIANGLE = "marker_up_triangle";
	public static final String MARKER_DIAMOND = "marker_diamond";
	public static final String MARKER_HOR_RECTANGLE = "marker_hor_rectangle";
	public static final String MARKER_DOWN_TRIANGLE = "marker_down_triangle";
	public static final String MARKER_HOR_ELLIPSE = "marker_hor_ellipse";
	public static final String MARKER_RIGHT_TRIANGLE = "marker_right_triangle";
	public static final String MARKER_VERT_RECTANGLE = "marker_vert_rectangle";
	public static final String MARKER_LEFT_TRIANGLE = "marker_left_triangle";

	public static final Shape[] defaultmarkers = org.jfree.chart.plot.DefaultDrawingSupplier
		.createStandardSeriesShapes();


	public static final String DATAS = "chart_datas";
	protected int dataNumber = 0;

	public ChartDataStatement(final IDescription desc) {
		super(desc);
	}

	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 */
	public ChartDataSourceUnique createDataSource(final IScope scope, ChartDataSet graphdataset) throws GamaRuntimeException {
		
		
		ChartDataSourceUnique data = new ChartDataSourceUnique();

		IExpression string1 = getFacet(IKeyword.TYPE);
		
		String stval = getLiteral(IKeyword.STYLE);
		if ( stval == null ) {
			stval = IKeyword.LINE;
		}

		data.setStyle(scope,stval);

		data.setDataset(graphdataset);
		
		stval = getFacetValue(scope,  IKeyword.LEGEND, null);
		data.setLegend(scope,stval);
		
		IExpression expval = getFacet(IKeyword.VALUE).resolveAgainst(scope);
		data.setValueExp(scope,expval);
		
		boolean boolval = getFacetValue(scope, MARKER, true);
		data.setMarkerBool(scope,boolval);
		
		stval = getFacetValue(scope, MARKERSHAPE, null);
		data.setMarkerShape(scope,stval);

		
//TODO		
/*		
		stval = getFacetValue(scope, IKeyword.COLOR, "black");
		data.sourceParameters.put(IKeyword.COLOR,stval);
		
		 boolval = getFacetValue(scope, MARKER, true);
		data.sourceParameters.put(MARKER,boolval);
		
		 boolval = getFacetValue(scope, LINE_VISIBLE, true);
		data.sourceParameters.put(LINE_VISIBLE,boolval);
		
		 boolval = getFacetValue(scope, FILL, true);
		data.sourceParameters.put(FILL,boolval);

		stval = getFacetValue(scope, MARKERSHAPE, null);
		data.sourceParameters.put(MARKERSHAPE,stval);

		stval = getFacetValue(scope, MARKERSHAPE, null);
		data.sourceParameters.put(MARKERSHAPE,stval);
	*/	
		data.createInitialSeries(scope);
		
		return data;
	}

	/**
	 * Data statements rely on the fact that a variable called "chart_datas" is available in the
	 * scope. If not, it will not do anything.
	 * This variable is normally created by the ChartLayerStatement.
	 * @see msi.gaml.statements.AbstractStatement#privateExecuteIn(msi.gama.runtime.IScope)
	 */

	@Override
	protected Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		ChartDataSet graphdataset=(ChartDataSet) scope.getVarValue(ChartLayerStatement.CHARTDATASET);
		ChartDataSourceUnique data = createDataSource(scope,graphdataset);
		graphdataset.addDataSource(data);
		return data;
	}

}