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

import java.util.Arrays;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix.GridPopulation;
import msi.gama.metamodel.topology.grid.IGrid;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.fastmaths.*;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.*;
import ummisco.gaml.extensions.maths.pde.diffusion.statements.DiffusionStatement.DiffusionValidator;

@facets(
	value = { @facet(name = IKeyword.VAR, type = IType.ID, optional = false, doc = @doc("the variable to be diffused")),
		@facet(name = IKeyword.ON,
			type = IType.CONTAINER,
			optional = false,
			doc = @doc("the list of agents (in general cells of a grid), on which the diffusion will occur")),
		@facet(name = "mat_diffu",
			type = IType.MATRIX,
			optional = true,
			doc = @doc(value = "the diffusion matrix (can have any size)", deprecated = "Please use 'matrix' instead")),
		@facet(name = IKeyword.MATRIX,
			type = IType.MATRIX,
			optional = true,
			doc = @doc("the diffusion matrix (can have any size)")),
		@facet(name = IKeyword.METHOD,
			type = IType.ID,
			optional = true,
			values = { "convolution", "dot_product" },
			doc = @doc("the diffusion method")),
		@facet(name = IKeyword.MASK,
			type = IType.MATRIX,
			optional = true,
			doc = @doc("a matrix masking the diffusion (matrix created from a image for example). The cells corresponding to the values smaller than \"-1\" in the mask matrix will not diffuse, and the other will diffuse.")),
		@facet(name = IKeyword.PROPORTION, type = IType.FLOAT, optional = true, doc = @doc("a diffusion rate")),
		@facet(name = IKeyword.PROPAGATION,
			type = IType.LABEL,
			values = { IKeyword.DIFFUSION, IKeyword.GRADIENT },
			optional = true,
			doc = @doc("represents both the way the signal is propagated and the way to treat multiple propagations of the same signal occuring at once from different places. If propagation equals 'diffusion', the intensity of a signal is shared between its neighbours with respect to 'proportion', 'variation' and the number of neighbours of the environment places (4, 6 or 8). I.e., for a given signal S propagated from place P, the value transmitted to its N neighbours is : S' = (S / N / proportion) - variation. The intensity of S is then diminished by S `*` proportion on P. In a diffusion, the different signals of the same name see their intensities added to each other on each place. If propagation equals 'gradient', the original intensity is not modified, and each neighbours receives the intensity : S / proportion - variation. If multiple propagations occur at once, only the maximum intensity is kept on each place. If 'propagation' is not defined, it is assumed that it is equal to 'diffusion'.")),
		@facet(name = "radius",
			type = IType.INT,
			optional = true,
			doc = @doc("a diffusion radius (in number of cells from the center)")),
		@facet(name = IKeyword.VARIATION,
			type = IType.FLOAT,
			optional = true,
			doc = @doc("an absolute value to decrease at each neighbors")),
		@facet(name = IKeyword.CYCLE_LENGTH,
			type = IType.INT,
			optional = true,
			doc = @doc("the number of diffusion operation applied in one simulation step")) },
	omissible = IKeyword.VAR)
@symbol(name = { IKeyword.DIFFUSE, IKeyword.DIFFUSION }, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@validator(DiffusionValidator.class)
@doc(
	value = "This statements allows a value to diffuse among a species on agents (generally on a grid) depending on a given diffusion matrix.",
	usages = {
		@usage(
			value = "A basic example of diffusion of the variable phero defined in the species cells, given a diffusion matrix math_diff is:",
			examples = {
				@example(value = "matrix<float> math_diff <- matrix([[1/9,1/9,1/9],[1/9,1/9,1/9],[1/9,1/9,1/9]]);",
					isExecutable = false),
				@example(value = "diffuse var: phero on: cells mat_diffu: math_diff;", isExecutable = false) }),
		@usage(value = "The diffusion can be masked by obstacles, created from a bitmap image:",
			examples = { @example(value = "diffuse var: phero on: cells mat_diffu: math_diff mask: mymask;",
				isExecutable = false) }),
		@usage(
			value = "A convenient way to have an uniform diffusion in a given radius is (which is equivalent to the above diffusion):",
			examples = {
				@example(value = "diffuse var: phero on: cells proportion: 1/9 radius: 1;", isExecutable = false) }) })
public class DiffusionStatement extends AbstractStatement {

	// public static Class VALIDATOR = DiffusionValidator.class;

	public static class DiffusionValidator implements IDescriptionValidator {

		@Override
		public void validate(final IDescription desc) {
			String kw = desc.getKeyword();
			if ( DIFFUSION.equals(kw) ) {
				desc.warning("The keyword 'diffusion' is deprecated. Please use the keyword 'diffuse' instead",
					IGamlIssue.DEPRECATED);
			}
			IExpression spec = desc.getFacets().getExpr("on");
			if ( spec.getType().getTitle().split("\\[")[0].equals(Types.SPECIES.toString()) ) {
				if ( !desc.getSpeciesDescription(spec.getName()).isGrid() ) {
					desc.error("Diffusions can only be executed on grid species", IGamlIssue.GENERAL);
				}
			} else {
				if ( !spec.getType().getContentType().isAgentType() ) {
					desc.error("Diffusions can only be executed on list of agents", IGamlIssue.GENERAL);
				}
			}
			IExpressionDescription mat_diffu = desc.getFacets().get("mat_diffu");
			if ( mat_diffu == null ) {
				mat_diffu = desc.getFacets().get(MATRIX);
			}
			IExpressionDescription propor = desc.getFacets().get("proportion");
			IExpressionDescription propagation = desc.getFacets().get("propagation");
			IExpressionDescription radius = desc.getFacets().get("radius");
			IExpressionDescription variation = desc.getFacets().get("variation");
			IExpressionDescription cycleLength = desc.getFacets().get(IKeyword.CYCLE_LENGTH);

			// conflict diffusion matrix /vs/ parameters
			if ( propor != null && mat_diffu != null ) {
				desc.error("\"matrix:\" and \"proportion:\" can not be used at the same time", IGamlIssue.GENERAL);
			}
			if ( mat_diffu != null && radius != null ) {
				desc.error("\"matrix:\" and \"radius:\" can not be used at the same time", IGamlIssue.GENERAL);
			}
			if ( mat_diffu != null && variation != null ) {
				desc.error("\"matrix:\" and \"variation:\" can not be used at the same time", IGamlIssue.GENERAL);
			}
		}
	}

	boolean initialized = false;
	String envName = getLiteral(IKeyword.ENVIRONMENT);

	public DiffusionStatement(final IDescription desc) {
		super(desc);
		if ( envName == null ) {
			SpeciesDescription s = getFacet(IKeyword.ON).getType().getContentType().getSpecies();
			envName = s.getName();
		}

	}

	double[] input, output;
	int nbRows, nbCols;

	private IGrid getEnvironment(final IScope scope) {
		return (IGrid) scope.getSimulationScope().getPopulationFor(envName).getTopology().getPlaces();
	}

	public double[][] translateMatrix(final IScope scope, final IMatrix<?> mm) {
		if ( mm == null ) { return null; }
		int rows = mm.getRows(scope);
		int cols = mm.getCols(scope);
		double[][] res = new double[cols][rows];
		for ( int i = 0; i < rows; i++ ) {
			for ( int j = 0; j < cols; j++ ) {
				res[j][i] = Cast.asFloat(scope, mm.get(scope, j, i));
			}
		}
		return res;
	}

	private double[][] computeMatrix(final double[][] basicMatrix, final int numberOfIteration,
		final boolean is_gradient) {
		double[][] input_mat_diffu = basicMatrix;
		for ( int nb = 2; nb <= numberOfIteration; nb++ ) {
			double[][] output_mat_diffu =
				new double[(basicMatrix.length - 1) * nb + 1][(basicMatrix[0].length - 1) * nb + 1];
			for ( int i = 0; i < output_mat_diffu.length; i++ ) {
				Arrays.fill(output_mat_diffu[i], 0);
			}
			for ( int i = 0; i < input_mat_diffu.length; i++ ) {
				for ( int j = 0; j < input_mat_diffu[0].length; j++ ) {
					for ( int ii = 0; ii < basicMatrix.length; ii++ ) {
						for ( int jj = 0; jj < basicMatrix[0].length; jj++ ) {
							if ( is_gradient ) {
								if ( output_mat_diffu[i + ii][j + jj] < input_mat_diffu[i][j] * basicMatrix[ii][jj] ) {
									output_mat_diffu[i + ii][j + jj] = input_mat_diffu[i][j] * basicMatrix[ii][jj];
								}
							} else {
								output_mat_diffu[i + ii][j + jj] += input_mat_diffu[i][j] * basicMatrix[ii][jj];
							}
						}
					}
				}
			}
			input_mat_diffu = output_mat_diffu;
		}
		return input_mat_diffu;
	}

	private GridPopulation computePopulation(final IScope scope) {
		GridPopulation pop = null;
		Object obj = getFacetValue(scope, IKeyword.ON);
		if ( obj instanceof ISpecies ) {
			// the diffusion is applied to the whole grid
			if ( ((ISpecies) obj).isGrid() ) {
				pop = (GridPopulation) ((ISpecies) obj).getPopulation(scope);
			}
		} else {
			// the diffusion is applied just to a certain part of the grid.
			IList<IAgent> ags = Cast.asList(scope, obj);
			if ( !ags.isEmpty() ) {
				ISpecies sp = ags.get(0).getSpecies();
				if ( sp.isGrid() ) {
					pop = (GridPopulation) sp.getPopulation(scope);
				}
			}
		}
		return pop;
	}

	private double[][] computeMask(final IScope scope, final IMatrix<?> mm, GridPopulation pop) {
		double[][] mask = null;

		// if the mask is not null, translate the mask
		if ( mm != null ) {
			int rows = mm.getRows(scope);
			int cols = mm.getCols(scope);
			double[][] res = new double[cols][rows];
			for ( int i = 0; i < rows; i++ ) {
				for ( int j = 0; j < cols; j++ ) {
					if ( Cast.asFloat(scope, mm.get(scope, j, i)) < -1 ) {
						res[j][i] = 0;
					} else {
						res[j][i] = 1;
					}
				}
			}
			mask = res;
		}

		Object obj = getFacetValue(scope, IKeyword.ON);
		if ( obj instanceof ISpecies ) {
			// the diffusion is applied to the whole grid
			if ( ((ISpecies) obj).isGrid() ) {
				pop = (GridPopulation) ((ISpecies) obj).getPopulation(scope);
				if ( mask == null ) {
					// the mask is null. Let's build a "fake" mask equal to 1 everywhere.
					mask = new double[pop.getNbCols()][pop.getNbRows()];
					for ( int i = 0; i < mask.length; i++ ) {
						for ( int j = 0; j < mask[0].length; j++ ) {
							mask[i][j] = 1;
						}
					}
				}
			}
		} else {
			// the diffusion is applied just to a certain part of the grid.
			// Search the mask.
			IList<IAgent> ags = Cast.asList(scope, obj);
			if ( !ags.isEmpty() ) {
				ISpecies sp = ags.get(0).getSpecies();
				if ( sp.isGrid() ) {
					pop = (GridPopulation) sp.getPopulation(scope);
					if ( mask == null ) {
						mask = new double[pop.getNbCols()][pop.getNbRows()];
						for ( int i = 0; i < mask.length; i++ ) {
							for ( int j = 0; j < mask[0].length; j++ ) {
								mask[i][j] = 0;
							}
						}
					}
					for ( IAgent ag : ags ) {
						mask[ag.getIndex() - ag.getIndex() / pop.getNbCols() * pop.getNbCols()][ag.getIndex() /
							pop.getNbCols()] = 1;
					}
				} else {
					throw GamaRuntimeException.error("Diffusion statement works only on grid agents", scope);
				}
			}
		}
		return mask;
	}

	public double[][] computeDiffusionMatrix(final IScope scope, final int nb_neighbors, final boolean is_gradient) {
		double[][] mat_diffu;
		double proportion = Cast.asFloat(scope, getFacetValue(scope, IKeyword.PROPORTION));
		double variation = Cast.asFloat(scope, getFacetValue(scope, IKeyword.VARIATION));
		int range = Cast.asInt(scope, getFacetValue(scope, "radius"));
		if ( range == 0 ) {
			range = 1;
		}
		if ( proportion == 0 ) {
			proportion = 1;
		}
		if ( is_gradient ) {
			int mat_diff_size = range * 2 + 1;
			mat_diffu = new double[mat_diff_size][mat_diff_size];
			int distanceFromCenter = 0;
			for ( int i = 0; i < mat_diff_size; i++ ) {
				for ( int j = 0; j < mat_diff_size; j++ ) {
					if ( nb_neighbors == 8 ) {
						distanceFromCenter =
							CmnFastMath.max(FastMath.abs(i - mat_diff_size / 2), CmnFastMath.abs(j - mat_diff_size / 2));
					} else {
						distanceFromCenter =
							CmnFastMath.abs(i - mat_diff_size / 2) + CmnFastMath.abs(j - mat_diff_size / 2);
					}
					mat_diffu[i][j] =
						proportion / FastMath.pow(nb_neighbors, distanceFromCenter) - distanceFromCenter * variation;
					if ( mat_diffu[i][j] < 0 ) {
						mat_diffu[i][j] = 0;
					}
				}
			}
		} else {
			mat_diffu = new double[3][3];
			int distanceFromCenter = 0;
			if ( nb_neighbors == 8 ) {
				for ( int i = 0; i < 3; i++ ) {
					for ( int j = 0; j < 3; j++ ) {
						if ( nb_neighbors == 8 ) {
							distanceFromCenter = CmnFastMath.max(FastMath.abs(i - 3 / 2), CmnFastMath.abs(j - 3 / 2));
						} else {
							distanceFromCenter = CmnFastMath.abs(i - 3 / 2) + CmnFastMath.abs(j - 3 / 2);
						}
						if ( distanceFromCenter == 0 ) {
							mat_diffu[i][j] = 1 / (nb_neighbors + 1);
						} else if ( distanceFromCenter == 1 ) {
							mat_diffu[i][j] = proportion / (nb_neighbors + 1);
						} else {
							mat_diffu[i][j] = 0;
						}
					}
				}
			}
			if ( nb_neighbors == 4 ) {
				mat_diffu[0][1] = proportion / 5.0;
				mat_diffu[1][0] = proportion / 5.0;
				mat_diffu[1][2] = proportion / 5.0;
				mat_diffu[2][1] = proportion / 5.0;
				mat_diffu[1][1] = proportion / 5.0;
			}
			if ( range > 1 ) {
				mat_diffu = computeMatrix(mat_diffu, range, is_gradient);
			}
			if ( variation > 0 ) {
				int mat_diff_size = mat_diffu.length;
				for ( int i = 0; i < mat_diff_size; i++ ) {
					for ( int j = 0; j < mat_diff_size; j++ ) {
						if ( nb_neighbors == 8 ) {
							distanceFromCenter =
								Math.max(FastMath.abs(i - mat_diff_size / 2), CmnFastMath.abs(j - mat_diff_size / 2));
						} else {
							distanceFromCenter =
								CmnFastMath.abs(i - mat_diff_size / 2) + CmnFastMath.abs(j - mat_diff_size / 2);
						}
						mat_diffu[i][j] = mat_diffu[i][j] - distanceFromCenter * variation;
					}
				}
			}
		}
		return mat_diffu;
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {

		int cLen = Cast.asInt(scope, getFacetValue(scope, IKeyword.CYCLE_LENGTH, 1));
		IMatrix<?> row_mask = Cast.asMatrix(scope, getFacetValue(scope, IKeyword.MASK));
		double[][] mat_diffu = translateMatrix(scope,
			Cast.asMatrix(scope, getFacetValue(scope, "mat_diffu", getFacetValue(scope, IKeyword.MATRIX))));
		String var_diffu = Cast.asString(scope, getFacetValue(scope, IKeyword.VAR));
		// true for convolution, false for dot_product
		boolean method_diffu = getLiteral(IKeyword.METHOD, "convolution").equals("convolution");
		boolean is_gradient = getLiteral(IKeyword.PROPAGATION, "diffusion").equals("gradient");;

		GridPopulation pop = computePopulation(scope);

		double[][] mask = computeMask(scope, row_mask, pop);

		if ( mat_diffu == null ) {
			// build a diffusion matrix from proportion, variation and range parameters
			IExpression nb = pop.getSpecies().getFacet(IKeyword.NEIGHBORS);
			int nb_neighbors = 8;
			if ( nb != null ) {
				nb_neighbors = Cast.asInt(scope, nb.value(scope));
			}
			mat_diffu = computeDiffusionMatrix(scope, nb_neighbors, is_gradient);
		}

		if ( cLen != 1 ) {
			mat_diffu = computeMatrix(mat_diffu, cLen, is_gradient);
		}

		if ( pop != null ) {
			getEnvironment(scope).diffuseVariable(scope, method_diffu, is_gradient, mat_diffu, mask, var_diffu, pop);
		}

		return null;
	}
}
