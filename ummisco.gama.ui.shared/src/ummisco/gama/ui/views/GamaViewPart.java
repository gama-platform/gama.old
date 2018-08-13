/*********************************************************************************************
 *
 * 'GamaViewPart.java, in plugin ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.views;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

import msi.gama.common.interfaces.IGamaView;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.outputs.IOutputManager;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.controls.ITooltipDisplayer;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;
import ummisco.gama.ui.views.toolbar.GamaToolbarFactory;
import ummisco.gama.ui.views.toolbar.IToolbarDecoratedView;

/**
 * @author drogoul
 */
public abstract class GamaViewPart extends ViewPart
		implements DisposeListener, IGamaView, IToolbarDecoratedView, ITooltipDisplayer {

	static {
		DEBUG.OFF();
	}

	public final List<IDisplayOutput> outputs = new ArrayList<>();
	private Composite parent;
	protected GamaToolbar2 toolbar;
	private GamaUIJob updateJob;
	private StateListener toolbarUpdater;
	private Composite rootComposite;

	public enum UpdatePriority {
		HIGH, LOW, HIGHEST, LOWEST;
	}

	public abstract class GamaUIJob extends UIJob {

		public GamaUIJob() {
			super("Updating " + getPartName());
			final UpdatePriority p = jobPriority();
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
			WorkbenchHelper.run(() -> runInUIThread(null));
		}

	}

	@Override
	public void reset() {}

	@Override
	public void addStateListener(final StateListener listener) {
		toolbarUpdater = listener;
	}

	@Override
	public void updateToolbarState() {
		if (toolbarUpdater != null) {
			toolbarUpdater.updateToReflectState();
		}
	}

	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		this.toolbar = tb;
	}

	@Override
	public void init(final IViewSite site) throws PartInitException {
		super.init(site);
		OutputPartsManager.install();
		String s_id = site.getSecondaryId();
		if (s_id != null) {
			final int i = s_id.indexOf("@@@");
			if (i != -1) {
				s_id = s_id.substring(0, i);
			}
		}
		final String id = site.getId() + (s_id == null ? "" : s_id);
		IDisplayOutput out = null;

		final IExperimentPlan experiment = GAMA.getExperiment();

		if (experiment != null) {
			for (final IOutputManager manager : concat(
					transform(GAMA.getControllers(), each -> each.getExperiment().getActiveOutputManagers()))) {
				out = (IDisplayOutput) manager.get(id);
				if (out != null) {
					break;
				}
			}

			// hqngh in case of micro-model
			if (out == null) {
				final SimulationAgent sim = GAMA.getExperiment().getCurrentSimulation();
				if (sim != null) {
					final String[] stemp = id.split("#");
					if (stemp.length > 1) {
						final IPopulation<? extends IAgent> externPop =
								sim.getExternMicroPopulationFor(stemp[1] + "." + stemp[2]);
						if (externPop != null) {
							for (final IAgent expAgent : externPop) {
								final SimulationAgent spec = ((ExperimentAgent) expAgent).getSimulation();
								if (spec != null) {
									final IOutputManager manager = spec.getOutputManager();
									if (manager != null) {
										out = (IDisplayOutput) manager.get(s_id);
									}
								}
							}
						}
					}
				}
			}
		} else {
			if (shouldBeClosedWhenNoExperiments()) {
				WorkbenchHelper.asyncRun(() -> {
					if (shouldBeClosedWhenNoExperiments()) {
						close(GAMA.getRuntimeScope());
					}
				});

			}
		}
		addOutput(out);
	}

	/**
	 * Can be redefined by subclasses that accept that their instances remain open when no experiment is running.
	 * 
	 * @return
	 */
	protected boolean shouldBeClosedWhenNoExperiments() {
		return true;
	}

	public boolean containsPoint(final int x, final int y) {
		final Point o = rootComposite.toDisplay(0, 0);
		final Point s = rootComposite.getSize();
		return new Rectangle(o.x, o.y, s.x, s.y).contains(x, y);
	}

	@Override
	public void createPartControl(final Composite composite) {
		this.rootComposite = composite;
		composite.addDisposeListener(this);
		if (needsOutput() && getOutput() == null) { return; }
		this.setParentComposite(GamaToolbarFactory.createToolbars(this, composite));
		ownCreatePartControl(getParentComposite());
		// activateContext();
		// toggle.run();
	}

	protected boolean needsOutput() {
		return true;
	}

	public abstract void ownCreatePartControl(Composite parent);

	protected final GamaUIJob getUpdateJob() {
		if (updateJob == null) {
			updateJob = createUpdateJob();
		}
		return updateJob;
	}

	protected abstract GamaUIJob createUpdateJob();

	@Override
	public void update(final IDisplayOutput output) {
		final GamaUIJob job = getUpdateJob();
		if (job != null) {
			if (output.isSynchronized()) {
				job.runSynchronized();
			} else {
				job.schedule();
			}
		}
	}

	@Override
	public IDisplayOutput getOutput() {
		if (outputs.isEmpty()) { return null; }
		return outputs.get(0);
	}

	@Override
	public void addOutput(final IDisplayOutput out) {
		if (out == null) { return; }
		if (!outputs.contains(out)) {
			outputs.add(out);
		} else {
			if (toolbar != null) {
				toolbar.wipe(SWT.LEFT, true);
				toolbar.wipe(SWT.RIGHT, true);
				GamaToolbarFactory.buildToolbar(GamaViewPart.this, toolbar);
			}

		}

	}

	@Override
	public void setFocus() {}

	@Override
	public void widgetDisposed(final DisposeEvent e) {
		toolbar = null;
		outputs.clear();
	}

	@Override
	public void dispose() {
		DEBUG.OUT("+++ Part " + this.getPartName() + " is being disposed");
		toolbarUpdater = null;
		super.dispose();
	}

	/**
	 * Needs to be redefined for views that use the left toolbar (so that they maintain their previous state) Method
	 * stopDisplayingTooltips()
	 * 
	 * @see ummisco.gama.ui.controls.ITooltipDisplayer#stopDisplayingTooltips()
	 */
	@Override
	public void stopDisplayingTooltips() {
		if (toolbar == null || toolbar.isDisposed()) { return; }
		if (toolbar.hasTooltip()) {
			toolbar.wipe(SWT.LEFT, false);
		}
	}

	@Override
	public void displayTooltip(final String text, final GamaUIColor color) {
		if (toolbar == null || toolbar.isDisposed()) { return; }
		toolbar.tooltip(text, color, SWT.LEFT);
	}

	@Override
	public void close(final IScope scope) {

		WorkbenchHelper.asyncRun(() -> {
			try {
				WorkbenchHelper.hideView(GamaViewPart.this);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		});

	}

	@Override
	public void removeOutput(final IDisplayOutput output) {
		outputs.remove(output);
		if (outputs.isEmpty()) {
			close(output.getScope());
		}
	}

	@Override
	public void changePartNameWithSimulation(final SimulationAgent agent) {
		final String old = getPartName();
		final int first = old.lastIndexOf('(');
		final int second = old.lastIndexOf(')');
		if (first == -1) {
			if (agent.getPopulation().size() > 1) {
				setPartName(old + " (" + agent.getName() + ")");
			}
		} else {

			setPartName(overlay(old, agent.getName(), first + 1, second));
		}
	}

	// To avoid a dependency towards apache.commons.lang
	private String overlay(final String str, final String over, final int s, final int e) {
		String overlay = over;
		int start = s;
		int end = e;
		if (str == null) { return null; }
		if (overlay == null) {
			overlay = "";
		}
		final int len = str.length();
		if (start < 0) {
			start = 0;
		}
		if (start > len) {
			start = len;
		}
		if (end < 0) {
			end = 0;
		}
		if (end > len) {
			end = len;
		}
		if (start > end) {
			final int temp = start;
			start = end;
			end = temp;
		}
		return new StringBuilder(len + start - end + overlay.length() + 1).append(str.substring(0, start))
				.append(overlay).append(str.substring(end)).toString();
	}

	@Override
	public void setName(final String name) {
		super.setPartName(name);

	}

	public Composite getParentComposite() {
		return parent;
	}

	public void setParentComposite(final Composite parent) {
		this.parent = parent;
	}

}
