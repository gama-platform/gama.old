/*******************************************************************************************************
 *
 * IReference.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.util;

import java.util.ArrayList;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;

/**
 * The Interface IReference.
 */
public interface IReference {

	/**
	 * Construct referenced object.
	 *
	 * @param simulationAgent the simulation agent
	 * @return the object
	 */
	Object constructReferencedObject(SimulationAgent simulationAgent);
	
	/**
	 * Gets the agent attributes.
	 *
	 * @return the agent attributes
	 */
	ArrayList<AgentAttribute> getAgentAttributes();

	/**
	 * Resolve references.
	 *
	 * @param scope the scope
	 * @param sim the sim
	 */
	default public void resolveReferences(IScope scope, SimulationAgent sim) {
		Object referencedObject = constructReferencedObject(sim);
		for(AgentAttribute agtAttr : getAgentAttributes()) {	
			agtAttr.getAgt().setDirectVarValue(scope, agtAttr.getAttributeName(), referencedObject);
		}		
	}

	/**
	 * Sets the agent and attr name.
	 *
	 * @param _agt the agt
	 * @param attrName the attr name
	 */
	default public void setAgentAndAttrName(IAgent _agt, String attrName) {
		getAgentAttributes().add(new AgentAttribute(_agt, attrName));
	}

	/**
	 * Checks if is reference.
	 *
	 * @param o the o
	 * @return true, if is reference
	 */
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
	
	/**
	 * Gets the object without reference.
	 *
	 * @param o the o
	 * @param sim the sim
	 * @return the object without reference
	 */
	public static Object getObjectWithoutReference(Object o, SimulationAgent sim) {
		return IReference.isReference(o) ? 
				((IReference) o).constructReferencedObject(sim) :
				o;
	}
	
	
	/**
	 * The Class AgentAttribute.
	 */
	class AgentAttribute {
		
		/** The agt. */
		IAgent agt;
		
		/** The attribute name. */
		String attributeName;	
		
		/**
		 * Gets the agt.
		 *
		 * @return the agt
		 */
		public IAgent getAgt() { return agt; }
		
		/**
		 * Gets the attribute name.
		 *
		 * @return the attribute name
		 */
		public String getAttributeName() { return attributeName; }
		
		/**
		 * Instantiates a new agent attribute.
		 *
		 * @param _agt the agt
		 * @param agtAttrName the agt attr name
		 */
		public AgentAttribute(IAgent _agt, String agtAttrName) {
			agt = _agt;
			attributeName = agtAttrName ;
		}
	}
	
}
