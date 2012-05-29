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
	public final static String TYPE_PREFIX = "(";
	public final static String SYMBOL_PREFIX = "[";
	public final static String FACTORY_PREFIX = "]";
	public final static String VAR_PREFIX = "%";
	protected final ProcessingEnvironment env;
	static String ln = "\n";
	static String tab = "\t";

	class Pair {

		String key, value;

		Pair(final String s1, final String s2) {
			key = s1;
			value = s2;
		}
	}

	final static String IAGENT = "IAgent";
	final static String IPOPULATION = "msi.gama.metamodel.population.IPopulation";
	final static String ISIMULATION = "ISimulation";
	final static String ISKILL = "ISkill";
	final static String ISYMBOL = "ISymbol";
	final static String IDESCRIPTION = "IDescription";
	final static String ISCOPE = "IScope";
	final static String OBJECT = "Object";
	final static String IVALUE = "IValue";
	// final static String EXCEPTION = "throws GamaRuntimeException";
	final static String IEXPRESSION = "msi.gaml.expressions.IExpression";
	final static String TYPES = "msi.gaml.types.Types";
	final static String INTEGER = "Integer";
	final static String DOUBLE = "Double";
	final static String BOOLEAN = "Boolean";
	final static String[] IMPORTS = new String[] { "msi.gaml.architecture.weighted_tasks",
		"msi.gaml.architecture.user", "msi.gaml.architecture.reflex",
		"msi.gaml.architecture.finite_state_machine", "msi.gaml.species",
		"msi.gama.metamodel.shape", "msi.gaml.expressions", "msi.gama.metamodel.topology",
		"msi.gama.metamodel.population", "msi.gama.kernel.simulation", "java.util",
		" msi.gama.metamodel.shape", "msi.gama.common.interfaces", "msi.gama.runtime", "java.lang",
		"msi.gama.metamodel.agent", "msi.gaml.types", "msi.gaml.compilation", "msi.gaml.factories",
		"msi.gaml.descriptions", "msi.gama.util", "msi.gama.util.file", "msi.gama.util.matrix",
		"msi.gama.util.graph", "msi.gama.runtime.exceptions", "msi.gaml.factories",
		"msi.gaml.statements", "msi.gaml.skills", "msi.gaml.variables" };

	// final static String[] CLASS_IMPORTS = new String[] { "msi.gaml.operators.Containers",
	// "msi.gaml.operators.Cast", "msi.gaml.operators.Maths" };

	JavaWriter(final ProcessingEnvironment pe) {
		env = pe;
	}

	public String write(final String packageName, final GamlProperties props) {
		StringBuilder sb = new StringBuilder();
		writeHeader(sb, packageName);
		// writeSymbolConstructors(sb, props.get(GamlProperties.SYMBOLS));

		for ( Map.Entry<String, String> entry : props.filterFirst(TYPE_PREFIX).entrySet() ) {
			writeType(sb, entry.getKey(), entry.getValue());
		}
		writeFactoriesAddition(sb, props.filterFirst(FACTORY_PREFIX));
		for ( Map.Entry<String, String> entry : props.filterFirst(SYMBOL_PREFIX).entrySet() ) {
			writeSymbolAddition(sb, entry.getKey(), entry.getValue());
		}
		for ( Map.Entry<String, Set<String>> entry : props.filterAll(VAR_PREFIX).entrySet() ) {
			writeVarAddition(sb, entry.getKey(), entry.getValue());
		}
		for ( Map.Entry<String, String> entry : props.filterFirst(OPERATOR_PREFIX).entrySet() ) {
			writeOperatorAddition(sb, entry.getKey(), entry.getValue());
		}
		for ( Map.Entry<String, String> entry : props.filterFirst(ACTION_PREFIX).entrySet() ) {
			writeActionAddition(sb, entry.getKey(), entry.getValue());
		}
		for ( Map.Entry<String, String> entry : props.filterFirst(GETTER_PREFIX).entrySet() ) {
			writeGetterAddition(sb, entry.getKey(), entry.getValue());
		}
		for ( Map.Entry<String, String> entry : props.filterFirst(SETTER_PREFIX).entrySet() ) {
			writeSetterAddition(sb, entry.getKey(), entry.getValue());
		}
		for ( Map.Entry<String, String> entry : props.filterFirst(FIELD_PREFIX).entrySet() ) {
			writeFieldGetterAddition(sb, entry.getKey(), entry.getValue());
		}
		for ( Map.Entry<String, String> entry : props.filterFirst(SPECIES_PREFIX).entrySet() ) {
			writeSpecies(sb, entry.getKey(), entry.getValue());
		}
		for ( Map.Entry<String, String> entry : props.filterFirst(SKILL_PREFIX).entrySet() ) {
			writeSkill(sb, entry.getKey(), entry.getValue());
		}
		writeFooter(sb);
		return sb.toString();
	}

	protected void writeFactoriesAddition(final StringBuilder sb,
		final Map<String, String> factoryMap) {
		if ( factoryMap == null || factoryMap.isEmpty() ) { return; }
		ln(sb);
		ln(sb);
		tab(sb);
		sb.append("addFactories(");
		for ( String key : factoryMap.keySet() ) {
			String[] segments = key.split("\\$");
			String handles = "Arrays.asList(" + segments[0] + ")";
			String uses = "null";
			if ( segments.length > 1 ) {
				uses = "Arrays.asList(" + segments[1] + ")";
			}
			sb.append("new ").append(factoryMap.get(key));
			sb.append("(").append(handles).append(",").append(uses).append("),");
		}
		sb.setLength(sb.length() - 1);
		sb.append(");");
	}

	protected void writeVarAddition(final StringBuilder sb, final String var,
		final Set<String> classes) {
		String[] segments = var.split("\\$");
		String type = segments[0];
		String facets = "new String[] {" + segments[1] + "}";
		for ( String clazz : classes ) {
			sb.append(ln).append(tab).append(tab);
			sb.append("addVarDescription(");
			sb.append(clazz).append(".class").append(",");
			sb.append("DescriptionFactory.create(");
			sb.append(type).append(",");
			sb.append(facets).append("));");
		}
	}

	public static void main(final String[] args) {
		System.out.println(Arrays.toString(new String[] { "type", "int", "name", "toto" }));
	}

	protected void writeSymbolAddition(final StringBuilder sb, final String s, final String clazz) {
		String[] segments = s.split("\\$");
		String kind = segments[0];
		String remote = segments[1];
		String args = segments[2];
		String scope = segments[3];
		String sequence = segments[4];
		String sc =
			concat("new ISymbolConstructor() {@Override public ISymbol ", "create(final " +
				IDESCRIPTION + " description) {	return new ", clazz, "(description);}}");
		sb.append(ln).append(tab).append(tab);
		sb.append("addSymbol(");
		sb.append(clazz).append(".class").append(",");
		sb.append(kind).append(",");
		sb.append(remote).append(",");
		sb.append(args).append(",");
		sb.append(scope).append(",");
		sb.append(sequence).append(",");
		sb.append(sc);
		if ( segments.length > 5 ) {
			for ( int i = 5; i < segments.length; i++ ) {
				sb.append(",\"").append(segments[i]).append("\"");
			}
		}
		sb.append(");");
	}

	protected void writeType(final StringBuilder sb, final String s, final String clazz) {
		String[] segments = s.split("\\$");
		String keyword = segments[0];
		String id = segments[1];
		String varKind = segments[2];
		sb.append(ln).append(tab).append(tab);
		sb.append("addType(");
		sb.append('"').append(keyword).append('"').append(',');
		sb.append("new ").append(clazz).append("()").append(',');
		sb.append("(short)").append(id).append(',');
		sb.append("(int)").append(varKind);
		if ( segments.length > 3 ) {
			for ( int i = 3; i < segments.length; i++ ) {
				sb.append(",").append(segments[i]).append(".class");
			}
		}
		sb.append(");");
	}

	protected String operatorKey(final String keyword, final String leftClass,
		final String rightClass, final boolean canBeConst, final short type,
		final short contentType, final boolean iterator, final short priority, final String retClass) {
		return concat(OPERATOR_PREFIX, keyword, "$", leftClass, "$", rightClass, "$",
			String.valueOf(canBeConst), "$", String.valueOf(type), "$",
			String.valueOf(contentType), "$", String.valueOf(iterator), "$",
			String.valueOf(priority), "$", retClass);
	}

	protected void writeOperatorAddition(final StringBuilder sb, final String s, final String helper) {
		boolean isUnary = true;
		String[] segments = s.split("\\$");
		String leftClass = segments[1] + ".class";
		String keyword = segments[0];
		String rightClass;
		if ( segments[2].equals("") ) {
			rightClass = "null";
		} else {
			rightClass = segments[2] + ".class";
			isUnary = false;
		}
		String canBeConst = segments[3];
		String type = segments[4];
		String contentType = segments[5];
		String iterator = segments[6];
		String priority = segments[7];
		String returnClass = segments[8] + ".class";

		if ( isUnary ) {
			sb.append(ln).append(tab).append(tab);
			sb.append("addUnary(");
			sb.append('"').append(keyword).append('"').append(',');
			sb.append(leftClass).append(',');
			sb.append(returnClass).append(',');
			sb.append(canBeConst).append(',');
			sb.append("(short)").append(type).append(',');
			sb.append("(short)").append(contentType).append(',');
			sb.append(helper);
			sb.append(");");
		} else {
			sb.append(ln).append(tab).append(tab);
			sb.append("addBinary(");
			sb.append('"').append(keyword).append('"').append(',');
			sb.append(leftClass).append(',');
			sb.append(rightClass).append(',');
			sb.append(returnClass).append(',');
			sb.append(iterator).append(',');
			sb.append("(short)").append(priority).append(',');
			sb.append(canBeConst).append(',');
			sb.append("(short)").append(type).append(',');
			sb.append("(short)").append(contentType).append(',');
			sb.append(helper);
			sb.append(");");
		}
	}

	protected void writeGetterAddition(final StringBuilder sb, final String s, final String code) {
		String[] segments = s.split("\\$");
		String clazz = segments[1];
		String name = segments[0];
		sb.append(concat(ln, ln, tab, tab, "addGetterExecuter(\"", name, "\",", clazz,
			".class, new IVarGetter() {", code, "});"));
	}

	protected void writeSetterAddition(final StringBuilder sb, final String s, final String code) {
		String[] segments = s.split("\\$");
		String clazz = segments[1];
		String name = segments[0];
		sb.append(concat(ln, ln, tab, tab, "addSetterExecuter(\"", name, "\",", clazz,
			".class, new IVarSetter() {", code, "});"));
	}

	protected void writeSpecies(final StringBuilder sb, final String s, final String clazz) {
		String[] segments = s.split("\\$");
		String name = segments[0];
		String helper = "new IAgentConstructor() {" + buildAgentConstructor(clazz) + "}";
		sb.append(concat(ln, ln, tab, tab, "addSpecies(\"", name, "\",", clazz, ".class,", helper));
		for ( int i = 1; i < segments.length; i++ ) {
			sb.append(",").append('\"').append(segments[i]).append('\"');
		}
		sb.append(");");
	}

	protected void writeActionAddition(final StringBuilder sb, final String s, final String code) {
		String[] segments = s.split("\\$");
		String clazz = segments[1];
		String methodName = segments[0];
		String actionName = segments[2];
		String args = "";
		if ( segments.length > 3 ) {
			args = "," + segments[3];
		}
		sb.append(concat(ln, ln, tab, tab, "addAction(\"", methodName, "\",", clazz,
			".class, new PrimitiveExecuter() {", code, "}, \"", actionName, "\"", args, ");"));
	}

	protected void writeSkill(final StringBuilder sb, final String s, final String clazz) {
		String[] segments = s.split("\\$");
		String name = segments[0];
		String helper =
			"new ISkillConstructor() { @Override public" + " ISkill newInstance() {return new " +
				clazz + "();}}";
		sb.append(concat(ln, ln, tab, tab, "addSkill(\"", name, "\",", clazz, ".class,", helper));
		for ( int i = 1; i < segments.length; i++ ) {
			sb.append(",").append('\"').append(segments[i]).append('\"');
		}
		sb.append(");");
	}

	protected void writeFieldGetterAddition(final StringBuilder sb, final String s,
		final String code) {
		String[] segments = s.split("\\$");
		String clazz = segments[1];
		String name = segments[0];
		sb.append(concat(ln, ln, tab, tab, "addFieldGetterExecuter(\"", name, "\",", clazz,
			".class, new IFieldGetter() {", code, "});"));
	}

	// protected void writeSymbolConstructors(final StringBuilder sb,
	// final LinkedHashSet<String> symbols) {
	// if ( symbols == null ) { return; }
	// ln(sb);
	// for ( String clazz : symbols ) {
	// sb.append(concat(ln, ln, tab, tab, "addSymbolConstructor(", clazz,
	// ".class, new ISymbolConstructor() {", ln, ln, tab, "@Override", ln, tab, tab,
	// "public ISymbol ",
	// "create(final msi.gaml.descriptions.IDescription description) {", ln, tab, tab,
	// tab, "	return new ", clazz, "(description);}});"));
	// }
	// }

	protected StringBuilder ln(final StringBuilder sb) {
		return sb.append(ln);
	}

	protected StringBuilder tab(final StringBuilder sb) {
		return sb.append(tab);
	}

	protected void writeHeader(final StringBuilder sb, final String packageName) {
		sb.append("package ").append(packageName).append(';');
		for ( int i = 0; i < IMPORTS.length; i++ ) {
			ln(sb).append("import ").append(IMPORTS[i]).append(".*;");
		}
		// for ( int i = 0; i < CLASS_IMPORTS.length; i++ ) {
		// ln(sb).append("import ").append(CLASS_IMPORTS[i]).append(";");
		// }
		ln(sb).append(ln).append(classDefinition()).append(" {");
		ln(sb);
		tab(sb).append("public ").append(simpleClassName()).append("() {}");
		ln(sb).append(ln).append(tab);
		sb.append("@Override public void initialize() {");
	}

	protected String classDefinition() {
		return "public class GamlAdditions extends AbstractGamlAdditions";
	}

	protected String simpleClassName() {
		return "GamlAdditions";
	}

	protected void writeFooter(final StringBuilder sb) {
		ln(sb);
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

	protected String check(final String clazz) {
		// for ( int i = 0; i < CLASS_IMPORTS.length; i++ ) {
		// if ( clazz.startsWith(CLASS_IMPORTS[i]) ) { return clazz.substring(clazz
		// .lastIndexOf('.') + 1); }
		// }

		for ( int i = 0; i < IMPORTS.length; i++ ) {
			if ( clazz.startsWith(IMPORTS[i]) ) { return clazz
				.substring(clazz.lastIndexOf('.') + 1); }
		}

		return clazz;
	}

	protected String checkPrimitiveClass(final String c) {
		if ( c.equals(int.class.getCanonicalName()) || c.equals(short.class.getCanonicalName()) ||
			c.equals(long.class.getCanonicalName()) ) { return INTEGER; }
		if ( c.equals(double.class.getCanonicalName()) || c.equals(float.class.getCanonicalName()) ) { return DOUBLE; }
		if ( c.equals(boolean.class.getCanonicalName()) ) { return BOOLEAN; }
		return c;
	}

	protected String parameter(final String c, final String par) {
		String jc = checkPrimitiveClass(c);
		if ( jc.equals(DOUBLE) ) {
			return concat("(", par, " == null) ? 0d : ", par, " instanceof Double ? (Double) ",
				par, " : Double.valueOf(((Number)", par, ").doubleValue())");
		} else if ( jc.equals(INTEGER) ) {
			return concat("(", par, " == null) ? 0 : ", par, " instanceof Integer ? (Integer) ",
				par, " : Integer.valueOf(((Number)", par, ").intValue())");
		} else if ( jc.equals(BOOLEAN) ) {
			return concat("(", par, " == null) ? false : ((Boolean)", par, ")");
		} else {
			return concat("((", jc, ")", par, ")");
		}
	}

	protected String returnWhenNull(final String returnClass) {
		if ( returnClass.equals(DOUBLE) ) { return " 0d "; }
		if ( returnClass.equals(INTEGER) ) { return " 0 "; }
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
		return check(string.replace('$', '.'));
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
		boolean isUnary = isUnary(element, isStatic, context);
		Map<String, String> result = new HashMap();
		for ( String kw : op.value() ) {
			if ( isUnary ) {
				Pair p =
					getUnaryOperator(kw, m1, declClass, ret, args, context, isStatic,
						op.can_be_const(), op.type(), op.content_type(), op.iterator(),
						op.priority());
				result.put(p.key, p.value);

			} else {
				Pair p =
					getBinaryOperator(kw, m1, declClass, ret, args, context, isStatic,
						op.can_be_const(), op.type(), op.content_type(), op.iterator(),
						op.priority());
				result.put(p.key, p.value);
			}
		}
		return result;
	}

	public Pair getActionExecuterFor(final ExecutableElement element) {
		String declClass = rawNameOf(element.getEnclosingElement());
		String m1 = element.getSimpleName().toString();
		String retClass = rawNameOf(element.getReturnType());
		Pair result =
			new Pair(concat(ACTION_PREFIX, m1, "$", declClass), buildActionExecuter(declClass,
				retClass, m1));
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

	protected boolean isUnary(final ExecutableElement element, final boolean isStatic,
		final boolean contextual) {
		int args = element.getParameters().size();
		if ( args == 0 && !isStatic ) { return true; }
		if ( args == 1 && !isStatic && contextual ) { return true; }
		if ( args == 1 && isStatic ) { return true; }
		if ( args == 2 && isStatic && contextual ) { return true; }
		return false;
	}

	protected boolean isContextual(final ExecutableElement element) {
		List<? extends VariableElement> args = element.getParameters();
		return args.size() > 0 && args.get(0).asType().toString().contains("IScope");
	}

	protected boolean isStatic(final ExecutableElement element) {
		Set<Modifier> m = element.getModifiers();
		return m.contains(Modifier.STATIC);
	}

	protected Pair getBinaryOperator(final String keyword, final String mName,
		final String declClass, final String retClass, final String[] argsClasses,
		final boolean contextual, final boolean isStatic, final boolean canBeConst,
		final short type, final short contentType, final boolean iterator, final short priority) {
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
		return new Pair(operatorKey(keyword, leftClass, rightClass, canBeConst, type, contentType,
			iterator, priority, retClass), buildBinary(leftClass, rightClass, methodName, retClass,
			isStatic, contextual));
	}

	protected Pair getUnaryOperator(final String keyword, final String mName,
		final String declClass, final String retClass, final String[] argsClasses,
		final boolean contextual, final boolean isStatic, final boolean canBeConst,
		final short type, final short contentType, final boolean iterator, final short priority) {
		String childClass;
		String methodName = mName;

		if ( isStatic ) {
			childClass = contextual ? argsClasses[1] : argsClasses[0];
			methodName = concat(declClass, ".", methodName);
		} else {
			childClass = declClass;
		}
		return new Pair(operatorKey(keyword, childClass, "", canBeConst, type, contentType,
			iterator, priority, retClass), buildUnary(childClass, methodName, retClass, isStatic,
			contextual));

	}

	protected String buildBinary(final String leftClass, final String rightClass,
		final String name, final String retClass, final boolean isStatic,
		final boolean isContextDependent) {
		String ret = checkPrimitiveClass(retClass);
		String rc = rightClass;
		String result =
			isStatic ? isContextDependent ? buildStaticContextDependentBinary(leftClass, rc, name,
				ret) : buildStaticBinary(leftClass, rc, name, ret) : isContextDependent
				? buildDynamicContextDependentBinary(leftClass, rc, name, ret)
				: buildDynamicBinary(leftClass, rc, name, ret);
		return concat(ln, tab, tab, tab, "new IOperatorExecuter() {", result, "}");
	}

	protected String buildUnary(final String childClass, final String name,
		final String returnClass, final boolean isStatic, final boolean isContextDependent) {
		String ret = checkPrimitiveClass(returnClass);
		String result =
			isStatic ? isContextDependent ? buildStaticContextDependentUnary(childClass, name, ret)
				: buildStaticUnary(childClass, name, ret) : isContextDependent
				? buildDynamicContextDependentUnary(childClass, name, ret) : buildDynamicUnary(
					childClass, name, ret);
		return concat(ln, tab, tab, tab, "new IOperatorExecuter() {", result, "}");

	}

	protected String buildDynamicUnary(final String targetClass, final String m,
		final String returnClass) {
		return concat(ln, tab, tab, tab, "@Override", ln, tab, tab, tab, "public ", returnClass,
			" execute(final " + ISCOPE +
				" scope, final Object target, final Object right) { if (target == null) return ",
			returnWhenNull(returnClass), "; \nreturn ((", targetClass, ") target).", m, "();}");
	}

	protected String buildDynamicContextDependentUnary(final String targetClass, final String m,
		final String returnClass) {
		return concat(ln, tab, tab, tab, "@Override", ln, tab, tab, tab, "public ", returnClass,
			" execute(final ", ISCOPE,
			" scope,final Object target, final Object right) { if (target == null) return ",
			returnWhenNull(returnClass), "; \nreturn ((", targetClass, ") target).", m, "(scope);}");
	}

	protected String buildStaticUnary(final String targetClass, final String m,
		final String returnClass) {
		return concat(ln, tab, tab, tab, "@Override", ln, tab, tab, tab, "public ", returnClass,
			" execute(final " + ISCOPE +
				" scope, final Object target, final Object right) { return ", m, "(",
			parameter(targetClass, "target"), ");}");
	}

	protected String buildStaticContextDependentUnary(final String targetClass, final String m,
		final String returnClass) {
		return concat(ln, tab, tab, tab, "@Override", ln, tab, tab, tab, "public ", returnClass,
			" execute(final " + ISCOPE +
				" scope, final Object target, final Object right) { return " + m + "(scope,",
			parameter(targetClass, "target"), ");}");
	}

	protected String buildDynamicBinary(final String leftClass, final String rightClass,
		final String m, final String returnClass) {
		return concat(ln, tab, tab, tab, "@Override", ln, tab, tab, tab, "public ", returnClass,
			" execute(final " + ISCOPE + " scope,final Object left,",
			" final Object right) { if (left == null) return ", returnWhenNull(returnClass),
			"; \nreturn ((", leftClass, ") left).", m, "(", parameter(rightClass, "right"), ");}");
	}

	protected String buildDynamicContextDependentBinary(final String leftClass,
		final String rightClass, final String m, final String returnClass) {
		return concat(ln, tab, tab, tab, "@Override", ln, "public ", returnClass,
			" execute(final " + ISCOPE + " scope,final Object left, ",
			"final Object right) { if (left == null) return ", returnWhenNull(returnClass),
			"; \nreturn ((", leftClass, ") left).", m, "(scope,", parameter(rightClass, "right"),
			");}");
	}

	protected String buildStaticBinary(final String leftClass, final String rightClass,
		final String m, final String returnClass) {
		return concat(ln, tab, tab, tab, "@Override", ln, tab, tab, tab, "public ", returnClass,
			" execute(final " + ISCOPE + " scope, final Object left, ",
			"final Object right)  { return ", m, "(", parameter(leftClass, "left"), ",",
			parameter(rightClass, "right"), ");}");
	}

	protected String buildStaticContextDependentBinary(final String leftClass,
		final String rightClass, final String m, final String returnClass) {
		return concat(ln, tab, tab, tab, "@Override", ln, tab, tab, tab, "public ", returnClass,
			" execute(final " + ISCOPE +
				" scope, final Object left, final Object right) { return " + m + "(scope,",
			parameter(leftClass, "left"), ",", parameter(rightClass, "right"), ");}");
	}

	protected String buildAgentConstructor(final String javaBase) {
		return concat(ln, tab, tab, tab, "@Override", ln, tab, tab, tab, "public ", IAGENT,
			" createOneAgent(", ISIMULATION, " sim,", IPOPULATION, " manager)  { \n return new ",
			javaBase, "(sim, manager);}");
	}

	protected String buildActionExecuter(final String targetClass, final String retClass,
		final String m) {
		return concat(ln, tab, tab, tab, "@Override", ln, tab, tab, tab, "public ",
			checkPrimitiveClass(retClass), " execute(final ", ISKILL, " target, ", IAGENT,
			" agent, final ", ISCOPE, " scope) { \n return ((", targetClass, ") target).", m,
			"(scope);  }", ln, "@Override public IType getReturnType()", " { return Types.get(",
			retClass, ".class);}");
	}

	protected String buildGetter(final String target, final String method, final String r,
		final boolean dynamic) {
		String ret = checkPrimitiveClass(r);
		return concat("@Override public ", ret, " execute(final ", IAGENT, " agent, final ",
			ISKILL, " target) { if (target == null) return ", returnWhenNull(ret),
			"; \n  return (" + ret + ")((", target, ") target).", method, dynamic ? "(agent);}"
				: "();}");
	}

	protected String buildFieldGetter(final String target, final String method, final String r) {
		String ret = checkPrimitiveClass(r);
		return concat("@Override public ", ret, " value(final ", IVALUE,
			" v)  { if (v == null) return ", returnWhenNull(ret), "; \n", ret, " result = ((",
			target, ") v).", method, "(); return result;}");
	}

	protected String buildSetter(final String target, final String method, final String param,
		final boolean dynamic) {
		String pcName = checkPrimitiveClass(param);
		return concat("@Override public void execute(final ", IAGENT, " agent, final ", ISKILL,
			" target, final ", OBJECT, " arg) { if (target == null) return;  \n((", target,
			") target).", method, "(", dynamic ? "agent, " : "", "(" + pcName + ")", "arg); }");
	}

}
