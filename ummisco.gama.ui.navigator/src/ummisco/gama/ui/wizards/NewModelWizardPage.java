package ummisco.gama.ui.wizards;

import java.net.InetAddress;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import ummisco.gama.ui.navigator.contents.ResourceManager;
import ummisco.gama.ui.resources.GamaFonts;

public abstract class NewModelWizardPage extends WizardPage {

	protected final ISelection selection;
	protected Text containerText, fileText, authorText, nameText;

	protected NewModelWizardPage(final ISelection selection) {
		super("wizardPage");
		this.selection = selection;
	}

	/** Gets the container name of the new file */
	public String getContainerName() {
		// TODO user has to select a project otherwise it doesn't work
		return containerText.getText();
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
	public String getTitleName() {
		return nameText.getText();
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

	/**
	 * Uses the standard container selection dialog to choose the new value for the container field.
	 */
	protected void handleContainerBrowse() {
		final ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(),
				ResourcesPlugin.getWorkspace().getRoot(), false, "Select a project or a folder");
		if (dialog.open() == Window.OK) {
			final Object[] result = dialog.getResult();
			if (result.length == 1) {
				containerText.setText(((Path) result[0]).toString());
			}
		}
	}

	protected void updateStatus(final String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	protected void initialize(final String newFileName) {
		final IContainer container = findContainer();
		if (container != null)
			containerText.setText(container.getFullPath().toString());
		fileText.setText(newFileName);
	}

	private IContainer findContainer() {
		Object obj = null;
		if (selection instanceof IStructuredSelection && !selection.isEmpty())
			obj = ((IStructuredSelection) selection).getFirstElement();
		final IResource r = ResourceManager.getResource(obj);
		if (r == null) { return null; }
		if (r instanceof IContainer) {
			return (IContainer) r;
		} else {
			return r.getParent();
		}
	}

	Label createLabel(final Composite c, final String t) {
		final Label label = new Label(c, t == null ? SWT.NULL : SWT.RIGHT);
		final GridData d = new GridData(SWT.END, SWT.CENTER, false, true);
		label.setLayoutData(d);
		label.setFont(GamaFonts.getLabelfont());
		label.setText(t == null ? "" : t);
		return label;
	}

	public void createAuthorSection(final Composite container) {
		GridData gd;
		/* Need to add empty label so the next two controls are pushed to the next line in the grid. */
		createLabel(container, null);
		createLabel(container, "&Author:");

		authorText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		authorText.setLayoutData(gd);
		authorText.setText(getComputerFullName());
		authorText.addModifyListener(e -> dialogChanged());
	}

	public void createContainerSection(final Composite container) {
		final GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;

		createLabel(container, "&Container:");

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		containerText.addModifyListener(e -> dialogChanged());

		final Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleContainerBrowse();
			}
		});
	}

	public void createNameSection(final Composite container) {
		createLabel(container, null);
		createLabel(container, gamlType() + " name:");

		nameText = new Text(container, SWT.BORDER | SWT.SINGLE);
		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		nameText.setLayoutData(gd);
		nameText.setText("New " + gamlType());
		nameText.addModifyListener(e -> dialogChanged());
	}

	public void createFileNameSection(final Composite container) {
		createLabel(container, "&File name:");
		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		fileText.setLayoutData(gd);
		fileText.addModifyListener(e -> {
			final Text t = (Text) e.getSource();
			final String fname = t.getText();
			final int i = fname.lastIndexOf(getExtension());
			if (i > 0) {
				// model title = filename less extension less all non alphanumeric characters
				nameText.setText(fname.substring(0, i).replaceAll("[^\\p{Alnum}]", ""));
			}
			dialogChanged();
		});
	}

	/** Ensures that controls are correctly set. */
	public void dialogChanged() {
		if (getContainerName().length() == 0) {
			updateStatus("The name of the containing folder must be specified");
			return;
		}
		final String fileName = getFileName();
		if (fileName.length() == 0) {
			updateStatus("The name of the file must be specified");
			return;
		}
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
			updateStatus("The name of the file is not valid");
			return;
		}
		if (!fileName.endsWith(getExtension())) {
			updateStatus("The file extension must be '" + getExtension() + "'");
			return;
		}

		final String author = getAuthor();
		if (author.length() == 0) {
			updateStatus("The name of the author must be specified");
			return;
		}

		final String titleName = getTitleName();
		if (titleName.length() == 0) {
			updateStatus("The name of the " + gamlType() + " must be specified");
			return;
		}

		final IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(getContainerName()));
		if (resource instanceof IContainer) {
			final IFile modelfile = ((IContainer) resource).getFile(new Path(fileName));
			if (modelfile.exists()) {
				updateStatus("A file with the same name already exists");
				return;
			}
		}

		updateStatus(null);
	}

	@Override
	public abstract void createControl(final Composite parent);

	public abstract String getExtension();

	public abstract String gamlType();
}
