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
import msi.gama.kernel.simulation.ISimulationAgent;
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
	@facet(name = IKeyword.TYPE, type = IType.LABEL, values = { IKeyword.BATCH, IKeyword.REMOTE, IKeyword.GUI_ }, optional = false) }, omissible = IKeyword.NAME)
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.MODEL })
public class GuiExperimentSpecies extends GamlSpecies implements IExperimentSpecies {

	protected OutputManager output;
	private ItemList parametersEditors;
	protected final Map<String, IParameter> targetedVars;
	protected final List<IParameter> systemParameters;
	protected ExperimentAgent agent;
	protected final List<IParameter> regularParameters;
	protected final ExperimentScope stack;
	protected ExperimentGUIHelper helper;
	protected IModel model;

	@Override
	public ExperimentAgent getAgent() {
		return agent;
	}

	public GuiExperimentSpecies(final IDescription description) {
		super(description);
		regularParameters = new ArrayList();
		setName(description.getName());
		targetedVars = new LinkedHashMap();
		systemParameters = new ArrayList();
		stack = new ExperimentScope(this, "Experiment scope of " + getName());
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
		ExperimentatorPopulation pop = new ExperimentatorPopulation(this);
		IScope scope = getExperimentScope();
		pop.initializeFor(scope);
		agent = (ExperimentAgent) pop.createAgents(scope, 1, Collections.EMPTY_LIST, false).get(0);
		helper = new ExperimentGUIHelper(agent);
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
		String cat = getSystemParametersCategory();
		addSystemParameter(new ExperimentParameter(stack, getVar(IKeyword.RNG), "Random number generator", cat,
			RandomUtils.GENERATOR_NAMES, false));
		addSystemParameter(new ExperimentParameter(stack, getVar(IKeyword.SEED), "Random seed", cat, null, true));
		if ( !isBatch() ) { // FIXME Test to be removed at some point
			for ( IVariable v : model.getVars() ) {
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
		for ( ISymbol s : children ) {
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
		String name = p.getName();
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
		Object o = getParameter(IKeyword.SEED).getInitialValue(stack);
		if ( o == null ) { return null; }
		if ( o instanceof Number ) { return ((Number) o).doubleValue(); }
		return null;
	}

	@Override
	public void userOpen() {
		if ( helper != null && helper.isOpen() ) { return; }
		createAgent();

		parametersEditors = null;
		buildOutputs();
	}

	public void wipeConsoleErrorsAndParameters() {
		if ( isGui() ) {
			GuiUtils.updateParameterView();
			GuiUtils.clearErrors();
			GuiUtils.showConsoleView();
		}
	}

	@Override
	public void userPause() {
		helper.offer(_PAUSE);
	}

	@Override
	public void userStep() {
		helper.offer(_STEP);
	}

	@Override
	public void userInit() {
		helper.offer(_INIT);
	}

	@Override
	public void userInterrupt() {
		helper.interrupt();
	}

	@Override
	public void userReload() {
		helper.offer(_RELOAD);
	}

	@Override
	public void userStart() {
		helper.offer(_START);
	}

	@Override
	public void stop() {
		if ( helper.isOpen() ) {
			helper.offer(_STOP);
		} else {
			agent.userStopExperiment();
		}
	}

	@Override
	public void userClose() {
		GuiUtils.debug("GuiExperimentSpecies.userClose");
		GAMA.updateSimulationState(GAMA.NOTREADY);
		agent.closeSimulation();
		agent.dispose();
		GAMA.updateSimulationState();
		if ( output != null ) {
			output.dispose(true);
			output = null;
		}
	}

	@Override
	public void buildOutputs() {
		wipeConsoleErrorsAndParameters();
		if ( output == null ) {
			output = new OutputManager(null);
		}
		GuiUtils.waitStatus(" Building outputs ");
		output.buildOutputs(this);
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
		IParameter p = targetedVars.get(name);
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
		IParameter.Batch v = getParameter(name);
		if ( v == null ) { throw new GamaRuntimeException("No parameter named " + name + " in experiment " + getName()); }
		return v;
	}

	public void addRegularParameter(final IParameter p) {
		if ( registerParameter(p) ) {
			regularParameters.add(p);
		}
	}

	@Override
	public void desynchronizeOutputs() {
		output.desynchronizeOutputs();
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
		IList<IParameter> result = new GamaList();
		result.addAll(regularParameters);
		result.addAll(systemParameters);
		return result;
	}

	@Override
	public ISimulationAgent getCurrentSimulation() {
		if ( agent == null ) { return null; }
		return agent.getSimulation();
	}

	@Override
	public boolean isLoading() {
		if ( agent == null ) { return false; }
		return agent.isLoading();
	}

	@Override
	public boolean isPaused() {
		if ( agent == null ) { return false; }
		return agent.isPaused();
	}

	@Override
	public boolean isRunning() {
		if ( agent == null ) { return false; }
		return agent.isRunning();
	}

}
