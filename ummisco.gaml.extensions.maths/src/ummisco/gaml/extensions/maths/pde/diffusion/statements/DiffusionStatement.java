/*********************************************************************************************
 * 
 * 
 * 'DiffusionStatement.java', in plugin 'ummisco.gaml.extensions.maths', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ummisco.gaml.extensions.maths.pde.diffusion.statements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix.GridPopulation;
import msi.gama.metamodel.topology.grid.IGrid;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gaml.extensions.maths.pde.diffusion.statements.DiffusionStatement.DiffusionValidator;

@facets(value = {
		@facet(name = IKeyword.VAR, type = IType.ID, optional = false, doc = @doc("the variable to be diffused") ),
		@facet(name = IKeyword.ON, type = IType.CONTAINER, optional = false, doc = @doc("the list of agents (in general cells of a grid), on which the diffusion will occur") ),
		@facet(name = "mat_diffu", type = IType.MATRIX, optional = true, doc = @doc("the diffusion matrix (can have any size)") ),
		@facet(name = IKeyword.METHOD, type = IType.ID, optional = true, values = { "convolution",
				"dot_product" }, doc = @doc("the diffusion method") ),
		@facet(name = IKeyword.MASK, type = IType.MATRIX, optional = true, doc = @doc("a matrix masking the diffusion (matrix created from a image for example)") ),
		@facet(name = "proportion", type = IType.FLOAT, optional = true, doc = @doc("a diffusion rate") ),
		@facet(name = "radius", type = IType.INT, optional = true, doc = @doc("a diffusion radius") ),
		@facet(name = "variation", type = IType.FLOAT, optional = true, doc = @doc("an absolute value to decrease at each neighbors") ),
		@facet(name = IKeyword.CYCLE_LENGTH, type = IType.INT, optional = true, doc = @doc("the number of diffusion operation applied in one simulation step") ) }, omissible = IKeyword.VAR)
@symbol(name = { "diffusion" }, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@validator(DiffusionValidator.class)
@doc(value = "This statements allows a value to diffuse among a species on agents (generally on a grid) depending on a given diffusion matrix.", usages = {
		@usage(value = "A basic example of diffusion of the variable phero defined in the species cells, given a diffusion matrix math_diff is:", examples = {
				@example(value = "matrix<float> math_diff <- matrix([[1/9,1/9,1/9],[1/9,1/9,1/9],[1/9,1/9,1/9]]);", isExecutable = false),
				@example(value = "diffusion var: phero on: cells mat_diffu: math_diff;", isExecutable = false) }),
		@usage(value = "The diffusion can be masked by obstacles, created from a bitmap image:", examples = {
				@example(value = "diffusion var: phero on: cells mat_diffu: math_diff mask: mymask;", isExecutable = false) }),
		@usage(value = "A convenient way to have an uniform diffusion in a given radius is (which is equivalent to the above diffusion):", examples = {
				@example(value = "diffusion var: phero on: cells proportion: 1/9 radius: 1;", isExecutable = false) }) })
public class DiffusionStatement extends AbstractStatement {

	// public static Class VALIDATOR = DiffusionValidator.class;

	public static class DiffusionValidator implements IDescriptionValidator {

		@Override
		public void validate(final IDescription desc) {
			IExpression spec = desc.getFacets().getExpr("on");
			if (spec.getType().getTitle().split("\\[")[0].equals(Types.SPECIES.toString())) {
				if (!desc.getSpeciesDescription(spec.getName()).isGrid()) {
					desc.error("Diffusions can only be executed on grid species", IGamlIssue.GENERAL);
				}
			} else {
				if (!spec.getType().getContentType().isAgentType())
					desc.error("Diffusions can only be executed on list of agents", IGamlIssue.GENERAL);
			}
			IExpressionDescription mat_diffu = desc.getFacets().get("mat_diffu");
			IExpressionDescription propor = desc.getFacets().get("proportion");
			IExpressionDescription radius = desc.getFacets().get("radius");
			IExpressionDescription variation = desc.getFacets().get("variation");
			if (propor != null && mat_diffu != null) {
				desc.error("\"mat_diffu:\" and \"proportion:\" can not be used at the same time", IGamlIssue.GENERAL);
			}

			if (radius != null && propor == null) {
				desc.error("\"radius:\" can be used only with \"proportion:\"", IGamlIssue.GENERAL);
			}
			
			if (variation != null && propor == null) {
				desc.error("\"radius:\" can be used only with \"proportion:\"", IGamlIssue.GENERAL);
			}
		}
	}

	private boolean is_torus = false;
	private String var_diffu = "";
	// true for convolution, false for dot_product
	private boolean method_diffu = true;
	boolean initialized = false;
	int cLen = 1;
	double[][] mask, mat_diffu;
	List<Integer> agents;
	IExpression onExpr = getFacet(IKeyword.ON);
	String envName = getLiteral(IKeyword.ENVIRONMENT);
	private double proportion;
	private double variation;
	private int range;

	public DiffusionStatement(final IDescription desc) {
		super(desc);
		method_diffu = getLiteral(IKeyword.METHOD, "convolution").equals("convolution");
		if (envName == null) {
			SpeciesDescription s = onExpr.getType().getContentType().getSpecies();
			envName = s.getName();
		}

	}

	double[] input, output;
	int nbRows, nbCols;

	private IGrid getEnvironment(final IScope scope) {
		return (IGrid) scope.getSimulationScope().getPopulationFor(envName).getTopology().getPlaces();
	}

	public double[][] translateMatrix(final IScope scope, final IMatrix mm) {
		if (mm == null) {
			return null;
		}
		int rows = mm.getRows(scope);
		int cols = mm.getCols(scope);
		double[][] res = new double[cols][rows];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				res[j][i] = Cast.asFloat(scope, mm.get(scope, j, i));
			}
		}
		return res;
	}
	
	public double[][] translateAndComputeMask(final IScope scope, final IMatrix mm) {
		if (mm == null) {
			return null;
		}
		int rows = mm.getRows(scope);
		int cols = mm.getCols(scope);
		double[][] res = new double[cols][rows];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (Cast.asFloat(scope, mm.get(scope, j, i))<-1)
					res[j][i] = 0;
				else
					res[j][i] = 1;
			}
		}
		return res;
	}

	private double[][] computeMatrix(double[][] basicMatrix, int numberOfIteration) {
		double[][] input_mat_diffu = basicMatrix;
		for (int nb = 2; nb <= numberOfIteration; nb++) {
			double[][] output_mat_diffu = new double[(basicMatrix.length - 1) * nb + 1][(basicMatrix[0].length - 1) * nb
					+ 1];
			for (int i = 0; i < output_mat_diffu.length; i++) {
				Arrays.fill(output_mat_diffu[i], 0);
			}
			for (int i = 0; i < input_mat_diffu.length; i++) {
				for (int j = 0; j < input_mat_diffu[0].length; j++) {
					for (int ii = 0; ii < basicMatrix.length; ii++) {
						for (int jj = 0; jj < basicMatrix[0].length; jj++) {
							output_mat_diffu[i + ii][j + jj] += input_mat_diffu[i][j] * basicMatrix[ii][jj];
						}
					}
				}
			}
			input_mat_diffu = output_mat_diffu;
		}
		return input_mat_diffu;
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {

		cLen = Cast.asInt(scope, getFacetValue(scope, IKeyword.CYCLE_LENGTH, 1));
		mask = translateAndComputeMask(scope, Cast.asMatrix(scope, getFacetValue(scope, IKeyword.MASK)));
		mat_diffu = translateMatrix(scope, Cast.asMatrix(scope, getFacetValue(scope, "mat_diffu")));
		var_diffu = Cast.asString(scope, getFacetValue(scope, IKeyword.VAR));
		
		if (mat_diffu == null) {
			// build a diffusion matrix from proportion, variation and range parameters
			proportion = Cast.asFloat(scope, getFacetValue(scope, "proportion"));
			variation = Cast.asFloat(scope, getFacetValue(scope, "variation"));
			range = Cast.asInt(scope, getFacetValue(scope, "radius"));
			
			int nb_neighbors = 4;
			mat_diffu = new double[3][3];
			if (nb_neighbors == 8) {
				for (int i = 0 ; i < 3 ; i++) {
					for (int j = 0 ; j < 3 ; j++) {
						mat_diffu[i][j] = proportion/9.0;
					}
				}
				mat_diffu[1][1] = 1.0/9.0; // central cell
			}
			if (nb_neighbors == 4) {
				mat_diffu[0][1] = 1.0/5.0;
				mat_diffu[1][0] = 1.0/5.0;
				mat_diffu[1][2] = 1.0/5.0;
				mat_diffu[2][1] = 1.0/5.0;
				mat_diffu[1][1] = 1.0/5.0;
			}
			if (range>1) {
				mat_diffu = computeMatrix(mat_diffu,range);
			}
			int mat_diff_size = range*2+1;
			for (int i = 0 ; i < mat_diff_size ; i++) {
				for (int j = 0 ; j < mat_diff_size ; j++) {
					int distanceFromCenter = Math.max(Math.abs(i - mat_diff_size/2),Math.abs(j - mat_diff_size/2));
					mat_diffu[i][j] = mat_diffu[i][j] - distanceFromCenter*variation;
					if (mat_diffu[i][j] < 0) mat_diffu[i][j] = 0;
				}
			}
//			mat_diffu[range][range] = 0;
		}

		if (cLen != 1) {
			mat_diffu = computeMatrix(mat_diffu, cLen);
		}

		Object obj = getFacetValue(scope, IKeyword.ON);
		GridPopulation pop = null;

		if (obj instanceof ISpecies) {
			// the diffusion is applied to the whole grid
			if (((ISpecies) obj).isGrid()) {
				pop = (GridPopulation) ((ISpecies) obj).getPopulation(scope);
				if (mask == null) {
					mask = new double[pop.getNbCols()][pop.getNbRows()];
					for (int i = 0; i < mask.length; i++) {
						for (int j = 0; j < mask[0].length; j++) {
							mask[i][j] = 1;
						}
					}
				}
			}
		} else {
			// the diffusion is applied just to a certain part of the grid.
			// Search the mask.
			IList<IAgent> ags = Cast.asList(scope, obj);
			if (!ags.isEmpty()) {
				ISpecies sp = ags.get(0).getSpecies();
				if (sp.isGrid()) {
					pop = (GridPopulation) sp.getPopulation(scope);
					if (mask == null) {
						mask = new double[pop.getNbCols()][pop.getNbRows()];
						for (int i = 0; i < mask.length; i++) {
							for (int j = 0; j < mask[0].length; j++) {
								mask[i][j] = 0;
							}
						}
					}
					for (IAgent ag : ags) {
						mask[ag.getIndex() - ag.getIndex() / pop.getNbCols() * pop.getNbCols()][ag.getIndex()
								/ pop.getNbCols()] = 1;
					}
				} else {
					throw GamaRuntimeException.error("Diffusion statement works only on grid agents", scope);
				}
			}
		}
		agents = new ArrayList<Integer>();

		int rowMax = pop.getNbRows();
		int colMax = pop.getNbCols();
		for (int colNb = 0; colNb < pop.getNbCols(); colNb++) {
			for (int rowNb = 0; rowNb < pop.getNbRows(); rowNb++) {
				if ((Cast.asFloat(scope, pop.getAgent(colNb, rowNb).getDirectVarValue(scope, var_diffu)) != 0)
						&& (mask[colNb][rowNb] != 0)) {
					int agentIndex = pop.getAgent(colNb, rowNb).getIndex();
					for (int i = 0; i < mat_diffu.length; i++) {
						for (int j = 0; j < mat_diffu[0].length; j++) {
							int rowIdx = (int) (agentIndex / colMax) + i - mat_diffu.length / 2;
							int colIdx = agentIndex - colMax * (int) (agentIndex / colMax) + j
									- mat_diffu[0].length / 2;
							if (pop.getTopology().isTorus()) {
								if (rowIdx < 0) {
									rowIdx = rowIdx + rowMax;
								} else if (rowIdx >= rowMax) {
									rowIdx = rowIdx - rowMax;
								}
								if (colIdx < 0) {
									colIdx = colIdx + colMax;
								} else if (colIdx >= colMax) {
									colIdx = colIdx - colMax;
								}
							}
							if (!agents.contains(rowIdx * colMax + (colIdx)) && (rowIdx >= 0) && (rowIdx < rowMax)
									&& (colIdx >= 0) && (colIdx < colMax)) {
								agents.add(rowIdx * colMax + colIdx);
							}
						}
					}
				}
			}
		}

		getEnvironment(scope).diffuseVariableWithMatrix(scope, method_diffu, mat_diffu, mask, var_diffu, pop, agents);

		return null;
	}
}
