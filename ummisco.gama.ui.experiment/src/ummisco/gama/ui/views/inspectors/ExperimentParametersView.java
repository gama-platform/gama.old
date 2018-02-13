/*********************************************************************************************
 *
 * 'ExperimentParametersView.java, in plugin ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling
 * and simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.views.inspectors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import msi.gama.common.interfaces.IGamaView;
import msi.gama.common.interfaces.IGui;
import msi.gama.kernel.experiment.IExperimentDisplayable;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.runtime.GAMA;
import ummisco.gama.ui.experiment.parameters.EditorsList;
import ummisco.gama.ui.experiment.parameters.ExperimentsParametersList;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;

public class ExperimentParametersView extends AttributesEditorsView<String> implements IGamaView.Parameters {

	public static final String ID = IGui.PARAMETER_VIEW_ID;
	public final static int REVERT = 0;
	private IExperimentPlan experiment;

	@Override
	public void ownCreatePartControl(final Composite view) {
		final Composite intermediate = new Composite(view, SWT.NONE);
		intermediate.setBackground(view.getBackground());
		final GridLayout parentLayout = new GridLayout(1, false);
		parentLayout.marginWidth = 0;
		parentLayout.marginHeight = 0;
		parentLayout.verticalSpacing = 5;
		intermediate.setLayout(parentLayout);
		view.pack();
		view.layout();
		setParentComposite(intermediate);
	}

	@Override
	public void addItem(final IExperimentPlan exp) {
		if (exp != null) {
			experiment = exp;
			if (!exp.hasParametersOrUserCommands()) { return; }
			reset();
			final List<IExperimentDisplayable> params = new ArrayList<>(exp.getParameters().values());
			params.addAll(exp.getExplorableParameters().values());
			params.addAll(exp.getUserCommands());
			params.sort(null);
			editors = new ExperimentsParametersList(exp.getAgent().getScope(), params);
			final String expInfo = "Model " + experiment.getModel().getDescription().getTitle() + " / "
					+ StringUtils.capitalize(experiment.getDescription().getTitle());
			this.setPartName(expInfo);
			displayItems();
		} else {
			experiment = null;
		}
	}

	// @Override
	// public void displayItems() {
	// super.displayItems();
	// // this.displayCommands();
	// }

	// protected void displayCommands() {
	// toolbar.wipe(SWT.LEFT, true);
	// final Collection<UserCommandStatement> userCommands = experiment.getUserCommands();
	// for (final UserCommandStatement command : userCommands) {
	// GamaUIColor color = GamaColors.get(command.getColor(GAMA.getRuntimeScope()));
	// if (color == null)
	// color = IGamaColors.BLUE;
	// toolbar.button(color, command.getName(), new SelectionAdapter() {
	//
	// @Override
	// public void widgetSelected(final SelectionEvent e) {
	//
	// GAMA.getExperiment().getAgent().executeAction(scope -> {
	// final Object result = command.executeOn(scope);
	// GAMA.getExperiment().refreshAllOutputs();
	// return result;
	// });
	// }
	//
	// }, SWT.LEFT);
	// toolbar.sep(2, SWT.LEFT);
	// }
	// toolbar.refresh(true);
	//
	// }

	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		super.createToolItems(tb);
		tb.button(GamaIcons.create(IGamaIcons.ACTION_REVERT).getCode(), "Revert parameter values",
				"Revert parameters to their initial values", e -> {
					final EditorsList<?> eds = editors;
					if (eds != null) {
						eds.revertToDefaultValue();
					}
				}, SWT.RIGHT);
		tb.button("menu.add2", "Add simulation",
				"Add a new simulation (with the current parameters) to this experiment",
				e -> GAMA.getExperiment().getAgent().createSimulation(new ParametersSet(), true), SWT.RIGHT);

	}

	@Override
	public boolean addItem(final String object) {
		createItem(getParentComposite(), object, true, null);
		return true;
	}

	public IExperimentPlan getExperiment() {
		return experiment;
	}

	@Override
	public void stopDisplayingTooltips() {
		// displayCommands();
	}

	@Override
	protected GamaUIJob createUpdateJob() {
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
