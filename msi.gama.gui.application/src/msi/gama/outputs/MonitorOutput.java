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
package msi.gama.outputs;

import java.util.List;
import msi.gama.factories.DescriptionFactory;
import msi.gama.gui.application.views.*;
import msi.gama.interfaces.*;
import msi.gama.internal.compilation.*;
import msi.gama.internal.expressions.ExpressionDescription;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;

/**
 * The Class MonitorOutput.
 * 
 * @author drogoul
 */
@symbol(name = ISymbol.MONITOR, kind = ISymbolKind.OUTPUT)
@facets(value = { @facet(name = ISymbol.NAME, type = IType.LABEL, optional = false),
	@facet(name = ISymbol.REFRESH_EVERY, type = IType.INT_STR, optional = true),
	@facet(name = ISymbol.VALUE, type = IType.NONE_STR, optional = false) })
@inside(symbols = ISymbol.OUTPUT)
public class MonitorOutput extends AbstractDisplayOutput {

	public MonitorOutput(final IDescription desc) {
		super(desc);
		value = getFacet(ISymbol.VALUE);
		expressionText = value == null ? "" : value.toGaml();
	}

	public MonitorOutput(final String name, final String expr, final ISimulation sim,
		final boolean openRightNow) throws GamlException {
		super(DescriptionFactory.createDescription(ISymbol.MONITOR, ISymbol.VALUE, expr,
			ISymbol.NAME, name == null ? expr : name));
		setUserCreated(true);
		setNewExpressionText(expr, sim);
		prepare(sim);
		outputManager.addOutput(this);
		if ( openRightNow ) {
			schedule();
			open();
		}
	}

	private String expressionText = "";
	protected IExpression value;
	protected Object lastValue = "No expression to monitor";
	private boolean isUserCreated = false;

	@Override
	public final boolean isUserCreated() {
		return isUserCreated;
	}

	public final void setUserCreated(final boolean isUserCreated) {
		this.isUserCreated = isUserCreated;
	}

	public Object getLastValue() {
		return lastValue;
	}

	@Override
	public String getViewId() {
		return MonitorView.ID;
	}

	@Override
	public String getId() {
		return getViewId() + ":" + getName();
	}

	@Override
	public void compute(final IScope scope, final Long cycle) throws GamaRuntimeException {
		if ( value != null ) {
			try {
				lastValue = value.value(scope);
			} catch (GamaRuntimeException e) {
				lastValue = ItemList.ERROR_CODE + e.getMessage();
			}
		} else {
			lastValue = "No expression to monitor";
		}
	}

	public String getExpressionText() {
		return expressionText == null ? "" : expressionText;
	}

	@Override
	public boolean isUnique() {
		return true;
	}

	public boolean setNewExpressionText(final String string, final ISimulation sim)
		throws GamlException {
		expressionText = string;
		value =
			GAMA.getExpressionFactory().createExpr(new ExpressionDescription(string),
				sim.getWorldPopulation().getSpecies().getDescription());
		return true;
	}

	public void setNewExpression(final IExpression expr) throws GamaRuntimeException {
		expressionText = expr == null ? "" : expr.toGaml();
		value = expr;
		compute(getOwnScope(), 0l);
	}

	@Override
	public String getViewName() {
		String result = super.getViewName();
		if ( result == null ) {
			result = getExpressionText();
		}
		return result;
	}

	public IExpression getValue() {
		return value;
	}

	@Override
	public String toGaml() {
		final List<MonitorOutput> outputs = outputManager.getMonitors();
		StringBuilder s = new StringBuilder();
		for ( final MonitorOutput output : outputs ) {
			s.append("<monitor name=\"").append(output.getViewName()).append("\" value=\"")
				.append(output.expressionText).append("\" refresh_every=\"")
				.append(output.getFacet(ISymbol.REFRESH_EVERY).toString());
			s.append("\" />\n");
		}
		return s.toString();
	}

}
