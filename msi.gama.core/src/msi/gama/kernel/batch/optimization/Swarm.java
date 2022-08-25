/*******************************************************************************************************
 *
 * Swarm.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.kernel.batch.optimization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.batch.StoppingCriterion;
import msi.gama.kernel.batch.StoppingCriterionMaxIt;
import msi.gama.kernel.experiment.ParametersSet;
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
import msi.gama.util.GamaMapFactory;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

/**
 * The Class Swarm.
 */
@symbol (
		name = IKeyword.PSO,
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
						name = Swarm.ITER_MAX,
						type = IType.INT,
						optional = false,
						doc = @doc ("number of iterations")),
				@facet (
						name = Swarm.NUM_PARTICLES,
						type = IType.INT,
						optional = true,
						doc = @doc ("number of particles")),
				@facet (
						name = Swarm.INERTIA_WEIGHT,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("weight for the inertia component")),
				@facet (
						name = Swarm.COGNITIVE_WEIGHT,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("weight for the cognitive component")),
				@facet (
						name = Swarm.SOCIAL_WEIGHT,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("weight for the social component")),
				@facet (
						name = IKeyword.MAXIMIZE,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("the value the algorithm tries to maximize")),
				@facet (
						name = IKeyword.MINIMIZE,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("the value the algorithm tries to minimize")),
				@facet (
						name = IKeyword.AGGREGATION,
						type = IType.LABEL,
						optional = true,
						values = { IKeyword.MIN, IKeyword.MAX, "avr" },
						doc = @doc ("the agregation method")) },
		omissible = IKeyword.NAME)
@doc (
		value = "This algorithm is an implementation of the Particle Swarm Optimization algorithm. Only usable for numerical paramaters and based on a continuous parameter space search. See the wikipedia article for more details.",
		usages = { @usage (
				value = "As other batch methods, the basic syntax of the `pso` statement uses `method pso` instead of the expected `pso name: id` : ",
				examples = { @example (
						value = "method pso [facet: value];",
						isExecutable = false) }),
				@usage (
						value = "For example: ",
						examples = { @example (
								value = "method pso iter_max: 50 num_particles: 10 weight_inertia:0.7 weight_cognitive: 1.5 weight_social: 1.5 maximize: food_gathered; ",
								isExecutable = false) }) })
public class Swarm extends AOptimizationAlgorithm {

	/** The Constant DEFAULT_INERTIA. */
	public static final double DEFAULT_INERTIA = 0.729844;
	
	/** The Constant DEFAULT_COGNITIVE. */
	public static final double DEFAULT_COGNITIVE = 1.496180; // Cognitive component.
	
	/** The Constant DEFAULT_SOCIAL. */
	public static final double DEFAULT_SOCIAL = 1.496180; // Social component.

	/** The Constant ITER_MAX. */
	protected static final String ITER_MAX = "iter_max";
	
	/** The Constant NUM_PARTICLES. */
	protected static final String NUM_PARTICLES = "num_particles";
	
	/** The Constant INERTIA_WEIGHT. */
	protected static final String INERTIA_WEIGHT = "weight_inertia";
	
	/** The Constant COGNITIVE_WEIGHT. */
	protected static final String COGNITIVE_WEIGHT = "weight_cognitive";
	
	/** The Constant SOCIAL_WEIGHT. */
	protected static final String SOCIAL_WEIGHT = "weight_social";

	/** The stopping criterion. */
	StoppingCriterion stoppingCriterion = null;
	
	/** The max it. */
	int maxIt;
	
	/** The num particles. */
	int numParticles;
	
	/** The weight inertia. */
	double weightInertia;
	
	/** The weight cognitive. */
	double weightCognitive;
	
	/** The weight social. */
	double weightSocial;

	/**
	 * Instantiates a new swarm.
	 *
	 * @param species the species
	 */
	public Swarm(final IDescription species) {
		super(species);
		initParams();
	}

	@Override
	public void initParams(final IScope scope) {
		final IExpression maxItExp = getFacet(ITER_MAX);
		if (maxItExp != null) {
			maxIt = Cast.asInt(scope, maxItExp.value(scope));
			stoppingCriterion = new StoppingCriterionMaxIt(maxIt);
		}

		final IExpression numParticExp = getFacet(NUM_PARTICLES);
		if (maxItExp != null) {
			numParticles = Cast.asInt(scope, numParticExp.value(scope));
		} else {
			numParticles = 10;
		}

		final IExpression intertiaExp = getFacet(INERTIA_WEIGHT);
		if (intertiaExp != null) {
			weightInertia = Cast.asFloat(scope, intertiaExp.value(scope));
		} else {
			weightInertia = DEFAULT_INERTIA;
		}

		final IExpression cognitiveExp = getFacet(COGNITIVE_WEIGHT);
		if (cognitiveExp != null) {
			weightCognitive = Cast.asFloat(scope, cognitiveExp.value(scope));
		} else {
			weightCognitive = DEFAULT_COGNITIVE;
		}

		final IExpression socialExp = getFacet(SOCIAL_WEIGHT);
		if (socialExp != null) {
			weightSocial = Cast.asFloat(scope, socialExp.value(scope));
		} else {
			weightSocial = DEFAULT_SOCIAL;
		}
	}

	@Override
	public ParametersSet findBestSolution(final IScope scope) throws GamaRuntimeException {
		Particle[] particles = initialize(scope);

		int nbIt = 0;

		final Map<String, Object> endingCritParams = new Hashtable<>();
		endingCritParams.put("Iteration", Integer.valueOf(nbIt));
		while (!stoppingCriterion.stopSearchProcess(endingCritParams)) {
			Map<ParametersSet, List<Particle>> soltTotest = GamaMapFactory.create();

			if (GamaExecutorService.CONCURRENCY_SIMULATIONS_ALL.getValue()
					&& !currentExperiment.getParametersToExplore().isEmpty()) {
				for (Particle particle : particles) {
					List<Particle> ps = null;
					if (soltTotest.containsKey(particle.getPosition())) {
						ps = soltTotest.get(particle.getPosition());
					} else {
						ps = new ArrayList<>();
					}
					ps.add(particle);
					soltTotest.put(particle.getPosition(), ps);
				}
			}
			evaluation(particles, soltTotest);
			for (Particle p : particles) {
				updateVelocity(scope, p);
				p.updatePosition(scope);
			}

			nbIt++;
			endingCritParams.put("Iteration", Integer.valueOf(nbIt));
		}

		return getBestSolution();
	}

	/**
	 * Test solutions.
	 *
	 * @param solutions the solutions
	 * @return the map
	 */
	public Map<ParametersSet, Double> testSolutions(final Collection<ParametersSet> solutions) {
		Map<ParametersSet, Double> results = GamaMapFactory.create();
		solutions.removeIf(a -> a == null);
		List<ParametersSet> solTotest = new ArrayList<>();
		for (ParametersSet sol : solutions) {
			if (testedSolutions.containsKey(sol)) {
				results.put(sol, testedSolutions.get(sol));
			} else {
				solTotest.add(sol);
			}
		}
		Map<ParametersSet, Double> res = currentExperiment.launchSimulationsWithSolution(solTotest).entrySet().stream()
				.collect(Collectors.toMap(Entry::getKey, e -> (Double) e.getValue().get(IKeyword.FITNESS).get(0)));
		testedSolutions.putAll(res);
		results.putAll(res);

		return results;
	}

	/**
	 * Evaluation.
	 *
	 * @param particles the particles
	 * @param soltTotest the solt totest
	 */
	public void evaluation(final Particle[] particles, final Map<ParametersSet, List<Particle>> soltTotest) {
		if (GamaExecutorService.CONCURRENCY_SIMULATIONS_ALL.getValue()
				&& !currentExperiment.getParametersToExplore().isEmpty()) {
			Map<ParametersSet, Double> res = testSolutions(soltTotest.keySet());
			for (ParametersSet ps : res.keySet()) {
				for (Particle particle : soltTotest.get(ps)) {
					particle.updatePersonalBest();
					// updateGlobalBest(particle);
				}
			}

		} else {
			for (int i = 0; i < numParticles; i++) {
				Particle particle = particles[i];
				particle.eval();

				particle.updatePersonalBest();
				// updateGlobalBest(particle);
			}
		}

	}

	/**
	 * Create a set of particles, each with random starting positions.
	 *
	 * @return an array of particles
	 */
	private Particle[] initialize(final IScope scope) {
		Particle[] particles = new Particle[numParticles];
		Map<ParametersSet, List<Particle>> soltTotest = GamaMapFactory.create();
		for (int i = 0; i < numParticles; i++) {
			Particle particle = new Particle(scope, currentExperiment, this, testedSolutions);
			particles[i] = particle;
			if (GamaExecutorService.CONCURRENCY_SIMULATIONS_ALL.getValue()
					&& !currentExperiment.getParametersToExplore().isEmpty()) {
				List<Particle> ps = null;
				if (soltTotest.containsKey(particle.getPosition())) {
					ps = soltTotest.get(particle.getPosition());
				} else {
					ps = new ArrayList<>();
				}
				ps.add(particle);
				soltTotest.put(particle.getPosition(), ps);
			}
		}
		evaluation(particles, soltTotest);

		return particles;
	}

	/**
	 * Update the velocity of a particle using the velocity update formula
	 *
	 * @param particle
	 *            the particle to update
	 */
	private void updateVelocity(final IScope scope, final Particle particle) {
		ParametersSet oldVelocity = particle.getVelocity();
		ParametersSet pBest = new ParametersSet(particle.getBestPosition());
		ParametersSet gBest = new ParametersSet(bestSolution);
		ParametersSet pos = particle.getPosition();

		double r1 = scope.getRandom().next();
		double r2 = scope.getRandom().next();

		// The first product of the formula.
		ParametersSet newVelocity = new ParametersSet(oldVelocity);
		newVelocity = mul(scope, newVelocity, weightInertia);

		// The second product of the formula.
		pBest = sub(scope, pBest, pos);
		pBest = mul(scope, pBest, weightCognitive);
		pBest = mul(scope, pBest, r1);
		newVelocity = add(scope, newVelocity, pBest);

		// The third product of the formula.
		gBest = sub(scope, gBest, pos);
		gBest = mul(scope, gBest, weightSocial);
		gBest = mul(scope, gBest, r2);
		newVelocity = add(scope, newVelocity, gBest);

		particle.setVelocity(newVelocity);
	}

	/**
	 * Mul.
	 *
	 * @param scope the scope
	 * @param set the set
	 * @param val the val
	 * @return the parameters set
	 */
	protected ParametersSet mul(final IScope scope, final ParametersSet set, final double val) {
		for (String key : set.keySet()) { set.put(key, Cast.asFloat(scope, set.get(key)) * val); }
		return set;
	}

	/**
	 * Sub.
	 *
	 * @param scope the scope
	 * @param set1 the set 1
	 * @param set2 the set 2
	 * @return the parameters set
	 */
	protected ParametersSet sub(final IScope scope, final ParametersSet set1, final ParametersSet set2) {
		for (String key : set1.keySet()) {
			set1.put(key, Cast.asFloat(scope, set1.get(key)) - Cast.asFloat(scope, set2.get(key)));
		}
		return set1;
	}

	/**
	 * Adds the.
	 *
	 * @param scope the scope
	 * @param set1 the set 1
	 * @param set2 the set 2
	 * @return the parameters set
	 */
	protected ParametersSet add(final IScope scope, final ParametersSet set1, final ParametersSet set2) {
		for (String key : set1.keySet()) {
			set1.put(key, Cast.asFloat(scope, set1.get(key)) + Cast.asFloat(scope, set2.get(key)));
		}
		return set1;
	}

}
