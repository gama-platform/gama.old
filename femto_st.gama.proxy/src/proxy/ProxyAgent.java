package proxy;

import java.util.List;

import org.locationtech.jts.geom.Geometry;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.interfaces.BiConsumerWithPruning;
import msi.gama.kernel.model.IModel;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.agent.MinimalAgent;
import msi.gama.metamodel.agent.SavedAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.species.ISpecies;
import proxyPopulation.ProxyPopulation;
import synchronizationMode.DistantSynchronizationMode;
import synchronizationMode.LocalSynchronizationMode;
import synchronizationMode.SynchronizationModeAbstract;
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

	protected final ProxyPopulation population;
	private IScope scope;
	
	public SynchronizationModeAbstract synchroMode;


	public ProxyAgent(final ProxyPopulation s, final int index, IScope scope) 
	{
		DEBUG.OUT("create new proxy index : " + index);
		this.population = s;
		this.scope = scope;
	}
    
	public ProxyAgent(IAgent proxiedAgent, final ProxyPopulation s, IScope scope)
    {
		DEBUG.OUT("create new proxy : " + proxiedAgent.getName());
		
    	this.synchroMode = new LocalSynchronizationMode(proxiedAgent);
    	this.population = s;
		this.scope = scope;
    }

	public SynchronizationModeAbstract getSynchroMode() {
		return synchroMode;
	}

	public void setSynchroMode(SynchronizationModeAbstract synchroMode) {
		this.synchroMode = synchroMode;
	}
	
	public void setSynchronizationMode(LocalSynchronizationMode synchroMode)
	{
		DEBUG.OUT("set synchroMode " + synchroMode.getClass());
		this.synchroMode = synchroMode;
	}
	public void setSynchronizationMode(DistantSynchronizationMode synchroMode)
	{
		DEBUG.OUT("set setDistantSynchronizationMode " + synchroMode.getClass());
		this.synchroMode = synchroMode;
	}	
	
	public void updateProxied(DistantSynchronizationMode synchroMode)
	{
		DEBUG.OUT("updateProxied with :" + synchroMode.getClass());
		this.setSynchronizationMode(synchroMode);
	}	
	
	@Override
	public IAgent getAgent() {
		DEBUG.OUT("getAgent() " + synchroMode.getAgent());
		return this.getSynchroMode().getAgent();
	}

	@Override
	public void setAgent(IAgent agent) {
		DEBUG.OUT("setAgent() " + agent);
		this.getSynchroMode().setAgent(agent);
	}
	
	public IPopulation<?> getProxyPopulation() {
		DEBUG.OUT("getProxyPopulation() " + this.population);
		return this.population;
	}
	
	@Override
	public IMap<String, Object> getOrCreateAttributes() {
		DEBUG.OUT("getOrCreateAttributes " + this.getSynchroMode().getOrCreateAttributes());
		return this.getSynchroMode().getOrCreateAttributes();
	}

	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		return this.getSynchroMode().stringValue(scope);
	}

	@Override
	public Object getAttribute(String key) {
		DEBUG.OUT("getAttribute ProxyAgent " + key);
		return this.getSynchroMode().getAttribute(key);
	}

	@Override
	public void setAttribute(String key, Object value) {
		DEBUG.OUT("setAttribute ProxyAgent " + key + " :: " + value);
		this.getSynchroMode().setAttribute(key, value);
	}

	@Override
	public boolean hasAttribute(String key) {
		DEBUG.OUT("hasAttribute " + this.getSynchroMode().hasAttribute(key));
		return this.getSynchroMode().hasAttribute(key);
	}

	@Override
	public GamaPoint getLocation() {
		//DEBUG.OUT("getLocation " + this.getSynchroMode().getLocation());
		return this.getSynchroMode().getLocation();
	}

	@Override
	public GamaPoint setLocation(GamaPoint l) {
		return this.getSynchroMode().setLocation(l);
	}

	@Override
	public boolean dead() {
		return this.getSynchroMode().dead();
	}

	@Override
	public void updateWith(IScope s, SavedAgent sa) {
		this.getSynchroMode().updateWith(s, sa);
	}

	@Override
	public IShape copy(IScope scope) {
		return this.getSynchroMode().copy(scope);
	}

	@Override
	public void dispose() {
		DEBUG.OUT("DISPOSING proxyAgent " + this.getSynchroMode());
		this.getSynchroMode().dispose();
	}
	
	@Override
	public boolean init(IScope scope) throws GamaRuntimeException {
		return this.getSynchroMode().init(scope);
	}

	@Override
	public boolean step(IScope scope) throws GamaRuntimeException 
	{
		DEBUG.OUT("proxy step : ");
		return this.getSynchroMode().step(scope);
	}

	@Override
	public Object get(IScope scope, String index) throws GamaRuntimeException {
		return this.getSynchroMode().get(scope, index);
	}

	@Override
	public String getName() {
		return this.getSynchroMode().getName();
	}

	@Override
	public void setName(String name) {
		this.getSynchroMode().setName(name);
	}

	@Override
	public GamaPoint getLocation(IScope scope) {
		return this.getSynchroMode().getLocation(scope);
	}

	@Override
	public GamaPoint setLocation(IScope scope, GamaPoint l) {
		return this.getSynchroMode().setLocation(scope, l);
	}

	@Override
	public IShape getGeometry(IScope scope) {
		return this.getSynchroMode().getGeometry(scope);
	}
	
	@Override
	public IShape getGeometry() {
		return this.getSynchroMode().getGeometry();
	}

	@Override
	public void setGeometry(IScope scope, IShape newGeometry) {
		this.getSynchroMode().setGeometry(scope, newGeometry);
	}

	@Override
	public void schedule(IScope scope) {
		if (!dead()) { 
			scope.init(this); 
		}
	}

	@Override
	public int getIndex() {
		return this.getSynchroMode().getIndex();
	}

	@Override
	public String getSpeciesName() {
		return this.getSynchroMode().getSpeciesName();
	}

	@Override
	public ISpecies getSpecies() {
		return this.getSynchroMode().getSpecies();
	}

	@Override
	public IPopulation<? extends IAgent> getPopulation() {
		return this.getSynchroMode().getPopulation();
	}

	@Override
	public boolean isInstanceOf(ISpecies s, boolean direct) {
		return this.getSynchroMode().isInstanceOf(s, direct);
	}

	@Override
	public Object getDirectVarValue(IScope scope, String s) throws GamaRuntimeException {
		return this.getSynchroMode().getDirectVarValue(scope, s);
	}

	@Override
	public void setDirectVarValue(IScope scope, String s, Object v) throws GamaRuntimeException {
		this.getSynchroMode().setDirectVarValue(scope, s, v);
	}

	@Override
	public IModel getModel() {
		return this.getSynchroMode().getModel();
	}

	@Override
	public Object primDie(IScope scope) throws GamaRuntimeException {
		DEBUG.OUT("do primDie");
		population.fireAgentRemoved(scope, this.getSynchroMode().getAgent());
		return this.getSynchroMode().primDie(scope);
	}

	@Override
	public Type getGeometricalType() {
		return this.getSynchroMode().getGeometricalType();
	}

	@Override
	public void forEachAttribute(BiConsumerWithPruning<String, Object> visitor) {
		this.getSynchroMode().forEachAttribute(visitor);
		
	}

	@Override
	public int compareTo(IAgent o) {
		return (this.getHashCode() == ((MinimalAgent) o).hashCode) ? 1 : 0;
	}

	@Override
	public Object getFromIndicesList(IScope scope, IList<String> indices) throws GamaRuntimeException {
		return this.getSynchroMode().getFromIndicesList(scope, indices);
	}

	@Override
	public ITopology getTopology() {
		return this.getSynchroMode().getTopology();
	}

	@Override
	public void setPeers(IList<IAgent> peers) {
		this.getSynchroMode().setPeers(peers);
	}

	@Override
	public IList<IAgent> getPeers() throws GamaRuntimeException {
		return this.getSynchroMode().getPeers();
	}

	@Override
	public IMacroAgent getHost() {
		return this.getSynchroMode().getHost();
	}

	@Override
	public void setHost(IMacroAgent macroAgent) {
		this.getSynchroMode().setHost(macroAgent);
	}

	@Override
	public List<IAgent> getMacroAgents() {
		return this.getSynchroMode().getMacroAgents();
	}

	@Override
	public boolean isInstanceOf(String skill, boolean direct) {
		return this.getSpecies().implementsSkill(skill);
	}

	@Override
	public IPopulation<? extends IAgent> getPopulationFor(ISpecies microSpecies) {
		DEBUG.OUT("???");
		return this.getScope().getSimulation().getPopulationFor(microSpecies);
	}

	@Override
	public IPopulation<? extends IAgent> getPopulationFor(String speciesName) {
		return this.getScope().getSimulation().getMicroPopulation(speciesName);
	}
	

	public int getHashCode() {
		return this.getSynchroMode().getHashcode();
	}
	
	public void proxyDispose()
	{
		this.getProxyPopulation().remove(this);
	}

	@Override
	public IScope getScope() {
		return this.scope;
	}
	
	@Override
	public boolean covers(IShape g)
	{
		return this.getSynchroMode().covers(g);
	}
	
	@Override
	public boolean intersects(IShape g)
	{
		return this.getSynchroMode().intersects(g);
	}
	
	@Override
	public boolean crosses(IShape g)
	{
		return this.getSynchroMode().crosses(g);
	}
	
	@Override
	public void setInnerGeometry(final Geometry geom) {
		this.getGeometry().setInnerGeometry(geom);
	}

	@Override
	public IList<GamaPoint> getPoints() {
		if (this.getGeometry() == null) return GamaListFactory.EMPTY_LIST;
		return this.getGeometry().getPoints();
	}

	@Override
	public void setDepth(final double depth) 
	{
		if (this.getGeometry() == null) return;
		this.getGeometry().setDepth(depth);
	}
	
	@Override
	public void setGeometricalType(final IShape.Type t) 
	{
		this.getSynchroMode().setGeometricalType(t);
	}

	
	@Override
	public int intValue(final IScope scope) 
	{
		return this.getIndex();
	}
	
	@Override
	public Double getArea() { return this.getGeometry().getArea(); }

	@Override
	public Double getVolume() { return this.getGeometry().getVolume(); }

	@Override
	public double getPerimeter() { return this.getGeometry().getPerimeter(); }

	@Override
	public IList<GamaShape> getHoles() { return this.getGeometry().getHoles(); }

	@Override
	public GamaPoint getCentroid() { return this.getGeometry().getCentroid(); }

	@Override
	public GamaShape getExteriorRing(final IScope scope) {
		return this.getGeometry().getExteriorRing(scope);
	}

	@Override
	public Double getWidth() { return this.getGeometry().getWidth(); }

	@Override
	public Double getHeight() { return this.getGeometry().getHeight(); }

	@Override
	public Double getDepth() { return this.getGeometry().getDepth(); }

	@Override
	public GamaShape getGeometricEnvelope() { return this.getGeometry().getGeometricEnvelope(); }

	@Override
	public IList<? extends IShape> getGeometries() { return this.getGeometry().getGeometries(); }

	@Override
	public boolean isMultiple() { return this.getGeometry().isMultiple(); }

	@Override
	public boolean isPoint() { return this.getGeometry().isPoint(); }

	@Override
	public boolean isLine() { return this.getGeometry().isLine(); }

	@Override
	public Geometry getInnerGeometry() { return this.getGeometry().getInnerGeometry(); }

	@Override
	public Envelope3D getEnvelope() {
		final IShape g = this.getGeometry();
		return g == null ? null : g.getEnvelope();
	}
	@Override
	public double euclidianDistanceTo(final IShape g) {
		return this.getGeometry().euclidianDistanceTo(g);
	}
	@Override
	public double euclidianDistanceTo(final GamaPoint g) {
		return this.getGeometry().euclidianDistanceTo(g);
	}
	
	@Override
	public boolean partiallyOverlaps(final IShape g) {
		return this.getGeometry().partiallyOverlaps(g);
	}
	
	@Override
	public boolean touches(final IShape g) {
		return this.getGeometry().touches(g);
	}
}
