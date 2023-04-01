/*******************************************************************************************************
 *
 * ExperimentParametersView.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.views.inspectors;

import static msi.gama.common.preferences.GamaPreferences.Displays.CORE_DISPLAY_LAYOUT;
import static ummisco.gama.ui.resources.IGamaIcons.ACTION_REVERT;
import static ummisco.gama.ui.resources.IGamaIcons.MENU_ADD_MONITOR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.kernel.experiment.IExperimentDisplayable;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.outputs.MonitorOutput;
import msi.gama.outputs.SimulationOutputManager;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gaml.constants.GamlCoreConstants;
import ummisco.gama.dev.utils.COUNTER;
import ummisco.gama.ui.commands.ArrangeDisplayViews;
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

/**
 * The Class ExperimentParametersView.
 */
public class ExperimentParametersView extends AttributesEditorsView<String> implements IGamaView.Parameters {

	/** The Constant MONITOR_CATEGORY. */
	private static final String MONITOR_SECTION_NAME = "Monitors";

	/** The Constant ID. */
	public static final String ID = IGui.PARAMETER_VIEW_ID;

	/** The Constant REVERT. */
	public final static int REVERT = 0;

	/** The experiment. */
	private IExperimentPlan experiment;

	/** The monitor section. */
	ParameterExpandItem monitorSection;

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

	/**
	 * Display items.
	 */
	@Override
	public void displayItems() {
		super.displayItems();
		createMonitorSectionIfNeeded(false);
		final Map<MonitorOutput, MonitorDisplayer> monitors = getEditorsList().getMonitors();
		monitors.forEach((mo, md) -> {
			md.createControls((EditorsGroup) monitorSection.getControl());
			md.setCloser(() -> deleteMonitor(md));
		});
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
		MonitorDisplayer md = getEditorsList().addMonitor(m);
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

	@Override
	public void setExperiment(final IExperimentPlan exp) {
		if (exp != null) {
			experiment = exp;
			if (!exp.hasParametersOrUserCommands()) return;
			reset();
			final List<IExperimentDisplayable> params = new ArrayList<>(exp.getDisplayables());
			// params.addAll(exp.getExplorableParameters().values());
			params.addAll(exp.getUserCommands());
			// params.addAll(exp.getTexts());
			SimulationAgent sim = exp.getCurrentSimulation();
			if (GamaPreferences.Runtime.CORE_MONITOR_PARAMETERS.getValue() && sim != null) {
				SimulationOutputManager som = sim.getOutputManager();
				if (som != null) { params.addAll(som.getMonitors()); }
			}
			Collections.sort(params);
			editors = new ExperimentsParametersList(exp.getAgent().getScope(), params);
			final String expInfo = "Model " + experiment.getModel().getDescription().getTitle() + " / "
					+ StringUtils.capitalize(experiment.getDescription().getTitle());
			this.setPartName(expInfo);
			displayItems();
		} else {
			experiment = null;
		}
	}

	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		super.createToolItems(tb);
		tb.button(ACTION_REVERT, "Revert parameter values", "Revert parameters to their initial values", e -> {
			final EditorsList<?> eds = editors;
			if (eds != null) { eds.revertToDefaultValue(); }
		}, SWT.RIGHT);
		if (GamaPreferences.Runtime.CORE_MONITOR_PARAMETERS.getValue()) {
			tb.button(MENU_ADD_MONITOR, "Add new monitor", "Add new monitor", e -> createNewMonitor(), SWT.RIGHT);
			tb.sep(SWT.RIGHT);
		}

		tb.button(IGamaIcons.ADD_SIMULATION, "Add simulation",
				"Add a new simulation (with the current parameters) to this experiment", e -> {
					final SimulationAgent sim =
							GAMA.getExperiment().getAgent().createSimulation(new ParametersSet(), true);
					if (sim == null) return;
					WorkbenchHelper.runInUI("", 0, m -> {
						if ("None".equals(CORE_DISPLAY_LAYOUT.getValue())) {
							ArrangeDisplayViews.execute(GamlCoreConstants.split);
						} else {
							ArrangeDisplayViews
									.execute(GamaPreferences.Displays.LAYOUTS.indexOf(CORE_DISPLAY_LAYOUT.getValue()));
						}
					});
				}, SWT.RIGHT);

	}

	@Override
	public boolean addItem(final String object) {
		if (GamaPreferences.Runtime.CORE_MONITOR_PARAMETERS.getValue() && MONITOR_SECTION_NAME.equals(object)) {
			createMonitorSectionIfNeeded(true);
			return true;
		}
		createItem(getParentComposite(), object, editors.getItemExpanded(object),
				GamaColors.get(editors.getItemDisplayColor(object)));
		return true;
	}

	/**
	 * Gets the experiment.
	 *
	 * @return the experiment
	 */
	public IExperimentPlan getExperiment() { return experiment; }

	@Override
	public void stopDisplayingTooltips() {
		toolbar.wipe(SWT.LEFT, true);
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

	/**
	 * Method handleMenu()
	 *
	 * @see msi.gama.common.interfaces.ItemList#handleMenu(java.lang.Object, int, int)
	 */
	@Override
	public Map<String, Runnable> handleMenu(final String data, final int x, final int y) {
		return null;
	}

}
