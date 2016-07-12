package ummisco.gama.ui.interfaces;

import java.util.Collection;

import org.eclipse.swt.widgets.Menu;

import msi.gama.metamodel.agent.IAgent;
import ummisco.gama.ui.menus.MenuAction;

public interface IAgentMenuFactory {

	void fillPopulationSubMenu(final Menu menu, final Collection<IAgent> species, final MenuAction... actions);
}