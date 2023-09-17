/*******************************************************************************************************
 *
 * AbstractAgent.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.agent;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.primitives.Ints;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.model.IModel;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
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

	/** The index. */
	private final int index;

	/** The dead. */
	protected volatile boolean dead = false;

	/** The dying. */
	protected volatile boolean dying = false;

	/**
	 * Instantiates a new abstract agent.
	 *
	 * @param index
	 *            the index
	 */
	protected AbstractAgent(final int index) {
		this.index = index;
	}

	/**
	 * Gets the agent.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the agent
	 * @date 17 sept. 2023
	 */
	@Override
	public IAgent getAgent() { return this; }

	/**
	 * Sets the agent.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param agent
	 *            the new agent
	 * @date 17 sept. 2023
	 */
	@Override
	public void setAgent(final IAgent agent) {}

	/**
	 * Dispose.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 17 sept. 2023
	 */
	@Override
	public void dispose() {
		if (dead) return;
		dead = true;
		final IPopulation<? extends IAgent> p = getPopulation();
		if (p != null) { p.removeValue(null, this); }
		final IShape s = getGeometry();
		if (s != null) { s.dispose(); }

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
		return serialize(true);
	}

	/**
	 * Copy.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @return the i shape
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 * @date 17 sept. 2023
	 */
	@Override
	public IShape copy(final IScope scope) throws GamaRuntimeException {
		return this;
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
		final StringBuilder sb = new StringBuilder(30);
		// AD. See issue #3053
		sb.append(getSpeciesName()).append('(').append(getIndex()).append(')');
		if (dead()) { sb.append(" /* dead */"); }
		return sb.toString();
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Gets the attributes.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the attributes
	 * @date 17 sept. 2023
	 */
	@Override
	public IMap<String, Object> getAttributes() { return (IMap<String, Object>) getGeometry().getAttributes(); }

	/**
	 * Gets the or create attributes.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the or create attributes
	 * @date 17 sept. 2023
	 */
	@Override
	public IMap<String, Object> getOrCreateAttributes() { return getGeometry().getOrCreateAttributes(); }

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
		return Ints.compare(getIndex(), o.getIndex());
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
			result = preStep(scope) && doStep(scope);
			return result;
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

	/**
	 * Inits the sub populations.
	 *
	 * @param scope
	 *            the scope
	 * @return true, if successful
	 */
	protected boolean initSubPopulations(final IScope scope) {
		return true;
	}

	/**
	 * Step sub populations.
	 *
	 * @param scope
	 *            the scope
	 * @return true, if successful
	 */
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
	public ITopology getTopology() { return getPopulation().getTopology(); }

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
	public String getName() { return getSpeciesName() + getIndex() + (dead() ? " (dead)" : ""); }

	@Override
	public void setName(final String name) {}

	@Override
	public GamaPoint getLocation(final IScope scope) {
		return getGeometry().getLocation();
	}

	@Override
	public GamaPoint setLocation(final IScope scope, final GamaPoint l) {
		return getGeometry(scope).setLocation(l);
	}

	@Override
	public void setGeometry(final IScope scope, final IShape newGeometry) {
		getGeometry(scope).setGeometry(newGeometry);
	}

	@Override
	public boolean dead() {
		return dead;
	}

	@Override
	public IMacroAgent getHost() { return getPopulation().getHost(); }

	@Override
	public void setHost(final IMacroAgent macroAgent) {}

	@Override
	public void schedule(final IScope scope) {
		if (!dead()) { scope.init(this); }
	}

	@Override
	public final int getIndex() { return index; }

	// @Override
	// public final void setIndex(final int index) {
	// this.index = index;
	// }

	@Override
	public String getSpeciesName() { return getSpecies().getName(); }

	@Override
	public ISpecies getSpecies() { return getPopulation().getSpecies(); }

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

	/**
	 * Gets the scope.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the scope
	 * @date 17 sept. 2023
	 */
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
			final ModelDescription main = this.getModel().getDescription();
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

	/**
	 * Prim write.
	 *
	 * @param scope
	 *            the scope
	 * @deprecated
	 * @return the object
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = "write",
			args = { @arg (
					name = "message",
					type = IType.STRING,
					doc = @doc ("The message to write")) },
			doc = { @doc (
					value = "",
					deprecated = "Use the 'write' statement instead") })
	@Deprecated (
			forRemoval = true)
	public final Object primWrite(final IScope scope) throws GamaRuntimeException {
		final String s = (String) scope.getArg("message", IType.STRING);
		scope.getGui().getConsole().informConsole(s, scope.getRoot());
		return s;
	}

	/**
	 * Prim error.
	 *
	 * @param scope
	 *            the scope
	 * @return the object
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = IKeyword.ERROR,
			args = { @arg (
					name = "message",
					type = IType.STRING,
					doc = @doc ("The message to display")) })
	public final Object primError(final IScope scope) throws GamaRuntimeException {
		final String error = (String) scope.getArg("message", IType.STRING);
		scope.getGui().openErrorDialog(scope, error);
		return error;
	}

	/**
	 * Prim tell.
	 *
	 * @param scope
	 *            the scope
	 * @return the object
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = "tell",
			args = { @arg (
					name = "msg",
					type = IType.STRING,
					doc = @doc ("The message to display")),
					@arg (
							name = "add_name",
							optional = true,
							type = IType.BOOL,
							doc = @doc ("Should the name of the agent that uses the action be displayed?")) })
	public final Object primTell(final IScope scope) throws GamaRuntimeException {
		boolean addName = !scope.hasArg("add_name") || Cast.asBool(scope, scope.getArg("add_name", IType.BOOL));
		final String s = (addName ? getName() + " says : " : "") + scope.getArg("msg", IType.STRING);
		scope.getGui().openMessageDialog(scope, s);
		return s;
	}

	@Override
	public Object primDie(final IScope scope) throws GamaRuntimeException {
		if (!dying) {
			dying = true;
			getSpecies().getArchitecture().abort(scope);
			scope.setDeathStatus();
			dispose();
		}
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
	public Type getGeometricalType() { return getGeometry().getGeometricalType(); }

	/**
	 * Gets the gaml type.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the gaml type
	 * @date 17 sept. 2023
	 */
	@Override
	public IType<?> getGamlType() { return getScope().getType(getSpeciesName()); }

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

	/**
	 * Sets the defining plugin.
	 *
	 * @param plugin
	 *            the new defining plugin
	 */
	public void setDefiningPlugin(final String plugin) {}

	@Override
	public void updateWith(final IScope scope, final SavedAgent sa) {
		// Update attributes
		final Map<String, Object> mapAttr = sa.getVariables();
		for (final Entry<String, Object> attr : mapAttr.entrySet()) {
			this.setDirectVarValue(scope, attr.getKey(), attr.getValue());
		}

	}
}
