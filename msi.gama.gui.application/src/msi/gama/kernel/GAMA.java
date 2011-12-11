/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.kernel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.Semaphore;
import msi.gama.gui.application.GUI;
import msi.gama.gui.application.commands.SimulationStateProvider;
import msi.gama.gui.graphics.IDisplaySurface;
import msi.gama.interfaces.*;
import msi.gama.internal.compilation.*;
import msi.gama.internal.expressions.*;
import msi.gama.kernel.exceptions.*;
import msi.gama.kernel.experiment.IExperiment;
import msi.gama.outputs.*;
import msi.gama.util.RandomAgent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.ISourceProvider;

/**
 * Written by drogoul Modified on 23 nov. 2009
 * 
 * @todo Description
 */
public class GAMA {

	public final static String			PAUSED									= "STOPPED";
	public final static String			RUNNING									= "RUNNING";
	public final static String			NOTREADY								= "NOTREADY";
	public final static String			NONE									= "NONE";

	public static boolean				TREAT_ERRORS_AS_FATAL					= false;
	public static boolean				TREAT_WARNINGS_AS_ERRORS				= false;
	public static boolean				USE_DISTANCE_CACHE						= false;
	public static final String			_UDC									=
																					"USE_DISTANCE_CACHE";
	public static boolean				USE_QUALITY_RENDERING					= false;
	public static final String			_UQR									=
																					"USE_QUALITY_RENDERING";
	public static final String			_FATAL									= "fatal";
	public static final String			_WARNINGS								= "warnings";
	public final static Semaphore		OUTPUT_AUTHORIZATION					= new Semaphore(1);
	public final static Semaphore		SCHEDULER_AUTHORIZATION					= new Semaphore(1);
	public final static Semaphore		OUTPUT_FINISHED							= new Semaphore(1,
																					true);
	public static final Object			SPATIAL_REORGANIZATION_AUTHORIZATION	= new Object();

	private static volatile IExperiment	currentExperiment						= null;
	public static ISourceProvider		state									= null;

	public static void interruptLoading() {
		if ( currentExperiment != null ) {
			currentExperiment.interrupt();
		}
	}

	public static void newExperiment(final String id, final IModel model) {
		final IExperiment newExperiment = model.getExperiment(id);
		// TODO if newExperiment.isGui() ...
		GUI.openSimulationPerspective();
		if ( currentExperiment != null ) {
			IModel m = currentExperiment.getModel();
			if ( !m.getFileName().equals(model.getFileName()) ) {
				if ( !verifyClose() ) { return; }
				closeCurrentExperiment();
				m.dispose();
			} else if ( !id.equals(currentExperiment.getName()) ) {
				if ( !verifyClose() ) { return; }
				closeCurrentExperiment();
			} else {
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
			GUI.run(new Runnable() {

				@Override
				public void run() {
					((SimulationStateProvider) state).updateStateTo(forcedState);
				}
			});
		}
	}

	public static void stepExperiment() {
		if ( currentExperiment != null ) {
			currentExperiment.step();
		}
	}

	public static void startOrPauseExperiment(final ISourceProvider state) {
		if ( currentExperiment == null ) {
			return;
		} else if ( !currentExperiment.isRunning() || currentExperiment.isPaused() ) {
			startExperiment();
		} else {
			pauseExperiment();
		}
	}

	public static void reloadExperiment() {
		if ( currentExperiment != null ) {
			currentExperiment.reload();
		}
	}

	private static void startExperiment() {
		currentExperiment.start();
	}

	private static void pauseExperiment() {
		currentExperiment.pause();
	}

	private static boolean verifyClose() {
		pauseExperiment();
		return MessageDialog.openQuestion(GUI.getShell(), "Close simulation confirmation",
			"Do you want to close experiment '" + currentExperiment.getName() + "' of model '" +
				currentExperiment.getModel().getName() + "' ?");
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

	public static RandomAgent getRandom() {
		if ( currentExperiment == null ) { return RandomAgent.getDefault(); }
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
		if ( currentExperiment != null ) { return currentExperiment.getExpressionFactory(); }
		return null;
	}

	/**
	 * @param g
	 */
	public static void reportError(final GamaRuntimeException g) {
		if ( currentExperiment == null ) { return; }
		currentExperiment.reportError(g);
		if ( TREAT_ERRORS_AS_FATAL ) {
			if ( !g.isWarning() ) {
				pauseExperiment();
			}
		}
	}

	public static Object evaluateExpression(final String expression) throws GamlException,
		GamaRuntimeException {
		if ( currentExperiment == null ) { throw new GamaRuntimeException("No experiment running"); }
		try {
			IExpression expr = compileExpression(expression);
			if ( expr == null ) { return null; }
			return expr.value(getDefaultScope());
		} catch (final GamaRuntimeException e) {
			e.addContext("in evaluating :" + expression);
			throw e;
		}
	}

	public static Object evaluateExpression(final String expression, final IAgent a)
		throws GamlException, GamaRuntimeException {
		try {
			final IExpression expr =
				getExpressionFactory().createExpr(new ExpressionDescription(expression),
					a.getSpecies().getDescription());
			if ( expr == null ) { return null; }
			return getDefaultScope().evaluate(expr, a);
		} catch (final GamaRuntimeException e) {
			e.addContext("in evaluating :" + expression);
			throw e;
		} catch (final GamlException e) {
			e.addContext("in compiling :" + expression);
			throw e;
		}
	}

	public static IExpression compileExpression(final String expression) throws GamlException {
		try {
			return GAMA.getExpressionFactory().createExpr(new ExpressionDescription(expression),
				currentExperiment.getModel().getDescription());
		} catch (final GamlException e) {
			e.addContext("in compiling :" + expression);
			throw e;
		}
	}

	public static IExpression compileExpression(final String expression, final IAgent agent)
		throws GamlException {
		try {
			return GAMA.getExpressionFactory().createExpr(new ExpressionDescription(expression),
				agent.getSpecies().getDescription());
		} catch (final GamlException e) {
			e.addContext("in compiling :" + expression);
			throw e;
		}
	}

	private static IDisplayOutput getOutput(final String outputName) {
		OutputManager man = getExperiment().getOutputManager();
		if ( man == null ) { return null; }
		IOutput out = man.getOutput(outputName);
		if ( out == null || !(out instanceof LayerDisplayOutput) ) { return null; }
		return (LayerDisplayOutput) out;
	}

	public static BufferedImage getImage(final String outputName) {
		IDisplayOutput out = getOutput(outputName);
		return out == null ? null : out.getImage();
	}

	public static BufferedImage getImage(final String outputName, final int width, final int height) {
		IDisplayOutput out = getOutput(outputName);
		IDisplaySurface surface = out.getSurface();
		surface.resizeImage(width, height);
		surface.updateDisplay();
		return surface.getImage();
	}

	public static void getImage(final String outputName, final Image im) {
		if ( im == null ) { return; }
		IDisplayOutput out = getOutput(outputName);
		IDisplaySurface surface = out.getSurface();
		surface.resizeImage(im.getWidth(null), im.getHeight(null));
		surface.updateDisplay();
		Graphics2D g2d = (Graphics2D) im.getGraphics();
		g2d.drawImage(surface.getImage(), 0, 0, null);
		g2d.dispose();
	}

	public static void releaseScope(final IScope scope) {
		if ( currentExperiment == null || currentExperiment.getCurrentSimulation() == null ) { return; }
		currentExperiment.getCurrentSimulation().getScheduler().releaseStack(scope);

	}

	public static IScope obtainNewScope() {
		if ( currentExperiment == null || currentExperiment.getCurrentSimulation() == null ) { return null; }
		return currentExperiment.getCurrentSimulation().getScheduler().obtainNewStack();
	}

}
