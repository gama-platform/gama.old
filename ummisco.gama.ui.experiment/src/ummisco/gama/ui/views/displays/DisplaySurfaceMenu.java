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
package ummisco.gama.ui.views.displays;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.ILayer;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.layers.AgentLayer;
import msi.gama.outputs.layers.GraphicLayer;
import msi.gama.outputs.layers.GridLayer;
import msi.gama.outputs.layers.ImageLayer;
import msi.gama.outputs.layers.SpeciesLayer;
import msi.gama.outputs.layers.TextLayer;
import msi.gama.outputs.layers.charts.ChartLayer;
import ummisco.gama.ui.menus.AgentsMenu;
import ummisco.gama.ui.menus.GamaMenu;
import ummisco.gama.ui.menus.MenuAction;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.utils.PlatformHelper;
import ummisco.gama.ui.utils.WorkbenchHelper;

public class DisplaySurfaceMenu {

	public static Map<Class, Image> layer_images = new LinkedHashMap();

	static {
		layer_images.put(GridLayer.class, GamaIcons.create(IGamaIcons.LAYER_GRID).image());
		layer_images.put(AgentLayer.class, GamaIcons.create(IGamaIcons.LAYER_AGENTS).image());
		layer_images.put(ImageLayer.class, GamaIcons.create(IGamaIcons.LAYER_IMAGE).image());
		layer_images.put(TextLayer.class, GamaIcons.create(IGamaIcons.LAYER_TEXT).image());
		layer_images.put(SpeciesLayer.class, GamaIcons.create(IGamaIcons.LAYER_SPECIES).image());
		layer_images.put(ChartLayer.class, GamaIcons.create(IGamaIcons.LAYER_CHART).image());
		layer_images.put(GraphicLayer.class, GamaIcons.create(IGamaIcons.LAYER_GRAPHICS).image());
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
			if (a != null && !a.dead()) {
				surface.runAndUpdate(new Runnable() {

					@Override
					public void run() {
						if (!a.dead()) {
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
		if (s != null)
			s.setMenuManager(this);

	}

	org.eclipse.swt.widgets.Menu menu;

	public void buildMenu(final int mousex, final int mousey, final int x, final int y, final List<ILayer> displays) {
		if (displays.isEmpty()) {
			return;
		}
		if (menu != null && !menu.isDisposed()) {
			menu.dispose();
			menu = null;
		}
		final Set<IAgent> all = new LinkedHashSet();
		for (final ILayer display : displays) {
			if (display.isSelectable()) {
				final Set<IAgent> agents = display.collectAgentsAt(x, y, surface);
				if (agents.isEmpty()) {
					continue;
				}
				all.addAll(agents);
			}
		}
		buildMenu(true, mousex, mousey, all, null);
	}

	public void buildMenu(final int mousex, final int mousey, final IAgent agent, final Runnable cleanup,
			final MenuAction... actions) {
		// cleanup is an optional runnable to do whatever is necessary after the
		// menu has disappeared
		buildMenu(false, mousex, mousey, agent == null ? Collections.EMPTY_LIST : Collections.singleton(agent), cleanup,
				actions);
	}

	public void buildMenu(final boolean byLayer, final int mousex, final int mousey, final Collection<IAgent> agents,
			final Runnable cleanup, final MenuAction... actions) {
		WorkbenchHelper.asyncRun(new Runnable() {

			@Override
			public void run() {
				if (menu != null && !menu.isDisposed()) {
					menu.dispose();
				}
				menu = fill(new Menu(swtControl), -1, true, byLayer, agents, actions);
				menu.setLocation(swtControl.toDisplay(mousex, mousey));
				menu.setVisible(true);
				// AD 3/10/13: Fix for Issue 669 on Linux GTK setup. See :
				// http://www.eclipse.org/forums/index.php/t/208284/
				retryVisible(menu, MAX_RETRIES);
				if (cleanup != null)
					menu.addMenuListener(new MenuListener() {

						@Override
						public void menuShown(final MenuEvent e) {
							// System.out.println("Selection menu has been
							// shown");
						}

						@Override
						public void menuHidden(final MenuEvent e) {
							// System.out.println("Selection menu has been
							// hiden");
							cleanup.run();
							menu.removeMenuListener(this);
						}
					});
			}
		});
	}

	public void buildToolbarMenu(final Menu menu) {
		fill(menu, -1, false, true, null);
	}

	static int MAX_RETRIES = 10;

	private void retryVisible(final Menu menu, final int retriesRemaining) {
		if (!PlatformHelper.isGtk())
			return;
		WorkbenchHelper.asyncRun(new Runnable() {

			@Override
			public void run() {
				if (!menu.isVisible() && retriesRemaining > 0) {
					menu.setVisible(false);
					{
						final Shell shell = new Shell(WorkbenchHelper.getDisplay(),
								SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
						shell.setSize(10, 10); // big enough to avoid errors
												// from the gtk layer
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
			final Collection<IAgent> filteredList, final MenuAction... actions) {
		if (withWorld) {
			AgentsMenu.cascadingAgentMenuItem(menu, surface.getScope().getSimulation(), "World", actions);
			if (filteredList != null && !filteredList.isEmpty()) {
				GamaMenu.separate(menu);
			} else {
				return menu;
			}
			if (byLayer) {
				GamaMenu.separate(menu, "Layers");
			}
		}
		if (!byLayer) {
			// If the list is null or empty, no need to display anything more
			if (filteredList == null || filteredList.isEmpty()) {
				return menu;
			}
			// If only the world is selected, no need to display anything more
			if (filteredList.size() == 1 && filteredList.contains(surface.getScope().getSimulation())) {
				return menu;
			}
			final FocusOnSelection adapter = new FocusOnSelection(surface);
			final MenuAction focus = new MenuAction(adapter, GamaIcons.create(IGamaIcons.MENU_FOCUS).image(),
					"Focus on this display");
			final MenuAction[] actions2 = new MenuAction[actions.length + 1];
			for (int i = 0; i < actions.length; i++) {
				actions2[i + 1] = actions[i];
			}
			actions2[0] = focus;
			AgentsMenu.fillPopulationSubMenu(menu, filteredList, actions2);
		} else {

			for (final ILayer layer : surface.getManager().getItems()) {
				if (layer.isSelectable()) {
					Collection<IAgent> pop = layer.getAgentsForMenu(surface.getScope());
					pop = new ArrayList(pop);
					if (pop.isEmpty()) {
						continue;
					}
					final String layerName = layer.getType() + ": " + layer.getName();
					final FocusOnSelection adapter = new FocusOnSelection(surface);
					final MenuAction focus = new MenuAction(adapter, GamaIcons.create(IGamaIcons.MENU_FOCUS).image(),
							"Focus on this display");
					final MenuAction[] actions2;
					if (layer instanceof GridLayer)
						actions2 = new MenuAction[] { focus };
					else
						actions2 = new MenuAction[] { focus, AgentsMenu.HIGHLIGHT_ACTION };

					if (filteredList != null) {
						pop.retainAll(filteredList);
					}
					if (pop.isEmpty()) {
						continue;
					}
					final MenuItem layerMenu = new MenuItem(menu, SWT.CASCADE);
					layerMenu.setText(layerName);
					layerMenu.setImage(layer_images.get(layer.getClass()));
					final Menu submenu = new Menu(layerMenu);
					layerMenu.setMenu(submenu);
					AgentsMenu.fillPopulationSubMenu(submenu, pop, actions2);
				}
			}
		}
		return menu;
	}

	public Menu buildROIMenu(final int x, final int y, final Collection<IAgent> agents,
			final Map<String, Runnable> actions, final Map<String, Image> images) {

		if (menu != null && !menu.isDisposed()) {
			menu.dispose();
		}
		menu = fill(new Menu(swtControl), -1, false, true, agents);
		menu.setLocation(swtControl.toDisplay(x, y));
		int i = 0;
		for (final String s : actions.keySet()) {
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
