package ummisco.gama.ui.factories;

import java.util.Collection;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import msi.gama.metamodel.agent.IAgent;
import ummisco.gama.ui.menus.AgentsMenu;
import ummisco.gama.ui.menus.MenuAction;

public class AgentMenuFactory extends AbstractServiceFactory implements ummisco.gama.ui.interfaces.IAgentMenuFactory {

	@Override
	public void fillPopulationSubMenu(final Menu menu, final Collection<? extends IAgent> species,
			final MenuAction... actions) {
		AgentsMenu.fillPopulationSubMenu(menu, species, actions);

	}

	@Override
	public Object create(final Class serviceInterface, final IServiceLocator parentLocator,
			final IServiceLocator locator) {
		return this;
	}

}
