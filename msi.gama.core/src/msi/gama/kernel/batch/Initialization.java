/*********************************************************************************************
 * 
 *
 * 'Initialization.java', in plugin 'msi.gama.core', is part of the source code of the 
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
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public interface Initialization {

	public List<Chromosome> initializePop(IScope scope, List<IParameter.Batch> variables, BatchAgent exp,
			int populationDim, int nbPrelimGenerations, boolean isMaximize) throws GamaRuntimeException;
}
