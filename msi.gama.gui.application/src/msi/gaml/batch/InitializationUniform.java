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
import msi.gama.interfaces.IParameter;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.kernel.experiment.BatchExperiment;

public class InitializationUniform implements Initialization {

	public InitializationUniform() {}

	@Override
	public List<Chromosome> initializePop(final List<IParameter.Batch> variables,
		final BatchExperiment exp, final int populationDim, final int nbPrelimGenerations,
		final boolean isMaximize) throws GamaRuntimeException {
		final Set<Chromosome> populationInit = new HashSet<Chromosome>();
		for ( int i = 0; i < nbPrelimGenerations; i++ ) {
			for ( int j = 0; j < populationDim; j++ ) {
				populationInit.add(new Chromosome(variables, true));
			}
		}
		for ( final Chromosome chromosome : populationInit ) {
			final Solution sol = chromosome.convertToSolution(variables);
			chromosome.setFitness(exp.launchSimulationsWithSolution(sol));
		}
		for ( final Chromosome chromosome1 : populationInit ) {
			final Solution sol = chromosome1.convertToSolution(variables);
			chromosome1.setFitness(exp.launchSimulationsWithSolution(sol));
		}
		final List<Chromosome> populationInitOrd = new ArrayList<Chromosome>(populationInit);
		Collections.sort(populationInitOrd);
		final List<Chromosome> populationInitFinal = new ArrayList<Chromosome>();
		if ( !isMaximize ) {
			for ( int i = 0; i < populationDim; i++ ) {
				populationInitFinal.add(populationInitOrd.get(i));
			}
		} else {
			for ( int i = populationInitOrd.size() - 1; i > populationInitOrd.size() -
				populationDim - 1; i-- ) {
				populationInitFinal.add(populationInitOrd.get(i));
			}
		}
		return populationInitFinal;
	}

}
