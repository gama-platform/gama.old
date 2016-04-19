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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ILayer;
import msi.gama.gui.swt.IGamaIcons;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.swt.commands.AgentsMenu;
import msi.gama.gui.swt.commands.AgentsMenu.MenuAction;
import msi.gama.gui.swt.swing.Platform;
import msi.gama.gui.views.LayeredDisplayView;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.outputs.layers.AgentLayer;
import msi.gama.outputs.layers.ChartLayer;
import msi.gama.outputs.layers.GraphicLayer;
import msi.gama.outputs.layers.GridLayer;
import msi.gama.outputs.layers.ImageLayer;
import msi.gama.outputs.layers.SpeciesLayer;
import msi.gama.outputs.layers.TextLayer;
import msi.gama.runtime.GAMA;

public class DisplaySurfaceMenu {

	public static Map<Class, Image> layer_images = new LinkedHashMap();

	static {
		layer_images.put(GridLayer.class, IGamaIcons.LAYER_GRID.image());
		layer_images.put(AgentLayer.class, IGamaIcons.LAYER_AGENTS.image());
		layer_images.put(ImageLayer.class, IGamaIcons.LAYER_IMAGE.image());
		layer_images.put(TextLayer.class, IGamaIcons.LAYER_TEXT.image());
		layer_images.put(SpeciesLayer.class, IGamaIcons.LAYER_SPECIES.image());
		layer_images.put(ChartLayer.class, IGamaIcons.LAYER_CHART.image());
		layer_images.put(GraphicLayer.class, IGamaIcons.LAYER_GRAPHICS.image());
	}

	private static class FocusOnSelection extends SelectionAdapter {

		IDisplaySurface surface;

		FocusOnSelection(final IDisplaySurface surface) {
			this.surface = surface;
		}

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final MenuItem mi = (MenuItem) e.widget;
			final IAgent a = (IAgent) mi.getData("agent");
			if ( a != null && !a.dead() ) {
				surface.runAndUpdate(new Runnable() {

					@Override
					public void run() {
						if ( !a.dead() ) {
							surface.focusOn(a);
						}
					}
				});
			}
		}
	}

	private final IDisplaySurface surface;
	private final Control swtControl;
	private final LayeredDisplayView view;

	public DisplaySurfaceMenu(final IDisplaySurface s, final Control c, final LayeredDisplayView view) {
		surface = s;
		this.view = view;
		swtControl = c;
		if ( s != null )
			s.setMenuManager(this);

	}

	org.eclipse.swt.widgets.Menu menu;

	public void buildMenu(final int mousex, final int mousey, final int x, final int y,
		final ILocation modelCoordinates, final List<ILayer> displays) {
		if ( displays.isEmpty() ) { return; }
		if ( menu != null && !menu.isDisposed() ) {
			menu.dispose();
			menu = null;
		}
		final Set<IAgent> all = new LinkedHashSet();
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
		final GamaPoint modelCoordinates = agent == null ? null : (GamaPoint) agent.getLocation();
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
				menu = fill(new Menu(swtControl), -1, true, byLayer, agents, modelCoordinates);
				menu.setData(IKeyword.USER_LOCATION, modelCoordinates);
				menu.setLocation(swtControl.toDisplay(mousex, mousey));
				menu.setVisible(true);
				// AD 3/10/13: Fix for Issue 669 on Linux GTK setup. See :
				// http://www.eclipse.org/forums/index.php/t/208284/
				retryVisible(menu, MAX_RETRIES);
			}
		});
	}

	public void buildToolbarMenu(final Menu menu) {
		fill(menu, -1, false, true, null, null);
	}

	static int MAX_RETRIES = 10;

	private void retryVisible(final Menu menu, final int retriesRemaining) {
		if ( !Platform.isGtk() )
			return;
		GAMA.getGui().asyncRun(new Runnable() {

			@Override
			public void run() {
				if ( !menu.isVisible() && retriesRemaining > 0 ) {
					menu.setVisible(false);
					{
						final Shell shell = new Shell(SwtGui.getDisplay(), SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
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

	private Menu fill(final Menu menu, final int index, final boolean withWorld, final boolean byLayer,
		final Collection<IAgent> filteredList, final ILocation userLocation) {
		// final LayeredDisplayView view2 = (LayeredDisplayView) view;
		// final IDisplaySurface displaySurface = view2.getDisplaySurface();
		// AgentsMenu.MenuAction follow =
		// new AgentsMenu.MenuAction(new FollowSelection(displaySurface), IGamaIcons.MENU_FOLLOW.image(), "Follow");
		if ( withWorld ) {
			AgentsMenu.cascadingAgentMenuItem(menu, surface.getDisplayScope().getSimulationScope(), userLocation,
				"World");
			if ( filteredList != null && !filteredList.isEmpty() ) {
				AgentsMenu.separate(menu);
			} else {
				return menu;
			}
			if ( byLayer ) {
				AgentsMenu.separate(menu, "Layers");
			}
		}
		if ( !byLayer ) {
			// If the list is null or empty, no need to display anything more
			if ( filteredList == null || filteredList.isEmpty() ) { return menu; }
			// If only the world is selected, no need to display anything more
			if ( filteredList.size() == 1 &&
				filteredList.contains(surface.getDisplayScope().getSimulationScope()) ) { return menu; }
			final FocusOnSelection adapter = new FocusOnSelection(surface);
			final AgentsMenu.MenuAction focus =
				new AgentsMenu.MenuAction(adapter, IGamaIcons.MENU_FOCUS.image(), "Focus");
			if ( view.isOpenGL() ) {
				// FIXME: 18/03/2014 a.g the follow item has been temporaly removed from opengl because not yet
				// implemented but should be available in 1.7
				AgentsMenu.fillPopulationSubMenu(menu, filteredList, userLocation, focus /* , follow */);
			} else {
				AgentsMenu.fillPopulationSubMenu(menu, filteredList, userLocation, focus);
			}
		} else {

			for ( final ILayer layer : surface.getManager().getItems() ) {
				if ( layer.isSelectable() ) {
					Collection<IAgent> pop = layer.getAgentsForMenu(surface.getDisplayScope());
					pop = new ArrayList(pop);
					if ( pop.isEmpty() ) {
						continue;
					}
					final String layerName = layer.getType() + ": " + layer.getName();
					final FocusOnSelection adapter = new FocusOnSelection(surface);
					final AgentsMenu.MenuAction focus =
						new AgentsMenu.MenuAction(adapter, IGamaIcons.MENU_FOCUS.image(), "Focus on");
					final MenuAction[] actions = { focus };

					if ( filteredList != null ) {
						pop.retainAll(filteredList);
					}
					if ( pop.isEmpty() ) {
						continue;
					}
					final MenuItem layerMenu = new MenuItem(menu, SWT.CASCADE);
					layerMenu.setText(layerName);
					layerMenu.setImage(layer_images.get(layer.getClass()));
					final Menu submenu = new Menu(layerMenu);
					layerMenu.setMenu(submenu);
					AgentsMenu.fillPopulationSubMenu(submenu, pop, userLocation, actions);
				}
			}
		}
		return menu;
	}

	public Menu buildROIMenu(final int x, final int y, final Collection<IAgent> agents,
		final ILocation modelCoordinates, final Map<String, Runnable> actions, final Map<String, Image> images) {

		if ( menu != null && !menu.isDisposed() ) {
			menu.dispose();
		}
		menu = fill(new Menu(swtControl), -1, false, true, agents, modelCoordinates);
		menu.setData(IKeyword.USER_LOCATION, modelCoordinates);
		menu.setLocation(swtControl.toDisplay(x, y));
		int i = 0;
		for ( final String s : actions.keySet() ) {
			final MenuItem mu = new MenuItem(menu, SWT.PUSH, i++);
			mu.setText(s);
			mu.setImage(images.get(s));
			mu.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(final SelectionEvent e) {
					actions.get(s).run();
				}

				@Override
				public void widgetDefaultSelected(final SelectionEvent e) {
					widgetSelected(e);
				}
			});
		}

		new MenuItem(menu, SWT.SEPARATOR, i);

		return menu;
	}

}
