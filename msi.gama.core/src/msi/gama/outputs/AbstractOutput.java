/*******************************************************************************************************
 *
 * AbstractOutput.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs;

import java.util.Collections;
import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.Symbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IExpressionFactory;
import msi.gaml.operators.Cast;

/**
 * The Class AbstractOutput.
 *
 * @author drogoul
 */
@inside (
		symbols = IKeyword.OUTPUT)
public abstract class AbstractOutput extends Symbol implements IOutput {

	/** The output scope. */
	private IScope outputScope;

	/** The permanent. */
	boolean paused, open, permanent = false;

	/** The is user created. */
	private boolean isUserCreated = true;

	/** The refresh. */
	final IExpression refresh;

	/** The original name. */
	final String originalName;

	/** The refresh rate. */
	private int refreshRate = 1;

	/**
	 * Instantiates a new abstract output.
	 *
	 * @param desc
	 *            the desc
	 */
	public AbstractOutput(final IDescription desc) {
		super(desc);
		if (hasFacet(IKeyword.REFRESH)) {
			refresh = this.getFacet(IKeyword.REFRESH);
		} else {
			refresh = IExpressionFactory.TRUE_EXPR;
		}
		if (desc != null) { name = desc.getName(); }
		originalName = name;
		if (name != null) {
			name = name.replace(':', '_').replace('/', '_').replace('\\', '_');
			if (name.length() == 0) { name = "output"; }
		}
	}

	@Override
	public String getOriginalName() { return originalName; }

	/**
	 * Checks if is user created.
	 *
	 * @return true, if is user created
	 */
	// @Override
	final boolean isUserCreated() { return isUserCreated; }

	// @Override
	@Override
	public final void setUserCreated(final boolean isUserCreated) { this.isUserCreated = isUserCreated; }

	@Override
	public boolean init(final IScope scope) {
		setScope(buildScopeFrom(scope));
		final IExpression refreshExpr = getFacet(IKeyword.REFRESH_EVERY);
		if (refreshExpr != null) { setRefreshRate(Cast.asInt(getScope(), refreshExpr.value(getScope()))); }
		// getScope().setCurrentSymbol(this);
		return true;
	}

	/**
	 * Builds the scope from.
	 *
	 * @param scope
	 *            the scope
	 * @return the i scope
	 */
	protected IScope buildScopeFrom(final IScope scope) {
		String desc = description == null ? " " : description.getKeyword() + " ";
		return scope.copy("of " + desc + getName());
	}

	@Override
	public void close() {
		setPaused(true);
		setOpen(false);
	}

	@Override
	public boolean isOpen() { return open; }

	@Override
	public boolean isPaused() { return paused; }

	@Override
	public void open() {
		setOpen(true);
	}

	// @Override
	@Override
	public boolean isRefreshable() {
		if (!isOpen() || isPaused()) return false;
		final IScope scope = getScope();
		if (scope == null || scope.interrupted()) return false;
		return Cast.asBool(scope, refresh.value(scope)) && refreshRate > 0
				&& scope.getClock().getCycle() % refreshRate == 0;
	}

	@Override
	public int getRefreshRate() { return refreshRate; }

	@Override
	public void setRefreshRate(final int refresh) { refreshRate = refresh; }

	@Override
	public abstract boolean step(IScope scope);

	/**
	 * Sets the open.
	 *
	 * @param open
	 *            the new open
	 */
	void setOpen(final boolean open) { this.open = open; }

	@Override
	public void setPaused(final boolean suspended) { paused = suspended; }

	@Override
	public void setChildren(final Iterable<? extends ISymbol> commands) {

	}

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public List<? extends ISymbol> getChildren() { return Collections.EMPTY_LIST; }

	// @Override
	@Override
	public String getId() {
		if (!"".equals(this.getDescription().getModelDescription().getAlias())) return getName() + "#"
				+ this.getDescription().getModelDescription().getAlias() + "#" + getScope().getExperiment().getName();
		return getName(); // by default
	}

	/**
	 * Sets the scope.
	 *
	 * @param scope
	 *            the new scope
	 */
	public void setScope(final IScope scope) {
		if (this.outputScope != null) { GAMA.releaseScope(this.outputScope); }
		if (scope.getModel() != null) {
			final ModelDescription micro = this.getDescription().getModelDescription();
			final ModelDescription main = scope.getModel().getDescription();
			final boolean fromMicroModel = main.getMicroModel(micro.getAlias()) != null;
			if (fromMicroModel) {
				final ExperimentAgent exp = (ExperimentAgent) scope.getRoot()
						.getExternMicroPopulationFor(micro.getAlias() + "." + this.getDescription().getOriginName())
						.getAgent(0);
				this.outputScope = exp.getSimulation().getScope();
			} else {
				this.outputScope = scope;
			}
		} else {
			this.outputScope = scope;
		}
	}

	@Override
	public IScope getScope() { return outputScope; }

	/**
	 * Sets the permanent.
	 */
	// @Override
	void setPermanent() {
		permanent = true;
	}

	/**
	 * Checks if is permanent.
	 *
	 * @return true, if is permanent
	 */
	public boolean isPermanent() { return permanent; }

}
