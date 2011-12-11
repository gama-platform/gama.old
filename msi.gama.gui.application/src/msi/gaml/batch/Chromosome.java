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

import java.util.List;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;

public class Chromosome implements Comparable<Chromosome> {

	private double[] genes;
	private final String[] phenotype;
	private double fitness;

	public double[] getGenes() {
		return genes;
	}

	public void setGenes(final double[] genes) {
		this.genes = genes;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(final double fitness) {
		this.fitness = fitness;
	}

	public Chromosome(final Chromosome chromosome) {

		genes = new double[chromosome.genes.length];
		phenotype = new String[chromosome.phenotype.length];

		for ( int i = 0; i < genes.length; i++ ) {
			genes[i] = chromosome.genes[i];
			phenotype[i] = chromosome.phenotype[i];
		}

		fitness = chromosome.fitness;
	}

	public Chromosome(final List<IParameter.Batch> variables, final boolean reInitVal) {
		genes = new double[variables.size()];
		phenotype = new String[variables.size()];
		int cpt = 0;
		for ( final IParameter.Batch var : variables ) {
			if ( reInitVal ) {
				var.reinitRandomly();
			}
			phenotype[cpt] = var.getName();
			if ( var.type().id() == IType.FLOAT ) {
				genes[cpt] = ((Double) var.value()).doubleValue();
			} else if ( var.type().id() == IType.INT ) {
				genes[cpt] = ((Integer) var.value()).doubleValue();
			} else {
				genes[cpt] = 0;
			}
			cpt++;
		}
	}

	public void setGene(final IParameter.Batch var, final int index) {
		if ( var.type().id() == IType.FLOAT ) {
			genes[index] = ((Double) var.value()).doubleValue();
		} else if ( var.type().id() == IType.INT ) {
			genes[index] = ((Integer) var.value()).doubleValue();
		} else {
			genes[index] = 0;
		}
	}

	public Solution convertToSolution(final List<IParameter.Batch> variables)
		throws GamaRuntimeException {
		final Solution sol = new Solution(variables, true);
		// TODO or false ???
		for ( int i = 0; i < phenotype.length; i++ ) {
			sol.put(phenotype[i], genes[i]);
		}
		return sol;
	}

	@Override
	public int compareTo(final Chromosome other) {
		return Double.valueOf(this.fitness).compareTo(Double.valueOf(other.fitness));
	}

	public String[] getPhenotype() {
		return phenotype;
	}

}
