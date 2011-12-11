/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.interfaces;

import java.util.List;
import msi.gama.kernel.exceptions.GamlException;

/**
 * Written by drogoul Modified on 19 mars 2010
 * 
 * @todo Description
 * 
 */
public interface ISymbol extends INamed {

	public static final String	ACTION				= "action";
	public static final String	ADD					= "add";
	public static final String	AGENT				= "agent";
	public static final String	AGENTS				= "agents";
	public static final String	AGGREGATION			= "aggregation";
	public static final String	ALL					= "all";
	public static final String	AMONG				= "among";
	public static final String	AREA				= "area";
	public static final String	ARG					= "arg";
	public static final String	AS					= "as";
	public static final String	ASK					= "ask";
	public static final String	ASPECT				= "aspect";
	public static final String	AT					= "at";
	public static final String	AXES				= "axes";
	public static final String	BACKGROUND			= "background";
	public static final String	BAR					= "bar";
	public final static String	BATCH				= "batch";
	public static final String	BEHAVIOR			= "behavior";
	public static final String	BEHAVIORS			= "behaviors";
	public static final String	CAPTURE				= "capture";
	public static final String	CATEGORY			= "category";
	public static final String	CELLULAR			= "cellular";
	public static final String	CHART				= "chart";
	public static final String	COLOR				= "color";
	public static final String	CONDITION			= "condition";
	public static final String	CONST				= "const";
	public static final String	CONTRIBUTE			= "contribute";
	public static final String	CREATE				= "create";
	public static final String	CSV					= "csv";
	public static final String	DATA				= "data";
	public static final String	DECAY				= "decay";
	public static final String	DEFAULT				= "default";
	public static final String	DEPENDS_ON			= "depends_on";
	public static final String	DIFFUSION			= "diffusion";
	public static final String	DISABLE				= "disable";
	public static final String	DISPLAY				= "display";
	public static final String	DISTANCE_CACHE		= "distance_cache_enabled";
	public static final String	DO					= "do";
	public static final String	DOT					= "dot";
	public static final String	DRAW				= "draw";
	public static final String	DYNAMIC				= "dynamic";
	public static final String	EDGE				= "edge";
	public static final String	ELSE				= "else";
	public static final String	EMPTY				= "empty";
	public static final String	ENABLE				= "enable";
	public static final String	ENTER				= "enter";
	public static final String	ENTITIES			= "entities";
	public static final String	ENVIRONMENT			= "environment";
	public static final String	EXPERIMENT			= "experiment";
	public static final String	EXPLODED			= "exploded";
	public static final String	FILE				= "file";
	public static final String	FILL_WITH			= "fill_with";
	public static final String	FOCUS				= "focus";
	public static final String	FONT				= "font";
	public static final String	FOOTER				= "footer";
	public static final String	FREQUENCY			= "frequency";
	public static final String	FROM				= "from";
	public static final String	GAML				= "gaml";
	public static final String	GETTER				= "getter";
	public static final String	GIS					= "gis";
	public static final String	GLOBAL				= "global";
	public static final String	GRADIENT			= "gradient";
	public static final String	GRID				= "grid";
	public static final String	GROUP				= "group";
	public final static String	GUI_				= "gui";
	public static final String	HEADER				= "header";
	public static final String	HISTOGRAM			= "histogram";
	public static final String	IF					= "if";
	public static final String	IMAGE				= "image";
	public static final String	IN					= "in";
	public static final String	INCLUDE				= "include";
	public static final String	INDEX				= "index";
	public static final String	INIT				= "init";
	public static final String	INITER				= "initer";
	public static final String	INSPECT				= "inspect";
	public static final String	ITEM				= "item";
	public static final String	JAVA				= "java";
	public static final String	KEEP				= "keep";
	public static final String	KEEP_SEED			= "keep_seed";
	public static final String	KEY					= "key";
	public static final String	KEYWORD				= "keyword";
	public static final String	KILL				= "kill";
	public static final String	LET					= "let";
	public static final String	LEAVE_GROUP			= "leave_group";
	public static final String	LINE				= "line";
	public static final String	LINES				= "lines";
	public static final String	LOOP				= "loop";
	public static final String	MAPPING				= "mapping";
	public static final String	MAX					= "max";
	public static final String	MAXIMIZE			= "maximize";
	public static final String	MERSENNE			= "mersenne";
	public static final String	METHOD				= "method";
	public static final String	MICRO				= "micro";
	public static final String	MICRO_LAYER			= "micro_layer";
	public static final String	MIN					= "min";
	public static final String	MINIMIZE			= "minimize";
	public static final String	MODEL				= "model";
	public static final String	MONITOR				= "monitor";
	public static final String	MYSELF				= "myself";
	public static final String	NAME				= "name";
	public static final String	NUMBER				= "number";
	public static final String	OF					= "of";
	public static final String	ON					= "on";
	public static final String	OUTPUT				= "output";
	public static final String	OVER				= "over";
	public static final String	PARAM				= "param";
	public static final String	PARAMETER			= "parameter";
	public static final String	PIE					= "pie";
	public static final String	POSITION			= "position";
	public static final String	PRIMITIVE			= "primitive";
	public static final String	PRIORITY			= "priority";
	public static final String	PROPAGATION			= "propagation";
	public static final String	PROPORTION			= "proportion";
	public static final String	PUT					= "put";
	public static final String	QUADTREE			= "quadtree";
	public static final String	RANDOM_SPECIES_NAME	= "random_builder";
	public static final String	RANGE				= "range";
	public static final String	REFLEX				= "reflex";
	public static final String	REFRESH_EVERY		= "refresh_every";
	public static final String	REGISTER			= "register";
	public static final String	RELEASE				= "release";
	public final static String	REMOTE				= "remote";
	public static final String	REMOVE				= "remove";
	public static final String	REPEAT				= "repeat";
	public static final String	RETURN				= "return";
	public static final String	RETURNS				= "returns";
	public static final String	REWRITE				= "rewrite";
	public static final String	RING				= "ring";
	public static final String	RNG					= "rng";
	public static final String	ROTATE				= "rotate";
	public static final String	SAVE				= "save";
	public static final String	SCALE				= "scale";
	public static final String	SCHEDULE			= "schedule";
	public static final String	SEED				= "seed";
	public static final String	SERIES				= "series";
	public static final String	SET					= "set";
	public static final String	SETTER				= "setter";
	public static final String	SHAPE				= "shape";
	public static final String	SIGNAL				= "signal";
	public static final String	SIMULATION			= "simulation";
	public static final String	SIZE				= "size";
	public static final String	SPECIES				= "species";
	public static final String	SPLINE				= "spline";
	public static final String	STACK				= "stack";
	public static final String	STEP				= "step";
	public static final String	STRATEGY			= "scheduling_strategy";
	public static final String	STYLE				= "style";
	public static final String	TARGET				= "target";
	public static final String	TARGETS				= "scheduling_targets";
	public static final String	TEXT				= "text";
	public static final String	THREE_D				= "3d";
	public static final String	TIME				= "time";
	public static final String	TIMES				= "times";
	public static final String	TITLE				= "title";
	public static final String	TO					= "to";
	public static final String	TOPOLOGY			= "topology";
	public static final String	TRANSPARENCY		= "transparency";
	public static final String	TYPE				= "type";
	public static final String	UNIT				= "unit";
	public static final String	UNTIL				= "until";
	public static final String	UPDATE				= "update";
	public static final String	VALUE				= "value";
	public static final String	VALUES				= "values";
	public static final String	VAR					= "var";
	public static final String	VARIATION			= "variation";
	public static final String	VERTEX				= "vertex";
	public static final String	WEIGHT				= "weight";
	public static final String	WHEN				= "when";
	public static final String	WHILE				= "while";
	public static final String	WITH				= "with";
	public static final String	WORLD_AGENT_NAME	= "world";
	public static final String	WORLD_SPECIES_NAME	= "world_species";
	public static final String	XML					= "xml";
	public static final String	XOR					= "xor";
	public static final String	XY					= "xy";

	public abstract void dispose();

	public abstract IDescription getDescription();

	public abstract IExpression getFacet(String key);

	public abstract boolean hasFacet(String key);

	public abstract void setChildren(List<? extends ISymbol> commands) throws GamlException;
}
