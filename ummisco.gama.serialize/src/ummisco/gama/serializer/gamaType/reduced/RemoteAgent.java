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
package ummisco.gama.serializer.gamaType.reduced;

import java.util.Map;

import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.TOrderedHashMap;

public class RemoteAgent {

	String name;
	String speciesName;
	IShape geometry;
	final Map<String, Object> attributes;

	public RemoteAgent() {
		attributes = new TOrderedHashMap<>();
	}

	public RemoteAgent(final GamlAgent agt) {
		this.name = agt.getName();
		speciesName = agt.getSpecies().getName();
		this.geometry = agt.getGeometry();
		this.attributes = agt.getAttributes();
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
