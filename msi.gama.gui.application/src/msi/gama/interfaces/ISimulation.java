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
package msi.gama.interfaces;

import msi.gama.environment.ITopology;
import msi.gama.kernel.Scheduler;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;

/**
 * Written by drogoul Modified on 18 aožt 2010
 * 
 * @todo Description
 * 
 */
public interface ISimulation {

	public abstract Scheduler getScheduler();

	public abstract IAgent getWorld();

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

	public abstract GamaList<IAgent> getAllAgents();

	public abstract IScope getGlobalScope();

	public abstract boolean isLoading();

	public abstract IModel getModel();
}