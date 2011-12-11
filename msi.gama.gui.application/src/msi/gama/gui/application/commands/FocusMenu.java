/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.application.commands;

import java.util.Set;
import msi.gama.gui.application.GUI;
import msi.gama.gui.application.views.LayeredDisplayView;
import msi.gama.gui.displays.*;
import msi.gama.gui.graphics.DisplayManager.DisplayItem;
import msi.gama.gui.graphics.*;
import msi.gama.interfaces.IAgent;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

public class FocusMenu extends ContributionItem {

	public FocusMenu() {
		// TODO Auto-generated constructor stub
	}

	public FocusMenu(final String id) {
		super(id);
		// TODO Auto-generated constructor stub
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
		LayeredDisplayView view = (LayeredDisplayView) GUI.getPage().getActivePart();
		final IDisplaySurface displaySurface = view.getDisplaySurface();
		for ( final DisplayItem item : displaySurface.getManager().getItems() ) {
			if ( item.display instanceof AgentDisplay ) {
				MenuItem displayMenu = new MenuItem(menu, SWT.CASCADE);
				displayMenu.setText(item.display.getMenuName());
				displayMenu.setImage(item.display.getMenuImage());
				Menu agentsMenu = new Menu(displayMenu);
				Set<IAgent> agents = ((AgentDisplay) item.display).getAgentsToDisplay();
				for ( IAgent agent : agents ) {
					MenuItem agentItem = new MenuItem(agentsMenu, SWT.PUSH);
					agentItem.setData("agent", agent);
					agentItem.setData("display", item.display);
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
