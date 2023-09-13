/*******************************************************************************************************
 *
 * ExperimentParametersView.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.views.inspectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolItem;

import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.outputs.MonitorOutput;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import ummisco.gama.dev.utils.COUNTER;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.controls.ParameterExpandItem;
import ummisco.gama.ui.experiment.parameters.EditorsList;
import ummisco.gama.ui.experiment.parameters.ExperimentsParametersList;
import ummisco.gama.ui.interfaces.IParameterEditor;
import ummisco.gama.ui.parameters.EditorsGroup;
import ummisco.gama.ui.parameters.MonitorDisplayer;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;
import ummisco.gama.ui.views.toolbar.Selector;

/**
 * The Class ExperimentParametersView.
 */
public class ExperimentParametersView extends AttributesEditorsView<String> implements IGamaView.Parameters {

	static {
		DEBUG.ON();
	}

	/**
	 * Instantiates a new experiment parameters view.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 14 août 2023
	 */
	public ExperimentParametersView() {
		GAMA.registerTopLevelAgentChangeListener(this);
	}

	/** The Constant MONITOR_CATEGORY. */
	private static final String MONITOR_SECTION_NAME = "Monitors";

	/** The Constant ID. */
	public static final String ID = IGui.PARAMETER_VIEW_ID;

	/** The Constant REVERT. */
	public final static int REVERT = 0;

	/** 'agent' represents the "real" listening agent, which can be an experiment or a simulation */
	ITopLevelAgent agent;

	/** The monitor section. */
	ParameterExpandItem monitorSection;

	/** The status. */
	ToolItem status;

	@Override
	public void ownCreatePartControl(final Composite view) {
		final Composite intermediate = new Composite(view, SWT.NONE);
		final GridLayout parentLayout = new GridLayout(1, false);
		parentLayout.marginWidth = 0;
		parentLayout.marginHeight = 0;
		parentLayout.verticalSpacing = 5;
		intermediate.setLayout(parentLayout);
		setParentComposite(intermediate);
	}

	/**
	 * Gets the editors list.
	 *
	 * @return the editors list
	 */
	ExperimentsParametersList getEditorsList() { return (ExperimentsParametersList) editors; }

	@Override
	public List<String> getItems() {
		if (!(editors instanceof ExperimentsParametersList eds)) return Collections.EMPTY_LIST;
		return eds.getItems();
	}

	/**
	 * Display items.
	 */
	@Override
	public void displayItems() {
		WorkbenchHelper.run(() -> {
			super.displayItems();
			createMonitorSectionIfNeeded(false);
			final Map<MonitorOutput, MonitorDisplayer> monitors = getEditorsList().getMonitors();
			monitors.forEach((mo, md) -> {
				md.createControls((EditorsGroup) monitorSection.getControl());
				md.setCloser(() -> deleteMonitor(md));
			});
		});
		updateToolbar();
	}

	/**
	 * Update toolbar.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 13 août 2023
	 */
	private void updateToolbar() {
		ITopLevelAgent a = GAMA.getCurrentTopLevelAgent();
		if (a != null) {
			WorkbenchHelper.asyncRun(() -> {
				if (toolbar != null && !toolbar.isDisposed()) {
					toolbar.status(null, "Parameters for " + a.getFamilyName() + " " + a.getName(),
							GamaColors.get(a.getColor()), SWT.LEFT);
					toolbar.setBackgroundColor(GamaColors.toSwtColor(a.getColor()));
				}
			});
		} else {
			WorkbenchHelper.asyncRun(() -> {
				if (toolbar != null && !toolbar.isDisposed()) {
					toolbar.wipe(SWT.LEFT, true);
					toolbar.setBackgroundColor(null);
					FlatButton button = (FlatButton) status.getControl();
					button.setColor(GamaColors.get(toolbar.getBackground()));
				}
			});
		}
	}

	/**
	 * Delete monitor section if empty.
	 */
	private void deleteMonitorSectionIfEmpty() {
		if (monitorSection == null || getEditorsList().hasMonitors()
				|| getEditorsList().getItems().contains(MONITOR_SECTION_NAME))
			return;
		monitorSection.dispose();
		monitorSection = null;
	}

	/**
	 * Creates the monitor section if needed.
	 *
	 * @param aMonitorIsAboutToBeCreated
	 *            the a monitor is about to be created
	 */
	private void createMonitorSectionIfNeeded(final boolean aMonitorIsAboutToBeCreated) {
		if (monitorSection != null || !GamaPreferences.Runtime.CORE_MONITOR_PARAMETERS.getValue()
				|| !aMonitorIsAboutToBeCreated && !getEditorsList().hasMonitors())
			return;
		final EditorsGroup compo = (EditorsGroup) createItemContentsFor(MONITOR_SECTION_NAME);
		Composite parent = getParentComposite();
		boolean isExpanded = editors.getItemExpanded(MONITOR_SECTION_NAME);
		GamaUIColor color = GamaColors.get(editors.getItemDisplayColor(MONITOR_SECTION_NAME));

		monitorSection =
				createItem(parent, MONITOR_SECTION_NAME, MONITOR_SECTION_NAME, compo, getViewer(), isExpanded, color);
	}

	/**
	 * Creates the new monitor.
	 */
	private void createNewMonitor() {
		createMonitorSectionIfNeeded(true);
		IScope scope = GAMA.getRuntimeScope();
		MonitorOutput m = new MonitorOutput(scope, "Monitor " + COUNTER.COUNT(), null);
		MonitorDisplayer md = getEditorsList().addMonitor(GAMA.getCurrentTopLevelAgent().getScope(), m);
		md.createControls((EditorsGroup) monitorSection.getControl());
		monitorSection.setHeight(monitorSection.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		md.setCloser(() -> deleteMonitor(md));
	}

	/**
	 * Delete monitor.
	 *
	 * @param md
	 *            the md
	 */
	private void deleteMonitor(final MonitorDisplayer md) {
		MonitorOutput mo = md.getStatement();
		mo.close();
		getEditorsList().removeMonitor(mo);
		md.dispose();
		monitorSection.setHeight(monitorSection.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		deleteMonitorSectionIfEmpty();
	}

	@SuppressWarnings ("unchecked")
	@Override
	protected Composite createItemContentsFor(final String cat) {
		final Map<String, IParameterEditor<?>> parameters = editors.getSections().get(cat);
		createViewer(getParentComposite());
		final EditorsGroup compo = new EditorsGroup(getViewer());
		if (parameters != null) {
			final List<IParameterEditor> list = new ArrayList<>(parameters.values());
			Collections.sort(list);
			for (final IParameterEditor<?> gpParam : list) {
				gpParam.createControls(compo);
				if (!editors.isEnabled(gpParam)) { gpParam.setActive(false); }
			}
		}

		return compo;
	}

	/**
	 * Sets the listening agent.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param experiment
	 *            the experiment
	 * @param previous
	 *            the previous
	 * @param current
	 *            the current
	 * @date 14 août 2023
	 */
	@Override
	public void topLevelAgentChanged(final ITopLevelAgent newTopLevelAgent) {

		// DEBUG.OUT("Settin' topLevelAgent to : " + (newTopLevelAgent == null ? "null"
		// : newTopLevelAgent.getFamilyName() + " " + newTopLevelAgent.getName()));

		if (newTopLevelAgent == null || newTopLevelAgent.isPlatform()) {
			agent = null;
			reset();
			updateToolbar();
			return;
		}

		if (newTopLevelAgent.isSimulation()) {
			SimulationAgent newSimulation = (SimulationAgent) newTopLevelAgent;
			if (agent == null || agent.isPlatform() || !agent.getExperiment().getSpecies().isBatch()) {
				// Platform ==> Simulation
				reset();
				agent = newSimulation;
				editors = new ExperimentsParametersList(newSimulation);
				getEditorsList().setItemValues(newSimulation.getExternalInits());
				displayItems();
				return;
			}
			if (agent.isSimulation()) {
				// Simulation ==> Simulation
				saveParameterValuesForCurrentAgent();
				agent = newSimulation;
				getEditorsList().setItemValues(newSimulation.getExternalInits());
				getEditorsList().updateItemValues(false);
				updateToolbar();
				return;
			}
			if (agent.isExperiment()) {
				// Experiment ==> Simulation
				agent = newSimulation;
				getEditorsList().updateItemValues(false);
				if (getEditorsList().hasMonitors()) { createMonitorSectionIfNeeded(false); }
				updateToolbar();
				return;
			}
		}

		if (newTopLevelAgent.isExperiment()) {
			IExperimentAgent newExperiment = (IExperimentAgent) newTopLevelAgent;
			if (agent == newExperiment || !newExperiment.hasParametersOrUserCommands()) return;
			if (monitorSection != null) {
				WorkbenchHelper.run(() -> monitorSection.dispose());
				monitorSection = null;
			}
			if (agent != null && agent.isSimulation()) {
				// Simulation ==> Experiment
				saveParameterValuesForCurrentAgent();
				agent = newExperiment;
				getEditorsList().updateItemValues(false);
				updateToolbar();
				return;
			}
			// Platform/Experiment ==> Experiment
			agent = newExperiment;
			reset();
			editors = new ExperimentsParametersList(newExperiment);
			final String expInfo = "Model " + newExperiment.getModel().getDescription().getTitle() + " / "
					+ StringUtils.capitalize(newExperiment.getSpecies().getDescription().getTitle());
			this.setPartName(expInfo);
			displayItems();
		}

	}

	/**
	 * Save parameter values for current simulation.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 12 août 2023
	 */
	private void saveParameterValuesForCurrentAgent() {
		if (!(agent instanceof SimulationAgent sim)) return;
		sim.setExternalInits(getEditorsList().getItemValues());
		DEBUG.OUT("Saving " + sim.getName() + " " + sim.getExternalInits());
	}

	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		super.createToolItems(tb);
		GridData data = (GridData) tb.getToolbar(SWT.LEFT).getLayoutData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = SWT.FILL;
		status = toolbar.button(GamaColors.get(toolbar.getBackground()), "", (Selector) null, SWT.LEFT);

		if (GAMA.getExperiment() == null || GAMA.getExperiment().isBatch()) return;
		tb.button(IGamaIcons.ACTION_REVERT, "Revert parameter values", "Revert parameters to their initial values",
				e -> {
					final EditorsList<?> eds = editors;
					if (eds != null) { eds.revertToDefaultValue(); }
				}, SWT.RIGHT);
		if (GamaPreferences.Runtime.CORE_MONITOR_PARAMETERS.getValue()) {
			tb.button(IGamaIcons.MENU_ADD_MONITOR, "Add new monitor", "Add new monitor", e -> createNewMonitor(),
					SWT.RIGHT);
			tb.sep(SWT.RIGHT);
		}

	}

	@Override
	public boolean addItem(final String object) {
		if (GamaPreferences.Runtime.CORE_MONITOR_PARAMETERS.getValue()
				&& ExperimentParametersView.MONITOR_SECTION_NAME.equals(object)) {
			createMonitorSectionIfNeeded(true);
			return true;
		}
		createItem(getParentComposite(), object, editors.getItemExpanded(object),
				GamaColors.get(editors.getItemDisplayColor(object)));
		return true;
	}

	@Override
	protected GamaUIJob createUpdateJob() {
		ExperimentsParametersList editorsList = getEditorsList();
		if (editorsList != null && GamaPreferences.Runtime.CORE_MONITOR_PARAMETERS.getValue()
				&& editorsList.hasMonitors())
			return new GamaUIJob() {

				@Override
				protected UpdatePriority jobPriority() {
					return UpdatePriority.LOW;
				}

				@Override
				public IStatus runInUIThread(final IProgressMonitor monitor) {
					if (!isOpen) return Status.CANCEL_STATUS;
					if (getViewer() != null && !getViewer().isDisposed()) {
						((ExperimentsParametersList) editors).updateMonitors(GAMA.isSynchronized());
					}
					return Status.OK_STATUS;
				}
			};

		return null;
	}

	@Override
	protected boolean needsOutput() {
		return false;
	}

	@Override
	public void dispose() {
		GAMA.removeTopLevelAgentChangeListener(this);
		super.dispose();
	}

}
