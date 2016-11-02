/*********************************************************************************************
 *
 *
 * 'IScope.java', in plugin 'msi.gama.core', is part of the source code of the GAMA modeling and simulation platform.
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
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.IExecutable;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 18 janv. 2011
 *
 * @todo Description
 *
 */
@SuppressWarnings ({ "rawtypes" })
public interface IScope {

	/**
	 * Use this class to accumulate a series of execution results. Only the last one marked as 'passed' will be returned
	 * 
	 * @author drogoul
	 *
	 */
	public static class MutableResult extends ExecutionResultWithValue {
		public MutableResult() {
			super(true, null);
		}

		public boolean accept(final ExecutionResult e) {
			passed = passed && e.passed();
			if (passed)
				this.value = e.getValue();
			return passed;
		}

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
		public abstract boolean passed();

		public Object getValue() {
			return passed();
		}

	}

	public static class FailedExecutionResult extends ExecutionResult {

		@Override
		public boolean passed() {
			return false;
		}

	}

	public static class SuccessfulExecutionResult extends ExecutionResult {

		@Override
		public boolean passed() {
			return true;
		}

	}

	public static class ExecutionResultWithValue extends ExecutionResult {

		protected Object value;
		protected boolean passed;

		public ExecutionResultWithValue(final Object value) {
			this(true, value);
		}

		public ExecutionResultWithValue(final boolean passed, final Object value) {
			this.passed = passed;
			this.value = value;
		}

		@Override
		public Object getValue() {
			return value;
		}

		@Override
		public boolean passed() {
			return passed;
		}

	}

	public final static ExecutionResult PASSED = new SuccessfulExecutionResult();
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

	public abstract void setOnUserHold(boolean b);

	public abstract boolean isOnUserHold();

	public abstract boolean isPaused();

	public abstract void disableErrorReporting();

	public abstract void enableErrorReporting();

	public abstract boolean reportErrors();

	public abstract void setTrace(boolean trace);

	public abstract Object getName();

	public abstract IScope copy(String additionalName);

	public boolean interrupted();

	public void setInterrupted();

	/**
	 * Keeping track of symbols.
	 * 
	 * setCurrentSymbol() indicates which symbol (statement, variable, output, ..) is currently executing. push() does
	 * the same but creates a local context where variables can be manipulated. pop() discards this local context.
	 * getCurrentSymbol() allows to retrieve the latest symbol that has been pushed or set
	 */

	public abstract void push(ISymbol symbol);

	public abstract void pop(ISymbol symbol);

	public abstract void setCurrentSymbol(ISymbol symbol);

	public abstract ISymbol getCurrentSymbol();

	/**
	 * Access to read attributes
	 * 
	 * Manipulates a distinct stack where the attributes read from files, databases, etc. are temporarily stored.
	 * pushReadAttributes() allows to store a new map of attributes. popReadAttributes() retieves the latest pushed
	 * attributes (and removes them from the stack). peekReadAttributes() retrieves the latest without removing them.
	 * 
	 */
	public abstract void pushReadAttributes(Map values);

	public abstract Map popReadAttributes();

	public abstract Map peekReadAttributes();

	/**
	 * Access to various agents and objects
	 * 
	 * setEach() allows to fix temporarily the value of the 'each' pseudo-variable, getEach() to retrieve it.
	 */

	public abstract void setEach(Object value);

	public abstract Object getEach();

	public abstract ITopLevelAgent getRoot();

	public abstract SimulationAgent getSimulation();

	public abstract IExperimentAgent getExperiment();

	/**
	 * Current agent management.
	 * 
	 * getAgentScope() returns the currently pushed agent. push() allos to keep trace of the current agent, while pop()
	 * will retrieve it. getAgentScope() returns the currently pushed agent. getAgentsStack() returns a copy of the
	 * stack of agents
	 * 
	 */

	public abstract void pop(IAgent iAgent);

	public abstract boolean push(IAgent iAgent);

	public abstract IAgent getAgent();

	public IAgent[] getAgentsStack();

	/**
	 * Access to utilities and runtime contexts
	 * 
	 * getRandom() gives access to the current random number generator. getGui() returns the current user-interface
	 * component being used.
	 */

	public abstract RandomUtils getRandom();

	public abstract IGui getGui();

	public abstract SimulationClock getClock();

	public ITopology getTopology();

	public ITopology setTopology(ITopology topology);

	public abstract void setGraphics(IGraphics val);

	public abstract IGraphics getGraphics();

	/**
	 * Execution flow
	 * 
	 * 
	 */

	public abstract ExecutionResult execute(final IExecutable executable, final IAgent agent, final Arguments args);

	public abstract ExecutionResult evaluate(IExpression expr, IAgent agent) throws GamaRuntimeException;

	/**
	 * Access to variables (agent and context)
	 * 
	 * 
	 */

	public abstract Object getVarValue(String varName);

	public abstract Object getAgentVarValue(IAgent agent, String name) throws GamaRuntimeException;

	public abstract void setAgentVarValue(IAgent agent, String name, Object v) throws GamaRuntimeException;

	public abstract Object getGlobalVarValue(String name) throws GamaRuntimeException;

	public abstract void setGlobalVarValue(String name, Object v) throws GamaRuntimeException;

	public abstract void setVarValue(String varName, Object val);

	public abstract void saveAllVarValuesIn(Map<String, Object> varsToSave);

	public abstract void removeAllVars();

	public abstract void addVarWithValue(String varName, Object val);

	/**
	 * Access to arguments (of actions)
	 * 
	 */

	public abstract Object getArg(String string, int type) throws GamaRuntimeException;

	public abstract Integer getIntArg(String string) throws GamaRuntimeException;

	public abstract Double getFloatArg(String string) throws GamaRuntimeException;

	public abstract IList getListArg(String string) throws GamaRuntimeException;

	public abstract String getStringArg(String string) throws GamaRuntimeException;

	public abstract Boolean getBoolArg(String string) throws GamaRuntimeException;

	public abstract boolean hasArg(String string);

	public IType getType(final String name);

	public abstract IModel getModel();

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

	public abstract ExecutionResult init(final IStepable agent);

	public abstract ExecutionResult step(final IStepable agent);

	/**
	 * @param actualArgs
	 */
	public abstract void stackArguments(Arguments actualArgs);

	/**
	 * @param gamlAgent
	 */
	public abstract ExecutionResult update(IAgent agent);

	/**
	 * @return the current statement or null if none
	 */

}
