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
package msi.gama.gui.swt.commands;

import java.util.*;
import java.util.List;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.*;
import msi.gama.gui.swt.*;
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.outputs.InspectDisplayOutput;
import msi.gama.runtime.*;
import msi.gama.util.GAML;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.statements.*;
import msi.gaml.types.Types;

public class AgentsMenu extends ContributionItem {

	public static MenuItem separate(final Menu parent) {
		return new MenuItem(parent, SWT.SEPARATOR);
	}

	public static MenuItem separate(final Menu parent, final String s) {
		MenuItem string = new MenuItem(parent, SWT.PUSH);
		string.setEnabled(false);
		string.setText(s);
		return string;
	}

	public static MenuItem cascadingAgentMenuItem(final Menu parent, final IAgent agent, final ILocation userLocation,
		final String title, final MenuAction ... actions) {
		MenuItem result = new MenuItem(parent, SWT.CASCADE);
		result.setText(title);
		result.setImage(IGamaIcons.MENU_AGENT.image());
		Menu agentMenu = new Menu(result);
		result.setMenu(agentMenu);
		createMenuForAgent(agentMenu, agent, userLocation, false, actions);
		return result;
	}

	private static MenuItem actionAgentMenuItem(final Menu parent, final IAgent agent, final SelectionListener listener,
		final Image image, final String prefix) {
		MenuItem result = new MenuItem(parent, SWT.PUSH);
		result.setText(prefix /* + " " + agent.getName() */);
		result.addSelectionListener(listener);
		result.setImage(image);
		result.setData("agent", agent);
		return result;
	}

	private static MenuItem browsePopulationMenuItem(final Menu parent, final Collection<IAgent> pop,
		final Image image) {
		MenuItem result = new MenuItem(parent, SWT.PUSH);
		if ( pop instanceof IPopulation ) {
			result.setText("Browse " + ((IPopulation) pop).getName() + " population...");
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
		final IPopulation population, final ILocation userLocation, final Image image) {
		MenuItem result = new MenuItem(parent, SWT.CASCADE);
		result.setText("Population of " + population.getName());
		result.setImage(image);
		Menu agentsMenu = new Menu(result);
		result.setMenu(agentsMenu);
		fillPopulationSubMenu(agentsMenu, population, userLocation);
		return result;
	}

	private static MenuItem actionAgentMenuItem(final Menu parent, final IAgent agent, final IStatement command,
		final ILocation point, final String prefix) {
		MenuItem result = new MenuItem(parent, SWT.PUSH);
		result.setText(prefix + " " + command.getName());
		result.setImage(IGamaIcons.MENU_RUN_ACTION.image());
		result.addSelectionListener(runner);
		result.setData("agent", agent);
		result.setData("location", point);
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
			if ( a != null && !a.dead() ) {
				a.getScope().getGui().setSelectedAgent(a);
			}
		}
	};

	private static SelectionAdapter highlighter = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final MenuItem mi = (MenuItem) e.widget;
			final IAgent a = (IAgent) mi.getData("agent");
			if ( a != null && !a.dead() ) {
				a.getScope().getGui().setHighlightedAgent(a);
				GAMA.getExperiment().refreshAllOutputs();
			}
		}
	};

	public static class Focuser extends SelectionAdapter {

		final IDisplaySurface surface;

		public Focuser(final IDisplaySurface s) {
			surface = s;
		}

		@Override
		public void widgetSelected(final SelectionEvent e) {
			if ( surface == null ) { return; }
			final MenuItem mi = (MenuItem) e.widget;
			final IAgent a = (IAgent) mi.getData("agent");
			if ( a != null && !a.dead() ) {
				surface.focusOn(a);
				GAMA.getExperiment().refreshAllOutputs();
			}
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
			if ( a != null && !a.dead() ) {
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
			if ( c != null && a != null && !a.dead() ) {
				final GamaHelper action = new GamaHelper() {

					@Override
					public Object run(final IScope scope) {
						// if ( p != null ) {
						// scope.addVarWithValue(IKeyword.USER_LOCATION, p);
						// }
						Object[] result = new Object[1];
						Arguments args = new Arguments();
						if ( p != null ) {
							args.put(IKeyword.USER_LOCATION, GAML.getExpressionFactory().createConst(p, Types.POINT));
						}
						scope.execute(c, a, args, result);
						GAMA.getExperiment().refreshAllOutputs();
						return result[0];
					}

				};
				GAMA.getExperiment().getAgent().getSimulationsScheduler().executeOneAction(action);
			}
		}
	};

	@Override
	public boolean isDynamic() {
		return true;
	}

	public static class MenuAction {

		SelectionListener listener;
		Image image;
		String text;

		public MenuAction(final SelectionListener listener, final Image image, final String text) {
			super();
			this.listener = listener;
			this.image = image;
			this.text = text;
		}

	}

	public static void createMenuForAgent(final Menu menu, final IAgent agent, final ILocation userLocation,
		final boolean topLevel, final MenuAction ... actions) {
		if ( agent == null ) { return; }
		separate(menu, "Actions");
		if ( topLevel ) {
			browsePopulationMenuItem(menu, agent.getPopulation(), IGamaIcons.MENU_BROWSE.image());
		}
		actionAgentMenuItem(menu, agent, inspector, IGamaIcons.MENU_INSPECT.image(), "Inspect");
		if ( !topLevel ) {
			actionAgentMenuItem(menu, agent, highlighter, IGamaIcons.MENU_HIGHLIGHT.image(), "Highlight");
			if ( SwtGui.getFirstDisplaySurface() != null && actions == null || actions.length == 0 ) {
				actionAgentMenuItem(menu, agent, new Focuser(SwtGui.getFirstDisplaySurface()),
					IGamaIcons.MENU_FOCUS.image(), "Focus");
			}
		}
		if ( actions != null ) {
			for ( MenuAction ma : actions ) {
				actionAgentMenuItem(menu, agent, ma.listener, ma.image, ma.text);
			}
		}
		final Collection<UserCommandStatement> commands = agent.getSpecies().getUserCommands();
		if ( !commands.isEmpty() ) {
			separate(menu);
			for ( final UserCommandStatement c : commands ) {
				actionAgentMenuItem(menu, agent, c, userLocation, "Apply");
			}
		}

		if ( !topLevel ) {
			separate(menu);
			actionAgentMenuItem(menu, agent, killer, IGamaIcons.MENU_KILL.image(), "Kill");
		}
		if ( agent instanceof IMacroAgent ) {
			final IMacroAgent macro = (IMacroAgent) agent;
			if ( macro.hasMembers() ) {
				separate(menu);
				separate(menu, "Micro-populations");
				for ( final IPopulation pop : macro.getMicroPopulations() ) {
					if ( !pop.isEmpty() ) {
						cascadingPopulationMenuItem(menu, agent, pop, userLocation, IGamaIcons.MENU_POPULATION.image());
					}
				}
			}
		}

	}

	public static void fillPopulationSubMenu(final Menu menu, final Collection<IAgent> species,
		final ILocation userLocation, final MenuAction ... actions) {

		int subMenuSize = GamaPreferences.CORE_MENU_SIZE.getValue();
		if ( subMenuSize < 2 ) {
			subMenuSize = 2;
		}
		separate(menu, "Actions");
		browsePopulationMenuItem(menu, species, IGamaIcons.MENU_BROWSE.image());

		final List<IAgent> agents = new ArrayList(species);
		final int size = agents.size();
		if ( size != 0 ) {
			separate(menu);
			separate(menu, "Agents");
		}
		if ( size < subMenuSize ) {
			for ( final IAgent agent : agents ) {
				cascadingAgentMenuItem(menu, agent, userLocation, agent.getName(), actions);
			}
		} else {
			final int nb = size / subMenuSize + 1;
			for ( int i = 0; i < nb; i++ ) {
				final int begin = i * subMenuSize;
				final int end = Math.min((i + 1) * subMenuSize, size);
				if ( begin >= end ) {
					break;
				}
				final MenuItem rangeItem = new MenuItem(menu, SWT.CASCADE);
				final Menu rangeMenu = new Menu(rangeItem);
				rangeItem.setMenu(rangeMenu);
				rangeItem.setText("" + begin + " to " + (end - 1));
				rangeItem.setImage(IGamaIcons.MENU_POPULATION.image());
				rangeMenu.addListener(SWT.Show, new Listener() {

					@Override
					public void handleEvent(final Event e) {
						if ( !menu.isVisible() ) { return; }
						MenuItem[] items = rangeMenu.getItems();
						for ( MenuItem item : items ) {
							item.dispose();
						}
						for ( int j = begin; j < end; j++ ) {
							IAgent ag = agents.get(j);
							if ( ag != null && !ag.dead() ) {
								cascadingAgentMenuItem(rangeMenu, ag, userLocation, ag.getName(), actions);
							}
						}
					}
				});

			}
		}
	}

	@Override
	public void fill(final Menu parent, final int index) {
		createMenuForAgent(parent, GAMA.getSimulation(), null, true);
	}
}
