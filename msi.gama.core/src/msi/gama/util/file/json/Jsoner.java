/*******************************************************************************************************
 *
 * Jsoner.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.util.file.json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.geotools.data.DataUtilities;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import msi.gama.common.geometry.GeometryUtils;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.IShape;
import msi.gama.util.GamaColor;
import msi.gama.util.GamaPair;
import msi.gama.util.serialize.IStreamConverter;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.statements.SaveStatement;

/**
 * Jsoner provides JSON utilities for escaping strings to be JSON compatible, thread safe parsing (RFC 4627) JSON
 * strings, and serializing data to strings in JSON format.
 *
 * @author A. Drogoul; adapted for GAMA from json-simple library
 * @since 2.0.0
 */
public class Jsoner {
	/** Flags to tweak the behavior of the primary deserialization method. */
	private enum DeserializationOptions {
		/** Whether multiple JSON values can be deserialized as a root element. */
		ALLOW_CONCATENATED_JSON_VALUES,
		/** Whether a JsonArray can be deserialized as a root element. */
		ALLOW_JSON_ARRAYS,
		/** Whether a boolean, null, Number, or String can be deserialized as a root element. */
		ALLOW_JSON_DATA,
		/** Whether a JsonObject can be deserialized as a root element. */
		ALLOW_JSON_OBJECTS;
	}

	/** The stream converter. */
	public static IStreamConverter streamConverter;

	/** Flags to tweak the behavior of the primary serialization method. */
	private enum SerializationOptions {
		/**
		 * Instead of aborting serialization on non-JSON values that are Enums it will continue serialization with the
		 * Enums' "${PACKAGE}.${DECLARING_CLASS}.${NAME}".
		 *
		 * @see Enum
		 * @deprecated 2.3.0 the enum should implement Jsonable instead.
		 */
		@Deprecated
		ALLOW_FULLY_QUALIFIED_ENUMERATIONS,
		/**
		 * Instead of aborting serialization on non-JSON values it will continue serialization by serializing the
		 * non-JSON value directly into the now invalid JSON. Be mindful that invalid JSON will not successfully
		 * deserialize.
		 */
		ALLOW_INVALIDS,
		/**
		 * Instead of aborting serialization on non-JSON values that implement Jsonable it will continue serialization
		 * by deferring serialization to the Jsonable.
		 *
		 * @see Jsonable
		 */
		ALLOW_JSONABLES,
		/**
		 * Instead of aborting serialization on non-JSON values it will continue serialization by using reflection to
		 * best describe the value as a JsonObject.
		 *
		 * @deprecated 2.3.0 there is no passive way to accomplish this contract and so will be abandoned.
		 */
		@Deprecated
		ALLOW_UNDEFINEDS;
	}

	/** The possible States of a JSON deserializer. */
	private enum States {
		/** Post-parsing state. */
		DONE,
		/** Pre-parsing state. */
		INITIAL,
		/** Parsing error, ParsingException should be thrown. */
		PARSED_ERROR,

		/** The parsing array. */
		PARSING_ARRAY,
		/** Parsing a key-value pair inside of an object. */
		PARSING_ENTRY,

		/** The parsing object. */
		PARSING_OBJECT;
	}

	/**
	 * Instantiates a new jsoner.
	 */
	private Jsoner() {
		/* Keeping it classy. */
	}

	/**
	 * Deserializes a readable stream according to the RFC 4627 JSON specification.
	 *
	 * @param readableDeserializable
	 *            representing content to be deserialized as JSON.
	 * @return either a boolean, null, Number, String, JsonObject, or JsonArray that best represents the deserializable.
	 * @throws DeserializationException
	 *             if an unexpected token is encountered in the deserializable. To recover from a
	 *             DeserializationException: fix the deserializable to no longer have an unexpected token and try again.
	 */
	public static Object deserialize(final Reader readableDeserializable) throws DeserializationException {
		return Jsoner.deserialize(readableDeserializable, EnumSet.of(DeserializationOptions.ALLOW_JSON_ARRAYS,
				DeserializationOptions.ALLOW_JSON_OBJECTS, DeserializationOptions.ALLOW_JSON_DATA)).get(0);
	}

	/**
	 * Deserialize a stream with all deserialized JSON values are wrapped in a JsonArray.
	 *
	 * @param deserializable
	 *            representing content to be deserialized as JSON.
	 * @param flags
	 *            representing the allowances and restrictions on deserialization.
	 * @return the allowable object best represented by the deserializable.
	 * @throws DeserializationException
	 *             if a disallowed or unexpected token is encountered in the deserializable. To recover from a
	 *             DeserializationException: fix the deserializable to no longer have a disallowed or unexpected token
	 *             and try again.
	 */
	private static GamaJsonList deserialize(final Reader deserializable, final Set<DeserializationOptions> flags)
			throws DeserializationException {
		final Yylex lexer = new Yylex(deserializable);
		Yytoken token;
		States currentState;
		int returnCount = 1;
		final LinkedList<States> stateStack = new LinkedList<>();
		final LinkedList<Object> valueStack = new LinkedList<>();
		stateStack.addLast(States.INITIAL);
		// System.out.println("//////////DESERIALIZING//////////");
		do {
			/* Parse through the parsable string's tokens. */
			currentState = Jsoner.popNextState(stateStack);
			token = Jsoner.lexNextToken(lexer);
			switch (currentState) {
				case DONE:
					/* The parse has finished a JSON value. */
					if (!flags.contains(DeserializationOptions.ALLOW_CONCATENATED_JSON_VALUES)
							|| Yytoken.Types.END.equals(token.getType())) {
						/* Break if concatenated values are not allowed or if an END token is read. */
						break;
					}
					/* Increment the amount of returned JSON values and treat the token as if it were a fresh parse. */
					returnCount += 1;
					/* Fall through to the case for the initial state. */
					//$FALL-THROUGH$
				case INITIAL:
					/* The parse has just started. */
					switch (token.getType()) {
						case DATUM:
							/* A boolean, null, Number, or String could be detected. */
							if (!flags.contains(DeserializationOptions.ALLOW_JSON_DATA))
								throw new DeserializationException(lexer.getPosition(),
										DeserializationException.Problems.DISALLOWED_TOKEN, token);
							valueStack.addLast(token.getValue());
							stateStack.addLast(States.DONE);
							break;
						case LEFT_BRACE:
							/* An object is detected. */
							if (!flags.contains(DeserializationOptions.ALLOW_JSON_OBJECTS))
								throw new DeserializationException(lexer.getPosition(),
										DeserializationException.Problems.DISALLOWED_TOKEN, token);
							valueStack.addLast(new GamaJsonMap());
							stateStack.addLast(States.PARSING_OBJECT);
							break;
						case LEFT_SQUARE:
							/* An array is detected. */
							if (!flags.contains(DeserializationOptions.ALLOW_JSON_ARRAYS))
								throw new DeserializationException(lexer.getPosition(),
										DeserializationException.Problems.DISALLOWED_TOKEN, token);
							valueStack.addLast(new GamaJsonList());
							stateStack.addLast(States.PARSING_ARRAY);
							break;
						default:
							/* Neither a JSON array or object was detected. */
							throw new DeserializationException(lexer.getPosition(),
									DeserializationException.Problems.UNEXPECTED_TOKEN, token);
					}
					break;
				case PARSED_ERROR:
					/* The parse could be in this state due to the state stack not having a state to pop off. */
					throw new DeserializationException(lexer.getPosition(),
							DeserializationException.Problems.UNEXPECTED_TOKEN, token);
				case PARSING_ARRAY:
					switch (token.getType()) {
						case COMMA:
							/* The parse could detect a comma while parsing an array since it separates each element. */
							stateStack.addLast(currentState);
							break;
						case DATUM:
							/* The parse found an element of the array. */
							GamaJsonList val = (GamaJsonList) valueStack.getLast();
							val.add(token.getValue());
							stateStack.addLast(currentState);
							break;
						case LEFT_BRACE:
							/* The parse found an object in the array. */
							val = (GamaJsonList) valueStack.getLast();
							final GamaJsonMap object = new GamaJsonMap();
							val.add(object);
							valueStack.addLast(object);
							stateStack.addLast(currentState);
							stateStack.addLast(States.PARSING_OBJECT);
							break;
						case LEFT_SQUARE:
							/* The parse found another array in the array. */
							val = (GamaJsonList) valueStack.getLast();
							final GamaJsonList array = new GamaJsonList();
							val.add(array);
							valueStack.addLast(array);
							stateStack.addLast(currentState);
							stateStack.addLast(States.PARSING_ARRAY);
							break;
						case RIGHT_SQUARE:
							/* The parse found the end of the array. */
							if (valueStack.size() > returnCount) {
								valueStack.removeLast();
							} else {
								/* The parse has been fully resolved. */
								stateStack.addLast(States.DONE);
							}
							break;
						default:
							/* Any other token is invalid in an array. */
							throw new DeserializationException(lexer.getPosition(),
									DeserializationException.Problems.UNEXPECTED_TOKEN, token);
					}
					break;
				case PARSING_OBJECT:
					/* The parse has detected the start of an object. */
					switch (token.getType()) {
						case COMMA:
							/*
							 * The parse could detect a comma while parsing an object since it separates each key value
							 * pair. Continue parsing the object.
							 */
							stateStack.addLast(currentState);
							break;
						case DATUM:
							/* The token ought to be a key. */
							if (!(token.getValue() instanceof String)) /*
																		 * Abort! JSON keys are always strings and it
																		 * wasn't a string.
																		 */
								throw new DeserializationException(lexer.getPosition(),
										DeserializationException.Problems.UNEXPECTED_TOKEN, token);
							/*
							 * JSON keys are always strings, strings are not always JSON keys but it is going to be
							 * treated as one. Continue parsing the object.
							 */
							final String key = (String) token.getValue();
							valueStack.addLast(key);
							stateStack.addLast(currentState);
							stateStack.addLast(States.PARSING_ENTRY);
							break;
						case RIGHT_BRACE:
							/* The parse has found the end of the object. */
							if (valueStack.size() > returnCount) {
								/* There are unresolved values remaining. */
								valueStack.removeLast();
							} else {
								/* The parse has been fully resolved. */
								stateStack.addLast(States.DONE);
							}
							break;
						default:
							/* The parse didn't detect the end of an object or a key. */
							throw new DeserializationException(lexer.getPosition(),
									DeserializationException.Problems.UNEXPECTED_TOKEN, token);
					}
					break;
				case PARSING_ENTRY:
					switch (token.getType()) {
						/* Parsed pair keys can only happen while parsing objects. */
						case COLON:
							/*
							 * The parse could detect a colon while parsing a key value pair since it separates the key
							 * and value from each other. Continue parsing the entry.
							 */
							stateStack.addLast(currentState);
							break;
						case DATUM:
							/* The parse has found a value for the parsed pair key. */
							String key = (String) valueStack.removeLast();
							GamaJsonMap parent = (GamaJsonMap) valueStack.getLast();
							parent.put(key, token.getValue());
							break;
						case LEFT_BRACE:
							/* The parse has found an object for the parsed pair key. */
							key = (String) valueStack.removeLast();
							parent = (GamaJsonMap) valueStack.getLast();
							final GamaJsonMap object = new GamaJsonMap();
							parent.put(key, object);
							valueStack.addLast(object);
							stateStack.addLast(States.PARSING_OBJECT);
							break;
						case LEFT_SQUARE:
							/* The parse has found an array for the parsed pair key. */
							key = (String) valueStack.removeLast();
							parent = (GamaJsonMap) valueStack.getLast();
							final GamaJsonList array = new GamaJsonList();
							parent.put(key, array);
							valueStack.addLast(array);
							stateStack.addLast(States.PARSING_ARRAY);
							break;
						default:
							/* The parse didn't find anything for the parsed pair key. */
							throw new DeserializationException(lexer.getPosition(),
									DeserializationException.Problems.UNEXPECTED_TOKEN, token);
					}
					break;
				default:
					break;
			}
			// System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			// System.out.println(currentState);
			// System.out.println(token);
			// System.out.println(valueStack);
			// System.out.println(stateStack);
			/* If we're not at the END and DONE then do the above again. */
		} while (!States.DONE.equals(currentState) || !Yytoken.Types.END.equals(token.getType()));
		// System.out.println("!!!!!!!!!!DESERIALIZED!!!!!!!!!!");
		return new GamaJsonList(valueStack);
	}

	/**
	 * A convenience method that assumes a StringReader to deserialize a string.
	 *
	 * @param deserializable
	 *            representing content to be deserialized as JSON.
	 * @return either a boolean, null, Number, String, JsonObject, or JsonArray that best represents the deserializable.
	 * @throws DeserializationException
	 *             if an unexpected token is encountered in the deserializable. To recover from a
	 *             DeserializationException: fix the deserializable to no longer have an unexpected token and try again.
	 * @see Jsoner#deserialize(Reader)
	 * @see StringReader
	 */
	public static Object deserialize(final String deserializable) throws DeserializationException {
		Object returnable;
		try (StringReader readableDeserializable = new StringReader(deserializable);) {
			returnable = Jsoner.deserialize(readableDeserializable);
		} catch (final NullPointerException caught) {
			/*
			 * They both have the same recovery scenario. See StringReader. If deserializable is null, it should be
			 * reasonable to expect null back.
			 */
			returnable = null;
		}
		return returnable;
	}

	/**
	 * A convenience method that assumes multiple RFC 4627 JSON values (except numbers) have been concatenated together
	 * for deserilization which will be collectively returned in a JsonArray wrapper. There may be numbers included,
	 * they just must not be concatenated together as it is prone to NumberFormatExceptions (thus causing a
	 * DeserializationException) or the numbers no longer represent their respective values. Examples: "123null321"
	 * returns [123, null, 321] "nullnullnulltruefalse\"\"{}[]" returns [null, null, null, true, false, "", {}, []]
	 * "123" appended to "321" returns [123321] "12.3" appended to "3.21" throws
	 * DeserializationException(NumberFormatException) "123" appended to "-321" throws
	 * DeserializationException(NumberFormatException) "123e321" appended to "-1" throws
	 * DeserializationException(NumberFormatException) "null12.33.21null" throws
	 * DeserializationException(NumberFormatException)
	 *
	 * @param deserializable
	 *            representing concatenated content to be deserialized as JSON in one reader. Its contents may not
	 *            contain two numbers concatenated together.
	 * @return a JsonArray that contains each of the concatenated objects as its elements. Each concatenated element is
	 *         either a boolean, null, Number, String, JsonArray, or JsonObject that best represents the concatenated
	 *         content inside deserializable.
	 * @throws DeserializationException
	 *             if an unexpected token is encountered in the deserializable. To recover from a
	 *             DeserializationException: fix the deserializable to no longer have an unexpected token and try again.
	 */
	public static GamaJsonList deserializeMany(final Reader deserializable) throws DeserializationException {
		return Jsoner.deserialize(deserializable,
				EnumSet.of(DeserializationOptions.ALLOW_JSON_ARRAYS, DeserializationOptions.ALLOW_JSON_OBJECTS,
						DeserializationOptions.ALLOW_JSON_DATA, DeserializationOptions.ALLOW_CONCATENATED_JSON_VALUES));
	}

	/**
	 * Escapes potentially confusing or important characters in the String provided.
	 *
	 * @param escapable
	 *            an unescaped string.
	 * @return an escaped string for usage in JSON; An escaped string is one that has escaped all of the quotes ("),
	 *         backslashes (\), return character (\r), new line character (\n), tab character (\t), backspace character
	 *         (\b), form feed character (\f) and other control characters [u0000..u001F] or characters [u007F..u009F],
	 *         [u2000..u20FF] with a backslash (\) which itself must be escaped by the backslash in a java string.
	 */
	public static String escape(final String escapable) {
		if (escapable == null) return "";
		final StringBuilder builder = new StringBuilder();
		final int characters = escapable.length();
		for (int i = 0; i < characters; i++) {
			final char character = escapable.charAt(i);
			switch (character) {
				case '"':
					builder.append("\\\"");
					break;
				case '\\':
					builder.append("\\\\");
					break;
				case '\b':
					builder.append("\\b");
					break;
				case '\f':
					builder.append("\\f");
					break;
				case '\n':
					builder.append("\\n");
					break;
				case '\r':
					builder.append("\\r");
					break;
				case '\t':
					builder.append("\\t");
					break;
				case '/':
					builder.append("\\/");
					break;
				default:
					/*
					 * The many characters that get replaced are benign to software but could be mistaken by people
					 * reading it for a JSON relevant character.
					 */
					if (character >= '\u0000' && character <= '\u001F' || character >= '\u007F' && character <= '\u009F'
							|| character >= '\u2000' && character <= '\u20FF') {
						final String characterHexCode = Integer.toHexString(character);
						builder.append("\\u");
						for (int k = 0; k < 4 - characterHexCode.length(); k++) { builder.append("0"); }
						builder.append(characterHexCode.toUpperCase());
					} else {
						/* Character didn't need escaping. */
						builder.append(character);
					}
			}
		}
		return builder.toString();
	}

	/**
	 * Processes the lexer's reader for the next token.
	 *
	 * @param lexer
	 *            represents a text processor being used in the deserialization process.
	 * @return a token representing a meaningful element encountered by the lexer.
	 * @throws DeserializationException
	 *             if an unexpected character is encountered while processing the text.
	 */
	private static Yytoken lexNextToken(final Yylex lexer) throws DeserializationException {
		Yytoken returnable;
		/* Parse through the next token. */
		try {
			returnable = lexer.yylex();
		} catch (final IOException caught) {
			throw new DeserializationException(-1, DeserializationException.Problems.UNEXPECTED_EXCEPTION, caught);
		}
		if (returnable == null) {
			/* If there isn't another token, it must be the end. */
			returnable = new Yytoken(Yytoken.Types.END, null);
		}
		return returnable;
	}

	/**
	 * Used for state transitions while deserializing.
	 *
	 * @param stateStack
	 *            represents the deserialization states saved for future processing.
	 * @return a state for deserialization context so it knows how to consume the next token.
	 */
	private static States popNextState(final LinkedList<States> stateStack) {
		if (!stateStack.isEmpty()) return stateStack.removeLast();
		return States.PARSED_ERROR;
	}

	/**
	 * Formats the JSON string to be more easily human readable using tabs for indentation.
	 *
	 * @param printable
	 *            representing a JSON formatted string with out extraneous characters, like one returned from
	 *            Jsoner#serialize(Object).
	 * @return printable except it will have '\n' then '\t' characters inserted after '[', '{', ',' and before ']' '}'
	 *         tokens in the JSON. It will return null if printable isn't a JSON string.
	 */
	public static String prettyPrint(final String printable) {
		return Jsoner.prettyPrint(printable, "\t");
	}

	/**
	 * Formats the JSON string to be more easily human readable using an arbitrary amount of spaces for indentation.
	 *
	 * @param printable
	 *            representing a JSON formatted string with out extraneous characters, like one returned from
	 *            Jsoner#serialize(Object).
	 * @param spaces
	 *            representing the amount of spaces to use for indentation. Must be between 2 and 10.
	 * @return printable except it will have '\n' then space characters inserted after '[', '{', ',' and before ']' '}'
	 *         tokens in the JSON. It will return null if printable isn't a JSON string.
	 * @throws IllegalArgumentException
	 *             if spaces isn't between [2..10].
	 * @see Jsoner#prettyPrint(String)
	 * @since 2.2.0 to allow pretty printing with spaces instead of tabs.
	 */
	public static String prettyPrint(final String printable, final int spaces) {
		if (spaces > 10 || spaces < 2)
			throw new IllegalArgumentException("Indentation with spaces must be between 2 and 10.");
		final StringBuilder indentation = new StringBuilder("");
		for (int i = 0; i < spaces; i++) { indentation.append(" "); }
		return Jsoner.prettyPrint(printable, indentation.toString());
	}

	/**
	 * Makes the JSON string more easily human readable using indentation of the caller's choice.
	 *
	 * @param printable
	 *            representing a JSON formatted string with out extraneous characters, like one returned from
	 *            Jsoner#serialize(Object).
	 * @param indentation
	 *            representing the indentation used to format the JSON string.
	 * @return printable except it will have '\n' then indentation characters inserted after '[', '{', ',' and before
	 *         ']' '}' tokens in the JSON. It will return null if printable isn't a JSON string.
	 */
	private static String prettyPrint(final String printable, final String indentation) {
		final Yylex lexer = new Yylex(new StringReader(printable));
		Yytoken lexed;
		final StringBuilder returnable = new StringBuilder();
		int level = 0;
		try {
			do {
				lexed = Jsoner.lexNextToken(lexer);
				switch (lexed.getType()) {
					case COLON:
						returnable.append(":");
						break;
					case COMMA:
						returnable.append(lexed.getValue());
						returnable.append("\n");
						for (int i = 0; i < level; i++) { returnable.append(indentation); }
						break;
					case END:
						break;
					case LEFT_BRACE, LEFT_SQUARE:
						returnable.append(lexed.getValue());
						returnable.append("\n");
						level++;
						for (int i = 0; i < level; i++) { returnable.append(indentation); }
						break;
					case RIGHT_BRACE, RIGHT_SQUARE:
						returnable.append("\n");
						level--;
						for (int i = 0; i < level; i++) { returnable.append(indentation); }
						returnable.append(lexed.getValue());
						break;
					default:
						if (lexed.getValue() instanceof String s) {
							returnable.append("\"");
							returnable.append(Jsoner.escape(s));
							returnable.append("\"");
						} else {
							returnable.append(lexed.getValue());
						}
						break;
				}
				// System.out.println(lexed);
			} while (!Yytoken.Types.END.equals(lexed.getType()));
		} catch (final DeserializationException caught) {
			/* This is according to the method's contract. */
			return null;
		}
		// System.out.println(printable);
		// System.out.println(returnable);
		// System.out.println(Jsoner.escape(returnable.toString()));
		return returnable.toString();
	}

	/**
	 * A convenience method that assumes a StringWriter.
	 *
	 * @param jsonSerializable
	 *            represents the object that should be serialized as a string in JSON format.
	 * @return a string, in JSON format, that represents the object provided.
	 * @throws IllegalArgumentException
	 *             if the jsonSerializable isn't serializable in JSON.
	 * @see Jsoner#serialize(Object, Writer)
	 * @see StringWriter
	 */
	public static String serialize(final Object jsonSerializable) {
		final StringWriter writableDestination = new StringWriter();
		try {
			Jsoner.serialize(jsonSerializable, writableDestination);
		} catch (final IOException caught) {
			/* See StringWriter. */
		}
		return writableDestination.toString();
	}

	/**
	 * Serializes values according to the RFC 4627 JSON specification. It will also trust the serialization provided by
	 * any Jsonables it serializes and serializes Enums that don't implement Jsonable as a string of their fully
	 * qualified name.
	 *
	 * @param jsonSerializable
	 *            represents the object that should be serialized in JSON format.
	 * @param writableDestination
	 *            represents where the resulting JSON text is written to.
	 * @throws IOException
	 *             if the writableDestination encounters an I/O problem, like being closed while in use.
	 * @throws IllegalArgumentException
	 *             if the jsonSerializable isn't serializable in JSON.
	 */
	public static void serialize(final Object jsonSerializable, final Writer writableDestination) throws IOException {
		Jsoner.serialize(jsonSerializable, writableDestination, EnumSet.of(SerializationOptions.ALLOW_JSONABLES,
				SerializationOptions.ALLOW_FULLY_QUALIFIED_ENUMERATIONS));
	}

	/**
	 * Serialize values to JSON and write them to the provided writer based on behavior flags.
	 *
	 * @param jsonSerializable
	 *            represents the object that should be serialized to a string in JSON format.
	 * @param writableDestination
	 *            represents where the resulting JSON text is written to.
	 * @param replacement
	 *            represents what is serialized instead of a non-JSON value when replacements are allowed.
	 * @param flags
	 *            represents the allowances and restrictions on serialization.
	 * @throws IOException
	 *             if the writableDestination encounters an I/O problem.
	 * @throws IllegalArgumentException
	 *             if the jsonSerializable isn't serializable in JSON.
	 * @see SerializationOptions
	 */
	private static void serialize(final Object jsonSerializable, final Writer writableDestination,
			final Set<SerializationOptions> flags) throws IOException {
		if (jsonSerializable == null) {
			/* When a null is passed in the word null is supported in JSON. */
			writableDestination.write("null");
		} else if (jsonSerializable instanceof Jsonable j && flags.contains(SerializationOptions.ALLOW_JSONABLES)) {
			/* Writes the writable as defined by the writable. */
			writableDestination.write(j.toJson());
		} else if (jsonSerializable instanceof Enum e
				&& flags.contains(SerializationOptions.ALLOW_FULLY_QUALIFIED_ENUMERATIONS)) {
			writableDestination.write('"');
			writableDestination.write(e.getDeclaringClass().getName());
			writableDestination.write('.');
			writableDestination.write(e.name());
			writableDestination.write('"');
		} else if (jsonSerializable instanceof String s) {
			/* Make sure the string is properly escaped. */
			writableDestination.write('"');
			writableDestination.write(Jsoner.escape(s));
			writableDestination.write('"');
		} else if (jsonSerializable instanceof Character) {
			/* Make sure the string is properly escaped. */
			// writableDestination.write('"');
			writableDestination.write(Jsoner.escape(jsonSerializable.toString()));
			// writableDestination.write('"');
		} else if (jsonSerializable instanceof Double d) {
			if (((Double) jsonSerializable).isInfinite() || d.isNaN()) {
				/* Infinite and not a number are not supported by the JSON specification, so null is used instead. */
				writableDestination.write("null");
			} else {
				writableDestination.write(jsonSerializable.toString());
			}
		} else if (jsonSerializable instanceof Float f) {
			if (((Float) jsonSerializable).isInfinite() || f.isNaN()) {
				/* Infinite and not a number are not supported by the JSON specification, so null is used instead. */
				writableDestination.write("null");
			} else {
				writableDestination.write(jsonSerializable.toString());
			}
		} else if (jsonSerializable instanceof Number || jsonSerializable instanceof Boolean) {
			writableDestination.write(jsonSerializable.toString());
		} else if (jsonSerializable instanceof GamaColor col) {
			writableDestination.write('{');
			writableDestination.write('"' + "r" + '"' + ":");
			Jsoner.serialize(col.red(), writableDestination, flags);
			writableDestination.write("," + '"' + "g" + '"' + ":");
			Jsoner.serialize(col.blue(), writableDestination, flags);
			writableDestination.write("," + '"' + "b" + '"' + ":");
			Jsoner.serialize(col.green(), writableDestination, flags);
			writableDestination.write('}');
		} else if (jsonSerializable instanceof Map m) {
			/* Writes the map in JSON object format. */
			boolean isFirstEntry = true;
			@SuppressWarnings ("rawtypes") final Iterator entries = m.entrySet().iterator();
			writableDestination.write('{');
			while (entries.hasNext()) {
				if (isFirstEntry) {
					isFirstEntry = false;
				} else {
					writableDestination.write(',');
				}
				@SuppressWarnings ("rawtypes") final Map.Entry entry = (Map.Entry) entries.next();
				Jsoner.serialize(entry.getKey(), writableDestination, flags);
				writableDestination.write(':');
				Jsoner.serialize(entry.getValue(), writableDestination, flags);
			}
			writableDestination.write('}');
		} else if (jsonSerializable instanceof Collection c) {
			/* Writes the collection in JSON array format. */
			boolean isFirstElement = true;
			@SuppressWarnings ("rawtypes") final Iterator elements = c.iterator();
			writableDestination.write('[');
			while (elements.hasNext()) {
				if (isFirstElement) {
					isFirstElement = false;
				} else {
					writableDestination.write(',');
				}
				Jsoner.serialize(elements.next(), writableDestination, flags);
			}
			writableDestination.write(']');

		} else if (jsonSerializable instanceof GamaPair p) {
			// Considers gamapairs as json
			writableDestination.write('{');
			Jsoner.serialize(p.key, writableDestination, flags);
			writableDestination.write(':');
			Jsoner.serialize(p.value, writableDestination, flags);
			writableDestination.write('}');

		} else if (jsonSerializable instanceof byte[] bw) {
			final int numberOfElements = bw.length;
			writableDestination.write('[');
			for (int i = 0; i < numberOfElements; i++) {
				if (i == numberOfElements - 1) {
					Jsoner.serialize(bw[i], writableDestination, flags);
				} else {
					Jsoner.serialize(bw[i], writableDestination, flags);
					writableDestination.write(',');
				}
			}
			writableDestination.write(']');
		} else if (jsonSerializable instanceof short[] sw) {
			final int numberOfElements = sw.length;
			writableDestination.write('[');
			for (int i = 0; i < numberOfElements; i++) {
				if (i == numberOfElements - 1) {
					Jsoner.serialize(sw[i], writableDestination, flags);
				} else {
					Jsoner.serialize(sw[i], writableDestination, flags);
					writableDestination.write(',');
				}
			}
			writableDestination.write(']');
		} else if (jsonSerializable instanceof int[] iw) {
			final int numberOfElements = iw.length;
			writableDestination.write('[');
			for (int i = 0; i < numberOfElements; i++) {
				if (i == numberOfElements - 1) {
					Jsoner.serialize(iw[i], writableDestination, flags);
				} else {
					Jsoner.serialize(iw[i], writableDestination, flags);
					writableDestination.write(',');
				}
			}
			writableDestination.write(']');
		} else if (jsonSerializable instanceof long[] lw) {
			final int numberOfElements = lw.length;
			writableDestination.write('[');
			for (int i = 0; i < numberOfElements; i++) {
				if (i == numberOfElements - 1) {
					Jsoner.serialize(lw[i], writableDestination, flags);
				} else {
					Jsoner.serialize(lw[i], writableDestination, flags);
					writableDestination.write(',');
				}
			}
			writableDestination.write(']');
		} else if (jsonSerializable instanceof float[] fw) {
			final int numberOfElements = fw.length;
			writableDestination.write('[');
			for (int i = 0; i < numberOfElements; i++) {
				if (i == numberOfElements - 1) {
					Jsoner.serialize(fw[i], writableDestination, flags);
				} else {
					Jsoner.serialize(fw[i], writableDestination, flags);
					writableDestination.write(',');
				}
			}
			writableDestination.write(']');
		} else if (jsonSerializable instanceof double[] dw) {
			final int numberOfElements = dw.length;
			writableDestination.write('[');
			for (int i = 0; i < numberOfElements; i++) {
				if (i == numberOfElements - 1) {
					Jsoner.serialize(dw[i], writableDestination, flags);
				} else {
					Jsoner.serialize(dw[i], writableDestination, flags);
					writableDestination.write(',');
				}
			}
			writableDestination.write(']');
		} else if (jsonSerializable instanceof boolean[] bw) {
			final int numberOfElements = bw.length;
			writableDestination.write('[');
			for (int i = 0; i < numberOfElements; i++) {
				if (i == numberOfElements - 1) {
					Jsoner.serialize(bw[i], writableDestination, flags);
				} else {
					Jsoner.serialize(bw[i], writableDestination, flags);
					writableDestination.write(',');
				}
			}
			writableDestination.write(']');
		} else if (jsonSerializable instanceof char[] cw) {
			final int numberOfElements = cw.length;
			writableDestination.write("[\"");
			for (int i = 0; i < numberOfElements; i++) {
				if (i == numberOfElements - 1) {
					Jsoner.serialize(cw[i], writableDestination, flags);
				} else {
					Jsoner.serialize(cw[i], writableDestination, flags);
					writableDestination.write("\",\"");
				}
			}
			writableDestination.write("\"]");
		} else if (jsonSerializable instanceof Object[] ow) {
			final int numberOfElements = ow.length;
			writableDestination.write('[');
			for (int i = 0; i < numberOfElements; i++) {
				if (i == numberOfElements - 1) {
					Jsoner.serialize(ow[i], writableDestination, flags);
				} else {
					Jsoner.serialize(ow[i], writableDestination, flags);
					writableDestination.write(",");
				}
			}
			writableDestination.write(']');
		} else if (jsonSerializable instanceof IAgent agent) {
			final SpeciesDescription species = agent.getSpecies().getDescription();

			writableDestination.write('{');
			writableDestination.write('"' + "species" + '"' + ":");
			Jsoner.serialize(species.getSpeciesExpr().getName(), writableDestination, flags);

			for (final String theVar : species.getAttributeNames()) {
				if (!NON_JSONABLE_ATTRIBUTE_NAMES.contains(theVar)) {
					writableDestination.write(',');
					writableDestination.write('"' + theVar + '"');
					writableDestination.write(":");

					Object attrValue = species.getVarExpr(theVar, false).value(agent.getScope());
					if (attrValue instanceof IAgent ia) {
						Jsoner.serialize(ia.getName(), writableDestination, flags);
					} else {
						Jsoner.serialize(attrValue, writableDestination, flags);
					}
				}
			}
			writableDestination.write('}');

		} else if (jsonSerializable instanceof IShape agentOrIShape) {
			final StringBuilder specs = new StringBuilder(1 * 20);
			final String geomType = GeometryUtils.getGeometryStringType(Arrays.asList(agentOrIShape));
			specs.append("geometry:" + geomType);
			try {
				final SimpleFeatureType type = DataUtilities.createType("geojson", specs.toString());
				final SimpleFeatureBuilder builder = new SimpleFeatureBuilder(type);
				final SimpleFeature ff = builder.buildFeature("");

				SaveStatement.buildFeature(null, ff, agentOrIShape, null, Collections.emptyList());

				final FeatureJSON io = new FeatureJSON(new GeometryJSON(20));
				writableDestination.write(io.toString(ff));
			} catch (ClassCastException | SchemaException e) {
				e.printStackTrace();
			}
		} else if (jsonSerializable instanceof Exception ex) {
			writableDestination.write('{');

			writableDestination.write("\"exception\": \"" + ex.getClass().getName() + "\",");
			writableDestination.write("\"message\": \"" + escape(ex.getMessage()) + "\",");
			writableDestination.write("\"stack\": [");
			int i = 0;
			for (var trace : ex.getStackTrace()) {
				writableDestination
						.write("\"" + escape(trace.toString()) + "\"" + (i < ex.getStackTrace().length - 1 ? "," : "") // add
																														// trailing
																														// comma
						);
				i++;
			}
			writableDestination.write("]");
			writableDestination.write("}");
		} else {
			try {
				writableDestination.write(streamConverter.convertObjectToJSONStream(null, jsonSerializable));
			} catch (Exception e) {
				/* It cannot by any measure be safely serialized according to specification. */
				if (!flags.contains(SerializationOptions.ALLOW_INVALIDS)) /*
																			 * Notify the caller the cause of failure
																			 * for the serialization.
																			 */
					throw new IllegalArgumentException("Encountered a: " + jsonSerializable.getClass().getName()
							+ " as: " + jsonSerializable.toString()
							+ "  that isn't JSON serializable.\n  Try:\n    1) Implementing the Jsonable interface for the object to return valid JSON. If it already does it probably has a bug.\n    2) If you cannot edit the source of the object or couple it with this library consider wrapping it in a class that does implement the Jsonable interface.\n    3) Otherwise convert it to a boolean, null, number, JsonArray, JsonObject, or String value before serializing it.\n    4) If you feel it should have serialized you could use a more tolerant serialization for debugging purposes.");
				/* Can be helpful for debugging how it isn't valid. */
				writableDestination.write(jsonSerializable.toString());
			}
		}

		// else {
		// /* It cannot by any measure be safely serialized according to specification. */
		// if (flags.contains(SerializationOptions.ALLOW_INVALIDS)) {
		// /* Can be helpful for debugging how it isn't valid. */
		// writableDestination.write(jsonSerializable.toString());
		// } else {
		// /* Notify the caller the cause of failure for the serialization. */
		// throw new IllegalArgumentException("Encountered a: " + jsonSerializable.getClass().getName() + " as: "
		// + jsonSerializable.toString()
		// + " that isn't JSON serializable.\n Try:\n 1) Implementing the Jsonable interface for the object to return
		// valid JSON. If it already does it probably has a bug.\n 2) If you cannot edit the source of the object or
		// couple it with this library consider wrapping it in a class that does implement the Jsonable interface.\n 3)
		// Otherwise convert it to a boolean, null, number, JsonArray, JsonObject, or String value before serializing
		// it.\n 4) If you feel it should have serialized you could use a more tolerant serialization for debugging
		// purposes.");
		// }
		// }
		// System.out.println(writableDestination.toString());
	}

	/** The Constant NON_SAVEABLE_ATTRIBUTE_NAMES. */
	private static final Set<String> NON_JSONABLE_ATTRIBUTE_NAMES =
			new HashSet<>(Arrays.asList(IKeyword.PEERS, IKeyword.HOST, IKeyword.AGENTS, IKeyword.MEMBERS));

	/**
	 * Serializes like the first version of this library. It has been adapted to use Jsonable for serializing custom
	 * objects, but otherwise works like the old JSON string serializer. It will allow non-JSON values in its output
	 * like the old one. It can be helpful for last resort log statements and debugging errors in self generated JSON.
	 * Anything serialized using this method isn't guaranteed to be deserializable.
	 *
	 * @param jsonSerializable
	 *            represents the object that should be serialized in JSON format.
	 * @param writableDestination
	 *            represents where the resulting JSON text is written to.
	 * @throws IOException
	 *             if the writableDestination encounters an I/O problem, like being closed while in use.
	 */
	public static void serializeCarelessly(final Object jsonSerializable, final Writer writableDestination)
			throws IOException {
		Jsoner.serialize(jsonSerializable, writableDestination,
				EnumSet.of(SerializationOptions.ALLOW_JSONABLES, SerializationOptions.ALLOW_INVALIDS));
	}

	/**
	 * Serializes JSON values and only JSON values according to the RFC 4627 JSON specification.
	 *
	 * @param jsonSerializable
	 *            represents the object that should be serialized in JSON format.
	 * @param writableDestination
	 *            represents where the resulting JSON text is written to.
	 * @throws IOException
	 *             if the writableDestination encounters an I/O problem, like being closed while in use.
	 * @throws IllegalArgumentException
	 *             if the jsonSerializable isn't serializable in raw JSON.
	 */
	public static void serializeStrictly(final Object jsonSerializable, final Writer writableDestination)
			throws IOException {
		Jsoner.serialize(jsonSerializable, writableDestination, EnumSet.noneOf(SerializationOptions.class));
	}
}
