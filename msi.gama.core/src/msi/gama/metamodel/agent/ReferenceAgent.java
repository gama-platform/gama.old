package msi.gama.metamodel.agent;

import msi.gama.kernel.simulation.SimulationAgent;

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
}
