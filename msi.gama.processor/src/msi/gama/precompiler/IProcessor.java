package msi.gama.precompiler;

import java.lang.annotation.Annotation;
import java.util.Collection;

public interface IProcessor<T extends Annotation> {

	default void process(final ProcessorContext context) {}

	default String getInitializationMethodName() {
		return null;
	}

	default String getExceptions() {
		return "";
	}

	default boolean outputToJava() {
		return true;
	}

	boolean hasElements();

	void serialize(ProcessorContext context, Collection<StringBuilder> elements, StringBuilder sb);

	void writeJavaBody(StringBuilder sb, ProcessorContext context);

}
