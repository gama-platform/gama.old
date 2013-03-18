package msi.gama.precompiler;

import java.util.*;

public class JavaWriter {

	public final static String ACTION_PREFIX = "!";
	public final static String OPERATOR_PREFIX = "?";
	public final static String GETTER_PREFIX = "/";
	public final static String SETTER_PREFIX = "-";
	public final static String FIELD_PREFIX = "*";
	public final static String SPECIES_PREFIX = "&";
	public final static String DISPLAY_PREFIX = "\\";
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
	static String in = ln;
	final static String OVERRIDE = " @Override ";
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
	final static String[] EXPLICIT_IMPORTS = new String[] { "msi.gaml.operators.Random",
		"msi.gaml.operators.Maths", "msi.gaml.operators.Points",
		"msi.gaml.operators.Spatial.Properties", "msi.gaml.operators.System" };

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
		for ( Map.Entry<String, String> entry : props.filterFirst(SKILL_PREFIX).entrySet() ) {
			writeSkill(sb, entry.getKey(), entry.getValue());
		}
		for ( Map.Entry<String, String> entry : props.filterFirst(SPECIES_PREFIX).entrySet() ) {
			writeSpecies(sb, entry.getKey(), entry.getValue());
		}
		for ( Map.Entry<String, String> entry : props.filterFirst(DISPLAY_PREFIX).entrySet() ) {
			writeDisplay(sb, entry.getKey(), entry.getValue());
		}

		writeFooter(sb);
		return sb.toString();
	}

	String toClassObject(final String s) {
		if ( s.equals("IAgent") ) { return "IA"; }
		if ( s.equals("IGamlAgent") ) { return "IG"; }
		if ( s.equals("GamaColor") ) { return "GC"; }
		if ( s.equals("GamaPair") ) { return "GP"; }
		if ( s.equals("GamaShape") ) { return "GS"; }
		if ( s.equals("Object") ) { return "O"; }
		if ( s.equals("Integer") ) { return "I"; }
		if ( s.equals("Double") ) { return "D"; }
		if ( s.equals("Boolean") ) { return "B"; }
		if ( s.equals("IExpression") ) { return "IE"; }
		if ( s.equals("IShape") ) { return "IS"; }
		if ( s.equals("GamaMap") ) { return "GM"; }
		if ( s.equals("IContainer") ) { return "IC"; }
		if ( s.equals("ILocation") ) { return "IL"; }
		if ( s.equals("IMatrix") ) { return "IM"; }
		if ( s.equals("String") ) { return "S"; }
		if ( s.equals("GamaPoint") ) { return "P"; }
		if ( s.equals("GamaList") ) { return "GL"; }
		if ( s.equals("MovingSkill") ) { return "MSK"; }
		if ( s.equals("WorldSkill") ) { return "WSK"; }
		if ( s.equals("GridSkill") ) { return "GSK"; }
		if ( s.equals("IGamaFile") ) { return "GF"; }
		if ( s.equals("IGraph") ) { return "GR"; }
		if ( s.equals("IPath") ) { return "IP"; }
		if ( s.equals("IList") ) { return "LI"; }
		if ( s.equals("ITopology") ) { return "IT"; }
		if ( s.equals("GamlAgent") ) { return "GA"; }
		if ( s.equals("ISpecies") ) { return "SP"; }
		return s + ".class";
	}

	private String toArrayOfStrings(final String array) {
		if ( array.equals("") ) { return "AS"; }
		String[] segments = array.split("\\,");
		String result = "S(";
		for ( int i = 0; i < segments.length; i++ ) {
			if ( i > 0 ) {
				result += ",";
			}
			result += toJava(segments[i]);
		}
		result += ")";
		return result;
	}

	String toBoolean(final String s) {
		return s.equals("true") ? "T" : "F";
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
			sb.append("new ").append(clazz);
			sb.append("(").append(handles).append("),");
		}
		sb.setLength(sb.length() - 1);
		sb.append(");");
	}

	protected void writeVarAddition(final StringBuilder sb, final String var, final String doc) {
		String[] segments = var.split("\\$");
		String type = toJava(segments[0]);
		String contentType = toJava(segments[1]);
		String name = toJava(segments[2]);
		String clazz = segments[3];
		String facets = segments[4];
		String getterHelper = null;
		String initerHelper = null;
		String setterHelper = null;
		// getter
		String getterName = segments[5];
		boolean isField = false;
		if ( !getterName.equals("null") ) {
			String ret = checkPrim(segments[6]);
			boolean dynamic = segments[7].equals("true");
			isField = segments[8].equals("true");
			boolean scope = segments[9].equals("true");

			if ( isField ) {
				getterHelper =
					concat("new IFieldGetter(){", OVERRIDE, "public ", ret, " run(", ISCOPE,
						" scope, ", IVALUE, " v){return v == null?", returnWhenNull(ret), ":((",
						clazz, ") v).", getterName, scope ? "(scope);}}" : "();}}");
			} else {
				getterHelper =
					concat("new VarGetter(", toClassObject(clazz), "){", OVERRIDE, "public ", ret,
						" run(", ISCOPE, " scope, ", IAGENT, " a, ", ISKILL,
						" t) {return t == null?", returnWhenNull(ret), ":((", clazz, ")t).",
						getterName, "(", scope ? "scope," : "", dynamic ? "a);}}" : ");}}");
			}

			// initer
			boolean init = segments[10].equals("true");
			if ( init ) {
				initerHelper = getterHelper;
			}
		}
		int i = getterHelper == null ? 6 : 11;
		// setter
		String setterName = segments[i];
		if ( !"null".equals(setterName) ) {
			String param = checkPrim(segments[i + 1]);
			boolean dyn = segments[i + 2].equals("true");
			boolean scope = segments[i + 3].equals("true");
			setterHelper =
				concat("new VarSetter(", toClassObject(clazz), ")", "{", OVERRIDE, "public void ",
					"run(IScope scope, IAgent a, ISkill t, Object arg)", " {if (t != null) ((",
					clazz, ") t).", setterName, "(", scope ? "scope," : "", dyn ? "a, " : "", "(" +
						param + ") arg); }}");

		}
		sb.append(in).append(isField ? "_field(" : "_var(").append(toClassObject(clazz))
			.append(",");
		if ( isField ) {
			sb.append("new TypeFieldExpression(").append(name).append(",T(").append(type)
				.append("), T(").append(contentType).append("),").append(getterHelper).append(")");
		} else {
			sb.append("desc(").append(type).append(",");
			sb.append(toArrayOfStrings(facets)).append("),").append(getterHelper);
			sb.append(',').append(initerHelper).append(',').append(setterHelper);
		}
		sb.append(");");
	}

	protected void writeSymbolAddition(final StringBuilder sb, final String s, final String doc) {
		String[] segments = s.split("\\$");
		String kind = segments[0];
		String clazz = segments[1];
		String remote = toBoolean(segments[2]);
		String args = toBoolean(segments[3]);
		String scope = toBoolean(segments[4]);
		String sequence = toBoolean(segments[5]);
		String unique = toBoolean(segments[6]);
		String name_unique = toBoolean(segments[7]);
		String parentSymbols = toArrayOfStrings(segments[8]);
		String parentKinds = segments[9];
		if ( parentKinds.equals("") ) {
			parentKinds = "AI";
		} else {
			parentKinds = "I(" + parentKinds + ")";
		}
		int nbFacets = Integer.decode(segments[10]);
		int pointer = 11;
		String facets;
		String constants = "";
		if ( nbFacets == 0 ) {
			facets = "null";
			pointer++;
		} else {
			facets = "P(";
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
				String values = segments[pointer++];
				if ( !values.isEmpty() ) {
					constants += toArrayOfStrings(values) + ",";
				}
				facets += toArrayOfStrings(values) + ",";
				// optional
				facets += segments[pointer++] + ',';
				// doc
				facets += toJava(segments[pointer++]);
				facets += ")";
			}
			facets += ")";
		}
		// pointer++;
		// pointer++;

		// new String[][]{ {"s","a"},{"s","b"}},
		int nbCombinations = Integer.parseInt("0" + segments[pointer++]);
		String combinations = "";
		if ( nbCombinations == 0 ) {
			combinations = "null";
		} else {
			for ( int i = 0; i < nbCombinations; i++ ) {
				if ( i > 0 ) {
					combinations += ",";
				}
				combinations += toArrayOfStrings(segments[pointer++]);
			}
		}
		pointer++;

		String omissible = segments[pointer++];

		String sc =
			concat("new ISymbolConstructor() {", OVERRIDE, "public ISymbol create(" + IDESC +
				" d) {return new ", clazz, "(d);}}");
		sb.append(in).append("_symbol(").append(toClassObject(clazz)).append(",").append(kind)
			.append(',').append(remote).append(',').append(args).append(',').append(scope)
			.append(',').append(sequence).append(',').append(unique).append(',')
			.append(name_unique).append(',').append(parentSymbols).append(",").append(parentKinds)
			.append(',').append(facets).append(',').append(toJava(omissible)).append(',')
			.append("new String[][]{").append(combinations).append("},")
			// .append("new String[][]{}").append(",")
			// .append("Collections.<String[]> emptyList()").append(",")
			.append(sc);
		if ( segments.length > pointer ) {
			for ( int i = pointer; i < segments.length; i++ ) {
				sb.append(',').append(toJava(segments[i]));
			}
		}
		sb.append(");");
		if ( !constants.isEmpty() ) {
			constants = constants.substring(0, constants.length() - 1);
			sb.append("_constants(").append(constants).append(");");
		}
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
				sb.append(',').append(toClassObject(segments[i]));
			}
		}
		sb.append(");");
	}

	protected void writeOperatorAddition(final StringBuilder sb, final String s, final String doc) {
		String[] segments = s.split("\\$");
		int arg_number = Integer.decode(segments[0]);
		String[] classes = new String[arg_number];
		int index = 1;
		for ( int i = index; i <= arg_number; i++ ) {
			classes[i - 1] = segments[i];
		}
		index += arg_number;
		String canBeConst = toBoolean(segments[index++]);
		String type = segments[index++];
		String contentType = segments[index++];
		boolean iterator = segments[index++].equals("true");
		// String priority = segments[index++];
		String ret = segments[index++];
		String m = segments[index++];
		boolean stat = segments[index++].equals("true");
		boolean scope = segments[index++].equals("true");
		String kw = "S(";
		for ( int i = index; i < segments.length; i++ ) {
			kw += toJava(segments[i]);
			if ( i < segments.length - 1 ) {
				kw += ",";
			}
		}
		kw += ")";
		String classNames = "C(";
		for ( int i = 0; i < classes.length; i++ ) {
			classNames += toClassObject(classes[i]);
			if ( i < classes.length - 1 ) {
				classNames += ",";
			}
		}
		classNames += ")";
		String helper =
			concat("new IOpRun(){", OVERRIDE, "public ", checkPrim(ret), " run(", ISCOPE,
				" s,Object... o)", buildNAry(classes, m, ret, stat, scope), "}");

		sb.append(in).append(iterator ? "_iterator(" : "_operator(").append(kw).append(',')
			.append(classNames).append(",").append(toClassObject(ret))/* .append(",").append(priority) */
			.append(',').append(canBeConst).append(',').append(type).append(',')
			.append(contentType).append(',').append(helper).append(");");
	}

	private String toJava(final String s) {
		if ( s == null || s.isEmpty() ) { return "(String)null"; }
		int i = ss1.indexOf(s);
		return i == -1 ? "\"" + replaceCommas(s) + "\"" : ss2.get(i);
	}

	private String replaceCommas(final String s) {
		return s.replace("COMMA", ",");
	}

	final static List<String> ss1 = Arrays.asList("const", "true", "false", "name", "type");
	final static List<String> ss2 = Arrays.asList("CONST", "TRUE", "FALSE", "NAME", "TYPE");

	protected void writeSpecies(final StringBuilder sb, final String s, final String doc) {
		String[] segments = s.split("\\$");
		String name = segments[0];
		String clazz = segments[1];
		sb.append(in).append("_species(").append(toJava(name)).append(",")
			.append(toClassObject(clazz))
			.append(", new IAgentConstructor(){" + OVERRIDE + "public ").append(IAGENT)
			.append(" createOneAgent(").append(IPOPULATION).append(" p) {return new ")
			.append(clazz).append("(p);}}");
		for ( int i = 2; i < segments.length; i++ ) {
			sb.append(",").append(toJava(segments[i]));
		}
		sb.append(");");
	}

	protected void writeActionAddition(final StringBuilder sb, final String s, final String doc) {
		String[] segments = s.split("\\$");
		String method = segments[0];
		String clazz = segments[1];
		String virtual = segments[3];
		String name = segments[4];
		String ret = checkPrim(segments[2]);
		int nbArgs = Integer.decode(segments[5]);
		String args = "new ChildrenProvider(Arrays.asList(";
		// TODO Argument types not taken into account when declaring them
		int pointer = 6;
		for ( int i = 0; i < nbArgs; i++ ) {
			if ( i > 0 ) {
				args += ",";
			}
			// picking name
			args += "desc(ARG,NAME," + toJava(segments[pointer++]) + ')';
			// skipping type
			pointer++;
			// skipping optional
			pointer++;
			// skipping doc
			pointer++;
		}
		args += "))";
		String desc =
			"desc(PRIMITIVE, null, " + args + ", NAME, " + toJava(name) + ",TYPE, " + "T(" +
				toClassObject(ret) + ").toString(), JAVA," + toJava(method) + ", VIRTUAL," +
				toJava(virtual) + ")";
		sb.append(concat(in, "_action(", toJava(method), ",", toClassObject(clazz),
			",new PrimRun(T(", toClassObject(ret), "), ", toClassObject(clazz), "){", OVERRIDE,
			"public ", ret.equals("void") ? "Object" : ret, " run(", ISKILL, " t,", IAGENT, " a,",
			ISCOPE, " s){ ", !ret.equals("void") ? "return" : "", " ((", clazz, ") t).", method,
			"(s); ", ret.equals("void") ? "return null;" : "", "} },", desc, ");"));
	}

	protected void writeSkill(final StringBuilder sb, final String s, final String doc) {
		String[] segments = s.split("\\$");
		String name = segments[0];
		String clazz = segments[1];
		sb.append(concat(in, "_skill(", toJava(name), ",", toClassObject(clazz),
			", new ISkillConstructor(){", OVERRIDE, "public ISkill newInstance(){return new ",
			clazz, "();}}"));
		for ( int i = 2; i < segments.length; i++ ) {
			sb.append(",").append(toJava(segments[i]));
		}
		sb.append(");");
	}

	protected void writeDisplay(final StringBuilder sb, final String s, final String doc) {
		String[] segments = s.split("\\$");
		String name = segments[0];
		String clazz = segments[1];
		sb.append(concat(in, "_display(", toJava(name), ",", toClassObject(clazz),
			", new IDisplayCreator(){", OVERRIDE, "public IDisplaySurface create(){return new ",
			clazz, "();}}"));
		sb.append(");");
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
		sb.append(ln).append(tab);
		sb.append("public void initialize() {");
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
		if ( returnClass.equals(DOUBLE) ) { return " 0d"; }
		if ( returnClass.equals(INTEGER) ) { return " 0"; }
		if ( returnClass.equals(BOOLEAN) ) { return " false"; }
		return " null";
	}

	protected String buildNAry(final String[] classes, final String name, final String retClass,
		final boolean stat, final boolean scope) {
		String ret = checkPrim(retClass);
		int index = stat ? 0 : 1;
		String firstArg = scope ? "s" : "";
		String body =
			stat ? concat("{return ", name, "(", firstArg) : concat("{return o[0]", " == null?",
				returnWhenNull(ret), ":((", classes[0], ")o[0]).", name, "(", firstArg);
		if ( index < classes.length ) {
			if ( scope ) {
				body += ",";
			}
			for ( int i = index; i < classes.length; i++ ) {
				body += param(classes[i], "o[" + i + "]") + (i != classes.length - 1 ? "," : "");
			}
		}
		body += ");}";
		return body;
	}

	protected String buildBinary(final String lc, final String rc, final String name,
		final String retClass, final boolean stat, final boolean scope) {
		String ret = checkPrim(retClass);
		return stat ? scope ? concat("{return ", name, "(s,", param(lc, "t"), ",", param(rc, "r"),
			");}") : concat("{return ", name, "(", param(lc, "t"), ",", param(rc, "r"), ");}")
			: scope ? concat("{return t == null?", returnWhenNull(ret), ":((", lc, ")t).", name,
				"(s,", param(rc, "r"), ");}") : concat("{return t == null?", returnWhenNull(ret),
				":((", lc, ")t).", name, "(", param(rc, "r"), ");}");
	}

	protected String buildUnary(final String c, final String name, final String returnClass,
		final boolean stat, final boolean scope) {
		String ret = checkPrim(returnClass);
		return stat ? scope ? concat("{return ", name, "(s,", param(c, "t"), ");}") : concat(
			"{return ", name, "(", param(c, "t"), ");}") : scope ? concat("{return t == null?",
			returnWhenNull(ret), ":((", c, ")t).", name, "(s);}") : concat("{return t == null?",
			returnWhenNull(ret), ":((", c, ")t).", name, "();}");
	}

}
