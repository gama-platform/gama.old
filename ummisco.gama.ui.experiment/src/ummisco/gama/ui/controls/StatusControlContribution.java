/*******************************************************************************************************
 *
 * StatusControlContribution.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.controls;

import java.awt.Color;

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
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.kernel.simulation.SimulationPopulation;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.GAMA;
import msi.gaml.operators.Strings;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.GamaIcon;
import ummisco.gama.ui.resources.IGamaColors;

/**
 * The Class StatusControlContribution.
 */
public class StatusControlContribution extends WorkbenchWindowControlContribution
		implements IPopupProvider, IUpdaterTarget<IStatusMessage> {

	/** The is updating. */
	volatile boolean isUpdating;

	/** The label. */
	FlatButton label;

	/** The popup. */
	private Popup2 popup;

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

	/** The agent index. */
	int agentIndex; // 0 for experiments, > 0 for simulation(s)

	/** The text. */
	StringBuilder text = new StringBuilder(2000);

	/** The instance. */
	static StatusControlContribution INSTANCE;

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
		label = FlatButton.label(compo, IGamaColors.NEUTRAL, "No simulation running", WIDTH);
		label.setLayoutData(data);

		label.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent e) {
				final ITopLevelAgent agent = getStatusAgent();
				if (agent == null) return;

				final IExperimentAgent exp = agent.getExperiment();
				final int all = exp.getSimulationPopulation().size() + 1;
				agentIndex++;
				if (agentIndex > all) { agentIndex = 0; }
				exp.informStatus();
			}
		});
		popup = new Popup2(this, label);
		return compo;
	}

	@Override
	public boolean isDisposed() { return label.isDisposed(); }

	/**
	 * Gets the status agent.
	 *
	 * @return the status agent
	 */
	ITopLevelAgent getStatusAgent() {
		if (agentIndex < 0) { agentIndex = 0; }
		final IExperimentPlan exp = GAMA.getExperiment();
		if (exp == null) return null;
		if (agentIndex == 0) return exp.getAgent();
		if (exp.getAgent() == null) return null;
		final SimulationPopulation pop = exp.getAgent().getSimulationPopulation();
		ITopLevelAgent agent = pop.getSimulationAtIndex(agentIndex - 1);
		if (agent == null) { agentIndex = 0; }
		return agent;

	}

	/**
	 * @see ummisco.gama.ui.controls.IPopupProvider#getPopupText()
	 */
	@Override
	public PopupText getPopupText() {
		final PopupText result = new PopupText();

		if (state == IGui.ERROR || state == IGui.WAIT) {
			final GamaUIColor color = state == IGui.ERROR ? IGamaColors.ERROR : IGamaColors.WARNING;
			result.add(color, label.getText());
			return result;
		}

		final ITopLevelAgent agent = getStatusAgent();

		if (agent == null || agent.dead() || agent.getScope().isClosed()) {
			result.add(IGamaColors.NEUTRAL, "No experiment available");
			return result;
		}
		appendPopupTextFor(agent.getExperiment(), result);

		final IPopulation<? extends IAgent> pop = agent.getExperiment().getSimulationPopulation();
		if (pop == null) {
			result.add(IGamaColors.NEUTRAL, "No simulations available");
			return result;
		}
		final IAgent[] simulations = pop.toArray();

		for (final IAgent a : simulations) { appendPopupTextFor((SimulationAgent) a, result); }

		return result;
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
		text.setLength(0);
		text.append(Strings.LN);
		final SimulationClock clock = exp.getClock();
		clock.getInfo(text).append(Strings.LN);
		text.append("Durations: cycle ").append(clock.getDuration()).append("ms; average ")
				.append((int) clock.getAverageDuration()).append("ms; total ").append(clock.getTotalDuration())
				.append("ms");
		text.append(Strings.LN);
		result.add(GamaColors.get(exp.getColor()), text.toString());
	}

	/**
	 * @see ummisco.gama.ui.controls.IPopupProvider#getPopupBackground()
	 */
	// @Override
	public GamaUIColor getPopupBackground() {
		if (inUserStatus && color != null) return color;
		return state == IGui.ERROR ? IGamaColors.ERROR : state == IGui.WAIT ? IGamaColors.WARNING
				: state == IGui.NEUTRAL ? IGamaColors.NEUTRAL : IGamaColors.OK;
	}

	@Override
	public Shell getControllingShell() { return label.getShell(); }

	@Override
	public Point getAbsoluteOrigin() { return label.toDisplay(new Point(label.getLocation().x, label.getSize().y)); }

	@Override
	public int getPopupWidth() { return label.getSize().x; }

	/**
	 * Method updateWith()
	 *
	 * @see msi.gama.gui.swt.controls.ThreadedUpdater.IUpdaterTarget#updateWith(java.lang.Object)
	 */
	@Override
	public void updateWith(final IStatusMessage m) {
		if (isUpdating) return;
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
				final Color c = m.getColor();
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

		// updater.run();
		if (m.getIcon() != null) {
			label.setImage(GamaIcon.named(m.getIcon()).image());
		} else {
			label.setImage(null);
		}
		label.setColor(getPopupBackground());
		if (!inUserStatus && !inSubTask && mainTaskName == null) {
			if (getStatusAgent() == null) {
				label.setColor(IGamaColors.NEUTRAL);
			} else {
				label.setColor(GamaColors.get(getStatusAgent().getColor()));
			}
		}

		if (inSubTask) {
			label.setText(
					subTaskName + (subTaskCompletion != null ? " [" + (int) (subTaskCompletion * 100) + "%]" : ""));
		} else {
			label.setText(mainTaskName == null ? getClockMessage(getStatusAgent()) : mainTaskName);
		}
		if (popup.isVisible()) { popup.display(); }
		isUpdating = false;
		inUserStatus = false;

	}

	/**
	 * Gets the clock message.
	 *
	 * @param agent
	 *            the agent
	 * @return the clock message
	 */
	private String getClockMessage(final ITopLevelAgent agent) {
		if (agent == null) return "";
		// final StringBuilder text = new StringBuilder(200);
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
