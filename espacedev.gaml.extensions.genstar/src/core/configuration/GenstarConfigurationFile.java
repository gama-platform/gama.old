package core.configuration;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import core.configuration.dictionary.IGenstarDictionary;
import core.configuration.jackson.GenstarConfigurationFileDeserializer;
import core.configuration.jackson.GenstarConfigurationFileSerializer;
import core.metamodel.attribute.Attribute;
import core.metamodel.io.GSSurveyWrapper;
import core.metamodel.value.IValue;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Data configuration consist in a base directory where to find resources, plus a list of wrapped file that encapsulate
 * data file plus information to read it, and finally dictionary to understand data in file
 * <p>
 * <ul>
 * <li>list of survey files
 * <li>list of survey attribute
 * <li>list of key attribute (link between survey and spatial attribute)
 * </ul>
 * <p>
 * Configuration file can be saved using Json based file with {@link GenstarJsonUtil}
 * <p>
 * TODO: add configuration for localization process
 *
 * @author kevinchapuis
 *
 */
@JsonTypeName (
		value = GenstarConfigurationFile.SELF)
@JsonSerialize (
		using = GenstarConfigurationFileSerializer.class)
@JsonDeserialize (
		using = GenstarConfigurationFileDeserializer.class)
public class GenstarConfigurationFile {

	public static final String SELF = "CONFIGURATION FILE";
	public static final String BASE_DIR = "MAIN DIRECTORY";

	public static final String INPUT_FILES = "INPUT FILES";
	public static final String DICOS = "DICTIONARIES";

	public static final String LAYER = "LAYER LEVEL";

	private final Map<GSSurveyWrapper, List<Integer>> dataFiles = new HashMap<>();

	private Set<IGenstarDictionary<Attribute<? extends IValue>>> dictionaries = new HashSet<>();

	/**
	 * The path in which the files included in this configuration is stored, if known.
	 */
	protected Path baseDirectory = null;

	/**
	 * Default constructor
	 */
	public GenstarConfigurationFile() {}

	// ------------------------------------------- //

	/**
	 * Gives the survey wrappers
	 *
	 * @return the wrapper and associated layer level
	 */
	@JsonProperty (GenstarConfigurationFile.INPUT_FILES)
	public Map<GSSurveyWrapper, List<Integer>> getWrappers() { return Collections.unmodifiableMap(dataFiles); }

	/**
	 * Set wrappers (serialization purpose)
	 *
	 * @param surveys
	 */
	@JsonProperty (GenstarConfigurationFile.INPUT_FILES)
	public void setWrappers(final Map<GSSurveyWrapper, List<Integer>> surveys) {
		this.dataFiles.putAll(surveys);
	}

	/**
	 * Default wrappers for 0 layer
	 *
	 * @return
	 */
	public List<GSSurveyWrapper> getSurveyWrappers() { return this.getSurveyWrappers(0); }

	/**
	 * Gives a collection of survey wrapper for a particular layer
	 *
	 * @param level
	 * @return a collection of wrapper
	 */
	public List<GSSurveyWrapper> getSurveyWrappers(final int level) {
		if (this.dataFiles.values().stream().noneMatch(levels -> levels.contains(level)))
			throw new NullPointerException("No survey wrappers for layer " + level);
		return this.dataFiles.keySet().stream().filter(wrapper -> this.dataFiles.get(wrapper).contains(level))
				.collect(Collectors.toList());
	}

	/**
	 * Set wrappers for level 0
	 *
	 * @param surveys
	 */
	public void setSurveyWrappers(final List<GSSurveyWrapper> surveys) {
		surveys.stream().forEach(wrapper -> this.dataFiles.put(wrapper, Arrays.asList(0)));
	}

	/**
	 * Add new wrapper for layer 0
	 *
	 * @param survey
	 */
	public void addSurveyWrapper(final GSSurveyWrapper survey) {
		this.dataFiles.putIfAbsent(survey, Arrays.asList(0));
	}

	/**
	 * Add new wrapper for specified layers
	 *
	 * @param survey
	 * @param layers
	 */
	public void addSurveyWrapper(final GSSurveyWrapper survey, final Integer... layers) {
		this.dataFiles.putIfAbsent(survey, Arrays.asList(layers));
	}

	/**
	 * Get 0 based level dictionary
	 *
	 * @return
	 */
	public IGenstarDictionary<Attribute<? extends IValue>> getDictionary() {
		return this.getDictionaries().size() == 1 ? this.dictionaries.iterator().next() : this.getDictionary(0);
	}

	/**
	 * Set 0 based level dictionary
	 *
	 * @param dictionary
	 */
	public void setDictionary(final IGenstarDictionary<Attribute<? extends IValue>> dictionary) {
		this.dictionaries.add(dictionary);
	}

	/**
	 * Get {@code level} based dictionary
	 *
	 * @param level
	 * @return
	 */
	public IGenstarDictionary<Attribute<? extends IValue>> getDictionary(final int level) {
		return this.dictionaries.stream().filter(dico -> dico.getLevel() == level).findFirst().get();
	}

	/**
	 * Gives dictionary of attributes according to the layer in multi-level or multi-typed population
	 *
	 * @return
	 */
	@JsonProperty (GenstarConfigurationFile.DICOS)
	public Set<IGenstarDictionary<Attribute<? extends IValue>>> getDictionaries() {
		return Collections.unmodifiableSet(this.dictionaries);
	}

	@JsonProperty (GenstarConfigurationFile.DICOS)
	public void setDictionaries(final Set<IGenstarDictionary<Attribute<? extends IValue>>> dictionaries) {
		if (!dictionaries.stream().anyMatch(dico -> dico.getLevel() == 0))
			throw new IllegalArgumentException("Dictionary must include 0 based layer population");
		dictionaries.stream().forEach(this::isCircleReferencedAttribute);
		this.dictionaries = dictionaries;
	}

	/**
	 * The root directory from when to resolve relative path
	 *
	 * @return
	 */
	@JsonProperty (GenstarConfigurationFile.BASE_DIR)
	public Path getBaseDirectory() { return this.baseDirectory; }

	@JsonProperty (GenstarConfigurationFile.BASE_DIR)
	public void setBaseDirectory(final Path f) {
		DEBUG.OUT("Setting Genstar configuration basepath to " + f);
		this.baseDirectory = f;
	}

	public int getLevel() { return dictionaries.size(); }

	public List<Integer> getLayers() {
		return dictionaries.stream().map(IGenstarDictionary::getLevel).collect(Collectors.toList());
	}

	// --------------- UTILITIES --------------- //

	/*
	 * Throws an exception if attributes have feedback loop references, e.g. : A referees to B that referees to C that
	 * referees to A; in this case, no any attribute can be taken as a referent one
	 */
	private void isCircleReferencedAttribute(final IGenstarDictionary<Attribute<? extends IValue>> dictionary)
			throws IllegalArgumentException {
		Collection<Attribute<? extends IValue>> attributes = new HashSet<>();
		if (dictionary != null) { attributes.addAll(dictionary.getAttributes()); }
		// store attributes that have referent attribute
		Map<Attribute<? extends IValue>, Attribute<? extends IValue>> attToRefAtt =
				attributes.stream().filter(att -> !att.getReferentAttribute().equals(att))
						.collect(Collectors.toMap(att -> att, Attribute::getReferentAttribute));
		// store attributes that are referent and which also have a referent attribute
		Map<Attribute<? extends IValue>, Attribute<? extends IValue>> opCircle =
				attToRefAtt.keySet().stream().filter(key -> attToRefAtt.containsValue(key))
						.collect(Collectors.toMap(key -> key, key -> attToRefAtt.get(key)));
		// check if all referent attributes are also ones to refer to another attributes (circle)
		if (!opCircle.isEmpty() && opCircle.keySet().containsAll(opCircle.values())) throw new IllegalArgumentException(
				"You cannot setup circular references between attributes: " + opCircle.entrySet().stream()
						.map(e -> e.getKey().getAttributeName() + " > " + e.getValue().getAttributeName())
						.reduce((s1, s2) -> s1.concat(" >> " + s2)).get());
	}

}
