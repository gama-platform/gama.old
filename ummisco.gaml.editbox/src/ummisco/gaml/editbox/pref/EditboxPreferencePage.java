package ummisco.gaml.editbox.pref;

import java.util.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.*;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import ummisco.gaml.editbox.*;

public class EditboxPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private List categoryList;
	private TabFolder folder;
	private Map<String, LinkedHashSet<String>> categoryFiles;
	private List namesList;
	private Button bAddFile;
	private boolean providersChanged;

	@Override
	protected Control createContents(final Composite parent) {
		noDefaultAndApplyButton();

		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(1, false));
		Link link = new Link(c, SWT.NONE);
		link.setText("Turn off current line highlighting <A>here</A>.");
		FontData[] fontData = link.getFont().getFontData();
		for ( FontData fd : fontData ) {
			fd.setHeight(10);
			fd.setStyle(SWT.BOLD);
		}
		link.setFont(new Font(getShell().getDisplay(), fontData));
		link.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				IWorkbenchPreferenceContainer container = (IWorkbenchPreferenceContainer) getContainer();
				container.openPage("org.eclipse.ui.preferencePages.GeneralTextEditor", null);
			}
		});

		folder = new TabFolder(c, SWT.NONE);
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));
		TabItem ti = new TabItem(folder, SWT.NONE);
		ti.setText("Themes");
		ti.setControl(createCategoryControl(folder));
		folder.pack();
		return c;
	}

	protected Control createCategoryControl(final Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, true));

		Label categoryLabel = new Label(c, SWT.NONE);
		categoryLabel.setText("Themes");

		Label namesLabel = new Label(c, SWT.NONE);
		namesLabel.setText("Associated file names");
		namesLabel.setAlignment(SWT.RIGHT);

		categoryList = new List(c, SWT.V_SCROLL | SWT.BORDER);
		categoryList.setLayoutData(new GridData(GridData.FILL_BOTH));
		categoryList.addSelectionListener(new SelectCategory());
		namesList = new List(c, SWT.V_SCROLL | SWT.BORDER);
		namesList.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite cLeft = new Composite(c, SWT.NONE);
		cLeft.setLayout(new GridLayout(2, true));
		Button bAddCategory = new Button(cLeft, SWT.NONE);
		bAddCategory.setText("Add");
		bAddCategory.addSelectionListener(new AddCategory());
		bAddCategory.setLayoutData(new GridData(GridData.FILL_BOTH));
		Button bRemoveCategory = new Button(cLeft, SWT.NONE);
		bRemoveCategory.setText("Remove");
		bRemoveCategory.setLayoutData(new GridData(GridData.FILL_BOTH));
		bRemoveCategory.addSelectionListener(new RemoveCategory());

		Composite cRight = new Composite(c, SWT.NONE);
		cRight.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		cRight.setLayout(new GridLayout(2, true));
		bAddFile = new Button(cRight, SWT.NONE);
		bAddFile.setText("Add");
		bAddFile.setLayoutData(new GridData(GridData.FILL_BOTH));
		bAddFile.addSelectionListener(new AddFile());
		bAddFile.setEnabled(false);
		Button bRemoveFile = new Button(cRight, SWT.NONE);
		bRemoveFile.setText("Remove");
		bRemoveFile.setLayoutData(new GridData(GridData.FILL_BOTH));
		bRemoveFile.addSelectionListener(new RemoveFile());

		loadData();

		return c;
	}

	protected void loadData() {
		Collection<IBoxProvider> boxProviders = EditBox.getDefault().getProviderRegistry().getBoxProviders();
		for ( IBoxProvider provider : boxProviders ) {
			newTab(provider.getName());
		}
	}

	@Override
	public void init(final IWorkbench workbench) {}

	protected boolean contains(final String[] items, final String newText) {
		if ( items == null || items.length == 0 ) { return false; }
		for ( String s : items ) {
			if ( s.equalsIgnoreCase(newText) ) { return true; }
		}
		return false;
	}

	protected void newTab(final String value) {
		categoryList.add(value);
		TabItem item = new TabItem(folder, SWT.NONE);
		item.setText(value);
		BoxSettingsTab p = new BoxSettingsTab();
		IBoxProvider provider = EditBox.getDefault().getProviderRegistry().providerForName(value);
		item.setControl(p.createContro(folder, provider));
		item.setData(p);
		if ( categoryFiles == null ) {
			categoryFiles = new LinkedHashMap<String, LinkedHashSet<String>>();
		}
		Collection<String> fileNames = p.getSettings().getFileNames();
		if ( fileNames == null ) {
			fileNames = Collections.emptyList();
		}
		categoryFiles.put(value, new LinkedHashSet<String>(fileNames));
		categoryList.setSelection(new String[] { value });
		namesList.setItems(fileNames.toArray(new String[0]));
		bAddFile.setEnabled(true);
	}

	public String[] namesArray(final String name) {
		LinkedHashSet<String> set = categoryFiles.get(name);
		if ( set == null || set.isEmpty() ) { return new String[0]; }
		return set.toArray(new String[0]);
	}

	public void addFileName(final String value) {
		int i = categoryList.getSelectionIndex();
		if ( i > -1 ) {
			String categoryName = categoryList.getItem(i);
			LinkedHashSet<String> fileNames = categoryFiles.get(categoryName);
			fileNames.add(value);
			namesList.add(value);
			Object o = folder.getItem(i + 1).getData();
			if ( o instanceof BoxSettingsTab ) {
				((BoxSettingsTab) o).getSettings().setFileNames(fileNames);
			}
		}
	}

	@Override
	public boolean performOk() {
		TabItem[] items = folder.getItems();
		for ( int i = 1; i < items.length; i++ ) {
			Object o = items[i].getData();
			if ( o instanceof BoxSettingsTab ) {
				BoxSettingsTab pref = (BoxSettingsTab) o;
				String msg = pref.validate();
				if ( msg != null ) {
					folder.setSelection(i);
					setMessage(msg);
					return false;
				}
				pref.save();
			}
		}
		if ( providersChanged ) {
			EditBox.getDefault().getProviderRegistry().storeProviders();
		}
		return true;
	}

	@Override
	public boolean performCancel() {
		TabItem[] items = folder.getItems();
		for ( int i = 1; i < items.length; i++ ) {
			Object o = items[i].getData();
			if ( o instanceof BoxSettingsTab ) {
				((BoxSettingsTab) o).cancel();
			}
		}

		if ( providersChanged ) {
			EditBox.getDefault().getProviderRegistry().setProvideres(null);
		}
		return true;
	}

	class AddCategory extends SelectionAdapter {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			InputDialog dialog = new InputDialog(getShell(), "New Category", "Name:", null, new IInputValidator() {

				@Override
				public String isValid(final String newText) {
					if ( newText != null && newText.trim().length() > 0 && !contains(categoryList.getItems(), newText) ) { return null; }
					return "Unique name required";
				}
			});

			if ( dialog.open() == Window.OK ) {
				newTab(dialog.getValue());
				providersChanged = true;
			}

		}

	}

	class RemoveCategory extends SelectionAdapter {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			int i = categoryList.getSelectionIndex();
			if ( i > -1 ) {
				String name = categoryList.getItem(i);
				categoryList.remove(i);
				categoryFiles.remove(name);
				namesList.setItems(new String[0]);
				bAddFile.setEnabled(false);
				TabItem ti = folder.getItem(i + 1);
				Object o = ti.getData();
				ti.dispose();
				if ( o instanceof BoxSettingsTab ) {
					((BoxSettingsTab) o).dispose();
				}
				EditBox.getDefault().getProviderRegistry().removeProvider(name);
				providersChanged = true;
			}
		}
	}

	class SelectCategory extends SelectionAdapter {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			int i = categoryList.getSelectionIndex();
			if ( i > -1 ) {
				String name = categoryList.getItem(i);
				namesList.setItems(namesArray(name));
				bAddFile.setEnabled(true);
			} else {
				namesList.setItems(new String[0]);
				bAddFile.setEnabled(false);
			}
		}
	}

	class AddFile extends SelectionAdapter {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			InputDialog dialog =
				new InputDialog(getShell(), "New Name", "File name pattern like *.java, my.xml:", null,
					new IInputValidator() {

						@Override
						public String isValid(final String newText) {
							if ( newText != null && newText.trim().length() > 0 ) { return null; }
							return "";
						}
					});

			if ( dialog.open() == Window.OK ) {
				addFileName(dialog.getValue());
			}

		}
	}

	class RemoveFile extends SelectionAdapter {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			int i = namesList.getSelectionIndex();
			if ( i > -1 ) {
				int n = categoryList.getSelectionIndex();
				if ( n > -1 ) {
					String key = categoryList.getItem(n);
					String value = namesList.getItem(i);
					LinkedHashSet<String> fNames = categoryFiles.get(key);
					fNames.remove(value);
					namesList.remove(i);
					Object o = folder.getItem(n + 1).getData();
					if ( o instanceof BoxSettingsTab ) {
						((BoxSettingsTab) o).getSettings().setFileNames(new ArrayList<String>(fNames));
					}
				}
			}
		}
	}
}
