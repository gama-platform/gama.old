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
import msi.gama.kernel.simulation.*;
import msi.gama.metamodel.agent.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.*;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 18 juin 2011
 * 
 * @todo Description
 * 
 */
public abstract class AbstractScope implements IScope {

	// TODO AJOUTER DES EXCEPTIONS PARTOUT

	private static int MAX = 200;
	private static int MAX_VARS = MAX;
	private static int MAX_COMMMANDS = MAX / 2;
	private static int MAX_AGENTS = MAX / 5;

	private static class Record {

		String name;
		Object value;

		Record(final String s, final Object v) {
			name = s;
			value = v;
		}

		@Override
		public String toString() {
			return "{" + name + " : " + value + "}";
		}
	}

	private final Record[] vars = new Record[MAX_VARS];
	private final int[] statementsPointers = new int[MAX_COMMMANDS];
	protected final IAgent[] agentsStack = new IAgent[MAX_AGENTS];
	private Object each = null;
	private int statementsPointer = 0;
	private int varsPointer = 0;
	protected int agentsPointer = 0;
	private IGraphics context;
	private ITopology topology;
	private ExecutionStatus currentStatus;
	private final String name;

	{
		for ( int i = 0; i < vars.length; i++ ) {
			vars[i] = new Record(null, null);
		}
	}

	// private final HashMap<String, Integer> varIndex = new HashMap();

	public AbstractScope(final String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public abstract void setGlobalVarValue(final String name, final Object v)
		throws GamaRuntimeException;

	@Override
	public abstract Object getGlobalVarValue(final String name) throws GamaRuntimeException;

	@Override
	public void setAgentVarValue(final String name, final Object v) throws GamaRuntimeException {
		if ( agentsPointer == 0 ) { return; }
		IAgent agent = agentsStack[agentsPointer - 1];
		if ( agent.dead() ) { return; }
		agent.setDirectVarValue(this, name, v); // ??
	}

	@Override
	public Object getAgentVarValue(final String name) throws GamaRuntimeException {
		if ( agentsPointer == 0 ) { return getGlobalVarValue(name); } // ?
		for ( int i = agentsPointer; i > 0; i-- ) {
			IAgent agent = agentsStack[i - 1];

			if ( !agent.dead() ) { return agent.getDirectVarValue(this, name); // What if agent
																				// doesn't
																				// have var?
			}

		}
		// Error ?
		return null;
	}

	@Override
	public abstract WorldAgent getWorldScope();

	@Override
	public abstract ISimulation getSimulationScope();

	@Override
	public abstract IModel getModel();

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

	@Override
	public final Object getArg(final String varName, final int type) throws GamaRuntimeException {
		int i = getVarIndex(varName); // Only in the local scope
		Object result =
			i > -1 && i >= statementsPointers[statementsPointer - 1] ? vars[i].value : null;
		return type == IType.NONE ? result : Types.get(type).cast(this, result, null);
	}

	@Override
	public final boolean hasArg(final String varName) {
		int i = getVarIndex(varName);// Only in the local scope
		return i > -1 && i >= statementsPointers[statementsPointer - 1];
	}

	@Override
	public final boolean hasVar(final String varName) {
		return getVarIndex(varName) != -1;
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
	public final void push(final IStatement statement) {
		statementsPointers[statementsPointer++] = varsPointer;
	}

	@Override
	public final void pop(final IAgent agent) {
		agentsPointer--;
	}

	@Override
	public void pop(final IStatement statement) {
		varsPointer = statementsPointers[--statementsPointer];
	}

	@Override
	public final void saveAllVarValuesIn(final Map<String, Object> varsToSave) {
		for ( int i = statementsPointers[statementsPointer - 1]; i < varsPointer; i++ ) {
			varsToSave.put(vars[i].name, vars[i].value);
		}
	}

	@Override
	public final void removeAllVars() {
		varsPointer = statementsPointers[statementsPointer - 1];
	}

	@Override
	public final void clear() {
		each = null;
		statementsPointer = 0;
		varsPointer = 0;
		agentsPointer = 0;
		context = null;
	}

	@Override
	public final Object execute(final IStatement statement, final IAgent agent)
		throws GamaRuntimeException {
		Object result;
		push(agent);
		try {
			result = statement.executeOn(this);
		} finally {
			pop(agent);
		}
		return result;
	}

	@Override
	public final Object execute(final IStatement.WithArgs statement, final IAgent agent,
		final Arguments args) throws GamaRuntimeException {
		Object result;
		push(agent);
		try {
			statement.setRuntimeArgs(args);
			result = statement.executeOn(this);
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
		return getName() + "; current agent: " + getAgentScope();
	}

	@Override
	public final void setGraphics(final IGraphics val) {
		context = val;
	}

	@Override
	public final IGraphics getGraphics() {
		return context;
	}

	@Override
	public final ITopology getTopology() {
		if ( topology != null ) { return topology; }
		IAgent agent = getAgentScope();
		if ( agent != null ) { return agent.getTopology(); }
		return null;
	}

	@Override
	public final ITopology setTopology(final ITopology topo) {
		ITopology previous = topology;
		topology = topo;
		return previous;
	}

	@Override
	public final SimulationClock getClock() {
		ISimulation sim = getSimulationScope();
		return sim == null ? null : sim.getScheduler().getClock();
	}

}