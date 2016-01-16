/*********************************************************************************************
 *
 *
 * 'IKeyword.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gama.common.interfaces;

/**
 * The class IKeyword. Defines most of the keywords used in GAMA and GAML.
 *
 * @author drogoul
 * @since 13 dec. 2011
 *
 */
public interface IKeyword {

	public static final String _DOT = ".";
	public static final String ACTION = "action";
	public static final String ADD = "add";
	public static final String AGENT = "agent";
	public static final String AGENTS = "agents";
	public static final String AGGREGATION = "aggregation";
	public static final String ALL = "all";
	public static final String AMBIENT_LIGHT = "ambient_light";
	public static final String AMONG = "among";
	public static final String ANNEALING = "annealing";
	public static final String APPEND_HORIZONTALLY = "append_horizontally";
	public static final String APPEND_VERTICALLY = "append_vertically";
	public static final String AREA = "area";
	public static final String AREA_STACK = "area_stack";
	public static final String ARG = "arg";
	public static final String AS = "as";
	public static final String ASK = "ask";
	public static final String ASPECT = "aspect";
	public static final String ASSET3D = "asset3D";
	public static final String AT = "at";
	public static final String ATTRIBUTES = "attributes";
	public static final String AUTHOR = "author";
	public static final String AUTOSAVE = "autosave";
	public static final String AXES = "axes";
	public static final String BACKGROUND = "background";
	public static final String BAR = "bar";
	public static final String BOX_WHISKER = "box_whisker"; // new type of chart: box and whisker
	public final static String BATCH = "batch";
	public static final String BEHAVIOR = "behavior";
	public static final String BEHAVIORS = "behaviors";
	public static final String BITMAP = "bitmap";
	public static final String BOUNDS = "bounds";
	public static final String BREAK = "break";
	public static final String BRIGHTER = "brighter";
	public static final String BROWSE = "browse";
	public static final String CAMERA = "camera";
	public static final String CAMERA_POS = "camera_pos";
	public static final String CAMERA_LOOK_POS = "camera_look_pos";
	public static final String CAMERA_UP_VECTOR = "camera_up_vector";
	public static final String CAPTURE = "capture";
	public static final String CATEGORY = "category";
	public static final String CELL_WIDTH = "cell_width";
	public static final String CELL_HEIGHT = "cell_height";

	public static final String CELLULAR = "cellular";
	public static final String CENTER = "center";
	public static final String CHAIN = "chain";
	public static final String CHART = "chart";
	public static final String CHOOSE = "choose";
	public static final String CLOSE_EXP = ")";
	public static final String CLOSE_LIST = "]";
	public static final String CLOSE_POINT = "}";
	public static final String COLOR = "color";
	public static final String COLOR_BLUE = "blue";
	public static final String COLOR_GREEN = "green";
	public static final String COLOR_RED = "red";
	public static final String COMMA = ",";
	public static final String CONDITION = "condition";
	public static final String CONST = "const";
	public static final String CONTENTS = "contents";
	public static final String CONTRIBUTE = "contribute";
	public static final String CONTROL = "control";
	public static final String CREATE = "create";
	public static final String CSV = "csv";
	public static final String CURRENT_STATE = "currentState";
	public static final String DARKER = "darker";
	public static final String DATA = "data";
	public static final String DECAY = "decay";
	public static final String DEFAULT = "default";
	// public static final String DEFAULT_EXP = "default";
	// public static final String DEM = "dem";
	public static final String DEPENDS_ON = "depends_on";
	public static final String DEPTH = "depth";
	public static final String DESTINATION = "destination";
	public static final String DIFFUSION = "diffusion";
	public static final String DIFFUSE_LIGHT = "diffuse_light";
	public static final String DIFFUSE_LIGHT_POS = "diffuse_light_pos";
	public static final String DISABLE = "disable";
	public static final String DISPLAY = "display";
	public static final String DISPLAY_GL = "graphdisplaygl";
	public static final String DISPLAY_GRAPH = "graphdisplay";
	public static final String DISTANCE_CACHE = "distance_cache_enabled";
	public final static String DIVIDE = "/";
	public static final String DO = "do";
	public static final String DOT = "dot";
	public static final String DRAW = "draw";
	public static final String DRAWENV = "draw_env";
	public static final String DRAW_NORM = "draw_norm";
	public static final String DRAW_DIFFUSE_LIGHT = "draw_diffuse_light";
	// public static final String DRAWASDEM = "draw_as_dem";
	public static final String ELEVATION = "elevation";
	public static final String DYNAMIC = "dynamic";
	public static final String EACH = "each";
	public static final String EDGE = "edge";
	public static final String EDGE_SPECIES = "edge_species";
	public static final String ELSE = "else";
	public static final String EMPTY = "empty";
	public static final String BORDER = "border";
	public static final String ENABLE = "enable";
	public static final String ENTER = "enter";
	public static final String ENTITIES = "entities";
	public static final String ENVIRONMENT = "environment";
	public static final String EQUALS = "equals";
	/*
	 * Equations
	 */
	public static final String EQUATION_OP = "=";
	public static final String EQUATION_LEFT = "left";
	public static final String EQUATION_RIGHT = "right";
	public static final String EQUATION = "equation";
	public static final String SIMULTANEOUSLY = "simultaneously";
	public static final String SOLVER = "solver";
	public static final String SOLVE = "solve";
	public static final String TIME_INITIAL = "t0";
	public static final String TIME_FINAL = "tf";
	public static final String CYCLE_LENGTH = "cycle_length";
	public static final String DIFF = "diff";
	public static final String DIF2 = "diff2";
	public static final String ZERO = "internal_zero_order_equation";

	/*
	 *
	 */
	public static final String EVENT = "event";
	public static final String ERROR = "error";
	public static final String EXHAUSTIVE = "exhaustive";
	public static final String EXISTS = "exists";
	public static final String EXPERIMENT = "experiment";
	// public static final String EXPERIMENTATOR = "experimentator";
	public static final String EXPLODED = "exploded";
	public static final String EXPORT = "export";
	public static final String EXTENSION = "extension";
	public static final String FADING = "fading";
	public static final String FALSE = "false";
	public static final String FILE = "file";
	public static final String OUTPUT_FILE = "output_file";
	public static final String FILL_WITH = "fill_with";
	public static final String FOCUS = "focus";
	public static final String FONT = "font";
	public static final String FOOTER = "footer";
	public static final String FRAMERATE = "framerate";
	public static final String FREQUENCY = "frequency";
	public static final String FROM = "from";
	public static final String FSM = "fsm";
	public static final String FUNCTION = "function";
	public static final String GAP = "gap";
	// public static final String GAML = "gaml";
	public static final String GENETIC = "genetic";
	public static final String GIS = "gis";
	public static final String GLOBAL = "global";
	public static final String GRADIENT = "gradient";
	public static final String GRAPH = "graph";
	public static final String GRAPH_SKILL = "graph_user";
	public static final String GRAPHIC_SKILL = "graphic";
	public static final String GRAPHICS = "graphics";
	public static final String GRAYSCALE = "grayscale";
	public static final String GRID = "grid";
	public static final String GRID_POPULATION = "display_grid";
	public static final String GRID_X = "grid_x";
	public static final String GRID_Y = "grid_y";
	public static final String GRID_VALUE = "grid_value";
	public static final String GROUP = "group";
	public final static String GUI_ = "gui";
	public static final String HEADER = "header";
	public static final String HEADING = "heading";
	public static final String HEADLESS_UI = "headless";
	public static final String HEIGHT = "height";
	public static final String HER = "her";
	public static final String HILL_CLIMBING = "hill_climbing";
	public static final String HIS = "his";
	public static final String HISTOGRAM = "histogram";
	public static final String HOST = "host";
	public static final String IF = "if";
	public static final String IGNORE = "ignore";
	public static final String IMAGE = "image";
	public static final String IN = "in";
	public static final String INCLUDE = "include";
	public static final String INDEX = "index";
	public static final String INERTIA = "inertia";
	public static final String INIT = "init";
	public static final String INPUT = "input";
	public static final String INSPECT = "inspect";
	public static final String INTERNAL_FUNCTION = "internal_function";
	public static final String IS = "is";
	public static final String ISFOLDER = "is_folder";
	public static final String IS_LIGHT_ON = "light";
	public static final String ISNOT = "is_not";
	public static final String ITEM = "item";
	public static final String ITS = "its";
	public static final String FRAGMENT = "fragment";
	public static final String JAVA = "java";
	public static final String KEEP = "keep";
	public static final String KEEP_SEED = "keep_seed";
	public static final String KEY = "key";
	public static final String KEYWORD = "keyword";
	public static final String KILL = "kill";
	public static final String OVERLAY = "overlay";
	public static final String LEFT = "left";
	public static final String LEGEND = "legend";
	public static final String LET = "let";
	public static final String LINE = "line";
	public static final String LINES = "lines";
	public static final String LOCATION = "location";
	public static final String LOOP = "loop";
	public static final String LOOK_AT = "look_at";
	public static final String MAPPING = "mapping";
	public static final String MATCH = "match";
	public static final String MATCH_BETWEEN = "match_between";
	public static final String MATCH_ONE = "match_one";
	public static final String MASK = "mask";
	public static final String MAX = "max";
	public static final String MAXIMIZE = "maximize";
	public static final String MEMBERS = "members";
	public static final String MERSENNE = "mersenne";
	public static final String MESSAGE = "message";
	public static final String METHOD = "method";
	public static final String MICRO = "micro";
	public static final String MICRO_LAYER = "micro_layer";
	public static final String MIGRATE = "migrate";
	public static final String MIN = "min";
	public static final String MINIMIZE = "minimize";
	public final static String MINUS = "-";
	public final static String MIRRORS = "mirrors";
	public final static String MODE = "mode";
	public static final String MODEL = "model";
	public static final String MONITOR = "monitor";
	public static final String MOUSE_DOWN = "mouse_down";
	public static final String MOUSE_UP = "mouse_up";
	public static final String MOUSE_CLICKED = "mouse_click";
	public static final String MOVING_SKILL = "moving";
	public static final String MOVING_3D_SKILL = "moving3D";
	public final static String MULTIPLY = "*";
	public final static String MULTICORE = "multicore";
	public static final String MY = "my";
	public static final String MYGRAPH = "my_graph";
	public static final String MYSELF = "myself";
	public static final String NAME = "name";
	public static final String NEIGHBOURS = "neighbours";
	public static final String NODE = "node";
	public static final String NULL = "nil";
	public static final String NUMBER = "number";
	public static final String NB_COLS = "nb_cols";
	public static final String NB_ROWS = "nb_rows";
	public static final String OF = "of";
	public static final String ON = "on";
	public static final String OPEN_EXP = "(";
	public static final String OPEN_LIST = "[";
	public static final String OPEN_POINT = "{";
	public static final String OPTIONAL = "optional";
	public static final String ORTHOGRAPHIC_PROJECTION = "orthographic_projection";
	public static final String OTHER_EVENTS = "other_events";
	public static final String OUTPUT = "output";
	public final static String OUTPUT3D = "output3D";
	public static final String OVER = "over";
	public static final String OVERWRITE = "overwrite";
	public static final String PARAM = "param";
	public static final String PARAMETER = "parameter";
	public static final String PARAMS = "params";
	public static final String PARENT = "parent";
	public static final String PATH = "path";
	public static final String PAUSE_SOUND = "pause_sound";
	public static final String PEERS = "peers";
	public static final String PERMANENT = "permanent"; // "show" // "front_end" // "presentation" // "
	public static final String PIE = "pie";
	public static final String PITCH = "pitch";
	public static final String PLACES = "places";
	public final static String PLUS = "+";
	public final static String POLYGONMODE = "polygonmode";
	public final static String POPULATION = "display_population";
	public static final String POSITION = "position";
	public static final String PRIMITIVE = "primitive";
	public static final String PRIORITY = "priority";
	public static final String PROPAGATION = "propagation";
	public static final String PROPORTION = "proportion";
	public static final String PUT = "put";
	public static final String QUADTREE = "quadtree";
	public static final String RAISES = "raises";
	public static final String RANDOM_SPECIES_NAME = "random_builder";
	public static final String RANGE = "range";
	public static final String REACTIVE_TABU = "reactive_tabu";
	public static final String READABLE = "readable";
	public static final String REFLEX = "reflex";
	public static final String REFRESH = "refresh";
	public static final String REFRESH_EVERY = "refresh_every";
	public static final String REGISTER = "register";
	public static final String RELEASE = "release";
	public final static String REMOTE = "remote";
	public static final String REMOVE = "remove";
	public static final String REPEAT = "repeat";
	public static final String RESUME_SOUND = "resume_sound";
	public static final String RETURN = "return";
	public static final String RETURNS = "returns";
	public static final String REWRITE = "rewrite";
	public static final String RIGHT = "right";
	public static final String RING = "ring";
	public static final String RNG = "rng";
	public static final String ROLL = "roll";
	public static final String ROUNDED = "rounded";
	public static final String ROTATE = "rotate";
	public static final String ROTATE3D = "rotate3D";
	public static final String SAVE = "save";
	public static final String SAVE_BATCH = "save_batch";
	public static final String SCALE = "scale";
	public static final String SCATTER = "scatter";
	public static final String SCHEDULE = "schedule";
	public static final String SCHEDULES = "schedules";
	public static final String SEED = "seed";
	public static final String SEGMENTS = "segments";
	public static final String SELECTABLE = "selectable";
	public static final String SELF = "self";
	public static final String SERIES = "series";
	public static final String SET = "set";
	public static final String SHAPE = "shape";
	public static final String SHOWFPS = "show_fps";
	public static final String SIGNAL = "signal";
	public static final String SIMULATION = "simulation";
	public static final String SIZE = "size";
	public static final String SKILL = "skill";
	public static final String SKILLS = "skills";
	public static final String SOURCE = "source";
	public static final String SPECIES = "species";
	public static final String SPEED = "speed";
	public static final String SPLINE = "spline";
	public static final String STACK = "stack";
	public static final String START_SOUND = "start_sound";
	public static final String STATE = "state";
	public static final String STATES = "states";
	public static final String STATUS = "status";
	public static final String STEP = "step";
	public static final String STOP_SOUND = "stop_sound";
	public static final String STRATEGY = "scheduling_strategy";
	public static final String STYLE = "style";
	public static final String SWITCH = "switch";
	public static final String TABU = "tabu";
	public static final String TARGET = "target";
	public static final String TARGETS = "scheduling_targets";
	public static final String TESSELATION = "tesselation";
	public static final String TEXT = "text";
	public static final String TEXTURE = "texture";
	public static final String THE = "the";
	public static final String THEIR = "their";
	public static final String THREE_D = "3d";
	public static final String TIMES = "times";
	public static final String TIMEXSERIES = "timexseries"; // hqnghi facet timeXseries for
															// continuous Chart
	public static final String TABLE = "table";
	public static final String TITLE = "title";
	public static final String TO = "to";
	public static final String TOPOLOGY = "topology";
	public static final String TORUS = "torus";
	public static final String TRACE = "trace";
	public static final String TRANSPARENCY = "transparency";
	public static final String TRIANGULATION = "triangulation";
	public static final String TRUE = "true";
	public static final String TYPE = "type";
	public static final String UNIT = "unit";
	public static final String UNTIL = "until";
	public static final String UPDATE = "update";
	public static final String UP_VECTOR = "up_vector";
	public static final String USER_CONTROLLED = "user_controlled";
	public static final String USER_LOCATION = "user_location";
	public static final String USER_COMMAND = "user_command";
	public static final String USER_INPUT = "user_input";
	public static final String USER_ONLY = "user_only";
	public static final String USER_FIRST = "user_first";
	public static final String USER_LAST = "user_last";
	public static final String USER_PANEL = "user_panel";
	public static final String USING = "using";
	public static final String VALUE = "value";
	public static final String VALUES = "values";
	public static final String VAR = "var";
	public static final String VARIATION = "variation";
	public static final String VARS = "vars";
	public static final String VERSION = "version";
	public static final String VERTEX = "vertex";
	public static final String VIRTUAL = "virtual";
	public static final String WARNING = "warn";
	public static final String WARNING_TEST = "warning";
	public static final String WEIGHT = "weight";
	public static final String WHEN = "when";
	public static final String WHISKER = "whisker"; // new type of datachart
	public static final String WHILE = "while";
	public static final String WIDTH = "width";
	public static final String WITH = "with";
	public static final String WORLD_AGENT_NAME = "world";
	public static final String WRITABLE = "writable";
	public static final String WRITE = "write";
	public static final String X = "x";
	public static final String XML = "xml";
	public static final String XOR = "xor";
	public static final String XY = "xy";
	public static final String Y = "y";
	public static final String Z = "z";
	public static final String ZFIGHTING = "z_fighting";
	public static final String[] METHODS = { GENETIC, ANNEALING, HILL_CLIMBING, TABU, REACTIVE_TABU, EXHAUSTIVE };
	public static final String[] EVENT_TYPE = { OTHER_EVENTS, MOUSE_DOWN };
	public static final String USER_INIT = "user_init";
	// public static final String AS_SKILL = "as_skill";
	public static final String IS_SKILL = "is_skill";

	/**
	 * TYPES
	 */
	public static final String LIST = "list";
	public static final String MAP = "map";
	public static final String BOOL = "bool";
	public static final String FLOAT = "float";
	public static final String INT = "int";
	public static final String STRING = "string";
	public static final String POINT = "point";
	public static final String PAIR = "pair";
	public static final String UNKNOWN = "unknown";
	public static final String MATRIX = "matrix";
	public static final String RGB = "rgb";
	public static final String CONTAINER = "container";
	public static final String GEOMETRY = "geometry";

	/*
	 * files
	 */
	public static final String OPEN = "open"; // TODO "launch", or "open", or "sysopen" ? This opens a file with an
												// external progam
	public static final String ALPHA = "alpha";

	public final static String INTERNAL = "internal_";

	/**
	 * Gen*
	 */
	public static final String POPULATION_GENERATORS = "population_generators";
	public static final String SYNTHETIC_POPULATIONS = "synthetic_populations";
	public static final String GENSTAR_POPULATION = "genstar_population";
	public static final String GENSTAR_ENTITY = "genstar_entity";
}
