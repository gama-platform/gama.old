package ummisco.gaml.extensions.maths.pde.diffusion.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix.GridPopulation;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.GamaFloatMatrix;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;

@facets(value = {
		@facet(name = "var", type = IType.ID, optional = false),
		@facet(name = "on", type = IType.ID, optional = false),
		@facet(name = "mat_diffu", type = IType.MATRIX, optional = false),
		@facet(name = "method", type = IType.ID, optional = true, values = {
				"convolution", "dot_product" }),
		@facet(name = "mask", type = IType.MATRIX, optional = true),
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

	public void doDiffusion2(IScope scope, String varName, String speciesName,
			double[][] mat_diffu, double[][] mask) {

		GridPopulation pop = (GridPopulation) scope.getAgentScope()
				.getPopulationFor(speciesName);
		boolean isTorus = pop.getTopology().isTorus();

		IAgent[] lstAgents = pop.toArray();
		IMatrix mmm = pop.matrixValue(scope);
		int cols = mmm.getCols(scope);
		int rows = mmm.getRows(scope);

		// IMatrix<Double> mask = new GamaFloatMatrix(scope,cols, rows);

		// if (getFacet("mask") != null) {
		// mask = (IMatrix) getFacet("mask").value(scope);
		// }

		int kRows = mat_diffu.length;
		int kCols = mat_diffu[0].length;

		int xcenter = kRows / 2;
		int ycenter = kCols / 2;
		// GamaFloatMatrix tmp = new GamaFloatMatrix(scope, cols, rows);
		double[][] tmp = new double[rows][cols];

		// System.out.println(xcenter+" "+ycenter);
		// find center position of kernel (half of kernel size)

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				double currentValue = Double.parseDouble(""
						+ lstAgents[i * rows + j].getAttribute(varName));
				// if (currentValue <= Double.MAX_VALUE
				// && currentValue >= - (Double.MAX_VALUE))
				// {
				// && i >= xcenter
				// && j >= ycenter && i < cols - xcenter
				// && j < rows - ycenter) {

				int um = 0;
				for (int uu = i - xcenter; uu <= i + xcenter; uu++) {
					int vm = 0;
					for (int vv = j - ycenter; vv <= j + ycenter; vv++) {
						int u=uu;
						int v=vv;
						double currentMask = (mask != null) ? ((mask[i][j] < -1) ? 0
								: 1)
								: 1;
						if (isTorus) {
							if (u < 0) {
								u = rows + u;
							} else if (u >= rows) {
								u = u - rows;
							}

							if (v < 0) {
								v = cols + v;
							} else if (v >= cols) {
								v = v - cols;
							}
						}
						else if (u < 0 || v < 0 || u >= cols || v >= rows) {
							continue;
						}
						System.out.println(um + " " + vm +" | "+u+" "+v);
						// System.out.println(currentMask);
						tmp[u][v] = tmp[u][v] + currentValue
								* mat_diffu[um][vm] * currentMask;
						vm++;
					}
					um++;
					// }
					 System.out.println();
				}
			}
		}

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				lstAgents[i * rows + j].setAttribute(varName, tmp[i][j]);
			}
		}

	}

	public void doDiffusion1(IScope scope, String varName, String speciesName,
			double[][] mat_diffu, double[][] mask) {

		GridPopulation pop = (GridPopulation) scope.getAgentScope()
				.getPopulationFor(speciesName);
		// GuiUtils.informConsole(""+pop.getTopology().isTorus());
		boolean isTorus = pop.getTopology().isTorus();
		IAgent[] lstAgents = pop.toArray();
		IMatrix mmm = pop.matrixValue(scope);
		int cols = mmm.getCols(scope);
		int rows = mmm.getRows(scope);

		int kRows = mat_diffu.length;
		int kCols = mat_diffu[0].length;
		// double[][] mat_diffu=translateMatrix(scope, mat_diffusion);

		int kCenterX = kRows / 2;
		int kCenterY = kCols / 2;
		// GamaFloatMatrix tmp = new GamaFloatMatrix(scope, cols, rows);
		double[][] tmp = new double[rows][cols];
		// System.out.println(xcenter+" "+ycenter);
		// find center position of kernel (half of kernel size)
		// float sum=0;
		int mm = 0, nn = 0, ii = 0, jj = 0;

		for (int i = 0; i < rows; ++i) // rows
		{
			for (int j = 0; j < cols; ++j) // columns
			{

				// sum = 0; // init to 0 before sum
				for (int m = 0; m < kRows; ++m) // kernel rows
				{
					mm = kRows - 1 - m; // row index of flipped kernel
					for (int n = 0; n < kCols; ++n) // kernel columns
					{
						nn = kCols - 1 - n; // column index of flipped kernel

						// index of input signal, used for checking boundary
						ii = i + (m - kCenterX);
						jj = j + (n - kCenterY);

						// ignore input samples which are out of bound
						if (isTorus) {
							if (ii < 0) {
								ii = rows + ii;
							} else if (ii >= rows) {
								ii = ii - rows;
							}

							if (jj < 0) {
								jj = cols + jj;
							} else if (jj >= cols) {
								jj = jj - cols;
							}
						}
						if (ii >= 0 && ii < rows && jj >= 0 && jj < cols) {
							// out[i][j] = out[i][j] + in[ii][jj] *
							// kernel[mm][nn];

							double inValue = Double.parseDouble(""
									+ lstAgents[ii * rows + jj]
											.getAttribute(varName));

							double currentMask = (mask != null) ? ((mask[i][j] < -1) ? 0
									: 1)
									: 1;
							double currentMatValue = mat_diffu[mm][nn];
							tmp[i][j] = tmp[i][j] + inValue * currentMatValue
									* currentMask;
						}

					}
				}
			}
		}

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				lstAgents[i * rows + j].setAttribute(varName, tmp[i][j]);
			}
		}

	}

	public double[][] translateMatrix(IScope scope, IMatrix mm) {
		int rows = mm.getRows(scope);
		int cols = mm.getCols(scope);
		double[][] res = new double[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				res[i][j] = Double.parseDouble("" + mm.get(scope, i, j));
			}
		}
		return res;
	}

	@Override
	public Object privateExecuteIn(final IScope scope)
			throws GamaRuntimeException {
		int cLen = 1;
		if (getFacet("cycle_length") != null) {
			cLen = Integer.parseInt("" + getFacet("cycle_length").value(scope));
		}

		String varName = (String) getFacet("var").value(scope);
		String speciesName = (String) getFacet("on").value(scope);
		// IMatrix mat_diffu = (IMatrix) getFacet("mat_diffu").value(scope);
		double[][] mat_diffu = translateMatrix(scope,
				(IMatrix) getFacet("mat_diffu").value(scope));

		double[][] mask = null;

		if (getFacet("mask") != null) {
			mask = translateMatrix(scope,
					(IMatrix) getFacet("mask").value(scope));

		}

		IExpression method_diffu = getFacet("method");
		if (method_diffu != null) {
			if (method_diffu.value(scope).equals("dot_product")) {
				for (int time = 0; time < cLen; time++) {
					doDiffusion2(scope, varName, speciesName, mat_diffu, mask);
				}
				return null;
			}
		}

		for (int time = 0; time < cLen; time++) {
			doDiffusion1(scope, varName, speciesName, mat_diffu, mask);
		}
		// super.privateExecuteIn(scope);
		// if (getFacet("cycle_length") != null) {
		// cycle_length = Double.parseDouble(""
		// + getFacet("cycle_length").value(scope));
		// }

		// System.out.println(tcc);
		return null;
	}
}
