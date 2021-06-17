/*******************************************************************************************************
 *
 * msi.gama.runtime.ExecutionScope.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime;

import static msi.gama.runtime.ExecutionResult.FAILED;
import static msi.gama.runtime.ExecutionResult.PASSED;
import static msi.gama.runtime.ExecutionResult.withValue;

import java.util.Collections;
import java.util.Map;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IStepable;
import msi.gama.common.util.RandomUtils;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.kernel.experiment.IExperimentController;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.benchmark.StopWatch;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.Collector;
import msi.gama.util.IList;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Strings;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.IExecutable;
import msi.gaml.types.IType;
import msi.gaml.types.ITypesManager;
import msi.gaml.types.Types;

/**
 * Class AbstractScope.
 *
 * @author drogoul
 * @since 23 mai 2013
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class ExecutionScope implements IScope {

	private static final String ATTRIBUTES = "%_attributes_%";
	private static int SCOPE_NUMBER = 0;

	private final String scopeName;
	protected IExecutionContext executionContext;
	protected AgentExecutionContext agentContext;
	protected final SpecialContext additionalContext = new SpecialContext();
	private volatile boolean _action_halted, _loop_halted, _agent_halted, _trace, _in_try_mode, _interrupted,
			_errors_disabled;
	private ISymbol currentSymbol;

	class SpecialContext {
		Object each;
		IGraphics graphics;
		public ITopology topology;
		ITopLevelAgent rootAgent;
		IGui gui;
		ITypesManager types;
		GamaRuntimeException currentError;
		boolean horizontalPixelContext = false;

		void clear() {
			each = null;
			graphics = null;
			topology = null;
			rootAgent = null;
			gui = null;
			types = null;
			currentError = null;
		}

		public void copyFrom(final SpecialContext specialContext) {
			if (specialContext == null) return;
			each = specialContext.each;
			graphics = specialContext.graphics;
			topology = specialContext.topology;
			rootAgent = specialContext.rootAgent;
			gui = specialContext.gui;
			types = specialContext.types;
			currentError = specialContext.currentError;
		}

	}

	public ExecutionScope(final ITopLevelAgent root) {
		this(root, null);
	}

	public ExecutionScope(final ITopLevelAgent root, final String otherName) {
		this(root, otherName, null);
	}

	public ExecutionScope(final ITopLevelAgent root, final String otherName, final IExecutionContext context) {
		this(root, otherName, context, null, null);
	}

	public ExecutionScope(final ITopLevelAgent root, final String otherName, final IExecutionContext context,
			final AgentExecutionContext agentContext, final SpecialContext specialContext) {
		String name = "Scope #" + ++SCOPE_NUMBER;
		setRoot(root);
		if (root != null) { name += " of " + root.stringValue(root.getScope()); }
		name += otherName == null || otherName.isEmpty() ? "" : " (" + otherName + ")";
		this.scopeName = name;
		this.executionContext = context == null ? ExecutionContext.create(this) : context.createCopy();
		this.agentContext = agentContext == null ? AgentExecutionContext.create(root, null) : agentContext;
		this.additionalContext.copyFrom(specialContext);
	}

	public AgentExecutionContext createChildContext(final IAgent agent) {
		return AgentExecutionContext.create(agent, agentContext);
	}

	/**
	 * Method clear()
	 *
	 * @see msi.gama.runtime.IScope#clear()
	 */
	@Override
	public void clear() {
		if (executionContext != null) { executionContext.dispose(); }
		executionContext = null;
		if (agentContext != null) { agentContext.dispose(); }
		agentContext = null;
		additionalContext.clear();
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

	/**
	 * In 'try' mode, the errors are thrown even if _errors_disabled is true
	 */
	@Override
	public void enableTryMode() {
		_in_try_mode = true;
	}

	@Override
	public void disableTryMode() {
		_in_try_mode = false;
	}

	@Override
	public boolean isInTryMode() {
		return _in_try_mode;
	}

	@Override
	public void setTrace(final boolean t) {
		_trace = t;
	}

	/**
	 *
	 * Method interrupted(). Returns true if the scope is currently marked as interrupted.
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
	 * @return true if the root agent of the scope is marked as interrupted (i.e. dead)
	 */

	public boolean _root_interrupted() {
		return _interrupted || getRoot() == null || getRoot().dead();
	}

	@Override
	public boolean isOnUserHold() {
		final ITopLevelAgent root = getRoot();
		if (root == null) return false;
		return root.isOnUserHold();
	}

	@Override
	public void setOnUserHold(final boolean state) {
		final ITopLevelAgent root = getRoot();
		if (root == null) return;
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
		final IAgent a = agentContext == null ? null : agentContext.getAgent();
		if (a == null) {
			if (agent instanceof ITopLevelAgent) {
				// Previous context didnt have a root.
				setRoot((ITopLevelAgent) agent);
			}
			// get rid of the previous context **important**
			agentContext = null;
		} else if (a == agent) return false;
		agentContext = createChildContext(agent);
		return true;
	}

	protected void setRoot(final ITopLevelAgent agent) {
		additionalContext.rootAgent = agent;
	}

	/**
	 * Method pop()
	 *
	 * @see msi.gama.runtime.IScope#pop(msi.gama.metamodel.agent.IAgent)
	 */
	// @Override
	@Override
	public void pop(final IAgent agent) {
		if (agentContext == null) throw GamaRuntimeException.warning("Agents stack is empty", this);
		final AgentExecutionContext previous = agentContext;
		agentContext = agentContext.getOuterContext();
		previous.dispose();
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
		if (executionContext != null) {
			executionContext = executionContext.createChildContext();
		} else {
			executionContext = ExecutionContext.create(this);
		}
	}

	@Override
	public void setCurrentSymbol(final ISymbol statement) {
		currentSymbol = statement;
		if (_trace) { writeTrace(); }
	}

	/**
	 *
	 */
	private void writeTrace() {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < executionContext.depth(); i++) {
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
		if (executionContext != null) {
			final IExecutionContext previous = executionContext;
			executionContext = executionContext.getOuterContext();
			previous.dispose();
		}
	}

	@Override
	public ISymbol getCurrentSymbol() {
		return currentSymbol;
	}

	/**
	 * Method execute(). Asks the scope to manage the execution of a statement on an agent, taking care of pushing the
	 * agent on the stack, verifying the runtime state, etc. This method accepts optional arguments (which can be null)
	 *
	 * @see msi.gama.runtime.IScope#execute(msi.gaml.statements.IStatement, msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	public ExecutionResult execute(final IExecutable statement, final IAgent agent, final Arguments args) {
		if (statement == null || agent == null || interrupted() || agent.dead()) return FAILED;
		// We keep the current pushed agent (context of this execution)
		final IAgent caller = this.getAgent();
		// We then try to push the agent on the stack
		final boolean pushed = push(agent);
		try (StopWatch w = GAMA.benchmark(this, statement)) {
			// Otherwise we compute the result of the statement, pushing the
			// arguments if the statement expects them
			if (args != null) { args.setCaller(caller); }
			// See issue #2815: we also push args even if they are null
			statement.setRuntimeArgs(this, args);
			// We push the caller to the remote sequence (will be cleaned when the remote sequence leaves its scope)
			statement.setMyself(caller);
			return withValue(statement.executeOn(ExecutionScope.this));
		} catch (final GamaRuntimeException g) {
			GAMA.reportAndThrowIfNeeded(this, g, true);
			return ExecutionResult.FAILED;
		} finally {
			// We clean the caller that may have been set previously so as to keep the arguments clean
			if (args != null) { args.setCaller(null); }
			// Whatever the outcome, we pop the agent from the stack if it has
			// been previously pushed
			if (pushed) { pop(agent); }
		}

	}

	@Override
	public void stackArguments(final Arguments actualArgs) {
		if (actualArgs == null) return;
		boolean callerPushed = false;
		final IAgent caller = actualArgs.getCaller();
		if (caller != null) { callerPushed = push(caller); }
		try {
			actualArgs.forEachFacet((a, b) -> {
				final IExpression e = b.getExpression();
				if (e != null) { addVarWithValue(a, e.value(ExecutionScope.this)); }
				return true;
			});

		} finally {
			if (callerPushed) { pop(caller); }
		}
	}

	@Override
	public ExecutionResult step(final IStepable agent) {
		if (agent == null || interrupted()) return FAILED;
		try (StopWatch w = GAMA.benchmark(this, agent)) {
			return withValue(agent.step(this));
		} catch (final Throwable ex) {
			if (ex instanceof OutOfMemoryError) {
				GamaExecutorService.EXCEPTION_HANDLER.uncaughtException(Thread.currentThread(), ex);
				return FAILED;
			} else {
				final GamaRuntimeException g = GamaRuntimeException.create(ex, this);
				GAMA.reportAndThrowIfNeeded(this, g, true);
				return FAILED;
			}
		}
	}

	@Override
	public ExecutionResult init(final IStepable agent) {
		if (agent == null || interrupted()) return FAILED;
		try (StopWatch w = GAMA.benchmark(this, agent)) {
			return withValue(agent.init(this));
		} catch (final Throwable ex) {
			if (ex instanceof OutOfMemoryError) {
				GamaExecutorService.EXCEPTION_HANDLER.uncaughtException(Thread.currentThread(), ex);
				return FAILED;
			} else {
				final GamaRuntimeException g = GamaRuntimeException.create(ex, this);
				GAMA.reportAndThrowIfNeeded(this, g, true);
				return FAILED;
			}
		}
	}

	@Override
	public ExecutionResult step(final IAgent agent) {
		if (agent == null || agent.dead() || interrupted()) return FAILED;
		final boolean pushed = push(agent);
		try {
			try (StopWatch w = GAMA.benchmark(this, agent)) {
				return withValue(agent.step(this));
			} catch (final Throwable ex) {
				if (ex instanceof OutOfMemoryError) {
					GamaExecutorService.EXCEPTION_HANDLER.uncaughtException(Thread.currentThread(), ex);
					return FAILED;
				} else {
					final GamaRuntimeException g = GamaRuntimeException.create(ex, this);
					GAMA.reportAndThrowIfNeeded(this, g, true);
					return FAILED;
				}
			}
		} finally {
			if (pushed) { pop(agent); }
		}
	}

	@Override
	public ExecutionResult init(final IAgent agent) {
		if (agent == null || agent.dead() || interrupted()) return FAILED;
		final boolean pushed = push(agent);
		try {
			try (StopWatch w = GAMA.benchmark(this, agent)) {
				return withValue(agent.init(this));
			} catch (final Throwable ex) {
				if (ex instanceof OutOfMemoryError) {
					GamaExecutorService.EXCEPTION_HANDLER.uncaughtException(Thread.currentThread(), ex);
					return FAILED;
				} else {
					final GamaRuntimeException g = GamaRuntimeException.create(ex, this);
					GAMA.reportAndThrowIfNeeded(this, g, true);
					return FAILED;
				}
			}
		} finally {
			if (pushed) { pop(agent); }
		}
	}

	@Override
	public ExecutionResult evaluate(final IExpression expr, final IAgent agent) throws GamaRuntimeException {
		if (agent == null || agent.dead() || interrupted()) return FAILED;
		final boolean pushed = push(agent);
		try {
			try (StopWatch w = GAMA.benchmark(this, agent)) {
				return withValue(expr.value(this));
			} catch (final Throwable ex) {
				if (ex instanceof OutOfMemoryError) {
					GamaExecutorService.EXCEPTION_HANDLER.uncaughtException(Thread.currentThread(), ex);
					return FAILED;
				} else {
					final GamaRuntimeException g = GamaRuntimeException.create(ex, this);
					GAMA.reportAndThrowIfNeeded(this, g, true);
					return FAILED;
				}
			}
		} finally {
			if (pushed) { pop(agent); }
		}
	}

	/**
	 * Method getVarValue()
	 *
	 * @see msi.gama.runtime.IScope#getVarValue(java.lang.String)
	 */
	@Override
	public Object getVarValue(final String varName) {
		if (executionContext != null) return executionContext.getTempVar(varName);
		return null;
	}

	/**
	 * Method setVarValue()
	 *
	 * @see msi.gama.runtime.IScope#setVarValue(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setVarValue(final String varName, final Object val) {
		if (executionContext != null) { executionContext.setTempVar(varName, val); }
	}

	/**
	 * Method setVarValue()
	 *
	 * @see msi.gama.runtime.IScope#setVarValue(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setVarValue(final String varName, final Object val, final boolean localScopeOnly) {
		if (executionContext != null) {
			if (localScopeOnly) {
				executionContext.putLocalVar(varName, val);
			} else {
				executionContext.setTempVar(varName, val);
			}
		}
	}

	/**
	 * Method saveAllVarValuesIn()
	 *
	 * @see msi.gama.runtime.IScope#saveAllVarValuesIn(java.util.Map)
	 */
	@Override
	public void saveAllVarValuesIn(final Map<String, Object> varsToSave) {
		if (executionContext != null) { varsToSave.putAll(executionContext.getLocalVars()); }
	}

	/**
	 * Method removeAllVars()
	 *
	 * @see msi.gama.runtime.IScope#removeAllVars()
	 */
	@Override
	public void removeAllVars() {
		if (executionContext != null) { executionContext.clearLocalVars(); }
	}

	/**
	 * Method addVarWithValue()
	 *
	 * @see msi.gama.runtime.IScope#addVarWithValue(java.lang.String, java.lang.Object)
	 */
	@Override
	public void addVarWithValue(final String varName, final Object val) {
		if (executionContext != null) { executionContext.putLocalVar(varName, val); }
	}

	/**
	 * Method setEach()
	 *
	 * @see msi.gama.runtime.IScope#setEach(java.lang.Object)
	 */
	@Override
	public void setEach(final Object value) {
		additionalContext.each = value;

	}

	/**
	 * Method getEach()
	 *
	 * @see msi.gama.runtime.IScope#getEach()
	 */
	@Override
	public Object getEach() {
		return additionalContext.each;
	}

	/**
	 * Method getArg()
	 *
	 * @see msi.gama.runtime.IScope#getArg(java.lang.String, int)
	 */
	@Override
	public Object getArg(final String string, final int type) throws GamaRuntimeException {
		if (executionContext != null)
			return Types.get(type).cast(this, executionContext.getLocalVar(string), null, false);
		return null;
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
		if (executionContext != null) return executionContext.hasLocalVar(name);
		return false;
	}

	/**
	 * Method getAgentVarValue()
	 *
	 * @see msi.gama.runtime.IScope#getAgentVarValue(msi.gama.metamodel.agent.IAgent, java.lang.String)
	 */
	@Override
	public Object getAgentVarValue(final IAgent agent, final String name) throws GamaRuntimeException {
		if (agent == null || agent.dead() || interrupted()) return null;
		final boolean pushed = push(agent);
		try {
			return agent.getDirectVarValue(ExecutionScope.this, name);
		} finally {
			if (pushed) { pop(agent); }
		}
	}

	/**
	 * Method setAgentVarValue()
	 *
	 * @see msi.gama.runtime.IScope#setAgentVarValue(msi.gama.metamodel.agent.IAgent, java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void setAgentVarValue(final IAgent agent, final String name, final Object v) {
		if (agent == null || agent.dead() || interrupted()) return;
		final boolean pushed = push(agent);
		try {
			agent.setDirectVarValue(ExecutionScope.this, name, v);
		} finally {
			if (pushed) { pop(agent); }
		}
	}

	@Override
	public ExecutionResult update(final IAgent a) {
		if (a == null || a.dead() || interrupted()) return FAILED;
		final boolean pushed = push(a);
		try {
			a.getPopulation().updateVariables(this, a);
			return PASSED;
		} catch (final GamaRuntimeException g) {
			GAMA.reportAndThrowIfNeeded(this, g, true);
			return FAILED;
		} finally {
			if (pushed) { pop(a); }
		}
	}

	/**
	 * Method getGlobalVarValue()
	 *
	 * @see msi.gama.runtime.IScope#getGlobalVarValue(java.lang.String)
	 */
	@Override
	public Object getGlobalVarValue(final String name) throws GamaRuntimeException {
		final ITopLevelAgent root = getRoot();
		if (root == null) return null;
		return root.getDirectVarValue(this, name);
	}

	@Override
	public boolean hasAccessToGlobalVar(final String name) {
		final ITopLevelAgent root = getRoot();
		if (root == null) return false;
		return root.hasAttribute(name);
	}

	/**
	 * Method setGlobalVarValue()
	 *
	 * @see msi.gama.runtime.IScope#setGlobalVarValue(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setGlobalVarValue(final String name, final Object v) throws GamaRuntimeException {
		final ITopLevelAgent root = getRoot();
		if (root == null) return;
		root.setDirectVarValue(this, name, v);
	}

	/**
	 * Method getName()
	 *
	 * @see msi.gama.runtime.IScope#getName()
	 */

	@Override
	public String getName() {
		return scopeName;
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
		final ITopology topology = additionalContext.topology;
		if (topology != null) return topology;
		final IAgent a = getAgent();
		return a == null ? null : a.getTopology();
	}

	/**
	 * Method setTopology()
	 *
	 * @see msi.gama.runtime.IScope#setTopology(msi.gama.metamodel.topology.ITopology)
	 */
	@Override
	public ITopology setTopology(final ITopology topo) {
		final ITopology previous = getTopology();
		additionalContext.topology = topo;
		return previous;
	}

	/**
	 * Method setGraphics()
	 *
	 * @see msi.gama.runtime.IScope#setGraphics(msi.gama.common.interfaces.IGraphics)
	 */
	@Override
	public void setGraphics(final IGraphics val) {
		additionalContext.graphics = val;
	}

	/**
	 * Method getGraphics()
	 *
	 * @see msi.gama.runtime.IScope#getGraphics()
	 */
	@Override
	public IGraphics getGraphics() {
		return additionalContext.graphics;
	}

	/**
	 * Method getAgentScope()
	 *
	 * @see msi.gama.runtime.IScope#getAgent()
	 */
	@Override
	public IAgent getAgent() {
		if (agentContext == null) return null;
		return agentContext.getAgent();
	}

	/**
	 * Method getSimulationScope()
	 *
	 * @see msi.gama.runtime.IScope#getSimulation()
	 */
	@Override
	public SimulationAgent getSimulation() {
		final ITopLevelAgent root = getRoot();
		if (root == null) return null;
		return root.getSimulation();
	}

	@Override
	public IExperimentAgent getExperiment() {
		final ITopLevelAgent root = getRoot();
		if (root == null) return null;
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
		if (root == null) return null;
		return getRoot().getModel();
	}

	@Override
	public IType getType(final String name) {
		if (additionalContext.types == null) {
			additionalContext.types =
					((ModelDescription) getExperiment().getSpecies().getModel().getDescription()).getTypesManager();
		}
		return additionalContext.types.get(name);
	}

	/**
	 * Method getClock()
	 *
	 * @see msi.gama.runtime.IScope#getClock()
	 */
	@Override
	public SimulationClock getClock() {
		final ITopLevelAgent root = getRoot();
		if (root == null) return null;
		return root.getClock();
	}

	@Override
	public IAgent[] getAgentsStack() {
		try (final Collector.AsOrderedSet<IAgent> agents = Collector.getOrderedSet()) {
			AgentExecutionContext current = agentContext;
			if (current == null) return new IAgent[0];
			while (current != null) {
				agents.add(current.getAgent());
				current = current.getOuterContext();
			}
			return agents.items().stream().toArray(IAgent[]::new);
		}
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
		if (executionContext != null) {
			final Map value = (Map) this.getVarValue(ATTRIBUTES);
			executionContext.removeLocalVar(ATTRIBUTES);
			return value;
		}
		return Collections.EMPTY_MAP;
	}

	@Override
	public Map peekReadAttributes() {
		final Map value = (Map) this.getVarValue(ATTRIBUTES);
		return value;
	}

	@Override
	public IGui getGui() {
		if (additionalContext.gui != null) return additionalContext.gui;
		final IExperimentAgent experiment = getExperiment();
		if (experiment == null) {
			additionalContext.gui = GAMA.getGui();
		} else if (experiment.getSpecies().isHeadless()) {
			additionalContext.gui = GAMA.getHeadlessGui();
		} else {
			additionalContext.gui = GAMA.getRegularGui();
		}
		return additionalContext.gui;
	}

	@Override
	public ITopLevelAgent getRoot() {
		return additionalContext.rootAgent;
	}

	@Override
	public boolean isPaused() {
		final IExperimentAgent exp = getExperiment();
		if (exp != null) {
			final IExperimentPlan plan = exp.getSpecies();
			if (plan != null) {
				final IExperimentController controller = plan.getController();
				if (controller != null) return controller.getScheduler().paused || isOnUserHold();
			}
		}
		return isOnUserHold();
	}

	/**
	 * Method getRandom()
	 *
	 * @see msi.gama.runtime.IScope#getRandom()
	 */
	@Override
	public RandomUtils getRandom() {
		final ITopLevelAgent root = getRoot();
		if (root == null) return new RandomUtils();
		return root.getRandomGenerator();
	}

	@Override
	public IScope copy(final String additionalName) {
		final ExecutionScope scope = new ExecutionScope(getRoot(), additionalName);
		scope.executionContext = executionContext == null ? null : executionContext.createCopy();
		scope.agentContext = agentContext == null ? null : agentContext.createCopy();
		scope.additionalContext.copyFrom(additionalContext);
		return scope;
	}

	@Override
	public IExecutionContext getExecutionContext() {
		return executionContext;
	}

	@Override
	public void setCurrentError(final GamaRuntimeException g) {
		additionalContext.currentError = g;
	}

	@Override
	public GamaRuntimeException getCurrentError() {
		return additionalContext.currentError;
	}

	@Override
	public void setHorizontalPixelContext() {
		additionalContext.horizontalPixelContext = true;

	}

	@Override
	public void setVerticalPixelContext() {
		additionalContext.horizontalPixelContext = false;

	}

	@Override
	public boolean isHorizontalPixelContext() {
		return additionalContext.horizontalPixelContext;
	}

}
