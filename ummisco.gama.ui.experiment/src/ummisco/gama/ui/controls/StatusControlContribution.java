/*********************************************************************************************
 *
 * 'StatusControlContribution.java, in plugin ummisco.gama.ui.experiment, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
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
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.GAMA;
import msi.gaml.operators.Dates;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;

public class StatusControlContribution extends WorkbenchWindowControlContribution
		implements IPopupProvider, IUpdaterTarget<IStatusMessage> {

	volatile boolean isUpdating;
	FlatButton label;
	private Popup popup;
	int state;
	volatile String mainTaskName;
	volatile String subTaskName;
	volatile boolean inSubTask = false;
	volatile boolean inUserStatus = false;
	volatile Double subTaskCompletion;
	private final static int WIDTH = 400;
	private GamaUIColor color;
	int agentIndex; // 0 for experiments, > 0 for simulation(s)

	static StatusControlContribution INSTANCE;

	public static StatusControlContribution getInstance() {
		return INSTANCE;
	}

	public StatusControlContribution() {
		INSTANCE = this;
	}

	public StatusControlContribution(final String id) {
		super(id);
		INSTANCE = this;
	}

	@Override
	protected int computeWidth(final Control control) {
		return WIDTH;
	}

	@Override
	public boolean isBusy() {
		return isUpdating;
	}

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
		label = FlatButton.label(compo, IGamaColors.NEUTRAL, "No simulation running");
		label.setLayoutData(data);

		label.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(final MouseEvent e) {
				final ITopLevelAgent agent = getStatusAgent();
				if (agent == null)
					return;

				final IExperimentAgent exp = agent.getExperiment();
				final int all = exp.getSimulationPopulation().size() + 1;
				agentIndex++;
				if (agentIndex > all)
					agentIndex = 0;
				exp.informStatus();
			}
		});
		popup = new Popup(this, label);
		return compo;
	}

	@Override
	public boolean isDisposed() {
		return label.isDisposed();
	}

	private ITopLevelAgent getStatusAgent() {
		if (agentIndex < 0)
			agentIndex = 0;
		final IExperimentPlan exp = GAMA.getExperiment();
		if (exp == null)
			return null;
		// final ITopLevelAgent agent;
		if (agentIndex == 0) {
			return exp.getAgent();
		}
		if (exp.getAgent() == null)
			return null;
		final IPopulation<? extends IAgent> pop = exp.getAgent().getSimulationPopulation();
		if (pop.isEmpty())
			return null;
		final IAgent[] simulations = pop.toArray();
		if (agentIndex > simulations.length) {
			agentIndex = 0;
			return exp.getAgent();
		}
		return (ITopLevelAgent) simulations[agentIndex - 1];
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

		if (agent == null) {
			result.add(IGamaColors.NEUTRAL, "No experiment available");
			return result;
		}
		final IExperimentAgent exp = agent.getExperiment();

		final StringBuilder sb = new StringBuilder(300);
		SimulationClock clock = exp.getClock();
		sb.append(String.format("%-20s %-10d\n", "Experiment cycles elapsed: ", clock.getCycle()));
		sb.append(String.format("%-20s cycle %5d; average %5d; total %10d", "Duration (ms)", clock.getDuration(),
				(int) clock.getAverageDuration(), clock.getTotalDuration()));
		result.add(GamaColors.get(exp.getColor()), sb.toString());
		final IPopulation<? extends IAgent> pop = exp.getSimulationPopulation();
		if (pop == null) {
			result.add(IGamaColors.NEUTRAL, "No simulations available");
			return result;
		}
		final IAgent[] simulations = pop.toArray();

		for (final IAgent a : simulations) {
			sb.setLength(0);
			final SimulationAgent sim = (SimulationAgent) a;
			clock = sim.getClock();

			sb.append(String.format("%-20s %-10d\tSimulated time %-30s\n", "Cycles elapsed: ", clock.getCycle(),
					Dates.asDuration(clock.getStartingDate(), clock.getCurrentDate())));
			sb.append(String.format("%-20s cycle %5d; average %5d; total %10d", "Duration (ms)", clock.getDuration(),
					(int) clock.getAverageDuration(), clock.getTotalDuration()));
			result.add(GamaColors.get(sim.getColor()), sb.toString());

		}

		return result;
	}

	/**
	 * @see ummisco.gama.ui.controls.IPopupProvider#getPopupBackground()
	 */
	// @Override
	public GamaUIColor getPopupBackground() {
		if (inUserStatus && color != null) {
			return color;
		}
		return state == IGui.ERROR ? IGamaColors.ERROR
				: state == IGui.WAIT ? IGamaColors.WARNING
						: state == IGui.NEUTRAL ? IGamaColors.NEUTRAL : IGamaColors.OK;
	}

	@Override
	public Shell getControllingShell() {
		return label.getShell();
	}

	@Override
	public Point getAbsoluteOrigin() {
		return label.toDisplay(new Point(label.getLocation().x, label.getSize().y));
	}

	/**
	 * Method updateWith()
	 * 
	 * @see msi.gama.gui.swt.controls.ThreadedUpdater.IUpdaterTarget#updateWith(java.lang.Object)
	 */
	@Override
	public void updateWith(final IStatusMessage m) {
		if (isUpdating) {
			return;
		}
		isUpdating = true;
		if (m instanceof SubTaskMessage) {
			if (inUserStatus) {
				return;
			}
			final SubTaskMessage m2 = (SubTaskMessage) m;
			final Boolean beginOrEnd = m2.getBeginOrEnd();
			if (beginOrEnd == null) {
				// completion
				subTaskCompletion = ((SubTaskMessage) m).getCompletion();
			} else if (beginOrEnd) {
				// begin task
				subTaskName = m.getText();
				inSubTask = true;
				subTaskCompletion = null;
			} else {
				// end task
				inSubTask = false;
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
			if (inUserStatus) {
				return;
			}
			inSubTask = false; // in case
			mainTaskName = m.getText();
			state = m.getCode();
		}

		// updater.run();
		if (m.getIcon() != null) {
			label.setImage(GamaIcons.create(m.getIcon()).image());
		} else {
			label.setImage(null);
		}
		label.setColor(getPopupBackground());
		if (!inUserStatus && !inSubTask && mainTaskName == null) {
			if (getStatusAgent() == null)
				label.setColor(IGamaColors.NEUTRAL);
			else
				label.setColor(GamaColors.get(getStatusAgent().getColor()));
		}

		if (inSubTask) {
			label.setText(
					subTaskName + (subTaskCompletion != null ? " [" + (int) (subTaskCompletion * 100) + "%]" : ""));
		} else {
			label.setText(mainTaskName == null ? getClockMessage() : mainTaskName);
		}
		if (popup.isVisible()) {
			popup.display();
		}
		isUpdating = false;
		inUserStatus = false;

	}

	private String getClockMessage() {
		final ITopLevelAgent agent = getStatusAgent();
		if (agent == null)
			return "";
		final StringBuilder sb = new StringBuilder(200);
		sb.append(agent.getClock().getInfo());
		final IExperimentAgent exp = agent.getExperiment();
		final int nbThreads = exp.getSimulationPopulation().getNumberOfActiveThreads();
		if (agent.getScope().isOnUserHold())
			sb.append(" (waiting)");
		else if (nbThreads > 1)
			sb.append(" (" + nbThreads + " threads)");
		return sb.toString();
	}

	@Override
	public int getCurrentState() {
		return state;
	}

	@Override
	public boolean isDynamic() {
		return false;
	}

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
