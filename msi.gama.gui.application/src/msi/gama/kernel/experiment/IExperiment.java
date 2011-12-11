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
package msi.gama.kernel.experiment;

import java.util.List;
import msi.gama.environment.ModelEnvironment;
import msi.gama.gui.parameters.EditorsList;
import msi.gama.interfaces.*;
import msi.gama.internal.expressions.IExpressionFactory;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.outputs.OutputManager;
import msi.gama.util.RandomAgent;
import msi.gaml.batch.Solution;

/**
 * Written by drogoul Modified on 31 mai 2011
 * 
 * @todo Description
 * 
 */
public interface IExperiment extends ISymbol {

	static final String BATCH_CATEGORY_NAME = "Exploration method";
	static final String EXPLORABLE_CATEGORY_NAME = "Parameters to explore";
	static final String FIXED_CATEGORY_NAME = "Fixed parameters";
	static final String SYSTEM_CATEGORY_PREFIX = "System parameters for experiment";

	static final int _INIT = 0;
	static final int _START = 1;
	static final int _STEP = 2;
	static final int _PAUSE = 3;
	static final int _STOP = 4;
	static final int _CLOSE = 5;
	static final int _RELOAD = 6;
	static final int _NEXT = 7;

	public abstract IExpressionFactory getExpressionFactory();

	public abstract IModel getModel();

	public abstract void setModel(final IModel model);

	public abstract ISimulation getCurrentSimulation();

	public abstract OutputManager getOutputManager();

	public abstract boolean isRunning();

	public abstract boolean isLoading();

	public abstract boolean isPaused();

	public abstract void open();

	public abstract void stop();

	public abstract void start();

	public abstract void pause();

	public abstract void step();

	public abstract void close();

	public abstract void reload();

	public abstract void initialize();

	public abstract void initialize(Solution sol, Double seed) throws GamaRuntimeException,
		InterruptedException;

	public abstract boolean isGui();

	public abstract ModelEnvironment getModelEnvironment();

	public abstract RandomAgent getRandomGenerator();

	public abstract EditorsList getParametersEditors();

	public abstract boolean isBatch();

	public abstract List<? extends IParameter> getParametersToDisplay();

	public abstract void reportError(GamaRuntimeException g);

	public abstract void setParameterValue(String name, Object v) throws GamaRuntimeException;

	public abstract boolean hasParameter(String name);

	public abstract Object getParameterValue(final String name) throws GamaRuntimeException;

	public abstract List<String> getParametersNames();

	public abstract boolean hasParameters();

	public abstract void startCurrentSimulation() throws GamaRuntimeException;

	public abstract IScope getExperimentScope();

	public abstract boolean isOpen();

	void interrupt();

}