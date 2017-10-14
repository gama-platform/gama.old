package msi.gama.precompiler;

import static msi.gama.precompiler.java.JavaWriter.SEP;
import static msi.gama.precompiler.java.JavaWriter.SYMBOL_PREFIX;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.serializer;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;

public class SymbolProcessor implements IProcessor<symbol> {

	@Override
	public void process(final ProcessorContext environment) {
		/**
		 * Computes the representation of symbols. Format: prefix 0.kind 1.class 2.remote 3.with_args 4.with_scope
		 * 5.with_sequence 6.symbols_inside 7.kinds_inside 8.nbFacets 9.[facet]* 10.omissible 11.[name$]*
		 * 
		 * @param env
		 */
		final List<? extends Element> symbols = environment.sortElements(symbol.class);
		for (final Element e : symbols) {
			final StringBuilder sb = new StringBuilder();
			final symbol symbol = e.getAnnotation(symbol.class);
			validator validator = e.getAnnotation(validator.class);
			serializer serializer = e.getAnnotation(serializer.class);
			TypeMirror sup = ((TypeElement) e).getSuperclass();
			// Workaround for bug
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=419944
			// Effectively inherits from a given validator
			while (validator == null && sup != null) {

				if (sup.getKind().equals(TypeKind.NONE)) {
					sup = null;
					continue;
				}
				final TypeElement te = (TypeElement) environment.getTypeUtils().asElement(sup);
				validator = te.getAnnotation(validator.class);
				sup = te.getSuperclass();
			}
			sup = ((TypeElement) e).getSuperclass();
			while (serializer == null && sup != null) {

				if (sup.getKind().equals(TypeKind.NONE)) {
					sup = null;
					continue;
				}
				final TypeElement te = (TypeElement) environment.getTypeUtils().asElement(sup);
				serializer = te.getAnnotation(serializer.class);
				sup = te.getSuperclass();
			}
			TypeMirror type_validator = null;
			// getting the class present in validator
			try {
				if (validator != null) {
					validator.value();
				}
			} catch (final MirroredTypeException e1) {
				type_validator = e1.getTypeMirror();
			} catch (final MirroredTypesException e1) {
				type_validator = e1.getTypeMirrors().get(0);
			}
			TypeMirror type_serializer = null;
			// getting the class present in serializer
			try {
				if (serializer != null) {
					serializer.value();
				}
			} catch (final MirroredTypeException e1) {
				type_serializer = e1.getTypeMirror();
			} catch (final MirroredTypesException e1) {
				type_serializer = e1.getTypeMirrors().get(0);
			}

			// prefix

			sb.append(SYMBOL_PREFIX);
			// validator
			sb.append(type_validator == null ? "" : environment.rawNameOf(type_validator)).append(SEP);
			// serializer
			sb.append(type_serializer == null ? "" : environment.rawNameOf(type_serializer)).append(SEP);
			// kind
			sb.append(symbol.kind()).append(SEP);
			// class
			sb.append(environment.rawNameOf(e)).append(SEP);
			// remote
			sb.append(symbol.remote_context()).append(SEP);
			// with_args
			sb.append(symbol.with_args()).append(SEP);
			// with_scope
			sb.append(symbol.with_scope()).append(SEP);
			// with_sequence
			sb.append(symbol.with_sequence()).append(SEP);
			// unique_in_context
			sb.append(symbol.unique_in_context()).append(SEP);
			// name_unique
			sb.append(symbol.unique_name()).append(SEP);
			final inside inside = e.getAnnotation(inside.class);
			// symbols_inside && kinds_inside
			if (inside != null) {
				final String[] parentSymbols = inside.symbols();
				for (int i = 0; i < parentSymbols.length; i++) {
					if (i > 0) {
						sb.append(',');
					}
					sb.append(parentSymbols[i]);
				}
				sb.append(SEP);
				final int[] parentKinds = inside.kinds();
				for (int i = 0; i < parentKinds.length; i++) {
					if (i > 0) {
						sb.append(',');
					}
					sb.append(parentKinds[i]);
				}
				sb.append(SEP);

			} else {
				sb.append(SEP).append(SEP);
			}
			final facets facets = e.getAnnotation(facets.class);
			// facets
			if (facets == null) {
				sb.append('0').append(SEP).append(SEP).append(SEP);
			} else {
				sb.append(facets.value().length).append(SEP);
				sb.append(environment.facetsToString(facets, e)).append(SEP);
				sb.append(facets.omissible()).append(SEP);
			}
			// names
			for (final String s : symbol.name()) {
				sb.append(s).append(SEP);
			}
			sb.setLength(sb.length() - 1);
			final doc doc = e.getAnnotation(doc.class);

			if (doc == null && !symbol.internal()) {
				environment.emitWarning("GAML: symbol '" + symbol.name()[0] + "' is not documented", e);
			}
			environment.getProperties().put(sb.toString(), "");
		}
	}

}
