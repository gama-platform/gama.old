/*******************************************************************************************************
 *
 * DisplaySurfaceMenu.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.views.displays;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolItem;

import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.common.interfaces.ILayer;
import msi.gama.common.interfaces.ILayer.IGridLayer;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.layers.AgentLayer;
import msi.gama.outputs.layers.GraphicLayer;
import msi.gama.outputs.layers.GridAgentLayer;
import msi.gama.outputs.layers.GridLayer;
import msi.gama.outputs.layers.GridLayerStatement;
import msi.gama.outputs.layers.ILayerStatement;
import msi.gama.outputs.layers.ImageLayer;
import msi.gama.outputs.layers.MeshLayer;
import msi.gama.outputs.layers.OverlayLayer;
import msi.gama.outputs.layers.SpeciesLayer;
import msi.gama.outputs.layers.SpeciesLayerStatement;
import msi.gama.outputs.layers.charts.ChartLayer;
import msi.gama.outputs.layers.charts.ChartLayerStatement;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.PlatformHelper;
import msi.gama.util.IList;
import msi.gaml.types.Types;
import ummisco.gama.ui.menus.AgentsMenu;
import ummisco.gama.ui.menus.GamaMenu;
import ummisco.gama.ui.menus.MenuAction;
import ummisco.gama.ui.resources.GamaIcon;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.utils.WorkbenchHelper;

/**
 * The Class DisplaySurfaceMenu.
 */
public class DisplaySurfaceMenu {

	/** The layer images. */
	public static Map<Class<? extends ILayer>, Image> layer_images = new LinkedHashMap<>();

	static {
		layer_images.put(GridLayer.class, GamaIcon.named(IGamaIcons.LAYER_GRID).image());
		layer_images.put(GridAgentLayer.class, GamaIcon.named(IGamaIcons.LAYER_GRID).image());
		layer_images.put(MeshLayer.class, GamaIcon.named(IGamaIcons.LAYER_GRID).image());
		layer_images.put(AgentLayer.class, GamaIcon.named(IGamaIcons.LAYER_AGENTS).image());
		layer_images.put(ImageLayer.class, GamaIcon.named(IGamaIcons.LAYER_IMAGE).image());
		layer_images.put(OverlayLayer.class, GamaIcon.named(IGamaIcons.LAYER_IMAGE).image());
		layer_images.put(SpeciesLayer.class, GamaIcon.named(IGamaIcons.LAYER_SPECIES).image());
		layer_images.put(ChartLayer.class, GamaIcon.named(IGamaIcons.LAYER_CHART).image());
		layer_images.put(GraphicLayer.class, GamaIcon.named(IGamaIcons.LAYER_GRAPHICS).image());
	}

	/** The menu. */
	Menu menu;

	/** The surface. */
	private final IDisplaySurface surface;

	/** The swt control. */
	private final Control swtControl;

	/** The presentation menu. */
	private final Function<Menu, Menu> presentationMenu;

	/**
	 * The Class FocusOnSelection.
	 */
	private static class FocusOnSelection extends SelectionAdapter {

		/** The surface. */
		IDisplaySurface surface;

		/**
		 * Instantiates a new focus on selection.
		 *
		 * @param surface
		 *            the surface
		 */
		FocusOnSelection(final IDisplaySurface surface) {
			this.surface = surface;
		}

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final MenuItem mi = (MenuItem) e.widget;
			final IAgent a = (IAgent) mi.getData("agent");
			if (a != null && !a.dead()) { surface.runAndUpdate(() -> { if (!a.dead()) { surface.focusOn(a); } }); }
		}
	}

	/**
	 * Instantiates a new display surface menu.
	 *
	 * @param s
	 *            the s
	 * @param c
	 *            the c
	 * @param viewMenu
	 *            the view menu
	 */
	public DisplaySurfaceMenu(final IDisplaySurface s, final Control c, final Function<Menu, Menu> viewMenu) {
		surface = s;
		swtControl = c;
		if (s != null) { s.setMenuManager(this); }
		this.presentationMenu = viewMenu;
	}

	/**
	 * Prepare new menu.
	 *
	 * @param c
	 *            the c
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param withPresentation
	 *            the with presentation
	 */
	public void prepareNewMenu(final Control c, final int x, final int y, final boolean withPresentation) {
		disposeMenu();
		menu = new Menu(c);
		// menu.setLocation(scaleDownIfWin(c.toDisplay(x, y)));
		if (withPresentation) {
			presentationMenu.apply(menu);
			GamaMenu.separate(menu);
		}
	}

	/**
	 * Builds the menu.
	 *
	 * @param mousex
	 *            the mousex
	 * @param mousey
	 *            the mousey
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param displays
	 *            the displays
	 */
	public void buildMenu(final int mousex, final int mousey, final int x, final int y, final List<ILayer> displays) {
		if (displays.isEmpty()) return;
		final Set<IAgent> all = new LinkedHashSet<>();
		for (final ILayer display : displays) {
			if (display.getData().isSelectable()) {
				final Set<IAgent> agents = display.collectAgentsAt(x, y, surface);
				if (agents.isEmpty()) { continue; }
				all.addAll(agents);
			}
		}
		buildMenu(true, mousex, mousey, all, null);
	}

	/**
	 * Builds the menu.
	 *
	 * @param mousex
	 *            the mousex
	 * @param mousey
	 *            the mousey
	 * @param agent
	 *            the agent
	 * @param cleanup
	 *            the cleanup
	 * @param actions
	 *            the actions
	 */
	public void buildMenu(final int mousex, final int mousey, final IAgent agent, final Runnable cleanup,
			final MenuAction... actions) {
		// cleanup is an optional runnable to do whatever is necessary after the
		// menu has disappeared
		buildMenu(false, mousex, mousey, agent == null ? Collections.EMPTY_LIST : Collections.singleton(agent), cleanup,
				actions);
	}

	/**
	 * Builds the menu.
	 *
	 * @param byLayer
	 *            the by layer
	 * @param mousex
	 *            the mousex
	 * @param mousey
	 *            the mousey
	 * @param agents
	 *            the agents
	 * @param cleanup
	 *            the cleanup
	 * @param actions
	 *            the actions
	 */
	private void buildMenu(final boolean byLayer, final int mousex, final int mousey, final Collection<IAgent> agents,
			final Runnable cleanup, final MenuAction... actions) {
		WorkbenchHelper.asyncRun(() -> {
			prepareNewMenu(swtControl, mousex, mousey, true);
			fill(menu, -1, true, byLayer, agents, actions);
			menu.setVisible(true);
			// AD 3/10/13: Fix for Issue 669 on Linux GTK setup. See :
			// http://www.eclipse.org/forums/index.php/t/208284/
			retryVisible(menu, MAX_RETRIES);
			if (cleanup != null) {
				menu.addMenuListener(new MenuAdapter() {

					@Override
					public void menuHidden(final MenuEvent e) {
						menu.removeMenuListener(this);
						cleanup.run();
					}
				});
			}
		});
	}

	/**
	 * Builds the toolbar menu.
	 *
	 * @param trigger
	 *            the trigger
	 * @param t
	 *            the t
	 */
	public void buildToolbarMenu(final SelectionEvent trigger, final ToolItem t) {
		prepareNewMenu(t.getParent(), t.getBounds().x + t.getBounds().width, t.getBounds().y + t.getBounds().height,
				false);
		fill(menu, -1, false, true, null);
		menu.setVisible(true);
	}

	/** The max retries. */
	static int MAX_RETRIES = 10;

	/**
	 * Retry visible.
	 *
	 * @param menu
	 *            the menu
	 * @param retriesRemaining
	 *            the retries remaining
	 */
	private void retryVisible(final Menu menu, final int retriesRemaining) {
		if (!PlatformHelper.isLinux()) return;
		WorkbenchHelper.asyncRun(() -> {
			if (!menu.isVisible() && retriesRemaining > 0) {
				menu.setVisible(false);
				{
					final Shell shell =
							new Shell(WorkbenchHelper.getDisplay(), SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
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
		});
	}

	/**
	 * Fill.
	 *
	 * @param menu
	 *            the menu
	 * @param index
	 *            the index
	 * @param withWorld
	 *            the with world
	 * @param byLayer
	 *            the by layer
	 * @param filteredList
	 *            the filtered list
	 * @param actions
	 *            the actions
	 */
	private void fill(final Menu menu, final int index, final boolean withWorld, final boolean byLayer,
			final Collection<IAgent> filteredList, final MenuAction... actions) {
		if (withWorld) {
			AgentsMenu.cascadingAgentMenuItem(menu, surface.getScope().getSimulation(), "World", actions);
			if (filteredList == null || filteredList.isEmpty()) return;
			GamaMenu.separate(menu);
			if (byLayer) { GamaMenu.separate(menu, "Layers"); }
		}
		if (!byLayer) {
			// If the list is null or empty, no need to display anything more
			// If only the world is selected, no need to display anything more
			if (filteredList == null || filteredList.isEmpty()
					|| filteredList.size() == 1 && filteredList.contains(surface.getScope().getSimulation()))
				return;
			final FocusOnSelection adapter = new FocusOnSelection(surface);
			final MenuAction focus =
					new MenuAction(adapter, GamaIcon.named(IGamaIcons.MENU_FOCUS).image(), "Focus on this display");
			final MenuAction[] actions2 = new MenuAction[actions.length + 1];
			for (int i = 0; i < actions.length; i++) { actions2[i + 1] = actions[i]; }
			actions2[0] = focus;
			AgentsMenu.fillPopulationSubMenu(menu, filteredList, actions2);
		} else {
			for (final ILayer layer : surface.getManager().getItems()) {
				boolean select = layer.getData().isSelectable();
				boolean visible = layer.getData().isVisible();
				final ILayerStatement definition = layer.getDefinition();

				IList<? extends IAgent> pop = layer.getAgentsForMenu(surface.getScope());
				pop = pop.listValue(null, Types.AGENT, false);
				// if (pop.isEmpty()) { continue; }

				if (filteredList != null) { pop.retainAll(filteredList); }
				// if (pop.isEmpty()) { continue; }
				final MenuItem layerMenu = new MenuItem(menu, SWT.CASCADE);
				layerMenu.setText(layer.getType() + ": " + layer.getName());
				layerMenu.setImage(layer_images.get(layer.getClass()));
				if (!layer.isControllable()) { continue; }
				final Menu submenu = new Menu(layerMenu);
				layerMenu.setMenu(submenu);
				GamaMenu.separate(submenu, "Actions");
				GamaMenu.action(submenu, visible ? "Hide" : "Show", t -> {
					layer.getData().setVisible(!visible);
					surface.updateDisplay(true);
				}, GamaIcon.named(IGamaIcons.MENU_INSPECT).image());
				if (!pop.isEmpty()) {
					GamaMenu.action(submenu, select ? "Forbid selection" : "Allow selection",
							t -> layer.getData().setSelectable(!select),
							GamaIcon.named(IGamaIcons.LAYER_SELECTION).image());
				}
				Menu transparency = GamaMenu.sub(submenu, "Transparency", "",
						GamaIcon.named(IGamaIcons.LAYER_TRANSPARENCY).image());
				transparency.setEnabled(layer.getData().isDynamic());
				Double td = layer.getData().getTransparency(GAMA.getRuntimeScope());
				int ti = (int) (td == null ? 0 : Math.round(td * 10) * 10);
				for (int i = 0; i <= 100; i += 10) {
					double value = i;
					GamaMenu.check(transparency, " " + i + "%", ti == i, t -> {
						layer.getData().setTransparency(value / 100d);
						surface.updateDisplay(true);
					}, null);
				}
				if (definition instanceof SpeciesLayerStatement spec) {
					Menu aspectMenu =
							GamaMenu.sub(submenu, "Aspect", "", GamaIcon.named(IGamaIcons.MENU_AGENT).image());
					aspectMenu.setEnabled(layer.getData().isDynamic());
					String current = spec.getAspectName();
					for (String aspect : spec.getAspects()) {
						GamaMenu.check(aspectMenu, aspect, aspect.equals(current), t -> {
							spec.setAspect(aspect);
							surface.updateDisplay(true);
						}, null);
					}
				} else if (definition instanceof ChartLayerStatement chart) {
					GamaMenu.action(submenu, "Properties", t -> {
						// FIXME Editor not working for the moment
						Point p = WorkbenchHelper.getDisplay().getCursorLocation();
						p.x -= 100;
						p.y += 100;
						final SWTChartEditor editor = new SWTChartEditor(WorkbenchHelper.getDisplay(),
								((ChartLayerStatement) definition).getChart(), p);
						editor.open();
						surface.updateDisplay(true);
					}, GamaIcon.named(IGamaIcons.CHART_PARAMETERS).image());
					if (chart.keepsHistory()) {
						GamaMenu.action(submenu, "Save history...", t -> chart.saveHistory(),
								GamaIcon.named(IGamaIcons.MENU_BROWSE).image());
					}
				} else if (definition instanceof GridLayerStatement grid) {
					boolean lines = ((IGridLayer) layer).getData().drawLines();
					GamaMenu.action(submenu, lines ? "Hide lines" : "Draw lines", t -> {
						((IGridLayer) layer).getData().setDrawLines(!lines);
						surface.updateDisplay(true);
					}, GamaIcon.named(IGamaIcons.MENU_BROWSE).image());
				}
				if (select) {
					final FocusOnSelection adapter = new FocusOnSelection(surface);
					final MenuAction focus = new MenuAction(adapter, GamaIcon.named(IGamaIcons.MENU_FOCUS).image(),
							"Focus on this display");
					final MenuAction[] actions2 = { focus };
					Menu agentsMenu =
							GamaMenu.sub(submenu, "Agents", "", GamaIcon.named(IGamaIcons.MENU_POPULATION).image());
					AgentsMenu.fillPopulationSubMenu(agentsMenu, pop, actions2);
				}
			}
		}
	}

	/**
	 * Builds the ROI menu.
	 *
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param agents
	 *            the agents
	 * @param actions
	 *            the actions
	 * @param images
	 *            the images
	 * @return the menu
	 */
	@SuppressWarnings ("unused")
	public Menu buildROIMenu(final int x, final int y, final Collection<IAgent> agents,
			final Map<String, Runnable> actions, final Map<String, Image> images) {

		prepareNewMenu(swtControl, x, y, true);
		fill(menu, -1, false, true, agents);
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

	/**
	 * Dispose menu.
	 */
	public void disposeMenu() {
		if (menu != null && !menu.isDisposed()) { menu.dispose(); }
		menu = null;
	}

}
