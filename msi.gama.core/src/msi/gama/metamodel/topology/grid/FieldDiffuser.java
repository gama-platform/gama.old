/*******************************************************************************************************
 *
 * FieldDiffuser.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.1).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.metamodel.topology.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class FieldDiffuser.
 */
public class FieldDiffuser {

	static {
		// DEBUG.ON();
	}

	// static ThreadLocal<FieldDiffuser> CACHE = new ThreadLocal<>();

	/** The cache. */
	static LoadingCache<SimulationAgent, FieldDiffuser> CACHE =
			CacheBuilder.newBuilder().build(new CacheLoader<SimulationAgent, FieldDiffuser>() {

				@Override
				public FieldDiffuser load(final SimulationAgent sim) throws Exception {
					FieldDiffuser diffuser = new FieldDiffuser(sim.getScope());
					DEBUG.OUT("Field Diffuser created for " + sim);
					sim.postEndAction(s -> diffuser.diffuse());
					sim.postDisposeAction(s -> {
						CACHE.invalidate(sim);
						return null;
					});
					return diffuser;
				}
			});

	// static SimulationLocal<FieldDiffuser> CACHE = SimulationLocal.withInitial(s -> {
	// FieldDiffuser diffuser = new FieldDiffuser(s);
	// s.getSimulation().postEndAction(scope -> diffuser.diffuse());
	// return diffuser;
	// });

	/**
	 * Gets the diffuser.
	 *
	 * @param scope
	 *            the scope
	 * @return the diffuser
	 */
	public static FieldDiffuser getDiffuser(final IScope scope) {
		try {
			return CACHE.get(scope.getSimulation());
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** The diffusions map. */
	protected final ListMultimap<DiffusionContext, GridDiffusion> diffusionsMap =
			MultimapBuilder.hashKeys(10).arrayListValues().build();

	/** The context. */
	protected DiffusionContext context;

	/** The diffusion. */
	protected GridDiffusion diffusion;

	/** The proportion. */
	private float proportion; // in case of "avoid_mask"

	/** The scope. */
	final IScope scope;

	/** The output. */
	double[] input, output;

	/**
	 * The Class DiffusionContext.
	 */
	// Structure of the Key for the map of diffusions.
	private static class DiffusionContext {

		/** The nb rows. */
		int nbRows;

		/** The nb cols. */
		int nbCols;

		/** The is torus. */
		boolean isTorus;

		/** The target. */
		IDiffusionTarget target;

		/** The var name. */
		final String varName;

		/**
		 * Instantiates a new diffusion context.
		 *
		 * @param var_name
		 *            the var name
		 * @param pop
		 *            the pop
		 */
		public DiffusionContext(final IScope scope, final String var_name, final IDiffusionTarget pop) {
			varName = var_name;
			nbRows = pop.getRows(scope);
			nbCols = pop.getCols(scope);
			isTorus = scope.getSimulation().getTopology().isTorus();
			target = pop;
		}

		@Override
		public int hashCode() {
			return varName.hashCode() + target.hashCode();
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == this) return true;
			if (!(obj instanceof DiffusionContext otherGrid)) return false;
			return otherGrid.varName.equals(varName) && otherGrid.target.equals(target);
		}
	}

	/**
	 * The Class GridDiffusion.
	 */
	private static class GridDiffusion {

		/** The use convolution. */
		public boolean useConvolution = true;

		/** The is gradient. */
		public boolean isGradient;

		/** The diffusion matrix. */
		public double[][] mask, diffusionMatrix;

		/** The min value. */
		public double minValue;

		/** The avoid mask. */
		public boolean avoidMask;

		/**
		 * Instantiates a new grid diffusion.
		 *
		 * @param use_convolution
		 *            the use convolution
		 * @param is_gradient
		 *            the is gradient
		 * @param mat_diffu
		 *            the mat diffu
		 * @param mask
		 *            the mask
		 * @param min_value
		 *            the min value
		 * @param avoid_mask
		 *            the avoid mask
		 */
		public GridDiffusion(final boolean use_convolution, final boolean is_gradient, final double[][] mat_diffu,
				final double[][] mask, final double min_value, final boolean avoid_mask) {
			useConvolution = use_convolution;
			diffusionMatrix = mat_diffu;
			this.mask = mask;
			isGradient = is_gradient;
			minValue = min_value;
			avoidMask = avoid_mask;
		}
	}

	/**
	 * Compare arrays.
	 *
	 * @param array1
	 *            the array 1
	 * @param array2
	 *            the array 2
	 * @return true, if successful
	 */
	public boolean compareArrays(final double[][] array1, final double[][] array2) {
		if (array1 == null) return array2 == null;
		if (array2 == null || array1.length != array2.length) return false;
		for (int i = 0; i < array1.length; i++) { if (!Arrays.equals(array1[i], array2[i])) return false; }
		return true;
	}

	/**
	 * Adds the diffusion.
	 *
	 * @param varDiffu
	 *            the var diffu
	 * @param pop
	 *            the pop
	 * @param method_diffu
	 *            the method diffu
	 * @param isGradient
	 *            the is gradient
	 * @param matDiffu
	 *            the mat diffu
	 * @param theMask
	 *            the the mask
	 * @param minValue
	 *            the min value
	 * @param avoidMask
	 *            the avoid mask
	 */
	public void addDiffusion(final String varDiffu, final IDiffusionTarget pop, final boolean method_diffu,
			final boolean isGradient, final double[][] matDiffu, final double[][] theMask, final double minValue,
			final boolean avoidMask) {
		// DEBUG.OUT("Diffusion added for diffuser of " + this.scope.getSimulation() + " with scope of "
		// + scope.getSimulation() + " for var " + varDiffu);
		final GridDiffusion newGridDiff =
				new GridDiffusion(method_diffu, isGradient, matDiffu, theMask, minValue, avoidMask);
		final DiffusionContext keyValue = new DiffusionContext(scope, varDiffu, pop);
		if (diffusionsMap.containsKey(keyValue)) {
			final List<GridDiffusion> listWithSameVar = diffusionsMap.get(keyValue);
			// try to mix diffusions if possible
			for (final GridDiffusion gridToAnalyze : listWithSameVar) {
				if (gridToAnalyze != newGridDiff && gridToAnalyze.useConvolution == newGridDiff.useConvolution
						&& compareArrays(gridToAnalyze.mask, newGridDiff.mask)
						&& gridToAnalyze.isGradient == newGridDiff.isGradient
						&& gridToAnalyze.avoidMask == newGridDiff.avoidMask) {
					// we can add the two diffusion matrix
					diffusionsMap.remove(keyValue, gridToAnalyze);
					int iiLength = gridToAnalyze.diffusionMatrix.length;
					int cellNbiiToAddToGridToAnalyze = 0;
					int cellNbiiToAddToNewGrid = 0;
					if (gridToAnalyze.diffusionMatrix.length < newGridDiff.diffusionMatrix.length) {
						iiLength = newGridDiff.diffusionMatrix.length;
						cellNbiiToAddToGridToAnalyze =
								(newGridDiff.diffusionMatrix.length - gridToAnalyze.diffusionMatrix.length) / 2;
					} else if (gridToAnalyze.diffusionMatrix.length > newGridDiff.diffusionMatrix.length) {
						iiLength = newGridDiff.diffusionMatrix.length;
						cellNbiiToAddToNewGrid =
								(gridToAnalyze.diffusionMatrix.length - newGridDiff.diffusionMatrix.length) / 2;
					}
					int jjLength = gridToAnalyze.diffusionMatrix[0].length;
					int cellNbjjToAddToGridToAnalyze = 0;
					int cellNbjjToAddToNewGrid = 0;
					if (gridToAnalyze.diffusionMatrix[0].length < newGridDiff.diffusionMatrix[0].length) {
						jjLength = newGridDiff.diffusionMatrix[0].length;
						cellNbjjToAddToGridToAnalyze =
								(newGridDiff.diffusionMatrix[0].length - gridToAnalyze.diffusionMatrix[0].length) / 2;
					} else if (gridToAnalyze.diffusionMatrix[0].length > newGridDiff.diffusionMatrix[0].length) {
						jjLength = gridToAnalyze.diffusionMatrix[0].length;
						cellNbjjToAddToNewGrid =
								(gridToAnalyze.diffusionMatrix[0].length - newGridDiff.diffusionMatrix[0].length) / 2;
					}
					final double[][] newMat = new double[iiLength][jjLength];
					for (int ii = 0; ii < iiLength; ii++) {
						for (int jj = 0; jj < jjLength; jj++) {
							double result = 0;
							final int indexiForGridToAnalyze =
									gridToAnalyze.diffusionMatrix.length - iiLength + ii + cellNbiiToAddToGridToAnalyze;
							final int indexjForGridToAnalyze = gridToAnalyze.diffusionMatrix[0].length - jjLength + jj
									+ cellNbjjToAddToGridToAnalyze;
							if (indexiForGridToAnalyze >= 0
									&& indexiForGridToAnalyze < gridToAnalyze.diffusionMatrix.length
									&& indexjForGridToAnalyze >= 0
									&& indexjForGridToAnalyze < gridToAnalyze.diffusionMatrix[0].length) {
								result += gridToAnalyze.diffusionMatrix[indexiForGridToAnalyze][indexjForGridToAnalyze];
							}
							final int indexiForNewGridDiff =
									newGridDiff.diffusionMatrix.length - iiLength + ii + cellNbiiToAddToNewGrid;
							final int indexjForNewGridDiff =
									newGridDiff.diffusionMatrix[0].length - jjLength + jj + cellNbjjToAddToNewGrid;
							if (indexiForNewGridDiff >= 0 && indexiForNewGridDiff < newGridDiff.diffusionMatrix.length
									&& indexjForNewGridDiff >= 0
									&& indexjForNewGridDiff < newGridDiff.diffusionMatrix[0].length) {
								result += newGridDiff.diffusionMatrix[indexiForNewGridDiff][indexjForNewGridDiff];
							}
							newMat[ii][jj] = result;
						}
					}
					newGridDiff.diffusionMatrix = newMat;
				}
			}

		}
		diffusionsMap.put(keyValue, newGridDiff);
	}

	/**
	 * Instantiates a new field diffuser.
	 *
	 * @param scope
	 *            the scope
	 */
	public FieldDiffuser(final IScope scope) {
		this.scope = scope;
	}

	/**
	 * Load grid properties.
	 *
	 * @param pairVarGrid
	 *            the pair var grid
	 */
	public void loadGridProperties(final DiffusionContext pairVarGrid) {
		context = pairVarGrid;
	}

	/**
	 * Load diff properties.
	 *
	 * @param gridDiff
	 *            the grid diff
	 * @return true, if successful
	 */
	public boolean loadDiffProperties(final GridDiffusion gridDiff) {
		diffusion = gridDiff;
		if (gridDiff.avoidMask) {
			// compute proportion
			proportion = 0;
			for (final double[] element : diffusion.diffusionMatrix) {
				for (int j = 0; j < diffusion.diffusionMatrix[0].length; j++) { proportion += element[j]; }
			}
		}

		context.target.getValuesInto(scope, context.varName, diffusion.minValue, input);
		return true;

	}

	/**
	 * Diffusion with convolution.
	 */
	public void diffusionWithConvolution() {
		// default method : convolution

		final int kRows = diffusion.diffusionMatrix.length;
		final int kCols = diffusion.diffusionMatrix[0].length;

		final int kCenterX = kCols / 2;
		final int kCenterY = kRows / 2;

		for (int i = 0; i < context.nbCols; ++i) // output rows
		{
			for (int j = 0; j < context.nbRows; ++j) // output columns
			{
				double value_to_redistribute = 0;
				final ArrayList<int[]> non_masked_cells = new ArrayList<>();
				for (int m = 0; m < kRows; ++m) // kernel rows
				{
					for (int n = 0; n < kCols; ++n) // kernel columns
					{
						// index of input signal, used for checking boundary
						int ii = i + n - kCenterX;
						int jj = j + m - kCenterY;
						// ignore input samples which are out of bound
						if (context.isTorus) {
							if (ii < 0) {
								ii = context.nbRows + ii;
							} else if (ii >= context.nbRows) { ii = ii - context.nbRows; }

							if (jj < 0) {
								jj = context.nbCols + jj;
							} else if (jj >= context.nbCols) { jj = jj - context.nbCols; }
						}
						// diffuse if the input value is in the grid, and if the
						// cell is not masked
						if (ii >= 0 && ii < context.nbCols && jj >= 0 && jj < context.nbRows
								&& (diffusion.mask == null || diffusion.mask[ii][jj] == 1)) {
							final double value_before_change = output[j * context.nbCols + i];
							if (output[j * context.nbCols + i] == -Double.MAX_VALUE) {
								output[j * context.nbCols + i] = input[jj * context.nbCols + ii]
										* diffusion.diffusionMatrix[kRows - m - 1][kCols - n - 1];
							} else if (diffusion.isGradient) {
								if (output[j * context.nbCols + i] < input[jj * context.nbCols + ii]
										* diffusion.diffusionMatrix[kRows - m - 1][kCols - n - 1]) {
									output[j * context.nbCols + i] = input[jj * context.nbCols + ii]
											* diffusion.diffusionMatrix[kRows - m - 1][kCols - n - 1];
								}
							} else {
								output[j * context.nbCols + i] += input[jj * context.nbCols + ii]
										* diffusion.diffusionMatrix[kRows - m - 1][kCols - n - 1];
							}

							// undo the changes if "avoid_mask" and if the
							// output cell is masked.
							if (diffusion.avoidMask && (diffusion.mask == null ? false : diffusion.mask[i][j] != 1)) {
								value_to_redistribute += output[j * context.nbCols + i];
								output[j * context.nbCols + i] = value_before_change;
								if (diffusion.mask[ii][jj] == 1) {
									// input cell not masked
									non_masked_cells.add(new int[] { ii, jj });
								}
							}
						}
					}
				}
				if (value_to_redistribute != 0) {
					final double value_to_add = value_to_redistribute * proportion / non_masked_cells.size();
					for (final int[] coord : non_masked_cells) {
						if (output[coord[1] * context.nbCols + coord[0]] == -Double.MAX_VALUE) {
							output[coord[1] * context.nbCols + coord[0]] = value_to_add;
						} else if (!diffusion.isGradient) {
							output[coord[1] * context.nbCols + coord[0]] += value_to_add;
						}
					}
				}
			}
		}
	}

	/**
	 * Diffusion with dot product.
	 */
	public void diffusionWithDotProduct() {
		// dot product

		final int kRows = diffusion.diffusionMatrix.length;
		final int kCols = diffusion.diffusionMatrix[0].length;

		final int kCenterX = kCols / 2;
		final int kCenterY = kRows / 2;

		for (int ii = 0; ii < context.nbRows; ++ii) // input rows
		{
			for (int jj = 0; jj < context.nbCols; ++jj) // input columns
			{
				if (diffusion.mask == null || diffusion.mask[ii][jj] == 1) {
					// diffuse only if the input is not masked
					double value_to_redistribute = 0;
					final ArrayList<int[]> non_masked_cells = new ArrayList<>();
					for (int m = 0; m < kRows; ++m) // kernel rows
					{
						for (int n = 0; n < kCols; ++n) // kernel columns
						{
							// index of output signal, used for checking
							// boundary
							int i = ii + n - kCenterX;
							int j = jj + m - kCenterY;
							// ignore output samples which are out of bound
							if (context.isTorus) {
								if (i < 0) {
									i = context.nbRows + i;
								} else if (i >= context.nbRows) { i = i - context.nbRows; }

								if (j < 0) {
									j = context.nbCols + j;
								} else if (j >= context.nbCols) { j = j - context.nbCols; }
							}
							// diffuse if the output value is in the grid
							if (i >= 0 && i < context.nbCols && j >= 0 && j < context.nbRows) {
								final double value_before_change = output[j * context.nbCols + i];
								final int outputIndex = j * context.nbCols + i;
								final int inputIndex = jj * context.nbCols + ii;
								final double matrixValue = diffusion.diffusionMatrix[m][n];
								if (output[outputIndex] == -Double.MAX_VALUE) {
									output[outputIndex] = input[inputIndex] * matrixValue;
								} else if (diffusion.isGradient) {
									if (output[outputIndex] < input[inputIndex] * matrixValue) {
										output[outputIndex] = input[inputIndex] * matrixValue;
									}
								} else {
									output[outputIndex] += input[inputIndex] * matrixValue;
								}

								// undo the changes if "avoid_mask" and if the
								// output cell is masked.
								if (diffusion.avoidMask
										&& (diffusion.mask == null ? false : diffusion.mask[i][j] != 1)) {
									value_to_redistribute += output[outputIndex];
									output[outputIndex] = value_before_change;
									if (diffusion.mask[ii][jj] == 1) {
										// input cell not masked
										non_masked_cells.add(new int[] { ii, jj });
									}
								}
							}
						}
					}
					if (value_to_redistribute != 0) {
						final double value_to_add = value_to_redistribute * proportion / non_masked_cells.size();
						for (final int[] coord : non_masked_cells) {
							if (output[coord[1] * context.nbCols + coord[0]] == -Double.MAX_VALUE) {
								output[coord[1] * context.nbCols + coord[0]] = value_to_add;
							} else if (!diffusion.isGradient) {
								output[coord[1] * context.nbCols + coord[0]] += value_to_add;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Finish diffusion.
	 */
	public void finishDiffusion() {
		for (int i = 0; i < output.length; i++) {
			double valToPut = output[i];
			if (valToPut == -Double.MAX_VALUE) { continue; }
			if (diffusion.isGradient) {
				if (valToPut <= input[i]) { continue; }
				if (valToPut < diffusion.minValue) { valToPut = 0; }
			} else {
				valToPut = Math.max(valToPut, diffusion.minValue);
			}
			context.target.setValueAtIndex(scope, i, context.varName, valToPut);
		}
	}

	/**
	 * Diffuse.
	 *
	 * @return the object
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public Object diffuse() throws GamaRuntimeException {
		if (scope == null || scope.interrupted()) return false;
		diffusionsMap.asMap().forEach((context, diffusions) -> {
			loadGridProperties(context);
			int length = context.nbCols * context.nbRows;
			if (input != null && input.length == length) {
				Arrays.fill(input, 0);
			} else {
				input = new double[context.nbCols * context.nbRows];
				output = new double[context.nbCols * context.nbRows];
			}
			Arrays.fill(output, -Double.MAX_VALUE);

			diffusions.forEach(diffusion -> {
				loadDiffProperties(diffusion);
				if (!diffusion.useConvolution) {
					diffusionWithDotProduct();
				} else {
					diffusionWithConvolution();
				}
				finishDiffusion();
			});
		});

		diffusionsMap.clear();
		return null;
	}

}
