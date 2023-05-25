package proxy;

import java.util.List;

import msi.gama.common.interfaces.BiConsumerWithPruning;
import msi.gama.kernel.model.IModel;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.agent.MinimalAgent;
import msi.gama.metamodel.agent.SavedAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.species.ISpecies;
import synchronizationMode.DistantSynchronizationMode;
import synchronizationMode.SynchronizationMode;
import ummisco.gama.dev.utils.DEBUG;

/**
 * ProxyAgent class, it is used to control access to an agent's attributes
 * 
 * @author Lucas Grosjean
 *
 */
public class ProxyAgent implements IAgent
{
	static
	{
		DEBUG.OFF();
	}
	

	protected final IPopulation<? extends IAgent> population;
	
	public ProxyAgent(final IPopulation<? extends IAgent> s, final int index) 
	{
		this.population = s;
	}
	
	public SynchronizationMode synchroMode;
    
	public ProxyAgent(IAgent proxiedAgent, final IPopulation<? extends IAgent> s)
    {
		DEBUG.OUT("hello new proxy : " + proxiedAgent.getName());
    	this.synchroMode = new SynchronizationMode(proxiedAgent);
    	this.population = s;
    }
	
	public void setSynchronizationMode(SynchronizationMode synchroMode)
	{
		DEBUG.OUT("set synchroMode " + synchroMode.getClass());
		this.synchroMode = synchroMode;
	}
	
	public void setSynchronizationMode(DistantSynchronizationMode synchroMode)
	{
		DEBUG.OUT("set setDistantSynchronizationMode " + synchroMode.getClass());
		this.synchroMode = synchroMode;
	}	
	
	@Override
	public IAgent getAgent() {
		DEBUG.OUT("getAgent() " + synchroMode.getAgent());
		return synchroMode.getAgent();
	}

	@Override
	public void setAgent(IAgent agent) {
		DEBUG.OUT("setAgent() " + agent);
		this.synchroMode.proxiedAgent = agent;
	}
	
	public IPopulation<?> getProxyPopulation() {
		DEBUG.OUT("getProxyPopulation() " + this.population);
		return this.population;
	}
	
	@Override
	public IMap<String, Object> getOrCreateAttributes() {
		DEBUG.OUT("getOrCreateAttributes " + this.synchroMode.getOrCreateAttributes());
		return this.synchroMode.getOrCreateAttributes();
	}

	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		return this.synchroMode.stringValue(scope);
	}

	@Override
	public Object getAttribute(String key) {
		DEBUG.OUT("getAttribute ProxyAgent " + key);
		return this.synchroMode.getAttribute(key);
	}

	@Override
	public void setAttribute(String key, Object value) {
		DEBUG.OUT("setAttribute ProxyAgent " + key + " :: " + value);
		this.synchroMode.setAttribute(key, value);
	}

	@Override
	public boolean hasAttribute(String key) {
		DEBUG.OUT("hasAttribute " + this.synchroMode.hasAttribute(key));
		return this.synchroMode.hasAttribute(key);
	}

	@Override
	public GamaPoint getLocation() {
		DEBUG.OUT("getLocation " + this.synchroMode.getLocation());
		return this.synchroMode.getLocation();
	}

	@Override
	public GamaPoint setLocation(GamaPoint l) {
		return this.synchroMode.setLocation(l);
	}

	@Override
	public boolean dead() {
		return this.synchroMode.dead();
	}

	@Override
	public void updateWith(IScope s, SavedAgent sa) {
		this.synchroMode.updateWith(s, sa);
	}

	@Override
	public IShape copy(IScope scope) {
		return this.synchroMode.copy(scope);
	}

	@Override
	public void dispose() {
		this.synchroMode.dispose();
	}
	
	@Override
	public boolean init(IScope scope) throws GamaRuntimeException {
		return this.synchroMode.init(scope);
	}

	@Override
	public boolean step(IScope scope) throws GamaRuntimeException {
		return this.synchroMode.step(scope);
	}

	@Override
	public Object get(IScope scope, String index) throws GamaRuntimeException {
		return this.synchroMode.get(scope, index);
	}

	@Override
	public String getName() {
		return this.synchroMode.getName();
	}

	@Override
	public void setName(String name) {
		this.synchroMode.setName(name);
	}

	@Override
	public GamaPoint getLocation(IScope scope) {
		return this.synchroMode.getLocation(scope);
	}

	@Override
	public GamaPoint setLocation(IScope scope, GamaPoint l) {
		return this.synchroMode.setLocation(scope, l);
	}

	@Override
	public IShape getGeometry(IScope scope) {
		return this.synchroMode.getGeometry(scope);
	}

	@Override
	public void setGeometry(IScope scope, IShape newGeometry) {
		this.synchroMode.setGeometry(scope, newGeometry);
	}

	@Override
	public void schedule(IScope scope) {
		if (!dead()) { 
			scope.init(this); 
		}
	}

	@Override
	public int getIndex() {
		return this.synchroMode.getIndex();
	}

	@Override
	public String getSpeciesName() {
		return this.synchroMode.getSpeciesName();
	}

	@Override
	public ISpecies getSpecies() {
		return this.synchroMode.getSpecies();
	}

	@Override
	public IPopulation<? extends IAgent> getPopulation() {
		return this.synchroMode.getPopulation();
	}

	@Override
	public boolean isInstanceOf(ISpecies s, boolean direct) {
		return this.synchroMode.isInstanceOf(s, direct);
	}

	@Override
	public Object getDirectVarValue(IScope scope, String s) throws GamaRuntimeException {
		return this.synchroMode.getDirectVarValue(scope, s);
	}

	@Override
	public void setDirectVarValue(IScope scope, String s, Object v) throws GamaRuntimeException {
		this.synchroMode.setDirectVarValue(scope, s, v);
	}

	@Override
	public IModel getModel() {
		return this.synchroMode.getModel();
	}

	@Override
	public Object primDie(IScope scope) throws GamaRuntimeException {
		DEBUG.OUT("do primDie");
		return this.synchroMode.primDie(scope);
	}

	@Override
	public Type getGeometricalType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void forEachAttribute(BiConsumerWithPruning<String, Object> visitor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int compareTo(IAgent o) {
		return (this.getHashCode() == ((MinimalAgent) o).hashCode) ? 1 : 0;
	}

	@Override
	public Object getFromIndicesList(IScope scope, IList<String> indices) throws GamaRuntimeException {
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
	public IMacroAgent getHost() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHost(IMacroAgent macroAgent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<IAgent> getMacroAgents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInstanceOf(String skill, boolean direct) {
		return this.getSpecies().implementsSkill(skill);
	}

	@Override
	public IPopulation<? extends IAgent> getPopulationFor(ISpecies microSpecies) {
		return this.getScope().getSimulation().getMicroPopulation(microSpecies);
	}

	@Override
	public IPopulation<? extends IAgent> getPopulationFor(String speciesName) {
		return this.getScope().getSimulation().getMicroPopulation(speciesName);
	}
	

	public int getHashCode() {
		return this.synchroMode.getHashcode();
	}
	
	public void proxyDispose()
	{
		DEBUG.OUT("this.getProxyPopulation() b4 " + this.getProxyPopulation());
		for(var auto : this.getProxyPopulation())
		{
			DEBUG.OUT("pop b4 : " + auto);
		}
		this.getProxyPopulation().remove(this);
		DEBUG.OUT("this.getProxyPopulation() after " + this.getProxyPopulation());
		DEBUG.OUT("pop size : " + this.getProxyPopulation().size());
		for(var auto : this.getProxyPopulation())
		{
			DEBUG.OUT("pop after : " + auto);
		}
	}

	@Override
	public IScope getScope() {
		return null;
	}
}
