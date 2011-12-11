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
package msi.gaml.batch;

import java.util.List;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.kernel.experiment.BatchExperiment;

public abstract class LocalSearchAlgorithm extends ParamSpaceExploAlgorithm {

	protected Neighborhood	neighborhood;
	protected Solution		solutionInit;

	public LocalSearchAlgorithm(final IDescription species) {
		super(species);
	}

	@Override
	public void initializeFor(final BatchExperiment f) throws GamaRuntimeException {
		super.initializeFor(f);
		List<IParameter.Batch> v = f.getParametersToExplore();
		neighborhood = new Neighborhood1Var(v);
		solutionInit = new Solution(v, true);
	}

}
