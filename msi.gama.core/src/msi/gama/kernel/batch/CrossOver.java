/*********************************************************************************************
 * 
 * 
 * 'CrossOver.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.kernel.batch;

import java.util.Set;
import msi.gama.runtime.IScope;

public interface CrossOver {

	public Set<Chromosome> crossOver(IScope scope, final Chromosome parent1, final Chromosome parent2);
}
