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
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.SwtGui;
import msi.gama.kernel.experiment.*;
import msi.gama.runtime.*;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.statements.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class ExperimentParametersView extends AttributesEditorsView<String> {

	public static final String ID = GuiUtils.PARAMETER_VIEW_ID;

	private IExperimentPlan experiment;
	private Composite commandsComposite, commandsBody;
	int maxButtonWidth;

	@Override
	public void ownCreatePartControl(final Composite view) {
		super.ownCreatePartControl(view);
		final Composite intermediate = new Composite(view, SWT.NONE);
		final GridLayout parentLayout = new GridLayout(1, false);
		parentLayout.marginWidth = 0;
		parentLayout.marginHeight = 0;
		parentLayout.verticalSpacing = 0;
		intermediate.setLayout(parentLayout);
		commandsComposite = new Composite(intermediate, SWT.BORDER_SOLID);
		commandsComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 0;
		// layout.horizontalSpacing = 0;
		commandsComposite.setLayout(layout);
		Label l = new Label(commandsComposite, SWT.None);
		l.setText("Commands");
		l.setFont(SwtGui.getLabelfont());
		commandsBody = new Composite(commandsComposite, SWT.None);
		commandsBody.setBackgroundMode(SWT.INHERIT_FORCE);
		layout = new GridLayout(5, true);
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		commandsBody.setLayout(layout);
		commandsBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		// commands.pack();
		view.pack();
		view.layout();
		parent = intermediate;
	}

	public void addItem(final IExperimentPlan exp) {
		if ( exp != null /* && exp != experiment */) {
			experiment = exp;
			reset();
			editors = (EditorsList<String>) exp.getParametersEditors();
			if ( editors == null ) { return; }
			displayItems();
		} else {
			experiment = null;
		}
	}

	@Override
	public void displayItems() {
		super.displayItems();
		final Collection<UserCommandStatement> userCommands = experiment.getUserCommands();
		for ( final Control c : commandsBody.getChildren() ) {
			c.dispose();
		}
		if ( userCommands.isEmpty() ) {
			commandsComposite.setVisible(false);
			((GridData) commandsComposite.getLayoutData()).exclude = true;
			return;
		}
		((GridData) commandsComposite.getLayoutData()).exclude = false;
		commandsComposite.setVisible(true);

		for ( final IStatement command : userCommands ) {
			final Button b = new Button(commandsBody, SWT.PUSH);
			GridData data = new GridData();
			b.setLayoutData(data);
			b.setText(command.getName());
			b.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			b.pack();
			if ( b.getSize().x > maxButtonWidth ) {
				maxButtonWidth = b.getSize().x;
			}

			b.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					GAMA.getSimulation().getScheduler().executeOneAction(new GamaHelper() {

						@Override
						public Object run(final IScope scope) {
							final Object result = command.executeOn(scope);
							experiment.getSimulationOutputs().forceUpdateOutputs();
							return result;
						}

					});
				}

			});
		}
		commandsComposite.layout();
		int numColumns = commandsBody.getClientArea().width / maxButtonWidth;
		((GridLayout) commandsBody.getLayout()).numColumns = numColumns;
		commandsBody.layout();
		parent.layout();
		parent.addControlListener(new ControlListener() {

			@Override
			public void controlResized(final ControlEvent e) {
				if ( !commandsComposite.isVisible() ) { return; }
				commandsComposite.layout();
				int numColumns = commandsBody.getClientArea().width / maxButtonWidth;
				if ( numColumns == 0 ) {
					numColumns = 1;
				}
				((GridLayout) commandsBody.getLayout()).numColumns = numColumns;
				commandsBody.layout();
				parent.layout();
			}

			@Override
			public void controlMoved(final ControlEvent e) {}
		});
	}

	/**
	 * @see msi.gama.gui.views.GamaVie@Override
	 *      wPart#getToolbarActionsId()
	 */
	@Override
	public Integer[] getToolbarActionsId() {
		return new Integer[] { /* SAVE, */REVERT };
	}

	@Override
	public boolean addItem(final String object) {
		createItem(object, true);
		return true;
	}

	public IExperimentPlan getExperiment() {
		return experiment;
	}

}
