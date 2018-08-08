package ummisco.gama.serializer.gamaType.reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.geom.Geometry;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.AbstractAgent;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.agent.SavedAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gama.util.IList;
import msi.gama.util.IReference;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

public class ReferenceAgent implements IReference, IAgent {

	ArrayList<AgentAttribute> agtAttr;
	ReferenceToAgent attributeValue;
	
	public ReferenceAgent(IAgent _agt, String agtAttrName, IAgent agtAttrValue) {
	//	super(null,-1);
		agtAttr = new ArrayList<AgentAttribute>();
		
		if(_agt != null && agtAttrName != null) {
			agtAttr.add(new AgentAttribute(_agt, agtAttrName));
		}
		attributeValue = new ReferenceToAgent(agtAttrValue);
	}
	
	public ReferenceAgent(IAgent refAgt, String attrName, ReferenceToAgent refAttrValue) {
	//	super(null,-1);

		agtAttr = new ArrayList<AgentAttribute>();
		if(refAgt != null && attrName != null) {
			agtAttr.add(new AgentAttribute(refAgt, attrName));
		}
		attributeValue = refAttrValue;
	}

	public ReferenceToAgent getAttributeValue() {return attributeValue;}

	public Object constructReferencedObject(SimulationAgent sim) {
		return getAttributeValue().getReferencedAgent(sim);	
	}

	@Override
	public ArrayList<AgentAttribute> getAgentAttributes() {
		return agtAttr;
	}

    public boolean equals(Object o) {
        if (o == this)
            return true;
        else
        	return false;
    }

	@Override
	public IShape copy(IScope scope) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean covers(IShape g) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean crosses(IShape g) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double euclidianDistanceTo(ILocation g) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double euclidianDistanceTo(IShape g) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IAgent getAgent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Envelope3D getEnvelope() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type getGeometricalType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Geometry getInnerGeometry() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean intersects(IShape g) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLine() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPoint() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setAgent(IAgent agent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInnerGeometry(Geometry intersection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDepth(double depth) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public GamaMap<String, Object> getOrCreateAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isMultiple() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Double getArea() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getVolume() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getPerimeter() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IList<GamaShape> getHoles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GamaPoint getCentroid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GamaShape getExteriorRing(IScope scope) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getWidth() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getHeight() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getDepth() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GamaShape getGeometricEnvelope() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IList<? extends ILocation> getPoints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IList<? extends IShape> getGeometries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String serialize(boolean includingBuiltIn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IType<?> getGamlType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getAttribute(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttribute(String key, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasAttribute(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int compareTo(IAgent o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean init(IScope scope) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean step(IScope scope) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object get(IScope scope, String index) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getFromIndicesList(IScope scope, IList<String> indices) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IScope getScope() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITopology getTopology() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPeers(IList<IAgent> peers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IList<IAgent> getPeers() throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ILocation getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLocation(ILocation l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IShape getGeometry() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGeometry(IShape newGeometry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean dead() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IMacroAgent getHost() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHost(IMacroAgent macroAgent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedule(IScope scope) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setExtraAttributes(Map<String, Object> map) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getSpeciesName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISpecies getSpecies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPopulation<? extends IAgent> getPopulation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInstanceOf(ISpecies s, boolean direct) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getDirectVarValue(IScope scope, String s) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDirectVarValue(IScope scope, String s, Object v) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<IAgent> getMacroAgents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IModel getModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInstanceOf(String skill, boolean direct) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IPopulation<? extends IAgent> getPopulationFor(ISpecies microSpecies) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPopulation<? extends IAgent> getPopulationFor(String speciesName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateWith(IScope s, SavedAgent sa) {
		// TODO Auto-generated method stub
		
	}	
}
