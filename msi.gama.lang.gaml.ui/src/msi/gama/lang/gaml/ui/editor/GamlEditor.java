/*********************************************************************************************
 * 
 * 
 * 'GamlEditor.java', in plugin 'msi.gama.lang.gaml.ui', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editor;

import java.util.*;
import java.util.List;
import msi.gama.common.GamaPreferences;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.*;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.resource.*;
import msi.gama.lang.gaml.validation.*;
import msi.gama.lang.gaml.validation.IGamlBuilderListener.IGamlBuilderListener2;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.types.IType;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.source.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.*;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import ummisco.gaml.editbox.*;
import com.google.common.collect.ObjectArrays;
import com.google.inject.Inject;

/**
 * The class GamlEditor.
 * 
 * @author drogoul
 * @since 4 mars 2012
 * 
 */
public class GamlEditor extends XtextEditor implements IGamlBuilderListener2, IBoxEnabledEditor {

	public GamlEditor() {}

	static final GamaPreferences.Entry<Boolean> EDITOR_CLEAN_UP = GamaPreferences
		.create("editor.cleanup.save", "Applying formatting to models on save", false, IType.BOOL)
		.in(GamaPreferences.EDITOR).group("Options");

	protected static Map<IPartService, IPartListener2> partListeners;

	IBoxDecorator decorator;
	boolean decorationEnabled;
	ToolBar toolbar;
	Composite parent, indicator;
	ToolItem status;
	final List<String> completeNamesOfExperiments = new ArrayList();
	final List<Boolean> experimentTypes = new ArrayList();
	final List<String> abbreviations = new ArrayList();
	boolean inited = false;

	@Inject
	IResourceSetProvider resourceSetProvider;

	@Inject
	private CompoundXtextEditorCallback callback;

	@Inject
	private GamlJavaValidator validator;

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
		super.init(site, input);
		assignBoxPartListener();
	}

	@Override
	public void dispose() {
		if ( getDocument() != null ) {
			getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {

				@Override
				public void process(final XtextResource state) throws Exception {
					((GamlResource) state).removeListener();
				}

			});
		}

		if ( toolbar != null && !toolbar.isDisposed() ) {
			toolbar.dispose();
			toolbar = null;
		}
		decorator = null;

		super.dispose();
	}

	@Override
	public void createPartControl(final Composite parent) {
		this.parent = parent;

		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 2;
		parent.setLayout(layout);

		toolbar = new ToolBar(parent, SWT.FLAT | SWT.HORIZONTAL | SWT.WRAP | SWT.RIGHT);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		toolbar.setLayoutData(data);

		indicator = new Composite(parent, SWT.None);
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.heightHint = 5;
		indicator.setLayoutData(data);
		FillLayout layout2 = new FillLayout();
		layout2.marginWidth = 12;
		layout2.marginHeight = 0;
		indicator.setLayout(layout2);

		final ToolItem b = new ToolItem(toolbar, SWT.FLAT | SWT.CHECK);

		b.setImage(IGamaIcons.BUTTON_EDITBOX.image());
		b.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				toggleEditBox(b.getSelection());
			}

		});

		final ToolItem sep = new ToolItem(toolbar, SWT.SEPARATOR);

		status = new ToolItem(toolbar, SWT.None);
		status.setEnabled(false);

		// Asking the editor to fill the rest
		Composite parent2 = new Composite(parent, SWT.BORDER);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		parent2.setLayoutData(data);
		parent2.setLayout(new FillLayout());
		super.createPartControl(parent2);
		getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource state) throws Exception {
				if ( state != null ) {
					((GamlResource) state).setListener(GamlEditor.this);
				}

			}
		});
	}

	private void toggleEditBox(final boolean selection) {
		decorationEnabled = selection;
		if ( selection ) {
			decorate();
		} else {
			undecorate();
		}
	}

	private void addOtherExperiments(final boolean addSeparator) {
		if ( addSeparator ) {
			new ToolItem(toolbar, SWT.SEPARATOR);
		}
		final ToolItem menu = new ToolItem(toolbar, SWT.DROP_DOWN | SWT.DOWN);
		menu.setText("Other...");
		menu.setToolTipText("Run experiments defined in the project");
		menu.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

				Menu old = (Menu) menu.getData();
				menu.setData(null);
				if ( old != null ) {
					old.dispose();
				}
				Menu dropMenu = createExperimentsSubMenu();
				menu.setData(dropMenu);
				Rectangle rect = menu.getBounds();
				Point pt = new Point(rect.x, rect.y + rect.height);
				pt = toolbar.toDisplay(pt);
				dropMenu.setLocation(pt.x, pt.y);
				dropMenu.setVisible(true);
			}

		});
	}

	private final SelectionAdapter adapter = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			MenuItem mi = (MenuItem) e.widget;
			final URI uri = (URI) mi.getData("uri");
			String exp = (String) mi.getData("exp");
			if ( uri != null && exp != null ) {
				IModel model = getDocument().readOnly(new IUnitOfWork<IModel, XtextResource>() {

					@Override
					public IModel exec(final XtextResource state) throws Exception {
						ResourceSet rs = state.getResourceSet();
						GamlResource resource = (GamlResource) rs.getResource(uri, true);
						return GamlModelBuilder.getInstance().compile(resource);
					}

				});
				if ( model == null ) { return; }
				GuiUtils.openSimulationPerspective();
				GAMA.controller.newExperiment(exp, model);
			}
		}
	};

	public Menu createExperimentsSubMenu() {
		Menu parent = new Menu(this.parent);
		Map<URI, List<String>> map = grabProjectModelsAndExperiments();
		if ( map.isEmpty() ) {
			MenuItem nothing = new MenuItem(parent, SWT.PUSH);
			nothing.setText("No experiments defined");
			nothing.setEnabled(false);
			return parent;
		}
		for ( URI uri : map.keySet() ) {
			MenuItem modelItem = new MenuItem(parent, SWT.CASCADE);

			modelItem.setText("Model " + URI.decode(uri.lastSegment()));
			modelItem.setImage(GamaIcons.getEclipseIcon(ISharedImages.IMG_OBJ_FILE));
			Menu expMenu = new Menu(modelItem);
			modelItem.setMenu(expMenu);
			List<String> expNames = map.get(uri);
			for ( String name : expNames ) {
				MenuItem expItem = new MenuItem(expMenu, SWT.PUSH);
				expItem.setText(name);
				expItem.setData("uri", uri);
				expItem.setData("exp", name);
				expItem.setImage(IGamaIcons.BUTTON_GUI.image());
				expItem.addSelectionListener(adapter);
			}
		}
		return parent;
	}

	/**
	 * @param resource
	 * @return
	 */
	private Map<URI, List<String>> grabProjectModelsAndExperiments() {
		final Map<URI, List<String>> map = new TOrderedHashMap();
		getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource resource) throws Exception {
				String platformString = resource.getURI().toPlatformString(true);
				IFile myFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(platformString));
				IProject proj = myFile.getProject();
				// AD Addresses Issue 796 by passing null to the "without" parameter
				List<URI> resources = getAllGamaFilesInProject(proj, /* resource.getURI() */null);
				ResourceSet rs = resourceSetProvider.get(proj);
				for ( URI uri : resources ) {
					GamlResource xr = (GamlResource) rs.getResource(uri, true);
					if ( xr.getErrors().isEmpty() ) {
						ISyntacticElement el = xr.getSyntacticContents();
						for ( ISyntacticElement ch : el.getChildren() ) {
							if ( ch.isExperiment() ) {
								if ( !map.containsKey(uri) ) {
									map.put(uri, new ArrayList());
								}
								map.get(uri).add(ch.getName());
							}
						}
					}
				}

			}
		});
		return map;
	}

	private void gotoEditor(final GamaRuntimeException exception) {

		final EObject o = exception.getEditorContext();
		if ( o != null ) {
			GuiUtils.asyncRun(new Runnable() {

				@Override
				public void run() {
					GuiUtils.editModel(o);
				}
			});
		}

	}

	public static ArrayList<URI> getAllGamaFilesInProject(final IProject project, final URI without) {
		ArrayList<URI> allGamaFiles = new ArrayList();
		IWorkspaceRoot myWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IPath path = project.getLocation();
		recursiveFindGamaFiles(allGamaFiles, path, myWorkspaceRoot, without);
		return allGamaFiles;
	}

	private static void recursiveFindGamaFiles(final ArrayList<URI> allGamaFiles, final IPath path,
		final IWorkspaceRoot myWorkspaceRoot, final URI without) {
		IContainer container = myWorkspaceRoot.getContainerForLocation(path);

		try {
			IResource[] iResources;
			iResources = container.members();
			for ( IResource iR : iResources ) {
				// for gama files
				if ( "gaml".equalsIgnoreCase(iR.getFileExtension()) ) {
					URI uri = URI.createPlatformResourceURI(iR.getFullPath().toString(), true);
					if ( !uri.equals(without) ) {
						allGamaFiles.add(uri);
					}
				}
				if ( iR.getType() == IResource.FOLDER ) {
					IPath tempPath = iR.getLocation();
					recursiveFindGamaFiles(allGamaFiles, tempPath, myWorkspaceRoot, without);
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private final SelectionListener listener = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent evt) {
			GamlEditor.this.performSave(true, null);
			String name = ((ToolItem) evt.widget).getText();
			int i = abbreviations.indexOf(name);
			if ( i == -1 ) { return; }
			name = completeNamesOfExperiments.get(i);
			IModel model = null;
			try {
				model = getDocument().readOnly(new IUnitOfWork<IModel, XtextResource>() {

					@Override
					public IModel exec(final XtextResource state) throws Exception {
						List<GamlCompilationError> errors = new ArrayList();
						return GamlModelBuilder.getInstance().compile(state);
					}

				});
			} catch (GamaRuntimeException e) {
				gotoEditor(e);
				GuiUtils.error("Experiment " + name + " cannot be instantiated because of the following error: " +
					e.getMessage());
			}
			if ( model == null ) { return; }

			GAMA.controller.newExperiment(name, model);
		}

	};

	private void enableButton(final int index, final String text) {
		if ( text == null ) { return; }
		ToolItem t = new ToolItem(toolbar, SWT.FLAT);
		t.setText(text);
		if ( experimentTypes.get(index) ) {
			t.setImage(IGamaIcons.BUTTON_BATCH.image());
			t.setHotImage(IGamaIcons.BUTTON_BATCH2.image());
		} else {
			t.setImage(IGamaIcons.BUTTON_GUI.image());
			t.setHotImage(IGamaIcons.BUTTON_GUI2.image());
		}
		t.addSelectionListener(listener);
		t = new ToolItem(toolbar, SWT.SEPARATOR);
	}

	private void setStatus(final String text, final Color c) {
		indicator.setBackground(c);
		if ( text != null && !text.isEmpty() && status != null && !status.isDisposed() ) {
			status.setText(text);
		}
	}

	private Color getColor(final ErrorCollector status) {
		if ( status.hasInternalErrors() ) { return SwtGui.getErrorColor(); }
		if ( status.hasImportedErrors() ) { return SwtGui.getImportedErrorColor(); }
		if ( abbreviations.size() == 0 ) { return SwtGui.getWarningColor(); }
		return SwtGui.getOkColor();
	}

	private void updateToolbar(final ErrorCollector status) {

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if ( toolbar == null || toolbar.isDisposed() ) { return; }
				boolean addSeparator = false;
				ToolItem[] items = toolbar.getItems();
				for ( int i = 3; i < items.length; i++ ) {
					ToolItem t = items[i];
					t.dispose();
				}
				Color c = getColor(status);
				if ( !status.hasErrors() ) {
					int size = abbreviations.size();
					if ( size == 0 ) {
						setStatus("This model is functional, but no experiments have been defined", c);
						addSeparator = true;
					} else {
						setStatus("Run:", c);
						int i = 0;
						for ( String e : abbreviations ) {
							enableButton(i++, e);
						}
					}

				} else if ( status.hasInternalErrors() || status.hasInternalSyntaxErrors() ) {
					setStatus("Error(s) were detected. Impossible to run any experiment", c);
					addSeparator = true;
				} else if ( status.hasImportedErrors() ) {
					String msg = "This model is functional but error(s) were detected in imported files";
					if ( abbreviations.size() != 0 ) {
						msg += ". Impossible to run any experiment";
					}
					setStatus(msg, c);
					addSeparator = true;
				}
				addOtherExperiments(addSeparator);
				parent.layout();
			}
		});

	}

	/**
	 * @see msi.gama.common.interfaces.IGamlBuilder.Listener#validationEnded(boolean)
	 */
	@Override
	public void validationEnded(final Set<String> newExperiments, final ErrorCollector status) {
		completeNamesOfExperiments.clear();
		completeNamesOfExperiments.addAll(newExperiments);
		abbreviations.clear();
		for ( String s : completeNamesOfExperiments ) {
			abbreviations.add(s.replaceFirst("Experiment ", ""));
			experimentTypes.add(false);
		}
		updateToolbar(status);
	}

	@Override
	public void validationEnded(final Collection<? extends IDescription> newExperiments, final ErrorCollector status) {
		completeNamesOfExperiments.clear();
		experimentTypes.clear();
		abbreviations.clear();
		for ( IDescription ep : newExperiments ) {
			String name = ep.getName();
			completeNamesOfExperiments.add(name);
			abbreviations.add(name.replaceFirst("Experiment ", ""));
			experimentTypes.add(((ExperimentDescription) ep).isBatch());
		}
		updateToolbar(status);
	}

	public static class GamaSourceViewerConfiguration extends XtextSourceViewerConfiguration {

		@Override
		public ITextHover getTextHover(final ISourceViewer sourceViewer, final String contentType) {
			return super.getTextHover(sourceViewer, contentType);
		}

	}

	@Override
	public void doSave(final IProgressMonitor progressMonitor) {
		this.beforeSave();
		super.doSave(progressMonitor);
	}

	@Override
	public void doSaveAs() {
		this.beforeSave();
		super.doSaveAs();
	}

	/**
	 *
	 */
	private void beforeSave() {
		if ( !EDITOR_CLEAN_UP.getValue() ) { return; }
		SourceViewer sv = (SourceViewer) getInternalSourceViewer();
		Point p = sv.getSelectedRange();
		sv.setSelectedRange(0, sv.getDocument().getLength());
		sv.doOperation(ISourceViewer.FORMAT);
		sv.setSelectedRange(p.x, p.y);
	}

	@Override
	protected String[] collectContextMenuPreferencePages() {
		String[] commonPages = super.collectContextMenuPreferencePages();
		String[] langSpecificPages = new String[] { "pm.eclipse.editbox.pref.default" };
		return ObjectArrays.concat(langSpecificPages, commonPages, String.class);
	}

	/**
	 * @see ummisco.gaml.editbox.IBoxEnabledEditor#getDecorator()
	 */
	@Override
	public IBoxDecorator getDecorator() {
		if ( decorator == null ) {
			createDecorator();
		}
		return decorator;
	}

	/**
	 * @see ummisco.gaml.editbox.IBoxEnabledEditor#createDecorator(ummisco.gaml.editbox.IBoxProvider)
	 */
	@Override
	public void createDecorator() {
		if ( decorator != null ) { return; }
		IBoxProvider provider = EditBox.getDefault().getGamlProvider();
		decorator = provider.createDecorator();
		decorator.setStyledText(getStyledText());
		decorator.setSettings(provider.getEditorsBoxSettings());
	}

	/**
	 * @return
	 */
	private StyledText getStyledText() {
		return (StyledText) super.getAdapter(Control.class);
	}

	/**
	 * @see ummisco.gaml.editbox.IBoxEnabledEditor#decorate()
	 */
	@Override
	public void decorate() {
		getDecorator().decorate(false);
		enableUpdates(true);
	}

	/**
	 * @see ummisco.gaml.editbox.IBoxEnabledEditor#undecorate()
	 */
	@Override
	public void undecorate() {
		getDecorator().undecorate();
		enableUpdates(false);
	}

	@Override
	public void enableUpdates(final boolean visible) {
		getDecorator().enableUpdates(visible);
	}

	@Override
	public boolean isDecorationEnabled() {
		return decorationEnabled;
	}

	private void assignBoxPartListener() {
		IPartService partService = getSite().getWorkbenchWindow().getPartService();
		if ( partService == null ) { return; }
		if ( partListeners == null ) {
			partListeners = new HashMap<IPartService, IPartListener2>();
		}
		IPartListener2 oldListener = partListeners.get(partService);
		if ( oldListener == null ) {
			IPartListener2 listener = new BoxDecoratorPartListener();
			partService.addPartListener(listener);
			partListeners.put(partService, listener);
		}
	}

}
