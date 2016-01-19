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
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gaml.extensions.maths.pde.diffusion.statements.DiffusionStatement.DiffusionValidator;

@facets(value = { 
	@facet(name = IKeyword.VAR, type = IType.ID, optional = false, doc = @doc("the variable to be diffused")),
	@facet(name = IKeyword.ON, type = IType.CONTAINER ,optional = false, doc = @doc("the list of agents (in general cells of a grid), on which the diffusion will occur")),
	@facet(name = "mat_diffu", type = IType.MATRIX, optional = true, doc = @doc("the diffusion matrix (can have any size)")),
	@facet(name = IKeyword.METHOD, type = IType.ID, optional = true, values = { "convolution", "dot_product" }, doc = @doc("the diffusion method")),
	@facet(name = IKeyword.MASK, type = IType.MATRIX, optional = true, doc = @doc("a matrix masking the diffusion (matrix created from a image for example)")),
	@facet(name = "proportion", type = IType.FLOAT, optional = true, doc = @doc("a diffusion rate")),
	@facet(name = "radius", type = IType.INT, optional = true, doc = @doc("a diffusion radius")),
	@facet(name = IKeyword.CYCLE_LENGTH, type = IType.INT, optional = true, doc = @doc("the number of diffusion operation applied in one simulation step")) }, omissible = IKeyword.VAR)
@symbol(name = { "diffusion" }, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@validator(DiffusionValidator.class)
@doc(value="This statements allows a value to diffuse among a species on agents (generally on a grid) depending on a given diffusion matrix.", usages = {
	@usage(value="A basic example of diffusion of the variable phero defined in the species cells, given a diffusion matrix math_diff is:", examples = {
		@example(value="matrix<float> math_diff <- matrix([[1/9,1/9,1/9],[1/9,1/9,1/9],[1/9,1/9,1/9]]);",isExecutable=false),
		@example(value="diffusion var: phero on: cells mat_diffu: math_diff;",isExecutable=false)
	}),
	@usage(value="The diffusion can be masked by obstacles, created from a bitmap image:", examples = {
		@example(value="diffusion var: phero on: cells mat_diffu: math_diff mask: mymask;",isExecutable=false)
	}),	
	@usage(value="A convenient way to have an uniform diffusion in a given radius is (which is equivalent to the above diffusion):", examples = {
		@example(value="diffusion var: phero on: cells proportion: 1/9 radius: 1;",isExecutable=false)
	})
})
public class DiffusionStatement extends AbstractStatement {
 
//	public static Class VALIDATOR = DiffusionValidator.class;

	public static class DiffusionValidator implements IDescriptionValidator {

		@Override
		public void validate(final IDescription desc) {
			IExpression spec = desc.getFacets().getExpr("on");
			if (spec.getType().getTitle().split("\\[")[0].equals(Types.SPECIES.toString())) {
				if ( !desc.getSpeciesDescription(spec.getName()).isGrid() ) {
					desc.error("Diffusions can only be executed on grid species", IGamlIssue.GENERAL);
				}
			} else {
				if (! spec.getType().getContentType().isAgentType())
					desc.error("Diffusions can only be executed on list of agents", IGamlIssue.GENERAL);
			}
			IExpressionDescription mat_diffu = desc.getFacets().get("mat_diffu");
			IExpressionDescription propor = desc.getFacets().get("proportion");
			IExpressionDescription radius = desc.getFacets().get("radius");
			if ( propor != null && mat_diffu != null ) {
				desc.error("\"mat_diffu:\" and \"proportion:\" can not be used at the same time", IGamlIssue.GENERAL);
			}

			if ( radius != null && propor == null ) {
				desc.error("\"radius:\" can be used only with \"proportion:\"", IGamlIssue.GENERAL);
			}

		}
	}

	private boolean is_torus = false;
	private String var_diffu = "";
	private  String species_diffu;
	// true for convolution, false for dot_product
	private boolean method_diffu = true;
	boolean initialized = false;
	int cLen = 1;
	double[][] mask, mat_diffu;
	List<Integer> agents;

	public DiffusionStatement(final IDescription desc) {
		super(desc);
		method_diffu = getLiteral(IKeyword.METHOD, "convolution").equals("convolution");
		//species_diffu = getLiteral(IKeyword.ON);

	}

	public void doDiffusion2(final IScope scope) {

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

	double[] input, output;
	int nbRows, nbCols;

	public void initDiffusion(final IScope scope, final IPopulation pop) {

		if ( getFacet("proportion") != null ) {
			double init_proportion = Cast.asFloat(scope, getFacet("proportion").value(scope));
			int radius = 1;
			if ( getFacet("radius") != null ) {
				radius = Cast.asInt(scope, getFacet("radius").value(scope));
			}
			mat_diffu = new double[radius * 2 + 1][radius * 2 + 1];
			for ( int i = 0; i <= radius * 2; i++ ) {
				Arrays.fill(mat_diffu[i], init_proportion);
			}
		}
		for ( int i = 0; i < input.length; i++ ) {
			if (agents == null || agents.contains(i))
				input[i] = Cast.asFloat(scope, pop.get(scope, i).getDirectVarValue(scope, var_diffu));
			else 
				input[i] = 0;
			
		}
		
		Arrays.fill(output, 0d);
	}

	public void doDiffusion1(final IScope scope) {

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

	private void initialize(final IScope scope) {
		initialized = true;
		cLen = Cast.asInt(scope, getFacetValue(scope, IKeyword.CYCLE_LENGTH, 1));
		
		Object obj = getFacetValue(scope, IKeyword.ON);
		GridPopulation pop = null;
		if (obj instanceof ISpecies) {
			agents = null;
			if (((ISpecies)obj).isGrid())
				pop = (GridPopulation) ((ISpecies)obj).getPopulation(scope);
		} else {
			IList<IAgent> ags = Cast.asList(scope, obj);
			if (! ags.isEmpty()) {
				ISpecies sp = ags.get(0).getSpecies();
				if (sp.isGrid()) {
					pop = (GridPopulation) sp.getPopulation(scope);
					agents = new ArrayList<Integer>();
					for (IAgent ag : ags) 
						agents.add(ag.getIndex());
				} else {
					throw GamaRuntimeException.error("Diffusion statement works only on grid agents", scope);
				}
				
			}
		}
		
		species_diffu = pop.getName();
		input = new double[pop.length(scope)];
		output = new double[pop.length(scope)];
		nbRows = ((IGrid) pop.getTopology().getPlaces()).getRows(scope);
		nbCols = ((IGrid) pop.getTopology().getPlaces()).getCols(scope);
		is_torus = pop.getTopology().isTorus();
		mask = translateMatrix(scope, Cast.asMatrix(scope, getFacetValue(scope, IKeyword.MASK)));
		mat_diffu = translateMatrix(scope, Cast.asMatrix(scope, getFacetValue(scope, "mat_diffu")));
		var_diffu = Cast.asString(scope, getFacetValue(scope, IKeyword.VAR));
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		if ( !initialized ) {
			initialize(scope);
		}
		IPopulation pop = scope.getAgentScope().getPopulationFor(species_diffu);

		for ( int time = 0; time < cLen; time++ ) {
			initDiffusion(scope, pop);
			
			if ( !method_diffu ) {
			
				doDiffusion2(scope);
			} else {
				doDiffusion1(scope);
			}
			finishDiffusion(scope, pop);
		}
		return null;
	}
}
