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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.swt;

import msi.gama.common.util.GuiUtils;
import msi.gama.runtime.GAMA;
import org.eclipse.ui.*;

public class ActionWiper implements IStartup, IPerspectiveListener, IPartListener {

	// IContextActivation simulationContext;

	private static final String[] ACTIONS_2_WIPE = new String[] {
		"org.eclipse.ui.edit.text.actionSet.presentation",
		"org.eclipse.jdt.ui.edit.text.java.toggleMarkOccurrences",
		"org.eclipse.cdt.ui.text.c.actionSet.presentation",
		"org.eclipse.jdt.ui.text.java.actionSet.presentation",
		"org.eclipse.ui.externaltools.ExternalToolsSet", "org.eclipse.update.ui.softwareUpdates" };

	@Override
	public void partActivated(final IWorkbenchPart part) {
		// Possibility to track parts, here ?
		// GuiUtils.debug("Part activated: " + part.getId()) ?
		if ( !(part instanceof IEditorPart) ) { return; }
		GuiUtils.openModelingPerspective();
	}

	@Override
	public void earlyStartup() {
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for ( int i = 0; i < windows.length; i++ ) {
			IWorkbenchPage page = windows[i].getActivePage();
			if ( page != null ) {
				wipeActions(page);
				page.addPartListener(new ActionWiper());
			}
			windows[i].addPerspectiveListener(this);
		}
	}

	private void wipeActions(final IWorkbenchPage page) {
		for ( int i = 0; i < ACTIONS_2_WIPE.length; i++ ) {
			wipeAction(page, ACTIONS_2_WIPE[i]);
		}

	}

	private void wipeAction(final IWorkbenchPage page, final String actionsetId) {
		GuiUtils.run(new Runnable() {

			@Override
			public void run() {
				page.hideActionSet(actionsetId);
			}
		});

	}

	@Override
	public void perspectiveActivated(final IWorkbenchPage page,
		final IPerspectiveDescriptor perspective) {
		wipeActions(page);
		// if ( perspective.getId().equals(SimulationPerspective.ID) ) {
		// simulationContext =
		// ((IContextService) PlatformUI.getWorkbench().getService(IContextService.class))
		// .activateContext("msi.gama.application.simulation.context");
		// } else {
		// if ( simulationContext != null ) {
		// simulationContext.getContextService().deactivateContext(simulationContext);
		// }
		if ( GAMA.getFrontmostSimulation() == null ) {
			GuiUtils.informStatus("No simulation");
		}
		// }

	}

	@Override
	public void perspectiveChanged(final IWorkbenchPage page,
		final IPerspectiveDescriptor perspective, final String changeId) {

	}

	@Override
	public void partBroughtToTop(final IWorkbenchPart part) {
		// nothing to do
	}

	@Override
	public void partClosed(final IWorkbenchPart part) {
		// nothing to do
	}

	@Override
	public void partDeactivated(final IWorkbenchPart part) {
		// nothing to do
	}

	@Override
	public void partOpened(final IWorkbenchPart part) {
		// nothing to do
	}

}
