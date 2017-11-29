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

public class TopLevelFolderPropertyPage extends PropertyPage {

	private static final int TEXT_FIELD_WIDTH = 50;

	private Text ownerText;

	/**
	 * Constructor for SamplePropertyPage.
	 */
	public TopLevelFolderPropertyPage() {
		super();
		noDefaultAndApplyButton();
	}

	public TopLevelFolder getFolder() {
		return super.getElement().getAdapter(TopLevelFolder.class);
	}

	private void addFirstSection(final Composite parent) {
		final Composite composite = createDefaultComposite(parent);

		// Label for path field
		final Label pathLabel = new Label(composite, SWT.NONE);
		pathLabel.setText("Virtual Folder ");

		// Path text field
		final Text pathValueText = new Text(composite, SWT.WRAP | SWT.READ_ONLY);
		pathValueText.setText(getFolder().getName());
	}

	private void addSeparator(final Composite parent) {
		final Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		final GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
	}

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
		setTitle(getFolder().getMessageForStatus());
		setImageDescriptor(ImageDescriptor.createFromImage(getFolder().getImage()));
		getContainer().updateTitle();
		return composite;
	}

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