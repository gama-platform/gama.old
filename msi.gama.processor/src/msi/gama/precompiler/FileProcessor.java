package msi.gama.precompiler;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import msi.gama.precompiler.FileProcessor.Fi;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.file;

public class FileProcessor extends ElementProcessor<file, Fi> {

	public static class Fi {

		public String name;
		public String clazz;
		public int type;
		public int contents;
		public int index;
		public String[] suffixes;
		public List<String[]> constructors = new ArrayList<>();

	}

	final static String[] STRING_ARRAY = new String[] { "String" };

	@Override
	public void createJava(final ProcessorContext context, final StringBuilder sb, final Fi node) {
		sb.append(in).append("_file(").append(toJavaString(node.name)).append(',').append(toClassObject(node.clazz))
				.append(',');
		buildFileConstructor(sb, STRING_ARRAY, node.clazz);
		sb.append(",").append(node.type).append(",").append(node.index).append(",").append(node.contents).append(",");
		toArrayOfStrings(node.suffixes, sb).append(");");
		sb.append(in).append("_operator(S(").append(toJavaString("is_" + node.name))
				.append("),null,C(S),I(0),B,true,3,0,0,0,").append("new GamaHelper(){").append(OVERRIDE)
				.append("public Boolean run(").append(ISCOPE)
				.append(" s,Object... o) { return GamaFileType.verifyExtension(").append(toJavaString(node.name))
				.append(",(String)o[0]);}});");
		for (int i = 0; i < node.constructors.size(); i++) {
			writeCreateFileOperator(context, sb, node.name, node.clazz, node.constructors.get(i), node.contents,
					node.index);
		}
	}

	@Override
	public Fi createElement(final ProcessorContext context, final Element e, final file f) {
		final Fi node = new Fi();
		verifyDoc(context, e, f);
		node.name = f.name();
		node.clazz = rawNameOf(context, e.asType());
		node.type = f.buffer_type();
		node.contents = f.buffer_content();
		node.index = f.buffer_index();
		node.suffixes = f.extensions();
		for (final Element m : e.getEnclosedElements()) {
			if (m.getKind() == ElementKind.CONSTRUCTOR) {
				final ExecutableElement ex = (ExecutableElement) m;
				final List<? extends VariableElement> argParams = ex.getParameters();
				// The first parameter must be IScope
				// TODO: VERIFY THIS HERE
				final int n = argParams.size();
				if (n <= 1) {
					continue;
				}
				final String[] args = new String[n - 1];
				for (int i = 1; i < n; i++) {
					args[i - 1] = rawNameOf(context, argParams.get(i).asType());
				}
				node.constructors.add(args);
			}
		}
		return node;
	}

	private void verifyDoc(final ProcessorContext context, final Element e, final file f) {
		final doc[] docs = f.doc();
		doc d;
		if (docs.length == 0) {
			d = e.getAnnotation(doc.class);
		} else {
			d = docs[0];
		}
		if (d == null) {
			context.emitWarning("GAML: file declaration '" + f.name() + "' is not documented", e);
		}
	}

	@Override
	protected Class<file> getAnnotationClass() {
		return file.class;
	}

	private void writeCreateFileOperator(final ProcessorContext context, final StringBuilder sb, final String name,
			final String clazz, final String[] names, final int contents, final int index) {
		// AD 13/04/14: Changed true to false in the "can_be_const" parameter
		sb.append(in).append("_operator(S(").append(toJavaString(name + "_file")).append("),")
				.append(toClassObject(clazz)).append(".getConstructor(").append(toClassObject(ISCOPE)).append(',');
		for (final String classe : names) {
			sb.append(toClassObject(classe)).append(',');
		}
		sb.setLength(sb.length() - 1);
		sb.append("),C(");
		for (int i = 0; i < names.length; i++) {
			sb.append(toClassObject(names[i]));
			if (i < names.length - 1) {
				sb.append(',');
			}
		}
		sb.append("),I(0),GF,false,").append(toJavaString(name)).append(',');
		buildFileConstructor(sb, names, clazz);
		sb.append(");");
	}

	protected void buildFileConstructor(final StringBuilder sb, final String[] classes, final String className) {
		sb.append("new GamaHelper(){").append(OVERRIDE).append("public IGamaFile run(").append(ISCOPE)
				.append(" s,Object... o) {return new ").append(className).append("(s");
		for (int i = 0; i < classes.length; i++) {
			sb.append(',');
			param(sb, classes[i], "o[" + i + "]");
		}
		sb.append(");}}");
	}

	@Override
	public String getExceptions() {
		return "throws SecurityException, NoSuchMethodException";
	}

}
