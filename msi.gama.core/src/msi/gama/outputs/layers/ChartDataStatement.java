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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.outputs.layers;

import java.awt.Color;
import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import org.jfree.chart.renderer.xy.*;

@symbol(name = IKeyword.DATA, kind = ISymbolKind.LAYER, with_sequence = false)
@inside(symbols = IKeyword.CHART)
@facets(value = {
	@facet(name = IKeyword.VALUE, type = IType.FLOAT_STR, optional = false),
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false),
	@facet(name = IKeyword.COLOR, type = IType.COLOR_STR, optional = true),
	@facet(name = IKeyword.STYLE, type = IType.ID, values = { IKeyword.LINE, IKeyword.AREA,
		IKeyword.BAR, IKeyword.DOT, IKeyword.STEP, IKeyword.SPLINE, IKeyword.STACK,
		IKeyword.THREE_D, IKeyword.RING, IKeyword.EXPLODED }, optional = true) }, omissible = IKeyword.NAME)
public class ChartDataStatement extends Symbol {

	GamaColor color;
	IExpression value;
	AbstractXYItemRenderer renderer;

	public ChartDataStatement(final IDescription desc) {
		super(desc);
		computeRenderer();
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {}

	public List<? extends ISymbol> getChildren() {
		return Collections.EMPTY_LIST;
	}

	void computeRenderer() {
		String style = getLiteral(IKeyword.STYLE);
		if ( style == null ) {
			style = IKeyword.LINE;
		}
		AbstractXYItemRenderer r = null;
		if ( style.equals(IKeyword.LINE) ) {
			r = new XYLineAndShapeRenderer();
		} else if ( style.equals(IKeyword.AREA) ) {
			r = new XYAreaRenderer();
		} else if ( style.equals(IKeyword.BAR) ) {
			r = new XYBarRenderer();
		} else if ( style.equals(IKeyword.DOT) ) {
			r = new XYDotRenderer();
		} else if ( style.equals(IKeyword.SPLINE) ) {
			r = new XYSplineRenderer();
		} else if ( style.equals(IKeyword.STEP) ) {
			r = new XYStepRenderer();
		}
		renderer = r;
	}

	public Color getColor() {
		return color;
	}

	public double getValue(final IScope scope) throws GamaRuntimeException {
		return Cast.asFloat(scope, value.value(scope));
	}

	public AbstractXYItemRenderer getRenderer() {
		return renderer;
	}

	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 */
	public void prepare(final IScope scope) throws GamaRuntimeException {
		name = Cast.asString(scope, getFacetValue(scope, IKeyword.NAME));
		color =
			Cast.asColor(scope, getFacetValue(scope, IKeyword.COLOR, Cast.asColor(scope, "black")));
		value = getFacet(IKeyword.VALUE);

	}

}