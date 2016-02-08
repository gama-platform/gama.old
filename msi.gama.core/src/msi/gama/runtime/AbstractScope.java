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

import java.util.*;
import gnu.trove.map.hash.THashMap;
import gnu.trove.procedure.TObjectObjectProcedure;
import msi.gama.common.interfaces.*;
import msi.gama.kernel.experiment.*;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.*;
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Strings;
import msi.gaml.statements.*;
import msi.gaml.types.*;

/**
 * Class TrialScope.
 *
 * @author drogoul
 * @since 23 mai 2013
 *
 */

// TODO : PEUT ETRE FAIRE L'INVERSE : A CHAQUE FOIS QUE L'AGENT MEURT ("die"), QU'UNE BOUCLE EST INTERROMPUE ("break")
// OU QU'UNE ACTION AUSSI ("return"), CALCULER LA VALEUR DE INTERRUPTED AU LIEU DE L'EVALUER TOUT LE TEMPS ICI.
public abstract class AbstractScope implements IScope {

	private static int ScopeNumber = 0;
	private final Deque<IAgent> agents = new ArrayDeque(3);
	private final Deque<IRecord> statements = new ArrayDeque();
	private IGraphics graphics;
	private ITopology topology;
	private volatile boolean _action_halted, _loop_halted, _agent_halted;
	protected final ITopLevelAgent root;
	protected final SimulationAgent simulation;
	private Object each = null;
	private final int number = ScopeNumber++;
	private IStatement currentStatement;
	private int tabLevel = -1;
	private boolean trace;
	public Deque<Map> readAttributes = new LinkedList();

	// Allows to disable error reporting for this scope (the value will be read by the error reporting mechnanism).
	private boolean reportErrors = true;

	// Allows (for debugging purposes) to trace how the agents are popped and pushed to the scope
	public boolean traceAgents = false;

	public AbstractScope(final ITopLevelAgent root) {
		this.root = root;
		if ( root != null ) {
			agents.push(root);
			IMacroAgent a = root;
			while (!(a instanceof SimulationAgent) && a != null) {
				a = root.getHost();
			}
			simulation = (SimulationAgent) a;
		} else {
			simulation = null;
		}
		statements.push(new NullRecord());
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
		trace = t;
	}

	final static class NullRecord implements IRecord {

		@Override
		public void setVar(final String name, final Object value) {}

		@Override
		public Object getVar(final String name) {
			return null;
		}

		@Override
		public boolean hasVar(final String name) {
			return false;
		}

		@Override
		public Object put(final String name, final Object value) {
			return null;
		}

		@Override
		public Object get(final Object name) {
			return null;
		}

		@Override
		public boolean contains(final Object name) {
			return false;
		}

		@Override
		public void clear() {}

		@Override
		public Map<? extends String, ? extends Object> getMap() {
			return Collections.EMPTY_MAP;
		}

	}

	private static interface IRecord {

		/**
		 * Adds this variable to the record
		 * @param name
		 * @param value
		 * @return
		 */
		Object put(final String name, final Object value);

		/**
		 * Allows to set either a local variable or a variable belonging to the previous record
		 * @param name
		 * @param value
		 */
		void setVar(final String name, final Object value);

		/**
		 * Allows to get the value of a local variable or a variable belonging to the previous record
		 * @param name
		 * @return
		 */
		Object getVar(final String name);

		/**
		 * Gets the value of a local var. Null if not found.
		 * @param name
		 * @return
		 */
		Object get(final Object name);

		/**
		 * Checks if this record or a previous record has a variable of this name
		 * @param name
		 * @return
		 */
		boolean hasVar(final String name);

		/**
		 * Checks if this record has a variable of this name
		 * @param name
		 * @return
		 */
		public boolean contains(final Object name);

		/**
		 * Removes all variables from the record
		 */
		void clear();

		/**
		 * Returns the backing map
		 * @return
		 */
		Map<? extends String, ? extends Object> getMap();
	}

	private final static class Record extends THashMap<String, Object> implements IRecord {

		IRecord previous;

		public Record(final IRecord previous) {
			super(5);
			this.previous = previous;
		}

		@Override
		public boolean equals(final Object other) {
			if ( !(other instanceof Record) ) { return false; }
			Record that = (Record) other;
			if ( this == that ) { return true; }
			return super.equals(other);
		}

		@Override
		public void setVar(final String name, final Object value) {
			int i = index(name);
			if ( i == -1 ) {
				previous.setVar(name, value);
			} else {
				_values[i] = value;
			}
		}

		@Override
		public Object getVar(final String name) {
			int i = index(name);
			if ( i < 0 ) { return previous.getVar(name); }
			return _values[i];
		}

		@Override
		public boolean hasVar(final String name) {
			return index(name) >= 0 || previous.hasVar(name);
		}

		@Override
		public Map<? extends String, ? extends Object> getMap() {
			return this;
		}

	}

	/**
	 *
	 * Method interrupted(). Returns true if the scope is currently marked as interrupted.
	 * @see msi.gama.runtime.IScope#interrupted()
	 */
	@Override
	public final boolean interrupted() {
		return _root_interrupted() || _action_halted || _loop_halted || _agent_halted;
		// final IAgent a = agents.peek();
		// return a == null || a.dead();
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
	 * @return true if the root agent of the scope is marked as interrupted (i.e. dead)
	 */
	protected abstract boolean _root_interrupted();

	/**
	 * Method clear()
	 * @see msi.gama.runtime.IScope#clear()
	 */
	@Override
	public void clear() {
		agents.clear();
		statements.clear();
		each = null;
		graphics = null;
		topology = null;
		currentStatement = null;
		readAttributes.clear();
	}

	/**
	 * Method push()
	 * @see msi.gama.runtime.IScope#push(msi.gama.metamodel.agent.IAgent)
	 */
	// @Override
	protected boolean push(final IAgent agent) {
		final IAgent a = agents.peek();
		if ( a != null && a.equals(agent) ) { return false; }
		if ( traceAgents ) {
			for ( int i = 0; i < agents.size(); i++ ) {
				System.out.print("\t");
			}
			System.out.println("" + agent + " pushed to " + this);
		}
		agents.push(agent);
		return true;
	}

	/**
	 * Method pop()
	 * @see msi.gama.runtime.IScope#pop(msi.gama.metamodel.agent.IAgent)
	 */
	// @Override
	protected void pop(final IAgent agent) {
		try {
			IAgent a = agents.pop();
			if ( !a.equals(agent) ) {
				System.out
					.println("Problem with the scope. Trying to pop  " + agent + " but " + a + " was in the stack...");
			}
			if ( traceAgents ) {
				for ( int i = 0; i < agents.size(); i++ ) {
					System.out.print("\t");
				}
				System.out.println("" + a + " popped from " + this);
			}
		} catch (NoSuchElementException e) {
			return;
		}

		_agent_halted = false;
	}

	/**
	 * Method push()
	 * @see msi.gama.runtime.IScope#push(msi.gaml.statements.IStatement)
	 */
	@Override
	public void push(final IStatement statement) {
		tabLevel++;
		setStatement(statement);
		statements.push(new Record(statements.peek()));
	}

	@Override
	public void setStatement(final IStatement statement) {
		currentStatement = statement;
		if ( trace ) {
			writeTrace();
		}
	}

	/**
	 *
	 */
	private void writeTrace() {
		StringBuilder sb = new StringBuilder();
		for ( int i = 0; i < tabLevel; i++ ) {
			sb.append(Strings.TAB);
		}
		sb.append(currentStatement.getTrace(this));
		this.getGui().informConsole(sb.toString(), root);
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
	 * @see msi.gama.runtime.IScope#pop(msi.gaml.statements.IStatement)
	 */
	@Override
	public void pop(final IStatement statement) {
		try {
			statements.pop();
		} catch (NoSuchElementException e) {
			return;
		}
		if ( trace ) {
			tabLevel--;
		}
	}

	@Override
	public IStatement getStatement() {
		return currentStatement;
	}

	/**
	 * Method execute(). Asks the scope to manage the execution of a statement on an agent, taking care of pushing the
	 * agent on the stack, verifying the runtime state, etc. This method accepts optional arguments (which can be null)
	 * @see msi.gama.runtime.IScope#execute(msi.gaml.statements.IStatement, msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	public boolean execute(final IExecutable statement, final IAgent agent, final Arguments args,
		final Object[] result) {
		// If the statement or the agent is null, we act as if the scope had been marked as INTERRUPTED
		// IScope scope = agent == null ? this :(statement instanceof RemoteSequence ? this : agent.getScope());
		IAgent caller = this.getAgentScope();
		if ( statement == null || agent == null || interrupted() || agent.dead() ) { return false; }
		// We then try to push the agent on the stack
		final boolean pushed = push(agent);
		try {
			// Otherwise we compute the result of the statement, pushing the arguments if the statement expects them
			if ( args != null && statement instanceof IStatement.WithArgs ) {
				args.setCaller(caller);
				((IStatement.WithArgs) statement).setRuntimeArgs(args);
			} else if ( statement instanceof RemoteSequence ) {
				((RemoteSequence) statement).setMyself(caller);
				// We delegate to the remote scope
				result[0] = statement.executeOn(this);
				return true;
			}
			result[0] = statement.executeOn(this);
		} catch (final GamaRuntimeException g) {
			// If an exception occurs, we throw it and return false (could be INTERRUPTED as well)
			// g.addAgent(agent.getName());
			GAMA.reportAndThrowIfNeeded(this, g, true);
		} finally {
			// Whatever the outcome, we pop the agent from the stack if it has been previously pushed
			if ( pushed ) {
				pop(agent);
			}
		}
		return true;

	}

	@Override
	public void stackArguments(final Arguments actualArgs) {
		boolean callerPushed = false;
		if ( actualArgs == null ) { return; }
		final IAgent caller = actualArgs.getCaller();
		if ( caller != null ) {
			callerPushed = push(caller);
		}
		try {
			actualArgs.forEachEntry(new TObjectObjectProcedure<String, IExpressionDescription>() {

				@Override
				public boolean execute(final String a, final IExpressionDescription b) {
					final IExpression e = b.getExpression();
					if ( e != null ) {
						addVarWithValue(a, e.value(AbstractScope.this));
					}
					return true;
				}
			});

		} finally {
			if ( callerPushed ) {
				pop(caller);
			}
		}
	}

	/**
	 * Method evaluate()
	 * @see msi.gama.runtime.IScope#evaluate(msi.gaml.expressions.IExpression, msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	public Object evaluate(final IExpression expr, final IAgent agent) throws GamaRuntimeException {
		if ( agent == null || interrupted() || agent.dead() ) { return null; }
		final boolean pushed = push(agent);
		try {
			return expr.value(this);
		} catch (final GamaRuntimeException g) {
			// g.addAgent(agent.toString());
			GAMA.reportAndThrowIfNeeded(this, g, true);
			return null;
		} finally {
			if ( pushed ) {
				pop(agent);
			}
		}
	}

	@Override
	public boolean step(final IStepable agent) {
		boolean result = false;
		final boolean isAgent = agent instanceof IAgent;
		if ( agent == null || interrupted() || isAgent && ((IAgent) agent).dead() ) { return false; }
		final boolean pushed = isAgent && push((IAgent) agent);
		try {
			result = agent.step(this);
		} catch (final Exception ex) {
			GamaRuntimeException g = GamaRuntimeException.create(ex, this);
			GAMA.reportAndThrowIfNeeded(this, g, true);
		} finally {
			if ( pushed ) {
				pop((IAgent) agent);
			}
		}
		return result;
	}

	@Override
	public boolean init(final IStepable agent) {
		boolean result = false;
		final boolean isAgent = agent instanceof IAgent;
		if ( agent == null || interrupted() || isAgent && ((IAgent) agent).dead() ) { return false; }
		final boolean pushed = isAgent && push((IAgent) agent);
		try {
			result = agent.init(this);
		} catch (final GamaRuntimeException g) {
			GAMA.reportAndThrowIfNeeded(this, g, true);
		} finally {
			if ( pushed ) {
				pop((IAgent) agent);
			}
		}
		return result;
	}

	/**
	 * Method getVarValue()
	 * @see msi.gama.runtime.IScope#getVarValue(java.lang.String)
	 */
	@Override
	public Object getVarValue(final String varName) {
		return statements.peek().getVar(varName);
	}

	/**
	 * Method setVarValue()
	 * @see msi.gama.runtime.IScope#setVarValue(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setVarValue(final String varName, final Object val) {
		statements.peek().setVar(varName, val);
	}

	/**
	 * Method saveAllVarValuesIn()
	 * @see msi.gama.runtime.IScope#saveAllVarValuesIn(java.util.Map)
	 */
	@Override
	public void saveAllVarValuesIn(final Map<String, Object> varsToSave) {
		// final IRecord r = statements.peek();
		varsToSave.putAll(statements.peek().getMap());
	}

	/**
	 * Method removeAllVars()
	 * @see msi.gama.runtime.IScope#removeAllVars()
	 */
	@Override
	public void removeAllVars() {
		final IRecord r = statements.peek();
		r.clear();

	}

	/**
	 * Method addVarWithValue()
	 * @see msi.gama.runtime.IScope#addVarWithValue(java.lang.String, java.lang.Object)
	 */
	@Override
	public void addVarWithValue(final String varName, final Object val) {
		statements.peek().put(varName, val);
	}

	/**
	 * Method setEach()
	 * @see msi.gama.runtime.IScope#setEach(java.lang.Object)
	 */
	@Override
	public void setEach(final Object value) {
		each = value;
	}

	/**
	 * Method getEach()
	 * @see msi.gama.runtime.IScope#getEach()
	 */
	@Override
	public Object getEach() {
		return each;
	}

	/**
	 * Method getArg()
	 * @see msi.gama.runtime.IScope#getArg(java.lang.String, int)
	 */
	@Override
	public Object getArg(final String string, final int type) throws GamaRuntimeException {
		return Types.get(type).cast(this, statements.peek().get(string), null, Types.NO_TYPE, Types.NO_TYPE, false);
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
	 * @see msi.gama.runtime.IScope#hasArg(java.lang.String)
	 */
	@Override
	public boolean hasArg(final String name) {
		return statements.peek().contains(name);
	}

	/**
	 * Method hasVar()
	 * @see msi.gama.runtime.IScope#hasVar(java.lang.String)
	 */
	@Override
	public boolean hasVar(final String name) {
		return statements.peek().hasVar(name);
	}

	/**
	 * Method getAgentVarValue()
	 * @see msi.gama.runtime.IScope#getAgentVarValue(msi.gama.metamodel.agent.IAgent, java.lang.String)
	 */
	@Override
	public Object getAgentVarValue(final IAgent agent, final String name) throws GamaRuntimeException {
		if ( agent == null || agent.dead() || interrupted() ) { return null; } // TODO Interrupted ?
		Object result = null;
		final boolean pushed = push(agent);
		try {
			result = agent.getDirectVarValue(this, name);
			// result = getAgentVarValue(name);
		} finally {
			if ( pushed ) {
				pop(agent);
			}
		}
		return result;
	}

	/**
	 * Method getAgentVarValue()
	 * @see msi.gama.runtime.IScope#getAgentVarValue(java.lang.String)
	 */
	// @Override
	// public Object getAgentVarValue(final String name) throws GamaRuntimeException {
	// if ( interrupted() ) { return null; } // TODO INTERRUPTED ?
	// return agents.peek().getDirectVarValue(this, name);
	// }

	/**
	 * Method setAgentVarValue()
	 * @see msi.gama.runtime.IScope#setAgentVarValue(java.lang.String, java.lang.Object)
	 */
	// @Override
	// public void setAgentVarValue(final String name, final Object v) throws GamaRuntimeException {
	// if ( !interrupted() ) {
	// agents.peek().setDirectVarValue(this, name, v);
	// }
	// }

	/**
	 * Method setAgentVarValue()
	 * @see msi.gama.runtime.IScope#setAgentVarValue(msi.gama.metamodel.agent.IAgent, java.lang.String, java.lang.Object)
	 */
	@Override
	public void setAgentVarValue(final IAgent agent, final String name, final Object v) {
		if ( agent == null || agent.dead() || interrupted() ) { return; }
		final boolean pushed = push(agent);
		try {
			agent.setDirectVarValue(this, name, v);
			// setAgentVarValue(name, v);
		} finally {
			if ( pushed ) {
				pop(agent);
			}
		}
	}

	@Override
	public boolean update(final IAgent a) {
		if ( a == null || a.dead() || interrupted() ) { return false; }
		final boolean pushed = push(a);
		try {
			a.getPopulation().updateVariables(this, a);
		} finally {
			if ( pushed ) {
				pop(a);
			}
		}
		return true;
	}

	/**
	 * Method getGlobalVarValue()
	 * @see msi.gama.runtime.IScope#getGlobalVarValue(java.lang.String)
	 */
	@Override
	public Object getGlobalVarValue(final String name) throws GamaRuntimeException {
		return root.getDirectVarValue(this, name);
	}

	/**
	 * Method setGlobalVarValue()
	 * @see msi.gama.runtime.IScope#setGlobalVarValue(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setGlobalVarValue(final String name, final Object v) throws GamaRuntimeException {
		root.setDirectVarValue(this, name, v);
	}

	/**
	 * Method getName()
	 * @see msi.gama.runtime.IScope#getName()
	 */

	@Override
	public String getName() {
		return "SimulationScope #" + number + " of " + root;
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Method getTopology()
	 * @see msi.gama.runtime.IScope#getTopology()
	 */
	@Override
	public ITopology getTopology() {
		return topology != null ? topology : agents.peek().getTopology();
	}

	/**
	 * Method setTopology()
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
	 * @see msi.gama.runtime.IScope#setGraphics(msi.gama.common.interfaces.IGraphics)
	 */
	@Override
	public void setGraphics(final IGraphics val) {
		graphics = val;
	}

	/**
	 * Method getGraphics()
	 * @see msi.gama.runtime.IScope#getGraphics()
	 */
	@Override
	public IGraphics getGraphics() {
		return graphics;
	}

	/**
	 * Method getAgentScope()
	 * @see msi.gama.runtime.IScope#getAgentScope()
	 */
	@Override
	public IAgent getAgentScope() {
		return agents.peek();
	}

	/**
	 * Method getSimulationScope()
	 * @see msi.gama.runtime.IScope#getSimulationScope()
	 */
	@Override
	public SimulationAgent getSimulationScope() {
		return simulation;
	}

	@Override
	public IExperimentAgent getExperiment() {
		return simulation == null ? null : simulation.getExperiment();
	}

	/**
	 * Method getModel()
	 * @see msi.gama.runtime.IScope#getModel()
	 */
	@Override
	public IModel getModel() {
		return root.getModel();
	}

	/**
	 * Method getExperimentContext()
	 * @see msi.gama.runtime.IScope#getExperimentContext()
	 */
	@Override
	public IDescription getExperimentContext() {
		IExperimentAgent a = getExperiment();
		if ( a == null ) { return null; }
		return a.getSpecies().getDescription();
	}

	/**
	 * Method getModelContext()
	 * @see msi.gama.runtime.IScope#getModelContext()
	 */
	@Override
	public IDescription getModelContext() {
		IModel model = getModel();
		if ( model == null ) { return null; }
		return model.getDescription();
	}

	/**
	 * Method getClock()
	 * @see msi.gama.runtime.IScope#getClock()
	 */
	@Override
	public SimulationClock getClock() {
		if ( root == null ) { return null; }
		// if ( root == null ) { return new SimulationClock(); }
		return root.getClock();
	}

	@Override
	public IAgent[] getAgentsStack() {
		IAgent[] result = new IAgent[agents.size()];
		return agents.toArray(result);
	}

	/**
	 * Method pushReadAttributes()
	 * @see msi.gama.runtime.IScope#pushReadAttributes(java.util.Map)
	 */
	@Override
	public void pushReadAttributes(final Map values) {
		readAttributes.push(values);
	}

	/**
	 * Method popReadAttributes()
	 * @see msi.gama.runtime.IScope#popReadAttributes()
	 */
	@Override
	public Map popReadAttributes() {
		return readAttributes.pop();
	}

	@Override
	public Map peekReadAttributes() {
		return readAttributes.peek();
	}

	@Override
	public IGui getGui() {
		IExperimentAgent experiment = getExperiment();
		if ( experiment == null ) { return GAMA.getGui(); }
		if ( experiment.getSpecies().isHeadless() ) {
			return GAMA.getHeadlessGui();
		} else {
			return GAMA.getRegularGui();
		}
	}

	@Override
	public ITopLevelAgent getRoot() {
		return root;
	}

}
