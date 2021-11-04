/*******************************************************************************************************
 *
 * msi.gama.kernel.batch.HillClimbing.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.batch.exploration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ParametersSet;
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
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

@symbol (
		name = IKeyword.EXPLICIT,
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
						name = ExplicitExploration.PARAMETER_SET,
						type = IType.LIST,
						of = IType.MAP,
						optional = false,
						doc = @doc ("the list of parameter sets to explore; a parameter set is defined by a map: key: name of the variable, value: expression for the value of the variable")),
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
						doc = @doc ("the agregation method")) },
		omissible = IKeyword.NAME)
@doc (
		value = "This algorithm run simulations with the given parameter sets",
		usages = { @usage (
				value = "As other batch methods, the basic syntax of the `explicit` statement uses `method explicit` instead of the expected `explicit name: id` : ",
				examples = { @example (
						value = "method explicit [facet: value];",
						isExecutable = false) }),
				@usage (
						value = "For example: ",
						examples = { @example (
								value = "method explicit parameter_sets:[[\"a\"::0.5, \"b\"::10],[\"a\"::0.1, \"b\"::100]]; ",
								isExecutable = false) }) })
public class ExplicitExploration extends AExplorationAlgorithm {

	protected static final String PARAMETER_SET = "parameter_sets";
	protected List<Map<String, Object>> parameterSets;
	public ExplicitExploration(final IDescription desc) { super(desc); }
	@Override public void setChildren(Iterable<? extends ISymbol> children) { }
	
	
	@Override
	public void explore(final IScope scope) throws GamaRuntimeException {
		List<ParametersSet> solutions = buildParameterSets(scope, new ArrayList<>(), 0);
		if (GamaExecutorService.CONCURRENCY_SIMULATIONS_ALL.getValue()) {
			currentExperiment.launchSimulationsWithSolution(solutions);
		} else {
			for (ParametersSet sol : solutions) { currentExperiment.launchSimulationsWithSolution(sol); }
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<ParametersSet> buildParameterSets(IScope scope, List<ParametersSet> sets, int index) {
		IExpression psexp = getFacet(PARAMETER_SET);
		parameterSets = (List<Map<String, Object>>) Cast.asList(scope, psexp.value(scope));
		for (Map<String,Object> parameterSet : parameterSets) {
			ParametersSet p = new ParametersSet();
			for (String v : parameterSet.keySet()) {
				Object val = parameterSet.get(v);
				p.put(v,(val instanceof IExpression) ? ((IExpression) val).value(scope) : val);
			}
			sets.add(p);
		}
		return sets;
	}

}
