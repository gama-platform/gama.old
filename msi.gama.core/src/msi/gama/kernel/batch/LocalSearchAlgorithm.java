/*********************************************************************************************
 *
 *
 * 'LocalSearchAlgorithm.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.kernel.batch;

import java.util.List;

import msi.gama.kernel.experiment.BatchAgent;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;

public abstract class LocalSearchAlgorithm extends ParamSpaceExploAlgorithm {

	protected Neighborhood neighborhood;
	protected ParametersSet solutionInit;

	public LocalSearchAlgorithm(final IDescription species) {
		super(species);
	}

	@Override
	public void initializeFor(final IScope scope, final BatchAgent agent) throws GamaRuntimeException {
		super.initializeFor(scope, agent);
		final List<IParameter.Batch> v = agent.getParametersToExplore();
		neighborhood = new Neighborhood1Var(v);
		solutionInit = new ParametersSet(scope, v, true);
	}

}
