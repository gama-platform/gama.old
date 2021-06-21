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

	default void process(final ProcessorContext context) {}

	/*
	 * Returns the name of the initialization method in which the elements will be written
	 *
	 * @return a string or null if no output to Java
	 */
	default String getInitializationMethodName() {
		return null;
	}

	/*
	 * The exceptions that should decorate the initialization method
	 */
	default String getExceptions() {
		return "";
	}

	/*
	 * Returns whether or not this processor produces Java code
	 */
	default boolean outputToJava() {
		return true;
	}

	/*
	 * Returns whether or not this processor has elements to process
	 *
	 */
	boolean hasElements();

	void serialize(ProcessorContext context, Collection<StringBuilder> elements, StringBuilder sb);

	void writeJavaBody(StringBuilder sb, ProcessorContext context);

}
