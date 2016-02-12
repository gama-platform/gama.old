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

import java.util.*;
import msi.gama.common.GamaPreferences;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.RandomUtils;
import msi.gama.kernel.batch.*;
import msi.gama.kernel.experiment.ExperimentPlan.BatchValidator;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.outputs.*;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.species.GamlSpecies;
import msi.gaml.types.IType;
import msi.gaml.variables.IVariable;

/**
 * Written by drogoul Modified on 28 mai 2011
 * Apr. 2013: Important modifications to enable running true experiment agents
 *
 * Principe de base du batch :
 * - si batch, créer un agent "spécial" (BatchExperimentAgent ?)
 * - faire que ce soit lui qui gère tout ce qu'il y a dans BatchExperimentSpecies
 *
 * Dec 2015: ExperimentPlans now manage their own controller. They are entirely responsible for its life-cycle (creation, disposal)
 * @todo Description
 *
 */
@symbol(name = { IKeyword.EXPERIMENT }, kind = ISymbolKind.EXPERIMENT, with_sequence = true)
@facets(value = {
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false, doc = @doc("identifier of the experiment")),
	@facet(name = IKeyword.TITLE, type = IType.LABEL, optional = false, doc = @doc(""), internal = true),
	@facet(name = IKeyword.PARENT,
		type = IType.ID,
		optional = true,
		doc = @doc("the parent experiment (in case of inheritance between experiments)")),
	@facet(name = IKeyword.SKILLS, type = IType.LIST, optional = true, doc = @doc(""), internal = true),
	@facet(name = IKeyword.CONTROL, type = IType.ID, optional = true, doc = @doc(""), internal = true),
	@facet(name = IKeyword.FREQUENCY,
		type = IType.INT,
		optional = true,
		internal = true,
		doc = @doc("the execution frequence of the experiment (default value: 1). If frequency: 10, the experiment is executed only each 10 steps.")),
	@facet(name = IKeyword.SCHEDULES,
		type = IType.CONTAINER,
		optional = true,
		internal = true,
		doc = @doc("an ordered list of agents giving the order of their execution")),
	@facet(name = IKeyword.KEEP_SEED, type = IType.BOOL, optional = true, doc = @doc("")),
	@facet(name = IKeyword.REPEAT,
		type = IType.INT,
		optional = true,
		doc = @doc("In case of a batch experiment, expresses hom many times the simulations must be repeated")),
	@facet(name = IKeyword.UNTIL,
		type = IType.BOOL,
		optional = true,
		doc = @doc("In case of a batch experiment, an expression that will be evaluated to know when a simulation should be terminated")),
	@facet(name = IKeyword.MULTICORE,
		type = IType.BOOL,
		optional = true,
		doc = @doc("Allows the experiment, when set to true, to use multiple threads to run its simulations")),
	@facet(name = IKeyword.TYPE,
		type = IType.LABEL,
		values = { IKeyword.BATCH, /* IKeyword.REMOTE, */IKeyword.GUI_, IKeyword.HEADLESS_UI },
		optional = false,
		doc = @doc("the type of the experiment (either 'gui' or 'batch'")) },
	omissible = IKeyword.NAME)
@inside(kinds = { ISymbolKind.MODEL })
@validator(BatchValidator.class)
public class ExperimentPlan extends GamlSpecies implements IExperimentPlan {

	public static class BatchValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription desc) {
			String type = desc.getFacets().getLabel(IKeyword.TYPE);
			if ( !type.equals(IKeyword.BATCH) ) { return; }
			if ( !desc.getFacets().containsKey(IKeyword.UNTIL) ) {
				desc.warning(
					"No stopping condition have been defined (facet 'until:'). This may result in an endless run of the simulations",
					IGamlIssue.MISSING_FACET);
			}
		}
	}

	protected IExperimentController controller;
	// An original copy of the simualtion outputs (which will be eventually duplicated in all the simulations)
	protected IOutputManager originalSimulationOutputs;
	protected IOutputManager experimentOutputs;
	private ItemList parametersEditors;
	protected final Map<String, IParameter> parameters = new TOrderedHashMap();
	protected final Map<String, IParameter.Batch> explorableParameters = new TOrderedHashMap();
	protected ExperimentAgent agent;
	protected final Scope scope = new Scope();
	protected IModel model;
	protected IExploration exploration;
	private FileOutput log;
	private boolean isHeadless;
	private final boolean isMulticore;

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
		String type = description.getFacets().getLabel(IKeyword.TYPE);
		if ( type.equals(IKeyword.BATCH) ) {
			exploration = new ExhaustiveSearch(null);
		} else if ( type.equals(IKeyword.HEADLESS_UI) ) {
			setHeadless(true);
		}
		IExpression coreExpr = description.getFacets().getExpr(IKeyword.MULTICORE);
		isMulticore = (coreExpr == null ? GamaPreferences.MULTITHREADED_SIMULATIONS.getValue()
			: coreExpr.literalValue().equals(IKeyword.TRUE)) && !isHeadless();

	}

	@Override
	public boolean isMulticore() {
		return isMulticore;
	}

	@Override
	public void dispose() {
		// System.out.println("ExperimentPlan.dipose BEGIN");
		parametersEditors = null;
		// Dec 2015 Addition
		if ( controller != null ) {
			controller.dispose();
		}
		if ( agent != null ) {
			GAMA.releaseScope(agent.getScope());
			agent.dispose();
			agent = null;
		}
		if ( originalSimulationOutputs != null ) {
			originalSimulationOutputs.dispose();
			originalSimulationOutputs = null;
		}
		if ( experimentOutputs != null ) {
			experimentOutputs.dispose();
			experimentOutputs = null;
		}
		parameters.clear();

		// FIXME Should be put somewhere around here, but probably not here exactly.
		// ProjectionFactory.reset();

		super.dispose();
		// System.out.println("ExperimentPlan.dipose END");
	}

	public void createAgent() {
		final ExperimentPopulation pop = new ExperimentPopulation(this);
		final IScope scope = getExperimentScope();
		pop.initializeFor(scope);
		agent = (ExperimentAgent) pop.createAgents(scope, 1, Collections.EMPTY_LIST, false).get(0);
		addDefaultParameters();
	}

	@Override
	public IModel getModel() {
		return model;
	}

	@Override
	public void setModel(final IModel model) {
		this.model = model;
		if ( !isBatch() ) {
			for ( final IVariable v : model.getVars() ) {
				if ( v.isParameter() ) {
					// scope.getGui().debug("from ExperimentPlan.setModel:");
					IParameter p = new ExperimentParameter(scope, v);
					final String name = p.getName();
					boolean already = parameters.containsKey(name);
					if ( !already ) {
						parameters.put(name, p);
					}
					// boolean registerParameter = !already;
				}

			}
		}
	}

	protected void addDefaultParameters() {
		for ( IParameter.Batch p : agent.getDefaultParameters() ) {
			addParameter(p);
		}
	}

	@Override
	public final List<IOutputManager> getAllSimulationOutputs() {
		if ( getAgent() == null ) { return GamaListFactory.EMPTY_LIST; }
		return getAgent().getAllSimulationOutputs();
	}

	@Override
	public final IOutputManager getExperimentOutputs() {
		return experimentOutputs;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> children) {
		super.setChildren(children);
		// We first verify if we are in a batch -- or normal -- situation
		for ( final ISymbol s : children ) {
			if ( s instanceof IExploration /* && (s.hasFacet(IKeyword.MAXIMIZE) || s.hasFacet(IKeyword.MINIMIZE)) */ ) {
				exploration = (IExploration) s;
				break;
			}
		}
		if ( exploration != null ) {
			children.remove(exploration);
		}

		BatchOutput fileOutputDescription = null;
		for ( final ISymbol s : children ) {
			if ( s instanceof BatchOutput ) {
				fileOutputDescription = (BatchOutput) s;
			} else if ( s instanceof SimulationOutputManager ) {
				if ( originalSimulationOutputs != null ) {
					((SimulationOutputManager) originalSimulationOutputs)
						.setChildren(new ArrayList(((AbstractOutputManager) s).getOutputs().values()));
				} else {
					originalSimulationOutputs = (SimulationOutputManager) s;
				}
			} else if ( s instanceof IParameter.Batch ) {
				IParameter.Batch pb = (IParameter.Batch) s;
				if ( isBatch() ) {
					if ( pb.canBeExplored() ) {
						pb.setEditable(false);
						addExplorableParameter(pb);
						continue;
					}
				}
				IParameter p = (IParameter) s;
				final String name = p.getName();
				boolean already = parameters.containsKey(name);
				if ( !already ) {
					parameters.put(name, p);
				}
			} else if ( s instanceof ExperimentOutputManager ) {
				if ( experimentOutputs != null ) {
					((ExperimentOutputManager) experimentOutputs)
						.setChildren(new ArrayList(((AbstractOutputManager) s).getOutputs().values()));
				} else {
					experimentOutputs = (ExperimentOutputManager) s;
				}
			}
		}
		if ( originalSimulationOutputs == null ) {
			originalSimulationOutputs = new SimulationOutputManager(null);
		}
		if ( experimentOutputs == null ) {
			experimentOutputs = new ExperimentOutputManager(null);
		}
		if ( fileOutputDescription != null ) {
			createOutput(fileOutputDescription);
		}
	}

	private void createOutput(final BatchOutput output) throws GamaRuntimeException {
		// TODO revoir tout ceci. Devrait plut�t �tre une commande
		if ( output == null ) { return; }
		IExpression data = output.getFacet(IKeyword.DATA);
		if ( data == null ) {
			data = exploration.getFitnessExpression();
		}
		String dataString = data == null ? "time" : data.serialize(false);
		log = new FileOutput(output.getLiteral(IKeyword.TO), dataString, new ArrayList(parameters.keySet()), this);
	}

	@Override
	public void open() {
		createAgent();
		scope.getGui().prepareForExperiment(this);
		agent.schedule(agent.getScope());
		// agent.scheduleAndExecute(null);
		if ( isBatch() ) {
			agent.getScope().getGui().informStatus(" Batch ready ");
			agent.getScope().getGui().updateSimulationState();
		}
	}

	@Override
	public void reload() {
		if ( isBatch() ) {
			agent.dispose();
			parametersEditors = null;
			open();
		} else {
			agent.reset();
			agent.getScope().getGui().eraseConsole(true);
			agent.init(agent.getScope());

			agent.getScope().getGui().updateParameterView(this);
		}
	}

	@Override
	public ItemList getParametersEditors() {
		if ( parameters.isEmpty() && explorableParameters.isEmpty() ) { return null; }
		if ( parametersEditors == null ) {
			Collection<IParameter> params = new ArrayList(getParameters().values());
			params.addAll(explorableParameters.values());
			parametersEditors = new ExperimentsParametersList(params);
		}
		return parametersEditors;
	}

	// @Override
	@Override
	public boolean isBatch() {
		return exploration != null;
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
	// public boolean hasParameters() {
	// return targetedVars.size() != 0;
	// }

	// @Override
	public void setParameterValue(final IScope scope, final String name, final Object val) throws GamaRuntimeException {
		checkGetParameter(name).setValue(scope, val);
	}

	// @Override
	public Object getParameterValue(final String name) throws GamaRuntimeException {
		return checkGetParameter(name).value();
	}

	@Override
	public boolean hasParameter(final String name) {
		return getParameter(name) != null;
	}

	public IParameter.Batch getParameter(final String name) {
		final IParameter p = parameters.get(name);
		if ( p != null && p instanceof IParameter.Batch ) { return (IParameter.Batch) p; }
		return null;
	}

	public void addParameter(final IParameter p) {
		// scope.getGui().debug("ExperimentPlan.addParameter " + p.getName());
		// TODO Verify this
		final String name = p.getName();
		IParameter already = parameters.get(name);
		if ( already != null ) {
			p.setValue(scope, already.getInitialValue(scope));
		}
		parameters.put(name, p);
	}

	protected IParameter.Batch checkGetParameter(final String name) throws GamaRuntimeException {
		final IParameter.Batch v = getParameter(name);
		if ( v == null ) { throw GamaRuntimeException
			.error("No parameter named " + name + " in experiment " + getName(), getExperimentScope()); }
		return v;
	}

	@Override
	public Map<String, IParameter> getParameters() {
		return parameters;
		// result.addAll(systemParameters);
		// return result;
	}

	@Override
	public SimulationAgent getCurrentSimulation() {
		if ( agent == null ) { return null; }
		return agent.getSimulation();
	}

	//
	// @Override
	// public boolean isLoading() {
	// if ( agent == null ) { return false; }
	// return agent.isLoading();
	// }

	/**
	 * A short-circuited scope that represents the scope of the experiment. If a simulation is
	 * available, it refers to it and gains access to its global scope. If not, it throws the
	 * appropriate runtime exceptions when a feature dependent on the existence of a simulation is
	 * accessed
	 *
	 * @author Alexis Drogoul
	 * @since November 2011
	 */
	private class Scope extends AbstractScope {

		/**
		 * A flag indicating that the experiment is shutting down. As this scope is used before any experiment agent
		 * (and runtime scope) is defined, it is necessary to define it here.
		 */
		private volatile boolean interrupted;

		public Scope() {
			super(null);
		}

		@Override
		public void setGlobalVarValue(final String name, final Object v) throws GamaRuntimeException {
			if ( hasParameter(name) ) {
				setParameterValue(this, name, v);
				GAMA.getGui().updateParameterView(ExperimentPlan.this);
				return;
			}
			SimulationAgent a = getCurrentSimulation();
			if ( a != null ) {
				a.setDirectVarValue(this, name, v);
			}
		}

		@Override
		public Object getGlobalVarValue(final String name) throws GamaRuntimeException {
			if ( hasParameter(name) ) { return getParameterValue(name); }
			SimulationAgent a = getCurrentSimulation();
			if ( a != null ) { return a.getDirectVarValue(this, name); }
			return null;
		}

		@Override
		public SimulationAgent getSimulationScope() {
			return getCurrentSimulation();
		}

		@Override
		public IExperimentAgent getExperiment() {
			return getAgent();
		}

		@Override
		public IDescription getExperimentContext() {
			return ExperimentPlan.this.getDescription();
		}

		@Override
		public IDescription getModelContext() {
			return ExperimentPlan.this.getModel().getDescription();
		}

		@Override
		public IModel getModel() {
			return ExperimentPlan.this.getModel();
		}

		@Override
		protected boolean _root_interrupted() {
			return interrupted;
		}

		@Override
		public void setInterrupted(final boolean interrupted) {
			this.interrupted = interrupted;
		}

		@Override
		public IScope copy() {
			return new Scope();
		}

		/**
		 * Method getRandom()
		 * @see msi.gama.runtime.IScope#getRandom()
		 */
		@Override
		public RandomUtils getRandom() {
			if ( getAgent() == null ) { return null; }
			return getAgent().getRandomGenerator();
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
	 * @see msi.gama.kernel.experiment.IExperimentPlan#getController()
	 */
	@Override
	public IExperimentController getController() {
		if ( controller == null ) {
			controller = new ExperimentController(this);
		}
		return controller;
	}

	/**
	 * Method refreshAllOutputs()
	 * @see msi.gama.kernel.experiment.IExperimentPlan#refreshAllOutputs()
	 */
	@Override
	public void refreshAllOutputs() {
		for ( IOutputManager manager : getAllSimulationOutputs() ) {
			manager.forceUpdateOutputs();
		}
	}

	/**
	 * Method getOriginalSimulationOutputs()
	 * @see msi.gama.kernel.experiment.IExperimentPlan#getOriginalSimulationOutputs()
	 */
	@Override
	public IOutputManager getOriginalSimulationOutputs() {
		return originalSimulationOutputs;
	}
}
