/*********************************************************************************************
 *
 * 'IAgentMenuFactory.java, in plugin ummisco.gama.ui.shared, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.ui.interfaces;

import java.util.Collection;

import org.eclipse.swt.widgets.Menu;

import msi.gama.metamodel.agent.IAgent;
import ummisco.gama.ui.menus.MenuAction;

public interface IAgentMenuFactory {

	void fillPopulationSubMenu(final Menu menu, final Collection<? extends IAgent> species,
			final MenuAction... actions);
}