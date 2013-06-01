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
package msi.gama.outputs;

import java.util.List;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GAML;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.types.IType;

/**
 * The Class MonitorOutput.
 * 
 * @author drogoul
 */
@symbol(name = IKeyword.MONITOR, kind = ISymbolKind.OUTPUT, with_sequence = false)
@facets(value = { @facet(name = IKeyword.NAME, type = IType.LABEL, optional = false),
	@facet(name = IKeyword.REFRESH_EVERY, type = IType.INT, optional = true),
	@facet(name = IKeyword.VALUE, type = IType.NONE, optional = false) }, omissible = IKeyword.NAME)
@inside(symbols = IKeyword.OUTPUT)
public class MonitorOutput extends AbstractDisplayOutput {

	public MonitorOutput(final IDescription desc) {
		super(desc);
		setValue(getFacet(IKeyword.VALUE));
		expressionText = getValue() == null ? "" : getValue().toGaml();
	}

	public MonitorOutput(final String name, final String expr, final boolean openRightNow) {
		super(DescriptionFactory.create(IKeyword.MONITOR, IKeyword.VALUE, expr, IKeyword.NAME, name == null ? expr
			: name));
		setScope(GAMA.obtainNewScope());
		setUserCreated(true);
		setNewExpressionText(expr, getScope());
		if ( getScope().init(this) ) {
			outputManager.addOutput(this);
			if ( openRightNow ) {
				schedule();
				open();
			}
		}
	}

	private String expressionText = "";
	private IExpression value;
	protected Object lastValue = "";
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
		return GuiUtils.MONITOR_VIEW_ID;
	}

	@Override
	public String getId() {
		return getViewId() + ":" + getName();
	}

	@Override
	public void step(final IScope scope) {
		if ( scope.interrupted() ) { return; }
		if ( getValue() != null ) {
			try {
				lastValue = getValue().value(scope);
			} catch (final GamaRuntimeException e) {
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

	public boolean setNewExpressionText(final String string, final IScope scope) {
		expressionText = string;
		setValue(GAML.compileExpression(string, scope.getSimulationScope()));
		return true;
	}

	public void setNewExpression(final IExpression expr) throws GamaRuntimeException {
		expressionText = expr == null ? "" : expr.toGaml();
		setValue(expr);
		getScope().step(this);
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
		final List<MonitorOutput> outputs = (List<MonitorOutput>) outputManager.getMonitors();
		final StringBuilder s = new StringBuilder(200);
		for ( final MonitorOutput output : outputs ) {
			s.append("monitor \"").append(output.getViewName()).append("\" value: ").append(output.expressionText)
				.append(" refresh_every: ").append(output.getFacet(IKeyword.REFRESH_EVERY).toString());
			s.append("\n");
		}
		return s.toString();
	}

	protected void setValue(IExpression value) {
		this.value = value;
	}

}
