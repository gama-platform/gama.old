/*******************************************************************************************************
 *
 * GSUtilPopulation.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gospl.generator.util;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import core.configuration.GenstarJsonUtil;
import core.configuration.dictionary.AttributeDictionary;
import core.metamodel.attribute.Attribute;
import core.metamodel.value.IValue;
import core.util.exception.GSIllegalRangedData;
import core.util.random.GenstarRandom;
import gospl.GosplPopulation;
import gospl.distribution.GosplNDimensionalMatrixFactory;
import gospl.distribution.exception.IllegalDistributionCreation;
import gospl.distribution.matrix.AFullNDimensionalMatrix;
import gospl.distribution.matrix.ASegmentedNDimensionalMatrix;
import gospl.generator.ISyntheticGosplPopGenerator;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Util class to generate population from a dictionary using either a custom generator or, if not provided, a random
 * default generator.
 * <p>
 * <b>HINT</b>: Could be realy usefull when you want to quickly generate a population and you do not care about how
 * reliable it is. For ex. to make test on or to be used for localisation / networking.
 *
 * @author kevinchapuis
 *
 */
public class GSUtilPopulation {

	/** The Constant NO_POPULATION_HAVE_BEEN_GENERATED_SEE_BUILD_POPULATION. */
	private static final String NO_POPULATION_HAVE_BEEN_GENERATED_SEE_BUILD_POPULATION =
			"No population have been generated - see #buildPopulation";

	/** The generator. */
	private ISyntheticGosplPopGenerator generator;

	/** The population. */
	private GosplPopulation population = null;

	/** The distribution. */
	private AFullNDimensionalMatrix<Double> distribution = null;

	/** The contingency. */
	private AFullNDimensionalMatrix<Integer> contingency = null;

	/** The dico. */
	private AttributeDictionary dico;

	/** The path to dictionary. */
	private final Path pathToDictionary =
			FileSystems.getDefault().getPath("src", "test", "resources", "attributedictionary");

	/** The default dictionary. */
	public static final String defaultDictionary = "defaultDictionary.gns";

	/**
	 * Default constructor that use a pre-define dictionary of attributes.
	 *
	 * @throws GSIllegalRangedData
	 */
	public GSUtilPopulation() {
		this(defaultDictionary);
	}

	/**
	 * Uses custom generator to generate a population. Relationship to dictionary is not guarantee and must be set
	 * before calling {@link GSUtilPopulation}
	 *
	 * @param dictionary
	 * @param generator
	 */
	public GSUtilPopulation(final AttributeDictionary dictionary, final ISyntheticGosplPopGenerator generator) {
		this.dico = dictionary;
		this.generator = generator;
	}

	/**
	 * Uses a random generator to generate a population based on a dictionary of attribute
	 *
	 * @param dictionary
	 */
	public GSUtilPopulation(final AttributeDictionary dictionary) {
		this.dico = dictionary;
		this.generator = new GSUtilGenerator(dico);
	}

	/**
	 * Uses custom generator to generate a population based on a dictionary of attribute
	 *
	 * @param dictionaryFile
	 * @param generator
	 */
	public GSUtilPopulation(final String dictionaryFile, final ISyntheticGosplPopGenerator generator) {
		try {
			this.dico = new GenstarJsonUtil().unmarshalFromGenstarJson(pathToDictionary.resolve(dictionaryFile),
					AttributeDictionary.class);
		} catch (IllegalArgumentException | IOException e) {

			e.printStackTrace();
		}
		this.generator = generator;
	}

	/**
	 * same as {@link #GSUtilPopulation(DemographicDictionary)} with dictionary path
	 *
	 * @param dictionaryFile
	 */
	public GSUtilPopulation(final Path dictionaryFile) {
		try {
			this.dico = new GenstarJsonUtil().unmarshalFromGenstarJson(dictionaryFile, AttributeDictionary.class);
		} catch (IllegalArgumentException | IOException e) {

			e.printStackTrace();
		}
		this.generator = new GSUtilGenerator(dico);
	}

	/**
	 * same as {@link #GSUtilPopulation(DemographicDictionary)} with dictionary path provided as {@link String}
	 *
	 * @param dictionaryFile
	 */
	public GSUtilPopulation(final String dictionaryFile) {
		try {
			this.dico = new GenstarJsonUtil().unmarshalFromGenstarJson(pathToDictionary.resolve(dictionaryFile),
					AttributeDictionary.class);
		} catch (IllegalArgumentException | IOException e) {

			e.printStackTrace();
		}
		this.generator = new GSUtilGenerator(dico);
	}

	/**
	 * same as {@link #GSUtilPopulation(DemographicDictionary)} but with a custom dictionary based on attribute
	 * collection passed as argument
	 *
	 * @param dictionary
	 */
	@SuppressWarnings ("unchecked")
	public GSUtilPopulation(final Collection<Attribute<? extends IValue>> dictionary) {
		dico = new AttributeDictionary();
		dictionary.stream().forEach(att -> dico.addAttributes(att));
		this.generator = new GSUtilGenerator(dico);
	}

	/**
	 * Use util methods directly from a pre generated population
	 *
	 * @param population
	 */
	public GSUtilPopulation(final GosplPopulation population) {
		this.population = population;
	}

	// ---------------------------------------------------- //

	/**
	 * Path to dictionary
	 *
	 * @return
	 */
	public Path getPathToDictionary() { return pathToDictionary; }

	/**
	 * The dictionary of attribute used to generate entity wi th
	 *
	 * @return
	 */
	public AttributeDictionary getDictionary() { return dico; }

	// ---------------------------------------------------- //

	/**
	 * Create a population with random component and given attributes
	 *
	 * @param size
	 * @return
	 * @return
	 */
	public GosplPopulation buildPopulation(final int size) {
		this.population = generator.generate(size);
		return this.population;
	}

	// ---------------------------------------------------- //

	/**
	 * Get a contingency based on a created population using {@link #getPopulation(int)}
	 *
	 * @param size
	 * @return
	 */
	public AFullNDimensionalMatrix<Integer> getContingency() {
		if (this.population == null)
			throw new NullPointerException(NO_POPULATION_HAVE_BEEN_GENERATED_SEE_BUILD_POPULATION);
		if (this.contingency == null) {
			this.contingency = new GosplNDimensionalMatrixFactory().createContingency(this.population);
		}
		return this.contingency;
	}

	/**
	 * Get a frequency based on a created population using {@link #getPopulation(int)}
	 *
	 * @param size
	 * @return
	 */
	public AFullNDimensionalMatrix<Double> getFrequency() {
		if (this.population == null)
			throw new NullPointerException(NO_POPULATION_HAVE_BEEN_GENERATED_SEE_BUILD_POPULATION);
		if (this.distribution == null) {
			this.distribution = new GosplNDimensionalMatrixFactory().createDistribution(this.population);
		}
		return this.distribution;
	}

	/**
	 * Get a segmented frequency based on several created population using {@link #getPopulation(int)}
	 *
	 * @param segmentSize
	 * @return
	 * @throws IllegalDistributionCreation
	 */
	public ASegmentedNDimensionalMatrix<Double> getSegmentedFrequency(final int segmentSize)
			throws IllegalDistributionCreation {
		if (this.population == null)
			throw new NullPointerException(NO_POPULATION_HAVE_BEEN_GENERATED_SEE_BUILD_POPULATION);
		DEBUG.OUT("Try to build segmented matrix with {} dimensions" + this.dico.getAttributes().size());
		Map<Attribute<? extends IValue>, Double> attributesProb =
				this.dico.getAttributes().stream().collect(Collectors.toMap(Function.identity(), att -> 0.5));

		Collection<Set<Attribute<? extends IValue>>> segmentedAttribute = new HashSet<>();
		while (!segmentedAttribute.stream().flatMap(Set::stream).collect(Collectors.toSet())
				.containsAll(this.dico.getAttributes())) {
			Set<Attribute<? extends IValue>> atts = new HashSet<>();
			// WARNING: linked attribute could be in the same matrix
			for (Attribute<? extends IValue> attribute : attributesProb.keySet()) {
				if (atts.stream().anyMatch(a -> a.getReferentAttribute().equals(attribute)
						|| a.equals(attribute.getReferentAttribute()))) {
					continue;
				}
				if (GenstarRandom.getInstance().nextDouble() < attributesProb.get(attribute)) {
					atts.add(attribute);
					attributesProb.put(attribute, attributesProb.get(attribute) * 0.5);
				} else {
					attributesProb.put(attribute, Math.tanh(attributesProb.get(attribute) + 0.5));
				}
			}
			if (atts.size() < 2) { continue; }
			DEBUG.OUT("Build a new full inner matrix with {} attributes"
					+ atts.stream().map(Attribute::getAttributeName).collect(Collectors.joining(", ")));
			segmentedAttribute.add(atts);
		}
		DEBUG.OUT("Build the segmented matrix with {} inner full matrix" + segmentedAttribute.size());
		GosplNDimensionalMatrixFactory factory = new GosplNDimensionalMatrixFactory();
		return factory.createDistributionFromDistributions(segmentedAttribute.stream()
				.map(sa -> factory.createDistribution(sa, this.population)).collect(Collectors.toSet()));
	}

}
