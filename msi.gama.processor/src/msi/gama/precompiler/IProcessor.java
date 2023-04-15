/*******************************************************************************************************
 *
 * IProcessor.java, in msi.gama.processor, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.precompiler;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * Individual processors taking care of specific annotations (class parameter). They are fed with the annotated elements
 * and expected to produce java source code (if they return true to outputToJava()) that fills a method
 *
 * @author Alexis Drogoul
 *
 * @param <T>
 */
public interface IProcessor<T extends Annotation> {

	/**
	 * Process.
	 *
	 * @param context the context
	 */
	default void process(final ProcessorContext context) {}

	/**
	 * Gets the initialization method name.
	 *
	 * @return the initialization method name
	 */
	/*
	 * Returns the name of the initialization method in which the elements will be written
	 *
	 * @return a string or null if no output to Java
	 */
	default String getInitializationMethodName() {
		return null;
	}

	/**
	 * Gets the exceptions.
	 *
	 * @return the exceptions
	 */
	/*
	 * The exceptions that should decorate the initialization method
	 */
	default String getExceptions() {
		return "";
	}

	/**
	 * Output to java.
	 *
	 * @return true, if successful
	 */
	/*
	 * Returns whether or not this processor produces Java code
	 */
	default boolean outputToJava() {
		return true;
	}

	/**
	 * Checks for elements.
	 *
	 * @return true, if successful
	 */
	/*
	 * Returns whether or not this processor has elements to process
	 *
	 */
	boolean hasElements();

	/**
	 * Serialize.
	 *
	 * @param context the context
	 * @param elements the elements
	 * @param sb the sb
	 */
	void serialize(ProcessorContext context, Collection<StringBuilder> elements, StringBuilder sb);

	/**
	 * Write java body.
	 *
	 * @param sb the sb
	 * @param context the context
	 */
	void writeJavaBody(StringBuilder sb, ProcessorContext context);

}
