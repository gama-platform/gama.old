package espacedev.gaml.extensions.genstar.utils;

import java.util.Arrays;
import java.util.List;

/**
 * 
 * Define the constant of the Genstar Gama plugin
 * 
 * @author kevinchapuis
 *
 */
public class GenStarConstant {

	public static final String GSGENERATOR = "generator";
	public static final String GSATTRIBUTES = "attributes";
	
	public static final Double EPSILON = Math.pow(10, -6);
	
	/**
	 * Interface to deal with aliases
	 * 
	 * @author kevinchapuis
	 *
	 */
	public interface IGSAlias {
		
		List<String> getAlias();
		
		/**
		 * Test if the alias is a valid one
		 * @param alias
		 * @return true if provided alias is authorized, false otherwise
		 */
		default boolean getMatch(String alias) {
			return getAlias().stream().anyMatch(elem -> elem.equalsIgnoreCase(alias)); 
		}
		
		default String getDefault() { return getAlias().get(0); }
		
	}
	
	/**
	 * The spatial distribution to be used in the localization process
	 * 
	 * @author kevinchapuis
	 *
	 */
	public enum SpatialDistribution implements IGSAlias {
		DEFAULT (Arrays.asList("uniform",""),SpatialDistributionConcept.SIMPLE),
		AREA (Arrays.asList("area","areal"),SpatialDistributionConcept.SIMPLE),
		CAPACITY (Arrays.asList("capacity","number"),SpatialDistributionConcept.NUMBER),
		DENSITY (Arrays.asList("density"),SpatialDistributionConcept.NUMBER);
		
		public enum SpatialDistributionConcept {
			SIMPLE, NUMBER, COMPLEX;
		}
		
		List<String> alias;
		SpatialDistributionConcept concept;
		
		private SpatialDistribution(List<String> alias, SpatialDistributionConcept sdp) { this.alias = alias; this.concept = sdp;}
		
		@Override
		public List<String> getAlias() { return alias; }
		
		public SpatialDistributionConcept getConcept() {return concept;}
		
	}
	
	public enum SpatialConstraint implements IGSAlias {
		CAPACITY (Arrays.asList("capacity","number","threshold")),
		DENSITY (Arrays.asList("density")),
		DISTANCE (Arrays.asList("distance","proximity"));
		
		private List<String> alias;
		
		private SpatialConstraint(List<String> alias) {this.alias = alias;}
		
		@Override
		public List<String> getAlias() {return alias;}
	}
	
	/**
	 * The generation algorithms available in the plugin
	 * 
	 * @author kevinchapuis
	 *
	 */
	public enum GenerationAlgorithm implements IGSAlias {
		DIRECTSAMPLING (Arrays.asList("Direct Sampling","DS","IS")), 
		HIERARCHICALSAMPLING (Arrays.asList("Hierarchical Sampling","HS")), 
		UNIFORMSAMPLING (Arrays.asList("Uniform Sampling","US","simple_draw")),
		MULTILAYER (Arrays.asList("Multi Type", "Multi Layer", "Household", "ML"));
		
		List<String> alias;
		
		private GenerationAlgorithm(List<String> alias) { this.alias = alias; }
		
		@Override
		public List<String> getAlias() { return alias; }
		
		public static GenerationAlgorithm getAlgorithm(String algorithm) {
			if(DIRECTSAMPLING.getMatch(algorithm)) {return DIRECTSAMPLING;}
			else if(HIERARCHICALSAMPLING.getMatch(algorithm)) {return HIERARCHICALSAMPLING;}
			else if(UNIFORMSAMPLING.getMatch(algorithm)) {return UNIFORMSAMPLING;}
			else if(MULTILAYER.getMatch(algorithm)) {return MULTILAYER;}
			else throw new IllegalArgumentException(algorithm+": No such generation algorithm supported");
		}
	}
	
	/**
	 * The different type of input data that can be process by Genstar Gama plugin
	 * 
	 * @author kevinchapuis
	 *
	 */
	public enum InputDataType implements IGSAlias {
		CONTINGENCY (Arrays.asList("Contingency","Contingency table","ContingencyTable")),
		FREQUENCY (Arrays.asList("Frequency","Frequency table","FrequencyTable","Global Frequency", "Global Frequency Table","GlobalFrequencyTable")),
		LOCAL (Arrays.asList("Local","Local Frequency Table","LocalFrequency","Local Frequency","LocalFrequencyTable")), 
		SAMPLE (Arrays.asList("Sample","Micro Sample","MicroSample","Micro Data","MicroData"));
		
		List<String> alias;
		
		private InputDataType(List<String> alias) { this.alias = alias; }
		
		@Override
		public List<String> getAlias() { return alias; }
		
	}
	
	/**
	 * The different type of network engine to be used by Genstar to generate interaction graph
	 * 
	 * @author kevinchapuis
	 *
	 */
	public enum NetworkEngine implements IGSAlias {
		COMPLETE (Arrays.asList("Complete", "Complet")),
		RANDOM (Arrays.asList("Random", "Uniform", "Aléatoire")),
		REGULAR (Arrays.asList("Regular", "Latice", "Regulier")),
		SPATIAL (Arrays.asList("Spatial", "Spatiale")),
		SCALE_FREE (Arrays.asList("Scale free", "SF", "Barabási–Albert", "Power low")),
		SMALL_WORLD (Arrays.asList("Small world", "SW", "Watts-Strogatz"));
		
		List<String> alias;
		
		private NetworkEngine(List<String> alias) { this.alias = alias; }

		@Override
		public List<String> getAlias() { return alias; }
	}
	
}
