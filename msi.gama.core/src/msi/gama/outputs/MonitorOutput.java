/*********************************************************************************************
 *
 *
 * 'MonitorOutput.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.outputs;

import msi.gama.common.interfaces.*;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

/**
 * The Class MonitorOutput.
 *
 * @author drogoul
 */
@symbol(name = IKeyword.MONITOR, kind = ISymbolKind.OUTPUT, with_sequence = false)
@facets(
	value = {
		@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false, doc = @doc("identifier of the monitor") ),
		@facet(name = IKeyword.REFRESH_EVERY,
			type = IType.INT,
			optional = true,
			doc = @doc(value = "Allows to refresh the monitor every n time steps (default is 1)",
				deprecated = "Use refresh: every(n) instead") ),
		@facet(name = IKeyword.COLOR,
			type = IType.COLOR,
			optional = true,
			doc = @doc("Indicates the (possibly dynamic) color of this output (default is a light gray)") ),
		@facet(name = IKeyword.REFRESH,
			type = IType.BOOL,
			optional = true,
			doc = @doc("Indicates the condition under which this output should be refreshed (default is true)") ),
		@facet(name = IKeyword.VALUE,
			type = IType.NONE,
			optional = false,
			doc = @doc("expression that will be evaluated to be displayed in the monitor") ) },
	omissible = IKeyword.NAME)
@inside(symbols = { IKeyword.OUTPUT, IKeyword.PERMANENT })
@doc(value = "A monitor allows to follow the value of an arbitrary expression in GAML.",
	usages = { @usage(value = "An example of use is:",
		examples = @example(value = "monitor \"nb preys\" value: length(prey as list) refresh_every: 5;  ",
			isExecutable = false) ) })
public class MonitorOutput extends AbstractDisplayOutput {

	protected String expressionText = "";
	protected IExpression value;
	protected IExpression colorExpression = null;
	protected GamaColor color = null;
	protected GamaColor constantColor = null;
	protected Object lastValue = "";

	public MonitorOutput(final IDescription desc) {
		super(desc);
		setValue(getFacet(IKeyword.VALUE));
		setColor(getFacet(IKeyword.COLOR));
		expressionText = getValue() == null ? "" : getValue().serialize(false);
	}

	/**
	 * @param facet
	 */
	private void setColor(final IExpression facet) {
		colorExpression = facet;
		if ( facet != null && facet.isConst() ) {
			constantColor = Cast.as(facet, GamaColor.class, false);
		}
	}

	public MonitorOutput(final String name, final String expr) {
		super(DescriptionFactory.create(IKeyword.MONITOR, IKeyword.VALUE, expr, IKeyword.NAME,
			name == null ? expr : name));
		setScope(GAMA.obtainNewScope());
		// setUserCreated(true);
		setNewExpressionText(expr);
		if ( getScope().init(this) ) {
			getScope().getSimulationScope().addOutput(this);
			setPaused(false);
			open();
		}
	}

	public Object getLastValue() {
		return lastValue;
	}

	@Override
	public String getViewId() {
		return IGui.MONITOR_VIEW_ID;
	}

	@Override
	public String getId() {
		return getViewId() + ":" + getName();
	}

	@Override
	public boolean init(final IScope scope) {
		super.init(scope);
		if ( colorExpression == null ) {
			IMacroAgent sim = scope.getRoot();
			if ( sim != null ) {
				constantColor = sim.getColor();
			}
		}
		return true;
	}

	@Override
	public boolean step(final IScope scope) {
		if ( getScope().interrupted() ) { return false; }
		if ( getValue() != null ) {
			try {
				lastValue = getValue().value(getScope());
			} catch (final GamaRuntimeException e) {
				lastValue = ItemList.ERROR_CODE + e.getMessage();
			}
		} else {
			lastValue = null;
		}
		if ( constantColor == null ) {
			if ( colorExpression != null ) {
				color = Cast.asColor(scope, colorExpression.value(scope));
			}
		}
		return true;
	}

	public GamaColor getColor() {
		return constantColor == null ? color : constantColor;
	}

	public String getExpressionText() {
		return expressionText == null ? "" : expressionText;
	}

	@Override
	public boolean isUnique() {
		return true;
	}

	public boolean setNewExpressionText(final String string) {
		expressionText = string;
		setValue(GAML.compileExpression(string, getScope().getSimulationScope()));
		return getScope().step(this);
	}

	public void setNewExpression(final IExpression expr) throws GamaRuntimeException {
		expressionText = expr == null ? "" : expr.serialize(false);
		setValue(expr);
		getScope().step(this);
	}

	@Override
	public String getName() {
		String result = super.getName();
		if ( result == null ) {
			result = getExpressionText();
		}
		return result;
	}

	public IExpression getValue() {
		return value;
	}

	protected void setValue(final IExpression value) {
		this.value = value;
	}

}
