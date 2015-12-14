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
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.IType;

@symbol(name = { IKeyword.EXHAUSTIVE }, kind = ISymbolKind.BATCH_METHOD, with_sequence = false)
@inside(kinds = { ISymbolKind.EXPERIMENT })
@facets(
	value = { @facet(name = IKeyword.NAME, type = IType.ID, optional = false, internal = true),
		@facet(name = IKeyword.MAXIMIZE,
			type = IType.FLOAT,
			optional = true,
			doc = @doc("the value the algorithm tries to maximize") ),
		@facet(name = IKeyword.MINIMIZE,
			type = IType.FLOAT,
			optional = true,
			doc = @doc("the value the algorithm tries to minimize") ),
		@facet(name = IKeyword.AGGREGATION,
			type = IType.LABEL,
			optional = true,
			values = { IKeyword.MIN, IKeyword.MAX },
			doc = @doc("the agregation method") ) },
	omissible = IKeyword.NAME)
@doc(
	value = "This is the standard batch method. The exhaustive mode is defined by default when there is no method element present in the batch section. It explores all the combination of parameter values in a sequential way. See [batch161 the batch dedicated page].",
	usages = {
		@usage(
			value = "As other batch methods, the basic syntax of the exhaustive statement uses `method exhaustive` instead of the expected `exhaustive name: id` : ",
			examples = { @example(value = "method exhaustive [facet: value];", isExecutable = false) }),
		@usage(value = "For example: ",
			examples = { @example(value = "method exhaustive maximize: food_gathered;", isExecutable = false) }) })
public class ExhaustiveSearch extends ParamSpaceExploAlgorithm {

	public ExhaustiveSearch(final IDescription desc) {
		super(desc);
	}

	@Override
	public ParametersSet findBestSolution(final IScope scope) throws GamaRuntimeException {
		setBestFitness(isMaximize() ? Double.MIN_VALUE : Double.MAX_VALUE);
		testSolutions(new ParametersSet(), 0);
		return getBestSolution();
	}

	private void testSolutions(final ParametersSet sol, final int index) throws GamaRuntimeException {
		List<IParameter.Batch> variables = currentExperiment.getParametersToExplore();
		ParametersSet solution = new ParametersSet(sol);
		if ( variables.isEmpty() ) {
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
