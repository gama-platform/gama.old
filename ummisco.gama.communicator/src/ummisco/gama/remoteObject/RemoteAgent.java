package ummisco.gama.remoteObject;

import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaMap;


public class RemoteAgent {
	String name;
	String speciesName;
	IShape geometry;
	GamaMap<Object, Object> attributes = new GamaMap<Object, Object>();
	
	public RemoteAgent()
	{
		
	}
	
	public RemoteAgent(GamlAgent agt)
	{
		this.name=agt.getName();
		speciesName = agt.getSpecies().getName();
		this.geometry=agt.getGeometry();
		this.attributes=agt.getAttributes();
	}
	
	public Object getAttributes(Object key)
	{
		return attributes.get(key);
	}

	public ILocation getLocation() 
	{
		return geometry.getLocation();
	}
	
	public String getName()
	{
		return name;
	}
	
}
