package gospl.algo;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Define higher order abstract concepts of such as the generation processes, sampling algorithms and fitting procedures
 * <p>
 * <ul>
 * <li> Type of Generation : define the type of generation </li>
 * <li> Type of Algorithms : define how the entity are defined (correlated to the type of generation) </li>
 * <li> Type of Fitting : define the fitting procedure to estimate/approximate underlyin joint distribution </li>
 * </ul>
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

		HS ("Hierarchical Sampling", EGosplGenerationConcept.SR), 
		DS ("Direct Sampling", EGosplGenerationConcept.SR), 
		SA ("Simulated Annealing", EGosplGenerationConcept.CO), 
		TABU ("Tabu Search", EGosplGenerationConcept.CO), 
		RS ("Random Search (Hill Climbing)", EGosplGenerationConcept.CO),
		US ("Uniform Sampling", EGosplGenerationConcept.CO);
		
		public String name;
		public EGosplGenerationConcept concept;
		
		private EGosplAlgorithm(String name, EGosplGenerationConcept concept) {
			this.name = name;
			this.concept = concept;
		}
		
		public static Set<EGosplAlgorithm> getConceptAvailableAlgorithms(EGosplGenerationConcept concept) {
			return Arrays.asList(EGosplAlgorithm.values()).stream()
					.filter(algo -> algo.concept.equals(concept)).collect(Collectors.toSet());
		}
		
	}

	/**
	 * The GOSPL fitting procedure to estimate/approximate joint distribution
	 * 
	 * @author kevinchapuis
	 *
	 */
	public enum EGosplFittingProcedure {
		IPF ("Factor estimation model"), 
		MCMC ("Metropolis-Hastings"),
		BNSL ("Structure learning model");
		
		public String detail;
		
		private EGosplFittingProcedure(String detail) {this.detail = detail;}
	}
	
	/**
	 * The GOSPL synthetic population generation concepts
	 * 
	 * @author kevinchapuis
	 */
	public enum EGosplGenerationConcept {
		CO ("Draw individual entity from a sample to generate a synthetic population"), 
		SR ("Sample individual entity from a distribution of attribute to generate a synthetic population"), 
		MULTILEVEL ("Use a combination of methods to generate a multi-level synthetic population"), 
		MIXTURE ("Update individual entity to enhance a synthetic population");
		
		public String description;
		
		private EGosplGenerationConcept(String description) { this.description = description;}
	}
	
}
