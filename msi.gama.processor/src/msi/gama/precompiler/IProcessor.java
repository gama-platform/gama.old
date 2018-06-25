package msi.gama.precompiler;

import java.lang.annotation.Annotation;

public interface IProcessor<T extends Annotation> {

	default void process(final ProcessorContext context) {}

	void serialize(final ProcessorContext context, final StringBuilder sb);

	default public String getInitializationMethodName() {
		return null;
	}

	default String getExceptions() {
		return "";
	}

	default boolean outputToJava() {
		return true;
	}

	boolean hasElements();

}
