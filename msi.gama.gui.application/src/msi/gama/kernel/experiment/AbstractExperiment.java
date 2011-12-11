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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.kernel.experiment;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import msi.gama.environment.ModelEnvironment;
import msi.gama.gui.application.GUI;
import msi.gama.gui.application.views.*;
import msi.gama.gui.parameters.*;
import msi.gama.interfaces.*;
import msi.gama.internal.expressions.IExpressionFactory;
import msi.gama.kernel.*;
import msi.gama.kernel.exceptions.*;
import msi.gama.kernel.stack.ExperimentStack;
import msi.gama.outputs.OutputManager;
import msi.gama.util.RandomAgent;
import msi.gaml.batch.Solution;
import msi.gaml.expressions.GamlExpressionFactory;
import msi.gaml.kernel.GamlSimulation;

// TODO RAJOUTER :
// - des descriptions de buttons (final pour actions)
// - un init
// - des statistiques

/**
 * Written by drogoul Modified on 28 mai 2011
 * 
 * @todo Description
 * 
 */
public abstract class AbstractExperiment extends Symbol implements IExperiment, Runnable {

	protected volatile boolean isOpen;
	protected IModel model;
	protected ISimulation currentSimulation;
	protected OutputManager output;
	protected RandomAgent random;
	IExpressionFactory expressionFactory;
	private EditorsList parametersEditors;
	private final Map<String, IParameter> targetedVars;
	private final ExperimentStack stack;
	public volatile Thread experimentThread;
	protected volatile ArrayBlockingQueue<Integer> commands;
	protected final List<IParameter> systemParameters;

	public AbstractExperiment(final IDescription description) {
		super(description);
		setName(description.getName());
		targetedVars = new HashMap();
		stack = new ExperimentStack(this);
		commands = new ArrayBlockingQueue(10);
		systemParameters = new ArrayList();
	}

	@Override
	public IExpressionFactory getExpressionFactory() {
		if ( expressionFactory == null ) {
			expressionFactory = new GamlExpressionFactory(getDescription().getModelDescription());
		}
		return expressionFactory;
	}

	@Override
	public RandomAgent getRandomGenerator() {
		return random == null ? RandomAgent.getDefault() : random;
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
		ISpecies world = model.getWorldSpecies();
		String cat = getSystemParametersCategory();
		addSystemParameter(new ExperimentParameter(world.getVar(ISymbol.RNG),
			"Random number generator", cat, RandomAgent.GENERATOR_NAMES, false));
		addSystemParameter(new ExperimentParameter(world.getVar(ISymbol.SEED), "Random seed", cat,
			null, true));
		// addSystemParameter(new ExperimentParameter(world.getVar(ISymbol.STRATEGY),
		// "Scheduling strategy", cat, Scheduler.STRATEGY_NAMES, false));
		// addSystemParameter(new ExperimentParameter(world.getVar(GAMA._FATAL),
		// "Pause in case of errors", cat, null, false));
		// addSystemParameter(new ExperimentParameter(world.getVar(GAMA._WARNINGS),
		// "Treat warnings as errors", cat, null, false));

	}

	protected String getSystemParametersCategory() {
		return "Model " + getModel().getName() + ItemList.SEPARATION_CODE + ItemList.INFO_CODE +
			SYSTEM_CATEGORY_PREFIX + " '" + getName() + "'";
	}

	@Override
	public ISimulation getCurrentSimulation() {
		return currentSimulation;
	}

	@Override
	public final OutputManager getOutputManager() {
		return output;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> children) throws GamlException {
		for ( ISymbol s : children ) {
			if ( s instanceof OutputManager ) {
				if ( output != null ) {
					output.setChildren(((OutputManager) s).getChildren());
				} else {
					output = (OutputManager) s;
				}
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
	public void run() {
		while (isOpen) {
			try {
				processCommand(commands.take());
			} catch (InterruptedException e) {
				GUI.errorStatus("Cancelled");
				close();
			}
		}
	}

	protected void processCommand(final int command) throws InterruptedException {
		switch (command) {
			case _INIT:
				GAMA.updateSimulationState(GAMA.NOTREADY);
				try {
					initializeExperiment();
				} catch (InterruptedException e) {
					throw e;
				} catch (GamaRuntimeException e) {
					GUI.errorStatus(e.getMessage());
					reportError(e);
					close();
				} catch (Exception e) {
					GUI.errorStatus(e.getMessage());
					reportError(new GamaRuntimeException(e));
					close();
				} finally {
					GAMA.updateSimulationState();
				}
				break;
			case _START:
				try {
					startExperiment();
				} catch (GamaRuntimeException e) {
					reportError(e);
					close();
				} finally {
					GAMA.updateSimulationState(GAMA.RUNNING);
				}
				break;
			case _PAUSE:
				GAMA.updateSimulationState(GAMA.PAUSED);
				pauseExperiment();
				break;
			case _STEP:
				GAMA.updateSimulationState(GAMA.PAUSED);
				stepExperiment();
				break;
			case _STOP:
				GAMA.updateSimulationState(GAMA.NONE);
				stopExperiment();
				break;
			case _CLOSE:
				GAMA.updateSimulationState(GAMA.NOTREADY);
				closeExperiment();
				GAMA.updateSimulationState();
				break;
			case _RELOAD:
				GAMA.updateSimulationState(GAMA.NOTREADY);
				try {
					reloadExperiment();
				} catch (InterruptedException e) {
					throw e;
				} catch (GamaRuntimeException e) {
					reportError(e);
					close();
				} catch (Exception e) {
					reportError(new GamaRuntimeException(e));
					close();
				} finally {
					GAMA.updateSimulationState();
				}
				break;
		}
	}

	@Override
	public void open() {
		if ( isOpen ) { return; }
		isOpen = true;
		experimentThread = new Thread(this, "Experiment " + getName());
		experimentThread.start();
	}

	@Override
	public void stop() {
		if ( isOpen && experimentThread.isAlive() ) {
			commands.offer(_STOP);
		} else {
			stopExperiment();
		}
	}

	@Override
	public void pause() {
		commands.offer(_PAUSE);
	}

	@Override
	public void step() {
		commands.offer(_STEP);
	}

	@Override
	public void initialize() {
		commands.offer(_INIT);
	}

	@Override
	public void interrupt() {
		experimentThread.interrupt();
	}

	@Override
	public void reload() {
		commands.offer(_RELOAD);
	}

	@Override
	public void close() {
		// if ( experimentThread.isAlive() ) {
		// commands.offer(_CLOSE);
		// } else {
		GAMA.updateSimulationState(GAMA.NOTREADY);
		closeExperiment();
		GAMA.updateSimulationState();
		// }
	}

	@Override
	public void start() {
		commands.offer(_START);
	}

	public abstract void stopExperiment();

	public abstract void startExperiment() throws GamaRuntimeException;

	public abstract void stepExperiment();

	public abstract void reloadExperiment() throws GamaRuntimeException, GamlException,
		InterruptedException;

	public void closeExperiment() {
		closeCurrentSimulation(true);
		if ( output != null ) {
			output.dispose(true);
			output = null;
		}
		isOpen = false;
	}

	protected void pauseExperiment() {
		if ( currentSimulation != null && !isLoading() ) {
			currentSimulation.pause();
		}
	}

	protected Solution getCurrentSolution() throws GamaRuntimeException {
		return new Solution(targetedVars, false);
	}

	protected Double getCurrentSeed() {
		Object o = getParameter(ISymbol.SEED).getInitialValue();
		if ( o == null ) { return null; }
		if ( o instanceof Number ) { return ((Number) o).doubleValue(); }
		return null;
	}

	public void initializeExperiment() throws GamaRuntimeException, InterruptedException {
		initialize(getCurrentSolution(), getCurrentSeed());
	}

	@Override
	public void initialize(final Solution sol, final Double seed) throws InterruptedException,
		GamaRuntimeException {
		// GUI.debug("Beginning to initialize a new simulation");
		if ( currentSimulation != null ) { return; }
		parametersEditors = null;
		// GUI.debug("Setting the value of parameters from " + sol);

		for ( IParameter p : targetedVars.values() ) {
			String name = p.getName();
			if ( sol.containsKey(name) ) {
				p.setValue(sol.get(name));
				// TODO NECESSARY ?? Parameters have normally already been changed.
			}
		}
		// GUI.debug("Initializing the random agent");
		getParameter(ISymbol.SEED).setValue(seed);
		random = new RandomAgent(seed);
		// GUI.debug("Instanciating a new simulation");
		currentSimulation = new GamlSimulation(this, sol);
		// GUI.debug("Building the outputs of the new simulation");
		buildOutputs();
	}

	protected void buildOutputs() {
		if ( output == null ) {
			output = new OutputManager(null);
		}
		GUI.waitStatus(" Building outputs ");
		output.buildOutputs(this);
	}

	@Override
	public ModelEnvironment getModelEnvironment() {
		return model.getModelEnvironment();
	}

	@Override
	public EditorsList getParametersEditors() {
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
	public boolean isRunning() {
		return currentSimulation != null && currentSimulation.isAlive();
	}

	@Override
	public boolean isLoading() {
		return currentSimulation != null && currentSimulation.isLoading();
	}

	@Override
	public boolean isPaused() {
		return currentSimulation == null || currentSimulation.isPaused();
	}

	@Override
	public void reportError(final GamaRuntimeException g) {
		g.printStackTrace();
		final ErrorView v = GUI.showErrorView();
		if ( v != null ) {
			GUI.asyncRun(new Runnable() {

				@Override
				public void run() {
					v.addNewError(g);
				}
			});
		}
	}

	protected void closeCurrentSimulation(final boolean closingExperiment) {
		if ( currentSimulation == null ) { return; }
		currentSimulation.stop();
		currentSimulation.close();
		currentSimulation = null;
	}

	@Override
	public void startCurrentSimulation() throws GamaRuntimeException {
		if ( currentSimulation != null && currentSimulation.isPaused() ) {
			currentSimulation.start();
		}
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
			p.setValue(targetedVars.get(p.getName()).getInitialValue());
			targetedVars.put(p.getName(), p);
		}
	}

	protected IParameter.Batch checkGetParameter(final String name) throws GamaRuntimeException {
		IParameter.Batch v = getParameter(name);
		if ( v == null ) { throw new GamaRuntimeException("No parameter named " + name +
			" in experiment " + getName()); }
		return v;
	}

	@Override
	public boolean isOpen() {
		return isOpen;
	}

}
