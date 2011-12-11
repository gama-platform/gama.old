/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.outputs.layers;

import java.awt.Color;
import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.kernel.Symbol;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.util.*;
import org.jfree.chart.renderer.xy.*;

@symbol(name = ISymbol.DATA, kind = ISymbolKind.LAYER)
@inside(symbols = ISymbol.CHART)
@facets({
	@facet(name = ISymbol.VALUE, type = IType.FLOAT_STR, optional = false),
	@facet(name = ISymbol.NAME, type = IType.LABEL, optional = false),
	@facet(name = ISymbol.COLOR, type = IType.COLOR_STR, optional = true),
	@facet(name = ISymbol.STYLE, type = IType.ID, values = { ISymbol.LINE, ISymbol.AREA,
		ISymbol.BAR, ISymbol.DOT, ISymbol.STEP, ISymbol.SPLINE, ISymbol.STACK, ISymbol.THREE_D,
		ISymbol.RING, ISymbol.EXPLODED }, optional = true) })
public class Data extends Symbol {

	GamaColor color;
	IExpression value;
	AbstractXYItemRenderer renderer;

	public Data(final IDescription desc) {
		super(desc);
		computeRenderer();
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {}

	public List<? extends ISymbol> getChildren() {
		return Collections.EMPTY_LIST;
	}

	void computeRenderer() {
		String style = getLiteral(ISymbol.STYLE);
		if ( style == null ) {
			style = ISymbol.LINE;
		}
		AbstractXYItemRenderer r = null;
		if ( style.equals(ISymbol.LINE) ) {
			r = new XYLineAndShapeRenderer();
		} else if ( style.equals(ISymbol.AREA) ) {
			r = new XYAreaRenderer();
		} else if ( style.equals(ISymbol.BAR) ) {
			r = new XYBarRenderer();
		} else if ( style.equals(ISymbol.DOT) ) {
			r = new XYDotRenderer();
		} else if ( style.equals(ISymbol.SPLINE) ) {
			r = new XYSplineRenderer();
		} else if ( style.equals(ISymbol.STEP) ) {
			r = new XYStepRenderer();
		}
		renderer = r;
	}

	public Color getColor() {
		return color;
	}

	public Double getValue(final IScope scope) throws GamaRuntimeException {
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
		name = Cast.asString(getFacetValue(scope, ISymbol.NAME));
		color = Cast.asColor(getFacetValue(scope, ISymbol.COLOR, Cast.asColor(scope, "black")));
		value = getFacet(ISymbol.VALUE);

	}

}