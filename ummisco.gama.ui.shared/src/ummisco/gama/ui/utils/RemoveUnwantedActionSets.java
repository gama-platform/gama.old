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
package ummisco.gama.ui.utils;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;

public class RemoveUnwantedActionSets
		extends PerspectiveAdapter /* implements IStartup */ {

	String[] TOOLBAR_ACTION_SETS_TO_REMOVE = new String[] { "org.eclipse", "msi.gama.lang.gaml.Gaml" };
	String[] MENUS_TO_REMOVE = new String[] { "org.eclipse.ui.run", "window", "navigate", "project" };

	public static void run() {
		final RemoveUnwantedActionSets remove = new RemoveUnwantedActionSets();
		final IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (int i = 0; i < windows.length; i++) {
			final IWorkbenchPage page = windows[i].getActivePage();
			if (page != null) {
				// Doing the initial cleanup on the default perspective
				// (modeling)
				remove.perspectiveActivated(page, null);
			}
			windows[i].addPerspectiveListener(remove);
		}
	}

	@Override
	public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
		final WorkbenchWindow w = (WorkbenchWindow) page.getWorkbenchWindow();
		Display.getCurrent().syncExec(new Runnable() {

			@Override
			public void run() {
				// RearrangeMenus.run();
				final IContributionItem[] items = w.getCoolBarManager2().getItems();
				// We remove all contributions to the toolbar that do not relate
				// to gama
				for (final IContributionItem item : items) {

					for (final String s : TOOLBAR_ACTION_SETS_TO_REMOVE) {
						if (item.getId().contains(s)) {
							// System.out.println("Removed perspective
							// contribution to toolbar:" + item.getId());
							try {
								if (w.getCoolBarManager2().find(item.getId()) != null)
									w.getCoolBarManager2().remove(item);
							} catch (final Exception e) {
							}
						}
					}
				}

				for (final String s : MENUS_TO_REMOVE) {
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
		if (c.equals(IWorkbenchPage.CHANGE_RESET_COMPLETE)) {
			perspectiveActivated(p, d);
		}
	}

}
