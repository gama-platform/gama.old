/*********************************************************************************************
 *
 *
 * 'ExperimentParametersView.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.views;

import java.util.Collection;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolItem;
import msi.gama.common.interfaces.IGui;
import msi.gama.gui.swt.GamaColors;
import msi.gama.gui.swt.GamaColors.GamaUIColor;
import msi.gama.gui.swt.IGamaColors;
import msi.gama.gui.swt.IGamaIcons;
import msi.gama.gui.swt.controls.GamaToolbar2;
import msi.gama.kernel.experiment.EditorsList;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gaml.statements.IExecutable;
import msi.gaml.statements.UserCommandStatement;

public class ExperimentParametersView extends AttributesEditorsView<String> {

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
		parent = intermediate;
	}

	public void addItem(final IExperimentPlan exp) {
		if ( exp != null /* && exp != experiment */ ) {
			experiment = exp;
			reset();
			editors = (EditorsList<String>) exp.getParametersEditors();
			if ( editors == null && exp.getUserCommands().isEmpty() ) { return; }
			final String expInfo = "Model " + experiment.getModel().getDescription().getTitle() + " / " +
				StringUtils.capitalize(experiment.getDescription().getTitle());
			this.setPartName(expInfo);
			displayItems();
		} else {
			experiment = null;
		}
	}

	// @Override
	// public/* final */void createPartControl(final Composite composite) {
	// super.createPartControl(composite);
	// }

	@Override
	public void displayItems() {
		super.displayItems();
		this.displayCommands();
	}

	protected void displayCommands() {
		toolbar.wipe(SWT.LEFT, true);
		final Collection<UserCommandStatement> userCommands = experiment.getUserCommands();
		//
		// String expInfo = "Model " + experiment.getModel().getDescription().getTitle() + " / " +
		// StringUtils.capitalize(experiment.getDescription().getTitle());
		// toolbar.status((Image) null, expInfo, IGamaColors.NEUTRAL, SWT.LEFT);
		// toolbar.sep(2, SWT.LEFT);
		for ( final UserCommandStatement command : userCommands ) {
			GamaUIColor color = GamaColors.get(command.getColor(GAMA.getRuntimeScope()));
			if ( color == null )
				color = IGamaColors.BLUE;
			final ToolItem f = toolbar.button(color, command.getName(), new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {

					GAMA.getExperiment().getAgent().executeAction(new IExecutable() {

						@Override
						public Object executeOn(final IScope scope) {
							final Object result = command.executeOn(scope);
							GAMA.getExperiment().refreshAllOutputs();
							return result;
						}

					});
				}

			}, SWT.LEFT);
			toolbar.sep(2, SWT.LEFT);
		}
		toolbar.refresh(true);

	}

	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		super.createToolItems(tb);
		tb.button(IGamaIcons.ACTION_REVERT.getCode(), "Revert parameter values",
			"Revert parameters to their initial values", new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					final EditorsList eds = (EditorsList) GAMA.getExperiment().getParametersEditors();
					if ( eds != null ) {
						eds.revertToDefaultValue();
					}
				}

			}, SWT.RIGHT);
		tb.button("menu.reload4", "Reload experiment", "Reload experiment with the current parameters",
			new SelectionListener() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					GAMA.reloadFrontmostExperiment();
				}

				@Override
				public void widgetDefaultSelected(final SelectionEvent e) {
					widgetSelected(e);
				}
			}, SWT.RIGHT);
		tb.button("menu.add2", "Add simulation",
			"Add a new simulation (with the current parameters) to this experiment", new SelectionListener() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					GAMA.getExperiment().getAgent().createSimulation(new ParametersSet(), true);
				}

				@Override
				public void widgetDefaultSelected(final SelectionEvent e) {
					widgetSelected(e);
				}
			}, SWT.RIGHT);

	}

	@Override
	public boolean addItem(final String object) {
		createItem(parent, object, true, null);
		return true;
	}

	public IExperimentPlan getExperiment() {
		return experiment;
	}

	@Override
	public void stopDisplayingTooltips() {
		displayCommands();
	}

	@Override
	protected GamaUIJob createUpdateJob() {
		return null;
	}

	/**
	 * Method handleMenu()
	 * @see msi.gama.common.interfaces.ItemList#handleMenu(java.lang.Object, int, int)
	 */
	@Override
	public Map<String, Runnable> handleMenu(final String data, final int x, final int y) {
		return null;
	}

}
