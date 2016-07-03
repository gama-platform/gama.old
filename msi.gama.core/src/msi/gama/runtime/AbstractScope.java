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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.NoSuchElementException;

import gnu.trove.map.hash.THashMap;
import gnu.trove.procedure.TObjectObjectProcedure;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IStepable;
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
import msi.gaml.descriptions.IExpressionDescription;
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

public abstract class AbstractScope implements IScope {

	private static int ScopeNumber = 0;
	private final String name;
	private final Deque<IAgent> agentsStack = new ArrayDeque(3);
	private final Deque<Record> statementContextsStack = new ArrayDeque(5);
	private Deque<Map> fileAttributesStack;
	private IGraphics graphics;
	private ITopology topology;
	private volatile boolean _action_halted, _loop_halted, _agent_halted, _trace;
	private Object each;
	private ISymbol currentSymbol;

	// Allows to disable error reporting for this scope (the value will be read
	// by the error reporting mechnanism).
	private boolean reportErrors = true;

	public AbstractScope(final ITopLevelAgent root) {
		// this.root = root;
		final int number = ScopeNumber++;
		if (root != null) {
			agentsStack.push(root);
			// IMacroAgent a = root;
			// while (!(a instanceof SimulationAgent) && a != null) {
			// a = root.getHost();
			// }
			// simulation = (SimulationAgent) a;
			name = "Scope of " + root + " #" + number;
		} else {
			// simulation = null;
			name = "Scope without root #" + number;
		}
		statementContextsStack.push(new Record(null));
	}

	public AbstractScope(final ITopLevelAgent root, final String otherName) {
		// this.root = root;
		final int number = ScopeNumber++;
		if (root != null) {
			agentsStack.push(root);
			// IMacroAgent a = root;
			// while (!(a instanceof SimulationAgent) && a != null) {
			// a = root.getHost();
			// }
			// simulation = (SimulationAgent) a;
			name = "Scope of " + root + " (" + otherName + ") #" + number;
		} else {
			// simulation = null;
			name = "Scope without root (" + otherName + ") #" + number;
		}
		statementContextsStack.push(new Record(null));
	}

	/**
	 * Method clear()
	 * 
	 * @see msi.gama.runtime.IScope#clear()
	 */
	@Override
	public void clear() {
		agentsStack.clear();
		statementContextsStack.clear();
		each = null;
		graphics = null;
		topology = null;
		currentSymbol = null;
		if (fileAttributesStack != null)
			fileAttributesStack.clear();
	}

	@Override
	public void disableErrorReporting() {
		reportErrors = false;
	}

	@Override
	public void enableErrorReporting() {
		reportErrors = true;
	}

	@Override
	public boolean reportErrors() {
		return reportErrors;
	}

	@Override
	public void setTrace(final boolean t) {
		_trace = t;
	}

	private class Record extends THashMap<String, Object> {

		Record previous;

		public Record(final Record previous) {
			super(5);
			this.previous = previous;
		}

		@Override
		public boolean equals(final Object other) {
			if (!(other instanceof Record)) {
				return false;
			}
			final Record that = (Record) other;
			if (this == that) {
				return true;
			}
			return super.equals(other);
		}

		public void setVar(final String name, final Object value) {
			final int i = index(name);
			if (i == -1) {
				if (previous != null)
					previous.setVar(name, value);
			} else {
				_values[i] = value;
			}
		}

		public Object getVar(final String name) {
			final int i = index(name);
			if (i < 0) {
				if (previous == null)
					return null;
				return previous.getVar(name);
			}
			return _values[i];
		}

		public boolean hasVar(final String name) {
			return index(name) >= 0 || previous != null && previous.hasVar(name);
		}

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
	public boolean isOnUserHold() {
		return getRoot().isOnUserHold();
	}

	@Override
	public void setOnUserHold(final boolean state) {
		ITopLevelAgent root = getRoot();
		if (root == null) return ;
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
	 * @return true if the root agent of the scope is marked as interrupted
	 *         (i.e. dead)
	 */
	protected abstract boolean _root_interrupted();

	/**
	 * Method push()
	 * 
	 * @see msi.gama.runtime.IScope#push(msi.gama.metamodel.agent.IAgent)
	 */
	// @Override
	@Override
	public synchronized boolean push(final IAgent agent) {
		final IAgent a = agentsStack.peek();
		if (a != null && a.equals(agent)) {
			return false;
		}
		agentsStack.push(agent);
		return true;
	}

	/**
	 * Method pop()
	 * 
	 * @see msi.gama.runtime.IScope#pop(msi.gama.metamodel.agent.IAgent)
	 */
	// @Override
	@Override
	public void pop(final IAgent agent) {
		if (agentsStack.size() == 0) {
			return;
		}
		agentsStack.pop();
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
		statementContextsStack.push(new Record(statementContextsStack.peek()));
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
		for (int i = 0; i < statementContextsStack.size(); i++) {
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
		try {
			statementContextsStack.pop();
		} catch (final NoSuchElementException e) {
			return;
		}
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
		// If the statement or the agent is null, we act as if the scope had
		// been marked as INTERRUPTED
		// IScope scope = agent == null ? this :(statement instanceof
		// RemoteSequence ? this : agent.getScope());
		final IAgent caller = this.getAgentScope();
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
			actualArgs.forEachEntry(new TObjectObjectProcedure<String, IExpressionDescription>() {

				@Override
				public boolean execute(final String a, final IExpressionDescription b) {
					final IExpression e = b.getExpression();
					if (e != null) {
						addVarWithValue(a, e.value(AbstractScope.this));
					}
					return true;
				}
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
		} catch (final Exception ex) {
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
		return statementContextsStack.peek().getVar(varName);
	}

	/**
	 * Method setVarValue()
	 * 
	 * @see msi.gama.runtime.IScope#setVarValue(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void setVarValue(final String varName, final Object val) {
		statementContextsStack.peek().setVar(varName, val);
	}

	/**
	 * Method saveAllVarValuesIn()
	 * 
	 * @see msi.gama.runtime.IScope#saveAllVarValuesIn(java.util.Map)
	 */
	@Override
	public void saveAllVarValuesIn(final Map<String, Object> varsToSave) {
		varsToSave.putAll(statementContextsStack.peek());
	}

	/**
	 * Method removeAllVars()
	 * 
	 * @see msi.gama.runtime.IScope#removeAllVars()
	 */
	@Override
	public void removeAllVars() {
		final Record r = statementContextsStack.peek();
		r.clear();

	}

	/**
	 * Method addVarWithValue()
	 * 
	 * @see msi.gama.runtime.IScope#addVarWithValue(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void addVarWithValue(final String varName, final Object val) {
		statementContextsStack.peek().put(varName, val);
	}

	/**
	 * Method setEach()
	 * 
	 * @see msi.gama.runtime.IScope#setEach(java.lang.Object)
	 */
	@Override
	public void setEach(final Object value) {
		each = value;
	}

	/**
	 * Method getEach()
	 * 
	 * @see msi.gama.runtime.IScope#getEach()
	 */
	@Override
	public Object getEach() {
		return each;
	}

	/**
	 * Method getArg()
	 * 
	 * @see msi.gama.runtime.IScope#getArg(java.lang.String, int)
	 */
	@Override
	public Object getArg(final String string, final int type) throws GamaRuntimeException {
		return Types.get(type).cast(this, statementContextsStack.peek().get(string), null, false);
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
		return statementContextsStack.peek().contains(name);
	}

	/**
	 * Method hasVar()
	 * 
	 * @see msi.gama.runtime.IScope#hasVar(java.lang.String)
	 */
	@Override
	public boolean hasVar(final String name) {
		return statementContextsStack.peek().hasVar(name);
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
		} // TODO Interrupted ?
		Object result = null;
		final boolean pushed = push(agent);
		try {
			result = agent.getDirectVarValue(this, name);
			// result = getAgentVarValue(name);
		} finally {
			if (pushed) {
				pop(agent);
			}
		}
		return result;
	}

	/**
	 * Method getAgentVarValue()
	 * 
	 * @see msi.gama.runtime.IScope#getAgentVarValue(java.lang.String)
	 */
	// @Override
	// public Object getAgentVarValue(final String name) throws
	// GamaRuntimeException {
	// if ( interrupted() ) { return null; } // TODO INTERRUPTED ?
	// return agents.peek().getDirectVarValue(this, name);
	// }

	/**
	 * Method setAgentVarValue()
	 * 
	 * @see msi.gama.runtime.IScope#setAgentVarValue(java.lang.String,
	 *      java.lang.Object)
	 */
	// @Override
	// public void setAgentVarValue(final String name, final Object v) throws
	// GamaRuntimeException {
	// if ( !interrupted() ) {
	// agents.peek().setDirectVarValue(this, name, v);
	// }
	// }

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
			agent.setDirectVarValue(this, name, v);
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
		ITopLevelAgent root = getRoot();
		if (root == null) return null;
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
		ITopLevelAgent root = getRoot();
		if (root == null) return ;
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
		return topology != null ? topology : agentsStack.peek().getTopology();
	}

	/**
	 * Method setTopology()
	 * 
	 * @see msi.gama.runtime.IScope#setTopology(msi.gama.metamodel.topology.ITopology)
	 */
	@Override
	public ITopology setTopology(final ITopology topo) {
		final ITopology previous = topology;
		topology = topo;
		return previous;
	}

	/**
	 * Method setGraphics()
	 * 
	 * @see msi.gama.runtime.IScope#setGraphics(msi.gama.common.interfaces.IGraphics)
	 */
	@Override
	public void setGraphics(final IGraphics val) {
		graphics = val;
	}

	/**
	 * Method getGraphics()
	 * 
	 * @see msi.gama.runtime.IScope#getGraphics()
	 */
	@Override
	public IGraphics getGraphics() {
		return graphics;
	}

	/**
	 * Method getAgentScope()
	 * 
	 * @see msi.gama.runtime.IScope#getAgentScope()
	 */
	@Override
	public IAgent getAgentScope() {
		return agentsStack.peek();
	}

	/**
	 * Method getSimulationScope()
	 * 
	 * @see msi.gama.runtime.IScope#getSimulationScope()
	 */
	@Override
	public SimulationAgent getSimulationScope() {
		ITopLevelAgent root = getRoot();
		if (root == null) return null;
		return root.getSimulation();
	}

	@Override
	public IExperimentAgent getExperiment() {
		ITopLevelAgent root = getRoot();
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
		ITopLevelAgent root = getRoot();
		if (root == null) return null;
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
		ITopLevelAgent root = getRoot();
		if (root == null) {
		 return null;
		 }
		// if ( root == null ) { return new SimulationClock(); }
		return root.getClock();
	}

	@Override
	public IAgent[] getAgentsStack() {
		final IAgent[] result = new IAgent[agentsStack.size()];
		return agentsStack.toArray(result);
	}

	/**
	 * Method pushReadAttributes()
	 * 
	 * @see msi.gama.runtime.IScope#pushReadAttributes(java.util.Map)
	 */
	@Override
	public void pushReadAttributes(final Map values) {
		if (fileAttributesStack == null) {
			fileAttributesStack = new ArrayDeque<>();
		}
		fileAttributesStack.push(values);
	}

	/**
	 * Method popReadAttributes()
	 * 
	 * @see msi.gama.runtime.IScope#popReadAttributes()
	 */
	@Override
	public Map popReadAttributes() {
		if (fileAttributesStack == null)
			return null;
		final Map result = fileAttributesStack.pop();
		if (fileAttributesStack.isEmpty())
			fileAttributesStack = null;
		return result;
	}

	@Override
	public Map peekReadAttributes() {
		if (fileAttributesStack == null)
			return null;
		return fileAttributesStack.peek();
	}

	@Override
	public IGui getGui() {
		final IExperimentAgent experiment = getExperiment();
		if (experiment == null) {
			return GAMA.getGui();
		}
		if (experiment.getSpecies().isHeadless()) {
			return GAMA.getHeadlessGui();
		} else {
			return GAMA.getRegularGui();
		}
	}

	@Override
	public ITopLevelAgent getRoot() {
		return (ITopLevelAgent) agentsStack.peekLast();
	}

	@Override
	public boolean isPaused() {
		return getExperiment().getSpecies().getController().getScheduler().paused || isOnUserHold();
	}

}
