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

import gnu.trove.map.hash.THashMap;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.gui.swt.IGamaIcons;
import msi.gama.gui.swt.commands.AgentsMenu;
import msi.gama.gui.views.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.layers.*;
import msi.gama.runtime.GAMA;
import org.eclipse.jface.action.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;

/**
 * The class FocusItem.
 * 
 * @author drogoul
 * @since 19 janv. 2012
 * 
 */
public class DisplayedAgentsMenu extends GamaViewItem implements IMenuCreator {

	// private final Collection<IAgent> filteredList;

	public static Map<Class, Image> layer_images = new THashMap();

	static {
		layer_images.put(GridLayer.class, IGamaIcons.LAYER_GRID.image());
		layer_images.put(AgentLayer.class, IGamaIcons.LAYER_AGENTS.image());
		layer_images.put(ImageLayer.class, IGamaIcons.LAYER_IMAGE.image());
		layer_images.put(TextLayer.class, IGamaIcons.LAYER_TEXT.image());
		layer_images.put(SpeciesLayer.class, IGamaIcons.LAYER_SPECIES.image());
		layer_images.put(ChartLayer.class, IGamaIcons.LAYER_CHART.image());
		layer_images.put(GraphicLayer.class, IGamaIcons.LAYER_GRAPHICS.image());
	}

	public DisplayedAgentsMenu(final GamaViewPart view) {
		super(view);
	}

	/**
	 * @see msi.gama.gui.views.actions.GamaViewItem#createItem()
	 */
	@Override
	protected IContributionItem createItem() {
		final IAction action =
			new GamaAction("Browse displayed agents", "Browse through all displayed agents", IAction.AS_DROP_DOWN_MENU,
				IGamaIcons.MENU_POPULATION.descriptor()) {

				@Override
				public void run() {}
			};
		action.setMenuCreator(this);
		return new ActionContributionItem(action);
	}

	// @Override
	// public boolean isDynamic() {
	// return true;
	// }

	/**
	 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Control)
	 */
	@Override
	public Menu getMenu(final Control parent) {
		// Dispose ?
		Menu menu = new Menu(parent);
		fill(menu, -1);
		return menu;
	}

	public Menu getMenu(final Control parent, final boolean withWorld, final boolean byLayer,
		final Collection<IAgent> filteredList, final GamaPoint userLocation) {
		// Dispose ?
		Menu menu = new Menu(parent);
		fill(menu, -1, withWorld, byLayer, filteredList, userLocation);
		return menu;
	}

	/**
	 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Menu)
	 */
	@Override
	public Menu getMenu(final Menu parent) {
		fill(parent, -1);
		return parent;
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
				new Thread(new Runnable() {

					@Override
					public void run() {
						while (!surface.canBeUpdated()) {
							try {
								Thread.sleep(10);
							} catch (final InterruptedException e) {

							}
						}
						if ( !a.dead() ) {
							surface.focusOn(a);
						}

					}
				}).start();

			}
		}

	}

	// private class FollowSelection extends SelectionAdapter {
	//
	// IDisplaySurface surface;
	//
	// FollowSelection(final IDisplaySurface surface) {
	// this.surface = surface;
	// }
	//
	// @Override
	// public void widgetSelected(final SelectionEvent e) {
	// final MenuItem mi = (MenuItem) e.widget;
	// final IAgent a = (IAgent) mi.getData("agent");
	// if ( a != null && !a.dead() ) {
	// new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// if ( !a.dead() ) {
	// surface.followAgent(a);
	// }
	//
	// }
	// }).start();
	//
	// }
	// }
	//
	// }

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public void fill(final Menu menu, final int index) {
		fill(menu, index, false, true, null, null);
	}

	public void fill(final Menu menu, final int index, final boolean withWorld, final boolean byLayer,
		final Collection<IAgent> filteredList, final GamaPoint userLocation) {
		final LayeredDisplayView view = (LayeredDisplayView) this.view;
		final IDisplaySurface displaySurface = view.getDisplaySurface();
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
			final FocusOnSelection adapter = new FocusOnSelection(displaySurface);
			AgentsMenu.MenuAction focus = new AgentsMenu.MenuAction(adapter, IGamaIcons.MENU_FOCUS.image(), "Focus");
			if ( view.getOutput().isOpenGL() ) {
				// FIXME: 18/03/2014 a.g the follow item has been temporaly remove from opengl because not yet
				// implemented but should be available in 1.7
				AgentsMenu.fillPopulationSubMenu(menu, filteredList, userLocation, focus /* , follow */);
			} else {
				AgentsMenu.fillPopulationSubMenu(menu, filteredList, userLocation, focus);
			}
		} else {
			for ( final ILayer layer : view.getDisplayManager().getItems() ) {
				final FocusOnSelection adapter = new FocusOnSelection(displaySurface);
				AgentsMenu.MenuAction focus =
					new AgentsMenu.MenuAction(adapter, IGamaIcons.MENU_FOCUS.image(), "Focus on");
				boolean isSpeciesLayer = layer instanceof SpeciesLayer || layer instanceof GridLayer;
				boolean isAgentLayer = isSpeciesLayer || layer instanceof AgentLayer;
				if ( !isAgentLayer ) {
					continue;
				}
				Collection<IAgent> pop = null;
				if ( isSpeciesLayer ) {
					pop = GAMA.getSimulation().getMicroPopulation(layer.getName());
				} else {
					pop = ((AgentLayer) layer).getAgentsForMenu(displaySurface.getDisplayScope());
				}
				pop = new ArrayList(pop);
				if ( pop.isEmpty() ) {
					continue;
				}
				String layerName = layer.getType() + ": " + layer.getName();

				if ( view.getOutput().isOpenGL() ) {
					fill(menu, layer_images.get(layer.getClass()), layerName, pop, filteredList, userLocation, focus/* , follow */);
				} else {
					fill(menu, layer_images.get(layer.getClass()), layerName, pop, filteredList, userLocation, focus);
				}

			}
		}
	}

	void fill(final Menu menu, final Image image, final String layerName, final Collection<IAgent> pop,
		final Collection<IAgent> filteredList, final GamaPoint userLocation, final AgentsMenu.MenuAction ... actions) {
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

}
