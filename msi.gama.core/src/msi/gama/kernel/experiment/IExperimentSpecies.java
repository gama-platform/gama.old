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
import msi.gama.kernel.model.IModel;
import msi.gama.kernel.simulation.SimulationAgent;
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
public interface IExperimentSpecies extends ISpecies {

	static final String BATCH_CATEGORY_NAME = "Exploration method";
	static final String EXPLORABLE_CATEGORY_NAME = "Parameters to explore";
	static final String FIXED_CATEGORY_NAME = "Fixed parameters";
	static final String SYSTEM_CATEGORY_PREFIX = "System parameters for experiment";

	public abstract IModel getModel();

	public void desynchronizeOutputs();

	public abstract void setModel(final IModel model);

	public abstract IOutputManager getOutputManager();

	public abstract boolean isBatch();

	public abstract boolean isGui();

	public abstract ItemList getParametersEditors();

	public abstract IList<? extends IParameter> getParametersToDisplay();

	public abstract void setParameterValue(String name, Object v) throws GamaRuntimeException;

	public abstract boolean hasParameter(String name);

	public abstract Object getParameterValue(final String name) throws GamaRuntimeException;

	public abstract IList<String> getParametersNames();

	public abstract boolean hasParameters();

	public ExperimentAgent getAgent();

	public abstract IScope getExperimentScope();

	public abstract ParametersSet getCurrentSolution();

	public abstract Double getCurrentSeed();

	public abstract void open();

	public abstract void reload();

	public abstract void schedule();

	public abstract void close();

	public abstract SimulationAgent getCurrentSimulation();

	public abstract boolean isLoading();

	public abstract void buildOutputs();

}