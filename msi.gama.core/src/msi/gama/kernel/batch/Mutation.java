/*********************************************************************************************
 *
 * 'Mutation.java, in plugin msi.gama.core, is part of the source code of the
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

public interface Mutation {

	public Chromosome mutate(IScope scope, Chromosome chromosome, List<IParameter.Batch> variables);

}
