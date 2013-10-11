/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.gui.swt.commands;

import java.util.*;
import java.util.List;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.gui.swt.IGamaIcons;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.outputs.InspectDisplayOutput;
import msi.gama.runtime.*;
import msi.gama.util.GamaList;
import msi.gaml.compilation.GamaHelper;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.*;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;

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

	public static MenuItem cascadingAgentMenuItem(final Menu parent, final IAgent agent, final MenuAction ... actions) {
		return cascadingAgentMenuItem(parent, agent, agent.getName(), actions);
	}

	public static MenuItem cascadingAgentMenuItem(final Menu parent, final IAgent agent, final String title,
		final MenuAction ... actions) {
		MenuItem result = new MenuItem(parent, SWT.CASCADE);
		result.setText(title);
		result.setImage(IGamaIcons.MENU_AGENT.image());
		Menu agentMenu = new Menu(result);
		result.setMenu(agentMenu);
		createMenuForAgent(agentMenu, agent, false, actions);
		return result;
	}

	static MenuItem actionAgentMenuItem(final Menu parent, final IAgent agent, final SelectionListener listener,
		final Image image, final String prefix) {
		MenuItem result = new MenuItem(parent, SWT.PUSH);
		result.setText(prefix /* + " " + agent.getName() */);
		result.addSelectionListener(listener);
		result.setImage(image);
		result.setData("agent", agent);
		return result;
	}

	static MenuItem browsePopulationMenuItem(final Menu parent, final Collection<IAgent> pop, final Image image) {
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

	static MenuItem cascadingPopulationMenuItem(final Menu parent, final IAgent agent, final IPopulation population,
		final Image image) {
		MenuItem result = new MenuItem(parent, SWT.CASCADE);
		result.setText("Population of " + population.getName());
		result.setImage(image);
		Menu agentsMenu = new Menu(result);
		result.setMenu(agentsMenu);
		fillPopulationSubMenu(agentsMenu, population);
		return result;
	}

	static MenuItem actionAgentMenuItem(final Menu parent, final IAgent agent, final IStatement command,
		final GamaPoint point, final String prefix) {
		MenuItem result = new MenuItem(parent, SWT.PUSH);
		result.setText(prefix + " " + command.getName());
		result.setImage(IGamaIcons.MENU_RUN_ACTION.image());
		result.addSelectionListener(runner);
		result.setData("agent", agent);
		result.setData("location", point);
		result.setData("command", command);
		return result;
	}

	public AgentsMenu() {}

	public AgentsMenu(final String id) {
		super(id);
	}

	private static SelectionAdapter inspector = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final MenuItem mi = (MenuItem) e.widget;
			final IAgent a = (IAgent) mi.getData("agent");
			if ( a != null && !a.dead() ) {
				GuiUtils.setSelectedAgent(a);
			}
		}
	};

	private static SelectionAdapter highlighter = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final MenuItem mi = (MenuItem) e.widget;
			final IAgent a = (IAgent) mi.getData("agent");
			if ( a != null && !a.dead() ) {
				GuiUtils.setHighlightedAgent(a);
				GAMA.getExperiment().getSimulationOutputs().forceUpdateOutputs();
			}
		}
	};

	private static SelectionAdapter killer = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			final MenuItem mi = (MenuItem) e.widget;
			final IAgent a = (IAgent) mi.getData("agent");
			if ( a != null && !a.dead() ) {
				a.dispose();
				GAMA.getExperiment().getSimulationOutputs().forceUpdateOutputs();
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
						if ( p != null ) {
							scope.addVarWithValue(IKeyword.USER_LOCATION, p);
						}
						Object[] result = new Object[1];
						scope.execute(c, a, null, result);
						return result[0];
					}

				};
				GAMA.getSimulation().getScheduler().executeOneAction(action);
			}
		}
	};

	@Override
	public boolean isDirty() {
		return true;
	}

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

	public static void createMenuForAgent(final Menu menu, final IAgent agent, final boolean topLevel,
		final MenuAction ... actions) {
		separate(menu, "Actions");
		if ( topLevel ) {
			browsePopulationMenuItem(menu, agent.getPopulation(), IGamaIcons.MENU_BROWSE.image());
		}
		actionAgentMenuItem(menu, agent, inspector, IGamaIcons.MENU_INSPECT.image(), "Inspect");
		if ( !topLevel ) {
			actionAgentMenuItem(menu, agent, highlighter, IGamaIcons.MENU_HIGHLIGHT.image(), "Highlight");
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
				actionAgentMenuItem(menu, agent, c, null, "Apply");
			}
		}
		separate(menu);
		actionAgentMenuItem(menu, agent, killer, IGamaIcons.MENU_KILL.image(), "Kill");
		if ( agent instanceof IMacroAgent ) {
			final IMacroAgent macro = (IMacroAgent) agent;
			if ( macro.hasMembers() ) {
				separate(menu);
				separate(menu, "Micro-populations");
				for ( final IPopulation pop : macro.getMicroPopulations() ) {
					if ( !pop.isEmpty() ) {
						cascadingPopulationMenuItem(menu, agent, pop, IGamaIcons.MENU_POPULATION.image());
					}
				}
			}
		}

	}

	private static void fillAgentsMenu(final Menu menu, final IPopulation species, final SelectionListener select) {
		if ( species == null ) {
			fill(menu, select);
			return;
		}
		final SelectionListener listener = select == null ? inspector : select;
		final IAgent[] agents = species.toArray(new IAgent[0]);
		final int size = agents.length;
		if ( size < 100 ) {
			for ( final IAgent agent : agents ) {
				final MenuItem agentItem = new MenuItem(menu, SWT.PUSH);
				agentItem.setData("agent", agent);
				agentItem.setText(agent.getName());
				agentItem.addSelectionListener(listener);
				agentItem.setImage(IGamaIcons.MENU_AGENT.image());
			}
		} else {
			final int nb = size / 100;
			for ( int i = 0; i < nb; i++ ) {
				final MenuItem rangeItem = new MenuItem(menu, SWT.CASCADE);
				final int begin = i * 100;
				final int end = Math.min((i + 1) * 100, size);
				rangeItem.setText("From " + begin + " to " + (end - 1));
				final Menu rangeMenu = new Menu(rangeItem);
				for ( int j = begin; j < end; j++ ) {
					final IAgent agent = agents[j];
					final MenuItem agentItem = new MenuItem(rangeMenu, SWT.PUSH);
					agentItem.setData("agent", agent);
					agentItem.setText(agent.getName());
					agentItem.addSelectionListener(listener);
					agentItem.setImage(IGamaIcons.MENU_AGENT.image());
				}
				rangeItem.setMenu(rangeMenu);
			}
		}
	}

	public static void fillPopulationSubMenu(final Menu menu, final Collection<IAgent> species,
		final MenuAction ... actions) {
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
				cascadingAgentMenuItem(menu, agent, actions);
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
					public void handleEvent(final Event event) {
						for ( int j = begin; j < end; j++ ) {
							cascadingAgentMenuItem(rangeMenu, agents.get(j), actions);
						}
					}
				});
			}
		}
	}

	public static Menu fillPopulationSubMenu(final MenuItem parent, final Collection<IAgent> species,
		final MenuAction ... actions) {
		final Menu agentsMenu = new Menu(parent);
		fillPopulationSubMenu(agentsMenu, species, actions);
		parent.setMenu(agentsMenu);
		return agentsMenu;
	}

	// TODO adapt this to multi-scale organization!!!
	public static Menu createSpeciesSubMenu(final Control parent, final IPopulation species,
		final SelectionListener select) {
		final Menu agentsMenu = new Menu(parent);
		fillAgentsMenu(agentsMenu, species, select);
		parent.setMenu(agentsMenu);
		return agentsMenu;
	}

	private static void populateComponents(final Menu parent, final IMacroAgent macro, final SelectionListener listener) {
		final MenuItem microAgentsItem = new MenuItem(parent, SWT.CASCADE);
		microAgentsItem.setText("Micro agents");

		final Menu microSpeciesMenu = new Menu(microAgentsItem);
		microAgentsItem.setMenu(microSpeciesMenu);

		IPopulation microPopulation;
		final List<ISpecies> microSpecies = macro.getSpecies().getMicroSpecies();
		final List<String> microSpeciesNames = new ArrayList();
		for ( final ISpecies spec : microSpecies ) {
			microSpeciesNames.add(spec.getName());
		}
		Collections.sort(microSpeciesNames);
		for ( final String microSpec : microSpeciesNames ) {
			microPopulation = macro.getMicroPopulation(microSpec);
			if ( microPopulation != null && microPopulation.size() > 0 ) {
				populateSpecies(microSpeciesMenu, microPopulation, false, listener);
			}
		}
	}

	private static void populateAgentContent(final Menu parent, final IMacroAgent agent,
		final SelectionListener listener) {
		final MenuItem agentItem = new MenuItem(parent, SWT.PUSH);
		agentItem.setData("agent", agent);
		agentItem.setText(agent.getName());
		agentItem.addSelectionListener(listener);
		agentItem.setImage(IGamaIcons.MENU_AGENT.image());

		populateComponents(parent, agent, listener);
	}

	private static void populateAgent(final Menu parent, final IAgent agent, final SelectionListener listener) {
		if ( !(agent instanceof IMacroAgent) ) { // TODO review IAgent.isGridAgent
			final MenuItem agentItem = new MenuItem(parent, SWT.PUSH);
			agentItem.setData("agent", agent);
			agentItem.setText(agent.getName());
			agentItem.setImage(IGamaIcons.MENU_AGENT.image());
			agentItem.addSelectionListener(listener);
		} else {
			final MenuItem agentItem = new MenuItem(parent, SWT.CASCADE);
			agentItem.setData("agent", agent);
			agentItem.setText(agent.getName() + " (macro)");
			agentItem.setImage(IGamaIcons.MENU_AGENT.image()); // TODO a suitable icon for macro-agent?

			final Menu agentMenu = new Menu(agentItem);
			agentItem.setMenu(agentMenu);

			populateAgentContent(agentMenu, (IMacroAgent) agent, listener);
		}
	}

	public static void fill(final Menu parent, final SelectionListener listener) {
		final SimulationAgent sim = GAMA.getSimulation();
		if ( sim == null ) { return; }
		final IPopulation worldPopulation = sim.getPopulation();
		populateSpecies(parent, worldPopulation, true, listener);
	}

	private static void populateSpecies(final Menu parent, final IPopulation population, final boolean isGlobal,
		final SelectionListener listener) {

		if ( isGlobal ) {
			List<IPopulation> populations = new GamaList(GAMA.getModelPopulations());
			Collections.reverse(populations);
			for ( final IPopulation pop : populations ) {
				MenuItem popItem = new MenuItem(parent, SWT.PUSH, 0);
				popItem.setText("Browse population of " + pop.getName());
				popItem.setImage(IGamaIcons.MENU_BROWSE.image());
				popItem.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(final SelectionEvent e) {
						InspectDisplayOutput.browse(pop.getHost(), pop.getSpecies());
					}

				});
			}
		}

		MenuItem speciesItem = null;
		if ( isGlobal ) {
			speciesItem = new MenuItem(parent, SWT.CASCADE, 0);
			speciesItem.setText("Explore all agents");
			new MenuItem(parent, SWT.SEPARATOR, 1);
		} else {
			speciesItem = new MenuItem(parent, SWT.CASCADE);
			speciesItem.setText("Species " + population.getName());
		}

		speciesItem.setData("agent", population);
		speciesItem.setImage(IGamaIcons.MENU_POPULATION.image());

		final Menu speciesMenu = new Menu(speciesItem);
		speciesItem.setMenu(speciesMenu);

		final List<IAgent> agents = new GamaList(population.iterator());
		final int size = agents.size();

		if ( size < 100 ) {
			for ( final IAgent a : agents ) {
				populateAgent(speciesMenu, a, listener);
			}
		} else {
			final int nb = size / 100 + 1;
			for ( int i = 0; i < nb; i++ ) {
				final int begin = i * 100;
				final int end = Math.min((i + 1) * 100, size);
				if ( begin >= end ) {
					break;
				}
				final MenuItem rangeItem = new MenuItem(speciesMenu, SWT.CASCADE);
				rangeItem.setText("From " + begin + " to " + (end - 1));
				final Menu rangeMenu = new Menu(rangeItem);
				for ( int j = begin; j < end; j++ ) {
					final IAgent agent = agents.get(j);
					populateAgent(rangeMenu, agent, listener);
				}
				rangeItem.setMenu(rangeMenu);
			}
		}
	}

	@Override
	public void fill(final Menu parent, final int index) {
		createMenuForAgent(parent, GAMA.getSimulation(), true);
	}
}
