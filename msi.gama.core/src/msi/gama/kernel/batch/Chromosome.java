/*********************************************************************************************
 * 
 * 
 * 'Chromosome.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.kernel.batch;

import java.util.Collection;
import java.util.List;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.types.IType;

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

		for (int i = 0; i < genes.length; i++) {
			genes[i] = chromosome.genes[i];
			phenotype[i] = chromosome.phenotype[i];
		}

		fitness = chromosome.fitness;
	}

	public Chromosome(final IScope scope, final List<IParameter.Batch> variables, final boolean reInitVal) {
		genes = new double[variables.size()];
		phenotype = new String[variables.size()];
		int cpt = 0;
		for (final IParameter.Batch var : variables) {
			if (reInitVal) {
				var.reinitRandomly(scope);
			}
			phenotype[cpt] = var.getName();
			if (var.getType().id() == IType.FLOAT) {
				genes[cpt] = ((Double) var.value(scope)).doubleValue();
			} else if (var.getType().id() == IType.INT) {
				genes[cpt] = ((Integer) var.value(scope)).doubleValue();
			} else {
				genes[cpt] = 0;
			}
			cpt++;
		}
	}

	public void setGene(final IScope scope, final IParameter.Batch var, final int index) {
		if (var.getType().id() == IType.FLOAT) {
			genes[index] = ((Double) var.value(scope)).doubleValue();
		} else if (var.getType().id() == IType.INT) {
			genes[index] = ((Integer) var.value(scope)).doubleValue();
		} else {
			genes[index] = 0;
		}
	}

	public ParametersSet convertToSolution(final IScope scope, final Collection<IParameter.Batch> variables)
			throws GamaRuntimeException {
		final ParametersSet sol = new ParametersSet(scope, variables, true);
		// TODO or false ???
		for (int i = 0; i < phenotype.length; i++) {
			sol.put(phenotype[i], genes[i]);
		}
		return sol;
	}

	@Override
	public int compareTo(final Chromosome other) {
		return Double.compare(this.fitness, other.fitness);
	}

	public String[] getPhenotype() {
		return phenotype;
	}

}
