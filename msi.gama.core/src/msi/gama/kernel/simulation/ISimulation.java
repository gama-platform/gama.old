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
package msi.gama.kernel.simulation;

import msi.gama.kernel.experiment.*;
import msi.gama.kernel.model.IModel;
import msi.gama.metamodel.agent.WorldAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Written by drogoul Modified on 18 aožt 2010
 * 
 * @todo Description
 * 
 */
public interface ISimulation {

	public abstract IScheduler getScheduler();

	public abstract WorldAgent getWorld();

	public abstract String getName();

	public abstract boolean isAlive();

	public abstract boolean isPaused();

	public abstract boolean isBatch();

	public abstract void pause();

	public abstract void stop();

	public abstract void step();

	public abstract void start() throws GamaRuntimeException;

	public abstract void close();

	public abstract void dispose();

	public abstract IPopulation getWorldPopulation();

	public abstract IScope getGlobalScope();

	public abstract boolean isLoading();

	public abstract IModel getModel();

	public abstract IScope getExecutionScope();

	public abstract void releaseScope(IScope scope);

	public abstract IScope obtainNewScope();

	public abstract void initialize(ParametersSet parameters) throws GamaRuntimeException,
		InterruptedException;

	public abstract IExperiment getExperiment();
}