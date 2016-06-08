package msi.gama.metamodel.topology.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix.GridPopulation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.operators.Cast;

public class GridDiffuser {

	// Structure of the Key for our map.
	private class PairVarGrid {
		String Var_name;
		String Grid_name;
		int NbRows;
		int NbCols;
		boolean Is_torus;
		IPopulation Pop;

		public PairVarGrid(final IScope scope, final String var_name, final GridPopulation pop) {
			Var_name = var_name;
			Grid_name = pop.getName();
			NbRows = ((IGrid) pop.getTopology().getPlaces()).getRows(scope);
			NbCols = ((IGrid) pop.getTopology().getPlaces()).getCols(scope);
			Is_torus = pop.getTopology().isTorus();
			Pop = pop;
		}

		@Override
		public int hashCode() {
			return Var_name.hashCode() + Grid_name.hashCode();
		}

		@Override
		public boolean equals(final Object obj) {
			if (!(obj instanceof PairVarGrid)) {
				return false;
			}
			if (obj == this) {
				return true;
			}

			final PairVarGrid otherGrid = (PairVarGrid) obj;
			if (otherGrid.Var_name.equals(Var_name) && otherGrid.Grid_name.equals(Grid_name)) {
				return true;
			}
			return false;
		}
	}

	// Structure for the Value of our map
	private class GridDiffusion {
		public boolean Use_convolution = true;
		public boolean Is_gradient;
		public double[][] Mask, Mat_diffu;
		public IScope Scope;
		public double Min_value;
		public boolean Avoid_mask;

		public GridDiffusion(final IScope scope, final boolean use_convolution, final boolean is_gradient,
				final double[][] mat_diffu, final double[][] mask, final double min_value, final boolean avoid_mask) {
			Scope = scope;
			Use_convolution = use_convolution;
			Mat_diffu = mat_diffu;
			Mask = mask;
			Is_gradient = is_gradient;
			Min_value = min_value;
			Avoid_mask = avoid_mask;
		}
	}

	public boolean compareArrays(final double[][] array1, final double[][] array2) {
		boolean b = true;
		if (array1 != null && array2 != null) {
			if (array1.length != array2.length) {
				b = false;
			} else {
				for (int i = 0; i < array2.length; i++) {
					if (array1[i].length != array2[i].length) {
						b = false;
					} else {
						for (int j = 0; j < array2[i].length; j++) {
							if (array2[i][j] != array1[i][j]) {
								b = false;
							}
						}
					}
				}
			}
		} else {
			b = false;
		}
		return b;
	}

	protected final Map<PairVarGrid, List<GridDiffusion>> m_diffusions = new HashMap<PairVarGrid, List<GridDiffusion>>();

	public void addDiffusion(final IScope scope, final String var_diffu, final GridPopulation pop,
			final boolean method_diffu, final boolean is_gradient, final double[][] mat_diffu, final double[][] mask,
			final double min_value, final boolean avoid_mask) {
		final GridDiffusion newGridDiff = new GridDiffusion(scope, method_diffu, is_gradient, mat_diffu, mask,
				min_value, avoid_mask);
		final PairVarGrid keyValue = new PairVarGrid(scope, var_diffu, pop);
		if (m_diffusions.containsKey(keyValue)) {
			List<GridDiffusion> listWithSameVar = new ArrayList<GridDiffusion>();
			listWithSameVar = m_diffusions.get(keyValue);
			// try to mix diffusions if possible
			for (int i = 0; i < listWithSameVar.size(); i++) {
				final GridDiffusion gridToAnalyze = listWithSameVar.get(i);
				if (gridToAnalyze != newGridDiff && gridToAnalyze.Use_convolution == newGridDiff.Use_convolution
						&& compareArrays(gridToAnalyze.Mask, newGridDiff.Mask)
						&& gridToAnalyze.Is_gradient == newGridDiff.Is_gradient
						&& gridToAnalyze.Avoid_mask == newGridDiff.Avoid_mask) {
					// we can add the two diffusion matrix
					listWithSameVar.remove(gridToAnalyze);
					int iiLength = gridToAnalyze.Mat_diffu.length;
					int cellNbiiToAddToGridToAnalyze = 0;
					int cellNbiiToAddToNewGrid = 0;
					if (gridToAnalyze.Mat_diffu.length < newGridDiff.Mat_diffu.length) {
						iiLength = newGridDiff.Mat_diffu.length;
						cellNbiiToAddToGridToAnalyze = (newGridDiff.Mat_diffu.length - gridToAnalyze.Mat_diffu.length)
								/ 2;
					} else if (gridToAnalyze.Mat_diffu.length > newGridDiff.Mat_diffu.length) {
						iiLength = newGridDiff.Mat_diffu.length;
						cellNbiiToAddToNewGrid = (gridToAnalyze.Mat_diffu.length - newGridDiff.Mat_diffu.length) / 2;
					}
					int jjLength = gridToAnalyze.Mat_diffu[0].length;
					int cellNbjjToAddToGridToAnalyze = 0;
					int cellNbjjToAddToNewGrid = 0;
					if (gridToAnalyze.Mat_diffu[0].length < newGridDiff.Mat_diffu[0].length) {
						jjLength = newGridDiff.Mat_diffu[0].length;
						cellNbjjToAddToGridToAnalyze = (newGridDiff.Mat_diffu[0].length
								- gridToAnalyze.Mat_diffu[0].length) / 2;
					} else if (gridToAnalyze.Mat_diffu[0].length > newGridDiff.Mat_diffu[0].length) {
						jjLength = gridToAnalyze.Mat_diffu[0].length;
						cellNbjjToAddToNewGrid = (gridToAnalyze.Mat_diffu[0].length - newGridDiff.Mat_diffu[0].length)
								/ 2;
					}
					final double[][] newMat = new double[iiLength][jjLength];
					for (int ii = 0; ii < iiLength; ii++) {
						for (int jj = 0; jj < jjLength; jj++) {
							double result = 0;
							final int indexiForGridToAnalyze = gridToAnalyze.Mat_diffu.length - iiLength + ii
									+ cellNbiiToAddToGridToAnalyze;
							final int indexjForGridToAnalyze = gridToAnalyze.Mat_diffu[0].length - jjLength + jj
									+ cellNbjjToAddToGridToAnalyze;
							if (indexiForGridToAnalyze >= 0 && indexiForGridToAnalyze < gridToAnalyze.Mat_diffu.length
									&& indexjForGridToAnalyze >= 0
									&& indexjForGridToAnalyze < gridToAnalyze.Mat_diffu[0].length) {
								result += gridToAnalyze.Mat_diffu[indexiForGridToAnalyze][indexjForGridToAnalyze];
							}
							final int indexiForNewGridDiff = newGridDiff.Mat_diffu.length - iiLength + ii
									+ cellNbiiToAddToNewGrid;
							final int indexjForNewGridDiff = newGridDiff.Mat_diffu[0].length - jjLength + jj
									+ cellNbjjToAddToNewGrid;
							if (indexiForNewGridDiff >= 0 && indexiForNewGridDiff < newGridDiff.Mat_diffu.length
									&& indexjForNewGridDiff >= 0
									&& indexjForNewGridDiff < newGridDiff.Mat_diffu[0].length) {
								result += newGridDiff.Mat_diffu[indexiForNewGridDiff][indexjForNewGridDiff];
							}
							newMat[ii][jj] = result;
						}
					}
					newGridDiff.Mat_diffu = newMat;
				}
			}
			listWithSameVar.add(newGridDiff);
			m_diffusions.put(keyValue, listWithSameVar);
		} else {
			final List<GridDiffusion> valueToAdd = new ArrayList<GridDiffusion>();
			valueToAdd.add(newGridDiff);
			m_diffusions.put(keyValue, valueToAdd);
		}
	}

	private boolean is_torus;
	private boolean is_gradient;
	private String var_diffu;
	// true for convolution, false for dot_product
	private boolean use_convolution = true;
	boolean m_initialized = false;
	double[][] mask, mat_diffu;
	private boolean avoid_mask;
	private float proportion; // in case of "avoid_mask", compute the
								// proportion.
	IScope scope;

	double[] input, output;
	int nbRows, nbCols;
	double min_value;
	IPopulation pop;

	public GridDiffuser() {
	}

	public void loadGridProperties(final PairVarGrid pairVarGrid) {
		nbRows = pairVarGrid.NbRows;
		nbCols = pairVarGrid.NbCols;
		is_torus = pairVarGrid.Is_torus;
		var_diffu = pairVarGrid.Var_name;
		pop = pairVarGrid.Pop;
	}

	public boolean loadDiffProperties(final GridDiffusion gridDiff) {

		mat_diffu = gridDiff.Mat_diffu;
		mask = gridDiff.Mask;
		use_convolution = gridDiff.Use_convolution;
		is_gradient = gridDiff.Is_gradient;
		min_value = gridDiff.Min_value;
		scope = gridDiff.Scope;
		avoid_mask = gridDiff.Avoid_mask;
		if (scope == null || scope.interrupted()) {
			return false;
		}

		if (avoid_mask) {
			// compute proportion
			proportion = 0;
			for (int i = 0; i < mat_diffu.length; i++) {
				for (int j = 0; j < mat_diffu[0].length; j++) {
					proportion += mat_diffu[i][j];
				}
			}
		}

		for (int i = 0; i < input.length; i++) {
			input[i] = Cast.asFloat(scope, pop.get(scope, i).getDirectVarValue(scope, var_diffu));
			if (input[i] < min_value) {
				input[i] = 0;
			}
		}
		return true;

	}

	public void doDiffusion_with_convolution() {
		// default method : convolution

		final int kRows = mat_diffu.length;
		final int kCols = mat_diffu[0].length;

		final int kCenterX = kCols / 2;
		final int kCenterY = kRows / 2;

		for (int i = 0; i < nbRows; ++i) // output rows
		{
			for (int j = 0; j < nbCols; ++j) // output columns
			{
				double value_to_redistribute = 0;
				final ArrayList<int[]> non_masked_cells = new ArrayList<int[]>();
				for (int m = 0; m < kRows; ++m) // kernel rows
				{
					for (int n = 0; n < kCols; ++n) // kernel columns
					{
						// index of input signal, used for checking boundary
						int ii = i + n - kCenterX;
						int jj = j + m - kCenterY;
						// ignore input samples which are out of bound
						if (is_torus) {
							if (ii < 0) {
								ii = nbRows + ii;
							} else if (ii >= nbRows) {
								ii = ii - nbRows;
							}

							if (jj < 0) {
								jj = nbCols + jj;
							} else if (jj >= nbCols) {
								jj = jj - nbCols;
							}
						}
						// diffuse if the input value is in the grid, and if the
						// cell is not masked
						if (ii >= 0 && ii < nbCols && jj >= 0 && jj < nbRows && (mask == null || mask[ii][jj] == 1)) {
							final double value_before_change = output[j * nbCols + i];
							if (output[j * nbCols + i] == -Double.MAX_VALUE) {
								output[j * nbCols + i] = input[jj * nbCols + ii]
										* mat_diffu[kRows - m - 1][kCols - n - 1];
							} else {
								if (is_gradient) {
									if (output[j * nbCols + i] < input[jj * nbCols + ii]
											* mat_diffu[kRows - m - 1][kCols - n - 1]) {
										output[j * nbCols + i] = input[jj * nbCols + ii]
												* mat_diffu[kRows - m - 1][kCols - n - 1];
									}
								} else {
									output[j * nbCols + i] += input[jj * nbCols + ii]
											* mat_diffu[kRows - m - 1][kCols - n - 1];
								}
							}

							// undo the changes if "avoid_mask" and if the
							// output cell is masked.
							if (avoid_mask && (mask == null ? false : mask[i][j] != 1)) {
								value_to_redistribute += output[j * nbCols + i];
								output[j * nbCols + i] = value_before_change;
								if (mask[ii][jj] == 1) {
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
						if (output[coord[1] * nbCols + coord[0]] == -Double.MAX_VALUE) {
							output[coord[1] * nbCols + coord[0]] = value_to_add;
						} else {
							if (!is_gradient) {
								output[coord[1] * nbCols + coord[0]] += value_to_add;
							}
						}
					}
				}
			}
		}
	}

	public void doDiffusion_with_dotProduct() {
		// dot product

		final int kRows = mat_diffu.length;
		final int kCols = mat_diffu[0].length;

		final int kCenterX = kCols / 2;
		final int kCenterY = kRows / 2;

		for (int ii = 0; ii < nbRows; ++ii) // input rows
		{
			for (int jj = 0; jj < nbCols; ++jj) // input columns
			{
				if (mask == null || mask[ii][jj] == 1) {
					// diffuse only if the input is not masked
					double value_to_redistribute = 0;
					final ArrayList<int[]> non_masked_cells = new ArrayList<int[]>();
					for (int m = 0; m < kRows; ++m) // kernel rows
					{
						for (int n = 0; n < kCols; ++n) // kernel columns
						{
							// index of output signal, used for checking
							// boundary
							int i = ii + n - kCenterX;
							int j = jj + m - kCenterY;
							// ignore output samples which are out of bound
							if (is_torus) {
								if (i < 0) {
									i = nbRows + i;
								} else if (i >= nbRows) {
									i = i - nbRows;
								}

								if (j < 0) {
									j = nbCols + j;
								} else if (j >= nbCols) {
									j = j - nbCols;
								}
							}
							// diffuse if the output value is in the grid
							if (i >= 0 && i < nbCols && j >= 0 && j < nbRows) {
								final double value_before_change = output[j * nbCols + i];
								if (output[j * nbCols + i] == -Double.MAX_VALUE) {
									output[j * nbCols + i] = input[jj * nbCols + ii] * mat_diffu[m][n];
								} else {
									if (is_gradient) {
										if (output[j * nbCols + i] < input[jj * nbCols + ii] * mat_diffu[m][n]) {
											output[j * nbCols + i] = input[jj * nbCols + ii] * mat_diffu[m][n];
										}
									} else {
										output[j * nbCols + i] += input[jj * nbCols + ii] * mat_diffu[m][n];
									}
								}

								// undo the changes if "avoid_mask" and if the
								// output cell is masked.
								if (avoid_mask && (mask == null ? false : mask[i][j] != 1)) {
									value_to_redistribute += output[j * nbCols + i];
									output[j * nbCols + i] = value_before_change;
									if (mask[ii][jj] == 1) {
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
							if (output[coord[1] * nbCols + coord[0]] == -Double.MAX_VALUE) {
								output[coord[1] * nbCols + coord[0]] = value_to_add;
							} else {
								if (!is_gradient) {
									output[coord[1] * nbCols + coord[0]] += value_to_add;
								}
							}
						}
					}
				}
			}
		}
	}

	public void finishDiffusion(final IScope scope, final IPopulation pop) {
		for (int i = 0; i < output.length; i++) {
			if (output[i] != -Double.MAX_VALUE) {
				if (is_gradient) {
					if (output[i] > input[i]) {
						if (output[i] < min_value) {
							pop.get(scope, i).setDirectVarValue(scope, var_diffu, 0);
						} else {
							pop.get(scope, i).setDirectVarValue(scope, var_diffu, output[i]);
						}
					}
				} else {
					if (output[i] < min_value) {
						pop.get(scope, i).setDirectVarValue(scope, var_diffu, 0);
					} else {
						pop.get(scope, i).setDirectVarValue(scope, var_diffu, output[i]);
					}
				}
			}
		}
	}

	public double[][] translateMatrix(final IScope scope, final IMatrix<?> mm) {
		if (mm == null) {
			return null;
		}
		final int rows = mm.getRows(scope);
		final int cols = mm.getCols(scope);
		final double[][] res = new double[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				res[i][j] = Cast.asFloat(scope, mm.get(scope, i, j));
			}
		}
		return res;
	}

	public Object diffuse() throws GamaRuntimeException {

		final Set<PairVarGrid> keySet = m_diffusions.keySet();
		final Iterator<PairVarGrid> iterator = keySet.iterator();

		while (iterator.hasNext()) {
			final PairVarGrid pairVarGrid = iterator.next();
			final List<GridDiffusion> listGridDiffu = m_diffusions.get(pairVarGrid);
			loadGridProperties(pairVarGrid);
			final Iterator<GridDiffusion> gridDiffIterator = listGridDiffu.iterator();
			input = new double[pairVarGrid.NbCols * pairVarGrid.NbRows];
			output = new double[pairVarGrid.NbCols * pairVarGrid.NbRows];
			Arrays.fill(output, -Double.MAX_VALUE);
			while (gridDiffIterator.hasNext()) {
				final GridDiffusion gridDiffusion = gridDiffIterator.next();
				final boolean success = loadDiffProperties(gridDiffusion);
				if (success) {
					if (!use_convolution) {
						doDiffusion_with_dotProduct();
					} else {
						doDiffusion_with_convolution();
					}
				}
				finishDiffusion(scope, pop);
			}
		}
		m_diffusions.clear();
		return null;
	}

}
