package ummisco.gama.ui.factories;

import java.util.Collection;

import org.eclipse.swt.widgets.Menu;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;
import ummisco.gama.ui.menus.AgentsMenu;
import ummisco.gama.ui.menus.MenuAction;

public class AgentMenuFactory implements ummisco.gama.ui.utils.SwtGui.IAgentMenuFactory {

	@Override
	public void fillPopulationSubMenu(final Menu menu, final Collection<IAgent> species, final ILocation userLocation,
			final MenuAction... actions) {
		AgentsMenu.fillPopulationSubMenu(menu, species, userLocation, actions);

	}

}
