/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.kernel.simulation;

import java.util.concurrent.Semaphore;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.IScheduledAction;

/**
 * The class IGlobalScheduler.
 * 
 * @author drogoul
 * @since 14 déc. 2011
 * 
 */
public interface IScheduler extends IStepable, Runnable {

	public final static Semaphore SCHEDULER_AUTHORIZATION = new Semaphore(1);

	public abstract void insertAgentToInit(final IAgent entity) throws GamaRuntimeException;

	public abstract IScheduledAction insertBeginAction(final Object target, final String method);

	public abstract IScheduledAction insertEndAction(final Object target, final String method);

	public abstract void removeAction(final IScheduledAction haltAction);

	public abstract boolean inInitSequence();

	public abstract void start();

	public abstract void stepByStep();

	public abstract void dispose();

	public abstract void insertEndAction(IScheduledAction action);

}
