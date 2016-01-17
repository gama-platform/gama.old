/*********************************************************************************************
 * 
 * 
 * 'DisplayedAgentsMenu.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.gui.views.actions;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.gui.swt.IGamaIcons;
import msi.gama.gui.swt.commands.AgentsMenu;
import msi.gama.gui.swt.controls.GamaToolbar2;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.outputs.layers.*;
import msi.gama.outputs.layers.charts.ChartLayer;
import msi.gama.runtime.GAMA;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

/**
 * The class DisplayedAgentsMenu.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public class DisplayedAgentsMenu {

	private Menu menu;

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

	public Menu getMenu(final IDisplaySurface surface, final Control parent, final boolean withWorld,
		final boolean byLayer, final Collection<IAgent> filteredList, final ILocation userLocation,
		final boolean isOpenGL) {
		// Dispose ?
		Menu menu = new Menu(parent);
		fill(surface, menu, -1, withWorld, byLayer, filteredList, userLocation, isOpenGL);
		return menu;
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

	public void fill(final IDisplaySurface surface, final Menu menu, final int index, final boolean withWorld,
		final boolean byLayer, final Collection<IAgent> filteredList, final ILocation userLocation,
		final boolean isOpenGL) {
		// final LayeredDisplayView view2 = (LayeredDisplayView) view;
		// final IDisplaySurface displaySurface = view2.getDisplaySurface();
		// AgentsMenu.MenuAction follow =
		// new AgentsMenu.MenuAction(new FollowSelection(displaySurface), IGamaIcons.MENU_FOLLOW.image(), "Follow");
		if ( withWorld ) {
			AgentsMenu.cascadingAgentMenuItem(menu, GAMA.getSimulation(), userLocation, "World");
			if ( filteredList != null && !filteredList.isEmpty() ) {
				AgentsMenu.separate(menu);
			}
			if ( byLayer ) {
				AgentsMenu.separate(menu, "Layers");
			}
		}
		if ( !byLayer ) {
			// If the list is null or empty, no need to display anything more
			if ( filteredList == null || filteredList.isEmpty() ) { return; }
			// If only the world is selected, no need to display anything more
			if ( filteredList.size() == 1 && filteredList.contains(GAMA.getSimulation()) ) { return; }
			final FocusOnSelection adapter = new FocusOnSelection(surface);
			AgentsMenu.MenuAction focus = new AgentsMenu.MenuAction(adapter, IGamaIcons.MENU_FOCUS.image(), "Focus");
			if ( isOpenGL ) {
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
					String layerName = layer.getType() + ": " + layer.getName();
					final FocusOnSelection adapter = new FocusOnSelection(surface);
					AgentsMenu.MenuAction focus =
						new AgentsMenu.MenuAction(adapter, IGamaIcons.MENU_FOCUS.image(), "Focus on");

					// if ( isOpenGL ) {
					fill(menu, layer_images.get(layer.getClass()), layerName, pop, filteredList, userLocation, focus/* , follow */);
					// } else {
					// fill(menu, layer_images.get(layer.getClass()), layerName, pop, filteredList, userLocation, focus);
					// }
				}
			}
		}
	}

	void fill(final Menu menu, final Image image, final String layerName, final Collection<IAgent> pop,
		final Collection<IAgent> filteredList, final ILocation userLocation, final AgentsMenu.MenuAction ... actions) {
		if ( filteredList != null ) {
			pop.retainAll(filteredList);
		}
		if ( pop.isEmpty() ) { return; }
		final MenuItem layerMenu = new MenuItem(menu, SWT.CASCADE);
		layerMenu.setText(layerName);
		layerMenu.setImage(image);
		Menu submenu = new Menu(layerMenu);
		layerMenu.setMenu(submenu);
		AgentsMenu.fillPopulationSubMenu(submenu, pop, userLocation, actions);

	}

	/**
	 * @param tb
	 * @param view
	 */
	public void createItem(final GamaToolbar2 tb, final IDisplaySurface surface, final boolean isOpenGL) {

		tb.menu(IGamaIcons.MENU_POPULATION.getCode(), "Browse displayed agents by layers",
			"Browse through all displayed agents", new SelectionAdapter() {

				@Override
				public void widgetSelected(final SelectionEvent trigger) {
					boolean asMenu = trigger.detail == SWT.ARROW;
					final ToolItem target = (ToolItem) trigger.widget;
					final ToolBar toolBar = target.getParent();
					if ( menu != null ) {
						menu.dispose();
					}
					menu = new Menu(toolBar.getShell(), SWT.POP_UP);
					fill(surface, menu, -1, false, true, null, null, isOpenGL);
					Point point = toolBar.toDisplay(new Point(trigger.x, trigger.y));
					menu.setLocation(point.x, point.y);
					menu.setVisible(true);

				}
			}, SWT.LEFT);

	}

}
