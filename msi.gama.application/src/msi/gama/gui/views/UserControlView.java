package msi.gama.gui.views;

import java.util.List;
import msi.gama.common.interfaces.EditorListener;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.parameters.EditorFactory;
import msi.gama.gui.swt.SwtGui;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.commands.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

public class UserControlView extends GamaViewPart {

	public static String ID = "msi.gama.views.userControlView";

	IScope scope;
	List<ICommand> userCommands;
	String title;
	Composite body, buttons;

	public void initFor(final IScope scope, final List<ICommand> userCommands, final String title) {
		this.scope = scope;
		this.userCommands = userCommands;
		this.title = title;
		if ( body != null && !body.isDisposed() ) {
			body.dispose();
			body = null;
		}
		if ( buttons != null && !buttons.isDisposed() ) {
			buttons.dispose();
			buttons = null;
		}
		createPartControl(parent);
		parent.layout();
	}

	@Override
	protected Integer[] getToolbarActionsId() {
		return new Integer[0];
	}

	@Override
	public void ownCreatePartControl(final Composite parent) {
		if ( scope == null ) { return; }
		GridLayout layout = new GridLayout(1, true);
		parent.setLayout(layout);
		body = new Composite(parent, SWT.None);
		layout = new GridLayout(3, false);
		body.setLayout(layout);
		Label text = new Label(body, SWT.None);
		text.setBackground(SwtGui.COLOR_OK);
		text.setForeground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		text.setText(title);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		text.setLayoutData(data);
		Label sep = new Label(body, SWT.SEPARATOR | SWT.HORIZONTAL);
		data = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		data.heightHint = 20;
		sep.setLayoutData(data);
		for ( final ICommand c : userCommands ) {
			if ( c instanceof UserCommandCommand ) {
				List<UserInputCommand> inputs = ((UserCommandCommand) c).getInputs();
				int nbLines = inputs.size() > 1 ? inputs.size() : 1;
				int nbCol = inputs.size() > 0 ? 1 : 3;
				Button b = new Button(body, SWT.PUSH);
				b.setText(c.getName());
				GridData gd = new GridData(SWT.LEFT, SWT.TOP, true, true, nbCol, nbLines);
				b.setLayoutData(gd);
				b.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						c.executeOn(scope);
						GAMA.getExperiment().getOutputManager().forceUpdateOutputs();
					}

				});
				for ( final UserInputCommand i : inputs ) {

					scope.addVarWithValue(i.getTempVarName(), i.value(scope));
					EditorFactory.create(body, i, new EditorListener() {

						@Override
						public void valueModified(final Object newValue)
							throws GamaRuntimeException {
							i.setValue(newValue);
							c.executeOn(scope);
						}

					});
				}

				sep = new Label(body, SWT.SEPARATOR | SWT.HORIZONTAL);
				data = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
				sep.setLayoutData(data);
			}
		}
		body.layout();
		// body.pack();
		buttons = new Composite(parent, SWT.None);
		layout = new GridLayout(2, true);
		buttons.setLayout(layout);
		Button cont = new Button(buttons, SWT.PUSH);
		cont.setText("Continue");
		cont.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				GAMA.getFrontmostSimulation().getScheduler().setUserHold(false);
				GuiUtils.hideView(ID);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				widgetSelected(e);
			}

		});
		Button inspect = new Button(buttons, SWT.PUSH);
		inspect.setText("Inspect");
		inspect.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				GAMA.getExperiment().getOutputManager().selectionChanged(scope.getAgentScope());
			}

		});
		buttons.layout();
		parent.update();
		// return composite;

	}
}
