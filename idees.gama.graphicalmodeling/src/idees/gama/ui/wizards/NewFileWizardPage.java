/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package idees.gama.ui.wizards;

import java.net.InetAddress;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

/**
 * The "New" wizard page allows setting the container for the new file as well as the file name. The
 * page will only accept file name without the extension OR with the extension that matches the
 * expected one.
 */

public class NewFileWizardPage extends WizardPage {

	private Text containerText;
	private Text fileText;
	private Text authorText;
	private Text descriptionText;
	private Text titleText;
	private Button exampleModelButton;
	private Button emptyModelButton;
	private Button skeletonModelButton;
	private String typeOfModel = "empty";
	private final ISelection selection;

	public NewFileWizardPage(final ISelection selection) {
		super("wizardPage");
		setTitle("Model Diagram");
		setDescription("This wizard creates a new model file.");
		this.selection = selection;
	}

	@Override
	public void createControl(final Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText("&Container:");

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		containerText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				dialogChanged();
			}
		});

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleBrowse();
			}
		});

		label = new Label(container, SWT.NULL);
		label.setText("&Choose a diagram:");

		Composite middleComposite = new Composite(container, SWT.NULL);
		FillLayout fillLayout = new FillLayout();
		middleComposite.setLayout(fillLayout);

		emptyModelButton = new Button(middleComposite, SWT.RADIO);
		emptyModelButton.setText("Empty");
		emptyModelButton.setSelection(true);
		skeletonModelButton = new Button(middleComposite, SWT.RADIO);
		skeletonModelButton.setText("Skeleton");
		exampleModelButton = new Button(middleComposite, SWT.RADIO);
		exampleModelButton.setText("Example");
		emptyModelButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent se) {
				typeOfModel = "empty";
				radioChanged();
			}

		});
		exampleModelButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent se) {
				typeOfModel = "example";
				radioChanged();
			}
		});
		skeletonModelButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent se) {
				typeOfModel = "skeleton";
				radioChanged();
			}
		});

		/* Need to add empty label so the next controls are pushed to the next line in the grid. */
		label = new Label(container, SWT.NULL);
		label.setText("");

		label = new Label(container, SWT.NULL);
		label.setText("&File name:");

		fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fileText.setLayoutData(gd);
		fileText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				Text t = (Text)e.getSource();
				String fname = t.getText();
				int i = fname.lastIndexOf(".gadl");
				if (i>0) {
					// model title = filename less extension less all non alphanumeric characters
					titleText.setText(fname.substring(0, i).replaceAll("[^\\p{Alnum}]", ""));
				}/* else if (fname.length()>0) {
					int pos = t.getSelection().x;
					fname = fname.replaceAll("[[^\\p{Alnum}]&&[^_-]&&[^\\x2E]]", "_");
					t.setText(fname+".gaml");
					t.setSelection(pos);
				} else {
					t.setText("new.gaml");
				}*/
				dialogChanged();
			}
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
		authorText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				dialogChanged();
			}
		});

		/* Need to add empty label so the next two controls are pushed to the next line in the grid. */
		label = new Label(container, SWT.NULL);
		label.setText("");

		label = new Label(container, SWT.NULL);
		label.setText("&Diagram name:");

		titleText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		titleText.setLayoutData(gd);
		titleText.setText("new");
		titleText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				dialogChanged();
			}
		});

		/* Need to add empty label so the next two controls are pushed to the next line in the grid. */
		label = new Label(container, SWT.NULL);
		label.setText("");

		label = new Label(container, SWT.NULL);
		label.setText("&Model description:");

		descriptionText =
			new Text(container, SWT.WRAP | SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		descriptionText.setBounds(0, 0, 250, 100);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.verticalSpan = 4;
		descriptionText.setLayoutData(gd);

		/*
		 * Need to add seven empty labels in order to push next controls after the descriptionText
		 * box.
		 */
		// TODO Dirty!! Change the way to do this
		for ( int i = 0; i < 7; i++ ) {
			label = new Label(container, SWT.NULL);
			label.setText("");
		}

		/* Finished adding the custom control */
		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Return the computer full name. <br>
	 * @return the name or <b>null</b> if the name cannot be found
	 */
	public static String getComputerFullName() {
		String uname = System.getProperty("user.name");
		if (uname == null || uname.isEmpty()) {
			try {
				final InetAddress addr = InetAddress.getLocalHost();
				uname = new String(addr.getHostName());
			} catch (final Exception e) {}
		}
		return uname;
	}

	private void radioChanged() {
		if ( exampleModelButton.getSelection() ) {
			descriptionText.setText("This model displays an awesome simulation of something ...");
			titleText.setText("example");
			fileText.setText("example.gadl");
			updateStatus(null);
		}
		if ( emptyModelButton.getSelection() || skeletonModelButton.getSelection() ) {
			descriptionText.setText("");
			titleText.setText("new");
			fileText.setText("new.gadl");
			updateStatus(null);
		}
		dialogChanged();
	}

	/** Tests if the current workbench selection is a suitable container to use. */
	private void initialize() {
		if ( selection != null && selection.isEmpty() == false &&
			selection instanceof IStructuredSelection ) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if ( ssel.size() > 1 ) { return; }
			Object obj = ssel.getFirstElement();
			if ( obj instanceof IResource ) {
				IContainer container;
				if ( obj instanceof IContainer ) {
					container = (IContainer) obj;
				} else {
					container = ((IResource) obj).getParent();
				}
				containerText.setText(container.getFullPath().toString());
			}
		}
		fileText.setText("new.gadl");
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for the container field.
	 */
	private void handleBrowse() {
		ContainerSelectionDialog dialog =
			new ContainerSelectionDialog(getShell(), ResourcesPlugin.getWorkspace().getRoot(),
				false, "Select a project as a container");
		if ( dialog.open() == Window.OK ) {
			Object[] result = dialog.getResult();
			if ( result.length == 1 ) {
				containerText.setText(((Path) result[0]).toString());
			}
		}
	}

	/** Ensures that controls are correctly set. */
	private void dialogChanged() {
		IResource resource =
			ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(getContainerName()));
		IContainer container = (IContainer) resource;
		String fileName = getFileName();
		String author = getAuthor();
		String titleName = getModelName();

		final IFile modelfile = container.getFile(new Path("diagrams/" + fileName));
		
		if ( getContainerName().length() == 0 ) {
			updateStatus("File container must be specified");
			return;
		}
		if ( resource == null || (resource.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0 ) {
			updateStatus("File container must exist");
			return;
		}
		if ( !resource.isAccessible() ) {
			updateStatus("Project must be writable");
			return;
		}
		if ( fileName.length() == 0 ) {
			updateStatus("File name must be specified");
			return;
		}
		if ( fileName.replace('\\', '/').indexOf('/', 1) > 0 ) {
			updateStatus("File name must be valid");
			return;
		}
		if ( !fileName.endsWith(".gadl") ) {
			updateStatus("File extension must be \".gadl\"");
			return;
		}
		if ( author.length() == 0 ) {
			updateStatus("Author name must be specified");
			return;
		}
		if ( modelfile.exists() ) {
			updateStatus("File already exists");
			return;
		}

		if ( titleName.length() == 0 ) {
			updateStatus("Diagram name must be specified");
			return;
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


	/** Return the type of model (empty, skeleton or example) */
	public String getTypeOfModel() {
		return typeOfModel;

	}
}