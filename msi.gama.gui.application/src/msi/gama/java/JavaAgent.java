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
package msi.gama.java;

import msi.gama.agents.AbstractAgent;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;

public abstract class JavaAgent extends AbstractAgent {

	/**
	 * @param sim
	 */
	public JavaAgent(final ISimulation sim, final IPopulation m) {
		super(sim, m);
		// TODO Auto-generated constructor stub
	}

	@Override
	public abstract void init(IScope scope);

	@Override
	public boolean isGridAgent() {
		return false;
	}

	@Override
	public abstract void step(IScope scope);

	public final void move() {
		// manager.run("wander", this);
	}

}
