package msi.gama.metamodel.topology.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gnu.trove.map.hash.THashMap;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.topology.grid.GamaSpatialMatrix.GridPopulation;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;

public class GridDiffuserWithMatrix {
	
	private class PairVarGrid {
		String m_var_name;
		String m_grid_name;
		int m_nbRows;
		int m_nbCols;
		boolean m_is_torus;
		
		public PairVarGrid(IScope scope, String var_name, GridPopulation pop) {
			m_var_name = var_name;
			m_grid_name = pop.getName();
			m_nbRows = ((IGrid) pop.getTopology().getPlaces()).getRows(scope);
			m_nbCols = ((IGrid) pop.getTopology().getPlaces()).getCols(scope);
			m_is_torus = pop.getTopology().isTorus();
		}
		
		public boolean isEqualTo(PairVarGrid otherGrid) {
			if ((otherGrid.m_var_name == m_var_name) && (otherGrid.m_grid_name == m_grid_name)) {
				return true;
			}
			return false;
		}
		
	    @Override
	    public int hashCode() {
	    	return m_var_name.hashCode()+m_grid_name.hashCode();
	    }

	    @Override
	    public boolean equals(Object obj) {
	       if (!(obj instanceof PairVarGrid))
	            return false;
	        if (obj == this)
	            return true;
	        
	        PairVarGrid otherGrid = (PairVarGrid)obj;
	        String my_string = otherGrid.m_var_name;
	        if ((otherGrid.m_var_name.equals(m_var_name)) && (otherGrid.m_grid_name.equals(m_grid_name))) {
				return true;
			}
			return false;
	    }
	}
	
	private class GridDiffusion {
		public String m_var_diffu;
		public String m_species_diffu;
		public boolean m_method_diffu = true;
		public double[][] m_mask, m_mat_diffu;
		public List<Integer> m_agents;
		public IScope m_scope;
		public IPopulation m_pop;
		
		public GridDiffusion(IScope scope, String var_diffu, String species_diffu, boolean method_diffu, double[][] mat_diffu, List<Integer> agents) {
			m_scope = scope;
			m_var_diffu = var_diffu;
			m_species_diffu = species_diffu;
			m_method_diffu = method_diffu;
			m_mat_diffu = mat_diffu;
			m_agents = agents;
		}
	}
	
	protected final Map<PairVarGrid, List<GridDiffusion>> m_diffusions = new HashMap<PairVarGrid,List<GridDiffusion>>();
	
	public void addDiffusion(IScope scope, String var_diffu, GridPopulation pop, boolean method_diffu, double[][] mat_diffu, List<Integer> agents) {

		GridDiffusion newGridDiff = new GridDiffusion(scope, var_diffu, pop.getName(), method_diffu, mat_diffu, agents);
		PairVarGrid keyValue = new PairVarGrid(scope, var_diffu, pop);
		boolean is_present = false;
		for (int h = 0; h < m_diffusions.keySet().size(); h++) {
			System.out.println(m_diffusions.keySet().getClass().getName());
			if (m_diffusions.keySet().getClass().equals(keyValue)) {
				is_present=true;
			};
		}
//		if (is_present)
		if (m_diffusions.containsKey(keyValue))
		{
			List<GridDiffusion> listWithSameVar = new ArrayList<GridDiffusion>();
			listWithSameVar = m_diffusions.get(keyValue);
			// try to mix diffusions if possible
			for (int i = 0; i<listWithSameVar.size(); i++) {
				GridDiffusion gridToAnalyze = listWithSameVar.get(i);
				listWithSameVar.remove(gridToAnalyze);
				if (gridToAnalyze.m_method_diffu == newGridDiff.m_method_diffu
						&& gridToAnalyze.m_agents == newGridDiff.m_agents
						&& gridToAnalyze.m_mask == newGridDiff.m_mask) {
					// we can add the two diffusion matrix
					int iiLength = gridToAnalyze.m_mat_diffu.length;
					int cellNbiiToAddToGridToAnalyze = 0;
					int cellNbiiToAddToNewGrid = 0;
					if (gridToAnalyze.m_mat_diffu.length < newGridDiff.m_mat_diffu.length) {
						iiLength = newGridDiff.m_mat_diffu.length;
						cellNbiiToAddToGridToAnalyze = (newGridDiff.m_mat_diffu.length-gridToAnalyze.m_mat_diffu.length)/2;
					}
					else if (gridToAnalyze.m_mat_diffu.length > newGridDiff.m_mat_diffu.length) {
						iiLength = newGridDiff.m_mat_diffu.length;
						cellNbiiToAddToNewGrid = (gridToAnalyze.m_mat_diffu.length-newGridDiff.m_mat_diffu.length)/2;
					}
					int jjLength = gridToAnalyze.m_mat_diffu[0].length;
					int cellNbjjToAddToGridToAnalyze = 0;
					int cellNbjjToAddToNewGrid = 0;
					if (gridToAnalyze.m_mat_diffu[0].length < newGridDiff.m_mat_diffu[0].length) {
						jjLength = newGridDiff.m_mat_diffu[0].length;
						cellNbjjToAddToGridToAnalyze = (newGridDiff.m_mat_diffu[0].length-gridToAnalyze.m_mat_diffu[0].length)/2;
					}
					else if (gridToAnalyze.m_mat_diffu[0].length > newGridDiff.m_mat_diffu[0].length) {
						jjLength = newGridDiff.m_mat_diffu[0].length;
						cellNbjjToAddToNewGrid = (gridToAnalyze.m_mat_diffu[0].length-newGridDiff.m_mat_diffu[0].length)/2;
					}
					double[][] newMat = new double[iiLength][jjLength];
					for (int ii = 0 ; ii < iiLength ; ii++) {
						for (int jj = 0 ; jj < jjLength ; jj++) {
							double result = 0;
							if ((gridToAnalyze.m_mat_diffu.length - iiLength + ii + cellNbiiToAddToGridToAnalyze >= 0) 
									&& (gridToAnalyze.m_mat_diffu.length - iiLength + ii + cellNbiiToAddToGridToAnalyze < gridToAnalyze.m_mat_diffu.length)
									&& (gridToAnalyze.m_mat_diffu[0].length - jjLength + jj + cellNbjjToAddToGridToAnalyze >= 0)
									&& (gridToAnalyze.m_mat_diffu[0].length - jjLength + jj + cellNbjjToAddToGridToAnalyze < gridToAnalyze.m_mat_diffu[0].length)) {
								result += gridToAnalyze.m_mat_diffu[ii][jj];
							}
							if ((newGridDiff.m_mat_diffu.length - iiLength + ii + cellNbiiToAddToNewGrid >= 0)
									&& (newGridDiff.m_mat_diffu.length - iiLength + ii + cellNbiiToAddToNewGrid < newGridDiff.m_mat_diffu.length)
									&& (newGridDiff.m_mat_diffu[0].length - jjLength + jj + cellNbjjToAddToNewGrid >= 0)
									&& (newGridDiff.m_mat_diffu[0].length - jjLength + jj + cellNbjjToAddToNewGrid < newGridDiff.m_mat_diffu[0].length)) {
								result += newGridDiff.m_mat_diffu[ii][jj];
							}
							newMat[ii][jj] = result;
						}
					}
					newGridDiff.m_mat_diffu = newMat;
				}
				listWithSameVar.add(newGridDiff);
			}
			m_diffusions.put(keyValue, listWithSameVar);
		}
		else {
			List<GridDiffusion> valueToAdd = new ArrayList<GridDiffusion>();
			valueToAdd.add(newGridDiff);
			m_diffusions.put(keyValue, valueToAdd);
		}
	}
	
	private boolean is_torus;
	private String var_diffu;
	// true for convolution, false for dot_product
	private boolean method_diffu = true;
	boolean m_initialized = false;
	double[][] mask, mat_diffu;
	List<Integer> agents;
	IScope scope;
	
	double[] input, output;
	int nbRows, nbCols;
	
	public GridDiffuserWithMatrix() {
	}
	
	public void initDiffusion(final IScope scope, final IPopulation pop) {

		for ( int i = 0; i < input.length; i++ ) {
			if (agents == null || agents.contains(i))
				input[i] = Cast.asFloat(scope, pop.get(scope, i).getDirectVarValue(scope, var_diffu));
			else 
				input[i] = 0;
		}
		
		Arrays.fill(output, 0d);
	}
	
	public void loadGridProperties(PairVarGrid pairVarGrid) {
		nbRows = pairVarGrid.m_nbRows;
		nbCols = pairVarGrid.m_nbCols;
		is_torus = pairVarGrid.m_is_torus;
		var_diffu = pairVarGrid.m_var_name;
	}
	
	public void loadDiffProperties(GridDiffusion gridDiff) {
		mat_diffu = gridDiff.m_mat_diffu;
		mask = gridDiff.m_mask;
		method_diffu = gridDiff.m_method_diffu;
		scope = gridDiff.m_scope;
		IPopulation pop = scope.getAgentScope().getPopulationFor(gridDiff.m_species_diffu);
		
		input = new double[pop.length(scope)];
		output = new double[pop.length(scope)];
		
		for ( int i = 0; i < input.length; i++ ) {
			if (agents == null || agents.contains(i))
				input[i] = Cast.asFloat(scope, pop.get(scope, i).getDirectVarValue(scope, var_diffu));
			else 
				input[i] = 0;
			
		}
		
		Arrays.fill(output, 0d);
	}

	public void doDiffusion1() {

		int kRows = mat_diffu.length;
		int kCols = mat_diffu[0].length;

		int kCenterX = kRows / 2;
		int kCenterY = kCols / 2;
		int mm = 0, nn = 0, ii = 0, jj = 0;

		for ( int i = 0; i < nbRows; ++i ) // rows
		{
			for ( int j = 0; j < nbCols; ++j ) // columns
			{
				// sum = 0; // init to 0 before sum
				for ( int m = 0; m < kRows; ++m ) // kernel rows
				{
					mm = kRows - 1 - m; // row index of flipped kernel
					for ( int n = 0; n < kCols; ++n ) // kernel columns
					{
						nn = kCols - 1 - n; // column index of flipped kernel
						// index of input signal, used for checking boundary
						ii = i + m - kCenterX;
						jj = j + n - kCenterY;
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
						if ( ii >= 0 && ii < nbRows && jj >= 0 && jj < nbCols ) {
							double mask_current = mask != null ? mask[i][j] < -1 ? 0 : 1 : 1;
							output[i * nbCols + j] += input[ii * nbCols + jj] * mat_diffu[mm][nn] * mask_current;
						}
					}
				}
			}
		}
	}

	public void doDiffusion2() {

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
						double mask_current = mask != null ? mask[i][j] < -1 ? 0 : 1 : 1;
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
						} else if ( u >= 0 && v >= 0 & v < nbCols & u < nbRows ) {
							output[u * nbCols + v] += input[i * nbCols + j] * mat_diffu[um][vm] * mask_current;
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
			if (agents == null || agents.contains(i))
					pop.get(scope, i).setDirectVarValue(scope, var_diffu, output[i]);
		}
	}

	public double[][] translateMatrix(final IScope scope, final IMatrix mm) {
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

	public Object diffuse2(/*final IScope scope*/) throws GamaRuntimeException {
		
		System.out.println("EXECUTE DIFFUSE !!!");
		
		Set<PairVarGrid> keySet = m_diffusions.keySet();
		Iterator<PairVarGrid> iterator = keySet.iterator();
		
		while (iterator.hasNext()) {
			PairVarGrid pairVarGrid = iterator.next();
			List<GridDiffusion> listGridDiffu = m_diffusions.get(pairVarGrid);
			loadGridProperties(pairVarGrid);
			Iterator<GridDiffusion> gridDiffIterator = listGridDiffu.iterator();
			while (gridDiffIterator.hasNext()) {
				GridDiffusion gridDiffusion = gridDiffIterator.next();
				loadDiffProperties(gridDiffusion);
				if (!method_diffu) {
					doDiffusion2();
				}
				else {
					doDiffusion1();
				}
			}
			IPopulation pop = scope.getAgentScope().getPopulationFor(pairVarGrid.m_grid_name);
			finishDiffusion(scope, pop);
		}
		return null;
	}

}
