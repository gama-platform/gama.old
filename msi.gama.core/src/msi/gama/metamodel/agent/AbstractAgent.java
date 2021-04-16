/*******************************************************************************************************
 *
 * msi.gama.metamodel.agent.AbstractAgent.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.agent;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.locationtech.jts.geom.Geometry;

import com.google.common.primitives.Ints;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.interfaces.BiConsumerWithPruning;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.model.IModel;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import msi.gaml.variables.IVariable;

/**
 *
 * Class AbstractAgent. An abstract class that tries to minimize the number of attributes manipulated by agents. In
 * particular, it declares no Geometry (leaving the programmer the possibility to redeclare getGeometry(), for example
 * in a dynamic fashion), no Population (leaving the programmer the possibility to redeclare getPopulation(), for
 * example in a dynamic fashion, etc.)
 *
 * These agents have no sub-population by default (but subclasses can be declared by implementing IMacroAgent, and the
 * appropriate methods can be redefined). Their name is fixed by construction (but subclasses can always implement a
 * name).
 *
 * From a functional point of view, this class delegates most of its methods to either the geometry (by calling
 * getGeometry()) or the population (by calling getPopulation()).
 *
 * Furthermore, and contrary to GamlAgent, this class does not delegate its step() and init() behaviors to GAML actions
 * (_init_ and _step_).
 *
 * Most of the methods observe a "fail-fast" pattern. That is, if either the population or the geometry of the agent is
 * null, it throws an exception and does not attempt to return guessed values.
 *
 * Abstract methods to override: - getGeometry() - getPopulation()
 *
 * @author drogoul
 * @since 18 mai 2013
 *
 */
public abstract class AbstractAgent implements IAgent {

	private final int index;
	protected volatile boolean dead = false;
	protected volatile boolean dying = false;

	public AbstractAgent(final int index) {
		this.index = index;
	}

	@Override
	public IAgent getAgent() {
		return this;
	}

	@Override
	public void setAgent(final IAgent agent) {}

	@Override
	public boolean isPoint() {
		return getGeometry().isPoint();
	}

	@Override
	public boolean isLine() {
		return getGeometry().isLine();
	}

	/**
	 * @see msi.gama.interfaces.IShape#getInnerGeometry()
	 */
	@Override
	public Geometry getInnerGeometry() {
		return getGeometry().getInnerGeometry();
	}

	/**
	 * Returns the envelope of the geometry of the agent, or null if the geometry has not yet been defined
	 *
	 * @see msi.gama.interfaces.IShape#getEnvelope()
	 */
	@Override
	public Envelope3D getEnvelope() {
		final IShape g = getGeometry();
		return g == null ? null : g.getEnvelope();
	}

	/**
	 * @see msi.gama.interfaces.IShape#covers(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean covers(final IShape g) {
		return getGeometry().covers(g);
	}

	/**
	 * @see msi.gama.interfaces.IShape#euclidianDistanceTo(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public double euclidianDistanceTo(final IShape g) {
		return getGeometry().euclidianDistanceTo(g);
	}

	@Override
	public double euclidianDistanceTo(final ILocation g) {
		return getGeometry().euclidianDistanceTo(g);
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#intersects(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean intersects(final IShape g) {
		return getGeometry().intersects(g);
	}

	@Override
	public boolean crosses(final IShape g) {
		return getGeometry().crosses(g);
	}

	/**
	 * @see msi.gama.common.interfaces.IGeometry#setInnerGeometry(org.locationtech.jts.geom.Geometry)
	 */
	@Override
	public void setInnerGeometry(final Geometry geom) {
		getGeometry().setInnerGeometry(geom);
	}

	@Override
	public void dispose() {
		if (dead) return;
		dead = true;
		final IPopulation<? extends IAgent> p = getPopulation();
		if (p != null) { p.removeValue(null, this); }
		final IShape s = getGeometry();
		if (s != null) { s.dispose(); }

	}

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return serialize(true);
	}

	@Override
	public IShape copy(final IScope scope) throws GamaRuntimeException {
		return this;
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		final StringBuilder sb = new StringBuilder(30);
		// AD. See issue #3053
		sb.append(getSpeciesName()).append('[').append(getIndex()).append(']');
		if (dead()) { sb.append(" /* dead */"); }
		return sb.toString();
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void setExtraAttributes(final Map<String, Object> map) {
		if (map == null) return;
		getOrCreateAttributes().putAll(map);
	}
	//
	// @Override
	// public GamaMap<String, Object> getAttributes() {
	// return (GamaMap<String, Object>) getGeometry().getAttributes();
	// }

	@Override
	public IMap<String, Object> getOrCreateAttributes() {
		return getGeometry().getOrCreateAttributes();
	}

	@Override
	public boolean hasAttribute(final String key) {
		return getGeometry().hasAttribute(key);
	}

	@Override
	public void forEachAttribute(final BiConsumerWithPruning<String, Object> visitor) {
		getGeometry().forEachAttribute(visitor);
	}

	@Override
	public Object getAttribute(final String key) {
		return getGeometry().getAttribute(key);
	}

	@Override
	public void setAttribute(final String name, final Object val) {
		getOrCreateAttributes().put(name, val);
	}

	@Override
	public int compareTo(final IAgent o) {
		return Ints.compare(getIndex(), o.getIndex());
	}

	@Override
	public boolean init(final IScope scope) throws GamaRuntimeException {
		return getSpecies().getArchitecture().init(scope) ? initSubPopulations(scope) : false;
	}

	/**
	 * Method called repetitively by the simulation engine. Should not be redefined except in rare cases (like special
	 * forms of experiments, which need to define their own sequence)
	 */
	@Override
	public boolean step(final IScope scope) throws GamaRuntimeException {
		boolean result = false;
		try {
			return result = preStep(scope) ? doStep(scope) : false;
		} finally {
			if (result) { postStep(scope); }
		}
	}

	/**
	 * This method contains everything to do *before* the actual step is done (runs of reflexes, etc.). The basis
	 * consists in updating the variables.
	 *
	 * @param scope
	 *            the scope in which the agent is asked to do the preStep()
	 * @return r
	 */
	protected boolean preStep(final IScope scope) {
		return scope.update(this).passed();
	}

	/**
	 * This method contains everything to do *during* during the step of an agent. The basis consists in asking the
	 * architecture to execute on this and, if successfull, to step its sub-populations (if any). Only called if the
	 * preStep() method has been sucessfull
	 *
	 * @param scope
	 *            the scope in which the agent is asked to do the step
	 * @return whether or not the step has been successful (i.e. no errors, etc.)
	 */
	protected boolean doStep(final IScope scope) {
		return scope.execute(getSpecies().getArchitecture(), this, null).passed() ? stepSubPopulations(scope) : false;
	}

	protected boolean initSubPopulations(final IScope scope) {
		return true;
	}

	protected boolean stepSubPopulations(final IScope scope) {
		return true;
	}

	/**
	 * This method contains everything to do *after* the actual step of the agent has been done. Only called if the
	 * doStep() method has been successful.
	 *
	 * @param scope
	 */
	protected void postStep(final IScope scope) {}

	@Override
	public ITopology getTopology() {
		return getPopulation().getTopology();
	}

	@Override
	public void setPeers(final IList<IAgent> peers) {
		// "peers" is read-only attribute
	}

	@SuppressWarnings ("unchecked")
	@Override
	public IList<IAgent> getPeers() throws GamaRuntimeException {
		if (getHost() == null) return GamaListFactory.EMPTY_LIST;
		final IPopulation<? extends IAgent> pop = getHost().getPopulationFor(this.getSpecies());
		if (pop != null) {
			final IScope scope = getScope();
			final IList<IAgent> retVal =
					GamaListFactory.<IAgent> createWithoutCasting(scope.getType(getSpeciesName()), (List<IAgent>) pop);
			retVal.remove(this);
			return retVal;
		}
		return GamaListFactory.EMPTY_LIST;
	}

	@Override
	public String getName() {
		return getSpeciesName() + getIndex() + (dead() ? " (dead)" : "");
	}

	@Override
	public void setName(final String name) {}

	@Override
	public ILocation getLocation() {
		return getGeometry().getLocation();
	}

	@Override
	public void setLocation(final ILocation l) {
		getGeometry().setLocation(l);
	}

	@Override
	public void setGeometry(final IShape newGeometry) {
		getGeometry().setGeometry(newGeometry);
	}

	@Override
	public boolean dead() {
		return dead;
	}

	@Override
	public IMacroAgent getHost() {
		return getPopulation().getHost();
	}

	@Override
	public void setHost(final IMacroAgent macroAgent) {}

	@Override
	public void schedule(final IScope scope) {
		if (!dead()) { scope.init(this); }
	}

	@Override
	public final int getIndex() {
		return index;
	}

	// @Override
	// public final void setIndex(final int index) {
	// this.index = index;
	// }

	@Override
	public String getSpeciesName() {
		return getSpecies().getName();
	}

	@Override
	public ISpecies getSpecies() {
		return getPopulation().getSpecies();
	}

	@Override
	public boolean isInstanceOf(final ISpecies s, final boolean direct) {
		final ISpecies species = getSpecies();
		if (species == s) return true;
		if (!direct) return species.extendsSpecies(s);
		return false;
	}

	@Override
	public Object getDirectVarValue(final IScope scope, final String n) throws GamaRuntimeException {
		final IVariable var = getPopulation().getVar(n);
		if (var != null) return var.value(scope, this);
		final IMacroAgent host = this.getHost();
		if (host != null) {
			final IVariable varOfHost = host.getPopulation().getVar(n);
			if (varOfHost != null) return varOfHost.value(scope, host);
		}
		return null;
	}

	@Override
	public void setDirectVarValue(final IScope scope, final String s, final Object v) throws GamaRuntimeException {
		final IVariable var = getPopulation().getVar(s);
		if (var != null) {
			var.setVal(scope, this, v);
		} else {
			final IAgent host = this.getHost();
			if (host != null) {
				final IVariable varOfHost = host.getPopulation().getVar(s);
				if (varOfHost != null) { varOfHost.setVal(scope, host, v); }
			}
		}
		// TODO: else ? launch an error ?
	}

	@Override
	public List<IAgent> getMacroAgents() {
		final List<IAgent> retVal = GamaListFactory.create(Types.AGENT);
		IAgent currentMacro = this.getHost();
		while (currentMacro != null) {
			retVal.add(currentMacro);
			currentMacro = currentMacro.getHost();
		}
		return retVal;
	}

	@Override
	public IModel getModel() {
		final IMacroAgent a = getHost();
		if (a == null) return GAMA.getModel();
		return a.getModel();
	}

	// @Override
	// public IExperimentAgent getExperiment() {
	// return getHost().getExperiment();
	// }

	@Override
	public IScope getScope() {
		final IMacroAgent a = getHost();
		if (a == null) return null;
		return a.getScope();
	}

	@Override
	public boolean isInstanceOf(final String skill, final boolean direct) {
		return getSpecies().implementsSkill(skill);
	}

	/**
	 * Method getPopulationFor()
	 *
	 * @see msi.gama.metamodel.agent.IAgent#getPopulationFor(msi.gaml.species.ISpecies)
	 */
	@Override
	public IPopulation<? extends IAgent> getPopulationFor(final ISpecies microSpecies) {

		IPopulation<? extends IAgent> pop = getPopulationFor(microSpecies.getName());
		if (pop == null) {
			final ModelDescription micro = microSpecies.getDescription().getModelDescription();
			final ModelDescription main = (ModelDescription) this.getModel().getDescription();
			if (main.getMicroModel(micro.getAlias()) != null && getHost() != null) {
				pop = getHost().getExternMicroPopulationFor(micro.getAlias() + "." + microSpecies.getName());
			}
		}
		return pop;
	}

	/**
	 * Method getPopulationFor()
	 *
	 * @see msi.gama.metamodel.agent.IAgent#getPopulationFor(java.lang.String)
	 */
	@Override
	public IPopulation<? extends IAgent> getPopulationFor(final String speciesName) {
		final IMacroAgent a = getHost();
		if (a == null) return null;
		return getHost().getPopulationFor(speciesName);
	}

	/**
	 * GAML actions
	 */

	@action (
			name = "debug",
			args = { @arg (
					name = "message",
					type = IType.STRING,
					doc = @doc ("The message to display")) })
	public final Object primDebug(final IScope scope) throws GamaRuntimeException {
		final String m = (String) scope.getArg("message", IType.STRING);
		scope.getGui().getConsole().debugConsole(scope.getClock().getCycle(),
				m + "\nsender: " + Cast.asMap(scope, this, false), scope.getRoot());
		return m;
	}

	@action (
			name = "write",
			args = { @arg (
					name = "message",
					type = IType.STRING,
					doc = @doc ("The message to write")) },
			doc = { @doc (
					value = "",
					deprecated = "Use the 'write' statement instead") })
	@Deprecated
	public final Object primWrite(final IScope scope) throws GamaRuntimeException {
		final String s = (String) scope.getArg("message", IType.STRING);
		scope.getGui().getConsole().informConsole(s, scope.getRoot());
		return s;
	}

	@action (
			name = IKeyword.ERROR,
			args = { @arg (
					name = "message",
					type = IType.STRING,
					doc = @doc ("The message to display")) })
	public final Object primError(final IScope scope) throws GamaRuntimeException {
		final String error = (String) scope.getArg("message", IType.STRING);
		scope.getGui().error(error);
		return error;
	}

	@action (
			name = "tell",
			args = { @arg (
					name = "message",
					type = IType.STRING,
					doc = @doc ("The message to display")) })
	public final Object primTell(final IScope scope) throws GamaRuntimeException {
		final String s = getName() + " says : " + scope.getArg("message", IType.STRING);
		scope.getGui().tell(s);
		return s;
	}

	@action (
			name = "die",
			doc = @doc ("Kills the agent and disposes of it. Once dead, the agent cannot behave anymore"))
	public Object primDie(final IScope scope) throws GamaRuntimeException {
		if (dying) return null;
		dying = true;
		getSpecies().getArchitecture().abort(scope);
		scope.interruptAgent();
		dispose();
		return null;
	}

	@Override
	public Type getGeometricalType() {
		return getGeometry().getGeometricalType();
	}

	@Override
	public IType<?> getGamlType() {
		return getScope().getType(getSpeciesName());
	}

	/**
	 * Method get()
	 *
	 * @see msi.gama.util.IContainer.Addressable#get(msi.gama.runtime.IScope, java.lang.Object)
	 */
	@Override
	public Object get(final IScope scope, final String index) throws GamaRuntimeException {
		if (getPopulation().hasVar(index)) return scope.getAgentVarValue(this, index);
		return getAttribute(index);
	}

	/**
	 * Method getFromIndicesList()
	 *
	 * @see msi.gama.util.IContainer.Addressable#getFromIndicesList(msi.gama.runtime.IScope, msi.gama.util.IList)
	 */
	@Override
	public Object getFromIndicesList(final IScope scope, final IList<String> indices) throws GamaRuntimeException {
		if (indices == null || indices.isEmpty()) return null;
		return get(scope, indices.firstValue(scope));
	}

	public void setDefiningPlugin(final String plugin) {}

	/**
	 * Method getPoints()
	 *
	 * @see msi.gama.metamodel.shape.IShape#getPoints()
	 */
	@Override
	public IList<? extends ILocation> getPoints() {
		if (getGeometry() == null) return GamaListFactory.EMPTY_LIST;
		return getGeometry().getPoints();
	}

	@Override
	public void setDepth(final double depth) {
		if (getGeometry() == null) return;
		getGeometry().setDepth(depth);
	}

	@Override
	public void updateWith(final IScope scope, final SavedAgent sa) {
		// Update attributes
		final Map<String, Object> mapAttr = sa.getVariables();
		for (final Entry<String, Object> attr : mapAttr.entrySet()) {
			this.setDirectVarValue(scope, attr.getKey(), attr.getValue());
		}

	}

}
