/*******************************************************************************************************
 *
 * IScope.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.runtime;

import java.io.Closeable;
import java.util.EnumSet;
import java.util.Map;

import msi.gama.common.interfaces.IBenchmarkable;
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
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.IExecutable;
import msi.gaml.types.IType;

// TODO: Auto-generated Javadoc
/**
 * Written by drogoul Modified on 18 janv. 2011
 *
 * @todo Description
 *
 */

/**
 * The Interface IScope.
 */
@SuppressWarnings ({ "rawtypes" })
public interface IScope extends Closeable, IBenchmarkable {

	/** The interrupting statuses. */
	EnumSet<FlowStatus> INTERRUPTING_STATUSES =
			EnumSet.of(FlowStatus.BREAK, FlowStatus.RETURN, FlowStatus.CONTINUE, FlowStatus.DIE, FlowStatus.DISPOSE);

	/**
	 * The Interface IGraphicsScope.
	 */
	public interface IGraphicsScope extends IScope {
		/**
		 * Copy.
		 *
		 * @param additionalName
		 *            the additional name
		 * @return the i scope
		 */
		@Override
		IGraphicsScope copy(String additionalName);

		/**
		 * Sets the horizontal pixel context.
		 */
		void setHorizontalPixelContext();

		/**
		 * Sets the vertical pixel context.
		 */
		void setVerticalPixelContext();

		/**
		 * Checks if is horizontal pixel context.
		 *
		 * @return true, if is horizontal pixel context
		 */
		boolean isHorizontalPixelContext();

		/**
		 * Sets the graphics.
		 *
		 * @param val
		 *            the new graphics
		 */
		void setGraphics(IGraphics val);

		/**
		 * Gets the graphics.
		 *
		 * @return the graphics
		 */
		IGraphics getGraphics();

		/**
		 * Checks if is graphics.
		 *
		 * @return true, if is graphics
		 */
		@Override
		default boolean isGraphics() { return true; }
	}

	/**
	 * Management of the scope state.
	 *
	 * clear() removes any contextual information from it. setOnUserHold() allows to suspend execution because the user
	 * is asked for something. isOnUserHold() allows to know it. isPaused() allows to know if the execution is paused.
	 * disableErrorReporting() allows to disable any output of exceptions during an execution. enableErrorReporting()
	 * does the contrary. reportErrors() will return whether this property is true or false. setTrace() allows to turn
	 * the trace of execution on and off. When tracing, the scope will output every execution that takes place on it.
	 * getName() returns the name of the scope. copy() allows to make a copy of the current scope. setInterrupted() and
	 * interrupted() respectively set and retrieve the value of 'interrupted', which marks the end of the execution on
	 * this scope
	 */

	void clear();

	/**
	 * Close.
	 */
	@Override
	default void close() {
		clear();
	}

	/**
	 * Sets the scope on user hold.
	 *
	 * @param b
	 *            the new state
	 */
	void setOnUserHold(boolean b);

	/**
	 * Checks if the scope is on user hold.
	 *
	 * @return true, if is on user hold
	 */
	boolean isOnUserHold();

	/**
	 * Checks if the scope is paused.
	 *
	 * @return true, if is paused
	 */
	boolean isPaused();

	/**
	 * Disable error reporting.
	 */
	void disableErrorReporting();

	/**
	 * Enable error reporting.
	 */
	void enableErrorReporting();

	/**
	 * Report errors.
	 *
	 * @return true, if successful
	 */
	boolean reportErrors();

	/**
	 * Sets whether to trace or not the execution
	 *
	 * @param trace
	 *            the new trace
	 */
	void setTrace(boolean trace);

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	String getName();

	/**
	 * Gets the name for benchmarks.
	 *
	 * @return the name for benchmarks
	 */
	@Override
	default String getNameForBenchmarks() { return getName(); }

	/**
	 * Copy.
	 *
	 * @param additionalName
	 *            the additional name
	 * @return the i scope
	 */
	IScope copy(String additionalName);

	/**
	 * Copy for graphics.
	 *
	 * @param additionalName
	 *            the additional name
	 * @return the i graphics scope
	 */
	IGraphicsScope copyForGraphics(String additionalName);

	/**
	 * Interrupted.
	 *
	 * @return true, if successful
	 */
	boolean interrupted();

	/**
	 * Sets the interrupted.
	 */
	// void setInterrupted();

	/**
	 * Keeping track of symbols.
	 *
	 * setCurrentSymbol() indicates which symbol (statement, variable, output, ..) is currently executing. push() does
	 * the same but creates a local context where variables can be manipulated. pop() discards this local context.
	 * getCurrentSymbol() allows to retrieve the latest symbol that has been pushed or set
	 *
	 * @param symbol
	 *            the symbol
	 */

	void push(ISymbol symbol);

	/**
	 * Pop.
	 *
	 * @param symbol
	 *            the symbol
	 */
	void pop(ISymbol symbol);

	/**
	 * Sets the current symbol.
	 *
	 * @param symbol
	 *            the new current symbol
	 */
	void setCurrentSymbol(ISymbol symbol);

	/**
	 * Gets the current symbol.
	 *
	 * @return the current symbol
	 */
	ISymbol getCurrentSymbol();

	/**
	 * Access to read attributes
	 *
	 * Manipulates a distinct stack where the attributes read from files, databases, etc. are temporarily stored.
	 * pushReadAttributes() allows to store a new map of attributes. popReadAttributes() retieves the latest pushed
	 * attributes (and removes them from the stack). peekReadAttributes() retrieves the latest without removing them.
	 *
	 * @param values
	 *            the values
	 */
	void pushReadAttributes(Map values);

	/**
	 * Pop read attributes.
	 *
	 * @return the map
	 */
	Map popReadAttributes();

	/**
	 * Peek read attributes.
	 *
	 * @return the map
	 */
	Map peekReadAttributes();

	/**
	 * Access to various agents and objects
	 *
	 * setEach() allows to fix temporarily the value of the 'each' pseudo-variable, getEach() to retrieve it.
	 *
	 * @param value
	 *            the new each
	 */

	void setEach(Object value);

	/**
	 * Gets the each.
	 *
	 * @return the each
	 */
	Object getEach();

	/**
	 * Gets the root.
	 *
	 * @return the root
	 */
	ITopLevelAgent getRoot();

	/**
	 * Gets the simulation.
	 *
	 * @return the simulation
	 */
	SimulationAgent getSimulation();

	/**
	 * Gets the experiment.
	 *
	 * @return the experiment
	 */
	IExperimentAgent getExperiment();

	/**
	 * Current agent management.
	 *
	 * getAgentScope() returns the currently pushed agent. push() allos to keep trace of the current agent, while pop()
	 * will retrieve it. getAgentScope() returns the currently pushed agent. getAgentsStack() returns a copy of the
	 * stack of agents
	 *
	 * @param iAgent
	 *            the i agent
	 */

	void pop(IAgent iAgent);

	/**
	 * Push.
	 *
	 * @param iAgent
	 *            the i agent
	 * @return true, if successful
	 */
	boolean push(IAgent iAgent);

	/**
	 * Gets the agent.
	 *
	 * @return the agent
	 */
	IAgent getAgent();

	/**
	 * Gets the agents stack.
	 *
	 * @return the agents stack
	 */
	IAgent[] getAgentsStack();

	/**
	 * Access to utilities and runtime contexts
	 *
	 * getRandom() gives access to the current random number generator. getGui() returns the current user-interface
	 * component being used.
	 *
	 * @return the random
	 */

	RandomUtils getRandom();

	/**
	 * Gets the gui.
	 *
	 * @return the gui
	 */
	IGui getGui();

	/**
	 * Gets the clock.
	 *
	 * @return the clock
	 */
	SimulationClock getClock();

	/**
	 * Gets the topology.
	 *
	 * @return the topology
	 */
	ITopology getTopology();

	/**
	 * Sets the topology.
	 *
	 * @param topology
	 *            the topology
	 * @return the i topology
	 */
	ITopology setTopology(ITopology topology);

	/**
	 * Execute.
	 *
	 * @param executable
	 *            the executable
	 * @return the execution result
	 */
	default ExecutionResult execute(final IExecutable executable) {
		return execute(executable, getAgent(), null);
	}

	/**
	 * Execute.
	 *
	 * @param executable
	 *            the executable
	 * @param args
	 *            the args
	 * @return the execution result
	 */
	default ExecutionResult execute(final IExecutable executable, final Arguments args) {
		return execute(executable, getAgent(), args);
	}

	/**
	 * Execution flow.
	 *
	 * @param executable
	 *            the executable
	 * @param agent
	 *            the agent
	 * @param args
	 *            the args
	 * @return the execution result
	 */

	default ExecutionResult execute(final IExecutable executable, final IAgent agent, final Arguments args) {
		return execute(executable, agent, false, args);
	}

	/**
	 * Evaluate.
	 *
	 * @param expr
	 *            the expr
	 * @param agent
	 *            the agent
	 * @return the execution result
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	ExecutionResult evaluate(IExpression expr, IAgent agent) throws GamaRuntimeException;

	/**
	 * Access to variables (agent and context).
	 *
	 * @param varName
	 *            the var name
	 * @return the var value
	 */

	Object getVarValue(String varName);

	/**
	 * Gets the agent var value.
	 *
	 * @param agent
	 *            the agent
	 * @param name
	 *            the name
	 * @return the agent var value
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	Object getAgentVarValue(IAgent agent, String name) throws GamaRuntimeException;

	/**
	 * Sets the agent var value.
	 *
	 * @param agent
	 *            the agent
	 * @param name
	 *            the name
	 * @param v
	 *            the v
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	void setAgentVarValue(IAgent agent, String name, Object v) throws GamaRuntimeException;

	/**
	 * Gets the global var value.
	 *
	 * @param name
	 *            the name
	 * @return the global var value
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	Object getGlobalVarValue(String name) throws GamaRuntimeException;

	/**
	 * Verifies that this scope has access to the global var value named 'name'
	 *
	 * @param name
	 * @return
	 */
	boolean hasAccessToGlobalVar(String name);

	/**
	 * Sets the global var value.
	 *
	 * @param name
	 *            the name
	 * @param v
	 *            the v
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	void setGlobalVarValue(String name, Object v) throws GamaRuntimeException;

	/**
	 * Sets the var value.
	 *
	 * @param varName
	 *            the var name
	 * @param val
	 *            the val
	 */
	default void setVarValue(final String varName, final Object val) {
		setVarValue(varName, val, false);
	}

	/**
	 * Sets the var value, and states whether the var should be written in an outer scope (if it is defined there) or
	 * kept in this scope (like for instance the variable defined in a loop (see Issue #3085)
	 *
	 * @param varName
	 * @param val
	 * @param localScopeOnly
	 */
	void setVarValue(String varName, Object val, boolean localScopeOnly);

	/**
	 * Save all var values in.
	 *
	 * @param varsToSave
	 *            the vars to save
	 */
	void saveAllVarValuesIn(Map<String, Object> varsToSave);

	/**
	 * Removes the all vars.
	 */
	void removeAllVars();

	/**
	 * Adds the var with value.
	 *
	 * @param varName
	 *            the var name
	 * @param val
	 *            the val
	 */
	void addVarWithValue(String varName, Object val);

	/**
	 * Access to arguments (of actions).
	 *
	 * @param string
	 *            the string
	 * @param type
	 *            the type
	 * @return the arg
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */

	Object getArg(String string, int type) throws GamaRuntimeException;

	/**
	 * Gets the int arg.
	 *
	 * @param string
	 *            the string
	 * @return the int arg
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	Integer getIntArg(String string) throws GamaRuntimeException;

	/**
	 * Gets the float arg.
	 *
	 * @param string
	 *            the string
	 * @return the float arg
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	Double getFloatArg(String string) throws GamaRuntimeException;

	/**
	 * Gets the list arg.
	 *
	 * @param string
	 *            the string
	 * @return the list arg
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	<T> IList<T> getListArg(String string) throws GamaRuntimeException;

	/**
	 * Gets the string arg.
	 *
	 * @param string
	 *            the string
	 * @return the string arg
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	String getStringArg(String string) throws GamaRuntimeException;

	/**
	 * Gets the bool arg.
	 *
	 * @param string
	 *            the string
	 * @return the bool arg
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	Boolean getBoolArg(String string) throws GamaRuntimeException;

	/**
	 * Checks for arg.
	 *
	 * @param string
	 *            the string
	 * @return true, if successful
	 */
	boolean hasArg(String string);

	/**
	 * Gets the type.
	 *
	 * @param name
	 *            the name
	 * @return the type
	 */
	IType getType(final String name);

	/**
	 * Gets the model.
	 *
	 * @return the model
	 */
	IModel getModel();

	/**
	 * Indicates that a loop is finishing : should clear any _loop_halted status present.
	 */
	// void popLoop();

	/**
	 * Indicates that an action is finishing : should clear any _action_halted status present.
	 */
	// void popAction();

	/**
	 * Should set the _action_halted flag to true.
	 */
	// void interruptAction();

	/**
	 * Should set the _agent_halted flag to true.
	 */
	// void interruptAgent();

	/**
	 * Should set the _loop_halted flag to true.
	 */
	// void interruptLoop();
	void setFlowStatus(FlowStatus status);

	/**
	 * Gets the flow status.
	 *
	 * @return the flow status
	 */
	// FlowStatus getAndClearFlowStatus();

	/**
	 * Inits the.
	 *
	 * @param agent
	 *            the agent
	 * @return the execution result
	 */
	ExecutionResult init(final IStepable agent);

	/**
	 * Step.
	 *
	 * @param agent
	 *            the agent
	 * @return the execution result
	 */
	ExecutionResult step(final IStepable agent);

	/**
	 * Inits the.
	 *
	 * @param agent
	 *            the agent
	 * @return the execution result
	 */
	ExecutionResult init(final IAgent agent);

	/**
	 * Step.
	 *
	 * @param agent
	 *            the agent
	 * @return the execution result
	 */
	ExecutionResult step(final IAgent agent);

	/**
	 * Stack arguments.
	 *
	 * @param actualArgs
	 *            the actual args
	 */
	void stackArguments(Arguments actualArgs);

	/**
	 * Update.
	 *
	 * @param agent
	 *            the agent
	 * @return the execution result
	 */
	ExecutionResult update(IAgent agent);

	/**
	 * Gets the execution context.
	 *
	 * @return the execution context
	 */
	IExecutionContext getExecutionContext();

	/**
	 * Checks if is in try mode.
	 *
	 * @return true, if is in try mode
	 */
	boolean isInTryMode();

	/**
	 * Enable try mode.
	 */
	void enableTryMode();

	/**
	 * Disable try mode.
	 */
	void disableTryMode();

	/**
	 * @return the current statement or null if none
	 */

	void setCurrentError(GamaRuntimeException g);

	/**
	 * Gets the current error.
	 *
	 * @return the current error
	 */
	GamaRuntimeException getCurrentError();

	/**
	 * Checks if is graphics.
	 *
	 * @return true, if is graphics
	 */
	default boolean isGraphics() { return false; }

	/**
	 * Method execute(). Asks the scope to manage the execution of a statement on an agent, taking care of pushing the
	 * agent on the stack, verifying the runtime state, etc. This method accepts optional arguments (which can be null)
	 *
	 * @see msi.gama.runtime.IScope#execute(msi.gaml.statements.IStatement, msi.gama.metamodel.agent.IAgent)
	 */
	ExecutionResult execute(IExecutable statement, IAgent target, boolean useTargetScopeForExecution, Arguments args);

	/**
	 * Gets the and clear break status.
	 *
	 * @return the and clear break status
	 */
	default FlowStatus getAndClearBreakStatus() { return getAndClearFlowStatus(FlowStatus.BREAK); }

	/**
	 * Gets the and clear continue status.
	 *
	 * @return the and clear continue status
	 */
	default FlowStatus getAndClearContinueStatus() { return getAndClearFlowStatus(FlowStatus.CONTINUE); }

	/**
	 * Gets the and clear return status.
	 *
	 * @return the and clear return status
	 */
	default FlowStatus getAndClearReturnStatus() { return getAndClearFlowStatus(FlowStatus.RETURN); }

	/**
	 * Gets the and clear death status.
	 *
	 * @return the and clear death status
	 */
	default FlowStatus getAndClearDeathStatus() { return getAndClearFlowStatus(FlowStatus.DIE); }

	/**
	 * Gets the and clear flow status.
	 *
	 * @param comparison
	 *            the comparison
	 * @return the and clear flow status
	 */
	FlowStatus getAndClearFlowStatus(final FlowStatus comparison);

	/**
	 * Sets the break status.
	 */
	default void setBreakStatus() {
		setFlowStatus(FlowStatus.BREAK);
	}

	/**
	 * Sets the continue status.
	 */
	default void setContinueStatus() {
		setFlowStatus(FlowStatus.CONTINUE);
	}

	/**
	 * Sets the return status.
	 */
	default void setReturnStatus() {
		setFlowStatus(FlowStatus.RETURN);
	}

	/**
	 * Sets the death status.
	 */
	default void setDeathStatus() {
		setFlowStatus(FlowStatus.DIE);
	}

	/**
	 * Sets the release status.
	 */
	default void setDisposeStatus() {
		setFlowStatus(FlowStatus.DISPOSE);
	}

	/**
	 * Checks if is closed.
	 *
	 * @return true, if is closed
	 */
	boolean isClosed();

}
