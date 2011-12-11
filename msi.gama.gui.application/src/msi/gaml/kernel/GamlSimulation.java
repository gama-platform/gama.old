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
package msi.gaml.kernel;

import java.util.*;
import msi.gama.interfaces.IAgent;
import msi.gama.kernel.AbstractSimulation;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.kernel.experiment.IExperiment;
import msi.gama.util.GamaList;
import msi.gaml.batch.Solution;
import msi.gaml.kernel.GamlPopulation.WorldPopulation;

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