/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.application.perspectives;

import msi.gama.gui.application.GUI;
import msi.gama.kernel.GAMA;
import org.eclipse.ui.*;

public class ActionWiper implements IStartup, IPerspectiveListener {

	// IContextActivation simulationContext;

	private static final String[]	ACTIONS_2_WIPE	= new String[] {
		"org.eclipse.ui.externaltools.ExternalToolsSet", "org.eclipse.update.ui.softwareUpdates" };

	@Override
	public void earlyStartup() {
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for ( int i = 0; i < windows.length; i++ ) {
			IWorkbenchPage page = windows[i].getActivePage();
			if ( page != null ) {
				wipeActions(page);
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
		GUI.run(new Runnable() {

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
		// .activateContext("msi.gama.gui.application.simulation.context");
		// } else {
		// if ( simulationContext != null ) {
		// simulationContext.getContextService().deactivateContext(simulationContext);
		// }
		if ( GAMA.getFrontmostSimulation() == null ) {
			GUI.informStatus("No simulation");
		}
		// }

	}

	@Override
	public void perspectiveChanged(final IWorkbenchPage page,
		final IPerspectiveDescriptor perspective, final String changeId) {

	}

}
