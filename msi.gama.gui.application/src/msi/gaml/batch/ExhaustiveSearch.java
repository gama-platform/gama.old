/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.batch;

import java.util.List;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;

@symbol(name = { ISymbol.METHOD, IBatch.EXHAUSTIVE }, kind = ISymbolKind.BATCH_METHOD)
@inside(kinds = { ISymbolKind.EXPERIMENT })
@facets({
	@facet(name = ISymbol.NAME, type = IType.ID, optional = false),
	@facet(name = ISymbol.MAXIMIZE, type = IType.FLOAT_STR, optional = true),
	@facet(name = ISymbol.MINIMIZE, type = IType.FLOAT_STR, optional = true),
	@facet(name = ISymbol.AGGREGATION, type = IType.LABEL, optional = true, values = { ISymbol.MIN,
		ISymbol.MAX }) })
public class ExhaustiveSearch extends ParamSpaceExploAlgorithm {

	public ExhaustiveSearch(final IDescription desc) {
		super(desc);
	}

	@Override
	public Solution findBestSolution() throws GamaRuntimeException {
		bestFitness = isMaximize() ? Double.MIN_VALUE : Double.MAX_VALUE;
		testSolutions(new Solution(), 0);
		return bestSolution;
	}

	private void testSolutions(final Solution sol, final int index) throws GamaRuntimeException {
		List<IParameter.Batch> variables = currentExperiment.getParametersToExplore();
		final IParameter.Batch var = variables.get(index);
		Solution solution = new Solution(sol);
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
				if ( var.type().id() == IType.INT ) {
					solution.put(var.getName(), (int) varValue);
				} else if ( var.type().id() == IType.FLOAT ) {
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
