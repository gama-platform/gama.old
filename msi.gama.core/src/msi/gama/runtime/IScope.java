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
import msi.gama.kernel.simulation.ISimulation;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.IStatement;

/**
 * Written by drogoul Modified on 18 janv. 2011
 * 
 * @todo Description
 * 
 */
public interface IScope {

	public abstract void clear();

	public abstract void push(IAgent agent);

	public abstract void push(IStatement statement);

	public abstract void pop(IAgent agent);

	public abstract void pop(IStatement statement);

	public abstract IAgent getAgentScope();

	public abstract IAgent getWorldScope();

	public abstract ISimulation getSimulationScope();

	public abstract Object execute(IStatement statement, IAgent agent) throws GamaRuntimeException;

	public abstract Object evaluate(IExpression expr, IAgent agent) throws GamaRuntimeException;

	public abstract Object getVarValue(String varName);

	public abstract void setVarValue(String varName, Object val);

	public abstract void saveAllVarValuesIn(Map<String, Object> varsToSave);

	public abstract void removeAllVars();

	public abstract void addVarWithValue(String varName, Object val);

	public abstract void setEach(Object value);

	public abstract Object getEach();

	// Used to setup a "context" (i.e. a persistent object)
	// like the graphics context for Draw statements, for instance
	public abstract void setContext(Object val);

	public abstract Object getContext();

	public abstract Object getArg(String string, short type) throws GamaRuntimeException;

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

	public abstract void setAgentVarValue(IAgent agent, String name, Object v)
		throws GamaRuntimeException;

	public abstract void setStatus(ExecutionStatus status);

	public abstract ExecutionStatus getStatus();

	public abstract Object getGlobalVarValue(String name) throws GamaRuntimeException;

	public abstract void setGlobalVarValue(String name, Object v) throws GamaRuntimeException;

	public abstract Object getName();

}
