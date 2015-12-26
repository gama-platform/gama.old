/*********************************************************************************************
 *
 *
 * 'MinimalAgent.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.metamodel.agent;

import java.util.*;
import com.google.common.primitives.Ints;
import com.vividsolutions.jts.geom.Geometry;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.experiment.*;
import msi.gama.kernel.model.IModel;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.compilation.ISkillConstructor;
import msi.gaml.operators.Cast;
import msi.gaml.skills.ISkill;
import msi.gaml.species.ISpecies;
import msi.gaml.types.*;
import msi.gaml.variables.IVariable;

/**
 *
 * Class MinimalAgent. An abstract class that tries to minimize the number of attributes manipulated by agents. In
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
 * Abstract methods to override:
 * - getGeometry()
 * - getPopulation()
 *
 * @author drogoul
 * @since 18 mai 2013
 *
 */
public abstract class MinimalAgent implements IAgent {

	private volatile int index;
	protected volatile boolean dead = false;
	private volatile boolean lockAcquired = false;
	protected final GamaMap<Object, Object> attributes = GamaMapFactory.create(Types.NO_TYPE, Types.NO_TYPE);

	@Override
	public abstract IPopulation getPopulation();

	@Override
	public abstract IShape getGeometry();

	@Override
	public void setDuplicator(final ISkillConstructor duplicator) {
		// Nothing to do here
	}

	/**
	 *
	 * @return the population of the agent if it not null, otherwise throws a runtime exeception.
	 * @note If checking for a null value of population imposes too much overhead in cases where the population is sure
	 * not to be nil, this method can be safely overriden with a direct call to getPopulation()
	 */
	protected IPopulation checkedPopulation() {
		return getPopulation();
		// return nullCheck(getPopulation(), "The agent's population is nil");
	}

	/**
	 *
	 * @return the geometry of the agent if it not null, otherwise throws a runtime exeception.
	 * @note If checking for a null value in geometry imposes too much overhead in cases where the geometry is sure not
	 * to be nil, this method can be safely overriden with a direct call to getGeometry()
	 */
	protected IShape checkedGeometry() {
		return getGeometry();
		// return nullCheck(getGeometry(), "The agent's shape is nil");
	}

	@Override
	public ISkill duplicate() {
		return this;
	}

	@Override
	public IAgent getAgent() {
		return this;
	}

	@Override
	public void setAgent(final IAgent agent) {}

	@Override
	public boolean isPoint() {
		return checkedGeometry().isPoint();
	}

	@Override
	public boolean isLine() {
		return checkedGeometry().isLine();
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#getInnerGeometry()
	 */
	@Override
	public Geometry getInnerGeometry() {
		return checkedGeometry().getInnerGeometry();
	}

	/**
	 * Returns the envelope of the geometry of the agent, or null if the geometry has not yet been defined
	 * @see msi.gama.interfaces.IGeometry#getEnvelope()
	 */
	@Override
	public Envelope3D getEnvelope() {
		final IShape g = getGeometry();
		return g == null ? null : g.getEnvelope();
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#covers(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean covers(final IShape g) {
		return checkedGeometry().covers(g);
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#euclidianDistanceTo(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public double euclidianDistanceTo(final IShape g) {
		return checkedGeometry().euclidianDistanceTo(g);
	}

	@Override
	public double euclidianDistanceTo(final ILocation g) {
		return checkedGeometry().euclidianDistanceTo(g);
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#intersects(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean intersects(final IShape g) {
		return checkedGeometry().intersects(g);
	}

	@Override
	public boolean crosses(final IShape g) {
		return checkedGeometry().crosses(g);
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#getPerimeter()
	 */
	@Override
	public double getPerimeter() {
		return checkedGeometry().getPerimeter();
	}

	/**
	 * @see msi.gama.common.interfaces.IGeometry#setInnerGeometry(com.vividsolutions.jts.geom.Geometry)
	 */
	@Override
	public void setInnerGeometry(final Geometry geom) {
		checkedGeometry().setInnerGeometry(geom);
	}

	@Override
	public void dispose() {
		if ( dead() ) { return; }
		acquireLock();
		try {
			dead = true;
			final IPopulation p = getPopulation();
			if ( p != null ) {
				p.removeValue(null, this);
			}

			final IShape s = getGeometry();
			if ( s != null ) {
				s.dispose();
			}
			attributes.clear();
		} finally {
			releaseLock();
		}
	}

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return getName();
	}

	@Override
	public IShape copy(final IScope scope) throws GamaRuntimeException {
		return this;
	}

	@Override
	public String serialize(final boolean includingBuiltIn) {
		if ( dead() ) { return "nil"; }
		final StringBuilder sb = new StringBuilder(30);
		sb.append(getIndex());
		sb.append(" as ");
		sb.append(getSpeciesName());
		return sb.toString();
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void setExtraAttributes(final Map<Object, Object> map) {
		if ( map == null ) { return; }
		attributes.putAll(map);
	}

	@Override
	public GamaMap getAttributes() {
		return attributes;
	}

	@Override
	public GamaMap getOrCreateAttributes() {
		return attributes;
	}

	@Override
	public boolean hasAttribute(final Object key) {
		return attributes.containsKey(key);
	}

	@Override
	public/* synchronized */Object getAttribute(final Object index) {
		return attributes.get(index);
	}

	@Override
	public/* synchronized */void setAttribute(final Object name, final Object val) {
		attributes.put(name, val);
	}

	@Override
	public int compareTo(final IAgent o) {
		return Ints.compare(getIndex(), o.getIndex());
	}

	@Override
	public boolean init(final IScope scope) throws GamaRuntimeException {
		return getSpecies().getArchitecture().init(scope);
	}

	@Override
	public boolean step(final IScope scope) throws GamaRuntimeException {
		if ( scope.update(this) ) {
			Object[] result = new Object[1];
			return scope.execute(getSpecies().getArchitecture(), this, null, result);
		}
		return false;
	}

	@Override
	public ITopology getTopology() {
		return checkedPopulation().getTopology();
	}

	@Override
	public void setPeers(final IList<IAgent> peers) {
		// "peers" is read-only attribute
	}

	@Override
	public IList<IAgent> getPeers() throws GamaRuntimeException {
		final IPopulation pop = getHost().getPopulationFor(this.getSpecies());
		if ( pop != null ) {
			IScope scope = getScope();
			final IList<IAgent> retVal = GamaListFactory
				.<IAgent> createWithoutCasting(scope.getModelContext().getTypeNamed(getSpeciesName()), pop.toArray());
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
		return checkedGeometry().getLocation();
	}

	@Override
	public void setLocation(final ILocation l) {
		checkedGeometry().setLocation(l);
	}

	@Override
	public void setGeometry(final IShape newGeometry) {
		checkedGeometry().setGeometry(newGeometry);
	}

	@Override
	public boolean dead() {
		return dead;
	}

	@Override
	public IMacroAgent getHost() {
		return checkedPopulation().getHost();
	}

	@Override
	public void setHost(final IMacroAgent macroAgent) {}

	@Override
	public void schedule() {
		// public void scheduleAndExecute(final RemoteSequence sequence) {
		if ( !dead() ) {
			getScope().init(this);
			// getScheduler().insertAgentToInit(getScope(), this, sequence);
		}
	}

	@Override
	public final int getIndex() {
		return index;
	}

	@Override
	public final void setIndex(final int index) {
		this.index = index;
	}

	@Override
	public String getSpeciesName() {
		return getSpecies().getName();
	}

	@Override
	public ISpecies getSpecies() {
		return checkedPopulation().getSpecies();
	}

	@Override
	public boolean isInstanceOf(final ISpecies s, final boolean direct) {
		ISpecies species = getSpecies();
		if ( species == s ) { return true; }
		if ( !direct ) { return species.extendsSpecies(s); }
		return false;
	}

	@Override
	public Object getDirectVarValue(final IScope scope, final String n) throws GamaRuntimeException {
		final IVariable var = checkedPopulation().getVar(this, n);
		if ( var != null ) { return var.value(scope, this); }
		final IMacroAgent host = this.getHost();
		if ( host != null ) {
			final IVariable varOfHost = host.getPopulation().getVar(host, n);
			if ( varOfHost != null ) { return varOfHost.value(scope, host); }
		}
		return null;
	}

	@Override
	public void setDirectVarValue(final IScope scope, final String s, final Object v) throws GamaRuntimeException {
		checkedPopulation().getVar(this, s).setVal(scope, this, v);
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

	/**
	 * Solve the synchronization problem between Execution Thread and Event Dispatch Thread.
	 *
	 * The synchronization problem may happen when 1. The Event Dispatch Thread is drawing an agent
	 * while the Execution Thread tries to it; 2. The Execution Thread is disposing the agent while
	 * the Event Dispatch Thread tries to draw it.
	 *
	 * To avoid this, the corresponding thread has to invoke "acquireLock" to lock the agent before
	 * drawing or disposing the agent. After finish the task, the thread invokes "releaseLock" to
	 * release the agent's lock.
	 *
	 */
	@Override
	public synchronized void acquireLock() {
		while (lockAcquired) {
			try {
				wait();
			} catch (final InterruptedException e) {
				// e.printStackTrace();
			}
		}
		lockAcquired = true;
	}

	@Override
	public synchronized void releaseLock() {
		lockAcquired = false;
		notify();
	}

	@Override
	public void hostChangesShape() {}

	@Override
	public AgentScheduler getScheduler() {
		final IMacroAgent a = getHost();
		if ( a == null ) { return null; }
		return a.getScheduler();
	}

	@Override
	public IModel getModel() {
		final IMacroAgent a = getHost();
		if ( a == null ) { return GAMA.getModel(); }
		return a.getModel();
	}

	@Override
	public IExperimentAgent getExperiment() {
		return getHost().getExperiment();
	}

	@Override
	public IScope getScope() {
		final IMacroAgent a = getHost();
		if ( a == null ) { return GAMA.obtainNewScope(); }
		return a.getScope();
	}

	@Override
	public boolean isInstanceOf(final String skill, final boolean direct) {
		return getSpecies().implementsSkill(skill);
	}

	// @Override
	// public SimulationClock getClock() {
	// final IMacroAgent a = getHost();
	// if ( a == null ) { return GAMA.getClock(); }
	// return a.getClock();
	// }

	/**
	 * Method getPopulationFor()
	 * @see msi.gama.metamodel.agent.IAgent#getPopulationFor(msi.gaml.species.ISpecies)
	 */
	@Override
	public IPopulation getPopulationFor(final ISpecies microSpecies) {
		return getPopulationFor(microSpecies.getName());
	}

	/**
	 * Method getPopulationFor()
	 * @see msi.gama.metamodel.agent.IAgent#getPopulationFor(java.lang.String)
	 */
	@Override
	public IPopulation getPopulationFor(final String speciesName) {
		final IMacroAgent a = getHost();
		if ( a == null ) { return null; }
		return getHost().getPopulationFor(speciesName);
	}

	/**
	 * GAML actions
	 */

	@action(name = "debug")
	@args(names = { "message" })
	public final Object primDebug(final IScope scope) throws GamaRuntimeException {
		final String m = (String) scope.getArg("message", IType.STRING);
		GuiUtils.debugConsole(scope.getClock().getCycle(), m + "\nsender: " + Cast.asMap(scope, this, false));
		return m;
	}

	@action(name = "write")
	@args(names = { "message" })
	public final Object primWrite(final IScope scope) throws GamaRuntimeException {
		final String s = (String) scope.getArg("message", IType.STRING);
		GuiUtils.informConsole(s);
		return s;
	}

	@action(name = IKeyword.ERROR)
	@args(names = { "message" })
	public final Object primError(final IScope scope) throws GamaRuntimeException {
		final String error = (String) scope.getArg("message", IType.STRING);
		GuiUtils.error(error);
		return error;
	}

	@action(name = "tell")
	@args(names = { "message" })
	public final Object primTell(final IScope scope) throws GamaRuntimeException {
		final String s = getName() + " says : " + scope.getArg("message", IType.STRING);
		GuiUtils.tell(s);
		return s;
	}

	@action(name = "die")
	public Object primDie(final IScope scope) throws GamaRuntimeException {
		scope.interruptAgent();
		dispose();
		return null;
	}

	@Override
	public Type getGeometricalType() {
		return getGeometry().getGeometricalType();
	}

	@Override
	public IType getType() {
		return getScope().getModelContext().getTypeNamed(getSpeciesName());
	}

	/**
	 * Method get()
	 * @see msi.gama.util.IContainer.Addressable#get(msi.gama.runtime.IScope, java.lang.Object)
	 */
	@Override
	public Object get(final IScope scope, final String index) throws GamaRuntimeException {
		if ( getPopulation().hasVar(index) ) {
			return scope.getAgentVarValue(this, index);
		} else {
			return attributes.get(scope, index);
			// return attributes.get(scope, index);
		}
	}

	/**
	 * Method getFromIndicesList()
	 * @see msi.gama.util.IContainer.Addressable#getFromIndicesList(msi.gama.runtime.IScope, msi.gama.util.IList)
	 */
	@Override
	public Object getFromIndicesList(final IScope scope, final IList<String> indices) throws GamaRuntimeException {
		if ( indices == null || indices.isEmpty() ) { return null; }
		return get(scope, indices.firstValue(scope));
	}

	/**
	 * Method asShapeWithGeometry()
	 * @see msi.gama.metamodel.shape.IShape#asShapeWithGeometry(msi.gama.runtime.IScope, com.vividsolutions.jts.geom.Geometry)
	 */
	// @Override
	// public GamaShape asShapeWithGeometry(final IScope scope, final Geometry g) {
	// return getGeometry().asShapeWithGeometry(scope, g);
	// }

}
