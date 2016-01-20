/*********************************************************************************************
 * 
 * 
 * 'RemoteAgent.java', in plugin 'ummisco.gama.communicator', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gama.communicator.common.remoteObject;

import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.species.ISpecies;

public class RemoteAgent {

	String name;
	String speciesName;
	IShape geometry;
	final Map<Object, Object> attributes;

	public RemoteAgent() {
		attributes = new TOrderedHashMap();
	}

	public RemoteAgent(final GamlAgent agt) {
		this.name = agt.getName();
		speciesName = agt.getSpecies().getName();
		this.geometry = agt.getGeometry();
		this.attributes = agt.getAttributes();

		for ( final String specVar : agt.getSpecies().getVarNames() ) {
			System.out.println(name + " " + specVar);
			Object val = agt.getSpecies().getVar(specVar).value(agt.getScope(), agt);	
			attributes.put(specVar, val);
		}
	}

	public Object getAttributes(final Object key) {
		return attributes.get(key);
	}

	public ILocation getLocation() {
		return geometry.getLocation();
	}

	public String getName() {
		return name;
	}

}
