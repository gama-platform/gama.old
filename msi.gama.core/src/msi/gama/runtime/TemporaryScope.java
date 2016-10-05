/**
 * Created by drogoul, 5 déc. 2015
 *
 */
package msi.gama.runtime;

import java.util.HashMap;
import java.util.Map;

import msi.gama.common.interfaces.IGraphics;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IKeyword;
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
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.IExecutable;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Class TemporaryScope.
 *
 * @author drogoul
 * @since 5 déc. 2015
 *
 */
class TemporaryScope implements IScope {

	Map<String, Object> vars = new HashMap<>();

	/**
	 *
	 */
	public TemporaryScope() {
	}

	/**
	 * Method clear()
	 * 
	 * @see msi.gama.runtime.IScope#clear()
	 */
	@Override
	public void clear() {
		vars.clear();
		random = null;
	}

	/**
	 * Method push()
	 * 
	 * @see msi.gama.runtime.IScope#push(msi.gama.metamodel.agent.IAgent) No
	 *      agents
	 */
	@Override
	public boolean push(final IAgent agent) {
		return false;
	}

	/**
	 * Method push()
	 * 
	 * @see msi.gama.runtime.IScope#push(msi.gaml.statements.IStatement) No
	 *      statements
	 */
	@Override
	public void push(final ISymbol statement) {
	}

	/**
	 * Method pop()
	 * 
	 * @see msi.gama.runtime.IScope#pop(msi.gama.metamodel.agent.IAgent) No
	 *      agents
	 */
	@Override
	public void pop(final IAgent agent) {
	}

	/**
	 * Method pop()
	 * 
	 * @see msi.gama.runtime.IScope#pop(msi.gaml.statements.IStatement) No
	 *      statements
	 */
	@Override
	public void pop(final ISymbol statement) {
	}

	/**
	 * Method execute()
	 * 
	 * @see msi.gama.runtime.IScope#execute(msi.gaml.statements.IExecutable,
	 *      msi.gama.metamodel.agent.IAgent, msi.gaml.statements.Arguments,
	 *      java.lang.Object[]) Impossible to execute anything here
	 */
	@Override
	public boolean execute(final IExecutable executable, final IAgent agent, final Arguments args,
			final Object[] result) {
		return false;
	}

	/**
	 * Method evaluate()
	 * 
	 * @see msi.gama.runtime.IScope#evaluate(msi.gaml.expressions.IExpression,
	 *      msi.gama.metamodel.agent.IAgent)
	 */
	@Override
	public Object evaluate(final IExpression expr, final IAgent agent) throws GamaRuntimeException {
		try {
			return expr.value(this);
		} catch (final GamaRuntimeException g) {
			GAMA.reportAndThrowIfNeeded(this, g, true);
			return null;
		}

	}

	/**
	 * Method getVarValue()
	 * 
	 * @see msi.gama.runtime.IScope#getVarValue(java.lang.String)
	 */
	@Override
	public Object getVarValue(final String varName) {
		return vars.get(varName);
	}

	/**
	 * Method setVarValue()
	 * 
	 * @see msi.gama.runtime.IScope#setVarValue(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void setVarValue(final String varName, final Object val) {
		vars.put(varName, val);
	}

	/**
	 * Method saveAllVarValuesIn()
	 * 
	 * @see msi.gama.runtime.IScope#saveAllVarValuesIn(java.util.Map)
	 */
	@Override
	public void saveAllVarValuesIn(final Map<String, Object> varsToSave) {
		varsToSave.putAll(vars);
	}

	/**
	 * Method removeAllVars()
	 * 
	 * @see msi.gama.runtime.IScope#removeAllVars()
	 */
	@Override
	public void removeAllVars() {
		vars.clear();
	}

	/**
	 * Method addVarWithValue()
	 * 
	 * @see msi.gama.runtime.IScope#addVarWithValue(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void addVarWithValue(final String varName, final Object val) {
		vars.put(varName, val);
	}

	/**
	 * Method setEach()
	 * 
	 * @see msi.gama.runtime.IScope#setEach(java.lang.Object)
	 */
	@Override
	public void setEach(final Object value) {
		vars.put(IKeyword.EACH, value);
	}

	/**
	 * Method getEach()
	 * 
	 * @see msi.gama.runtime.IScope#getEach()
	 */
	@Override
	public Object getEach() {
		return vars.get(IKeyword.EACH);
	}

	/**
	 * Method getArg()
	 * 
	 * @see msi.gama.runtime.IScope#getArg(java.lang.String, int)
	 */
	@Override
	public Object getArg(final String string, final int type) throws GamaRuntimeException {
		return Types.get(type).cast(this, vars.get(string), null, false);
	}

	/**
	 * Method getIntArg()
	 * 
	 * @see msi.gama.runtime.IScope#getIntArg(java.lang.String)
	 */
	@Override
	public Integer getIntArg(final String string) throws GamaRuntimeException {
		return (Integer) getArg(string, IType.INT);
	}

	/**
	 * Method getFloatArg()
	 * 
	 * @see msi.gama.runtime.IScope#getFloatArg(java.lang.String)
	 */
	@Override
	public Double getFloatArg(final String string) throws GamaRuntimeException {
		return (Double) getArg(string, IType.FLOAT);
	}

	/**
	 * Method getListArg()
	 * 
	 * @see msi.gama.runtime.IScope#getListArg(java.lang.String)
	 */
	@Override
	public IList<?> getListArg(final String string) throws GamaRuntimeException {
		return (IList<?>) getArg(string, IType.LIST);
	}

	/**
	 * Method getStringArg()
	 * 
	 * @see msi.gama.runtime.IScope#getStringArg(java.lang.String)
	 */
	@Override
	public String getStringArg(final String string) throws GamaRuntimeException {
		return (String) getArg(string, IType.STRING);
	}

	/**
	 * Method getBoolArg()
	 * 
	 * @see msi.gama.runtime.IScope#getBoolArg(java.lang.String)
	 */
	@Override
	public Boolean getBoolArg(final String string) throws GamaRuntimeException {
		return (Boolean) getArg(string, IType.BOOL);
	}

	/**
	 * Method hasArg()
	 * 
	 * @see msi.gama.runtime.IScope#hasArg(java.lang.String)
	 */
	@Override
	public boolean hasArg(final String string) {
		return vars.containsKey(string);
	}

	/**
	 * Method hasVar()
	 * 
	 * @see msi.gama.runtime.IScope#hasVar(java.lang.String)
	 */
	@Override
	public boolean hasVar(final String string) {
		return vars.containsKey(string);
	}

	/**
	 * Method getAgentVarValue()
	 * 
	 * @see msi.gama.runtime.IScope#getAgentVarValue(msi.gama.metamodel.agent.IAgent,
	 *      java.lang.String)
	 */
	@Override
	public Object getAgentVarValue(final IAgent agent, final String name) throws GamaRuntimeException {
		return vars.get(name);
	}

	/**
	 * Method setAgentVarValue()
	 * 
	 * @see msi.gama.runtime.IScope#setAgentVarValue(msi.gama.metamodel.agent.IAgent,
	 *      java.lang.String, java.lang.Object)
	 */
	@Override
	public void setAgentVarValue(final IAgent agent, final String name, final Object v) throws GamaRuntimeException {
		vars.put(name, v);
	}

	/**
	 * Method interrupted()
	 * 
	 * @see msi.gama.runtime.IScope#interrupted() No interruption
	 */
	@Override
	public boolean interrupted() {
		return false;
	}

	/**
	 * Method setInterrupted()
	 * 
	 * @see msi.gama.runtime.IScope#setInterrupted(boolean) No interruption
	 */
	@Override
	public void setInterrupted() {
	}

	/**
	 * Method getGlobalVarValue()
	 * 
	 * @see msi.gama.runtime.IScope#getGlobalVarValue(java.lang.String)
	 */
	@Override
	public Object getGlobalVarValue(final String name) throws GamaRuntimeException {
		return null;
	}

	/**
	 * Method setGlobalVarValue()
	 * 
	 * @see msi.gama.runtime.IScope#setGlobalVarValue(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public void setGlobalVarValue(final String name, final Object v) throws GamaRuntimeException {
	}

	/**
	 * Method getName()
	 * 
	 * @see msi.gama.runtime.IScope#getName()
	 */
	@Override
	public Object getName() {
		return "Temporary scope";
	}

	/**
	 * Method getTopology()
	 * 
	 * @see msi.gama.runtime.IScope#getTopology() No Topology
	 */
	@Override
	public ITopology getTopology() {
		return null;
	}

	/**
	 * Method setTopology()
	 * 
	 * @see msi.gama.runtime.IScope#setTopology(msi.gama.metamodel.topology.ITopology)
	 *      No Topology to set
	 */
	@Override
	public ITopology setTopology(final ITopology topology) {
		return null;
	}

	/**
	 * Method setGraphics()
	 * 
	 * @see msi.gama.runtime.IScope#setGraphics(msi.gama.common.interfaces.IGraphics)
	 *      No Graphics to set
	 */
	@Override
	public void setGraphics(final IGraphics val) {
	}

	/**
	 * Method getGraphics()
	 * 
	 * @see msi.gama.runtime.IScope#getGraphics() No Graphics
	 */
	@Override
	public IGraphics getGraphics() {
		return null;
	}

	/**
	 * Method getAgentScope()
	 * 
	 * @see msi.gama.runtime.IScope#getAgent() No agent there
	 */
	@Override
	public IAgent getAgent() {
		return null;
	}

	/**
	 * Method getSimulationScope()
	 * 
	 * @see msi.gama.runtime.IScope#getSimulation() No simulation here
	 */
	@Override
	public SimulationAgent getSimulation() {
		return null;
	}

	/**
	 * Method getExperiment()
	 * 
	 * @see msi.gama.runtime.IScope#getExperiment() No Experiment
	 */
	@Override
	public IExperimentAgent getExperiment() {
		return null;
	}

	/**
	 * Method getExperimentContext()
	 * 
	 * @see msi.gama.runtime.IScope#getExperimentContext() No Experiment
	 */
	@Override
	public IDescription getExperimentContext() {
		return null;
	}

	/**
	 * Method getModelContext()
	 * 
	 * @see msi.gama.runtime.IScope#getModelContext() No Model
	 */
	@Override
	public IDescription getModelContext() {
		return null;
	}

	/**
	 * Method getModel()
	 * 
	 * @see msi.gama.runtime.IScope#getModel() No Model
	 */
	@Override
	public IModel getModel() {
		return null;
	}

	/**
	 * Method getClock()
	 * 
	 * @see msi.gama.runtime.IScope#getClock() Always null in this scope
	 */
	@Override
	public SimulationClock getClock() {
		return null;
	}

	/**
	 * Method copy()
	 * 
	 * @see msi.gama.runtime.IScope#copy()
	 */
	@Override
	public IScope copy(final String additionalName) {
		return new TemporaryScope();
	}

	/**
	 * Method popLoop()
	 * 
	 * @see msi.gama.runtime.IScope#popLoop() Nothing to do here
	 */
	@Override
	public void popLoop() {
	}

	/**
	 * Method popAction()
	 * 
	 * @see msi.gama.runtime.IScope#popAction() Nothing to do here
	 */
	@Override
	public void popAction() {
	}

	/**
	 * Method interruptAction()
	 * 
	 * @see msi.gama.runtime.IScope#interruptAction() Nothing to do here
	 */
	@Override
	public void interruptAction() {
	}

	/**
	 * Method interruptAgent()
	 * 
	 * @see msi.gama.runtime.IScope#interruptAgent() Nothing to do here
	 */
	@Override
	public void interruptAgent() {
	}

	/**
	 * Method interruptLoop()
	 * 
	 * @see msi.gama.runtime.IScope#interruptLoop() Nothing to do here
	 */
	@Override
	public void interruptLoop() {
	}

	/**
	 * Method init()
	 * 
	 * @see msi.gama.runtime.IScope#init(msi.gama.common.interfaces.IStepable)
	 *      Nothing to do here
	 */
	@Override
	public boolean init(final IStepable agent) {
		return false;
	}

	/**
	 * Method step()
	 * 
	 * @see msi.gama.runtime.IScope#step(msi.gama.common.interfaces.IStepable)
	 *      Nothing to do here
	 */
	@Override
	public boolean step(final IStepable agent) {
		return false;
	}

	/**
	 * Method stackArguments()
	 * 
	 * @see msi.gama.runtime.IScope#stackArguments(msi.gaml.statements.Arguments)
	 */
	@Override
	public void stackArguments(final Arguments actualArgs) {
	}

	/**
	 * Method update()
	 * 
	 * @see msi.gama.runtime.IScope#update(msi.gama.metamodel.agent.IAgent)
	 *      Nothing to do here
	 */
	@Override
	public boolean update(final IAgent agent) {
		return false;
	}

	/**
	 * Method getStatement()
	 * 
	 * @see msi.gama.runtime.IScope#getStatement()
	 */
	@Override
	public IStatement getCurrentSymbol() {
		return null;
	}

	/**
	 * Method setTrace()
	 * 
	 * @see msi.gama.runtime.IScope#setTrace(boolean) Nothing to do here
	 */
	@Override
	public void setTrace(final boolean trace) {
	}

	/**
	 * Method setStatement()
	 * 
	 * @see msi.gama.runtime.IScope#setStatement(msi.gaml.statements.IStatement)
	 *      Nothing to do here
	 */
	@Override
	public void setCurrentSymbol(final ISymbol abstractStatement) {
	}

	RandomUtils random = null;

	/**
	 * Method getRandom()
	 * 
	 * @see msi.gama.runtime.IScope#getRandom()
	 */
	@Override
	public RandomUtils getRandom() {
		if (random == null) {
			random = new RandomUtils();
		}
		return random;
	}

	boolean reportErrors = true;

	/**
	 * Method disableErrorReporting()
	 * 
	 * @see msi.gama.runtime.IScope#disableErrorReporting()
	 */
	@Override
	public void disableErrorReporting() {
		reportErrors = false;
	}

	/**
	 * Method enableErrorReporting()
	 * 
	 * @see msi.gama.runtime.IScope#enableErrorReporting()
	 */
	@Override
	public void enableErrorReporting() {
		reportErrors = true;
	}

	/**
	 * Method reportErrors()
	 * 
	 * @see msi.gama.runtime.IScope#reportErrors()
	 */
	@Override
	public boolean reportErrors() {
		return reportErrors;
	}

	/**
	 * Method getAgentsStack()
	 * 
	 * @see msi.gama.runtime.IScope#getAgentsStack()
	 */
	@Override
	public IAgent[] getAgentsStack() {
		return new IAgent[0];
	}

	/**
	 * Method pushReadAttributes()
	 * 
	 * @see msi.gama.runtime.IScope#pushReadAttributes(java.util.Map)
	 */
	@Override
	public void pushReadAttributes(final Map values) {

	}

	/**
	 * Method popReadAttributes()
	 * 
	 * @see msi.gama.runtime.IScope#popReadAttributes()
	 */
	@Override
	public Map popReadAttributes() {
		return null;
	}

	/**
	 * Method peekReadAttributes()
	 * 
	 * @see msi.gama.runtime.IScope#peekReadAttributes()
	 */
	@Override
	public Map peekReadAttributes() {
		return null;
	}

	/**
	 * Method getGui()
	 * 
	 * @see msi.gama.runtime.IScope#getGui()
	 */
	@Override
	public IGui getGui() {
		return GAMA.getGui();
	}

	/**
	 * Method getRoot()
	 * 
	 * @see msi.gama.runtime.IScope#getRoot()
	 */
	@Override
	public ITopLevelAgent getRoot() {
		return null;
	}

	@Override
	public void setOnUserHold(final boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isOnUserHold() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPaused() {
		// TODO Auto-generated method stub
		return false;
	}

}
