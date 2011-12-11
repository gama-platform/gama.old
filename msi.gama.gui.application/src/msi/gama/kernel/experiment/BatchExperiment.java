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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.kernel.experiment;

import java.util.*;
import java.util.concurrent.Semaphore;
import msi.gama.gui.application.GUI;
import msi.gama.interfaces.*;
import msi.gama.internal.compilation.*;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.*;
import msi.gama.outputs.FileOutput;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.with_sequence;
import msi.gama.precompiler.*;
import msi.gama.util.*;
import msi.gaml.batch.*;
import org.jfree.data.statistics.Statistics;

/**
 * Written by drogoul Modified on 28 mai 2011
 * 
 * @todo Description
 * 
 */
@symbol(name = { ISymbol.BATCH }, kind = ISymbolKind.EXPERIMENT)
@with_sequence
@facets({
	@facet(name = ISymbol.NAME, type = IType.LABEL, optional = false),
	@facet(name = ISymbol.KEEP_SEED, type = IType.BOOL_STR, optional = true),
	@facet(name = ISymbol.REPEAT, type = IType.INT_STR, optional = true),
	@facet(name = ISymbol.UNTIL, type = IType.BOOL_STR, optional = true),
	@facet(name = ISymbol.TYPE, type = IType.LABEL, values = { ISymbol.BATCH, ISymbol.REMOTE,
		ISymbol.GUI_ }, optional = false) })
@inside(symbols = ISymbol.MODEL)
public class BatchExperiment extends AbstractExperiment implements IBatch {

	private ParamSpaceExploAlgorithm exploAlgo;
	private FileOutput log;
	private final IExpression stopCondition;
	private int innerLoopRepeat;
	private boolean keep_seed;
	private Solution currentSolution;
	private Double[] seeds;
	private final List<Double> fitnessValues = new ArrayList();
	private Double lastFitnessValue;
	private BatchOutput fileOutputDescription;
	private final List<IParameter.Batch> explorableParameters = new ArrayList();
	private final List<IParameter.Batch> fixedParameters = new ArrayList();
	private final List<IParameter.Batch> methodParameters = new ArrayList();
	private ScheduledAction haltAction;
	private final Semaphore innerLoopSemaphore = new Semaphore(1, false);
	private volatile int runNumber, innerLoopIndex;

	public BatchExperiment(final IDescription description) throws GamaRuntimeException {
		super(description);
		IExpression expr = getFacet(ISymbol.KEEP_SEED);
		if ( expr != null && expr.isConst() ) {
			keep_seed = Cast.asBool(expr.value(getExperimentScope()));
		}

		stopCondition = getFacet(UNTIL);
		if ( stopCondition != null ) {
			haltAction = ScheduledAction.newInstance(this, "conditionalHalt");
		}
		expr = getFacet(REPEAT);
		if ( expr != null && expr.isConst() ) {
			innerLoopRepeat = Cast.asInt(expr.value(getExperimentScope()));
		}
		exploAlgo = new ExhaustiveSearch(null);
	}

	@Override
	public void open() {
		if ( isOpen ) { return; }
		super.open();
		GUI.showParameterView(this);
		GUI.informStatus(" Batch ready ");
	}

	@Override
	protected void processCommand(final int command) throws InterruptedException {
		if ( command == _NEXT ) {
			closeCurrentSimulation(false);
		} else {
			super.processCommand(command);
		}
	}

	@Override
	protected Solution getCurrentSolution() throws GamaRuntimeException {
		return currentSolution;
	}

	@Override
	public void initializeExperiment() {
		// DOES NOTHING UNTIL THE SOLUTION/SEED ARE NOT GIVEN
	}

	@Override
	public void initialize(final Solution sol, final Double seed) throws GamaRuntimeException,
		InterruptedException {
		super.initialize(sol, seed);
		currentSimulation.getScheduler().insertEndAction(haltAction);
	}

	public void conditionalHalt(final IScope scope) {
		try {
			if ( Cast.asBool(scope, stopCondition.value(scope)) ) {
				commands.offer(_NEXT);
				currentSimulation.getScheduler().removeAction(haltAction);
			}
		} catch (GamaRuntimeException e) {
			e.addContext("in the halt condition of batch experiment " + getName());
			GAMA.reportError(e);
		}
	}

	@Override
	public boolean isBatch() {
		return true;
	}

	@Override
	public void closeCurrentSimulation(final boolean closingExperiment) {
		if ( currentSimulation == null ) { return; }
		// currentSimulation.stop();
		if ( !closingExperiment ) {
			try { // while the simulation is still "alive"
				IExpression fitness = exploAlgo.getFitnessExpression();
				if ( fitness != null ) {
					lastFitnessValue =
						Cast.asFloat(fitness.value(currentSimulation.getGlobalScope()));
					fitnessValues.add(lastFitnessValue);
				}
				if ( log != null ) {
					log.doRefreshWriteAndClose(currentSolution, lastFitnessValue);
				}
			} catch (GamaRuntimeException e) {
				e.addContext("in saving the results of the batch");
				GAMA.reportError(e);
			}
		}
		// if ( currentSimulation != null ) {
		currentSimulation.close();
		currentSimulation = null;
		// }
		if ( !closingExperiment ) {
			innerLoopSemaphore.release(2);
		}
	}

	@Override
	public void stopExperiment() {
		closeCurrentSimulation(true);
	}

	void initRandom() {

		seeds = new Double[innerLoopRepeat];

		// A REVOIR. JE NE COMPRENDS PAS CE QUE CA FAIT.
		if ( keep_seed ) {
			for ( int i = 0; i < innerLoopRepeat; i++ ) {
				seeds[i] = GAMA.getRandom().between(0d, Long.MAX_VALUE);
			}
		}
		// GUI.debug("Seeds : " + Arrays.toString(seeds));
	}

	@Override
	public void startExperiment() throws GamaRuntimeException {
		if ( !isRunning() ) {
			initRandom();
			exploAlgo.start();
		}
	}

	public Double launchSimulationsWithSolution(final Solution sol) throws GamaRuntimeException {
		currentSolution = sol;
		fitnessValues.clear();
		runNumber++;
		for ( innerLoopIndex = 0; innerLoopIndex < seeds.length; innerLoopIndex++ ) {
			try {
				innerLoopSemaphore.acquire(1);
				initialize(currentSolution, seeds[innerLoopIndex]);
				startCurrentSimulation();
				innerLoopSemaphore.acquire(1);
			} catch (InterruptedException e) {}
		}
		short fitnessCombination = exploAlgo.getCombination();
		return fitnessCombination == ParamSpaceExploAlgorithm.C_MAX ? Collections
			.max(fitnessValues) : fitnessCombination == ParamSpaceExploAlgorithm.C_MIN
			? Collections.min(fitnessValues) : Statistics.calculateMean(fitnessValues);

	}

	@Override
	public void stepExperiment() {
		// Avancer a la prochaine simulation ??
		if ( currentSimulation != null && !isLoading() ) {
			currentSimulation.step();
		}
	}

	@Override
	public void closeExperiment() {
		super.closeExperiment();
		exploAlgo = null;
	}

	@Override
	public void reloadExperiment() throws GamaRuntimeException, GamlException, InterruptedException {
		boolean wasRunning = isRunning() && !isPaused();
		stopExperiment();
		initializeExperiment();
		if ( wasRunning ) {
			startExperiment();
		}
	}

	@Override
	public void setChildren(final List<? extends ISymbol> children) throws GamlException {
		super.setChildren(children);
		for ( ISymbol s : children ) {
			if ( s instanceof BatchOutput ) {
				fileOutputDescription = (BatchOutput) s;
			} else if ( s instanceof ParamSpaceExploAlgorithm &&
				(s.hasFacet(MAXIMIZE) || s.hasFacet(MINIMIZE)) ) {
				exploAlgo = (ParamSpaceExploAlgorithm) s;
				// initRandom();
			} else if ( s instanceof IParameter.Batch ) {
				IParameter.Batch pb = (IParameter.Batch) s;
				pb.setEditable(false);
				if ( pb.canBeExplored() ) {
					addExplorableParameter(pb);
				} else {
					addFixedParameter(pb);
				}
			}
		}
		exploAlgo.initializeFor(this);
	}

	private void createOutput(final BatchOutput output) throws GamlException, GamaRuntimeException {
		// TODO revoir tout ceci. Devrait plutôt être une commande
		if ( output == null ) { return; }
		IExpression data = output.getFacet(ISymbol.DATA);
		if ( data == null ) {
			data = exploAlgo.getFitnessExpression();
		}
		String dataString = data == null ? "time" : data.toGaml();
		log = new FileOutput(output.getLiteral(ISymbol.TO), dataString, getParametersNames(), this);
	}

	@Override
	protected void addOwnParameters() {
		ISpecies world = model.getWorldSpecies();
		for ( IVariable v : world.getVars() ) {
			if ( v.isParameter() ) {
				ExperimentParameter p = new ExperimentParameter(v);
				if ( p.canBeExplored() ) {
					p.setEditable(false);
					p.setCategory(EXPLORABLE_CATEGORY_NAME);
					addExplorableParameter(p);
				}
			}
		}
		addMethodParameter(new ParameterAdapter("Run", BATCH_CATEGORY_NAME, IType.INT) {

			@Override
			public Object value() {
				return runNumber;
			}

		});

		addMethodParameter(new ParameterAdapter("Repeat", BATCH_CATEGORY_NAME, IType.STRING) {

			@Override
			public String getUnitLabel() {
				return " / " + innerLoopRepeat;
			}

			@Override
			public Object value() {
				return innerLoopIndex + 1;
			}

		});
		addMethodParameter(new ParameterAdapter("Stop condition", BATCH_CATEGORY_NAME, IType.STRING) {

			@Override
			public Object value() {
				return stopCondition != null ? stopCondition.toGaml() : "none";
			}

		});
		String s =
			"(" + exploAlgo.getCombinationName() + " of " + innerLoopRepeat + " simulations)";
		addMethodParameter(new ParameterAdapter("Best fitness", BATCH_CATEGORY_NAME, s,
			IType.STRING) {

			@Override
			public Object value() {
				return exploAlgo != null && exploAlgo.getBestFitness() != null ? exploAlgo
					.getBestFitness().toString() : "-";
			}

		});

		addMethodParameter(new ParameterAdapter("Last fitness", BATCH_CATEGORY_NAME,
			"(of the last single simulation)", IType.STRING) {

			@Override
			public Object value() {
				return lastFitnessValue != null ? lastFitnessValue.toString() : "-";
			}

		});
		addMethodParameter(new ParameterAdapter("Best solution", BATCH_CATEGORY_NAME, IType.STRING) {

			@Override
			public Object value() {
				return exploAlgo != null && exploAlgo.getBestSolution() != null ? exploAlgo
					.getBestSolution().toString() : "-";
			}

		});
		super.addOwnParameters();
		exploAlgo.addParametersTo(this);
	}

	@Override
	public List<IParameter> getParametersToDisplay() {
		List<IParameter> result = new GamaList();
		result.addAll(methodParameters);
		result.addAll(explorableParameters);
		result.addAll(fixedParameters);
		return result;
	}

	public List<IParameter.Batch> getParametersToExplore() {
		return explorableParameters;
	}

	@Override
	public List<String> getParametersNames() {
		final GamaList<String> result = new GamaList<String>();
		for ( final IParameter v : explorableParameters ) {
			result.add(v.getName());
		}
		return result;
	}

	public void addFixedParameter(final IParameter.Batch p) {
		if ( registerParameter(p) ) {
			p.setCategory(FIXED_CATEGORY_NAME);
			fixedParameters.add(p);
		}
	}

	public void addMethodParameter(final IParameter.Batch p) {
		if ( registerParameter(p) ) {
			methodParameters.add(p);
		}
	}

	public void addExplorableParameter(final IParameter.Batch p) {
		if ( registerParameter(p) ) {
			p.setCategory(EXPLORABLE_CATEGORY_NAME);
			explorableParameters.add(p);
		}
	}

	@Override
	protected void buildOutputs() {
		super.buildOutputs();
		if ( fileOutputDescription != null && log == null ) {
			try {
				createOutput(fileOutputDescription);
			} catch (GamaRuntimeException e) {
				GAMA.reportError(e);
			} catch (GamlException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
