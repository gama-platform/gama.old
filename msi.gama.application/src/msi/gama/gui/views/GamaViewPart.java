/*********************************************************************************************
 * 
 * 
 * 'GamaViewPart.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.views;

import msi.gama.common.interfaces.IGamaView;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.views.actions.*;
import msi.gama.outputs.*;
import msi.gama.runtime.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

/**
 * @author drogoul
 */
public abstract class GamaViewPart extends ViewPart implements IGamaView, IGamaViewActions {

	protected IDisplayOutput output = null;
	protected Composite parent;

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		IPartService ps = (IPartService) site.getService(IPartService.class);
		ps.addPartListener(SwtGui.getPartListener());
		final String s_id = site.getSecondaryId();
		final String id = site.getId() + (s_id == null ? "" : s_id);
		IDisplayOutput out = null;
		if ( GAMA.getExperiment() != null ) {
			IOutputManager manager = GAMA.getExperiment().getSimulationOutputs();
			if ( manager != null ) {
				out = (IDisplayOutput) manager.getOutput(id);
			}
			if ( out == null ) {
				manager = GAMA.getExperiment().getExperimentOutputs();
				if ( manager != null ) {
					out = (IDisplayOutput) manager.getOutput(id);
				}
				if ( out == null ) {
					for ( FrontEndController fec : GAMA.getControllers().getValues() ) {
						manager = fec.getExperiment().getSimulationOutputs();
						if ( manager != null ) {
							out = (IDisplayOutput) manager.getOutput(id);
						}
						if ( out == null ) {
							manager = fec.getExperiment().getExperimentOutputs();
							if ( manager != null ) {
								out = (IDisplayOutput) manager.getOutput(id);
							}
						}
					}
				}
			}
		}
		setOutput(out);
		GamaToolbarFactory.buildToolbar(this, getToolbarActionsId());
	}

	/**
	 * @return
	 */
	public abstract Integer[] getToolbarActionsId();

	@Override
	public final void createPartControl(final Composite parent) {
		this.parent = parent;
		ownCreatePartControl(parent);
		activateContext();
	}

	// /**
	// * @param state one of the IWorkbenchPage STATE_* values: STATE_MAXIMIZED,
	// * STATE_MINIMIZED, STATE_RESTORED
	// */
	// public void setViewState(final int state) {
	// IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	// int currentState = page.getPartState(page.getReference(this));
	// if ( currentState != state ) {
	// page.activate(this);
	// page.setPartState(page.getReference(this), state);
	// }
	// }

	public abstract void ownCreatePartControl(Composite parent);

	public void activateContext() {
		final IContextService contextService = (IContextService) getSite().getService(IContextService.class);
		contextService.activateContext("msi.gama.application.simulation.context");
	}

	@Override
	public void setRefreshRate(final int rate) {
		if ( rate > 0 ) {
			setPartName(getOutput().getName() + " [refresh every " + String.valueOf(rate) +
				(rate == 1 ? " cycle]" : " cycles]"));
		}
	}

	@Override
	public void update(final IDisplayOutput output) {}

	@Override
	public IDisplayOutput getOutput() {
		return output;
	}

	@Override
	public void setOutput(final IDisplayOutput out) {
		if ( output != null ) {
			resetButtonStates();
		}
		output = out;
	}

	private void resetButtonStates() {
		GamaToolbarFactory.resetToolbar(this);
	}

	@Override
	public void setFocus() {}

	public void fixSize() {};

	@Override
	public void dispose() {
		IWorkbenchPartSite s = getSite();
		if ( s != null ) {
			IPartService ps = (IPartService) s.getService(IPartService.class);
			if ( ps != null ) {
				ps.removePartListener(SwtGui.getPartListener());
			}
		}

	}

}
