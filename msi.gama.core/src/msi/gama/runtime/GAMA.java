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
package msi.gama.runtime;

import msi.gama.common.util.*;
import msi.gama.kernel.experiment.IExperimentSpecies;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.*;

/**
 * Written by drogoul Modified on 23 nov. 2009
 * 
 * @todo Description
 */
public class GAMA {

	public final static String VERSION = "GAMA 1.6";

	public final static String PAUSED = "STOPPED";
	public final static String RUNNING = "RUNNING";
	public final static String NOTREADY = "NOTREADY";
	public final static String NONE = "NONE";
	public static final String _FATAL = "fatal";
	public static final String _WARNINGS = "warnings";

	public static boolean TREAT_ERRORS_AS_FATAL = true;
	public static boolean TREAT_WARNINGS_AS_ERRORS = false;

	private static volatile IExperimentSpecies currentExperiment = null;
	private static IExpressionFactory expressionFactory = null;
	public static ISimulationStateProvider state = null;

	public static void interruptLoading() {
		if ( currentExperiment != null ) {
			currentExperiment.userInterrupt();
		}
	}

	public static void newExperiment(final String id, final IModel model) {
		final IExperimentSpecies newExperiment = model.getExperiment(id);
		if ( newExperiment == currentExperiment && currentExperiment != null ) {
			currentExperiment.userReload();
			return;
		}
		// TODO if newExperiment.isGui() ...
		if ( currentExperiment != null ) {
			IModel m = currentExperiment.getModel();
			if ( !m.getFilePath().equals(model.getFilePath()) ) {
				if ( !verifyClose() ) { return; }
				closeCurrentExperiment();
				m.dispose();
			} else if ( !id.equals(currentExperiment.getName()) ) {
				if ( !verifyClose() ) { return; }
				closeCurrentExperiment();
			} else {
				if ( !verifyClose() ) { return; }
				closeCurrentExperiment();
			}
		}
		currentExperiment = newExperiment;
		currentExperiment.userOpen();
		currentExperiment.userInit();

	}

	public static void closeCurrentExperiment() {
		if ( currentExperiment != null ) {
			currentExperiment.userClose();
			currentExperiment = null;
		}
	}

	public static void closeCurrentExperimentOnException(GamaRuntimeException e) {
		GuiUtils.errorStatus(e.getMessage());
		GuiUtils.runtimeError(e);
		closeCurrentExperiment();
	}

	public static void updateSimulationState() {
		updateSimulationState(getFrontmostSimulationState());
	}

	public static void updateSimulationState(final String forcedState) {
		if ( state != null ) {
			GuiUtils.run(new Runnable() {

				@Override
				public void run() {
					state.updateStateTo(forcedState);
				}
			});
		}
	}

	public static void stepExperiment() {
		if ( currentExperiment != null ) {
			currentExperiment.userStep();
		}
	}

	public static void startOrPauseExperiment() {
		if ( currentExperiment == null ) {
			return;
		} else if ( !currentExperiment.isRunning() || currentExperiment.isPaused() ) {
			currentExperiment.userStart();
		} else {
			currentExperiment.userPause();
		}
	}

	private static boolean verifyClose() {
		if ( currentExperiment == null ) { return true; }
		currentExperiment.userPause();
		return GuiUtils.confirmClose(currentExperiment);
	}

	public static ISimulationAgent getFrontmostSimulation() {
		if ( currentExperiment == null ) { return null; }
		return currentExperiment.getCurrentSimulation();
	}

	public static IExperimentSpecies getExperiment() {
		return currentExperiment;
	}

	public static IScope getDefaultScope() {
		if ( currentExperiment == null ) { return null; }
		return currentExperiment.getExperimentScope();
	}

	public static SimulationClock getClock() {
		SimulationClock clock = currentExperiment == null ? null : currentExperiment.getExperimentScope().getClock();
		return clock == null ? new SimulationClock() : clock;
	}

	public static RandomUtils getRandom() {
		if ( currentExperiment == null || currentExperiment.getAgent() == null ) { return RandomUtils.getDefault(); }
		return currentExperiment.getAgent().getRandomGenerator();
	}

	public static IModel getModel() {
		if ( currentExperiment == null ) { return null; }
		return currentExperiment.getModel();
	}

	public static String getFrontmostSimulationState() {
		return currentExperiment == null ? NONE : currentExperiment.isLoading() ? NOTREADY : currentExperiment
			.isPaused() ? PAUSED : RUNNING;
	}

	public static IExpressionFactory getExpressionFactory() {
		if ( expressionFactory == null ) {
			expressionFactory = new GamlExpressionFactory();
		}
		return expressionFactory;
	}

	public static void reportError(final GamaRuntimeException g) {
		GuiUtils.runtimeError(g);
		if ( currentExperiment == null ) { return; }
		if ( TREAT_ERRORS_AS_FATAL ) {
			if ( TREAT_WARNINGS_AS_ERRORS || !g.isWarning() ) {
				currentExperiment.userPause();
			}
		}
	}

	public static Object evaluateExpression(final String expression, final IAgent a) throws GamaRuntimeException {
		if ( a == null ) { return null; }
		final IExpression expr = compileExpression(expression, a);
		if ( expr == null ) { return null; }
		return getDefaultScope().evaluate(expr, a);
	}

	public static IExpression compileExpression(final String expression, final IAgent agent)
		throws GamaRuntimeException {
		return getExpressionFactory().createExpr(expression, agent.getSpecies().getDescription());

	}

	public static void releaseScope(final IScope scope) {
		if ( currentExperiment == null || currentExperiment.getCurrentSimulation() == null ) { return; }
		currentExperiment.getAgent().releaseScope(scope);

	}

	public static IScope obtainNewScope() {
		if ( currentExperiment == null || currentExperiment.getCurrentSimulation() == null ) { return null; }
		return currentExperiment.getAgent().obtainNewScope();
	}

	public final static String SIMULATION_RUNNING_STATE = "msi.gama.application.commands.SimulationRunningState";

	/**
	 * @return
	 */
	public static IDescription getModelContext() {
		if ( currentExperiment == null ) { return null; }
		return currentExperiment.getModel().getDescription();
	}

}
