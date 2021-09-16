/*********************************************************************************************
 *
 * 'GamaRegression.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/

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

	IPopulation<? extends ADemoEntity, ?> generatedPopulation;
	Map<ADemoEntity, IAgent> mapEntitiesAgent;
	
	//////////////////////////////////////////////
	// Attirbute for the Gospl generation
	//////////////////////////////////////////////
	
	public final static String GENERATION_ALGO = "GOSP_algorithm";
	private String generationAlgorithm;
	
	public final static String DEMOGRAPHIC_FILES = "demographic_files";
	private List<GSSurveyWrapper> inputFiles;
	
	public final static String ATTRIBUTES_DICTIONARY = "individual_dictionary";
	public final static String HOUSEHOLD_DICTIONARY = "household_dictionary";
	public final static String ATTRIBUTES = "demographic_attributes";
	public final static String MARGINALS = "demogrphic_marginals";
	
	private AttributeDictionary householdAttributes;
	private AttributeDictionary individualAttributes;
	private List<Attribute<? extends core.metamodel.value.IValue>> marginals;
	
	public final static String IPF = "ipf";
	public boolean ipf;
	
	// CO related variables
	public int max_iteration = 1000;
	public double neighborhood_extends = 0.05d;
	public double fitness_threshold = 0.05d;
	
	//////////////////////////////////////////////
	// Attirbute for the Spll localization
	//////////////////////////////////////////////
	boolean spatializePopulation;	
	
	Double minDistanceLocalize;
	Double maxDistanceLocalize;
	boolean localizeOverlaps;
	
	public final static String MATCH = "matcher_file";
	String pathCensusGeometries;
	
	public final static String NESTS = "Nests_geometries";
	String pathNestGeometries;
	
	public final static String AGENT_NESTS = "Nests_agents";
	IContainer<?, ? extends IAgent> listOfNestAgents;
	
	// Spatial distribution
	public final static String SPATIALDISTRIBUTION = "spatial_distribution";
	private SpatialDistribution spatialDistribution;
	
	public final static String D_FEATURE = "distribution_feature";
	private String distributionFeature = "";

	public final static String CONSTRAINTS = "spatial_constraints";

	private GenStarGamaConstraintBuilder cBuilder;
	
	String crs;
	
	private int priorityCounter;

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
	
	public AttributeFactory getAttf() {
		return AttributeFactory.getFactory();
	}

	@setter(DEMOGRAPHIC_FILES)
	public void setInputFiles(List<GSSurveyWrapper> inputFiles) {
		this.inputFiles = inputFiles;
	}
	
	@getter(HOUSEHOLD_DICTIONARY)
	public AttributeDictionary getHouseholdAttributes() {
		return householdAttributes;
	}
	
	@setter(HOUSEHOLD_DICTIONARY)
	public void setHouseholdAttributes(AttributeDictionary householdAttributes) {
		this.householdAttributes = householdAttributes;
	}

	@getter(ATTRIBUTES_DICTIONARY)
	public AttributeDictionary getInputAttributes() {
		return individualAttributes;
	}

	@setter(ATTRIBUTES_DICTIONARY)
	public void setInputAttributes(AttributeDictionary inputAttributes) {
		this.individualAttributes = inputAttributes;
	}
	
	@getter(ATTRIBUTES)
	public IList<String> getAttributeName(){
		IList<String> atts = GamaListFactory.create(Types.STRING);
		for (Attribute<? extends core.metamodel.value.IValue> a : this.getInputAttributes().getAttributes())
			atts.add(a.getAttributeName());
		return atts;
	}
	
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
	
	@getter(DEMOGRAPHIC_FILES)
	public List<GSSurveyWrapper> getInputFiles() {
		return inputFiles;
	}
	
	@getter(GENERATION_ALGO)
	public String getGenerationAlgorithm() { return generationAlgorithm; }
	
	/**
	 * Get the constant enumerate algorithm for Gen*
	 * @return
	 */
	public GenerationAlgorithm getGenstarGenerationAlgorithm() {return GenerationAlgorithm.getAlgorithm(this.generationAlgorithm); }
	
	@getter(IPF)
	public boolean getIPF() { return this.ipf; }
	
	@setter(IPF)
	public void setIPF(boolean ipf) { this.ipf = ipf; }

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
	
	@getter(NESTS)
	public String getPathNestGeometries() {
		return this.pathNestGeometries;
	}
	
	@setter(NESTS)
	public void setPathNestGeometries(String path) {
		this.pathNestGeometries = path;
	}
	

	@getter(AGENT_NESTS)
	public void setNestAgentsGeometries(IContainer<?, ? extends IAgent> listOfAgents) {
		this.listOfNestAgents = listOfAgents;
	}
	
	@setter(AGENT_NESTS)
	public IContainer<?, ? extends IAgent> getNestAgentsGeometries() {
		return this.listOfNestAgents;
	}

	@setter(MATCH)
	public void setPathCensusGeometries(String stringPathToCensusShapefile) {
		this.pathCensusGeometries = stringPathToCensusShapefile;
		
		setSpatializePopulation(pathCensusGeometries != null);
	}

	@getter(MATCH)
	public String getPathCensusGeometries() {
		return pathCensusGeometries;
	}

	// TODO : get ride of next methods before spatial distribution

	public void setLocalizedAround(Double min, Double max, boolean overlaps) {
		setMinDistanceLocalize(min);
		setMaxDistanceLocalize(max);
		setLocalizeOverlaps(overlaps);
	}


	public Double getMinDistanceLocalize() {
		return minDistanceLocalize;
	}


	public void setMinDistanceLocalize(Double minDistanceLocalize) {
		this.minDistanceLocalize = minDistanceLocalize;
	}


	public Double getMaxDistanceLocalize() {
		return maxDistanceLocalize;
	}


	public void setMaxDistanceLocalize(Double maxDistanceLocalize) {
		this.maxDistanceLocalize = maxDistanceLocalize;
	}


	public boolean isLocalizeOverlaps() {
		return localizeOverlaps;
	}


	public void setLocalizeOverlaps(boolean localizeOverlaps) {
		this.localizeOverlaps = localizeOverlaps;
	}

	// --------------------
	// Spatial distribution
	// --------------------
	
	@getter(SPATIALDISTRIBUTION)
	public SpatialDistribution getSpatialDistribution() { return spatialDistribution; }
	
	@setter(SPATIALDISTRIBUTION)
	public void setSpatialDistribution(SpatialDistribution spatialDistribution) { this.spatialDistribution = spatialDistribution; }

	@getter(D_FEATURE)
	public String getSpatialDistributionFeature() {return distributionFeature;}	
	
	@setter(D_FEATURE)
	public void setSpatialDistributionFeature(String feature) { this.distributionFeature = feature; }
	
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
	
	@getter(CONSTRAINTS)
	public GenStarGamaConstraintBuilder getConstraintBuilder() {
		return this.cBuilder;
	}
	
	public Collection<ISpatialConstraint> getConstraints(SPLVectorFile sfGeometries, IScope scope) throws IllegalStateException {
		return this.cBuilder.buildConstraints(sfGeometries.getGeoEntity());
	}
	
	// -------------------------
	// Mapper / ancilary methods
	// -------------------------
	
	public List<String> getPathAncilaryGeofiles() {
		return pathAncilaryGeofiles;
	}

	public void addAncilaryGeoFiles(String pathToFile) {
		pathAncilaryGeofiles.add(pathToFile);
	}

	public void setPathAncilaryGeofiles(List<String> pathAncilaryGeofiles) {
		this.pathAncilaryGeofiles = pathAncilaryGeofiles;
	}
	
	// GLOBALS

	public void setGeneratedPopulation(IPopulation<? extends ADemoEntity, ?> population) {
		this.generatedPopulation = population;
	}
	public IPopulation<? extends ADemoEntity, ?> getGeneratedPopulation() {
		return generatedPopulation;
	}
	
	public void addAgent(ADemoEntity e, IAgent a) {
		mapEntitiesAgent.put(e, a);	
	}

	public IAgent getAgent(ADemoEntity e) {
		return mapEntitiesAgent.get(e);
	}
	
	public Collection<IAgent> getAgents() {
		return mapEntitiesAgent.values();
	}


}
