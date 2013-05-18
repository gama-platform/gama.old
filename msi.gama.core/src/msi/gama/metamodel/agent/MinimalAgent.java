package msi.gama.metamodel.agent;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gama.kernel.experiment.*;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.operators.*;
import msi.gaml.skills.ISkill;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;
import msi.gaml.variables.IVariable;
import com.google.common.primitives.Ints;
import com.vividsolutions.jts.geom.*;

/**
 * 
 * Class MinimalAgent. An abstract class that tries to minimize the number of attributes manipulated by agents. In
 * particular, it declares no Geometry (leaving the programmer the possibility to redeclare getGeometry(), for example
 * in a dynamic fashion), no Population (leaving the programmer the possibility to redeclare getPopulation(), for
 * example in a dynamic fashion, etc.)
 * 
 * These agents have no sub-population by default (but subclasses can be decalred as implementing IMacroAgent, and the
 * appropriate methods can be redefined). Their name is fixed by construction (but subclasses can always implement a
 * name).
 * 
 * From a functional point of view, this class delegates most of its methods to either the geometry (by calling
 * getGeometry()) or the population (by calling getPopulation()).
 * 
 * Furthermore, and contrary to GamlAgent, this class does not delegate its step() and init() behaviors to GAML actions
 * (_init_ and _step_).
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
	protected final GamaMap<Object, Object> attributes = new GamaMap();

	@Override
	public abstract IPopulation getPopulation();

	@Override
	public abstract IShape getGeometry();

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
		return getGeometry().isPoint();
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#getInnerGeometry()
	 */
	@Override
	public Geometry getInnerGeometry() {
		final IShape g = getGeometry();
		return g == null ? null : g.getInnerGeometry();
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#getEnvelope()
	 */
	@Override
	public Envelope getEnvelope() {
		final IShape g = getGeometry();
		return g == null ? null : g.getEnvelope();
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#covers(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean covers(final IShape g) {
		final IShape gg = getGeometry();
		return gg == null ? false : gg.covers(g);
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#euclidianDistanceTo(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public double euclidianDistanceTo(final IShape g) {
		final IShape gg = getGeometry();
		return gg == null ? 0d : gg.euclidianDistanceTo(g);
	}

	@Override
	public double euclidianDistanceTo(final ILocation g) {
		final IShape gg = getGeometry();
		return gg == null ? 0d : gg.euclidianDistanceTo(g);
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#intersects(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean intersects(final IShape g) {
		final IShape gg = getGeometry();
		return gg == null ? false : gg.intersects(g);
	}

	@Override
	public boolean crosses(final IShape g) {
		final IShape gg = getGeometry();
		return gg == null ? false : gg.crosses(g);
	}

	/**
	 * @see msi.gama.interfaces.IGeometry#getPerimeter()
	 */
	@Override
	public double getPerimeter() {
		return getGeometry().getPerimeter();
	}

	/**
	 * @see msi.gama.common.interfaces.IGeometry#setInnerGeometry(com.vividsolutions.jts.geom.Geometry)
	 */
	@Override
	public void setInnerGeometry(final Geometry geom) {
		getGeometry().setInnerGeometry(geom);
	}

	@Override
	public void dispose() {}

	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return getName();
	}

	@Override
	public IValue copy(final IScope scope) throws GamaRuntimeException {
		return this;
	}

	@Override
	public String toGaml() {
		if ( dead() ) { return "nil"; }
		final StringBuilder sb = new StringBuilder(30);
		sb.append(getIndex());
		sb.append(" as ");
		sb.append(getSpeciesName());
		return sb.toString();
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
	public synchronized Object getAttribute(final Object index) {
		return attributes.get(index);
	}

	@Override
	public synchronized void setAttribute(final Object name, final Object val) {
		attributes.put(name, val);
	}

	@Override
	public int compareTo(final IAgent o) {
		return Ints.compare(getIndex(), o.getIndex());
	}

	@Override
	public void init(final IScope scope) throws GamaRuntimeException {
		scope.push(this);
		try {
			getSpecies().getArchitecture().init(scope);
		} finally {
			scope.pop(this);
		}
	}

	@Override
	public void step(final IScope scope) throws GamaRuntimeException {
		if ( scope.interrupted() || dead() ) { return; }
		scope.push(this);
		try {
			getPopulation().updateVariables(scope, this);
			getSpecies().getArchitecture().executeOn(scope);
		} finally {
			scope.pop(this);
		}
	}

	@Override
	public ITopology getTopology() {
		return getPopulation().getTopology();
	}

	@Override
	public void setPeers(final IList<IAgent> peers) {
		// "peers" is read-only attribute
	}

	@Override
	public IList<IAgent> getPeers() throws GamaRuntimeException {
		final IAgent host = getHost();
		if ( host != null ) {
			final IPopulation pop = host.getPopulationFor(this.getSpecies());
			final IList<IAgent> retVal = pop.getAgentsList();
			retVal.remove(this);
			return retVal;
		}
		return GamaList.EMPTY_LIST;
	}

	@Override
	public String getName() {
		if ( dead() ) { return "dead agent"; }
		return getSpeciesName() + getIndex();
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
	public void setGeometry(final IShape newGeometry) {}

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
	public void schedule() {
		// GuiUtils.debug("GamlAgent.schedule : " + this);
		if ( !dead() ) {
			getScheduler().insertAgentToInit(this, getScope());
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
		return getPopulation().getSpecies();
	}

	@Override
	public boolean isInstanceOf(final ISpecies s, final boolean direct) {
		return getPopulation().manages(s, direct);
	}

	@Override
	public Integer getHeading() {
		Integer h = (Integer) getAttribute(IKeyword.HEADING);
		if ( h == null ) {
			h = RandomUtils.getDefault().between(0, 359);
			setHeading(h);
		}
		return Maths.checkHeading(h);
	}

	@Override
	public void setHeading(final Integer newHeading) {
		setAttribute(IKeyword.HEADING, newHeading);
	}

	@Override
	public Object getDirectVarValue(final IScope scope, final String n) throws GamaRuntimeException {
		final IVariable var = getPopulation().getVar(this, n);
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
		getPopulation().getVar(this, s).setVal(scope, this, v);
	}

	@Override
	public List<IAgent> getMacroAgents() {
		final List<IAgent> retVal = new GamaList<IAgent>();
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
	 * return true if the agent is available for drawing or disposing false otherwise
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
		return getHost().getScheduler();
	}

	@Override
	public IModel getModel() {
		return getHost().getModel();
	}

	@Override
	public IExperimentAgent getExperiment() {
		return getHost().getExperiment();
	}

	@Override
	public IScope getScope() {
		return getHost().getScope();
	}

	@Override
	public boolean isInstanceOf(final String skill, final boolean direct) {
		return getSpecies().implementsSkill(skill);
	}

	@Override
	public SimulationClock getClock() {
		if ( getHost() == null ) { return GAMA.getClock(); }
		return getHost().getClock();
	}

	/**
	 * Method getPopulationFor()
	 * @see msi.gama.metamodel.agent.IAgent#getPopulationFor(msi.gaml.species.ISpecies)
	 */
	@Override
	public IPopulation getPopulationFor(final ISpecies microSpecies) {
		return getHost().getPopulationFor(microSpecies);
	}

	/**
	 * Method getPopulationFor()
	 * @see msi.gama.metamodel.agent.IAgent#getPopulationFor(java.lang.String)
	 */
	@Override
	public IPopulation getPopulationFor(final String speciesName) {
		return getHost().getPopulationFor(speciesName);
	}

	/**
	 * GAML actions
	 */

	@action(name = "debug")
	@args(names = { "message" })
	public final Object primDebug(final IScope scope) throws GamaRuntimeException {
		final String m = (String) scope.getArg("message", IType.STRING);
		GuiUtils.debugConsole(scope.getClock().getCycle(), m + "\nsender: " + Cast.asMap(scope, this));
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
		// FIXME VERIFY THIS STATUS
		scope.setStatus(ExecutionStatus.interrupt);
		dispose();
		return null;
	}
}
