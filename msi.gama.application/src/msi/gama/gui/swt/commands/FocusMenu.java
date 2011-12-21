/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.swt.commands;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.gui.displays.*;
import msi.gama.gui.swt.SwtGui;
import msi.gama.gui.views.LayeredDisplayView;
import msi.gama.metamodel.agent.IAgent;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;

public class FocusMenu extends ContributionItem {

	private static Map<Class, Image> images = new HashMap();

	static {
		images.put(GridDisplay.class, SwtGui.getImageDescriptor("/icons/display_grid.png")
			.createImage());
		images.put(AgentDisplay.class, SwtGui.getImageDescriptor("/icons/display_agents.png")
			.createImage());
		images.put(ImageDisplay.class, SwtGui.getImageDescriptor("/icons/display_image.png")
			.createImage());
		images.put(TextDisplay.class, SwtGui.getImageDescriptor("/icons/display_text.png")
			.createImage());
		images.put(SpeciesDisplay.class, SwtGui.getImageDescriptor("/icons/display_species.png")
			.createImage());
		images.put(ChartDisplay.class, SwtGui.getImageDescriptor("/icons/display_chart.png")
			.createImage());
	}

	public FocusMenu() {}

	public FocusMenu(final String id) {
		super(id);
	}

	SelectionAdapter adapter = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			MenuItem mi = (MenuItem) e.widget;
			final IAgent a = (IAgent) mi.getData("agent");
			final IDisplay d = (IDisplay) mi.getData("display");
			final IDisplaySurface s = (IDisplaySurface) mi.getData("surface");
			if ( a != null && !a.dead() ) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						while (!s.canBeUpdated()) {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {

							}
						}
						if ( !a.dead() ) {
							s.focusOn(a.getGeometry(), d);
						}

					}
				}).start();

			}
		}
	};

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public void fill(final Menu menu, final int index) {
		LayeredDisplayView view = (LayeredDisplayView) SwtGui.getPage().getActivePart();
		final IDisplaySurface displaySurface = view.getDisplaySurface();
		for ( final IDisplay item : displaySurface.getManager().getItems() ) {
			if ( item instanceof AgentDisplay ) {
				MenuItem displayMenu = new MenuItem(menu, SWT.CASCADE);
				displayMenu.setText(item.getMenuName());
				displayMenu.setImage(images.get(item.getClass()));
				Menu agentsMenu = new Menu(displayMenu);
				Set<IAgent> agents = ((AgentDisplay) item).getAgentsToDisplay();
				for ( IAgent agent : agents ) {
					MenuItem agentItem = new MenuItem(agentsMenu, SWT.PUSH);
					agentItem.setData("agent", agent);
					agentItem.setData("display", item);
					agentItem.setData("surface", displaySurface);
					agentItem.setText(agent.getName());
					agentItem.addSelectionListener(adapter);
					// agentItem.setImage(agentImage);
				}
				displayMenu.setMenu(agentsMenu);
			}

		}
	}

}
