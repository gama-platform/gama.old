package msi.gama.precompiler;

import java.lang.annotation.Annotation;

public interface IProcessor<T extends Annotation> {

	public static final IProcessor<Annotation> NULL = e -> {};

	void process(ProcessorContext environment);

}
