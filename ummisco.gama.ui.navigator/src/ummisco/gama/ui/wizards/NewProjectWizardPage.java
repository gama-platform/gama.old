/*******************************************************************************************************
 *
 * NewProjectWizardPage.java, in ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.wizards;

/*******************************************************************************
 * are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: IBM Corporation - initial API and implementation Jakub Jurkiewicz <jakub.jurkiewicz@gmail.com> - Fix
 * for Bug 174737 [IDE] New Plug-in Project wizard status handling is inconsistent Oakland Software Incorporated
 * (Francis Upton) <francisu@ieee.org> Bug 224997 [Workbench] Impossible to copy project
 *******************************************************************************/

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * The Class NewProjectWizardPage.
 */
public class NewProjectWizardPage extends WizardPage {

	/** The initial project field value. */
	// initial value stores
	String initialProjectFieldValue;

	/** The is test. */
	boolean isTest;

	/** The create new model. */
	boolean createNewModel = true;

	/** The project name field. */
	// widgets
	Text projectNameField;

	/** The name modify listener. */
	private final Listener nameModifyListener = e -> {
		final boolean valid = validatePage();
		setPageComplete(valid);

	};

	// private ProjectContentsLocationArea locationArea;

	/** The Constant SIZING_TEXT_FIELD_WIDTH. */
	// constants
	private static final int SIZING_TEXT_FIELD_WIDTH = 250;

	/**
	 * Creates a new project creation wizard page.
	 *
	 * @param pageName
	 *            the name of this page
	 */
	public NewProjectWizardPage(final String pageName) {
		super(pageName);
		setPageComplete(false);
	}

	@Override
	public void createControl(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NULL);
		initializeDialogUnits(parent);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		createProjectNameGroup(composite);
		setPageComplete(validatePage());
		// Show description on opening
		setErrorMessage(null);
		setMessage(null);
		setControl(composite);
		Dialog.applyDialogFont(composite);
	}

	/**
	 * Creates the project name specification controls.
	 *
	 * @param parent
	 *            the parent composite
	 */
	private final void createProjectNameGroup(final Composite parent) {
		// project specification group
		final Composite projectGroup = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		projectGroup.setLayout(layout);
		projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// new project label
		final Label projectLabel = new Label(projectGroup, SWT.NONE);
		projectLabel.setText("&Project name:");
		// projectLabel.setFont(parent.getFont());

		// new project name entry field
		projectNameField = new Text(projectGroup, SWT.BORDER);
		final GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		projectNameField.setLayoutData(data);
		// projectNameField.setFont(parent.getFont());
		final Button test = new Button(projectGroup, SWT.CHECK);
		final Button newModel = new Button(projectGroup, SWT.CHECK);
		test.setText("Configure as a test project");
		test.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				isTest = test.getSelection();
				if (isTest) {
					newModel.setText("Create a new test experiment file");
				} else {
					newModel.setText("Create a new model file");
				}
				projectGroup.layout();
			}
		});

		newModel.setText("Create a new model file");
		newModel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				createNewModel = newModel.getSelection();
			}
		});
		newModel.setSelection(createNewModel);

		// Set the initial value first before listener
		// to avoid handling an event during the creation.
		if (initialProjectFieldValue != null) { projectNameField.setText(initialProjectFieldValue); }
		projectNameField.addListener(SWT.Modify, nameModifyListener);
	}

	/**
	 * Checks if is test.
	 *
	 * @return true, if is test
	 */
	public boolean isTest() { return isTest; }

	/**
	 * Creates the new model.
	 *
	 * @return true, if successful
	 */
	public boolean createNewModel() {
		return createNewModel;
	}

	/**
	 * /** Returns the current project location URI as entered by the user, or <code>null</code> if a valid project
	 * location has not been entered.
	 *
	 * @return the project location URI, or <code>null</code>
	 * @since 3.2
	 */
	public URI getLocationURI() {
		return Platform.getLocation().addTrailingSeparator().append(getProjectName()).toFile().toURI();
	}

	/**
	 * Creates a project resource handle for the current project name field value. The project handle is created
	 * relative to the workspace root.
	 * <p>
	 * This method does not create the project resource; this is the responsibility of <code>IProject::create</code>
	 * invoked by the new project resource wizard.
	 * </p>
	 *
	 * @return the new project resource handle
	 */
	public IProject getProjectHandle() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(getProjectName());
	}

	/**
	 * Returns the current project name as entered by the user, or its anticipated initial value.
	 *
	 * @return the project name, its anticipated initial value, or <code>null</code> if no project name is known
	 */
	public String getProjectName() {
		return projectNameField == null ? initialProjectFieldValue : getProjectNameFieldValue();
	}

	/**
	 * Returns the value of the project name field with leading and trailing spaces removed.
	 *
	 * @return the project name in the field
	 */
	private String getProjectNameFieldValue() {
		return projectNameField == null ? "" : projectNameField.getText().trim();
	}

	/**
	 * Sets the initial project name that this page will use when created. The name is ignored if the
	 * createControl(Composite) method has already been called. Leading and trailing spaces in the name are ignored.
	 * Providing the name of an existing project will not necessarily cause the wizard to warn the user. Callers of this
	 * method should first check if the project name passed already exists in the workspace.
	 *
	 * @param name
	 *            initial project name for this page
	 *
	 * @see IWorkspace#validateName(String, int)
	 *
	 */
	public void setInitialProjectName(final String name) {
		if (name == null) {
			initialProjectFieldValue = null;
		} else {
			initialProjectFieldValue = name.trim();
		}
	}

	/**
	 * Returns whether this page's controls currently all contain valid values.
	 *
	 * @return <code>true</code> if all controls are valid, and <code>false</code> if at least one is invalid
	 */
	protected boolean validatePage() {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();

		final String projectFieldContents = getProjectNameFieldValue();
		if ("".equals(projectFieldContents)) { //$NON-NLS-1$
			setErrorMessage(null);
			setMessage("Project name is empty");
			return false;
		}

		final IStatus nameStatus = workspace.validateName(projectFieldContents, IResource.PROJECT);
		if (!nameStatus.isOK()) {
			setErrorMessage(nameStatus.getMessage());
			return false;
		}

		final IProject handle = getProjectHandle();
		if (handle.exists()) {
			getProjectHandle();
			setErrorMessage("Project already exists");
			return false;
		}

		setErrorMessage(null);
		setMessage(null);
		return true;
	}

	/*
	 * see @DialogPage.setVisible(boolean)
	 */
	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		if (visible) { projectNameField.setFocus(); }
	}

}
