/*******************************************************************************************************
 *
 * IGosplConcept.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.algo;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Define higher order abstract concepts of such as the generation processes, sampling algorithms and fitting procedures
 * <p>
 * <ul>
 * <li>Type of Generation : define the type of generation</li>
 * <li>Type of Algorithms : define how the entity are defined (correlated to the type of generation)</li>
 * <li>Type of Fitting : define the fitting procedure to estimate/approximate underlyin joint distribution</li>
 * </ul>
 *
 * @author kevinchapuis
 *
 */
public interface IGosplConcept {

	/**
	 * The GOSPL algorithms to sample/draw synthetic entity
	 *
	 * @author kevinchapuis
	 *
	 */
	public enum EGosplAlgorithm {

		/** The hs. */
		HS("Hierarchical Sampling", EGosplGenerationConcept.SR),

		/** The ds. */
		DS("Direct Sampling", EGosplGenerationConcept.SR),

		/** The sa. */
		SA("Simulated Annealing", EGosplGenerationConcept.CO),

		/** The tabu. */
		TABU("Tabu Search", EGosplGenerationConcept.CO),

		/** The rs. */
		RS("Random Search (Hill Climbing)", EGosplGenerationConcept.CO),

		/** The us. */
		US("Uniform Sampling", EGosplGenerationConcept.CO);

		/** The name. */
		public String name;

		/** The concept. */
		public EGosplGenerationConcept concept;

		/**
		 * Instantiates a new e gospl algorithm.
		 *
		 * @param name
		 *            the name
		 * @param concept
		 *            the concept
		 */
		EGosplAlgorithm(final String name, final EGosplGenerationConcept concept) {
			this.name = name;
			this.concept = concept;
		}

		/**
		 * Gets the concept available algorithms.
		 *
		 * @param concept
		 *            the concept
		 * @return the concept available algorithms
		 */
		public static Set<EGosplAlgorithm> getConceptAvailableAlgorithms(final EGosplGenerationConcept concept) {
			return Arrays.asList(EGosplAlgorithm.values()).stream().filter(algo -> algo.concept.equals(concept))
					.collect(Collectors.toSet());
		}

	}

	/**
	 * The GOSPL fitting procedure to estimate/approximate joint distribution
	 *
	 * @author kevinchapuis
	 *
	 */
	public enum EGosplFittingProcedure {

		/** The ipf. */
		IPF("Factor estimation model"),
		/** The mcmc. */
		MCMC("Metropolis-Hastings"),
		/** The bnsl. */
		BNSL("Structure learning model");

		/** The detail. */
		public String detail;

		/**
		 * Instantiates a new e gospl fitting procedure.
		 *
		 * @param detail
		 *            the detail
		 */
		EGosplFittingProcedure(final String detail) {
			this.detail = detail;
		}
	}

	/**
	 * The GOSPL synthetic population generation concepts
	 *
	 * @author kevinchapuis
	 */
	public enum EGosplGenerationConcept {

		/** The co. */
		CO("Draw individual entity from a sample to generate a synthetic population"),

		/** The sr. */
		SR("Sample individual entity from a distribution of attribute to generate a synthetic population"),

		/** The multilevel. */
		MULTILEVEL("Use a combination of methods to generate a multi-level synthetic population"),

		/** The mixture. */
		MIXTURE("Update individual entity to enhance a synthetic population");

		/** The description. */
		private final String description;

		/**
		 * Instantiates a new e gospl generation concept.
		 *
		 * @param description
		 *            the description
		 */
		EGosplGenerationConcept(final String description) {
			this.description = description;
		}
	}

}
