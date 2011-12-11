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
package msi.gama.kernel.stack;

import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;

public class ExecutionStack extends AbstractStack {

	final ISimulation simulation;
	IAgent world;

	public ExecutionStack(final ISimulation sim) {
		simulation = sim;
		// world = simulation.getWorldScope();
	}

	@Override
	public final ISimulation getSimulationScope() {
		return simulation;
	}

	@Override
	public final IAgent getWorldScope() {
		if ( world == null ) {
			world = simulation.getWorld();
		}
		return world;
	}

	@Override
	public Object getGlobalVarValue(final String name) throws GamaRuntimeException {
		return getWorldScope().getDirectVarValue(name);
	}

	@Override
	public void setGlobalVarValue(final String name, final Object v) throws GamaRuntimeException {
		getWorldScope().setDirectVarValue(this, name, v);
	}

}
