/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.kernel.batch;

import java.util.*;
import msi.gama.runtime.GAMA;

public class SelectionRoulette implements Selection {

	public SelectionRoulette() {}

	@Override
	public List<Chromosome> select(final List<Chromosome> population, final int populationDim,
		final boolean maximize) {

		final List<Chromosome> nextGen = new ArrayList<Chromosome>();
		double fitnessTotal = 0;
		for ( final Chromosome chromosome : population ) {
			if ( maximize ) {
				fitnessTotal += chromosome.getFitness();
			} else {
				fitnessTotal += 1.0 / chromosome.getFitness();
			}
		}
		// TODO Infinite Loop problem here ???
		while (nextGen.size() < populationDim) {
			final double rand = GAMA.getRandom().next();
			double fitnessSum = 0;
			for ( final Chromosome chromosome : population ) {
				if ( maximize ) {
					fitnessSum += chromosome.getFitness() / fitnessTotal;
				} else {
					fitnessSum += 1.0 / chromosome.getFitness() / fitnessTotal;
				}
				if ( fitnessSum >= rand ) {
					nextGen.add(chromosome);
					break;
				}
			}
		}

		return nextGen;
	}

}
