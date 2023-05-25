package synchronizationMode;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.model.IModel;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.MinimalAgent;
import msi.gama.metamodel.agent.SavedAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaMap;
import msi.gama.util.IMap;
import msi.gaml.species.ISpecies;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Control the access of agent from a distant processor / simulation
 * 
 * @author Lucas Grosjean
 *
 */
public class DistantSynchronizationMode extends SynchronizationMode
{	
	static
	{
		DEBUG.OFF();
	}
	
	IMap<String, Object> attributes;
	
	public DistantSynchronizationMode(IAgent agentToDistantProxy)
	{
		DEBUG.OUT("agentToDistantProxy : " + agentToDistantProxy);
		updateAttributes(agentToDistantProxy);
	}
	
	@Override
	public void setAgent(IAgent agent)
	{
		this.proxiedAgent = agent;
	}
	
	@Override
	public IAgent getAgent()
	{
		if(proxiedAgent != null)
		{			
			return proxiedAgent;
		}
		return null;
	}
	
	public void stepProxy()
	{
		// TODO
	}
	
	@Override
	public void updateProxiedAgent(IAgent agentUpdated)
	{
		updateAttributes(agentUpdated);
	}

	@Override
	public void updateAttributes(IAgent agentWithData)
	{
		DEBUG.OUT("agentWithData : " + agentWithData);
		var attributesFromAgentData = agentWithData.getOrCreateAttributes();
		
		var mapAttributes = new GamaMap<String, Object>(attributesFromAgentData.size(), attributesFromAgentData.getKeys().getGamlType(), attributesFromAgentData.getValues().getGamlType());
		for(var auto : attributesFromAgentData.entrySet())
		{
			mapAttributes.put(auto.getKey(), auto.getValue());
		}
		
		this.attributes = mapAttributes;
		
		this.attributes.put(IKeyword.LOCATION, agentWithData.getLocation());
		this.attributes.put(IKeyword.HASHCODE, ((MinimalAgent)agentWithData).hashCode);
		this.attributes.put("col", new GamaColor(122,122,122));
		
		DEBUG.OUT("attributes = " + this.attributes);
		DEBUG.OUT("location : " + agentWithData.getLocation());
		DEBUG.OUT("hashcode : " + ((MinimalAgent)agentWithData).hashCode);
		DEBUG.OUT("color : " + agentWithData.getAttribute("col"));
	}
	
	@Override
	public boolean step(IScope scope) throws GamaRuntimeException {
		stepProxy();
		return true;
	}
	
	@Override
	public IMap<String, Object> getOrCreateAttributes()
	{
		return attributes;
	}

	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		return "";
	}
	
	@Override
	public Object getAttribute(String key)
	{
		return attributes.get(key);
	}

	@Override
	public void setAttribute(String key, Object value) 
	{
		attributes.put(key, value);
	}
	
	@Override
	public boolean hasAttribute(String key)
	{
		return attributes.containsKey(key);
	}
	
	@Override
	public GamaPoint getLocation() 
	{
		return (GamaPoint) attributes.get("location");
	}
	
	@Override
	public GamaPoint setLocation(GamaPoint l)
	{
		attributes.put("location", l);
		return l;
	}
	
	@Override
	public boolean dead() 
	{
		return false;
	}
	
	@Override
	public Object getDirectVarValue(IScope scope, String s) throws GamaRuntimeException {

		if(attributes == null)
		{
			return null;
		}
		return this.attributes.get(s);
	}

	
	@Override
	public void setDirectVarValue(IScope scope, String s, Object v) throws GamaRuntimeException {
		this.attributes.put(s, v);
	}
	
	@Override
	public void updateWith(IScope s, SavedAgent sa)
	{
	}
	
	@Override
	public IShape copy(IScope scope) {
		return null;
	}

	@Override
	public void dispose() {
		// TODO
	}

	@Override
	public boolean init(IScope scope) throws GamaRuntimeException {
		return true;
	}
	
	@Override
	public Object get(IScope scope, String index) throws GamaRuntimeException {
		return null;
	}
	
	@Override
	public String getName() {
		return this.attributes.get("name").toString();
	}
	
	@Override
	public void setName(String name) {
		this.attributes.put("name", name);	
	}
	
	@Override
	public GamaPoint getLocation(IScope scope) {
		return (GamaPoint) this.attributes.get("location");
	}

	
	@Override
	public GamaPoint setLocation(IScope scope, GamaPoint l) {
		this.attributes.put("location", l);	
		return l;
	}

	
	@Override
	public IShape getGeometry(IScope scope) {
		return (IShape) this.attributes.get("geometry");
	}

	
	@Override
	public void setGeometry(IScope scope, IShape newGeometry) {
		this.attributes.put("geometry", newGeometry);	
	}
	
	@Override
	public void schedule(IScope scope) {
		// TODO
	}

	
	@Override
	public int getIndex() {
		// TODO
		return 0;
	}

	
	@Override
	public String getSpeciesName() {
		// TODO
		return "";
	}

	
	@Override
	public ISpecies getSpecies() {
		// TODO
		return null;
	}

	@Override
	public IPopulation<? extends IAgent> getPopulation() {
		return null;
	}

	@Override
	public boolean isInstanceOf(ISpecies s, boolean direct) {
		return false;
	}

	@Override
	public IModel getModel() {
		return null;	
	}
	 
	@Override
	public Object primDie(IScope scope) throws GamaRuntimeException
	{
		return null;
	}

	@Override
	public int getHashcode() {
		return (int) this.attributes.get(IKeyword.HASHCODE);
	}
}
