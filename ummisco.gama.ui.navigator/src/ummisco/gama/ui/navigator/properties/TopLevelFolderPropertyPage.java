/*******************************************************************************************************
 *
 * TopLevelFolderPropertyPage.java, in ummisco.gama.ui.navigator, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.navigator.properties;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

import ummisco.gama.ui.navigator.contents.TopLevelFolder;

/**
 * The Class TopLevelFolderPropertyPage.
 */
public class TopLevelFolderPropertyPage extends PropertyPage {

	/** The Constant TEXT_FIELD_WIDTH. */
	private static final int TEXT_FIELD_WIDTH = 50;

	/** The owner text. */
	private Text ownerText;

	/**
	 * Constructor for SamplePropertyPage.
	 */
	public TopLevelFolderPropertyPage() {
		super();
		noDefaultAndApplyButton();
	}

	/**
	 * Gets the folder.
	 *
	 * @return the folder
	 */
	public TopLevelFolder getFolder() {
		return super.getElement().getAdapter(TopLevelFolder.class);
	}

	/**
	 * Adds the first section.
	 *
	 * @param parent the parent
	 */
	private void addFirstSection(final Composite parent) {
		final Composite composite = createDefaultComposite(parent);

		// Label for path field
		final Label pathLabel = new Label(composite, SWT.NONE);
		pathLabel.setText("Virtual Folder ");

		// Path text field
		final Text pathValueText = new Text(composite, SWT.WRAP | SWT.READ_ONLY);
		pathValueText.setText(getFolder().getName());
	}

	/**
	 * Adds the separator.
	 *
	 * @param parent the parent
	 */
	private void addSeparator(final Composite parent) {
		final Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		final GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
	}

	/**
	 * Adds the second section.
	 *
	 * @param parent the parent
	 */
	private void addSecondSection(final Composite parent) {
		final Composite composite = createDefaultComposite(parent);

		// Label for owner field
		final Label ownerLabel = new Label(composite, SWT.NONE);
		ownerLabel.setText("Groups projects of nature");

		// Owner text field
		ownerText = new Text(composite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		final GridData gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		ownerText.setLayoutData(gd);

		// Populate owner text field
		final String owner = getFolder().getNature();
		ownerText.setText(owner != null ? owner : "user");
	}

	/**
	 * Adds the third section.
	 *
	 * @param parent the parent
	 */
	private void addThirdSection(final Composite parent) {
		final Composite composite = createDefaultComposite(parent);

		// Label for owner field
		final Label ownerLabel = new Label(composite, SWT.NONE);
		ownerLabel.setText("Current number of projects");

		// Owner text field
		final Text ownerText = new Text(composite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
		final GridData gd = new GridData();
		gd.widthHint = convertWidthInCharsToPixels(TEXT_FIELD_WIDTH);
		ownerText.setLayoutData(gd);

		// Populate owner text field
		final String owner = String.valueOf(getFolder().getNavigatorChildren().length);
		ownerText.setText(owner);
	}

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	@Override
	protected Control createContents(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		final GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

		addFirstSection(composite);
		addSeparator(composite);
		addSecondSection(composite);
		addThirdSection(composite);
		setTitle(getFolder().getStatusMessage());
		setImageDescriptor(ImageDescriptor.createFromImage(getFolder().getImage()));
		getContainer().updateTitle();
		return composite;
	}

	/**
	 * Creates the default composite.
	 *
	 * @param parent the parent
	 * @return the composite
	 */
	private Composite createDefaultComposite(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NULL);
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		final GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

		return composite;
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		// store the value in the owner text field
		// try {
		// ((IResource) getElement()).setPersistentProperty(new QualifiedName("", OWNER_PROPERTY),
		// ownerText.getText());
		// } catch (final CoreException e) {
		// return false;
		// }
		return true;
	}

}