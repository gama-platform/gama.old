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

public class GSDisplayUtil {
	
	public static String prettyPrint(final Collection<?> theCollection, String elementSeparator) {
		return prettyPrint(theCollection, elementSeparator, 0);
	}
	
	public static String prettyPrint(final Collection<?> theCollection, String elementSeparator, int numberOfEntry) {
		
		Class<?> clazz = theCollection.getClass();
		numberOfEntry = numberOfEntry <= 0 ? theCollection.size() : numberOfEntry;  
		
		switch (clazz.getCanonicalName()) {
		case "List":
			return prettyPrintList((List<?>) theCollection, elementSeparator, numberOfEntry);
		case "Map":
			return prettyPrintMap((Map<?,?>)theCollection, elementSeparator, numberOfEntry);
		case "Set":
			return prettyPrintSet((Set<?>)theCollection, elementSeparator, numberOfEntry);
		default:
			return theCollection.stream()
					.limit(numberOfEntry)
					.map(e -> e.toString())
					.collect(Collectors.joining(elementSeparator));
		}
		
	}
	
	public static String prettyPrintList(final List<?> theList, String elementSeparator, int numberOfEntry) {
		
		return theList.stream()
				.limit(numberOfEntry)
				.map(e -> e.toString())
				.collect(Collectors.joining(elementSeparator));
		
	}
	
	public static String prettyPrintMap(final Map<?,?> theMap, String elementSeparator, int numberOfEntry) {
		
		return theMap.entrySet().stream()
				.limit(numberOfEntry)
				.map(e -> '"'+e.getKey().toString()+"="+e.getValue().toString()+'"')
				.collect(Collectors.joining(elementSeparator));
		
	}
	
	public static String prettyPrintSet(final Set<?> theSet, String elementSeparator, int numberOfEntry) {
		
		return theSet.stream()
				.limit(numberOfEntry)
				.map(e -> e.toString())
				.collect(Collectors.joining(elementSeparator));
		
		
	}
	
	// ---------------------- PRINTLN
	
	/*
	 * 
	 */
	public static void println(Object caller, Object toString) {
		if(toString == null)
			println(caller, null);
		else
			println(caller, toString.toString());
	}
	
	/*
	 * 
	 */
	public static void println(Object caller, Object toString, Object... variableInMessage) {
		if(toString == null)
			println(caller, null, variableInMessage);
		else
			println(caller, toString.toString(), variableInMessage);
	}
	
	/*
	 * 
	 */
	public static void println(Object caller, String message) {
		println(caller.getClass().getSimpleName(), message);
	}
	
	/*
	 * 
	 */
	public static void println(Object caller, String message, Object... variableInMessage) {
		println(caller.getClass().getSimpleName(), message, variableInMessage);
	}
	
	/*
	 * 
	 */
	public static void println(Class<?> clazz, String methodCaller, String message) {
		
		Method mCaller = Collections.min(Arrays.asList(clazz.getDeclaredMethods()), 
				Comparator.comparing(method -> LevenshteinDistance.getDefaultInstance().apply(method.getName(), methodCaller)));
		
		println(clazz.getSimpleName() + GSKeywords.SERIALIZE_KEY_VALUE_SEPARATOR + mCaller.getName(), message);
	}
	
	/*
	 * 
	 */
	public static void println(Class<?> clazz, String methodCaller, String message, Object... variableInMessage) {
		
		Method mCaller = Collections.min(Arrays.asList(clazz.getDeclaredMethods()), 
				Comparator.comparing(method -> LevenshteinDistance.getDefaultInstance().apply(method.getName(), methodCaller)));
		
		println(clazz.getSimpleName() + GSKeywords.SERIALIZE_KEY_VALUE_SEPARATOR + mCaller.getName(), message, variableInMessage);
	}

	
	// CORE
	
	/**
	 * Identifier into [] : followed by the message
	 * 
	 * @param id
	 * @param message
	 */
	public static void println(String id, String message) {
		System.out.println("["+ id +"] : "+ message);
	}
	
	/**
	 * The variable to put at end should replace '{}' in the message in the same order of appearance
	 * 
	 * @param caller
	 * @param message
	 * @param variableInMessage
	 */
	public static void println(String caller, String message, Object... variableInMessage) {
		String the_message = "";
		String[] parts = message.split("{}");
		
		for(int i = 0; i < parts.length; i++) {
			String iVar = "";
			try {
				iVar = variableInMessage[i].toString();
			} catch (NullPointerException e) {
				// TODO: handle exception
			}
			the_message += parts + iVar;
		}
		
		println(caller, the_message);
	}
	
}
