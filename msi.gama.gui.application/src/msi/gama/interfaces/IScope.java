/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC 
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.interfaces;

import java.util.Map;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.ExecutionStatus;

/**
 * Written by drogoul Modified on 18 janv. 2011
 * 
 * @todo Description
 * 
 */
public interface IScope {

	public abstract void push(IAgent agent);

	public abstract void push(ICommand command);

	public abstract void pop(IAgent agent);

	public abstract void pop(ICommand command);

	public abstract IAgent getAgentScope();

	public abstract IAgent getWorldScope();

	public abstract ISimulation getSimulationScope();

	public abstract Object execute(ICommand command, IAgent agent) throws GamaRuntimeException;

	public abstract Object evaluate(IExpression expr, IAgent agent) throws GamaRuntimeException;

	public abstract Object getVarValue(String varName);

	public abstract void setVarValue(String varName, Object val);

	public abstract void saveAllVarValuesIn(Map<String, Object> varsToSave);

	public abstract void removeAllVars();

	public abstract void addVarWithValue(String varName, Object val);

	public abstract void setEach(Object value);

	public abstract Object getEach();

	// Used to setup a "context" (i.e. a persistent object)
	// like the graphics context for Draw commands, for instance
	public abstract void setContext(Object val);

	public abstract Object getContext();

	public abstract Object getArg(String string);

	public abstract boolean hasArg(String string);

	public abstract Object getAgentVarValue(IAgent agent, String name) throws GamaRuntimeException;

	public abstract Object getAgentVarValue(String name) throws GamaRuntimeException;

	public abstract void setAgentVarValue(String name, Object v) throws GamaRuntimeException;

	public abstract void setAgentVarValue(IAgent agent, String name, Object v)
		throws GamaRuntimeException;

	public abstract void setStatus(ExecutionStatus status);

	public abstract ExecutionStatus getStatus();

	public abstract Object getGlobalVarValue(String name) throws GamaRuntimeException;

	public abstract void setGlobalVarValue(String name, Object v) throws GamaRuntimeException;

}
