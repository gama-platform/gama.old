/*********************************************************************************************
 *
 *
 * 'GamaPreferencesView.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.views;

import java.util.*;
import java.util.List;
import org.eclipse.jface.preference.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.dialogs.WorkbenchPreferenceDialog;
import msi.gama.common.GamaPreferences;
import msi.gama.common.GamaPreferences.*;
import msi.gama.common.interfaces.IParameterEditor;
import msi.gama.gui.parameters.*;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.controls.*;
import msi.gama.runtime.GAMA;

/**
 * Class GamaPreferencesView.
 *
 * @author drogoul
 * @since 31 août 2013
 *
 */
public class GamaPreferencesView /* implements IWorkbenchPreferenceContainer, IPreferencePageContainer */ {

	Map<String, IPreferenceNode> preferencePages = new LinkedHashMap();
	static Map<String, String> preferenceNames = new LinkedHashMap();
	public static Map<String, Image> prefs_images = new LinkedHashMap();

	static GamaPreferencesView instance;

	public static void show() {
		if ( instance == null || instance.shell == null || instance.shell.isDisposed() ) {
			instance = new GamaPreferencesView(SwtGui.getShell());
		}
		for ( IParameterEditor ed : instance.editors.values() ) {
			if ( ed.getParam() instanceof GamaPreferences.Entry ) {
				if ( ((GamaPreferences.Entry) ed.getParam()).getKey().equals("editor.info.enabled") ) {
					System.out.println("editor.info.enabled");
				}
			}
			ed.updateValue();

		}
		instance.open();
	}

	static {
		preferenceNames.put("msi.gama.lang.gaml.Gaml.coloring", "Code");
		preferenceNames.put("org.eclipse.ui.preferencePages.GeneralTextEditor", "Editor");
		preferenceNames.put("org.eclipse.ui.preferencePages.Workspace", "Workspace");
		prefs_images.put(GamaPreferences.GENERAL, IGamaIcons.PREFS_GENERAL.image());
		prefs_images.put(GamaPreferences.UI, GamaIcons.create("prefs.ui2").image());
		prefs_images.put(GamaPreferences.SIMULATION, GamaIcons.create("prefs.simulations2").image());
		prefs_images.put(GamaPreferences.DISPLAY, IGamaIcons.PREFS_DISPLAY.image());
		// prefs_images.put(GamaPreferences.CODE, IGamaIcons.PREFS_CODE.image());
		prefs_images.put(GamaPreferences.EDITOR, IGamaIcons.PREFS_EDITOR.image());
		// prefs_images.put(GamaPreferences.WORKSPACE, IGamaIcons.PREFS_WORKSPACE.image());
		prefs_images.put(GamaPreferences.LIBRARIES, IGamaIcons.PREFS_LIBS.image());

	}

	Shell parentShell, shell;
	CTabFolder tabFolder;
	final Map<String, IParameterEditor> editors = new LinkedHashMap();
	final Map<String, Object> modelValues = new LinkedHashMap();

	private GamaPreferencesView(final Shell parent) {
		parentShell = parent;
		shell = new Shell(parentShell, SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL | SWT.SHEET);
		final GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = gridLayout.marginHeight = 5;
		gridLayout.horizontalSpacing = gridLayout.verticalSpacing = 5;
		shell.setLayout(gridLayout);
		PreferenceManager preferenceManager = PlatformUI.getWorkbench().getPreferenceManager();

		// We clean the default preference manager to remove useless preferences
		for ( Object elem : preferenceManager.getElements(PreferenceManager.POST_ORDER) ) {
			if ( elem instanceof IPreferenceNode ) {
				String id = ((IPreferenceNode) elem).getId();
				if ( preferenceNames.containsKey(id) ) {
					preferencePages.put(preferenceNames.get(id), (IPreferenceNode) elem);
				}
				if ( id.contains("debug.ui") || id.contains("help.ui") || id.contains("search") ||
					id.contains("Spelling") || id.contains("Linked") || id.contains("Perspectives") ||
					id.contains("Content") ) {
					preferenceManager.remove((IPreferenceNode) elem);
				}
				// scope.getGui().debug(((IPreferenceNode) elem).getId());
				// preferenceManager.remove((IPreferenceNode) elem);
			}
		}
		buildContents();
	}

	private void buildContents() {
		tabFolder = new CTabFolder(shell, SWT.TOP | SWT.NO_TRIM);
		tabFolder.setBorderVisible(false);
		tabFolder.setBackgroundMode(SWT.INHERIT_DEFAULT);
		tabFolder.setSimple(true); // rounded tabs
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		final Label sep = new Label(this.shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		sep.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		Map<String, Map<String, List<Entry>>> prefs = GamaPreferences.organizePrefs();
		for ( String tabName : prefs.keySet() ) {
			CTabItem item = new CTabItem(tabFolder, SWT.NONE);
			item.setText(tabName);
			item.setImage(prefs_images.get(tabName));
			item.setShowClose(false);
			buildContentsFor(item, prefs.get(tabName));
		}

		// Aborted attempt to put Eclipse pages within this view. The problem is that the preference store seems to be
		// not set for some preferences pages (Editors in particular).

		// for ( String tabName : preferencePages.keySet() ) {
		// CTabItem item = new CTabItem(tabFolder, SWT.None);
		// item.setText(tabName);
		// item.setImage(tabImages.get(tabName));
		// item.setShowClose(false);
		// Composite c = new Composite(tabFolder, SWT.None);
		// GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		// c.setLayoutData(data);
		// FillLayout layout = new FillLayout();
		// layout.marginHeight = 5;
		// layout.marginWidth = 5;
		// c.setLayout(layout);
		// IPreferenceNode node = preferencePages.get(tabName);
		// node.disposeResources();
		// node.createPage();
		// node.getPage().createControl(c);
		// c.layout();
		// item.setControl(c);
		// }

		buildButtons();

	}

	private void buildContentsFor(final CTabItem tab, final Map<String, List<Entry>> entries) {
		ParameterExpandBar viewer = new ParameterExpandBar(tab.getParent(), SWT.V_SCROLL);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		viewer.setLayoutData(data);
		// ?
		viewer.computeSize(tab.getBounds().x, SWT.DEFAULT);
		//
		viewer.setSpacing(10);
		tab.setControl(viewer);
		for ( String groupName : entries.keySet() ) {
			ParameterExpandItem item = new ParameterExpandItem(viewer, entries.get(groupName), SWT.NONE, null);
			item.setText(groupName);
			Composite compo = new Composite(viewer, SWT.NONE);
			GridLayout layout = new GridLayout(2, false);
			layout.marginHeight = 0;
			layout.verticalSpacing = 0;
			compo.setLayout(layout);
			compo.setBackground(viewer.getBackground());
			buildGroupContents(compo, entries.get(groupName));

			item.setControl(compo);
			item.setHeight(compo.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
			// item.setImage(GamaIcons.menu_action);
			item.setExpanded(true);

		}
	}

	final Map<String, Boolean> activations = new HashMap();

	private void checkActivables(final Entry e, final Object value) {
		if ( e.getActivable() != null ) {
			for ( String activable : e.getActivable() ) {
				IParameterEditor ed = editors.get(activable);
				if ( ed == null ) {
					if ( value instanceof Boolean ) {
						activations.put(activable, (Boolean) value);
					} else {
						activations.put(activable, true);
					}
				} else {
					if ( value instanceof Boolean ) {
						ed.setActive((Boolean) value);
					} else {
						ed.setActive(true);
					}
				}
			}
		}
		if ( e.getDeactivable() != null && value instanceof Boolean ) {
			for ( String deactivable : e.getDeactivable() ) {
				IParameterEditor ed = editors.get(deactivable);
				if ( ed == null ) {
					activations.put(deactivable, !(Boolean) value);
				} else {
					ed.setActive(!(Boolean) value);
				}
			}
		}
	}

	private void buildGroupContents(final Composite compo, final List<Entry> list) {

		for ( final Entry e : list ) {
			modelValues.put(e.getKey(), e.getValue());
			// Initial activations of editors
			checkActivables(e, e.getValue());
			e.addChangeListener(new IPreferenceChangeListener() {

				@Override
				public boolean beforeValueChange(final Object newValue) {
					return true;
				}

				@Override
				public void afterValueChange(final Object value) {
					if ( e.acceptChange(value) ) {
						modelValues.put(e.getKey(), value);
						checkActivables(e, value);
					} else {
						GamaPreferencesView.this.showError("" + value + " is not accepted for parameter " + e.getKey());
					}

				}
			});
			boolean isSubParameter = activations.containsKey(e.getKey());

			AbstractEditor ed = EditorFactory.create(compo, e, isSubParameter);
			// ed.acceptPopup(false);
			editors.put(e.getKey(), ed);
		}

		// Initial activations of editors
		for ( String s : activations.keySet() ) {
			IParameterEditor ed = editors.get(s);
			if ( ed != null ) {
				ed.setActive(activations.get(s));
			}
		}
		activations.clear();
		compo.layout();
		compo.pack(true);
	}

	/**
	 * @param string
	 */
	protected void showError(final String string) {
		// TODO make it a proper component of the view
		GAMA.getGui().debug("Error in preferences : " + string);
	}

	private void buildButtons() {
		Composite group1 = new Composite(shell, SWT.NONE);
		group1.setLayout(new FillLayout());
		final GridData gridDataGroup1 = new GridData(GridData.BEGINNING, GridData.END, true, false);
		gridDataGroup1.widthHint = 300;
		group1.setLayoutData(gridDataGroup1);

		final Button buttonRevert = new Button(group1, SWT.PUSH | SWT.FLAT);
		buttonRevert.setText("Revert to defaults");
		buttonRevert.setImage(IGamaIcons.ACTION_REVERT.image());
		buttonRevert.setToolTipText("Restore default values for all preferences");

		final Button buttonAdvanced = new Button(group1, SWT.PUSH | SWT.FLAT);
		buttonAdvanced.setText("Advanced...");
		buttonAdvanced.setToolTipText("Access to advanced preferences");

		Composite group2 = new Composite(shell, SWT.NONE);
		group2.setLayout(new FillLayout());
		final GridData gridDataGroup2 = new GridData(GridData.END, GridData.END, true, false);
		gridDataGroup2.widthHint = 200;
		group2.setLayoutData(gridDataGroup2);

		final Button buttonCancel = new Button(group2, SWT.PUSH);
		buttonCancel.setText("Cancel");
		buttonCancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				// shell.close();
				shell.setVisible(false);
			}

		});

		final Button buttonOK = new Button(group2, SWT.PUSH);
		buttonOK.setText("Save");
		buttonOK.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				// for ( IPreferenceNode pn : preferencePages.values() ) {
				// pn.getPage().performOk();
				// }
				// shell.close();
				shell.setVisible(false);
				GamaPreferences.setNewPreferences(modelValues);
			}

		});

		this.shell.setDefaultButton(buttonOK);

		buttonRevert.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				GamaPreferences.revertToDefaultValues(modelValues);
				for ( IParameterEditor ed : editors.values() ) {
					ed.updateValue();
				}
			}

		});

		buttonAdvanced.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				shell.setVisible(false);
				// for ( IPreferenceNode pn : preferencePages.values() ) {
				// pn.disposeResources();
				// pn.createPage();
				// }
				GAMA.getGui().asyncRun(new Runnable() {

					@Override
					public void run() {
						PreferenceDialog pd = WorkbenchPreferenceDialog.createDialogOn(parentShell, null);
						pd.open();
						shell.setVisible(true);
					}
				});

			}

		});

	}

	public void open() {
		shell.setSize(shell.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		shell.open();

		while (!this.shell.isDisposed() && this.shell.isVisible()) {
			if ( !this.shell.getDisplay().readAndDispatch() ) {
				this.shell.getDisplay().sleep();
			}
		}

	}

	// /**
	// * Method openPage()
	// * @see org.eclipse.ui.preferences.IWorkbenchPreferenceContainer#openPage(java.lang.String, java.lang.Object)
	// */
	// @Override
	// public boolean openPage(final String preferencePageId, final Object data) {
	// return false;
	// }
	//
	// /**
	// * Method getWorkingCopyManager()
	// * @see org.eclipse.ui.preferences.IWorkbenchPreferenceContainer#getWorkingCopyManager()
	// */
	// @Override
	// public IWorkingCopyManager getWorkingCopyManager() {
	// return new WorkingCopyManager();
	// }
	//
	// /**
	// * Method registerUpdateJob()
	// * @see
	// org.eclipse.ui.preferences.IWorkbenchPreferenceContainer#registerUpdateJob(org.eclipse.core.runtime.jobs.Job)
	// */
	// @Override
	// public void registerUpdateJob(final Job job) {}
	//
	// /**
	// * Method getPreferenceStore()
	// * @see org.eclipse.jface.preference.IPreferencePageContainer#getPreferenceStore()
	// */
	// @Override
	// public IPreferenceStore getPreferenceStore() {
	// return PlatformUI.getPreferenceStore();
	// }
	//
	// /**
	// * Method updateButtons()
	// * @see org.eclipse.jface.preference.IPreferencePageContainer#updateButtons()
	// */
	// @Override
	// public void updateButtons() {}
	//
	// /**
	// * Method updateMessage()
	// * @see org.eclipse.jface.preference.IPreferencePageContainer#updateMessage()
	// */
	// @Override
	// public void updateMessage() {}
	//
	// /**
	// * Method updateTitle()
	// * @see org.eclipse.jface.preference.IPreferencePageContainer#updateTitle()
	// */
	// @Override
	// public void updateTitle() {}

}
