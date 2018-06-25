package msi.gama.precompiler;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.constant;
import msi.gama.precompiler.GamlAnnotations.display;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.experiment;
import msi.gama.precompiler.GamlAnnotations.factory;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.tests;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.doc.DocProcessor;
import msi.gama.precompiler.tests.TestProcessor;

public interface Constants {

	public static String capitalizeFirstLetter(final String original) {
		if (original == null || original.length() == 0) { return original; }
		return original.substring(0, 1).toUpperCase() + original.substring(1);
	}

	public static String capitalizeAllWords(final String str) {
		if (str == null || str.length() == 0) { return str; }
		final int strLen = str.length();
		final StringBuffer buffer = new StringBuffer(strLen);
		boolean capitalizeNext = true;
		for (int i = 0; i < strLen; i++) {
			final char ch = str.charAt(i);
			if (' ' == ch) {
				buffer.append(ch);
				capitalizeNext = true;
			} else if (capitalizeNext) {
				buffer.append(Character.toTitleCase(ch));
				capitalizeNext = false;
			} else {
				buffer.append(ch);
			}
		}
		return buffer.toString();

	}

	public static String getAlphabetOrder(final String name) {
		String order = "";
		final String lastChar = "z";

		for (int i = 0; i < cuttingLettersOperatorDoc.length; i++) {
			final Character previousChar = i == 0 ? 'a' : cuttingLettersOperatorDoc[i - 1];
			final Character c = cuttingLettersOperatorDoc[i];

			if (i == 0 && name.compareTo(c.toString()) < 0
					|| name.compareTo(previousChar.toString()) >= 0 && name.compareTo(c.toString()) < 0) {
				order = previousChar.toString() + ((Character) Character.toChars(c - 1)[0]).toString();
			}
		}
		if ("".equals(order)) {
			order = cuttingLettersOperatorDoc[cuttingLettersOperatorDoc.length - 1].toString() + lastChar;
		}

		return order;
	}

	public static final String BASIC_SKILL = "msi.gaml.skills.Skill";

	public static final Character[] cuttingLettersOperatorDoc = { 'c', 'i', 'o', 't' };

	public final static String DOC_SEP = "~";

	static String ln = "\n";
	static String tab = "\t";
	static String in = ln;
	final static String OVERRIDE = " @Override ", IAGENT = "IAgent", IPOPULATION = "IPopulation",
			ISIMULATION = "ISimulation", ISKILL = "ISkill", ISUPPORT = "IVarAndActionSupport", ISYMBOL = "ISymbol",
			IDESC = "IDescription", ISCOPE = "IScope", OBJECT = "Object", IVALUE = "IValue",
			IEXPRESSION = "IExpression", INTEGER = "Integer", DOUBLE = "Double", BOOLEAN = "Boolean";
	public final static String[] IMPORTS = new String[] { "msi.gama.outputs.layers", "msi.gama.outputs",
			"msi.gama.kernel.batch", "msi.gama.kernel.root", "msi.gaml.architecture.weighted_tasks",
			"msi.gaml.architecture.user", "msi.gama.outputs.layers.charts", "msi.gaml.architecture.reflex",
			"msi.gaml.architecture.finite_state_machine", "msi.gaml.species", "msi.gama.metamodel.shape",
			"msi.gaml.expressions", "msi.gama.metamodel.topology", "msi.gaml.statements.test",
			"msi.gama.metamodel.population", "msi.gama.kernel.simulation", "java.util", "msi.gaml.statements.draw",
			" msi.gama.metamodel.shape", "msi.gama.common.interfaces", "msi.gama.runtime", "java.lang",
			"msi.gama.metamodel.agent", "msi.gaml.types", "msi.gaml.compilation", "msi.gaml.factories",
			"msi.gaml.descriptions", "msi.gama.util.file", "msi.gama.util.matrix", "msi.gama.util.graph",
			"msi.gama.util.path", "msi.gama.util", "msi.gama.runtime.exceptions", "msi.gaml.factories",
			"msi.gaml.statements", "msi.gaml.skills", "msi.gaml.variables", "msi.gama.kernel.experiment",
			"msi.gaml.operators", "msi.gaml.extensions.genstar", "msi.gama.common.interfaces",
			"msi.gama.extensions.messaging", "msi.gama.metamodel.population" },
			EXPLICIT_IMPORTS = new String[] { "msi.gaml.operators.Random", "msi.gaml.operators.Maths",
					"msi.gaml.operators.Points", "msi.gaml.operators.Spatial.Properties", "msi.gaml.operators.System" };

	final static List<String> ss1 = Arrays.asList("const", "true", "false", "name", "type");
	final static List<String> ss2 = Arrays.asList("CONST", "TRUE", "FALSE", "NAME", "TYPE");
	final static Map<String, String> CLASS_NAMES = new HashMap<String, String>() {
		{
			put("IAgent", "IA");
			put("IGamlAgent", "IG");
			put("GamaColor", "GC");
			put("GamaPair", "GP");
			put("GamaShape", "GS");
			put("Object", "O");
			put("Integer", "I");
			put("Double", "D");
			put("Boolean", "B");
			put("IExpression", "IE");
			put("IShape", "IS");
			put("GamaMap", "GM");
			put("IContainer", "IC");
			put("ILocation", "IL");
			put("IMatrix", "IM");
			put("String", "S");
			put("GamaPoint", "P");
			put("GamaList", "GL");
			put("MovingSkill", "MSK");
			put("WorldSkill", "WSK");
			put("GridSkill", "GSK");
			put("IGamaFile", "GF");
			put("IPath", "IP");
			put("IList", "LI");
			put("ITopology", "IT");
			put("GamlAgent", "GA");
			put("ISpecies", "SP");
			put("IScope", "SC");
			put("GamaDate", "GD");

		}
	};
	final static Map<String, String> RETURN_WHEN_NULL = new HashMap<String, String>() {
		{
			put(DOUBLE, " 0d");
			put(INTEGER, " 0");
			put(BOOLEAN, " false");
		}
	};

	final static Map<String, String> CHECK_PRIM = new HashMap<String, String>() {
		{
			put("int", INTEGER);
			put("short", INTEGER);
			put("long", INTEGER);
			put("double", DOUBLE);
			put("float", DOUBLE);
			put("boolean", BOOLEAN);
		}
	};

	final static String PACKAGE_NAME = "gaml.additions";

	final static Map<Class<? extends Annotation>, IProcessor<?>> processors =
			new LinkedHashMap<Class<? extends Annotation>, IProcessor<?>>() {
				{
					// Order is important
					// Doc built first, so that test generation can happen subsequently
					put(doc.class, new DocProcessor());
					// Then all the processors for specific annotations
					put(type.class, new TypeProcessor());
					put(factory.class, new FactoryProcessor());
					put(species.class, new SpeciesProcessor());
					put(symbol.class, new SymbolProcessor());
					put(vars.class, new VarsProcessor());
					put(operator.class, new OperatorProcessor());
					put(file.class, new FileProcessor());
					put(action.class, new ActionProcessor());
					put(skill.class, new SkillProcessor());
					put(display.class, new DisplayProcessor());
					put(experiment.class, new ExperimentProcessor());
					put(constant.class, new ConstantProcessor());
					// TestProcessor actually processes both @tests and @test annotations
					put(tests.class, new TestProcessor());
				}
			};

}
