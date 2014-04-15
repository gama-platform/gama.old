/*********************************************************************************************
 * 
 *
 * 'ExhaustiveSearch.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.kernel.batch;

import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.IType;

@symbol(name = { IKeyword.EXHAUSTIVE }, kind = ISymbolKind.BATCH_METHOD, with_sequence = false)
@inside(kinds = { ISymbolKind.EXPERIMENT })
@facets(value = { @facet(name = IKeyword.NAME, type = IType.ID, optional = false),
	@facet(name = IKeyword.MAXIMIZE, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.MINIMIZE, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.AGGREGATION, type = IType.LABEL, optional = true, values = { IKeyword.MIN, IKeyword.MAX }) }, omissible = IKeyword.NAME)
public class ExhaustiveSearch extends ParamSpaceExploAlgorithm {

	public ExhaustiveSearch(final IDescription desc) {
		super(desc);
	}

	@Override
	public ParametersSet findBestSolution() throws GamaRuntimeException {
		setBestFitness(isMaximize() ? Double.MIN_VALUE : Double.MAX_VALUE);
		testSolutions(new ParametersSet(), 0);
		return getBestSolution();
	}

	private void testSolutions(final ParametersSet sol, final int index) throws GamaRuntimeException {
		List<IParameter.Batch> variables = currentExperiment.getParametersToExplore();
		ParametersSet solution = new ParametersSet(sol);
		if (variables.isEmpty()) {
			final double fitness = currentExperiment.launchSimulationsWithSolution(solution);
			setBestFitness(fitness);
			setBestSolution(solution);
			return;
		}
		final IParameter.Batch var = variables.get(index);
		if ( var.getAmongValue() != null ) {
			for ( Object val : var.getAmongValue() ) {
				solution.put(var.getName(), val);
				if ( solution.size() == variables.size() ) {
					final double fitness = currentExperiment.launchSimulationsWithSolution(solution);
					if ( isMaximize() ? fitness > getBestFitness() : fitness < getBestFitness() ) {
						setBestFitness(fitness);
						setBestSolution(solution);
					}
				} else {
					testSolutions(solution, index + 1);
				}
			}
		} else {
			double varValue = var.getMinValue().doubleValue();
			while (varValue <= var.getMaxValue().doubleValue()) {
				if ( var.getType().id() == IType.INT ) {
					solution.put(var.getName(), (int) varValue);
				} else if ( var.getType().id() == IType.FLOAT ) {
					solution.put(var.getName(), varValue);
				} else {
					continue;
				}
				if ( solution.size() == variables.size() ) {
					final double fitness = currentExperiment.launchSimulationsWithSolution(solution);
					if ( isMaximize() ? fitness > getBestFitness() : fitness < getBestFitness() ) {
						setBestFitness(fitness);
						setBestSolution(solution);
					}
				} else {
					testSolutions(solution, index + 1);
				}
				varValue = (int) ((varValue + var.getStepValue().doubleValue()) * 100000 + 0.5) / 100000.0;
			}
		}

	}
}
