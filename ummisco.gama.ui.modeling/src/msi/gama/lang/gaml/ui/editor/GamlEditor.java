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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyListener;
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
import org.eclipse.swt.widgets.Display;
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
import org.eclipse.xtext.ui.editor.outline.quickoutline.QuickOutlinePopup;
import org.eclipse.xtext.ui.editor.templates.XtextTemplateContextType;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.common.collect.ObjectArrays;
import com.google.inject.Inject;
import com.google.inject.Injector;

import msi.gama.lang.gaml.ui.AutoStartup;
import msi.gama.lang.gaml.ui.decorators.GamlAnnotationImageProvider;
import msi.gama.lang.gaml.ui.editbox.BoxDecoratorPartListener;
import msi.gama.lang.gaml.ui.editbox.BoxProviderRegistry;
import msi.gama.lang.gaml.ui.editbox.IBoxDecorator;
import msi.gama.lang.gaml.ui.editbox.IBoxEnabledEditor;
import msi.gama.lang.gaml.ui.editbox.IBoxProvider;
import msi.gama.lang.gaml.ui.editor.toolbar.CreateExperimentSelectionListener;
import msi.gama.lang.gaml.ui.editor.toolbar.EditToolbar;
import msi.gama.lang.gaml.ui.editor.toolbar.GamlQuickOutlinePopup;
import msi.gama.lang.gaml.ui.editor.toolbar.ImportedInButton;
import msi.gama.lang.gaml.ui.editor.toolbar.OpenExperimentSelectionListener;
import msi.gama.lang.gaml.ui.editor.toolbar.OpenImportedErrorSelectionListener;
import msi.gama.lang.gaml.ui.editor.toolbar.OtherExperimentsButton;
import msi.gama.lang.gaml.ui.editor.toolbar.RevalidateModelSelectionListener;
import msi.gama.lang.gaml.ui.templates.GamlEditTemplateDialog;
import msi.gama.lang.gaml.ui.templates.GamlEditTemplateDialogFactory;
import msi.gama.lang.gaml.ui.templates.GamlTemplateStore;
import msi.gama.lang.gaml.validation.IGamlBuilderListener;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ValidationContext;
import ummisco.gama.ui.controls.FlatButton;
import ummisco.gama.ui.controls.ITooltipDisplayer;
import ummisco.gama.ui.interfaces.IModelRunner;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaColors.GamaUIColor;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaColors;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.views.toolbar.GamaToolbar2;
import ummisco.gama.ui.views.toolbar.GamaToolbarFactory;
import ummisco.gama.ui.views.toolbar.IToolbarDecoratedView;

/*
 * The class GamlEditor.
 *
 * @author drogoul
 *
 * @since 4 mars 2012
 */
@SuppressWarnings("all")
public class GamlEditor extends XtextEditor
		implements IGamlBuilderListener, IBoxEnabledEditor, IToolbarDecoratedView, ITooltipDisplayer {

	static {
		final IPreferenceStore store = EditorsUI.getPreferenceStore();
		store.setDefault(AbstractDecoratedTextEditorPreferenceConstants.SHOW_RANGE_INDICATOR, false);
		store.setDefault(SpellingService.PREFERENCE_SPELLING_ENABLED, false);
		store.setValue(SpellingService.PREFERENCE_SPELLING_ENABLED, false);

	}

	public GamlEditor() {
	}

	protected static Map<IPartService, IPartListener2> partListeners;

	IBoxDecorator decorator;
	GamlEditorState state = new GamlEditorState(null, Collections.EMPTY_LIST);
	GamaToolbar2 toolbar;
	Composite toolbarParent;
	EditToolbar editToolbar;
	boolean decorationEnabled = AutoStartup.EDITBOX_ENABLED.getValue();
	boolean editToolbarEnabled = AutoStartup.EDITOR_SHOW_TOOLBAR.getValue();

	@Inject public IResourceSetProvider resourceSetProvider;
	@Inject Injector injector;
	@Inject IModelRunner runner;
	@Inject private GamlEditTemplateDialogFactory templateDialogFactory;

	@Inject private TemplateStore templateStore;

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
				if (annotation.isMarkedDeleted()) {
					return IAnnotationAccessExtension.DEFAULT_LAYER;
				}
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
				if (imageProvider.getManagedImage(annotation) != null) {
					return true;
				}
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

	private void foldRegionsOnStartup(final ProjectionAnnotationModel model) {
		//
		// // TODO retrieve set of types to fold from helper, as other types
		// might
		// // be added
		// final Set<EClass> typesToFold = new HashSet<EClass>();
		// if
		// (preferencStore.getBoolean(TurtlePreferenceConstants.FOLD_TRIPLES_KEY))
		// {
		// typesToFold.add(XturtlePackage.Literals.TRIPLES);
		// }
		// if
		// (preferencStore.getBoolean(TurtlePreferenceConstants.FOLD_STRINGS_KEY))
		// {
		// typesToFold.add(XturtlePackage.Literals.STRING_LITERAL);
		// }
		// if
		// (preferencStore.getBoolean(TurtlePreferenceConstants.FOLD_DIRECTIVES_KEY))
		// {
		// typesToFold.add(XturtlePackage.Literals.DIRECTIVES);
		// }
		// if
		// (preferencStore.getBoolean(TurtlePreferenceConstants.FOLD_BLANK_COLL))
		// {
		// typesToFold.add(XturtlePackage.Literals.BLANK_COLLECTION);
		// }
		// if
		// (preferencStore.getBoolean(TurtlePreferenceConstants.FOLD_BLANK_OBJ))
		// {
		// typesToFold.add(XturtlePackage.Literals.BLANK_OBJECTS);
		// }
		// if (!typesToFold.isEmpty()) {
		// final List<Annotation> changes = new ArrayList<Annotation>();
		// final Iterator<?> iterator = model.getAnnotationIterator();
		// while (iterator.hasNext()) {
		// final Object next = iterator.next();
		// if (next instanceof ProjectionAnnotation) {
		// final ProjectionAnnotation pa = (ProjectionAnnotation) next;
		// final Position position = model.getPosition(pa);
		// if (position instanceof TypedFoldedRegion
		// && typesToFold.contains(((TypedFoldedRegion) position).getType())) {
		// pa.markCollapsed();
		// changes.add(pa);
		// }
		// }
		// }
		// model.modifyAnnotations(null, null, changes.toArray(new
		// Annotation[0]));
		// }
	}

	@Override
	public void dispose() {
		decorator = null;
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
		toolbar.wipe(SWT.RIGHT, true);
		new OtherExperimentsButton(this, toolbar, runner, resourceSetProvider);
		toolbar.sep(4, SWT.LEFT);
		new ImportedInButton(this, toolbar);
		final ToolItem toggle = toolbar.button("action.toolbar.toggle2", null, "Toggle edit toolbar", null, SWT.RIGHT);
		toggle.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				editToolbarEnabled = !editToolbarEnabled;
				editToolbar.setVisible(editToolbarEnabled);
				toggle.setImage(editToolbarEnabled ? GamaIcons.create("action.toolbar.toggle2").image()
						: GamaIcons.create("action.toolbar.toggle3").image());
				toolbarParent.layout();
			}

		});
		toolbar.refresh(true);
	}

	@Override
	public boolean isLineNumberRulerVisible() {
		final IPreferenceStore store = getAdvancedPreferenceStore();
		return store != null ? store.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_LINE_NUMBER_RULER)
				: false;
	}

	public boolean isRangeIndicatorEnabled() {
		final IPreferenceStore store = getAdvancedPreferenceStore();
		return store != null ? store.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.SHOW_RANGE_INDICATOR)
				: false;
	}

	public final IPreferenceStore getAdvancedPreferenceStore() {
		return super.getPreferenceStore();
	}

	@Override
	public void createPartControl(final Composite compo) {
		toolbarParent = GamaToolbarFactory.createToolbars(this, compo);
		buildRightToolbar();

		final GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		toolbarParent.setLayout(layout);
		toolbarParent.setBackground(IGamaColors.WHITE.color());
		editToolbar = new EditToolbar(this, toolbarParent);
		editToolbar.setVisible(editToolbarEnabled);

		// Asking the editor to fill the rest
		final Composite editor = new Composite(toolbarParent, SWT.BORDER);
		final GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		editor.setLayoutData(data);
		editor.setLayout(new FillLayout());
		super.createPartControl(editor);
		final ISourceViewer viewer = getSourceViewer();
		if (viewer instanceof GamaSourceViewer) {
			((GamaSourceViewer) viewer).setResourceListener(this);
		}
		toolbarParent.layout();
		installGestures();

	}

	//
	@Override
	public void setFocus() {
		if (getSourceViewer() != null && getSourceViewer().getTextWidget() != null
				&& !getSourceViewer().getTextWidget().isFocusControl()) {
			getSourceViewer().getTextWidget().forceFocus();
		}
	}

	private void installGestures() {
		editToolbar.installGesturesFor(this);
	}

	@Override
	protected void installFoldingSupport(final ProjectionViewer projectionViewer) {
		super.installFoldingSupport(projectionViewer);
		if (!getAdvancedPreferenceStore()
				.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.SHOW_RANGE_INDICATOR))
			projectionViewer.doOperation(ProjectionViewer.TOGGLE);
	};

	@Override
	protected void handleCursorPositionChanged() {
		super.handleCursorPositionChanged();
		this.markInNavigationHistory();
	}

	private void enableButton(final int index, final String text, final SelectionListener listener) {
		if (text == null) {
			return;
		}
		final boolean isBatch = state.types.get(index);
		final Image image = isBatch ? IGamaIcons.BUTTON_BATCH.image() : IGamaIcons.BUTTON_GUI.image();
		final ToolItem t = toolbar.button(IGamaColors.OK, text, image, SWT.LEFT);
		final String type = isBatch ? "batch" : "regular";
		t.getControl().setToolTipText("Executes the " + type + " experiment " + text);
		((FlatButton) t.getControl()).addSelectionListener(listener);
		toolbar.sep(4, SWT.LEFT);
	}

	@Override
	public void stopDisplayingTooltips() {
		updateToolbar(state, true);
	}

	@Override
	public void displayTooltip(final String text, final GamaUIColor color) {
		if (toolbar == null || toolbar.isDisposed()) {
			return;
		}
		toolbar.tooltip(text, color, SWT.LEFT);
	}

	private void updateToolbar(final GamlEditorState newState, final boolean forceState) {
		if (forceState || !state.equals(newState)) {
			Display.getDefault().asyncExec(new Runnable() {

				@Override
				public void run() {
					if (toolbar == null || toolbar.isDisposed()) {
						return;
					}
					toolbar.wipe(SWT.LEFT, true);

					final GamaUIColor c = state.getColor();
					final String msg = state.getStatus();

					SelectionListener listener = null;
					String imageName = null;

					if (msg == GamlEditorState.NO_EXP_DEFINED) {
						listener = new CreateExperimentSelectionListener(GamlEditor.this, toolbar.getToolbar(SWT.LEFT));
						imageName = "small.dropdown";
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
						final ToolItem t = toolbar.button(c, msg, GamaIcons.create(imageName).image(), listener,
								SWT.LEFT);

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

					toolbar.refresh(true);

				}
			});
		}

	}

	@Override
	public void validationEnded(final Collection<? extends IDescription> newExperiments,
			final ValidationContext status) {
		if (newExperiments == null && state != null)
			updateToolbar(state, true);
		else {
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
		if (!AutoStartup.EDITOR_CLEAN_UP.getValue()) {
			return;
		}
		final SourceViewer sv = (SourceViewer) getInternalSourceViewer();
		final Point p = sv.getSelectedRange();
		sv.setSelectedRange(0, sv.getDocument().getLength());
		if (sv.canDoOperation(SourceViewer.FORMAT))
			sv.doOperation(ISourceViewer.FORMAT);
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
		if (decorator != null) {
			return;
		}
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
		if (!decorationEnabled) {
			return;
		}
		getDecorator().forceUpdate();
	}

	@Override
	public boolean isDecorationEnabled() {
		return decorationEnabled;
	}

	private void assignBoxPartListener() {
		final IPartService partService = getSite().getWorkbenchWindow().getPartService();
		if (partService == null) {
			return;
		}
		if (partListeners == null) {
			partListeners = new HashMap<IPartService, IPartListener2>();
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
		if (length == 0) {
			return "";
		}
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
		selectAndReveal(this.getStyledText().getCharCount() + 1, 0);
		insertText("\n");
		applyTemplate(t);
		selectAndReveal(this.getStyledText().getCharCount() + 1, 0);
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

	/**
	 * @return
	 */
	public EditToolbar getEditToolbar() {
		return editToolbar;
	}

	public void openOutlinePopup() {

		getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {

			@Override
			public void process(final XtextResource state) throws Exception {
				final QuickOutlinePopup popup = new GamlQuickOutlinePopup(GamlEditor.this);
				injector.injectMembers(popup);
				popup.open();
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
			// this.getVerticalRuler().getControl().redraw();
			// ((SourceViewer)
			// this.getInternalSourceViewer()).getControl().setBackground(
			// GamaColors.get(EDITOR_BACKGROUND_COLOR.getValue()).color());

		}
	}

	@Override
	public void removeVerifyListener(final VerifyListener listener) {
		super.removeVerifyListener(listener);
	}
}
