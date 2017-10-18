package msi.gama.precompiler.java;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import msi.gama.precompiler.ActionProcessor;
import msi.gama.precompiler.DisplayProcessor;
import msi.gama.precompiler.ExperimentProcessor;
import msi.gama.precompiler.FactoryProcessor;
import msi.gama.precompiler.FileProcessor;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.display;
import msi.gama.precompiler.GamlAnnotations.experiment;
import msi.gama.precompiler.GamlAnnotations.factory;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.test;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IProcessor;
import msi.gama.precompiler.OperatorProcessor;
import msi.gama.precompiler.SkillProcessor;
import msi.gama.precompiler.SpeciesProcessor;
import msi.gama.precompiler.SymbolProcessor;
import msi.gama.precompiler.TestProcessor;
import msi.gama.precompiler.TypeProcessor;
import msi.gama.precompiler.VarsProcessor;

public interface Constants {
	public final static String DOC_SEP = "~";
	// public final static String ACTION_PREFIX = "!", OPERATOR_PREFIX = "?", GETTER_PREFIX = "/", SETTER_PREFIX = "-",
	// FIELD_PREFIX = "*", SPECIES_PREFIX = "&", DISPLAY_PREFIX = "\\", EXPERIMENT_PREFIX = "§",
	// SKILL_PREFIX = ")", TYPE_PREFIX = "(", SYMBOL_PREFIX = "[", FACTORY_PREFIX = "]", VAR_PREFIX = "%",
	// FILE_PREFIX = "+", DOC_PREFIX = "@", CONSTANT_PREFIX = "£", POPULATIONS_LINKER_PREFIX = "�", DOC_SEP = "~",
	// DOC_REGEX = "\\~", SEP = "$";
	static String ln = "\n";
	static String tab = "\t";
	static String in = ln;
	final static String OVERRIDE = " @Override ", IAGENT = "IAgent", IPOPULATION = "IPopulation",
			ISIMULATION = "ISimulation", ISKILL = "ISkill", ISUPPORT = "IVarAndActionSupport", ISYMBOL = "ISymbol",
			IDESC = "IDescription", ISCOPE = "IScope", OBJECT = "Object", IVALUE = "IValue",
			IEXPRESSION = "IExpression", INTEGER = "Integer", DOUBLE = "Double", BOOLEAN = "Boolean";
	public final static String[] IMPORTS =
			new String[] { "msi.gama.outputs.layers", "msi.gama.outputs", "msi.gama.kernel.batch",
					"msi.gama.kernel.root", "msi.gaml.architecture.weighted_tasks", "msi.gaml.architecture.user",
					"msi.gama.outputs.layers.charts", "msi.gaml.architecture.reflex",
					"msi.gaml.architecture.finite_state_machine", "msi.gaml.species", "msi.gama.metamodel.shape",
					"msi.gaml.expressions", "msi.gama.metamodel.topology", "msi.gaml.statements.test",
					"msi.gama.metamodel.population", "msi.gama.kernel.simulation", "java.util",
					"msi.gaml.statements.draw", " msi.gama.metamodel.shape", "msi.gama.common.interfaces",
					"msi.gama.runtime", "java.lang", "msi.gama.metamodel.agent", "msi.gaml.types",
					"msi.gaml.compilation", "msi.gaml.factories", "msi.gaml.descriptions", "msi.gama.util.file",
					"msi.gama.util.matrix", "msi.gama.util.graph", "msi.gama.util.path", "msi.gama.util",
					"msi.gama.runtime.exceptions", "msi.gaml.factories", "msi.gaml.statements", "msi.gaml.skills",
					"msi.gaml.variables", "msi.gama.kernel.experiment", "msi.gaml.operators",
					"msi.gaml.extensions.genstar", "msi.gama.common.interfaces", "msi.gama.extensions.messaging",
					"msi.gama.metamodel.population" },
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

					// put(constant.class, new ConstantProcessor());
					put(test.class, new TestProcessor());
					// put(getter.class, IProcessor.NULL);
					// put(setter.class, IProcessor.NULL);
					// put(doc.class, new DocProcessor());
				}
			};

}
