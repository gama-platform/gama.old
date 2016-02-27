/*********************************************************************************************
 *
 *
 * 'DisplaySurfaceMenu.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.gui.displays.awt;

import java.util.*;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import msi.gama.common.interfaces.*;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.views.LayeredDisplayView;
import msi.gama.gui.views.actions.DisplayedAgentsMenu;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.GAMA;

public class DisplaySurfaceMenu {

	private final IDisplaySurface surface;
	private final Control swtControl;
	private final DisplayedAgentsMenu menuBuilder;
	private final LayeredDisplayView view;

	public DisplaySurfaceMenu(final IDisplaySurface s, final Control c, final LayeredDisplayView view) {
		surface = s;
		this.view = view;
		swtControl = c;
		menuBuilder = new DisplayedAgentsMenu();
		s.setSWTMenuManager(this);

	}

	org.eclipse.swt.widgets.Menu menu;

	public void buildMenu(final int mousex, final int mousey, final int x, final int y,
		final ILocation modelCoordinates, final List<ILayer> displays) {
		if ( displays.isEmpty() ) { return; }
		if ( menu != null && !menu.isDisposed() ) {
			menu.dispose();
		}
		Set<IAgent> all = new LinkedHashSet();
		for ( final ILayer display : displays ) {
			if ( display.isSelectable() ) {
				final Set<IAgent> agents = display.collectAgentsAt(x, y, surface);
				if ( agents.isEmpty() ) {
					continue;
				}
				all.addAll(agents);
			}
		}
		buildMenu(true, mousex, mousey, modelCoordinates, all);
	}

	public void buildMenu(final int mousex, final int mousey, final IAgent agent) {
		GamaPoint modelCoordinates = agent == null ? null : (GamaPoint) agent.getLocation();
		buildMenu(false, mousex, mousey, modelCoordinates,
			agent == null ? Collections.EMPTY_LIST : Collections.singleton(agent));
	}

	public void buildMenu(final boolean byLayer, final int mousex, final int mousey, final ILocation modelCoordinates,
		final Collection<IAgent> agents) {
		GAMA.getGui().asyncRun(new Runnable() {

			@Override
			public void run() {
				if ( menu != null && !menu.isDisposed() ) {
					menu.dispose();
				}
				menu = menuBuilder.getMenu(view.getDisplaySurface(), swtControl, true, byLayer, agents,
					modelCoordinates, view.isOpenGL());
				menu.setData(IKeyword.USER_LOCATION, modelCoordinates);
				menu.setLocation(swtControl.toDisplay(mousex, mousey));
				menu.setVisible(true);
				// AD 3/10/13: Fix for Issue 669 on Linux GTK setup. See :
				// http://www.eclipse.org/forums/index.php/t/208284/
				retryVisible(menu, MAX_RETRIES);
			}
		});
	}

	static int MAX_RETRIES = 10;

	private void retryVisible(final Menu menu, final int retriesRemaining) {
		GAMA.getGui().asyncRun(new Runnable() {

			@Override
			public void run() {
				if ( !menu.isVisible() && retriesRemaining > 0 ) {
					menu.setVisible(false);
					{
						Shell shell = new Shell(SwtGui.getDisplay(), SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
						shell.setSize(10, 10); // big enough to avoid errors from the gtk layer
						shell.setLocation(menu.getShell().getLocation());
						shell.setText("Not visible");
						shell.setVisible(false);
						shell.open();
						shell.dispose();
					}
					menu.getShell().forceActive();
					menu.setVisible(true);
					retryVisible(menu, retriesRemaining - 1);
				}
			}
		});
	}

}
