package ummisco.gaml.editbox.actions;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

import ummisco.gaml.editbox.EditBox;

public class EditBoxStartup implements IStartup {

	public void earlyStartup() {
		if (!EditBox.getDefault().isEnabled())
			return;

		EditBox.getDefault().setEnabled(false);

		final IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getDisplay().asyncExec(new Runnable() {

			public void run() {
				ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
				Command command = commandService.getCommand(EnableEditBox.COMMAND_ID);
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window != null) {
					IHandlerService handlerService = (IHandlerService) window.getWorkbench().getService(IHandlerService.class);
					if (handlerService != null)
						try {
							handlerService.executeCommand(new ParameterizedCommand(command, null), null);
							toggle(window);
						} catch (Exception e) {
							EditBox.logError(this, "Failed to enable EditBox at startup", e);
						}
				}
			}

		});
	}

	protected void toggle(IWorkbenchWindow window) {
		// any better way to toggle toolbar button in 3.2?
		if (window instanceof ApplicationWindow) {
			CoolBarManager coolBarManager = ((ApplicationWindow) window).getCoolBarManager();
			if (coolBarManager != null) {
				IContributionItem item = coolBarManager.find("ummisco.gaml.editbox.ActionSetId");
				if (item instanceof ToolBarContributionItem) {
					IToolBarManager tbMgr2 = ((ToolBarContributionItem) item).getToolBarManager();
					if (tbMgr2 != null) {
						IContributionItem item2 = tbMgr2.find("ummisco.gaml.editbox.EnableEditboxActionId");
						if (item2 instanceof ActionContributionItem) {
							((ActionContributionItem) item2).getAction().setChecked(true);
						}
					}
				}
			}
		}
	}

}
