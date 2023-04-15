/*******************************************************************************************************
 *
 * FileProcessor.java, in msi.gama.processor, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.2).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.precompiler;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import msi.gama.precompiler.GamlAnnotations.file;

/**
 * The Class FileProcessor.
 */
public class FileProcessor extends ElementProcessor<file> {

	/** The Constant STRING_ARRAY. */
	final static String[] STRING_ARRAY = { "String" };

	@Override
	public void createElement(final StringBuilder sb, final ProcessorContext context, final Element e, final file f) {
		verifyDoc(context, e, "file declaration " + f.name(), f);
		final String clazz = rawNameOf(context, e.asType());
		sb.append(in).append("_file(").append(toJavaString(f.name())).append(',').append(toClassObject(clazz))
				.append(',');
		buildUnaryFileConstructor(sb, clazz);
		sb.append(",").append(f.buffer_type()).append(",").append(f.buffer_index()).append(",")
				.append(f.buffer_content()).append(",");
		toArrayOfStrings(f.extensions(), sb).append(");");
		sb.append(in).append("_operator(S(").append(toJavaString("is_" + f.name()))
				.append("),null,I(0),B,true,3,0,0,0,").append("(s,o)-> { return GamaFileType.verifyExtension(")
				.append(toJavaString(f.name())).append(",(String)o[0]);}, false);");
		for (final Element m : e.getEnclosedElements()) {
			if (m.getKind() == ElementKind.CONSTRUCTOR) {
				final List<? extends VariableElement> argParams = ((ExecutableElement) m).getParameters();
				final int n = argParams.size();
				if (n <= 1) { continue; }
				// If the first parameter is not IScope, we consider it is not a constructor usable in GAML
				final String scope = rawNameOf(context, argParams.get(0).asType());
				if (!scope.contains("IScope")) { continue; }
				verifyDoc(context, m, "constructor of " + f.name(), null);
				final String[] args = new String[n - 1];
				int indexOfIType = -1;
				for (int i = 1; i < n; i++) {
					TypeMirror type = argParams.get(i).asType();
					if (context.isIType(type)) { indexOfIType = i; }
					args[i - 1] = rawNameOf(context, argParams.get(i).asType());
				}
				final int content = indexOfIType == -1 ? ITypeProvider.NONE
						: ITypeProvider.DENOTED_TYPE_AT_INDEX + indexOfIType + 1;
				writeCreateFileOperator(context, sb, f.name(), clazz, args, content, f.buffer_content(),
						f.buffer_index());
			}
		}
	}

	@Override
	protected Class<file> getAnnotationClass() { return file.class; }

	/**
	 * Write create file operator.
	 *
	 * @param context
	 *            the context
	 * @param sb
	 *            the sb
	 * @param name
	 *            the name
	 * @param clazz
	 *            the clazz
	 * @param names
	 *            the names
	 * @param forcedContent
	 *            the forced content
	 * @param contents
	 *            the contents
	 * @param index
	 *            the index
	 */
	private void writeCreateFileOperator(final ProcessorContext context, final StringBuilder sb, final String name,
			final String clazz, final String[] names, final int forcedContent, final int contents, final int index) {
		sb.append(in).append("_operator(S(").append(toJavaString(name + "_file")).append("),")
				.append(toClassObject(clazz)).append(".getConstructor(").append(toClassObject(ISCOPE)).append(',');
		for (final String classe : names) { sb.append(toClassObject(classe)).append(','); }
		sb.setLength(sb.length() - 1);
		sb.append("),");
		sb.append(forcedContent);
		// for (int i = 0; i < names.length; i++) {
		// sb.append(toClassObject(names[i]));
		// if (i < names.length - 1) { sb.append(','); }
		// }
		sb.append(",I(0),GF,false,").append(toJavaString(name)).append(',');
		buildFileConstructor(sb, names, clazz);
		sb.append(");");
	}

	/**
	 * Builds the unary file constructor.
	 *
	 * @param sb
	 *            the sb
	 * @param className
	 *            the class name
	 */
	protected void buildUnaryFileConstructor(final StringBuilder sb, final String className) {
		sb.append("(s,o)-> {return new ").append(className).append("(s");
		sb.append(',');
		param(sb, "String", "o[0]");
		sb.append(");}");
	}

	/**
	 * Builds the file constructor.
	 *
	 * @param sb
	 *            the sb
	 * @param classes
	 *            the classes
	 * @param className
	 *            the class name
	 */
	protected void buildFileConstructor(final StringBuilder sb, final String[] classes, final String className) {
		sb.append("(s,o)-> {return new ").append(className).append("(s");
		for (int i = 0; i < classes.length; i++) {
			sb.append(',');
			param(sb, classes[i], "o[" + i + "]");
		}
		sb.append(");}");
	}

	@Override
	public String getExceptions() { return "throws SecurityException, NoSuchMethodException"; }

	@Override
	protected boolean validateElement(final ProcessorContext context, final Element e) {
		boolean result =
				assertClassExtends(context, true, (TypeElement) e, context.getType("msi.gama.util.file.IGamaFile"));
		result &= assertOneScopeAndStringConstructor(context, true, (TypeElement) e);
		return result;
	}

}
