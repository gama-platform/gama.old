/*********************************************************************************************
 *
 * 'GamlEditor.java, in plugin ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.ui.editor;

import static msi.gama.lang.gaml.ui.AutoStartup.EDITOR_DRAG_RESOURCES;
import static org.eclipse.xtext.validation.CheckMode.NORMAL_AND_FAST;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IAnnotationAccessExtension;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRulerColumn;
import org.eclipse.jface.text.source.ImageUtilities;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.internal.texteditor.LineNumberColumn;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.spelling.SpellingService;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.XtextUIMessages;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.XtextSourceViewerConfiguration;
import org.eclipse.xtext.ui.editor.model.XtextDocument;
import org.eclipse.xtext.ui.editor.outline.quickoutline.QuickOutlinePopup;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionProvider;
import org.eclipse.xtext.ui.editor.templates.XtextTemplateContextType;
import org.eclipse.xtext.ui.editor.validation.AnnotationIssueProcessor;
import org.eclipse.xtext.ui.editor.validation.IValidationIssueProcessor;
import org.eclipse.xtext.ui.editor.validation.MarkerCreator;
import org.eclipse.xtext.ui.editor.validation.MarkerIssueProcessor;
import org.eclipse.xtext.ui.editor.validation.ValidationJob;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;
import org.eclipse.xtext.ui.validation.MarkerTypeProvider;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.concurrent.CancelableUnitOfWork;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;

import com.google.common.collect.ObjectArrays;
import com.google.inject.Inject;
import com.google.inject.Injector;

import msi.gama.common.GamlFileExtension;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.IPreferenceChangeListener.IPreferenceAfterChangeListener;
import msi.gama.lang.gaml.resource.GamlResourceServices;
import msi.gama.lang.gaml.ui.AutoStartup;
import msi.gama.lang.gaml.ui.decorators.GamlAnnotationImageProvider;
import msi.gama.lang.gaml.ui.editbox.BoxDecoratorPartListener;
import msi.gama.lang.gaml.ui.editbox.BoxProviderRegistry;
import msi.gama.lang.gaml.ui.editbox.IBoxDecorator;
import msi.gama.lang.gaml.ui.editbox.IBoxEnabledEditor;
import msi.gama.lang.gaml.ui.editbox.IBoxProvider;
import msi.gama.lang.gaml.ui.editor.toolbar.CreateExperimentSelectionListener;
import msi.gama.lang.gaml.ui.editor.toolbar.EditorSearchControls;
import msi.gama.lang.gaml.ui.editor.toolbar.EditorToolbar;
import msi.gama.lang.gaml.ui.editor.toolbar.GamlQuickOutlinePopup;
import msi.gama.lang.gaml.ui.editor.toolbar.OpenExperimentSelectionListener;
import msi.gama.lang.gaml.ui.editor.toolbar.OpenImportedErrorSelectionListener;
import msi.gama.lang.gaml.ui.editor.toolbar.RevalidateModelSelectionListener;
import msi.gama.lang.gaml.ui.templates.GamlEditTemplateDialog;
import msi.gama.lang.gaml.ui.templates.GamlEditTemplateDialogFactory;
import msi.gama.lang.gaml.ui.templates.GamlTemplateStore;
import msi.gama.lang.gaml.validation.IGamlBuilderListener;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ValidationContext;
import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.interfaces.IModelRunner;
import ummisco.gama.ui.navigator.contents.NavigatorRoot;
import ummisco.gama.ui.navigator.contents.WrappedGamaFile;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.utils.PlatformHelper;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.IGamlEditor;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;
import ummisco.gama.ui.views.toolbar.GamaToolbarFactory;
import ummisco.gama.ui.views.toolbar.IToolbarDecoratedView;
import ummisco.gama.ui.views.toolbar.Selector;

/*
 * The class GamlEditor.
 *
 * @author drogoul
 *
 * @since 4 mars 2012
 */
@SuppressWarnings ("all")
public class GamlEditor extends XtextEditor implements IGamlBuilderListener, IGamlEditor, IBoxEnabledEditor,
		IToolbarDecoratedView /* IToolbarDecoratedView.Sizable, ITooltipDisplayer */ {

	static {
		final IPreferenceStore store = EditorsUI.getPreferenceStore();
		store.setDefault(AbstractDecoratedTextEditorPreferenceConstants.SHOW_RANGE_INDICATOR, false);
		store.setDefault(SpellingService.PREFERENCE_SPELLING_ENABLED, false);
		store.setValue(SpellingService.PREFERENCE_SPELLING_ENABLED, false);

	}

	public GamlEditor() {
		dndHandler = new GamlEditorDragAndDropHandler(this);
	}

	protected static Map<IPartService, IPartListener2> partListeners;

	IBoxDecorator decorator;
	GamlEditorState state = new GamlEditorState(null, Collections.EMPTY_LIST);
	GamaToolbar2 toolbar;
	Composite toolbarParent;
	private EditorSearchControls findControl;
	boolean decorationEnabled = AutoStartup.EDITBOX_ENABLED.getValue();
	// boolean editToolbarEnabled = AutoStartup.EDITOR_SHOW_TOOLBAR.getValue();

	@Inject public IResourceSetProvider resourceSetProvider;
	@Inject Injector injector;
	@Inject IModelRunner runner;
	@Inject private GamlEditTemplateDialogFactory templateDialogFactory;
	@Inject private TemplateStore templateStore;
	@Inject private IResourceValidator validator;
	@Inject private MarkerCreator markerCreator;
	@Inject private MarkerTypeProvider markerTypeProvider;
	@Inject private IssueResolutionProvider issueResolver;
	private final GamlEditorDragAndDropHandler dndHandler;
	private final IPreferenceAfterChangeListener dndChangedListener = newValue -> {
		uninstallTextDragAndDrop(getInternalSourceViewer());
		installTextDragAndDrop(getInternalSourceViewer());
	};

	private boolean fIsTextDragAndDropInstalled;

	protected Object fTextDragAndDropToken;

	private URI fileURI;

	// Fix for #2108 -- forces the selection of the "clicked" tab
	// private static MouseAdapter FIX_FOR_ISSUE_2108 = new MouseAdapter() {
	//
	// @Override
	// public void mouseUp(final MouseEvent e) {
	// // System.out.println("MOUSE up IN TAB FOLDER");
	// final CTabFolder folder = (CTabFolder) e.widget;
	// final int x = e.x;
	// final int y = e.y;
	// for (final CTabItem item : folder.getItems()) {
	// final Rectangle r = item.getBounds();
	// if (r.contains(x, y) && !item.equals(folder.getSelection())) {
	// System.out.println("Detected problem in editors tab selection (see #2108). Fixed.");
	// WorkbenchHelper.runInUI("", 100, (m) -> {
	// folder.setSelection(item);
	// folder.layout(true, true);
	// folder.update();
	// return;
	// });
	// }
	// }
	// }
	//
	// };

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
		super.init(site, input);
		assignBoxPartListener();
	}

	static GamlAnnotationImageProvider imageProvider = new GamlAnnotationImageProvider();

	@Override
	protected IAnnotationAccess createAnnotationAccess() {
		return new DefaultMarkerAnnotationAccess() {

			@Override
			public int getLayer(final Annotation annotation) {
				if (annotation.isMarkedDeleted()) { return IAnnotationAccessExtension.DEFAULT_LAYER; }
				return super.getLayer(annotation);
			}

			@Override
			public void paint(final Annotation annotation, final GC gc, final Canvas canvas, final Rectangle bounds) {
				final Image image = imageProvider.getManagedImage(annotation);
				if (image != null) {
					ImageUtilities.drawImage(image, gc, canvas, bounds, SWT.CENTER, SWT.TOP);
				} else {
					super.paint(annotation, gc, canvas, bounds);
				}

			}

			@Override
			public boolean isPaintable(final Annotation annotation) {
				if (imageProvider.getManagedImage(annotation) != null) { return true; }
				return super.isPaintable(annotation);
			}

		};
	}

	@Override
	protected void rulerContextMenuAboutToShow(final IMenuManager menu) {
		super.rulerContextMenuAboutToShow(menu);
		menu.remove("projection");

		final IMenuManager foldingMenu = new MenuManager(XtextUIMessages.Editor_FoldingMenu_name, "projection"); //$NON-NLS-1$
		menu.appendToGroup(ITextEditorActionConstants.GROUP_RULERS, foldingMenu);
		IAction action = getAction("FoldingToggle"); //$NON-NLS-1$
		foldingMenu.add(action);
		action = getAction("FoldingExpandAll"); //$NON-NLS-1$
		foldingMenu.add(action);
		action = getAction("FoldingCollapseAll"); //$NON-NLS-1$
		foldingMenu.add(action);
		action = getAction("FoldingCollapseStrings"); //$NON-NLS-1$
		foldingMenu.add(action);
		action = getAction("FoldingRestore"); //$NON-NLS-1$
		foldingMenu.add(action);
	}

	@Override
	public void dispose() {
		decorator = null;
		EDITOR_DRAG_RESOURCES.removeChangeListener(dndChangedListener);
		GamlResourceServices.removeResourceListener(this);

		super.dispose();
	}

	public GamlTemplateStore getTemplateStore() {
		return (GamlTemplateStore) templateStore;
	}

	public GamlEditTemplateDialogFactory getTemplateFactory() {
		return templateDialogFactory;
	}

	public void setShowOtherEnabled(final boolean showOtherEnabled) {
		buildRightToolbar();
	}

	private void buildRightToolbar() {
		toolbar.wipe(SWT.LEFT, true);
		final ToolItem t = toolbar.button(IGamaColors.NEUTRAL, "Waiting...", GamaIcons.create("status.clock").image(),
				null, SWT.LEFT);
		toolbar.sep(4, SWT.LEFT);
		findControl = new EditorToolbar(this).fill(toolbar.getToolbar(SWT.RIGHT));

		// toolbar.sep(4, SWT.RIGHT);
		toolbar.refresh(true);
	}

	@Override
	public boolean isLineNumberRulerVisible() {
		final IPreferenceStore store = getAdvancedPreferenceStore();
		return store != null ? store.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_LINE_NUMBER_RULER)
				: false;
	}

	public boolean isRangeIndicatorEnabled() {
		return getInternalSourceViewer().isProjectionMode();
	}

	public final IPreferenceStore getAdvancedPreferenceStore() {
		return super.getPreferenceStore();
	}

	private void configureTabFolder(final Composite compo) {
		Composite c = compo;
		while (c != null) {
			if (c instanceof CTabFolder) {
				break;
			}
			c = c.getParent();
		}
		if (c != null) {
			final CTabFolder folder = (CTabFolder) c;
			folder.setMaximizeVisible(true);
			folder.setMinimizeVisible(true);
			folder.setMinimumCharacters(10);
			folder.setMRUVisible(true);
			// Makes sure the listener is added only once
			// folder.removeMouseListener(FIX_FOR_ISSUE_2108);
			// folder.addMouseListener(FIX_FOR_ISSUE_2108);
		}

	}

	@Override
	public void createPartControl(final Composite compo) {
		configureTabFolder(compo);
		toolbarParent = GamaToolbarFactory.createToolbars(this, compo);
		final GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 0;
		layout.marginRight = -5;
		toolbarParent.setLayout(layout);
		toolbarParent.setBackground(IGamaColors.WHITE.color());

		// Asking the editor to fill the rest
		final Composite editor = new Composite(toolbarParent, SWT.BORDER);
		final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		editor.setLayoutData(data);
		editor.setLayout(new FillLayout());
		super.createPartControl(editor);
		toolbarParent.layout();
		installGestures();
		scheduleValidationJob();
	}

	private void scheduleValidationJob() {
		fileURI = ((XtextDocument) getDocument()).getResourceURI();
		IValidationIssueProcessor processor;
		if (isEditable()) {
			if (getResource() == null) {
				processor = new AnnotationIssueProcessor(getDocument(), getSourceViewer().getAnnotationModel(),
						issueResolver);
			} else {
				processor = new MarkerIssueProcessor(getResource(), getInternalSourceViewer().getAnnotationModel(),
						markerCreator, markerTypeProvider);
			}
			final ValidationJob validate = new ValidationJob(validator, getDocument(), processor, NORMAL_AND_FAST) {
				@Override
				protected IStatus run(final IProgressMonitor monitor) {
					processor.processIssues(createIssues(monitor), monitor);
					return Status.OK_STATUS;
				}

				@Override
				public List<Issue> createIssues(final IProgressMonitor monitor) {
					final List<Issue> issues = getDocument().readOnly(resource -> {
						if (resource == null || resource.isValidationDisabled()) { return Collections.emptyList(); }
						GamlResourceServices.addResourceListener(resource.getURI(), GamlEditor.this);
						return validator.validate(resource, getCheckMode(), null);
					});
					return issues;
				}

			};
			validate.schedule();
		}
	}

	@Override
	public boolean isOverviewRulerVisible() {
		final GamaSourceViewer viewer = getInternalSourceViewer();
		if (viewer == null) { return super.isOverviewRulerVisible(); }
		return viewer.isOverviewVisible();
	}

	@Override
	public void showOverviewRuler() {
		getInternalSourceViewer().showAnnotationsOverview(true);
	}

	@Override
	public void hideOverviewRuler() {
		getInternalSourceViewer().showAnnotationsOverview(false);
	}

	@Override
	public GamaSourceViewer getInternalSourceViewer() {
		return (GamaSourceViewer) super.getInternalSourceViewer();
	}

	// @Override
	// public void setFocus() {
	// if (getSourceViewer() != null && getSourceViewer().getTextWidget() != null
	// && !getSourceViewer().getTextWidget().isFocusControl()) {
	// getSourceViewer().getTextWidget().setFocus();
	// }
	// // EditorMenu.getInstance().editorChanged();
	// }

	private void installGestures() {
		final StyledText text = this.getInternalSourceViewer().getTextWidget();
		if (text != null) {
			text.addGestureListener(ge -> {
				if (ge.detail == SWT.GESTURE_END) {
					updateBoxes();
				}
			});
		}
	}

	@Override
	protected void installFoldingSupport(final ProjectionViewer projectionViewer) {
		super.installFoldingSupport(projectionViewer);
		if (!isRangeIndicatorEnabled()) {
			projectionViewer.doOperation(ProjectionViewer.TOGGLE);
		}
	};

	@Override
	protected void handleCursorPositionChanged() {
		if (getSelectionProvider() == null) { return; }
		if (getInternalSourceViewer() == null) { return; }
		if (getInternalSourceViewer().getControl() == null) { return; }
		if (getInternalSourceViewer().getControl().isDisposed()) { return; }
		/// AAAAA
		super.handleCursorPositionChanged();
		this.markInNavigationHistory();
	}

	private void enableButton(final int index, final String text, final SelectionListener listener) {
		if (text == null) { return; }
		// final boolean isBatch = state.types.get(index);
		final String expType = state.types.get(index);
		// final Image image = isBatch ?
		// GamaIcons.create(IGamaIcons.BUTTON_BATCH).image()
		// : GamaIcons.create(IGamaIcons.BUTTON_GUI).image();
		final Image image = IKeyword.BATCH.equals(expType) ? GamaIcons.create(IGamaIcons.BUTTON_BATCH).image()
				: IKeyword.MEMORIZE.equals(expType) ? GamaIcons.create(IGamaIcons.BUTTON_BACK).image()
						: GamaIcons.create(IGamaIcons.BUTTON_GUI).image();

		final ToolItem t = toolbar.button(IGamaColors.OK,
				text/*
					 * + "  " + GamaKeyBindings.format(GamlEditorBindings.MODIFIERS, String.valueOf(index).charAt(0))
					 */, image, SWT.LEFT);
		// final String type = isBatch ? "batch" : "regular";
		final String type =
				IKeyword.BATCH.equals(expType) ? "batch" : IKeyword.MEMORIZE.equals(expType) ? "memorize" : "regular";

		t.getControl().setToolTipText("Executes the " + type + " experiment " + text);
		((FlatButton) t.getControl()).addSelectionListener(listener);
		t.setData("index", index);
		((FlatButton) t.getControl()).setData("exp", text);
		toolbar.sep(4, SWT.LEFT);
	}

	private void updateToolbar(final GamlEditorState newState, final boolean forceState) {
		if (forceState || !state.equals(newState)) {
			WorkbenchHelper.asyncRun(() -> {
				if (toolbar == null || toolbar.isDisposed()) { return; }
				toolbar.wipe(SWT.LEFT, true);
				if (PlatformHelper.isWindows()) {
					toolbar.sep(4, SWT.LEFT);
				}

				final GamaUIColor c = state.getColor();
				String msg = state.getStatus();

				Selector listener = null;
				String imageName = null;

				if (msg == GamlEditorState.NO_EXP_DEFINED) {
					// listener = new CreateExperimentSelectionListener(GamlEditor.this,
					// toolbar.getToolbar(SWT.LEFT));
					// imageName = "small.dropdown";
					msg = null;
				} else if (newState.hasImportedErrors) {
					listener = new OpenImportedErrorSelectionListener(GamlEditor.this, newState,
							toolbar.getToolbar(SWT.LEFT));
					imageName = "small.dropdown";
				} else if (msg != null) {
					listener = new RevalidateModelSelectionListener(GamlEditor.this);
					imageName = "marker.error2";
				} else {
					listener = new OpenExperimentSelectionListener(GamlEditor.this, newState, runner);
				}

				if (msg != null) {
					final ToolItem t = toolbar.button(c, msg, GamaIcons.create(imageName).image(), listener, SWT.LEFT);

					// without the following line, the display of the
					// text "msg" is not updated
					// correctly (at least for Windows OS)
					toolbar.sep(4, SWT.LEFT);
				} else {
					int i = 0;
					for (final String e : state.abbreviations) {
						enableButton(i++, e, listener);

					}
				}
				if (!GamlFileExtension.isExperiment(getDocument().getAdapter(IFile.class).getName())) {
					toolbar.button(IGamaColors.NEUTRAL, "Add experiment", GamaIcons.create("small.plus").image(),
							new CreateExperimentSelectionListener(GamlEditor.this, toolbar.getToolbar(SWT.LEFT)),
							SWT.LEFT);
				}

				toolbar.refresh(true);

			});
		}

	}

	@Override
	public void validationEnded(final Iterable<? extends IDescription> newExperiments, final ValidationContext status) {
		final String platformString = getURI().toPlatformString(true);
		final IFile myFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(platformString));
		final WrappedGamaFile file = (WrappedGamaFile) NavigatorRoot.getInstance().getManager().findWrappedInstanceOf(myFile);
		NavigatorRoot.getInstance().getManager().refreshResource(file);
		NavigatorRoot.getInstance().getManager().resourceChanged(null);
		if (newExperiments == null && state != null) {
			updateToolbar(state, true);
		} else {
			final GamlEditorState newState = new GamlEditorState(status, newExperiments);
			updateToolbar(newState, false);
			state = newState;
		}
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

	private void beforeSave() {
		if (!AutoStartup.EDITOR_CLEAN_UP.getValue()) { return; }
		final SourceViewer sv = getInternalSourceViewer();
		final Point p = sv.getSelectedRange();
		sv.setSelectedRange(0, sv.getDocument().getLength());
		if (sv.canDoOperation(SourceViewer.FORMAT)) {
			sv.doOperation(ISourceViewer.FORMAT);
		}
		sv.setSelectedRange(p.x, p.y);
	}

	@Override
	protected String[] collectContextMenuPreferencePages() {
		final String[] commonPages = super.collectContextMenuPreferencePages();
		final String[] langSpecificPages = new String[] { "pm.eclipse.editbox.pref.default" };
		return ObjectArrays.concat(langSpecificPages, commonPages, String.class);
	}

	/**
	 * @see msi.gama.lang.gaml.ui.editbox.IBoxEnabledEditor#getDecorator()
	 */
	@Override
	public IBoxDecorator getDecorator() {
		if (decorator == null) {
			createDecorator();
		}
		return decorator;
	}

	/**
	 * @see msi.gama.lang.gaml.ui.editbox.IBoxEnabledEditor#createDecorator(msi.gama.lang.gaml.ui.editbox.IBoxProvider)
	 */
	@Override
	public void createDecorator() {
		if (decorator != null) { return; }
		final IBoxProvider provider = BoxProviderRegistry.getInstance().getGamlProvider();
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
	 * @see msi.gama.lang.gaml.ui.editbox.IBoxEnabledEditor#decorate()
	 */
	@Override
	public void decorate(final boolean doIt) {
		if (doIt) {
			getDecorator().decorate(false);
		} else {
			getDecorator().undecorate();
		}
		enableUpdates(doIt);
	}

	@Override
	public void enableUpdates(final boolean visible) {
		getDecorator().enableUpdates(visible);
	}

	public void setDecorationEnabled(final boolean toggle) {
		decorationEnabled = toggle;
	}

	public void updateBoxes() {
		if (!decorationEnabled) { return; }
		getDecorator().forceUpdate();
	}

	@Override
	public boolean isDecorationEnabled() {
		return decorationEnabled;
	}

	private void assignBoxPartListener() {
		final IPartService partService = getSite().getWorkbenchWindow().getPartService();
		if (partService == null) { return; }
		if (partListeners == null) {
			partListeners = new HashMap<>();
		}
		final IPartListener2 oldListener = partListeners.get(partService);
		if (oldListener == null) {
			final IPartListener2 listener = new BoxDecoratorPartListener();
			partService.addPartListener(listener);
			partListeners.put(partService, listener);
		}
	}

	public void insertText(final String s) {
		final ITextSelection selection = (ITextSelection) getSelectionProvider().getSelection();
		final int offset = selection.getOffset();
		final int length = selection.getLength();
		try {
			new ReplaceEdit(offset, length, s).apply(getDocument());
		} catch (final MalformedTreeException e) {
			e.printStackTrace();
			return;
		} catch (final BadLocationException e) {
			e.printStackTrace();
			return;
		}
		getSelectionProvider().setSelection(new TextSelection(getDocument(), offset + s.length(), 0));
	}

	public String getSelectedText() {
		final ITextSelection sel = (ITextSelection) getSelectionProvider().getSelection();
		final int length = sel.getLength();
		if (length == 0) { return ""; }
		final IDocument doc = getDocument();
		try {
			return doc.get(sel.getOffset(), length);
		} catch (final BadLocationException e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * @see msi.gama.lang.gaml.ui.editor.IGamlEditor#openEditTemplateDialog()
	 */
	public boolean openEditTemplateDialog(final TemplatePersistenceData data, final boolean edit) {
		final GamlEditTemplateDialog d = getTemplateFactory().createDialog(data, edit, getEditorSite().getShell());
		if (d.open() == Window.OK) {
			getTemplateStore().directAdd(d.getData(), edit);
			return true;
		}
		return false;
	}

	/**
	 * @see msi.gama.lang.gaml.ui.editor.IGamlEditor#getNewTemplateId(java.lang.String)
	 */
	public String getNewTemplateId(final String path) {
		return getTemplateStore().getNewIdFromId(path);
	}

	/**
	 * @see msi.gama.lang.gaml.ui.editor.IGamlEditor#applyTemplate(org.eclipse.jface.text.templates.Template)
	 */

	public void applyTemplateAtTheEnd(final Template t) {

		try {
			final IDocument doc = getDocument();
			int offset = doc.getLineOffset(doc.getNumberOfLines() - 1);
			doc.replace(offset, 0, "\n\n");
			offset += 2;
			final int length = 0;
			final Position pos = new Position(offset, length);
			final XtextTemplateContextType ct = new XtextTemplateContextType();
			final DocumentTemplateContext dtc = new DocumentTemplateContext(ct, doc, pos);
			final IRegion r = new Region(offset, length);
			final TemplateProposal tp = new TemplateProposal(t, dtc, r, null);
			tp.apply(getInternalSourceViewer(), (char) 0, 0, offset);
		} catch (final BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void applyTemplate(final Template t) {
		// TODO Create a specific context type (with GAML specific variables ??)
		final XtextTemplateContextType ct = new XtextTemplateContextType();
		final IDocument doc = getDocument();
		final ITextSelection selection = (ITextSelection) getSelectionProvider().getSelection();
		final int offset = selection.getOffset();
		final int length = selection.getLength();
		final Position pos = new Position(offset, length);
		final DocumentTemplateContext dtc = new DocumentTemplateContext(ct, doc, pos);
		final IRegion r = new Region(offset, length);
		final TemplateProposal tp = new TemplateProposal(t, dtc, r, null);
		tp.apply(getInternalSourceViewer(), (char) 0, 0, offset);
	}

	public void toggleBlockComment() {
		final String s = getSelectedText().trim();
		if (s.startsWith("/*") && s.endsWith("*/")) {
			insertText(s.substring(2, s.length() - 2));
		} else {
			insertText("/*" + s + "*/");
		}
	}

	public void openOutlinePopup() {

		getDocument().readOnly(new CancelableUnitOfWork<Object, XtextResource>() {

			@Override
			public Object exec(final XtextResource state, final CancelIndicator c) throws Exception {
				final QuickOutlinePopup popup = new GamlQuickOutlinePopup(GamlEditor.this, toolbar);
				injector.injectMembers(popup);
				return popup.open();
			}
		});

	}

	/**
	 * @see ummisco.gama.ui.views.toolbar.IToolbarDecoratedView#createToolItem(int,
	 *      ummisco.gama.ui.views.toolbar.GamaToolbar2)
	 */
	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		this.toolbar = tb;
		buildRightToolbar();
	}

	@Override
	protected void handlePreferenceStoreChanged(final PropertyChangeEvent event) {
		final LineNumberColumn c;
		super.handlePreferenceStoreChanged(event);
		if (event.getProperty().equals(PREFERENCE_COLOR_BACKGROUND)) {
			// this.fSourceViewerDecorationSupport.updateOverviewDecorations();

			this.getVerticalRuler().getControl()
					.setBackground(GamaColors.get(AutoStartup.EDITOR_BACKGROUND_COLOR.getValue()).color());

			final Iterator e = ((CompositeRuler) getVerticalRuler()).getDecoratorIterator();
			while (e.hasNext()) {
				final IVerticalRulerColumn column = (IVerticalRulerColumn) e.next();
				column.getControl()
						.setBackground(GamaColors.get(AutoStartup.EDITOR_BACKGROUND_COLOR.getValue()).color());
				column.redraw();
			}
		}
	}

	public void doSearch() {
		if (findControl.getFindControl().isFocusControl()) {
			findControl.findNext();
		} else {
			findControl.getFindControl().setFocus();
		}
	}

	@Override
	protected void initializeDragAndDrop(final ISourceViewer viewer) {
		EDITOR_DRAG_RESOURCES.addChangeListener(dndChangedListener);
		super.initializeDragAndDrop(viewer);
	}

	@Override
	protected void installTextDragAndDrop(final ISourceViewer viewer) {
		dndHandler.install(!EDITOR_DRAG_RESOURCES.getValue());
	}

	@Override
	protected void uninstallTextDragAndDrop(final ISourceViewer viewer) {
		dndHandler.uninstall();
	}

	/**
	 * @return
	 */
	public URI getURI() {
		return fileURI;
	}

}
