/*********************************************************************************************
 *
 *
 * 'AbstractScope.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.runtime;

import java.util.Map;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IStepable;
import msi.gama.common.util.RandomUtils;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Strings;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.IExecutable;
import msi.gaml.statements.IStatement;
import msi.gaml.statements.RemoteSequence;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Class AbstractScope.
 *
 * @author drogoul
 * @since 23 mai 2013
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ExecutionScope implements IScope {

	private static final String TOPOLOGY = "%_topology_%";
	private static final String GRAPHICS = "%_graphics_%";
	private static final String EACH = "%_each_%";
	private static final String ATTRIBUTES = "%_attributes_%";
	private static int SCOPE_NUMBER = 0;

	private final String name;
	private ITopLevelAgent rootAgent;
	protected IExecutionContext.Agent currentAgentContext;
	protected IExecutionContext.Statement currentExecutionContext;
	private volatile boolean _action_halted, _loop_halted, _agent_halted, _trace, _interrupted, _errors_disabled;
	private ISymbol currentSymbol;

	class AgentExecutionContext implements IExecutionContext.Agent {

		final IAgent agent;
		final IExecutionContext.Agent outer;

		public AgentExecutionContext(final IAgent agent) {
			this(agent, null);
		}

		private AgentExecutionContext(final IAgent agent, final IExecutionContext.Agent outer) {
			this.agent = agent;
			this.outer = outer;
		}

		@Override
		public void setVar(final String name, final Object value) {
			agent.setDirectVarValue(ExecutionScope.this, name, value);

		}

		@Override
		public Object getVar(final String name) {
			return agent.getDirectVarValue(ExecutionScope.this, name);
		}

		@Override
		public boolean hasVar(final String name) {
			return agent.getSpecies().hasVar(name);
		}

		@Override
		public IExecutionContext.Agent copy() {
			return this;
		}

		@Override
		public IExecutionContext.Agent getOuter() {
			return outer;
		}

		@Override
		public IAgent getAgent() {
			return agent;
		}

		@Override
		public Agent createChild(final IAgent agent) {
			if (this.agent == agent)
				return this;
			return new AgentExecutionContext(agent, this);
		}

	}

	public ExecutionScope(final ITopLevelAgent root) {
		this(root, null);
	}

	public ExecutionScope(final ITopLevelAgent root, final String otherName) {
		String name = "Scope #" + ++SCOPE_NUMBER;

		setRoot(root);
		currentAgentContext = new AgentExecutionContext(root);
		if (root != null) {
			name += " of " + root;
		}
		name += otherName == null || otherName.isEmpty() ? "" : "(" + otherName + ")";
		this.name = name;
		currentExecutionContext = new ExecutionContext();
	}

	/**
	 * Method clear()
	 * 
	 * @see msi.gama.runtime.IScope#clear()
	 */
	@Override
	public void clear() {
		currentAgentContext = null;
		currentExecutionContext = null;
		currentSymbol = null;
	}

	@Override
	public void disableErrorReporting() {
		_errors_disabled = true;
	}

	@Override
	public void enableErrorReporting() {
		_errors_disabled = false;
	}

	@Override
	public boolean reportErrors() {
		return !_errors_disabled;
	}

	@Override
	public void setTrace(final boolean t) {
		_trace = t;
	}

	/**
	 *
	 * Method interrupted(). Returns true if the scope is currently marked as
	 * interrupted.
	 * 
	 * @see msi.gama.runtime.IScope#interrupted()
	 */
	@Override
	public final boolean interrupted() {
		return _root_interrupted() || _action_halted || _loop_halted || _agent_halted;
	}

	@Override
	public void setInterrupted() {
		this._interrupted = true;
	}

	/**
	 * @return true if the root agent of the scope is marked as interrupted
	 *         (i.e. dead)
	 */

	protected boolean _root_interrupted() {
		return _interrupted || getRoot() == null || getRoot().dead();
	}

	@Override
	public boolean isOnUserHold() {
		final ITopLevelAgent root = getRoot();
		if (root == null)
			return false;
		return root.isOnUserHold();
	}

	@Override
	public void setOnUserHold(final boolean state) {
		final ITopLevelAgent root = getRoot();
		if (root == null)
			return;
		root.setOnUserHold(state);
	}

	@Override
	public final void interruptAction() {
		_action_halted = true;
	}

	@Override
	public final void interruptLoop() {
		_loop_halted = true;
	}

	@Override
	public final void interruptAgent() {
		_agent_halted = true;
	}

	/**
	 * Method push()
	 * 
	 * @see msi.gama.runtime.IScope#push(msi.gama.metamodel.agent.IAgent)
	 */
	// @Override
	@Override
	public synchronized boolean push(final IAgent agent) {
		final IAgent a = currentAgentContext.getAgent();
		if (a != null && a.equals(agent)) {
			return false;
		}
		// Previous context didnt have a root.
		if (a == null) {
			if (agent instanceof ITopLevelAgent) {
				setRoot((ITopLevelAgent) agent);
			}
			currentAgentContext = new AgentExecutionContext(agent);
		} else
			currentAgentContext = currentAgentContext.createChild(agent);
		return true;
	}

	protected void setRoot(final ITopLevelAgent agent) {
		rootAgent = agent;
	}

	/**
	 * Method pop()
	 * 
	 * @see msi.gama.runtime.IScope#pop(msi.gama.metamodel.agent.IAgent)
	 */
	// @Override
	@Override
	public void pop(final IAgent agent) {
		if (currentAgentContext == null) {
			throw GamaRuntimeException.warning("Agents stack is empty", this);
		}
		currentAgentContext = currentAgentContext.getOuter();
		_agent_halted = false;
	}

	/**
	 * Method push()
	 * 
	 * @see msi.gama.runtime.IScope#push(msi.gaml.statements.IStatement)
	 */
	@Override
	public void push(final ISymbol statement) {
		setCurrentSymbol(statement);
		currentExecutionContext = currentExecutionContext.createChild();
	}

	@Override
	public void setCurrentSymbol(final ISymbol statement) {
		currentSymbol = statement;
		if (_trace) {
			writeTrace();
		}
	}

	/**
	 *
	 */
	private void writeTrace() {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < currentExecutionContext.depth(); i++) {
			sb.append(Strings.TAB);
		}
		sb.append(currentSymbol.getTrace(this));
		this.getGui().getConsole().informConsole(sb.toString(), getRoot());
	}

	@Override
	public void popLoop() {
		_loop_halted = false;
	}

	@Override
	public void popAction() {
		_action_halted = false;
	}

	/**
	 * Method pop()
	 * 
	 * @see msi.gama.runtime.IScope#pop(msi.gaml.statements.IStatement)
	 */
	@Override
	public void pop(final ISymbol symbol) {
		currentExecutionContext = currentExecutionContext.getOuter();
	}

	@Override
	public ISymbol getCurrentSymbol() {
		return currentSymbol;
	}

	/**
	 * Method execute(). Asks the scope to manage the execution of a statement
	 * on an agent, taking care of pushing the agent on the stack, verifying the
	 * runtime state, etc. This method accepts optional arguments (which can be
	 * null)
	 * 
	 * @see msi.gama.runtime.IScope#execute(msi.gaml.statements.IStatement,
	 *      msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	public boolean execute(final IExecutable statement, final IAgent agent, final Arguments args,
			final Object[] result) {
		final IAgent caller = this.getAgent();
		if (statement == null || agent == null || interrupted() || agent.dead()) {
			return false;
		}
		// We then try to push the agent on the stack
		final boolean pushed = push(agent);
		try {
			// Otherwise we compute the result of the statement, pushing the
			// arguments if the statement expects them
			if (args != null && statement instanceof IStatement.WithArgs) {
				args.setCaller(caller);
				((IStatement.WithArgs) statement).setRuntimeArgs(args);
			} else if (statement instanceof RemoteSequence) {
				((RemoteSequence) statement).setMyself(caller);
				// We delegate to the remote scope
				result[0] = statement.executeOn(this);
				return true;
			}
			result[0] = statement.executeOn(this);
		} catch (final GamaRuntimeException g) {
			// If an exception occurs, we throw it and return false (could be
			// INTERRUPTED as well)
			// g.addAgent(agent.getName());
			GAMA.reportAndThrowIfNeeded(this, g, true);
		} finally {
			// Whatever the outcome, we pop the agent from the stack if it has
			// been previously pushed
			if (pushed) {
				pop(agent);
			}
		}
		return true;

	}

	@Override
	public void stackArguments(final Arguments actualArgs) {
		boolean callerPushed = false;
		if (actualArgs == null) {
			return;
		}
		final IAgent caller = actualArgs.getCaller();
		if (caller != null) {
			callerPushed = push(caller);
		}
		try {
			actualArgs.forEachEntry((a, b) -> {
				final IExpression e = b.getExpression();
				if (e != null) {
					addVarWithValue(a, e.value(ExecutionScope.this));
				}
				return true;
			});

		} finally {
			if (callerPushed) {
				pop(caller);
			}
		}
	}

	/**
	 * Method evaluate()
	 * 
	 * @see msi.gama.runtime.IScope#evaluate(msi.gaml.expressions.IExpression,
	 *      msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	public Object evaluate(final IExpression expr, final IAgent agent) throws GamaRuntimeException {
		if (agent == null || interrupted() || agent.dead()) {
			return null;
		}
		final boolean pushed = push(agent);
		try {
			return expr.value(this);
		} catch (final GamaRuntimeException g) {
			// g.addAgent(agent.toString());
			GAMA.reportAndThrowIfNeeded(this, g, true);
			return null;
		} finally {
			if (pushed) {
				pop(agent);
			}
		}
	}

	@Override
	public boolean step(final IStepable agent) {
		boolean result = false;
		final boolean isAgent = agent instanceof IAgent;
		if (agent == null || interrupted() || isAgent && ((IAgent) agent).dead()) {
			return false;
		}
		final boolean pushed = isAgent && push((IAgent) agent);
		try {
			result = agent.step(this);
		} catch (final Throwable ex) {
			final GamaRuntimeException g = GamaRuntimeException.create(ex, this);
			GAMA.reportAndThrowIfNeeded(this, g, true);
		} finally {
			if (pushed) {
				pop((IAgent) agent);
			}
		}
		return result;
	}

	@Override
	public boolean init(final IStepable agent) {
		boolean result = false;
		final boolean isAgent = agent instanceof IAgent;
		if (agent == null || interrupted() || isAgent && ((IAgent) agent).dead()) {
			return false;
		}
		final boolean pushed = isAgent && push((IAgent) agent);
		try {
			result = agent.init(this);
		} catch (final GamaRuntimeException g) {
			GAMA.reportAndThrowIfNeeded(this, g, true);
		} finally {
			if (pushed) {
				pop((IAgent) agent);
			}
		}
		return result;
	}

	/**
	 * Method getVarValue()
	 * 
	 * @see msi.gama.runtime.IScope#getVarValue(java.lang.String)
	 */
	@Override
	public Object getVarValue(final String varName) {
		return currentExecutionContext.getVar(varName);
	}

	/**
	 * Method setVarValue()
	 * 
	 * @see msi.gama.runtime.IScope#setVarValue(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void setVarValue(final String varName, final Object val) {
		currentExecutionContext.setVar(varName, val);
	}

	/**
	 * Method saveAllVarValuesIn()
	 * 
	 * @see msi.gama.runtime.IScope#saveAllVarValuesIn(java.util.Map)
	 */
	@Override
	public void saveAllVarValuesIn(final Map<String, Object> varsToSave) {
		varsToSave.putAll(currentExecutionContext.getAllOwnVars());
	}

	/**
	 * Method removeAllVars()
	 * 
	 * @see msi.gama.runtime.IScope#removeAllVars()
	 */
	@Override
	public void removeAllVars() {
		currentExecutionContext.clearOwnVars();
	}

	/**
	 * Method addVarWithValue()
	 * 
	 * @see msi.gama.runtime.IScope#addVarWithValue(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void addVarWithValue(final String varName, final Object val) {
		currentExecutionContext.putOwnVar(varName, val);
	}

	/**
	 * Method setEach()
	 * 
	 * @see msi.gama.runtime.IScope#setEach(java.lang.Object)
	 */
	@Override
	public void setEach(final Object value) {
		this.addVarWithValue(EACH, value);
	}

	/**
	 * Method getEach()
	 * 
	 * @see msi.gama.runtime.IScope#getEach()
	 */
	@Override
	public Object getEach() {
		return getVarValue(EACH);
	}

	/**
	 * Method getArg()
	 * 
	 * @see msi.gama.runtime.IScope#getArg(java.lang.String, int)
	 */
	@Override
	public Object getArg(final String string, final int type) throws GamaRuntimeException {
		return Types.get(type).cast(this, currentExecutionContext.getOwnVar(string), null, false);
	}

	@Override
	public final Integer getIntArg(final String name) throws GamaRuntimeException {
		return (Integer) getArg(name, IType.INT);
	}

	@Override
	public final Double getFloatArg(final String name) throws GamaRuntimeException {
		return (Double) getArg(name, IType.FLOAT);
	}

	@Override
	public final IList getListArg(final String name) throws GamaRuntimeException {
		return (IList) getArg(name, IType.LIST);
	}

	@Override
	public final Boolean getBoolArg(final String name) throws GamaRuntimeException {
		return (Boolean) getArg(name, IType.BOOL);
	}

	@Override
	public final String getStringArg(final String name) throws GamaRuntimeException {
		return (String) getArg(name, IType.STRING);
	}

	/**
	 * Method hasArg()
	 * 
	 * @see msi.gama.runtime.IScope#hasArg(java.lang.String)
	 */
	@Override
	public boolean hasArg(final String name) {
		return currentExecutionContext.hasOwnVar(name);
	}

	/**
	 * Method hasVar()
	 * 
	 * @see msi.gama.runtime.IScope#hasVar(java.lang.String)
	 */
	@Override
	public boolean hasVar(final String name) {
		return currentExecutionContext.hasVar(name);
	}

	/**
	 * Method getAgentVarValue()
	 * 
	 * @see msi.gama.runtime.IScope#getAgentVarValue(msi.gama.metamodel.agent.IAgent,
	 *      java.lang.String)
	 */
	@Override
	public Object getAgentVarValue(final IAgent agent, final String name) throws GamaRuntimeException {
		if (agent == null || agent.dead() || interrupted()) {
			return null;
		}
		Object result = null;
		final boolean pushed = push(agent);
		try {
			result = currentAgentContext.getVar(name);
		} finally {
			if (pushed) {
				pop(agent);
			}
		}
		return result;
	}

	/**
	 * Method setAgentVarValue()
	 * 
	 * @see msi.gama.runtime.IScope#setAgentVarValue(msi.gama.metamodel.agent.IAgent,
	 *      java.lang.String, java.lang.Object)
	 */
	@Override
	public void setAgentVarValue(final IAgent agent, final String name, final Object v) {
		if (agent == null || agent.dead() || interrupted()) {
			return;
		}
		final boolean pushed = push(agent);
		try {
			currentAgentContext.setVar(name, v);
		} finally {
			if (pushed) {
				pop(agent);
			}
		}
	}

	@Override
	public boolean update(final IAgent a) {
		if (a == null || a.dead() || interrupted()) {
			return false;
		}
		final boolean pushed = push(a);
		try {
			a.getPopulation().updateVariables(this, a);

		} catch (final GamaRuntimeException g) {
			GAMA.reportAndThrowIfNeeded(this, g, true);
		} finally {
			if (pushed) {
				pop(a);
			}
		}
		return true;
	}

	/**
	 * Method getGlobalVarValue()
	 * 
	 * @see msi.gama.runtime.IScope#getGlobalVarValue(java.lang.String)
	 */
	@Override
	public Object getGlobalVarValue(final String name) throws GamaRuntimeException {
		final ITopLevelAgent root = getRoot();
		if (root == null)
			return null;
		return root.getDirectVarValue(this, name);
	}

	/**
	 * Method setGlobalVarValue()
	 * 
	 * @see msi.gama.runtime.IScope#setGlobalVarValue(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void setGlobalVarValue(final String name, final Object v) throws GamaRuntimeException {
		final ITopLevelAgent root = getRoot();
		if (root == null)
			return;
		root.setDirectVarValue(this, name, v);
	}

	/**
	 * Method getName()
	 * 
	 * @see msi.gama.runtime.IScope#getName()
	 */

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Method getTopology()
	 * 
	 * @see msi.gama.runtime.IScope#getTopology()
	 */
	@Override
	public ITopology getTopology() {
		final ITopology topology = (ITopology) this.getVarValue(TOPOLOGY);
		return topology != null ? topology : getAgent().getTopology();
	}

	/**
	 * Method setTopology()
	 * 
	 * @see msi.gama.runtime.IScope#setTopology(msi.gama.metamodel.topology.ITopology)
	 */
	@Override
	public ITopology setTopology(final ITopology topo) {
		final ITopology previous = getTopology();
		addVarWithValue(TOPOLOGY, topo);
		return previous;
	}

	/**
	 * Method setGraphics()
	 * 
	 * @see msi.gama.runtime.IScope#setGraphics(msi.gama.common.interfaces.IGraphics)
	 */
	@Override
	public void setGraphics(final IGraphics val) {
		addVarWithValue(GRAPHICS, val);
	}

	/**
	 * Method getGraphics()
	 * 
	 * @see msi.gama.runtime.IScope#getGraphics()
	 */
	@Override
	public IGraphics getGraphics() {
		return (IGraphics) getVarValue(GRAPHICS);
	}

	/**
	 * Method getAgentScope()
	 * 
	 * @see msi.gama.runtime.IScope#getAgent()
	 */
	@Override
	public IAgent getAgent() {
		return currentAgentContext.getAgent();
	}

	/**
	 * Method getSimulationScope()
	 * 
	 * @see msi.gama.runtime.IScope#getSimulation()
	 */
	@Override
	public SimulationAgent getSimulation() {
		final ITopLevelAgent root = getRoot();
		if (root == null)
			return null;
		return root.getSimulation();
	}

	@Override
	public IExperimentAgent getExperiment() {
		final ITopLevelAgent root = getRoot();
		if (root == null)
			return null;
		return root.getExperiment();
	}

	/**
	 * Method getModel()
	 * 
	 * @see msi.gama.runtime.IScope#getModel()
	 */
	@Override
	public IModel getModel() {
		final ITopLevelAgent root = getRoot();
		if (root == null)
			return null;
		return getRoot().getModel();
	}

	/**
	 * Method getExperimentContext()
	 * 
	 * @see msi.gama.runtime.IScope#getExperimentContext()
	 */
	@Override
	public IDescription getExperimentContext() {
		final IExperimentAgent a = getExperiment();
		if (a == null) {
			return null;
		}
		return a.getSpecies().getDescription();
	}

	/**
	 * Method getModelContext()
	 * 
	 * @see msi.gama.runtime.IScope#getModelContext()
	 */
	@Override
	public IDescription getModelContext() {
		final IModel model = getModel();
		if (model == null) {
			return null;
		}
		return model.getDescription();
	}

	/**
	 * Method getClock()
	 * 
	 * @see msi.gama.runtime.IScope#getClock()
	 */
	@Override
	public SimulationClock getClock() {
		final ITopLevelAgent root = getRoot();
		if (root == null) {
			return null;
		}
		return root.getClock();
	}

	@Override
	public IAgent[] getAgentsStack() {
		final IAgent[] result = new IAgent[currentAgentContext.depth() + 1];
		IExecutionContext.Agent current = currentAgentContext;
		int i = 0;
		while (current != null) {
			result[i++] = current.getAgent();
			current = current.getOuter();
		}
		return result;
	}

	/**
	 * Method pushReadAttributes()
	 * 
	 * @see msi.gama.runtime.IScope#pushReadAttributes(java.util.Map)
	 */
	@Override
	public void pushReadAttributes(final Map values) {
		addVarWithValue(ATTRIBUTES, values);
	}

	/**
	 * Method popReadAttributes()
	 * 
	 * @see msi.gama.runtime.IScope#popReadAttributes()
	 */
	@Override
	public Map popReadAttributes() {
		final Map value = (Map) this.getVarValue(ATTRIBUTES);
		currentExecutionContext.removeOwnVar(ATTRIBUTES);
		return value;
	}

	@Override
	public Map peekReadAttributes() {
		final Map value = (Map) this.getVarValue(ATTRIBUTES);
		return value;
	}

	@Override
	public IGui getGui() {
		final IExperimentAgent experiment = getExperiment();
		if (experiment == null) {
			return GAMA.getGui();
		}
		if (experiment.getSpecies().isHeadless()) {
			return GAMA.getHeadlessGui();
		}
		return GAMA.getRegularGui();
	}

	@Override
	public ITopLevelAgent getRoot() {
		return rootAgent;
	}

	@Override
	public boolean isPaused() {
		return getExperiment().getSpecies().getController().getScheduler().paused || isOnUserHold();
	}

	/**
	 * Method getRandom()
	 * 
	 * @see msi.gama.runtime.IScope#getRandom()
	 */
	@Override
	public RandomUtils getRandom() {
		return getRoot().getRandomGenerator();
	}

	@Override
	public IScope copy(final String additionalName) {
		final ExecutionScope scope = new ExecutionScope(getRoot(), additionalName);
		scope.currentExecutionContext = currentExecutionContext.copy();
		return scope;
	}

}
