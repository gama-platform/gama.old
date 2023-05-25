package synchronizationMode;

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
import msi.gama.metamodel.shape.IShape.Type;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.species.ISpecies;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Class used to define the way to access agent in distributed context
 * 
 * @author Lucas Grosjean
 *
 */
public class SynchronizationMode
{
	static
	{
		DEBUG.OFF();
	}
	
	public IAgent proxiedAgent;
	protected boolean hasDistantCopy;
	
	public SynchronizationMode(IAgent proxiedAgent)
	{
		this.proxiedAgent = proxiedAgent;
	}
	
	public SynchronizationMode(){}
	
	public void setHasDistantCopy(boolean hasDistantCopy)
	{
		this.hasDistantCopy = hasDistantCopy;
	}
	
	public void setAgent(IAgent agent)
	{
		this.proxiedAgent = agent;
	}
	
	public IAgent getAgent()
	{
		return this.proxiedAgent;
	}
	
	public void updateProxiedAgent(IAgent agentUpdated)
	{
		setAgent(agentUpdated);
	}
	
	public IMap<String, Object> getOrCreateAttributes()
	{
		return this.proxiedAgent.getOrCreateAttributes();
	}
	
	public String stringValue(IScope scope) throws GamaRuntimeException
	{
		return this.proxiedAgent.stringValue(scope);
	}
	
	public Object getAttribute(String key)
	{
		return this.proxiedAgent.getAttribute(key);
	}

	public void setAttribute(String key, Object value) 
	{
		this.proxiedAgent.setAttribute(key, value);
	}
	
	public boolean hasAttribute(String key)
	{
		return this.proxiedAgent.hasAttribute(key);
	}
	
	public GamaPoint getLocation() 
	{
		return this.proxiedAgent.getLocation();
	}
	
	public GamaPoint setLocation(GamaPoint l)
	{
		return this.proxiedAgent.setLocation(l);
	}
	
	public boolean dead() 
	{
		return this.proxiedAgent.dead();
	}
	
	public void updateWith(IScope s, SavedAgent sa)
	{
		this.proxiedAgent.updateWith(s, sa);
	}
	
	public IShape copy(IScope scope) {
		return this.proxiedAgent.copy(scope);
	}

	public void dispose() {
		 this.proxiedAgent.dispose();
	}

	public Type getGeometricalType() {
		return this.proxiedAgent.getGeometricalType();
	}

	public void forEachAttribute(BiConsumerWithPruning<String, Object> visitor) {
		 this.proxiedAgent.forEachAttribute(visitor);
	}

	public int compareTo(IAgent o) {
		return this.proxiedAgent.compareTo(o);
	}

	public boolean init(IScope scope) throws GamaRuntimeException {
		return this.proxiedAgent.init(scope);
	}

	public boolean step(IScope scope) throws GamaRuntimeException {
		return this.proxiedAgent.step(scope);
	}
	
	public Object get(IScope scope, String index) throws GamaRuntimeException {
		return this.proxiedAgent.get(scope, index);
	}
	
	public Object getFromIndicesList(IScope scope, IList<String> indices) throws GamaRuntimeException {
		return this.proxiedAgent.getFromIndicesList(scope, indices);
	}

	public IScope getScope() {
		return this.proxiedAgent.getScope();
	}
	
	public ITopology getTopology() {
		return this.proxiedAgent.getTopology();
	}
	
	public void setPeers(IList<IAgent> peers) {
		 this.proxiedAgent.setPeers(peers);
	}
	
	public IList<IAgent> getPeers() throws GamaRuntimeException {
		return this.proxiedAgent.getPeers();
	}
	
	public String getName() {
		System.out.println("getname " + this.proxiedAgent.getName());
		return this.proxiedAgent.getName();
	}

	public void setName(String name) {
		this.proxiedAgent.setName(name);	
	}
	
	public GamaPoint getLocation(IScope scope) {
		return this.proxiedAgent.getLocation(scope);
	}
	
	public GamaPoint setLocation(IScope scope, GamaPoint l) {
		return this.proxiedAgent.setLocation(scope, l);
	}
	
	public IShape getGeometry(IScope scope) {
		return this.proxiedAgent.getGeometry(scope);
	}
	
	public void setGeometry(IScope scope, IShape newGeometry) {
		this.proxiedAgent.setGeometry(scope, newGeometry);
	}
	
	public IMacroAgent getHost() {
		return this.proxiedAgent.getHost();
	}
	
	public void setHost(IMacroAgent macroAgent) {
		this.proxiedAgent.setHost(macroAgent);
	}
	
	public void schedule(IScope scope) {
		this.proxiedAgent.schedule(scope);
	}
	
	public int getIndex() {
		return this.proxiedAgent.getIndex();
	}
	
	public String getSpeciesName() {
		return this.proxiedAgent.getSpeciesName();
	}

	public ISpecies getSpecies() {
		return this.proxiedAgent.getSpecies();
	}

	public IPopulation<? extends IAgent> getPopulation() {
		return this.proxiedAgent.getPopulation();
	}

	public boolean isInstanceOf(ISpecies s, boolean direct) {
		return this.proxiedAgent.isInstanceOf(s, direct);
	}

	public Object getDirectVarValue(IScope scope, String s) throws GamaRuntimeException {
	
		return this.proxiedAgent.getDirectVarValue(scope, s);
	}

	public void setDirectVarValue(IScope scope, String s, Object v) throws GamaRuntimeException {
		this.proxiedAgent.setDirectVarValue(scope, s, v);	
	}

	public List<IAgent> getMacroAgents() {
		return this.proxiedAgent.getMacroAgents();	
	}

	public IModel getModel() {
		return this.proxiedAgent.getModel();	
	}

	public boolean isInstanceOf(String skill, boolean direct) {
		return this.proxiedAgent.isInstanceOf(skill,direct);	
	}

	public IPopulation<? extends IAgent> getPopulationFor(ISpecies microSpecies) {
		return this.proxiedAgent.getPopulationFor(microSpecies);	
	}

	public IPopulation<? extends IAgent> getPopulationFor(String speciesName) {
		return this.proxiedAgent.getPopulationFor(speciesName);	
	}	
	 
	public Object primDie(IScope scope) throws GamaRuntimeException
	{
		 return this.proxiedAgent.primDie(scope);
	}
	
	public void updateAttributes(IAgent agent)
	{
		DEBUG.OUT("updateAttributes SynchronizationMode : " + agent);
	}

	public int getHashcode() {
		return ((MinimalAgent)this.proxiedAgent).hashCode;
	}
}
