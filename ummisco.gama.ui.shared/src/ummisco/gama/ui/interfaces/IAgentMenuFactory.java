/*******************************************************************************************************
 *
 * IAgentMenuFactory.java, in ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.ui.interfaces;

import java.util.Collection;

import org.eclipse.swt.widgets.Menu;

import msi.gama.metamodel.agent.IAgent;
import ummisco.gama.ui.menus.MenuAction;

/**
 * A factory for creating IAgentMenu objects.
 */
public interface IAgentMenuFactory {

	/**
	 * Fill population sub menu.
	 *
	 * @param menu the menu
	 * @param species the species
	 * @param actions the actions
	 */
	void fillPopulationSubMenu(final Menu menu, final Collection<? extends IAgent> species,
			final MenuAction... actions);
}