/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
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
@facets(value = {
	@facet(name = IKeyword.NAME, type = IType.ID, optional = false),
	@facet(name = IKeyword.MAXIMIZE, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.MINIMIZE, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.AGGREGATION, type = IType.LABEL, optional = true, values = {
		IKeyword.MIN, IKeyword.MAX }) }, omissible = IKeyword.NAME)
public class ExhaustiveSearch extends ParamSpaceExploAlgorithm {

	public ExhaustiveSearch(final IDescription desc) {
		super(desc);
	}

	@Override
	public ParametersSet findBestSolution() throws GamaRuntimeException {
		bestFitness = isMaximize() ? Double.MIN_VALUE : Double.MAX_VALUE;
		testSolutions(new ParametersSet(), 0);
		return bestSolution;
	}

	private void testSolutions(final ParametersSet sol, final int index)
		throws GamaRuntimeException {
		List<IParameter.Batch> variables = currentExperiment.getParametersToExplore();
		final IParameter.Batch var = variables.get(index);
		ParametersSet solution = new ParametersSet(sol);
		if ( var.getAmongValue() != null ) {
			for ( Object val : var.getAmongValue() ) {
				solution.put(var.getName(), val);
				if ( solution.size() == variables.size() ) {
					final double fitness =
						currentExperiment.launchSimulationsWithSolution(solution);
					if ( isMaximize() && fitness > bestFitness || !isMaximize() &&
						fitness < bestFitness ) {
						bestFitness = fitness;
						bestSolution = solution;
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
					final double fitness =
						currentExperiment.launchSimulationsWithSolution(solution);
					if ( isMaximize() && fitness > bestFitness || !isMaximize() &&
						fitness < bestFitness ) {
						bestFitness = fitness;
						bestSolution = solution;
					}
				} else {
					testSolutions(solution, index + 1);
				}
				varValue =
					(int) ((varValue + var.getStepValue().doubleValue()) * 100000 + 0.5) / 100000.0;
			}
		}

	}
}
