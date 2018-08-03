package msi.gama.metamodel.agent;

import java.util.ArrayList;
import java.util.List;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.population.IPopulation;

public class ReferenceAgent extends GamlAgent {

	// ReferenceToAgent agt;
	IAgent agt;
	String attributeName;
	ReferenceToAgent attributeValue;
	
	public ReferenceAgent(IAgent _agt, String agtAttrName, IAgent agtAttrValue) {
		super(null, -1);
		// agt = new ReferenceToAgent(_agt);
		agt = _agt;
		attributeName = agtAttrName ;
		attributeValue = new ReferenceToAgent(agtAttrValue);
	}
	
	public ReferenceAgent(IAgent refAgt, String attrName, ReferenceToAgent refAttrValue) {
		super(null, -1);

		agt = refAgt;
		attributeName = attrName;
		attributeValue = refAttrValue;
	}

	public IAgent getAgt() {return agt;}
	public String getAttributeName() {return attributeName;}
	public ReferenceToAgent getAttributeValue() {return attributeValue;}
		
	public void setAgentAndAttrName(IAgent _agt, String attrName) {
		agt = _agt;
		attributeName = attrName;
	}

	public IAgent getReferencedAgent(SimulationAgent sim) {
		return attributeValue.getReferencedAgent(sim);
	}
	
	
	
	public class ReferenceToAgent {
		List<String> species;
		List<Integer> index;
		
		private ReferenceToAgent() {
			species = new ArrayList<>();
			index = new ArrayList<>();
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

}
