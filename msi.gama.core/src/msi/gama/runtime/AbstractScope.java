/**
 * Created by drogoul, 23 mai 2013
 * 
 */
package msi.gama.runtime;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.*;
import msi.gaml.statements.Facets.Facet;
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
	private final Deque<IAgent> agents = new ArrayDeque();
	private final Deque<Record> statements = new ArrayDeque();
	private IGraphics graphics;
	private ITopology topology;
	private volatile boolean _action_halted, _loop_halted, _agent_halted;
	protected final IMacroAgent root;
	private Object each = null;
	private final int number = ScopeNumber++;
	private IStatement currentStatement;

	public AbstractScope(final IMacroAgent root) {
		this.root = root;
		if ( root != null ) {
			agents.push(root);
		}
		statements.push(new NullRecord());
	}

	class NullRecord extends Record {

		public NullRecord() {
			super(null);
			variables = new HashMap();
		}

		@Override
		void setVar(final String name, final Object value) {}

		@Override
		Object getVar(final String name) {
			return null;
		}

		@Override
		boolean hasVar(final String name) {
			return false;
		}

	}

	private class Record {

		Map<String, Object> variables;
		Record previous;

		public Record(final Record previous) {
			this.previous = previous;
		}

		void addVar(final String name, final Object value) {
			if ( variables == null ) {
				variables = new LinkedHashMap();
			}
			variables.put(name, value);
		}

		void setVar(final String name, final Object value) {
			if ( !hasLocalVar(name) ) {
				previous.setVar(name, value);
			} else {
				variables.put(name, value);
			}
		}

		Object getVar(final String name) {
			if ( !hasLocalVar(name) ) { return previous.getVar(name); }
			return variables.get(name);
		}

		Object getLocalVar(final String name) {
			return hasLocalVar(name) ? variables.get(name) : null;
		}

		boolean hasLocalVar(final String name) {
			return variables != null && variables.containsKey(name);
		}

		boolean hasVar(final String name) {
			return hasLocalVar(name) || previous.hasVar(name);
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
	}

	/**
	 * Method push()
	 * @see msi.gama.runtime.IScope#push(msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	public boolean push(final IAgent agent) {
		final IAgent a = agents.peek();
		if ( a != null && a.equals(agent) ) { return false; }
		// GuiUtils.debug("AbstractScope.push " + agent);
		agents.push(agent);
		return true;
	}

	/**
	 * Method push()
	 * @see msi.gama.runtime.IScope#push(msi.gaml.statements.IStatement)
	 */
	@Override
	public void push(final IStatement statement) {
		currentStatement = statement;
		statements.push(new Record(statements.peek()));
	}

	/**
	 * Method pop()
	 * @see msi.gama.runtime.IScope#pop(msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	public void pop(final IAgent agent) {
		// GuiUtils.debug("AbstractScope.pop " + agent);
		// final IAgent a = agents.pop();
		// if ( agent != a ) {
		// GuiUtils.debug("AbstractScope.pop : Different agents !");
		// }
		agents.pop();
		_agent_halted = false;
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
		statements.pop();
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
	public boolean execute(final IStatement statement, final IAgent agent, final Arguments args, final Object[] result) {
		// If the statement or the agent is null, we act as if the scope had been marked as INTERRUPTED
		if ( statement == null || agent == null || interrupted() || agent.dead() ) { return false; }
		// We then try to push the agent on the stack
		final boolean pushed = push(agent);
		try {
			// Otherwise we compute the result of the statement, pushing the arguments if the statement expects them
			if ( args != null && statement instanceof IStatement.WithArgs ) {
				args.setCaller(agent);
				((IStatement.WithArgs) statement).setRuntimeArgs(args);
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

	//
	// /**
	// * Method execute()
	// * @see msi.gama.runtime.IScope#execute(msi.gaml.statements.IStatement.WithArgs, msi.gama.metamodel.agent.IAgent,
	// * msi.gaml.statements.Arguments)
	// */
	// @Override
	// public Object execute(final WithArgs statement, final IAgent agent, final Arguments args) {
	// if ( interrupted() ) { return INTERRUPTED; }
	// Object result;
	// final boolean pushed = push(agent);
	// try {
	// if ( args != null ) {
	// args.setCaller(agent);
	// statement.setRuntimeArgs(args);
	// }
	// result = statement.executeOn(this);
	// } finally {
	// if ( pushed ) {
	// pop(agent);
	// }
	// }
	// return result;
	//
	// }

	@Override
	public void stackArguments(final Arguments actualArgs) {
		boolean callerPushed = false;
		if ( actualArgs == null ) { return; }
		final IAgent caller = actualArgs.getCaller();
		if ( caller != null ) {
			callerPushed = push(caller);
		}
		try {
			for ( final Facet entry : actualArgs.entrySet() ) {
				if ( entry == null ) {
					continue;
				}
				final IExpressionDescription o = entry.getValue();
				final IExpression e = o.getExpression();
				if ( e != null ) {
					addVarWithValue(entry.getKey(), e.value(this));
				}
			}
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
		// GuiUtils.debug("AbstractScope.step" + agent);
		boolean result = false;
		final boolean isAgent = agent instanceof IAgent;
		if ( agent == null || interrupted() || isAgent && ((IAgent) agent).dead() ) { return false; }
		final boolean pushed = isAgent && push((IAgent) agent);
		try {
			result = agent.step(this);
		} catch (final Exception ex) {
			GamaRuntimeException g = GamaRuntimeException.create(ex);
			// if ( isAgent ) {
			// g.addAgent(agent.toString());
			// }
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
			// if ( isAgent ) {
			// g.addAgent(agent.toString());
			// }
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
		final Record r = statements.peek();
		if ( r.variables == null ) { return; }
		varsToSave.putAll(statements.peek().variables);
	}

	/**
	 * Method removeAllVars()
	 * @see msi.gama.runtime.IScope#removeAllVars()
	 */
	@Override
	public void removeAllVars() {
		final Record r = statements.peek();
		if ( r.variables == null ) { return; }
		r.variables.clear();
	}

	/**
	 * Method addVarWithValue()
	 * @see msi.gama.runtime.IScope#addVarWithValue(java.lang.String, java.lang.Object)
	 */
	@Override
	public void addVarWithValue(final String varName, final Object val) {
		statements.peek().addVar(varName, val);
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
		return Types.get(type).cast(this, statements.peek().getLocalVar(string), null);
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
		return statements.peek().hasLocalVar(name);
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
			result = getAgentVarValue(name);
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
	@Override
	public Object getAgentVarValue(final String name) throws GamaRuntimeException {
		if ( interrupted() ) { return null; } // TODO INTERRUPTED ?
		return agents.peek().getDirectVarValue(this, name);
	}

	/**
	 * Method setAgentVarValue()
	 * @see msi.gama.runtime.IScope#setAgentVarValue(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setAgentVarValue(final String name, final Object v) throws GamaRuntimeException {
		if ( !interrupted() ) {
			agents.peek().setDirectVarValue(this, name, v);
		}
	}

	/**
	 * Method setAgentVarValue()
	 * @see msi.gama.runtime.IScope#setAgentVarValue(msi.gama.metamodel.agent.IAgent, java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void setAgentVarValue(final IAgent agent, final String name, final Object v) {
		if ( agent == null || agent.dead() || interrupted() ) { return; }
		final boolean pushed = push(agent);
		try {
			setAgentVarValue(name, v);
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
		return "Scope #" + number + " of " + root;
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
	public IMacroAgent getSimulationScope() {
		return root;
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
	 * Method getClock()
	 * @see msi.gama.runtime.IScope#getClock()
	 */
	@Override
	public SimulationClock getClock() {
		if ( root == null ) { return new SimulationClock(); }
		return root.getClock();
	}

}
