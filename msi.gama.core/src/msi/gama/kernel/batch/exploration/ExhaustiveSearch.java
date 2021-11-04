/*******************************************************************************************************
 *
 * msi.gama.kernel.batch.ExhaustiveSearch.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.batch.exploration;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaDate;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.operators.Cast;
import msi.gaml.types.GamaDateType;
import msi.gaml.types.IType;

@symbol (
		name = { IKeyword.EXHAUSTIVE },
		kind = ISymbolKind.BATCH_METHOD,
		with_sequence = false,
		concept = { IConcept.BATCH, IConcept.ALGORITHM })
@inside (
		kinds = { ISymbolKind.EXPERIMENT })
@facets (
		value = { @facet (
				name = IKeyword.NAME,
				type = IType.ID,
				optional = false,
				internal = true,
				doc = @doc ("The name of the method. For internal use only")),
				@facet (
						name = IKeyword.MAXIMIZE,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("the value the algorithm tries to maximize")),
				@facet (
						name = IKeyword.MINIMIZE,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("the value the algorithm tries to minimize")),
				@facet (
						name = IKeyword.AGGREGATION,
						type = IType.LABEL,
						optional = true,
						values = { IKeyword.MIN, IKeyword.MAX },
						doc = @doc ("The aggregation method to use (either min or max)")) },
		omissible = IKeyword.NAME)
@doc (
		value = "This is the standard batch method. The exhaustive mode is defined by default when there is no method element present in the batch section. It explores all the combination of parameter values in a sequential way. See [batch161 the batch dedicated page].",
		usages = { @usage (
				value = "As other batch methods, the basic syntax of the exhaustive statement uses `method exhaustive` instead of the expected `exhaustive name: id` : ",
				examples = { @example (
						value = "method exhaustive [facet: value];",
						isExecutable = false) }),
				@usage (
						value = "For example: ",
						examples = { @example (
								value = "method exhaustive maximize: food_gathered;",
								isExecutable = false) }) })
public class ExhaustiveSearch extends AExplorationAlgorithm {

	public ExhaustiveSearch(final IDescription desc) { super(desc); }
	
	@Override
	public void setChildren(Iterable<? extends ISymbol> children) { }

	@Override
	public void explore(final IScope scope) throws GamaRuntimeException {
		if (GamaExecutorService.CONCURRENCY_SIMULATIONS_ALL.getValue() && ! currentExperiment.getParametersToExplore().isEmpty())
			testSolutionsAll(scope);
		else
			testSolutions(scope, new ParametersSet(), 0);
	}
	
	@Override
	public List<ParametersSet> buildParameterSets(IScope scope, List<ParametersSet> sets, int index) {
		List<ParametersSet> sets2 = new ArrayList<>();
		final List<IParameter.Batch> variables = currentExperiment.getParametersToExplore();
		if (variables.isEmpty()) return sets2;
			
		final IParameter.Batch var = variables.get(index);
		for (ParametersSet solution : sets) {
			if (var.getAmongValue(scope) != null) {
				for (final Object val : var.getAmongValue(scope)) {
					ParametersSet ps = new ParametersSet(solution);
					ps.put(var.getName(), val);
					sets2.add(ps);
				}
				
			} else {
				
				
				
				switch (var.getType().id()) {
					case IType.INT:
						int intValue = Cast.asInt(scope, var.getMinValue(scope));
						int maxIntValue = Cast.asInt(scope, var.getMaxValue(scope));
						while (intValue <= maxIntValue) {
							ParametersSet ps = new ParametersSet(solution);
							ps.put(var.getName(), intValue);
							sets2.add(ps);
							
							intValue = intValue + Cast.asInt(scope, var.getStepValue(scope));
						}
						break;
					case IType.FLOAT:
						double floatValue = Cast.asFloat(scope, var.getMinValue(scope));
						double maxFloatValue = Cast.asFloat(scope, var.getMaxValue(scope));
						while (floatValue <= maxFloatValue) {
							
							ParametersSet ps = new ParametersSet(solution);
							ps.put(var.getName(), floatValue);
							sets2.add(ps);
							
							floatValue = floatValue + Cast.asFloat(scope, var.getStepValue(scope));
						}
						break;
					case IType.DATE:
						GamaDate dateValue = GamaDateType.staticCast(scope, var.getMinValue(scope), null, false);
						GamaDate maxDateValue = GamaDateType.staticCast(scope, var.getMaxValue(scope), null, false);
						while (dateValue.isSmallerThan(maxDateValue, false)) {
							ParametersSet ps = new ParametersSet(solution);
							ps.put(var.getName(), dateValue);
							sets2.add(ps);
							
							dateValue = dateValue.plus(Cast.asFloat(scope, var.getStepValue(scope)), ChronoUnit.SECONDS);
						}
						break;
					case IType.POINT:
						GamaPoint pointValue = Cast.asPoint(scope, var.getMinValue(scope));
						GamaPoint maxPointValue = Cast.asPoint(scope, var.getMaxValue(scope));
						while (pointValue.smallerThanOrEqualTo(maxPointValue)) {
							ParametersSet ps = new ParametersSet(solution);
							ps.put(var.getName(), pointValue);
							sets2.add(ps);
							
							pointValue = pointValue.plus(Cast.asPoint(scope, var.getStepValue(scope)));
						}
						break;
					default:
						double varValue = Cast.asFloat(scope, var.getMinValue(scope));
						while (varValue <= Cast.asFloat(scope, var.getMaxValue(scope))) {
							ParametersSet ps = new ParametersSet(solution);
							if (var.getType().id() == IType.INT) {
								ps.put(var.getName(), (int) varValue);
							} else if (var.getType().id() == IType.FLOAT) {
								ps.put(var.getName(), varValue);
							} else {
								continue;
							}
							sets2.add(ps);
							
							varValue = varValue + Cast.asFloat(scope, var.getStepValue(scope));
						}
				}
			}
		}
		if (index == (variables.size() - 1)) {
			return sets2;
		}
		return buildParameterSets(scope,sets2,index+1);
	}
	
	// INNER UTILITY METHODS
	
	private void testSolutionsAll(final IScope scope) {
		List<ParametersSet> sets = new ArrayList<>();
		sets.add(new ParametersSet());
		final List<ParametersSet> solutions = buildParameterSets(scope,sets, 0);
		currentExperiment.launchSimulationsWithSolution(solutions);
	}

	private void testSolutions(final IScope scope, final ParametersSet sol, final int index)
			throws GamaRuntimeException {
		final List<IParameter.Batch> variables = currentExperiment.getParametersToExplore();
		final ParametersSet solution = new ParametersSet(sol);
		if (variables.isEmpty()) {
			currentExperiment.launchSimulationsWithSolution(solution);
			return;
		}
		final IParameter.Batch var = variables.get(index);
		if (var.getAmongValue(scope) != null) {
			for (final Object val : var.getAmongValue(scope)) {
				solution.put(var.getName(), val);
				if (solution.size() == variables.size()) {
					currentExperiment.launchSimulationsWithSolution(solution);
				} else {
					testSolutions(scope, solution, index + 1);
				}
			}
		} else {
			switch (var.getType().id()) {
				case IType.INT:
					int intValue = Cast.asInt(scope, var.getMinValue(scope));
					int maxIntValue = Cast.asInt(scope, var.getMaxValue(scope));
					while (intValue <= maxIntValue) {
						solution.put(var.getName(), intValue);
						if (solution.size() == variables.size()) {
							currentExperiment.launchSimulationsWithSolution(solution);
						} else {
							testSolutions(scope, solution, index + 1);
						}
						intValue = intValue + Cast.asInt(scope, var.getStepValue(scope));
					}
					break;
				case IType.FLOAT:
					double floatValue = Cast.asFloat(scope, var.getMinValue(scope));
					double maxFloatValue = Cast.asFloat(scope, var.getMaxValue(scope));
					while (floatValue <= maxFloatValue) {
						solution.put(var.getName(), floatValue);
						if (solution.size() == variables.size()) {
							currentExperiment.launchSimulationsWithSolution(solution);
						} else {
							testSolutions(scope, solution, index + 1);
						}
						floatValue = floatValue + Cast.asFloat(scope, var.getStepValue(scope));
					}
					break;
				case IType.DATE:
					GamaDate dateValue = GamaDateType.staticCast(scope, var.getMinValue(scope), null, false);
					GamaDate maxDateValue = GamaDateType.staticCast(scope, var.getMaxValue(scope), null, false);
					while (dateValue.isSmallerThan(maxDateValue, false)) {
						solution.put(var.getName(), dateValue);
						if (solution.size() == variables.size()) {
							currentExperiment.launchSimulationsWithSolution(solution);
						} else {
							testSolutions(scope, solution, index + 1);
						}
						dateValue = dateValue.plus(Cast.asFloat(scope, var.getStepValue(scope)), ChronoUnit.SECONDS);
					}
					break;
				case IType.POINT:
					GamaPoint pointValue = Cast.asPoint(scope, var.getMinValue(scope));
					GamaPoint maxPointValue = Cast.asPoint(scope, var.getMaxValue(scope));
					while (pointValue.smallerThanOrEqualTo(maxPointValue)) {
						solution.put(var.getName(), pointValue);
						if (solution.size() == variables.size()) {
							currentExperiment.launchSimulationsWithSolution(solution);
						} else {
							testSolutions(scope, solution, index + 1);
						}
						pointValue = pointValue.plus(Cast.asPoint(scope, var.getStepValue(scope)));
					}
					break;
				default:
					double varValue = Cast.asFloat(scope, var.getMinValue(scope));
					while (varValue <= Cast.asFloat(scope, var.getMaxValue(scope))) {
						if (var.getType().id() == IType.INT) {
							solution.put(var.getName(), (int) varValue);
						} else if (var.getType().id() == IType.FLOAT) {
							solution.put(var.getName(), varValue);
						} else {
							continue;
						}
						if (solution.size() == variables.size()) {
							currentExperiment.launchSimulationsWithSolution(solution);
						} else {
							testSolutions(scope, solution, index + 1);
						}
						varValue = varValue + Cast.asFloat(scope, var.getStepValue(scope));
					}
			}

		}

	}

}
