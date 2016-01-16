/*********************************************************************************************
 *
 *
 * 'JavaWriter.java', in plugin 'msi.gama.processor', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
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
	public final static String FILE_PREFIX = "+";
	public final static String DOC_PREFIX = "@";
	public final static String CONSTANT_PREFIX = "£";
	public final static String POPULATIONS_LINKER_PREFIX = "�";
	public final static String DOC_SEP = "~";
	public final static String DOC_REGEX = "\\~";
	public final static String SEP = "$";
	static String ln = "\n";
	static String tab = "\t";
	static String in = ln;
	final static String OVERRIDE = " @Override ";
	final static String IAGENT = "IAgent";
	final static String IPOPULATION = "IPopulation";
	final static String ISIMULATION = "ISimulation";
	final static String ISKILL = "ISkill";
	final static String ISUPPORT = "IVarAndActionSupport";
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
	final static String[] IMPORTS = new String[] { "msi.gama.outputs.layers", "msi.gama.outputs",
		"msi.gama.kernel.batch", "msi.gaml.architecture.weighted_tasks", "msi.gaml.architecture.user",
		"msi.gaml.architecture.reflex", "msi.gaml.architecture.finite_state_machine", "msi.gaml.species",
		"msi.gama.metamodel.shape", "msi.gaml.expressions", "msi.gama.metamodel.topology",
		"msi.gama.metamodel.population", "msi.gama.kernel.simulation", "java.util", " msi.gama.metamodel.shape",
		"msi.gama.common.interfaces", "msi.gama.runtime", "java.lang", "msi.gama.metamodel.agent", "msi.gaml.types",
		"msi.gaml.compilation", "msi.gaml.factories", "msi.gaml.descriptions", "msi.gama.util.file",
		"msi.gama.util.matrix", "msi.gama.util.graph", "msi.gama.util.path", "msi.gama.util",
		"msi.gama.runtime.exceptions", "msi.gaml.factories", "msi.gaml.statements", "msi.gaml.skills",
		"msi.gaml.variables", "msi.gama.kernel.experiment", "msi.gaml.operators", "msi.gaml.extensions.genstar" };
	final static String[] EXPLICIT_IMPORTS = new String[] { "msi.gaml.operators.Random", "msi.gaml.operators.Maths",
		"msi.gaml.operators.Points", "msi.gaml.operators.Spatial.Properties", "msi.gaml.operators.System" };

	// Keeps track of the current index of the documentation in the GamlDocumentation.contents list
	int docCount = 0;

	public String write(final String packageName, final GamlProperties props, final StringBuilder sb/*
																									 * ,
																									 * final StringBuilder doc
																									 */) {
		writeHeader(sb, packageName);
		// writeDocHeader(doc, packageName);
		sb.append(ln);
		writeTypesInitialization(props, sb/* , doc */);
		sb.append(ln);
		writeSpeciesInitialization(props, sb/* , doc */);
		sb.append(ln);
		writeSymbolsInitialization(props, sb/* , doc */);
		sb.append(ln);
		writeVarsInitialization(props, sb/* , doc */);
		sb.append(ln);
		writeOperatorsInitialization(props, sb/* , doc */);
		sb.append(ln);
		writeFilesInitialization(props, sb/* , doc */);
		sb.append(ln);
		writeActionsInitialization(props, sb/* , doc */);
		sb.append(ln);
		writeSkillsInitialization(props, sb/* , doc */);
		sb.append(ln);
		writeDisplaysInitialization(props, sb/* , doc */);
		sb.append(ln);
		writePopulationsLinkersInitialization(props, sb);
		sb.append(ln);
		writeFooter(sb);
		// writeDocFooter(doc);
		return sb.toString();
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
		// sb.append(
		// "static int base = GamlDocumentation.getCount(); static GamlDocumentation _doc = GamlDocumentation.getInstance();")
		// .append(ln);
		// sb.append(" protected static int DOC(int i) { return base + i;}").append(ln).append(ln);

		sb.append("public void initialize() throws SecurityException, NoSuchMethodException {");
		sb.append(ln).append(tab).append("initializeTypes();");
		sb.append(ln).append(tab).append("initializeSymbols();");
		sb.append(ln).append(tab).append("initializeVars();");
		sb.append(ln).append(tab).append("initializeOperators();");
		sb.append(ln).append(tab).append("initializeFiles();");
		sb.append(ln).append(tab).append("initializeActions();");
		sb.append(ln).append(tab).append("initializeSkills();");
		sb.append(ln).append(tab).append("initializeSpecies();");
		sb.append(ln).append(tab).append("initializeDisplays();");
		sb.append(ln).append(tab).append("initializePopulationsLinkers();");
		sb.append(ln).append('}');
	}

	private void writeDisplaysInitialization(final GamlProperties props,
		final StringBuilder sb/* , final StringBuilder doc */) {
		sb.append("public void initializeDisplays() {");
		for ( Map.Entry<String, String> entry : props.filterFirst(DISPLAY_PREFIX).entrySet() ) {
			writeDisplay(sb /* ,doc */, entry.getKey(), entry.getValue());
		}
		sb.append("};");
	}

	private void writePopulationsLinkersInitialization(final GamlProperties props, final StringBuilder sb) {
		sb.append("public void initializePopulationsLinkers() {");
		for ( Map.Entry<String, String> entry : props.filterFirst(POPULATIONS_LINKER_PREFIX).entrySet() ) {
			writePopulationsLinker(sb, entry.getKey(), entry.getValue());
		}
		sb.append("};");
	}

	private void writeActionsInitialization(final GamlProperties props,
		final StringBuilder sb/* , final StringBuilder doc */) {
		sb.append("public void initializeActions() {");
		for ( Map.Entry<String, String> entry : props.filterFirst(ACTION_PREFIX).entrySet() ) {
			writeActionAddition(sb /* ,doc */, entry.getKey(), entry.getValue());
		}
		sb.append("};");
	}

	private void writeSpeciesInitialization(final GamlProperties props,
		final StringBuilder sb/* , final StringBuilder doc */) {
		sb.append("public void initializeSpecies() {");
		for ( Map.Entry<String, String> entry : props.filterFirst(SPECIES_PREFIX).entrySet() ) {
			writeSpecies(sb /* ,doc */, entry.getKey(), entry.getValue());
		}
		sb.append("};");
	}

	private void writeSkillsInitialization(final GamlProperties props,
		final StringBuilder sb/* , final StringBuilder doc */) {
		sb.append("public void initializeSkills() {");
		for ( Map.Entry<String, String> entry : props.filterFirst(SKILL_PREFIX).entrySet() ) {
			writeSkill(sb /* ,doc */, entry.getKey(), entry.getValue());
		}
		sb.append("};");
	}

	private void writeFilesInitialization(final GamlProperties props,
		final StringBuilder sb/* , final StringBuilder doc */) {
		sb.append("public void initializeFiles() throws SecurityException, NoSuchMethodException  {");
		for ( Map.Entry<String, String> entry : props.filterFirst(FILE_PREFIX).entrySet() ) {
			writeFileAddition(sb /* ,doc */, entry.getKey(), entry.getValue());
		}
		sb.append("};");
	}

	void writeTypesInitialization(final GamlProperties props, final StringBuilder sb/* , final StringBuilder doc */) {
		sb.append("public void initializeTypes() {");
		for ( Map.Entry<String, String> entry : props.filterFirst(TYPE_PREFIX).entrySet() ) {
			writeType(sb /* ,doc */, entry.getKey(), entry.getValue());
		}
		writeFactoriesAddition(sb, props.filterFirst(FACTORY_PREFIX));
		sb.append("};");
	}

	void writeSymbolsInitialization(final GamlProperties props, final StringBuilder sb/* , final StringBuilder doc */) {
		sb.append("public void initializeSymbols() {");
		for ( Map.Entry<String, String> entry : props.filterFirst(SYMBOL_PREFIX).entrySet() ) {
			writeSymbolAddition(sb /* ,doc */, entry.getKey()/* , entry.getValue() */);
		}
		sb.append("};");
	}

	void writeVarsInitialization(final GamlProperties props, final StringBuilder sb/* , final StringBuilder doc */) {
		sb.append("public void initializeVars() throws SecurityException, NoSuchMethodException {");
		for ( Map.Entry<String, String> entry : props.filterFirst(VAR_PREFIX).entrySet() ) {
			writeVarAddition(sb /* ,doc */, entry.getKey(), entry.getValue());
		}
		sb.append("};");
	}

	void writeOperatorsInitialization(final GamlProperties props,
		final StringBuilder sb/* , final StringBuilder doc */) {
		sb.append("public void initializeOperators() throws SecurityException, NoSuchMethodException  {");
		for ( Map.Entry<String, String> entry : props.filterFirst(OPERATOR_PREFIX).entrySet() ) {
			writeOperatorAddition(sb /* ,doc */, entry.getKey(), entry.getValue());
		}
		sb.append("};");
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
		return toArrayOfStrings(array, "\\,");
	}

	private String toArrayOfInts(final String array) {
		if ( array == null || array.length() == 0 ) { return "AI"; }
		return "I(" + array + ")";
	}

	private String toArrayOfStrings(final String array, final String regex) {
		if ( array == null || array.equals("") ) { return "AS"; }
		// FIX AD 3/4/13: split(regex) would not include empty trailing strings
		String[] segments = array.split(regex, -1);
		String result = "S(";
		for ( int i = 0; i < segments.length; i++ ) {
			if ( i > 0 ) {
				result += ",";
			}
			result += toJavaString(segments[i]);
		}
		result += ")";
		return result;
	}

	final static List<String> ss1 = Arrays.asList("const", "true", "false", "name", "type");
	final static List<String> ss2 = Arrays.asList("CONST", "TRUE", "FALSE", "NAME", "TYPE");

	private String toJavaString(final String s) {
		if ( s == null || s.isEmpty() ) { return "(String)null"; }
		int i = ss1.indexOf(s);
		return i == -1 ? "\"" + replaceCommas(s) + "\"" : ss2.get(i);
	}

	private String replaceCommas(final String s) {
		String result = s.replace("COMMA", ",");
		result = result.replace("\"", "\\\"");
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
	protected void writeFactoriesAddition(final StringBuilder sb, final Map<String, String> factoryMap) {
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

	protected void writeFileAddition(final StringBuilder sb, /* final StringBuilder docBuilder, */final String file,
		final String doc) {
		String[] segments = file.split("\\$");
		String name = toJavaString(segments[0]);
		String clazz = segments[1];
		String type = segments[2];
		String contentType = segments[3];
		String keyType = segments[4];
		String suffixes = toArrayOfStrings(segments[5]);
		String helper = buildFileConstructor(new String[] { "String" }, clazz);
		sb.append(in).append("_file(").append(name).append(',').append(toClassObject(clazz)).append(',').append(helper)
			.append(",").append(type).append(",").append(keyType).append(",").append(contentType).append(",")
			.append(suffixes).append(");");
		writeIsFileOperator(sb, name);
		for ( int i = 6; i < segments.length; i++ ) {
			writeCreateFileOperator(sb, name, clazz, segments[i], contentType, keyType);
		}
	}

	protected void writeVarAddition(final StringBuilder sb, /* final StringBuilder docBuilder, */final String var,
		final String doc) {
		String[] segments = var.split("\\$");
		String type = segments[0];
		String contentType = segments[1];
		String keyType = segments[2];
		String name = toJavaString(segments[3]);
		String clazz = segments[4];
		String facets = segments[5];
		String getterHelper = null;
		String initerHelper = null;
		String setterHelper = null;
		// getter
		String getterName = segments[6];
		boolean isField = false;
		if ( !getterName.equals("null") ) {
			String ret = checkPrim(segments[7]);
			boolean dynamic = segments[8].equals("true");
			isField = segments[9].equals("true");
			boolean scope = segments[10].equals("true");

			if ( isField ) {
				getterHelper = concat("new GamaHelper(){", OVERRIDE, "public ", ret, " run(", ISCOPE, " scope, ",
					OBJECT, "... v){return (v==null||v.length==0)?", returnWhenNull(ret), ":((", clazz, ") v[0]).",
					getterName, scope ? "(scope);}}" : "();}}");
			} else {
				getterHelper = concat("new GamaHelper(", toClassObject(clazz), "){", OVERRIDE, "public ", ret, " run(",
					ISCOPE, " scope, ", IAGENT, " a, ", ISUPPORT, " t, Object... v) {return t == null?",
					returnWhenNull(ret), ":((", clazz, ")t).", getterName, "(", scope ? "scope" : "",
					dynamic ? (scope ? "," : "") + "a);}}" : ");}}");
			}

			// initer
			boolean init = segments[11].equals("true");
			if ( init ) {
				initerHelper = getterHelper;
			}
		}
		int i = getterHelper == null ? 7 : 12;
		// setter
		String setterName = segments[i];
		if ( !"null".equals(setterName) ) {
			String param = checkPrim(segments[i + 1]);
			boolean dyn = segments[i + 2].equals("true");
			boolean scope = segments[i + 3].equals("true");
			setterHelper = concat("new GamaHelper(", toClassObject(clazz), ")", "{", OVERRIDE, "public Object ",
				" run(", ISCOPE, " scope, ", IAGENT, " a, ", ISUPPORT, " t, Object... arg)", " {if (t != null) ((",
				clazz, ") t).", setterName, "(", scope ? "scope," : "", dyn ? "a, " : "",
				"(" + param + ") arg[0]); return null; }}");

		}
		sb.append(in).append(isField ? "_field(" : "_var(").append(toClassObject(clazz)).append(",");
		if ( isField ) {
			sb.append("new OperatorProto(").append(name).append(", null, ").append(getterHelper)
				.append(", false, true, ").append(type).append(",").append(toClassObject(clazz)).append(", false, ")
				.append(type).append(",").append(contentType).append(",").append(keyType).append(",")
				.append(toArrayOfInts(null)).append(")");
		} else {
			sb.append("desc(").append(type).append(",");
			sb.append(toArrayOfStrings(facets)).append("),").append(getterHelper);
			sb.append(',').append(initerHelper).append(',').append(setterHelper);
		}
		sb.append(");");
	}

	protected void writeSymbolAddition(final StringBuilder sb, final String s) {
		String[] segments = s.split("\\$");
		String validator = segments[0];
		if ( validator.isEmpty() ) {
			validator = "null";
		} else {
			validator = "new " + validator + "()";
		}
		String serializer = segments[1];
		if ( serializer.isEmpty() ) {
			serializer = "null";
		} else {
			serializer = "new " + serializer + "()";
		}
		String kind = segments[2];
		String clazz = segments[3];
		String remote = toBoolean(segments[4]);
		String args = toBoolean(segments[5]);
		String scope = toBoolean(segments[6]);
		String sequence = toBoolean(segments[7]);
		String unique = toBoolean(segments[8]);
		String name_unique = toBoolean(segments[9]);
		String parentSymbols = toArrayOfStrings(segments[10]);
		String parentKinds = segments[11];
		if ( parentKinds.equals("") ) {
			parentKinds = "AI";
		} else {
			parentKinds = "I(" + parentKinds + ")";
		}
		int nbFacets = Integer.decode(segments[12]);
		int pointer = 13;
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
				facets += toJavaString(segments[pointer++]) + ',';
				// types
				facets += toArrayOfInts(segments[pointer++]) + ",";
				// values
				String values = segments[pointer++];
				if ( !values.isEmpty() ) {
					constants += toArrayOfStrings(values) + ",";
				}
				facets += toArrayOfStrings(values) + ",";
				// optional
				facets += segments[pointer++] + ',';
				// internal
				facets += segments[pointer++] + ',';
				// doc
				facets += toJavaString(segments[pointer++]);
				facets += ")";
			}
			facets += ")";
		}

		String omissible = segments[pointer++];

		String sc = concat("new ISymbolConstructor() {", OVERRIDE,
			"public ISymbol create(" + IDESC + " d) {return new ", clazz, "(d);}}");
		sb.append(in).append("_symbol(").append(toClassObject(clazz)).append(",");
		// sb.append("DOC(").append(addDoc(docBuilder, toArrayOfStrings(doc, DOC_REGEX))).append("),")
		sb.append(validator).append(',').append(serializer);
		sb.append(",").append(kind).append(',').append(remote).append(',').append(args).append(',').append(scope)
			.append(',');
		sb.append(sequence).append(',').append(unique).append(',').append(name_unique).append(',').append(parentSymbols)
			.append(",");
		sb.append(parentKinds).append(',').append(facets).append(',').append(toJavaString(omissible)).append(',')
			.append(sc);
		if ( segments.length > pointer ) {
			for ( int i = pointer; i < segments.length; i++ ) {
				sb.append(',').append(toJavaString(segments[i]));
			}
		}
		sb.append(");");
		if ( !constants.isEmpty() ) {
			constants = constants.substring(0, constants.length() - 1);
			sb.append("_constants(").append(constants).append(");");
		}
	}

	protected void writeType(final StringBuilder sb, /* final StringBuilder docBuilder, */final String s,
		final String doc) {
		String[] segments = s.split("\\$");
		String keyword = segments[0];
		String id = segments[1];
		String varKind = segments[2];
		String clazz = segments[3];
		sb.append(in).append("_type(").append(toJavaString(keyword)).append(",new ").append(clazz).append("(),")
			.append(id).append(',').append(varKind);
		if ( segments.length > 4 ) {
			for ( int i = 4; i < segments.length; i++ ) {
				sb.append(',').append(toClassObject(segments[i]));
			}
		}
		sb.append(");");
	}

	protected void writeOperatorAddition(final StringBuilder sb, /* final StringBuilder docBuilder, */final String s,
		final String doc) {
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
		String indexType = segments[index++];
		boolean iterator = segments[index++].equals("true");
		int expected = Integer.decode(segments[index++]);
		String[] expected_ct = new String[expected];
		for ( int i = 0; i < expected; i++ ) {
			expected_ct[i] = segments[index++];
		}
		String ret = segments[index++];
		String m = segments[index++];
		boolean stat = segments[index++].equals("true");
		boolean scope = segments[index++].equals("true");
		String kw = "S(";
		for ( int i = index; i < segments.length; i++ ) {
			kw += toJavaString(segments[i]);
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
		String content_type_expected = "I(";
		for ( int i = 0; i < expected; i++ ) {
			content_type_expected += expected_ct[i];
			if ( i < expected - 1 ) {
				content_type_expected += ",";
			}
		}
		content_type_expected += ")";
		String helper = concat("new GamaHelper(){", OVERRIDE, "public ", checkPrim(ret), " run(", ISCOPE,
			" s,Object... o)", buildNAry(classes, m, ret, stat, scope), "}");

		sb.append(in).append(iterator ? "_iterator(" : "_operator(").append(kw).append(',')
			.append(buildMethodCall(classes, m, stat, scope)).append(',').append(classNames).append(",")
			.append(content_type_expected).append(",").append(toClassObject(ret)).append(',').append(canBeConst)
			.append(',').append(type).append(',').append(contentType).append(',').append(indexType).append(',')
			.append(
				helper)/* .append(',').append("DOC(").append(addDoc(docBuilder, toArrayOfStrings(doc, DOC_REGEX))) */
			/* .append(")") */.append(");");
	}

	protected void writeSpecies(final StringBuilder sb, /* final StringBuilder docBuilder, */final String s,
		final String doc) {
		String[] segments = s.split("\\$");
		String name = segments[0];
		String clazz = segments[1];
		sb.append(in).append("_species(").append(toJavaString(name)).append(",").append(toClassObject(clazz))
			.append(", new IAgentConstructor(){" + OVERRIDE + "public ").append(IAGENT).append(" createOneAgent(")
			.append(IPOPULATION).append(" p) {return new ").append(clazz).append("(p);}}");
		for ( int i = 2; i < segments.length; i++ ) {
			sb.append(",").append(toJavaString(segments[i]));
		}
		sb.append(");");
	}

	/**
	 * @param sb
	 * @param name
	 */
	private void writeIsFileOperator(final StringBuilder sb, final String name) {
		String helper = concat("new GamaHelper(){", OVERRIDE, "public Boolean run(", ISCOPE,
			" s,Object... o) { return GamaFileType.verifyExtension(", name, ",(String)o[0]);}}");
		sb.append(in).append("_operator(S(").append(toJavaString("is_")).append("+").append(name)
			.append("),null,C(S),I(0),B,true,3,0,0,").append(helper).append(");");
	}

	private void writeCreateFileOperator(final StringBuilder sb, final String name, final String clazz,
		final String classes, final String contentType, final String keyType) {
		String[] names = classes.split(",");
		String helper = buildFileConstructor(names, clazz);
		String classNames = "C(";
		for ( int i = 0; i < names.length; i++ ) {
			classNames += toClassObject(names[i]);
			if ( i < names.length - 1 ) {
				classNames += ",";
			}
		}
		classNames += ")";
		// AD 13/04/14: Changed true to false in the "can_be_const" parameter
		sb.append(in).append("_operator(S(").append(name).append("+").append(toJavaString("_file")).append("),")
			.append(buildConstructor(names, clazz)).append(',').append(classNames).append(",I(0),GF,false,12,")
			.append(contentType).append(",").append(keyType).append(",").append(helper).append(");");
	}

	protected String buildConstructor(final String[] classes, final String className) {
		// String methodName = extractMethod(name, stat);
		String result = toClassObject(className) + ".getConstructor(";
		result += toClassObject(ISCOPE) + ",";
		for ( int i = 0; i < classes.length; i++ ) {
			result += toClassObject(classes[i]) + ",";
		}
		if ( result.endsWith(",") ) {
			result = result.substring(0, result.length() - 1);
		}
		result += ")";
		return result;
	}

	protected String buildFileConstructor(final String[] classes, final String className) {
		String body = concat("new GamaHelper(){", OVERRIDE, "public IGamaFile run(", ISCOPE,
			" s,Object... o) {return new ", className, "(s");
		for ( int i = 0; i < classes.length; i++ ) {
			body += ",";
			body += param(classes[i], "o[" + i + "]");
		}
		body += ");}}";
		return body;
	}

	protected void writeActionAddition(final StringBuilder sb, /* final StringBuilder docBuilder, */final String s,
		final String doc) {
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
			args += "desc(ARG,NAME," + toJavaString(segments[pointer++]);
			// skipping type
			pointer++;
			// not skipping optional
			String optional = toJavaString(segments[pointer++]);
			args += ", \"optional\", " + optional;
			// pointer++;
			// skipping doc
			pointer++;
			args += ')';
		}
		args += "))";
		String desc = "desc(PRIMITIVE, null, " + args + ", NAME, " + toJavaString(name) + ",TYPE, " + "Ti(" +
			toClassObject(ret) + "), VIRTUAL," + toJavaString(virtual) + ")";
		sb.append(concat(in, "_action(", toJavaString(method), ",", toClassObject(clazz), ",new GamaHelper(T(",
			toClassObject(ret), "), ", toClassObject(clazz), "){", OVERRIDE, "public ",
			ret.equals("void") ? "Object" : ret, " run(", ISCOPE, " s, ", IAGENT, " a, ", ISUPPORT,
			" t, Object... v){ ", !ret.equals("void") ? "return" : "", " ((", clazz, ") t).", method, "(s); ",
			ret.equals("void") ? "return null;" : "", "} },", desc, ");"));
	}

	protected void writeSkill(final StringBuilder sb, /* final StringBuilder docBuilder, */final String s,
		final String doc) {
		String[] segments = s.split("\\$");
		String name = segments[0];
		String clazz = segments[1];
		sb.append(concat(in, "_skill(", toJavaString(name), ",", toClassObject(clazz)));
		for ( int i = 2; i < segments.length; i++ ) {
			sb.append(",").append(toJavaString(segments[i]));
		}
		sb.append(");");
	}

	protected void writeDisplay(final StringBuilder sb, /* final StringBuilder docBuilder, */final String s,
		final String doc) {
		String[] segments = s.split("\\$");
		String name = segments[0];
		String clazz = segments[1];
		sb.append(concat(in, "_display(", toJavaString(name), ",", toClassObject(clazz), ", new IDisplayCreator(){",
			OVERRIDE, "public IDisplaySurface create(Object...args){return new ", clazz, "(args);}}"));
		sb.append(");");
	}

	protected void writePopulationsLinker(final StringBuilder sb, final String s, final String doc) {
		String[] segments = s.split("\\$");
		String name = segments[0];
		String clazz = segments[1];
		sb.append(concat(in, "_populationsLinker(", toJavaString(name), ",", toClassObject(clazz),
			", new IGamaPopulationsLinkerConstructor() {", OVERRIDE,
			"public IGamaPopulationsLinker newInstance() {return new ", clazz, "();}}"));
		sb.append(");");
	}

	protected void writeDocHeader(final StringBuilder sb, final String packageName) {
		sb.append("package ").append(packageName).append(';');
		sb.append(ln).append("import ").append("java.util").append(".*;");
		sb.append(ln).append("import msi.gaml.compilation.*;");
		sb.append(ln).append(ln).append(docDefinition()).append(" {");
		sb.append(ln);
		sb.append("static GamlDocumentation instance;");
		sb.append(
			"static GamlDocumentation getInstance() {if (instance == null) {	instance = new GamlDocumentation(); instance.initialize();} return instance;}");
		sb.append(ln).append(tab).append(" void initialize() {");
		sb.append(ln);
	}

	protected int addDoc(final StringBuilder sb, final String doc) {
		// doc already formatted as a java array of strings
		// sb.append("contents.add(").append("S(\"\") ").append(");");
		String s = "AS".equals(doc) ? doc + "()" : doc;
		sb.append(s).append(";").append(ln);
		return docCount++;
	}

	protected String classDefinition() {
		return "public class GamlAdditions extends AbstractGamlAdditions";
	}

	protected String docDefinition() {
		return "public class GamlDocumentation extends AbstractGamlDocumentation";
	}

	protected String docClassName() {
		return "GamlDocumentation";
	}

	protected String simpleClassName() {
		return "GamlAdditions";
	}

	protected void writeFooter(final StringBuilder sb) {
		sb.append(ln).append('}');
	}

	protected void writeDocFooter(final StringBuilder sb) {
		sb.append(ln).append('}');
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
			if ( clazz.startsWith(IMPORTS[i]) && !clazz.replace(IMPORTS[i] + ".", "").contains(".") ) { return clazz
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

	private String extractMethod(final String s, final boolean stat) {
		if ( !stat ) { return s; }
		return s.substring(s.lastIndexOf('.') + 1);
	}

	private String extractClass(final String name, final String string, final boolean stat) {
		if ( stat ) { return name.substring(0, name.lastIndexOf('.')); }
		return string;
	}

	protected String buildMethodCall(final String[] classes, final String name, final boolean stat,
		final boolean scope) {
		int index = stat ? 0 : 1;
		String methodName = extractMethod(name, stat);
		String className = toClassObject(extractClass(name, classes[0], stat));
		String result = className + ".getMethod(" + toJavaString(methodName) + ", ";
		result += scope ? toClassObject(ISCOPE) + "," : "";
		for ( int i = index; i < classes.length; i++ ) {
			result += toClassObject(classes[i]) + ",";
		}
		if ( result.endsWith(",") ) {
			result = result.substring(0, result.length() - 1);
		}
		result += ")";
		return result;
	}

	protected String buildNAry(final String[] classes, final String name, final String retClass, final boolean stat,
		final boolean scope) {
		String ret = checkPrim(retClass);
		int index = stat ? 0 : 1;
		String firstArg = scope ? "s" : "";
		String body = stat ? concat("{return ", name, "(", firstArg) : concat("{return o[0]", " == null?",
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

}
