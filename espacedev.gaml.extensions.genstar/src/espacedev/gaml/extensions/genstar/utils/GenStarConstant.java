/*******************************************************************************************************
 *
 * GenStarConstant.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
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

	/** The Constant GSGENERATOR. */
	public static final String GSGENERATOR = "generator";

	/** The Constant GSATTRIBUTES. */
	public static final String GSATTRIBUTES = "attributes";

	/** The Constant EPSILON. */
	public static final Double EPSILON = Math.pow(10, -6);

	/**
	 * Interface to deal with aliases
	 *
	 * @author kevinchapuis
	 *
	 */
	public interface IGSAlias {

		/**
		 * Gets the alias.
		 *
		 * @return the alias
		 */
		List<String> getAlias();

		/**
		 * Test if the alias is a valid one
		 *
		 * @param alias
		 * @return true if provided alias is authorized, false otherwise
		 */
		default boolean getMatch(final String alias) {
			return getAlias().stream().anyMatch(elem -> elem.equalsIgnoreCase(alias));
		}

		/**
		 * Gets the default.
		 *
		 * @return the default
		 */
		default String getDefault() { return getAlias().get(0); }

	}

	/**
	 * The spatial distribution to be used in the localization process
	 *
	 * @author kevinchapuis
	 *
	 */
	public enum SpatialDistribution implements IGSAlias {

		/** The default. */
		DEFAULT(Arrays.asList("uniform", ""), SpatialDistributionConcept.SIMPLE),

		/** The area. */
		AREA(Arrays.asList("area", "areal"), SpatialDistributionConcept.SIMPLE),

		/** The capacity. */
		CAPACITY(Arrays.asList("capacity", "number"), SpatialDistributionConcept.NUMBER),

		/** The density. */
		DENSITY(Arrays.asList("density"), SpatialDistributionConcept.NUMBER);

		/**
		 * The Enum SpatialDistributionConcept.
		 */
		public enum SpatialDistributionConcept {

			/** The simple. */
			SIMPLE,
			/** The number. */
			NUMBER,
			/** The complex. */
			COMPLEX;
		}

		/** The alias. */
		List<String> alias;

		/** The concept. */
		SpatialDistributionConcept concept;

		/**
		 * Instantiates a new spatial distribution.
		 *
		 * @param alias
		 *            the alias
		 * @param sdp
		 *            the sdp
		 */
		SpatialDistribution(final List<String> alias, final SpatialDistributionConcept sdp) {
			this.alias = alias;
			this.concept = sdp;
		}

		@Override
		public List<String> getAlias() { return alias; }

		/**
		 * Gets the concept.
		 *
		 * @return the concept
		 */
		public SpatialDistributionConcept getConcept() { return concept; }

	}

	/**
	 * The Enum SpatialConstraint.
	 */
	public enum SpatialConstraint implements IGSAlias {

		/** The capacity. */
		CAPACITY(Arrays.asList("capacity", "number", "threshold")),

		/** The density. */
		DENSITY(Arrays.asList("density")),

		/** The distance. */
		DISTANCE(Arrays.asList("distance", "proximity"));

		/** The alias. */
		private final List<String> alias;

		/**
		 * Instantiates a new spatial constraint.
		 *
		 * @param alias
		 *            the alias
		 */
		SpatialConstraint(final List<String> alias) {
			this.alias = alias;
		}

		@Override
		public List<String> getAlias() { return alias; }
	}

	/**
	 * The generation algorithms available in the plugin
	 *
	 * @author kevinchapuis
	 *
	 */
	public enum GenerationAlgorithm implements IGSAlias {

		/** The directsampling. */
		DIRECTSAMPLING(Arrays.asList("Direct Sampling", "DS", "IS")),

		/** The hierarchicalsampling. */
		HIERARCHICALSAMPLING(Arrays.asList("Hierarchical Sampling", "HS")),

		/** The uniformsampling. */
		UNIFORMSAMPLING(Arrays.asList("Uniform Sampling", "US", "simple_draw")),

		/** The multilayer. */
		MULTILAYER(Arrays.asList("Multi Type", "Multi Layer", "Household", "ML"));

		/** The alias. */
		List<String> alias;

		/**
		 * Instantiates a new generation algorithm.
		 *
		 * @param alias
		 *            the alias
		 */
		GenerationAlgorithm(final List<String> alias) {
			this.alias = alias;
		}

		@Override
		public List<String> getAlias() { return alias; }

		/**
		 * Gets the algorithm.
		 *
		 * @param algorithm
		 *            the algorithm
		 * @return the algorithm
		 */
		public static GenerationAlgorithm getAlgorithm(final String algorithm) {
			if (DIRECTSAMPLING.getMatch(algorithm)) return DIRECTSAMPLING;
			if (HIERARCHICALSAMPLING.getMatch(algorithm))
				return HIERARCHICALSAMPLING;
			else if (UNIFORMSAMPLING.getMatch(algorithm))
				return UNIFORMSAMPLING;
			else if (MULTILAYER.getMatch(algorithm))
				return MULTILAYER;
			else
				throw new IllegalArgumentException(algorithm + ": No such generation algorithm supported");
		}
	}

	/**
	 * The different type of input data that can be process by Genstar Gama plugin
	 *
	 * @author kevinchapuis
	 *
	 */
	public enum InputDataType implements IGSAlias {

		/** The contingency. */
		CONTINGENCY(Arrays.asList("Contingency", "Contingency table", "ContingencyTable")),

		/** The frequency. */
		FREQUENCY(Arrays.asList("Frequency", "Frequency table", "FrequencyTable", "Global Frequency",
				"Global Frequency Table", "GlobalFrequencyTable")),

		/** The local. */
		LOCAL(Arrays.asList("Local", "Local Frequency Table", "LocalFrequency", "Local Frequency",
				"LocalFrequencyTable")),

		/** The sample. */
		SAMPLE(Arrays.asList("Sample", "Micro Sample", "MicroSample", "Micro Data", "MicroData"));

		/** The alias. */
		List<String> alias;

		/**
		 * Instantiates a new input data type.
		 *
		 * @param alias
		 *            the alias
		 */
		InputDataType(final List<String> alias) {
			this.alias = alias;
		}

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

		/** The complete. */
		COMPLETE(Arrays.asList("Complete", "Complet")),

		/** The random. */
		RANDOM(Arrays.asList("Random", "Uniform", "Aléatoire")),

		/** The regular. */
		REGULAR(Arrays.asList("Regular", "Latice", "Regulier")),

		/** The spatial. */
		SPATIAL(Arrays.asList("Spatial", "Spatiale")),

		/** The scale free. */
		SCALE_FREE(Arrays.asList("Scale free", "SF", "Barabási–Albert", "Power low")),

		/** The small world. */
		SMALL_WORLD(Arrays.asList("Small world", "SW", "Watts-Strogatz"));

		/** The alias. */
		List<String> alias;

		/**
		 * Instantiates a new network engine.
		 *
		 * @param alias
		 *            the alias
		 */
		NetworkEngine(final List<String> alias) {
			this.alias = alias;
		}

		@Override
		public List<String> getAlias() { return alias; }
	}

}
