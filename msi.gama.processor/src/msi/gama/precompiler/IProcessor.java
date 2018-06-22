package msi.gama.precompiler;

import java.lang.annotation.Annotation;

public interface IProcessor<T extends Annotation> {

	public static final IProcessor<Annotation> NULL = (context, sb) -> {};

	default void process(final ProcessorContext context) {}

	void serialize(final ProcessorContext context, final StringBuilder sb);

	default public String getInitializationMethodName() {
		return null;
	}

	default String getExceptions() {
		return "";
	}

}
