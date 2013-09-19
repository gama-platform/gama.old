/**
 * Created by drogoul, 4 mars 2012
 * 
 */
package msi.gama.lang.gaml.ui;

import java.util.*;
import java.util.List;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.*;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.validation.*;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISyntacticElement;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.ISharedImages;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.*;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;
import com.google.inject.Inject;

/**
 * The class GamlEditor.
 * 
 * @author drogoul
 * @since 4 mars 2012
 * 
 */
public class GamlEditor extends XtextEditor implements IGamlBuilderListener {

	// Copied from SwtGui. See how to factorize this.
	// public static Image run = GamaIcons.action_run;

	public static final Color COLOR_TEXT = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
	private static final int INITIAL_BUTTONS = 20;
	private static Font labelFont;
	Composite toolbar, parent, indicator;
	Button[] buttons = new Button[INITIAL_BUTTONS];
	CLabel status;
	Button menu;
	List<String> completeNamesOfExperiments = new ArrayList();
	List<String> abbreviations = new ArrayList();
	boolean wasOK = true, inited = false;

	@Inject
	IResourceSetProvider resourceSetProvider;

	@Inject
	private GamlJavaValidator validator;

	// @Inject
	// private DescriptionUtils descriptionUtils;

	// @Inject
	// IResourceDescriptions index;

	static {
		FontData fd = Display.getDefault().getSystemFont().getFontData()[0];
		fd.setStyle(SWT.BOLD);
		labelFont = new Font(Display.getDefault(), fd);
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

		if ( buttons != null ) {
			for ( Button b : buttons ) {
				if ( b != null && !b.isDisposed() ) {
					b.dispose();
				}
			}
			buttons = null;
		}

		// if ( top != null && !top.isDisposed() ) {
		// top.dispose();
		// top = null;
		// }
		if ( toolbar != null && !toolbar.isDisposed() ) {
			toolbar.dispose();
			toolbar = null;
		}
		if ( status != null && !status.isDisposed() ) {
			status.dispose();
			status = null;
		}
		super.dispose();
	}

	@Override
	public void createPartControl(final Composite parent) {
		this.parent = parent;
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		parent.setLayout(layout);

		toolbar = new Composite(parent, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.heightHint = 26;
		data.horizontalIndent = 10;
		toolbar.setLayoutData(data);
		layout = new GridLayout(INITIAL_BUTTONS + 2, false);
		layout.horizontalSpacing = 2;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		toolbar.setLayout(layout);

		Composite others = new Composite(parent, SWT.None);
		data = new GridData(SWT.RIGHT, SWT.FILL, false, false);
		data.heightHint = 26;
		// data.horizontalIndent = 10;
		others.setLayoutData(data);
		layout = new GridLayout(2, false);
		others.setLayout(layout);

		indicator = new Composite(parent, SWT.None);
		data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.horizontalSpan = 2;
		data.heightHint = 8;
		indicator.setLayoutData(data);
		FillLayout layout2 = new FillLayout();
		layout2.marginWidth = 12;
		layout2.marginHeight = 0;
		indicator.setLayout(layout2);

		status = new CLabel(toolbar, SWT.NONE);
		status.setFont(labelFont);
		data = new GridData(SWT.FILL, SWT.CENTER, false, false);
		data.minimumHeight = SWT.DEFAULT;
		status.setLayoutData(data);
		status.setForeground(COLOR_TEXT);

		for ( int i = 0; i < INITIAL_BUTTONS; i++ ) {
			buttons[i] = new Button(toolbar, SWT.PUSH);
			data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
			buttons[i].setLayoutData(data);
			buttons[i].setText("Experiment " + i);
			buttons[i].addSelectionListener(listener);
			hideButton(buttons[i]);
		}
		Label toto = new Label(others, SWT.None);
		toto.setText("In project: ");
		data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		toto.setLayoutData(data);
		menu = new Button(others, SWT.DROP_DOWN | SWT.DOWN);
		data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		menu.setLayoutData(data);
		menu.setText("Other...");
		menu.setToolTipText("Experiments defined in other models of the project");
		menu.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				Menu old = menu.getMenu();
				menu.setMenu(null);
				if ( old != null ) {
					old.dispose();
				}
				Menu dropMenu = createExperimentsSubMenu(menu);
				dropMenu.setVisible(true);
			}

		});

		// Asking the editor to fill the rest
		Composite parent2 = new Composite(parent, SWT.BORDER);
		data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = 2;
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
						return validator.build(resource);
					}

				});
				if ( model == null ) { return; }
				GuiUtils.openSimulationPerspective();
				GAMA.controller.newExperiment(exp, model);
			}
		}
	};

	public Menu createExperimentsSubMenu(final Button button) {
		Menu parent = new Menu(button);
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
				expItem.setImage(GamaIcons.panel_continue);
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
		final Map<URI, List<String>> map = new LinkedHashMap();
		getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource resource) throws Exception {
				String platformString = resource.getURI().toPlatformString(true);
				IFile myFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(platformString));
				IProject proj = myFile.getProject();
				List<URI> resources = getAllGamaFilesInProject(proj, resource.getURI());
				ResourceSet rs = resourceSetProvider.get(proj);
				for ( URI uri : resources ) {
					// GuiUtils.debug("GamlEditor.fillCombo().new Void() {...}.process : " + uri);
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
					GuiUtils.openEditorAndSelect(o);
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
					// GuiUtils.debug("GamlEditor.recursiveFindGamaFiles uri:" + uri + " equals " + without);
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
			String name = ((Button) evt.widget).getText();
			int i = abbreviations.indexOf(name);
			if ( i == -1 ) { return; }
			name = completeNamesOfExperiments.get(i);
			IModel model = null;
			try {
				model = getDocument().readOnly(new IUnitOfWork<IModel, XtextResource>() {

					@Override
					public IModel exec(final XtextResource state) throws Exception {
						return validator.build((GamlResource) state);
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
		((GridData) buttons[index].getLayoutData()).exclude = false;
		buttons[index].setVisible(true);
		buttons[index].setText(text);
		buttons[index].pack();
	}

	private void hideButton(final Button b) {
		((GridData) b.getLayoutData()).exclude = true;
		b.setVisible(false);
	}

	private void setStatus(final String text, final boolean ok) {
		Color c = ok ? abbreviations.size() == 0 ? SwtGui.COLOR_WARNING : SwtGui.COLOR_OK : SwtGui.COLOR_ERROR;
		indicator.setBackground(c);
		status.setText(text);
	}

	private void updateToolbar(final boolean ok) {

		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if ( toolbar == null || toolbar.isDisposed() ) { return; }
				for ( Button b : buttons ) {
					if ( b.isVisible() ) {
						hideButton(b);
					}
				}
				if ( ok ) {
					int size = abbreviations.size();
					if ( size == 0 ) {
						setStatus("Model is functional, but no experiments have been defined.", ok);
					} else {
						setStatus(size == 1 ? "Run experiment:" : "Choose an experiment:", ok);
					}
					int i = 0;
					for ( String e : abbreviations ) {
						enableButton(i++, e);
					}
				} else {
					setStatus("Error(s) detected. Impossible to run any experiment", ok);
				}

				toolbar.layout(true);
			}
		});

	}

	private void updateExperiments(final Set<String> newExperiments, final boolean withErrors) {
		if ( withErrors == true && wasOK == false ) { return; }
		Set<String> oldNames = new LinkedHashSet(completeNamesOfExperiments);
		if ( inited && wasOK && !withErrors && oldNames.equals(newExperiments) ) { return; }
		inited = true;
		wasOK = !withErrors;
		completeNamesOfExperiments = new ArrayList(newExperiments);
		buildAbbreviations();
		updateToolbar(wasOK);
	}

	private void buildAbbreviations() {
		// Very simple method used here
		int size = completeNamesOfExperiments.size();
		abbreviations.clear();
		if ( size > 6 ) {
			// We remove "Experiment".
			for ( String s : completeNamesOfExperiments ) {
				int i = s.indexOf(' ');
				if ( i != -1 ) {
					abbreviations.add(s.substring(i));
				}
			}
		} else if ( size > 4 ) {
			// We replace "Experiment" by "Exp."
			for ( String s : completeNamesOfExperiments ) {
				abbreviations.add(s.replaceFirst("Experiment ", "Exp."));
			}
		} else {
			// We copy the names as it is
			abbreviations.addAll(completeNamesOfExperiments);
		}
	}

	/**
	 * @see msi.gama.common.interfaces.IGamlBuilder.Listener#validationEnded(boolean)
	 */
	@Override
	public void validationEnded(final Set<String> newExperiments, final boolean withErrors) {
		updateExperiments(newExperiments, withErrors);
	}

	public static class GamaSourceViewerConfiguration extends XtextSourceViewerConfiguration {

		@Override
		public ITextHover getTextHover(final ISourceViewer sourceViewer, final String contentType) {
			return super.getTextHover(sourceViewer, contentType);
		}

	}

}
