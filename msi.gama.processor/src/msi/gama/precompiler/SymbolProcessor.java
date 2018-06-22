package msi.gama.precompiler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.serializer;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.SymbolProcessor.Sy;
import msi.gama.precompiler.SymbolProcessor.Sy.Facet;

public class SymbolProcessor extends ElementProcessor<symbol, Sy> {

	public static class Sy {

		public String validator;
		public String serializer;
		public String[] names;
		public int kind;
		public String clazz;
		public boolean remote;
		public boolean withArgs;
		public boolean scope;
		public boolean sequence;
		public boolean unique;
		public boolean uniqueName;
		public String[] insideSymbols;
		public int[] insideKinds;
		public List<Facet> facets = new ArrayList<>();
		public String omissible;

		public static class Facet {

			public String name;
			public int[] types;
			public int contents;
			public int index;
			public String[] values;
			public boolean optional;
			public boolean internal;
			public String doc;

		}
	}

	@Override
	public void createJava(final ProcessorContext context, final StringBuilder sb, final Sy node) {
		String validator = node.validator;
		if (validator == null || validator.isEmpty()) {
			validator = "null";
		} else {
			validator = "new " + validator + "()";
		}
		String serializer = node.serializer;
		if (serializer == null || serializer.isEmpty()) {
			serializer = "null";
		} else {
			serializer = "new " + serializer + "()";
		}
		final StringBuilder constants = new StringBuilder();
		final StringBuilder fb = new StringBuilder();
		if (node.facets.isEmpty()) {
			fb.append("null");
		} else {
			fb.append("P(");
			for (int i = 0; i < node.facets.size(); i++) {
				final Facet child = node.facets.get(i);
				if (i > 0) {
					fb.append(',');
				}
				fb.append("new FacetProto(").append(toJavaString(child.name)).append(',');
				toArrayOfInts(child.types, fb).append(',').append(child.contents).append(',').append(child.index)
						.append(',');
				final String[] values = child.values;
				if (values != null && values.length > 0) {
					toArrayOfStrings(values, constants).append(',');
				}
				toArrayOfStrings(values, fb).append(',').append(child.optional).append(',').append(child.internal)
						.append(',').append(toJavaString(escapeDoubleQuotes(child.doc))).append(')');
			}
			fb.append(')');
		}

		sb.append(in).append("_symbol(");
		toArrayOfStrings(node.names, sb).append(',').append(toClassObject(node.clazz)).append(",");
		sb.append(validator).append(',').append(serializer);
		sb.append(",").append(node.kind).append(',').append(node.remote).append(',').append(node.withArgs).append(',')
				.append(node.scope).append(',');
		sb.append(node.sequence).append(',').append(node.unique).append(',').append(node.uniqueName).append(',');
		toArrayOfStrings(node.insideSymbols, sb).append(",");
		toArrayOfInts(node.insideKinds, sb).append(',').append(fb).append(',').append(toJavaString(node.omissible))
				.append(',').append("new ISymbolConstructor() {").append(OVERRIDE).append("public ISymbol create(")
				.append(IDESC).append(" d) {return new ").append(node.clazz).append("(d);}}");
		sb.append(");");
		if (constants.length() > 0) {
			constants.setLength(constants.length() - 1);
			sb.append(ln).append("_constants(").append(constants).append(");");
		}

	}

	final static Set<String> UNDOCUMENTED = new HashSet<>();

	@Override
	public Sy createElement(final ProcessorContext context, final Element e, final symbol symbol) {
		final Sy node = new Sy();
		addValidator(context, e, node);
		addSerializer(context, e, node);
		node.names = symbol.name();
		node.kind = symbol.kind();
		node.clazz = rawNameOf(context, e.asType());
		node.remote = symbol.remote_context();
		node.withArgs = symbol.with_args();
		node.scope = symbol.with_scope();
		node.sequence = symbol.with_sequence();
		node.unique = symbol.unique_in_context();
		node.uniqueName = symbol.unique_name();

		final inside inside = e.getAnnotation(inside.class);
		if (inside != null) {
			node.insideSymbols = inside.symbols();
			node.insideKinds = inside.kinds();
		}
		final facets facets = e.getAnnotation(facets.class);

		if (facets != null) {
			node.omissible = facets.omissible();
			for (final facet facet : facets.value()) {
				final Facet child = new Facet();
				child.name = facet.name();
				child.types = facet.type();
				child.contents = facet.of();
				child.index = facet.index();
				if (facet.values().length > 0) {
					child.values = facet.values();
				}
				child.optional = facet.optional();
				child.internal = facet.internal();
				final doc[] d = facet.doc();
				if (d == null || d.length == 0) {
					if (!facet.internal()) {
						UNDOCUMENTED.add(facet.name());
					}
				} else {
					child.doc = docToString(facet.doc());
				}
				node.facets.add(child);
			}
			if (!UNDOCUMENTED.isEmpty()) {
				context.emitWarning("GAML: facets '" + UNDOCUMENTED + "' are not documented", e);
				UNDOCUMENTED.clear();
			}
		}
		verifyDoc(context, e, symbol);
		return node;
	}

	private void verifyDoc(final ProcessorContext context, final Element e, final symbol symbol) {
		final doc d = e.getAnnotation(doc.class);

		if (d == null && !symbol.internal()) {
			context.emitWarning("GAML: symbol '" + symbol.name()[0] + "' is not documented", e);
		}
	}

	public void addValidator(final ProcessorContext context, final Element e, final Sy node) {
		validator validator = e.getAnnotation(validator.class);
		TypeMirror sup = ((TypeElement) e).getSuperclass();
		// Workaround for bug
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=419944
		// Effectively inherits from a given validator
		while (validator == null && sup != null) {
			if (sup.getKind().equals(TypeKind.NONE)) {
				sup = null;
				continue;
			}
			final TypeElement te = (TypeElement) context.getTypeUtils().asElement(sup);
			validator = te.getAnnotation(validator.class);
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
		if (type_validator != null) {
			node.validator = rawNameOf(context, type_validator);
		}
	}

	public void addSerializer(final ProcessorContext context, final Element e, final Sy node) {
		TypeMirror sup;
		sup = ((TypeElement) e).getSuperclass();
		serializer serializer = e.getAnnotation(serializer.class);
		while (serializer == null && sup != null) {
			if (sup.getKind().equals(TypeKind.NONE)) {
				sup = null;
				continue;
			}
			final TypeElement te = (TypeElement) context.getTypeUtils().asElement(sup);
			serializer = te.getAnnotation(serializer.class);
			sup = te.getSuperclass();
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
		if (type_serializer != null) {
			node.serializer = rawNameOf(context, type_serializer);
		}
	}

	@Override
	protected Class<symbol> getAnnotationClass() {
		return symbol.class;
	}

}
