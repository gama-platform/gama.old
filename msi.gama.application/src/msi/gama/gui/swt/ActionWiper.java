/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.swt;

import msi.gama.common.util.GuiUtils;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.*;
import org.eclipse.ui.internal.WorkbenchWindow;

public class ActionWiper extends PerspectiveAdapter implements IStartup/* , IPartListener */{

	// private static final String[] ACTIONS_2_WIPE = new String[] { "org.eclipse.ui.edit.text.actionSet.presentation",
	// "org.eclipse.jdt.ui.edit.text.java.toggleMarkOccurrences", "org.eclipse.cdt.ui.text.c.actionSet.presentation",
	// "org.eclipse.jdt.ui.text.java.actionSet.presentation", "org.eclipse.ui.externaltools.ExternalToolsSet",
	// "org.eclipse.ui.workbench.navigate", "org.eclipse.ui.edit.text.actionSet.annotationNavigation"
	// // "org.eclipse.update.ui.softwareUpdates"
	// };

	// @Override
	// public void partActivated(final IWorkbenchPart part) {
	// // if ( !(part instanceof IEditorPart) ) { return; }
	// // GuiUtils.openModelingPerspective();
	// }
	//
	@Override
	public void earlyStartup() {
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for ( int i = 0; i < windows.length; i++ ) {
			IWorkbenchPage page = windows[i].getActivePage();
			if ( page != null ) {
				// // wipeActions(page);
				// // ActionWiper aw = new ActionWiper();
				// // page.addPartListener(aw);
				// }
				// Doing the initial cleanup on the default perspective (modeling)
				this.perspectiveActivated(page, null);
			}
			windows[i].addPerspectiveListener(this);
		}
	}

	// private void wipeActions(final IWorkbenchPage page) {
	// for ( int i = 0; i < ACTIONS_2_WIPE.length; i++ ) {
	// wipeAction(page, ACTIONS_2_WIPE[i]);
	// }
	// }
	//
	// private void wipeAction(final IWorkbenchPage page, final String actionsetId) {
	// }

	// static String[] ItemsToHide = { "org.eclipse.ui.workbench.file", "org.eclipse.debug.ui.launchActionSet",
	// "org.eclipse.search.searchActionSet", "org.eclipse.ui.edit.text.actionSet.presentation",
	// "org.eclipse.ui.workbench.navigate", "org.eclipse.ui.workbench.help", "org.eclipse.ui.DefaultTextEditor",
	// "msi.gama.lang.gaml.Gaml" };
	//
	// static String[] MenusToHide = { "org.eclipse.ui.run" };

	@Override
	public void perspectiveActivated(final IWorkbenchPage page, final IPerspectiveDescriptor perspective) {
		final WorkbenchWindow w = (WorkbenchWindow) page.getWorkbenchWindow();
		GuiUtils.run(new Runnable() {

			@Override
			public void run() {
				IContributionItem[] items = w.getCoolBarManager2().getItems();
				// We remove all contributions to the toolbar that do not relate to gama
				for ( IContributionItem item : items ) {
					if ( !item.getId().contains("gama") ) {
						w.getCoolBarManager2().remove(item);
					}
				}
				// Special case for the contribution of XText
				w.getCoolBarManager2().remove("msi.gama.lang.gaml.Gaml");
				// Special case for the Run menu
				w.getMenuBarManager().remove("org.eclipse.ui.run");
				w.getMenuManager().remove("org.eclipse.ui.run");
				// Update the tool and menu bars
				w.getCoolBarManager2().update(true);
				w.getMenuManager().update(true);
				w.getMenuBarManager().update(true);
			}

		});

		// for ( IContributionItem item : w.getMenuManager().getItems() ) {
		// GuiUtils.debug("ActionWiper.perspectiveActivated + showing item in MENUMANAGER " +
		// item.getClass().getSimpleName() + " id= " + item.getId());
		// }
	}

	@Override
	public void perspectiveChanged(final IWorkbenchPage p, final IPerspectiveDescriptor d, final String c) {
		if ( c.equals(IWorkbenchPage.CHANGE_RESET_COMPLETE) ) {
			//GuiUtils.debug("ActionWiper.perspectiveChanged : Complete");
			perspectiveActivated(p, d);
		}
	}

	// @Override
	// public void partBroughtToTop(final IWorkbenchPart part) {}
	//
	// @Override
	// public void partClosed(final IWorkbenchPart part) {}
	//
	// @Override
	// public void partDeactivated(final IWorkbenchPart part) {}
	//
	// @Override
	// public void partOpened(final IWorkbenchPart part) {}

}
