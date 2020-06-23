/*******************************************************************************************************
 *
 * msi.gama.kernel.batch.Chromosome.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.batch;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.operators.Cast;
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

	public void update(final IScope scope, final ParametersSet solution) {
		final int nb = this.getGenes().length;
		for (int i = 0; i < nb; i++) {
			final String var = getPhenotype()[i];
			genes[i] = Cast.asFloat(scope, solution.get(var));
		}
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
				genes[cpt] = Cast.asFloat(scope, var.value(scope));
			} else if (var.getType().id() == IType.INT) {
				genes[cpt] = Cast.asInt(scope, var.value(scope));
			} else {
				genes[cpt] = 0;
			}
			cpt++;
		}
	}

	public void setGene(final IScope scope, final IParameter.Batch var, final int index) {
		if (var.getType().id() == IType.FLOAT) {
			genes[index] = Cast.asFloat(scope, var.value(scope));
		} else if (var.getType().id() == IType.INT) {
			genes[index] = Cast.asInt(scope, var.value(scope));
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(genes);
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		final Chromosome other = (Chromosome) obj;
		if (!Arrays.equals(genes, other.genes)) { return false; }
		return true;
	}

}
