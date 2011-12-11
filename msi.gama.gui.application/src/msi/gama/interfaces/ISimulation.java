/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
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