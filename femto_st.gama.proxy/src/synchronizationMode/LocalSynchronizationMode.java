package synchronizationMode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
public class LocalSynchronizationMode implements SynchronizationModeAbstract
{
	static
	{
		DEBUG.OFF();
	}
	
	public IAgent proxiedAgent;
	private Set<Integer> procsWithDistantAgent;
	
	public Set<Integer> getProcsWithDistantAgent() {
		return procsWithDistantAgent;
	}

	public void setProcsWithDistantAgent(Set<Integer> procsWithDistantAgent) {
		this.procsWithDistantAgent = procsWithDistantAgent;
	}

	public LocalSynchronizationMode(IAgent proxiedAgent)
	{
		this.proxiedAgent = proxiedAgent;
		procsWithDistantAgent = new HashSet<Integer>();
	}
	
	public LocalSynchronizationMode(){
		procsWithDistantAgent = new HashSet<Integer>();
	}
	
	public void addProcs(int procNumber) // TODO : define when to call
	{
		procsWithDistantAgent.add(procNumber);
	}
	
	public void removeProcs(int procNumber) // TODO : define when to call
	{
		procsWithDistantAgent.remove(procNumber);
	}
	
	public void sendUpdate() // TODO : call this at the end of each cycle
	{
		// link this with endActionProxy
	}
	
	@Override
	public void setAgent(IAgent agent)
	{
		this.proxiedAgent = agent;
	}
	
	@Override
	public IAgent getAgent()
	{
		return this.proxiedAgent;
	}
	
	public void updateProxiedAgent(IAgent agentUpdated)
	{
		setAgent(agentUpdated);
	}
	
	
	@Override
	public IMap<String, Object> getOrCreateAttributes()
	{
		return this.proxiedAgent.getOrCreateAttributes();
	}
	
	
	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException
	{
		return this.proxiedAgent.stringValue(scope);
	}
	
	
	@Override
	public Object getAttribute(String key)
	{
		return this.proxiedAgent.getAttribute(key);
	}

	
	@Override
	public void setAttribute(String key, Object value) 
	{
		this.proxiedAgent.setAttribute(key, value);
	}
	
	
	@Override
	public boolean hasAttribute(String key)
	{
		return this.proxiedAgent.hasAttribute(key);
	}
	
	
	@Override
	public GamaPoint getLocation() 
	{
		return this.proxiedAgent.getLocation();
	}
	
	
	@Override
	public GamaPoint setLocation(GamaPoint l)
	{
		return this.proxiedAgent.setLocation(l);
	}
	
	
	@Override
	public boolean dead() 
	{
		return this.proxiedAgent.dead();
	}
	
	
	@Override
	public void updateWith(IScope s, SavedAgent sa)
	{
		this.proxiedAgent.updateWith(s, sa);
	}
	
	
	@Override
	public IShape copy(IScope scope) {
		return this.proxiedAgent.copy(scope);
	}

	
	@Override
	public void dispose() {
		 this.proxiedAgent.dispose();
	}

	
	@Override
	public IShape.Type getGeometricalType() {
		return this.proxiedAgent.getGeometricalType();
	}

	
	@Override
	public void forEachAttribute(BiConsumerWithPruning<String, Object> visitor) {
		 this.proxiedAgent.forEachAttribute(visitor);
	}

	
	@Override
	public int compareTo(IAgent o) {
		return this.proxiedAgent.compareTo(o);
	}

	
	@Override
	public boolean init(IScope scope) throws GamaRuntimeException {
		return this.proxiedAgent.init(scope);
	}

	
	@Override
	public boolean step(IScope scope) throws GamaRuntimeException {
		return this.proxiedAgent.step(scope);
	}
	
	
	@Override
	public Object get(IScope scope, String index) throws GamaRuntimeException {
		return this.proxiedAgent.get(scope, index);
	}
	
	
	@Override
	public Object getFromIndicesList(IScope scope, IList<String> indices) throws GamaRuntimeException {
		return this.proxiedAgent.getFromIndicesList(scope, indices);
	}

	
	@Override
	public IScope getScope() {
		return this.proxiedAgent.getScope();
	}
	
	
	@Override
	public ITopology getTopology() {
		return this.proxiedAgent.getTopology();
	}
	
	
	@Override
	public void setPeers(IList<IAgent> peers) {
		 this.proxiedAgent.setPeers(peers);
	}
	
	
	@Override
	public IList<IAgent> getPeers() throws GamaRuntimeException {
		return this.proxiedAgent.getPeers();
	}
	
	
	@Override
	public String getName() {
		return this.proxiedAgent.getName();
	}

	
	@Override
	public void setName(String name) {
		this.proxiedAgent.setName(name);	
	}
	
	
	@Override
	public GamaPoint getLocation(IScope scope) {
		return this.proxiedAgent.getLocation(scope);
	}
	
	
	@Override
	public GamaPoint setLocation(IScope scope, GamaPoint l) {
		return this.proxiedAgent.setLocation(scope, l);
	}
	
	
	@Override
	public IShape getGeometry(IScope scope) {
		return this.proxiedAgent.getGeometry(scope);
	}
	
	
	@Override
	public IShape getGeometry() {
		return this.proxiedAgent.getGeometry();
	}
	
	
	@Override
	public void setGeometry(IScope scope, IShape newGeometry) {
		this.proxiedAgent.setGeometry(scope, newGeometry);
	}
	
	
	@Override
	public IMacroAgent getHost() {
		return this.proxiedAgent.getHost();
	}
	
	
	@Override
	public void setHost(IMacroAgent macroAgent) {
		this.proxiedAgent.setHost(macroAgent);
	}
	
	
	@Override
	public void schedule(IScope scope) {
		this.proxiedAgent.schedule(scope);
	}
	
	
	@Override
	public int getIndex() {
		return this.proxiedAgent.getIndex();
	}
	
	
	@Override
	public String getSpeciesName() {
		return this.proxiedAgent.getSpeciesName();
	}

	
	@Override
	public ISpecies getSpecies() {
		return this.proxiedAgent.getSpecies();
	}

	
	@Override
	public IPopulation<? extends IAgent> getPopulation() {
		return this.proxiedAgent.getPopulation();
	}

	
	@Override
	public boolean isInstanceOf(ISpecies s, boolean direct) {
		return this.proxiedAgent.isInstanceOf(s, direct);
	}

	
	@Override
	public Object getDirectVarValue(IScope scope, String s) throws GamaRuntimeException {
	
		return this.proxiedAgent.getDirectVarValue(scope, s);
	}

	
	@Override
	public void setDirectVarValue(IScope scope, String s, Object v) throws GamaRuntimeException {
		this.proxiedAgent.setDirectVarValue(scope, s, v);	
	}

	
	@Override
	public List<IAgent> getMacroAgents() {
		return this.proxiedAgent.getMacroAgents();	
	}

	
	@Override
	public IModel getModel() {
		return this.proxiedAgent.getModel();	
	}

	
	@Override
	public boolean isInstanceOf(String skill, boolean direct) {
		return this.proxiedAgent.isInstanceOf(skill,direct);	
	}

	
	@Override
	public IPopulation<? extends IAgent> getPopulationFor(ISpecies microSpecies) {
		return this.proxiedAgent.getPopulationFor(microSpecies);	
	}

	
	@Override
	public IPopulation<? extends IAgent> getPopulationFor(String speciesName) {
		return this.proxiedAgent.getPopulationFor(speciesName);	
	}	
	 
	
	@Override
	public Object primDie(IScope scope) throws GamaRuntimeException
	{
		DEBUG.OUT("primDIE synchro");
		return this.proxiedAgent.primDie(scope);
	}
	
	@Override
	public void updateAttributes(IAgent agent)
	{
		DEBUG.OUT("updateAttributes SynchronizationMode : " + agent);
	}

	@Override
	public int getHashcode() {
		return ((MinimalAgent)this.proxiedAgent).hashCode;
	}

	
	@Override
	public boolean covers(IShape g) 
	{
		return this.proxiedAgent.covers(g);
	}

	
	@Override
	public boolean intersects(IShape g) {
		return this.proxiedAgent.intersects(g);
	}
	
	@Override
	public boolean crosses(IShape g) {
		return this.proxiedAgent.crosses(g);
	}

	@Override
	public void setInnerGeometry(Geometry geom) {
		this.proxiedAgent.setInnerGeometry(geom);
	}

	@Override
	public IList<GamaPoint> getPoints() {
		return this.proxiedAgent.getPoints();
	}

	@Override
	public void setDepth(double depth) {
		this.proxiedAgent.setDepth(depth);
	}

	@Override
	public void setGeometricalType(Type t) {
		this.proxiedAgent.setGeometricalType(t);
	}

	@Override
	public int intValue(IScope scope) {
		return this.proxiedAgent.intValue(scope);
	}

	@Override
	public Double getArea() {
		return this.proxiedAgent.getArea();
	}

	@Override
	public Double getVolume() {
		return this.proxiedAgent.getVolume();
	}

	@Override
	public double getPerimeter() {
		return this.proxiedAgent.getPerimeter();
	}

	@Override
	public IList<GamaShape> getHoles() {
		return this.proxiedAgent.getHoles();
	}

	@Override
	public GamaPoint getCentroid() {
		return this.proxiedAgent.getCentroid();
	}

	@Override
	public GamaShape getExteriorRing(IScope scope) {
		return this.proxiedAgent.getExteriorRing(scope);
	}

	@Override
	public Double getWidth() {
		return this.proxiedAgent.getWidth();
	}

	@Override
	public Double getHeight() {
		return this.proxiedAgent.getHeight();
	}

	@Override
	public Double getDepth() {
		return this.proxiedAgent.getDepth();
	}

	@Override
	public GamaShape getGeometricEnvelope() {
		return this.proxiedAgent.getGeometricEnvelope();
	}

	@Override
	public IList<? extends IShape> getGeometries() {
		return this.proxiedAgent.getGeometries();
	}

	@Override
	public boolean isMultiple() {
		return this.proxiedAgent.isMultiple();
	}

	@Override
	public boolean isPoint() {
		return this.proxiedAgent.isPoint();
	}

	@Override
	public boolean isLine() {
		return this.proxiedAgent.isLine();
	}

	@Override
	public Geometry getInnerGeometry() {
		return this.proxiedAgent.getInnerGeometry();
	}

	@Override
	public Envelope3D getEnvelope() {
		return this.proxiedAgent.getEnvelope();
	}

	@Override
	public double euclidianDistanceTo(IShape g) {
		return this.proxiedAgent.euclidianDistanceTo(g);
	}

	@Override
	public double euclidianDistanceTo(GamaPoint g) {
		return this.proxiedAgent.euclidianDistanceTo(g);
	}

	@Override
	public boolean partiallyOverlaps(IShape g) {
		return this.proxiedAgent.partiallyOverlaps(g);
	}

	@Override
	public boolean touches(IShape g) {
		return this.proxiedAgent.touches(g);
	}

	@Override
	public void stepProxy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object _init_(IScope scope) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean initSubPopulations(IScope scope) {
		// TODO Auto-generated method stub
		return false;
	}
}
