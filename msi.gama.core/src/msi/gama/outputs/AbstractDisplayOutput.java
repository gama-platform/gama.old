/*******************************************************************************************************
 *
 * AbstractDisplayOutput.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.outputs;

import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class AbstractDisplayOutput.
 *
 * @author drogoul
 */
public abstract class AbstractDisplayOutput extends AbstractOutput implements IDisplayOutput {

	static {
		DEBUG.ON();
	}

	/** The virtual. */
	final boolean virtual;

	/** The rendered. */
	volatile boolean rendered;

	/**
	 * Instantiates a new abstract display output.
	 *
	 * @param desc
	 *            the desc
	 */
	public AbstractDisplayOutput(final IDescription desc) {
		super(desc);
		virtual = IKeyword.TRUE.equals(getLiteral(IKeyword.VIRTUAL, null));
	}

	/** The disposed. */
	protected boolean disposed = false;

	/** The synchro. */
	// protected boolean synchro = false;

	/** The in init phase. */
	// protected boolean inInitPhase = true;

	/** The view. */
	protected IGamaView view;

	/** The opener. */
	final Runnable opener = () -> {
		view = getScope().getGui().showView(getScope(), getViewId(), isUnique() ? null : getName(), 1); // IWorkbenchPage.VIEW_ACTIVATE
		if (view == null) return;
		view.addOutput(AbstractDisplayOutput.this);
	};

	@Override
	public boolean isVirtual() { return virtual; }

	protected boolean shouldOpenView() {
		return true;
	}

	@Override
	public void open() {
		super.open();
		if (shouldOpenView()) { GAMA.getGui().run("Opening " + getName(), opener, false); }
	}

	@Override
	public boolean init(final IScope scope) throws GamaRuntimeException {
		super.init(scope);
		return true;
	}

	@Override
	public void dispose() {
		if (disposed) return;
		disposed = true;
		if (view != null) {
			view.removeOutput(this);
			view = null;
		}
		if (getScope() != null) { GAMA.releaseScope(getScope()); }
	}

	@Override
	public void update() throws GamaRuntimeException {
		if (view != null) {
			// DEBUG.OUT("Output asking view to update");
			view.update(this);
		}
	}

	@Override
	public boolean isRendered() {
		if (view != null && !view.isVisible()) return true;
		if (!this.isRefreshable() || !this.isOpen() || this.isPaused()) return true;
		return rendered;
	}

	/**
	 * Sets the rendered.
	 *
	 * @param b
	 *            the b
	 * @return true, if successful
	 */
	@Override
	public void setRendered(final boolean b) { rendered = b; }

	@Override
	public boolean isUnique() { return false; }

	@Override
	public void setPaused(final boolean pause) {
		super.setPaused(pause);
		if (view != null) { view.updateToolbarState(); }
	}

	@Override
	public IGamaView getView() { return view; }

	@Override
	public String getId() {
		IDescription desc = this.getDescription();
		final String cName = desc == null ? null : desc.getModelDescription().getAlias();
		if (cName != null && !"".equals(cName) && !getName().contains("#"))
			return isUnique() ? getViewId() : getViewId() + getName() + "#" + cName;
		return isUnique() ? getViewId() : getViewId() + getName();
	}

}
