/*******************************************************************************************************
 *
 * UserControlDialog.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.views.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gaml.architecture.user.UserInputStatement;
import msi.gaml.architecture.user.UserPanelStatement;
import msi.gaml.statements.IStatement;
import msi.gaml.statements.UserCommandStatement;
import ummisco.gama.ui.dialogs.AbstractDetailsDialog;
import ummisco.gama.ui.experiment.parameters.AgentAttributesEditorsList;
import ummisco.gama.ui.interfaces.IParameterEditor;
import ummisco.gama.ui.parameters.AbstractEditor;
import ummisco.gama.ui.parameters.EditorFactory;
import ummisco.gama.ui.parameters.EditorsGroup;
import ummisco.gama.ui.resources.GamaIcon;
import ummisco.gama.ui.resources.IGamaIcons;

/**
 * The class EditorsDialog.
 *
 * @author drogoul
 * @since 10 mai 2012
 *
 */
public class UserControlDialog extends AbstractDetailsDialog {

	/**
	 * The Class PreviousDialog.
	 */
	public static class PreviousDialog {

		/** The location. */
		final Point location;

		/** The extent. */
		final Point extent;

		/** The toggled. */
		final boolean toggled;

		/** The name. */
		final String name;

		/**
		 * Instantiates a new previous dialog.
		 *
		 * @param d
		 *            the d
		 */
		PreviousDialog(final UserControlDialog d) {
			location = d.getShell().getLocation();
			extent = d.getShell().getSize();
			toggled = d.detailsArea != null;
			name = d.title;
		}

	}

	/** The current. */
	public static UserControlDialog current = null;

	/** The previous. */
	private static PreviousDialog previous = null;

	/** The user commands. */
	private final List<IStatement> userCommands;

	/** The scope. */
	final IScope scope;

	/**
	 * Instantiates a new user control dialog.
	 *
	 * @param scope
	 *            the scope
	 * @param panel
	 *            the panel
	 */
	public UserControlDialog(final IScope scope, final UserPanelStatement panel) {
		super((Shell) null, "[" + scope.getAgent().getName() + "] " + panel.getName(), null, null);
		setShellStyle(SWT.CLOSE | SWT.BORDER | SWT.TOOL | SWT.MODELESS | SWT.RESIZE);
		userCommands = panel.getUserCommands();
		this.scope = scope;
	}

	@Override
	public boolean close() {
		previous = new PreviousDialog(this);
		current = null;
		scope.setOnUserHold(false);
		return super.close();
	}

	@Override
	protected void configureShell(final Shell newShell) {
		super.configureShell(newShell);
		if (previous == null || !previous.name.equals(title)) return;
		newShell.setLocation(previous.location);
		newShell.setSize(previous.extent);

	}

	@Override
	public int open() {
		current = this;
		return super.open();
	}

	@Override
	protected Control createContents(final Composite parent) {
		final Composite composite = (Composite) super.createContents(parent);
		if (previous != null && previous.name.equals(title) && previous.toggled) {
			detailsArea = createDetailsArea(composite);
		}
		return composite;

	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Continue", true);
		detailsButton = createButton(parent, IDialogConstants.DETAILS_ID,
				"Inspect " + scope.getAgent().getName() + "...", false);
		detailsButton.setImage(GamaIcon.named(IGamaIcons.MENU_INSPECT).image());

	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite above = (Composite) super.createDialogArea(parent);
		final EditorsGroup composite = new EditorsGroup(above);
		for (final IStatement c : userCommands) {
			if (c instanceof UserCommandStatement) {
				final List<UserInputStatement> inputs = ((UserCommandStatement) c).getInputs();
				final int nbLines = inputs.size() > 1 ? inputs.size() : 1;
				final int nbCol = inputs.size() > 0 ? 1 : 3;
				final Button b = new Button(composite, SWT.PUSH);
				b.setText(c.getName());
				b.setEnabled(((UserCommandStatement) c).isEnabled(scope));
				final GridData gd = new GridData(SWT.LEFT, SWT.TOP, true, true, nbCol, nbLines);
				b.setLayoutData(gd);
				b.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						scope.execute(c);
						GAMA.getExperiment().refreshAllOutputs();
					}

				});
				for (final UserInputStatement i : inputs) {

					scope.addVarWithValue(i.getTempVarName(), i.value(scope));
					EditorFactory.create(scope, composite, i, newValue -> {
						i.setValue(scope, newValue);
						scope.execute(i);
					}, false, false);
				}

				final Label sep = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
				final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
				sep.setLayoutData(data);
			}
		}
		composite.layout();
		composite.pack();

		return composite;
	}

	@Override
	protected boolean isResizable() { return true; }

	@Override
	protected void toggleDetailsArea() {
		final Point oldWindowSize = getShell().getSize();
		Point newWindowSize = cachedWindowSize;
		cachedWindowSize = oldWindowSize;

		// Show the details area.
		if (detailsArea == null) {
			detailsArea = createDetailsArea((Composite) getContents());
		}

		// Hide the details area.
		else {
			detailsArea.dispose();
			detailsArea = null;
		}

		/*
		 * Must be sure to call getContents().computeSize(SWT.DEFAULT, SWT.DEFAULT) before calling
		 * getShell().setSize(newWindowSize) since controls have been added or removed.
		 */

		// Compute the new window size.
		final Point oldSize = getContents().getSize();
		final Point newSize = getContents().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		if (newWindowSize == null) {
			newWindowSize = new Point(oldWindowSize.x + newSize.x - oldSize.x, oldWindowSize.y + newSize.y - oldSize.y);
		}

		// Crop new window size to screen.
		final Point windowLoc = getShell().getLocation();
		final Rectangle screenArea = getContents().getDisplay().getClientArea();
		if (newWindowSize.y > screenArea.height - (windowLoc.y - screenArea.y)) {
			newWindowSize.y = screenArea.height - (windowLoc.y - screenArea.y);
		}
		if (newWindowSize.x > screenArea.width - (windowLoc.x - screenArea.x)) {
			newWindowSize.x = screenArea.width - (windowLoc.x - screenArea.x);
		}
		getShell().setSize(newWindowSize);
		// ((Composite) getContents()).pack();
		((Composite) getContents()).layout();
	}

	@SuppressWarnings ({ "rawtypes", "unchecked" })
	@Override
	protected Control createDetailsArea(final Composite parent) {
		final EditorsGroup compo = new EditorsGroup(parent, SWT.BORDER | SWT.SHADOW_IN);
		// compo.setBackground(WorkbenchHelper.getDisplay().getSystemColor(SWT.COLOR_GRAY));
		final FillLayout layout = new FillLayout();
		compo.setLayout(layout);
		final IAgent agent = scope.getAgent();
		final AgentAttributesEditorsList editors = new AgentAttributesEditorsList();
		editors.add(new ArrayList<IParameter>(agent.getSpecies().getVars()), agent);
		final Map<String, IParameterEditor<?>> parameters = editors.getSections().get(agent);
		if (parameters != null) {
			final List<AbstractEditor> list = new ArrayList(parameters.values());
			Collections.sort(list);
			for (final AbstractEditor gpParam : list) { gpParam.createControls(compo); }
		}
		return compo;

	}

}
