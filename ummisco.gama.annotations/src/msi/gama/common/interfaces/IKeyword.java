/*******************************************************************************************************
 *
 * IKeyword.java, in ummisco.gama.annotations, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2021 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.common.interfaces;

/**
 * The class IKeyword. Defines most of the keywords used in GAMA and GAML.
 *
 * @author drogoul
 * @since 13 dec. 2011
 *
 */
public interface IKeyword {

	/** The dot. */
	String _DOT = ".";

	/** The abort. */
	String ABORT = "abort";

	/** The action. */
	String ACTION = "action";

	/** The add. */
	String ADD = "add";

	/** The agent. */
	String AGENT = "agent";

	/** The agents. */
	String AGENTS = "agents";

	/** The aggregation. */
	String AGGREGATION = "aggregation";

	/** The all. */
	String ALL = "all";

	/** The anchor. */
	String ANCHOR = "anchor";

	/** The ambient light. */
	String AMBIENT_LIGHT = "ambient_light";

	/** The among. */
	String AMONG = "among";

	/** The annealing. */
	String ANNEALING = "annealing";

	/** The append horizontally. */
	String APPEND_HORIZONTALLY = "append_horizontally";

	/** The append vertically. */
	String APPEND_VERTICALLY = "append_vertically";

	/** The area. */
	String AREA = "area";

	/** The area stack. */
	String AREA_STACK = "area_stack";

	/** The arg. */
	String ARG = "arg";

	/** The as. */
	String AS = "as";

	/** The ask. */
	String ASK = "ask";

	/** The aspect. */
	String ASPECT = "aspect";

	/** The asset3d. */
	String ASSET3D = "asset3D";

	/** The at. */
	String AT = "at";

	/** The attributes. */
	String ATTRIBUTES = "attributes";

	/** The author. */
	String AUTHOR = "author";

	/** The autosave. */
	String AUTOSAVE = "autosave";

	/** The autorun. */
	String AUTORUN = "autorun";

	/** The avoid mask. */
	String AVOID_MASK = "avoid_mask";

	/** The axes. */
	String AXES = "axes";

	/** The background. */
	String BACKGROUND = "background";

	/** The bar. */
	String BAR = "bar";

	/** The box whisker. */
	String BOX_WHISKER = "box_whisker"; // new type of
										// chart: box and
	/** The batch. */
	// whisker
	String BATCH = "batch";
	
	/** The batch outputs */
	String BATCH_OUTPUTS = "outputs";

	/** The behavior. */
	String BEHAVIOR = "behavior";

	/** The behaviors. */
	String BEHAVIORS = "behaviors";

	/** The benchmark. */
	// public static final String BITMAP = "bitmap";
	String BENCHMARK = "benchmark";

	/** The bounds. */
	String BOUNDS = "bounds";

	/** The break. */
	String BREAK = "break";

	/** The brighter. */
	String BRIGHTER = "brighter";

	/** The browse. */
	String BROWSE = "browse";

	/** The camera. */
	String CAMERA = "camera";

	/** The camera location. */
	String CAMERA_LOCATION = "camera_location";

	/** The camera target. */
	String CAMERA_TARGET = "camera_target";

	/** The camera orientation. */
	String CAMERA_ORIENTATION = "camera_orientation";

	/** The camera lens. */
	String CAMERA_LENS = "camera_lens";

	/** The camera interaction. */
	String CAMERA_INTERACTION = "camera_interaction";

	/** The capture. */
	String CAPTURE = "capture";

	/** The catch. */
	String CATCH = "catch";

	/** The category. */
	String CATEGORY = "category";

	/** The cell width. */
	String CELL_WIDTH = "cell_width";

	/** The cell height. */
	String CELL_HEIGHT = "cell_height";

	/** The cellular. */
	String CELLULAR = "cellular";

	/** The center. */
	String CENTER = "center";

	/** The chain. */
	String CHAIN = "chain";

	/** The chart. */
	String CHART = "chart";

	/** The choose. */
	String CHOOSE = "choose";

	/** The close exp. */
	String CLOSE_EXP = ")";

	/** The close list. */
	String CLOSE_LIST = "]";

	/** The close point. */
	String CLOSE_POINT = "}";

	/** The color. */
	String COLOR = "color";

	/** The color blue. */
	String COLOR_BLUE = "blue";

	/** The color green. */
	String COLOR_GREEN = "green";

	/** The color red. */
	String COLOR_RED = "red";

	/** The comma. */
	String COMMA = ",";

	/** The condition. */
	String CONDITION = "condition";

	/** The const. */
	String CONST = "const";

	/** The contents. */
	String CONTENTS = "contents";

	/** The contribute. */
	String CONTRIBUTE = "contribute";

	/** The control. */
	String CONTROL = "control";

	/** The convolution. */
	String CONVOLUTION = "convolution";

	/** The continue. */
	String CONTINUE = "continue";

	/** The create. */
	String CREATE = "create";

	/** The csv. */
	String CSV = "csv";

	/** The current state. */
	String CURRENT_STATE = "currentState";

	/** The damper. */
	String DAMPER = "damper";

	/** The darker. */
	String DARKER = "darker";

	/** The data. */
	String DATA = "data";

	/** The decay. */
	String DECAY = "decay";

	/** The default. */
	String DEFAULT = "default";
	// public static final String DEFAULT_EXP = "default";
	// public static final String DEM = "dem";
	/** The depth. */
	// public static final String DEPENDS_ON = "depends_on";
	String DEPTH = "depth";

	/** The destination. */
	String DESTINATION = "destination";

	/** The diffusion. */
	String DIFFUSION = "diffusion";

	/** The diffuse. */
	String DIFFUSE = "diffuse";

	/** The diffuse light. */
	String DIFFUSE_LIGHT = "diffuse_light";

	/** The diffuse light pos. */
	String DIFFUSE_LIGHT_POS = "diffuse_light_pos";

	/** The direction. */
	String DIRECTION = "direction";

	/** The display. */
	String DISPLAY = "display";

	/** The display gl. */
	String DISPLAY_GL = "graphdisplaygl";

	/** The display graph. */
	String DISPLAY_GRAPH = "graphdisplay";

	/** The distance cache. */
	String DISTANCE_CACHE = "distance_cache_enabled";

	/** The divide. */
	String DIVIDE = "/";

	/** The do. */
	String DO = "do";

	/** The dot. */
	String DOT = "dot";

	/** The draw. */
	String DRAW = "draw";

	/** The drawenv. */
	String DRAWENV = "draw_env";

	/** The draw diffuse light. */
	String DRAW_DIFFUSE_LIGHT = "draw_diffuse_light";

	/** The draw light. */
	String DRAW_LIGHT = "draw_light";

	/** The elevation. */
	String ELEVATION = "elevation";

	/** The dynamic. */
	String DYNAMIC = "dynamic";

	/** The each. */
	String EACH = "each";

	/** The edge. */
	String EDGE = "edge";

	/** The edge species. */
	String EDGE_SPECIES = "edge_species";

	/** The else. */
	String ELSE = "else";

	/** The empty. */
	String EMPTY = "empty";

	/** The wireframe. */
	String WIREFRAME = "wireframe";

	/** The border. */
	String BORDER = "border";

	/** The enter. */
	String ENTER = "enter";

	/** The entities. */
	String ENTITIES = "entities";

	/** The environment. */
	String ENVIRONMENT = "environment";

	/** The equals. */
	String EQUALS = "equals";

	/** The gama. */
	String GAMA = "gama";

	/** The equation op. */
	/*
	 * Equations
	 */
	String EQUATION_OP = "=";

	/** The equation left. */
	String EQUATION_LEFT = "left";

	/** The equation right. */
	String EQUATION_RIGHT = "right";

	/** The equation. */
	String EQUATION = "equation";

	/** The simultaneously. */
	String SIMULTANEOUSLY = "simultaneously";

	/** The solver. */
	String SOLVER = "solver";

	/** The solve. */
	String SOLVE = "solve";

	/** The time initial. */
	String TIME_INITIAL = "t0";

	/** The time final. */
	String TIME_FINAL = "tf";

	/** The cycle length. */
	String CYCLE_LENGTH = "cycle_length";

	/** The diff. */
	String DIFF = "diff";

	/** The dif2. */
	String DIF2 = "diff2";

	/** The zero. */
	String ZERO = "internal_zero_order_equation";

	/** The enables. */
	/*
	 *
	 */
	String ENABLES = "enables";

	/** The disables. */
	String DISABLES = "disables";

	/** The event. */
	String EVENT = "event";

	/** The error. */
	String ERROR = "error";

	/** The exhaustive. */
	String EXHAUSTIVE = "exhaustive";

	/** The exists. */
	String EXISTS = "exists";
	
	/** The explicit. */
	String EXPLICIT = "explicit";

	/** The experiment. */
	String EXPERIMENT = "experiment";

	/** The exploded. */
	// public static final String EXPERIMENTATOR = "experimentator";
	String EXPLODED = "exploded";

	/** The extension. */
	// public static final String EXPORT = "export";
	String EXTENSION = "extension";

	/** The fading. */
	String FADING = "fading";

	/** The false. */
	String FALSE = "false";

	/** The file. */
	String FILE = "file";

	/** The folder. */
	String FOLDER = "folder";

	/** The directory. */
	String DIRECTORY = "directory";

	/** The files. */
	String FILES = "files";

	/** The output file. */
	String OUTPUT_FILE = "output_file";

	/** The fill with. */
	String FILL_WITH = "fill_with";

	/** The field. */
	String FIELD = "field";
	
	String FITNESS = "fitness";

	/** The focus. */
	String FOCUS = "focus";

	/** The focus on. */
	String FOCUS_ON = "focus_on";

	/** The font. */
	String FONT = "font";

	/** The footer. */
	String FOOTER = "footer";

	/** The framerate. */
	String FRAMERATE = "framerate";

	/** The frequency. */
	String FREQUENCY = "frequency";

	/** The from. */
	String FROM = "from";

	/** The fsm. */
	String FSM = "fsm";

	/** The function. */
	String FUNCTION = "function";

	/** The fullscreen. */
	String FULLSCREEN = "fullscreen";

	/** The gap. */
	String GAP = "gap";

	/** The generate. */
	String GENERATE = "generate";

	/** The genetic. */
	String GENETIC = "genetic";

	/** The gis. */
	String GIS = "gis";

	/** The global. */
	String GLOBAL = "global";

	/** The gradient. */
	String GRADIENT = "gradient";

	/** The graph. */
	String GRAPH = "graph";

	/** The graph skill. */
	String GRAPH_SKILL = "graph_user";

	/** The graphic skill. */
	String GRAPHIC_SKILL = "graphic";

	/** The graphics. */
	String GRAPHICS = "graphics";

	/** The grayscale. */
	String GRAYSCALE = "grayscale";

	/** The grid. */
	String GRID = "grid";

	/** The grid population. */
	String GRID_POPULATION = "display_grid";

	/** The grid x. */
	String GRID_X = "grid_x";

	/** The grid y. */
	String GRID_Y = "grid_y";

	/** The grid value. */
	String GRID_VALUE = "grid_value";

	/** The bands. */
	String BANDS = "bands";

	/** The group. */
	String GROUP = "group";

	/** The gui. */
	String GUI_ = "gui";

	/** The header. */
	String HEADER = "header";

	/** The heading. */
	String HEADING = "heading";

	/** The headless ui. */
	String HEADLESS_UI = "headless";

	/** The heatmap. */
	String HEATMAP = "heatmap";

	/** The height. */
	String HEIGHT = "height";

	/** The her. */
	String HER = "her";

	/** The highlight. */
	String HIGHLIGHT = "highlight";

	/** The hill climbing. */
	String HILL_CLIMBING = "hill_climbing";

	/** The his. */
	String HIS = "his";

	/** The histogram. */
	String HISTOGRAM = "histogram";

	/** The host. */
	String HOST = "host";

	/** The id. */
	String ID = "id";

	/** The if. */
	String IF = "if";

	/** The ignore. */
	String IGNORE = "ignore";

	/** The image. */
	String IMAGE = "image";

	/** The in. */
	String IN = "in";

	/** The include. */
	String INCLUDE = "include";

	/** The index. */
	String INDEX = "index";

	/** The init. */
	String INIT = "init";

	/** The input. */
	String INPUT = "input";

	/** The inspect. */
	String INSPECT = "inspect";

	/** The internal function. */
	String INTERNAL_FUNCTION = "internal_function";

	/** The is. */
	String IS = "is";

	/** The isfolder. */
	String ISFOLDER = "is_folder";

	/** The is light on. */
	String IS_LIGHT_ON = "light";

	/** The isnot. */
	String ISNOT = "is_not";

	/** The item. */
	String ITEM = "item";

	/** The its. */
	String ITS = "its";

	/** The fragment. */
	String FRAGMENT = "fragment";

	/** The java. */
	String JAVA = "java";

	/** The keep. */
	String KEEP = "keep";

	/** The keep seed. */
	String KEEP_SEED = "keep_seed";

	/** The keep simulations. */
	String KEEP_SIMULATIONS = "keep_simulations";

	/** The key. */
	String KEY = "key";

	/** The keystone. */
	String KEYSTONE = "keystone";

	/** The kill. */
	// public static final String KEYWORD = "keyword";
	String KILL = "kill";

	/** The overlay. */
	String OVERLAY = "overlay";

	/** The layout. */
	String LAYOUT = "layout";

	/** The left. */
	String LEFT = "left";

	/** The legend. */
	String LEGEND = "legend";

	/** The let. */
	String LET = "let";

	/** The line. */
	String LINE = "line";

	/** The lines. */
	String LINES = "lines";

	/** The linear attenuation. */
	String LINEAR_ATTENUATION = "linear_attenuation";

	/** The location. */
	String LOCATION = "location";

	/** The loop. */
	String LOOP = "loop";

	/** The look at. */
	String LOOK_AT = "look_at";

	/** The mapping. */
	String MAPPING = "mapping";

	/** The material. */
	String MATERIAL = "material";

	/** The match. */
	String MATCH = "match";

	/** The match between. */
	String MATCH_BETWEEN = "match_between";

	/** The match one. */
	String MATCH_ONE = "match_one";

	/** The match regex. */
	String MATCH_REGEX = "match_regex";

	/** The mask. */
	String MASK = "mask";

	/** The max. */
	String MAX = "max";

	/** The maximize. */
	String MAXIMIZE = "maximize";

	/** The members. */
	String MEMBERS = "members";

	/** The memorize. */
	String MEMORIZE = "memorize";

	/** The mersenne. */
	String MERSENNE = "mersenne";

	/** The message. */
	String MESSAGE = "message";

	/** The mesh. */
	String MESH = "mesh";

	/** The method. */
	String METHOD = "method";

	/** The micro. */
	String MICRO = "micro";

	/** The micro layer. */
	String MICRO_LAYER = "micro_layer";

	/** The migrate. */
	String MIGRATE = "migrate";

	/** The min. */
	String MIN = "min";

	/** The minimize. */
	String MINIMIZE = "minimize";

	/** The minus. */
	String MINUS = "-";

	/** The minvalue. */
	String MINVALUE = "min_value";

	/** The mirrors. */
	String MIRRORS = "mirrors";

	/** The mode. */
	String MODE = "mode";

	/** The model. */
	String MODEL = "model";

	/** The monitor. */
	String MONITOR = "monitor";

	/** The mouse down. */
	String MOUSE_DOWN = "mouse_down";

	/** The mouse up. */
	String MOUSE_UP = "mouse_up";

	/** The mouse clicked. */
	String MOUSE_CLICKED = "mouse_click";

	/** The mouse moved. */
	String MOUSE_MOVED = "mouse_move";

	/** The mouse entered. */
	String MOUSE_ENTERED = "mouse_enter";

	/** The mouse exited. */
	String MOUSE_EXITED = "mouse_exit";

	/** The mouse menu. */
	String MOUSE_MENU = "mouse_menu";

	/** The moving skill. */
	String MOVING_SKILL = "moving";

	/** The moving 3d skill. */
	String MOVING_3D_SKILL = "moving3D";

	/** The multiply. */
	String MULTIPLY = "*";

	/** The multicore. */
	String MULTICORE = "multicore";

	/** The my. */
	String MY = "my";

	/** The mygraph. */
	String MYGRAPH = "my_graph";

	/** The myself. */
	String MYSELF = "myself";

	/** The name. */
	String NAME = "name";

	/** The neighbours. */
	String NEIGHBOURS = "neighbours";

	/** The neighbors. */
	String NEIGHBORS = "neighbors";

	/** The node. */
	String NODE = "node";

	/** The no info. */
	String NO_INFO = "no_info";

	/** The no warning. */
	String NO_WARNING = "no_warning";

	/** The no experiment. */
	String NO_EXPERIMENT = "no_experiment";

	/** The null. */
	String NULL = "nil";

	/** The number. */
	String NUMBER = "number";

	/** The nb cols. */
	String NB_COLS = "nb_cols";

	/** The nb rows. */
	String NB_ROWS = "nb_rows";

	/** The of. */
	String OF = "of";

	/** The on. */
	String ON = "on";

	/** The on change. */
	String ON_CHANGE = "on_change";

	/** The open exp. */
	String OPEN_EXP = "(";

	/** The open list. */
	String OPEN_LIST = "[";

	/** The open point. */
	String OPEN_POINT = "{";

	/** The optional. */
	String OPTIONAL = "optional";

	/** The orthographic projection. */
	String ORTHOGRAPHIC_PROJECTION = "orthographic_projection";

	/** The other events. */
	String OTHER_EVENTS = "other_events";

	/** The output. */
	String OUTPUT = "output";

	/** The over. */
	String OVER = "over";

	/** The overwrite. */
	String OVERWRITE = "overwrite";

	/** The parameter. */
	String PARAMETER = "parameter";

	/** The params. */
	String PARAMS = "params";

	/** The parent. */
	String PARENT = "parent";

	/** The path. */
	String PATH = "path";

	/** The pause sound. */
	String PAUSE_SOUND = "pause_sound";

	/** The peers. */
	String PEERS = "peers";

	/** The permanent. */
	String PERMANENT = "permanent"; // "show" // "front_end"
									// // "presentation" //
	/** The parallel. */
	// "
	String PARALLEL = "parallel";

	/** The perspective. */
	String PERSPECTIVE = "perspective";

	/** The pie. */
	String PIE = "pie";

	/** The pitch. */
	String PITCH = "pitch";

	/** The places. */
	String PLACES = "places";

	/** The plus. */
	String PLUS = "+";

	/** The population. */
	String POPULATION = "display_population";

	/** The position. */
	String POSITION = "position";

	/** The pragma. */
	String PRAGMA = "pragma";

	/** The primitive. */
	String PRIMITIVE = "primitive";

	/** The priority. */
	String PRIORITY = "priority";

	/** The propagation. */
	String PROPAGATION = "propagation";

	/** The proportion. */
	String PROPORTION = "proportion";

	/** The pso. */
	String PSO = "pso";
	
	/** The put. */
	String PUT = "put";

	/** The quadratic attenuation. */
	String QUADRATIC_ATTENUATION = "quadratic_attenuation";

	/** The quadtree. */
	String QUADTREE = "quadtree";

	/** The radar. */
	String RADAR = "radar";

	/** The radius. */
	String RADIUS = "radius";

	/** The raises. */
	String RAISES = "raises";

	/** The random species name. */
	String RANDOM_SPECIES_NAME = "random_builder";

	/** The range. */
	String RANGE = "range";

	/** The reactive tabu. */
	String REACTIVE_TABU = "reactive_tabu";

	/** The readable. */
	String READABLE = "readable";

	/** The reflectivity. */
	String REFLECTIVITY = "reflectivity";

	/** The reflex. */
	String REFLEX = "reflex";

	/** The refresh. */
	String REFRESH = "refresh";

	/** The refresh every. */
	String REFRESH_EVERY = "refresh_every";

	/** The register. */
	String REGISTER = "register";

	/** The release. */
	String RELEASE = "release";

	/** The remote. */
	String REMOTE = "remote";

	/** The remove. */
	String REMOVE = "remove";

	/** The repeat. */
	String REPEAT = "repeat";

	/** The resume sound. */
	String RESUME_SOUND = "resume_sound";

	/** The return. */
	String RETURN = "return";

	/** The returns. */
	String RETURNS = "returns";

	/** The reverse axis. */
	String REVERSE_AXIS = "reverse_axes";

	/** The rewrite. */
	String REWRITE = "rewrite";

	/** The right. */
	String RIGHT = "right";

	/** The ring. */
	String RING = "ring";

	/** The rng. */
	String RNG = "rng";

	/** The roll. */
	String ROLL = "roll";

	/** The rounded. */
	String ROUNDED = "rounded";

	/** The rotate. */
	String ROTATE = "rotate";

	/** The save. */
	String SAVE = "save";

	/** The save batch. */
	String SAVE_BATCH = "save_batch";

	/** The scale. */
	String SCALE = "scale";

	/** The scatter. */
	String SCATTER = "scatter";

	/** The schedule. */
	String SCHEDULE = "schedule";

	/** The schedules. */
	String SCHEDULES = "schedules";

	/** The seed. */
	String SEED = "seed";

	/** The segments. */
	String SEGMENTS = "segments";

	/** The selectable. */
	String SELECTABLE = "selectable";

	/** The self. */
	String SELF = "self";

	/** The super. */
	String SUPER = "super";

	/** The series. */
	String SERIES = "series";

	/** The set. */
	String SET = "set";

	/** The shape. */
	String SHAPE = "shape";

	/** The showfps. */
	String SHOWFPS = "show_fps";

	/** The simulation. */
	String SIMULATION = "simulation";

	/** The simulations. */
	String SIMULATIONS = "simulations";

	/** The size. */
	String SIZE = "size";

	/** The skill. */
	String SKILL = "skill";

	/** The skills. */
	String SKILLS = "skills";

	/** The sobol exploration method */
	String SOBOL = "sobol";
	
	/** The source. */
	String SOURCE = "source";

	/** The species. */
	String SPECIES = "species";

	/** The specular. */
	String SPECULAR = "specular";

	/** The speed. */
	String SPEED = "speed";

	/** The real speed. */
	String REAL_SPEED = "real_speed";

	/** The spline. */
	String SPLINE = "spline";

	/** The spot angle. */
	String SPOT_ANGLE = "spot_angle";

	/** The stack. */
	String STACK = "stack";

	/** The start sound. */
	String START_SOUND = "start_sound";

	/** The state. */
	String STATE = "state";

	/** The states. */
	String STATES = "states";

	/** The status. */
	String STATUS = "status";

	/** The step. */
	String STEP = "step";

	/** The stop sound. */
	String STOP_SOUND = "stop_sound";

	/** The strategy. */
	String STRATEGY = "scheduling_strategy";

	/** The style. */
	String STYLE = "style";

	/** The switch. */
	String SWITCH = "switch";

	/** The synthetic. */
	String SYNTHETIC = "__synthetic__";

	/** The tabu. */
	String TABU = "tabu";

	/** The target. */
	String TARGET = "target";

	/** The targets. */
	String TARGETS = "scheduling_targets";

	/** The tesselation. */
	String TESSELATION = "tesselation";

	/** The test. */
	String TEST = "test";

	/** The text. */
	String TEXT = "text";

	/** The texture. */
	String TEXTURE = "texture";

	/** The the. */
	String THE = "the";

	/** The their. */
	String THEIR = "their";

	/** The three d. */
	String THREE_D = "3d";

	/** The times. */
	String TIMES = "times";

	/** The time series. */
	String TIME_SERIES = "time_series"; // hqnghi facet for

	/** The table. */
	// continuous Chart
	String TABLE = "table";

	/** The description. */
	String DESCRIPTION = "description";

	/** The parameters. */
	String PARAMETERS = "parameters";

	/** The title. */
	String TITLE = "title";

	/** The to. */
	String TO = "to";

	/** The topology. */
	String TOPOLOGY = "topology";

	/** The torus. */
	String TORUS = "torus";

	/** The trace. */
	String TRACE = "trace";

	/** The transparency. */
	String TRANSPARENCY = "transparency";

	/** The triangulation. */
	String TRIANGULATION = "triangulation";

	/** The true. */
	String TRUE = "true";

	/** The try. */
	String TRY = "try";

	/** The type. */
	String TYPE = "type";

	/** The unit. */
	String UNIT = "unit";

	/** The until. */
	String UNTIL = "until";

	/** The update. */
	String UPDATE = "update";

	/** The up vector. */
	String UP_VECTOR = "up_vector";

	/** The user controlled. */
	String USER_CONTROLLED = "user_controlled";

	/** The user command. */
	String USER_COMMAND = "user_command";

	/** The user input. */
	String USER_INPUT = "user_input";

	/** The user input dialog. */
	String USER_INPUT_DIALOG = "user_input_dialog";

	/** The user confirm. */
	String USER_CONFIRM = "user_confirm";

	/** The wizard. */
	String WIZARD = "wizard";

	/** The wizard page. */
	String WIZARD_PAGE = "wizard_page";

	/** The user only. */
	String USER_ONLY = "user_only";

	/** The user first. */
	String USER_FIRST = "user_first";

	/** The user last. */
	String USER_LAST = "user_last";

	/** The user panel. */
	String USER_PANEL = "user_panel";

	/** The using. */
	String USING = "using";

	/** The value. */
	String VALUE = "value";

	/** The values. */
	String VALUES = "values";

	/** The var. */
	String VAR = "var";

	/** The variation. */
	String VARIATION = "variation";

	/** The vars. */
	String VARS = "vars";

	/** The version. */
	String VERSION = "version";

	/** The vertex. */
	String VERTEX = "vertex";

	/** The virtual. */
	String VIRTUAL = "virtual";

	/** The visible. */
	String VISIBLE = "visible";

	/** The warning. */
	String WARNING = "warn";

	/** The warning test. */
	String WARNING_TEST = "warning";

	/** The weight. */
	String WEIGHT = "weight";

	/** The when. */
	String WHEN = "when";

	/** The whisker. */
	String WHISKER = "whisker"; // new type of datachart

	/** The while. */
	String WHILE = "while";

	/** The width. */
	String WIDTH = "width";

	/** The lighted. */
	String LIGHTED = "lighted";

	/** The with. */
	String WITH = "with";

	/** The world agent name. */
	String WORLD_AGENT_NAME = "world";

	/** The writable. */
	String WRITABLE = "writable";

	/** The write. */
	String WRITE = "write";

	/** The x. */
	String X = "x";

	/** The xml. */
	String XML = "xml";

	/** The xy. */
	// public static final String XOR = "xor";
	String XY = "xy";

	/** The x labels. */
	String X_LABELS = "x_serie_labels";

	/** The x serie. */
	String X_SERIE = "x_serie";

	/** The y. */
	String Y = "y";

	/** The y labels. */
	String Y_LABELS = "y_serie_labels";

	/** The y serie. */
	String Y_SERIE = "y_serie";

	/** The z. */
	String Z = "z";

	/** The zfighting. */
	String ZFIGHTING = "z_fighting";

	/** The methods. */
	String[] METHODS = { GENETIC, ANNEALING, HILL_CLIMBING, TABU, REACTIVE_TABU, EXHAUSTIVE, PSO, EXPLICIT, SOBOL };

	/** The event type. */
	String[] EVENT_TYPE = { OTHER_EVENTS, MOUSE_DOWN };

	/** The user init. */
	String USER_INIT = "user_init";

	/** The is skill. */
	// public static final String AS_SKILL = "as_skill";
	String IS_SKILL = "is_skill";

	/**
	 * TYPES
	 */
	String LIST = "list";

	/** The map. */
	String MAP = "map";

	/** The bool. */
	String BOOL = "bool";

	/** The float. */
	String FLOAT = "float";

	/** The int. */
	String INT = "int";

	/** The string. */
	String STRING = "string";

	/** The point. */
	String POINT = "point";

	/** The pair. */
	String PAIR = "pair";

	/** The unknown. */
	String UNKNOWN = "unknown";

	/** The matrix. */
	String MATRIX = "matrix";

	/** The rgb. */
	String RGB = "rgb";

	/** The container. */
	String CONTAINER = "container";

	/** The geometry. */
	String GEOMETRY = "geometry";

	/** The open. */
	/*
	 * files
	 */
	String OPEN = "open"; // TODO "launch", or "open", or
							// "sysopen" ? This opens a file
							// with an
	/** The alpha. */
	// external progam
	String ALPHA = "alpha";

	/** The internal. */
	String INTERNAL = "_internal_";

	/** The synthetic resources prefix. */
	String SYNTHETIC_RESOURCES_PREFIX = "__synthetic__";

	/** The platform. */
	String PLATFORM = "platform";

	/** The invoke. */
	String INVOKE = "invoke";

	/** The toolbar. */
	String TOOLBAR = "toolbar";

	/** The smooth. */
	String SMOOTH = "smooth";
}
