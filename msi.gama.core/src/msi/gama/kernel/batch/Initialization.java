/*********************************************************************************************
 *
 * 'Initialization.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.kernel.batch;

import java.util.List;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;

public interface Initialization {

	public List<Chromosome> initializePop(IScope scope, List<IParameter.Batch> variables, GeneticAlgorithm algo) throws GamaRuntimeException;
}
