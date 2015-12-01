package msi.gama.lang.gaml.ui;

import java.util.*;
import java.util.List;
import msi.gama.common.*;
import msi.gama.common.GamaPreferences.IPreferenceChangeListener;
import msi.gama.gui.swt.*;
import msi.gama.gui.swt.controls.GamaToolbar2;
import msi.gama.gui.views.IToolbarDecoratedView;
import msi.gama.gui.views.actions.GamaToolbarFactory;
import msi.gama.lang.gaml.ui.decorators.GamlMarkerImageProvider;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.Command;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.*;
import org.eclipse.ui.views.markers.internal.*;

public class SyntaxErrorsView extends ProblemView implements IToolbarDecoratedView {

	// Syntax Error View Actions (see IGamaViewActions)
	public final static int CLEAN = 32;
	public final static int AUTO = 33;
	public final static int BUILD = 34;
	public final static int FILTER = 35;
	public final static int WARNINGS = 36;
	public final static int INFOS = 37;

	protected Composite parent;
	protected GamaToolbar2 toolbar;
	protected IAction filterAction;

	class ErrorField extends FieldSeverityAndMessage {

		@Override
		public Image getImage(final Object obj) {
			if ( obj == null || !(obj instanceof MarkerNode) ) { return null; }
			MarkerNode node = (MarkerNode) obj;
			if ( node.isConcrete() ) {
				if ( node instanceof ProblemMarker ) {
					return GamlMarkerImageProvider.getImage(((ProblemMarker) obj).getSeverity()).image();
				} else {
					return null;
				}
			} else {
				GamaIcon icon = GamlMarkerImageProvider.getImage(node.getDescription());
				return icon == null ? null : icon.image();
			}
		}

		@Override
		public Image getDescriptionImage() {
			return super.getDescriptionImage();
		}

	}

	class LineNumberField extends FieldLineNumber {

		@Override
		public String getColumnHeaderText() {
			return "Line ";
		}

		@Override
		public String getValue(final Object obj) {
			String s = super.getValue(obj);
			if ( s == null || s.isEmpty() ) { return s; }
			String[] segments = StringUtils.split(s);
			if ( segments.length < 2 ) { return StringUtils.EMPTY; }
			return segments[1];
		}

		@Override
		public int getPreferredWidth() {
			return 30;
		}

	}

	ToolItem warningAction, infoAction;
	final BuildPreferenceChangeListener listener;
	final ErrorField errorField = new ErrorField();
	final LineNumberField lineField = new LineNumberField();

	public SyntaxErrorsView() {
		listener = new BuildPreferenceChangeListener(this);
		GamaPreferences.WARNINGS_ENABLED.addChangeListener(listener);
		GamaPreferences.INFO_ENABLED.addChangeListener(listener);
	}

	@Override
	public void createPartControl(final Composite compo) {
		this.parent = GamaToolbarFactory.createToolbars(this, compo);
		super.createPartControl(parent);
	}

	@Override
	protected void initToolBar(final IToolBarManager tbm) {
		// Just to gather the action contributed before
		super.initToolBar(tbm);
		IContributionItem[] items = tbm.getItems();
		for ( IContributionItem item : items ) {
			if ( item instanceof ActionContributionItem ) {
				ActionContributionItem actionItem = (ActionContributionItem) item;
				IAction action = actionItem.getAction();
				if ( action instanceof GamaToolbarFactory.ToggleAction ) {
					continue;
				}
				filterAction = actionItem.getAction();
				tbm.remove(actionItem);
			}
		}
		// tbm.removeAll();
	}

	@Override
	protected String getMarkerName() {
		return super.getMarkerName();
	}

	@Override
	public void refreshViewer() {
		super.refreshViewer();
	}

	@Override
	protected void initMenu(final IMenuManager menu) {}

	@Override
	public IField[] getAllFields() {
		// super: return new IField[] { severityAndMessage, resource, folder, lineNumber,
		// creationTime };
		IField[] fields = super.getAllFields();
		IField[] newFields = new IField[4];
		newFields[0] = errorField;
		newFields[1] = fields[1];
		newFields[2] = lineField;
		newFields[3] = fields[2];
		return newFields;
	}

	@Override
	public IField[] getSortingFields() {
		// super: return new IField[] { severityAndMessage, resource, folder, lineNumber,
		// creationTime };
		IField[] fields = super.getSortingFields();
		IField[] newFields = new IField[5];
		newFields[0] = errorField;
		newFields[1] = fields[1];
		newFields[2] = lineField;
		newFields[3] = fields[2];
		newFields[4] = fields[5];
		return newFields;
	}

	@Override
	protected Tree createTree(final Composite parent) {
		Tree tree = super.createTree(parent);
		tree.setLinesVisible(false);
		tree.setHeaderVisible(false);
		return tree;
	}

	@Override
	protected void createColumns(final Tree tree) {
		super.createColumns(tree);
	}

	@Override
	protected TreeViewer getViewer() {
		return super.getViewer();
	}

	@Override
	protected Tree getTree() {
		return super.getTree();
	}

	@Override
	public void dispose() {
		super.dispose();
		GamaPreferences.WARNINGS_ENABLED.removeChangeListener(listener);
		GamaPreferences.INFO_ENABLED.removeChangeListener(listener);
	}

	public static class BuildPreferenceChangeListener implements IPreferenceChangeListener<Boolean> {

		SyntaxErrorsView view;

		BuildPreferenceChangeListener(final SyntaxErrorsView v) {
			view = v;
		}

		/**
		 * @see msi.gama.common.GamaPreferences.IPreferenceChangeListener#beforeValueChange(java.lang.Object)
		 */
		@Override
		public boolean beforeValueChange(final Boolean newValue) {
			return true;
		}

		/**
		 * @see msi.gama.common.GamaPreferences.IPreferenceChangeListener#afterValueChange(java.lang.Object)
		 */
		@Override
		public void afterValueChange(final Boolean newValue) {

			try {
				ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.CLEAN_BUILD, null);
				view.checkActions();
			} catch (CoreException e) {}

		}
	}

	void checkActions() {
		if ( warningAction != null ) {
			warningAction.setSelection(GamaPreferences.WARNINGS_ENABLED.getValue());
		}
		if ( infoAction != null ) {
			infoAction.setSelection(GamaPreferences.INFO_ENABLED.getValue());
		}
	}

	@Override
	public Object getAdapter(final Class adaptable) {

		if ( adaptable.equals(IShowInSource.class) ) { return new IShowInSource() {

			@Override
			public ShowInContext getShowInContext() {
				ISelection selection = getViewer().getSelection();
				if ( !(selection instanceof IStructuredSelection) ) { return null; }
				IStructuredSelection structured = (IStructuredSelection) selection;
				Iterator markerIterator = structured.iterator();
				List newSelection = new ArrayList();
				while (markerIterator.hasNext()) {
					Object temp = markerIterator.next();
					if ( temp instanceof ConcreteMarker ) {
						ConcreteMarker element = (ConcreteMarker) temp;
						newSelection.add(element.getResource());
					}
				}
				return new ShowInContext(getViewer().getInput(), new StructuredSelection(newSelection));
			}

		}; }
		return super.getAdapter(adaptable);
	}

	@Override
	protected void setContentDescription(final String description) {
		toolbar.status((Image) null, description, IGamaColors.BLUE, SWT.LEFT);
	}

	@Override
	public void createToolItems(final GamaToolbar2 tb) {
		this.toolbar = tb;
		// { FILTER, WARNINGS, INFOS, SEP, CLEAN, BUILD, AUTO };
		tb.button("build.sort2", "", "Configure filters", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				filterAction.run();
			}

		}, SWT.RIGHT);

		warningAction = tb.check("build.warnings2", "", "Toggle display of warning markers", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				boolean b = ((ToolItem) e.widget).getSelection();
				GamaPreferences.WARNINGS_ENABLED.set(b).save();
			}
		}, SWT.RIGHT);
		warningAction.setSelection(GamaPreferences.WARNINGS_ENABLED.getValue());

		infoAction = tb.check("build.infos2", "", "Toggle display of information markers", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				boolean b = ((ToolItem) e.widget).getSelection();
				GamaPreferences.INFO_ENABLED.set(b).save();
			}
		}, SWT.RIGHT);
		infoAction.setSelection(GamaPreferences.INFO_ENABLED.getValue());

		tb.sep(GamaToolbarFactory.TOOLBAR_SEP, SWT.RIGHT);
		tb.button("build.all2", "", "Clean and validate all projects", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				try {
					workspace.build(IncrementalProjectBuilder.CLEAN_BUILD, null);
				} catch (CoreException ex) {
					ex.printStackTrace();
				}

			}

		}, SWT.RIGHT);

		tb.button("build.project2", "", "Validate the current project", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try {
					ICommandService service = (ICommandService) getSite().getService(ICommandService.class);
					Command c = service.getCommand("msi.gama.lang.gaml.Gaml.validate");
					if ( c.isEnabled() ) {
						IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);
						handlerService.executeCommand("msi.gama.lang.gaml.Gaml.validate", null);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

		}, SWT.RIGHT);

		tb.check("build.auto2", "", "Automatically validate models", new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IWorkspaceDescription description = workspace.getDescription();
				description.setAutoBuilding(((ToolItem) e.widget).getSelection());
				try {
					workspace.setDescription(description);
				} catch (CoreException ex) {
					// ErrorDialog.openError(Swt.getShell(), null, null, e.getStatus());
				}

			}
		}, SWT.RIGHT).setSelection(true);

	}

}
