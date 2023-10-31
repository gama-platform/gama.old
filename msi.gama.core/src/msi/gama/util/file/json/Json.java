/*******************************************************************************************************
 *
 * Json.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file.json;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.root.PlatformAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gaml.interfaces.IJsonable;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * This class serves as the entry point to the minimal-json API. It has been adapted to GAMA by A. Drogoul in 2023 to
 * get rid of the static features, become stateful (i.e. add a serialisation context to keep references, be thread safe
 * and compute statistics), and add some useful features for GAMA (typedObject(...), etc.)
 */
public final class Json {

	/** The Constant NON_SAVEABLE_ATTRIBUTE_NAMES. */
	public static Set<String> NON_SERIALISABLE = Set.of(IKeyword.MEMBERS, IKeyword.AGENTS, IKeyword.LOCATION,
			IKeyword.HOST, IKeyword.PEERS, IKeyword.EXPERIMENT, IKeyword.WORLD_AGENT_NAME, SimulationAgent.TIME,
			PlatformAgent.MACHINE_TIME, SimulationAgent.DURATION, SimulationAgent.AVERAGE_DURATION,
			SimulationAgent.TOTAL_DURATION, IKeyword.INDEX);

	/** The Constant INSTANCE. A stateless instance that can be used for one shot serialisations. */
	private static final Json INSTANCE = new Json(false);

	/**
	 * Gets a single instance of Json.A stateless instance that can be used for one shot serialisations.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return single instance of Json
	 * @date 31 oct. 2023
	 */
	public static Json getInstance() { return INSTANCE; }

	/**
	 * Gets a new stateful instance of Json.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @return the new
	 * @date 31 oct. 2023
	 */
	public static Json getNew() { return new Json(); }

	/** The grid non serialisable. */
	public static Set<String> GRID_NON_SERIALISABLE = Set.of(IKeyword.GRID_X, IKeyword.GRID_Y, IKeyword.NEIGHBORS);

	/** The Constant GAML_TYPE_LABEL. */
	public static final String GAML_TYPE_LABEL = "gaml_type";

	/** The Constant GAML_SPECIES_LABEL. */
	public static final String GAML_SPECIES_LABEL = "gaml_species";

	/**
	 * Represents the JSON literal <code>null</code>.
	 */
	public static final JsonValue NULL = new JsonLiteral("null");

	/**
	 * Represents the JSON literal <code>true</code>.
	 */
	public static final JsonValue TRUE = new JsonLiteral("true");

	/**
	 * Represents the JSON literal <code>false</code>.
	 */
	public static final JsonValue FALSE = new JsonLiteral("false");

	/** The stateful. */
	private final boolean stateful;

	/**
	 * Instantiates a new json.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @date 31 oct. 2023
	 */
	private Json() {
		this(false);
	}

	/**
	 * Instantiates a new json.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param b
	 *            the b
	 * @date 31 oct. 2023
	 */
	private Json(final boolean b) {
		stateful = b;
	}

	/**
	 * Serialize.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param object
	 *            the object
	 * @return the json value
	 * @date 29 oct. 2023
	 */
	public JsonValue valueOf(final Object object) {
		if (object == null) return NULL;
		if (object instanceof JsonValue jv) return jv;
		if (object instanceof IJsonable j) return j.serializeToJson(this);
		if (object instanceof String s) return valueOf(s);
		if (object instanceof Character c) return valueOf(c);
		if (object instanceof Double d) return valueOf(d.doubleValue());
		if (object instanceof Float f) return valueOf(f.doubleValue());
		if (object instanceof Number n) return new JsonNumber(n.toString());
		if (object instanceof Boolean b) return valueOf(b.booleanValue());
		if (object instanceof Collection c) return GamaListFactory.wrap(Types.NO_TYPE, c).serializeToJson(this);
		if (object instanceof Map m) return GamaMapFactory.wrap(m).serializeToJson(this);
		if (object instanceof Exception ex) return object("exception", ex.getClass().getName(), "message",
				ex.getMessage(), "stack", array(Arrays.asList(ex.getStackTrace())));
		return valueOf(object.toString());
	}

	/**
	 * Returns a JsonValue instance that represents the given <code>int</code> value.
	 *
	 * @param value
	 *            the value to get a JSON representation for
	 * @return a JSON value that represents the given value
	 */
	public JsonValue valueOf(final int value) {
		return new JsonNumber(Integer.toString(value, 10));
	}

	/**
	 * Returns a JsonValue instance that represents the given <code>long</code> value.
	 *
	 * @param value
	 *            the value to get a JSON representation for
	 * @return a JSON value that represents the given value
	 */
	public JsonValue valueOf(final long value) {
		return new JsonNumber(Long.toString(value, 10));
	}

	/**
	 * Returns a JsonValue instance that represents the given <code>float</code> value.
	 *
	 * @param value
	 *            the value to get a JSON representation for
	 * @return a JSON value that represents the given value
	 */
	public JsonValue valueOf(final float value) {
		if (Float.isInfinite(value) || Float.isNaN(value))
			// throw new IllegalArgumentException("Infinite and NaN values not permitted in JSON");
			return NULL;
		return new JsonNumber(cutOffPointZero(Float.toString(value)));
	}

	/**
	 * Returns a JsonValue instance that represents the given <code>double</code> value.
	 *
	 * @param value
	 *            the value to get a JSON representation for
	 * @return a JSON value that represents the given value
	 */
	public JsonValue valueOf(final double value) {
		if (Double.isInfinite(value) || Double.isNaN(value))
			// throw new IllegalArgumentException("Infinite and NaN values not permitted in JSON");
			return NULL;
		return new JsonNumber(cutOffPointZero(Double.toString(value)));
	}

	/**
	 * Returns a JsonValue instance that represents the given string.
	 *
	 * @param string
	 *            the string to get a JSON representation for
	 * @return a JSON value that represents the given string
	 */
	public JsonValue valueOf(final String string) {
		return string == null ? NULL : new JsonString(string);
	}

	/**
	 * Value.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param c
	 *            the c
	 * @return the json value
	 * @date 29 oct. 2023
	 */
	public JsonValue valueOf(final Character c) {
		return c == null ? NULL : new JsonString(c.toString());
	}

	/**
	 * Returns a JsonValue instance that represents the given <code>boolean</code> value.
	 *
	 * @param value
	 *            the value to get a JSON representation for
	 * @return a JSON value that represents the given value
	 */
	public JsonValue valueOf(final boolean value) {
		return value ? TRUE : FALSE;
	}

	/**
	 * Creates a new empty JsonArray. This is equivalent to creating a new JsonArray using the constructor.
	 *
	 * @return a new empty JSON array
	 */
	public JsonArray array() {
		return new JsonArray(this);
	}

	/**
	 * Creates a new JsonArray that contains the JSON representations of the given <code>int</code> values.
	 *
	 * @param values
	 *            the values to be included in the new JSON array
	 * @return a new JSON array that contains the given values
	 */
	public JsonArray array(final int... values) {
		if (values == null) throw new NullPointerException("values is null");
		JsonArray array = new JsonArray(this);
		for (int value : values) { array.add(value); }
		return array;
	}

	/**
	 * Creates a new JsonArray that contains the JSON representations of the given <code>long</code> values.
	 *
	 * @param values
	 *            the values to be included in the new JSON array
	 * @return a new JSON array that contains the given values
	 */
	public JsonArray array(final long... values) {
		if (values == null) throw new NullPointerException("values is null");
		JsonArray array = new JsonArray(this);
		for (long value : values) { array.add(value); }
		return array;
	}

	/**
	 * Creates a new JsonArray that contains the JSON representations of the given <code>float</code> values.
	 *
	 * @param values
	 *            the values to be included in the new JSON array
	 * @return a new JSON array that contains the given values
	 */
	public JsonArray array(final float... values) {
		if (values == null) throw new NullPointerException("values is null");
		JsonArray array = new JsonArray(this);
		for (float value : values) { array.add(value); }
		return array;
	}

	/**
	 * Creates a new JsonArray that contains the JSON representations of the given <code>double</code> values.
	 *
	 * @param values
	 *            the values to be included in the new JSON array
	 * @return a new JSON array that contains the given values
	 */
	public JsonArray array(final double... values) {
		if (values == null) throw new NullPointerException("values is null");
		JsonArray array = new JsonArray(this);
		for (double value : values) { array.add(value); }
		return array;
	}

	/**
	 * Creates a new JsonArray that contains the JSON representations of the given <code>boolean</code> values.
	 *
	 * @param values
	 *            the values to be included in the new JSON array
	 * @return a new JSON array that contains the given values
	 */
	public JsonArray array(final boolean... values) {
		JsonArray array = new JsonArray(this);
		for (boolean value : values) { array.add(value); }
		return array;
	}

	/**
	 * Creates a new JsonArray that contains the JSON representations of the given strings.
	 *
	 * @param strings
	 *            the strings to be included in the new JSON array
	 * @return a new JSON array that contains the given strings
	 */
	public JsonArray array(final String... strings) {
		JsonArray array = new JsonArray(this);
		for (String value : strings) { array.add(value); }
		return array;
	}

	/**
	 * Array.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param objects
	 *            the objects
	 * @return the json array
	 * @date 29 oct. 2023
	 */
	public JsonArray array(final Object[] objects) {
		JsonArray array = new JsonArray(this);
		for (Object value : objects) { array.add(value); }
		return array;
	}

	/**
	 * Array.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param objects
	 *            the objects
	 * @return the json array
	 * @date 29 oct. 2023
	 */
	public JsonArray array(final Collection objects) {
		JsonArray array = new JsonArray(this);
		for (Object value : objects) { array.add(value); }
		return array;
	}

	/**
	 * Creates a new empty JsonObject. This is equivalent to creating a new JsonObject using the constructor.
	 *
	 * @return a new empty JSON object
	 */
	public JsonObject object() {
		return new JsonObject(this);
	}

	/**
	 * Object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param k1
	 *            the k 1
	 * @param v1
	 *            the v 1
	 * @return the json object
	 * @date 29 oct. 2023
	 */
	public JsonObject object(final String k1, final Object v1) {
		return object().add(k1, v1);
	}

	/**
	 * Object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 *
	 * @return the json object
	 * @date 29 oct. 2023
	 */
	public JsonObject object(final String k1, final Object v1, final String k2, final Object v2) {
		return object(k1, v1).add(k2, v2);
	}

	/**
	 * Object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 *
	 * @return the json object
	 * @date 29 oct. 2023
	 */
	public JsonObject object(final String k1, final Object v1, final String k2, final Object v2, final String k3,
			final Object v3) {
		return object(k1, v1, k2, v2).add(k3, v3);
	}

	/**
	 * Object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 *
	 * @return the json object
	 * @date 29 oct. 2023
	 */
	public JsonObject object(final String k1, final Object v1, final String k2, final Object v2, final String k3,
			final Object v3, final String k4, final Object v4) {
		return object(k1, v1, k2, v2, k3, v3).add(k4, v4);
	}

	/**
	 * Creates a new empty JsonObject provided with a gaml type. This is equivalent to creating a new JsonObject using
	 * the constructor and adding a first attribute with the type.
	 *
	 * @return a new empty JSON object with the corresponding type
	 */
	public JsonObject typedObject(final IType type) {
		return object(GAML_TYPE_LABEL, type.serializeToGaml(true));
	}

	/**
	 * Object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param k1
	 *            the k 1
	 * @param v1
	 *            the v 1
	 * @return the json object
	 * @date 29 oct. 2023
	 */
	public JsonObject typedObject(final IType type, final String k1, final Object v1) {
		return typedObject(type).add(k1, v1);
	}

	/**
	 * Object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 *
	 * @return the json object
	 * @date 29 oct. 2023
	 */
	public JsonObject typedObject(final IType type, final String k1, final Object v1, final String k2,
			final Object v2) {
		return typedObject(type, k1, v1).add(k2, v2);
	}

	/**
	 * Object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 *
	 * @return the json object
	 * @date 29 oct. 2023
	 */
	public JsonObject typedObject(final IType type, final String k1, final Object v1, final String k2, final Object v2,
			final String k3, final Object v3) {
		return typedObject(type, k1, v1, k2, v2).add(k3, v3);
	}

	/**
	 * Object.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 *
	 * @return the json object
	 * @date 29 oct. 2023
	 */
	public JsonObject typedObject(final IType type, final String k1, final Object v1, final String k2, final Object v2,
			final String k3, final Object v3, final String k4, final Object v4) {
		return typedObject(type, k1, v1, k2, v2, k3, v3).add(k4, v4);
	}

	/**
	 * Agent.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param agent
	 *            the agent
	 * @date 29 oct. 2023
	 */
	public JsonObject agent(final String species) {
		JsonObject object = object(GAML_SPECIES_LABEL, species);
		return object;
	}

	/**
	 * Parses the given input string as JSON. The input must contain a valid JSON value, optionally padded with
	 * whitespace.
	 *
	 * @param string
	 *            the input string, must be valid JSON
	 * @return a value that represents the parsed JSON
	 * @throws ParseException
	 *             if the input is not valid JSON
	 */
	public JsonValue parse(final String string) {
		if (string == null) throw new NullPointerException("string is null");
		DefaultHandler handler = new DefaultHandler();
		new JsonParser(handler).parse(string);
		return handler.getValue();
	}

	/**
	 * Reads the entire input from the given reader and parses it as JSON. The input must contain a valid JSON value,
	 * optionally padded with whitespace.
	 * <p>
	 * Characters are read in chunks into an input buffer. Hence, wrapping a reader in an additional
	 * <code>BufferedReader</code> likely won't improve reading performance.
	 * </p>
	 *
	 * @param reader
	 *            the reader to read the JSON value from
	 * @return a value that represents the parsed JSON
	 * @throws IOException
	 *             if an I/O error occurs in the reader
	 * @throws ParseException
	 *             if the input is not valid JSON
	 */
	public JsonValue parse(final Reader reader) throws IOException {
		if (reader == null) throw new NullPointerException("reader is null");
		DefaultHandler handler = new DefaultHandler();
		new JsonParser(handler).parse(reader);
		return handler.getValue();
	}

	/**
	 * Cut off point zero.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param string
	 *            the string
	 * @return the string
	 * @date 29 oct. 2023
	 */
	private String cutOffPointZero(final String string) {
		if (string.endsWith(".0")) return string.substring(0, string.length() - 2);
		return string;
	}

}
