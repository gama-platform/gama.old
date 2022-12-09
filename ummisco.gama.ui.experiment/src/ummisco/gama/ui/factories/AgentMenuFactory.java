/*******************************************************************************************************
 *
 * AgentMenuFactory.java, in ummisco.gama.ui.experiment, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.factories;

import java.util.Collection;

import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.services.AbstractServiceFactory;
import org.eclipse.ui.services.IServiceLocator;

import msi.gama.metamodel.agent.IAgent;
import ummisco.gama.ui.menus.AgentsMenu;
import ummisco.gama.ui.menus.MenuAction;

/**
 * A factory for creating AgentMenu objects.
 */
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
