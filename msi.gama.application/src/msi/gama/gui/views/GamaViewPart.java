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
import msi.gama.common.util.GuiUtils;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

/**
 * @author drogoul
 */
public abstract class GamaViewPart extends ViewPart implements IGamaView, IToolbarDecoratedView.Pausable, ITooltipDisplayer {

	protected IDisplayOutput output = null;
	protected Composite parent;
	protected GamaToolbar2 toolbar;
	private GamaUIJob updateJob;

	protected abstract class GamaUIJob extends UIJob {

		public GamaUIJob() {
			super("Updating " + getPartName());
		}

		public void runSynchronized() {
			GuiUtils.run(new Runnable() {

				@Override
				public void run() {
					runInUIThread(null);
				}
			});
		}

	}

	@Override
	public void setToolbar(final GamaToolbar2 toolbar) {
		this.toolbar = toolbar;
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

	@Override
	public void pauseChanged() {}

	@Override
	public void synchronizeChanged() {}

	protected final GamaUIJob getUpdateJob() {
		if ( updateJob == null ) {
			updateJob = createUpdateJob();
		}
		return updateJob;
	}

	protected abstract GamaUIJob createUpdateJob();

	@Override
	public final void update(final IDisplayOutput output) {
		GamaUIJob job = getUpdateJob();
		if ( job != null ) {
			if ( output.isSynchronized() ) {
				job.runSynchronized();
			} else {
				job.schedule();
			}
		}
	}

	@Override
	public IDisplayOutput getOutput() {
		return output;
	}

	@Override
	public void setOutput(final IDisplayOutput out) {
		resetButtonStates();
		if ( output != null && output != out ) {
			output.dispose();
		}
		output = out;
	}

	private void resetButtonStates() {
		GamaToolbarFactory.resetToolbar(this, toolbar);
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
		toolbar.wipe(SWT.LEFT);
	}

	@Override
	public void displayTooltip(final String text, final GamaUIColor color) {
		if ( toolbar == null || toolbar.isDisposed() ) { return; }
		toolbar.tooltip(text, color, SWT.LEFT);
	}

	@Override
	public void close() {

		GuiUtils.asyncRun(new Runnable() {

			@Override
			public void run() {
				try {
					getSite().getPage().hideView(GamaViewPart.this);

				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	public void outputReloaded(final IDisplayOutput output) {
		// if ( getOutput() != output ) {
		// setOutput(output);
		// }

	}

}
