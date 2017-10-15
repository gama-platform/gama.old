package msi.gama.precompiler;

import static msi.gama.precompiler.java.JavaWriter.OPERATOR_PREFIX;
import static msi.gama.precompiler.java.JavaWriter.SEP;

import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;

public class OperatorProcessor implements IProcessor<operator> {

	@Override
	public void process(final ProcessorContext environment) {
		/**
		 * Format : prefix 0.leftClass 1.rightClass 2.const 3.type 4.contentType 5.iterator 6.priority 7.returnClass
		 * 8.methodName 9.static 10.contextual 11.[name$]+
		 * 
		 * @param env
		 */

		for (final Element e : environment.sortElements(operator.class)) {
			final ExecutableElement method = (ExecutableElement) e;
			final operator operator = method.getAnnotation(operator.class);
			final String names[] = operator.value();
			if (names == null) {
				environment.emitError("GAML operators need to have at least one name", method);
				continue;
			}
			final String name = operator.value()[0];
			doc documentation = method.getAnnotation(doc.class);
			if (documentation == null) {
				final doc[] docs = operator.doc();
				if (docs.length > 0)
					documentation = operator.doc()[0];
			}

			if (documentation == null && !operator.internal()) {
				environment.emitWarning("GAML: operator '" + name + "' is not documented", e);
			}
			final Set<Modifier> modifiers = method.getModifiers();

			final boolean isStatic = modifiers.contains(Modifier.STATIC);

			if (!modifiers.contains(Modifier.PUBLIC)) {
				environment.emitError("GAML operators can only be implemented by public (or public static) methods",
						method);
				continue;
			}
			final String declClass = environment.rawNameOf(method.getEnclosingElement());
			final List<? extends VariableElement> argParams = method.getParameters();
			final String[] args = new String[argParams.size()];
			for (int i = 0; i < args.length; i++) {
				final VariableElement ve = argParams.get(i);
				switch (ve.asType().getKind()) {
					case ARRAY:
						environment.emitError(
								"GAML: operators cannot accept Java arrays arguments. Please wrap this argument in a GAML container type (list or matrix) ",
								ve);
						return;
					case CHAR:
					case BYTE:
					case SHORT:
						environment.emitWarning("GAML: The type of this argument will be casted to int", ve);
						break;
					default:
				}
				args[i] = environment.rawNameOf(argParams.get(i));
				environment.verifyClassTypeCompatibility(args[i], ve);

			}
			final int n = args.length;
			if (n == 0 && !isStatic) {
				environment.emitError("GAML: an operator needs to have at least one argument", method);
				continue;
			}
			final boolean scope = n > 0 && args[0].contains("IScope");
			final int actual_args_number = n + (scope ? -1 : 0) + (!isStatic ? 1 : 0);
			String methodName = method.getSimpleName().toString();
			final String[] classes = new String[actual_args_number];
			int begin = 0;
			if (!isStatic) {
				classes[0] = declClass;
				begin = 1;
			}
			final int shift = scope ? 1 : 0;
			try {
				for (int i = 0; i < actual_args_number - begin; i++) {
					classes[begin + i] = args[i + shift];
				}
			} catch (final Exception e1) {
				environment.emitError("An exception occurred in the processor: " + e1.getMessage(), method);
				continue;
			}

			final String ret = environment.rawNameOf(method.getReturnType());
			environment.verifyClassTypeCompatibility(ret, method);

			switch (method.getReturnType().getKind()) {
				case ARRAY:
					environment.emitError(
							"GAML operators cannot return Java arrays. Please wrap this result in a GAML container type (list or matrix) ",
							method);
					continue;
				case VOID: // does not seem to be recognized
				case NULL:
				case NONE:
				case ERROR:
					environment.emitError("GAML operators need to return a value.", method);
					continue;
				case CHAR:
				case BYTE:
				case SHORT:
					environment.emitWarning("The return type of this operator will be casted to integer in GAML",
							method);
					break;
				case EXECUTABLE:
					environment.emitError("GAML: operators cannot return Java executables", method);
					continue;
				default:
			}

			if (ret.equals("void")) {
				environment.emitError("GAML: operators need to return a value", method);
				continue;
			}

			methodName = isStatic ? declClass + "." + methodName : methodName;
			final StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(OPERATOR_PREFIX);
			// 0.number of arguments
			sb.append(actual_args_number).append(SEP);
			// 1+.arguments classes in the right order
			for (final String s : classes) {
				sb.append(s).append(SEP);
			}
			// 2.canBeConst
			sb.append(operator.can_be_const()).append(SEP);
			// 3.type
			sb.append(operator.type()).append(SEP);
			// 4.contentType
			sb.append(operator.content_type()).append(SEP);
			// 4+. index_type
			sb.append(operator.index_type()).append(SEP);
			// 5.iterator
			sb.append(operator.iterator()).append(SEP);
			// 6.expected types number
			sb.append(operator.expected_content_type().length).append(SEP);
			// 6+ expected types
			for (int i = 0; i < operator.expected_content_type().length; i++) {
				sb.append(operator.expected_content_type()[i]).append(SEP);
			}
			// 7.return class
			sb.append(ret).append(SEP);
			// 8.methodName
			sb.append(methodName).append(SEP);
			// 9.static
			sb.append(isStatic).append(SEP);
			// 10.contextual
			sb.append(scope);
			// 11+. names
			for (int i = 0; i < names.length; i++) {
				sb.append(SEP).append(names[i]);
			}

			environment.getProperties().put(sb.toString(), "");
		}
	}

}
