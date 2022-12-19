/*******************************************************************************************************
 *
 * GamaPopGenerator.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/

package espacedev.gaml.extensions.genstar.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.configuration.dictionary.AttributeDictionary;
import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.AttributeFactory;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.io.GSSurveyWrapper;
import espacedev.gaml.extensions.genstar.utils.GenStarConstant.GenerationAlgorithm;
import espacedev.gaml.extensions.genstar.utils.GenStarConstant.SpatialDistribution;
import espacedev.gaml.extensions.genstar.utils.GenStarGamaConstraintBuilder;
import msi.gama.common.interfaces.IValue;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gaml.operators.Strings;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import spll.entity.SpllFeature;
import spll.io.SPLVectorFile;
import spll.localizer.constraint.ISpatialConstraint;
import spll.localizer.constraint.SpatialConstraintMaxDensity;
import spll.localizer.constraint.SpatialConstraintMaxNumber;
import spll.localizer.distribution.ISpatialDistribution;
import spll.localizer.distribution.SpatialDistributionFactory;

/**
 * The Class GamaPopGenerator.
 */
@vars ({
		// -------------------------------
		// SYNTHETIC POPULATION GENERATION
		@variable (
				name = GamaPopGenerator.ATTRIBUTES_LABEL,
				type = IType.LIST,
				of = IType.STRING,
				doc = { @doc ("Returns the list of attribute names") }),
		@variable (
				name = GamaPopGenerator.GENERATION_ALGO_LABEL,
				type = IType.STRING,
				doc = { @doc ("Returns the name of the generation algorithm") }),
		@variable (
				name = GamaPopGenerator.MARGINALS_LABEL,
				type = IType.LABEL,
				doc = { @doc ("Returns the list of marginals to fit synthetic population with") }),
		@variable (
				name = GamaPopGenerator.IPF_LABEL,
				type = IType.BOOL,
				init = "false",
				doc = { @doc ("Enable the use of IPF to extrapolate a joint distribution upon marginals and seed sample") }),
		// ---------------------------------
		// SYNTHETIC POPULATION LOCALISATION
		@variable (
				name = GamaPopGenerator.NESTS_LABEL,
				type = IType.STRING,
				doc = { @doc ("Returns the spatial file used to localize entities") }),
		@variable (
				name = GamaPopGenerator.D_FEATURE_LABEL,
				type = IType.STRING,
				doc = { @doc ("The spatial feature to based spatial distribution of nest uppon") }),
		@variable (
				name = GamaPopGenerator.SPATIAL_DISTRIBUTION_LABEL,
				type = 0,
				doc = { @doc ("The type of spatial distribution used to locate entities: uniform, based on area, based on density or capacity") }), // Should
																																					// it
																																					// be
																																					// a
																																					// species
																																					// ?
		@variable (
				name = GamaPopGenerator.CONSTRAINTS_LABEL,
				type = 0,
				doc = { @doc ("The constraints that weight on probabilities that make up the spatial distribution, e.g. a constraint of distance to a given geometry") }) // Same
																																											// as
																																											// distribution
		// ----------------------------
		// SYNTHETIC NETWORK GENERATION

})
public class GamaPopGenerator implements IValue {

	/** The generated population. */
	IPopulation<? extends ADemoEntity, ?> generatedPopulation;

	/** The map entities agent. */
	Map<ADemoEntity, IAgent> mapEntitiesAgent;

	//////////////////////////////////////////////
	// Attirbute for the Gospl generation
	//////////////////////////////////////////////

	/** The Constant GENERATION_ALGO. */
	public static final String GENERATION_ALGO_LABEL = "GOSP_algorithm";

	/** The generation algorithm. */
	private String generationAlgorithm;

	/** The Constant DEMOGRAPHIC_FILES. */
	public static final String DEMOGRAPHIC_FILES_LABEL = "demographic_files";

	/** The input files. */
	private List<GSSurveyWrapper> inputFiles;

	/** The Constant ATTRIBUTES_DICTIONARY. */
	public static final String ATTRIBUTES_DICTIONARY_LABEL = "individual_dictionary";

	/** The Constant HOUSEHOLD_DICTIONARY. */
	public static final String HOUSEHOLD_DICTIONARY_LABEL = "household_dictionary";

	/** The Constant ATTRIBUTES. */
	public static final String ATTRIBUTES_LABEL = "demographic_attributes";

	/** The Constant MARGINALS. */
	public static final String MARGINALS_LABEL = "demogrphic_marginals";

	/** The household attributes. */
	private AttributeDictionary householdAttributes;

	/** The individual attributes. */
	private AttributeDictionary individualAttributes;

	/** The marginals. */
	private List<Attribute<? extends core.metamodel.value.IValue>> marginals;

	/** The Constant IPF. */
	public static final String IPF_LABEL = "ipf";

	/** The ipf. */
	private boolean ipf;

	// /** The max iteration. */
	// // CO related variables
	// public static final int MaxIteration = 1000;
	//
	// /** The neighborhood extends. */
	// public static final double neighborhoodExtends = 0.05d;
	//
	// /** The fitness threshold. */
	// public static final double fitnessThreshold = 0.05d;

	//////////////////////////////////////////////
	// Attirbute for the Spll localization
	/** The spatialize population. */
	//////////////////////////////////////////////
	boolean spatializePopulation;

	/** The min distance localize. */
	Double minDistanceLocalize;

	/** The max distance localize. */
	Double maxDistanceLocalize;

	/** The localize overlaps. */
	boolean localizeOverlaps;

	/** The Constant MATCH. */
	public static final String MATCH_LABEL = "matcher_file";

	/** The path census geometries. */
	String pathCensusGeometries;

	/** The Constant NESTS. */
	public static final String NESTS_LABEL = "Nests_geometries";

	/** The path nest geometries. */
	String pathNestGeometries;

	/** The Constant AGENT_NESTS. */
	public static final String AGENT_NESTS_LABEL = "Nests_agents";

	/** The list of nest agents. */
	IContainer<?, ? extends IAgent> listOfNestAgents;

	/** The Constant SPATIALDISTRIBUTION. */
	// Spatial distribution
	public static final String SPATIAL_DISTRIBUTION_LABEL = "spatial_distribution";

	/** The spatial distribution. */
	private SpatialDistribution spatialDistribution;

	/** The Constant D_FEATURE. */
	public static final String D_FEATURE_LABEL = "distribution_feature";

	/** The distribution feature. */
	private String distributionFeature = "";

	/** The Constant CONSTRAINTS. */
	public static final String CONSTRAINTS_LABEL = "spatial_constraints";

	/** The c builder. */
	private final GenStarGamaConstraintBuilder cBuilder;

	/** The crs. */
	String crs;

	/** The priority counter. */
	private int priorityCounter;

	/** The path ancilary geofiles. */
	List<String> pathAncilaryGeofiles;

	/**
	 * Default constructor
	 */
	public GamaPopGenerator() {
		generationAlgorithm = GenerationAlgorithm.DIRECTSAMPLING.getAlias().get(0);
		inputFiles = new ArrayList<>();
		individualAttributes = new AttributeDictionary();

		minDistanceLocalize = 0.0;
		maxDistanceLocalize = 0.0;
		localizeOverlaps = false;
		pathAncilaryGeofiles = new ArrayList<>();

		mapEntitiesAgent = new HashMap<>();

		cBuilder = new GenStarGamaConstraintBuilder();
	}

	// IValue constract

	@Override
	public String serialize(final boolean includingBuiltIn) {
		return null;
	}

	@Override
	public String stringValue(final IScope scope) {
		return null;
	}

	@Override
	public IValue copy(final IScope scope) {
		return null;
	}

	// ACCESSOR FOR SP GENERATION

	/**
	 * Gets the attf.
	 *
	 * @return the attf
	 */
	public AttributeFactory getAttf() { return AttributeFactory.getFactory(); }

	/**
	 * Sets the input files.
	 *
	 * @param inputFiles
	 *            the new input files
	 */
	@setter (DEMOGRAPHIC_FILES_LABEL)
	public void setInputFiles(final List<GSSurveyWrapper> inputFiles) { this.inputFiles = inputFiles; }

	/**
	 * Gets the household attributes.
	 *
	 * @return the household attributes
	 */
	@getter (HOUSEHOLD_DICTIONARY_LABEL)
	public AttributeDictionary getHouseholdAttributes() { return householdAttributes; }

	/**
	 * Sets the household attributes.
	 *
	 * @param householdAttributes
	 *            the new household attributes
	 */
	@setter (HOUSEHOLD_DICTIONARY_LABEL)
	public void setHouseholdAttributes(final AttributeDictionary householdAttributes) {
		this.householdAttributes = householdAttributes;
	}

	/**
	 * Gets the input attributes.
	 *
	 * @return the input attributes
	 */
	@getter (ATTRIBUTES_DICTIONARY_LABEL)
	public AttributeDictionary getInputAttributes() { return individualAttributes; }

	/**
	 * Sets the input attributes.
	 *
	 * @param inputAttributes
	 *            the new input attributes
	 */
	@setter (ATTRIBUTES_DICTIONARY_LABEL)
	public void setInputAttributes(final AttributeDictionary inputAttributes) {
		this.individualAttributes = inputAttributes;
	}

	/**
	 * Gets the attribute name.
	 *
	 * @return the attribute name
	 */
	@getter (ATTRIBUTES_LABEL)
	public IList<String> getAttributeName() {
		IList<String> atts = GamaListFactory.create(Types.STRING);
		for (Attribute<? extends core.metamodel.value.IValue> a : this.getInputAttributes().getAttributes()) {
			atts.add(a.getAttributeName());
		}
		return atts;
	}

	/**
	 * Gets the marginals name.
	 *
	 * @return the marginals name
	 */
	@getter (MARGINALS_LABEL)
	public IList<String> getMarginalsName() {
		IList<String> atts = GamaListFactory.create(Types.STRING);
		for (Attribute<? extends core.metamodel.value.IValue> a : this.getMarginals()) {
			atts.add(a.getAttributeName());
		}
		return atts;
	}

	/**
	 * Set marginals to fit population with
	 *
	 * @param marginals
	 */
	public void setMarginals(final List<Attribute<? extends core.metamodel.value.IValue>> marginals) {
		if (marginals == null || marginals.isEmpty()) {
			this.marginals = new ArrayList<>(this.getInputAttributes().getAttributes());
		} else {
			this.marginals = marginals;
		}
	}

	/**
	 * Retrieve the marginals for the current generator: i.e. the attribute that define the goal total distribution
	 *
	 * @return
	 */
	public List<Attribute<? extends core.metamodel.value.IValue>> getMarginals() {
		return Collections.unmodifiableList(this.marginals);
	}

	/**
	 * Gets the input files.
	 *
	 * @return the input files
	 */
	@getter (DEMOGRAPHIC_FILES_LABEL)
	public List<GSSurveyWrapper> getInputFiles() { return inputFiles; }

	/**
	 * Gets the generation algorithm.
	 *
	 * @return the generation algorithm
	 */
	@getter (GENERATION_ALGO_LABEL)
	public String getGenerationAlgorithm() { return generationAlgorithm; }

	/**
	 * Get the constant enumerate algorithm for Gen*
	 *
	 * @return
	 */
	public GenerationAlgorithm getGenstarGenerationAlgorithm() {
		return GenerationAlgorithm.getAlgorithm(this.generationAlgorithm);
	}

	/**
	 * Gets the ipf.
	 *
	 * @return the ipf
	 */
	@getter (IPF_LABEL)
	public boolean getIPF() { return this.ipf; }

	/**
	 * Sets the ipf.
	 *
	 * @param ipf
	 *            the new ipf
	 */
	@setter (IPF_LABEL)
	public void setIPF(final boolean ipf) { this.ipf = ipf; }

	/**
	 * Sets the generation algorithm.
	 *
	 * @param generationAlgorithm
	 *            the new generation algorithm
	 */
	public void setGenerationAlgorithm(final String generationAlgorithm) {
		this.generationAlgorithm = generationAlgorithm;
	}

	// ACCESSOR FOR SP LOCALIZATION

	/**
	 * is this generator does also localize population
	 *
	 * @return
	 */
	public boolean isSpatializePopulation() { return spatializePopulation; }

	/**
	 * Define this generator to also localize population
	 *
	 * @return
	 */
	public void setSpatializePopulation(final boolean spatializePopulation) {
		this.spatializePopulation = spatializePopulation;
	}

	/**
	 * The main CRS of the localization process
	 *
	 * @return
	 */
	public String getCrs() { return crs; }

	/**
	 * Set the CRS of the localization process
	 *
	 * @param crs
	 */
	public void setCrs(final String crs) { this.crs = crs; }

	/**
	 * When priority of constraint are not specified there are prioriterized according to definition order
	 *
	 * @return
	 */
	public int uptadePriorityCounter() {
		int tmp = priorityCounter;
		priorityCounter++;
		return tmp;
	}

	/**
	 * Gets the path nest geometries.
	 *
	 * @return the path nest geometries
	 */
	@getter (NESTS_LABEL)
	public String getPathNestGeometries() { return this.pathNestGeometries; }

	/**
	 * Sets the path nest geometries.
	 *
	 * @param path
	 *            the new path nest geometries
	 */
	@setter (NESTS_LABEL)
	public void setPathNestGeometries(final String path) { this.pathNestGeometries = path; }

	/**
	 * Sets the nest agents geometries.
	 *
	 * @param listOfAgents
	 *            the list of agents
	 */
	@getter (AGENT_NESTS_LABEL)
	public void setNestAgentsGeometries(final IContainer<?, ? extends IAgent> listOfAgents) {
		this.listOfNestAgents = listOfAgents;
	}

	/**
	 * Gets the nest agents geometries.
	 *
	 * @return the nest agents geometries
	 */
	@setter (AGENT_NESTS_LABEL)
	public IContainer<?, ? extends IAgent> getNestAgentsGeometries() { return this.listOfNestAgents; }

	/**
	 * Sets the path census geometries.
	 *
	 * @param stringPathToCensusShapefile
	 *            the new path census geometries
	 */
	@setter (MATCH_LABEL)
	public void setPathCensusGeometries(final String stringPathToCensusShapefile) {
		this.pathCensusGeometries = stringPathToCensusShapefile;

		setSpatializePopulation(pathCensusGeometries != null);
	}

	/**
	 * Gets the path census geometries.
	 *
	 * @return the path census geometries
	 */
	@getter (MATCH_LABEL)
	public String getPathCensusGeometries() { return pathCensusGeometries; }

	// get rid of next methods before spatial distribution

	/**
	 * Sets the localized around.
	 *
	 * @param min
	 *            the min
	 * @param max
	 *            the max
	 * @param overlaps
	 *            the overlaps
	 */
	public void setLocalizedAround(final Double min, final Double max, final boolean overlaps) {
		setMinDistanceLocalize(min);
		setMaxDistanceLocalize(max);
		setLocalizeOverlaps(overlaps);
	}

	/**
	 * Gets the min distance localize.
	 *
	 * @return the min distance localize
	 */
	public Double getMinDistanceLocalize() { return minDistanceLocalize; }

	/**
	 * Sets the min distance localize.
	 *
	 * @param minDistanceLocalize
	 *            the new min distance localize
	 */
	public void setMinDistanceLocalize(final Double minDistanceLocalize) {
		this.minDistanceLocalize = minDistanceLocalize;
	}

	/**
	 * Gets the max distance localize.
	 *
	 * @return the max distance localize
	 */
	public Double getMaxDistanceLocalize() { return maxDistanceLocalize; }

	/**
	 * Sets the max distance localize.
	 *
	 * @param maxDistanceLocalize
	 *            the new max distance localize
	 */
	public void setMaxDistanceLocalize(final Double maxDistanceLocalize) {
		this.maxDistanceLocalize = maxDistanceLocalize;
	}

	/**
	 * Checks if is localize overlaps.
	 *
	 * @return true, if is localize overlaps
	 */
	public boolean isLocalizeOverlaps() { return localizeOverlaps; }

	/**
	 * Sets the localize overlaps.
	 *
	 * @param localizeOverlaps
	 *            the new localize overlaps
	 */
	public void setLocalizeOverlaps(final boolean localizeOverlaps) { this.localizeOverlaps = localizeOverlaps; }

	// --------------------
	// Spatial distribution
	// --------------------

	/**
	 * Gets the spatial distribution.
	 *
	 * @return the spatial distribution
	 */
	@getter (SPATIAL_DISTRIBUTION_LABEL)
	public SpatialDistribution getSpatialDistribution() { return spatialDistribution; }

	/**
	 * Sets the spatial distribution.
	 *
	 * @param spatialDistribution
	 *            the new spatial distribution
	 */
	@setter (SPATIAL_DISTRIBUTION_LABEL)
	public void setSpatialDistribution(final SpatialDistribution spatialDistribution) {
		this.spatialDistribution = spatialDistribution;
	}

	/**
	 * Gets the spatial distribution feature.
	 *
	 * @return the spatial distribution feature
	 */
	@getter (D_FEATURE_LABEL)
	public String getSpatialDistributionFeature() { return distributionFeature; }

	/**
	 * Sets the spatial distribution feature.
	 *
	 * @param feature
	 *            the new spatial distribution feature
	 */
	@setter (D_FEATURE_LABEL)
	public void setSpatialDistributionFeature(final String feature) { this.distributionFeature = feature; }

	/**
	 * Gets the spatial distribution.
	 *
	 * @param sfGeometries
	 *            the sf geometries
	 * @param scope
	 *            the scope
	 * @return the spatial distribution
	 */
	@SuppressWarnings ("rawtypes")
	public ISpatialDistribution getSpatialDistribution(final SPLVectorFile sfGeometries, final IScope scope) {
		if (getSpatialDistribution() == null) { setSpatialDistribution(SpatialDistribution.DEFAULT); }
		switch (getSpatialDistribution().getConcept()) {
			case NUMBER:
				SpatialConstraintMaxNumber scmn = null;
				if (distributionFeature == null || Strings.isEmpty(distributionFeature)) throw GamaRuntimeException
						.error("You must specified a spatial feature (attribute) to based distribution upon", scope);
				List<SpllFeature> sf =
						sfGeometries.getGeoEntity().stream()
								.filter(f -> f.getAttributes().stream()
										.noneMatch(af -> af.getAttributeName().equalsIgnoreCase(distributionFeature)))
								.toList();
				if (!sf.isEmpty()) throw GamaRuntimeException.error("The specified capacity constraint feature "
						+ distributionFeature + " is not present in " + Arrays.asList(sf).toString(), scope);
				switch (getSpatialDistribution()) {
					case CAPACITY:
						scmn = new SpatialConstraintMaxNumber(sfGeometries.getGeoEntity(), distributionFeature);
						break;
					case DENSITY:
						scmn = new SpatialConstraintMaxDensity(sfGeometries.getGeoEntity(), distributionFeature);
						break;
					default:
						break;
				}
				return SpatialDistributionFactory.getInstance().getCapacityBasedDistribution(scmn);
			case COMPLEX:
				throw GamaRuntimeException.error(new UnsupportedOperationException(
						"Complex spatial distribution " + "have not been yet passed in the plugin").getMessage(),
						scope);
			case SIMPLE:
			default:
				return switch (getSpatialDistribution()) {
					case AREA -> SpatialDistributionFactory.getInstance().getAreaBasedDistribution(sfGeometries);
					default -> SpatialDistributionFactory.getInstance().getUniformDistribution();
				};
		}
	}

	// ------------------
	// Spatial constraint
	// ------------------

	/**
	 * Gets the constraint builder.
	 *
	 * @return the constraint builder
	 */
	@getter (CONSTRAINTS_LABEL)
	public GenStarGamaConstraintBuilder getConstraintBuilder() { return this.cBuilder; }

	/**
	 * Gets the constraints.
	 *
	 * @param sfGeometries
	 *            the sf geometries
	 * @param scope
	 *            the scope
	 * @return the constraints
	 * @throws IllegalStateException
	 *             the illegal state exception
	 */
	public Collection<ISpatialConstraint> getConstraints(final SPLVectorFile sfGeometries, final IScope scope)
			throws IllegalStateException {
		return this.cBuilder.buildConstraints(sfGeometries.getGeoEntity());
	}

	// -------------------------
	// Mapper / ancilary methods
	// -------------------------

	/**
	 * Gets the path ancilary geofiles.
	 *
	 * @return the path ancilary geofiles
	 */
	public List<String> getPathAncilaryGeofiles() { return pathAncilaryGeofiles; }

	/**
	 * Adds the ancilary geo files.
	 *
	 * @param pathToFile
	 *            the path to file
	 */
	public void addAncilaryGeoFiles(final String pathToFile) {
		pathAncilaryGeofiles.add(pathToFile);
	}

	/**
	 * Sets the path ancilary geofiles.
	 *
	 * @param pathAncilaryGeofiles
	 *            the new path ancilary geofiles
	 */
	public void setPathAncilaryGeofiles(final List<String> pathAncilaryGeofiles) {
		this.pathAncilaryGeofiles = pathAncilaryGeofiles;
	}

	// GLOBALS

	/**
	 * Sets the generated population.
	 *
	 * @param population
	 *            the population
	 */
	public void setGeneratedPopulation(final IPopulation<? extends ADemoEntity, ?> population) {
		this.generatedPopulation = population;
	}

	/**
	 * Gets the generated population.
	 *
	 * @return the generated population
	 */
	public IPopulation<? extends ADemoEntity, ?> getGeneratedPopulation() { return generatedPopulation; }

	/**
	 * Adds the agent.
	 *
	 * @param e
	 *            the e
	 * @param a
	 *            the a
	 */
	public void addAgent(final ADemoEntity e, final IAgent a) {
		mapEntitiesAgent.put(e, a);
	}

	/**
	 * Gets the agent.
	 *
	 * @param e
	 *            the e
	 * @return the agent
	 */
	public IAgent getAgent(final ADemoEntity e) {
		return mapEntitiesAgent.get(e);
	}

	/**
	 * Gets the agents.
	 *
	 * @return the agents
	 */
	public Collection<IAgent> getAgents() { return mapEntitiesAgent.values(); }

}
