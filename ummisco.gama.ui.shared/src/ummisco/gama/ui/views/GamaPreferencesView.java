/*********************************************************************************************
 *
 * 'GamaPreferencesView.java, in plugin ummisco.gama.ui.shared, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.views;

import java.util.ArrayList;
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
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
import msi.gama.common.preferences.IPreferenceChangeListener;
import msi.gama.common.preferences.Pref;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.runtime.GAMA;
import msi.gama.util.GamaColor;
import msi.gaml.types.IType;
import ummisco.gama.ui.controls.ParameterExpandBar;
import ummisco.gama.ui.controls.ParameterExpandItem;
import ummisco.gama.ui.dialogs.Messages;
import ummisco.gama.ui.interfaces.IParameterEditor;
import ummisco.gama.ui.parameters.AbstractEditor;
import ummisco.gama.ui.parameters.EditorFactory;
import ummisco.gama.ui.resources.GamaFonts;
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

	static Pref<GamaPoint> DIALOG_LOCATION = GamaPreferences.create("dialog_location",
			"Location of the preferences dialog on screen", new GamaPoint(-1, -1), IType.POINT).hidden();
	static Pref<GamaPoint> DIALOG_SIZE = GamaPreferences
			.create("dialog_size", "Size of the preferences dialog on screen", new GamaPoint(-1, -1), IType.POINT)
			.hidden();
	static Pref<Integer> DIALOG_TAB =
			GamaPreferences.create("dialog_tab", "Tab selected in the preferences dialog", -1, IType.INT).hidden();

	public static Map<String, Image> prefs_images = new LinkedHashMap();
	public static int NB_DIVISIONS = 2;

	static GamaPreferencesView instance;
	private static boolean restartRequired;

	public static void show() {
		if (instance == null || instance.shell == null || instance.shell.isDisposed()) {
			instance = new GamaPreferencesView(WorkbenchHelper.getShell());
		}
		for (final IParameterEditor ed : instance.editors.values()) {
			ed.updateValue(true);

		}
		instance.open();
	}

	static {
		// preferenceNames.put("msi.gama.lang.gaml.Gaml.coloring", "Code");
		// preferenceNames.put("org.eclipse.ui.preferencePages.GeneralTextEditor", "Editor");
		// preferenceNames.put("org.eclipse.ui.preferencePages.Workspace", "Workspace");
		prefs_images.put(GamaPreferences.Interface.NAME, GamaIcons.create(IGamaIcons.PREFS_GENERAL).image());
		prefs_images.put(GamaPreferences.Modeling.NAME, GamaIcons.create(IGamaIcons.PREFS_EDITOR).image());
		prefs_images.put(GamaPreferences.Runtime.NAME, GamaIcons.create("prefs.simulations2").image());
		// prefs_images.put(GamaPreferences.Experiments.NAME, GamaIcons.create("prefs.simulations2").image());
		prefs_images.put(GamaPreferences.Simulations.NAME, GamaIcons.create("prefs.runtime2").image());
		prefs_images.put(GamaPreferences.Displays.NAME, GamaIcons.create("prefs.ui2").image());
		// prefs_images.put(GamaPreferences.OpenGL.NAME, GamaIcons.create("prefs.opengl2").image());
		prefs_images.put(GamaPreferences.External.NAME, GamaIcons.create(IGamaIcons.PREFS_LIBS).image());

	}

	static class Group {
		Composite compo;
		int nb_divisions;
	}

	Shell parentShell, shell;
	CTabFolder tabFolder;
	List<ParameterExpandBar> contents = new ArrayList();
	final Map<String, IParameterEditor> editors = new LinkedHashMap();
	final Map<IParameterEditor, Composite> groups = new HashMap();
	final Map<String, Object> modelValues = new LinkedHashMap();

	private GamaPreferencesView(final Shell parent) {
		parentShell = parent;
		shell = new Shell(parentShell, SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL);
		final GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = gridLayout.marginHeight = 5;
		gridLayout.horizontalSpacing = gridLayout.verticalSpacing = 5;
		shell.setLayout(gridLayout);
		buildContents();
	}

	private void buildContents() {
		tabFolder = new CTabFolder(shell, SWT.TOP | SWT.NO_TRIM);
		tabFolder.setBorderVisible(true);
		tabFolder.setBackgroundMode(SWT.INHERIT_DEFAULT);
		tabFolder.setMRUVisible(true);
		tabFolder.setSimple(false); // rounded tabs
		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		final Map<String, Map<String, List<Pref>>> prefs = GamaPreferences.organizePrefs();
		for (final String tabName : prefs.keySet()) {
			final CTabItem item = new CTabItem(tabFolder, SWT.NONE);
			item.setFont(GamaFonts.getNavigHeaderFont());
			item.setText(tabName);
			item.setImage(prefs_images.get(tabName));
			item.setShowClose(false);
			buildContentsFor(item, prefs.get(tabName));
		}
		buildButtons();
		shell.layout();
	}

	private void buildContentsFor(final CTabItem tab, final Map<String, List<Pref>> entries) {
		final ParameterExpandBar viewer = new ParameterExpandBar(tab.getParent(), SWT.V_SCROLL);
		contents.add(viewer);
		final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		viewer.setLayoutData(data);
		// ?
		viewer.computeSize(tab.getBounds().x, SWT.DEFAULT);
		//
		viewer.setSpacing(5);
		tab.setControl(viewer);
		for (final String groupName : entries.keySet()) {
			final ParameterExpandItem item = new ParameterExpandItem(viewer, entries.get(groupName), SWT.NONE, null);
			item.setText(groupName);
			item.setColor(new GamaColor(230, 230, 230, 255));
			final Composite compo = new Composite(viewer, SWT.NONE);
			compo.setBackground(viewer.getBackground());
			buildGroupContents(compo, entries.get(groupName), NB_DIVISIONS);
			item.setControl(compo);
			item.setHeight(compo.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
			item.setExpanded(true);
		}

	}

	final Map<String, Boolean> activations = new HashMap();

	private void checkActivables(final Pref e, final Object value) {
		if (e.getActivable() != null) {
			for (final String activable : e.getActivable()) {
				final IParameterEditor ed = editors.get(activable);
				if (ed == null) {
					if (value instanceof Boolean) {
						activations.put(activable, (Boolean) value);
					} else {
						activations.put(activable, true);
					}
				} else {
					if (value instanceof Boolean) {
						ed.setActive((Boolean) value);
					} else {
						ed.setActive(true);
					}
				}
			}
		}
		if (e.getDeactivable() != null && value instanceof Boolean) {
			for (final String deactivable : e.getDeactivable()) {
				final IParameterEditor ed = editors.get(deactivable);
				if (ed == null) {
					activations.put(deactivable, !(Boolean) value);
				} else {
					ed.setActive(!(Boolean) value);
				}
			}
		}
	}

	private void buildGroupContents(final Composite compo, final List<Pref> list, final int nbColumns) {
		GridLayoutFactory.fillDefaults().numColumns(NB_DIVISIONS).spacing(0, 0).equalWidth(true).applyTo(compo);
		final Composite[] comps = new Composite[nbColumns];
		for (int i = 0; i < nbColumns; i++) {
			comps[i] = new Composite(compo, SWT.BORDER);
			comps[i].setBackground(compo.getBackground());
			GridLayoutFactory.fillDefaults().numColumns(2).spacing(0, 0).equalWidth(false).applyTo(comps[i]);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(comps[i]);
		}
		// final int compositeIndex = 0;

		int i = 0;
		for (final Pref e : list) {
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
					if (e.acceptChange(value)) {
						modelValues.put(e.getKey(), value);
						checkActivables(e, value);
					} else {
						GamaPreferencesView.this.showError("" + value + " is not accepted for parameter " + e.getKey());
					}

				}
			});
			final boolean isSubParameter = activations.containsKey(e.getKey());
			final AbstractEditor ed = EditorFactory.create(null, comps[(int) (i * ((double) nbColumns / list.size()))],
					e, isSubParameter, true);
			if (e.isDisabled()) {
				ed.setActive(false);
			} else {
				final Menu m = getMenuFor(e, ed);
				final Label l = ed.getLabel();
				l.setMenu(m);
			}
			editors.put(e.getKey(), ed);
			i++;
		}

		// Initial activations of editors
		for (final String s : activations.keySet()) {
			final IParameterEditor ed = editors.get(s);
			if (ed != null) {
				ed.setActive(activations.get(s));
			}
		}
		activations.clear();
		compo.layout();
		compo.pack(true);
	}

	private Menu getMenuFor(final Pref e, final AbstractEditor ed) {
		final Menu m = new Menu(ed.getLabel());
		final MenuItem title = new MenuItem(m, SWT.PUSH);
		title.setEnabled(false);
		title.setText(e.getKey());
		@SuppressWarnings ("unused") final MenuItem sep = new MenuItem(m, SWT.SEPARATOR);
		final MenuItem i = new MenuItem(m, SWT.PUSH);
		i.setText("Copy name to clipboard");
		i.addSelectionListener((Selector) se -> WorkbenchHelper.copy(e.getKey()));
		final MenuItem i2 = new MenuItem(m, SWT.PUSH);
		i2.setText("Revert to default value");
		i2.addSelectionListener((Selector) se -> {
			e.set(e.getInitialValue(GAMA.getRuntimeScope()));
			ed.forceUpdateValueAsynchronously();
		});
		return m;
	}

	/**
	 * @param string
	 */
	protected void showError(final String string) {
		// TODO make it a proper component of the view
		GAMA.getGui().debug("Error in preferences : " + string);
	}

	private void buildButtons() {
		final Label doc = new Label(shell, SWT.NONE);
		doc.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));
		doc.setFont(GamaFonts.boldHelpFont);
		doc.setText(
				"Preferences can also be set in GAML, using 'gama.pref_name <- new_value;'. 'pref_name' is displayed in the contextual menu of each preference");

		final Composite group1 = new Composite(shell, SWT.NONE);
		group1.setLayout(new FillLayout());
		final GridData gridDataGroup1 = new GridData(GridData.BEGINNING, GridData.END, true, false);
		gridDataGroup1.widthHint = 300;
		group1.setLayoutData(gridDataGroup1);

		final Button buttonRevert = new Button(group1, SWT.PUSH | SWT.FLAT);
		buttonRevert.setText("Revert to defaults");
		buttonRevert.setImage(GamaIcons.create(IGamaIcons.ACTION_REVERT).image());
		buttonRevert.setToolTipText("Restore default values for all preferences");

		final Button buttonAdvanced = new Button(group1, SWT.PUSH | SWT.FLAT);
		buttonAdvanced.setText("Advanced...");
		buttonAdvanced.setToolTipText("Access to advanced preferences");

		final Button buttonImport = new Button(group1, SWT.PUSH | SWT.FLAT);
		buttonImport.setText("Import...");
		buttonImport.setToolTipText("Import preferences from a file...");
		buttonImport.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setFilterExtensions(new String[] { "*.prefs" });
				final String path = fd.open();
				if (path == null) { return; }
				GamaPreferences.applyPreferencesFrom(path, modelValues);
				for (final IParameterEditor ed : editors.values()) {
					ed.updateValue(true);
				}
			}

		});

		final Button buttonExport = new Button(group1, SWT.PUSH | SWT.FLAT);
		buttonExport.setText("Export...");
		buttonExport.setToolTipText("Export preferences to a model that can be run to restore or share them...");
		buttonExport.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final FileDialog fd = new FileDialog(shell, SWT.SAVE);
				fd.setFileName("preferences.gaml");
				fd.setFilterExtensions(new String[] { "*.gaml" });
				fd.setOverwrite(false);
				final String path = fd.open();
				if (path == null) { return; }
				GamaPreferences.savePreferencesTo(path);
			}

		});

		final Composite group2 = new Composite(shell, SWT.NONE);
		group2.setLayout(new FillLayout());
		final GridData gridDataGroup2 = new GridData(GridData.END, GridData.END, true, false);
		gridDataGroup2.widthHint = 200;
		group2.setLayoutData(gridDataGroup2);

		final Button buttonCancel = new Button(group2, SWT.PUSH);
		buttonCancel.setText("Cancel");
		buttonCancel.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				close();
			}

		});

		final Button buttonOK = new Button(group2, SWT.PUSH);
		buttonOK.setText("Save");
		buttonOK.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				GamaPreferences.setNewPreferences(modelValues);
				if (restartRequired) {
					restartRequired = false;
					final boolean restart = Messages.confirm("Restart ?", "Restart GAMA now ?");
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
				GamaPreferences.revertToDefaultValues(modelValues);
				for (final IParameterEditor ed : editors.values()) {
					ed.updateValue(true);
				}

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

		shell.addDisposeListener(e -> {
			saveDialogProperties();
		});

		shell.addControlListener(new ControlListener() {

			@Override
			public void controlResized(final ControlEvent e) {
				for (final IParameterEditor ed : editors.values()) {
					((AbstractEditor) ed).resizeLabel(shell.getSize().x / (NB_DIVISIONS * 2));
					((AbstractEditor) ed).getLabel().update();
				}
				for (final ParameterExpandBar bar : contents) {
					for (final ParameterExpandItem item : bar.getItems()) {
						item.setHeight(item.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
					}

					bar.redraw();
				}
				shell.layout(true, true);
			}

			@Override
			public void controlMoved(final ControlEvent e) {}
		});

	}

	private void close() {
		shell.setVisible(false);
	}

	private void saveLocation() {
		final Point p = shell.getLocation();
		DIALOG_LOCATION.set(new GamaPoint(p.x, p.y)).save();
	}

	private void saveSize() {
		final Point s = shell.getSize();
		DIALOG_SIZE.set(new GamaPoint(s.x, s.y)).save();
	}

	private void saveTab() {
		final int index = tabFolder.getSelectionIndex();
		DIALOG_TAB.set(index).save();
	}

	private void saveDialogProperties() {
		if (shell.isDisposed()) { return; }
		saveLocation();
		saveSize();
		saveTab();
	}

	public void open() {
		final GamaPoint loc = DIALOG_LOCATION.getValue();
		final GamaPoint size = DIALOG_SIZE.getValue();
		final int tab = DIALOG_TAB.getValue();
		int x = (int) loc.x;
		int y = (int) loc.y;
		int width = (int) size.x;
		int height = (int) size.y;
		if (loc.x == -1 || loc.y == -1 || size.x == -1 || size.y == -1) {
			final Point p = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
			final Rectangle bounds = WorkbenchHelper.getDisplay().getBounds();
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
			if (!this.shell.getDisplay().readAndDispatch()) {
				this.shell.getDisplay().sleep();
			}
		}

		saveDialogProperties();

	}

	public static void setRestartRequired() {
		restartRequired = true;

	}

}
