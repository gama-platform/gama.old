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
package msi.gama.java;

import java.util.*;
import msi.gama.agents.AbstractPopulation;
import msi.gama.interfaces.*;
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
