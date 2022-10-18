/*******************************************************************************************************
 *
 * IKeyword.java, in ummisco.gama.annotations, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
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

	/** The alpha. */
	String ALPHA = "alpha";

	/** The among. */
	String AMONG = "among";

	/** The anchor. */
	String ANCHOR = "anchor";

	/** The angle. */
	String ANGLE = "angle";

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

	/** The at. */
	String AT = "at";

	/** The attributes. */
	String ATTRIBUTES = "attributes";

	/** The author. */
	String AUTHOR = "author";

	/** The autorun. */
	String AUTORUN = "autorun";

	/** The autosave. */
	String AUTOSAVE = "autosave";

	/** The avoid mask. */
	String AVOID_MASK = "avoid_mask";

	/** The axes. */
	String AXES = "axes";

	/** The background. */
	String BACKGROUND = "background";

	/** The bands. */
	String BANDS = "bands";

	/** The bar. */
	String BAR = "bar";

	/** The batch. */
	String BATCH = "batch";

	/** The batch outputs */
	String BATCH_OUTPUT = "results";

	/** The batch outputs */
	String BATCH_REPORT = "report";

	/** The batch outputs */
	String BATCH_VAR_OUTPUTS = "outputs";

	/** The behavior. */
	String BEHAVIOR = "behavior";

	/** The benchmark. */
	String BENCHMARK = "benchmark";

	/** The beta^d coefficient */
	String BETAD = "betad";

	/** The bool. */
	String BOOL = "bool";

	/** The border. */
	String BORDER = "border";

	/** The bounds. */
	String BOUNDS = "bounds";

	/** The box whisker. */
	String BOX_WHISKER = "box_whisker";

	/** The break. */
	String BREAK = "break";

	/** The brighter. */
	String BRIGHTER = "brighter";

	/** The browse. */
	String BROWSE = "browse";

	/** The camera. */
	String CAMERA = "camera";

	/** The capture. */
	String CAPTURE = "capture";

	/** The catch. */
	String CATCH = "catch";

	/** The category. */
	String CATEGORY = "category";

	/** The cell height. */
	String CELL_HEIGHT = "cell_height";

	/** The cell width. */
	String CELL_WIDTH = "cell_width";

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

	/** The container. */
	String CONTAINER = "container";

	/** The contents. */
	String CONTENTS = "contents";

	/** The continue. */
	String CONTINUE = "continue";

	/** The control. */
	String CONTROL = "control";

	/** The convolution. */
	String CONVOLUTION = "convolution";

	/** The create. */
	String CREATE = "create";

	/** The csv. */
	String CSV = "csv";

	/** The current state. */
	String CURRENT_STATE = "currentState";

	/** The cycle length. */
	String CYCLE_LENGTH = "cycle_length";

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

	/** The depth. */
	String DEPTH = "depth";

	/** The description. */
	String DESCRIPTION = "description";

	/** The destination. */
	String DESTINATION = "destination";

	/** The dif2. */
	String DIF2 = "diff2";

	/** The diff. */
	String DIFF = "diff";

	/** The diffuse. */
	String DIFFUSE = "diffuse";

	/** The diffusion. */
	String DIFFUSION = "diffusion";

	/** The direction. */
	String DIRECTION = "direction";

	/** The directory. */
	String DIRECTORY = "directory";

	/** The disables. */
	String DISABLES = "disables";

	/** The display. */
	String DISPLAY = "display";

	/** The divide. */
	String DIVIDE = "/";

	/** The do. */
	String DO = "do";

	/** The dot. */
	String DOT = "dot";

	/** The draw. */
	String DRAW = "draw";

	/** The dynamic. */
	String DYNAMIC = "dynamic";

	/** The each. */
	String EACH = "each";

	/** The edge. */
	String EDGE = "edge";

	/** The edge species. */
	String EDGE_SPECIES = "edge_species";

	/** The elevation. */
	String ELEVATION = "elevation";

	/** The else. */
	String ELSE = "else";

	/** The empty. */
	String EMPTY = "empty";

	/** The enables. */
	/*
	 *
	 */
	String ENABLES = "enables";

	/** The enter. */
	String ENTER = "enter";

	/** The entities. */
	String ENTITIES = "entities";

	/** The environment. */
	String ENVIRONMENT = "environment";

	/** The equals. */
	String EQUALS = "equals";

	/** The equation. */
	String EQUATION = "equation";

	/** The equation left. */
	String EQUATION_LEFT = "left";

	/** The equation op. */
	/*
	 * Equations
	 */
	String EQUATION_OP = "=";

	/** The equation right. */
	String EQUATION_RIGHT = "right";

	/** The error. */
	String ERROR = "error";

	/** The event. */
	String EVENT = "event";

	/** The exists. */
	String EXISTS = "exists";

	/** The experiment. */
	String EXPERIMENT = "experiment";

	/** The exploration. */
	String EXPLORATION = "exploration";

	/** The exploded. */
	String EXPLODED = "exploded";

	/** The extension. */
	String EXTENSION = "extension";

	/** The extensions. */
	String EXTENSIONS = "extensions";
	
	/** Factorial sampling */
	String FACTORIAL = "factorial";

	/** The fading. */
	String FADING = "fading";

	/** The false. */
	String FALSE = "false";

	/** The field. */
	String FIELD = "field";

	/** The file. */
	String FILE = "file";

	/** The files. */
	String FILES = "files";

	/** The fill with. */
	String FILL_WITH = "fill_with";

	/** The fitness. */
	String FITNESS = "fitness";

	/** The float. */
	String FLOAT = "float";

	/** The focus. */
	String FOCUS = "focus";

	/** The focus on. */
	String FOCUS_ON = "focus_on";

	/** The folder. */
	String FOLDER = "folder";

	/** The font. */
	String FONT = "font";

	/** The footer. */
	String FOOTER = "footer";

	/** The fragment. */
	String FRAGMENT = "fragment";

	/** The framerate. */
	String FRAMERATE = "framerate";

	/** The frequency. */
	String FREQUENCY = "frequency";

	/** The from. */
	String FROM = "from";

	/** The fsm. */
	String FSM = "fsm";

	/** The fullscreen. */
	String FULLSCREEN = "fullscreen";

	/** The function. */
	String FUNCTION = "function";

	/** The gama. */
	String GAMA = "gama";

	/** The gap. */
	String GAP = "gap";

	/** The generate. */
	String GENERATE = "generate";

	/** The genetic. */
	String GENETIC = "genetic";

	/** The geometry. */
	String GEOMETRY = "geometry";

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

	/** The grid value. */
	String GRID_VALUE = "grid_value";

	/** The grid x. */
	String GRID_X = "grid_x";

	/** The grid y. */
	String GRID_Y = "grid_y";

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

	/** The int. */
	String INT = "int";

	/** The internal. */
	String INTERNAL = "_internal_";

	/** The internal function. */
	String INTERNAL_FUNCTION = "internal_function";

	/** The invoke. */
	String INVOKE = "invoke";

	/** The is. */
	String IS = "is";

	/** The is light on. */
	String IS_LIGHT_ON = "light";

	/** The is skill. */
	String IS_SKILL = "is_skill";

	/** The isfolder. */
	String ISFOLDER = "is_folder";

	/** The isnot. */
	String ISNOT = "is_not";

	/** The item. */
	String ITEM = "item";

	/** The its. */
	String ITS = "its";

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
	String KILL = "kill";

	/** The layout. */
	String LAYOUT = "layout";

	/** The left. */
	String LEFT = "left";

	/** The legend. */
	String LEGEND = "legend";

	/** The let. */
	String LET = "let";

	/** Latin Hypercube Sampling */
	String LHS = "latinhypercube";

	/** The lighted. */
	String LIGHTED = "lighted";

	/** The line. */
	String LINE = "line";

	/** The linear attenuation. */
	String LINEAR_ATTENUATION = "linear_attenuation";

	/** The lines. */
	String LINES = "lines";

	/**
	 * TYPES
	 */
	String LIST = "list";

	/** The location. */
	String LOCATION = "location";

	/** The look at. */
	String LOOK_AT = "look_at";

	/** The loop. */
	String LOOP = "loop";

	/** The map. */
	String MAP = "map";

	/** The mapping. */
	String MAPPING = "mapping";

	/** The mask. */
	String MASK = "mask";

	/** The match. */
	String MATCH = "match";

	/** The match between. */
	String MATCH_BETWEEN = "match_between";

	/** The match one. */
	String MATCH_ONE = "match_one";

	/** The match regex. */
	String MATCH_REGEX = "match_regex";

	/** The material. */
	String MATERIAL = "material";

	/** The matrix. */
	String MATRIX = "matrix";

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

	/** The mesh. */
	String MESH = "mesh";

	/** The message. */
	String MESSAGE = "message";

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

	/** The Morris method */
	String MORRIS = "morris";

	/** The mouse clicked. */
	String MOUSE_CLICKED = "mouse_click";

	/** The mouse down. */
	String MOUSE_DOWN = "mouse_down";

	/** The mouse entered. */
	String MOUSE_ENTERED = "mouse_enter";

	/** The mouse exited. */
	String MOUSE_EXITED = "mouse_exit";

	/** The mouse menu. */
	String MOUSE_MENU = "mouse_menu";

	/** The mouse moved. */
	String MOUSE_MOVED = "mouse_move";

	/** The mouse up. */
	String MOUSE_UP = "mouse_up";

	/** The moving 3d skill. */
	String MOVING_3D_SKILL = "moving3D";

	/** The moving skill. */
	String MOVING_SKILL = "moving";

	/** The multicore. */
	String MULTICORE = "multicore";

	/** The multiply. */
	String MULTIPLY = "*";

	/** The my. */
	String MY = "my";

	/** The mygraph. */
	String MYGRAPH = "my_graph";

	/** The myself. */
	String MYSELF = "myself";

	/** The name. */
	String NAME = "name";

	/** The nb cols. */
	String NB_COLS = "nb_cols";

	/** The nb rows. */
	String NB_ROWS = "nb_rows";

	/** The neighbors. */
	String NEIGHBORS = "neighbors";

	/** The neighbours. */
	String NEIGHBOURS = "neighbours";

	/** The no experiment. */
	String PRAGMA_NO_EXPERIMENT = "no_experiment";

	/** The no info. */
	String PRAGMA_NO_INFO = "no_info";

	/** The no warning. */
	String PRAGMA_NO_WARNING = "no_warning";

	/** The pragma requires. */
	String PRAGMA_REQUIRES = "requires";

	/** The node. */
	String NODE = "node";

	/** The null. */
	String NULL = "nil";

	/** The number. */
	String NUMBER = "number";

	/** The of. */
	String OF = "of";

	/** The on. */
	String ON = "on";

	/** The on change. */
	String ON_CHANGE = "on_change";

	/** The open. */
	String OPEN = "open";

	/** The open exp. */
	String OPEN_EXP = "(";

	/** The open list. */
	String OPEN_LIST = "[";

	/** The open point. */
	String OPEN_POINT = "{";

	/** The optional. */
	String OPTIONAL = "optional";

	/** The origin. */
	String ORIGIN = "**origin**";

	/** The orthogonal sampling */
	String ORTHOGONAL = "orthogonal";

	/** The orthographic projection. */
	String ORTHOGRAPHIC_PROJECTION = "orthographic_projection";

	/** The other events. */
	String OTHER_EVENTS = "other_events";

	/** The output. */
	String OUTPUT = "output";

	/** The output file. */
	String OUTPUT_FILE = "output_file";

	/** The over. */
	String OVER = "over";
	/** The overlay. */
	String OVERLAY = "overlay";

	/** The overwrite. */
	String OVERWRITE = "overwrite";

	/** The pair. */
	String PAIR = "pair";

	/** The parallel. */
	// "
	String PARALLEL = "parallel";

	/** The parameter. */
	String PARAMETER = "parameter";

	/** The parameters. */
	String PARAMETERS = "parameters";

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
	String PERMANENT = "permanent";

	/** The perspective. */
	String PERSPECTIVE = "perspective";

	/** The pie. */
	String PIE = "pie";

	/** The pitch. */
	String PITCH = "pitch";

	/** The places. */
	String PLACES = "places";

	/** The platform. */
	String PLATFORM = "platform";

	/** The plus. */
	String PLUS = "+";

	/** The point. */
	String POINT = "point";

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

	/** The real speed. */
	String REAL_SPEED = "real_speed";

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

	/** The rgb. */
	String RGB = "rgb";

	/** The right. */
	String RIGHT = "right";

	/** The ring. */
	String RING = "ring";

	/** The rng. */
	String RNG = "rng";

	/** The roll. */
	String ROLL = "roll";

	/** The rotate. */
	String ROTATE = "rotate";

	/** The rotation. */
	String ROTATION = "rotation";

	/** The rounded. */
	String ROUNDED = "rounded";

	/** Saltelli */
	String SALTELLI = "saltelli";

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

	/** The series. */
	String SERIES = "series";

	/** The set. */
	String SET = "set";

	/** The shape. */
	String SHAPE = "shape";

	/** The show. */
	String SHOW = "show";

	/** The simulation. */
	String SIMULATION = "simulation";

	/** The simulations. */
	String SIMULATIONS = "simulations";

	/** The simultaneously. */
	String SIMULTANEOUSLY = "simultaneously";

	/** The size. */
	String SIZE = "size";

	/** The skill. */
	String SKILL = "skill";

	/** The skills. */
	String SKILLS = "skills";

	/** The smooth. */
	String SMOOTH = "smooth";

	/** The sobol exploration method */
	String SOBOL = "sobol";

	/** The solve. */
	String SOLVE = "solve";

	/** The solver. */
	String SOLVER = "solver";

	/** The source. */
	String SOURCE = "source";

	/** The species. */
	String SPECIES = "species";

	/** The speed. */
	String SPEED = "speed";

	/** The spline. */
	String SPLINE = "spline";

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

	/** The Stochasticity Analysis */
	String STO = "stochanalyse";

	/** The stop sound. */
	String STOP_SOUND = "stop_sound";

	/** The string. */
	String STRING = "string";

	/** The style. */
	String STYLE = "style";

	/** The super. */
	String SUPER = "super";

	/** The switch. */
	String SWITCH = "switch";

	/** The synthetic. */
	String SYNTHETIC = "__synthetic__";

	/** The synthetic resources prefix. */
	String SYNTHETIC_RESOURCES_PREFIX = "__synthetic__";

	/** The table. */
	String TABLE = "table";

	/** The tabu. */
	String TABU = "tabu";

	/** The target. */
	String TARGET = "target";

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

	/** The thrad skill. */
	String THREAD_SKILL = "thread";

	/** The three d. */
	String THREE_D = "3d";

	/** The time final. */
	String TIME_FINAL = "tf";

	/** The time initial. */
	String TIME_INITIAL = "t0";

	/** The time series. */
	String TIME_SERIES = "time_series";

	/** The times. */
	String TIMES = "times";

	/** The title. */
	String TITLE = "title";

	/** The to. */
	String TO = "to";

	/** The toolbar. */
	String TOOLBAR = "toolbar";

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
	
	/** Uniform sampling */
	String UNIFORM = "uniform";
	
	/** The unit. */
	String UNIT = "unit";

	/** The unknown. */
	String UNKNOWN = "unknown";

	/** The until. */
	String UNTIL = "until";

	/** The update. */
	String UPDATE = "update";

	/** The updates. */
	String UPDATES = "updates";

	/** The user command. */
	String USER_COMMAND = "user_command";

	/** The user confirm. */
	String USER_CONFIRM = "user_confirm";

	/** The user controlled. */
	String USER_CONTROLLED = "user_controlled";

	/** The user first. */
	String USER_FIRST = "user_first";

	/** The user init. */
	String USER_INIT = "user_init";

	/** The user input. */
	String USER_INPUT = "user_input";

	/** The user input dialog. */
	String USER_INPUT_DIALOG = "user_input_dialog";

	/** The user last. */
	String USER_LAST = "user_last";

	/** The user only. */
	String USER_ONLY = "user_only";

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

	/** The while. */
	String WHILE = "while";

	/** The whisker. */
	String WHISKER = "whisker";

	/** The width. */
	String WIDTH = "width";

	/** The wireframe. */
	String WIREFRAME = "wireframe";

	/** The with. */
	String WITH = "with";

	/** The wizard. */
	String WIZARD = "wizard";

	/** The wizard page. */
	String WIZARD_PAGE = "wizard_page";

	/** The world agent name. */
	String WORLD_AGENT_NAME = "world";

	/** The writable. */
	String WRITABLE = "writable";

	/** The write. */
	String WRITE = "write";

	/** The x. */
	String X = "x";

	/** The x labels. */
	String X_LABELS = "x_serie_labels";

	/** The x serie. */
	String X_SERIE = "x_serie";

	/** The xml. */
	String XML = "xml";

	/** The xy. */
	String XY = "xy";

	/** The y. */
	String Y = "y";

	/** The y labels. */
	String Y_LABELS = "y_serie_labels";

	/** The y serie. */
	String Y_SERIE = "y_serie";

	/** The z. */
	String Z = "z";

	/** The zero. */
	String ZERO = "internal_zero_order_equation";

	/** The event type. */
	String[] EVENT_TYPE = { OTHER_EVENTS, MOUSE_DOWN };

	/** The methods. */
	String[] METHODS =
			{ GENETIC, ANNEALING, HILL_CLIMBING, TABU, REACTIVE_TABU, EXPLORATION, PSO, SOBOL, MORRIS, STO, BETAD };

	// DISPLAYS

	/** The Constant JAVA2D. */
	String _2D = "2d";

	/** The Constant OPENGL. */
	String _3D = "3d";

	/** The Constant WEB. */
	String WEB = "web";

	/** The Constant JAVA2D. */
	String JAVA2D = "java2D";

	/** The Constant OPENGL. */
	String OPENGL = "opengl";

}
