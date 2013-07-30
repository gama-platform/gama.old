package ummisco.gaml.extensions.maths.statements;

import java.util.Iterator;
import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix.GridPopulation;
import msi.gama.precompiler.GamlAnnotations.combination;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.matrix.GamaFloatMatrix;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;
import ummisco.gaml.extensions.maths.utils.*;

@facets(value = { @facet(name = "var", type = IType.ID, optional = false),
		@facet(name = "on", type = IType.ID, optional = false),
		@facet(name = "mat_diffu", type = IType.MATRIX, optional = false),
		@facet(name = "cycle_length", type = IType.INT, optional = true) }, omissible = IKeyword.EQUATION)
@symbol(name = { "diffusion" }, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
// , with_args = true)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SINGLE_STATEMENT,
		ISymbolKind.SPECIES, ISymbolKind.MODEL })
public class DiffusionStatement extends AbstractStatementSequence {

	double time_initial = 0, time_final = 1;
	int discret = 0;
	double cycle_length = 1;

	public DiffusionStatement(final IDescription desc) {
		super(desc);

		// List<IDescription> statements =
		// desc.getSpeciesContext().getChildren();
		// String eqName = getFacet("diffusion").literalValue();

		// Based on the facets, choose a solver and init it;

	}

	@Override
	public Object privateExecuteIn(final IScope scope)
			throws GamaRuntimeException {
		// super.privateExecuteIn(scope);
		String varName = (String) getFacet("var").value(scope);
		String speciesName = (String) getFacet("on").value(scope);
		IMatrix mat_diffu = (IMatrix) getFacet("mat_diffu").value(scope);

		GridPopulation pop = (GridPopulation) scope.getAgentScope()
				.getPopulationFor(speciesName);
		IAgent[] lstAgents = pop.toArray();
		IMatrix mmm = pop.matrixValue(scope);
		int cols = mmm.getCols(scope);
		int rows = mmm.getRows(scope);
		int xcenter = mat_diffu.getCols(scope) / 2;
		int ycenter = mat_diffu.getRows(scope) / 2;
		GamaFloatMatrix tmp = new GamaFloatMatrix(scope, cols, rows);
		double epsilon=1;
//		System.out.println(xcenter+" "+ycenter);
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				double currentValue = Double.parseDouble(""
						+ lstAgents[i * rows + j].getAttribute(varName));
				if (currentValue >= epsilon && i >= xcenter && j >= ycenter && i <= cols - xcenter
						&& j <= rows - ycenter) {
					
					for (int u = i - xcenter; u <= i + xcenter; u++) {
						for (int v = j - ycenter; v <= j + ycenter; v++) {
//							System.out.println(u+" "+v+" | "+( u - i + xcenter)+" "+(v - j + ycenter));
							double currentMatValue = Double.parseDouble(""
									+ mat_diffu.get(scope, u - i + xcenter, v
											- j + ycenter));
							double newValue = tmp.get(scope, u, v)
									+ currentValue * currentMatValue;
							tmp.set(scope, u, v, newValue);
						}
					}
//					System.out.println();
				}
			}
		}

		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				if(tmp.get(scope, i,j)<epsilon){
					tmp.set(scope, i,j,0);
				}
				lstAgents[i * rows + j].setAttribute(varName, tmp.get(scope, i,j));
			}
		}
//		if (getFacet("cycle_length") != null) {
//			cycle_length = Double.parseDouble(""
//					+ getFacet("cycle_length").value(scope));
//		}

		// System.out.println(tcc);
		return null;
	}
}
