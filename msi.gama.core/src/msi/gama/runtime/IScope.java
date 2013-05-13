/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.runtime;

import java.util.Map;
import msi.gama.common.interfaces.IGraphics;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationClock;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.*;

/**
 * Written by drogoul Modified on 18 janv. 2011
 * 
 * @todo Description
 * 
 */
public interface IScope {

	public abstract void clear();

	/**
	 * Make the agent the current agent in the scope
	 * @param agent
	 * @see getAgentScope()
	 */
	public abstract void push(IAgent agent);

	/**
	 * Makes the statement the current statement in the scope
	 * @param statement
	 */

	public abstract void push(IStatement statement);

	/**
	 * Removes the current agent
	 * @param agent
	 */
	public abstract void pop(IAgent agent);

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
	public abstract Object execute(IStatement statement, IAgent agent) throws GamaRuntimeException;

	public abstract Object execute(IStatement.WithArgs statement, IAgent agent, Arguments args)
		throws GamaRuntimeException;

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

	public abstract Object getAgentVarValue(String name) throws GamaRuntimeException;

	public abstract void setAgentVarValue(String name, Object v) throws GamaRuntimeException;

	public abstract void setAgentVarValue(IAgent agent, String name, Object v) throws GamaRuntimeException;

	public abstract void setStatus(ExecutionStatus status);

	public abstract ExecutionStatus getStatus();

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
	 * Return the current world of the simulation in which this scope is defined
	 * @return
	 */
	// public abstract WorldAgent getWorldScope();

	/**
	 * Returns the current simulation in which this scope is defined.
	 * @return the current simulation or null if none is defined (unlikely as the scope is created
	 *         by a simulation)
	 */

	public abstract IAgent getSimulationScope();

	public abstract IModel getModel();

	public abstract SimulationClock getClock();

	public abstract IScope copy();
}
