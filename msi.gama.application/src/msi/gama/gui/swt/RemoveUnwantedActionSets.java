/*********************************************************************************************
 *
 *
 * 'ActionWiper.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.swt;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.*;
import org.eclipse.ui.internal.WorkbenchWindow;
import msi.gama.runtime.GAMA;

public class RemoveUnwantedActionSets extends PerspectiveAdapter /* implements IStartup */ {

	String[] TOOLBAR_ACTION_SETS_TO_REMOVE = new String[] { "org.eclipse", "msi.gama.lang.gaml.Gaml" };
	String[] MENUS_TO_REMOVE = new String[] { "org.eclipse.ui.run", "window", "navigate", "project" };

	public static void run() {
		RemoveUnwantedActionSets remove = new RemoveUnwantedActionSets();
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for ( int i = 0; i < windows.length; i++ ) {
			IWorkbenchPage page = windows[i].getActivePage();
			if ( page != null ) {
				// Doing the initial cleanup on the default perspective (modeling)
				remove.perspectiveActivated(page, null);
			}
			windows[i].addPerspectiveListener(remove);
		}
	}

	@Override
	public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
		final WorkbenchWindow w = (WorkbenchWindow) page.getWorkbenchWindow();
		GAMA.getGui().run(new Runnable() {

			@Override
			public void run() {
				// RearrangeMenus.run();
				IContributionItem[] items = w.getCoolBarManager2().getItems();
				// We remove all contributions to the toolbar that do not relate to gama
				for ( IContributionItem item : items ) {

					for ( String s : TOOLBAR_ACTION_SETS_TO_REMOVE ) {
						if ( item.getId().contains(s) ) {
							System.out.println("Removed perspective contribution to toolbar:" + item.getId());
							w.getCoolBarManager2().remove(item);
						}
					}
				}

				for ( String s : MENUS_TO_REMOVE ) {
					w.getMenuBarManager().remove(s);
					w.getMenuManager().remove(s);
				}
				// Update the tool and menu bars
				w.getCoolBarManager2().update(true);
				w.getMenuManager().update(true);
				w.getMenuBarManager().update(true);
			}

		});
	}

	@Override
	public void perspectiveChanged(final IWorkbenchPage p, final IPerspectiveDescriptor d, final String c) {
		if ( c.equals(IWorkbenchPage.CHANGE_RESET_COMPLETE) ) {
			perspectiveActivated(p, d);
		}
	}

}
