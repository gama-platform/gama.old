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
import java.util.Arrays;

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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import msi.gaml.operators.Strings;
import ummisco.gama.ui.resources.GamaFonts;

/**
 * The "New" wizard page allows setting the container for the new file as well as the file name. The page will only
 * accept file name without the extension OR with the extension that matches the expected one.
 */

public class NewFileWizardPage extends WizardPage {

	private Text containerText;
	private Text fileText;
	private Text authorText;
	private Text descriptionText;
	private Text titleText;
	private Button yesButton;
	// private Button testModelButton;
	// private Button emptyModelButton;
	// private Button skeletonModelButton;
	private String typeOfModel = "Empty";
	private final ISelection selection;

	public NewFileWizardPage(final ISelection selection) {
		super("wizardPage");
		setTitle("Model file");
		setDescription("This wizard creates a new model file.");
		this.selection = selection;
	}

	@Override
	public void createControl(final Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);
		final GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;

		createLabel(container, "&Container:");

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		containerText.addModifyListener(e -> dialogChanged());

		final Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleBrowse();
			}
		});
		createLabel(container, "&Choose a template:");

		final Composite middleComposite = new Composite(container, SWT.NULL);
		FillLayout fillLayout = new FillLayout();
		middleComposite.setLayout(fillLayout);
		Arrays.asList("Empty", "Skeleton", "Test").forEach(s -> {
			final Button b = new Button(middleComposite, SWT.RADIO);
			b.setText(s);
			if (s.equals("Empty"))
				b.setSelection(true);
			b.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					typeOfModel = Strings.toLowerCase(((Button) e.widget).getText());
					updateStatus(null);
					dialogChanged();
					descriptionText.setText(typeOfModel.equals("test") ? "A model dedicated to run unit tests" : "");
				}

			});
		});

		createLabel(container, null);
		createLabel(container, "&File name:");

		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fileText.setLayoutData(gd);
		fileText.addModifyListener(e -> {
			final Text t = (Text) e.getSource();
			final String fname = t.getText();
			final int i = fname.lastIndexOf(".gaml");
			if (i > 0) {
				// model title = filename less extension less all non alphanumeric characters
				titleText.setText(fname.substring(0, i).replaceAll("[^\\p{Alnum}]", ""));
			}
			dialogChanged();
		});

		createLabel(container, null);
		createLabel(container, "&Author:");

		authorText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		authorText.setLayoutData(gd);
		authorText.setText(getComputerFullName());
		authorText.addModifyListener(e -> dialogChanged());

		createLabel(container, null);
		createLabel(container, "&Model name:");

		titleText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		titleText.setLayoutData(gd);
		titleText.setText("New Model");
		titleText.addModifyListener(e -> dialogChanged());

		createLabel(container, null);
		createLabel(container, "&Model description:");

		descriptionText = new Text(container, SWT.WRAP | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		descriptionText.setBounds(0, 0, 250, 100);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.verticalSpan = 4;
		descriptionText.setLayoutData(gd);

		/*
		 * Need to add seven empty labels in order to push next controls after the descriptionText box.
		 */
		// TODO Dirty!! Change the way to do this
		for (int i = 0; i < 7; i++) {
			createLabel(container, null);
		}

		createLabel(container, "&Create a html template \nfor the model description ?");

		final Composite compo = new Composite(container, SWT.NULL);
		fillLayout = new FillLayout();
		compo.setLayout(fillLayout);

		yesButton = new Button(compo, SWT.RADIO);
		yesButton.setText("Yes");
		final Button noButton = new Button(compo, SWT.RADIO);
		noButton.setText("No");
		noButton.setSelection(true);
		yesButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent se) {
				dialogChanged();
			}
		});

		/* Finished adding the custom control */
		initialize();
		dialogChanged();
		setControl(container);
	}

	Label createLabel(final Composite c, final String t) {
		final Label label = new Label(c, t == null ? SWT.NULL : SWT.RIGHT);
		final GridData d = new GridData(SWT.END, SWT.CENTER, false, true);
		label.setLayoutData(d);
		label.setFont(GamaFonts.getLabelfont());
		label.setText(t == null ? "" : t);
		return label;
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
		fileText.setText("New Model.gaml");
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for the container field.
	 */
	private void handleBrowse() {
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
			updateStatus("The name of the model file must be specified");
			return;
		}
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
			updateStatus("The name of the model file is not valid");
			return;
		}
		if (!fileName.endsWith(".gaml") && !fileName.endsWith(".experiment")) {
			updateStatus("GAML file extension must be '.gaml' or '.experiment'");
			return;
		}

		final String author = getAuthor();
		final String titleName = getModelName();
		if (author.length() == 0) {
			updateStatus("The name of the author should be specified");
			return;
		}

		if (titleName.length() == 0) {
			updateStatus("The name of the model must be specified");
			return;
		}

		final IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(getContainerName()));
		final IContainer container = (IContainer) resource;

		if (container != null) {
			final IFile modelfile = container.getFile(new Path(fileName));
			final IFile htmlfile = container.getProject().getFile(new Path("doc/" + titleName + ".html"));
			if (modelfile.exists()) {
				updateStatus("A model file with the same name already exists");
				return;
			}
			// if (htmlfile.exists()) {
			// updateStatus("Model name already defined in documentation");
			// return;
			// }
		}

		updateStatus(null);
	}

	private void updateStatus(final String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
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
	public String getModelName() {
		return titleText.getText();
	}

	/** Gets the model name of the new file */
	@Override
	public String getDescription() {
		return descriptionText.getText();
	}

	/** Return true if the user wants a html template, and false otherwise */
	public Boolean getValueHtmlTemplate() {
		return yesButton.getSelection();
	}

	/** Return the type of model (empty, skeleton or example) */
	public String getTypeOfModel() {
		return typeOfModel;

	}
}