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
package msi.gama.outputs.layers;

import java.awt.Shape;
import java.util.ArrayList;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.*;
import org.jfree.chart.renderer.xy.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;

@symbol(name = IKeyword.DATA, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@inside(symbols = IKeyword.CHART, kinds = ISymbolKind.SEQUENCE_STATEMENT)
@facets(
	value = { @facet(name = IKeyword.VALUE, type = { IType.FLOAT, IType.POINT, IType.LIST }, optional = false),
		@facet(name = IKeyword.NAME, type = IType.ID, optional = true),
		@facet(name = IKeyword.LEGEND, type = IType.STRING, optional = true),
		@facet(name = IKeyword.COLOR, type = IType.COLOR, optional = true), @facet(
			name = ChartDataStatement.LINE_VISIBLE, type = IType.BOOL, optional = true),
	@facet(name = ChartDataStatement.MARKER, type = IType.BOOL, optional = true),
	@facet(name = ChartDataStatement.MARKERSHAPE,
		type = IType.ID,
		values = { ChartDataStatement.MARKER_EMPTY, ChartDataStatement.MARKER_SQUARE, ChartDataStatement.MARKER_CIRCLE,
			ChartDataStatement.MARKER_UP_TRIANGLE, ChartDataStatement.MARKER_DIAMOND,
			ChartDataStatement.MARKER_HOR_RECTANGLE, ChartDataStatement.MARKER_DOWN_TRIANGLE,
			ChartDataStatement.MARKER_HOR_ELLIPSE, ChartDataStatement.MARKER_RIGHT_TRIANGLE,
			ChartDataStatement.MARKER_VERT_RECTANGLE, ChartDataStatement.MARKER_LEFT_TRIANGLE },
		optional = true),
		@facet(name = ChartDataStatement.FILL, type = IType.BOOL, optional = true),
		@facet(name = IKeyword.STYLE,
			type = IType.ID,
			values = { IKeyword.LINE, IKeyword.WHISKER, IKeyword.AREA, IKeyword.BAR, IKeyword.DOT, IKeyword.STEP,
				IKeyword.SPLINE, IKeyword.STACK, IKeyword.THREE_D, IKeyword.RING, IKeyword.EXPLODED },
			optional = true) },
	omissible = IKeyword.LEGEND)
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

	public static final Shape[] defaultmarkers =
		org.jfree.chart.plot.DefaultDrawingSupplier.createStandardSeriesShapes();

	public static class ChartData {

		String name;
		GamaColor color;
		IExpression value;
		AbstractRenderer renderer;
		Object lastvalue;

		public AbstractRenderer getRenderer() {
			return renderer;
		}

		public void setRenderer(final AbstractRenderer renderer) {
			this.renderer = renderer;
		}

		public String getName() {
			return name;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public GamaColor getColor() {
			return color;
		}

		public void setColor(final GamaColor color) {
			this.color = color;
		}

		public IExpression getValue() {
			return value;
		}

		public Object getValue(final IScope scope) throws GamaRuntimeException {
			Object o;
			if ( value != null && !scope.interrupted() ) {
				o = value.value(scope);
			} else {
				o = lastvalue;
			}
			if ( o instanceof GamaList ) { return Cast.asList(scope, o); }
			if ( o instanceof GamaPoint ) { return Cast.asPoint(scope, o); }
			return Cast.asFloat(scope, o);
		}

		public void setValue(final IExpression value) {
			this.value = value;
		}

	}

	public static final String DATAS = "chart_datas";
	protected int dataNumber = 0;

	public ChartDataStatement(final IDescription desc) {
		super(desc);
	}

	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 */
	public ChartData createData(final IScope scope) throws GamaRuntimeException {
		ChartData data = new ChartData();
		String style = getLiteral(IKeyword.STYLE);
		if ( style == null ) {
			style = IKeyword.LINE;
		}

		GamaColor color = Cast.asColor(scope, getFacetValue(scope, IKeyword.COLOR, Cast.asColor(scope, "black")));
		boolean showMarkers = getFacetValue(scope, MARKER, true);
		boolean showLine = getFacetValue(scope, LINE_VISIBLE, true);
		boolean fillMarkers = getFacetValue(scope, FILL, true);
		String shapeMarker = getFacetValue(scope, MARKERSHAPE, null);

		AbstractRenderer r = null;
		if ( style.equals(IKeyword.LINE) ) {
			r = new XYLineAndShapeRenderer(true, showMarkers);
			r.setSeriesPaint(0, color);
			((XYLineAndShapeRenderer) r).setBaseShapesFilled(fillMarkers);
			if ( shapeMarker != null ) {
				if ( shapeMarker.equals(MARKER_SQUARE) ) {
					((XYLineAndShapeRenderer) r).setSeriesShape(0, defaultmarkers[0]);
				} else if ( shapeMarker.equals(MARKER_CIRCLE) ) {
					((XYLineAndShapeRenderer) r).setSeriesShape(0, defaultmarkers[1]);
				} else if ( shapeMarker.equals(MARKER_UP_TRIANGLE) ) {
					((XYLineAndShapeRenderer) r).setSeriesShape(0, defaultmarkers[2]);
				} else if ( shapeMarker.equals(MARKER_DIAMOND) ) {
					((XYLineAndShapeRenderer) r).setSeriesShape(0, defaultmarkers[3]);
				} else if ( shapeMarker.equals(MARKER_HOR_RECTANGLE) ) {
					((XYLineAndShapeRenderer) r).setSeriesShape(0, defaultmarkers[4]);
				} else if ( shapeMarker.equals(MARKER_DOWN_TRIANGLE) ) {
					((XYLineAndShapeRenderer) r).setSeriesShape(0, defaultmarkers[5]);
				} else if ( shapeMarker.equals(MARKER_HOR_ELLIPSE) ) {
					((XYLineAndShapeRenderer) r).setSeriesShape(0, defaultmarkers[6]);
				} else if ( shapeMarker.equals(MARKER_RIGHT_TRIANGLE) ) {
					((XYLineAndShapeRenderer) r).setSeriesShape(0, defaultmarkers[7]);
				} else if ( shapeMarker.equals(MARKER_VERT_RECTANGLE) ) {
					((XYLineAndShapeRenderer) r).setSeriesShape(0, defaultmarkers[8]);
				} else if ( shapeMarker.equals(MARKER_LEFT_TRIANGLE) ) {
					((XYLineAndShapeRenderer) r).setSeriesShape(0, defaultmarkers[9]);
				} else if ( shapeMarker.equals(MARKER_EMPTY) ) {
					((XYLineAndShapeRenderer) r).setSeriesShapesVisible(0, false);
				}

			}
			((XYLineAndShapeRenderer) r).setSeriesLinesVisible(0, showLine);
		} else if ( style.equals(IKeyword.AREA) ) {
			r = new XYAreaRenderer();
			r.setSeriesPaint(0, color);
		} else if ( style.equals(IKeyword.WHISKER) ) {
			r = new BoxAndWhiskerRenderer();
			r.setSeriesPaint(0, color);
		} else if ( style.equals(IKeyword.BAR) ) {
			r = new BarRenderer();
			r.setSeriesPaint(0, color);
		} else if ( style.equals(IKeyword.DOT) ) {
			r = new XYDotRenderer();
			r = new XYShapeRenderer();
			r.setSeriesPaint(0, color);
		} else if ( style.equals(IKeyword.SPLINE) ) {
			r = new XYSplineRenderer();
			r.setSeriesPaint(0, color);
			((XYSplineRenderer) r).setBaseShapesFilled(fillMarkers);
			((XYSplineRenderer) r).setBaseShapesVisible(showMarkers);
		} else if ( style.equals(IKeyword.STEP) ) {
			r = new XYStepRenderer();
		} else if ( style.equals(IKeyword.AREA_STACK) ) {
			r = new StackedXYAreaRenderer2();
			r.setSeriesPaint(0, color);
		} else if ( style.equals(IKeyword.STACK) ) {
			r = new StackedBarRenderer();
			r.setSeriesPaint(0, color);
			// ((XYStepRenderer) r).setBaseShapesFilled(fillMarkers);
			// ((XYStepRenderer) r).setBaseShapesVisible(showMarkers);
		}
		data.renderer = r;

		data.name = Cast.asString(scope,
			getFacetValue(scope, IKeyword.LEGEND, getFacetValue(scope, IKeyword.NAME, "data" + dataNumber++)));
		data.color = color;
		// r.setSeriesPaint(0, data.color);
		// in order to "detach" the expression from the current definition scope
		data.value = getFacet(IKeyword.VALUE).resolveAgainst(scope);
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
		ChartData data = createData(scope);
		((ArrayList) scope.getVarValue(DATAS)).add(data);
		return data;
	}

}