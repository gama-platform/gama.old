/*******************************************************************************************************
 *
 * StatusControlContribution.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.controls;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

import msi.gama.common.StatusMessage;
import msi.gama.common.SubTaskMessage;
import msi.gama.common.UserStatusMessage;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IStatusMessage;
import msi.gama.common.interfaces.IUpdaterTarget;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.kernel.root.PlatformAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.kernel.simulation.SimulationPopulation;
import msi.gama.runtime.GAMA;
import msi.gaml.operators.Strings;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.controls.IPopupProvider.PopupText;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.GamaIcon;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class StatusControlContribution.
 */
public class StatusControlContribution extends WorkbenchWindowControlContribution
		implements IUpdaterTarget<IStatusMessage> {

	static {
		DEBUG.ON();
	}

	/** The is updating. */
	volatile boolean isUpdating;

	/** The label. */
	FlatButton label;

	/** The popup. */
	private SimulationPopupMenu popup;

	/** The state. */
	int state;

	/** The main task name. */
	volatile String mainTaskName;

	/** The sub task name. */
	volatile String subTaskName;

	/** The in sub task. */
	volatile boolean inSubTask = false;

	/** The in user status. */
	volatile boolean inUserStatus = false;

	/** The sub task completion. */
	volatile Double subTaskCompletion;

	/** The Constant WIDTH. */
	private final static int WIDTH = 400;

	/** The color. */
	private GamaUIColor color;

	/** The text. */
	StringBuilder text = new StringBuilder(2000);

	/** The instance. */
	static StatusControlContribution INSTANCE;

	/** The listening agent. Either gama, the experiment or the current simulation */
	// ITopLevelAgent listeningAgent;

	/**
	 * Gets the single instance of StatusControlContribution.
	 *
	 * @return single instance of StatusControlContribution
	 */
	public static StatusControlContribution getInstance() { return INSTANCE; }

	/**
	 * Instantiates a new status control contribution.
	 */
	public StatusControlContribution() {
		INSTANCE = this;
	}

	/**
	 * Instantiates a new status control contribution.
	 *
	 * @param id
	 *            the id
	 */
	public StatusControlContribution(final String id) { // NO_UCD (unused code)
		super(id);
		INSTANCE = this;
	}

	@Override
	protected int computeWidth(final Control control) {
		return WIDTH;
	}

	@Override
	public boolean isBusy() { return isUpdating; }

	@Override
	protected Control createControl(final Composite parent) {
		final Composite compo = new Composite(parent, SWT.DOUBLE_BUFFERED);
		final GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		compo.setLayout(layout);
		final GridData data = new GridData(SWT.FILL, SWT.CENTER, true, true);
		data.widthHint = WIDTH;
		data.heightHint = 24;
		label = FlatButton.label(compo, IGamaColors.NEUTRAL, "No experiment running", WIDTH)
				.setImage(GamaIcon.named(IGamaIcons.STATUS_CLOCK).image());
		label.setLayoutData(data);
		popup = new SimulationPopupMenu(this);
		label.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent e) {
				if (popup.isVisible()) {
					popup.hide();
				} else {
					final ITopLevelAgent agent = GAMA.getCurrentTopLevelAgent();
					if (state != IGui.ERROR && state != IGui.WAIT && agent != null && !agent.dead()
							&& !agent.getScope().isClosed() && agent.getExperiment() != null
							&& !(agent instanceof PlatformAgent)) {
						WorkbenchHelper.asyncRun(popup::display);
					}
				}

			}

		});
		return compo;
	}

	@Override
	public boolean isDisposed() { return label.isDisposed(); }

	/**
	 * Sets the selection.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param agent
	 *            the new selection
	 * @date 26 août 2023
	 */

	public void setSelection(final ITopLevelAgent agent) {
		if (agent instanceof IExperimentAgent exp) {
			GAMA.changeCurrentTopLevelAgent(exp, false);
		} else if (agent instanceof SimulationAgent sim) { GAMA.getExperiment().getAgent().setCurrentSimulation(sim); }
	}

	/**
	 * Append popup text for.
	 *
	 * @param exp
	 *            the exp
	 * @param result
	 *            the result
	 */
	void appendPopupTextFor(final ITopLevelAgent exp, final PopupText result) {
		if (exp == null) return;
		text.setLength(0);
		// text.append(Strings.LN);
		final SimulationClock clock = exp.getClock();
		clock.getInfo(text).append(Strings.LN);
		text.append("Durations: cycle ").append(clock.getDuration()).append("ms; average ")
				.append((int) clock.getAverageDuration()).append("ms; total ").append(clock.getTotalDuration())
				.append("ms");
		// text.append(Strings.LN);
		result.add(GamaColors.get(exp.getColor()), text.toString());
	}

	/**
	 * Popup text for.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param exp
	 *            the exp
	 * @return the string
	 * @date 26 août 2023
	 */
	String popupTextFor(final ITopLevelAgent exp) {
		if (exp == null) return "";
		text.setLength(0);
		// text.append(Strings.LN);
		final SimulationClock clock = exp.getClock();
		clock.getInfo(text).append(Strings.LN);
		text.append("Durations: cycle ").append(clock.getDuration()).append("ms; average ")
				.append((int) clock.getAverageDuration()).append("ms; total ").append(clock.getTotalDuration())
				.append("ms");
		// text.append(Strings.LN);
		return text.toString();
	}

	/**
	 * Gets the popup background.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the popup background
	 * @date 26 août 2023
	 */
	// @Override
	public GamaUIColor getPopupBackground() {
		if (inUserStatus && color != null) return color;
		return state == IGui.ERROR ? IGamaColors.ERROR : state == IGui.WAIT ? IGamaColors.WARNING
				: state == IGui.NEUTRAL ? IGamaColors.NEUTRAL : IGamaColors.OK;
	}

	/**
	 * Gets the controlling shell.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the controlling shell
	 * @date 26 août 2023
	 */
	// @Override
	public Shell getControllingShell() { return label.getShell(); }

	/**
	 * Gets the absolute origin.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the absolute origin
	 * @date 26 août 2023
	 */
	public Point getLocation() { return label.toDisplay(label.getLocation()); }

	/**
	 * Gets the popup width.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the popup width
	 * @date 26 août 2023
	 */
	public int getWidth() { return label.getSize().x; }

	/**
	 * Gets the height.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the height
	 * @date 26 août 2023
	 */
	public int getHeight() { return label.getSize().y; }

	/**
	 * Method updateWith()
	 *
	 * @see msi.gama.gui.swt.controls.ThreadedUpdater.IUpdaterTarget#updateWith(java.lang.Object)
	 */
	@Override
	public void updateWith(final IStatusMessage m) {
		if (isUpdating) return;
		if (GAMA.getExperiment() == null) {
			label.removeMenuSign();
			popup.wipe();
			if (popup.isVisible()) { popup.hide(); }
		} else {
			label.addMenuSign();
		}
		isUpdating = true;
		if (m instanceof SubTaskMessage) {
			if (inUserStatus) return;
			final SubTaskMessage m2 = (SubTaskMessage) m;
			final Boolean beginOrEnd = m2.getBeginOrEnd();
			if (beginOrEnd == null) {
				// completion
				subTaskCompletion = ((SubTaskMessage) m).getCompletion();
			} else {
				if (beginOrEnd) {
					// begin task
					subTaskName = m.getText();
					inSubTask = true;
				} else {
					// end task
					inSubTask = false;
				}
				subTaskCompletion = null;
			}
		} else if (m instanceof UserStatusMessage) {
			final String s = m.getText();
			if (s == null) {
				resume();
			} else {
				inSubTask = false; // in case
				inUserStatus = true;
				final java.awt.Color c = m.getColor();
				if (c == null) {
					color = null;
					state = IGui.NEUTRAL;
				} else {
					color = GamaColors.get(c);
				}
				mainTaskName = m.getText();
			}
		} else if (m instanceof StatusMessage) {
			if (inUserStatus) return;
			inSubTask = false; // in case
			mainTaskName = m.getText();
			state = m.getCode();
		}

		if (m.getIcon() != null) {
			label.setImage(GamaIcon.named(m.getIcon()).image());
		} else {
			label.setImage(null);
		}
		label.setColor(getPopupBackground());
		if (!inUserStatus && !inSubTask && mainTaskName == null) {
			label.setColor(GamaColors.get(GAMA.getCurrentTopLevelAgent().getColor()));
		}

		if (inSubTask) {
			label.setText(
					subTaskName + (subTaskCompletion != null ? " [" + (int) (subTaskCompletion * 100) + "%]" : ""));
		} else if (mainTaskName == null) {
			label.setText(getClockMessage());
		} else {
			label.setText(mainTaskName);
		}
		if (popup.isVisible()) { popup.display(); }
		isUpdating = false;
		inUserStatus = false;

	}

	/**
	 * Gets the clock message.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param agent
	 *            the agent
	 * @return the clock message
	 * @date 26 août 2023
	 */
	private String getClockMessage() {
		ITopLevelAgent agent = GAMA.getCurrentTopLevelAgent();
		if (agent == null) return "";
		if (agent instanceof PlatformAgent) {
			WorkbenchHelper.run(() -> {
				popup.wipe();
				if (popup.isVisible()) { popup.hide(); }
				label.removeMenuSign();
			});
			return "No experiment running";
		}
		text.setLength(0);
		agent.getClock().getInfo(text);
		final IExperimentAgent exp = agent.getExperiment();
		if (exp == null) return "";
		final SimulationPopulation pop = exp.getSimulationPopulation();
		final int nbThreads = pop == null ? 1 : pop.getNumberOfActiveThreads();
		if (agent.getScope().isOnUserHold()) {
			text.append(" (waiting)");
		} else if (nbThreads > 1) { text.append(" (" + nbThreads + " threads)"); }
		final IExperimentPlan plan = exp.getSpecies();
		if (plan.shouldBeBenchmarked()) { text.append(" [benchmarking]"); }
		return text.toString();
	}

	@Override
	public int getCurrentState() { return state; }

	@Override
	public boolean isDynamic() { return false; }

	/**
	 * Method resume()
	 *
	 * @see msi.gama.common.interfaces.IUpdaterTarget#resume()
	 */
	@Override
	public void resume() {
		inUserStatus = false;
		color = null;
		mainTaskName = null;
	}

}
