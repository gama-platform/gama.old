/*******************************************************************************************************
 *
 * GSDisplayUtil.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package core.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.text.similarity.LevenshteinDistance;

import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class GSDisplayUtil.
 */
public class GSDisplayUtil {

	/**
	 * GSD isplay util.
	 */
	private GSDisplayUtil() {}

	/**
	 * Pretty print.
	 *
	 * @param theCollection
	 *            the the collection
	 * @param elementSeparator
	 *            the element separator
	 * @return the string
	 */
	public static String prettyPrint(final Collection<?> theCollection, final String elementSeparator) {
		return prettyPrint(theCollection, elementSeparator, 0);
	}

	/**
	 * Pretty print.
	 *
	 * @param theCollection
	 *            the the collection
	 * @param elementSeparator
	 *            the element separator
	 * @param numberOfEntry
	 *            the number of entry
	 * @return the string
	 */
	public static String prettyPrint(final Collection<?> theCollection, final String elementSeparator,
			int numberOfEntry) {

		Class<?> clazz = theCollection.getClass();
		numberOfEntry = numberOfEntry <= 0 ? theCollection.size() : numberOfEntry;

		return switch (clazz.getCanonicalName()) {
			case "List" -> prettyPrintList((List<?>) theCollection, elementSeparator, numberOfEntry);
			case "Map" -> prettyPrintMap((Map<?, ?>) theCollection, elementSeparator, numberOfEntry);
			case "Set" -> prettyPrintSet((Set<?>) theCollection, elementSeparator, numberOfEntry);
			default -> theCollection.stream().limit(numberOfEntry).map(Object::toString)
					.collect(Collectors.joining(elementSeparator));
		};

	}

	/**
	 * Pretty print list.
	 *
	 * @param theList
	 *            the the list
	 * @param elementSeparator
	 *            the element separator
	 * @param numberOfEntry
	 *            the number of entry
	 * @return the string
	 */
	public static String prettyPrintList(final List<?> theList, final String elementSeparator,
			final int numberOfEntry) {

		return theList.stream().limit(numberOfEntry).map(Object::toString)
				.collect(Collectors.joining(elementSeparator));

	}

	/**
	 * Pretty print map.
	 *
	 * @param theMap
	 *            the the map
	 * @param elementSeparator
	 *            the element separator
	 * @param numberOfEntry
	 *            the number of entry
	 * @return the string
	 */
	public static String prettyPrintMap(final Map<?, ?> theMap, final String elementSeparator,
			final int numberOfEntry) {

		return theMap.entrySet().stream().limit(numberOfEntry)
				.map(e -> '"' + e.getKey().toString() + "=" + e.getValue().toString() + '"')
				.collect(Collectors.joining(elementSeparator));

	}

	/**
	 * Pretty print set.
	 *
	 * @param theSet
	 *            the the set
	 * @param elementSeparator
	 *            the element separator
	 * @param numberOfEntry
	 *            the number of entry
	 * @return the string
	 */
	public static String prettyPrintSet(final Set<?> theSet, final String elementSeparator, final int numberOfEntry) {

		return theSet.stream().limit(numberOfEntry).map(Object::toString).collect(Collectors.joining(elementSeparator));

	}

	// ---------------------- PRINTLN

	/**
	 * Println.
	 *
	 * @param caller
	 *            the caller
	 * @param toString
	 *            the to string
	 */
	/*
	 *
	 */
	public static void println(final Object caller, final Object toString) {
		if (toString == null) {
			println(caller, null);
		} else {
			println(caller, toString.toString());
		}
	}

	/**
	 * Println.
	 *
	 * @param caller
	 *            the caller
	 * @param toString
	 *            the to string
	 * @param variableInMessage
	 *            the variable in message
	 */
	/*
	 *
	 */
	public static void println(final Object caller, final Object toString, final Object... variableInMessage) {
		if (toString == null) {
			println(caller, null, variableInMessage);
		} else {
			println(caller, toString.toString(), variableInMessage);
		}
	}

	/**
	 * Println.
	 *
	 * @param caller
	 *            the caller
	 * @param message
	 *            the message
	 */
	/*
	 *
	 */
	public static void println(final Object caller, final String message) {
		println(caller.getClass().getSimpleName(), message);
	}

	/**
	 * Println.
	 *
	 * @param caller
	 *            the caller
	 * @param message
	 *            the message
	 * @param variableInMessage
	 *            the variable in message
	 */
	/*
	 *
	 */
	public static void println(final Object caller, final String message, final Object... variableInMessage) {
		println(caller.getClass().getSimpleName(), message, variableInMessage);
	}

	/**
	 * Println.
	 *
	 * @param clazz
	 *            the clazz
	 * @param methodCaller
	 *            the method caller
	 * @param message
	 *            the message
	 */
	/*
	 *
	 */
	public static void println(final Class<?> clazz, final String methodCaller, final String message) {

		Method mCaller = Collections.min(Arrays.asList(clazz.getDeclaredMethods()), Comparator
				.comparing(method -> LevenshteinDistance.getDefaultInstance().apply(method.getName(), methodCaller)));

		println(clazz.getSimpleName() + GSKeywords.SERIALIZE_KEY_VALUE_SEPARATOR + mCaller.getName(), message);
	}

	/**
	 * Println.
	 *
	 * @param clazz
	 *            the clazz
	 * @param methodCaller
	 *            the method caller
	 * @param message
	 *            the message
	 * @param variableInMessage
	 *            the variable in message
	 */
	/*
	 *
	 */
	public static void println(final Class<?> clazz, final String methodCaller, final String message,
			final Object... variableInMessage) {

		Method mCaller = Collections.min(Arrays.asList(clazz.getDeclaredMethods()), Comparator
				.comparing(method -> LevenshteinDistance.getDefaultInstance().apply(method.getName(), methodCaller)));

		println(clazz.getSimpleName() + GSKeywords.SERIALIZE_KEY_VALUE_SEPARATOR + mCaller.getName(), message,
				variableInMessage);
	}

	// CORE

	/**
	 * Identifier into [] : followed by the message
	 *
	 * @param id
	 * @param message
	 */
	public static void println(final String id, final String message) {
		DEBUG.OUT("[" + id + "] : " + message);
	}

	/**
	 * The variable to put at end should replace '{}' in the message in the same order of appearance
	 *
	 * @param caller
	 * @param message
	 * @param variableInMessage
	 */
	public static void println(final String caller, final String message, final Object... variableInMessage) {
		StringBuilder theMessage = new StringBuilder();
		String[] parts = message.split("{}");

		for (int i = 0; i < parts.length; i++) {
			String iVar = "";
			try {
				iVar = variableInMessage[i].toString();
			} catch (NullPointerException e) {
				// handle exception
			}
			theMessage.append(parts).append(iVar);
		}
		println(caller, theMessage.toString());
	}

}
