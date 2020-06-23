/*********************************************************************************************
 *
 * 'IConcept.java, in plugin ummisco.gama.annotations, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.precompiler;

public interface IConcept {
	// list of all the "concept" keywords used in the website.
	
	/* note : 
	 * - some of those keywords refers directly to an action when this action is very useful
	 *   (normally, when the user search an action, the result returned is the species/skill related)
	 * - GAML keywords can be tagged with those keywords by adding the attribute "keyword" in the declaration.
	 */
	
	public static final String ACTION					= "action";
	public static final String AGENT_LOCATION			= "agent_location";
	public static final String AGENT_MOVEMENT			= "agent_movement";
	public static final String ALGORITHM				= "algorithm";
	public static final String ARCHITECTURE				= "architecture";
	public static final String ARITHMETIC				= "arithmetic";
	public static final String ASC						= "asc";
	public static final String ATTRIBUTE				= "attribute";
	public static final String AUTOSAVE					= "autosave";
	public static final String BACKGROUND				= "background";
	public static final String BATCH					= "batch";
	public static final String BDI						= "bdi";
	public static final String BEHAVIOR					= "behavior";
	public static final String CAMERA					= "camera";
	public static final String CAST						= "cast";
	public static final String CHART					= "chart";
	public static final String CLUSTERING				= "clustering";
	public static final String COLOR					= "color";
	public static final String COMODEL					= "comodel";
	public static final String COMPARISON				= "comparison";
	public static final String COMMUNICATION			= "communication";
	public static final String CONDITION				= "condition";
	public static final String CONSTANT					= "constant";
	public static final String CONTAINER				= "container";
	public static final String CSV						= "csv";
	public static final String CYCLE					= "cycle";
	public static final String DATE						= "date";
	public static final String DATABASE					= "database";
	public static final String DEM						= "dem";
	public static final String DGS						= "dgs";
	public static final String DIFFUSION				= "diffusion";
	public static final String DIMENSION				= "dimension";
	public static final String DISPLAY					= "display";
	public static final String DISTRIBUTION				= "distribution";
	public static final String DXF						= "dxf";
	public static final String EDGE						= "edge";
	public static final String ELEVATION				= "elevation";
	public static final String ENUMERATION				= "enumeration";
	public static final String EQUATION					= "equation";
	public static final String EXPERIMENT				= "experiment";
	public static final String FACET					= "facet";
	public static final String FILE						= "file";
	public static final String FILTER					= "filter";
	public static final String FIPA						= "fipa";
	public static final String FSM						= "fsm";
	public static final String GEOMETRY					= "geometry";
	public static final String GIS						= "gis";
	public static final String GLOBAL					= "global";
	public static final String GRAPH					= "graph";
	public static final String GRAPH_WEIGHT				= "graph_weight";
	public static final String GML						= "gml";
	public static final String GRID						= "grid";
	public static final String GRAPHIC					= "graphic";
	public static final String GRAPHIC_UNIT				= "graphic_unit";
	public static final String GUI						= "gui";
	public static final String HALT						= "halt";
	public static final String HEADLESS					= "headless";
	public static final String HYDROLOGY				= "hydrology";
	public static final String IMAGE					= "image";
	public static final String IMPORT					= "import";
	public static final String INHERITANCE				= "inheritance";
	public static final String INIT						= "init";
	public static final String INSPECTOR				= "inspector";
	public static final String LAYER					= "layer";
	public static final String LENGTH_UNIT				= "length_unit";
	public static final String LIGHT					= "light";
	public static final String LIST						= "list";
	public static final String LOAD_FILE				= "load_file";
	public static final String LOGICAL					= "logical";
	public static final String LOOP						= "loop";
	public static final String MATRIX					= "matrix";
	public static final String MATH						= "math";
	public static final String MAP						= "map";
	public static final String MIRROR					= "mirror";
	public static final String MODEL					= "model";
	public static final String MONITOR					= "monitor";
	public static final String MULTI_LEVEL				= "multi_level";
	public static final String MULTI_CRITERIA			= "multi_criteria";
	public static final String MULTI_SIMULATION			= "multi_simulation";
	public static final String NEIGHBORS				= "neighbors";
	public static final String NETWORK					= "network";
	public static final String NIL						= "nil";
	public static final String NODE						= "node";
	public static final String OBJ						= "obj";
	public static final String OBSTACLE					= "obstacle";
	public static final String OPENGL					= "opengl";
	public static final String OPERATOR					= "operator";
	public static final String OPTIMIZATION				= "optimization";
	public static final String OSM						= "osm";
	public static final String OUTPUT					= "output";
	public static final String OVERLAY					= "overlay";
	public static final String PARAMETER				= "parameter";
	public static final String PAUSE					= "pause";
	public static final String PERMANENT				= "permanent";
	public static final String PHYSICS_ENGINE			= "physics_engine";
	public static final String POINT					= "point";
	public static final String PROBABILITY				= "probability";
	public static final String PSEUDO_VARIABLE			= "pseudo_variable";
	public static final String R						= "r";
	public static final String RANDOM					= "random";
	public static final String RANDOM_OPERATOR			= "random_operator";
	public static final String RASTER					= "raster";
	public static final String REGRESSION				= "regression";
	public static final String REFLEX					= "reflex";
	public static final String REFRESH					= "refresh";
	public static final String SAVE_FILE				= "save_file";
	public static final String SCHEDULER				= "scheduler";
	public static final String SERIALIZE				= "serialize";
	public static final String SHAPE					= "shape";
	public static final String SHAPEFILE				= "shapefile";
	public static final String SHORTEST_PATH			= "shortest_path";
	public static final String SKILL					= "skill";
	public static final String SOUND					= "sound";
	public static final String SPATIAL_COMPUTATION		= "spatial_computation";
	public static final String SPATIAL_RELATION			= "spatial_relation";
	public static final String SPATIAL_TRANSFORMATION	= "spatial_transformation";
	public static final String SPECIES					= "species";
	public static final String SPORT					= "sport";
	public static final String STATISTIC				= "statistic";
	public static final String STRING					= "string";
	public static final String SURFACE_UNIT				= "surface_unit";
	public static final String SVG						= "svg";
	public static final String SYSTEM					= "system";
	public static final String TASK_BASED				= "task_based";
	public static final String TERNARY					= "ternary";
	public static final String TEXT						= "text";
	public static final String TEXTURE					= "texture";
	public static final String TEST						= "test";
	public static final String THREED					= "3d";
	public static final String TIF						= "tif";
	public static final String TIME						= "time";
	public static final String TIME_UNIT				= "time_unit";
	public static final String TOPOLOGY					= "topology";
	public static final String TORUS					= "torus";
	public static final String TRANSPORT				= "transport";
	public static final String TXT						= "txt";
	public static final String TYPE						= "type";
	public static final String UPDATE					= "update";
	public static final String VOLUME_UNIT				= "volume_unit";
	public static final String WEIGHT_UNIT				= "weight_unit";
	public static final String WRITE					= "write";
	public static final String XML						= "xml";
	public static final String WORLD					= "world";
}
