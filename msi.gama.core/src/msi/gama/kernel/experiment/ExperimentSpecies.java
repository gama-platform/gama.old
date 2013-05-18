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
package msi.gama.kernel.experiment;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.outputs.OutputManager;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.species.GamlSpecies;
import msi.gaml.types.IType;
import msi.gaml.variables.IVariable;

/**
 * Written by drogoul Modified on 28 mai 2011
 * Apr. 2013: Important modifications to enable running true experiment agents
 * 
 * @todo Description
 * 
 */
@symbol(name = { IKeyword.GUI_ }, kind = ISymbolKind.EXPERIMENT, with_sequence = true)
@facets(value = {
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false),
	@facet(name = IKeyword.TITLE, type = IType.LABEL, optional = false),
	@facet(name = IKeyword.PARENT, type = IType.ID, optional = true),
	@facet(name = IKeyword.SKILLS, type = IType.LIST, optional = true),
	@facet(name = IKeyword.CONTROL, type = IType.ID, optional = true),
	@facet(name = IKeyword.FREQUENCY, type = IType.INT, optional = true),
	@facet(name = IKeyword.SCHEDULES, type = IType.CONTAINER, optional = true),
	@facet(name = IKeyword.TYPE, type = IType.LABEL, values = { IKeyword.BATCH, IKeyword.REMOTE, IKeyword.GUI_ }, optional = false) }, omissible = IKeyword.NAME)
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.MODEL })
public class ExperimentSpecies extends GamlSpecies implements IExperimentSpecies {

	protected OutputManager output;
	private ItemList parametersEditors;
	protected final Map<String, IParameter> targetedVars;
	protected final List<IParameter> systemParameters;
	protected ExperimentAgent agent;
	protected final List<IParameter> regularParameters;
	protected final Scope stack;
	// protected FrontEndController helper;
	protected IModel model;

	@Override
	public ExperimentAgent getAgent() {
		return agent;
	}

	public ExperimentSpecies(final IDescription description) {
		super(description);
		regularParameters = new ArrayList();
		setName(description.getName());
		targetedVars = new LinkedHashMap();
		systemParameters = new ArrayList();
		// FIXME Is Null acceptable ?
		stack = new Scope(null);
	}

	@Override
	public void dispose() {
		if ( output != null ) {
			output.dispose(true);
			output = null;
		}
		targetedVars.clear();
		systemParameters.clear();
		if ( agent != null ) {
			agent.dispose();
			agent = null;
		}
		regularParameters.clear();
		super.dispose();
	}

	protected void createAgent() {
		final ExperimentPopulation pop = new ExperimentPopulation(this);
		final IScope scope = getExperimentScope();
		pop.initializeFor(scope);
		agent = (ExperimentAgent) pop.createAgents(scope, 1, Collections.EMPTY_LIST, false).get(0);
	}

	@Override
	public IModel getModel() {
		return model;
	}

	@Override
	public void setModel(final IModel model) {
		this.model = model;
		addOwnParameters();
	}

	protected void addOwnParameters() {
		final String cat = getSystemParametersCategory();
		addSystemParameter(new ExperimentParameter(stack, getVar(IKeyword.RNG), "Random number generator", cat,
			RandomUtils.GENERATOR_NAMES, false));
		addSystemParameter(new ExperimentParameter(stack, getVar(IKeyword.SEED), "Random seed", cat, null, true));
		if ( !isBatch() ) { // FIXME Test to be removed at some point
			for ( final IVariable v : model.getVars() ) {
				if ( v.isParameter() ) {
					addRegularParameter(new ExperimentParameter(stack, v));
				}
			}
		}
	}

	protected String getSystemParametersCategory() {
		return "Model " + getModel().getName() + ItemList.SEPARATION_CODE + ItemList.INFO_CODE +
			SYSTEM_CATEGORY_PREFIX + " '" + getName() + "'";
	}

	@Override
	public final OutputManager getOutputManager() {
		return output;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> children) {
		super.setChildren(children);
		for ( final ISymbol s : children ) {
			if ( s instanceof OutputManager ) {
				if ( output != null ) {
					output.setChildren(((OutputManager) s).getChildren());
				} else {
					output = (OutputManager) s;
				}
			} else if ( s instanceof IParameter.Batch ) {
				addRegularParameter((IParameter) s);
			}
		}
	}

	protected boolean registerParameter(final IParameter p) {
		final String name = p.getName();
		if ( targetedVars.containsKey(name) ) { return false; }
		targetedVars.put(name, p);
		return true;
	}

	@Override
	public ParametersSet getCurrentSolution() throws GamaRuntimeException {
		return new ParametersSet(targetedVars, false);
	}

	@Override
	public Double getCurrentSeed() {
		final Object o = getParameter(IKeyword.SEED).getInitialValue(stack);
		if ( o == null ) { return null; }
		if ( o instanceof Number ) { return ((Number) o).doubleValue(); }
		return null;
	}

	@Override
	public void schedule() {
		// GuiUtils.debug("GuiExperimentSpecies.schedule");
		if ( agent == null ) { return; }
		// The scheduler of the agent is scheduled in the global scheduler
		GAMA.controller.scheduler.schedule(agent.getScheduler(), agent.getScope());
	}

	@Override
	public void open() {
		// GuiUtils.debug("GuiExperimentSpecies.open");
		createAgent();
		parametersEditors = null;
		buildOutputs();
	}

	@Override
	public void reload() {
		// GuiUtils.debug("GuiExperimentSpecies.reload");
		desynchronizeOutputs();
		agent.reset();
		buildOutputs();
		schedule();
	}

	@Override
	public void close() {
		// GuiUtils.debug("GuiExperimentSpecies.close");
		if ( agent != null ) {
			agent.dispose();
			agent = null;
		}
		if ( output != null ) {
			output.dispose(true);
			output = null;
		}
	}

	@Override
	public void buildOutputs() {
		if ( output == null ) {
			output = new OutputManager(null);
		}
		GuiUtils.waitStatus(" Building outputs ");
		output.buildOutputs(this);
	}

	@Override
	public void desynchronizeOutputs() {
		output.desynchronizeOutputs();
	}

	@Override
	public ItemList getParametersEditors() {
		if ( parametersEditors == null ) {
			parametersEditors = new ExperimentsParametersList(getParametersToDisplay());
		}
		return parametersEditors;
	}

	@Override
	public boolean isBatch() {
		return false;
	}

	@Override
	public boolean isGui() {
		return true;
	}

	@Override
	public IScope getExperimentScope() {
		return stack;
	}

	@Override
	public boolean hasParameters() {
		return targetedVars.size() != 0;
	}

	@Override
	public void setParameterValue(final String name, final Object val) throws GamaRuntimeException {
		checkGetParameter(name).setValue(val);
	}

	@Override
	public Object getParameterValue(final String name) throws GamaRuntimeException {
		return checkGetParameter(name).value();
	}

	@Override
	public boolean hasParameter(final String name) {
		return getParameter(name) != null;
	}

	public IParameter.Batch getParameter(final String name) {
		final IParameter p = targetedVars.get(name);
		if ( p != null && p instanceof IParameter.Batch ) { return (IParameter.Batch) p; }
		return null;
	}

	public void addSystemParameter(final IParameter p) {
		if ( registerParameter(p) ) {
			systemParameters.add(p);
		} else {
			p.setValue(targetedVars.get(p.getName()).getInitialValue(stack));
			targetedVars.put(p.getName(), p);
		}
	}

	protected IParameter.Batch checkGetParameter(final String name) throws GamaRuntimeException {
		final IParameter.Batch v = getParameter(name);
		if ( v == null ) { throw GamaRuntimeException.error("No parameter named " + name + " in experiment " +
			getName()); }
		return v;
	}

	public void addRegularParameter(final IParameter p) {
		if ( registerParameter(p) ) {
			regularParameters.add(p);
		}
	}

	@Override
	public IList<String> getParametersNames() {
		final GamaList<String> result = new GamaList<String>();
		for ( final IParameter v : regularParameters ) {
			result.add(v.getName());
		}
		return result;
	}

	@Override
	public IList<IParameter> getParametersToDisplay() {
		final IList<IParameter> result = new GamaList();
		result.addAll(regularParameters);
		result.addAll(systemParameters);
		return result;
	}

	@Override
	public SimulationAgent getCurrentSimulation() {
		if ( agent == null ) { return null; }
		return agent.getSimulation();
	}

	@Override
	public boolean isLoading() {
		if ( agent == null ) { return false; }
		return agent.isLoading();
	}

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
		 * (and
		 * runtime scope) is defined, it is necessary to define it here.
		 */
		private volatile boolean interrupted;

		public Scope(final IMacroAgent root) {
			super(root);
		}

		@Override
		public void setGlobalVarValue(final String name, final Object v) throws GamaRuntimeException {
			if ( hasParameter(name) ) {
				setParameterValue(name, v);
				return;
			}
			getCurrentSimulation().setDirectVarValue(this, name, v);
		}

		@Override
		public Object getGlobalVarValue(final String name) throws GamaRuntimeException {
			if ( hasParameter(name) ) { return getParameterValue(name); }
			return getCurrentSimulation().getDirectVarValue(this, name);
		}

		@Override
		public SimulationAgent getSimulationScope() {
			final ExperimentAgent a = getAgent();
			if ( a == null ) { return null; }
			return a.getSimulation();
		}

		@Override
		public IModel getModel() {
			return ExperimentSpecies.this.getModel();
		}

		@Override
		public boolean interrupted() {
			return interrupted;
		}

		@Override
		public void setInterrupted(final boolean interrupted) {
			this.interrupted = interrupted;
		}

		@Override
		public IScope copy() {
			return new Scope(getRoot());
		}

	}
}
