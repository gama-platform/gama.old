package msi.gama.precompiler;

import java.util.*;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import msi.gama.precompiler.GamlAnnotations.operator;

public class JavaWriter {

	public final static String ACTION_PREFIX = "!";
	public final static String OPERATOR_PREFIX = "?";
	public final static String GETTER_PREFIX = "/";
	public final static String SETTER_PREFIX = "-";
	public final static String FIELD_PREFIX = "*";
	public final static String SPECIES_PREFIX = "&";
	public final static String SKILL_PREFIX = ")";
	private final ProcessingEnvironment env;
	static String ln = "\n";
	static String tab = "\t";

	class Pair {

		String key, value;

		Pair(final String s1, final String s2) {
			key = s1;
			value = s2;
		}
	}

	final static String IAGENT = "msi.gama.metamodel.agent.IAgent";
	final static String IPOPULATION = "msi.gama.metamodel.population.IPopulation";
	final static String ISIMULATION = "msi.gama.kernel.simulation.ISimulation";
	final static String ISKILL = "msi.gaml.skills.ISkill";
	final static String ISYMBOL = "ISymbol";
	final static String IDESCRIPTION = "msi.gaml.descriptions.IDescription";
	final static String ISCOPE = "msi.gama.runtime.IScope";
	final static String OBJECT = "Object";
	final static String IVALUE = "msi.gama.common.interfaces.IValue";
	final static String EXCEPTION = "throws msi.gama.runtime.exceptions.GamaRuntimeException";
	final static String IEXPRESSION = "msi.gaml.expressions.IExpression";
	final static String TYPES = "msi.gaml.types.Types";
	final static String INTEGER = "java.lang.Integer";
	final static String DOUBLE = "java.lang.Double";
	final static String BOOLEAN = "java.lang.Boolean";
	final static String IMPORTS = "import msi.gaml.compilation.*;";

	JavaWriter(final ProcessingEnvironment pe) {
		env = pe;
	}

	public String write(final String packageName, final GamlProperties props) {
		StringBuilder sb = new StringBuilder();
		writeHeader(sb, packageName);
		writeCategory(sb, "types", props.get(GamlProperties.JAVA_TYPES));
		writeSymbolConstructors(sb, props.get(GamlProperties.SYMBOLS));
		for ( String s : props.keySet() ) {
			if ( s.startsWith(OPERATOR_PREFIX) ) {
				writeOperatorAddition(sb, s.substring(1), props.getFirst(s));
			} else if ( s.startsWith(ACTION_PREFIX) ) {
				writeActionAddition(sb, s.substring(1), props.getFirst(s));
			} else if ( s.startsWith(GETTER_PREFIX) ) {
				writeGetterAddition(sb, s.substring(1), props.getFirst(s));
			} else if ( s.startsWith(SETTER_PREFIX) ) {
				writeSetterAddition(sb, s.substring(1), props.getFirst(s));
			} else if ( s.startsWith(FIELD_PREFIX) ) {
				writeFieldGetterAddition(sb, s.substring(1), props.getFirst(s));
			} else if ( s.startsWith(SPECIES_PREFIX) ) {
				writeSpecies(sb, s.substring(1), props.getFirst(s));
			} else if ( s.startsWith(SKILL_PREFIX) ) {
				writeSkill(sb, s.substring(1), props.getFirst(s));
			}
		}
		writeCategory(sb, "types", props.get(GamlProperties.GAMA_TYPES));
		for ( int i = 0; i < GamaProcessor.cats.length; i++ ) {
			writeCategory(sb, GamaProcessor.cats[i], props.get(GamaProcessor.cats[i]));
		}
		writeFooter(sb);
		return sb.toString();
	}

	private void writeOperatorAddition(final StringBuilder sb, final String s, final String code) {
		String[] segments = s.split("\\$");
		String left = segments[1] + ".class";
		String name = segments[0];
		String right = "null";
		if ( segments.length == 3 ) {
			right = segments[2] + ".class";
		}
		sb.append(concat(ln, ln, tab, tab, "addOperatorExecuter(\"", name, "\",", left, ",", right,
			",", code, ");"));
	}

	private void writeActionAddition(final StringBuilder sb, final String s, final String code) {
		String[] segments = s.split("\\$");
		String clazz = segments[1];
		String name = segments[0];
		sb.append(concat(ln, ln, tab, tab, "addActionExecuter(\"", name, "\",", clazz,
			".class, new PrimitiveExecuter() {", code, "});"));
	}

	private void writeGetterAddition(final StringBuilder sb, final String s, final String code) {
		String[] segments = s.split("\\$");
		String clazz = segments[1];
		String name = segments[0];
		sb.append(concat(ln, ln, tab, tab, "addGetterExecuter(\"", name, "\",", clazz,
			".class, new IVarGetter() {", code, "});"));
	}

	private void writeSetterAddition(final StringBuilder sb, final String s, final String code) {
		String[] segments = s.split("\\$");
		String clazz = segments[1];
		String name = segments[0];
		sb.append(concat(ln, ln, tab, tab, "addSetterExecuter(\"", name, "\",", clazz,
			".class, new IVarSetter() {", code, "});"));
	}

	private void writeSpecies(final StringBuilder sb, final String s, final String clazz) {
		String[] segments = s.split("\\$");
		String name = segments[0];
		sb.append(concat(ln, ln, tab, tab, "speciesAdd(\"", name, "\",", clazz, ".class"));
		for ( int i = 1; i < segments.length; i++ ) {
			sb.append(",").append('\"').append(segments[i]).append('\"');
		}
		sb.append(");");
		ln(sb).append(tab).append(tab);
		sb.append(concat(ln, ln, tab, tab, "addAgentConstructor(", clazz,
			".class, new IAgentConstructor() {", buildAgentConstructor(clazz), "});"));
	}

	private void writeSkill(final StringBuilder sb, final String s, final String clazz) {
		String[] segments = s.split("\\$");
		String name = segments[0];
		sb.append(concat(ln, ln, tab, tab, "skillsAdd(\"", name, "\",", clazz, ".class"));
		for ( int i = 1; i < segments.length; i++ ) {
			sb.append(",").append('\"').append(segments[i]).append('\"');
		}
		sb.append(");");
		ln(sb).append(tab).append(tab);
		sb.append(concat(ln, ln, tab, tab, "addSkillConstructor(", clazz,
			".class, new ISkillConstructor() { @Override public",
			" msi.gaml.skills.ISkill newInstance() {return new ", clazz, "();}});"));
	}

	private void writeFieldGetterAddition(final StringBuilder sb, final String s, final String code) {
		String[] segments = s.split("\\$");
		String clazz = segments[1];
		String name = segments[0];
		sb.append(concat(ln, ln, tab, tab, "addFieldGetterExecuter(\"", name, "\",", clazz,
			".class, new IFieldGetter() {", code, "});"));
	}

	private void writeSymbolConstructors(final StringBuilder sb, final LinkedHashSet<String> symbols) {
		if ( symbols == null ) { return; }
		ln(sb);
		for ( String clazz : symbols ) {
			sb.append(concat(ln, ln, tab, tab, "addSymbolConstructor(", clazz,
				".class, new ISymbolConstructor() {", ln, ln, tab, "@Override", ln, tab, tab,
				"public ISymbol ",
				"create(final msi.gaml.descriptions.IDescription description) {", ln, tab, tab,
				tab, "	return new ", clazz, "(description);}});"));
		}
	}

	private void writeCategory(final StringBuilder sb, final String cat,
		final LinkedHashSet<String> classes) {
		if ( classes == null ) { return; }
		ln(sb);
		ln(sb);
		tab(sb).append(tab);
		sb.append(cat).append("Add(");
		for ( String clazz : classes ) {
			sb.append(clazz.replace("$", ".")).append(".class,");
		}
		sb.setLength(sb.length() - 1);
		sb.append(");");
	}

	private StringBuilder ln(final StringBuilder sb) {
		return sb.append(ln);
	}

	private StringBuilder tab(final StringBuilder sb) {
		return sb.append(tab);
	}

	private void writeHeader(final StringBuilder sb, final String packageName) {
		sb.append("package ").append(packageName).append(';');
		ln(sb).append(IMPORTS).append(ln);
		ln(sb).append(ln).append(
			"public class GamlAdditions extends msi.gaml.compilation.AbstractGamlAdditions {");
		ln(sb);
		tab(sb).append("public GamlAdditions() {");
	}

	private void writeFooter(final StringBuilder sb) {
		ln(sb);
		tab(sb).append("finalize();");
		tab(sb).append('}');
		ln(sb).append('}');
	}

	public final static String concat(final String ... tab) {
		StringBuilder concat = new StringBuilder();
		for ( int i = 0; i < tab.length; i++ ) {
			concat.append(tab[i]);
		}
		return concat.toString();
	}

	private String checkPrimitiveClass(final String c) {
		if ( c.equals(int.class.getCanonicalName()) || c.equals(short.class.getCanonicalName()) ||
			c.equals(long.class.getCanonicalName()) ) { return INTEGER; }
		if ( c.equals(double.class.getCanonicalName()) || c.equals(float.class.getCanonicalName()) ) { return DOUBLE; }
		if ( c.equals(boolean.class.getCanonicalName()) ) { return BOOLEAN; }
		return c;
	}

	private String parameter(final String c, final String par) {
		String jc = checkPrimitiveClass(c);
		if ( jc.equals(DOUBLE) ) {
			return concat("(", par, " == null) ? Double.valueOf(0d) : ", par,
				" instanceof Double ? (Double) ", par, " : Double.valueOf(((Number)", par,
				").doubleValue())");
		} else if ( jc.equals(INTEGER) ) {
			return concat("(", par, " == null) ? Integer.valueOf(0) : ", par,
				" instanceof Integer ? (Integer) ", par, " : Integer.valueOf(((Number)", par,
				").intValue())");
		} else if ( jc.equals(BOOLEAN) ) {
			return concat("(", par, " == null) ? Boolean.valueOf(false) : ((Boolean)", par, ")");
		} else {
			return concat("((", jc, ")", par, ")");
		}
	}

	private String returnWhenNull(final String returnClass) {
		if ( returnClass.equals(DOUBLE) ) { return " Double.valueOf(0d) "; }
		if ( returnClass.equals(INTEGER) ) { return " Integer.valueOf(0) "; }
		if ( returnClass.equals(BOOLEAN) ) { return " false "; }
		return concat(" (", returnClass, ")", TYPES + ".coerce(null, (Object) null, " + TYPES +
			".get(", returnClass, ".class), null) ");
	}

	String rawNameOf(final Element e) {
		return rawNameOf(e.asType());
	}

	String rawNameOf(final TypeMirror t) {
		String string = env.getTypeUtils().erasure(t).toString();
		int i = string.indexOf('<');
		string = i > -1 ? string.substring(0, i) : string;
		return string.replace('$', '.');
	}

	public Map<String, String> getOperatorExecutersFor(final ExecutableElement element) {
		operator op = element.getAnnotation(operator.class);
		boolean isStatic = isStatic(element);
		String declClass = rawNameOf(element.getEnclosingElement());
		List<? extends VariableElement> argParams = element.getParameters();
		String[] args = new String[argParams.size()];
		for ( int i = 0; i < args.length; i++ ) {
			args[i] = rawNameOf(argParams.get(i));
		}
		String m1 = element.getSimpleName().toString();
		String ret = rawNameOf(element.getReturnType());
		boolean context = isContextual(element);
		boolean lazy = isLazy(element);
		boolean isUnary = isUnary(element, isStatic, context);
		Map<String, String> result = new HashMap();
		for ( String kw : op.value() ) {
			if ( isUnary ) {
				Pair p = getUnaryOperator(kw, m1, declClass, ret, args, context, lazy, isStatic);
				result.put(p.key, p.value);

			} else {
				Pair p = getBinaryOperator(kw, m1, declClass, ret, args, context, lazy, isStatic);
				result.put(p.key, p.value);
			}
		}
		return result;
	}

	public Pair getActionExecuterFor(final ExecutableElement element) {
		String declClass = rawNameOf(element.getEnclosingElement());
		String m1 = element.getSimpleName().toString();
		Pair result =
			new Pair(concat(ACTION_PREFIX, m1, "$", declClass), buildActionExecuter(declClass, m1));
		return result;
	}

	public Pair getGetter(final ExecutableElement element) {
		boolean isDynamic = element.getParameters().size() > 0;
		String retClass = rawNameOf(element.getReturnType());
		String declClass = rawNameOf(element.getEnclosingElement());
		String m1 = element.getSimpleName().toString();
		Pair result =
			new Pair(concat(GETTER_PREFIX, m1, "$", declClass), buildGetter(declClass, m1,
				retClass, isDynamic));
		return result;
	}

	public Pair getSetter(final ExecutableElement element) {
		boolean isDynamic = element.getParameters().size() == 2;
		String paramClass =
			rawNameOf(isDynamic ? element.getParameters().get(1) : element.getParameters().get(0));
		String declClass = rawNameOf(element.getEnclosingElement());
		String m1 = element.getSimpleName().toString();
		Pair result =
			new Pair(concat(SETTER_PREFIX, m1, "$", declClass), buildSetter(declClass, m1,
				paramClass, isDynamic));
		return result;
	}

	public Pair getFiedGetter(final ExecutableElement element) {
		String retClass = rawNameOf(element.getReturnType());
		String declClass = rawNameOf(element.getEnclosingElement());
		String m1 = element.getSimpleName().toString();
		Pair result =
			new Pair(concat(FIELD_PREFIX, m1, "$", declClass), buildFieldGetter(declClass, m1,
				retClass));
		return result;
	}

	private boolean isUnary(final ExecutableElement element, final boolean isStatic,
		final boolean contextual) {
		int args = element.getParameters().size();
		if ( args == 0 && !isStatic ) { return true; }
		if ( args == 1 && !isStatic && contextual ) { return true; }
		if ( args == 1 && isStatic ) { return true; }
		if ( args == 2 && isStatic && contextual ) { return true; }
		return false;
	}

	private boolean isLazy(final ExecutableElement element) {
		List<? extends VariableElement> args = element.getParameters();
		return args.size() > 1 &&
			args.get(args.size() - 1).asType().toString().contains("IExpression");
	}

	private boolean isContextual(final ExecutableElement element) {
		List<? extends VariableElement> args = element.getParameters();
		return args.size() > 0 && args.get(0).asType().toString().contains("IScope");
	}

	private boolean isStatic(final ExecutableElement element) {
		Set<Modifier> m = element.getModifiers();
		return m.contains(Modifier.STATIC);
	}

	private Pair getBinaryOperator(final String keyword, final String mName,
		final String declClass, final String retClass, final String[] argsClasses,
		final boolean contextual, final boolean lazy, final boolean isStatic) {
		String leftClass;
		String rightClass;
		String methodName = mName;

		if ( isStatic ) {
			leftClass = contextual ? argsClasses[1] : argsClasses[0];
			rightClass = contextual ? argsClasses[2] : argsClasses[1];
			methodName = concat(declClass, ".", methodName);
		} else {
			leftClass = declClass;
			rightClass = contextual ? argsClasses[1] : argsClasses[0];
		}
		return new Pair(concat(OPERATOR_PREFIX, methodName, "$", leftClass, "$", rightClass),
			buildBinary(leftClass, rightClass, methodName, retClass, isStatic, contextual, lazy));
	}

	private Pair getUnaryOperator(final String keyword, final String mName, final String declClass,
		final String retClass, final String[] argsClasses, final boolean contextual,
		final boolean lazy, final boolean isStatic) {
		String childClass;
		String methodName = mName;

		if ( isStatic ) {
			childClass = contextual ? argsClasses[1] : argsClasses[0];
			methodName = concat(declClass, ".", methodName);
		} else {
			childClass = declClass;
		}
		return new Pair(concat(OPERATOR_PREFIX, methodName, "$", childClass), buildUnary(
			childClass, methodName, retClass, isStatic, contextual));

	}

	private String buildBinary(final String leftClass, final String rightClass, final String name,
		final String retClass, final boolean isStatic, final boolean isContextDependent,
		final boolean isLazyEvaluation) {
		String ret = checkPrimitiveClass(retClass);
		String rc = isLazyEvaluation ? IEXPRESSION : rightClass;
		String result =
			isStatic ? isContextDependent ? buildStaticContextDependentBinary(leftClass, rc, name,
				ret) : buildStaticBinary(leftClass, rc, name, ret) : isContextDependent
				? buildDynamicContextDependentBinary(leftClass, rc, name, ret)
				: buildDynamicBinary(leftClass, rc, name, ret);
		return concat(ln, tab, tab, tab, "new IOperatorExecuter() {", result, "}");
	}

	private String buildUnary(final String childClass, final String name, final String returnClass,
		final boolean isStatic, final boolean isContextDependent) {
		String ret = checkPrimitiveClass(returnClass);
		String result =
			isStatic ? isContextDependent ? buildStaticContextDependentUnary(childClass, name, ret)
				: buildStaticUnary(childClass, name, ret) : isContextDependent
				? buildDynamicContextDependentUnary(childClass, name, ret) : buildDynamicUnary(
					childClass, name, ret);
		return concat(ln, tab, tab, tab, "new IOperatorExecuter() {", result, "}");

	}

	private String buildDynamicUnary(final String targetClass, final String m,
		final String returnClass) {
		return concat(ln, tab, tab, tab, "@Override", ln, tab, tab, tab, "public ", returnClass,
			" execute(final " + ISCOPE + " scope, final Object target, final Object right) ",
			EXCEPTION, "{ if (target == null) return ", returnWhenNull(returnClass),
			"; \nreturn ((", targetClass, ") target).", m, "();}");
	}

	private String buildDynamicContextDependentUnary(final String targetClass, final String m,
		final String returnClass) {
		return concat(ln, tab, tab, tab, "@Override", ln, tab, tab, tab, "public ", returnClass,
			" execute(final ", ISCOPE, " scope,final Object target, final Object right)",
			EXCEPTION, " { if (target == null) return ", returnWhenNull(returnClass),
			"; \nreturn ((", targetClass, ") target).", m, "(scope);}");
	}

	private String buildStaticUnary(final String targetClass, final String m,
		final String returnClass) {
		return concat(ln, tab, tab, tab, "@Override", ln, tab, tab, tab, "public ", returnClass,
			" execute(final " + ISCOPE + " scope, final Object target, final Object right) ",
			EXCEPTION, " { return ", m, "(", parameter(targetClass, "target"), ");}");
	}

	private String buildStaticContextDependentUnary(final String targetClass, final String m,
		final String returnClass) {
		return concat(ln, tab, tab, tab, "@Override", ln, tab, tab, tab, "public ", returnClass,
			" execute(final " + ISCOPE + " scope, final Object target, final Object right) ",
			EXCEPTION, " { return " + m + "(scope,", parameter(targetClass, "target"), ");}");
	}

	private String buildDynamicBinary(final String leftClass, final String rightClass,
		final String m, final String returnClass) {
		return concat(ln, tab, tab, tab, "@Override", ln, tab, tab, tab, "public ", returnClass,
			" execute(final " + ISCOPE + " scope,final Object left,", " final Object right) ",
			EXCEPTION, " { if (left == null) return ", returnWhenNull(returnClass),
			"; \nreturn ((", leftClass, ") left).", m, "(", parameter(rightClass, "right"), ");}");
	}

	private String buildDynamicContextDependentBinary(final String leftClass,
		final String rightClass, final String m, final String returnClass) {
		return concat(ln, tab, tab, tab, "@Override", ln, "public ", returnClass,
			" execute(final " + ISCOPE + " scope,final java.lang.Object left, ",
			"final java.lang.Object right) ", EXCEPTION, " { if (left == null) return ",
			returnWhenNull(returnClass), "; \nreturn ((", leftClass, ") left).", m, "(scope,",
			parameter(rightClass, "right"), ");}");
	}

	private String buildStaticBinary(final String leftClass, final String rightClass,
		final String m, final String returnClass) {
		return concat(ln, tab, tab, tab, "@Override", ln, tab, tab, tab, "public ", returnClass,
			" execute(final " + ISCOPE + " scope, final Object left, ", "final Object right) ",
			EXCEPTION, " { return ", m, "(", parameter(leftClass, "left"), ",",
			parameter(rightClass, "right"), ");}");
	}

	private String buildStaticContextDependentBinary(final String leftClass,
		final String rightClass, final String m, final String returnClass) {
		return concat(ln, tab, tab, tab, "@Override", ln, tab, tab, tab, "public ", returnClass,
			" execute(final " + ISCOPE + " scope, final Object left, final Object right) ",
			EXCEPTION, " { return " + m + "(scope,", parameter(leftClass, "left"), ",",
			parameter(rightClass, "right"), ");}");
	}

	private String buildAgentConstructor(final String javaBase) {
		return concat(ln, tab, tab, tab, "@Override", ln, tab, tab, tab, "public ", IAGENT,
			" createOneAgent(", ISIMULATION, " sim,", IPOPULATION, " manager) " + EXCEPTION +
				" { \n return new ", javaBase, "(sim, manager);}");
	}

	private String buildActionExecuter(final String targetClass, final String m) {
		return concat(ln, tab, tab, tab, "@Override", ln, tab, tab, tab, "public ", OBJECT,
			" execute(final ", ISKILL, " target, ", IAGENT, " agent, final ", ISCOPE, " scope) ",
			EXCEPTION, " { \n return ((", targetClass, ") target).", m, "(scope);  }");
	}

	private String buildGetter(final String target, final String method, final String r,
		final boolean dynamic) {
		String ret = checkPrimitiveClass(r);
		return concat("public ", ret, " execute(final ", IAGENT, " agent, final ", ISKILL,
			" target) ", EXCEPTION, " { if (target == null) return ", returnWhenNull(ret),
			"; \n  return (" + ret + ")((", target, ") target).", method, dynamic ? "(agent);}"
				: "();}");
	}

	private String buildFieldGetter(final String target, final String method, final String r) {
		String ret = checkPrimitiveClass(r);
		return concat("public ", ret, " value(final ", IVALUE, " v) ", EXCEPTION,
			" { if (v == null) return ", returnWhenNull(ret), "; \n", ret, " result = ((", target,
			") v).", method, "(); return result;}");
	}

	private String buildSetter(final String target, final String method, final String param,
		final boolean dynamic) {
		String pcName = checkPrimitiveClass(param);
		return concat("public void execute(final ", IAGENT, " agent, final ", ISKILL,
			" target, final ", OBJECT, " arg) ", EXCEPTION, " { if (target == null) return;  \n((",
			target, ") target).", method, "(", dynamic ? "agent, " : "", "(" + pcName + ")",
			"arg); }");
	}

}
