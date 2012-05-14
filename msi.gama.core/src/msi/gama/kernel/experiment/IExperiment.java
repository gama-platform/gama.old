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

import msi.gama.common.interfaces.ItemList;
import msi.gama.common.util.RandomUtils;
import msi.gama.kernel.experiment.AbstractExperiment.ExperimentatorPopulation.ExperimentatorAgent;
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.ISimulation;
import msi.gama.outputs.IOutputManager;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.species.ISpecies;

/**
 * Written by drogoul Modified on 31 mai 2011
 * 
 * @todo Description
 * 
 */
public interface IExperiment extends ISpecies {

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

	public abstract IModel getModel();

	public abstract void setModel(final IModel model);

	public abstract ISimulation getCurrentSimulation();

	public abstract IOutputManager getOutputManager();

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

	public abstract void initialize(ParametersSet sol, Double seed) throws GamaRuntimeException,
		InterruptedException;

	public abstract boolean isGui();

	public abstract RandomUtils getRandomGenerator();

	public abstract ItemList getParametersEditors();

	public abstract boolean isBatch();

	public abstract IList<? extends IParameter> getParametersToDisplay();

	public abstract void reportError(GamaRuntimeException g);

	public abstract void setParameterValue(String name, Object v) throws GamaRuntimeException;

	public abstract boolean hasParameter(String name);

	public abstract Object getParameterValue(final String name) throws GamaRuntimeException;

	public abstract IList<String> getParametersNames();

	public abstract boolean hasParameters();

	public abstract void startCurrentSimulation() throws GamaRuntimeException;

	public abstract IScope getExperimentScope();

	public abstract boolean isOpen();

	void interrupt();

	public ExperimentatorAgent getAgent();

}