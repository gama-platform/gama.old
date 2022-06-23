/*******************************************************************************************************
 *
 * Experiment.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.core;

import java.util.Map;

import msi.gama.headless.job.ManualExperimentJob;
import msi.gama.kernel.experiment.ExperimentPlan;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.outputs.AbstractOutputManager;
import msi.gama.outputs.IOutput;
import msi.gama.outputs.MonitorOutput;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IMap;
import msi.gama.util.file.json.GamaJsonList;
import msi.gaml.compilation.GAML;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.Types;

/**
 * The Class Experiment.
 */
public class Experiment implements IExperiment {

	/** The Constant DEFAULT_SEED_VALUE. */
	public static final double DEFAULT_SEED_VALUE = 0;

	/** The current experiment. */
	protected IExperimentPlan currentExperiment;

	/** The current simulation. */
	protected SimulationAgent currentSimulation;

	/** The params. */
	protected ParametersSet params;

	/** The model. */
	protected IModel model;

	/** The experiment name. */
	protected String experimentName;

	/** The seed. */
	protected double seed;

	/** The current step. */
	protected long currentStep;

	/**
	 * Instantiates a new experiment.
	 */
	protected Experiment() {
		currentExperiment = null;
		params = new ParametersSet();
		model = null;
		experimentName = null;
		seed = DEFAULT_SEED_VALUE;
	}

	/**
	 * Instantiates a new experiment.
	 *
	 * @param mdl
	 *            the mdl
	 */
	public Experiment(final IModel mdl) {
		this();
		this.model = mdl;
	}

	@Override
	public SimulationAgent getSimulation() { return currentSimulation; }

	/**
	 * Gets the scope.
	 *
	 * @return the scope
	 */
	protected IScope getScope() { return this.currentExperiment.getCurrentSimulation().getScope(); }

	@Override
	public void setup(final String expName) {
		this.seed = 0;
		this.loadCurrentExperiment(expName);
	}

	@Override
	public void setup(final String expName, final double sd) {
		this.seed = sd;
		this.loadCurrentExperiment(expName);
	}

	@Override
	public void setup(final String expName, final double sd, GamaJsonList params,  ManualExperimentJob ec) {
		this.seed = sd;
		this.loadCurrentExperiment(expName,params,ec);
	}

	/**
	 * Load current experiment.
	 *
	 * @param expName
	 *            the exp name
	 */
	private synchronized void loadCurrentExperiment(final String expName, GamaJsonList p,  ManualExperimentJob ec) {
		this.experimentName = expName;
		this.currentStep = 0;
		
		final ExperimentPlan curExperiment = (ExperimentPlan) model.getExperiment(expName);
		curExperiment.setHeadless(true);
		curExperiment.setController(ec);

		if(p!=null) {				
			for(var O:((GamaJsonList)p).listValue(null, Types.MAP, false)) {
				IMap<String, Object> m=(IMap<String, Object>)O;
 				curExperiment.setParameterValueByTitle(curExperiment.getExperimentScope(), m.get("name").toString(),m.get("value"));
			}
		} 
		curExperiment.open(seed);
		if(!GAMA.getControllers().contains(curExperiment.getController())) {
			GAMA.getControllers().add(curExperiment.getController());
		}
		this.currentExperiment = curExperiment;
 		this.currentSimulation = this.currentExperiment.getAgent().getSimulation();
		this.currentExperiment.setHeadless(true);
	}
	
	/**
	 * Load current experiment.
	 *
	 * @param expName
	 *            the exp name
	 */
	private synchronized void loadCurrentExperiment(final String expName) {
		this.experimentName = expName;
		this.currentStep = 0;
		this.currentExperiment = GAMA.addHeadlessExperiment(model, experimentName, this.params, seed);
		this.currentSimulation = this.currentExperiment.getAgent().getSimulation();
		this.currentExperiment.setHeadless(true);
	}

	@Override
	public long step() {
		if (currentExperiment.isBatch()) {
			// Currently, the batch have the own way to control their
			// simulations so we call the experiment to do step instead of
			// demand simulation
			// MUST BE RE-ORGANIZE [ THE MULTI-SIMULATION + HEADLESS SIMULATION
			// + BATCH SIMULATION ]
			// AD commented this
			// currentExperiment.getAgent().getSimulation().removeAgent();
			// currentExperiment.getController().getScheduler().resume();
			currentExperiment.getAgent().step(currentExperiment.getAgent().getScope());
		} else {
			currentExperiment.getAgent().step(this.getScope());
			// currentExperiment.getAgent().getSimulation().step(this.getScope());
		}
		return currentStep++;

	}

	@Override
	public void setParameter(final String parameterName, final Object value) {
		// if (this.params.containsKey(parameterName)) { this.params.remove(parameterName); }
		this.params.put(parameterName, value);
	}

	@Override
	public Object getOutput(final String parameterName) {
		final IOutput output =
				((AbstractOutputManager) currentSimulation.getOutputManager()).getOutputWithOriginalName(parameterName);
		// System.out.
		if (output == null)
			throw GamaRuntimeException.error("Output does not exist: " + parameterName, currentSimulation.getScope());
		if (!(output instanceof MonitorOutput)) throw GamaRuntimeException
				.error("Output " + parameterName + " is not an alphanumeric data.", currentSimulation.getScope());
		output.update();
		return ((MonitorOutput) output).getLastValue();
	}

	@Override
	public Object getVariableOutput(final String parameterName) {
		// this.currentExperiment.getSimulationOutputs().step(this.getScope());
		final Object res =
				this.currentExperiment.getCurrentSimulation().getDirectVarValue(this.getScope(), parameterName);
		if (res == null)
			throw GamaRuntimeException.error("Output unresolved: " + parameterName, currentSimulation.getScope());
		return res;
	}

	@Override
	public void dispose() {
		GAMA.closeExperiment(currentExperiment);
	}

	@Override
	public boolean isInterrupted() {
		final SimulationAgent sim = currentExperiment.getCurrentSimulation();
		if (currentExperiment.isBatch() && sim == null) return false;
		return sim == null || sim.dead() || sim.getScope().interrupted();
	}

	@Override
	public IModel getModel() {
		// TODO Auto-generated method stub
		return this.model;
	}

	@Override
	public IExperimentPlan getExperimentPlan() { return this.currentExperiment; }

	@Override
	public IExpression compileExpression(final String expression) {
		return GAML.compileExpression(expression, this.getSimulation(), false);
	}

	@Override
	public Object evaluateExpression(final IExpression exp) {
		return exp.value(this.getSimulation().getScope());
	}

	@Override
	public Object evaluateExpression(final String exp) {
		final IExpression localExpression = compileExpression(exp);
		return evaluateExpression(localExpression);
	}

}
