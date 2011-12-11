/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.gui.application.commands;

import java.util.Collections;
import java.util.List;

import msi.gama.gui.application.GUI;
import msi.gama.interfaces.IAgent;
import msi.gama.interfaces.IPopulation;
import msi.gama.interfaces.ISimulation;
import msi.gama.kernel.GAMA;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class AgentsMenu extends ContributionItem {

	public AgentsMenu() {}

	public AgentsMenu(final String id) {
		super(id);
	}

	static SelectionAdapter adapter = new SelectionAdapter() {

		@Override
		public void widgetSelected(final SelectionEvent e) {
			MenuItem mi = (MenuItem) e.widget;
			IAgent a = (IAgent) mi.getData("agent");
			if ( a != null && !a.dead() ) {
				GAMA.getExperiment().getOutputManager().selectionChanged(a);
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

	private static void fillAgentsMenu(final Menu menu, final IPopulation species) {
		List<IAgent> agents = species.getAgentsList();
		int size = agents.size();
		if ( size < 100 ) {
			for ( IAgent agent : agents ) {
				MenuItem agentItem = new MenuItem(menu, SWT.PUSH);
				agentItem.setData("agent", agent);
				agentItem.setText(agent.getName());
				agentItem.addSelectionListener(adapter);
				agentItem.setImage(GUI.agentImage);
			}
		} else {
			int nb = size / 100;
			for ( int i = 0; i < nb; i++ ) {
				MenuItem rangeItem = new MenuItem(menu, SWT.CASCADE);
				int begin = i * 100;
				int end = Math.min((i + 1) * 100, size);
				rangeItem.setText("From " + begin + " to " + (end - 1));
				Menu rangeMenu = new Menu(rangeItem);
				for ( int j = begin; j < end; j++ ) {
					IAgent agent = agents.get(j);
					MenuItem agentItem = new MenuItem(rangeMenu, SWT.PUSH);
					agentItem.setData("agent", agent);
					agentItem.setText(agent.getName());
					agentItem.addSelectionListener(adapter);
					agentItem.setImage(GUI.agentImage);
				}
				rangeItem.setMenu(rangeMenu);
			}
		}
	}

	// TODO adapt this to multi-scale organization!!!
	public static Menu createSpeciesSubMenu(final Control parent, final IPopulation species) {
		Menu agentsMenu = new Menu(parent);
		fillAgentsMenu(agentsMenu, species);
		parent.setMenu(agentsMenu);
		return agentsMenu;
	}

	private void populateComponents(final Menu parent, IAgent macro) {
		MenuItem microAgentsItem = new MenuItem(parent, SWT.CASCADE);
		microAgentsItem.setText("Micro agents");
		
		Menu microSpeciesMenu = new Menu(microAgentsItem);
		microAgentsItem.setMenu(microSpeciesMenu);
		
		IPopulation microPopulation;
		List<String> microSpeciesNames = macro.getSpecies().getMicroSpeciesNames();
		Collections.sort(microSpeciesNames);
		for (String microSpec : microSpeciesNames) {
			microPopulation = macro.getMicroPopulation(microSpec);
			if ( (microPopulation != null) && (microPopulation.size() > 0) ) {
				populateSpecies(microSpeciesMenu, microPopulation, false);
			}
		}
	}
	
	private void populateAgentContent(final Menu parent, final IAgent agent) {
		MenuItem agentItem = new MenuItem(parent, SWT.PUSH);
		agentItem.setData("agent", agent);
		agentItem.setText(agent.getName());
		agentItem.addSelectionListener(adapter);
		agentItem.setImage(GUI.agentImage);
		
		populateComponents(parent, agent);
	}
	
	
	private void populateAgent(final Menu parent, final IAgent agent) {
		if (agent.isGridAgent() || !agent.hasMembers()) { // TODO review IAgent.isGridAgent
			MenuItem agentItem = new MenuItem(parent, SWT.PUSH);
			agentItem.setData("agent", agent);
			agentItem.setText(agent.getName());
			agentItem.setImage(GUI.agentImage);
			agentItem.addSelectionListener(adapter);
		} else {
			MenuItem agentItem = new MenuItem(parent, SWT.CASCADE);
			agentItem.setData("agent", agent);
			agentItem.setText(agent.getName() + " (macro)");
			agentItem.setImage(GUI.agentImage);  // TODO a suitable icon for macro-agent?
			
			Menu agentMenu = new Menu(agentItem);
			agentItem.setMenu(agentMenu);
			
			populateAgentContent(agentMenu, agent);
		}
	}
	
	private void populateSpecies(final Menu parent, IPopulation population, boolean isGlobal) {
		MenuItem speciesItem = null;
		if (isGlobal) {
			speciesItem = new MenuItem(parent, SWT.CASCADE, 0);
		} else {
			speciesItem = new MenuItem(parent, SWT.CASCADE);
		}
		speciesItem.setText("Species " + population.getName());
		speciesItem.setData("agent", population);
		speciesItem.setImage(GUI.speciesImage);

		Menu speciesMenu = new Menu(speciesItem);
		speciesItem.setMenu(speciesMenu);
		
		List<IAgent> agents = population.getAgentsList();
		int size = agents.size();

		if (size < 100) {
			for (IAgent a : agents) {
				populateAgent(speciesMenu, a);
			}
		} else {
			int nb = size / 100;
			for ( int i = 0; i < nb; i++ ) {
				MenuItem rangeItem = new MenuItem(speciesMenu, SWT.CASCADE);
				int begin = i * 100;
				int end = Math.min((i + 1) * 100, size);
				rangeItem.setText("From " + begin + " to " + (end - 1));
				Menu rangeMenu = new Menu(rangeItem);
				for ( int j = begin; j < end; j++ ) {
					IAgent agent = agents.get(j);
					populateAgent(rangeMenu, agent);
				}
				rangeItem.setMenu(rangeMenu);
			}
		}
	}
	
	@Override
	public void fill(final Menu parent, final int index) {
		ISimulation sim = GAMA.getFrontmostSimulation();
		IPopulation worldPopulation = sim.getWorldPopulation();
		populateSpecies(parent, worldPopulation, true);
	}
}
