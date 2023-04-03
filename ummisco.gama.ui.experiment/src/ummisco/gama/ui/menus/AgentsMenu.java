/*******************************************************************************************************
 *
 * AgentsMenu.java, in ummisco.gama.ui.experiment, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.ui.menus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import msi.gama.common.interfaces.IGui;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.kernel.simulation.SimulationPopulation;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.outputs.ValuedDisplayOutputFactory;
import msi.gama.runtime.ExecutionResult;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.PlatformHelper;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.IStatement;
import msi.gaml.statements.UserCommandStatement;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaIcon;
import ummisco.gama.ui.resources.IGamaIcons;

/**
 * The Class AgentsMenu.
 */
public class AgentsMenu extends ContributionItem {

	/**
	 * Cascading agent menu item.
	 *
	 * @param parent
	 *            the parent
	 * @param agent
	 *            the agent
	 * @param title
	 *            the title
	 * @param actions
	 *            the actions
	 * @return the menu item
	 */
	public static MenuItem cascadingAgentMenuItem(final Menu parent, final IAgent agent, final String title,
			final MenuAction... actions) {
		final MenuItem result = new MenuItem(parent, SWT.CASCADE);
		result.setText(title);
		Image image;
		if (agent instanceof SimulationAgent sim) {
			image = GamaIcon.ofColor(GamaColors.get(sim.getColor()), false).image();
		} else {
			image = GamaIcon.named(IGamaIcons.MENU_AGENT).image();
		}
		result.setImage(image);
		final Menu agentMenu = new Menu(result);
		result.setMenu(agentMenu);
		createMenuForAgent(agentMenu, agent, agent instanceof ITopLevelAgent, true, actions);
		return result;
	}

	/**
	 * Action agent menu item.
	 *
	 * @param parent
	 *            the parent
	 * @param agent
	 *            the agent
	 * @param listener
	 *            the listener
	 * @param image
	 *            the image
	 * @param prefix
	 *            the prefix
	 * @return the menu item
	 */
	private static MenuItem actionAgentMenuItem(final Menu parent, final IAgent agent, final SelectionListener listener,
			final Image image, final String prefix) {
		final MenuItem result = new MenuItem(parent, SWT.PUSH);
		result.setText(prefix /* + " " + agent.getName() */);
		result.addSelectionListener(listener);
		result.setImage(image);
		result.setData("agent", agent);
		return result;
	}

	/**
	 * Browse population menu item.
	 *
	 * @param parent
	 *            the parent
	 * @param pop
	 *            the pop
	 * @param image
	 *            the image
	 * @return the menu item
	 */
	private static MenuItem browsePopulationMenuItem(final Menu parent, final Collection<? extends IAgent> pop,
			final Image image) {
		final MenuItem result = new MenuItem(parent, SWT.PUSH);
		if (pop instanceof IPopulation) {
			if (pop instanceof SimulationPopulation) {
				result.setText("Browse simulations...");
				GamaMenu.separate(parent);
			} else {
				result.setText("Browse " + ((IPopulation<? extends IAgent>) pop).getName() + " population...");
			}
		} else {
			result.setText("Browse agents...");
		}
		result.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (pop instanceof SimulationPopulation) {
					ValuedDisplayOutputFactory.browseSimulations(((SimulationPopulation) pop).getHost());
				} else {
					ValuedDisplayOutputFactory.browse(pop);
				}
			}

		});
		result.setImage(image);
		return result;
	}

	/**
	 * Cascading population menu item.
	 *
	 * @param parent
	 *            the parent
	 * @param agent
	 *            the agent
	 * @param population
	 *            the population
	 * @param image
	 *            the image
	 * @param actions
	 *            the actions
	 * @return the menu item
	 */
	private static MenuItem cascadingPopulationMenuItem(final Menu parent, final IAgent agent,
			final IPopulation<? extends IAgent> population, final Image image, final MenuAction... actions) {
		if (population instanceof SimulationPopulation) {
			fillPopulationSubMenu(parent, population, actions);
			return null;
		}
		final MenuItem result = new MenuItem(parent, SWT.CASCADE);
		// if ( population instanceof SimulationPopulation ) {
		// result.setText("Simulations");
		// } else {
		result.setText("Population of " + population.getName());
		// }
		result.setImage(image);
		final Menu agentsMenu = new Menu(result);
		result.setMenu(agentsMenu);
		fillPopulationSubMenu(agentsMenu, population, actions);
		return result;
	}

	/**
	 * Action agent menu item.
	 *
	 * @param parent
	 *            the parent
	 * @param agent
	 *            the agent
	 * @param command
	 *            the command
	 * @param prefix
	 *            the prefix
	 * @return the menu item
	 */
	private static MenuItem actionAgentMenuItem(final Menu parent, final IAgent agent, final IStatement command,
			final String prefix) {
		final MenuItem result = new MenuItem(parent, SWT.PUSH);
		result.setText(prefix + " " + command.getName());
		result.setImage(GamaIcon.named(IGamaIcons.MENU_RUN_ACTION).image());
		result.addSelectionListener(runner);
		result.setData("agent", agent);
		result.setData("command", command);
		return result;
	}

	/**
	 * Instantiates a new agents menu.
	 *
	 * @param id
	 *            the id
	 */
	public AgentsMenu(final String id) { // NO_UCD (unused code)
		super(id);
	}

	/**
	 * Instantiates a new agents menu.
	 */
	public AgentsMenu() {}

	/** The inspector. */
	private static SelectionAdapter inspector = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final MenuItem mi = (MenuItem) e.widget;
			final IAgent a = (IAgent) mi.getData("agent");
			if (a != null && !a.dead()) { a.getScope().getGui().setSelectedAgent(a); }
		}
	};

	/** The highlighter. */
	public static SelectionAdapter highlighter = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final MenuItem mi = (MenuItem) e.widget;
			final IAgent a = (IAgent) mi.getData("agent");
			if (a != null && !a.dead()) {
				final IGui gui = a.getScope().getGui();
				if (gui.getHighlightedAgent() != a) {
					gui.setHighlightedAgent(a);
				} else {
					gui.setHighlightedAgent(null);
				}
				GAMA.getExperiment().refreshAllOutputs();
			}
		}
	};

	/**
	 * The Class Focuser.
	 */
	public static class Focuser extends SelectionAdapter {

		/**
		 * Instantiates a new focuser.
		 */
		public Focuser() {}

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final MenuItem mi = (MenuItem) e.widget;
			final IAgent a = (IAgent) mi.getData("agent");
			if (a != null && !a.dead()) { GAMA.getGui().setFocusOn(a); }
		}
	}

	// private static SelectionAdapter focuser = new SelectionAdapter() {
	//
	// @Override
	// public void widgetSelected(final SelectionEvent e) {
	// final MenuItem mi = (MenuItem) e.widget;
	// final IAgent a = (IAgent) mi.getData("agent");
	// if ( a != null && !a.dead() ) {
	// scope.getGui().getFirstDisplaySurface().focusOn(a);
	// GAMA.getExperiment().getSimulationOutputs().forceUpdateOutputs();
	// }
	// }
	//
	// };

	/** The killer. */
	private static SelectionAdapter killer = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final MenuItem mi = (MenuItem) e.widget;
			final IAgent a = (IAgent) mi.getData("agent");
			if (a != null && !a.dead()) {
				a.dispose();
				GAMA.getExperiment().refreshAllOutputs();
			}
		}
	};

	/** The runner. */
	private static SelectionAdapter runner = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final MenuItem source = (MenuItem) e.widget;
			final IAgent a = (IAgent) source.getData("agent");
			final IStatement c = (IStatement) source.getData("command");
			// final GamaPoint p = (GamaPoint) source.getData("location");

			// We run into the scope provided by the simulation to which this
			// agent belongs

			if (c != null && a != null && !a.dead()) {
				final IScope runningScope = a.getScope();
				runningScope.getSimulation().executeAction(scope -> {
					final Arguments args = new Arguments();
					final ExecutionResult result = scope.execute(c, a, args);
					GAMA.getExperiment().refreshAllOutputs();
					return result.getValue();
				});

			}
		}
	};

	@Override
	public boolean isDynamic() { return true; }

	/**
	 * Creates the menu for agent.
	 *
	 * @param menu
	 *            the menu
	 * @param agent
	 *            the agent
	 * @param topLevel
	 *            the top level
	 * @param withInspect
	 *            the with inspect
	 * @param actions
	 *            the actions
	 */
	public static void createMenuForAgent(final Menu menu, final IAgent agent, final boolean topLevel,
			final boolean withInspect, final MenuAction... actions) {
		if (agent == null) return;
		GamaMenu.separate(menu, "Actions");
		final boolean simulation = agent instanceof SimulationAgent;
		if (withInspect) {
			actionAgentMenuItem(menu, agent, inspector, GamaIcon.named(IGamaIcons.MENU_INSPECT).image(),
					"Inspect" + (topLevel ? simulation ? " simulation" : " experiment" : ""));
		}
		if (!topLevel) {
			actionAgentMenuItem(menu, agent, new Focuser(), GamaIcon.named(IGamaIcons.MENU_FOCUS).image(),
					"Focus on all displays");
			actionAgentMenuItem(menu, agent, highlighter, GamaIcon.named(IGamaIcons.MENU_HIGHLIGHT).image(),
					agent.getScope().getGui().getHighlightedAgent() == agent ? "Remove highlight" : "Highlight");
		}
		if (actions != null && !topLevel) {
			for (final MenuAction ma : actions) {
				if (ma != null) { actionAgentMenuItem(menu, agent, ma.listener, ma.image, ma.text); }
			}
		}
		final Collection<UserCommandStatement> commands = agent.getSpecies().getUserCommands();
		if (!commands.isEmpty()) {
			GamaMenu.separate(menu);
			for (final UserCommandStatement c : commands) { actionAgentMenuItem(menu, agent, c, "Apply"); }
		}

		if (!topLevel) {
			GamaMenu.separate(menu);
			actionAgentMenuItem(menu, agent, killer, GamaIcon.named(IGamaIcons.MENU_KILL).image(), "Kill");
		}
		if (agent instanceof IMacroAgent macro && macro.hasMembers()) {
			GamaMenu.separate(menu);
			if (!topLevel) { GamaMenu.separate(menu, "Micro-populations"); }
			for (final IPopulation<? extends IAgent> pop : macro.getMicroPopulations()) {
				if (!pop.isEmpty()) {
					cascadingPopulationMenuItem(menu, agent, pop, GamaIcon.named(IGamaIcons.MENU_POPULATION).image(),
							actions);
				}
			}
		}

	}

	/**
	 * Fill population sub menu.
	 *
	 * @param menu
	 *            the menu
	 * @param species
	 *            the species
	 * @param actions
	 *            the actions
	 */
	public static void fillPopulationSubMenu(final Menu menu, final Collection<? extends IAgent> species,
			final MenuAction... actions) {
		final boolean isSimulations = species instanceof SimulationPopulation;
		int subMenuSize = Math.max(2, GamaPreferences.Interface.CORE_MENU_SIZE.getValue());
		final List<IAgent> agents = new ArrayList<>(species);
		final int size = agents.size();
		// if (size > 1 && !isSimulations) { GamaMenu.separate(menu, "Actions"); }
		if (size >= 1) { browsePopulationMenuItem(menu, species, GamaIcon.named(IGamaIcons.MENU_BROWSE).image()); }
		if (size >= 1 && !isSimulations) {
			GamaMenu.separate(menu);
			GamaMenu.separate(menu, "Agents");
		}
		if (size < subMenuSize) {
			for (final IAgent agent : agents) {
				if (agent != null) { cascadingAgentMenuItem(menu, agent, agent.getName(), actions); }
			}
		} else {
			int nb = size / subMenuSize + 1;
			// See Issue #2967
			if (PlatformHelper.isWindows() && nb > 90) {
				// Absolutely no idea about the reality of this hard-coded limit
				nb = 90;
				subMenuSize = size / nb;
			}
			for (int i = 0; i < nb; i++) {
				final int begin = i * subMenuSize;
				final int end = Math.min((i + 1) * subMenuSize, size);
				if (begin >= end) { break; }
				try {
					final MenuItem rangeItem = new MenuItem(menu, SWT.CASCADE);
					final Menu rangeMenu = new Menu(rangeItem);
					rangeItem.setMenu(rangeMenu);
					rangeItem.setText("From " + agents.get(begin).getName() + " to " + agents.get(end - 1).getName());
					rangeItem.setImage(GamaIcon.named(IGamaIcons.MENU_POPULATION).image());
					rangeMenu.addListener(SWT.Show, e -> {
						if (!menu.isVisible()) return;
						final MenuItem[] items = rangeMenu.getItems();
						for (final MenuItem item : items) { item.dispose(); }
						for (int j = begin; j < end; j++) {
							final IAgent ag = agents.get(j);
							if (ag != null && !ag.dead()) {
								cascadingAgentMenuItem(rangeMenu, ag, ag.getName(), actions);
							}
						}
					});
				} catch (SWTError e) {
					if (e.code == SWT.ERROR_ITEM_NOT_ADDED) { continue; }
				}

			}
		}
	}

	/**
	 * Gets the highlight action for.
	 *
	 * @param a
	 *            the a
	 * @return the highlight action for
	 */
	public static MenuAction getHighlightActionFor(final IAgent a) {
		if (a == null) return null;
		return new MenuAction(highlighter, GamaIcon.named(IGamaIcons.MENU_HIGHLIGHT).image(),
				a.getScope().getGui().getHighlightedAgent() == a ? "Remove highlight" : "Highlight");
	}

	@Override
	public void fill(final Menu parent, final int index) {
		createMenuForAgent(parent, GAMA.getExperiment().getAgent(), true, true);
	}
}
