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
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
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
import org.eclipse.ui.internal.texteditor.LineNumberColumn;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.XtextSourceViewerConfiguration;
import org.eclipse.xtext.ui.editor.outline.quickoutline.QuickOutlinePopup;
import org.eclipse.xtext.ui.editor.templates.XtextTemplateContextType;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;
import org.eclipse.xtext.util.concurrent.IUnitOfWork;

import com.google.common.collect.ObjectArrays;
import com.google.inject.Inject;
import com.google.inject.Injector;

import msi.gama.common.interfaces.IGui;
import msi.gama.gui.swt.GamaColors;
import msi.gama.gui.swt.GamaColors.GamaUIColor;
import msi.gama.gui.swt.GamaIcons;
import msi.gama.gui.swt.IGamaColors;
import msi.gama.gui.swt.IGamaIcons;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.swt.commands.GamaMenu;
import msi.gama.gui.swt.controls.FlatButton;
import msi.gama.gui.swt.controls.GamaToolbar2;
import msi.gama.gui.swt.controls.ITooltipDisplayer;
import msi.gama.gui.views.IToolbarDecoratedView;
import msi.gama.gui.views.actions.GamaToolbarFactory;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.resource.GamlModelBuilder;
import msi.gama.lang.gaml.resource.GamlResource;
import msi.gama.lang.gaml.ui.XtextGui;
import msi.gama.lang.gaml.ui.decorators.GamlAnnotationImageProvider;
import msi.gama.lang.gaml.ui.templates.GamlEditTemplateDialog;
import msi.gama.lang.gaml.ui.templates.GamlEditTemplateDialogFactory;
import msi.gama.lang.gaml.ui.templates.GamlTemplateStore;
import msi.gama.lang.gaml.validation.IGamlBuilderListener.IGamlBuilderListener2;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.ErrorCollector;
import msi.gaml.descriptions.IDescription;
import ummisco.gaml.editbox.EditBox;
import ummisco.gaml.editbox.IBoxDecorator;
import ummisco.gaml.editbox.IBoxEnabledEditor;
import ummisco.gaml.editbox.IBoxProvider;

/*
 * The class GamlEditor.
 *
 * @author drogoul
 *
 * @since 4 mars 2012
 */
@SuppressWarnings("all")
public class GamlEditor extends XtextEditor
		implements IGamlBuilderListener2, IBoxEnabledEditor, IToolbarDecoratedView, ITooltipDisplayer {

	public GamlEditor() {
	}

	protected static Map<IPartService, IPartListener2> partListeners;

	IBoxDecorator decorator;
	GamlEditorState state = new GamlEditorState(null, Collections.EMPTY_LIST);
	GamaToolbar2 toolbar;
	Composite toolbarParent;
	EditToolbar editToolbar;
	boolean decorationEnabled = XtextGui.EDITBOX_ENABLED.getValue();
	boolean editToolbarEnabled = XtextGui.EDITOR_SHOW_TOOLBAR.getValue();
	// OtherExperimentsButton other;

	@Inject
	IResourceSetProvider resourceSetProvider;
	//
	// @Inject
	// private CompoundXtextEditorCallback callback;
	//
	// @Inject
	// private GamlJavaValidator validator;

	@Inject
	Injector injector;

	@Inject
	private GamlEditTemplateDialogFactory templateDialogFactory;

	@Inject
	private TemplateStore templateStore;

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
	public void dispose() {
		if (getDocument() != null) {
			getDocument().readOnly(new IUnitOfWork.Void<XtextResource>() {

				@Override
				public void process(final XtextResource state) throws Exception {
					((GamlResource) state).removeListener();
				}

			});
		}

		decorator = null;

		super.dispose();
	}

	public GamlTemplateStore getTemplateStore() {

		return (GamlTemplateStore) /* templatePrefs.getTemplateStore(); */templateStore;
	}

	public GamlEditTemplateDialogFactory getTemplateFactory() {
		return templateDialogFactory;
	}

	public void setShowOtherEnabled(final boolean showOtherEnabled) {
		buildRightToolbar();
	}

	private void buildRightToolbar() {
		toolbar.wipe(SWT.RIGHT, true);
		/* OtherExperimentsButton other = */new OtherExperimentsButton(this, toolbar);
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
				: true;
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
	protected void handleCursorPositionChanged() {
		super.handleCursorPositionChanged();
		this.markInNavigationHistory();
	}

	private void gotoEditor(final GamaRuntimeException exception) {
		final EObject o = exception.getEditorContext();
		if (o != null) {
			GAMA.getGui().asyncRun(new Runnable() {

				@Override
				public void run() {
					GAMA.getGui().editModel(o);
				}
			});
		}

	}

	private final SelectionListener listener = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent evt) {
			final IGui gui = GAMA.getRegularGui();
			// We refuse to run if there is no XtextGui available.
			if (!(gui instanceof XtextGui)) {
				return;
			}
			GamlEditor.this.performSave(true, null);
			if (XtextGui.EDITOR_SAVE.getValue()) {
				SwtGui.getPage().saveAllEditors(XtextGui.EDITOR_SAVE_ASK.getValue());
			}
			String name = ((FlatButton) evt.widget).getText();
			final int i = state.abbreviations.indexOf(name);
			if (i == -1) {
				return;
			}
			name = state.experiments.get(i);
			IModel model = null;
			try {
				model = getDocument().readOnly(new IUnitOfWork<IModel, XtextResource>() {

					@Override
					public IModel exec(final XtextResource state) throws Exception {
						// List<GamlCompilationError> errors = new ArrayList();
						return /* GamlModelBuilder.getInstance() */new GamlModelBuilder().compile(state);
					}

				});
			} catch (final GamaRuntimeException e) {
				gotoEditor(e);
				GAMA.getGui().error("Experiment " + name + " cannot be instantiated because of the following error: "
						+ e.getMessage());
			}
			if (model == null) {
				return;
			}
			GAMA.getGui().runModel(model, name);
		}

	};

	private void enableButton(final int index, final String text) {
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

					final GamaUIColor c = state.getColor();
					final String msg = state.getStatus();
					if (msg != null) {
						toolbar.wipe(SWT.LEFT, true);
						final ToolItem t = toolbar.button(c, msg, new SelectionListener() {

							@Override
							public void widgetSelected(final SelectionEvent e) {

								final GamaMenu menu = new GamaMenu() {

									@Override
									protected void fillMenu() {
										final Map<String, URI> msgs = newState.getImportedErrors();
										for (final String s : msgs.keySet()) {
											action(s, new SelectionAdapter() {

												@Override
												public void widgetSelected(final SelectionEvent e1) {
													GAMA.getGui().editModel(msgs.get(s));
												}

											}, null);
										}

									}
								};
								menu.open(toolbar.getToolbar(SWT.LEFT), e);

							}

							@Override
							public void widgetDefaultSelected(final SelectionEvent e) {
								widgetSelected(e);
							}
						}, SWT.LEFT);
						// For Issue #1697
						if (!state.getImportedErrors().isEmpty())
							((FlatButton) t.getControl()).setImage(GamaIcons.create("small.dropdown").image());
						// without the 2 following lines, the display of the
						// text "msg" is not updated
						// correctly (at least for Windows OS)
						toolbar.sep(4, SWT.LEFT);
						toolbar.refresh(true);
					} else {
						toolbar.wipe(SWT.LEFT, true);
						int i = 0;
						for (final String e : state.abbreviations) {
							enableButton(i++, e);
						}
						toolbar.refresh(true);
					}

				}
			});
		}

	}

	/**
	 * @see msi.gama.common.interfaces.IGamlBuilder.Listener#validationEnded(boolean)
	 */
	@Override
	public void validationEnded(final Set<String> newExperiments, final ErrorCollector status) {

	}

	@Override
	public void validationEnded(final Collection<? extends IDescription> newExperiments, final ErrorCollector status) {
		final GamlEditorState newState = new GamlEditorState(status, newExperiments);
		updateToolbar(newState, false);
		state = newState;
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
		if (!XtextGui.EDITOR_CLEAN_UP.getValue()) {
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
	 * @see ummisco.gaml.editbox.IBoxEnabledEditor#getDecorator()
	 */
	@Override
	public IBoxDecorator getDecorator() {
		if (decorator == null) {
			createDecorator();
		}
		return decorator;
	}

	/**
	 * @see ummisco.gaml.editbox.IBoxEnabledEditor#createDecorator(ummisco.gaml.editbox.IBoxProvider)
	 */
	@Override
	public void createDecorator() {
		if (decorator != null) {
			return;
		}
		final IBoxProvider provider = EditBox.getDefault().getGamlProvider();
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
	 * @see msi.gama.gui.views.IToolbarDecoratedView#createToolItem(int,
	 *      msi.gama.gui.swt.controls.GamaToolbar2)
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
					.setBackground(GamaColors.get(XtextGui.EDITOR_BACKGROUND_COLOR.getValue()).color());

			final Iterator e = ((CompositeRuler) getVerticalRuler()).getDecoratorIterator();
			while (e.hasNext()) {
				final IVerticalRulerColumn column = (IVerticalRulerColumn) e.next();
				column.getControl().setBackground(GamaColors.get(XtextGui.EDITOR_BACKGROUND_COLOR.getValue()).color());
				column.redraw();
			}
			// this.getVerticalRuler().getControl().redraw();
			// ((SourceViewer)
			// this.getInternalSourceViewer()).getControl().setBackground(
			// GamaColors.get(EDITOR_BACKGROUND_COLOR.getValue()).color());

		}
	}

	// @Override
	// public void setFocus() {
	// getStyledText().setFocus();
	// }
	// //
	// @Override
	// public void setToogle(final Action toggle) {}

}
