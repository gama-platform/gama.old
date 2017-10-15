/*********************************************************************************************
 *
 * 'GamaProcessor.java, in plugin msi.gama.processor, is part of the source code of the GAMA modeling and simulation
 * platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.precompiler;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.constant;
import msi.gama.precompiler.GamlAnnotations.display;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.experiment;
import msi.gama.precompiler.GamlAnnotations.factory;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.test;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.java.JavaWriter;

@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaProcessor extends AbstractProcessor {

	private ProcessorContext context;
	final static Map<Class<? extends Annotation>, IProcessor> processors = new LinkedHashMap();
	static {
		processors.put(factory.class, new FactoryProcessor());
		processors.put(type.class, new TypeProcessor());
		processors.put(species.class, new SpeciesProcessor());
		processors.put(skill.class, new SkillProcessor());
		processors.put(operator.class, new OperatorProcessor());
		processors.put(action.class, new ActionProcessor());
		processors.put(symbol.class, new SymbolProcessor());
		processors.put(vars.class, new VarsProcessor());
		processors.put(display.class, new DisplayProcessor());
		processors.put(experiment.class, new ExperimentProcessor());
		processors.put(file.class, new FileProcessor());
		processors.put(constant.class, new ConstantProcessor());
		processors.put(test.class, new TestProcessor());
		processors.put(getter.class, IProcessor.NULL);
		processors.put(setter.class, IProcessor.NULL);
		processors.put(doc.class, new DocProcessor());
	}

	@Override
	public synchronized void init(final ProcessingEnvironment pe) {
		super.init(pe);
		context = new ProcessorContext(pe);
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return processors.keySet().stream().map(p -> p.getCanonicalName()).collect(Collectors.toSet());
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment env) {
		context.setRoundEnvironment(env);
		if (!context.processingOver()) {
			try {
				processors.values().forEach(p -> p.process(context));
			} catch (final Exception e) {
				context.emitWarning("An exception occured in the parsing of GAML annotations: " + e.getMessage(), null);
				throw e;
			}
			context.storeProperties();
			generateJavaSource();
		}
		return true;
	}

	public void generateJavaSource() {
		final Writer source = context.createSourceWriter();
		if (source != null) {
			try {
				final StringBuilder sourceBuilder = new StringBuilder();
				new JavaWriter().write("gaml.additions", context.getProperties(), sourceBuilder);
				source.append(sourceBuilder);
			} catch (final Exception e) {
				context.emitWarning("An exception occured in the generation of Java files: " + e.getMessage(), null);
			}
			try {
				source.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

}
