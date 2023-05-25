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
import msi.gama.metamodel.shape.IShape.Type;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.species.ISpecies;

public class SynchronizationMode 
{
	
	IAgent proxiedAgent;
	
	public SynchronizationMode(IAgent proxiedAgent)
	{
		this.proxiedAgent = proxiedAgent;
	}
	
	IAgent getAgent()
	{
		return this.proxiedAgent;
	}
	
	IMap<String, Object> getOrCreateAttributes()
	{
		return this.proxiedAgent.getOrCreateAttributes();
	}
	
	String stringValue(IScope scope) throws GamaRuntimeException
	{
		return this.proxiedAgent.stringValue(scope);
	}
	
	Object getAttribute(String key)
	{
		return this.proxiedAgent.getAttribute(key);
	}

	void setAttribute(String key, Object value) 
	{
		this.proxiedAgent.setAttribute(key, value);
	}
	
	boolean hasAttribute(String key)
	{
		return this.proxiedAgent.hasAttribute(key);
	}
	
	GamaPoint getLocation() 
	{
		return this.proxiedAgent.getLocation();
	}
	
	GamaPoint setLocation(GamaPoint l)
	{
		return this.proxiedAgent.setLocation(l);
	}
	
	boolean dead() 
	{
		return this.proxiedAgent.dead();
	}
	
	void updateWith(IScope s, SavedAgent sa)
	{
		this.proxiedAgent.updateWith(s, sa);
	}
	
	IShape copy(IScope scope) {
		return this.proxiedAgent.copy(scope);
	}

	void dispose() {
		 this.proxiedAgent.dispose();
	}

	Type getGeometricalType() {
		return this.proxiedAgent.getGeometricalType();
	}

	void forEachAttribute(BiConsumerWithPruning<String, Object> visitor) {
		 this.proxiedAgent.forEachAttribute(visitor);
	}

	int compareTo(IAgent o) {
		return this.proxiedAgent.compareTo(o);
	}

	
	boolean init(IScope scope) throws GamaRuntimeException {
		return this.proxiedAgent.init(scope);
	}

	
	boolean step(IScope scope) throws GamaRuntimeException {
		return this.proxiedAgent.step(scope);
	}

	
	Object get(IScope scope, String index) throws GamaRuntimeException {
		return this.proxiedAgent.get(scope, index);
	}

	
	Object getFromIndicesList(IScope scope, IList<String> indices) throws GamaRuntimeException {
		return this.proxiedAgent.getFromIndicesList(scope, indices);
	}

	
	IScope getScope() {
		return this.proxiedAgent.getScope();
	}

	
	ITopology getTopology() {
		return this.proxiedAgent.getTopology();
	}

	
	void setPeers(IList<IAgent> peers) {
		 this.proxiedAgent.setPeers(peers);
	}

	
	IList<IAgent> getPeers() throws GamaRuntimeException {
		return this.proxiedAgent.getPeers();
	}

	
	String getName() {
		System.out.println("getname " + this.proxiedAgent.getName());
		return this.proxiedAgent.getName();
	}

	
	void setName(String name) {
		this.proxiedAgent.setName(name);	
	}

	
	GamaPoint getLocation(IScope scope) {
		return this.proxiedAgent.getLocation(scope);
	}

	
	GamaPoint setLocation(IScope scope, GamaPoint l) {
		return this.proxiedAgent.setLocation(scope, l);
	}

	
	IShape getGeometry(IScope scope) {
		return this.proxiedAgent.getGeometry(scope);
	}

	
	void setGeometry(IScope scope, IShape newGeometry) {
		this.proxiedAgent.setGeometry(scope, newGeometry);
	}

	
	IMacroAgent getHost() {
		return this.proxiedAgent.getHost();
	}

	
	void setHost(IMacroAgent macroAgent) {
		this.proxiedAgent.setHost(macroAgent);
	}

	
	void schedule(IScope scope) {
		this.proxiedAgent.schedule(scope);
	}

	
	int getIndex() {
		return this.proxiedAgent.getIndex();
	}

	
	String getSpeciesName() {
		return this.proxiedAgent.getSpeciesName();
	}

	
	ISpecies getSpecies() {
		return this.proxiedAgent.getSpecies();
	}

	
	IPopulation<? extends IAgent> getPopulation() {
		return this.proxiedAgent.getPopulation();
	}

	
	boolean isInstanceOf(ISpecies s, boolean direct) {
		return this.proxiedAgent.isInstanceOf(s, direct);
	}

	
	Object getDirectVarValue(IScope scope, String s) throws GamaRuntimeException {
		if(this.proxiedAgent.hasAttribute(s))
		{
			//System.out.println("getDirectVarValue Proxied value " + s + " = " + proxiedAgent.getAttribute(s));
		}
		
		return this.proxiedAgent.getDirectVarValue(scope, s);
	}

	
	void setDirectVarValue(IScope scope, String s, Object v) throws GamaRuntimeException {
		this.proxiedAgent.setDirectVarValue(scope, s, v);	
		
		if(this.proxiedAgent.hasAttribute(s))
		{
			System.out.println("setDirectVarValue Proxied value " + s + " = " + proxiedAgent.getAttribute(s));
		}
	}

	
	List<IAgent> getMacroAgents() {
		return this.proxiedAgent.getMacroAgents();	
	}

	
	IModel getModel() {
		return this.proxiedAgent.getModel();	
	}

	
	boolean isInstanceOf(String skill, boolean direct) {
		return this.proxiedAgent.isInstanceOf(skill,direct);	
	}

	
	IPopulation<? extends IAgent> getPopulationFor(ISpecies microSpecies) {
		return this.proxiedAgent.getPopulationFor(microSpecies);	
	}

	
	 IPopulation<? extends IAgent> getPopulationFor(String speciesName) {
		return this.proxiedAgent.getPopulationFor(speciesName);	
	}	
}
