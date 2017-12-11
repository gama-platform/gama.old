/*********************************************************************************************
 *
 * 'NewFileWizardPage.java, in plugin ummisco.gama.ui.navigator, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.wizards;

import org.eclipse.core.internal.resources.Resource;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;

/**
 * The "New" wizard page allows setting the container for the new file as well as the file name. The page will only
 * accept file name without the extension OR with the extension that matches the expected one.
 */

public class NewExperimentWizardPage extends NewModelWizardPage {

	private Text modelChooser;

	public NewExperimentWizardPage(final ISelection selection) {
		super(selection);
		setTitle("Experiment file");
		setDescription("This wizard creates a new experiment file.");
	}

	@Override
	public void createControl(final Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);
		createContainerSection(container);
		createLabel(container, "Model to experiment on:");
		modelChooser = new Text(container, SWT.BORDER | SWT.SINGLE);
		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		modelChooser.setLayoutData(gd);
		modelChooser.addModifyListener(e -> dialogChanged());
		final Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleBrowse();
			}
		});
		createFileNameSection(container);
		createAuthorSection(container);
		createNameSection(container);
		/* Need to add empty label so the next two controls are pushed to the next line in the grid. */
		createLabel(container, null);
		/* Finished adding the custom control */
		initialize();
		dialogChanged();
		setControl(container);
	}

	@Override
	protected void initialize() {
		super.initialize();
		modelChooser.setText("");
	}

	private FilteredResourcesSelectionDialog dialog;

	/**
	 * Uses the standard container selection dialog to choose the new value for the container field.
	 * 
	 * @throws CoreException
	 */
	private void handleBrowse() {
		final IContainer p = ResourcesPlugin.getWorkspace().getRoot();
		dialog = new FilteredResourcesSelectionDialog(getShell(), false, p, Resource.FILE);
		dialog.setInitialPattern("*.gaml");
		dialog.setTitle("Choose a gaml model in project " + p.getName());
		if (dialog.open() == Window.OK) {
			final Object[] result = dialog.getResult();
			if (result.length == 1) {
				final IResource res = (IResource) result[0];
				modelChooser.setText(res.getFullPath().toString());
			}
		}
	}

	public String getModelName() {
		return modelChooser.getText();
	}

	@Override
	public String getExtension() {
		return ".experiment";
	}

	@Override
	public String gamlType() {
		return "Experiment";
	}

}