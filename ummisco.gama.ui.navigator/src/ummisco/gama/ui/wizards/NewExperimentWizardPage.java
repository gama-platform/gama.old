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

import java.net.InetAddress;

import org.eclipse.core.internal.resources.Resource;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;

/**
 * The "New" wizard page allows setting the container for the new file as well as the file name. The page will only
 * accept file name without the extension OR with the extension that matches the expected one.
 */

public class NewExperimentWizardPage extends WizardPage {

	private Text containerText;
	private Text modelChooser;
	private Text fileText;
	private Text authorText;
	private Text experimentNameText;

	private final ISelection selection;

	public NewExperimentWizardPage(final ISelection selection) {
		super("wizardPage");
		setTitle("Experiment file");
		setDescription("This wizard creates a new experiment file.");
		this.selection = selection;
	}

	@Override
	public void createControl(final Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);
		final GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;

		Label label = new Label(container, SWT.NULL);
		label.setText("&Container:");

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		containerText.addModifyListener(e -> dialogChanged());

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleContainerBrowse();
			}
		});

		label = new Label(container, SWT.NULL);
		label.setText("Model to experiment on:");

		modelChooser = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		modelChooser.setLayoutData(gd);
		modelChooser.addModifyListener(e -> dialogChanged());

		button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleBrowse();
			}
		});

		label = new Label(container, SWT.NULL);
		label.setText("&File name:");

		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fileText.setLayoutData(gd);
		fileText.addModifyListener(e -> {
			final Text t = (Text) e.getSource();
			final String fname = t.getText();
			final int i = fname.lastIndexOf(".experiment");
			if (i > 0) {
				experimentNameText.setText(fname.substring(0, i).replaceAll("[^\\p{Alnum}]", ""));
			}
			dialogChanged();
		});

		/* Need to add empty label so the next two controls are pushed to the next line in the grid. */
		label = new Label(container, SWT.NULL);
		label.setText("");

		label = new Label(container, SWT.NULL);
		label.setText("&Author:");

		authorText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		authorText.setLayoutData(gd);
		authorText.setText(getComputerFullName());
		authorText.addModifyListener(e -> dialogChanged());

		/* Need to add empty label so the next two controls are pushed to the next line in the grid. */
		label = new Label(container, SWT.NULL);
		label.setText("");

		label = new Label(container, SWT.NULL);
		label.setText("&Experiment name:");

		experimentNameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		experimentNameText.setLayoutData(gd);
		experimentNameText.setText("new");
		experimentNameText.addModifyListener(e -> dialogChanged());

		/* Need to add empty label so the next two controls are pushed to the next line in the grid. */
		label = new Label(container, SWT.NULL);
		label.setText("");

		final Composite middleComposite = new Composite(container, SWT.NULL);
		final FillLayout fillLayout = new FillLayout();
		middleComposite.setLayout(fillLayout);

		/* Finished adding the custom control */
		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Return the computer full name. <br>
	 * 
	 * @return the name or <b>null</b> if the name cannot be found
	 */
	public static String getComputerFullName() {
		String uname = System.getProperty("user.name");
		if (uname == null || uname.isEmpty()) {
			try {
				final InetAddress addr = InetAddress.getLocalHost();
				uname = addr.getHostName();
			} catch (final Exception e) {}
		}
		return uname;
	}

	/** Tests if the current workbench selection is a suitable container to use. */
	private void initialize() {
		if (selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection) {
			final IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1) { return; }
			final Object obj = ssel.getFirstElement();
			if (obj instanceof IResource) {
				IContainer container;
				if (obj instanceof IContainer) {
					container = (IContainer) obj;
				} else {
					container = ((IResource) obj).getParent();
				}
				containerText.setText(container.getFullPath().toString());

			}
		}
		modelChooser.setText("");
		fileText.setText("new.experiment");
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

	private void handleContainerBrowse() {
		final ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(),
				ResourcesPlugin.getWorkspace().getRoot(), false, "Select a project or a folder");
		if (dialog.open() == Window.OK) {
			final Object[] result = dialog.getResult();
			if (result.length == 1) {
				containerText.setText(((Path) result[0]).toString());
			}
		}
	}

	/** Ensures that controls are correctly set. */
	private void dialogChanged() {
		if (getContainerName().length() == 0) {
			updateStatus("The name of the containing folder must be specified");
			return;
		}
		final String fileName = getFileName();
		if (fileName.length() == 0) {
			updateStatus("The name of the experiment file must be specified");
			return;
		}
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
			updateStatus("The name of the experiment file is not valid");
			return;
		}
		if (!fileName.endsWith(".experiment")) {
			updateStatus("Experiment file extension must be '.experiment'");
			return;
		}

		final String author = getAuthor();
		final String titleName = getExperimentName();
		if (author.length() == 0) {
			updateStatus("The name of the author must be specified");
			return;
		}

		if (titleName.length() == 0) {
			updateStatus("The name of the model must be specified");
			return;
		}

		final IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(getContainerName()));
		if (resource instanceof IContainer) {
			final IFile modelfile = ((IContainer) resource).getFile(new Path(fileName));
			if (modelfile.exists()) {
				updateStatus("A model file with the same name already exists");
				return;
			}

		}

		// if ( (resource.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0 ) {
		// updateStatus("File container must exist");
		// return;
		// }
		// if ( !resource.isAccessible() ) {
		// updateStatus("Project must be writable");
		// return;
		// }
		updateStatus(null);
	}

	private void updateStatus(final String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	/** Gets the file name of the new file */
	public String getFileName() {
		return fileText.getText();
	}

	/** Gets the author of the new file */
	public String getAuthor() {
		return authorText.getText();
	}

	/** Gets the model name of the new file */
	public String getExperimentName() {
		return experimentNameText.getText();
	}

	/** Gets the model name of the new file */
	@Override
	public String getDescription() {
		return "Create a new experiment file";
	}

	public String getModelName() {
		return modelChooser.getText();
	}

	public String getContainerName() {
		return containerText.getText();
	}

}