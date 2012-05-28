package msi.gama.headless.core;

import java.util.ArrayList;
import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.RandomUtils;
import msi.gama.headless.common.DataType;
import msi.gama.headless.common.ISimulator;
import msi.gama.kernel.experiment.AbstractExperiment;
import msi.gama.kernel.experiment.ExperimentParameter;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.GamlSimulation;
import msi.gama.kernel.simulation.IScheduler;
import msi.gama.kernel.simulation.ISimulation;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.with_sequence;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.IList;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;
import msi.gaml.variables.IVariable;



@symbol(name = {  IKeyword.HEADLESS_UI }, kind = ISymbolKind.EXPERIMENT)
@with_sequence
@facets(value = {
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false),
	@facet(name = IKeyword.TYPE, type = IType.LABEL, values = { IKeyword.HEADLESS_UI }, optional = false) }, omissible = IKeyword.NAME)
@inside(symbols=IKeyword.MODEL)
public class HeadLessExperiment extends AbstractExperiment implements IHeadLessExperiment {

	protected final List<IParameter> regularParameters;

	
	
	public HeadLessExperiment(IDescription description) {
		super(description);
		regularParameters = new ArrayList();
	}


	private ParametersSet parameters = new ParametersSet();
	private double seed;
	

	@Override
	public void setSeed(double seed) {
		this.seed=seed;
	}


	@Override
	public void start(int nbStep) {
		for(int i=0; i<nbStep;i++)
			this.stepExperiment();
			
		
	}


	@Override
	public IList<IParameter> getParametersToDisplay() {
		IList<IParameter> result = new GamaList();
		result.addAll(regularParameters);
		result.addAll(systemParameters);
		return result;
	}
	
	
	@Override
	public void stop() {}

	@Override
	public void pause() {}

	@Override
	public void step() {
		stepExperiment();
	}

	@Override
	public void stopExperiment() {}

	@Override
	public void startExperiment() throws GamaRuntimeException {	}


	@Override
	public void reloadExperiment() throws GamaRuntimeException, InterruptedException {
		boolean wasRunning = isRunning() && !isPaused();
		closeCurrentSimulation(false);
		initializeExperiment();
		if ( wasRunning ) {
			startExperiment();
		}
	}

	@Override
	public boolean isGui() {
		return false;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> children) {
		super.setChildren(children);
		for ( ISymbol s : children ) {
			if ( s instanceof IParameter ) {
				addRegularParameter((IParameter) s);
			}
		}
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
	public void stepExperiment() {
		this.currentSimulation.step();
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setParameterWithName(String name, Object value)
			throws GamaRuntimeException, InterruptedException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Object getParameterWithName(String name)
			throws GamaRuntimeException, InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize(final ParametersSet sol, final Double seed) throws InterruptedException,
		GamaRuntimeException {
		
		for ( IParameter p : targetedVars.values() ) {
			String name = p.getName();
			this.getParameter(name);
			
			if ( sol.containsKey(name) ) {
				p.setValue(sol.get(name));
			}
		}
	
		
		
		// GUI.debug("Initializing the random agent");
		getParameter(IKeyword.SEED).setValue(seed);
		random = new RandomUtils(seed);
		// GUI.debug("Instanciating a new simulation");
		currentSimulation = new HeadlessSimulation(this);
		// GUI.debug("Building the outputs of the new simulation");
		currentSimulation.initialize(sol);
		buildOutputs();
	}
	
}
