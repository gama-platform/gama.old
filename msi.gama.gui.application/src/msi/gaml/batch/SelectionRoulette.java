/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
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

import java.util.*;
import msi.gama.kernel.GAMA;

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
