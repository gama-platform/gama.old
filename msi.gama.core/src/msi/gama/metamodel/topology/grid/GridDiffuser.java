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
		
		public PairVarGrid(IScope scope, String var_name, GridPopulation pop) {
			Var_name = var_name;
			Grid_name = pop.getName();
			NbRows = ((IGrid) pop.getTopology().getPlaces()).getRows(scope);
			NbCols = ((IGrid) pop.getTopology().getPlaces()).getCols(scope);
			Is_torus = pop.getTopology().isTorus();
			Pop = pop;
		}
		
	    @Override
	    public int hashCode() {
	    	return Var_name.hashCode()+Grid_name.hashCode();
	    }

	    @Override
	    public boolean equals(Object obj) {
	       if (!(obj instanceof PairVarGrid))
	            return false;
	        if (obj == this)
	            return true;
	        
	        PairVarGrid otherGrid = (PairVarGrid)obj;
	        if ((otherGrid.Var_name.equals(Var_name)) && (otherGrid.Grid_name.equals(Grid_name))) {
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
		
		public GridDiffusion(IScope scope, boolean use_convolution, boolean is_gradient, 
				double[][] mat_diffu, double[][] mask, double min_value) {
			Scope = scope;
			Use_convolution = use_convolution;
			Mat_diffu = mat_diffu;
			Mask = mask;
			Is_gradient = is_gradient;
			Min_value = min_value;
		}
	}
	
	public boolean compareArrays(double[][] array1, double[][] array2) {
        boolean b = true;
        if (array1 != null && array2 != null){
          if (array1.length != array2.length)
              b = false;
          else
              for (int i = 0; i < array2.length; i++) {
            	  if (array1[i].length != array2[i].length)
            	  {
            		  b = false;
            	  }
            	  else {
            		  for (int j = 0; j < array2[i].length; j++) {
                          if (array2[i][j] != array1[i][j]) {
                              b = false;    
                          } 
            		  }
            	  }                
            }
        }else{
          b = false;
        }
        return b;
    }
	
	protected final Map<PairVarGrid, List<GridDiffusion>> m_diffusions = new HashMap<PairVarGrid,List<GridDiffusion>>();
	
	public void addDiffusion(IScope scope, String var_diffu, GridPopulation pop, boolean method_diffu, 
			boolean is_gradient, double[][] mat_diffu, double[][] mask, double min_value) {
		GridDiffusion newGridDiff = new GridDiffusion(scope, method_diffu, is_gradient, mat_diffu, mask, min_value);
		PairVarGrid keyValue = new PairVarGrid(scope, var_diffu, pop);
		if (m_diffusions.containsKey(keyValue))
		{
			List<GridDiffusion> listWithSameVar = new ArrayList<GridDiffusion>();
			listWithSameVar = m_diffusions.get(keyValue);
			// try to mix diffusions if possible
			for (int i = 0; i<listWithSameVar.size(); i++) {
				GridDiffusion gridToAnalyze = listWithSameVar.get(i);
				if (gridToAnalyze != newGridDiff
						&& gridToAnalyze.Use_convolution == newGridDiff.Use_convolution
						&& compareArrays(gridToAnalyze.Mask,newGridDiff.Mask)
						&& gridToAnalyze.Is_gradient == newGridDiff.Is_gradient) {
					// we can add the two diffusion matrix
					listWithSameVar.remove(gridToAnalyze);
					int iiLength = gridToAnalyze.Mat_diffu.length;
					int cellNbiiToAddToGridToAnalyze = 0;
					int cellNbiiToAddToNewGrid = 0;
					if (gridToAnalyze.Mat_diffu.length < newGridDiff.Mat_diffu.length) {
						iiLength = newGridDiff.Mat_diffu.length;
						cellNbiiToAddToGridToAnalyze = (newGridDiff.Mat_diffu.length-gridToAnalyze.Mat_diffu.length)/2;
					}
					else if (gridToAnalyze.Mat_diffu.length > newGridDiff.Mat_diffu.length) {
						iiLength = newGridDiff.Mat_diffu.length;
						cellNbiiToAddToNewGrid = (gridToAnalyze.Mat_diffu.length-newGridDiff.Mat_diffu.length)/2;
					}
					int jjLength = gridToAnalyze.Mat_diffu[0].length;
					int cellNbjjToAddToGridToAnalyze = 0;
					int cellNbjjToAddToNewGrid = 0;
					if (gridToAnalyze.Mat_diffu[0].length < newGridDiff.Mat_diffu[0].length) {
						jjLength = newGridDiff.Mat_diffu[0].length;
						cellNbjjToAddToGridToAnalyze = (newGridDiff.Mat_diffu[0].length-gridToAnalyze.Mat_diffu[0].length)/2;
					}
					else if (gridToAnalyze.Mat_diffu[0].length > newGridDiff.Mat_diffu[0].length) {
						jjLength = gridToAnalyze.Mat_diffu[0].length;
						cellNbjjToAddToNewGrid = (gridToAnalyze.Mat_diffu[0].length-newGridDiff.Mat_diffu[0].length)/2;
					}
					double[][] newMat = new double[iiLength][jjLength];
					for (int ii = 0 ; ii < iiLength ; ii++) {
						for (int jj = 0 ; jj < jjLength ; jj++) {
							double result = 0;
							int indexiForGridToAnalyze = gridToAnalyze.Mat_diffu.length - iiLength + ii + cellNbiiToAddToGridToAnalyze;
							int indexjForGridToAnalyze = gridToAnalyze.Mat_diffu[0].length - jjLength + jj + cellNbjjToAddToGridToAnalyze;
							if ((indexiForGridToAnalyze >= 0) 
									&& (indexiForGridToAnalyze < gridToAnalyze.Mat_diffu.length)
									&& (indexjForGridToAnalyze >= 0)
									&& (indexjForGridToAnalyze < gridToAnalyze.Mat_diffu[0].length)) {
								result += gridToAnalyze.Mat_diffu[indexiForGridToAnalyze][indexjForGridToAnalyze];
							}
							int indexiForNewGridDiff = newGridDiff.Mat_diffu.length - iiLength + ii + cellNbiiToAddToNewGrid;
							int indexjForNewGridDiff = newGridDiff.Mat_diffu[0].length - jjLength + jj + cellNbjjToAddToNewGrid;
							if ((indexiForNewGridDiff >= 0)
									&& (indexiForNewGridDiff < newGridDiff.Mat_diffu.length)
									&& (indexjForNewGridDiff >= 0)
									&& (indexjForNewGridDiff < newGridDiff.Mat_diffu[0].length)) {
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
		}
		else {
			List<GridDiffusion> valueToAdd = new ArrayList<GridDiffusion>();
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
	IScope scope;
	
	double[] input, output;
	int nbRows, nbCols;
	double min_value;
	IPopulation pop;
	
	public GridDiffuser() {
	}
	
	public void loadGridProperties(PairVarGrid pairVarGrid) {
		nbRows = pairVarGrid.NbRows;
		nbCols = pairVarGrid.NbCols;
		is_torus = pairVarGrid.Is_torus;
		var_diffu = pairVarGrid.Var_name;
		pop = pairVarGrid.Pop;
	}
	
	public void loadDiffProperties(GridDiffusion gridDiff) {
		mat_diffu = gridDiff.Mat_diffu;
		mask = gridDiff.Mask;
		use_convolution = gridDiff.Use_convolution;
		is_gradient = gridDiff.Is_gradient;
		min_value = gridDiff.Min_value;
		scope = gridDiff.Scope;
		
		for ( int i = 0; i < input.length; i++ ) {
			input[i] = Cast.asFloat(scope, pop.get(scope, i).getDirectVarValue(scope, var_diffu));
			if (input[i] < min_value)
				input[i] = 0;
		}
		

	}
	
	public void doDiffusion_with_convolution() {
		// default method : convolution

		int kRows = mat_diffu.length;
		int kCols = mat_diffu[0].length;

		int kCenterX = kCols/ 2;
		int kCenterY = kRows / 2;
		int ii = 0, jj = 0;

		for ( int i = 0; i < nbRows; ++i ) // rows
		{
			for ( int j = 0; j < nbCols; ++j ) // columns
			{
				if ((input[j * nbCols + i] > min_value) && ( (mask==null) ? true : (mask[i][j] == 1)) ) 
				{
					for ( int m = 0; m < kRows; ++m ) // kernel rows
					{
						for ( int n = 0; n < kCols; ++n ) // kernel columns
						{
							// index of input signal, used for checking boundary
							ii = i + n - kCenterX;
							jj = j + m - kCenterY;
							// ignore input samples which are out of bound
							if ( is_torus ) {
								if ( ii < 0 ) {
									ii = nbRows + ii;
								} else if ( ii >= nbRows ) {
									ii = ii - nbRows;
								}
	
								if ( jj < 0 ) {
									jj = nbCols + jj;
								} else if ( jj >= nbCols ) {
									jj = jj - nbCols;
								}
							}
							if ( ii >= 0 && ii < nbCols && jj >= 0 && jj < nbRows) {
								if (output[jj * nbCols + ii] == -Double.MAX_VALUE) {output[jj * nbCols + ii] = input[j * nbCols + i] * mat_diffu[m][n];}
								else {
									if (is_gradient) {
										if (output[jj * nbCols + ii] < input[j * nbCols + i] * mat_diffu[m][n]) {
											output[jj * nbCols + ii] = input[j * nbCols + i] * mat_diffu[m][n];
										}
									}
									else {
										output[jj * nbCols + ii] += input[j * nbCols + i] * mat_diffu[m][n];
									}
								}							
							}
						}
					}
				}
			}
		}
	}

	public void doDiffusion_with_dotProduct() {
		// dot_product

		int kRows = mat_diffu.length;
		int kCols = mat_diffu[0].length;

		int xcenter = kRows / 2;
		int ycenter = kCols / 2;

		for ( int i = 0; i < nbRows; i++ ) {
			for ( int j = 0; j < nbCols; j++ ) {

				int um = 0;
				for ( int uu = i - xcenter; uu <= i + xcenter; uu++ ) {
					int vm = 0;
					for ( int vv = j - ycenter; vv <= j + ycenter; vv++ ) {
						int u = uu;
						int v = vv;
						if ( is_torus ) {
							if ( u < 0 ) {
								u = nbRows + u;
							} else if ( u >= nbRows ) {
								u = u - nbRows;
							}

							if ( v < 0 ) {
								v = nbCols + v;
							} else if ( v >= nbCols ) {
								v = v - nbCols;
							}
						} else if ( u >= 0 && v >= 0 && v < nbCols && u < nbRows && ( (mask==null) ? true : (mask[i][j] == 1)) ) {
							if (output[u * nbCols + v]==-Double.MAX_VALUE) {
								output[u * nbCols + v]=input[i * nbCols + j] * mat_diffu[um][vm];
							}
							else {
								if (is_gradient) {
									if (output[u * nbCols + v] < input[i * nbCols + j] * mat_diffu[um][vm])
									output[u * nbCols + v] = input[i * nbCols + j] * mat_diffu[um][vm];
								}
								else {
									output[u * nbCols + v] += input[i * nbCols + j] * mat_diffu[um][vm];
								}
							}
						}

						vm++;
					}
					um++;

				}
			}
		}

	}

	public void finishDiffusion(final IScope scope, final IPopulation pop) {
		for ( int i = 0; i < output.length; i++ ) {
			if (output[i]!=-Double.MAX_VALUE)
			{
				if (is_gradient) {
					if (output[i]>input[i]) {
						if (output[i] < min_value) {
							pop.get(scope, i).setDirectVarValue(scope, var_diffu, 0);
						}
						else {
							pop.get(scope, i).setDirectVarValue(scope, var_diffu, output[i]);
						}
					}
				}
				else {
					if (output[i] < min_value) {
						pop.get(scope, i).setDirectVarValue(scope, var_diffu, 0);
					}
					else {
						pop.get(scope, i).setDirectVarValue(scope, var_diffu, output[i]);
					}
				}
			}
		}
	}

	public double[][] translateMatrix(final IScope scope, final IMatrix<?> mm) {
		if ( mm == null ) { return null; }
		int rows = mm.getRows(scope);
		int cols = mm.getCols(scope);
		double[][] res = new double[rows][cols];
		for ( int i = 0; i < rows; i++ ) {
			for ( int j = 0; j < cols; j++ ) {
				res[i][j] = Cast.asFloat(scope, mm.get(scope, i, j));
			}
		}
		return res;
	}

	public Object diffuse() throws GamaRuntimeException {
		
		Set<PairVarGrid> keySet = m_diffusions.keySet();
		Iterator<PairVarGrid> iterator = keySet.iterator();
		
		while (iterator.hasNext()) {
			PairVarGrid pairVarGrid = iterator.next();
			List<GridDiffusion> listGridDiffu = m_diffusions.get(pairVarGrid);
			loadGridProperties(pairVarGrid);
			Iterator<GridDiffusion> gridDiffIterator = listGridDiffu.iterator();
			input = new double[pairVarGrid.NbCols*pairVarGrid.NbRows];
			output = new double[pairVarGrid.NbCols*pairVarGrid.NbRows];
			Arrays.fill(output, -Double.MAX_VALUE);
			while (gridDiffIterator.hasNext()) {
				GridDiffusion gridDiffusion = gridDiffIterator.next();
				loadDiffProperties(gridDiffusion);
				if (!use_convolution) {
					doDiffusion_with_dotProduct();
				}
				else {
					doDiffusion_with_convolution();
				}
			}
			finishDiffusion(scope, pop);
		}
		m_diffusions.clear();
		return null;
	}

}
