/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.kernel.stack;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.ExecutionStatus;

/**
 * Written by drogoul Modified on 18 juin 2011
 * 
 * @todo Description
 * 
 */
public abstract class AbstractStack implements IScope {

	// TODO AJOUTER DES EXCEPTIONS PARTOUT

	private static int	MAX				= 200;
	private static int	MAX_VARS		= MAX;
	private static int	MAX_COMMMANDS	= MAX / 2;
	private static int	MAX_AGENTS		= MAX / 5;

	private static class Record {

		String	name;
		Object	value;

		Record(final String s, final Object v) {
			name = s;
			value = v;
		}

		@Override
		public String toString() {
			return "{" + name + " : " + value + "}";
		}
	}

	private final Record[]		vars				= new Record[MAX_VARS];
	private final int[]			commandsPointers	= new int[MAX_COMMMANDS];
	protected final IAgent[]	agentsStack			= new IAgent[MAX_AGENTS];
	private Object				each				= null;
	private int					commandsPointer		= 0;
	private int					varsPointer			= 0;
	protected int				agentsPointer		= 0;
	private Object				context;
	private ExecutionStatus		currentStatus;

	{
		for ( int i = 0; i < vars.length; i++ ) {
			vars[i] = new Record(null, null);
		}
	}

	// private final HashMap<String, Integer> varIndex = new HashMap();

	public AbstractStack() {

	}

	@Override
	public abstract void setGlobalVarValue(final String name, final Object v)
		throws GamaRuntimeException;

	@Override
	public abstract Object getGlobalVarValue(final String name) throws GamaRuntimeException;

	@Override
	public void setAgentVarValue(final String name, final Object v) throws GamaRuntimeException {
		if ( agentsPointer == 0 ) {
			// setGlobalVarValue(name, v);
			return;
		}
		IAgent agent = agentsStack[agentsPointer - 1];
		if ( agent.dead() ) { return; }
		agent.setDirectVarValue(this, name, v); // ??
	}

	@Override
	public Object getAgentVarValue(final String name) throws GamaRuntimeException {
		if ( agentsPointer == 0 ) { return getGlobalVarValue(name); } // ?
		for ( int i = agentsPointer; i > 0; i-- ) {
			IAgent agent = agentsStack[i - 1];

			if ( !agent.dead() ) { return agent.getDirectVarValue(name); // What if agent doesn't
																			// have var?
			}

		}
		// Error ?
		return null;
	}

	@Override
	public abstract IAgent getWorldScope();

	@Override
	public abstract ISimulation getSimulationScope();

	@Override
	public final Object getVarValue(final String varName) {
		return vars[getVarIndex(varName)].value;
	}

	private final int getVarIndex(final String varName) {
		for ( int i = varsPointer - 1; i >= 0; i-- ) {
			if ( vars[i].name.equals(varName) ) { return i; }
		}
		return -1;
	}

	@Override
	public final Object getArg(final String varName) {
		int i = getVarIndex(varName); // Only in the local scope
		return i > -1 && i >= commandsPointers[commandsPointer - 1] ? vars[i].value : null;
	}

	@Override
	public final boolean hasArg(final String varName) {
		int i = getVarIndex(varName);// Only in the local scope
		return i > -1 && i >= commandsPointers[commandsPointer - 1];
	}

	@Override
	public final IAgent getAgentScope() {
		return agentsPointer == 0 ? getWorldScope() : agentsStack[agentsPointer - 1];
	}

	@Override
	public final void setVarValue(final String varName, final Object val) {
		vars[getVarIndex(varName)].value = val;
	}

	@Override
	public final void addVarWithValue(final String varName, final Object val) {
		Record r = vars[varsPointer++];
		r.name = varName;
		r.value = val;
	}

	@Override
	public final void setEach(final Object value) {
		each = value;
	}

	@Override
	public final Object getEach() {
		return each;
	}

	@Override
	public final void push(final IAgent agent) {
		agentsStack[agentsPointer++] = agent;
	}

	@Override
	public final void push(final ICommand command) {
		commandsPointers[commandsPointer++] = varsPointer;
	}

	@Override
	public final void pop(final IAgent agent) {
		agentsPointer--;
	}

	@Override
	public void pop(final ICommand command) {
		varsPointer = commandsPointers[--commandsPointer];
	}

	@Override
	public final void saveAllVarValuesIn(final Map<String, Object> varsToSave) {
		for ( int i = commandsPointers[commandsPointer - 1]; i < varsPointer; i++ ) {
			varsToSave.put(vars[i].name, vars[i].value);
		}
	}

	@Override
	public final void removeAllVars() {
		varsPointer = commandsPointers[commandsPointer - 1];
	}

	@Override
	public final Object execute(final ICommand command, final IAgent agent)
		throws GamaRuntimeException {
		Object result;
		push(agent);
		try {
			result = command.executeOn(this);
		} finally {
			pop(agent);
		}
		return result;
	}

	@Override
	public final Object evaluate(final IExpression expr, final IAgent agent)
		throws GamaRuntimeException {
		Object result;
		push(agent);
		try {
			result = expr.value(this);
		} finally {
			pop(agent);
		}
		return result;
	}

	@Override
	public Object getAgentVarValue(final IAgent agent, final String name)
		throws GamaRuntimeException {
		Object result = null;
		try {
			push(agent);
			result = getAgentVarValue(name);
		} finally {
			pop(agent);
		}
		return result;
	}

	@Override
	public void setAgentVarValue(final IAgent agent, final String name, final Object v)
		throws GamaRuntimeException {
		try {
			push(agent);
			setAgentVarValue(name, v);
		} finally {
			pop(agent);
		}
	}

	@Override
	public void setStatus(final ExecutionStatus status) {
		currentStatus = status;
	}

	@Override
	public final ExecutionStatus getStatus() {
		return currentStatus;
	}

	@Override
	public final String toString() {
		return Arrays.toString(vars);
	}

	@Override
	public final void setContext(final Object val) {
		context = val;
	}

	@Override
	public final Object getContext() {
		return context;
	}

}