package msi.gama.metamodel.agent;

import java.util.ArrayList;
import java.util.List;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.population.IPopulation;

public class ReferenceToAgent {
	List<String> species;
	List<Integer> index;
	
	private ReferenceToAgent() {
		species = new ArrayList<>();
		index = new ArrayList<>();
	}
	
	public ReferenceToAgent(List<String> s, List<Integer> i) {
		species = s;
		index = i;
	}
	
	public ReferenceToAgent(IAgent agt) {
		this();
		if(agt != null) {
			species.add(agt.getSpeciesName());
			index.add(agt.getIndex());
			
			IAgent host = agt.getHost();
			
			while(host != null && !(host instanceof SimulationAgent)) {
				species.add(host.getSpeciesName());
				index.add(host.getIndex());
				host = host.getHost();
			}
		}
	}
	
	public String toString() {
		String res = "";
		
		for(int i = 0; i < species.size() ; i ++) {
			res = "/" + species.get(i)+index.get(i);
		}
		return res;
	}
	
	public IAgent getReferencedAgent(SimulationAgent sim) {

		IPopulation<? extends IAgent> pop = sim.getPopulationFor(species.get(species.size()-1));
		IAgent referencedAgt = pop.get(index.get(index.size()-1));
		
		for(int i = index.size()-2 ; i >= 0; i--) {
			pop = sim.getPopulationFor(species.get(i));
			referencedAgt = pop.get(index.get(i));			
		}		
	
		return referencedAgt;
	}	

}
