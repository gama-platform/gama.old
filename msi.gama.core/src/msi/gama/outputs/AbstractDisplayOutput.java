/*********************************************************************************************
 * 
 * 
 * 'AbstractDisplayOutput.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.outputs;

import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.util.GuiUtils;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;

/**
 * The Class AbstractDisplayOutput.
 * 
 * @author drogoul
 */
public abstract class AbstractDisplayOutput extends AbstractOutput implements IDisplayOutput {

	public AbstractDisplayOutput(final IDescription desc) {
		super(desc);
	}

	protected boolean disposed = false;

	final Runnable opener = new Runnable() {

		@Override
		public void run() {
			IGamaView view = GuiUtils.showView(getViewId(), isUnique() ? null : getName(), 3); // IWorkbenchPage.VIEW_CREATE
			if ( view == null ) { return; }
			view.setOutput(AbstractDisplayOutput.this);
		}

	};

	@Override
	public void open() {
		super.open();
		GuiUtils.run(opener);
		setRefreshRate(getRefreshRate()); // Workaround for displaying the title
	}

	@Override
	public void resume() {
		if ( !paused ) { return; }
		super.resume();
	}

	// @Override
	// public void setRefreshRate(final int refresh) {
	// super.setRefreshRate(refresh);
	// GuiUtils.setViewRateOf(this, refresh);
	// }

	@Override
	public void dispose() {
		if ( disposed ) { return; }
		disposed = true;
		GuiUtils.closeViewOf(this);
		if ( getScope() != null ) {
			GAMA.releaseScope(getScope());
		}
	}

	@Override
	public String getViewName() {
		return getName();
	}

	@Override
	public void update() throws GamaRuntimeException {
		GuiUtils.updateViewOf(this);
	}

	@Override
	public void forceUpdate() throws GamaRuntimeException {
		update();
	}

	@Override
	public boolean isUnique() {
		return false;
	}

	@Override
	public abstract String getViewId();

	@Override
	public String getId() {
		String cName = ((AbstractOutput) this).getDescription().getModelDescription().getAlias();
		if ( !cName.equals("") && !getName().contains("#") ) { return isUnique() ? getViewId() : getViewId() +
			getName() + "#" + cName; }
		return isUnique() ? getViewId() : getViewId() + getName();
	}

}
