package msi.gama.kernel.batch.exploration.betadistribution;

import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.batch.exploration.AExplorationAlgorithm;
import msi.gama.kernel.batch.exploration.ExhaustiveSearch;
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
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.IType;

/**
 * 
 * 
 * @author kevinchapuis
 * 
 * Coefficient derived from the work below:
 * 
 * E. Borgonovo, M. Pangallo, J. Rivkin, L. Rizzo, and N. Siggelkow, 
 * “Sensitivity analysis of agent-based models: a new protocol,” 
 * Comput. Math. Organ. Theory, vol. 28, no. 1, pp. 52–94, Mar. 2022, doi: 10.1007/s10588-021-09358-5.
 *
 */
@symbol (
		name = IKeyword.BETAD,
		kind = ISymbolKind.BATCH_METHOD,
		with_sequence = false,
		concept = { IConcept.BATCH, IConcept.ALGORITHM })
@inside ( kinds = { ISymbolKind.EXPERIMENT })
@facets (
		value = { 
			@facet (
				name = IKeyword.NAME,
				type = IType.ID,
				optional = false,
				internal = true,
				doc = @doc ("The name of the method. For internal use only")
			),
			@facet (
				name = ExhaustiveSearch.METHODS,
				type = IType.ID,
				optional = false,
				doc = @doc ("The sampling method to build parameters sets")
			),
			@facet(
					name = IKeyword.BATCH_VAR_OUTPUTS,
					type = IType.LIST,
					of = IType.STRING,
					optional = false,
					doc = @doc ("The list of output variables to analyse")
			),
			@facet(
					name = IKeyword.BATCH_OUTPUT,
					type = IType.STRING,
					optional = true,
					doc = @doc ("The path to the file where the automatic batch report will be written")
			),
			@facet(
				name = IKeyword.BATCH_REPORT,
				type = IType.STRING,
				optional = true,
				doc = @doc ("The path to the file where the Betad report will be written")
			)
		},
		omissible = IKeyword.NAME
		)
@doc (
		value = "This algorithm runs an exploration with a given sampling to compute BetadKu - see doi: 10.1007/s10588-021-09358-5",
		usages = { 
			@usage (
				value = "For example: ",
				examples = { @example (
						value = "method sobol sample_size:100 outputs:['my_var'] report:'../path/to/report/file.txt'; ",
						isExecutable = false) }
			) 
		}
		)
public class BetaExploration extends AExplorationAlgorithm {

	public BetaExploration(IDescription desc) {
		super(desc);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setChildren(Iterable<? extends ISymbol> children) {
		// TODO Auto-generated method stub

	}

	@Override
	public void explore(IScope scope) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<ParametersSet> buildParameterSets(IScope scope, List<ParametersSet> sets, int index) {
		// TODO Auto-generated method stub
		return null;
	}

}
