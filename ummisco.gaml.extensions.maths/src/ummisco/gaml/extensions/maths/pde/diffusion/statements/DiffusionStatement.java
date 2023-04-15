/*******************************************************************************************************
 *
 * DiffusionStatement.java, in ummisco.gaml.extensions.maths, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package ummisco.gaml.extensions.maths.pde.diffusion.statements;

import java.util.Arrays;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.topology.grid.FieldDiffuser;
import msi.gama.metamodel.topology.grid.IDiffusionTarget;
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
import msi.gama.runtime.concurrent.SimulationLocal;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;
import ummisco.gaml.extensions.maths.pde.diffusion.statements.DiffusionStatement.DiffusionValidator;

/**
 * The Class DiffusionStatement.
 */
@facets (
		value = { @facet (
				name = IKeyword.VAR,
				type = IType.ID,
				optional = false,
				doc = @doc ("the variable to be diffused. If diffused over a field, then this name will serve to identify the diffusion")),
				@facet (
						name = IKeyword.ON,
						type = { IType.SPECIES, IType.FIELD, IType.LIST },
						optional = false,
						doc = @doc ("the list of agents (in general cells of a grid), or a field on which the diffusion will occur")),
				@facet (
						name = IKeyword.MATRIX,
						type = IType.MATRIX,
						of = IType.FLOAT,
						optional = true,
						doc = @doc ("the diffusion matrix (\"kernel\" or \"filter\" in image processing). Can have any size, as long as dimensions are odd values.")),
				@facet (
						name = IKeyword.METHOD,
						type = IType.ID,
						optional = true,
						values = { IKeyword.CONVOLUTION, "dot_product" },
						doc = @doc ("the diffusion method. One of 'convolution' or 'dot_product'")),
				@facet (
						name = IKeyword.MIN,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("if a value is smaller than this value, it will not be diffused. By default, this value is equal to 0.0. This value cannot be smaller than 0.")),
				@facet (
						name = IKeyword.MASK,
						type = IType.MATRIX,
						of = IType.FLOAT,
						optional = true,
						doc = @doc ("a matrix that masks the diffusion ( created from an image for instance). The cells corresponding to the values smaller than \"-1\" in the mask matrix will not diffuse, and the other will diffuse.")),
				@facet (
						name = IKeyword.PROPORTION,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("a diffusion rate")),
				@facet (
						name = IKeyword.PROPAGATION,
						type = IType.LABEL,
						values = { IKeyword.DIFFUSION, IKeyword.GRADIENT },
						optional = true,
						doc = @doc ("represents both the way the signal is propagated and the way to treat multiple propagation of the same signal occurring at once from different places. If propagation equals 'diffusion', the intensity of a signal is shared between its neighbors with respect to 'proportion', 'variation' and the number of neighbors of the environment places (4, 6 or 8). I.e., for a given signal S propagated from place P, the value transmitted to its N neighbors is : S' = (S / N / proportion) - variation. The intensity of S is then diminished by S `*` proportion on P. In a diffusion, the different signals of the same name see their intensities added to each other on each place. If propagation equals 'gradient', the original intensity is not modified, and each neighbors receives the intensity : S / proportion - variation. If multiple propagation occur at once, only the maximum intensity is kept on each place. If 'propagation' is not defined, it is assumed that it is equal to 'diffusion'.")),
				@facet (
						name = IKeyword.RADIUS,
						type = IType.INT,
						optional = true,
						doc = @doc ("a diffusion radius (in number of cells from the center)")),
				@facet (
						name = IKeyword.VARIATION,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("an absolute value to decrease at each neighbors")),
				@facet (
						name = IKeyword.CYCLE_LENGTH,
						type = IType.INT,
						optional = true,
						doc = @doc ("the number of diffusion operation applied in one simulation step")),
				@facet (
						name = IKeyword.AVOID_MASK,
						type = IType.BOOL,
						optional = true,
						doc = @doc ("if true, the value will not be diffused in the masked cells, but will "
								+ "be restitute to the neighboring cells, multiplied by the proportion value (no signal lost)."
								+ " If false, the value will be diffused in the masked cells, but masked cells "
								+ "won't diffuse the value afterward (lost of signal). (default value : false)")) },
		omissible = IKeyword.VAR)
@symbol (
		name = { IKeyword.DIFFUSE, IKeyword.DIFFUSION },
		kind = ISymbolKind.SINGLE_STATEMENT,
		with_sequence = false,
		concept = { IConcept.MATH, IConcept.DIFFUSION })
@inside (
		kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@validator (DiffusionValidator.class)
@doc (
		value = "This statements allows a value to diffuse among a species on agents (generally on a grid) depending on a given diffusion matrix.",
		usages = { @usage (
				value = "A basic example of diffusion of the variable phero defined in the species cells, given a diffusion matrix math_diff is:",
				examples = { @example (
						value = "matrix<float> math_diff <- matrix([[1/9,1/9,1/9],[1/9,1/9,1/9],[1/9,1/9,1/9]]);",
						isExecutable = false),
						@example (
								value = "diffuse var: phero on: cells matrix: math_diff;",
								isExecutable = false) }),
				@usage (
						value = "The diffusion can be masked by obstacles, created from a bitmap image:",
						examples = { @example (
								value = "diffuse var: phero on: cells matrix: math_diff mask: mymask;",
								isExecutable = false) }),
				@usage (
						value = "A convenient way to have an uniform diffusion in a given radius is (which is equivalent to the above diffusion):",
						examples = { @example (
								value = "diffuse var: phero on: cells proportion: 1/9 radius: 1;",
								isExecutable = false) }) })
public class DiffusionStatement extends AbstractStatement {

	/**
	 * The Class DiffusionValidator.
	 */
	public static class DiffusionValidator implements IDescriptionValidator<StatementDescription> {

		@Override
		public void validate(final StatementDescription desc) {
			final String kw = desc.getKeyword();
			if (DIFFUSION.equals(kw)) {
				desc.warning("The keyword 'diffusion' is deprecated. Please use the keyword 'diffuse' instead",
						IGamlIssue.DEPRECATED);
			}
			IExpression spec = desc.getFacetExpr(IKeyword.ON);
			if (spec.getGamlType().isAgentType() && spec.getGamlType().getSpecies().isGrid()) {
				desc.error("Diffusions can only be executed on grid species", IGamlIssue.GENERAL);
			}

			// if (!spec.getGamlType().getContentType().isAgentType()) {
			// desc.error("Diffusions can only be executed on list of agents", IGamlIssue.GENERAL);
			// }

			spec = desc.getFacetExpr(IKeyword.MIN);
			if (spec != null && spec.isConst()) {
				final double min = Cast.asFloat(null, spec.literalValue());
				if (min < 0) {
					desc.error("'min_value' facet cannot accept negative values (" + spec.serialize(false) + ")",
							IGamlIssue.GENERAL);
				}
			}

			final IExpressionDescription mat_diffu = desc.getFacet(MATRIX);
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

	/**
	 * The Class DiffusionData.
	 */
	private class DiffusionData {

		/** The terrain. */
		IDiffusionTarget terrain;

		/** The variable name. */
		String variableName;

		/** The min value. */
		double minValue;

		/** The avoid mask. */
		boolean useConvolution, isGradient, avoidMask;

		/** The nb neighbors. */
		int cycleLength, nbNeighbors;

		/**
		 * Instantiates a new diffusion data.
		 *
		 * @param scope
		 *            the scope
		 */
		private DiffusionData(final IScope scope) {
			variableName = Cast.asString(scope, getFacetValue(scope, IKeyword.VAR));
			minValue = Cast.asFloat(scope, getFacetValue(scope, IKeyword.MIN, 0.0));
			if (minValue < 0) throw GamaRuntimeException.error("Facet \"min_value\" cannot be smaller than 0 !", scope);
			useConvolution = IKeyword.CONVOLUTION.equals(getLiteral(IKeyword.METHOD, IKeyword.CONVOLUTION));
			isGradient = IKeyword.GRADIENT.equals(getLiteral(IKeyword.PROPAGATION, IKeyword.DIFFUSION));
			Object on = getFacetValue(scope, IKeyword.ON);
			if (on instanceof ISpecies) {
				on = ((ISpecies) on).getPopulation(scope).getTopology().getPlaces();
			} else if (on instanceof IList) {
				Object first = ((IList) on).get(0);
				if (first instanceof IAgent) { on = ((IAgent) first).getPopulation().getTopology().getPlaces(); }
			}
			this.terrain = (IDiffusionTarget) on;
			cycleLength = Cast.asInt(scope, getFacetValue(scope, IKeyword.CYCLE_LENGTH, 1));
			nbNeighbors = terrain.getNbNeighbours();
			avoidMask = false;
			if (getFacet(IKeyword.AVOID_MASK) != null) {
				avoidMask = Cast.asBool(scope, getFacet(IKeyword.AVOID_MASK).value(scope));
			}
		}
	}

	/** The data supplier. */
	SimulationLocal<DiffusionData> dataSupplier = SimulationLocal.withInitial(DiffusionData::new);

	/**
	 * Instantiates a new diffusion statement.
	 *
	 * @param desc
	 *            the desc
	 */
	public DiffusionStatement(final IDescription desc) {
		super(desc);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		DiffusionData data = dataSupplier.get(scope);

		final IMatrix<?> rawMask = Cast.asMatrix(scope, getFacetValue(scope, IKeyword.MASK));

		double[][] diffusionMatrix =
				translateMatrix(scope, Cast.asMatrix(scope, getFacetValue(scope, IKeyword.MATRIX)));

		final double[][] mask = computeMask(scope, rawMask);

		if (diffusionMatrix == null) {
			// build a diffusion matrix from proportion, variation and range
			// parameters
			diffusionMatrix = computeDiffusionMatrix(scope);
		}
		if (data.cycleLength != 1) {
			// the cycle length is already computed in "computeDiffusionMatrix"
			// if no diffusion matrix is defined
			diffusionMatrix = computeMatrix(diffusionMatrix, data.cycleLength, data.isGradient);
		}

		FieldDiffuser.getDiffuser(scope).addDiffusion(data.variableName, data.terrain, data.useConvolution,
				data.isGradient, diffusionMatrix, mask, data.minValue, data.avoidMask);

		return null;
	}

	/**
	 * Translate matrix.
	 *
	 * @param scope
	 *            the scope
	 * @param mm
	 *            the mm
	 * @return the double[][]
	 */
	public double[][] translateMatrix(final IScope scope, final IMatrix<?> mm) {
		if (mm == null) return null;
		final int rows = mm.getRows(scope);
		final int cols = mm.getCols(scope);
		final double[][] res = new double[cols][rows];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) { res[j][i] = Cast.asFloat(scope, mm.get(scope, j, i)); }
		}
		return res;
	}

	/**
	 * Compute matrix.
	 *
	 * @param basicMatrix
	 *            the basic matrix
	 * @param numberOfIteration
	 *            the number of iteration
	 * @param is_gradient
	 *            the is gradient
	 * @return the double[][]
	 */
	private double[][] computeMatrix(final double[][] basicMatrix, final int numberOfIteration,
			final boolean is_gradient) {
		double[][] input_mat_diffu = basicMatrix;
		for (int nb = 2; nb <= numberOfIteration; nb++) {
			final double[][] output_mat_diffu =
					new double[(basicMatrix.length - 1) * nb + 1][(basicMatrix[0].length - 1) * nb + 1];
			for (final double[] element : output_mat_diffu) { Arrays.fill(element, 0); }
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

	/**
	 * Compute mask.
	 *
	 * @param scope
	 *            the scope
	 * @param mm
	 *            the mm
	 * @return the double[][]
	 */
	private double[][] computeMask(final IScope scope, final IMatrix<?> mm) {
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
		} else {
			final Object obj = getFacetValue(scope, IKeyword.ON);
			DiffusionData data = dataSupplier.get(scope);
			if (!(obj instanceof IDiffusionTarget)) {
				// the diffusion is applied just to a certain part of the grid.
				// Search the mask.
				final IList<IAgent> ags = Cast.asList(scope, obj);
				if (!ags.isEmpty()) {
					final ISpecies sp = ags.get(0).getSpecies();
					if (!sp.isGrid())
						throw GamaRuntimeException.error("Diffusion statement works only on grid agents", scope);
					mask = new double[data.terrain.getCols(scope)][data.terrain.getRows(scope)];
					for (final IAgent ag : ags) {
						int i = ag.getIndex();
						int cols = data.terrain.getCols(scope);
						mask[i - i / cols * cols][i / cols] = 1;
					}
				}
			}
		}
		return mask;
	}

	/**
	 * Compute diffusion matrix.
	 *
	 * @param scope
	 *            the scope
	 * @return the double[][]
	 */
	public double[][] computeDiffusionMatrix(final IScope scope) {
		double[][] mat_diffu;
		double proportion = Cast.asFloat(scope, getFacetValue(scope, IKeyword.PROPORTION));
		final double variation = Cast.asFloat(scope, getFacetValue(scope, IKeyword.VARIATION));
		int range = Cast.asInt(scope, getFacetValue(scope, IKeyword.RADIUS));
		DiffusionData data = dataSupplier.get(scope);
		if (range == 0) { range = 1; }
		if (proportion == 0) { proportion = 1; }
		if (data.isGradient) {
			final int mat_diff_size = range * 2 + 1;
			mat_diffu = new double[mat_diff_size][mat_diff_size];
			int distanceFromCenter = 0;
			for (int i = 0; i < mat_diff_size; i++) {
				for (int j = 0; j < mat_diff_size; j++) {
					if (data.nbNeighbors == 8) {
						distanceFromCenter = Math.max(Math.abs(i - mat_diff_size / 2), Math.abs(j - mat_diff_size / 2));
					} else {
						distanceFromCenter = Math.abs(i - mat_diff_size / 2) + Math.abs(j - mat_diff_size / 2);
					}
					mat_diffu[i][j] = proportion / Math.pow(data.nbNeighbors, distanceFromCenter)
							- distanceFromCenter * variation;
					if (mat_diffu[i][j] < 0) { mat_diffu[i][j] = 0; }
				}
			}
		} else {
			mat_diffu = new double[3][3];
			int distanceFromCenter = 0;
			if (data.nbNeighbors == 8) {
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						distanceFromCenter = Math.max(Math.abs(i - 3 / 2), Math.abs(j - 3 / 2));
						if (distanceFromCenter == 0) {
							mat_diffu[i][j] = 1.0 / (data.nbNeighbors + 1.0);
						} else if (distanceFromCenter == 1) {
							mat_diffu[i][j] = proportion / (data.nbNeighbors + 1.0);
						} else {
							mat_diffu[i][j] = 0;
						}
					}
				}
			}
			if (data.nbNeighbors == 4) {
				mat_diffu[0][1] = proportion / 5.0;
				mat_diffu[1][0] = proportion / 5.0;
				mat_diffu[1][2] = proportion / 5.0;
				mat_diffu[2][1] = proportion / 5.0;
				mat_diffu[1][1] = proportion / 5.0;
			}
			if (range > 1) { mat_diffu = computeMatrix(mat_diffu, range, data.isGradient); }
			if (variation > 0) {
				final int mat_diff_size = mat_diffu.length;
				for (int i = 0; i < mat_diff_size; i++) {
					for (int j = 0; j < mat_diff_size; j++) {
						if (data.nbNeighbors == 8) {
							distanceFromCenter =
									Math.max(Math.abs(i - mat_diff_size / 2), Math.abs(j - mat_diff_size / 2));
						} else {
							distanceFromCenter = Math.abs(i - mat_diff_size / 2) + Math.abs(j - mat_diff_size / 2);
						}
						mat_diffu[i][j] = mat_diffu[i][j] - distanceFromCenter * variation;
					}
				}
			}
		}
		return mat_diffu;
	}

}
