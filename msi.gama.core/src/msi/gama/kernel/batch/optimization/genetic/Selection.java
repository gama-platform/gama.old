/*******************************************************************************************************
 *
 * msi.gama.kernel.batch.Selection.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 * 
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.kernel.batch;

import java.util.List;
import msi.gama.runtime.IScope;

public interface Selection {

	public List<Chromosome> select(IScope scope, final List<Chromosome> population, final int populationDim,
		boolean maximize);
}
