package proxy;


import java.util.List;

import msi.gama.common.interfaces.BiConsumerWithPruning;
import msi.gama.kernel.model.IModel;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
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


public class ProxyAgent implements IAgent
{

	public SynchronizationMode synchroMode;
	public int uniqueID;
    
	public ProxyAgent(IAgent proxiedAgent, int uniqueID)
    {
    	this.synchroMode = new SynchronizationMode(proxiedAgent);
    	this.uniqueID = uniqueID;
    }
	
    public ProxyAgent(IAgent proxiedAgent)
    {
    	this.synchroMode = new SynchronizationMode(proxiedAgent);
    }
    
    public int getUniqueID() {
		return this.uniqueID;
	}
    
	@Override
	public IAgent getAgent() {
		return synchroMode.getAgent();
	}
	
	@Override
	public void setAgent(IAgent agent) {
		this.synchroMode = new SynchronizationMode(agent);
	}

	@Override
	public IMap<String, Object> getOrCreateAttributes() {
		return this.synchroMode.getOrCreateAttributes();
	}

	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		return this.synchroMode.stringValue(scope);
	}

	@Override
	public Object getAttribute(String key) {
		return this.synchroMode.getAttribute(key);
	}

	@Override
	public void setAttribute(String key, Object value) {
		this.synchroMode.setAttribute(key, value);
	}

	@Override
	public boolean hasAttribute(String key) {
		return this.synchroMode.hasAttribute(key);
	}

	@Override
	public GamaPoint getLocation() {
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
	public Type getGeometricalType() {
		return this.synchroMode.getGeometricalType();
	}

	@Override
	public void forEachAttribute(BiConsumerWithPruning<String, Object> visitor) {
		this.synchroMode.forEachAttribute(visitor);
	}

	@Override
	public int compareTo(IAgent o) {
		return this.synchroMode.compareTo(o);
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
	public Object getFromIndicesList(IScope scope, IList<String> indices) throws GamaRuntimeException {
		return this.synchroMode.getFromIndicesList(scope, indices);
	}

	@Override
	public IScope getScope() {
		return this.synchroMode.getScope();
	}

	@Override
	public ITopology getTopology() {
		return this.synchroMode.getTopology();
	}

	@Override
	public void setPeers(IList<IAgent> peers) {
		this.synchroMode.setPeers(peers);
	}

	@Override
	public IList<IAgent> getPeers() throws GamaRuntimeException {
		return this.synchroMode.getPeers();
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
	public IMacroAgent getHost() {
		return this.synchroMode.getHost();
	}

	@Override
	public void setHost(IMacroAgent macroAgent) {
		this.synchroMode.setHost(macroAgent);
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
	public List<IAgent> getMacroAgents() {
		return this.synchroMode.getMacroAgents();
	}

	@Override
	public IModel getModel() {
		return this.synchroMode.getModel();
	}

	@Override
	public boolean isInstanceOf(String skill, boolean direct) {
		return this.synchroMode.isInstanceOf(skill, direct);
	}

	@Override
	public IPopulation<? extends IAgent> getPopulationFor(ISpecies microSpecies) {
		return this.synchroMode.getPopulationFor(microSpecies);
	}

	@Override
	public IPopulation<? extends IAgent> getPopulationFor(String speciesName) {
		return this.synchroMode.getPopulationFor(speciesName);
	}	
}
