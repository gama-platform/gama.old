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
import msi.gama.kernel.experiment.IExperiment;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.*;
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

	private static volatile IExperiment currentExperiment = null;
	private static IExpressionFactory expressionFactory = null;
	public static ISimulationStateProvider state = null;

	public static void interruptLoading() {
		if ( currentExperiment != null ) {
			currentExperiment.interrupt();
		}
	}

	public static void newExperiment(final String id, final IModel model) {

		final IExperiment newExperiment = model.getExperiment(id);
		if ( newExperiment == currentExperiment && currentExperiment != null ) {
			currentExperiment.reload();
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
		currentExperiment.open();
		currentExperiment.initialize();

	}

	public static void closeCurrentExperiment() {
		if ( currentExperiment != null ) {
			currentExperiment.close();
			currentExperiment = null;
		}
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
			currentExperiment.step();
		}
	}

	public static void startOrPauseExperiment() {
		if ( currentExperiment == null ) {
			return;
		} else if ( !currentExperiment.isRunning() || currentExperiment.isPaused() ) {
			currentExperiment.start();
		} else {
			currentExperiment.pause();
		}
	}

	private static boolean verifyClose() {
		if ( currentExperiment == null ) { return true; }
		currentExperiment.pause();
		return GuiUtils.confirmClose(currentExperiment);
	}

	public static ISimulation getFrontmostSimulation() {
		if ( currentExperiment == null ) { return null; }
		return currentExperiment.getCurrentSimulation();
	}

	public static IExperiment getExperiment() {
		return currentExperiment;
	}

	public static IScope getDefaultScope() {
		if ( currentExperiment == null ) { return null; }
		return currentExperiment.getExperimentScope();
	}

	public static RandomUtils getRandom() {
		if ( currentExperiment == null ) { return RandomUtils.getDefault(); }
		return currentExperiment.getRandomGenerator();
	}

	public static IModel getModel() {
		if ( currentExperiment == null ) { return null; }
		return currentExperiment.getModel();
	}

	public static String getFrontmostSimulationState() {
		return currentExperiment == null ? NONE : currentExperiment.isLoading() ? NOTREADY
			: currentExperiment.isPaused() ? PAUSED : RUNNING;
	}

	public static IExpressionFactory getExpressionFactory() {
		if ( expressionFactory == null ) {
			expressionFactory = new GamlExpressionFactory();
		}
		return expressionFactory;
	}

	/**
	 * @param g
	 */
	public static void reportError(final GamaRuntimeException g) {
		if ( currentExperiment == null ) { return; }
		currentExperiment.reportError(g);
		if ( SimulationClock.TREAT_ERRORS_AS_FATAL ) {
			if ( SimulationClock.TREAT_WARNINGS_AS_ERRORS || !g.isWarning() ) {
				currentExperiment.pause();
			}
		}
	}

	public static Object evaluateExpression(final String expression, final IAgent a)
		throws GamaRuntimeException {
		try {
			if ( a == null ) { return null; }
			final IExpression expr =
				getExpressionFactory().createExpr(new StringBasedExpressionDescription(expression),
					a.getSpecies().getDescription());
			if ( expr == null ) { return null; }
			return getDefaultScope().evaluate(expr, a);
		} catch (final GamaRuntimeException e) {
			e.addContext("in evaluating :" + expression);
			return e;
		}
	}

	public static IExpression compileExpression(final String expression, final IAgent agent)
		throws GamaRuntimeException {
		try {
			return GAMA.getExpressionFactory().createExpr(
				new StringBasedExpressionDescription(expression),
				agent.getSpecies().getDescription());
		} catch (final GamaRuntimeException e) {
			e.addContext("in compiling :" + expression);
			throw e;
		}
	}

	// private static IDisplayOutput getOutput(final String outputName) {
	// IOutputManager man = getExperiment().getOutputManager();
	// if ( man == null ) { return null; }
	// IOutput out = man.getOutput(outputName);
	// if ( out == null || !(out instanceof IDisplayOutput) ) { return null; }
	// return (IDisplayOutput) out;
	// }
	//
	// public static BufferedImage getImage(final String outputName) {
	// IDisplayOutput out = getOutput(outputName);
	// return out == null ? null : out.getImage();
	// }
	//
	// public static BufferedImage getImage(final String outputName, final int width, final int
	// height) {
	// IDisplayOutput out = getOutput(outputName);
	// IDisplaySurface surface = out.getSurface();
	// surface.resizeImage(width, height);
	// surface.updateDisplay();
	// return surface.getImage();
	// }
	//
	// public static void getImage(final String outputName, final Image im) {
	// if ( im == null ) { return; }
	// IDisplayOutput out = getOutput(outputName);
	// IDisplaySurface surface = out.getSurface();
	// surface.resizeImage(im.getWidth(null), im.getHeight(null));
	// surface.updateDisplay();
	// Graphics2D g2d = (Graphics2D) im.getGraphics();
	// g2d.drawImage(surface.getImage(), 0, 0, null);
	// g2d.dispose();
	// }

	public static void releaseScope(final IScope scope) {
		if ( currentExperiment == null || currentExperiment.getCurrentSimulation() == null ) { return; }
		currentExperiment.getCurrentSimulation().releaseScope(scope);

	}

	public static IScope obtainNewScope() {
		if ( currentExperiment == null || currentExperiment.getCurrentSimulation() == null ) { return null; }
		return currentExperiment.getCurrentSimulation().obtainNewScope();
	}

	public final static String SIMULATION_RUNNING_STATE =
		"msi.gama.application.commands.SimulationRunningState";

	/**
	 * @return
	 */
	public static IDescription getModelContext() {
		if ( currentExperiment == null ) { return null; }
		return currentExperiment.getModel().getDescription();
	}

}
