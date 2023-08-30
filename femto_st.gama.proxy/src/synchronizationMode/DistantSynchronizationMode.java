package synchronizationMode;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.locationtech.jts.geom.Geometry;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.interfaces.BiConsumerWithPruning;
import msi.gama.common.interfaces.IKeyword;
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
import msi.gama.util.GamaColor;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.species.ISpecies;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Control the access of agent from a distant processor / simulation
 * 
 * @author Lucas Grosjean
 *
 */
public class DistantSynchronizationMode implements SynchronizationModeAbstract
{	
	static
	{
		DEBUG.OFF();
	}
	
	IMap<String, Object> attributes;
	
	public DistantSynchronizationMode(IScope scope, IAgent agentToDistantProxy)
	{
		DEBUG.OUT("agentToDistantProxy type " + agentToDistantProxy.getGamlType().toString());
		DEBUG.OUT("agentToDistantProxy : " + agentToDistantProxy);
		
		updateAttributes(agentToDistantProxy);
	}
	
	@Override
	public void setAgent(IAgent agent)
	{
	}
		
	@Override
	public IAgent getAgent()
	{
		return null; // TODO throw exception?
	}
	
	@Override
	public void stepProxy()
	{
		// TODO
	}
	
	public void updateProxiedAgent(IAgent agentUpdated)
	{
		DEBUG.OUT("updateProxiedAgent : " + agentUpdated);
		updateAttributes(agentUpdated);
	}

	@Override
	public void updateAttributes(IAgent agentWithData)
	{
		DEBUG.OUT("agentWithData : " + agentWithData);
		DEBUG.OUT("agentWithData class : " + agentWithData.getClass());
		
		var attributesFromAgentData = agentWithData.getOrCreateAttributes();
		
		var mapAttributes = new GamaMap<String, Object>(attributesFromAgentData.size(), attributesFromAgentData.getKeys().getGamlType(), attributesFromAgentData.getValues().getGamlType());
		for(var auto : attributesFromAgentData.entrySet())
		{
			mapAttributes.put(auto.getKey(), auto.getValue());
		}

		DEBUG.OUT("mapAttributes = " + mapAttributes);
		this.attributes = mapAttributes;

		this.attributes.put(IKeyword.TYPE, agentWithData.getGamlType().toString());

		this.attributes.put(IKeyword.NAME, agentWithData.getName());
		this.attributes.put(IKeyword.SHAPE, agentWithData.getGeometry());
		this.attributes.put(IKeyword.LOCATION, agentWithData.getLocation());
		this.attributes.put(IKeyword.HASHCODE, ((MinimalAgent)agentWithData).hashCode);
		this.attributes.put(IKeyword.COLOR_ATTRIBUTE, new GamaColor(122,122,122));
		this.attributes.put(IKeyword.POPULATION, agentWithData.getPopulation());
		
		DEBUG.OUT("attributes = " + this.attributes);
		DEBUG.OUT("type of agent = " + this.getAttribute(IKeyword.TYPE));
		DEBUG.OUT("GEOMETRY = " + this.getAttribute(IKeyword.GEOMETRY));
		DEBUG.OUT("location : " + this.getLocation());
		DEBUG.OUT("hashcode : " + this.getAttribute(IKeyword.HASHCODE));
		DEBUG.OUT("color : " + this.getAttribute(IKeyword.COLOR_ATTRIBUTE));
		DEBUG.OUT("population : " + this.getAttribute(IKeyword.POPULATION));
	}
	
	@Override
	public boolean step(IScope scope) throws GamaRuntimeException {
		return true;
	}
	
	@Override
	public IMap<String, Object> getOrCreateAttributes()
	{
		return attributes;
	}

	@Override
	public String stringValue(IScope scope) throws GamaRuntimeException {
		return serialize(true);
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
		return (GamaPoint) attributes.get(IKeyword.LOCATION);
	}
	
	@Override
	public GamaPoint setLocation(GamaPoint l)
	{
		attributes.put(IKeyword.LOCATION, l);
		return l;
	}
	
	@Override
	public boolean dead() 
	{
		return false;
	}
	
	@Override
	public Object getDirectVarValue(IScope scope, String s) throws GamaRuntimeException {
		return this.attributes.get(s);
	}

	
	@Override
	public void setDirectVarValue(IScope scope, String s, Object v) throws GamaRuntimeException {
		this.attributes.put(s, v);
	}
	
	@Override
	public void updateWith(IScope scope, SavedAgent sa)
	{
		final Map<String, Object> mapAttr = sa.getVariables();
		for (final Entry<String, Object> attr : mapAttr.entrySet()) 
		{
			this.setDirectVarValue(scope, attr.getKey(), attr.getValue());
		}
	}
	
	@Override
	public IShape copy(IScope scope) {
		IShape shape = (IShape) this.attributes.get(IKeyword.SHAPE);
		return shape.copy(scope);
	}

	@Override
	public void dispose() 
	{
		// TODO
	}

	@Override
	public boolean init(IScope scope) throws GamaRuntimeException 
	{
		if (!getPopulation().isInitOverriden()) 
		{
			_init_(scope);
		} else {
			scope.execute(getSpecies().getAction(ISpecies.initActionName), this, null);
		}
		return !scope.interrupted();
	}
	
	@Override
	public Object _init_(final IScope scope) {
		return getSpecies().getArchitecture().init(scope) ? initSubPopulations(scope) : false;
	}
	
	@Override
	public boolean initSubPopulations(final IScope scope) {
		return true;
	}

	@Override
	public Object get(final IScope scope, final String index) throws GamaRuntimeException 
	{
		if (getPopulation().hasVar(index)) return scope.getAgentVarValue(this, index);
		return getAttribute(index);
	}
	
	@Override
	public String getName() {
		return this.attributes.get(IKeyword.NAME).toString();
	}
	
	@Override
	public void setName(String name) {
		this.attributes.put(IKeyword.NAME, name);	
	}
	
	@Override
	public GamaPoint getLocation(IScope scope) {
		return (GamaPoint) this.attributes.get(IKeyword.LOCATION);
	}

	
	@Override
	public GamaPoint setLocation(IScope scope, GamaPoint l) {
		this.attributes.put(IKeyword.LOCATION, l);	
		return l;
	}

	
	@Override
	public IShape getGeometry(IScope scope) {
		return (IShape) this.attributes.get(IKeyword.SHAPE);
	}
	
	@Override
	public IShape getGeometry() {
		return (IShape) this.attributes.get(IKeyword.SHAPE);
	}

	
	@Override
	public void setGeometry(IScope scope, IShape newGeometry) {
		this.attributes.put(IKeyword.SHAPE, newGeometry);	
	}
	
	@Override
	public void schedule(IScope scope) {
		if (!dead()) { scope.init(this); }
	}

	
	@Override
	public int getIndex() {
		return 0; // TODO check if correct
	}

	
	@Override
	public String getSpeciesName() {
		return (String) this.attributes.get(IKeyword.TYPE);
	}

	
	@Override
	public ISpecies getSpecies() {
		return getPopulation().getSpecies();
	}
	
	@Override
	public IScope getScope() {
		final IMacroAgent a = getHost();
		if (a == null) return null;
		return a.getScope();
	}
	
	@Override
	public IMacroAgent getHost() {
		return getPopulation().getHost();
	}

	@SuppressWarnings("unchecked")
	@Override
	public IPopulation<? extends IAgent> getPopulation() {
		return (IPopulation<? extends IAgent>) this.attributes.get(IKeyword.POPULATION);
	}

	@Override
	public boolean isInstanceOf(ISpecies s, boolean direct) {
		return this.getSpecies().getName().equals(s.getName());
	}

	@Override
	public IModel getModel() {
		return null;	// TODO check if correct
	}
	 
	@Override
	public Object primDie(IScope scope) throws GamaRuntimeException{
		return null;	// TODO check if correct
	}

	@Override
	public int getHashcode() {
		return (int) this.attributes.get(IKeyword.HASHCODE);
	}
	
	@Override
	public boolean covers(IShape g)
	{
		IShape shape = this.getGeometry();
		return shape.covers(g);
	}
	
	@Override
	public boolean intersects(IShape g)
	{
		IShape shape = this.getGeometry();
		return shape.intersects(shape);
	}
	
	@Override
	public boolean crosses(IShape g)
	{
		IShape shape = this.getGeometry();
		return shape.crosses(shape);
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
		this.getGeometry().setGeometricalType(t);
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

	@Override
	public ITopology getTopology() {
		return null; // OK
	}

	@Override
	public void setPeers(IList<IAgent> peers) { // OK
	}

	@Override
	public IList<IAgent> getPeers() throws GamaRuntimeException {
		return null; // OK
	}

	@Override
	public void setHost(IMacroAgent macroAgent) { // OK
	}

	@Override
	public List<IAgent> getMacroAgents() {
		return null; // OK
	}

	@Override
	public boolean isInstanceOf(String skill, boolean direct) {
		return false; // OK
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getFromIndicesList(IScope scope, IList<String> indices) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}
}
