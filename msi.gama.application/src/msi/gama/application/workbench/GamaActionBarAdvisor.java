/*******************************************************************************************************
 *
 * GamaActionBarAdvisor.java, in msi.gama.application, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.application.workbench;

import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.Util;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.actions.BaseNewWizardMenu;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.internal.IWorkbenchHelpContextIds;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.handlers.IActionCommandMappingService;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

/**
 * The Class GamaActionBarAdvisor.
 */
public class GamaActionBarAdvisor extends ActionBarAdvisor {

	/** The window. */
	final IWorkbenchWindow window;

	// generic actions

	/** The hide show editor action. */
	private IWorkbenchAction hideShowEditorAction;

	/** The close action. */
	private IWorkbenchAction closeAction;

	/** The close all action. */
	private IWorkbenchAction closeAllAction;

	/** The save action. */
	private IWorkbenchAction saveAction;

	/** The save all action. */
	private IWorkbenchAction saveAllAction;

	/** The help contents action. */
	private IWorkbenchAction helpContentsAction;

	/** The about action. */
	private IWorkbenchAction aboutAction;

	/** The open preferences action. */
	private IWorkbenchAction openPreferencesAction;

	/** The save as action. */
	private IWorkbenchAction saveAsAction;

	/** The backward history action. */
	private IWorkbenchAction backwardHistoryAction;

	/** The forward history action. */
	private IWorkbenchAction forwardHistoryAction;

	/** The undo action. */
	// generic retarget actions
	private IWorkbenchAction undoAction;

	/** The redo action. */
	private IWorkbenchAction redoAction;

	/** The quit action. */
	private IWorkbenchAction quitAction;

	/** The open workspace action. */
	// IDE-specific actions
	private IWorkbenchAction openWorkspaceAction;

	/** The project property dialog action. */
	private IWorkbenchAction projectPropertyDialogAction;

	/** The new wizard menu. */
	private BaseNewWizardMenu newWizardMenu;

	/** The status line item. */
	// @issue class is workbench internal
	private StatusLineContributionItem statusLineItem;

	/**
	 * Indicates if the action builder has been disposed
	 */
	private boolean isDisposed = false;

	/** The icons. */
	final IIconProvider icons;

	/**
	 * Constructs a new action builder which contributes actions to the given window.
	 *
	 * @param configurer
	 *            the action bar configurer for the window
	 */
	public GamaActionBarAdvisor(final IActionBarConfigurer configurer) {
		super(configurer);
		window = configurer.getWindowConfigurer().getWindow();
		icons = window.getService(IIconProvider.class);
	}

	/**
	 * Returns the window to which this action builder is contributing.
	 */
	private IWorkbenchWindow getWindow() { return window; }

	/**
	 * Fills the menu bar with the workbench actions.
	 */
	@Override
	protected void fillMenuBar(final IMenuManager menuBar) {
		menuBar.add(createFileMenu());
		menuBar.add(createEditMenu());
		menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		menuBar.add(createHelpMenu());
	}

	/**
	 * Creates and returns the File menu.
	 */
	private MenuManager createFileMenu() {
		final MenuManager menu = new MenuManager(IDEWorkbenchMessages.Workbench_file, IWorkbenchActionConstants.M_FILE);
		menu.add(new GroupMarker(IWorkbenchActionConstants.FILE_START));
		{
			// create the New submenu, using the same id for it as the New action
			final String newText = IDEWorkbenchMessages.Workbench_new;
			final String newId = ActionFactory.NEW.getId();
			final MenuManager newMenu = new MenuManager(newText, newId);
			newMenu.setActionDefinitionId("org.eclipse.ui.file.newQuickMenu"); //$NON-NLS-1$
			newMenu.setImageDescriptor(icons.desc("navigator/navigator.new2"));
			newMenu.add(new Separator(newId));
			this.newWizardMenu = new BaseNewWizardMenu(getWindow(), null) {

				@Override
				protected void addItems(final List<IContributionItem> list) {
					addShortcuts(list);
				}
			};
			newMenu.add(this.newWizardMenu);
			newMenu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			menu.add(newMenu);
		}

		menu.add(new GroupMarker(IWorkbenchActionConstants.NEW_EXT));
		menu.add(new Separator());

		menu.add(closeAction);
		menu.add(closeAllAction);
		menu.add(new GroupMarker(IWorkbenchActionConstants.CLOSE_EXT));
		menu.add(new Separator());
		menu.add(saveAction);
		menu.add(saveAsAction);
		menu.add(saveAllAction);
		// menu.add(getRevertItem());
		menu.add(new Separator());
		menu.add(getMoveItem());
		menu.add(getRenameItem());
		menu.add(getRefreshItem());

		menu.add(new GroupMarker(IWorkbenchActionConstants.SAVE_EXT));
		menu.add(new Separator());
		menu.add(getPrintItem());
		menu.add(new GroupMarker(IWorkbenchActionConstants.PRINT_EXT));
		menu.add(new Separator());
		menu.add(openWorkspaceAction);
		menu.add(new GroupMarker(IWorkbenchActionConstants.OPEN_EXT));
		menu.add(new GroupMarker(IWorkbenchActionConstants.IMPORT_EXT));
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		menu.add(new Separator());
		menu.add(getPropertiesItem());

		menu.add(ContributionItemFactory.REOPEN_EDITORS.create(getWindow()));
		menu.add(new GroupMarker(IWorkbenchActionConstants.MRU));
		menu.add(new Separator());

		// If we're on OS X we shouldn't show this command in the File menu. It
		// should be invisible to the user. However, we should not remove it -
		// the carbon UI code will do a search through our menu structure
		// looking for it when Cmd-Q is invoked (or Quit is chosen from the
		// application menu.
		final ActionContributionItem quitItem = new ActionContributionItem(quitAction);
		quitItem.setVisible(!Util.isMac());
		menu.add(quitItem);
		menu.add(new GroupMarker(IWorkbenchActionConstants.FILE_END));
		return menu;
	}

	/**
	 * Creates and returns the Edit menu.
	 */
	private MenuManager createEditMenu() {
		final MenuManager menu = new MenuManager(IDEWorkbenchMessages.Workbench_edit, IWorkbenchActionConstants.M_EDIT);
		menu.add(new GroupMarker(IWorkbenchActionConstants.EDIT_START));
		menu.add(undoAction);
		menu.add(redoAction);
		menu.add(new GroupMarker(IWorkbenchActionConstants.UNDO_EXT));
		menu.add(new Separator());
		menu.add(getCutItem());
		menu.add(getCopyItem());
		menu.add(getPasteItem());
		menu.add(new GroupMarker(IWorkbenchActionConstants.CUT_EXT));
		menu.add(new Separator());
		menu.add(getDeleteItem());
		menu.add(getSelectAllItem());
		menu.add(new Separator());
		menu.add(getFindItem());
		menu.add(new GroupMarker(IWorkbenchActionConstants.FIND_EXT));
		menu.add(new Separator());
		menu.add(new GroupMarker(IWorkbenchActionConstants.ADD_EXT));
		menu.add(new GroupMarker(IWorkbenchActionConstants.EDIT_END));
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		return menu;
	}

	/**
	 * Creates and returns the Help menu.
	 */
	private MenuManager createHelpMenu() {
		final MenuManager menu = new MenuManager(IDEWorkbenchMessages.Workbench_help, IWorkbenchActionConstants.M_HELP);
		addSeparatorOrGroupMarker(menu, "group.intro"); //$NON-NLS-1$
		menu.add(new GroupMarker("group.intro.ext")); //$NON-NLS-1$
		addSeparatorOrGroupMarker(menu, "group.main"); //$NON-NLS-1$
		menu.add(helpContentsAction);
		addSeparatorOrGroupMarker(menu, "group.assist"); //$NON-NLS-1$
		menu.add(new GroupMarker(IWorkbenchActionConstants.HELP_START));
		menu.add(new GroupMarker("group.main.ext")); //$NON-NLS-1$
		addSeparatorOrGroupMarker(menu, "group.tutorials"); //$NON-NLS-1$
		addSeparatorOrGroupMarker(menu, "group.tools"); //$NON-NLS-1$
		addSeparatorOrGroupMarker(menu, "group.updates"); //$NON-NLS-1$
		menu.add(new GroupMarker(IWorkbenchActionConstants.HELP_END));
		addSeparatorOrGroupMarker(menu, IWorkbenchActionConstants.MB_ADDITIONS);
		// about should always be at the bottom
		menu.add(new Separator("group.about")); //$NON-NLS-1$

		final ActionContributionItem aboutItem = new ActionContributionItem(aboutAction);
		aboutItem.setVisible(!Util.isMac());
		menu.add(aboutItem);
		menu.add(new GroupMarker("group.about.ext")); //$NON-NLS-1$
		menu.add(openPreferencesAction);
		return menu;
	}

	/**
	 * Adds a <code>GroupMarker</code> or <code>Separator</code> to a menu. The test for whether a separator should be
	 * added is done by checking for the existence of a preference matching the string useSeparator.MENUID.GROUPID that
	 * is set to <code>true</code>.
	 *
	 * @param menu
	 *            the menu to add to
	 * @param groupId
	 *            the group id for the added separator or group marker
	 */
	private void addSeparatorOrGroupMarker(final MenuManager menu, final String groupId) {
		final String prefId = "useSeparator." + menu.getId() + "." + groupId; //$NON-NLS-1$ //$NON-NLS-2$
		final boolean addExtraSeparators = IDEWorkbenchPlugin.getDefault().getPreferenceStore().getBoolean(prefId);
		if (addExtraSeparators) {
			menu.add(new Separator(groupId));
		} else {
			menu.add(new GroupMarker(groupId));
		}
	}

	/**
	 * Disposes any resources and unhooks any listeners that are no longer needed. Called when the window is closed.
	 */
	@Override
	public void dispose() {
		if (isDisposed) return;
		isDisposed = true;

		getActionBarConfigurer().getStatusLineManager().remove(statusLineItem);

		// null out actions to make leak debugging easier
		closeAction = null;
		closeAllAction = null;
		saveAction = null;
		saveAllAction = null;
		helpContentsAction = null;
		aboutAction = null;
		openPreferencesAction = null;
		saveAsAction = null;
		backwardHistoryAction = null;
		forwardHistoryAction = null;
		undoAction = null;
		redoAction = null;
		quitAction = null;
		openWorkspaceAction = null;
		projectPropertyDialogAction = null;
		if (newWizardMenu != null) {
			newWizardMenu.dispose();
			newWizardMenu = null;
		}
		statusLineItem = null;
		super.dispose();
	}

	/**
	 * Returns true if the menu with the given ID should be considered as an OLE container menu. Container menus are
	 * preserved in OLE menu merging.
	 */
	@Override
	public boolean isApplicationMenu(final String menuId) {
		if (IWorkbenchActionConstants.M_FILE.equals(menuId) || IWorkbenchActionConstants.M_WINDOW.equals(menuId))
			return true;
		return false;
	}

	/**
	 * Fills the status line with the workbench contribution items.
	 */
	@Override
	protected void fillStatusLine(final IStatusLineManager statusLine) {
		statusLine.add(statusLineItem);
	}

	/**
	 * Creates actions (and contribution items) for the menu bar, toolbar and status line.
	 */
	@Override
	protected void makeActions(final IWorkbenchWindow aWindow) {
		statusLineItem = new StatusLineContributionItem("ModeContributionItem"); //$NON-NLS-1$
		createSaveAction(aWindow);
		createSaveAsAction(aWindow);
		createSaveAllAction(aWindow);
		createUndoAction(aWindow);
		createRedoAction(aWindow);
		createCloseAction(aWindow);
		createCloseAllAction(aWindow);
		createHelpContentsAction(aWindow);
		createAboutAction(aWindow);
		createOpenPreferencesAction(aWindow);
		makeFeatureDependentActions(aWindow);
		createForwardHistoryAction(aWindow);
		createBackwardHistoryAction(aWindow);
		createQuitAction(aWindow);
		createOpenWorkspaceAction(aWindow);
		createPropertyDialogAction(aWindow);
		hideShowEditorAction = ActionFactory.SHOW_EDITOR.create(aWindow);
		register(hideShowEditorAction);
	}

	/**
	 * Creates the property dialog action.
	 *
	 * @param aWindow
	 *            the a window
	 */
	public void createPropertyDialogAction(final IWorkbenchWindow aWindow) {
		projectPropertyDialogAction = IDEActionFactory.OPEN_PROJECT_PROPERTIES.create(aWindow);
		register(projectPropertyDialogAction);
	}

	/**
	 * Creates the open workspace action.
	 *
	 * @param aWindow
	 *            the a window
	 */
	public void createOpenWorkspaceAction(final IWorkbenchWindow aWindow) {
		openWorkspaceAction = IDEActionFactory.OPEN_WORKSPACE.create(aWindow);
		register(openWorkspaceAction);
	}

	/**
	 * Creates the quit action.
	 *
	 * @param aWindow
	 *            the a window
	 */
	public void createQuitAction(final IWorkbenchWindow aWindow) {
		quitAction = ActionFactory.QUIT.create(aWindow);
		register(quitAction);
	}

	/**
	 * Creates the backward history action.
	 *
	 * @param aWindow
	 *            the a window
	 */
	public void createBackwardHistoryAction(final IWorkbenchWindow aWindow) {
		backwardHistoryAction = ActionFactory.BACKWARD_HISTORY.create(aWindow);
		register(backwardHistoryAction);
	}

	/**
	 * Creates the forward history action.
	 *
	 * @param aWindow
	 *            the a window
	 */
	public void createForwardHistoryAction(final IWorkbenchWindow aWindow) {
		forwardHistoryAction = ActionFactory.FORWARD_HISTORY.create(aWindow);
		register(forwardHistoryAction);
	}

	/**
	 * The Class OpenPreferencesAction.
	 */
	private class OpenPreferencesAction extends Action implements IWorkbenchAction {

		/**
		 * Instantiates a new open preferences action.
		 */
		OpenPreferencesAction() {
			super(WorkbenchMessages.OpenPreferences_text);
			setId("preferences");
			setText("Preferences");
			setToolTipText("Open GAMA preferences");
			setImageDescriptor(icons.desc("generic/menu.open.preferences"));
			window.getWorkbench().getHelpSystem().setHelp(this, IWorkbenchHelpContextIds.OPEN_PREFERENCES_ACTION);
		}

		@Override
		public void run() {
			window.getService(IPreferenceHelper.class).openPreferences();
		}

		@Override
		public void dispose() {}

	}

	/**
	 * Creates the open preferences action.
	 *
	 * @param aWindow
	 *            the a window
	 */
	public void createOpenPreferencesAction(final IWorkbenchWindow aWindow) {
		openPreferencesAction = new OpenPreferencesAction();
		register(openPreferencesAction);
	}

	/**
	 * Creates the about action.
	 *
	 * @param aWindow
	 *            the a window
	 */
	public void createAboutAction(final IWorkbenchWindow aWindow) {
		aboutAction = ActionFactory.ABOUT.create(aWindow);
		aboutAction.setImageDescriptor(icons.desc("generic/menu.about"));
		register(aboutAction);
	}

	/**
	 * The Class HelpContentsAction.
	 */
	private class HelpContentsAction extends Action implements IWorkbenchAction {

		/**
		 * Instantiates a new help contents action.
		 */
		HelpContentsAction() {
			setActionDefinitionId(IWorkbenchCommandConstants.HELP_HELP_CONTENTS);
			setId("helpContents");
			setText("GAMA documentation");
			setToolTipText("GAMA online documentation");
			setImageDescriptor(icons.desc("generic/menu.help"));
			window.getWorkbench().getHelpSystem().setHelp(this, IWorkbenchHelpContextIds.HELP_CONTENTS_ACTION);
		}

		@Override
		public void run() {
			window.getService(IWebHelper.class).showPage("http://doc.gama-platform.org");
		}

		@Override
		public void dispose() {}

	}

	/**
	 * Creates the help contents action.
	 *
	 * @param aWindow
	 *            the a window
	 */
	public void createHelpContentsAction(final IWorkbenchWindow aWindow) {
		helpContentsAction = new HelpContentsAction();
		register(helpContentsAction);
	}

	/**
	 * Creates the close all action.
	 *
	 * @param aWindow
	 *            the a window
	 */
	public void createCloseAllAction(final IWorkbenchWindow aWindow) {
		closeAllAction = ActionFactory.CLOSE_ALL.create(aWindow);
		register(closeAllAction);
	}

	/**
	 * Creates the close action.
	 *
	 * @param aWindow
	 *            the a window
	 */
	public void createCloseAction(final IWorkbenchWindow aWindow) {
		closeAction = ActionFactory.CLOSE.create(aWindow);
		register(closeAction);
	}

	/**
	 * Creates the redo action.
	 *
	 * @param aWindow
	 *            the a window
	 */
	public void createRedoAction(final IWorkbenchWindow aWindow) {
		redoAction = ActionFactory.REDO.create(aWindow);
		register(redoAction);
	}

	/**
	 * Creates the save action.
	 *
	 * @param aWindow
	 *            the a window
	 */
	public void createSaveAction(final IWorkbenchWindow aWindow) {
		saveAction = ActionFactory.SAVE.create(aWindow);
		register(saveAction);
	}

	/**
	 * Creates the save as action.
	 *
	 * @param aWindow
	 *            the a window
	 */
	public void createSaveAsAction(final IWorkbenchWindow aWindow) {
		saveAsAction = ActionFactory.SAVE_AS.create(aWindow);
		register(saveAsAction);
	}

	/**
	 * Creates the save all action.
	 *
	 * @param aWindow
	 *            the a window
	 */
	public void createSaveAllAction(final IWorkbenchWindow aWindow) {
		saveAllAction = ActionFactory.SAVE_ALL.create(aWindow);
		register(saveAllAction);
	}

	/**
	 * Creates the undo action.
	 *
	 * @param aWindow
	 *            the a window
	 */
	public void createUndoAction(final IWorkbenchWindow aWindow) {
		undoAction = ActionFactory.UNDO.create(aWindow);
		register(undoAction);
	}

	/**
	 * Creates the feature-dependent actions for the menu bar.
	 */
	private void makeFeatureDependentActions(final IWorkbenchWindow aWindow) {
		// final AboutInfo[] infos = null;

		final IPreferenceStore prefs = IDEWorkbenchPlugin.getDefault().getPreferenceStore();

		// Optimization: avoid obtaining the about infos if the platform state is
		// unchanged from last time. See bug 75130 for details.
		final String stateKey = "platformState"; //$NON-NLS-1$
		final String prevState = prefs.getString(stateKey);
		final String currentState = String.valueOf(Platform.getStateStamp());
		final boolean sameState = currentState.equals(prevState);
		if (!sameState) { prefs.putValue(stateKey, currentState); }
	}

	/**
	 * Gets the cut item.
	 *
	 * @return the cut item
	 */
	private IContributionItem getCutItem() {
		return getItem(ActionFactory.CUT.getId(), ActionFactory.CUT.getCommandId(), "generic/menu.cut", null,
				WorkbenchMessages.Workbench_cut, WorkbenchMessages.Workbench_cutToolTip, null);
	}

	/**
	 * Gets the copy item.
	 *
	 * @return the copy item
	 */
	private IContributionItem getCopyItem() {
		return getItem(ActionFactory.COPY.getId(), ActionFactory.COPY.getCommandId(), "generic/menu.copy", null,
				WorkbenchMessages.Workbench_copy, WorkbenchMessages.Workbench_copyToolTip, null);
	}

	/**
	 * Gets the paste item.
	 *
	 * @return the paste item
	 */
	private IContributionItem getPasteItem() {
		return getItem(ActionFactory.PASTE.getId(), ActionFactory.PASTE.getCommandId(), "generic/menu.paste", null,
				WorkbenchMessages.Workbench_paste, WorkbenchMessages.Workbench_pasteToolTip, null);
	}

	/**
	 * Gets the prints the item.
	 *
	 * @return the prints the item
	 */
	private IContributionItem getPrintItem() {
		return getItem(ActionFactory.PRINT.getId(), ActionFactory.PRINT.getCommandId(), "generic/menu.print", null,
				WorkbenchMessages.Workbench_print, WorkbenchMessages.Workbench_printToolTip, null);
	}

	/**
	 * Gets the select all item.
	 *
	 * @return the select all item
	 */
	private IContributionItem getSelectAllItem() {
		return getItem(ActionFactory.SELECT_ALL.getId(), ActionFactory.SELECT_ALL.getCommandId(),
				"generic/action.selectall", null, WorkbenchMessages.Workbench_selectAll,
				WorkbenchMessages.Workbench_selectAllToolTip, null);
	}

	/**
	 * Gets the find item.
	 *
	 * @return the find item
	 */
	private IContributionItem getFindItem() {
		return getItem(ActionFactory.FIND.getId(), ActionFactory.FIND.getCommandId(), null, null,
				WorkbenchMessages.Workbench_findReplace, WorkbenchMessages.Workbench_findReplaceToolTip, null);
	}

	/**
	 * Gets the delete item.
	 *
	 * @return the delete item
	 */
	private IContributionItem getDeleteItem() {
		return getItem(ActionFactory.DELETE.getId(), ActionFactory.DELETE.getCommandId(), "generic/menu.delete", null,
				WorkbenchMessages.Workbench_delete, WorkbenchMessages.Workbench_deleteToolTip,
				IWorkbenchHelpContextIds.DELETE_RETARGET_ACTION);
	}

	// private IContributionItem getRevertItem() {
	// return getItem(ActionFactory.REVERT.getId(), ActionFactory.REVERT.getCommandId(), null, null,
	// WorkbenchMessages.Workbench_revert, WorkbenchMessages.Workbench_revertToolTip, null);
	// }

	/**
	 * Gets the refresh item.
	 *
	 * @return the refresh item
	 */
	private IContributionItem getRefreshItem() {
		return getItem(ActionFactory.REFRESH.getId(), ActionFactory.REFRESH.getCommandId(), "navigator/file.refresh",
				null, WorkbenchMessages.Workbench_refresh, WorkbenchMessages.Workbench_refreshToolTip, null);
	}

	/**
	 * Gets the properties item.
	 *
	 * @return the properties item
	 */
	private IContributionItem getPropertiesItem() {
		return getItem(ActionFactory.PROPERTIES.getId(), ActionFactory.PROPERTIES.getCommandId(),
				"generic/action.properties", null, WorkbenchMessages.Workbench_properties,
				WorkbenchMessages.Workbench_propertiesToolTip, null);
	}

	/**
	 * Gets the move item.
	 *
	 * @return the move item
	 */
	private IContributionItem getMoveItem() {
		return getItem(ActionFactory.MOVE.getId(), ActionFactory.MOVE.getCommandId(), "navigator/file.move", null,
				WorkbenchMessages.Workbench_move, WorkbenchMessages.Workbench_moveToolTip, null);
	}

	/**
	 * Gets the rename item.
	 *
	 * @return the rename item
	 */
	private IContributionItem getRenameItem() {
		return getItem(ActionFactory.RENAME.getId(), ActionFactory.RENAME.getCommandId(), "navigator/file.rename", null,
				WorkbenchMessages.Workbench_rename, WorkbenchMessages.Workbench_renameToolTip, null);
	}

	/**
	 * Gets the item.
	 *
	 * @param actionId
	 *            the action id
	 * @param commandId
	 *            the command id
	 * @param image
	 *            the image
	 * @param disabledImage
	 *            the disabled image
	 * @param label
	 *            the label
	 * @param tooltip
	 *            the tooltip
	 * @param helpContextId
	 *            the help context id
	 * @return the item
	 */
	private IContributionItem getItem(final String actionId, final String commandId, final String image,
			final String disabledImage, final String label, final String tooltip, final String helpContextId) {

		final IActionCommandMappingService acms = getWindow().getService(IActionCommandMappingService.class);
		acms.map(actionId, commandId);

		final CommandContributionItemParameter commandParm = new CommandContributionItemParameter(getWindow(), actionId,
				commandId, null, image != null ? icons.desc(image) : null, null, null, label, null, tooltip,
				CommandContributionItem.STYLE_PUSH, null, false);
		return new CommandContributionItem(commandParm);
	}
}
