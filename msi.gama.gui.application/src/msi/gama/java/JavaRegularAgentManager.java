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

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.kernel.AbstractPopulation;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;

public class JavaRegularAgentManager extends AbstractPopulation {

	/**
	 * @param species
	 */
	public JavaRegularAgentManager(final IAgent macroAgent, final ISpecies species) {
		super(macroAgent, species);
	}

	@Override
	public void createVariablesFor(final IScope scope, final List<? extends IAgent> agents,
		final List<Map<String, Object>> initialValues) throws GamaRuntimeException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.IPopulation#computeAgentsToSchedule(msi.gama.interfaces.IScope,
	 * msi.gama.util.GamaList)
	 */
	@Override
	public void computeAgentsToSchedule(final IScope scope, final GamaList list)
		throws GamaRuntimeException {
		// TODO Auto-generated method stub

	}

}
