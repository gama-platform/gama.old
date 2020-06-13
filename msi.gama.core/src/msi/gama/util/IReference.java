/*******************************************************************************************************
 *
 * msi.gama.util.IReference.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util;

import java.util.ArrayList;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;

public interface IReference {

	Object constructReferencedObject(SimulationAgent simulationAgent);
	
	ArrayList<AgentAttribute> getAgentAttributes();

	default public void resolveReferences(IScope scope, SimulationAgent sim) {
		Object referencedObject = constructReferencedObject(sim);
		for(AgentAttribute agtAttr : getAgentAttributes()) {	
			agtAttr.getAgt().setDirectVarValue(scope, agtAttr.getAttributeName(), referencedObject);
		}		
	}

	default public void setAgentAndAttrName(IAgent _agt, String attrName) {
		getAgentAttributes().add(new AgentAttribute(_agt, attrName));
	}

	public static boolean isReference(Object o) {
		boolean isReference = false;
		
		//final List<Class<?>> allClassesApa = ClassUtils.getAllSuperclasses(arg0);
		//for (final Object c : allClassesApa) {
		//	if (c.equals(GamlAgent.class))
		//		return true;
		//}		
		
		if(o != null) {
			Class<?>[] allInterface = o.getClass().getInterfaces();
			for( Class<?> c : allInterface) {
				if(c.equals(IReference.class))
					isReference = true;
			}
		}
		
		return isReference;
	}
	
	public static Object getObjectWithoutReference(Object o, SimulationAgent sim) {
		return IReference.isReference(o) ? 
				((IReference) o).constructReferencedObject(sim) :
				o;
	}
	
	
	class AgentAttribute {
		IAgent agt;
		String attributeName;	
		
		public IAgent getAgt() { return agt; }
		public String getAttributeName() { return attributeName; }
		
		public AgentAttribute(IAgent _agt, String agtAttrName) {
			agt = _agt;
			attributeName = agtAttrName ;
		}
	}
	
}
