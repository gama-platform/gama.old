package msi.gama.gui.displays.awt;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.views.LayeredDisplayView;
import msi.gama.gui.views.actions.DisplayedAgentsMenu;
import msi.gama.metamodel.agent.IAgent;
import org.eclipse.swt.widgets.Control;

public class DisplaySurfaceMenu {

	private final IDisplaySurface surface;
	private final Control swtControl;
	private final DisplayedAgentsMenu menuBuilder;

	public DisplaySurfaceMenu(final IDisplaySurface s, final Control c, final LayeredDisplayView view) {
		surface = s;
		swtControl = c;
		menuBuilder = new DisplayedAgentsMenu(view);
		((AbstractAWTDisplaySurface) s).setSWTMenuManager(this);

	}

	org.eclipse.swt.widgets.Menu menu;

	public void buildMenu(final int mousex, final int mousey, final int x, final int y,
		final List<ILayer> displays) {
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
		buildMenu(false, mousex, mousey, Collections.singleton(agent));
	}

	public void buildMenu(final boolean byLayer, final int mousex, final int mousey,
		final Collection<IAgent> agents) {
		GuiUtils.run(new Runnable() {

			@Override
			public void run() {
				if ( menu != null && !menu.isDisposed() ) {
					menu.dispose();
				}
				menu = menuBuilder.getMenu(swtControl, true, byLayer, 20, agents);
				menu.setLocation(swtControl.toDisplay(mousex, mousey));
				menu.setVisible(true);
			}
		});
	}

}
