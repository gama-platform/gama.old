package msi.gama.precompiler;

import java.util.*;

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
	public final static String DOC_PREFIX = "@";
	public final static String DOC_SEP = "¤";
	public final static String SEP = "$";
	static String ln = "\n";
	static String tab = "\t";
	static String in = ln + tab;

	final static String IAGENT = "IAgent";
	final static String IPOPULATION = "IPopulation";
	final static String ISIMULATION = "ISimulation";
	final static String ISKILL = "ISkill";
	final static String ISYMBOL = "ISymbol";
	final static String IDESC = "IDescription";
	final static String ISCOPE = "IScope";
	final static String OBJECT = "Object";
	final static String IVALUE = "IValue";
	final static String IEXPRESSION = "IExpression";
	final static String TYPES = "Types";
	final static String INTEGER = "Integer";
	final static String DOUBLE = "Double";
	final static String BOOLEAN = "Boolean";
	final static String[] CONSTANTS = new String[] { "int[] AINT = new int[0]",
		"String[] ASTR = new String[0]" };
	final static String[] IMPORTS = new String[] { "msi.gama.outputs", "msi.gama.kernel.batch",
		"msi.gaml.architecture.weighted_tasks", "msi.gama.outputs.layers",
		"msi.gaml.architecture.user", "msi.gaml.architecture.reflex",
		"msi.gaml.architecture.finite_state_machine", "msi.gaml.species",
		"msi.gama.metamodel.shape", "msi.gaml.expressions", "msi.gama.metamodel.topology",
		"msi.gama.metamodel.population", "msi.gama.kernel.simulation", "java.util",
		" msi.gama.metamodel.shape", "msi.gama.common.interfaces", "msi.gama.runtime", "java.lang",
		"msi.gama.metamodel.agent", "msi.gaml.types", "msi.gaml.compilation", "msi.gaml.factories",
		"msi.gaml.descriptions", "msi.gama.util", "msi.gama.util.file", "msi.gama.util.matrix",
		"msi.gama.util.graph", "msi.gama.runtime.exceptions", "msi.gaml.factories",
		"msi.gaml.statements", "msi.gaml.skills", "msi.gaml.variables",
		"msi.gama.kernel.experiment", "msi.gaml.operators" };
	final static String[] EXPLICIT_IMPORTS =
		new String[] { "msi.gaml.operators.Random", "msi.gaml.operators.Maths",
			"msi.gaml.operators.Points", "msi.gaml.operators.Spatial.Properties",
			"msi.gaml.operators.System",
			"msi.gama.kernel.experiment.AbstractExperiment.ExperimentatorPopulation.ExperimentatorAgent" };

	public String write(final String packageName, final GamlProperties props) {
		StringBuilder sb = new StringBuilder();
		writeHeader(sb, packageName);

		for ( Map.Entry<String, String> entry : props.filterFirst(TYPE_PREFIX).entrySet() ) {
			writeType(sb, entry.getKey(), entry.getValue());
		}
		writeFactoriesAddition(sb, props.filterFirst(FACTORY_PREFIX));
		for ( Map.Entry<String, String> entry : props.filterFirst(SYMBOL_PREFIX).entrySet() ) {
			writeSymbolAddition(sb, entry.getKey(), entry.getValue());
		}
		for ( Map.Entry<String, String> entry : props.filterFirst(VAR_PREFIX).entrySet() ) {
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

	/**
	 * All the factories are written at once, in order to initialize them
	 * @param sb
	 * @param factoryMap
	 */
	protected void writeFactoriesAddition(final StringBuilder sb,
		final Map<String, String> factoryMap) {
		if ( factoryMap == null || factoryMap.isEmpty() ) { return; }
		sb.append(in);
		sb.append("_factories(");
		for ( String key : factoryMap.keySet() ) {
			String[] segments = key.split("\\$");
			String clazz = segments[0];
			String handles = "Arrays.asList(" + segments[1] + ")";
			String uses = "null";
			if ( segments.length > 2 ) {
				uses = "Arrays.asList(" + segments[2] + ")";
			}
			sb.append("new ").append(clazz);
			sb.append("(").append(handles).append(",").append(uses).append("),");
		}
		sb.setLength(sb.length() - 1);
		sb.append(");");
	}

	protected void writeVarAddition(final StringBuilder sb, final String var, final String doc) {
		String[] segments = var.split("\\$");
		String type = toJava(segments[0]);
		String clazz = segments[1];
		String facets = segments[2];
		sb.append(in).append("_var(").append(clazz).append(".class,");
		sb.append("DescriptionFactory.create(").append(type).append(",");
		sb.append(toArrayOfStrings(facets)).append("));");
	}

	protected void writeSymbolAddition(final StringBuilder sb, final String s, final String doc) {
		String[] segments = s.split("\\$");
		String kind = segments[0];
		String clazz = segments[1];
		String remote = segments[2];
		String args = segments[3];
		String scope = segments[4];
		String sequence = segments[5];
		String parentSymbols = toArrayOfStrings(segments[6]);
		String parentKinds = segments[7];
		if ( parentKinds.equals("") ) {
			parentKinds = "AINT";
		} else {
			parentKinds = "new int[] {" + parentKinds + "}";
		}
		int nbFacets = Integer.decode(segments[8]);
		int pointer = 9;
		String facets;
		if ( nbFacets == 0 ) {
			facets = "null";
			pointer++;
		} else {
			facets = "new FacetProto[]{";
			for ( int i = 0; i < nbFacets; i++ ) {
				if ( i > 0 ) {
					facets += ",";
				}
				facets += "new FacetProto(";
				// name
				facets += toJava(segments[pointer++]) + ',';
				// types
				facets += toArrayOfStrings(segments[pointer++]) + ",";
				// values
				facets += toArrayOfStrings(segments[pointer++]) + ",";
				// optional
				facets += segments[pointer++];
				// doc
				pointer++;
				facets += ")";
			}
			facets += "}";
		}
		String omissible = segments[pointer++];
		String sc =
			concat("new ISymbolConstructor() {@Override public ISymbol create(" + IDESC +
				" d) {return new ", clazz, "(d);}}");
		sb.append(in).append("_symbol(").append(clazz).append(".class,").append(kind).append(',')
			.append(remote).append(',').append(args).append(',').append(scope).append(',')
			.append(sequence).append(',').append(parentSymbols).append(",").append(parentKinds)
			.append(',').append(facets).append(',').append(toJava(omissible)).append(',')
			.append(sc);
		if ( segments.length > pointer ) {
			for ( int i = pointer; i < segments.length; i++ ) {
				sb.append(',').append(toJava(segments[i]));
			}
		}
		sb.append(");");
	}

	private String toArrayOfStrings(final String array) {
		if ( array.equals("") ) { return "ASTR"; }
		String[] segments = array.split("\\,");
		String result = "new String[] {";
		for ( int i = 0; i < segments.length; i++ ) {
			if ( i > 0 ) {
				result += ",";
			}
			result += toJava(segments[i]);
		}
		result += "}";
		return result;
	}

	protected void writeType(final StringBuilder sb, final String s, final String doc) {
		String[] segments = s.split("\\$");
		String keyword = segments[0];
		String id = segments[1];
		String varKind = segments[2];
		String clazz = segments[3];
		sb.append(in).append("_type(").append(toJava(keyword)).append(",new ").append(clazz)
			.append("(),").append(id).append(',').append(varKind);
		if ( segments.length > 4 ) {
			for ( int i = 4; i < segments.length; i++ ) {
				sb.append(',').append(segments[i]).append(".class");
			}
		}
		sb.append(");");
	}

	protected void writeOperatorAddition(final StringBuilder sb, final String s, final String doc) {
		String[] segments = s.split("\\$");
		boolean unary = segments[1].equals("");
		String l = segments[0];
		String r = segments[1];
		String canBeConst = segments[2];
		String type = segments[3];
		String contentType = segments[4];
		String iterator = segments[5];
		String priority = segments[6];
		String ret = segments[7];
		String m = segments[8];
		boolean stat = segments[9].equals("true");
		boolean scope = segments[10].equals("true");
		String helper =
			concat("new IOperatorExecuter() {@Override public ", checkPrim(ret), " execute(",
				ISCOPE, " s, Object t, Object r)", unary ? buildUnary(l, m, ret, stat, scope)
					: buildBinary(l, r, m, ret, stat, scope), "}");
		for ( int i = 11; i < segments.length; i++ ) {
			String kw = segments[i];
			if ( unary ) {
				sb.append(in).append("_unary(").append(toJava(kw)).append(',').append(l)
					.append(".class,").append(ret).append(".class,").append(canBeConst).append(',')
					.append(type).append(',').append(contentType).append(',').append(helper)
					.append(");");
			} else {
				sb.append(in).append("_binary(").append(toJava(kw)).append(',').append(l)
					.append(".class,").append(r).append(".class,").append(ret).append(".class,")
					.append(iterator).append(',').append(priority).append(',').append(canBeConst)
					.append(',').append(type).append(',').append(contentType).append(',')
					.append(helper).append(");");
			}
		}
	}

	private String toJava(final String s) {
		if ( s == null || s.isEmpty() ) { return "null"; }
		int i = ss1.indexOf(s);
		return i == -1 ? "\"" + replaceCommas(s) + "\"" : ss2.get(i);
	}

	private String replaceCommas(final String s) {
		return s.replace("COMMA", ",");
	}

	final static List<String> ss1 = Arrays.asList("const", "true", "false", "name", "setter",
		"getter", "type", "initer");
	final static List<String> ss2 = Arrays.asList("CONST", "TRUE", "FALSE", "NAME", "SETTER",
		"GETTER", "TYPE", "INITER");

	protected void writeGetterAddition(final StringBuilder sb, final String s, final String doc) {
		String[] segments = s.split("\\$");
		String name = segments[0];
		String clazz = segments[1];
		String ret = checkPrim(segments[2]);
		boolean dynamic = segments[3].equals("true");
		sb.append(concat(in, "_getter(", toJava(name), ",", clazz,
			".class, new IVarGetter() {@Override public ", ret, " execute(", IAGENT, " a, ",
			ISKILL, " t) {if (t == null) return ", returnWhenNull(ret), "; return (" + ret + ")((",
			clazz, ") t).", name, dynamic ? "(a);}" : "();}", "});"));
	}

	protected void writeSetterAddition(final StringBuilder sb, final String s, final String doc) {
		String[] segments = s.split("\\$");
		String decl = segments[1];
		String name = segments[0];
		String param = checkPrim(segments[2]);
		boolean dyn = segments[3].equals("true");
		sb.append(concat(in, "_setter(", toJava(name), ",", decl, ".class,", " new IVarSetter() ",
			"{@Override public void ", "execute(IAgent a, ISkill t, Object arg)",
			" { if (t == null) return;  ((", decl, ") t).", name, "(", dyn ? "a, " : "", "(" +
				param + ") arg); }});"));
	}

	protected void writeSpecies(final StringBuilder sb, final String s, final String doc) {
		String[] segments = s.split("\\$");
		String name = segments[0];
		String clazz = segments[1];
		sb.append(in).append("_species(").append(toJava(name)).append(",").append(clazz)
			.append(".class, new IAgentConstructor() {@Override public ").append(IAGENT)
			.append(" createOneAgent(").append(ISIMULATION).append(" s,").append(IPOPULATION)
			.append(" p) {return new ").append(clazz).append("(s, p);}}");
		for ( int i = 2; i < segments.length; i++ ) {
			sb.append(",").append(toJava(segments[i]));
		}
		sb.append(");");
	}

	protected void writeActionAddition(final StringBuilder sb, final String s, final String doc) {
		String[] segments = s.split("\\$");
		String method = segments[0];
		String clazz = segments[1];
		String name = segments[3];
		String ret = checkPrim(segments[2]);
		int nbArgs = Integer.decode(segments[4]);
		String args = "";
		// TODO Argument types not taken into account
		int pointer = 5;
		for ( int i = 0; i < nbArgs; i++ ) {
			// picking name
			args += "," + toJava(segments[pointer++]);
			// skipping type
			pointer++;
			// skipping optional
			pointer++;
			// skipping doc
			pointer++;
		}
		sb.append(concat(in, "_action(", toJava(method), ",", clazz,
			".class, new PrimitiveExecuter() {@Override public ", ret, " execute(", ISKILL, " t,",
			IAGENT, " a,", ISCOPE, " s) {return ((", clazz, ") t).", method,
			"(s);} @Override public IType getReturnType(){ return Types.get(", ret, ".class);}},",
			toJava(name), args, ");"));
	}

	protected void writeSkill(final StringBuilder sb, final String s, final String doc) {
		String[] segments = s.split("\\$");
		String name = segments[0];
		String clazz = segments[1];
		sb.append(concat(in, "_skill(", toJava(name), ",", clazz,
			".class, new ISkillConstructor() {@Override public ISkill newInstance() {return new ",
			clazz, "();}}"));
		for ( int i = 2; i < segments.length; i++ ) {
			sb.append(",").append(toJava(segments[i]));
		}
		sb.append(");");
	}

	protected void writeFieldGetterAddition(final StringBuilder sb, final String s, final String doc) {
		String[] segments = s.split("\\$");
		String clazz = segments[1];
		String name = segments[0];
		String ret = checkPrim(segments[2]);
		sb.append(concat(in, "_field(", toJava(name), ",", clazz,
			".class, new IFieldGetter() {@Override public ", ret, " value(", IVALUE,
			" v)  { if (v == null) return ", returnWhenNull(ret), "; return ((", clazz, ") v).",
			name, "();}});"));
	}

	protected void writeHeader(final StringBuilder sb, final String packageName) {
		sb.append("package ").append(packageName).append(';');
		for ( int i = 0; i < IMPORTS.length; i++ ) {
			sb.append(ln).append("import ").append(IMPORTS[i]).append(".*;");
		}
		for ( int i = 0; i < EXPLICIT_IMPORTS.length; i++ ) {
			sb.append(ln).append("import ").append(EXPLICIT_IMPORTS[i]).append(";");
		}
		sb.append(ln).append("import static msi.gaml.operators.Cast.*;");
		sb.append(ln).append("import static msi.gaml.operators.Spatial.*;");
		sb.append(ln).append("import static msi.gama.common.interfaces.IKeyword.*;");
		sb.append(ln).append(ln).append(classDefinition()).append(" {");
		sb.append(ln);
		for ( int i = 0; i < CONSTANTS.length; i++ ) {
			sb.append(ln).append("private final static " + CONSTANTS[i] + ";");
		}
		sb.append(tab).append("public ").append(simpleClassName()).append("() {}");
		sb.append(ln).append(ln).append(tab);
		sb.append("@Override public void initialize() {");
	}

	protected String classDefinition() {
		return "public class GamlAdditions extends AbstractGamlAdditions";
	}

	protected String simpleClassName() {
		return "GamlAdditions";
	}

	protected void writeFooter(final StringBuilder sb) {
		sb.append(ln);
		sb.append(tab).append('}');
		sb.append(ln).append('}');
	}

	private String concat(final String ... tab) {
		StringBuilder concat = new StringBuilder();
		for ( int i = 0; i < tab.length; i++ ) {
			concat.append(tab[i]);
		}
		return concat.toString();
	}

	protected String check(final String clazz) {
		for ( int i = 0; i < IMPORTS.length; i++ ) {
			if ( clazz.startsWith(IMPORTS[i]) ) { return clazz
				.substring(clazz.lastIndexOf('.') + 1); }
		}

		return clazz;
	}

	protected String checkPrim(final String c) {
		if ( c.equals("int") || c.equals("short") || c.equals("long") ) { return INTEGER; }
		if ( c.equals("double") || c.equals("float") ) { return DOUBLE; }
		if ( c.equals("boolean") ) { return BOOLEAN; }
		return c;
	}

	protected String param(final String c, final String par) {
		String jc = checkPrim(c);
		if ( jc.equals(DOUBLE) ) {
			return concat("asFloat(s,", par, ")");
		} else if ( jc.equals(INTEGER) ) {
			return concat("asInt(s,", par, ")");
		} else if ( jc.equals(BOOLEAN) ) {
			return concat("asBool(s,", par, ")");
		} else if ( jc.equals(OBJECT) ) {
			return par;
		} else {
			return concat("((", jc, ")", par, ")");
		}
	}

	protected String returnWhenNull(final String returnClass) {
		if ( returnClass.equals(DOUBLE) ) { return " 0d "; }
		if ( returnClass.equals(INTEGER) ) { return " 0 "; }
		if ( returnClass.equals(BOOLEAN) ) { return " false "; }
		return concat(" ", TYPES + ".get(", returnClass, ".class).cast(null, null, null)");
	}

	protected String buildBinary(final String lc, final String rc, final String name,
		final String retClass, final boolean stat, final boolean scope) {
		String ret = checkPrim(retClass);
		return stat ? scope ? concat("{return ", name, "(s,", param(lc, "t"), ",", param(rc, "r"),
			");}") : concat("{return ", name, "(", param(lc, "t"), ",", param(rc, "r"), ");}")
			: scope ? concat("{if (t == null) return ", returnWhenNull(ret), ";return ((", lc,
				")t).", name, "(s,", param(rc, "r"), ");}") : concat("{if (t == null) return ",
				returnWhenNull(ret), ";return ((", lc, ")t).", name, "(", param(rc, "r"), ");}");
	}

	protected String buildUnary(final String c, final String name, final String returnClass,
		final boolean stat, final boolean scope) {
		String ret = checkPrim(returnClass);
		return stat ? scope ? concat("{return ", name, "(s,", param(c, "t"), ");}") : concat(
			"{return ", name, "(", param(c, "t"), ");}") : scope ? concat(
			"{if (t == null) return ", returnWhenNull(ret), ";return ((", c, ")t).", name, "(s);}")
			: concat("{if (t == null) return ", returnWhenNull(ret), ";return ((", c, ")t).", name,
				"();}");
	}

}
