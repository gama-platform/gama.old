/*********************************************************************************************
 *
 *
 * 'ExperimentPlan.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.kernel.experiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import com.google.common.collect.Iterables;

import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.batch.BatchOutput;
import msi.gama.kernel.batch.ExhaustiveSearch;
import msi.gama.kernel.batch.IExploration;
import msi.gama.kernel.experiment.ExperimentPlan.BatchValidator;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.outputs.ExperimentOutputManager;
import msi.gama.outputs.FileOutput;
import msi.gama.outputs.IOutputManager;
import msi.gama.outputs.SimulationOutputManager;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.ExecutionScope;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.Guava;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.species.GamlSpecies;
import msi.gaml.types.IType;
import msi.gaml.variables.IVariable;

/**
 * Written by drogoul Modified on 28 mai 2011 Apr. 2013: Important modifications
 * to enable running true experiment agents
 *
 * 
 * Dec 2015: ExperimentPlans now manage their own controller. They are entirely
 * responsible for its life-cycle (creation, disposal)
 * 
 * @todo Description
 *
 */
@symbol(name = { IKeyword.EXPERIMENT }, kind = ISymbolKind.EXPERIMENT, with_sequence = true, concept = {
		IConcept.EXPERIMENT })
@facets(value = {
		@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false, doc = @doc("identifier of the experiment")),
		@facet(name = IKeyword.TITLE, type = IType.LABEL, optional = false, doc = @doc(""), internal = true),
		@facet(name = IKeyword.PARENT, type = IType.ID, optional = true, doc = @doc("the parent experiment (in case of inheritance between experiments)")),
		@facet(name = IKeyword.SKILLS, type = IType.LIST, optional = true, doc = @doc(""), internal = true),
		@facet(name = IKeyword.CONTROL, type = IType.ID, optional = true, doc = @doc(""), internal = true),
		@facet(name = IKeyword.FREQUENCY, type = IType.INT, optional = true, internal = true, doc = @doc("the execution frequence of the experiment (default value: 1). If frequency: 10, the experiment is executed only each 10 steps.")),
		@facet(name = IKeyword.SCHEDULES, type = IType.CONTAINER, optional = true, internal = true, doc = @doc("an ordered list of agents giving the order of their execution")),
		@facet(name = IKeyword.KEEP_SEED, type = IType.BOOL, optional = true, doc = @doc("")),
		@facet(name = IKeyword.KEEP_SIMULATIONS, type = IType.BOOL, optional = true, doc = @doc("In the case of a batch experiment, specifies whether or not the simulations should be kept in memory for further analysis or immediately discarded with only their fitness kept in memory")),
		@facet(name = IKeyword.REPEAT, type = IType.INT, optional = true, doc = @doc("In the case of a batch experiment, expresses hom many times the simulations must be repeated")),
		@facet(name = IKeyword.UNTIL, type = IType.BOOL, optional = true, doc = @doc("In the case of a batch experiment, an expression that will be evaluated to know when a simulation should be terminated")),
		@facet(name = IKeyword.MULTICORE, type = IType.BOOL, optional = true, doc = @doc("Allows the experiment, when set to true, to use multiple threads to run its simulations")),
		@facet(name = IKeyword.TYPE, type = IType.LABEL, values = { IKeyword.BATCH, IKeyword.MEMORIZE,
				/* IKeyword.REMOTE, */IKeyword.GUI_,
				IKeyword.HEADLESS_UI }, optional = false, doc = @doc("the type of the experiment (either 'gui' or 'batch'")) }, omissible = IKeyword.NAME)
@inside(kinds = { ISymbolKind.MODEL })
@validator(BatchValidator.class)
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ExperimentPlan extends GamlSpecies implements IExperimentPlan {

	public static class BatchValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 * 
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription desc) {
			final String type = desc.getLitteral(IKeyword.TYPE);
			if (type.equals(IKeyword.MEMORIZE)) {
				desc.warning("The memorize experiment is still in development. It should not be used.",
						IGamlIssue.DEPRECATED);
			}
			if (!type.equals(IKeyword.BATCH)) {
				return;
			}
			if (!desc.hasFacet(IKeyword.UNTIL)) {
				desc.warning(
						"No stopping condition have been defined (facet 'until:'). This may result in an endless run of the simulations",
						IGamlIssue.MISSING_FACET);
			}
		}
	}

	protected IExperimentController controller;
	// An original copy of the simualtion outputs (which will be eventually
	// duplicated in all the simulations)
	protected IOutputManager originalSimulationOutputs;
	protected IOutputManager experimentOutputs;
	// private ItemList parametersEditors;
	protected final Map<String, IParameter> parameters = new TOrderedHashMap();
	protected final Map<String, IParameter.Batch> explorableParameters = new TOrderedHashMap();
	protected ExperimentAgent agent;
	protected final Scope scope = new Scope("in ExperimentPlan");
	protected IModel model;
	protected IExploration exploration;
	private FileOutput log;
	private boolean isHeadless;
	private final boolean isMulticore;
	private final boolean keepSeed;
	private final boolean keepSimulations;

	private final String experimentType;

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
		if (experimentType.equals(IKeyword.BATCH)) {
			exploration = new ExhaustiveSearch(null);
		} else if (experimentType.equals(IKeyword.HEADLESS_UI)) {
			setHeadless(true);
		}
		final IExpression coreExpr = getFacet(IKeyword.MULTICORE);
		isMulticore = (coreExpr == null ? GamaPreferences.MULTITHREADED_SIMULATIONS.getValue()
				: coreExpr.literalValue().equals(IKeyword.TRUE)) && !isHeadless();
		final IExpression expr = getFacet(IKeyword.KEEP_SEED);
		if (expr != null && expr.isConst())
			keepSeed = Cast.asBool(scope, expr.value(scope));
		else
			keepSeed = false;
		final IExpression ksExpr = getFacet(IKeyword.KEEP_SIMULATIONS);
		if (ksExpr != null && ksExpr.isConst())
			keepSimulations = Cast.asBool(scope, ksExpr.value(scope));
		else
			keepSimulations = true;

	}

	@Override
	public boolean isMulticore() {
		return isMulticore;
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
		// System.out.println("ExperimentPlan.dipose BEGIN");
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
		// System.out.println("ExperimentPlan.dipose END");
	}

	public void createAgent() {
		final ExperimentPopulation pop = new ExperimentPopulation(this);
		final IScope scope = getExperimentScope();
		pop.initializeFor(scope);
		agent = pop.createAgents(scope, 1, Collections.EMPTY_LIST, false, true).get(0);
		addDefaultParameters();
	}

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
					// scope.getGui().debug("from ExperimentPlan.setModel:");
					final IParameter p = new ExperimentParameter(scope, v);
					final String name = p.getName();
					final boolean already = parameters.containsKey(name);
					if (!already) {
						parameters.put(name, p);
					}
					// boolean registerParameter = !already;
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
		for (final ISymbol s : children) {
			if (s instanceof IExploration) {
				exploration = (IExploration) s;
			} else if (s instanceof BatchOutput) {
				fileOutputDescription = (BatchOutput) s;
			} else if (s instanceof SimulationOutputManager) {
				if (originalSimulationOutputs != null) {
					((SimulationOutputManager) originalSimulationOutputs).setChildren((SimulationOutputManager) s);
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
				final String name = p.getName();
				final boolean already = parameters.containsKey(name);
				if (!already) {
					parameters.put(name, p);
				}
			} else if (s instanceof ExperimentOutputManager) {
				if (experimentOutputs != null) {
					((ExperimentOutputManager) experimentOutputs).setChildren((ExperimentOutputManager) s);
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
		if (fileOutputDescription != null) {
			createOutput(fileOutputDescription);
		}

	}

	private void createOutput(final BatchOutput output) throws GamaRuntimeException {
		// TODO revoir tout ceci. Devrait plut�t �tre une commande
		if (output == null) {
			return;
		}
		IExpression data = output.getFacet(IKeyword.DATA);
		if (data == null) {
			data = exploration.getFitnessExpression();
		}
		final String dataString = data == null ? "time" : data.serialize(false);
		log = new FileOutput(output.getLiteral(IKeyword.TO), dataString, new ArrayList(parameters.keySet()), this);
	}

	@Override
	public void open() {
		createAgent();
		scope.getGui().prepareForExperiment(this);
		agent.schedule(agent.getScope());
		// agent.scheduleAndExecute(null);
		if (isBatch()) {
			agent.getScope().getGui().getStatus().informStatus(" Batch ready ");
			agent.getScope().getGui().updateExperimentState();
		}
	}

	@Override
	public void reload() {
		if (isBatch()) {
			agent.dispose();
			open();
		} else {
			agent.reset();
			agent.getScope().getGui().getConsole().eraseConsole(false);
			agent.init(agent.getScope());

			agent.getScope().getGui().updateParameterView(this);
		}
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
	public boolean isMemorize() {
		return IKeyword.MEMORIZE.equals(getExperimentType());
	}

	@Override
	public boolean isGui() {
		return true;
	}

	@Override
	public IScope getExperimentScope() {
		return scope;
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
	public Object getParameterValue(final String name) throws GamaRuntimeException {
		return checkGetParameter(name).value(scope);
		// VERIFY THE USAGE OF SCOPE HERE
	}

	@Override
	public boolean hasParameter(final String name) {
		return getParameter(name) != null;
	}

	public IParameter.Batch getParameterByTitle(final String title) {
		for (final IParameter p : parameters.values()) {
			if (p.getTitle().equals(title) && p instanceof IParameter.Batch)
				return (IParameter.Batch) p;
		}
		return null;
	}

	public IParameter.Batch getParameter(final String name) {
		final IParameter p = parameters.get(name);
		if (p != null && p instanceof IParameter.Batch) {
			return (IParameter.Batch) p;
		}
		return null;
	}

	public void addParameter(final IParameter p) {
		final String name = p.getName();
		final IParameter already = parameters.get(name);
		if (already != null) {
			p.setValue(scope, already.getInitialValue(scope));
		}
		parameters.put(name, p);
	}

	protected IParameter.Batch checkGetParameterByTitle(final String name) throws GamaRuntimeException {
		final IParameter.Batch v = getParameterByTitle(name);
		if (v == null) {
			throw GamaRuntimeException.error("No parameter named " + name + " in experiment " + getName(),
					getExperimentScope());
		}
		return v;
	}

	protected IParameter.Batch checkGetParameter(final String name) throws GamaRuntimeException {
		final IParameter.Batch v = getParameter(name);
		if (v == null) {
			throw GamaRuntimeException.error("No parameter named " + name + " in experiment " + getName(),
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
		if (agent == null) {
			return null;
		}
		return agent.getSimulation();
	}

	/**
	 * A short-circuited scope that represents the scope of the experiment plan,
	 * before any agent is defined. If a simulation is available, it refers to
	 * it and gains access to its global scope. If not, it throws the
	 * appropriate runtime exceptions when a feature dependent on the existence
	 * of a simulation is accessed
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
				GAMA.getGui().updateParameterView(ExperimentPlan.this);
				return;
			}
			final SimulationAgent a = getCurrentSimulation();
			if (a != null) {
				a.setDirectVarValue(this, name, v);
			}
		}

		@Override
		public Object getGlobalVarValue(final String name) throws GamaRuntimeException {
			if (hasParameter(name)) {
				return getParameterValue(name);
			}
			final SimulationAgent a = getCurrentSimulation();
			if (a != null) {
				return a.getDirectVarValue(this, name);
			}
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

	/**
	 * Same as the previous one, but forces the outputs to do one step of
	 * computation (if some values have changed)
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
	 * Returns the output managers that are currently active. If no agent is
	 * defined, then an empty iterable is returned
	 * 
	 * @return
	 */

	@Override
	public Iterable<IOutputManager> getActiveOutputManagers() {
		if (agent == null)
			return Collections.EMPTY_LIST;

		return Iterables.filter(
				Iterables.concat(getAgent().getAllSimulationOutputs(), Collections.singleton(experimentOutputs)),
				Guava.NOT_NULL);

	}

}
