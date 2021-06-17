/*********************************************************************************************
 *
 * 'IKeyword.java, in plugin ummisco.gama.annotations, is part of the source code of the GAMA modeling and simulation
 * platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
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

	String _DOT = ".";
	String ABORT = "abort";
	String ACTION = "action";
	String ADD = "add";
	String AGENT = "agent";
	String AGENTS = "agents";
	String AGGREGATION = "aggregation";
	String ALL = "all";
	String ANCHOR = "anchor";
	String AMBIENT_LIGHT = "ambient_light";
	String AMONG = "among";
	String ANNEALING = "annealing";
	String APPEND_HORIZONTALLY = "append_horizontally";
	String APPEND_VERTICALLY = "append_vertically";
	String AREA = "area";
	String AREA_STACK = "area_stack";
	String ARG = "arg";
	String AS = "as";
	String ASK = "ask";
	String ASPECT = "aspect";
	String ASSET3D = "asset3D";
	String AT = "at";
	String ATTRIBUTES = "attributes";
	String AUTHOR = "author";
	String AUTOSAVE = "autosave";
	String AUTORUN = "autorun";
	String AVOID_MASK = "avoid_mask";
	String AXES = "axes";
	String BACKGROUND = "background";
	String BAR = "bar";
	String BOX_WHISKER = "box_whisker"; // new type of
										// chart: box and
										// whisker
	String BATCH = "batch";
	String BEHAVIOR = "behavior";
	String BEHAVIORS = "behaviors";
	// public static final String BITMAP = "bitmap";
	String BENCHMARK = "benchmark";
	String BOUNDS = "bounds";
	String BREAK = "break";
	String BRIGHTER = "brighter";
	String BROWSE = "browse";
	String CAMERA = "camera";
	String CAMERA_LOCATION = "camera_location";
	String CAMERA_TARGET = "camera_target";
	String CAMERA_ORIENTATION = "camera_orientation";
	String CAMERA_LENS = "camera_lens";
	String CAMERA_INTERACTION = "camera_interaction";
	String CAPTURE = "capture";
	String CATCH = "catch";
	String CATEGORY = "category";
	String CELL_WIDTH = "cell_width";
	String CELL_HEIGHT = "cell_height";

	String CELLULAR = "cellular";
	String CENTER = "center";
	String CHAIN = "chain";
	String CHART = "chart";
	String CHOOSE = "choose";
	String CLOSE_EXP = ")";
	String CLOSE_LIST = "]";
	String CLOSE_POINT = "}";
	String COLOR = "color";
	String COLOR_BLUE = "blue";
	String COLOR_GREEN = "green";
	String COLOR_RED = "red";
	String COMMA = ",";
	String CONDITION = "condition";
	String CONST = "const";
	String CONTENTS = "contents";
	String CONTRIBUTE = "contribute";
	String CONTROL = "control";
	String CONVOLUTION = "convolution";
	String CONTINUE = "continue";
	String CREATE = "create";
	String CSV = "csv";
	String CURRENT_STATE = "currentState";
	String DAMPER = "damper";
	String DARKER = "darker";
	String DATA = "data";
	String DECAY = "decay";
	String DEFAULT = "default";
	// public static final String DEFAULT_EXP = "default";
	// public static final String DEM = "dem";
	// public static final String DEPENDS_ON = "depends_on";
	String DEPTH = "depth";
	String DESTINATION = "destination";
	String DIFFUSION = "diffusion";
	String DIFFUSE = "diffuse";
	String DIFFUSE_LIGHT = "diffuse_light";
	String DIFFUSE_LIGHT_POS = "diffuse_light_pos";
	String DIRECTION = "direction";
	String DISPLAY = "display";
	String DISPLAY_GL = "graphdisplaygl";
	String DISPLAY_GRAPH = "graphdisplay";
	String DISTANCE_CACHE = "distance_cache_enabled";
	String DIVIDE = "/";
	String DO = "do";
	String DOT = "dot";
	String DRAW = "draw";
	String DRAWENV = "draw_env";
	String DRAW_DIFFUSE_LIGHT = "draw_diffuse_light";
	String DRAW_LIGHT = "draw_light";
	String ELEVATION = "elevation";
	String DYNAMIC = "dynamic";
	String EACH = "each";
	String EDGE = "edge";
	String EDGE_SPECIES = "edge_species";
	String ELSE = "else";
	String EMPTY = "empty";
	String WIREFRAME = "wireframe";
	String BORDER = "border";
	String ENTER = "enter";
	String ENTITIES = "entities";
	String ENVIRONMENT = "environment";
	String EQUALS = "equals";
	String GAMA = "gama";
	/*
	 * Equations
	 */
	String EQUATION_OP = "=";
	String EQUATION_LEFT = "left";
	String EQUATION_RIGHT = "right";
	String EQUATION = "equation";
	String SIMULTANEOUSLY = "simultaneously";
	String SOLVER = "solver";
	String SOLVE = "solve";
	String TIME_INITIAL = "t0";
	String TIME_FINAL = "tf";
	String CYCLE_LENGTH = "cycle_length";
	String DIFF = "diff";
	String DIF2 = "diff2";
	String ZERO = "internal_zero_order_equation";

	/*
	 *
	 */
	String ENABLES = "enables";
	String DISABLES = "disables";
	String EVENT = "event";
	String ERROR = "error";
	String EXHAUSTIVE = "exhaustive";
	String EXISTS = "exists";
	String EXPERIMENT = "experiment";
	// public static final String EXPERIMENTATOR = "experimentator";
	String EXPLODED = "exploded";
	// public static final String EXPORT = "export";
	String EXTENSION = "extension";
	String FADING = "fading";
	String FALSE = "false";
	String FILE = "file";
	String FOLDER = "folder";

	String DIRECTORY = "directory";
	String FILES = "files";
	String OUTPUT_FILE = "output_file";
	String FILL_WITH = "fill_with";
	String FIELD = "field";
	String FOCUS = "focus";
	String FOCUS_ON = "focus_on";
	String FONT = "font";
	String FOOTER = "footer";
	String FRAMERATE = "framerate";
	String FREQUENCY = "frequency";
	String FROM = "from";
	String FSM = "fsm";
	String FUNCTION = "function";
	String FULLSCREEN = "fullscreen";
	String GAP = "gap";
	String GENETIC = "genetic";
	String GIS = "gis";
	String GLOBAL = "global";
	String GRADIENT = "gradient";
	String GRAPH = "graph";
	String GRAPH_SKILL = "graph_user";
	String GRAPHIC_SKILL = "graphic";
	String GRAPHICS = "graphics";
	String GRAYSCALE = "grayscale";
	String GRID = "grid";
	String GRID_POPULATION = "display_grid";
	String GRID_X = "grid_x";
	String GRID_Y = "grid_y";
	String GRID_VALUE = "grid_value";
	String BANDS = "bands";
	String GROUP = "group";
	String GUI_ = "gui";
	String HEADER = "header";
	String HEADING = "heading";
	String HEADLESS_UI = "headless";
	String HEATMAP = "heatmap";
	String HEIGHT = "height";
	String HER = "her";
	String HIGHLIGHT = "highlight";
	String HILL_CLIMBING = "hill_climbing";
	String HIS = "his";
	String HISTOGRAM = "histogram";
	String HOST = "host";
	String ID = "id";
	String IF = "if";
	String IGNORE = "ignore";
	String IMAGE = "image";
	String IN = "in";
	String INCLUDE = "include";
	String INDEX = "index";
	String INIT = "init";
	String INPUT = "input";
	String INSPECT = "inspect";
	String INTERNAL_FUNCTION = "internal_function";
	String IS = "is";
	String ISFOLDER = "is_folder";
	String IS_LIGHT_ON = "light";
	String ISNOT = "is_not";
	String ITEM = "item";
	String ITS = "its";
	String FRAGMENT = "fragment";
	String JAVA = "java";
	String KEEP = "keep";
	String KEEP_SEED = "keep_seed";
	String KEEP_SIMULATIONS = "keep_simulations";
	String KEY = "key";
	String KEYSTONE = "keystone";
	// public static final String KEYWORD = "keyword";
	String KILL = "kill";
	String OVERLAY = "overlay";
	String LAYOUT = "layout";
	String LEFT = "left";
	String LEGEND = "legend";
	String LET = "let";
	String LINE = "line";
	String LINES = "lines";
	String LINEAR_ATTENUATION = "linear_attenuation";
	String LOCATION = "location";
	String LOOP = "loop";
	String LOOK_AT = "look_at";
	String MAPPING = "mapping";
	String MATERIAL = "material";
	String MATCH = "match";
	String MATCH_BETWEEN = "match_between";
	String MATCH_ONE = "match_one";
	String MASK = "mask";
	String MAX = "max";
	String MAXIMIZE = "maximize";
	String MEMBERS = "members";
	String MEMORIZE = "memorize";
	String MERSENNE = "mersenne";
	String MESSAGE = "message";
	String MESH = "mesh";
	String METHOD = "method";
	String MICRO = "micro";
	String MICRO_LAYER = "micro_layer";
	String MIGRATE = "migrate";
	String MIN = "min";
	String MINIMIZE = "minimize";
	String MINUS = "-";
	String MINVALUE = "min_value";
	String MIRRORS = "mirrors";
	String MODE = "mode";
	String MODEL = "model";
	String MONITOR = "monitor";
	String MOUSE_DOWN = "mouse_down";
	String MOUSE_UP = "mouse_up";
	String MOUSE_CLICKED = "mouse_click";
	String MOUSE_MOVED = "mouse_move";
	String MOUSE_ENTERED = "mouse_enter";
	String MOUSE_EXITED = "mouse_exit";
	String MOUSE_MENU = "mouse_menu";
	String MOVING_SKILL = "moving";
	String MOVING_3D_SKILL = "moving3D";
	String MULTIPLY = "*";
	String MULTICORE = "multicore";
	String MY = "my";
	String MYGRAPH = "my_graph";
	String MYSELF = "myself";
	String NAME = "name";
	String NEIGHBOURS = "neighbours";
	String NEIGHBORS = "neighbors";
	String NODE = "node";
	String NO_INFO = "no_info";
	String NO_WARNING = "no_warning";
	String NO_EXPERIMENT = "no_experiment";
	String NULL = "nil";
	String NUMBER = "number";
	String NB_COLS = "nb_cols";
	String NB_ROWS = "nb_rows";
	String OF = "of";
	String ON = "on";
	String ON_CHANGE = "on_change";
	String OPEN_EXP = "(";
	String OPEN_LIST = "[";
	String OPEN_POINT = "{";
	String OPTIONAL = "optional";
	String ORTHOGRAPHIC_PROJECTION = "orthographic_projection";
	String OTHER_EVENTS = "other_events";
	String OUTPUT = "output";
	String OVER = "over";
	String OVERWRITE = "overwrite";
	String PARAMETER = "parameter";
	String PARAMS = "params";
	String PARENT = "parent";
	String PATH = "path";
	String PAUSE_SOUND = "pause_sound";
	String PEERS = "peers";
	String PERMANENT = "permanent"; // "show" // "front_end"
									// // "presentation" //
									// "
	String PARALLEL = "parallel";
	String PERSPECTIVE = "perspective";
	String PIE = "pie";
	String PITCH = "pitch";
	String PLACES = "places";
	String PLUS = "+";
	String POPULATION = "display_population";
	String POSITION = "position";
	String PRAGMA = "pragma";
	String PRIMITIVE = "primitive";
	String PRIORITY = "priority";
	String PROPAGATION = "propagation";
	String PROPORTION = "proportion";
	String PUT = "put";
	String QUADRATIC_ATTENUATION = "quadratic_attenuation";
	String QUADTREE = "quadtree";
	String RADAR = "radar";
	String RADIUS = "radius";
	String RAISES = "raises";
	String RANDOM_SPECIES_NAME = "random_builder";
	String RANGE = "range";
	String REACTIVE_TABU = "reactive_tabu";
	String READABLE = "readable";
	String REFLECTIVITY = "reflectivity";
	String REFLEX = "reflex";
	String REFRESH = "refresh";
	String REFRESH_EVERY = "refresh_every";
	String REGISTER = "register";
	String RELEASE = "release";
	String REMOTE = "remote";
	String REMOVE = "remove";
	String REPEAT = "repeat";
	String RESUME_SOUND = "resume_sound";
	String RETURN = "return";
	String RETURNS = "returns";
	String REVERSE_AXIS = "reverse_axes";
	String REWRITE = "rewrite";
	String RIGHT = "right";
	String RING = "ring";
	String RNG = "rng";
	String ROLL = "roll";
	String ROUNDED = "rounded";
	String ROTATE = "rotate";
	String SAVE = "save";
	String SAVE_BATCH = "save_batch";
	String SCALE = "scale";
	String SCATTER = "scatter";
	String SCHEDULE = "schedule";
	String SCHEDULES = "schedules";
	String SEED = "seed";
	String SEGMENTS = "segments";
	String SELECTABLE = "selectable";
	String SELF = "self";
	String SUPER = "super";
	String SERIES = "series";
	String SET = "set";
	String SHAPE = "shape";
	String SHOWFPS = "show_fps";
	String SIMULATION = "simulation";
	String SIMULATIONS = "simulations";
	String SIZE = "size";
	String SKILL = "skill";
	String SKILLS = "skills";
	String SOURCE = "source";
	String SPECIES = "species";
	String SPECULAR = "specular";
	String SPEED = "speed";
	String REAL_SPEED = "real_speed";
	String SPLINE = "spline";
	String SPOT_ANGLE = "spot_angle";
	String STACK = "stack";
	String START_SOUND = "start_sound";
	String STATE = "state";
	String STATES = "states";
	String STATUS = "status";
	String STEP = "step";
	String STOP_SOUND = "stop_sound";
	String STRATEGY = "scheduling_strategy";
	String STYLE = "style";
	String SWITCH = "switch";
	String SYNTHETIC = "__synthetic__";
	String TABU = "tabu";
	String TARGET = "target";
	String TARGETS = "scheduling_targets";
	String TESSELATION = "tesselation";
	String TEST = "test";
	String TEXT = "text";
	String TEXTURE = "texture";
	String THE = "the";
	String THEIR = "their";
	String THREE_D = "3d";
	String TIMES = "times";
	String TIME_SERIES = "time_series"; // hqnghi facet for
										// continuous Chart
	String TABLE = "table";

	String DESCRIPTION = "description";
	String PARAMETERS = "parameters";
	String TITLE = "title";
	String TO = "to";
	String TOPOLOGY = "topology";
	String TORUS = "torus";
	String TRACE = "trace";
	String TRANSPARENCY = "transparency";
	String TRIANGULATION = "triangulation";
	String TRUE = "true";
	String TRY = "try";
	String TYPE = "type";
	String UNIT = "unit";
	String UNTIL = "until";
	String UPDATE = "update";
	String UP_VECTOR = "up_vector";
	String USER_CONTROLLED = "user_controlled";
	String USER_COMMAND = "user_command";
	String USER_INPUT = "user_input";
	String USER_INPUT_DIALOG = "user_input_dialog";
	String USER_CONFIRM = "user_confirm";
	String WIZARD = "wizard";
	String WIZARD_PAGE = "wizard_page";
	String USER_ONLY = "user_only";
	String USER_FIRST = "user_first";
	String USER_LAST = "user_last";
	String USER_PANEL = "user_panel";
	String USING = "using";
	String VALUE = "value";
	String VALUES = "values";
	String VAR = "var";
	String VARIATION = "variation";
	String VARS = "vars";
	String VERSION = "version";
	String VERTEX = "vertex";
	String VIRTUAL = "virtual";
	String WARNING = "warn";
	String WARNING_TEST = "warning";
	String WEIGHT = "weight";
	String WHEN = "when";
	String WHISKER = "whisker"; // new type of datachart
	String WHILE = "while";
	String WIDTH = "width";
	String LIGHTED = "lighted";
	String WITH = "with";
	String WORLD_AGENT_NAME = "world";
	String WRITABLE = "writable";
	String WRITE = "write";
	String X = "x";
	String XML = "xml";
	// public static final String XOR = "xor";
	String XY = "xy";
	String X_LABELS = "x_serie_labels";
	String X_SERIE = "x_serie";
	String Y = "y";
	String Y_LABELS = "y_serie_labels";
	String Y_SERIE = "y_serie";
	String Z = "z";
	String ZFIGHTING = "z_fighting";
	String[] METHODS = { GENETIC, ANNEALING, HILL_CLIMBING, TABU, REACTIVE_TABU, EXHAUSTIVE };
	String[] EVENT_TYPE = { OTHER_EVENTS, MOUSE_DOWN };
	String USER_INIT = "user_init";
	// public static final String AS_SKILL = "as_skill";
	String IS_SKILL = "is_skill";

	/**
	 * TYPES
	 */
	String LIST = "list";
	String MAP = "map";
	String BOOL = "bool";
	String FLOAT = "float";
	String INT = "int";
	String STRING = "string";
	String POINT = "point";
	String PAIR = "pair";
	String UNKNOWN = "unknown";
	String MATRIX = "matrix";
	String RGB = "rgb";
	String CONTAINER = "container";
	String GEOMETRY = "geometry";

	/*
	 * files
	 */
	String OPEN = "open"; // TODO "launch", or "open", or
							// "sysopen" ? This opens a file
							// with an
	// external progam
	String ALPHA = "alpha";

	String INTERNAL = "_internal_";

	String SYNTHETIC_RESOURCES_PREFIX = "__synthetic__";
	String PLATFORM = "platform";
	String INVOKE = "invoke";
	String TOOLBAR = "toolbar";
	String SMOOTH = "smooth";
}
