/*******************************************************************************************************
 *
 * IConcept.java, in ummisco.gama.annotations, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.precompiler;

/**
 * The Interface IConcept.
 */
public interface IConcept {
	// list of all the "concept" keywords used in the website.
	
	/* note : 
	 * - some of those keywords refers directly to an action when this action is very useful
	 *   (normally, when the user search an action, the result returned is the species/skill related)
	 * - GAML keywords can be tagged with those keywords by adding the attribute "keyword" in the declaration.
	 */
	
	/** The Constant ACTION. */
	public static final String ACTION					= "action";
	
	/** The Constant AGENT_LOCATION. */
	public static final String AGENT_LOCATION			= "agent_location";
	
	/** The Constant AGENT_MOVEMENT. */
	public static final String AGENT_MOVEMENT			= "agent_movement";
	
	/** The Constant ALGORITHM. */
	public static final String ALGORITHM				= "algorithm";
	
	/** The Constant ARCHITECTURE. */
	public static final String ARCHITECTURE				= "architecture";
	
	/** The Constant ARITHMETIC. */
	public static final String ARITHMETIC				= "arithmetic";
	
	/** The Constant ASC. */
	public static final String ASC						= "asc";
	
	/** The Constant ATTRIBUTE. */
	public static final String ATTRIBUTE				= "attribute";
	
	/** The Constant AUTOSAVE. */
	public static final String AUTOSAVE					= "autosave";
	
	/** The Constant BACKGROUND. */
	public static final String BACKGROUND				= "background";
	
	/** The Constant BATCH. */
	public static final String BATCH					= "batch";
	
	/** The Constant BDI. */
	public static final String BDI						= "bdi";
	
	/** The Constant BEHAVIOR. */
	public static final String BEHAVIOR					= "behavior";
	
	/** The Constant CAMERA. */
	public static final String CAMERA					= "camera";
	
	/** The Constant CAST. */
	public static final String CAST						= "cast";
	
	/** The Constant CHART. */
	public static final String CHART					= "chart";
	
	/** The Constant CLUSTERING. */
	public static final String CLUSTERING				= "clustering";
	
	/** The Constant COLOR. */
	public static final String COLOR					= "color";
	
	/** The Constant COMODEL. */
	public static final String COMODEL					= "comodel";
	
	/** The Constant COMPARISON. */
	public static final String COMPARISON				= "comparison";
	
	/** The Constant COMMUNICATION. */
	public static final String COMMUNICATION			= "communication";
	
	/** The Constant CONDITION. */
	public static final String CONDITION				= "condition";
	
	/** The Constant CONSTANT. */
	public static final String CONSTANT					= "constant";
	
	/** The Constant CONTAINER. */
	public static final String CONTAINER				= "container";
	
	/** The Constant CSV. */
	public static final String CSV						= "csv";
	
	/** The Constant CYCLE. */
	public static final String CYCLE					= "cycle";
	
	/** The Constant DATE. */
	public static final String DATE						= "date";
	
	/** The Constant DATABASE. */
	public static final String DATABASE					= "database";
	
	/** The Constant DEM. */
	public static final String DEM						= "dem";
	
	/** The Constant DGS. */
	public static final String DGS						= "dgs";
	
	/** The Constant DIFFUSION. */
	public static final String DIFFUSION				= "diffusion";
	
	/** The Constant DIMENSION. */
	public static final String DIMENSION				= "dimension";
	
	/** The Constant DISPLAY. */
	public static final String DISPLAY					= "display";
	
	/** The Constant DISTRIBUTION. */
	public static final String DISTRIBUTION				= "distribution";
	
	/** The Constant DXF. */
	public static final String DXF						= "dxf";
	
	/** The Constant EDGE. */
	public static final String EDGE						= "edge";
	
	/** The Constant ELEVATION. */
	public static final String ELEVATION				= "elevation";
	
	/** The Constant ENUMERATION. */
	public static final String ENUMERATION				= "enumeration";
	
	/** The Constant EQUATION. */
	public static final String EQUATION					= "equation";
	
	/** The Constant EXPERIMENT. */
	public static final String EXPERIMENT				= "experiment";
	
	/** The Constant FACET. */
	public static final String FACET					= "facet";
	
	/** The Constant FILE. */
	public static final String FILE						= "file";
	
	/** The Constant FILTER. */
	public static final String FILTER					= "filter";
	
	/** The Constant FIPA. */
	public static final String FIPA						= "fipa";
	
	/** The Constant FSM. */
	public static final String FSM						= "fsm";
	
	/** The Constant GEOMETRY. */
	public static final String GEOMETRY					= "geometry";
	
	/** The Constant GIS. */
	public static final String GIS						= "gis";
	
	/** The Constant GLOBAL. */
	public static final String GLOBAL					= "global";
	
	/** The Constant GRAPH. */
	public static final String GRAPH					= "graph";
	
	/** The Constant GRAPH_WEIGHT. */
	public static final String GRAPH_WEIGHT				= "graph_weight";
	
	/** The Constant GML. */
	public static final String GML						= "gml";
	
	/** The Constant GRID. */
	public static final String GRID						= "grid";
	
	/** The Constant GRAPHIC. */
	public static final String GRAPHIC					= "graphic";
	
	/** The Constant GRAPHIC_UNIT. */
	public static final String GRAPHIC_UNIT				= "graphic_unit";
	
	/** The Constant GUI. */
	public static final String GUI						= "gui";
	
	/** The Constant HALT. */
	public static final String HALT						= "halt";
	
	/** The Constant HEADLESS. */
	public static final String HEADLESS					= "headless";
	
	/** The Constant HYDROLOGY. */
	public static final String HYDROLOGY				= "hydrology";
	
	/** The Constant IMAGE. */
	public static final String IMAGE					= "image";
	
	/** The Constant IMPORT. */
	public static final String IMPORT					= "import";
	
	/** The Constant INHERITANCE. */
	public static final String INHERITANCE				= "inheritance";
	
	/** The Constant INIT. */
	public static final String INIT						= "init";
	
	/** The Constant INSPECTOR. */
	public static final String INSPECTOR				= "inspector";
	
	/** The Constant LAYER. */
	public static final String LAYER					= "layer";
	
	/** The Constant LENGTH_UNIT. */
	public static final String LENGTH_UNIT				= "length_unit";
	
	/** The Constant LIGHT. */
	public static final String LIGHT					= "light";
	
	/** The Constant LIST. */
	public static final String LIST						= "list";
	
	/** The Constant LOAD_FILE. */
	public static final String LOAD_FILE				= "load_file";
	
	/** The Constant LOGICAL. */
	public static final String LOGICAL					= "logical";
	
	/** The Constant LOOP. */
	public static final String LOOP						= "loop";
	
	/** The Constant MATRIX. */
	public static final String MATRIX					= "matrix";
	
	/** The Constant MATH. */
	public static final String MATH						= "math";
	
	/** The Constant MAP. */
	public static final String MAP						= "map";
	
	/** The Constant MIRROR. */
	public static final String MIRROR					= "mirror";
	
	/** The Constant MODEL. */
	public static final String MODEL					= "model";
	
	/** The Constant MONITOR. */
	public static final String MONITOR					= "monitor";
	
	/** The Constant MULTI_LEVEL. */
	public static final String MULTI_LEVEL				= "multi_level";
	
	/** The Constant MULTI_CRITERIA. */
	public static final String MULTI_CRITERIA			= "multi_criteria";
	
	/** The Constant MULTI_SIMULATION. */
	public static final String MULTI_SIMULATION			= "multi_simulation";
	
	/** The Constant NEIGHBORS. */
	public static final String NEIGHBORS				= "neighbors";
	
	/** The Constant NETWORK. */
	public static final String NETWORK					= "network";
	
	/** The Constant NIL. */
	public static final String NIL						= "nil";
	
	/** The Constant NODE. */
	public static final String NODE						= "node";
	
	/** The Constant OBJ. */
	public static final String OBJ						= "obj";
	
	/** The Constant OBSTACLE. */
	public static final String OBSTACLE					= "obstacle";
	
	/** The Constant OPENGL. */
	public static final String OPENGL					= "opengl";
	
	/** The Constant OPERATOR. */
	public static final String OPERATOR					= "operator";
	
	/** The Constant OPTIMIZATION. */
	public static final String OPTIMIZATION				= "optimization";
	
	/** The Constant OSM. */
	public static final String OSM						= "osm";
	
	/** The Constant OUTPUT. */
	public static final String OUTPUT					= "output";
	
	/** The Constant OVERLAY. */
	public static final String OVERLAY					= "overlay";
	
	/** The Constant PARAMETER. */
	public static final String PARAMETER				= "parameter";
	
	/** The Constant PAUSE. */
	public static final String PAUSE					= "pause";
	
	/** The Constant PERMANENT. */
	public static final String PERMANENT				= "permanent";
	
	/** The Constant PHYSICS_ENGINE. */
	public static final String PHYSICS_ENGINE			= "physics_engine";
	
	/** The Constant POINT. */
	public static final String POINT					= "point";
	
	/** The Constant PROBABILITY. */
	public static final String PROBABILITY				= "probability";
	
	/** The Constant PSEUDO_VARIABLE. */
	public static final String PSEUDO_VARIABLE			= "pseudo_variable";
	
	/** The Constant R. */
	public static final String R						= "r";
	
	/** The Constant RANDOM. */
	public static final String RANDOM					= "random";
	
	/** The Constant RANDOM_OPERATOR. */
	public static final String RANDOM_OPERATOR			= "random_operator";
	
	/** The Constant RASTER. */
	public static final String RASTER					= "raster";
	
	/** The Constant REGRESSION. */
	public static final String REGRESSION				= "regression";
	
	/** The Constant REFLEX. */
	public static final String REFLEX					= "reflex";
	
	/** The Constant REFRESH. */
	public static final String REFRESH					= "refresh";
	
	/** The Constant SAVE_FILE. */
	public static final String SAVE_FILE				= "save_file";
	
	/** The Constant SCHEDULER. */
	public static final String SCHEDULER				= "scheduler";
	
	/** The Constant SERIALIZE. */
	public static final String SERIALIZE				= "serialize";
	
	/** The Constant SHAPE. */
	public static final String SHAPE					= "shape";
	
	/** The Constant SHAPEFILE. */
	public static final String SHAPEFILE				= "shapefile";
	
	/** The Constant SHORTEST_PATH. */
	public static final String SHORTEST_PATH			= "shortest_path";
	
	/** The Constant SKILL. */
	public static final String SKILL					= "skill";
	
	/** The Constant SOUND. */
	public static final String SOUND					= "sound";
	
	/** The Constant SPATIAL_COMPUTATION. */
	public static final String SPATIAL_COMPUTATION		= "spatial_computation";
	
	/** The Constant SPATIAL_RELATION. */
	public static final String SPATIAL_RELATION			= "spatial_relation";
	
	/** The Constant SPATIAL_TRANSFORMATION. */
	public static final String SPATIAL_TRANSFORMATION	= "spatial_transformation";
	
	/** The Constant SPECIES. */
	public static final String SPECIES					= "species";
	
	/** The Constant SPORT. */
	public static final String SPORT					= "sport";
	
	/** The Constant STATISTIC. */
	public static final String STATISTIC				= "statistic";
	
	/** The Constant STRING. */
	public static final String STRING					= "string";
	
	/** The Constant SURFACE_UNIT. */
	public static final String SURFACE_UNIT				= "surface_unit";
	
	/** The Constant SVG. */
	public static final String SVG						= "svg";
	
	/** The Constant SYSTEM. */
	public static final String SYSTEM					= "system";
	
	/** The Constant TASK_BASED. */
	public static final String TASK_BASED				= "task_based";
	
	/** The Constant TERNARY. */
	public static final String TERNARY					= "ternary";
	
	/** The Constant TEXT. */
	public static final String TEXT						= "text";
	
	/** The Constant TEXTURE. */
	public static final String TEXTURE					= "texture";
	
	/** The Constant TEST. */
	public static final String TEST						= "test";
	
	/** The Constant THREED. */
	public static final String THREED					= "3d";
	
	/** The Constant TIF. */
	public static final String TIF						= "tif";
	
	/** The Constant TIME. */
	public static final String TIME						= "time";
	
	/** The Constant TIME_UNIT. */
	public static final String TIME_UNIT				= "time_unit";
	
	/** The Constant TOPOLOGY. */
	public static final String TOPOLOGY					= "topology";
	
	/** The Constant TORUS. */
	public static final String TORUS					= "torus";
	
	/** The Constant TRANSPORT. */
	public static final String TRANSPORT				= "transport";
	
	/** The Constant TXT. */
	public static final String TXT						= "txt";
	
	/** The Constant TYPE. */
	public static final String TYPE						= "type";
	
	/** The Constant UPDATE. */
	public static final String UPDATE					= "update";
	
	/** The Constant VOLUME_UNIT. */
	public static final String VOLUME_UNIT				= "volume_unit";
	
	/** The Constant WEIGHT_UNIT. */
	public static final String WEIGHT_UNIT				= "weight_unit";
	
	/** The Constant WRITE. */
	public static final String WRITE					= "write";
	
	/** The Constant XML. */
	public static final String XML						= "xml";
	
	/** The Constant WORLD. */
	public static final String WORLD					= "world";
}
