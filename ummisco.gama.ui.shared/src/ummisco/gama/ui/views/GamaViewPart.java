/*******************************************************************************************************
 *
 * GamaViewPart.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.views;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.jobs.Job;
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
import ummisco.gama.ui.utils.ViewsHelper;
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
		DEBUG.ON();
	}

	/** The outputs. */
	public final List<IDisplayOutput> outputs = new ArrayList<>();

	/** The parent. */
	private Composite parent;

	/** The toolbar. */
	protected GamaToolbar2 toolbar;

	/** The update job. */
	private Job updateJob;

	/** The toolbar updater. */
	private StateListener toolbarUpdater;

	/** The root composite. */
	private Composite rootComposite;

	/**
	 * The Enum UpdatePriority.
	 */
	public enum UpdatePriority {

		/** The high. */
		HIGH,
		/** The low. */
		LOW,
		/** The highest. */
		HIGHEST,
		/** The lowest. */
		LOWEST;
	}

	/**
	 * The Class GamaUIJob.
	 */
	public abstract class GamaUIJob extends UIJob {

		/**
		 * Instantiates a new gama UI job.
		 */
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

		/**
		 * Job priority.
		 *
		 * @return the update priority
		 */
		protected abstract UpdatePriority jobPriority();

		/**
		 * Run synchronized.
		 */
		// public void runSynchronized() {
		// WorkbenchHelper.run(() -> runInUIThread(null));
		// }

	}

	@Override
	public void reset() {}

	/**
	 * Gets the top composite.
	 *
	 * @return the top composite
	 */
	// public Composite getTopComposite() {
	// Composite c = rootComposite;
	// while (!(c.getParent() instanceof CTabFolder)) { c = c.getParent(); }
	// return c;
	// }

	@Override
	public void addStateListener(final StateListener listener) {
		toolbarUpdater = listener;
	}

	@Override
	public void updateToolbarState() {
		if (toolbarUpdater != null) { toolbarUpdater.updateToReflectState(); }
		// if (toolbar != null && toolbar.isVisible()) { toolbar.visuallyUpdate(); }
	}

	@Override
	public void showToolbar(final boolean show) {

		if (toolbar != null) {
			if (show) {
				toolbar.show();
			} else {
				toolbar.hide();
			}
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
			if (i != -1) { s_id = s_id.substring(0, i); }
		}
		final String id = site.getId() + (s_id == null ? "" : s_id);
		IDisplayOutput out = null;

		final IExperimentPlan experiment = GAMA.getExperiment();

		if (experiment != null) {
			for (final IOutputManager manager : concat(
					transform(GAMA.getControllers(), each -> each.getExperiment().getActiveOutputManagers()))) {
				out = (IDisplayOutput) manager.getOutputWithId(id);
				if (out != null) { break; }
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
									if (manager != null) { out = (IDisplayOutput) manager.getOutputWithId(s_id); }
								}
							}
						}
					}
				}
			}
		} else if (shouldBeClosedWhenNoExperiments()) {
			WorkbenchHelper
					.asyncRun(() -> { if (shouldBeClosedWhenNoExperiments()) { close(GAMA.getRuntimeScope()); } });

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

	/**
	 * Contains point.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return true, if successful
	 */
	public boolean containsPoint(final int x, final int y) {
		final Point o = rootComposite.toDisplay(0, 0);
		final Point s = rootComposite.getSize();
		Rectangle r = new Rectangle(o.x, o.y, s.x, s.y);
		// DEBUG.OUT("Looking in rootComposite rectangle " + r);
		return r.contains(x, y);
	}

	@Override
	public void createPartControl(final Composite composite) {
		this.rootComposite = composite;
		// DEBUG.OUT("Root Composite is " + composite.getClass().getSimpleName());
		composite.addDisposeListener(this);
		// getTopComposite().addControlListener(this);
		if (needsOutput() && getOutput() == null) return;
		this.setParentComposite(GamaToolbarFactory.createToolbars(this, composite));
		ownCreatePartControl(getParentComposite());
		// activateContext();
		// toggle.run();
	}

	/**
	 * Needs output.
	 *
	 * @return true, if successful
	 */
	protected boolean needsOutput() {
		return true;
	}

	/**
	 * Own create part control.
	 *
	 * @param parent
	 *            the parent
	 */
	public abstract void ownCreatePartControl(Composite parent);

	/**
	 * Gets the update job.
	 *
	 * @return the update job
	 */
	protected final Job getUpdateJob() {
		if (updateJob == null) { updateJob = createUpdateJob(); }
		return updateJob;
	}

	/**
	 * Creates the update job.
	 *
	 * @return the gama UI job
	 */
	protected abstract Job createUpdateJob();

	@Override
	public void update(final IDisplayOutput output) {
		final Job job = getUpdateJob();
		if (job != null) {
			job.schedule();
			if (GAMA.isSynchronized() && !WorkbenchHelper.isDisplayThread()) {
				try {
					job.join();
				} catch (InterruptedException e) {
					DEBUG.OUT("Update of " + getTitle() + " interrupted.");
				}
			}
		}
	}

	@Override
	public IDisplayOutput getOutput() {
		if (outputs.isEmpty()) return null;
		return outputs.get(0);
	}

	@Override
	public void addOutput(final IDisplayOutput out) {
		if (out == null) return;
		if (!outputs.contains(out)) {
			outputs.add(out);
		} else if (toolbar != null) {
			toolbar.wipe(SWT.LEFT, true);
			toolbar.wipe(SWT.RIGHT, true);
			GamaToolbarFactory.buildToolbar(GamaViewPart.this, toolbar);
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
		toolbarUpdater = null;
		WorkbenchHelper.run(super::dispose);

	}

	/**
	 * Needs to be redefined for views that use the left toolbar (so that they maintain their previous state) Method
	 * stopDisplayingTooltips()
	 *
	 * @see ummisco.gama.ui.controls.ITooltipDisplayer#stopDisplayingTooltips()
	 */
	@Override
	public void stopDisplayingTooltips() {
		if (toolbar == null || toolbar.isDisposed()) return;
		if (toolbar.hasTooltip()) { toolbar.wipe(SWT.LEFT, false); }
	}

	@Override
	public void displayTooltip(final String text, final GamaUIColor color) {
		if (toolbar == null || toolbar.isDisposed()) return;
		toolbar.tooltip(text, color, SWT.LEFT);
	}

	@Override
	public void close(final IScope scope) {
		WorkbenchHelper.asyncRun(() -> {
			try {
				ViewsHelper.hideView(GamaViewPart.this);
				Job job = getUpdateJob();
				if (job != null && job.getThread() != null) { job.getThread().interrupt(); }
			} catch (final Exception e) {}
		});

	}

	@Override
	public void removeOutput(final IDisplayOutput output) {
		outputs.remove(output);
		if (outputs.isEmpty()) { close(output.getScope()); }
	}

	@Override
	public void changePartNameWithSimulation(final SimulationAgent agent) {
		final String old = getPartName();
		final int first = old.lastIndexOf('(');
		final int second = old.lastIndexOf(')');
		if (first == -1) {
			if (agent.getPopulation().size() > 1) { setPartName(old + " (" + agent.getName() + ")"); }
		} else {

			setPartName(overlay(old, agent.getName(), first + 1, second));
		}
	}

	/**
	 * Overlay.
	 *
	 * @param str
	 *            the str
	 * @param over
	 *            the over
	 * @param s
	 *            the s
	 * @param e
	 *            the e
	 * @return the string
	 */
	// To avoid a dependency towards apache.commons.lang
	private String overlay(final String str, final String over, final int s, final int e) {
		String overlay = over;
		int start = s;
		int end = e;
		if (str == null) return null;
		if (overlay == null) { overlay = ""; }
		final int len = str.length();
		if (start < 0) { start = 0; }
		if (start > len) { start = len; }
		if (end < 0) { end = 0; }
		if (end > len) { end = len; }
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

	/**
	 * Gets the parent composite.
	 *
	 * @return the parent composite
	 */
	public Composite getParentComposite() { return parent; }

	/**
	 * Sets the parent composite.
	 *
	 * @param parent
	 *            the new parent composite
	 */
	public void setParentComposite(final Composite parent) { this.parent = parent; }

	@Override
	public boolean isVisible() { return true; }

}
