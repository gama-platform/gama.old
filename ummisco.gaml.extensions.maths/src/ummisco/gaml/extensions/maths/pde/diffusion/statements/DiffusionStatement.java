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

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
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
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.operators.fastmaths.CmnFastMath;
import msi.gaml.operators.fastmaths.FastMath;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gaml.extensions.maths.pde.diffusion.statements.DiffusionStatement.DiffusionValidator;

@facets(value = {
		@facet(name = IKeyword.VAR, type = IType.ID, optional = false, doc = @doc("the variable to be diffused")),
		@facet(name = IKeyword.ON, type = { IType.CONTAINER,
				IType.SPECIES }, of = IType.AGENT, optional = false, doc = @doc("the list of agents (in general cells of a grid), on which the diffusion will occur")),
		@facet(name = "mat_diffu", type = IType.MATRIX, of = IType.FLOAT, optional = true, doc = @doc(value = "the diffusion matrix (can have any size)", deprecated = "Please use 'matrix' instead")),
		@facet(name = IKeyword.MATRIX, type = IType.MATRIX, of = IType.FLOAT, optional = true, doc = @doc("the diffusion matrix (\"kernel\" or \"filter\" in image processing). Can have any size, as long as dimensions are odd values.")),
		@facet(name = IKeyword.METHOD, type = IType.ID, optional = true, values = { IKeyword.CONVOLUTION,
				"dot_product" }, doc = @doc("the diffusion method")),
		@facet(name = IKeyword.MINVALUE, type = IType.FLOAT, optional = true, doc = @doc("if a value is smaller than this value, it will not be diffused. By default, this value is equal to 0.0. This value cannot be smaller than 0.")),
		@facet(name = IKeyword.MASK, type = IType.MATRIX, of = IType.FLOAT, optional = true, doc = @doc("a matrix masking the diffusion (matrix created from a image for example). The cells corresponding to the values smaller than \"-1\" in the mask matrix will not diffuse, and the other will diffuse.")),
		@facet(name = IKeyword.PROPORTION, type = IType.FLOAT, optional = true, doc = @doc("a diffusion rate")),
		@facet(name = IKeyword.PROPAGATION, type = IType.LABEL, values = { IKeyword.DIFFUSION,
				IKeyword.GRADIENT }, optional = true, doc = @doc("represents both the way the signal is propagated and the way to treat multiple propagation of the same signal occurring at once from different places. If propagation equals 'diffusion', the intensity of a signal is shared between its neighbors with respect to 'proportion', 'variation' and the number of neighbors of the environment places (4, 6 or 8). I.e., for a given signal S propagated from place P, the value transmitted to its N neighbors is : S' = (S / N / proportion) - variation. The intensity of S is then diminished by S `*` proportion on P. In a diffusion, the different signals of the same name see their intensities added to each other on each place. If propagation equals 'gradient', the original intensity is not modified, and each neighbors receives the intensity : S / proportion - variation. If multiple propagation occur at once, only the maximum intensity is kept on each place. If 'propagation' is not defined, it is assumed that it is equal to 'diffusion'.")),
		@facet(name = IKeyword.RADIUS, type = IType.INT, optional = true, doc = @doc("a diffusion radius (in number of cells from the center)")),
		@facet(name = IKeyword.VARIATION, type = IType.FLOAT, optional = true, doc = @doc("an absolute value to decrease at each neighbors")),
		@facet(name = IKeyword.CYCLE_LENGTH, type = IType.INT, optional = true, doc = @doc("the number of diffusion operation applied in one simulation step")),
		@facet(name = IKeyword.AVOID_MASK, type = IType.BOOL, optional = true, doc = @doc("if true, the value will not be diffused in the masked cells, but will "
				+ "be restitute to the neighboring cells, multiplied by the proportion value (no signal lost)."
				+ " If false, the value will be diffused in the masked cells, but masked cells "
				+ "won't diffuse the value afterward (lost of signal). (default value : false)")) }, omissible = IKeyword.VAR)
@symbol(name = { IKeyword.DIFFUSE,
		IKeyword.DIFFUSION }, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false, concept = { IConcept.MATH,
				IConcept.DIFFUSION })
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@validator(DiffusionValidator.class)
@doc(value = "This statements allows a value to diffuse among a species on agents (generally on a grid) depending on a given diffusion matrix.", usages = {
		@usage(value = "A basic example of diffusion of the variable phero defined in the species cells, given a diffusion matrix math_diff is:", examples = {
				@example(value = "matrix<float> math_diff <- matrix([[1/9,1/9,1/9],[1/9,1/9,1/9],[1/9,1/9,1/9]]);", isExecutable = false),
				@example(value = "diffuse var: phero on: cells mat_diffu: math_diff;", isExecutable = false) }),
		@usage(value = "The diffusion can be masked by obstacles, created from a bitmap image:", examples = {
				@example(value = "diffuse var: phero on: cells mat_diffu: math_diff mask: mymask;", isExecutable = false) }),
		@usage(value = "A convenient way to have an uniform diffusion in a given radius is (which is equivalent to the above diffusion):", examples = {
				@example(value = "diffuse var: phero on: cells proportion: 1/9 radius: 1;", isExecutable = false) }) })
public class DiffusionStatement extends AbstractStatement {

	public static class DiffusionValidator implements IDescriptionValidator<StatementDescription> {

		@Override
		public void validate(final StatementDescription desc) {
			final String kw = desc.getKeyword();
			if (DIFFUSION.equals(kw)) {
				desc.warning("The keyword 'diffusion' is deprecated. Please use the keyword 'diffuse' instead",
						IGamlIssue.DEPRECATED);
			}
			IExpression spec = desc.getFacetExpr(IKeyword.ON);
			// FIXME Terrible hack, while spec.getType().getSpecies() will give
			// the species...
			if (spec.getType().getTitle().split("\\[")[0].equals(Types.SPECIES.toString())) {
				if (!desc.getSpeciesDescription(spec.getName()).isGrid()) {
					desc.error("Diffusions can only be executed on grid species", IGamlIssue.GENERAL);
				}
			} else {
				if (!spec.getType().getContentType().isAgentType()) {
					desc.error("Diffusions can only be executed on list of agents", IGamlIssue.GENERAL);
				}
			}
			spec = desc.getFacetExpr(IKeyword.MINVALUE);
			if (spec != null && spec.isConst()) {
				final double min = Cast.asFloat(null, spec.literalValue());
				if (min < 0) {
					desc.error("'min_value' facet cannot accept negative values (" + spec.serialize(false) + ")",
							IGamlIssue.GENERAL);
				}
			}

			IExpressionDescription mat_diffu = desc.getFacet("mat_diffu");
			if (mat_diffu == null) {
				mat_diffu = desc.getFacet(MATRIX);
			}
			final IExpressionDescription propor = desc.getFacet(IKeyword.PROPORTION);
			final IExpressionDescription propagation = desc.getFacet(IKeyword.PROPAGATION);
			final IExpressionDescription radius = desc.getFacet(IKeyword.RADIUS);
			final IExpressionDescription variation = desc.getFacet(IKeyword.VARIATION);

			// conflict diffusion matrix /vs/ parameters
			if (propor != null && mat_diffu != null) {
				desc.error("\"matrix:\" and \"proportion:\" can not be used at the same time", IGamlIssue.GENERAL);
			}
			if (propagation != null && mat_diffu != null) {
				desc.error("\"matrix:\" and \"propagation:\" can not be used at the same time", IGamlIssue.GENERAL);
			}
			if (mat_diffu != null && radius != null) {
				desc.error("\"matrix:\" and \"radius:\" can not be used at the same time", IGamlIssue.GENERAL);
			}
			if (mat_diffu != null && variation != null) {
				desc.error("\"matrix:\" and \"variation:\" can not be used at the same time", IGamlIssue.GENERAL);
			}
		}
	}

	boolean initialized = false;

	final String envName;

	public DiffusionStatement(final IDescription desc) {
		super(desc);
		final SpeciesDescription s = getFacet(IKeyword.ON).getType().getContentType().getSpecies();
		envName = s.getName();

	}

	double[] input, output;
	int nbRows, nbCols;

	private IGrid getEnvironment(final IScope scope) {
		return (IGrid) scope.getSimulation().getPopulationFor(envName).getTopology().getPlaces();
	}

	public double[][] translateMatrix(final IScope scope, final IMatrix<?> mm) {
		if (mm == null) {
			return null;
		}
		final int rows = mm.getRows(scope);
		final int cols = mm.getCols(scope);
		final double[][] res = new double[cols][rows];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				res[j][i] = Cast.asFloat(scope, mm.get(scope, j, i));
			}
		}
		return res;
	}

	private double[][] computeMatrix(final double[][] basicMatrix, final int numberOfIteration,
			final boolean is_gradient) {
		double[][] input_mat_diffu = basicMatrix;
		for (int nb = 2; nb <= numberOfIteration; nb++) {
			final double[][] output_mat_diffu = new double[(basicMatrix.length - 1) * nb
					+ 1][(basicMatrix[0].length - 1) * nb + 1];
			for (int i = 0; i < output_mat_diffu.length; i++) {
				Arrays.fill(output_mat_diffu[i], 0);
			}
			for (int i = 0; i < input_mat_diffu.length; i++) {
				for (int j = 0; j < input_mat_diffu[0].length; j++) {
					for (int ii = 0; ii < basicMatrix.length; ii++) {
						for (int jj = 0; jj < basicMatrix[0].length; jj++) {
							if (is_gradient) {
								if (output_mat_diffu[i + ii][j + jj] < input_mat_diffu[i][j] * basicMatrix[ii][jj]) {
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
		final Object obj = getFacetValue(scope, IKeyword.ON);
		if (obj instanceof ISpecies) {
			// the diffusion is applied to the whole grid
			if (((ISpecies) obj).isGrid()) {
				pop = (GridPopulation) ((ISpecies) obj).getPopulation(scope);
			}
		} else {
			// the diffusion is applied just to a certain part of the grid.
			final IList<IAgent> ags = Cast.asList(scope, obj);
			if (!ags.isEmpty()) {
				final ISpecies sp = ags.get(0).getSpecies();
				if (sp.isGrid()) {
					pop = (GridPopulation) sp.getPopulation(scope);
				}
			}
		}
		return pop;
	}

	private double[][] computeMask(final IScope scope, final IMatrix<?> mm, GridPopulation pop) {
		double[][] mask = null;

		// if the mask is not null, translate the mask
		if (mm != null) {
			final int rows = mm.getRows(scope);
			final int cols = mm.getCols(scope);
			final double[][] res = new double[cols][rows];
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					if (Cast.asFloat(scope, mm.get(scope, j, i)) < -1) {
						res[j][i] = 0;
					} else {
						res[j][i] = 1;
					}
				}
			}
			mask = res;
		}

		final Object obj = getFacetValue(scope, IKeyword.ON);
		if (obj instanceof ISpecies) {
			// the diffusion is applied to the whole grid
			if (!((ISpecies) obj).isGrid()) {
				throw GamaRuntimeException.error("Diffusion statement works only on grid agents", scope);
			}
		} else {
			// the diffusion is applied just to a certain part of the grid.
			// Search the mask.
			final IList<IAgent> ags = Cast.asList(scope, obj);
			if (!ags.isEmpty()) {
				final ISpecies sp = ags.get(0).getSpecies();
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
					for (final IAgent ag : ags) {
						mask[ag.getIndex() - ag.getIndex() / pop.getNbCols() * pop.getNbCols()][ag.getIndex()
								/ pop.getNbCols()] = 1;
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
		final double variation = Cast.asFloat(scope, getFacetValue(scope, IKeyword.VARIATION));
		int range = Cast.asInt(scope, getFacetValue(scope, IKeyword.RADIUS));

		if (range == 0) {
			range = 1;
		}
		if (proportion == 0) {
			proportion = 1;
		}
		if (is_gradient) {
			final int mat_diff_size = range * 2 + 1;
			mat_diffu = new double[mat_diff_size][mat_diff_size];
			int distanceFromCenter = 0;
			for (int i = 0; i < mat_diff_size; i++) {
				for (int j = 0; j < mat_diff_size; j++) {
					if (nb_neighbors == 8) {
						distanceFromCenter = CmnFastMath.max(CmnFastMath.abs(i - mat_diff_size / 2),
								CmnFastMath.abs(j - mat_diff_size / 2));
					} else {
						distanceFromCenter = CmnFastMath.abs(i - mat_diff_size / 2)
								+ CmnFastMath.abs(j - mat_diff_size / 2);
					}
					mat_diffu[i][j] = proportion / FastMath.pow(nb_neighbors, distanceFromCenter)
							- distanceFromCenter * variation;
					if (mat_diffu[i][j] < 0) {
						mat_diffu[i][j] = 0;
					}
				}
			}
		} else {
			mat_diffu = new double[3][3];
			int distanceFromCenter = 0;
			if (nb_neighbors == 8) {
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						distanceFromCenter = CmnFastMath.max(CmnFastMath.abs(i - 3 / 2), CmnFastMath.abs(j - 3 / 2));
						if (distanceFromCenter == 0) {
							mat_diffu[i][j] = 1.0 / (nb_neighbors + 1.0);
						} else if (distanceFromCenter == 1) {
							mat_diffu[i][j] = proportion / (nb_neighbors + 1.0);
						} else {
							mat_diffu[i][j] = 0;
						}
					}
				}
			}
			if (nb_neighbors == 4) {
				mat_diffu[0][1] = proportion / 5.0;
				mat_diffu[1][0] = proportion / 5.0;
				mat_diffu[1][2] = proportion / 5.0;
				mat_diffu[2][1] = proportion / 5.0;
				mat_diffu[1][1] = proportion / 5.0;
			}
			if (range > 1) {
				mat_diffu = computeMatrix(mat_diffu, range, is_gradient);
			}
			if (variation > 0) {
				final int mat_diff_size = mat_diffu.length;
				for (int i = 0; i < mat_diff_size; i++) {
					for (int j = 0; j < mat_diff_size; j++) {
						if (nb_neighbors == 8) {
							distanceFromCenter = Math.max(CmnFastMath.abs(i - mat_diff_size / 2),
									CmnFastMath.abs(j - mat_diff_size / 2));
						} else {
							distanceFromCenter = CmnFastMath.abs(i - mat_diff_size / 2)
									+ CmnFastMath.abs(j - mat_diff_size / 2);
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

		final int cLen = Cast.asInt(scope, getFacetValue(scope, IKeyword.CYCLE_LENGTH, 1));
		final IMatrix<?> raw_mask = Cast.asMatrix(scope, getFacetValue(scope, IKeyword.MASK));
		double[][] mat_diffu = translateMatrix(scope,
				Cast.asMatrix(scope, getFacetValue(scope, "mat_diffu", getFacetValue(scope, IKeyword.MATRIX))));
		// FIXME: this one should be considered as a constant, no ?
		final String var_diffu = Cast.asString(scope, getFacetValue(scope, IKeyword.VAR));
		// true for convolution, false for dot_product
		final boolean use_convolution = getLiteral(IKeyword.METHOD, IKeyword.CONVOLUTION).equals(IKeyword.CONVOLUTION);
		final boolean is_gradient = getLiteral(IKeyword.PROPAGATION, IKeyword.DIFFUSION).equals(IKeyword.GRADIENT);
		boolean avoid_mask = false;
		if (getFacet(IKeyword.AVOID_MASK) != null) {
			avoid_mask = Cast.asBool(scope, getFacet(IKeyword.AVOID_MASK).value(scope));
		}
		final double minValue = Cast.asFloat(scope, getFacetValue(scope, IKeyword.MINVALUE, 0.0));

		final GridPopulation pop = computePopulation(scope);

		final double[][] mask = computeMask(scope, raw_mask, pop);

		if (mat_diffu == null) {
			// build a diffusion matrix from proportion, variation and range
			// parameters
			// FIXME: this one is a constant too. No need to recompute it every
			// cycle !
			final IExpression nb = pop.getSpecies().getFacet(IKeyword.NEIGHBORS);
			int nb_neighbors = 8;
			if (nb != null) {
				nb_neighbors = Cast.asInt(scope, nb.value(scope));
			}
			mat_diffu = computeDiffusionMatrix(scope, nb_neighbors, is_gradient);
		}
		if (cLen != 1) {
			// the cycle length is already computed in "computeDiffusionMatrix"
			// if no diffusion matrix is defined
			mat_diffu = computeMatrix(mat_diffu, cLen, is_gradient);
		}

		if (minValue < 0) {
			throw GamaRuntimeException.error("Facet \"min_value\" cannot be smaller than 0 !", scope);
		}

		if (pop != null) {
			getEnvironment(scope).diffuseVariable(scope, use_convolution, is_gradient, mat_diffu, mask, var_diffu, pop,
					minValue, avoid_mask);
		}

		return null;
	}
}
