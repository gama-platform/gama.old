package msi.gama.gui.views;

import java.util.List;
import msi.gama.common.interfaces.EditorListener;
import msi.gama.gui.parameters.EditorFactory;
import msi.gama.gui.swt.SwtGui;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.architecture.user.UserInputCommand;
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

	private void deactivate(final Composite parent) {
		for ( Control c : parent.getChildren() ) {
			if ( c instanceof Composite ) {
				deactivate((Composite) c);
			} else {
				c.setEnabled(false);
			}
		}
	}

	@Override
	public void ownCreatePartControl(final Composite parent) {
		if ( scope == null ) { return; }
		setPartName(title);
		GridLayout layout = new GridLayout(1, false);
		parent.setLayout(layout);

		// Label text = new Label(body, SWT.None);
		// text.setBackground(SwtGui.COLOR_OK);
		// text.setForeground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		// text.setText(title);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		// text.setLayoutData(data);
		Label sep;
		// Label sep = new Label(body, SWT.SEPARATOR | SWT.HORIZONTAL);
		// data = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		// data.heightHint = 20;
		// sep.setLayoutData(data);
		buttons = new Composite(parent, SWT.BORDER);
		layout = new GridLayout(3, false);
		buttons.setLayout(layout);
		data = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		buttons.setLayoutData(data);
		Label title = new Label(buttons, SWT.None);
		title.setText("Control Panel on Agent " + scope.getAgentScope().getName());
		title.setFont(SwtGui.bigFont);
		Button inspect = new Button(buttons, SWT.PUSH);
		inspect.setText("Inspect");
		inspect.setImage(SwtGui.panel_inspect);
		inspect.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				GAMA.getExperiment().getOutputManager().selectionChanged(scope.getAgentScope());
			}

		});
		Button cont = new Button(buttons, SWT.PUSH);
		cont.setText("Continue");
		cont.setImage(SwtGui.panel_continue);
		cont.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				GAMA.getFrontmostSimulation().getScheduler().setUserHold(false);
				deactivate(parent);
				// GuiUtils.hideView(ID);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				widgetSelected(e);
			}

		});
		body = new Composite(parent, SWT.None);
		layout = new GridLayout(3, false);
		body.setLayout(layout);
		for ( final ICommand c : userCommands ) {
			if ( c instanceof UserCommandCommand ) {
				Group commandComposite = new Group(body, SWT.SHADOW_IN);
				data = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
				commandComposite.setLayoutData(data);
				layout = new GridLayout(3, false);
				commandComposite.setLayout(layout);
				commandComposite.setText("   "/* c.getName() */);
				// commandComposite.setBackground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
				// sep = new Label(body, SWT.SEPARATOR | SWT.HORIZONTAL);
				// data = new GridData(SWT.FILL, SWT.CENTER, true, true, 3, 1);
				// data.heightHint = 20;
				// sep.setLayoutData(data);
				List<UserInputCommand> inputs = ((UserCommandCommand) c).getInputs();
				int nbLines = inputs.size() > 1 ? inputs.size() : 1;
				int nbCol = inputs.size() > 0 ? 1 : 3;
				Button b = new Button(commandComposite, SWT.PUSH);
				b.setText(c.getName());
				b.setImage(SwtGui.panel_action);
				b.setEnabled(((UserCommandCommand) c).isEnabled(scope));
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
					EditorFactory.create(commandComposite, i, new EditorListener() {

						@Override
						public void valueModified(final Object newValue)
							throws GamaRuntimeException {
							i.setValue(newValue);
							c.executeOn(scope);
						}

					});
				}

			}
		}
		// sep = new Label(body, SWT.SEPARATOR | SWT.HORIZONTAL);
		// data = new GridData(SWT.FILL, SWT.CENTER, true, true, 3, 1);
		// data.heightHint = 20;
		// sep.setLayoutData(data);
		// body.layout();
		// body.pack();

		// buttons.layout();
		// parent.update();
		// return composite;

	}

	@Override
	public void update(final IDisplayOutput output) {
		initFor(scope, userCommands, title);
	}

}
