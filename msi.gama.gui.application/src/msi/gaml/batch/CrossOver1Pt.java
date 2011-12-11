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
package msi.gaml.batch;

import java.util.*;
import msi.gama.kernel.GAMA;

public class CrossOver1Pt implements CrossOver {

	public CrossOver1Pt() {}

	@Override
	public Set<Chromosome> crossOver(final Chromosome parent1, final Chromosome parent2) {
		final Set<Chromosome> children = new HashSet<Chromosome>();
		final int nbGenes = parent2.getGenes().length;
		if ( nbGenes == 1 ) { return children; }
		int cutPt = 0;
		if ( nbGenes > 2 ) {
			cutPt = GAMA.getRandom().between(0, nbGenes - 2);
		}
		final Chromosome child1 = new Chromosome(parent1);
		final Chromosome child2 = new Chromosome(parent2);
		for ( int i = 0; i < cutPt; i++ ) {
			final double val1 = child1.getGenes()[i];
			child1.getGenes()[i] = child2.getGenes()[i];
			child2.getGenes()[i] = val1;
		}
		children.add(child1);
		children.add(child2);
		return children;
	}

}
