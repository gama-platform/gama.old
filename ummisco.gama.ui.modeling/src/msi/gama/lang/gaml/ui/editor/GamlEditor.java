/*******************************************************************************************************
 *
 * GamlEditor.java, in ummisco.gama.ui.modeling, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.ui.editor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
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
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.ReplaceEdit;
// import org.eclipse.text.templates.TemplatePersistenceData;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.internal.texteditor.LineNumberColumn;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.XtextUIMessages;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.XtextSourceViewerConfiguration;
import org.eclipse.xtext.ui.editor.model.IXtextDocument;
import org.eclipse.xtext.ui.editor.model.XtextDocument;
import org.eclipse.xtext.ui.editor.outline.quickoutline.QuickOutlinePopup;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionProvider;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.templates.XtextTemplateContextType;
import org.eclipse.xtext.ui.editor.validation.IValidationIssueProcessor;
import org.eclipse.xtext.ui.editor.validation.MarkerCreator;
import org.eclipse.xtext.ui.editor.validation.MarkerIssueProcessor;
import org.eclipse.xtext.ui.editor.validation.ValidationJob;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;
import org.eclipse.xtext.ui.validation.MarkerTypeProvider;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.util.concurrent.CancelableUnitOfWork;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;

import com.google.common.collect.ObjectArrays;
import com.google.inject.Inject;
import com.google.inject.Injector;

import msi.gama.application.workbench.ThemeHelper;
import msi.gama.common.GamlFileExtension;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.common.preferences.IPreferenceChangeListener.IPreferenceAfterChangeListener;
import msi.gama.lang.gaml.resource.GamlResourceServices;
import msi.gama.lang.gaml.ui.decorators.GamlAnnotationImageProvider;
import msi.gama.lang.gaml.ui.editbox.BoxDecoratorPartListener;
import msi.gama.lang.gaml.ui.editbox.BoxProviderRegistry;
import msi.gama.lang.gaml.ui.editbox.IBoxDecorator;
import msi.gama.lang.gaml.ui.editbox.IBoxEnabledEditor;
import msi.gama.lang.gaml.ui.editor.toolbar.CreateExperimentSelectionListener;
import msi.gama.lang.gaml.ui.editor.toolbar.EditorSearchControls;
import msi.gama.lang.gaml.ui.editor.toolbar.EditorToolbar;
import msi.gama.lang.gaml.ui.editor.toolbar.GamlQuickOutlinePopup;
import msi.gama.lang.gaml.ui.editor.toolbar.OpenExperimentSelectionListener;
import msi.gama.lang.gaml.ui.editor.toolbar.OpenImportedErrorSelectionListener;
import msi.gama.lang.gaml.ui.editor.toolbar.RevalidateModelSelectionListener;
import msi.gama.lang.gaml.ui.templates.GamlEditTemplateDialogFactory;
import msi.gama.lang.gaml.ui.templates.GamlTemplateStore;
import msi.gama.lang.gaml.validation.IGamlBuilderListener;
import msi.gama.precompiler.GamlProperties;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.ValidationContext;
import msi.gaml.operators.Strings;
import ummisco.gama.dev.utils.DEBUG;
import ummisco.gama.dev.utils.FLAGS;
import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.interfaces.IModelRunner;
import ummisco.gama.ui.menus.GamaMenu;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.utils.WorkbenchHelper;
import ummisco.gama.ui.views.IGamlEditor;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;
import ummisco.gama.ui.views.toolbar.GamaToolbarFactory;
import ummisco.gama.ui.views.toolbar.IToolbarDecoratedView;
import ummisco.gama.ui.views.toolbar.Selector;

/**
 * The Class GamlEditor.
 */
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
		DEBUG.OFF();
	}

	/** The images. */
	static Map<String, Image> images = new HashMap();

	/** The menu images. */
	static Map<String, Image> menu_images = new HashMap();

	/** The max image height. */
	static int maxImageHeight = 0;

	/** The button padding. How much space between each experiment button */
	static int buttonPadding = 4;
	static {
		final var store = EditorsUI.getPreferenceStore();
		store.setDefault(AbstractDecoratedTextEditorPreferenceConstants.SHOW_RANGE_INDICATOR, false);
		store.setDefault("spellingEnabled", false);
		store.setValue("spellingEnabled", false);
		images.put(IKeyword.BATCH, GamaIcons.create("small.exp.batch.white").image());
		images.put(IKeyword.MEMORIZE, GamaIcons.create("small.exp.back.white").image());
		images.put("regular", GamaIcons.create("small.exp.run.white").image());
		menu_images.put(IKeyword.BATCH,
				GamaIcons.create(ThemeHelper.isDark() ? IGamaIcons.BUTTON_BATCH : IGamaIcons.MENU_BATCH).image());
		menu_images.put(IKeyword.MEMORIZE,
				GamaIcons.create(ThemeHelper.isDark() ? IGamaIcons.BUTTON_BACK : IGamaIcons.MENU_BACK).image());
		menu_images.put("regular",
				GamaIcons.create(ThemeHelper.isDark() ? IGamaIcons.BUTTON_GUI : IGamaIcons.MENU_GUI).image());

		images.put("new", GamaIcons.create("small.exp.plus").image());
		for (Image im : images.values()) { maxImageHeight = Math.max(maxImageHeight, im.getBounds().height); }
	}

	/**
	 * Instantiates a new gaml editor.
	 */
	public GamlEditor() {
		dndHandler = new GamlEditorDragAndDropHandler(this);
	}

	/** The decorator. */
	IBoxDecorator decorator;

	/** The state. */
	GamlEditorState state = new GamlEditorState(null, Collections.EMPTY_LIST);

	/** The toolbar. */
	GamaToolbar2 toolbar;

	/** The toolbar parent. */
	Composite toolbarParent;

	/** The find control. */
	private EditorSearchControls findControl;

	/** The decoration enabled. */
	boolean decorationEnabled = GamaPreferences.Modeling.EDITBOX_ENABLED.getValue();
	// boolean editToolbarEnabled = AutoStartup.EDITOR_SHOW_TOOLBAR.getValue();

	/** The resource set provider. */
	@Inject public IResourceSetProvider resourceSetProvider;

	/** The injector. */
	@Inject Injector injector;

	/** The runner. */
	@Inject IModelRunner runner;

	/** The template dialog factory. */
	@Inject private GamlEditTemplateDialogFactory templateDialogFactory;

	/** The template store. */
	@Inject private TemplateStore templateStore;

	/** The validator. */
	@Inject private IResourceValidator validator;

	/** The marker creator. */
	@Inject private MarkerCreator markerCreator;

	/** The marker type provider. */
	@Inject private MarkerTypeProvider markerTypeProvider;

	/** The issue resolver. */
	@Inject private IssueResolutionProvider issueResolver;

	/** The highlighting configuration. */
	@Inject private IHighlightingConfiguration highlightingConfiguration;

	/** The dnd handler. */
	private final GamlEditorDragAndDropHandler dndHandler;

	/** The box listener. */
	private final IPartListener2 boxListener = new BoxDecoratorPartListener();

	/** The dnd changed listener. */
	private final IPreferenceAfterChangeListener dndChangedListener = newValue -> {
		uninstallTextDragAndDrop(getInternalSourceViewer());
		installTextDragAndDrop(getInternalSourceViewer());
	};

	/** The is text drag and drop installed. */
	private boolean fIsTextDragAndDropInstalled;

	/** The file URI. */
	private URI fileURI;

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
		super.init(site, input);
		ummisco.gama.dev.utils.DEBUG.OUT("init of Editor for " + input.getName());
		assignBoxPartListener();
	}

	/** The image provider. */
	static GamlAnnotationImageProvider imageProvider = new GamlAnnotationImageProvider();

	@Override
	protected IAnnotationAccess createAnnotationAccess() {
		return new DefaultMarkerAnnotationAccess() {

			@Override
			public int getLayer(final Annotation annotation) {
				if (annotation.isMarkedDeleted()) return IAnnotationAccessExtension.DEFAULT_LAYER;
				return super.getLayer(annotation);
			}

			@Override
			public void paint(final Annotation annotation, final GC gc, final Canvas canvas, final Rectangle bounds) {
				final var image = imageProvider.getManagedImage(annotation);
				if (image != null) {
					ImageUtilities.drawImage(image, gc, canvas, bounds, SWT.CENTER, SWT.TOP);
				} else {
					super.paint(annotation, gc, canvas, bounds);
				}

			}

			@Override
			public boolean isPaintable(final Annotation annotation) {
				if (imageProvider.getManagedImage(annotation) != null) return true;
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
		var action = getAction("FoldingToggle"); //$NON-NLS-1$
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
		GamaPreferences.Modeling.EDITOR_DRAG_RESOURCES.removeChangeListener(dndChangedListener);
		GamlResourceServices.removeResourceListener(this);
		removeBoxPartListener();
		super.dispose();
	}

	/**
	 * Gets the template store.
	 *
	 * @return the template store
	 */
	public GamlTemplateStore getTemplateStore() { return (GamlTemplateStore) templateStore; }

	/**
	 * Gets the template factory.
	 *
	 * @return the template factory
	 */
	public GamlEditTemplateDialogFactory getTemplateFactory() { return templateDialogFactory; }

	/**
	 * Sets the show other enabled.
	 *
	 * @param showOtherEnabled
	 *            the new show other enabled
	 */
	public void setShowOtherEnabled(final boolean showOtherEnabled) {
		buildRightToolbar();
	}

	/**
	 * Builds the right toolbar.
	 */
	private void buildRightToolbar() {
		toolbar.wipe(SWT.LEFT, true);
		final var t = toolbar.button(IGamaColors.NEUTRAL, "Waiting...", GamaIcons.create("status.clock").image(), null,
				SWT.LEFT);
		toolbar.sep(4, SWT.LEFT);
		findControl = new EditorToolbar(this).fill(toolbar.getToolbar(SWT.RIGHT));

		// toolbar.sep(4, SWT.RIGHT);
		toolbar.refresh(true);
	}

	@Override
	public boolean isLineNumberRulerVisible() {
		final var store = getAdvancedPreferenceStore();
		return store != null ? store.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_LINE_NUMBER_RULER)
				: false;
	}

	/**
	 * Checks if is range indicator enabled.
	 *
	 * @return true, if is range indicator enabled
	 */
	public boolean isRangeIndicatorEnabled() { return getInternalSourceViewer().isProjectionMode(); }

	/**
	 * Gets the advanced preference store.
	 *
	 * @return the advanced preference store
	 */
	public final IPreferenceStore getAdvancedPreferenceStore() { return super.getPreferenceStore(); }

	/**
	 * Configure tab folder.
	 *
	 * @param compo
	 *            the compo
	 */
	private void configureTabFolder(final Composite compo) {
		var c = compo;
		while (c != null) {
			if (c instanceof CTabFolder) { break; }
			c = c.getParent();
		}
		if (c != null) {
			final var folder = (CTabFolder) c;
			folder.setMaximizeVisible(true);
			folder.setMinimizeVisible(true);
			folder.setMinimumCharacters(10);
			folder.setMRUVisible(true);
			folder.setUnselectedCloseVisible(true);
			folder.setHighlightEnabled(true);
			// folder.setTabHeight(16);
		}

	}

	@Override
	public void createPartControl(final Composite compo) {
		ummisco.gama.dev.utils.DEBUG.OUT("Creating part control of " + this.getPartName());
		configureTabFolder(compo);
		toolbarParent = GamaToolbarFactory.createToolbars(this, compo);
		final var layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 0;
		layout.marginRight = -5;
		toolbarParent.setLayout(layout);
		// toolbarParent.setBackground(IGamaColors.WHITE.color());

		// Asking the editor to fill the rest
		// final int style = GamaToolbarFactory.REDUCED_VIEW_TOOLBAR_HEIGHT.getValue() ? SWT.NONE : SWT.BORDER;
		final var editor = new Composite(toolbarParent, SWT.NONE);
		final var data = new GridData(SWT.FILL, SWT.FILL, true, true);
		editor.setLayoutData(data);
		editor.setLayout(new FillLayout());
		super.createPartControl(editor);
		editor.addControlListener(new ControlListener() {

			long lastEvent;

			@Override
			public void controlMoved(final ControlEvent e) {}

			@Override
			public void controlResized(final ControlEvent e) {
				WorkbenchHelper.asyncRun(() -> {
					long time = System.currentTimeMillis();
					if (time - lastEvent > 500) {
						lastEvent = time;
						updateToolbar(state, true);
					}
				});
			}
		});
		toolbarParent.requestLayout();
		installGestures();
		// this.getStyledText().setEditable(!FLAGS.IS_READ_ONLY);
	}

	@Override
	public boolean isEditable() { return FLAGS.IS_READ_ONLY ? false : super.isEditable(); }

	@Override
	protected void initializeDirtyStateSupport() {
		if (getDocument() != null) {
			fileURI = ((XtextDocument) getDocument()).getResourceURI();
			GamlResourceServices.addResourceListener(fileURI, GamlEditor.this);
			super.initializeDirtyStateSupport();
			scheduleValidationJob();
		}
	}

	/**
	 * Schedule validation job.
	 */
	private void scheduleValidationJob() {
		// if (!isEditable()) return;
		final IValidationIssueProcessor processor = new MarkerIssueProcessor(getResource(),
				getInternalSourceViewer().getAnnotationModel(), markerCreator, markerTypeProvider);
		final ValidationJob validate = new ValidationJob(validator, getDocument(), processor, CheckMode.FAST_ONLY) {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				final var issues = getDocument().readOnly(resource -> {
					if (resource.isValidationDisabled()) return Collections.emptyList();
					return validator.validate(resource, getCheckMode(), null);
				});
				processor.processIssues((List<Issue>) issues, monitor);
				return Status.OK_STATUS;
			}

		};
		validate.schedule();

	}

	@Override
	public boolean isOverviewRulerVisible() {
		final var viewer = getInternalSourceViewer();
		if (viewer == null) return super.isOverviewRulerVisible();
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
	public GamaSourceViewer getInternalSourceViewer() { return (GamaSourceViewer) super.getInternalSourceViewer(); }

	/**
	 * Install gestures.
	 */
	private void installGestures() {
		final var text = this.getInternalSourceViewer().getTextWidget();
		if (text != null) { text.addGestureListener(ge -> { if (ge.detail == SWT.GESTURE_END) { updateBoxes(); } }); }
	}

	@Override
	protected void installFoldingSupport(final ProjectionViewer projectionViewer) {
		super.installFoldingSupport(projectionViewer);
		if (!isRangeIndicatorEnabled()) { projectionViewer.doOperation(ProjectionViewer.TOGGLE); }
	}

	@Override
	protected void handleCursorPositionChanged() {
		if (getSelectionProvider() == null || getInternalSourceViewer() == null
				|| getInternalSourceViewer().getControl() == null
				|| getInternalSourceViewer().getControl().isDisposed())
			return;
		super.handleCursorPositionChanged();
		this.markInNavigationHistory();
	}

	/**
	 * Update toolbar.
	 *
	 * @param newState
	 *            the new state
	 * @param forceState
	 *            the force state
	 */
	private void updateToolbar(final GamlEditorState newState, final boolean forceState) {
		DEBUG.OUT("Updating toolbar for " + this.getTitle());
		if (forceState || !state.equals(newState)) {
			WorkbenchHelper.runInUI("Editor refresh", 50, m -> {
				if (toolbar == null || toolbar.isDisposed()) return;
				toolbar.wipe(SWT.LEFT, true);
				toolbar.setDefaultHeight(maxImageHeight);

				final var c = state.getColor();
				var msg = state.getStatus();

				Selector listener = null;
				String imageName = null;

				if (GamlEditorState.NO_EXP_DEFINED.equals(msg)) {
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
					final var t = toolbar.button(c, msg, GamaIcons.create(imageName).image(), listener, SWT.LEFT);
					final FlatButton b = (FlatButton) t.getControl();
					b.setRightPadding(buttonPadding);
				} else if (newState.showExperiments) {
					if (GamaPreferences.Modeling.EDITOR_EXPERIMENT_MENU.getValue()) {
						displayExperimentMenu(newState, listener);
					} else if (newState.abbreviations.size() <= 1
							|| !GamaPreferences.Modeling.EDITOR_COLLAPSE_BUTTONS.getValue()) {
						displayExperimentButtons(newState, listener);
					} else {
						int width = computeWidth(newState);
						if (width > toolbar.getSize().x - toolbar.getToolbar(SWT.RIGHT).getSize().x) {
							displayExperimentMenu(newState, listener);
						} else {
							displayExperimentButtons(newState, listener);
						}
					}
					if (!GamlFileExtension.isExperiment(getDocument().getAdapter(IFile.class).getName())) {
						toolbar.button(IGamaColors.NEUTRAL, "Add Experiment", images.get("new"),
								new CreateExperimentSelectionListener(GamlEditor.this, toolbar.getToolbar(SWT.LEFT)),
								SWT.LEFT);
					}
				}

				toolbar.refresh(true);

			});
		}

	}

	/**
	 * Compute width.
	 *
	 * @param newState
	 *            the new state
	 * @return the int
	 */
	private int computeWidth(final GamlEditorState newState) {
		int width = 0;
		FlatButton t;
		for (final String text : newState.abbreviations) {
			if (text == null) { continue; }
			t = FlatButton.button(toolbar.getToolbar(SWT.LEFT), IGamaColors.OK, text, images.get(IKeyword.BATCH));
			width += t.computeSize(SWT.DEFAULT, 12).x + 2 * buttonPadding;
			t.dispose();
		}
		t = FlatButton.button(toolbar.getToolbar(SWT.LEFT), IGamaColors.OK, "Add Experiment", images.get("new"));
		width += t.computeSize(SWT.DEFAULT, 12).x + 2 * buttonPadding;
		t.dispose();
		return width;
	}

	/**
	 * Display experiment buttons.
	 *
	 * @param newState
	 *            the new state
	 * @param listener
	 *            the listener
	 */
	private void displayExperimentButtons(final GamlEditorState state, final Selector listener) {
		var index = 0;
		for (final String text : state.abbreviations) {
			if (text == null) return;
			final var expType = state.types.get(index++);
			final var type = IKeyword.BATCH.equals(expType) ? "batch" : IKeyword.MEMORIZE.equals(expType) ? "memorize"
					: "regular";
			final var image = images.get(type);
			final var t = toolbar.button(IGamaColors.OK, text, image, SWT.LEFT);
			// t.setWidth(t.getWidth() + buttonPadding);
			final FlatButton b = (FlatButton) t.getControl();
			b.setRightPadding(buttonPadding);
			// b.setImageHeight(maxImageHeight);
			b.setToolTipText("Executes the " + type + " experiment " + text);
			b.addSelectionListener(listener);
			t.setData("index", index);
			b.setData("exp", text);
		}
	}

	/**
	 * Display experiment menu.
	 *
	 * @param state
	 *            the state
	 * @param listener
	 *            the listener
	 */
	private void displayExperimentMenu(final GamlEditorState state, final Selector listener) {

		final var menu = toolbar.menu(IGamaColors.OK, "Run Experiment...", SWT.LEFT);
		final FlatButton b = (FlatButton) menu.getControl();
		b.setRightPadding(buttonPadding);

		((FlatButton) menu.getControl()).addSelectionListener(new SelectionAdapter() {

			Menu menu;

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (menu == null) {
					menu = new Menu(toolbar.getShell(), SWT.POP_UP);
					fillMenu();
				}
				final Point point = toolbar.toDisplay(new Point(e.x, e.y + toolbar.getSize().y));
				menu.setLocation(point.x, point.y);
				menu.setVisible(true);
			}

			private void fillMenu() {
				var index = 0;
				for (final String text : state.abbreviations) {
					if (text == null) return;
					final var expType = state.types.get(index++);
					final String type = IKeyword.BATCH.equals(expType) ? "batch"
							: IKeyword.MEMORIZE.equals(expType) ? "memorize" : "regular";
					final Image image = menu_images.get(type);
					GamaMenu.action(menu, text, listener, image).setData("exp", text);
				}
			}
		});
	}

	@Override
	public void validationEnded(final ModelDescription model, final Iterable<? extends IDescription> newExperiments,
			final ValidationContext status) {
		if (GamaPreferences.Experimental.REQUIRED_PLUGINS.getValue() && model != null && !status.hasErrors()) {
			String requires = "@" + IKeyword.PRAGMA_REQUIRES;
			GamlProperties meta = new GamlProperties();
			model.collectMetaInformation(meta);
			String newLine = requires + " " + meta.get(GamlProperties.PLUGINS);
			GamaSourceViewer viewer = getInternalSourceViewer();
			IXtextDocument document = getDocument();
			WorkbenchHelper.asyncRun(() -> {
				int offset;
				try {
					offset = document.search(0, requires, true, true, false);
					if (offset > -1) {
						int length = document.getLineInformationOfOffset(offset).getLength();
						if (!newLine.equals(document.get(offset, length))) {
							new ReplaceEdit(offset, length, newLine).apply(document);
						}
					} else {
						new InsertEdit(0, Strings.LN + newLine + Strings.LN + Strings.LN).apply(getDocument());
					}
				} catch (BadLocationException e) {}
			});
		}

		// }
		// });

		if (newExperiments == null && state != null) {
			updateToolbar(state, true);
		} else {
			final var newState = new GamlEditorState(status, newExperiments);
			updateToolbar(newState, false);
			state = newState;
		}
	}

	/**
	 * The Class GamaSourceViewerConfiguration.
	 */
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
	 * Before save.
	 */
	private void beforeSave() {
		if (!GamaPreferences.Modeling.EDITOR_CLEAN_UP.getValue()) return;
		final SourceViewer sv = getInternalSourceViewer();
		final var p = sv.getSelectedRange();
		sv.setSelectedRange(0, sv.getDocument().getLength());
		if (sv.canDoOperation(ISourceViewer.FORMAT)) { sv.doOperation(ISourceViewer.FORMAT); }
		sv.setSelectedRange(p.x, p.y);
	}

	@Override
	protected String[] collectContextMenuPreferencePages() {
		final var commonPages = super.collectContextMenuPreferencePages();
		final String[] langSpecificPages = { "pm.eclipse.editbox.pref.default" };
		return ObjectArrays.concat(langSpecificPages, commonPages, String.class);
	}

	/**
	 * @see msi.gama.lang.gaml.ui.editbox.IBoxEnabledEditor#getDecorator()
	 */
	@Override
	public IBoxDecorator getDecorator() {
		if (decorator == null) { createDecorator(); }
		return decorator;
	}

	/**
	 * @see msi.gama.lang.gaml.ui.editbox.IBoxEnabledEditor#createDecorator(msi.gama.lang.gaml.ui.editbox.IBoxProvider)
	 */
	@Override
	public void createDecorator() {
		if (decorator != null) return;
		final var provider = BoxProviderRegistry.getInstance().getGamlProvider();
		decorator = provider.createDecorator();
		decorator.setStyledText(getStyledText());
		decorator.setSettings(provider.getEditorsBoxSettings());
	}

	/**
	 * @return
	 */
	private StyledText getStyledText() { return (StyledText) super.getAdapter(Control.class); }

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

	/**
	 * Sets the decoration enabled.
	 *
	 * @param toggle
	 *            the new decoration enabled
	 */
	public void setDecorationEnabled(final boolean toggle) { decorationEnabled = toggle; }

	/**
	 * Update boxes.
	 */
	public void updateBoxes() {
		if (!decorationEnabled) return;
		getDecorator().forceUpdate();
	}

	@Override
	public boolean isDecorationEnabled() { return decorationEnabled; }

	/**
	 * Assign box part listener.
	 */
	private void assignBoxPartListener() {
		WorkbenchHelper.getPage().addPartListener(boxListener);
	}

	/**
	 * Removes the box part listener.
	 */
	private void removeBoxPartListener() {
		WorkbenchHelper.getPage().removePartListener(boxListener);
	}

	/**
	 * Insert text.
	 *
	 * @param s
	 *            the s
	 */
	public void insertText(final String s) {
		final var selection = (ITextSelection) getSelectionProvider().getSelection();
		final var offset = selection.getOffset();
		final var length = selection.getLength();
		try {
			new ReplaceEdit(offset, length, s).apply(getDocument());
		} catch (final MalformedTreeException | BadLocationException e) {
			e.printStackTrace();
			return;
		}
		getSelectionProvider().setSelection(new TextSelection(getDocument(), offset + s.length(), 0));
	}

	/**
	 * Gets the selected text.
	 *
	 * @return the selected text
	 */
	public String getSelectedText() {
		final var sel = (ITextSelection) getSelectionProvider().getSelection();
		final var length = sel.getLength();
		if (length == 0) return "";
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
		final var d = getTemplateFactory().createDialog(data, edit, getEditorSite().getShell());
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
			var offset = doc.getLineOffset(doc.getNumberOfLines() - 1);
			doc.replace(offset, 0, "\n\n");
			offset += 2;
			final var length = 0;
			final var pos = new Position(offset, length);
			final var ct = new XtextTemplateContextType();
			final var dtc = new DocumentTemplateContext(ct, doc, pos);
			final IRegion r = new Region(offset, length);
			final var tp = new TemplateProposal(t, dtc, r, null);
			tp.apply(getInternalSourceViewer(), (char) 0, 0, offset);
		} catch (final BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Apply template.
	 *
	 * @param t
	 *            the t
	 */
	public void applyTemplate(final Template t) {
		// TODO Create a specific context type (with GAML specific variables ??)
		final var ct = new XtextTemplateContextType();
		final IDocument doc = getDocument();
		final var selection = (ITextSelection) getSelectionProvider().getSelection();
		final var offset = selection.getOffset();
		final var length = selection.getLength();
		final var pos = new Position(offset, length);
		final var dtc = new DocumentTemplateContext(ct, doc, pos);
		final IRegion r = new Region(offset, length);
		final var tp = new TemplateProposal(t, dtc, r, null);
		tp.apply(getInternalSourceViewer(), (char) 0, 0, offset);
	}

	/**
	 * Open outline popup.
	 */
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
		if (PREFERENCE_COLOR_BACKGROUND.equals(event.getProperty())) {
			// this.fSourceViewerDecorationSupport.updateOverviewDecorations();

			this.getVerticalRuler().getControl()
					.setBackground(GamaColors.get(GamaPreferences.Modeling.EDITOR_BACKGROUND_COLOR.getValue()).color());

			final Iterator e = ((CompositeRuler) getVerticalRuler()).getDecoratorIterator();
			while (e.hasNext()) {
				final var column = (IVerticalRulerColumn) e.next();
				column.getControl().setBackground(
						GamaColors.get(GamaPreferences.Modeling.EDITOR_BACKGROUND_COLOR.getValue()).color());
				column.redraw();
			}
		}
	}

	/**
	 * Do search.
	 */
	public void doSearch() {
		if (findControl.getFindControl().isFocusControl()) {
			findControl.findNext();
		} else {
			findControl.getFindControl().setFocus();
		}
	}

	@Override
	protected void initializeDragAndDrop(final ISourceViewer viewer) {
		GamaPreferences.Modeling.EDITOR_DRAG_RESOURCES.addChangeListener(dndChangedListener);
		super.initializeDragAndDrop(viewer);
	}

	@Override
	protected void installTextDragAndDrop(final ISourceViewer viewer) {
		dndHandler.install(!GamaPreferences.Modeling.EDITOR_DRAG_RESOURCES.getValue());
	}

	@Override
	protected void uninstallTextDragAndDrop(final ISourceViewer viewer) {
		dndHandler.uninstall();
	}

	/**
	 * Creates a composite ruler to be used as the vertical ruler by this editor. Subclasses may re-implement this
	 * method.
	 *
	 * @return the vertical ruler
	 */
	@Override
	protected CompositeRuler createCompositeRuler() {
		return new CompositeRuler(6);
	}

	/**
	 * @return
	 */
	public URI getURI() { return fileURI; }

}
