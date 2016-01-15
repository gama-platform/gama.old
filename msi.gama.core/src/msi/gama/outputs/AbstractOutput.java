/*********************************************************************************************
 *
 *
 * 'AbstractOutput.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.outputs;

import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
import msi.gaml.operators.Cast;

/**
 * The Class AbstractOutput.
 *
 * @author drogoul
 */
@inside(symbols = IKeyword.OUTPUT)
public abstract class AbstractOutput extends Symbol implements IOutput {

	private IScope scope;
	boolean paused, open, permanent = false;
	private boolean isUserCreated = true;
	final IExpression refresh;

	private int refreshRate = 1;

	public AbstractOutput(final IDescription desc) {
		super(desc);
		if ( hasFacet(IKeyword.REFRESH) ) {
			refresh = this.getFacet(IKeyword.REFRESH);
		} else {
			refresh = IExpressionFactory.TRUE_EXPR;
		}

		name = getLiteral(IKeyword.NAME);
		if ( name != null ) {
			name = name.replace(':', '_').replace('/', '_').replace('\\', '_');
			if ( name.length() == 0 ) {
				name = "output";
			}
		}
	}

	// @Override
	final boolean isUserCreated() {
		return isUserCreated;
	}

	// @Override
	final void setUserCreated(final boolean isUserCreated) {
		this.isUserCreated = isUserCreated;
	}

	@Override
	public boolean init(final IScope scope) {
		setScope(scope.copy());
		final IExpression refresh = getFacet(IKeyword.REFRESH_EVERY);
		if ( refresh != null ) {
			setRefreshRate(Cast.asInt(getScope(), refresh.value(getScope())));
		}
		return true;
	}

	@Override
	public void close() {
		setPaused(true);
		setOpen(false);
	}

	// @Override
	boolean isOpen() {
		return open;
	}

	@Override
	public boolean isPaused() {
		return paused;
	}

	@Override
	public void open() {
		setOpen(true);
	}

	// @Override
	boolean isRefreshable() {
		IScope scope = getScope();
		return Cast.asBool(scope, refresh.value(scope)) && refreshRate > 0 &&
			scope.getClock().getCycle() % refreshRate == 0;
	}

	@Override
	public int getRefreshRate() {
		return refreshRate;
	}

	@Override
	public void setRefreshRate(final int refresh) {
		refreshRate = refresh;
	}

	@Override
	public abstract boolean step(IScope scope);

	@Override
	public abstract void update() throws GamaRuntimeException;

	void setOpen(final boolean open) {
		this.open = open;
	}

	@Override
	public void setPaused(final boolean suspended) {
		paused = suspended;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {

	}

	public List<? extends ISymbol> getChildren() {
		return Collections.EMPTY_LIST;
	}

	// @Override
	String getId() {
		if ( !this.getDescription().getModelDescription().getAlias().equals("") ) { return getName() + "#" +
			this.getDescription().getModelDescription().getAlias() + "#" + getScope().getExperiment().getName(); }
		return getName(); // by default
	}

	public void setScope(final IScope scope) {
		if ( this.scope != null ) {
			GAMA.releaseScope(this.scope);
		}
		ModelDescription micro = this.getDescription().getModelDescription();
		ModelDescription main = (ModelDescription) scope.getModel().getDescription();
		Boolean fromMicroModel = main.getMicroModel(micro.getAlias()) != null;
		if ( fromMicroModel ) {
			ExperimentAgent exp = (ExperimentAgent) scope.getAgentScope()
				.getExternMicroPopulationFor(this.getDescription().getOriginName()).getAgent(0);
			this.scope = exp.getSimulation().getScope();
		} else {
			this.scope = scope;
		}
	}

	@Override
	public IScope getScope() {
		return scope;
	}

	// @Override
	void setPermanent() {
		permanent = true;
	}

	public boolean isPermanent() {
		return permanent;
	}

}
