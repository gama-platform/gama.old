package msi.gama.precompiler;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import org.w3c.dom.NodeList;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.file;

public class FileProcessor extends ElementProcessor<file> {

	@Override
	protected void populateElement(final ProcessorContext context, final Element e, final file f,
			final org.w3c.dom.Element node) {
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
		node.setAttribute("name", f.name());
		node.setAttribute("class", rawNameOf(context, e));
		node.setAttribute("type", String.valueOf(f.buffer_type()));
		node.setAttribute("contents", String.valueOf(f.buffer_content()));
		node.setAttribute("index", String.valueOf(f.buffer_index()));
		node.setAttribute("suffixes", arrayToString(f.extensions()));
		for (final Element m : e.getEnclosedElements()) {
			if (m.getKind() == ElementKind.CONSTRUCTOR) {
				final ExecutableElement ex = (ExecutableElement) m;
				final List<? extends VariableElement> argParams = ex.getParameters();
				// The first parameter must be IScope
				final int n = argParams.size();
				if (n <= 1) {
					continue;
				}
				final String[] args = new String[n - 1];
				for (int i = 1; i < n; i++) {
					args[i - 1] = rawNameOf(context, argParams.get(i));
				}
				final org.w3c.dom.Element child = document.createElement("constructor");
				child.setAttribute("args", arrayToString(args));
				appendChild(node, child);

			}
		}
	}

	@Override
	protected Class<file> getAnnotationClass() {
		return file.class;
	}

	@Override
	protected void populateJava(final ProcessorContext context, final StringBuilder sb,
			final org.w3c.dom.Element node) {
		final String name = toJavaString(node.getAttribute("name"));
		final String clazz = node.getAttribute("class");
		final String type = toType(node.getAttribute("type"));
		final String contentType = toType(node.getAttribute("contents"));
		final String keyType = toType(node.getAttribute("index"));
		final String suffixes = toArrayOfStrings(node.getAttribute("suffixes"));
		final String helper = buildFileConstructor(new String[] { "String" }, clazz);
		sb.append(in).append("_file(").append(name).append(',').append(toClassObject(clazz)).append(',').append(helper)
				.append(",").append(type).append(",").append(keyType).append(",").append(contentType).append(",")
				.append(suffixes).append(");");
		writeIsFileOperator(sb, name);
		final NodeList list = node.getElementsByTagName("*");
		for (int i = 0; i < list.getLength(); i++) {
			final org.w3c.dom.Element child = (org.w3c.dom.Element) list.item(i);
			writeCreateFileOperator(context, sb, name, clazz, child.getAttribute("args"), contentType, keyType);
		}
	}

	private void writeCreateFileOperator(final ProcessorContext context, final StringBuilder sb, final String name,
			final String clazz, final String classes, final String contentType, final String keyType) {
		final String[] names = classes.split(",");
		final String helper = buildFileConstructor(names, clazz);
		String classNames = "C(";
		for (int i = 0; i < names.length; i++) {
			classNames += toClassObject(names[i]);
			if (i < names.length - 1) {
				classNames += ",";
			}
		}
		classNames += ")";
		// AD 13/04/14: Changed true to false in the "can_be_const" parameter
		sb.append(in).append("_operator(S(").append(name).append("+").append(toJavaString("_file")).append("),")
				.append(buildConstructor(names, clazz)).append(',').append(classNames).append(",I(0),GF,false,")
				.append(name).append(",").append(helper).append(");");
	}

	protected String buildConstructor(final String[] classes, final String className) {
		String result = toClassObject(className) + ".getConstructor(";
		result += toClassObject(ISCOPE) + ",";
		for (final String classe : classes) {
			result += toClassObject(classe) + ",";
		}
		if (result.endsWith(",")) {
			result = result.substring(0, result.length() - 1);
		}
		result += ")";
		return result;
	}

	private void writeIsFileOperator(final StringBuilder sb, final String name) {
		final String helper = concat("new GamaHelper(){", OVERRIDE, "public Boolean run(", ISCOPE,
				" s,Object... o) { return GamaFileType.verifyExtension(", name, ",(String)o[0]);}}");
		sb.append(in).append("_operator(S(").append(toJavaString("is_")).append("+").append(name)
				.append("),null,C(S),I(0),B,true,3,0,0,0,").append(helper).append(");");
	}

	protected String buildFileConstructor(final String[] classes, final String className) {
		String body = concat("new GamaHelper(){", OVERRIDE, "public IGamaFile run(", ISCOPE,
				" s,Object... o) {return new ", className, "(s");
		for (int i = 0; i < classes.length; i++) {
			body += ",";
			body += param(classes[i], "o[" + i + "]");
		}
		body += ");}}";
		return body;
	}

	@Override
	public String getExceptions() {
		return "throws SecurityException, NoSuchMethodException";
	}

}
