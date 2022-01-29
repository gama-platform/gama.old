/*******************************************************************************************************
 *
 * GamaPopGenerator.java, in espacedev.gaml.extensions.genstar, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
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
import java.util.stream.Collectors;

import core.configuration.dictionary.AttributeDictionary;
import core.metamodel.IPopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.attribute.AttributeFactory;
import core.metamodel.entity.ADemoEntity;
import core.metamodel.io.GSSurveyWrapper;
import espacedev.gaml.extensions.genstar.utils.GenStarConstant.SpatialDistribution;
import espacedev.gaml.extensions.genstar.utils.GenStarConstant.GenerationAlgorithm;
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
@vars({
	// -------------------------------
	// SYNTHETIC POPULATION GENERATION
	@variable(name = GamaPopGenerator.ATTRIBUTES, 
		type = IType.LIST, of = IType.STRING, 
		doc = {@doc("Returns the list of attribute names") }), 
	@variable(name = GamaPopGenerator.GENERATION_ALGO, 
		type = IType.STRING, 
		doc = {@doc("Returns the name of the generation algorithm") }),
	@variable(name = GamaPopGenerator.MARGINALS,
		type = IType.LABEL,
		doc = {@doc("Returns the list of marginals to fit synthetic population with")}),
	@variable(name = GamaPopGenerator.IPF, 
		type = IType.BOOL, init = "false",
		doc = {@doc("Enable the use of IPF to extrapolate a joint distribution upon marginals and seed sample")}),
	// ---------------------------------
	// SYNTHETIC POPULATION LOCALISATION
	@variable(name = GamaPopGenerator.NESTS, 
		type = IType.STRING, 
		doc = {@doc("Returns the spatial file used to localize entities") }),
	@variable(name = GamaPopGenerator.D_FEATURE, 
		type=IType.STRING, 
		doc = {@doc("The spatial feature to based spatial distribution of nest uppon")}),
	@variable(name = GamaPopGenerator.SPATIALDISTRIBUTION, 
		type = 0,
		doc = {@doc("The type of spatial distribution used to locate entities: uniform, based on area, based on density or capacity")}), // Should it be a species ?
	@variable(name = GamaPopGenerator.CONSTRAINTS, 
		type = 0,
		doc = {@doc("The constraints that weight on probabilities that make up the spatial distribution, e.g. a constraint of distance to a given geometry")}) // Same as distribution
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
	public final static String GENERATION_ALGO = "GOSP_algorithm";
	
	/** The generation algorithm. */
	private String generationAlgorithm;
	
	/** The Constant DEMOGRAPHIC_FILES. */
	public final static String DEMOGRAPHIC_FILES = "demographic_files";
	
	/** The input files. */
	private List<GSSurveyWrapper> inputFiles;
	
	/** The Constant ATTRIBUTES_DICTIONARY. */
	public final static String ATTRIBUTES_DICTIONARY = "individual_dictionary";
	
	/** The Constant HOUSEHOLD_DICTIONARY. */
	public final static String HOUSEHOLD_DICTIONARY = "household_dictionary";
	
	/** The Constant ATTRIBUTES. */
	public final static String ATTRIBUTES = "demographic_attributes";
	
	/** The Constant MARGINALS. */
	public final static String MARGINALS = "demogrphic_marginals";
	
	/** The household attributes. */
	private AttributeDictionary householdAttributes;
	
	/** The individual attributes. */
	private AttributeDictionary individualAttributes;
	
	/** The marginals. */
	private List<Attribute<? extends core.metamodel.value.IValue>> marginals;
	
	/** The Constant IPF. */
	public final static String IPF = "ipf";
	
	/** The ipf. */
	public boolean ipf;
	
	/** The max iteration. */
	// CO related variables
	public int max_iteration = 1000;
	
	/** The neighborhood extends. */
	public double neighborhood_extends = 0.05d;
	
	/** The fitness threshold. */
	public double fitness_threshold = 0.05d;
	
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
	public final static String MATCH = "matcher_file";
	
	/** The path census geometries. */
	String pathCensusGeometries;
	
	/** The Constant NESTS. */
	public final static String NESTS = "Nests_geometries";
	
	/** The path nest geometries. */
	String pathNestGeometries;
	
	/** The Constant AGENT_NESTS. */
	public final static String AGENT_NESTS = "Nests_agents";
	
	/** The list of nest agents. */
	IContainer<?, ? extends IAgent> listOfNestAgents;
	
	/** The Constant SPATIALDISTRIBUTION. */
	// Spatial distribution
	public final static String SPATIALDISTRIBUTION = "spatial_distribution";
	
	/** The spatial distribution. */
	private SpatialDistribution spatialDistribution;
	
	/** The Constant D_FEATURE. */
	public final static String D_FEATURE = "distribution_feature";
	
	/** The distribution feature. */
	private String distributionFeature = "";

	/** The Constant CONSTRAINTS. */
	public final static String CONSTRAINTS = "spatial_constraints";

	/** The c builder. */
	private GenStarGamaConstraintBuilder cBuilder;
	
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
	public String serialize(boolean includingBuiltIn) {
		return null;
	}

	@Override
	public String stringValue(IScope scope) {
		return null;
	}

	@Override
	public IValue copy(IScope scope) {
		return null;
	}

	// ACCESSOR FOR SP GENERATION
	
	/**
	 * Gets the attf.
	 *
	 * @return the attf
	 */
	public AttributeFactory getAttf() {
		return AttributeFactory.getFactory();
	}

	/**
	 * Sets the input files.
	 *
	 * @param inputFiles the new input files
	 */
	@setter(DEMOGRAPHIC_FILES)
	public void setInputFiles(List<GSSurveyWrapper> inputFiles) {
		this.inputFiles = inputFiles;
	}
	
	/**
	 * Gets the household attributes.
	 *
	 * @return the household attributes
	 */
	@getter(HOUSEHOLD_DICTIONARY)
	public AttributeDictionary getHouseholdAttributes() {
		return householdAttributes;
	}
	
	/**
	 * Sets the household attributes.
	 *
	 * @param householdAttributes the new household attributes
	 */
	@setter(HOUSEHOLD_DICTIONARY)
	public void setHouseholdAttributes(AttributeDictionary householdAttributes) {
		this.householdAttributes = householdAttributes;
	}

	/**
	 * Gets the input attributes.
	 *
	 * @return the input attributes
	 */
	@getter(ATTRIBUTES_DICTIONARY)
	public AttributeDictionary getInputAttributes() {
		return individualAttributes;
	}

	/**
	 * Sets the input attributes.
	 *
	 * @param inputAttributes the new input attributes
	 */
	@setter(ATTRIBUTES_DICTIONARY)
	public void setInputAttributes(AttributeDictionary inputAttributes) {
		this.individualAttributes = inputAttributes;
	}
	
	/**
	 * Gets the attribute name.
	 *
	 * @return the attribute name
	 */
	@getter(ATTRIBUTES)
	public IList<String> getAttributeName(){
		IList<String> atts = GamaListFactory.create(Types.STRING);
		for (Attribute<? extends core.metamodel.value.IValue> a : this.getInputAttributes().getAttributes())
			atts.add(a.getAttributeName());
		return atts;
	}
	
	/**
	 * Gets the marginals name.
	 *
	 * @return the marginals name
	 */
	@getter(MARGINALS)
	public IList<String> getMarginalsName(){
		IList<String> atts = GamaListFactory.create(Types.STRING);
		for (Attribute<? extends core.metamodel.value.IValue> a : this.getMarginals())
			atts.add(a.getAttributeName());
		return atts;
	}
	
	/**
	 * Set marginals to fit population with
	 * @param marginals
	 */
	public void setMarginals(List<Attribute<? extends core.metamodel.value.IValue>> marginals) {
		if(marginals==null || marginals.isEmpty()) {
			this.marginals = new ArrayList<>(this.getInputAttributes().getAttributes());
		} else {
			this.marginals = marginals;
		}
	}
	
	/**
	 * Retrieve the marginals for the current generator: i.e. the attribute that define the goal total distribution
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
	@getter(DEMOGRAPHIC_FILES)
	public List<GSSurveyWrapper> getInputFiles() {
		return inputFiles;
	}
	
	/**
	 * Gets the generation algorithm.
	 *
	 * @return the generation algorithm
	 */
	@getter(GENERATION_ALGO)
	public String getGenerationAlgorithm() { return generationAlgorithm; }
	
	/**
	 * Get the constant enumerate algorithm for Gen*
	 * @return
	 */
	public GenerationAlgorithm getGenstarGenerationAlgorithm() {return GenerationAlgorithm.getAlgorithm(this.generationAlgorithm); }
	
	/**
	 * Gets the ipf.
	 *
	 * @return the ipf
	 */
	@getter(IPF)
	public boolean getIPF() { return this.ipf; }
	
	/**
	 * Sets the ipf.
	 *
	 * @param ipf the new ipf
	 */
	@setter(IPF)
	public void setIPF(boolean ipf) { this.ipf = ipf; }

	/**
	 * Sets the generation algorithm.
	 *
	 * @param generationAlgorithm the new generation algorithm
	 */
	public void setGenerationAlgorithm(String generationAlgorithm) {
		this.generationAlgorithm = generationAlgorithm;
	}

	// ACCESSOR FOR SP LOCALIZATION

	/**
	 * is this generator does also localize population
	 * @return
	 */
	public boolean isSpatializePopulation() {
		return spatializePopulation;
	}

	/**
	 * Define this generator to also localize population
	 * @return
	 */
	public void setSpatializePopulation(boolean spatializePopulation) {
		this.spatializePopulation = spatializePopulation;
	}

	/**
	 * The main CRS of the localization process
	 * @return
	 */
	public String getCrs() {
		return crs;
	}

	/**
	 * Set the CRS of the localization process
	 * @param crs
	 */
	public void setCrs(String crs) {
		this.crs = crs;
	}
	
	/**
	 * When priority of constraint are not specified there are prioriterized according to definition order
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
	@getter(NESTS)
	public String getPathNestGeometries() {
		return this.pathNestGeometries;
	}
	
	/**
	 * Sets the path nest geometries.
	 *
	 * @param path the new path nest geometries
	 */
	@setter(NESTS)
	public void setPathNestGeometries(String path) {
		this.pathNestGeometries = path;
	}
	

	/**
	 * Sets the nest agents geometries.
	 *
	 * @param listOfAgents the list of agents
	 */
	@getter(AGENT_NESTS)
	public void setNestAgentsGeometries(IContainer<?, ? extends IAgent> listOfAgents) {
		this.listOfNestAgents = listOfAgents;
	}
	
	/**
	 * Gets the nest agents geometries.
	 *
	 * @return the nest agents geometries
	 */
	@setter(AGENT_NESTS)
	public IContainer<?, ? extends IAgent> getNestAgentsGeometries() {
		return this.listOfNestAgents;
	}

	/**
	 * Sets the path census geometries.
	 *
	 * @param stringPathToCensusShapefile the new path census geometries
	 */
	@setter(MATCH)
	public void setPathCensusGeometries(String stringPathToCensusShapefile) {
		this.pathCensusGeometries = stringPathToCensusShapefile;
		
		setSpatializePopulation(pathCensusGeometries != null);
	}

	/**
	 * Gets the path census geometries.
	 *
	 * @return the path census geometries
	 */
	@getter(MATCH)
	public String getPathCensusGeometries() {
		return pathCensusGeometries;
	}

	// TODO : get ride of next methods before spatial distribution

	/**
	 * Sets the localized around.
	 *
	 * @param min the min
	 * @param max the max
	 * @param overlaps the overlaps
	 */
	public void setLocalizedAround(Double min, Double max, boolean overlaps) {
		setMinDistanceLocalize(min);
		setMaxDistanceLocalize(max);
		setLocalizeOverlaps(overlaps);
	}


	/**
	 * Gets the min distance localize.
	 *
	 * @return the min distance localize
	 */
	public Double getMinDistanceLocalize() {
		return minDistanceLocalize;
	}


	/**
	 * Sets the min distance localize.
	 *
	 * @param minDistanceLocalize the new min distance localize
	 */
	public void setMinDistanceLocalize(Double minDistanceLocalize) {
		this.minDistanceLocalize = minDistanceLocalize;
	}


	/**
	 * Gets the max distance localize.
	 *
	 * @return the max distance localize
	 */
	public Double getMaxDistanceLocalize() {
		return maxDistanceLocalize;
	}


	/**
	 * Sets the max distance localize.
	 *
	 * @param maxDistanceLocalize the new max distance localize
	 */
	public void setMaxDistanceLocalize(Double maxDistanceLocalize) {
		this.maxDistanceLocalize = maxDistanceLocalize;
	}


	/**
	 * Checks if is localize overlaps.
	 *
	 * @return true, if is localize overlaps
	 */
	public boolean isLocalizeOverlaps() {
		return localizeOverlaps;
	}


	/**
	 * Sets the localize overlaps.
	 *
	 * @param localizeOverlaps the new localize overlaps
	 */
	public void setLocalizeOverlaps(boolean localizeOverlaps) {
		this.localizeOverlaps = localizeOverlaps;
	}

	// --------------------
	// Spatial distribution
	// --------------------
	
	/**
	 * Gets the spatial distribution.
	 *
	 * @return the spatial distribution
	 */
	@getter(SPATIALDISTRIBUTION)
	public SpatialDistribution getSpatialDistribution() { return spatialDistribution; }
	
	/**
	 * Sets the spatial distribution.
	 *
	 * @param spatialDistribution the new spatial distribution
	 */
	@setter(SPATIALDISTRIBUTION)
	public void setSpatialDistribution(SpatialDistribution spatialDistribution) { this.spatialDistribution = spatialDistribution; }

	/**
	 * Gets the spatial distribution feature.
	 *
	 * @return the spatial distribution feature
	 */
	@getter(D_FEATURE)
	public String getSpatialDistributionFeature() {return distributionFeature;}	
	
	/**
	 * Sets the spatial distribution feature.
	 *
	 * @param feature the new spatial distribution feature
	 */
	@setter(D_FEATURE)
	public void setSpatialDistributionFeature(String feature) { this.distributionFeature = feature; }
	
	/**
	 * Gets the spatial distribution.
	 *
	 * @param sfGeometries the sf geometries
	 * @param scope the scope
	 * @return the spatial distribution
	 */
	@SuppressWarnings("rawtypes")
	public ISpatialDistribution getSpatialDistribution(SPLVectorFile sfGeometries, IScope scope) {		
		if(getSpatialDistribution() == null) {setSpatialDistribution(SpatialDistribution.DEFAULT);}
		switch(getSpatialDistribution().getConcept()) {
			case NUMBER :  
				SpatialConstraintMaxNumber scmn = null;
				if (distributionFeature != null && !Strings.isEmpty(distributionFeature)) {
					List<SpllFeature> sf = sfGeometries.getGeoEntity().stream().filter(f -> f.getAttributes()
							.stream().noneMatch(af -> af.getAttributeName().equalsIgnoreCase(distributionFeature)))
							.collect(Collectors.toList());
					if(!sf.isEmpty()) {
						throw GamaRuntimeException.error("The specified capacity constraint feature "
							+distributionFeature+" is not present in "+Arrays.asList(sf).toString(), scope);
					}
				} else {
					throw GamaRuntimeException.error("You must specified a spatial feature (attribute) to based distribution upon", scope);
				}
				switch(getSpatialDistribution()) {
					case CAPACITY :
						scmn = new SpatialConstraintMaxNumber(sfGeometries.getGeoEntity(), distributionFeature);
						break;
					case DENSITY :
						scmn = new SpatialConstraintMaxDensity(sfGeometries.getGeoEntity(), distributionFeature);
						break;
					default: break;
				}
				return SpatialDistributionFactory.getInstance().getCapacityBasedDistribution(scmn);
			case COMPLEX :
				throw GamaRuntimeException.error(new UnsupportedOperationException("Complex spatial distribution "
						+ "have not been yet passed in the plugin").getMessage(), scope);
			case SIMPLE : 
			default :
				switch(getSpatialDistribution()) {
					case AREA : 
						return SpatialDistributionFactory.getInstance().getAreaBasedDistribution(sfGeometries);
					default :
						return SpatialDistributionFactory.getInstance().getUniformDistribution();
				}
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
	@getter(CONSTRAINTS)
	public GenStarGamaConstraintBuilder getConstraintBuilder() {
		return this.cBuilder;
	}
	
	/**
	 * Gets the constraints.
	 *
	 * @param sfGeometries the sf geometries
	 * @param scope the scope
	 * @return the constraints
	 * @throws IllegalStateException the illegal state exception
	 */
	public Collection<ISpatialConstraint> getConstraints(SPLVectorFile sfGeometries, IScope scope) throws IllegalStateException {
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
	public List<String> getPathAncilaryGeofiles() {
		return pathAncilaryGeofiles;
	}

	/**
	 * Adds the ancilary geo files.
	 *
	 * @param pathToFile the path to file
	 */
	public void addAncilaryGeoFiles(String pathToFile) {
		pathAncilaryGeofiles.add(pathToFile);
	}

	/**
	 * Sets the path ancilary geofiles.
	 *
	 * @param pathAncilaryGeofiles the new path ancilary geofiles
	 */
	public void setPathAncilaryGeofiles(List<String> pathAncilaryGeofiles) {
		this.pathAncilaryGeofiles = pathAncilaryGeofiles;
	}
	
	// GLOBALS

	/**
	 * Sets the generated population.
	 *
	 * @param population the population
	 */
	public void setGeneratedPopulation(IPopulation<? extends ADemoEntity, ?> population) {
		this.generatedPopulation = population;
	}
	
	/**
	 * Gets the generated population.
	 *
	 * @return the generated population
	 */
	public IPopulation<? extends ADemoEntity, ?> getGeneratedPopulation() {
		return generatedPopulation;
	}
	
	/**
	 * Adds the agent.
	 *
	 * @param e the e
	 * @param a the a
	 */
	public void addAgent(ADemoEntity e, IAgent a) {
		mapEntitiesAgent.put(e, a);	
	}

	/**
	 * Gets the agent.
	 *
	 * @param e the e
	 * @return the agent
	 */
	public IAgent getAgent(ADemoEntity e) {
		return mapEntitiesAgent.get(e);
	}
	
	/**
	 * Gets the agents.
	 *
	 * @return the agents
	 */
	public Collection<IAgent> getAgents() {
		return mapEntitiesAgent.values();
	}


}
