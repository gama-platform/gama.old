/*******************************************************************************************************
 *
 * msi.gama.kernel.experiment.ExperimentPlan.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.experiment;

import static msi.gama.common.interfaces.IKeyword.TEST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Iterables;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.preferences.GamaPreferences;
import msi.gama.kernel.batch.BatchOutput;
import msi.gama.kernel.batch.ExhaustiveSearch;
import msi.gama.kernel.batch.IExploration;
import msi.gama.kernel.experiment.ExperimentPlan.BatchValidator;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.population.GamaPopulation;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.metamodel.topology.continuous.AmorphousTopology;
import msi.gama.outputs.ExperimentOutputManager;
import msi.gama.outputs.FileOutput;
import msi.gama.outputs.IOutputManager;
import msi.gama.outputs.LayoutStatement;
import msi.gama.outputs.SimulationOutputManager;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.ExecutionScope;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.compilation.kernel.GamaMetaModel;
import msi.gaml.descriptions.ExperimentDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.species.GamlSpecies;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;
import msi.gaml.variables.IVariable;

/**
 * Written by drogoul Modified on 28 mai 2011 Apr. 2013: Important modifications to enable running true experiment
 * agents
 *
 *
 * Dec 2015: ExperimentPlans now manage their own controller. They are entirely responsible for its life-cycle
 * (creation, disposal)
 *
 * @todo Description
 *
 */
@symbol (
		name = { IKeyword.EXPERIMENT },
		kind = ISymbolKind.EXPERIMENT,
		with_sequence = true,
		concept = { IConcept.EXPERIMENT })
@doc ("Declaration of a particular type of agent that can manage simulations. If the experiment directly imports a model using the 'model:' facet, this facet *must* be the first one after the name of the experiment")
@facets (
		value = { @facet (
				name = IKeyword.NAME,
				type = IType.LABEL,
				optional = false,
				doc = @doc ("identifier of the experiment")),
				@facet (
						name = IKeyword.TITLE,
						type = IType.LABEL,
						optional = false,
						doc = @doc (""),
						internal = true),
				@facet (
						name = IKeyword.BENCHMARK,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("If true, make GAMA record the number of invocations and running time of the statements and operators of the simulations launched in this experiment. The results are automatically saved in a csv file in a folder called 'benchmarks' when the experiment is closed"),
						internal = false),
				@facet (
						name = IKeyword.PARENT,
						type = IType.ID,
						optional = true,
						doc = @doc ("the parent experiment (in case of inheritance between experiments)")),
				@facet (
						name = IKeyword.SKILLS,
						type = IType.LIST,
						optional = true,
						doc = @doc (""),
						internal = true),
				@facet (
						name = IKeyword.CONTROL,
						type = IType.ID,
						optional = true,
						doc = @doc (""),
						internal = true),
				@facet (
						name = IKeyword.FREQUENCY,
						type = IType.INT,
						optional = true,
						internal = true,
						doc = @doc ("the execution frequence of the experiment (default value: 1). If frequency: 10, the experiment is executed only each 10 steps.")),
				@facet (
						name = IKeyword.SCHEDULES,
						type = IType.CONTAINER,
						of = IType.AGENT,
						optional = true,
						internal = true,
						doc = @doc ("A container of agents (a species, a dynamic list, or a combination of species and containers) , which represents which agents will be actually scheduled when the population is scheduled for execution. For instance, 'species a schedules: (10 among a)' will result in a population that schedules only 10 of its own agents every cycle. 'species b schedules: []' will prevent the agents of 'b' to be scheduled. Note that the scope of agents covered here can be larger than the population, which allows to build complex scheduling controls; for instance, defining 'global schedules: [] {...} species b schedules: []; species c schedules: b + world; ' allows to simulate a model where the agents of b are scheduled first, followed by the world, without even having to create an instance of c.")),
				@facet (
						name = IKeyword.KEEP_SEED,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("Allows to keep the same seed between simulations. Mainly useful for batch experiments")),
				@facet (
						name = IKeyword.KEEP_SIMULATIONS,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("In the case of a batch experiment, specifies whether or not the simulations should be kept in memory for further analysis or immediately discarded with only their fitness kept in memory")),
				@facet (
						name = IKeyword.REPEAT,
						type = IType.INT,
						optional = true,
						doc = @doc ("In the case of a batch experiment, expresses hom many times the simulations must be repeated")),
				@facet (
						name = IKeyword.UNTIL,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("In the case of a batch experiment, an expression that will be evaluated to know when a simulation should be terminated")),
				@facet (
						name = IKeyword.PARALLEL,
						type = { IType.BOOL, IType.INT },
						optional = true,
						doc = @doc ("When set to true, use multiple threads to run its simulations. Setting it to n will set the numbers of threads to use")),
				@facet (
						name = IKeyword.TYPE,
						type = IType.LABEL,
						values = { IKeyword.BATCH, IKeyword.MEMORIZE, /* IKeyword.REMOTE, */IKeyword.GUI_,
								IKeyword.TEST, IKeyword.HEADLESS_UI },
						optional = false,
						doc = @doc ("the type of the experiment (either 'gui' or 'batch'")),
				@facet (
						name = IKeyword.VIRTUAL,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("whether the experiment is virtual (cannot be instantiated, but only used as a parent, false by default)")),
				@facet (
						name = IKeyword.AUTORUN,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("whether this experiment should be run automatically when launched (false by default)")) },
		omissible = IKeyword.NAME)
@inside (
		kinds = { ISymbolKind.MODEL })
@validator (BatchValidator.class)
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class ExperimentPlan extends GamlSpecies implements IExperimentPlan {

	public static class BatchValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 *
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription desc) {
			final String type = desc.getLitteral(TYPE);
			// if (type.equals(MEMORIZE)) {
			// desc.warning("The memorize experiment is still in development. It should not be used.",
			// IGamlIssue.DEPRECATED);
			// }
			if (!type.equals(BATCH)) {
				if (desc.getChildWithKeyword(METHOD) != null) {
					desc.error(type + " experiments cannot define exploration methods", IGamlIssue.CONFLICTING_FACETS,
							METHOD);
				}
			}
			if (type.equals(BATCH)) {
				if (!desc.hasFacet(UNTIL)) {
					desc.warning(
							"No stopping condition have been defined (facet 'until:'). This may result in an endless run of the "
									+ type + " experiment",
							IGamlIssue.MISSING_FACET, desc.getUnderlyingElement(), UNTIL, "true");
				}
			}
		}
	}

	protected IExperimentController controller;
	// An original copy of the simualtion outputs (which will be eventually
	// duplicated in all the simulations)
	protected SimulationOutputManager originalSimulationOutputs;
	protected ExperimentOutputManager experimentOutputs;
	// private ItemList parametersEditors;
	protected final Map<String, IParameter> parameters = GamaMapFactory.create();
	protected final Map<String, IParameter.Batch> explorableParameters = GamaMapFactory.create();
	protected ExperimentAgent agent;
	protected final Scope myScope = new Scope("in ExperimentPlan");
	protected IModel model;
	protected IExploration exploration;
	private FileOutput log;
	private boolean isHeadless;
	private final boolean keepSeed;
	private final boolean keepSimulations;
	private final String experimentType;
	private final boolean autorun;
	private final boolean benchmarkable;

	public class ExperimentPopulation extends GamaPopulation<ExperimentAgent> {

		public ExperimentPopulation(final ISpecies expr) {
			super(null, expr);
		}

		@Override
		public IList<ExperimentAgent> createAgents(final IScope scope, final int number,
				final List<? extends Map<String, Object>> initialValues, final boolean isRestored,
				final boolean toBeScheduled) throws GamaRuntimeException {
			for (int i = 0; i < number; i++) {
				agent = GamaMetaModel.INSTANCE.createExperimentAgent(getExperimentType(), this, currentAgentIndex++);
				// agent.setIndex(currentAgentIndex++);
				add(agent);
				scope.push(agent);
				createVariables(scope, agent, initialValues.isEmpty() ? Collections.EMPTY_MAP : initialValues.get(i));
				scope.pop(agent);
			}
			return this;
		}

		public void createVariables(final IScope scope, final IAgent a, final Map<String, Object> inits)
				throws GamaRuntimeException {
			final Set<String> names = inits.keySet();
			for (final String s : orderedVarNames) {
				final IVariable var = getVar(s);
				var.initializeWith(scope, a, inits.get(s));
				names.remove(s);
			}
			for (final String s : names) {
				a.getScope().setAgentVarValue(a, s, inits.get(s));
			}

		}

		@Override
		protected boolean stepAgents(final IScope scope) {
			return scope.step(agent).passed();
		}

		@Override
		public ExperimentAgent getAgent(final IScope scope, final ILocation value) {
			return agent;
		}

		@Override
		public void computeTopology(final IScope scope) throws GamaRuntimeException {
			topology = new AmorphousTopology();
		}

	}

	@Override
	public boolean isHeadless() {
		return GAMA.isInHeadLessMode() || isHeadless;
	}

	@Override
	public void setHeadless(final boolean headless) {
		isHeadless = headless;
	}

	@Override
	public ExperimentAgent getAgent() {
		return agent;
	}

	public ExperimentPlan(final IDescription description) {
		super(description);
		setName(description.getName());
		experimentType = description.getLitteral(IKeyword.TYPE);
		// final String type = description.getFacets().getLabel(IKeyword.TYPE);
		if (experimentType.equals(IKeyword.BATCH) || experimentType.equals(IKeyword.TEST)) {
			exploration = new ExhaustiveSearch(null);
		} else if (experimentType.equals(IKeyword.HEADLESS_UI)) {
			setHeadless(true);
		}
		final IExpression expr = getFacet(IKeyword.KEEP_SEED);
		if (expr != null && expr.isConst()) {
			keepSeed = Cast.asBool(myScope, expr.value(myScope));
		} else {
			keepSeed = false;
		}
		final IExpression ksExpr = getFacet(IKeyword.KEEP_SIMULATIONS);
		if (ksExpr != null && ksExpr.isConst()) {
			keepSimulations = Cast.asBool(myScope, ksExpr.value(myScope));
		} else {
			keepSimulations = true;
		}
		final IExpression ar = getFacet(IKeyword.AUTORUN);
		if (ar == null) {
			autorun = GamaPreferences.Runtime.CORE_AUTO_RUN.getValue();
		} else {
			autorun = Cast.asBool(myScope, ar.value(myScope));
		}
		final IExpression bm = getFacet(IKeyword.BENCHMARK);
		benchmarkable = bm != null && Cast.asBool(myScope, bm.value(myScope));
	}

	@Override
	public boolean isAutorun() {
		return autorun;
	}

	@Override
	public boolean keepsSeed() {
		return keepSeed;
	}

	@Override
	public boolean keepsSimulations() {
		return keepSimulations;
	}

	@Override
	public void dispose() {
		// DEBUG.LOG("ExperimentPlan.dipose BEGIN");
		// Dec 2015 Addition
		if (controller != null) {
			controller.dispose();
		}
		if (agent != null) {
			agent.dispose();
			agent = null;
		}
		if (originalSimulationOutputs != null) {
			originalSimulationOutputs.dispose();
			originalSimulationOutputs = null;
		}
		if (experimentOutputs != null) {
			experimentOutputs.dispose();
			experimentOutputs = null;
		}
		parameters.clear();

		// FIXME Should be put somewhere around here, but probably not here
		// exactly.
		// ProjectionFactory.reset();

		super.dispose();
		// DEBUG.LOG("ExperimentPlan.dipose END");
	}

	public void createAgent(final Double seed) {
		final ExperimentPopulation pop = new ExperimentPopulation(this);
		final IScope scope = getExperimentScope();
		pop.initializeFor(scope);
		final List<Map<String, Object>> params =
				seed == null ? Collections.EMPTY_LIST : Arrays.asList(new HashMap<String, Object>() {
					{
						put(IKeyword.SEED, seed);
					}
				});
		agent = pop.createAgents(scope, 1, params, false, true).get(0);
		addDefaultParameters();
	}

	/*
	 * public void createAgent() { final ExperimentPopulation pop = new ExperimentPopulation(this); final IScope scope =
	 * getExperimentScope(); pop.initializeFor(scope); agent = (ExperimentAgent) pop.createAgents(scope, 1,
	 * Collections.EMPTY_LIST, false, true).get(0); addDefaultParameters(); }
	 */
	@Override
	public IModel getModel() {
		return model;
	}

	@Override
	public void setModel(final IModel model) {
		this.model = model;
		if (!isBatch()) {
			for (final IVariable v : model.getVars()) {
				if (v.isParameter()) {
					final IParameter p = new ExperimentParameter(myScope, v);
					final String parameterName = p.getName();
					final boolean already = parameters.containsKey(parameterName);
					if (!already) {
						parameters.put(parameterName, p);
					}
				}

			}
		}
	}

	protected void addDefaultParameters() {
		for (final IParameter.Batch p : agent.getDefaultParameters()) {
			addParameter(p);
		}
	}

	@Override
	public final IOutputManager getExperimentOutputs() {
		return experimentOutputs;
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> children) {
		super.setChildren(children);

		BatchOutput fileOutputDescription = null;
		LayoutStatement layout = null;
		for (final ISymbol s : children) {
			if (s instanceof LayoutStatement) {
				layout = (LayoutStatement) s;
			} else if (s instanceof IExploration) {
				exploration = (IExploration) s;
			} else if (s instanceof BatchOutput) {
				fileOutputDescription = (BatchOutput) s;
			} else if (s instanceof SimulationOutputManager) {
				if (originalSimulationOutputs != null) {
					originalSimulationOutputs.setChildren((SimulationOutputManager) s);
				} else {
					originalSimulationOutputs = (SimulationOutputManager) s;
				}
			} else if (s instanceof IParameter.Batch) {
				final IParameter.Batch pb = (IParameter.Batch) s;
				if (isBatch()) {
					if (pb.canBeExplored()) {
						pb.setEditable(false);
						addExplorableParameter(pb);
						continue;
					}
				}
				final IParameter p = (IParameter) s;
				final String parameterName = p.getName();
				final boolean already = parameters.containsKey(parameterName);
				if (!already) {
					parameters.put(parameterName, p);
				}
			} else if (s instanceof ExperimentOutputManager) {
				if (experimentOutputs != null) {
					experimentOutputs.setChildren((ExperimentOutputManager) s);
				} else {
					experimentOutputs = (ExperimentOutputManager) s;
				}
			}
		}
		if (originalSimulationOutputs == null) {
			originalSimulationOutputs = SimulationOutputManager.createEmpty();
		}
		if (experimentOutputs == null) {
			experimentOutputs = ExperimentOutputManager.createEmpty();
		}
		if (experimentOutputs.getLayout() == null) {
			if (layout != null) {
				experimentOutputs.setLayout(layout);
			} else if (originalSimulationOutputs.getLayout() != null) {
				experimentOutputs.setLayout(originalSimulationOutputs.getLayout());
			}
		}
		if (fileOutputDescription != null) {
			createOutput(fileOutputDescription);
		}

	}

	private void createOutput(final BatchOutput output) throws GamaRuntimeException {
		// TODO revoir tout ceci. Devrait plut�t �tre une commande
		if (output == null) { return; }
		IExpression data = output.getFacet(IKeyword.DATA);
		if (data == null) {
			data = exploration.getFitnessExpression();
		}
		final String dataString = data == null ? "time" : data.serialize(false);
		log = new FileOutput(output.getLiteral(IKeyword.TO), dataString, new ArrayList(parameters.keySet()), this);
	}

	public synchronized void open(final Double seed) {

		createAgent(seed);
		myScope.getGui().prepareForExperiment(myScope, this);
		agent.schedule(agent.getScope());
		if (isBatch()) {
			agent.getScope().getGui().getStatus(agent.getScope())
					.informStatus(isTest() ? "Tests ready. Click run to begin." : " Batch ready. Click run to begin.");
			agent.getScope().getGui().updateExperimentState(agent.getScope());
		}
	}

	@Override
	public synchronized void open() {
		open(null);
	}

	/*
	 * @Override public synchronized void open() {
	 *
	 * createAgent(); myScope.getGui().prepareForExperiment(myScope, this); agent.schedule(agent.getScope()); if
	 * (isBatch()) { agent.getScope().getGui().getStatus(agent.getScope()) .informStatus(isTest() ?
	 * "Tests ready. Click run to begin." : " Batch ready. Click run to begin.");
	 * agent.getScope().getGui().updateExperimentState(agent.getScope()); } }
	 */
	@Override
	public void reload() {
		// if (isBatch()) {
		agent.dispose();
		open();
		// } else {
		// agent.reset();
		// agent.getScope().getGui().getConsole(agent.getScope()).eraseConsole(false);
		// agent.init(agent.getScope());
		//
		// agent.getScope().getGui().updateParameterView(agent.getScope(), this);
		// }
	}

	@Override
	public boolean hasParametersOrUserCommands() {
		return !parameters.isEmpty() || !explorableParameters.isEmpty() || !getUserCommands().isEmpty();
	}

	// @Override
	@Override
	public boolean isBatch() {
		return exploration != null;
	}

	@Override
	public boolean isTest() {
		return TEST.equals(getExperimentType());
	}

	@Override
	public boolean isMemorize() {
		return IKeyword.MEMORIZE.equals(getExperimentType());
	}

	@Override
	public boolean isGui() {
		return true;
	}

	@Override
	public IScope getExperimentScope() {
		return myScope;
	}

	// @Override
	public void setParameterValue(final IScope scope, final String name, final Object val) throws GamaRuntimeException {
		checkGetParameter(name).setValue(scope, val);
	}

	public void setParameterValueByTitle(final IScope scope, final String name, final Object val)
			throws GamaRuntimeException {
		checkGetParameterByTitle(name).setValue(scope, val);
	}

	// @Override
	public Object getParameterValue(final String parameterName) throws GamaRuntimeException {
		return checkGetParameter(parameterName).value(myScope);
		// VERIFY THE USAGE OF SCOPE HERE
	}

	@Override
	public boolean hasParameter(final String parameterName) {
		return getParameter(parameterName) != null;
	}

	public IParameter.Batch getParameterByTitle(final String title) {
		for (final IParameter p : parameters.values()) {
			if (p.getTitle().equals(title) && p instanceof IParameter.Batch) { return (IParameter.Batch) p; }
		}
		return null;
	}

	public IParameter.Batch getParameter(final String parameterName) {
		final IParameter p = parameters.get(parameterName);
		if (p instanceof IParameter.Batch) { return (IParameter.Batch) p; }
		return null;
	}

	public void addParameter(final IParameter p) {
		final String parameterName = p.getName();
		final IParameter already = parameters.get(parameterName);
		if (already != null) {
			p.setValue(myScope, already.getInitialValue(myScope));
		}
		parameters.put(parameterName, p);
	}

	protected IParameter.Batch checkGetParameterByTitle(final String parameterName) throws GamaRuntimeException {
		final IParameter.Batch v = getParameterByTitle(parameterName);
		if (v == null) {
			throw GamaRuntimeException.error("No parameter named " + parameterName + " in experiment " + getName(),
					getExperimentScope());
		}
		return v;
	}

	protected IParameter.Batch checkGetParameter(final String parameterName) throws GamaRuntimeException {
		final IParameter.Batch v = getParameter(parameterName);
		if (v == null) {
			throw GamaRuntimeException.error("No parameter named " + parameterName + " in experiment " + getName(),
					getExperimentScope());
		}
		return v;
	}

	@Override
	public Map<String, IParameter> getParameters() {
		return parameters;
	}

	@Override
	public SimulationAgent getCurrentSimulation() {
		if (agent == null) { return null; }
		return agent.getSimulation();
	}

	/**
	 * A short-circuited scope that represents the scope of the experiment plan, before any agent is defined. If a
	 * simulation is available, it refers to it and gains access to its global scope. If not, it throws the appropriate
	 * runtime exceptions when a feature dependent on the existence of a simulation is accessed
	 *
	 * @author Alexis Drogoul
	 * @since November 2011
	 */
	private class Scope extends ExecutionScope {

		public Scope(final String additionalName) {
			super(null, additionalName);
		}

		@Override
		public void setGlobalVarValue(final String name, final Object v) throws GamaRuntimeException {
			if (hasParameter(name)) {
				setParameterValue(this, name, v);
				GAMA.getGui().updateParameterView(getCurrentSimulation().getScope(), ExperimentPlan.this);
				return;
			}
			final SimulationAgent a = getCurrentSimulation();
			if (a != null) {
				a.setDirectVarValue(this, name, v);
			}
		}

		@Override
		public Object getGlobalVarValue(final String varName) throws GamaRuntimeException {
			if (hasParameter(varName)) { return getParameterValue(varName); }
			final SimulationAgent a = getCurrentSimulation();
			if (a != null) { return a.getDirectVarValue(this, varName); }
			return null;
		}

	}

	@Override
	public IExploration getExplorationAlgorithm() {
		return exploration;
	}

	@Override
	public FileOutput getLog() {
		return log;
	}

	public void addExplorableParameter(final IParameter.Batch p) {
		p.setCategory(EXPLORABLE_CATEGORY_NAME);
		p.setUnitLabel(null);
		explorableParameters.put(p.getName(), p);
	}

	@Override
	public Map<String, IParameter.Batch> getExplorableParameters() {
		return explorableParameters;
	}

	/**
	 * Method getController()
	 *
	 * @see msi.gama.kernel.experiment.IExperimentPlan#getController()
	 */
	@Override
	public IExperimentController getController() {
		if (controller == null) {
			controller = new ExperimentController(this);
		}
		return controller;
	}

	/**
	 * Method refreshAllOutputs()
	 *
	 * @see msi.gama.kernel.experiment.IExperimentPlan#refreshAllOutputs()
	 */
	@Override
	public void refreshAllOutputs() {
		for (final IOutputManager manager : getActiveOutputManagers()) {
			manager.forceUpdateOutputs();
		}
	}

	@Override
	public void pauseAllOutputs() {
		for (final IOutputManager manager : getActiveOutputManagers()) {
			manager.pause();
		}
	}

	@Override
	public void resumeAllOutputs() {
		for (final IOutputManager manager : getActiveOutputManagers()) {
			manager.resume();
		}
	}

	@Override
	public void synchronizeAllOutputs() {
		for (final IOutputManager manager : getActiveOutputManagers()) {
			manager.synchronize();
		}
	}

	@Override
	public void unSynchronizeAllOutputs() {
		for (final IOutputManager manager : getActiveOutputManagers()) {
			manager.unSynchronize();
		}
	}

	@Override
	public void closeAllOutputs() {
		for (final IOutputManager manager : getActiveOutputManagers()) {
			manager.close();
		}
	}

	/**
	 * Same as the previous one, but forces the outputs to do one step of computation (if some values have changed)
	 */
	@Override
	public void recomputeAndRefreshAllOutputs() {
		for (final IOutputManager manager : getActiveOutputManagers()) {
			manager.step(getExperimentScope());
		}
	}

	/**
	 * Method getOriginalSimulationOutputs()
	 *
	 * @see msi.gama.kernel.experiment.IExperimentPlan#getOriginalSimulationOutputs()
	 */
	@Override
	public IOutputManager getOriginalSimulationOutputs() {
		return originalSimulationOutputs;
	}

	@Override
	public String getExperimentType() {
		return experimentType;
	}

	/**
	 * Returns the output managers that are currently active. If no agent is defined, then an empty iterable is returned
	 *
	 * @return
	 */

	@Override
	public Iterable<IOutputManager> getActiveOutputManagers() {
		if (agent == null) { return Collections.EMPTY_LIST; }
		return Iterables.concat(agent.getAllSimulationOutputs(), Arrays.asList(experimentOutputs));

	}

	@Override
	public ExperimentDescription getDescription() {
		return (ExperimentDescription) super.getDescription();
	}

	@Override
	public boolean shouldBeBenchmarked() {
		return benchmarkable;
	}

}
