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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.kernel.experiment;

import java.util.*;
import java.util.concurrent.Semaphore;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.batch.*;
import msi.gama.outputs.FileOutput;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import msi.gaml.variables.IVariable;
import org.jfree.data.statistics.Statistics;

/**
 * Written by drogoul Modified on 28 mai 2011
 * 
 * @todo Description
 * 
 */
@symbol(name = { IKeyword.BATCH }, kind = ISymbolKind.EXPERIMENT, with_sequence = true)
@facets(value = {
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false),
	@facet(name = IKeyword.TITLE, type = IType.LABEL, optional = false),
	@facet(name = IKeyword.KEEP_SEED, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.REPEAT, type = IType.INT, optional = true),
	@facet(name = IKeyword.UNTIL, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.MULTICORE, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.TYPE, type = IType.LABEL, values = { IKeyword.BATCH, IKeyword.REMOTE, IKeyword.GUI_ }, optional = false) }, omissible = IKeyword.NAME)
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.MODEL })
@Deprecated
public class BatchExperiment extends ExperimentSpecies {

	/**
	 * FIXME EVERYTHING IS BROKEN HERE.
	 */
	private IExploration exploAlgo;
	private FileOutput log;
	private final IExpression stopCondition;
	private int innerLoopRepeat;
	private boolean keep_seed;
	private ParametersSet currentSolution;
	private Double[] seeds;
	private final List<Double> fitnessValues = new ArrayList();
	private Double lastFitnessValue;
	private BatchOutput fileOutputDescription;
	private final List<IParameter.Batch> explorableParameters = new ArrayList();
	private final List<IParameter.Batch> fixedParameters = new ArrayList();
	private final List<IParameter.Batch> methodParameters = new ArrayList();
	private GamaHelper haltAction;
	private final Semaphore innerLoopSemaphore = new Semaphore(1, false);
	private/* volatile */int runNumber, innerLoopIndex;
	private boolean multicore;

	public BatchExperiment(final IDescription description) throws GamaRuntimeException {
		super(description);
		IExpression expr = getFacet(IKeyword.KEEP_SEED);
		if ( expr != null && expr.isConst() ) {
			keep_seed = Cast.asBool(getExperimentScope(), expr.value(getExperimentScope()));
		}

		IExpression multi = getFacet(IKeyword.MULTICORE);
		if ( multi != null && multi.isConst() ) {
			this.multicore = Cast.asBool(getExperimentScope(), multi.value(getExperimentScope()));
			// GuiUtils.isSimulationPerspective();//(GuiUtils.HPC_PERSPECTIVE_ID);
		}

		stopCondition = getFacet(IKeyword.UNTIL);
		if ( stopCondition != null ) {
			haltAction = new GamaHelper() {

				@Override
				public Object run(final IScope scope) throws GamaRuntimeException {
					// conditionalHalt(scope);
					return null;
				}

			};
		}
		expr = getFacet(IKeyword.REPEAT);
		if ( expr != null && expr.isConst() ) {
			innerLoopRepeat = Cast.asInt(getExperimentScope(), expr.value(getExperimentScope()));
		}
		exploAlgo = new ExhaustiveSearch(null);
	}

	@Override
	public void open() {
		super.open();
		// GuiUtils.showParameterView(this);
		GuiUtils.informStatus(" Batch ready ");
	}

	//
	// @Override
	// protected void processUserCommand(final int command) throws InterruptedException {
	// if ( command == _NEXT ) {
	// closeCurrentSimulation(false);
	// } else {
	// super.processUserCommand(command);
	// }
	// }

	@Override
	public ParametersSet getCurrentSolution() throws GamaRuntimeException {
		return currentSolution;
	}

	// @Override
	// public void initializeNewSimulation(final ParametersSet sol, final Double seed) throws GamaRuntimeException,
	// InterruptedException {
	// super.initializeNewSimulation(sol, seed);
	// getCurrentSimulation().getScheduler().insertEndAction(haltAction);
	// }
	//
	// public void conditionalHalt(final IScope scope) {
	// try {
	// if ( Cast.asBool(scope, stopCondition.value(scope)) ) {
	// commands.offer(_NEXT);
	// getCurrentSimulation().getScheduler().removeAction(haltAction);
	// }
	// } catch (GamaRuntimeException e) {
	// e.addContext("in the halt condition of batch experiment " + getName());
	// GAMA.reportError(e);
	// }
	// }

	@Override
	public boolean isBatch() {
		return true;
	}

	public void closeCurrentSimulation(final boolean closingExperiment) {
		if ( getCurrentSimulation() == null ) { return; }
		// currentSimulation.stop();
		if ( !closingExperiment ) {
			try { // while the simulation is still "alive"
				IExpression fitness = exploAlgo.getFitnessExpression();
				if ( fitness != null ) {
					lastFitnessValue = Cast.asFloat(this.getExperimentScope(), fitness.value(getExperimentScope()));
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
		// getCurrentSimulation().close();
		// currentSimulation = null;
		// }
		if ( !closingExperiment ) {
			innerLoopSemaphore.release(2);
		}
	}

	@Override
	public void close() {
		closeCurrentSimulation(true);
	}

	void initRandom() {

		seeds = new Double[innerLoopRepeat];

		// FIXME A REVOIR. JE NE COMPRENDS PAS CE QUE CA FAIT.
		if ( keep_seed ) {
			for ( int i = 0; i < innerLoopRepeat; i++ ) {
				seeds[i] = GAMA.getRandom().between(0d, Long.MAX_VALUE);
			}
		}
		// GUI.debug("Seeds : " + Arrays.toString(seeds));
	}

	public void startExperiment() {
		// if ( !isRunning() ) {
		initRandom();
		exploAlgo.start();
		// }
	}

	public Double launchSimulationsWithSolution(final ParametersSet sol) throws GamaRuntimeException {
		currentSolution = sol;
		fitnessValues.clear();
		runNumber = runNumber + 1;
		for ( innerLoopIndex = 0; innerLoopIndex < seeds.length; innerLoopIndex++ ) {
			try {
				innerLoopSemaphore.acquire(1);
				// initialize(currentSolution, seeds[innerLoopIndex]);
				// startCurrentSimulation();
				innerLoopSemaphore.acquire(1);
			} catch (InterruptedException e) {}
		}
		short fitnessCombination = exploAlgo.getCombination();
		return fitnessCombination == IExploration.C_MAX ? Collections.max(fitnessValues)
			: fitnessCombination == IExploration.C_MIN ? Collections.min(fitnessValues) : Statistics
				.calculateMean(fitnessValues);

	}

	//
	// @Override
	// public void close() {
	// super.close();
	// exploAlgo = null;
	// }

	@Override
	public void reload() {
		// boolean wasRunning = !GAMA.isPaused(); // && not simulation null in agent ?
		// stopExperiment();
		// initializeSimulation();
		// if ( wasRunning ) {
		// startExperiment();
		// }
	}

	@Override
	public void setChildren(final List<? extends ISymbol> children) {
		super.setChildren(children);
		for ( ISymbol s : children ) {
			if ( s instanceof BatchOutput ) {
				fileOutputDescription = (BatchOutput) s;
			} else if ( s instanceof IExploration && (s.hasFacet(IKeyword.MAXIMIZE) || s.hasFacet(IKeyword.MINIMIZE)) ) {
				exploAlgo = (IExploration) s;
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
		try {
			exploAlgo.initializeFor(this);
		} catch (GamaRuntimeException e) {
			// TODO Why make an error on the description ?
			description.error(e.getMessage(), IGamlIssue.GENERAL);
		}
	}

	private void createOutput(final BatchOutput output) throws GamaRuntimeException {
		// TODO revoir tout ceci. Devrait plutôt être une commande
		if ( output == null ) { return; }
		IExpression data = output.getFacet(IKeyword.DATA);
		if ( data == null ) {
			data = exploAlgo.getFitnessExpression();
		}
		String dataString = data == null ? "time" : data.toGaml();
		log = new FileOutput(output.getLiteral(IKeyword.TO), dataString, getParametersNames(), this);
	}

	@Override
	protected void addOwnParameters() {
		// ISpecies world = model.getWorldSpecies();
		for ( IVariable v : model.getVars() ) {
			if ( v.isParameter() ) {
				ExperimentParameter p = new ExperimentParameter(stack, v);
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
		String s = "(" + exploAlgo.getCombinationName() + " of " + innerLoopRepeat + " simulations)";
		addMethodParameter(new ParameterAdapter("Best fitness", BATCH_CATEGORY_NAME, s, IType.STRING) {

			@Override
			public Object value() {
				return exploAlgo != null && exploAlgo.getBestFitness() != null ? exploAlgo.getBestFitness().toString()
					: "-";
			}

		});

		addMethodParameter(new ParameterAdapter("Last fitness", BATCH_CATEGORY_NAME, "(of the last single simulation)",
			IType.STRING) {

			@Override
			public Object value() {
				return lastFitnessValue != null ? lastFitnessValue.toString() : "-";
			}

		});
		addMethodParameter(new ParameterAdapter("Best solution", BATCH_CATEGORY_NAME, IType.STRING) {

			@Override
			public Object value() {
				return exploAlgo != null && exploAlgo.getBestSolution() != null ? exploAlgo.getBestSolution()
					.toString() : "-";
			}

		});
		super.addOwnParameters();
		exploAlgo.addParametersTo(this);
	}

	@Override
	public IList<IParameter> getParametersToDisplay() {
		IList<IParameter> result = new GamaList();
		result.addAll(methodParameters);
		result.addAll(explorableParameters);
		result.addAll(fixedParameters);
		return result;
	}

	public List<IParameter.Batch> getParametersToExplore() {
		return explorableParameters;
	}

	@Override
	public IList<String> getParametersNames() {
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

	// @Override
	// public void buildOutputs() {
	// super.buildOutputs();
	// if ( fileOutputDescription != null && log == null ) {
	// try {
	// createOutput(fileOutputDescription);
	// } catch (GamaRuntimeException e) {
	// GAMA.reportError(e);
	// }
	// }
	// }

}
