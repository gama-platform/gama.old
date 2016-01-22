package msi.gama.metamodel.topology.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	
//	private class GridDiffusion {
//		private String m_var_diffu;
//		private boolean m_method_diffu = true;
//	}
	
	private boolean m_is_torus;
	private String m_var_diffu;
	private  String m_species_diffu;
	// true for convolution, false for dot_product
	private boolean m_method_diffu = true;
	boolean m_initialized = false;
	int m_cLen = 1;
	double[][] m_mask, m_mat_diffu;
	List<Integer> m_agents;
	Object m_obj;
	IPopulation m_pop;
	IScope m_scope;
	
	double[] input, output;
	int m_nbRows, m_nbCols;
	
	public GridDiffuserWithMatrix(IScope scope, boolean method_diffu, double[][] mat_diffu, 
			double[][] mask, int cLen, Object obj, boolean is_torus, String var_diffu, 
			String species_diffu, List<Integer> agents, IPopulation pop, int nbRows, int nbCols) {
		m_method_diffu = method_diffu;
		m_mat_diffu = mat_diffu;
		m_mask = mask;
		m_cLen = cLen;
		m_obj = obj;
		m_is_torus = is_torus;
		m_var_diffu = var_diffu;
		m_species_diffu = species_diffu;
		m_agents = agents;
		m_pop = pop;
		m_nbRows = nbRows;
		m_nbCols = nbCols;
		m_scope = scope;
	}
	
	public void initDiffusion(final IScope scope/*, final IPopulation pop*/) {

		for ( int i = 0; i < input.length; i++ ) {
			if (m_agents == null || m_agents.contains(i))
				input[i] = Cast.asFloat(scope, m_pop.get(scope, i).getDirectVarValue(scope, m_var_diffu));
			else 
				input[i] = 0;
			
		}
		
		Arrays.fill(output, 0d);
	}

	public void doDiffusion1(final IScope scope) {

		int kRows = m_mat_diffu.length;
		int kCols = m_mat_diffu[0].length;

		int kCenterX = kRows / 2;
		int kCenterY = kCols / 2;
		int mm = 0, nn = 0, ii = 0, jj = 0;

		for ( int i = 0; i < m_nbRows; ++i ) // rows
		{
			for ( int j = 0; j < m_nbCols; ++j ) // columns
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
						if ( m_is_torus ) {
							if ( ii < 0 ) {
								ii = m_nbRows + ii;
							} else if ( ii >= m_nbRows ) {
								ii = ii - m_nbRows;
							}

							if ( jj < 0 ) {
								jj = m_nbCols + jj;
							} else if ( jj >= m_nbCols ) {
								jj = jj - m_nbCols;
							}
						}
						if ( ii >= 0 && ii < m_nbRows && jj >= 0 && jj < m_nbCols ) {
							double mask_current = m_mask != null ? m_mask[i][j] < -1 ? 0 : 1 : 1;
							output[i * m_nbCols + j] += input[ii * m_nbCols + jj] * m_mat_diffu[mm][nn] * mask_current;
						}
					}
				}
			}
		}
	}

	public void doDiffusion2(final IScope scope) {

		int kRows = m_mat_diffu.length;
		int kCols = m_mat_diffu[0].length;

		int xcenter = kRows / 2;
		int ycenter = kCols / 2;

		for ( int i = 0; i < m_nbRows; i++ ) {
			for ( int j = 0; j < m_nbCols; j++ ) {

				int um = 0;
				for ( int uu = i - xcenter; uu <= i + xcenter; uu++ ) {
					int vm = 0;
					for ( int vv = j - ycenter; vv <= j + ycenter; vv++ ) {
						int u = uu;
						int v = vv;
						double mask_current = m_mask != null ? m_mask[i][j] < -1 ? 0 : 1 : 1;
						if ( m_is_torus ) {
							if ( u < 0 ) {
								u = m_nbRows + u;
							} else if ( u >= m_nbRows ) {
								u = u - m_nbRows;
							}

							if ( v < 0 ) {
								v = m_nbCols + v;
							} else if ( v >= m_nbCols ) {
								v = v - m_nbCols;
							}
						} else if ( u >= 0 && v >= 0 & v < m_nbCols & u < m_nbRows ) {
							output[u * m_nbCols + v] += input[i * m_nbCols + j] * m_mat_diffu[um][vm] * mask_current;
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
			if (m_agents == null || m_agents.contains(i))
					m_pop.get(scope, i).setDirectVarValue(scope, m_var_diffu, output[i]);
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

	private void initialize(final IScope scope) {
		m_initialized = true;

		GridPopulation pop = null;
		if (m_obj instanceof ISpecies) {
			m_agents = null;
			if (((ISpecies)m_obj).isGrid())
				pop = (GridPopulation) ((ISpecies)m_obj).getPopulation(scope);
		} else {
			IList<IAgent> ags = Cast.asList(scope, m_obj);
			if (! ags.isEmpty()) {
				ISpecies sp = ags.get(0).getSpecies();
				if (sp.isGrid()) {
					pop = (GridPopulation) sp.getPopulation(scope);
					m_agents = new ArrayList<Integer>();
					for (IAgent ag : ags) 
						m_agents.add(ag.getIndex());
				} else {
					throw GamaRuntimeException.error("Diffusion statement works only on grid agents", scope);
				}
				
			}
		}
		
		m_species_diffu = pop.getName();
		input = new double[pop.length(scope)];
		output = new double[pop.length(scope)];
		m_nbRows = ((IGrid) pop.getTopology().getPlaces()).getRows(scope);
		m_nbCols = ((IGrid) pop.getTopology().getPlaces()).getCols(scope);
		m_is_torus = pop.getTopology().isTorus();
	}

	public Object diffuse2(/*final IScope scope*/) throws GamaRuntimeException {
		
		if ( !m_initialized ) {
			initialize(m_scope);
		}
//		IPopulation pop = scope.getAgentScope().getPopulationFor(m_species_diffu);

		for ( int time = 0; time < m_cLen; time++ ) {
			initDiffusion(m_scope/*, pop*/);
			
			if ( !m_method_diffu ) {
			
				doDiffusion2(m_scope);
			} else {
				doDiffusion1(m_scope);
			}
			finishDiffusion(m_scope, m_pop);
		}
		return null;
	}

}
