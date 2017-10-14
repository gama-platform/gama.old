package msi.gama.precompiler;

import static msi.gama.precompiler.java.JavaWriter.ACTION_PREFIX;
import static msi.gama.precompiler.java.JavaWriter.SEP;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;

public class ActionProcessor implements IProcessor<action> {
	private static Set<String> RESERVED_FACETS = new HashSet<>(Arrays.asList("name", "keyword", "returns"));

	@Override
	public void process(final ProcessorContext environment) {

		// Format: prefix 0.method 1.declClass 2.retClass 3.name 4.nbArgs 5.[arg]*
		for (final Element e : environment.sortElements(action.class)) {
			final action action = e.getAnnotation(action.class);
			final ExecutableElement ex = (ExecutableElement) e;
			// note("Action processed: " + ex.getSimpleName());
			final StringBuilder sb = new StringBuilder();
			// prefix
			sb.append(ACTION_PREFIX);
			// method
			sb.append(ex.getSimpleName()).append(SEP);
			// declClass
			sb.append(environment.rawNameOf(ex.getEnclosingElement())).append(SEP);
			// note("On class: " + ex.getSimpleName());
			// retClass
			final TypeMirror tm = ex.getReturnType();
			if (tm.getKind().equals(TypeKind.VOID)) {
				sb.append("void").append(SEP);
			} else {
				sb.append(environment.rawNameOf(tm)).append(SEP);
			}
			// virtual
			sb.append(action.virtual()).append(SEP);
			// name
			sb.append(action.name()).append(SEP);
			// argNumber
			final arg[] args = action.args();
			// final args deprecatedArgs = e.getAnnotation(args.class);
			// gathering names (in case of doublons)
			final Set<String> strings = new HashSet<>();
			for (int i = 0; i < args.length; i++) {
				strings.add(args[i].name());
			}
			// if (deprecatedArgs != null) {
			// for (int i = 0; i < deprecatedArgs.names().length; i++) {
			// strings.add(deprecatedArgs.names()[i]);
			// }
			// }
			final int nb = strings.size();
			sb.append(nb).append(SEP);
			// args format 1.name 2.type 3.optional
			strings.clear();
			if (args.length > 0) {
				for (int i = 0; i < args.length; i++) {
					final arg arg = args[i];
					final String argName = arg.name();
					if (RESERVED_FACETS.contains(argName)) {
						environment.emitWarning(
								"The argument called '" + argName
										+ "' will prevent this primitive to be called using facets (e.g. 'do action arg1: val1 arg2: val2;'). Consider renaming it to a non-reserved facet keyword",
								e);
					}
					sb.append(argName).append(SEP);
					sb.append(arg.type()).append(SEP);
					sb.append(arg.optional()).append(SEP);
					final doc[] docs = arg.doc();
					if (docs.length == 0) {
						environment.emitWarning("GAML: argument '" + argName + "' is not documented", e);
					}
					sb.append(environment.docToString(arg.doc())).append(SEP);
					strings.add(args[i].name());
				}
			}
			// if (deprecatedArgs != null && deprecatedArgs.names().length > 0) {
			// for (int i = 0; i < deprecatedArgs.names().length; i++) {
			// final String s = deprecatedArgs.names()[i];
			// if (!strings.contains(s)) {
			// sb.append(s).append(SEP);
			// sb.append("unknown").append(SEP);
			// sb.append("true").append(SEP);
			// sb.append("").append(SEP);
			// }
			// }
			// }
			environment.getProperties().put(sb.toString(), "");
		}

	}

}
