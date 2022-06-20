/*******************************************************************************************************
 *
 * ReferenceAgent.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.gamaType.reference;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Geometry;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.interfaces.BiConsumerWithPruning;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
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
import msi.gama.util.IReference;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

/**
 * The Class ReferenceAgent.
 */
public class ReferenceAgent implements IReference, IAgent {

	/** The agt attr. */
	ArrayList<AgentAttribute> agtAttr;

	/** The attribute value. */
	ReferenceToAgent attributeValue;

	/**
	 * Instantiates a new reference agent.
	 *
	 * @param _agt
	 *            the agt
	 * @param agtAttrName
	 *            the agt attr name
	 * @param agtAttrValue
	 *            the agt attr value
	 */
	public ReferenceAgent(final IAgent _agt, final String agtAttrName, final IAgent agtAttrValue) {
		// super(null,-1);
		agtAttr = new ArrayList<>();

		if (_agt != null && agtAttrName != null) { agtAttr.add(new AgentAttribute(_agt, agtAttrName)); }
		attributeValue = new ReferenceToAgent(agtAttrValue);
	}

	/**
	 * Instantiates a new reference agent.
	 *
	 * @param refAgt
	 *            the ref agt
	 * @param attrName
	 *            the attr name
	 * @param refAttrValue
	 *            the ref attr value
	 */
	public ReferenceAgent(final IAgent refAgt, final String attrName, final ReferenceToAgent refAttrValue) {
		// super(null,-1);

		agtAttr = new ArrayList<>();
		if (refAgt != null && attrName != null) { agtAttr.add(new AgentAttribute(refAgt, attrName)); }
		attributeValue = refAttrValue;
	}

	/**
	 * Gets the attribute value.
	 *
	 * @return the attribute value
	 */
	public ReferenceToAgent getAttributeValue() { return attributeValue; }

	@Override
	public Object constructReferencedObject(final SimulationAgent sim) {
		return getAttributeValue().getReferencedAgent(sim);
	}

	@Override
	public ArrayList<AgentAttribute> getAgentAttributes() { return agtAttr; }

	@Override
	public boolean equals(final Object o) {
		if (o == this) return true;
		return false;
	}
	
	public IAgent getReferencedAgent(final SimulationAgent sim) {
		return attributeValue.getReferencedAgent(sim);
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public IShape copy(final IScope scope) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean covers(final IShape g) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean crosses(final IShape g) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setGeometricalType(final Type t) {}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public double euclidianDistanceTo(final GamaPoint g) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double euclidianDistanceTo(final IShape g) {
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
	public boolean intersects(final IShape g) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touches(final IShape g) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean partiallyOverlaps(final IShape g) {
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
	public void setAgent(final IAgent agent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setInnerGeometry(final Geometry intersection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDepth(final double depth) {
		// TODO Auto-generated method stub

	}

	@Override
	public IMap<String, Object> getOrCreateAttributes() {
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
	public GamaShape getExteriorRing(final IScope scope) {
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
	public IList<GamaPoint> getPoints() { return null; }

	@Override
	public IList<? extends IShape> getGeometries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IType<?> getGamlType() {
		// TODO Auto-generated method stub
		return null;
	}
	//
	// @Override
	// public Map<String, Object> getAttributes() {
	// // TODO Auto-generated method stub
	// return null;
	// }

	@Override
	public Object getAttribute(final String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttribute(final String key, final Object value) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasAttribute(final String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int compareTo(final IAgent o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean init(final IScope scope) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean step(final IScope scope) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object get(final IScope scope, final String index) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getFromIndicesList(final IScope scope, final IList<String> indices) throws GamaRuntimeException {
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
	public void setPeers(final IList<IAgent> peers) {
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
	public void setName(final String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public GamaPoint getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GamaPoint setLocation(final GamaPoint l) {
		return l;
	}

	@Override
	public IShape getGeometry() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGeometry(final IShape newGeometry) {
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
	public void setHost(final IMacroAgent macroAgent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void schedule(final IScope scope) {
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
	public boolean isInstanceOf(final ISpecies s, final boolean direct) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getDirectVarValue(final IScope scope, final String s) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDirectVarValue(final IScope scope, final String s, final Object v) throws GamaRuntimeException {
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
	public boolean isInstanceOf(final String skill, final boolean direct) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IPopulation<? extends IAgent> getPopulationFor(final ISpecies microSpecies) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPopulation<? extends IAgent> getPopulationFor(final String speciesName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateWith(final IScope s, final SavedAgent sa) {
		// TODO Auto-generated method stub

	}

	@Override
	public void forEachAttribute(final BiConsumerWithPruning<String, Object> visitor) {}

	@Override
	public int getUniqueID() {
		// TODO Auto-generated method stub
		return -79;
	}

	@Override
	public void setUniqueID(int uID) {
	}
}
