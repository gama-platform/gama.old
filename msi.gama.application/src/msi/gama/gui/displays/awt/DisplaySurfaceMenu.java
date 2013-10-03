package msi.gama.gui.displays.awt;

import java.util.*;
import java.util.List;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.views.LayeredDisplayView;
import msi.gama.gui.views.actions.DisplayedAgentsMenu;
import msi.gama.metamodel.agent.IAgent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.*;

public class DisplaySurfaceMenu {

	private final IDisplaySurface surface;
	private final Control swtControl;
	private final DisplayedAgentsMenu menuBuilder;

	public DisplaySurfaceMenu(final IDisplaySurface s, final Control c, final LayeredDisplayView view) {
		surface = s;
		swtControl = c;
		c.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(final org.eclipse.swt.events.MouseEvent e) {
				GuiUtils.debug("Mouse up for SWT control");

			}

			@Override
			public void mouseDown(final org.eclipse.swt.events.MouseEvent e) {
				GuiUtils.debug("Mouse down for SWT control");

			}

			@Override
			public void mouseDoubleClick(final org.eclipse.swt.events.MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
		menuBuilder = new DisplayedAgentsMenu(view);
		((AbstractAWTDisplaySurface) s).setSWTMenuManager(this);

	}

	org.eclipse.swt.widgets.Menu menu;

	public void buildMenu(final int mousex, final int mousey, final int x, final int y, final List<ILayer> displays) {
		if ( displays.isEmpty() ) { return; }
		if ( menu != null && !menu.isDisposed() ) {
			menu.dispose();
		}
		Set<IAgent> all = new LinkedHashSet();
		// GamaPoint p = displays.get(0).getModelCoordinatesFrom(x, y, surface);
		// all.add(GAMA.getSimulation());
		for ( final ILayer display : displays ) {
			final Set<IAgent> agents = display.collectAgentsAt(x, y, surface);
			if ( agents.isEmpty() ) {
				continue;
			}
			// TODO How to pass the coordinates ??
			// p = display.getModelCoordinatesFrom(x, y, surface);
			all.addAll(agents);
		}
		buildMenu(true, mousex, mousey, all);
	}

	public void buildMenu(final int mousex, final int mousey, final IAgent agent) {
		buildMenu(false, mousex, mousey, agent == null ? Collections.EMPTY_LIST : Collections.singleton(agent));
	}

	public void buildMenu(final boolean byLayer, final int mousex, final int mousey, final Collection<IAgent> agents) {
		GuiUtils.asyncRun(new Runnable() {

			@Override
			public void run() {
				if ( menu != null && !menu.isDisposed() ) {
					menu.dispose();
				}
				menu = menuBuilder.getMenu(swtControl, true, byLayer, agents);
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
		GuiUtils.asyncRun(new Runnable() {

			@Override
			public void run() {
				if ( !menu.isVisible() && retriesRemaining > 0 ) {
					menu.setVisible(false);
					{
						Shell shell = new Shell(SwtGui.getDisplay(), SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
						shell.setSize(10, 10); // big enough to avoid errors from the gtk layer
						shell.setLocation(menu.getShell().getLocation());
						// shell.setBackground(SwtGui.getDisplay().getSystemColor(SWT.COLOR_RED));
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
