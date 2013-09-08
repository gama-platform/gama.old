/**
 * Created by drogoul, 19 janv. 2012
 * 
 */
package msi.gama.gui.views.actions;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.gui.displays.layers.*;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.swt.commands.AgentsMenu;
import msi.gama.gui.views.*;
import msi.gama.metamodel.agent.IAgent;
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

	private static Map<Class, Image> images = new HashMap();
	// private final Collection<IAgent> filteredList;

	static {
		images.put(GridLayer.class, SwtGui.getImageDescriptor("/icons/display_grid.png").createImage());
		images.put(AgentLayer.class, SwtGui.getImageDescriptor("/icons/display_agents.png").createImage());
		images.put(ImageLayer.class, SwtGui.getImageDescriptor("/icons/display_image.png").createImage());
		images.put(TextLayer.class, SwtGui.getImageDescriptor("/icons/display_text.png").createImage());
		images.put(SpeciesLayer.class, SwtGui.getImageDescriptor("/icons/display_species.png").createImage());
		images.put(ChartLayer.class, SwtGui.getImageDescriptor("/icons/display_chart.png").createImage());
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
				getImageDescriptor("/icons/display_species.png")) {

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
		final Collection<IAgent> filteredList) {
		// Dispose ?
		Menu menu = new Menu(parent);
		fill(menu, -1, withWorld, byLayer, filteredList);
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

	private class FocusOnSelection extends SelectionAdapter {

		ILayer display;
		IDisplaySurface surface;

		FocusOnSelection(final ILayer display, final IDisplaySurface surface) {
			this.display = display;
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
							surface.focusOn(a.getGeometry(), display);
						}

					}
				}).start();

			}
		}

	}

	private class FollowSelection extends SelectionAdapter {

		IDisplaySurface surface;

		FollowSelection(final IDisplaySurface surface) {
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

						if ( !a.dead() ) {
							surface.followAgent(a);
						}

					}
				}).start();

			}
		}

	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public void fill(final Menu menu, final int index) {
		fill(menu, index, false, true, null);
	}

	public void fill(final Menu menu, final int index, final boolean withWorld, final boolean byLayer,
		final Collection<IAgent> filteredList) {
		final LayeredDisplayView view = (LayeredDisplayView) this.view;
		final IDisplaySurface displaySurface = view.getDisplaySurface();
		AgentsMenu.MenuAction follow =
			new AgentsMenu.MenuAction(new FollowSelection(displaySurface), SwtGui.followImage, "Follow");
		if ( withWorld ) {
			AgentsMenu.cascadingAgentMenuItem(menu, GAMA.getSimulation(), "World");
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
			final FocusOnSelection adapter = new FocusOnSelection(null, displaySurface);
			AgentsMenu.MenuAction focus = new AgentsMenu.MenuAction(adapter, SwtGui.focusImage, "Focus on");
			if ( view.getOutput().isOpenGL() ) {
				AgentsMenu.fillPopulationSubMenu(menu, filteredList, focus, follow);
			} else {
				AgentsMenu.fillPopulationSubMenu(menu, filteredList, focus);
			}
		} else {
			for ( final ILayer layer : view.getDisplayManager().getItems() ) {
				final FocusOnSelection adapter = new FocusOnSelection(layer, displaySurface);
				AgentsMenu.MenuAction focus = new AgentsMenu.MenuAction(adapter, SwtGui.focusImage, "Focus on");
				boolean isSpeciesLayer = layer instanceof SpeciesLayer || layer instanceof GridLayer;
				boolean isAgentLayer = isSpeciesLayer || layer instanceof AgentLayer;
				if ( !isAgentLayer ) {
					continue;
				}
				Collection<IAgent> pop = null;
				if ( isSpeciesLayer ) {
					pop = GAMA.getSimulation().getMicroPopulation(layer.getName());
				} else {
					pop = ((AgentLayer) layer).getAgentsForMenu();
				}
				pop = new ArrayList(pop);
				if ( pop.isEmpty() ) {
					continue;
				}
				String layerName = layer.getType() + ": " + layer.getName();

				if ( view.getOutput().isOpenGL() ) {
					fill(menu, images.get(layer.getClass()), layerName, pop, filteredList, focus, follow);
				} else {
					fill(menu, images.get(layer.getClass()), layerName, pop, filteredList, focus);
				}

			}
		}
	}

	void fill(final Menu menu, final Image image, final String layerName, final Collection<IAgent> pop,
		final Collection<IAgent> filteredList, final AgentsMenu.MenuAction ... actions) {
		if ( filteredList != null ) {
			pop.retainAll(filteredList);
		}
		if ( pop.isEmpty() ) { return; }
		final MenuItem layerMenu = new MenuItem(menu, SWT.CASCADE);
		layerMenu.setText(layerName);
		layerMenu.setImage(image);

		AgentsMenu.fillPopulationSubMenu(layerMenu, pop, actions);

	}

}
