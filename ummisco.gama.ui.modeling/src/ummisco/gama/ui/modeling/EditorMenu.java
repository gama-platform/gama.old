/*********************************************************************************************
 *
 * 'EditorMenu.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.modeling;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import msi.gama.common.GamaPreferences;
import msi.gama.lang.gaml.indexer.GamlResourceIndexer;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.ui.editor.GamlEditor;
import msi.gama.lang.gaml.ui.reference.BuiltinReferenceMenu;
import msi.gama.lang.gaml.ui.reference.ColorReferenceMenu;
import msi.gama.lang.gaml.ui.reference.OperatorsReferenceMenu;
import msi.gama.lang.gaml.ui.reference.TemplateReferenceMenu;
import msi.gama.lang.gaml.validation.GamlModelBuilder;
import msi.gama.runtime.GAMA;
import msi.gaml.compilation.ast.ISyntacticElement;
import ummisco.gama.ui.commands.RefreshHandler;
import ummisco.gama.ui.menus.GamaMenu;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class EditorMenu extends ContributionItem implements IWorkbenchContribution {

	// private static EditorMenu INSTANCE;

	// MenuItem mark;
	GamaPreferences.Entry<Boolean> markPref;

	public EditorMenu() {
		super();
		// INSTANCE = this;
	}

	// public static EditorMenu getInstance() {
	// return INSTANCE;
	// }

	// public void editorChanged() {
	// line.setSelection(getEditor().isLineNumberRulerVisible());
	// folding.setSelection(getEditor().isRangeIndicatorEnabled());
	// box.setSelection(getEditor().isDecorationEnabled());
	// initializePreferences();
	// mark.setSelection(markPref.getValue());
	// }

	private void initializePreferences() {
		// if (markPref == null) {
		// markPref = GamaPreferences.get("pref_editor_mark_occurrences", Boolean.class);
		// // final IPreferenceChangeListener<Boolean> change = new IPreferenceChangeListener<Boolean>() {
		// //
		// // @Override
		// // public boolean beforeValueChange(final Boolean newValue) {
		// // return true;
		// // }
		// //
		// // @Override
		// // public void afterValueChange(final Boolean newValue) {
		// // mark.setSelection(newValue);
		// // }
		// // };
		// // markPref.addChangeListener(change);
		// // final IPreferenceStore store = getEditor().getAdvancedPreferenceStore();
		// // store.addPropertyChangeListener(event -> {
		// // final String id = event.getProperty();
		// // if (id.equals(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_LINE_NUMBER_RULER)) {
		// // line.setSelection((Boolean) event.getNewValue());
		// // }
		// // });
		// }

	}

	@Override
	public void initialize(final IServiceLocator serviceLocator) {}

	@Override
	public void fill(final Menu m, final int index) {

		final MenuItem menuItem = new MenuItem(m, SWT.CASCADE);
		menuItem.setText("Model");
		final Menu menu = new Menu(menuItem);
		if (menuItem.getMenu() != null) {
			menuItem.getMenu().dispose();
		}
		menuItem.setMenu(menu);
		menu.addListener(SWT.Show, e -> {
			markPref = GamaPreferences.get("pref_editor_mark_occurrences", Boolean.class);
			for (final MenuItem item : menu.getItems()) {
				item.dispose();
			}
			if (getEditor() != null) {
				GamaMenu.separate(menu, "Presentation");
				GamaMenu.separate(menu);
				createLineToggle(menu);
				createFoldingToggle(menu);
				createMarkToggle(menu);
				createBoxToggle(menu);
				createOverviewToggle(menu);
				GamaMenu.separate(menu);
				GamaMenu.separate(menu, "Insert");
				GamaMenu.separate(menu);
				createReferenceMenus(menu);
				GamaMenu.separate(menu);
				GamaMenu.separate(menu, "Model");
				GamaMenu.separate(menu);
				createUsedIn(menu);
				createOtherExperiments(menu);
				GamaMenu.separate(menu);
				createValidate(menu);
				// line.setSelection(getEditor().isLineNumberRulerVisible());
				// folding.setSelection(getEditor().isRangeIndicatorEnabled());
				// box.setSelection(getEditor().isDecorationEnabled());
				initializePreferences();
				// mark.setSelection(markPref.getValue());
			}
			createValidateAll(menu);
		});

	}

	/**
	 * @param menu
	 */
	private void createValidateAll(final Menu menu) {

		final MenuItem mark = new MenuItem(menu, SWT.PUSH);
		mark.setText(" Validate all");
		mark.setImage(GamaIcons.create("build.all2").image());
		mark.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

				final IWorkspace workspace = ResourcesPlugin.getWorkspace();
				try {
					GamlResourceIndexer.eraseIndex();
					workspace.build(IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor() {

						@Override
						public void done() {
							super.done();
							RefreshHandler.run(workspace.getRoot());

						}

					});

				} catch (final CoreException ex) {
					ex.printStackTrace();
				}

			}
		});

	}

	/**
	 * @param menu
	 */
	private void createReferenceMenus(final Menu menu) {
		new TemplateReferenceMenu().installSubMenuIn(menu);
		new BuiltinReferenceMenu().installSubMenuIn(menu);
		new OperatorsReferenceMenu().installSubMenuIn(menu);
		new ColorReferenceMenu().installSubMenuIn(menu);
	}

	/**
	 * @param menu
	 */
	private void createOtherExperiments(final Menu menu) {

		final MenuItem usedIn = new MenuItem(menu, SWT.CASCADE);
		usedIn.setText(" Other experiments...");
		usedIn.setImage(GamaIcons.create("other.experiments").image());
		final Menu sub = new Menu(usedIn);
		usedIn.setMenu(sub);
		sub.addListener(SWT.Show, e -> {
			for (final MenuItem item : sub.getItems()) {
				item.dispose();
			}
			createOtherSubMenu(sub, getEditor());
		});

	}

	public static Menu createOtherSubMenu(final Menu parentMenu, final GamlEditor editor) {
		final Map<URI, List<String>> map = grabProjectModelsAndExperiments(editor);
		if (map.isEmpty()) {
			final MenuItem nothing = new MenuItem(parentMenu, SWT.PUSH);
			nothing.setText("No experiments defined");
			nothing.setEnabled(false);
			return parentMenu;
		}
		for (final URI uri : map.keySet()) {
			final MenuItem modelItem = new MenuItem(parentMenu, SWT.CASCADE);
			modelItem.setText(URI.decode(uri.lastSegment()));
			modelItem.setImage(GamaIcons.create(IGamaIcons.FILE_ICON).image());
			final Menu expMenu = new Menu(modelItem);
			modelItem.setMenu(expMenu);
			final List<String> expNames = map.get(uri);
			for (final String name : expNames) {
				final MenuItem expItem = new MenuItem(expMenu, SWT.PUSH);
				expItem.setText(name);
				expItem.setData("uri", uri);
				expItem.setData("exp", name);
				expItem.setImage(GamaIcons.create(IGamaIcons.BUTTON_GUI).image());
				expItem.addSelectionListener(OtherAdapter);
			}
		}
		return parentMenu;
	}

	private static Map<URI, List<String>> grabProjectModelsAndExperiments(final GamlEditor editor) {
		final Map<URI, List<String>> map = new LinkedHashMap<>();
		editor.getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource resource) throws Exception {
				final String platformString = resource.getURI().toPlatformString(true);
				final IFile myFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(platformString));
				final IProject proj = myFile.getProject();
				// AD Addresses Issue 796 by passing null to the "without"
				// parameter
				final List<URI> resources = getAllGamaFilesInProject(proj, null);
				final ResourceSet rs = editor.resourceSetProvider.get(proj);
				for (final URI uri : resources) {
					final GamlResource xr = (GamlResource) rs.getResource(uri, true);
					if (!xr.hasErrors()) {
						final ISyntacticElement el = xr.getSyntacticContents();
						if (el != null)
							el.visitExperiments(element -> {

								if (!map.containsKey(uri)) {
									map.put(uri, new ArrayList<>());
								}
								map.get(uri).add(element.getName());

							});
					}
				}

			}
		});
		return map;
	}

	public static ArrayList<URI> getAllGamaFilesInProject(final IProject project, final URI without) {
		final ArrayList<URI> allGamaFiles = new ArrayList<>();
		final IWorkspaceRoot myWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		final IPath path = project.getLocation();
		recursiveFindGamaFiles(allGamaFiles, path, myWorkspaceRoot, without);
		return allGamaFiles;
	}

	private static void recursiveFindGamaFiles(final ArrayList<URI> allGamaFiles, final IPath path,
			final IWorkspaceRoot myWorkspaceRoot, final URI without) {
		final IContainer container = myWorkspaceRoot.getContainerForLocation(path);
		if (container != null)
			try {
				final IResource[] iResources = container.members();
				if (iResources != null)
					for (final IResource iR : iResources) {
						// for gama files
						if ("gaml".equalsIgnoreCase(iR.getFileExtension())) {
							final URI uri = URI.createPlatformResourceURI(iR.getFullPath().toString(), true);
							if (!uri.equals(without)) {
								allGamaFiles.add(uri);
							}
						}
						if (iR.getType() == IResource.FOLDER) {
							final IPath tempPath = iR.getLocation();
							recursiveFindGamaFiles(allGamaFiles, tempPath, myWorkspaceRoot, without);
						}
					}
			} catch (final CoreException e) {
				e.printStackTrace();
			}
	}

	/**
	 * @param menu
	 */
	private void createUsedIn(final Menu menu) {
		final MenuItem usedIn = new MenuItem(menu, SWT.CASCADE);
		usedIn.setText(" Imported in...");
		usedIn.setImage(GamaIcons.create("imported.in").image());
		final Menu sub = new Menu(usedIn);
		usedIn.setMenu(sub);
		sub.addListener(SWT.Show, e -> {
			for (final MenuItem item : sub.getItems()) {
				item.dispose();
			}
			createImportedSubMenu(sub, getEditor());
		});
	}

	private static final SelectionAdapter UsedInAdapter = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final MenuItem mi = (MenuItem) e.widget;
			final URI uri = (URI) mi.getData("uri");
			GAMA.getGui().editModel(uri);
		}
	};

	private static final SelectionAdapter OtherAdapter = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final MenuItem mi = (MenuItem) e.widget;
			final URI uri = (URI) mi.getData("uri");
			final String exp = (String) mi.getData("exp");
			if (uri != null && exp != null) {
				GAMA.getGui().runModel(uri, exp);
			}
		}
	};

	public static Menu createImportedSubMenu(final Menu parentMenu, final GamlEditor editor) {
		final Set<URI> importers = getImporters(editor);
		if (importers.isEmpty()) {
			final MenuItem nothing = new MenuItem(parentMenu, SWT.PUSH);
			nothing.setText("No importers");
			nothing.setEnabled(false);
			return parentMenu;
		}
		for (final URI uri : importers) {
			final MenuItem modelItem = new MenuItem(parentMenu, SWT.CASCADE);
			modelItem.setText(URI.decode(uri.lastSegment()));
			modelItem.setImage(GamaIcons.create(IGamaIcons.FILE_ICON).image());
			modelItem.setData("uri", uri);
			modelItem.addSelectionListener(UsedInAdapter);
		}
		return parentMenu;
	}

	private static Set<URI> getImporters(final GamlEditor editor) {
		final Set<URI> map = new LinkedHashSet<URI>();
		editor.getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource resource) throws Exception {
				final String platformString = resource.getURI().toPlatformString(true);
				final URI uri = URI.createPlatformResourceURI(platformString, false);
				map.addAll(GamlResourceIndexer.directImportersOf(uri));
			}
		});
		return map;
	}

	/**
	 * @param menu
	 */
	private void createValidate(final Menu menu) {
		final MenuItem validate = new MenuItem(menu, SWT.PUSH);
		validate.setText(" Validate");
		validate.setImage(GamaIcons.create("build.project2").image());
		validate.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				getEditor().getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {

					@Override
					public void process(final XtextResource state) throws Exception {
						GamlModelBuilder.compile(state.getURI(), null);
					}
				});
			}
		});

	}

	/**
	 * 
	 */
	private void createBoxToggle(final Menu menu) {
		final MenuItem box = new MenuItem(menu, SWT.CHECK);
		box.setText(" Colorize code sections");
		box.setImage(GamaIcons.create("toggle.box").image());
		box.setSelection(getEditor().isDecorationEnabled());
		box.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final boolean selection = box.getSelection();
				getEditor().setDecorationEnabled(selection);
				getEditor().decorate(selection);
			}
		});

	}

	/**
	 * 
	 */
	private void createMarkToggle(final Menu menu) {
		final MenuItem mark = new MenuItem(menu, SWT.CHECK);
		mark.setText(" Mark occurences of symbols");
		mark.setImage(GamaIcons.create("toggle.mark").image());
		mark.setSelection(markPref.getValue());
		mark.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				markPref.set(mark.getSelection()).save();
			}
		});

	}

	private void createOverviewToggle(final Menu menu) {
		final MenuItem overview = new MenuItem(menu, SWT.CHECK);
		overview.setText(" Show markers overview");
		overview.setSelection(getEditor().isOverviewRulerVisible());
		overview.setImage(GamaIcons.create("toggle.overview").image());
		overview.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final boolean shown = getEditor().isOverviewRulerVisible();
				if (shown)
					getEditor().hideOverviewRuler();
				else
					getEditor().showOverviewRuler();
			}
		});

	}

	/**
	 * 
	 */
	private void createFoldingToggle(final Menu menu) {
		final MenuItem folding = new MenuItem(menu, SWT.CHECK);
		folding.setText(" Fold code sections");
		folding.setImage(GamaIcons.create("toggle.folding").image());
		folding.setSelection(getEditor().isRangeIndicatorEnabled());
		folding.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				getEditor().getAction("FoldingToggle").run();
			}
		});

	}

	/**
	 * 
	 */
	private void createLineToggle(final Menu menu) {
		final MenuItem line = new MenuItem(menu, SWT.CHECK);
		line.setText(" Display line number");
		line.setImage(GamaIcons.create("toggle.numbers").image());
		line.setSelection(getEditor().isLineNumberRulerVisible());
		line.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				getEditor().getAction(ITextEditorActionConstants.LINENUMBERS_TOGGLE).run();
			}
		});

	}

	GamlEditor getEditor() {
		return (GamlEditor) WorkbenchHelper.getActiveEditor();
	}
}
