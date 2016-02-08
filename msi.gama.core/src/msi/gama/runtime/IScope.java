/*********************************************************************************************
 *
 *
 * 'IScope.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.runtime;

import java.util.Map;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.RandomUtils;
import msi.gama.kernel.experiment.*;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.*;

/**
 * Written by drogoul Modified on 18 janv. 2011
 *
 * @todo Description
 *
 */
public interface IScope {

	public static final Object INTERRUPTED = new Object();
	public static final Object OK = new Object();

	public abstract void clear();

	/**
	 * Make the agent the current agent in the scope
	 * @param agent
	 * @see getAgentScope()
	 */
	// public abstract boolean push(IAgent agent);

	/**
	 * Makes the statement the current statement in the scope
	 * @param statement
	 */

	public abstract void push(IStatement statement);

	/**
	 * Removes the current agent
	 * @param agent
	 */
	// public abstract void pop(IAgent agent);

	/**
	 * Removes the current statement
	 * @param statement
	 */

	public abstract void pop(IStatement statement);

	/**
	 * Executes the statement on this agent. Equivalent to:
	 * scope.push(agent); statement.executeOn(scope); scope.pop(agent);
	 * @param statement
	 * @param agent
	 * @return
	 * @throws GamaRuntimeException
	 */
	public abstract boolean execute(final IExecutable executable, final IAgent agent, final Arguments args,
		Object[] result);

	/**
	 * Evaluates the expression on the agent. Equivalent to:
	 * scope.push(agent); expr.value(scope); scope.pop(agent);
	 * @param expr
	 * @param agent
	 * @return
	 * @throws GamaRuntimeException
	 */

	public abstract Object evaluate(IExpression expr, IAgent agent) throws GamaRuntimeException;

	public abstract Object getVarValue(String varName);

	public abstract void setVarValue(String varName, Object val);

	public abstract void saveAllVarValuesIn(Map<String, Object> varsToSave);

	public abstract void removeAllVars();

	public abstract void addVarWithValue(String varName, Object val);

	public abstract void setEach(Object value);

	public abstract Object getEach();

	public abstract Object getArg(String string, int type) throws GamaRuntimeException;

	public abstract Integer getIntArg(String string) throws GamaRuntimeException;

	public abstract Double getFloatArg(String string) throws GamaRuntimeException;

	public abstract IList getListArg(String string) throws GamaRuntimeException;

	public abstract String getStringArg(String string) throws GamaRuntimeException;

	public abstract Boolean getBoolArg(String string) throws GamaRuntimeException;

	public abstract boolean hasArg(String string);

	public abstract boolean hasVar(String string);

	public abstract Object getAgentVarValue(IAgent agent, String name) throws GamaRuntimeException;

	// public abstract Object getAgentVarValue(String name) throws GamaRuntimeException;

	// public abstract void setAgentVarValue(String name, Object v) throws GamaRuntimeException;

	public abstract void setAgentVarValue(IAgent agent, String name, Object v) throws GamaRuntimeException;

	// public abstract void setStatus(ExecutionStatus status);

	// public abstract ExecutionStatus getStatus();

	public boolean interrupted();

	public void setInterrupted(boolean interrupted);

	public abstract Object getGlobalVarValue(String name) throws GamaRuntimeException;

	public abstract void setGlobalVarValue(String name, Object v) throws GamaRuntimeException;

	public abstract Object getName();

	/**
	 * CONTEXT METHODS
	 * Used to gather contextual information about the current simulation and
	 * execution context
	 */

	/**
	 * Returns the current topology to use in this scope. Either it has been set (and not unset) or
	 * the scope uses the current agent to compute it.
	 * @return the topology to use in the current scope
	 */
	public ITopology getTopology();

	/**
	 * Sets a new topological context and returns the previous one.
	 * @param topology, the new topology to set
	 * @return the previous topology used
	 */
	public ITopology setTopology(ITopology topology);

	/**
	 * Used to setup a "graphical context" in which the execution can take place. Called by the
	 * update procedures of displays.
	 * @param val, an instance of IGraphics
	 */

	public abstract void setGraphics(IGraphics val);

	/**
	 * Returns the instance of IGraphics currently set, or null if none.
	 * @return
	 */
	public abstract IGraphics getGraphics();

	/**
	 * Returns the current agent being executed.
	 * @return
	 */
	public abstract IAgent getAgentScope();

	/**
	 * Returns the current simulation in which this scope is defined.
	 * @return the current simulation or null if none is defined (unlikely as the scope is created
	 *         by a simulation)
	 */

	public ITopLevelAgent getRoot();

	public abstract SimulationAgent getSimulationScope();

	public abstract IExperimentAgent getExperiment();

	public abstract IDescription getExperimentContext();

	public abstract IDescription getModelContext();

	public abstract IModel getModel();

	public abstract SimulationClock getClock();

	public abstract IScope copy();

	/**
	 * Indicates that a loop is finishing : should clear any _loop_halted status present
	 */
	public abstract void popLoop();

	/**
	 * Indicates that an action is finishing : should clear any _action_halted status present
	 */
	public abstract void popAction();

	/**
	 * Should set the _action_halted flag to true.
	 */
	public abstract void interruptAction();

	/**
	 * Should set the _agent_halted flag to true.
	 */
	public abstract void interruptAgent();

	/**
	 * Should set the _loop_halted flag to true.
	 */
	public abstract void interruptLoop();

	public abstract boolean init(final IStepable agent);

	public abstract boolean step(final IStepable agent);

	/**
	 * @param actualArgs
	 */
	public abstract void stackArguments(Arguments actualArgs);

	/**
	 * @param gamlAgent
	 */
	public abstract boolean update(IAgent agent);

	/**
	 * @return the current statement or null if none
	 */
	public abstract IStatement getStatement();

	public abstract void setTrace(boolean trace);

	/**
	 * @param abstractStatement
	 */
	public abstract void setStatement(IStatement abstractStatement);

	/**
	 * @return
	 */
	public abstract RandomUtils getRandom();

	public void disableErrorReporting();

	public void enableErrorReporting();

	public boolean reportErrors();

	public IAgent[] getAgentsStack();

	/**
	 * Used to store the attributes read from shape files, etc. It involves a distinct stack.
	 * @param values
	 */
	public abstract void pushReadAttributes(Map values);

	public abstract Map popReadAttributes();

	public abstract Map peekReadAttributes();

	/**
	 * @return
	 */
	public abstract IGui getGui();

	/**
	 * @param iAgent
	 */
	public abstract void pop(IAgent iAgent);

	/**
	 * @param iAgent
	 * @return
	 */
	public abstract boolean push(IAgent iAgent);

}
