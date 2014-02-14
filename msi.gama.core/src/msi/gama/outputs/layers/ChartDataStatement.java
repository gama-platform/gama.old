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

import java.util.ArrayList;
import msi.gama.common.interfaces.IKeyword;
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
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.xy.*;

@symbol(name = IKeyword.DATA, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@inside(symbols = IKeyword.CHART, kinds = ISymbolKind.SEQUENCE_STATEMENT)
@facets(value = {
	@facet(name = IKeyword.VALUE, type = { IType.FLOAT, IType.LIST }, optional = false),
	@facet(name = IKeyword.NAME, type = IType.ID, optional = true),
	@facet(name = IKeyword.LEGEND, type = IType.STRING, optional = true),
	@facet(name = IKeyword.COLOR, type = IType.COLOR, optional = true),
	@facet(name = ChartDataStatement.MARKER, type = IType.BOOL, optional = true),
	@facet(name = ChartDataStatement.FILL, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.STYLE, type = IType.ID, values = { IKeyword.LINE, IKeyword.WHISKER, IKeyword.AREA,
		IKeyword.BAR, IKeyword.DOT, IKeyword.STEP, IKeyword.SPLINE, IKeyword.STACK, IKeyword.THREE_D, IKeyword.RING,
		IKeyword.EXPLODED }, optional = true) }, omissible = IKeyword.LEGEND)
public class ChartDataStatement extends AbstractStatement {

	public static final String MARKER = "marker";
	public static final String FILL = "fill";

	public static class ChartData {

		String name;
		GamaColor color;
		IExpression value;
		AbstractRenderer renderer;

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
			Object o = value.value(scope);
			if ( o instanceof GamaList ) { return Cast.asList(scope, o); }
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
		boolean fillMarkers = getFacetValue(scope, FILL, true);

		AbstractRenderer r = null;
		if ( style.equals(IKeyword.LINE) ) {
			r = new XYLineAndShapeRenderer(true, showMarkers);
			r.setSeriesPaint(0, color);
			((XYLineAndShapeRenderer) r).setBaseShapesFilled(fillMarkers);
		} else if ( style.equals(IKeyword.AREA) ) {
			r = new XYAreaRenderer();
			r.setSeriesPaint(0, color);
		} else if ( style.equals(IKeyword.WHISKER) ) {
			r = new BoxAndWhiskerRenderer();
			r.setSeriesPaint(0, color);
		} else if ( style.equals(IKeyword.BAR) ) {
			r = new XYBarRenderer();
			r.setSeriesPaint(0, color);
		} else if ( style.equals(IKeyword.DOT) ) {
			r = new XYDotRenderer();
			r.setSeriesPaint(0, color);
		} else if ( style.equals(IKeyword.SPLINE) ) {
			r = new XYSplineRenderer();
			r.setSeriesPaint(0, color);
			((XYSplineRenderer) r).setBaseShapesFilled(fillMarkers);
			((XYSplineRenderer) r).setBaseShapesVisible(showMarkers);
		} else if ( style.equals(IKeyword.STEP) ) {
			r = new XYStepRenderer();
			r.setSeriesPaint(0, color);
			((XYStepRenderer) r).setBaseShapesFilled(fillMarkers);
			((XYStepRenderer) r).setBaseShapesVisible(showMarkers);
		}
		data.renderer = r;

		data.name =
			Cast.asString(scope,
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