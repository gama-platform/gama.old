/*******************************************************************************************************
 *
 * EditboxPreferencePage.java, in ummisco.gama.ui.modeling, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

/**
 * The Class EditboxPreferencePage.
 */
public class EditboxPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	/** The category list. */
	List categoryList;
	
	/** The folder. */
	TabFolder folder;
	
	/** The category files. */
	Map<String, LinkedHashSet<String>> categoryFiles;
	
	/** The names list. */
	List namesList;
	
	/** The b add file. */
	Button bAddFile;
	
	/** The providers changed. */
	boolean providersChanged;

	@Override
	protected Control createContents(final Composite parent) {
		noDefaultAndApplyButton();

		final Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(1, false));
		final Link link = new Link(c, SWT.NONE);
		link.setText("Turn off current line highlighting <A>here</A>.");
		final FontData[] fontData = link.getFont().getFontData();
		for (final FontData fd : fontData) {
			fd.setHeight(10);
			fd.setStyle(SWT.BOLD);
		}
		link.setFont(new Font(getShell().getDisplay(), fontData));
		link.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final IWorkbenchPreferenceContainer container = (IWorkbenchPreferenceContainer) getContainer();
				container.openPage("org.eclipse.ui.preferencePages.GeneralTextEditor", null);
			}
		});

		folder = new TabFolder(c, SWT.NONE);
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));
		final TabItem ti = new TabItem(folder, SWT.NONE);
		ti.setText("Themes");
		ti.setControl(createCategoryControl(folder));
		folder.pack();
		return c;
	}

	/**
	 * Creates the category control.
	 *
	 * @param parent the parent
	 * @return the control
	 */
	protected Control createCategoryControl(final Composite parent) {
		final Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, true));

		final Label categoryLabel = new Label(c, SWT.NONE);
		categoryLabel.setText("Themes");

		final Label namesLabel = new Label(c, SWT.NONE);
		namesLabel.setText("Associated file names");
		namesLabel.setAlignment(SWT.RIGHT);

		categoryList = new List(c, SWT.V_SCROLL | SWT.BORDER);
		categoryList.setLayoutData(new GridData(GridData.FILL_BOTH));
		categoryList.addSelectionListener(new SelectCategory());
		namesList = new List(c, SWT.V_SCROLL | SWT.BORDER);
		namesList.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Composite cLeft = new Composite(c, SWT.NONE);
		cLeft.setLayout(new GridLayout(2, true));
		final Button bAddCategory = new Button(cLeft, SWT.NONE);
		bAddCategory.setText("Add");
		bAddCategory.addSelectionListener(new AddCategory());
		bAddCategory.setLayoutData(new GridData(GridData.FILL_BOTH));
		final Button bRemoveCategory = new Button(cLeft, SWT.NONE);
		bRemoveCategory.setText("Remove");
		bRemoveCategory.setLayoutData(new GridData(GridData.FILL_BOTH));
		bRemoveCategory.addSelectionListener(new RemoveCategory());

		final Composite cRight = new Composite(c, SWT.NONE);
		cRight.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		cRight.setLayout(new GridLayout(2, true));
		bAddFile = new Button(cRight, SWT.NONE);
		bAddFile.setText("Add");
		bAddFile.setLayoutData(new GridData(GridData.FILL_BOTH));
		bAddFile.addSelectionListener(new AddFile());
		bAddFile.setEnabled(false);
		final Button bRemoveFile = new Button(cRight, SWT.NONE);
		bRemoveFile.setText("Remove");
		bRemoveFile.setLayoutData(new GridData(GridData.FILL_BOTH));
		bRemoveFile.addSelectionListener(new RemoveFile());

		loadData();

		return c;
	}

	/**
	 * Load data.
	 */
	protected void loadData() {
		final Collection<IBoxProvider> boxProviders = BoxProviderRegistry.getInstance().getBoxProviders();
		for (final IBoxProvider provider : boxProviders) {
			newTab(provider.getName());
		}
	}

	@Override
	public void init(final IWorkbench workbench) {}

	/**
	 * Contains.
	 *
	 * @param items the items
	 * @param newText the new text
	 * @return true, if successful
	 */
	protected boolean contains(final String[] items, final String newText) {
		if (items == null || items.length == 0) { return false; }
		for (final String s : items) {
			if (s.equalsIgnoreCase(newText)) { return true; }
		}
		return false;
	}

	/**
	 * New tab.
	 *
	 * @param value the value
	 */
	protected void newTab(final String value) {
		categoryList.add(value);
		final TabItem item = new TabItem(folder, SWT.NONE);
		item.setText(value);
		final BoxSettingsTab p = new BoxSettingsTab();
		final IBoxProvider provider = BoxProviderRegistry.getInstance().providerForName(value);
		item.setControl(p.createContro(folder, provider));
		item.setData(p);
		if (categoryFiles == null) {
			categoryFiles = new LinkedHashMap<>();
		}
		Collection<String> fileNames = p.getSettings().getFileNames();
		if (fileNames == null) {
			fileNames = Collections.emptyList();
		}
		categoryFiles.put(value, new LinkedHashSet<>(fileNames));
		categoryList.setSelection(new String[] { value });
		namesList.setItems(fileNames.toArray(new String[0]));
		bAddFile.setEnabled(true);
	}

	/**
	 * Names array.
	 *
	 * @param name the name
	 * @return the string[]
	 */
	public String[] namesArray(final String name) {
		final LinkedHashSet<String> set = categoryFiles.get(name);
		if (set == null || set.isEmpty()) { return new String[0]; }
		return set.toArray(new String[0]);
	}

	/**
	 * Adds the file name.
	 *
	 * @param value the value
	 */
	public void addFileName(final String value) {
		final int i = categoryList.getSelectionIndex();
		if (i > -1) {
			final String categoryName = categoryList.getItem(i);
			final LinkedHashSet<String> fileNames = categoryFiles.get(categoryName);
			fileNames.add(value);
			namesList.add(value);
			final Object o = folder.getItem(i + 1).getData();
			if (o instanceof BoxSettingsTab) {
				((BoxSettingsTab) o).getSettings().setFileNames(fileNames);
			}
		}
	}

	@Override
	public boolean performOk() {
		final TabItem[] items = folder.getItems();
		for (int i = 1; i < items.length; i++) {
			final Object o = items[i].getData();
			if (o instanceof BoxSettingsTab) {
				final BoxSettingsTab pref = (BoxSettingsTab) o;
				final String msg = pref.validate();
				if (msg != null) {
					folder.setSelection(i);
					setMessage(msg);
					return false;
				}
				pref.save();
			}
		}
		if (providersChanged) {
			BoxProviderRegistry.getInstance().storeProviders();
		}
		return true;
	}

	@Override
	public boolean performCancel() {
		final TabItem[] items = folder.getItems();
		for (int i = 1; i < items.length; i++) {
			final Object o = items[i].getData();
			if (o instanceof BoxSettingsTab) {
				((BoxSettingsTab) o).cancel();
			}
		}

		if (providersChanged) {
			BoxProviderRegistry.getInstance().setProviders(null);
		}
		return true;
	}

	/**
	 * The Class AddCategory.
	 */
	class AddCategory extends SelectionAdapter {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final InputDialog dialog = new InputDialog(getShell(), "New Category", "Name:", null, newText -> {
				if (newText != null && newText.trim().length() > 0
						&& !contains(categoryList.getItems(), newText)) { return null; }
				return "Unique name required";
			});

			if (dialog.open() == Window.OK) {
				newTab(dialog.getValue());
				providersChanged = true;
			}

		}

	}

	/**
	 * The Class RemoveCategory.
	 */
	class RemoveCategory extends SelectionAdapter {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final int i = categoryList.getSelectionIndex();
			if (i > -1) {
				final String name = categoryList.getItem(i);
				categoryList.remove(i);
				categoryFiles.remove(name);
				namesList.setItems(new String[0]);
				bAddFile.setEnabled(false);
				final TabItem ti = folder.getItem(i + 1);
				final Object o = ti.getData();
				ti.dispose();
				if (o instanceof BoxSettingsTab) {
					((BoxSettingsTab) o).dispose();
				}
				BoxProviderRegistry.getInstance().removeProvider(name);
				providersChanged = true;
			}
		}
	}

	/**
	 * The Class SelectCategory.
	 */
	class SelectCategory extends SelectionAdapter {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final int i = categoryList.getSelectionIndex();
			if (i > -1) {
				final String name = categoryList.getItem(i);
				namesList.setItems(namesArray(name));
				bAddFile.setEnabled(true);
			} else {
				namesList.setItems(new String[0]);
				bAddFile.setEnabled(false);
			}
		}
	}

	/**
	 * The Class AddFile.
	 */
	class AddFile extends SelectionAdapter {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final InputDialog dialog =
					new InputDialog(getShell(), "New Name", "File name pattern like *.java, my.xml:", null, newText -> {
						if (newText != null && newText.trim().length() > 0) { return null; }
						return "";
					});

			if (dialog.open() == Window.OK) {
				addFileName(dialog.getValue());
			}

		}
	}

	/**
	 * The Class RemoveFile.
	 */
	class RemoveFile extends SelectionAdapter {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final int i = namesList.getSelectionIndex();
			if (i > -1) {
				final int n = categoryList.getSelectionIndex();
				if (n > -1) {
					final String key = categoryList.getItem(n);
					final String value = namesList.getItem(i);
					final LinkedHashSet<String> fNames = categoryFiles.get(key);
					fNames.remove(value);
					namesList.remove(i);
					final Object o = folder.getItem(n + 1).getData();
					if (o instanceof BoxSettingsTab) {
						((BoxSettingsTab) o).getSettings().setFileNames(new ArrayList<>(fNames));
					}
				}
			}
		}
	}
}
