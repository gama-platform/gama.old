package msi.gama.precompiler;

public interface IConcept {
	// list of all the "concept" keywords used in the website.
	
	/* note : 
	 * - some of those keywords refers directly to an action when this action is very useful
	 *   (normally, when the user search an action, the result returned is the species/skill related)
	 * - GAML keywords can be tagged with those keywords by adding the attribute "keyword" in the declaration.
	 */
	
	public static final String AGENT_LOCATION			= "agent_location";
	public static final String ALGORITHM				= "algorithm";
	public static final String ARITHMETIC				= "arithmetic";
	public static final String ASPATIAL_GRAPH			= "aspacial_graph";
	public static final String ATTRIBUTE				= "attribute";
	public static final String BROWSE					= "browse";
	public static final String CAST						= "cast";
	public static final String COLOR					= "color";
	public static final String COMPARISON				= "comparison";
	public static final String CONDITION				= "condition";
	public static final String CONSTANT					= "constant";
	public static final String CONTAINER				= "container";
	public static final String CYCLE					= "cycle";
	public static final String DATE						= "date";
	public static final String DIMENSION				= "dimension";
	public static final String DISPLAY					= "display";
	public static final String DISTRIBUTION				= "distribution";
	public static final String EDGE						= "edge";
	public static final String ENUMERATION				= "enumeration";
	public static final String FACET					= "facet";
	public static final String FILE						= "file";
	public static final String FILTER					= "filter";
	public static final String GAML						= "gaml";
	public static final String GEOMETRY					= "geometry";
	public static final String GLOBAL					= "global";
	public static final String GRAPH					= "graph";
	public static final String GRAPH_WEIGHT				= "graph_weight";
	public static final String GRID						= "grid";
	public static final String GRAPHIC_UNIT				= "graphic_unit";
	public static final String HALT						= "halt";
	public static final String IMPORT					= "import";
	public static final String INHERITANCE				= "inheritance";
	public static final String INIT						= "init";
	public static final String KEYWORD					= "keyword";
	public static final String LENGTH_UNIT				= "length_unit";
	public static final String LIST						= "list";
	public static final String LOGICAL					= "logical";
	public static final String LOOP						= "loop";
	public static final String MATRIX					= "matrix";
	public static final String MAP						= "map";
	public static final String MIRROR					= "mirror";
	public static final String MULTI_LEVEL				= "multi_level";
	public static final String NEIGHBORS				= "neighbors";
	public static final String NODE						= "node";
	public static final String OPENGL					= "opengl";
	public static final String OPERATOR					= "operator";
	public static final String OPTIMIZATION				= "optimization";
	public static final String OSM						= "osm";
	public static final String PATH						= "path";
	public static final String PAUSE					= "pause";
	public static final String POINT					= "point";
	public static final String PROBABILITY				= "probability";
	public static final String RANDOM					= "random";
	public static final String RANDOM_OPERATOR			= "random_operator";
	public static final String REFLEX					= "reflex";
	public static final String REFRESH					= "refresh";
	public static final String SHORTEST_PATH			= "shortest_path";
	public static final String SKILL					= "skill";
	public static final String SPATIAL_GRAPH			= "spatial_graph";
	public static final String SPECIES					= "species";
	public static final String STATIC					= "static";
	public static final String STATISTIC				= "statistic";
	public static final String STRING					= "string";
	public static final String SURFACE_UNIT				= "surface_unit";
	public static final String TERNARY					= "ternary";
	public static final String TIME						= "time";
	public static final String TIME_UNIT				= "time_unit";
	public static final String TOPOLOGY					= "topology";
	public static final String TORUS					= "torus";
	public static final String TYPE						= "type";
	public static final String UPDATE					= "update";
	public static final String VOLUME_UNIT				= "volume_unit";
	public static final String WEIGHT_UNIT				= "weight_unit";
	public static final String WRITE					= "write";
}
