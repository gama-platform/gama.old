/*******************************************************************************************************
 *
 * ExhaustiveSearch.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.batch.exploration;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.batch.exploration.sampling.LatinhypercubeSampling;
import msi.gama.kernel.batch.exploration.sampling.MorrisSampling;
import msi.gama.kernel.batch.exploration.sampling.OrthogonalSampling;
import msi.gama.kernel.batch.exploration.sampling.SaltelliSampling;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.IParameter.Batch;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.metamodel.shape.GamaPoint;
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
import msi.gama.runtime.concurrent.GamaExecutorService;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaDate;
import msi.gama.util.GamaListFactory;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.operators.Cast;
import msi.gaml.operators.Random;
import msi.gaml.types.GamaDateType;
import msi.gaml.types.GamaFloatType;
import msi.gaml.types.GamaPointType;
import msi.gaml.types.IType;

/**
 * The Class ExhaustiveSearch.
 */
@symbol (
		name = { IKeyword.EXHAUSTIVE },
		kind = ISymbolKind.BATCH_METHOD,
		with_sequence = false,
		concept = { IConcept.BATCH, IConcept.ALGORITHM })
@inside (
		kinds = { ISymbolKind.EXPERIMENT })
@facets (
		value = { @facet (
				name = IKeyword.NAME,
				type = IType.ID,
				optional = false,
				internal = true,
				doc = @doc ("The name of the method. For internal use only")),
				@facet (
						name = ExhaustiveSearch.METHODS,
						type = IType.STRING,
						optional = true,
						doc = @doc ("The name of the method you want to use. saltelli/morris/latinhypercube")),
				@facet (
						name = ExhaustiveSearch.SAMPLE_SIZE,
						type = IType.INT,
						optional = true,
						doc = @doc ("The number of sample required, 132 by default")),
				@facet (
						name = ExhaustiveSearch.NB_LEVELS,
						type = IType.INT,
						optional = true,
						doc = @doc ("The number of levels for morris sampling, 4 by default")),
				@facet (
						name = ExhaustiveSearch.ITERATIONS,
						type = IType.INT,
						optional = true,
						doc = @doc ("The number of iteration for orthogonal sampling, 5 by default"))

		},
		omissible = IKeyword.NAME)
@doc (
		value = "This is the standard batch method. The exhaustive mode is defined by default when there is no method element present in the batch section. It explores all the combination of parameter values in a sequential way. You can also choose a sampling method for the exploration. See [batch161 the batch dedicated page].",
		usages = { @usage (
				value = "As other batch methods, the basic syntax of the exhaustive statement uses `method exhaustive` instead of the expected `exhaustive name: id` : ",
				examples = { @example (
						value = "method exhaustive [facet: value];",
						isExecutable = false) }),
				@usage (
						value = "For example: ",
						examples = { @example (
								value = "method exhaustive maximize: food_gathered;",
								isExecutable = false) }) })
public class ExhaustiveSearch extends AExplorationAlgorithm {
	/** The Constant Method */
	public static final String METHODS = "sampling";

	/** The Constant SAMPLE_SIZE */
	public static final String SAMPLE_SIZE = "sample";

	/** The Constant NB_LEVELS */
	public static final String NB_LEVELS = "levels";
	
	/**The Constant ITERATIONS*/
	public static final String ITERATIONS="iterations";
	
	private final int __default_step_factor = 10;
	
	private int sample_size;
	private int nb_levels;
	private int iterations;

	private List<Batch> parameters;

	/**
	 * Instantiates a new exhaustive search.
	 *
	 * @param desc the desc
	 */
	public ExhaustiveSearch(final IDescription desc) { super(desc); }
	
	@Override
	public void setChildren(final Iterable<? extends ISymbol> children) {}

	@Override
	public void explore(final IScope scope) throws GamaRuntimeException {
		
		List<Batch> params = currentExperiment.getParametersToExplore();

		parameters = parameters == null ? params : parameters;
		List<ParametersSet> sets;

		String method = hasFacet(ExhaustiveSearch.METHODS) ? 
				Cast.asString(scope, getFacet(METHODS).value(scope)) : "";
		sets = switch (method) {
			case IKeyword.MORRIS -> MorrisExhaustive(scope);
			case IKeyword.SALTELLI -> SaltelliExhaustive(scope);
			case IKeyword.LHS -> LatinHypercubeExhaustive(scope);
			case IKeyword.ORTHOGONAL -> OrthogonalExhaustive(scope);
			default -> buildParameterSets(scope, new ArrayList<>(), 0);
		};

		if (GamaExecutorService.CONCURRENCY_SIMULATIONS_ALL.getValue()) {
			currentExperiment.launchSimulationsWithSolution(sets);
		} else {
			for (ParametersSet sol : sets) { currentExperiment.launchSimulationsWithSolution(sol); }
		}

	}

	@Override
	public List<ParametersSet> buildParameterSets(final IScope scope, final List<ParametersSet> sets, final int index) {
		List<ParametersSet> sets2 = new ArrayList<>();
		final List<Batch> variables = currentExperiment.getParametersToExplore();
		if (variables.isEmpty()) return sets2;
		if (sets == null) {throw GamaRuntimeException.error("Cannot build a sample with empty parameter set", scope);}
		if (sets.isEmpty()) { sets.add(new ParametersSet()); }

		final IParameter.Batch var = variables.get(index);
		for (ParametersSet solution : sets) {
			@SuppressWarnings("rawtypes") 
			List vals = (var.getAmongValue(scope) != null) ? var.getAmongValue(scope) : getParameterSwip(scope, var);
			for (final Object val : vals) {
				ParametersSet ps = new ParametersSet(solution);
				ps.put(var.getName(), val);
				sets2.add(ps);
			}
		}
		if (index == variables.size() - 1) return sets2;
		return buildParameterSets(scope, sets2, index + 1);
	}

	// ##################### Methods for sampling ######################
	/**
	 * 4 methods:
	 * Morris
	 * Saltelli
	 * Latin Hypercube
	 * Orthogonal Latin Hypercube
	 */

	private List<ParametersSet> MorrisExhaustive(final IScope scope) {
		if(hasFacet(ExhaustiveSearch.SAMPLE_SIZE)) {
			this.sample_size= Cast.asInt(scope, getFacet(SAMPLE_SIZE).value(scope));
		}else {
			this.sample_size=132;
		}
		if (hasFacet(ExhaustiveSearch.NB_LEVELS)) {
			this.nb_levels = Cast.asInt(scope, getFacet(NB_LEVELS).value(scope));
		} else {
			this.nb_levels = 4;
		}

		MorrisSampling morris_samples = new MorrisSampling();

		return morris_samples.MakeMorrisSampling(nb_levels, this.sample_size, parameters, scope);

	}

	private List<ParametersSet> LatinHypercubeExhaustive(final IScope scope) {
		if(hasFacet(ExhaustiveSearch.SAMPLE_SIZE)) {
			this.sample_size= Cast.asInt(scope, getFacet(SAMPLE_SIZE).value(scope));
		}else {
			this.sample_size=132;
		}

		LatinhypercubeSampling LHS = new LatinhypercubeSampling();
		return LHS.LatinHypercubeSamples(sample_size, parameters, scope.getRandom().getGenerator(), scope);
	}

	private List<ParametersSet> SaltelliExhaustive(final IScope scope) {
		if(hasFacet(ExhaustiveSearch.SAMPLE_SIZE)) {
			this.sample_size= Cast.asInt(scope, getFacet(SAMPLE_SIZE).value(scope));
		}else {
			this.sample_size=132;
		}

		SaltelliSampling saltelli = new SaltelliSampling();
		return saltelli.MakeSaltelliSampling(scope, sample_size, parameters);
	}

	private List<ParametersSet> OrthogonalExhaustive(final IScope scope) {
		if(hasFacet(ExhaustiveSearch.SAMPLE_SIZE)) {
			this.sample_size= Cast.asInt(scope, getFacet(SAMPLE_SIZE).value(scope));
		}else {
			this.sample_size=132;
		}
		
		if(hasFacet(ExhaustiveSearch.ITERATIONS)) {
			this.iterations= Cast.asInt(scope, getFacet(SAMPLE_SIZE).value(scope));
		}else {
			this.iterations=5;
		}
		
        OrthogonalSampling ortho= new OrthogonalSampling();
        return ortho.OrthogonalSamples(sample_size,iterations, parameters,scope.getRandom().getGenerator(),scope);
        	
	}
	
	// ##################### Methods to determine possible values based on exhaustive ######################

	/**
	 * Return all the possible value of a parameter based on 
	 * @param scope
	 * @param var
	 * @return
	 */
	private List<Object> getParameterSwip(IScope scope, Batch var) {
		List<Object> res = new ArrayList<>();
		switch (var.getType().id()) {
		case IType.INT:
			int minValue = Cast.asInt(scope, var.getMinValue(scope));
			int maxValue = Cast.asInt(scope, var.getMaxValue(scope));
			double stepValue = 1;
			if (hasFacet(IKeyword.STEP)) {
				stepValue = Cast.asInt(scope, var.getStepValue(scope));
			} else if ((maxValue - minValue) > __default_step_factor) {
				stepValue = (maxValue - minValue) / __default_step_factor;
			}
			   
			while (minValue <= maxValue) {
				res.add(minValue);
				minValue = minValue + (int) stepValue + (Random.opFlip(scope, (stepValue - (int) stepValue)) ? 1 : 0);
			}
			break;
		case IType.FLOAT:
			double floatValue = Cast.asFloat(scope, var.getMinValue(scope));
			double maxFloatValue = Cast.asFloat(scope, var.getMaxValue(scope));
			double stepFloatValue = 0.1;
			if (hasFacet(IKeyword.STEP)) {
				stepFloatValue = Cast.asFloat(scope, var.getStepValue(scope));
			} else {
				stepFloatValue = (maxFloatValue - floatValue) / __default_step_factor;
			}
			
			while (floatValue <= maxFloatValue) {
				res.add(floatValue); 
				floatValue = floatValue + stepFloatValue;
			}
			break;
		case IType.DATE:
			GamaDate dateValue = GamaDateType.staticCast(scope, var.getMinValue(scope), null, false);
			GamaDate maxDateValue = GamaDateType.staticCast(scope, var.getMaxValue(scope), null, false);
			while (dateValue.isSmallerThan(maxDateValue, false)) {
				res.add(dateValue);
				dateValue = dateValue.plus(Cast.asFloat(scope, var.getStepValue(scope)), ChronoUnit.SECONDS);
			}
			break;
		case IType.POINT:
			GamaPoint pointValue = Cast.asPoint(scope, var.getMinValue(scope));
			GamaPoint maxPointValue = Cast.asPoint(scope, var.getMaxValue(scope));
			GamaPoint increment = new GamaPoint();
			if (hasFacet(IKeyword.STEP)) {
				increment = GamaPointType.staticCast(scope, var.getStepValue(scope), true);
				if (increment == null) { 
					Double d = GamaFloatType.staticCast(scope, var.getStepValue(scope), null, false);
					if (d == null) {GamaRuntimeException.error("Cannot retrieve steps "+var.getStepValue(scope)+" of paramter "+var.getName(), scope);}
					increment = new GamaPoint(d, d, d);
				}
			} else {
				
			}
			while (pointValue.smallerThanOrEqualTo(maxPointValue)) {
				res.add(pointValue);
				pointValue = pointValue.plus(Cast.asPoint(scope, increment));
			}
			break;
		default:
			double varValue = Cast.asFloat(scope, var.getMinValue(scope));
			double maxVarValue = Cast.asFloat(scope, var.getMaxValue(scope));
			double floatcrement = 1;
			if (hasFacet(IKeyword.STEP)) {
				floatcrement = Cast.asFloat(scope, var.getStepValue(scope));
			} else {
				floatcrement = (maxVarValue - varValue) / __default_step_factor;
			}
			while (varValue <= Cast.asFloat(scope, var.getMaxValue(scope))) {
				
				if (var.getType().id() == IType.INT) {
					res.add( (int) varValue );
				} else if (var.getType().id() == IType.FLOAT) {
					res.add( varValue );
				} else {
					continue;
				}
				
				varValue = varValue + floatcrement;
			}
		}
		return res;
	}

}
