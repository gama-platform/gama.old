package msi.gama.precompiler;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.file;

public class FileProcessor extends ElementProcessor<file> {

	final static String[] STRING_ARRAY = new String[] { "String" };

	@Override
	public void createElement(final StringBuilder sb, final ProcessorContext context, final Element e, final file f) {
		verifyDoc(context, e, f);
		final String clazz = rawNameOf(context, e.asType());
		sb.append(in).append("_file(").append(toJavaString(f.name())).append(',').append(toClassObject(clazz))
				.append(',');
		buildFileConstructor(sb, STRING_ARRAY, clazz);
		sb.append(",").append(f.buffer_type()).append(",").append(f.buffer_index()).append(",")
				.append(f.buffer_content()).append(",");
		toArrayOfStrings(f.extensions(), sb).append(");");
		sb.append(in).append("_operator(S(").append(toJavaString("is_" + f.name()))
				.append("),null,C(S),I(0),B,true,3,0,0,0,").append("(s,o)-> { return GamaFileType.verifyExtension(")
				.append(toJavaString(f.name())).append(",(String)o[0]);});");
		for (final Element m : e.getEnclosedElements()) {
			if (m.getKind() == ElementKind.CONSTRUCTOR) {
				final List<? extends VariableElement> argParams = ((ExecutableElement) m).getParameters();
				final int n = argParams.size();
				if (n <= 1) {
					continue;
				}
				// If the first parameter is not IScope, we consider it is not a constructor usable in GAML
				final String scope = rawNameOf(context, argParams.get(0).asType());
				if (!scope.contains("IScope")) {
					continue;
				}
				verifyDoc(context, m, f.name());
				final String[] args = new String[n - 1];
				for (int i = 1; i < n; i++) {
					args[i - 1] = rawNameOf(context, argParams.get(i).asType());
				}
				writeCreateFileOperator(context, sb, f.name(), clazz, args, f.buffer_content(), f.buffer_index());
			}
		}
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

	private void verifyDoc(final ProcessorContext context, final Element e, final String fileName) {
		final doc d = e.getAnnotation(doc.class);
		if (d == null) {
			context.emitWarning("GAML: this constructor of " + fileName + "_files is not documented", e);
		}
	}

	@Override
	protected Class<file> getAnnotationClass() {
		return file.class;
	}

	private void writeCreateFileOperator(final ProcessorContext context, final StringBuilder sb, final String name,
			final String clazz, final String[] names, final int contents, final int index) {
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
		sb.append("(s,o)-> {return new ").append(className).append("(s");
		for (int i = 0; i < classes.length; i++) {
			sb.append(',');
			param(sb, classes[i], "o[" + i + "]");
		}
		sb.append(");}");
	}

	@Override
	public String getExceptions() {
		return "throws SecurityException, NoSuchMethodException";
	}

}
