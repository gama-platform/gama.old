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
import msi.gama.gui.swt.GamaColors.GamaUIColor;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.controls.*;
import msi.gama.gui.views.actions.GamaToolbarFactory;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.outputs.*;
import msi.gama.runtime.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

/**
 * @author drogoul
 */
public abstract class GamaViewPart extends ViewPart implements IGamaView, IToolbarDecoratedView, ITooltipDisplayer {

	/**
	 * A composite that ensures an access to the toolbars, etc. from within any control of the view.
	 * Class GamaComposite.
	 * 
	 * @author drogoul
	 * @since 8 déc. 2014
	 * 
	 */

	protected IDisplayOutput output = null;
	protected Composite parent;
	protected GamaToolbar leftToolbar, rightToolbar;

	@Override
	public void setToolbars(final GamaToolbar left, final GamaToolbar right) {
		leftToolbar = left;
		rightToolbar = right;
	}

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
				if ( out == null ) {
					manager = GAMA.getExperiment().getExperimentOutputs();
					if ( manager != null ) {
						out = (IDisplayOutput) manager.getOutput(id);
					}
				}
			}

			// hqnghi in case of multi-controller
			if ( out == null ) {
				for ( FrontEndController fec : GAMA.getControllers().values() ) {
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

			// hqngh in case of micro-model
			if ( out == null ) {
				SimulationAgent sim = GAMA.getExperiment().getCurrentSimulation();
				if ( sim != null ) {
					String[] stemp = id.split("#");
					if ( stemp.length > 1 ) {
						IPopulation externPop = sim.getExternMicroPopulationFor(stemp[2]);
						if ( externPop != null ) {
							for ( IAgent expAgent : externPop ) {
								SimulationAgent spec = (SimulationAgent) ((ExperimentAgent) expAgent).getSimulation();
								if ( spec != null ) {
									manager = spec.getOutputManger();
									if ( manager != null ) {
										out = (IDisplayOutput) manager.getOutput(s_id);
									}
								}
							}
						}
					}
				}
			}
		}
		setOutput(out);
		// GamaToolbarFactory.buildToolbar(this, getToolbarActionsId());
	}

	/**
	 * @return
	 */
	@Override
	public abstract Integer[] getToolbarActionsId();

	@Override
	public/* final */void createPartControl(final Composite composite) {
		this.parent = GamaToolbarFactory.createToolbars(this, composite);
		ownCreatePartControl(parent);
		activateContext();
	}

	public abstract void ownCreatePartControl(Composite parent);

	public void activateContext() {
		final IContextService contextService = (IContextService) getSite().getService(IContextService.class);
		contextService.activateContext("msi.gama.application.simulation.context");
	}

	public void pauseChanged() {}

	//
	// public void setRefreshRate(final int rate) {
	// if ( rate > 0 ) {
	// setPartName(getOutput().getName() + " [refresh every " + String.valueOf(rate) +
	// (rate == 1 ? " cycle]" : " cycles]"));
	// }
	// }

	@Override
	public void update(final IDisplayOutput output) {}

	@Override
	public IDisplayOutput getOutput() {
		return output;
	}

	@Override
	public void setOutput(final IDisplayOutput out) {
		resetButtonStates();
		output = out;
	}

	private void resetButtonStates() {
		GamaToolbarFactory.resetToolbar(this, rightToolbar);
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
		super.dispose();
	}

	/**
	 * Needs to be redefined for views that use the left toolbar (so that they maintain their previous state)
	 * Method stopDisplayingTooltips()
	 * @see msi.gama.gui.swt.controls.ITooltipDisplayer#stopDisplayingTooltips()
	 */
	@Override
	public void stopDisplayingTooltips() {
		leftToolbar.wipe();
	}

	@Override
	public void displayTooltip(final String text, final GamaUIColor color) {
		if ( leftToolbar == null || leftToolbar.isDisposed() ) { return; }
		final int width = 2 * (leftToolbar.getParent().getBounds().width - rightToolbar.getSize().x) / 3;
		leftToolbar.wipe();
		leftToolbar.tooltip(text, color, width);
		leftToolbar.refresh();
	}

	@Override
	public void createToolItem(final int code, final GamaToolbar tb) {
		// Do nothing by default. Subclasses may override to create specific buttons
	}

}
