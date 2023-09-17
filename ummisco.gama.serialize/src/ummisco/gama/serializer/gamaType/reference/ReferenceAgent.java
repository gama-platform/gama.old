/*******************************************************************************************************
 *
 * ReferenceAgent.java, in ummisco.gama.serialize, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gama.serializer.gamaType.reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

	/**
	 * Gets the referenced agent.
	 *
	 * @param sim
	 *            the sim
	 * @return the referenced agent
	 */
	public IAgent getReferencedAgent(final SimulationAgent sim) {
		return attributeValue.getReferencedAgent(sim);
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	/**
	 * Copy.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @return the i shape
	 * @date 17 sept. 2023
	 */
	@Override
	public IShape copy(final IScope scope) {

		return null;
	}

	@Override
	public boolean covers(final IShape g) {

		return false;
	}

	@Override
	public boolean crosses(final IShape g) {

		return false;
	}

	@Override
	public void setGeometricalType(final Type t) {}

	/**
	 * Dispose.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 17 sept. 2023
	 */
	@Override
	public void dispose() {

	}

	@Override
	public double euclidianDistanceTo(final GamaPoint g) {

		return 0;
	}

	@Override
	public double euclidianDistanceTo(final IShape g) {

		return 0;
	}

	/**
	 * Gets the agent.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the agent
	 * @date 17 sept. 2023
	 */
	@Override
	public IAgent getAgent() {

		return null;
	}

	@Override
	public Envelope3D getEnvelope() {

		return null;
	}

	/**
	 * Gets the geometrical type.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the geometrical type
	 * @date 17 sept. 2023
	 */
	@Override
	public Type getGeometricalType() {

		return null;
	}

	@Override
	public Geometry getInnerGeometry() {

		return null;
	}

	@Override
	public boolean intersects(final IShape g) {

		return false;
	}

	@Override
	public boolean touches(final IShape g) {

		return false;
	}

	@Override
	public boolean partiallyOverlaps(final IShape g) {

		return false;
	}

	@Override
	public boolean isLine() {

		return false;
	}

	@Override
	public boolean isPoint() {

		return false;
	}

	/**
	 * Sets the agent.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param agent
	 *            the new agent
	 * @date 17 sept. 2023
	 */
	@Override
	public void setAgent(final IAgent agent) {

	}

	@Override
	public void setInnerGeometry(final Geometry intersection) {

	}

	@Override
	public void setDepth(final double depth) {

	}

	/**
	 * Gets the or create attributes.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the or create attributes
	 * @date 17 sept. 2023
	 */
	@Override
	public IMap<String, Object> getOrCreateAttributes() {

		return null;
	}

	@Override
	public boolean isMultiple() {

		return false;
	}

	@Override
	public Double getArea() {

		return null;
	}

	@Override
	public Double getVolume() {

		return null;
	}

	@Override
	public double getPerimeter() {

		return 0;
	}

	@Override
	public IList<GamaShape> getHoles() {

		return null;
	}

	@Override
	public GamaPoint getCentroid() {

		return null;
	}

	@Override
	public GamaShape getExteriorRing(final IScope scope) {

		return null;
	}

	@Override
	public Double getWidth() {

		return null;
	}

	@Override
	public Double getHeight() {

		return null;
	}

	@Override
	public Double getDepth() {

		return null;
	}

	@Override
	public GamaShape getGeometricEnvelope() {

		return null;
	}

	@Override
	public IList<GamaPoint> getPoints() { return null; }

	@Override
	public IList<? extends IShape> getGeometries() {

		return null;
	}

	/**
	 * String value.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @return the string
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 * @date 17 sept. 2023
	 */
	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {

		return null;
	}

	/**
	 * Serialize.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param includingBuiltIn
	 *            the including built in
	 * @return the string
	 * @date 17 sept. 2023
	 */
	@Override
	public String serialize(final boolean includingBuiltIn) {

		return null;
	}

	/**
	 * Gets the gaml type.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the gaml type
	 * @date 17 sept. 2023
	 */
	@Override
	public IType<?> getGamlType() {

		return null;
	}

	/**
	 * Gets the attributes.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the attributes
	 * @date 17 sept. 2023
	 */
	@Override
	public Map<String, Object> getAttributes() { return null; }

	/**
	 * Compare to.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param o
	 *            the o
	 * @return the int
	 * @date 17 sept. 2023
	 */
	@Override
	public int compareTo(final IAgent o) {

		return 0;
	}

	/**
	 * Inits the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @return true, if successful
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 * @date 17 sept. 2023
	 */
	@Override
	public boolean init(final IScope scope) throws GamaRuntimeException {

		return false;
	}

	/**
	 * Step.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @return true, if successful
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 * @date 17 sept. 2023
	 */
	@Override
	public boolean step(final IScope scope) throws GamaRuntimeException {

		return false;
	}

	/**
	 * Gets the.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param index
	 *            the index
	 * @return the object
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 * @date 17 sept. 2023
	 */
	@Override
	public Object get(final IScope scope, final String index) throws GamaRuntimeException {

		return null;
	}

	/**
	 * Gets the from indices list.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @param indices
	 *            the indices
	 * @return the from indices list
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 * @date 17 sept. 2023
	 */
	@Override
	public Object getFromIndicesList(final IScope scope, final IList<String> indices) throws GamaRuntimeException {
		return null;
	}

	/**
	 * Gets the scope.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the scope
	 * @date 17 sept. 2023
	 */
	@Override
	public IScope getScope() { return null; }

	@Override
	public ITopology getTopology() { return null; }

	@Override
	public void setPeers(final IList<IAgent> peers) {

	}

	@Override
	public IList<IAgent> getPeers() throws GamaRuntimeException { return null; }

	@Override
	public String getName() { return null; }

	@Override
	public void setName(final String name) {

	}

	@Override
	public GamaPoint getLocation(final IScope scope) {
		return null;
	}

	@Override
	public GamaPoint setLocation(final IScope scope, final GamaPoint l) {
		return l;
	}

	@Override
	public IShape getGeometry(final IScope scope) {
		return null;
	}

	@Override
	public void setGeometry(final IScope scope, final IShape newGeometry) {}

	@Override
	public boolean dead() {
		return false;
	}

	@Override
	public IMacroAgent getHost() { return null; }

	@Override
	public void setHost(final IMacroAgent macroAgent) {}

	@Override
	public void schedule(final IScope scope) {}

	@Override
	public int getIndex() { return 0; }

	@Override
	public String getSpeciesName() { return null; }

	@Override
	public ISpecies getSpecies() { return null; }

	@Override
	public IPopulation<? extends IAgent> getPopulation() { return null; }

	@Override
	public boolean isInstanceOf(final ISpecies s, final boolean direct) {
		return false;
	}

	@Override
	public Object getDirectVarValue(final IScope scope, final String s) throws GamaRuntimeException {
		return null;
	}

	@Override
	public void setDirectVarValue(final IScope scope, final String s, final Object v) throws GamaRuntimeException {

	}

	@Override
	public List<IAgent> getMacroAgents() { return null; }

	@Override
	public IModel getModel() { return null; }

	@Override
	public boolean isInstanceOf(final String skill, final boolean direct) {
		return false;
	}

	@Override
	public IPopulation<? extends IAgent> getPopulationFor(final ISpecies microSpecies) {
		return null;
	}

	@Override
	public IPopulation<? extends IAgent> getPopulationFor(final String speciesName) {
		return null;
	}

	@Override
	public void updateWith(final IScope s, final SavedAgent sa) {

	}

	/**
	 * For each attribute.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param visitor
	 *            the visitor
	 * @date 17 sept. 2023
	 */
	@Override
	public void forEachAttribute(final BiConsumerWithPruning<String, Object> visitor) {}

	@Override
	public Object primDie(final IScope scope) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}
}
