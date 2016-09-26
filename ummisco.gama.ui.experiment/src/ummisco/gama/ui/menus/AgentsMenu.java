/*********************************************************************************************
 *
 *
 * 'AgentsMenu.java', in plugin 'msi.gama.application', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package ummisco.gama.ui.menus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IDisplaySurface;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.kernel.simulation.SimulationPopulation;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.outputs.InspectDisplayOutput;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gaml.operators.fastmaths.CmnFastMath;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.IExecutable;
import msi.gaml.statements.IStatement;
import msi.gaml.statements.UserCommandStatement;
import ummisco.gama.ui.resources.GamaColors;
import ummisco.gama.ui.resources.GamaIcons;
import ummisco.gama.ui.resources.IGamaIcons;
import ummisco.gama.ui.utils.SwtGui;

public class AgentsMenu extends ContributionItem {

	public static MenuItem cascadingAgentMenuItem(final Menu parent, final IAgent agent, final String title,
			final MenuAction... actions) {
		final MenuItem result = new MenuItem(parent, SWT.CASCADE);
		result.setText(title);
		Image image;
		if (agent instanceof SimulationAgent) {
			final SimulationAgent sim = (SimulationAgent) agent;
			image = GamaIcons.createTempRoundColorIcon(GamaColors.get(sim.getColor()));
		} else {
			image = GamaIcons.create(IGamaIcons.MENU_AGENT).image();
		}
		result.setImage(image);
		final Menu agentMenu = new Menu(result);
		result.setMenu(agentMenu);
		createMenuForAgent(agentMenu, agent, agent instanceof ITopLevelAgent, actions);
		return result;
	}

	private static MenuItem actionAgentMenuItem(final Menu parent, final IAgent agent, final SelectionListener listener,
			final Image image, final String prefix) {
		final MenuItem result = new MenuItem(parent, SWT.PUSH);
		result.setText(prefix /* + " " + agent.getName() */);
		result.addSelectionListener(listener);
		result.setImage(image);
		result.setData("agent", agent);
		return result;
	}

	private static MenuItem browsePopulationMenuItem(final Menu parent, final Collection<IAgent> pop,
			final Image image) {
		final MenuItem result = new MenuItem(parent, SWT.PUSH);
		if (pop instanceof IPopulation) {
			if (pop instanceof SimulationPopulation) {
				result.setText("Browse simulations...");
				GamaMenu.separate(parent);
			} else {
				result.setText("Browse " + ((IPopulation) pop).getName() + " population...");
			}
		} else {
			result.setText("Browse agents...");
		}
		result.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				InspectDisplayOutput.browse(pop);
			}

		});
		result.setImage(image);
		return result;
	}

	private static MenuItem cascadingPopulationMenuItem(final Menu parent, final IAgent agent,
			final IPopulation population, final Image image, final MenuAction... actions) {
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

	private static MenuItem actionAgentMenuItem(final Menu parent, final IAgent agent, final IStatement command,
			final String prefix) {
		final MenuItem result = new MenuItem(parent, SWT.PUSH);
		result.setText(prefix + " " + command.getName());
		result.setImage(GamaIcons.create(IGamaIcons.MENU_RUN_ACTION).image());
		result.addSelectionListener(runner);
		result.setData("agent", agent);
		result.setData("command", command);
		return result;
	}

	public AgentsMenu(final String id) {
		super(id);
	}

	public AgentsMenu() {
		super();
	}

	private static SelectionAdapter inspector = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final MenuItem mi = (MenuItem) e.widget;
			final IAgent a = (IAgent) mi.getData("agent");
			if (a != null && !a.dead()) {
				a.getScope().getGui().setSelectedAgent(a);
			}
		}
	};

	public static SelectionAdapter highlighter = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final MenuItem mi = (MenuItem) e.widget;
			final IAgent a = (IAgent) mi.getData("agent");
			if (a != null && !a.dead()) {
				a.getScope().getGui().setHighlightedAgent(a);
				GAMA.getExperiment().refreshAllOutputs();
			}
		}
	};

	public static class Focuser extends SelectionAdapter {

		public Focuser() {
		}

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final List<IDisplaySurface> surfaces = SwtGui.allDisplaySurfaces();
			final MenuItem mi = (MenuItem) e.widget;
			final IAgent a = (IAgent) mi.getData("agent");
			for (final IDisplaySurface surface : surfaces)
				if (a instanceof ITopLevelAgent) {
					surface.zoomFit();
				} else if (a != null && !a.dead()) {
					surface.focusOn(a);
				}
			GAMA.getExperiment().refreshAllOutputs();
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

	private static SelectionAdapter runner = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final MenuItem source = (MenuItem) e.widget;
			final IAgent a = (IAgent) source.getData("agent");
			final IStatement c = (IStatement) source.getData("command");
			final ILocation p = (ILocation) source.getData("location");

			// We run into the scope provided by the simulation to which this
			// agent belongs

			if (c != null && a != null && !a.dead()) {
				final IScope runningScope = a.getScope();
				runningScope.getSimulation().executeAction(new IExecutable() {

					@Override
					public Object executeOn(final IScope scope) {
						final Object[] result = new Object[1];
						final Arguments args = new Arguments();
						scope.execute(c, a, args, result);
						GAMA.getExperiment().refreshAllOutputs();
						return result[0];
					}

				});

			}
		}
	};

	@Override
	public boolean isDynamic() {
		return true;
	}

	public static void createMenuForAgent(final Menu menu, final IAgent agent, final boolean topLevel,
			final MenuAction... actions) {
		if (agent == null) {
			return;
		}
		GamaMenu.separate(menu, "Actions");
		actionAgentMenuItem(menu, agent, inspector, GamaIcons.create(IGamaIcons.MENU_INSPECT).image(),
				"Inspect" + (topLevel ? " experiment" : ""));
		if (!topLevel) {

			actionAgentMenuItem(menu, agent, new Focuser(), GamaIcons.create(IGamaIcons.MENU_FOCUS).image(),
					"Focus on all displays");
		}
		if (actions != null && !topLevel) {
			for (final MenuAction ma : actions) {
				actionAgentMenuItem(menu, agent, ma.listener, ma.image, ma.text);
			}
		}
		final Collection<UserCommandStatement> commands = agent.getSpecies().getUserCommands();
		if (!commands.isEmpty()) {
			GamaMenu.separate(menu);
			for (final UserCommandStatement c : commands) {
				actionAgentMenuItem(menu, agent, c, "Apply");
			}
		}

		if (!topLevel) {
			GamaMenu.separate(menu);
			actionAgentMenuItem(menu, agent, killer, GamaIcons.create(IGamaIcons.MENU_KILL).image(), "Kill");
		}
		if (agent instanceof IMacroAgent) {
			final IMacroAgent macro = (IMacroAgent) agent;
			if (macro.hasMembers()) {
				GamaMenu.separate(menu);
				if (!topLevel) {
					GamaMenu.separate(menu, "Micro-populations");
				}
				for (final IPopulation pop : macro.getMicroPopulations()) {
					if (!pop.isEmpty()) {
						cascadingPopulationMenuItem(menu, agent, pop,
								GamaIcons.create(IGamaIcons.MENU_POPULATION).image(), actions);
					}
				}
			}
		}

	}

	public static void fillPopulationSubMenu(final Menu menu, final Collection<IAgent> species,
			final MenuAction... actions) {
		final boolean isSimulations = species instanceof SimulationPopulation;
		int subMenuSize = GamaPreferences.CORE_MENU_SIZE.getValue();
		if (subMenuSize < 2) {
			subMenuSize = 2;
		}
		final List<IAgent> agents = new ArrayList(species);
		final int size = agents.size();
		if (size > 1 && !isSimulations) {
			GamaMenu.separate(menu, "Actions");
		}

		if (size > 1)
			browsePopulationMenuItem(menu, species, GamaIcons.create(IGamaIcons.MENU_BROWSE).image());

		if (size > 1 && !isSimulations) {
			GamaMenu.separate(menu);
			GamaMenu.separate(menu, "Agents");
		}
		if (size < subMenuSize) {
			for (final IAgent agent : agents) {
				cascadingAgentMenuItem(menu, agent, agent.getName(), actions);
			}
		} else {
			final int nb = size / subMenuSize + 1;
			for (int i = 0; i < nb; i++) {
				final int begin = i * subMenuSize;
				final int end = CmnFastMath.min((i + 1) * subMenuSize, size);
				if (begin >= end) {
					break;
				}
				final MenuItem rangeItem = new MenuItem(menu, SWT.CASCADE);
				final Menu rangeMenu = new Menu(rangeItem);
				rangeItem.setMenu(rangeMenu);
				rangeItem.setText("" + begin + " to " + (end - 1));
				rangeItem.setImage(GamaIcons.create(IGamaIcons.MENU_POPULATION).image());
				rangeMenu.addListener(SWT.Show, new Listener() {

					@Override
					public void handleEvent(final Event e) {
						if (!menu.isVisible()) {
							return;
						}
						final MenuItem[] items = rangeMenu.getItems();
						for (final MenuItem item : items) {
							item.dispose();
						}
						for (int j = begin; j < end; j++) {
							final IAgent ag = agents.get(j);
							if (ag != null && !ag.dead()) {
								cascadingAgentMenuItem(rangeMenu, ag, ag.getName(), actions);
							}
						}
					}
				});

			}
		}
	}

	public static MenuAction HIGHLIGHT_ACTION = new MenuAction(highlighter,
			GamaIcons.create(IGamaIcons.MENU_HIGHLIGHT).image(), "Highlight");

	@Override
	public void fill(final Menu parent, final int index) {
		createMenuForAgent(parent, GAMA.getExperiment().getAgent(), true, HIGHLIGHT_ACTION);
	}
}
