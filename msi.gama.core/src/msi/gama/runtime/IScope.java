/*******************************************************************************************************
 *
 * msi.gama.runtime.IScope.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.runtime;

import java.io.Closeable;
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
@SuppressWarnings ({ "rawtypes" })
public interface IScope extends Closeable, IBenchmarkable {

	/**
	 * Use this class to accumulate a series of execution results. Only the last one marked as 'passed' will be returned
	 * 
	 * @author drogoul
	 *
	 */
	public static class MutableResult extends ExecutionResultWithValue {

		/**
		 * Instantiates a new mutable result.
		 */
		public MutableResult() {
			super(true, null);
		}

		/**
		 * Accepts an execution result
		 *
		 * @param e
		 *            the execution result
		 * @return true, if successful
		 */
		public boolean accept(final ExecutionResult e) {
			passed = passed && e.passed();
			if (passed) {
				this.value = e.getValue();
			}
			return passed;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see msi.gama.runtime.IScope.ExecutionResultWithValue#getValue()
		 */
		@Override
		public Object getValue() {
			return value;
		}

	}

	/**
	 * The result of executions. 'passed' represents the success or failure of the computation, value its result
	 * 
	 * @author drogoul
	 *
	 */

	public abstract static class ExecutionResult {

		/**
		 * Passed.
		 *
		 * @return true, if successful
		 */
		public abstract boolean passed();

		/**
		 * Gets the value.
		 *
		 * @return the value
		 */
		public Object getValue() {
			return passed();
		}

	}

	/**
	 * The Class FailedExecutionResult.
	 */
	public static class FailedExecutionResult extends ExecutionResult {

		/*
		 * (non-Javadoc)
		 * 
		 * @see msi.gama.runtime.IScope.ExecutionResult#passed()
		 */
		@Override
		public boolean passed() {
			return false;
		}

	}

	/**
	 * The Class SuccessfulExecutionResult.
	 */
	public static class SuccessfulExecutionResult extends ExecutionResult {

		/*
		 * (non-Javadoc)
		 * 
		 * @see msi.gama.runtime.IScope.ExecutionResult#passed()
		 */
		@Override
		public boolean passed() {
			return true;
		}

	}

	/**
	 * The Class ExecutionResultWithValue.
	 */
	public static class ExecutionResultWithValue extends ExecutionResult {

		/** The value. */
		protected Object value;

		/** The passed. */
		protected boolean passed;

		/**
		 * Instantiates a new execution result with a given value.
		 *
		 * @param value
		 *            the value
		 */
		public ExecutionResultWithValue(final Object value) {
			this(true, value);
		}

		/**
		 * Instantiates a new execution result with a flag indicating if the execution is a sucess and an object
		 *
		 * @param passed
		 *            the passed
		 * @param value
		 *            the value
		 */
		public ExecutionResultWithValue(final boolean passed, final Object value) {
			this.passed = passed;
			this.value = value;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see msi.gama.runtime.IScope.ExecutionResult#getValue()
		 */
		@Override
		public Object getValue() {
			return value;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see msi.gama.runtime.IScope.ExecutionResult#passed()
		 */
		@Override
		public boolean passed() {
			return passed;
		}

	}

	/** The Constant PASSED. */
	public final static ExecutionResult PASSED = new SuccessfulExecutionResult();

	/** The Constant FAILED. */
	public final static ExecutionResult FAILED = new FailedExecutionResult();

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

	public abstract void clear();

	@Override
	public default void close() {
		clear();
	}

	/**
	 * Sets the scope on user hold.
	 *
	 * @param b
	 *            the new state
	 */
	public abstract void setOnUserHold(boolean b);

	/**
	 * Checks if the scope is on user hold.
	 *
	 * @return true, if is on user hold
	 */
	public abstract boolean isOnUserHold();

	/**
	 * Checks if the scope is paused.
	 *
	 * @return true, if is paused
	 */
	public abstract boolean isPaused();

	/**
	 * Disable error reporting.
	 */
	public abstract void disableErrorReporting();

	/**
	 * Enable error reporting.
	 */
	public abstract void enableErrorReporting();

	/**
	 * Report errors.
	 *
	 * @return true, if successful
	 */
	public abstract boolean reportErrors();

	/**
	 * Sets whether to trace or not the execution
	 *
	 * @param trace
	 *            the new trace
	 */
	public abstract void setTrace(boolean trace);

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public abstract String getName();

	@Override
	default String getNameForBenchmarks() {
		return getName();
	}

	/**
	 * Copy.
	 *
	 * @param additionalName
	 *            the additional name
	 * @return the i scope
	 */
	public abstract IScope copy(String additionalName);

	/**
	 * Interrupted.
	 *
	 * @return true, if successful
	 */
	public boolean interrupted();

	/**
	 * Sets the interrupted.
	 */
	public void setInterrupted();

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

	public abstract void push(ISymbol symbol);

	/**
	 * Pop.
	 *
	 * @param symbol
	 *            the symbol
	 */
	public abstract void pop(ISymbol symbol);

	/**
	 * Sets the current symbol.
	 *
	 * @param symbol
	 *            the new current symbol
	 */
	public abstract void setCurrentSymbol(ISymbol symbol);

	/**
	 * Gets the current symbol.
	 *
	 * @return the current symbol
	 */
	public abstract ISymbol getCurrentSymbol();

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
	public abstract void pushReadAttributes(Map values);

	/**
	 * Pop read attributes.
	 *
	 * @return the map
	 */
	public abstract Map popReadAttributes();

	/**
	 * Peek read attributes.
	 *
	 * @return the map
	 */
	public abstract Map peekReadAttributes();

	/**
	 * Access to various agents and objects
	 * 
	 * setEach() allows to fix temporarily the value of the 'each' pseudo-variable, getEach() to retrieve it.
	 *
	 * @param value
	 *            the new each
	 */

	public abstract void setEach(Object value);

	/**
	 * Gets the each.
	 *
	 * @return the each
	 */
	public abstract Object getEach();

	/**
	 * Gets the root.
	 *
	 * @return the root
	 */
	public abstract ITopLevelAgent getRoot();

	/**
	 * Gets the simulation.
	 *
	 * @return the simulation
	 */
	public abstract SimulationAgent getSimulation();

	/**
	 * Gets the experiment.
	 *
	 * @return the experiment
	 */
	public abstract IExperimentAgent getExperiment();

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

	public abstract void pop(IAgent iAgent);

	/**
	 * Push.
	 *
	 * @param iAgent
	 *            the i agent
	 * @return true, if successful
	 */
	public abstract boolean push(IAgent iAgent);

	/**
	 * Gets the agent.
	 *
	 * @return the agent
	 */
	public abstract IAgent getAgent();

	/**
	 * Gets the agents stack.
	 *
	 * @return the agents stack
	 */
	public IAgent[] getAgentsStack();

	/**
	 * Access to utilities and runtime contexts
	 * 
	 * getRandom() gives access to the current random number generator. getGui() returns the current user-interface
	 * component being used.
	 *
	 * @return the random
	 */

	public abstract RandomUtils getRandom();

	/**
	 * Gets the gui.
	 *
	 * @return the gui
	 */
	public abstract IGui getGui();

	/**
	 * Gets the clock.
	 *
	 * @return the clock
	 */
	public abstract SimulationClock getClock();

	/**
	 * Gets the topology.
	 *
	 * @return the topology
	 */
	public ITopology getTopology();

	/**
	 * Sets the topology.
	 *
	 * @param topology
	 *            the topology
	 * @return the i topology
	 */
	public ITopology setTopology(ITopology topology);

	/**
	 * Sets the graphics.
	 *
	 * @param val
	 *            the new graphics
	 */
	public abstract void setGraphics(IGraphics val);

	/**
	 * Gets the graphics.
	 *
	 * @return the graphics
	 */
	public abstract IGraphics getGraphics();

	public default ExecutionResult execute(final IExecutable executable) {
		return execute(executable, getAgent(), null);
	}

	public default ExecutionResult execute(final IExecutable executable, final Arguments args) {
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

	public abstract ExecutionResult execute(final IExecutable executable, final IAgent agent, final Arguments args);

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
	public abstract ExecutionResult evaluate(IExpression expr, IAgent agent) throws GamaRuntimeException;

	/**
	 * Access to variables (agent and context).
	 *
	 * @param varName
	 *            the var name
	 * @return the var value
	 */

	public abstract Object getVarValue(String varName);

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
	public abstract Object getAgentVarValue(IAgent agent, String name) throws GamaRuntimeException;

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
	public abstract void setAgentVarValue(IAgent agent, String name, Object v) throws GamaRuntimeException;

	/**
	 * Gets the global var value.
	 *
	 * @param name
	 *            the name
	 * @return the global var value
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public abstract Object getGlobalVarValue(String name) throws GamaRuntimeException;

	/**
	 * Verifies that this scope has access to the global var value named 'name'
	 * 
	 * @param name
	 * @return
	 */
	public abstract boolean hasAccessToGlobalVar(String name);

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
	public abstract void setGlobalVarValue(String name, Object v) throws GamaRuntimeException;

	/**
	 * Sets the var value.
	 *
	 * @param varName
	 *            the var name
	 * @param val
	 *            the val
	 */
	public abstract void setVarValue(String varName, Object val);

	/**
	 * Save all var values in.
	 *
	 * @param varsToSave
	 *            the vars to save
	 */
	public abstract void saveAllVarValuesIn(Map<String, Object> varsToSave);

	/**
	 * Removes the all vars.
	 */
	public abstract void removeAllVars();

	/**
	 * Adds the var with value.
	 *
	 * @param varName
	 *            the var name
	 * @param val
	 *            the val
	 */
	public abstract void addVarWithValue(String varName, Object val);

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

	public abstract Object getArg(String string, int type) throws GamaRuntimeException;

	/**
	 * Gets the int arg.
	 *
	 * @param string
	 *            the string
	 * @return the int arg
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public abstract Integer getIntArg(String string) throws GamaRuntimeException;

	/**
	 * Gets the float arg.
	 *
	 * @param string
	 *            the string
	 * @return the float arg
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public abstract Double getFloatArg(String string) throws GamaRuntimeException;

	/**
	 * Gets the list arg.
	 *
	 * @param string
	 *            the string
	 * @return the list arg
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public abstract IList getListArg(String string) throws GamaRuntimeException;

	/**
	 * Gets the string arg.
	 *
	 * @param string
	 *            the string
	 * @return the string arg
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public abstract String getStringArg(String string) throws GamaRuntimeException;

	/**
	 * Gets the bool arg.
	 *
	 * @param string
	 *            the string
	 * @return the bool arg
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public abstract Boolean getBoolArg(String string) throws GamaRuntimeException;

	/**
	 * Checks for arg.
	 *
	 * @param string
	 *            the string
	 * @return true, if successful
	 */
	public abstract boolean hasArg(String string);

	/**
	 * Gets the type.
	 *
	 * @param name
	 *            the name
	 * @return the type
	 */
	public IType getType(final String name);

	/**
	 * Gets the model.
	 *
	 * @return the model
	 */
	public abstract IModel getModel();

	/**
	 * Indicates that a loop is finishing : should clear any _loop_halted status present.
	 */
	public abstract void popLoop();

	/**
	 * Indicates that an action is finishing : should clear any _action_halted status present.
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

	/**
	 * Inits the.
	 *
	 * @param agent
	 *            the agent
	 * @return the execution result
	 */
	public abstract ExecutionResult init(final IStepable agent);

	/**
	 * Step.
	 *
	 * @param agent
	 *            the agent
	 * @return the execution result
	 */
	public abstract ExecutionResult step(final IStepable agent);

	/**
	 * Stack arguments.
	 *
	 * @param actualArgs
	 *            the actual args
	 */
	public abstract void stackArguments(Arguments actualArgs);

	/**
	 * Update.
	 *
	 * @param agent
	 *            the agent
	 * @return the execution result
	 */
	public abstract ExecutionResult update(IAgent agent);

	public abstract IExecutionContext getExecutionContext();

	public abstract boolean isInTryMode();

	public void enableTryMode();

	public void disableTryMode();

	/**
	 * @return the current statement or null if none
	 */

	public abstract void setCurrentError(GamaRuntimeException g);

	public GamaRuntimeException getCurrentError();

	public abstract void setHorizontalPixelContext();

	public abstract void setVerticalPixelContext();

	public abstract boolean isHorizontalPixelContext();

}
