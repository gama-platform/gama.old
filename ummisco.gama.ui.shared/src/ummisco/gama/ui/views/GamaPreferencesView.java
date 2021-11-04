/*******************************************************************************************************
 *
 * GamaPreferencesView.java, in ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.views;

import static msi.gama.application.workbench.ThemeHelper.isDark;
import static ummisco.gama.ui.resources.IGamaColors.DARK_GRAY;
import static ummisco.gama.ui.resources.IGamaColors.VERY_LIGHT_GRAY;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.dialogs.WorkbenchPreferenceDialog;

import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.preferences.Pref;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.GAMA;
import msi.gaml.types.IType;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.ui.controls.ParameterExpandBar;
import ummisco.gama.ui.controls.ParameterExpandItem;
import ummisco.gama.ui.dialogs.Messages;
import ummisco.gama.ui.interfaces.IParameterEditor;
import ummisco.gama.ui.parameters.AbstractEditor;
import ummisco.gama.ui.parameters.EditorFactory;
import ummisco.gama.ui.parameters.EditorsGroup;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.toolbar.Selector;

/**
 * Class GamaPreferencesView.
 *
 * @author drogoul
 * @since 31 août 2013
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaPreferencesView {

	static {
		DEBUG.ON();
	}

	/** The dialog location. */
	static Pref<GamaPoint> DIALOG_LOCATION = GamaPreferences.create("dialog_location",
			"Location of the preferences dialog on screen", new GamaPoint(-1, -1), IType.POINT, false).hidden();

	/** The dialog size. */
	static Pref<GamaPoint> DIALOG_SIZE = GamaPreferences.create("dialog_size",
			"Size of the preferences dialog on screen", new GamaPoint(-1, -1), IType.POINT, false).hidden();

	/** The dialog tab. */
	static Pref<Integer> DIALOG_TAB = GamaPreferences
			.create("dialog_tab", "Tab selected in the preferences dialog", -1, IType.INT, false).hidden();

	/** The prefs images. */
	public static Map<String, Image> prefs_images = new LinkedHashMap();

	/** The nb divisions. */
	public static final int NB_DIVISIONS = 2;

	/** The instance. */
	static GamaPreferencesView instance;

	/** The restart required. */
	static boolean restartRequired;

	/**
	 * Show.
	 */
	public static void show() {
		if (instance == null || instance.shell == null || instance.shell.isDisposed()) {
			instance = new GamaPreferencesView(WorkbenchHelper.getShell());
		}
		for (final IParameterEditor ed : instance.editors.values()) { ed.updateWithValueOfParameter(); }
		instance.open();
	}

	/**
	 * Preload.
	 */
	public static void preload() {
		DEBUG.TIMER(DEBUG.PAD("> GAMA: preferences", 45, ' ') + DEBUG.PAD(" loaded in", 15, '_'), () -> {
			WorkbenchHelper.run(() -> {
				if (instance == null || instance.shell == null || instance.shell.isDisposed()) {
					instance = new GamaPreferencesView(WorkbenchHelper.getShell());
				}

			});
			for (final IParameterEditor ed : instance.editors.values()) { ed.updateWithValueOfParameter(); }
		});

	}

	static {
		prefs_images.put(GamaPreferences.Interface.NAME, GamaIcons.create(IGamaIcons.PREFS_GENERAL).image());
		prefs_images.put(GamaPreferences.Modeling.NAME, GamaIcons.create(IGamaIcons.PREFS_EDITOR).image());
		prefs_images.put(GamaPreferences.Runtime.NAME, GamaIcons.create("prefs/prefs.simulations2").image());
		prefs_images.put(GamaPreferences.Simulations.NAME, GamaIcons.create("prefs/prefs.runtime2").image());
		prefs_images.put(GamaPreferences.Displays.NAME, GamaIcons.create("prefs/prefs.ui2").image());
		prefs_images.put(GamaPreferences.External.NAME, GamaIcons.create(IGamaIcons.PREFS_LIBS).image());

	}

	/** The shell. */
	Shell parentShell, shell;

	/** The tab folder. */
	CTabFolder tabFolder;

	/** The editors. */
	final Map<String, IParameterEditor> editors = new LinkedHashMap();

	/** The model values. */
	final Map<String, Object> modelValues = new LinkedHashMap();

	/**
	 * Instantiates a new gama preferences view.
	 *
	 * @param parent
	 *            the parent
	 */
	private GamaPreferencesView(final Shell parent) {
		parentShell = parent;
		shell = new Shell(parentShell, SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).spacing(5, 5).applyTo(shell);
		tabFolder = new CTabFolder(shell, SWT.TOP | SWT.NO_TRIM);
		tabFolder.setBorderVisible(true);
		tabFolder.setMRUVisible(true);
		tabFolder.setSimple(false); // rounded tabs
		GridDataFactory.fillDefaults().grab(true, true).span(2, 1).applyTo(tabFolder);
		final var prefs = GamaPreferences.organizePrefs();
		for (final String tabName : prefs.keySet()) {
			final var item = new CTabItem(tabFolder, SWT.NONE);
			item.setText(tabName);
			item.setImage(prefs_images.get(tabName));
			item.setShowClose(false);
			buildContentsFor(item, prefs.get(tabName));
		}
		buildButtons();
		shell.layout();
	}

	/**
	 * Builds the contents for.
	 *
	 * @param tab
	 *            the tab
	 * @param entries
	 *            the entries
	 */
	private void buildContentsFor(final CTabItem tab, final Map<String, List<Pref>> entries) {
		final var viewer = new ParameterExpandBar(tab.getParent(), SWT.V_SCROLL);
		viewer.setBackground(!isDark() ? VERY_LIGHT_GRAY.color() : DARK_GRAY.darker());
		viewer.setSpacing(5);
		tab.setControl(viewer);

		for (final String groupName : entries.keySet()) {
			final var item = new ParameterExpandItem(viewer, entries.get(groupName), SWT.NONE, null);
			item.setText(groupName);
			final var compo = new Composite(viewer, SWT.NONE);
			item.setControl(compo);
			// Build the contents *after* setting the control to the item.
			buildGroupContents(compo, entries.get(groupName));
			item.setHeight(compo.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
			item.setExpanded(true);
		}

	}

	/** The activations. */
	final Map<String, Boolean> activations = new HashMap();

	/**
	 * Check activables.
	 *
	 * @param e
	 *            the e
	 * @param value
	 *            the value
	 */
	void checkActivables(final Pref e, final Object value) {
		if (e.getEnablement() != null) {
			for (final String activable : e.getEnablement()) {
				final var ed = editors.get(activable);
				if (ed == null) {
					if (value instanceof Boolean) {
						activations.put(activable, (Boolean) value);
					} else {
						activations.put(activable, true);
					}
				} else if (value instanceof Boolean) {
					ed.setActive((Boolean) value);
				} else {
					ed.setActive(true);
				}
			}
		}
		if (e.getDisablement() != null && value instanceof Boolean) {
			for (final String deactivable : e.getDisablement()) {
				final var ed = editors.get(deactivable);
				if (ed == null) {
					activations.put(deactivable, !(Boolean) value);
				} else {
					ed.setActive(!(Boolean) value);
				}
			}
		}
	}

	/**
	 * Check refreshables.
	 *
	 * @param e
	 *            the e
	 * @param value
	 *            the value
	 */
	void checkRefreshables(final Pref e, final Object value) {
		if (e.getRefreshment() != null) {
			for (final String activable : e.getRefreshment()) {
				final var ed = editors.get(activable);
				if (ed != null) { ed.updateWithValueOfParameter(); }
			}
		}
	}

	/**
	 * Builds the group contents.
	 *
	 * @param compo
	 *            the compo
	 * @param list
	 *            the list
	 */
	private void buildGroupContents(final Composite compo, final List<Pref> list) {
		GridLayoutFactory.fillDefaults().numColumns(NB_DIVISIONS).spacing(5, 0).equalWidth(true).applyTo(compo);
		final var comps = new EditorsGroup[NB_DIVISIONS];
		for (var i = 0; i < NB_DIVISIONS; i++) { comps[i] = new EditorsGroup(compo, SWT.NONE); }
		var i = 0;
		for (final Pref e : list) {
			modelValues.put(e.getKey(), e.getValue());
			// Initial activations of editors
			checkActivables(e, e.getValue());
			e.onChange(value -> {
				if (e.acceptChange(value)) {
					modelValues.put(e.getKey(), value);
					checkActivables(e, value);
					checkRefreshables(e, value);
					if (e.isRestartRequired()) { setRestartRequired(); }
				} else {
					GamaPreferencesView.this.showError("" + value + " is not accepted for parameter " + e.getKey());
				}

			});
			final var isSubParameter = activations.containsKey(e.getKey());
			final var ed = EditorFactory.create(null, comps[(int) (i * ((double) NB_DIVISIONS / list.size()))], e,
					isSubParameter, true);
			if (e.isDisabled()) {
				ed.setActive(false);
			} else {
				final var m = getMenuFor(e, ed);
				final var l = ed.getLabel();
				l.setMenu(m);
			}
			editors.put(e.getKey(), ed);
			i++;
		}

		// Initial activations of editors
		for (final String s : activations.keySet()) {
			final var ed = editors.get(s);
			if (ed != null) { ed.setActive(activations.get(s)); }
		}
		activations.clear();
		compo.layout();
		compo.pack(true);
	}

	/**
	 * Gets the menu for.
	 *
	 * @param e
	 *            the e
	 * @param ed
	 *            the ed
	 * @return the menu for
	 */
	private static Menu getMenuFor(final Pref e, final AbstractEditor ed) {
		final var m = ed.getLabel().createMenu();
		final var title = new MenuItem(m, SWT.PUSH);
		title.setEnabled(false);

		if (e.inGaml()) {
			title.setText("Use gama." + e.getKey() + " in GAML");
			@SuppressWarnings ("unused") final var sep = new MenuItem(m, SWT.SEPARATOR);
			final var i = new MenuItem(m, SWT.PUSH);
			i.setText("Copy name to clipboard");
			i.addSelectionListener((Selector) se -> WorkbenchHelper.copy("gama." + e.getKey()));
		} else {
			title.setText("Not assignable from GAML");
			@SuppressWarnings ("unused") final var sep = new MenuItem(m, SWT.SEPARATOR);
		}
		final var i2 = new MenuItem(m, SWT.PUSH);
		i2.setText("Revert to default value");
		i2.addSelectionListener((Selector) se -> {
			e.set(e.getInitialValue(GAMA.getRuntimeScope()));
			ed.updateWithValueOfParameter();
		});
		return m;
	}

	/**
	 * @param string
	 */
	protected void showError(final String string) {
		// TODO make it a proper component of the view
		DEBUG.LOG("Error in preferences : " + string);
	}

	/**
	 * Builds the buttons.
	 */
	private void buildButtons() {
		final var doc = new Label(shell, SWT.NONE);
		doc.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		// doc.setFont(GamaFonts.boldHelpFont);
		doc.setText(
				"Some preferences can also be set in GAML, using 'gama.pref_name <- new_value;'. 'pref_name' is displayed in the contextual menu of each preference");

		final var group1 = new Composite(shell, SWT.NONE);
		group1.setLayout(new FillLayout());
		final var gridDataGroup1 = new GridData(GridData.BEGINNING, GridData.END, true, false);
		// gridDataGroup1.widthHint = 300;
		group1.setLayoutData(gridDataGroup1);

		final var buttonRevert = new Button(group1, SWT.PUSH | SWT.FLAT);
		buttonRevert.setText("Revert to defaults");
		buttonRevert.setImage(GamaIcons.create(IGamaIcons.ACTION_REVERT).image());
		buttonRevert.setToolTipText("Restore default values for all preferences");

		final var buttonAdvanced = new Button(group1, SWT.PUSH | SWT.FLAT);
		buttonAdvanced.setText("Advanced...");
		buttonAdvanced.setToolTipText("Access to advanced preferences");

		final var buttonImport = new Button(group1, SWT.PUSH | SWT.FLAT);
		buttonImport.setText("Import...");
		buttonImport.setToolTipText("Import preferences from a file...");
		buttonImport.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final var fd = new FileDialog(shell, SWT.OPEN);
				fd.setFilterExtensions(new String[] { "*.prefs" });
				final var path = fd.open();
				if (path == null) return;
				GamaPreferences.applyPreferencesFrom(path, modelValues);
				for (final IParameterEditor ed : editors.values()) { ed.updateWithValueOfParameter(); }
			}

		});

		final var buttonExportToGaml = new Button(group1, SWT.PUSH | SWT.FLAT);
		buttonExportToGaml.setText("Export to GAML");
		buttonExportToGaml.setToolTipText("Export preferences to a model that can be run to restore or share them...");
		buttonExportToGaml.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final var fd = new FileDialog(shell, SWT.SAVE);
				fd.setFileName("Preferences.gaml");
				fd.setFilterExtensions(new String[] { "*.gaml" });
				fd.setOverwrite(false);
				final var path = fd.open();
				if (path == null) return;
				GamaPreferences.savePreferencesToGAML(path);
			}

		});

		final var buttonExport = new Button(group1, SWT.PUSH | SWT.FLAT);
		buttonExport.setText("Export to preferences");
		buttonExport
				.setToolTipText("Export preferences in a format suitable to reimport them in another instance of GAMA");
		buttonExport.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final var fd = new FileDialog(shell, SWT.SAVE);
				fd.setFileName("gama.prefs");
				fd.setFilterExtensions(new String[] { "*.prefs" });
				fd.setOverwrite(false);
				final var path = fd.open();
				if (path == null) return;
				GamaPreferences.savePreferencesToProperties(path);
			}

		});

		final var group2 = new Composite(shell, SWT.NONE);
		group2.setLayout(new FillLayout());
		final var gridDataGroup2 = new GridData(GridData.END, GridData.END, true, false);
		gridDataGroup2.widthHint = 200;
		group2.setLayoutData(gridDataGroup2);

		final var buttonCancel = new Button(group2, SWT.PUSH);
		buttonCancel.setText("Cancel");
		buttonCancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				close();
			}

		});

		final var buttonOK = new Button(group2, SWT.PUSH);
		buttonOK.setText("Save");
		buttonOK.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				GamaPreferences.setNewPreferences(modelValues);
				if (restartRequired) {
					restartRequired = false;
					final var restart = Messages.confirm("Restart GAMA",
							"It is advised to restart GAMA after these changes. Restart now ?");
					if (restart) {
						close();
						PlatformUI.getWorkbench().restart(true);
					}
				} else {
					close();
				}
			}

		});

		this.shell.setDefaultButton(buttonOK);

		buttonRevert.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (!Messages.question("Revert to default",
						"Do you want to revert all preferences to their default values ? A restart of the platform will be performed immediately"))
					return;
				GamaPreferences.revertToDefaultValues(modelValues);
				PlatformUI.getWorkbench().restart(true);
			}

		});

		buttonAdvanced.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				close();
				WorkbenchHelper.asyncRun(() -> {
					final PreferenceDialog pd = WorkbenchPreferenceDialog.createDialogOn(parentShell, null);
					pd.open();
					shell.setVisible(true);
				});

			}

		});

		shell.addDisposeListener(e -> { saveDialogProperties(); });
	}

	/**
	 * Close.
	 */
	void close() {
		shell.setVisible(false);
	}

	/**
	 * Save location.
	 */
	private void saveLocation() {
		final var p = shell.getLocation();
		DIALOG_LOCATION.set(new GamaPoint(p.x, p.y)).save();
	}

	/**
	 * Save size.
	 */
	private void saveSize() {
		final var s = shell.getSize();
		DIALOG_SIZE.set(new GamaPoint(s.x, s.y)).save();
	}

	/**
	 * Save tab.
	 */
	private void saveTab() {
		final var index = tabFolder.getSelectionIndex();
		DIALOG_TAB.set(index).save();
	}

	/**
	 * Save dialog properties.
	 */
	private void saveDialogProperties() {
		if (shell.isDisposed()) return;
		saveLocation();
		saveSize();
		saveTab();
	}

	/**
	 * Open.
	 */
	public void open() {
		final var loc = DIALOG_LOCATION.getValue();
		final var size = DIALOG_SIZE.getValue();
		final int tab = DIALOG_TAB.getValue();
		var x = (int) loc.x;
		var y = (int) loc.y;
		var width = (int) size.x;
		var height = (int) size.y;
		if (loc.x == -1 || loc.y == -1 || size.x == -1 || size.y == -1) {
			final var p = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
			final var bounds = WorkbenchHelper.getDisplay().getBounds();
			width = Math.min(p.x, bounds.width - 200);
			height = Math.min(p.y, bounds.height - 200);
			x = (bounds.width - width) / 2;
			y = (bounds.height - height) / 2;
		}

		tabFolder.setSelection(tab);
		shell.setLocation(x, y);
		shell.setSize(width, height);
		shell.open();

		while (!this.shell.isDisposed() && this.shell.isVisible()) {
			if (!this.shell.getDisplay().readAndDispatch()) { this.shell.getDisplay().sleep(); }
		}

		saveDialogProperties();

	}

	/**
	 * Sets the restart required.
	 */
	public static void setRestartRequired() {
		restartRequired = true;

	}

}
