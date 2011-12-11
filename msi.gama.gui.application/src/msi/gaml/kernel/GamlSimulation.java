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
package msi.gaml.kernel;

import java.util.*;
import msi.gama.interfaces.IAgent;
import msi.gama.kernel.AbstractSimulation;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.kernel.experiment.IExperiment;
import msi.gama.util.GamaList;
import msi.gaml.agents.IGamlPopulation;
import msi.gaml.agents.GamlPopulation.WorldPopulation;
import msi.gaml.batch.Solution;

public class GamlSimulation extends AbstractSimulation {

	public GamlSimulation(final IExperiment exp, final Solution parameters)
		throws GamaRuntimeException, InterruptedException {
		super(exp, parameters);
	}

	@Override
	protected void initializeWorldPopulation() {
		worldPopulation = new WorldPopulation(getModel().getWorldSpecies());
	}

	@Override
	protected void initializeWorld(final Map<String, Object> parameters)
		throws GamaRuntimeException, InterruptedException {
		IGamlPopulation g = (IGamlPopulation) getWorldPopulation();
		g.initializeFor(getGlobalScope());
		List<? extends IAgent> newAgents =
			g.createAgents(getGlobalScope(), 1, GamaList.with(parameters), false);
		IAgent world = newAgents.get(0);
		world.schedule();
		world.initializeMicroPopulations(getGlobalScope());
	}
}