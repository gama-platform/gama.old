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

import java.util.*;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.*;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import msi.gama.common.interfaces.IGamaView;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.GamaColors.GamaUIColor;
import msi.gama.gui.swt.controls.*;
import msi.gama.gui.views.actions.GamaToolbarFactory;
import msi.gama.kernel.experiment.*;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.outputs.*;
import msi.gama.runtime.GAMA;

/**
 * @author drogoul
 */
public abstract class GamaViewPart extends ViewPart implements IGamaView, IToolbarDecoratedView, ITooltipDisplayer {

	protected final List<IDisplayOutput> outputs = new ArrayList();
	protected Composite parent;
	protected GamaToolbar2 toolbar;
	private GamaUIJob updateJob;
	Action toggle;

	enum UpdatePriority {
		HIGH, LOW, HIGHEST, LOWEST;
	}

	protected abstract class GamaUIJob extends UIJob {

		public GamaUIJob() {
			super("Updating " + getPartName());
			UpdatePriority p = jobPriority();
			switch (p) {
				case HIGHEST:
					setPriority(INTERACTIVE);
					break;
				case LOWEST:
					setPriority(DECORATE);
					break;
				case HIGH:
					setPriority(SHORT);
					break;
				case LOW:
					setPriority(LONG);
					break;
			}
		}

		protected abstract UpdatePriority jobPriority();

		public void runSynchronized() {
			GAMA.getGui().run(new Runnable() {

				@Override
				public void run() {
					runInUIThread(null);
				}
			});
		}

	}

	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		this.toolbar = tb;
	}

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		IPartService ps = site.getService(IPartService.class);
		ps.addPartListener(SwtGui.getPartListener());
		final String s_id = site.getSecondaryId();
		final String id = site.getId() + (s_id == null ? "" : s_id);
		IDisplayOutput out = null;

		IExperimentPlan experiment = GAMA.getExperiment();

		if ( GAMA.getExperiment() != null ) {
			// hqnghi in case of multi-controller
			if ( out == null ) {
				for ( IExperimentController fec : GAMA.getControllers() ) {
					List<IOutputManager> mm = fec.getExperiment().getAllSimulationOutputs();
					for ( IOutputManager manager : mm ) {
						if ( manager != null ) {
							out = (IDisplayOutput) manager.getOutput(id);
						}

					}
					if ( out == null ) {
						IOutputManager manager = fec.getExperiment().getExperimentOutputs();
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
								SimulationAgent spec = ((ExperimentAgent) expAgent).getSimulation();
								if ( spec != null ) {
									IOutputManager manager = spec.getOutputManager();
									if ( manager != null ) {
										out = (IDisplayOutput) manager.getOutput(s_id);
									}
								}
							}
						}
					}
				}
			}
		} else {
			if ( shouldBeClosedWhenNoExperiments() ) {
				System.err.println("Tried to reopen " + getClass().getSimpleName() + " ; automatically closed");
				org.eclipse.swt.widgets.Display.getDefault().asyncExec(new Runnable() {

					@Override
					public void run() {
						close();
						// GAMA.getGui().openModelingPerspective(false);
					}
				});

			}
		}
		addOutput(out);
	}

	/**
	 * Can be redefined by subclasses that accept that their instances remain open when no experiment is running.
	 * @return
	 */
	protected boolean shouldBeClosedWhenNoExperiments() {
		return true;
	}

	@Override
	public final void createPartControl(final Composite composite) {
		this.parent = GamaToolbarFactory.createToolbars(this, composite);
		ownCreatePartControl(parent);
		activateContext();
		// toggle.run();
	}

	public abstract void ownCreatePartControl(Composite parent);

	private void activateContext() {
		final IContextService contextService = getSite().getService(IContextService.class);
		contextService.activateContext("msi.gama.application.simulation.context");
	}

	// @Override
	// public void pauseChanged() {}
	//
	// @Override
	// public void synchronizeChanged() {}

	protected final GamaUIJob getUpdateJob() {
		if ( updateJob == null ) {
			updateJob = createUpdateJob();
		}
		return updateJob;
	}

	protected abstract GamaUIJob createUpdateJob();

	@Override
	public void update(final IDisplayOutput output) {
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
		if ( outputs.isEmpty() ) { return null; }
		return outputs.get(0);
	}

	@Override
	public void addOutput(final IDisplayOutput out) {
		if ( out == null ) { return; }
		if ( !outputs.contains(out) ) {
			outputs.add(out);
		} else {
			if ( toolbar != null ) {
				toolbar.wipe(SWT.LEFT, true);
				toolbar.wipe(SWT.RIGHT, true);
				GamaToolbarFactory.buildToolbar(this, toolbar);
			}

			// outputReloaded(out);
		}
		int r = (int) Math.random() * 255;
		int g = (int) Math.random() * 255;
		int b = (int) Math.random() * 255;
		if ( out.getScope().getSimulationScope() != null ) {
			this.setTitleImage(GamaIcons.createTempColorIcon(
				SwtGui.getColorForSimulationNumber(out.getScope().getSimulationScope().getIndex())));
		}
	}

	@Override
	public void setFocus() {}

	public void fixSize() {};

	@Override
	public void dispose() {
		toolbar = null;
		outputs.clear();
		IWorkbenchPartSite s = getSite();
		if ( s != null ) {
			IPartService ps = s.getService(IPartService.class);
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
		if ( toolbar == null || toolbar.isDisposed() ) { return; }
		if ( toolbar.hasTooltip() ) {
			toolbar.wipe(SWT.LEFT, false);
		}
	}

	@Override
	public void displayTooltip(final String text, final GamaUIColor color) {
		if ( toolbar == null || toolbar.isDisposed() ) { return; }
		toolbar.tooltip(text, color, SWT.LEFT);
	}

	@Override
	public void close() {

		GAMA.getGui().asyncRun(new Runnable() {

			@Override
			public void run() {
				try {
					// System.out.println("Closing: " + getPartName());
					getSite().getPage().hideView(GamaViewPart.this);
					// System.out.println("Closed: " + getPartName());
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	public void removeOutput(final IDisplayOutput output) {
		outputs.remove(output);
		if ( outputs.isEmpty() ) {
			close();
		}
	}

	// @Override
	// public void showToolbar() {
	// // toggle.run();
	// }
	//
	// @Override
	// public void hideToolbar() {
	// // toggle.run();
	// }
	//
	// @Override
	// public void setToogle(final Action toggle) {
	// this.toggle = toggle;
	// }

}
