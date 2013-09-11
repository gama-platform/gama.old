package ummisco.gaml.extensions.maths.pde.diffusion.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix.GridPopulation;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.GamaFloatMatrix;
import msi.gama.util.matrix.GamaMatrix;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;

@facets(value = {
		@facet(name = IKeyword.VAR, type = IType.ID, optional = false),
		@facet(name = IKeyword.ON, type = IType.ID, optional = false),
		@facet(name = "mat_diffu", type = IType.MATRIX, optional = false),
		@facet(name = IKeyword.METHOD, type = IType.ID, optional = true, values = {
				"convolution", "dot_product" }),
		@facet(name = IKeyword.MASK, type = IType.MATRIX, optional = true),
		@facet(name = IKeyword.CYCLE_LENGTH, type = IType.INT, optional = true) }, omissible = IKeyword.VAR)
@symbol(name = { "diffusion" }, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SINGLE_STATEMENT,
		ISymbolKind.SPECIES, ISymbolKind.MODEL })
public class DiffusionStatement extends AbstractStatementSequence {

	private double time_initial = 0, time_final = 1;
	private int discret = 0;
	private double cycle_length = 1;
	private boolean is_torus = false;
	private String var_diffu = "";
	private String species_diffu = "";
	private GridPopulation pop = null;
	public DiffusionStatement(final IDescription desc) {
		super(desc);
	}

	public GamaFloatMatrix doDiffusion2(IScope scope, GamaFloatMatrix mat_current,
			double[][] mat_diffu, double[][] mask) {

		GamaFloatMatrix tmp=new GamaFloatMatrix(scope, mat_current.numCols, mat_current.numRows);

		int kRows = mat_diffu.length;
		int kCols = mat_diffu[0].length;

		int xcenter = kRows / 2;
		int ycenter = kCols / 2;

		for (int i = 0; i < mat_current.numRows; i++) {
			for (int j = 0; j < mat_current.numCols; j++) {
				double currentValue = mat_current.get(scope, j, i);//Cast.asFloat(scope, lstAgents[i * rows + j].getDirectVarValue(scope, varName));
				
				int um = 0;
				for (int uu = i - xcenter; uu <= i + xcenter; uu++) {
					int vm = 0;
					for (int vv = j - ycenter; vv <= j + ycenter; vv++) {
						int u=uu;
						int v=vv;
						double mask_current = (mask != null) ? ((mask[i][j] < -1) ? 0
								: 1)
								: 1;
						if (is_torus) {
							if (u < 0) {
								u = mat_current.numRows + u;
							} else if (u >= mat_current.numRows) {
								u = u - mat_current.numRows;
							}

							if (v < 0) {
								v = mat_current.numCols + v;
							} else if (v >= mat_current.numCols) {
								v = v - mat_current.numCols;
							}
						}
						else if (u < 0 || v < 0 || u >= mat_current.numCols || v >= mat_current.numRows) {
							continue;
						}

						tmp.set(scope, v, u, tmp.get(scope, v, u) + mat_current.get(scope, j, i)
								* mat_diffu[um][vm] * mask_current);
						vm++;
					}
					um++;

				}
			}
		}


		return tmp;

	}

	public GamaFloatMatrix initDiffusion(IScope scope) {
		
		IAgent[] lstAgents = pop.toArray();
		GamaFloatMatrix tmp=new  GamaFloatMatrix(scope, pop.matrixValue(scope).getCols(scope), pop.matrixValue(scope).getRows(scope));
		
		for(int i=0; i< tmp.numRows; i++) {
			for(int j=0; j< tmp.numRows ; j++) {
				tmp.set(scope, j, i, Cast.asFloat(scope, lstAgents[i * tmp.numRows+ j]
						.getDirectVarValue(scope, var_diffu)));
			}
		}
		return tmp;
	}
	
	public GamaFloatMatrix doDiffusion1(IScope scope, GamaFloatMatrix mat_current,
			double[][] mat_diffu, double[][] mask) {

		int kRows = mat_diffu.length;
		int kCols = mat_diffu[0].length;
		
		int kCenterX = kRows / 2;
		int kCenterY = kCols / 2;
		int mm = 0, nn = 0, ii = 0, jj = 0;
		GamaFloatMatrix tmp=new GamaFloatMatrix(scope, mat_current.numCols, mat_current.numRows);

		for (int i = 0; i < mat_current.numRows; ++i) // rows
		{
			for (int j = 0; j < mat_current.numCols; ++j) // columns
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
						if (is_torus) {
							if (ii < 0) {
								ii = mat_current.numRows + ii;
							} else if (ii >= mat_current.numRows) {
								ii = ii - mat_current.numRows;
							}

							if (jj < 0) {
								jj = mat_current.numCols + jj;
							} else if (jj >= mat_current.numCols ) {
								jj = jj - mat_current.numCols ;
							}
						}
						if (ii >= 0 && ii <  mat_current.numRows && jj >= 0 && jj <  mat_current.numCols) {
						
							double mask_current = (mask != null) ? ((mask[i][j] < -1) ? 0
									: 1)
									: 1;

							tmp.set(scope, j, i, tmp.get(scope, j, i) + mat_current.get(scope, jj, ii) * mat_diffu[mm][nn]
									* mask_current);
						}

					}
				}
			}
		}

		return tmp;
	}
	
	public void finishDiffusion(IScope scope, GamaFloatMatrix mat_current) {		
		for (int i = 0; i < mat_current.numRows; i++) {
			for (int j = 0; j < mat_current.numCols; j++) {
				pop.getAgent(i *  mat_current.numRows + j).setDirectVarValue(scope, var_diffu, mat_current.get(scope, j,i));
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
		if (getFacet(IKeyword.CYCLE_LENGTH) != null) {
			cLen = Integer.parseInt("" + getFacet(IKeyword.CYCLE_LENGTH).value(scope));
		}

		var_diffu = (String) getFacet(IKeyword.VAR).value(scope);
		String speciesName = (String) getFacet(IKeyword.ON).value(scope);
		double[][] mat_diffu = translateMatrix(scope,
				(IMatrix) getFacet("mat_diffu").value(scope));

		double[][] mask = null;

		if (getFacet(IKeyword.MASK) != null) {
			mask = translateMatrix(scope,
					(IMatrix) getFacet(IKeyword.MASK).value(scope));

		}
		pop = (GridPopulation) scope.getAgentScope().getPopulationFor(speciesName);
		is_torus =  pop.getTopology().isTorus();
		
		IExpression method_diffu = getFacet(IKeyword.METHOD);

		GamaFloatMatrix mat_current=initDiffusion(scope);
		
		
		if (method_diffu != null && method_diffu.value(scope).equals("dot_product")) {
			for (int time = 0; time < cLen; time++) {
				mat_current = doDiffusion2(scope, mat_current, mat_diffu, mask);
			}
				
			
		}
		else
		{
			for (int time = 0; time < cLen; time++) {
				mat_current = doDiffusion1(scope, mat_current, mat_diffu, mask);
			}			
		}
		
		finishDiffusion(scope, mat_current);
		
		return null;
	}
}
