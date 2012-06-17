package msi.gaml.compilation;

import static msi.gama.common.interfaces.IKeyword.*;
import static msi.gaml.expressions.IExpressionCompiler.*;
import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.JavaUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.*;
import msi.gama.util.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
import msi.gaml.expressions.BinaryOperator.BinaryVarOperator;
import msi.gaml.factories.*;
import msi.gaml.skills.ISkill;
import msi.gaml.types.*;

/**
 * 
 * The class AbstractGamlAdditions. Default base implementation for plugins' gaml additions.
 * 
 * @author drogoul
 * @since 17 mai 2012
 * 
 */
public abstract class AbstractGamlAdditions implements IGamlAdditions {

	private final static Map<Set<Class>, List<IDescription>> ALL_ADDITIONS = new HashMap();
	private final static Map<String, Class> SKILL_CLASSES = new HashMap();
	private final static GamlProperties SPECIES_SKILLS = new GamlProperties();
	// private final static Map<String, ISkillConstructor> ARCHI_CONSTRUCTORS = new HashMap();
	private final static Map<Class, ISkill> SKILL_INSTANCES = new HashMap();
	private final static Map<Class, List<IDescription>> ADDITIONS = new HashMap();
	private final static Map<Class, List<TypeFieldExpression>> FIELDS = new HashMap();
	static TypePair FUNCTION_SIG;
	public static Map<String, SpeciesDescription> BUILT_IN_SPECIES = new HashMap();
	public static Class WORLD_AGENT_CLASS;
	public static IAgentConstructor WORLD_AGENT_CONSTRUCTOR;
	public static Class DEFAULT_AGENT_CLASS;
	public static IAgentConstructor DEFAULT_AGENT_CONSTRUCTOR;

	public void _species(final String name, final Class clazz, final IAgentConstructor helper,
		final String ... skills) {
		if ( IKeyword.WORLD_SPECIES.equals(name) ) {
			WORLD_AGENT_CLASS = clazz;
			WORLD_AGENT_CONSTRUCTOR = helper;
			return;
		} else if ( IKeyword.AGENT.equals(name) ) {
			DEFAULT_AGENT_CLASS = clazz;
			DEFAULT_AGENT_CONSTRUCTOR = helper;
		}
		Set<String> allSkills = new HashSet(Arrays.asList(skills));
		Set<String> builtInSkills = SPECIES_SKILLS.get(name);
		if ( builtInSkills != null ) {
			allSkills.addAll(builtInSkills);
		}
		BUILT_IN_SPECIES.put(name,
			DescriptionFactory.createSpeciesDescription(name, clazz, null, helper, allSkills));
	}

	protected void _type(final String keyword, final IType typeInstance, final int id,
		final int varKind, final Class ... wraps) {
		Types.initType(keyword, typeInstance, (short) id, varKind, wraps);
	}

	protected void _skill(final String name, final Class clazz, final ISkillConstructor helper,
		final String ... species) {
		// boolean archi = IArchitecture.class.isAssignableFrom(clazz);
		// if ( archi ) {
		// ARCHI_CONSTRUCTORS.put(name, helper);
		// } else {
		SKILL_INSTANCES.put(clazz, helper.newInstance());
		// }
		// SKILL_CONSTRUCTORS.put(clazz, helper);
		for ( String spec : species ) {
			SPECIES_SKILLS.put(spec, name);
		}
		SKILL_CLASSES.put(name, clazz);
	}

	protected void _factories(final ISymbolFactory ... factories) {
		Map<Integer, ISymbolFactory> factoriesByKind = new HashMap();
		for ( ISymbolFactory f : factories ) {
			for ( Integer kind : f.getHandles() ) {
				factoriesByKind.put(kind, f);
			}
		}
		DescriptionFactory.setModelFactory(factoriesByKind.get(ISymbolKind.MODEL));
		for ( ISymbolFactory f : factories ) {
			f.assembleWith(factoriesByKind);
		}
	}

	// combinations and doc missing
	protected void _symbol(final Class c, final int sKind, final boolean remote,
		final boolean args, final boolean scope, final boolean sequence,
		final String[] parentSymbols, final int[] parentKinds, final FacetProto[] fmd,
		final String omissible, final ISymbolConstructor sc, final String ... names) {
		Set<String> contextKeywords = new HashSet();
		Set<Short> contextKinds = new HashSet();
		final Map<String, FacetProto> facets = new HashMap();
		if ( fmd != null ) {
			for ( FacetProto f : fmd ) {
				facets.put(f.name, f);
			}
		}
		if ( parentSymbols != null ) {
			for ( String p : parentSymbols ) {
				contextKeywords.add(p);
			}
		}
		if ( parentKinds != null ) {
			for ( int p : parentKinds ) {
				contextKinds.add((short) p);
			}
		}

		List<String> keywords = new ArrayList(Arrays.asList(names));
		// if the symbol is a variable
		if ( ISymbolKind.Variable.KINDS.contains(sKind) ) {
			Set<String> additonal = Types.keywordsToVariableType.get(sKind);
			if ( additonal != null ) {
				keywords.addAll(additonal);
			}
			// Special trick and workaround for compiling species rather than variables
			keywords.remove(SPECIES);
		}

		SymbolProto md =
			new SymbolProto(sequence, args, sKind, !scope, facets, omissible,
				Collections.EMPTY_LIST, contextKeywords, contextKinds, remote, sc);
		DescriptionFactory.getModelFactory().registerSymbol(md, keywords);

	}

	public void _iterator(final String[] keywords, final Class left, final Class right,
		final Class ret, final int p, final boolean c, final int t, final int content,
		final IOpRun helper) {
		IExpressionCompiler.ITERATORS.addAll(Arrays.asList(keywords));
		_binary(keywords, left, right, ret, p, c, t, content, helper);
	}

	public void _binary(final String[] keywords, final Class left, final Class right,
		final Class ret, final int p, final boolean c, final int t, final int content,
		final IOpRun helper) {
		TypePair signature = new TypePair(Types.get(left), Types.get(right));
		for ( int i = 0; i < keywords.length; i++ ) {
			String kw = keywords[i];
			if ( !BINARIES.containsKey(kw) ) {
				BINARIES.put(kw, new GamaMap());
			}
			IExpressionCompiler.BINARY_PRIORITIES.put(kw, (short) p);
			Map<TypePair, IOperator> map = BINARIES.get(kw);
			if ( !map.containsKey(signature) ) {
				IOperator exp;
				if ( kw.equals(OF) || kw.equals(_DOT) ) {
					exp =
						new BinaryVarOperator(Types.get(ret), helper, c, (short) t,
							(short) content, IExpression.class.equals(right));
				} else {
					exp =
						new BinaryOperator(Types.get(ret), helper, c, (short) t, (short) content,
							IExpression.class.equals(right));
				}
				// simulation will be set after
				exp.setName(kw);
				map.put(signature, exp);
			}
		}
	}

	public void _unary(final String[] keywords, final Class childClass, final Class retClass,
		final boolean canBeConst, final int type, final int contentType, final IOpRun helper) {
		IType childType = Types.get(childClass);
		IType returnType = Types.get(retClass);
		for ( int i = 0; i < keywords.length; i++ ) {
			String kw = keywords[i];
			if ( !(UNARIES.containsKey(kw) && UNARIES.get(kw).containsKey(childType)) ) {
				IOperator result =
					new UnaryOperator(returnType, helper, canBeConst, (short) type,
						(short) contentType);
				result.setName(kw);
				if ( !UNARIES.containsKey(kw) ) {
					UNARIES.put(kw, new HashMap<IType, IOperator>());
				}
				UNARIES.get(kw).put(childType, result);
			}
		}
	}

	private void add(final Class clazz, final IDescription desc) {
		if ( !ADDITIONS.containsKey(clazz) ) {
			ADDITIONS.put(clazz, new ArrayList());
		}
		ADDITIONS.get(clazz).add(desc);
	}

	private void add(final Class clazz, final TypeFieldExpression expr) {
		if ( !FIELDS.containsKey(clazz) ) {
			FIELDS.put(clazz, new ArrayList());
		}
		FIELDS.get(clazz).add(expr);

	}

	protected void _var(final Class clazz, final IDescription desc, final IVarGetter get,
		final IVarGetter init, final IVarSetter set) {
		add(clazz, desc);
		((VariableDescription) desc).addHelpers(get, init, set);
	}

	protected void _field(final Class clazz, final TypeFieldExpression getter) {
		add(clazz, getter);
	}

	public static List<IDescription> getFieldDescriptions(final Class clazz) {
		List<Class> classes = JavaUtils.collectImplementationClasses(clazz, Collections.EMPTY_SET);
		Map<String, IDescription> fieldsMap = new LinkedHashMap();
		for ( Class c : classes ) {
			List<IDescription> descriptions = getAdditions(c);
			if ( descriptions == null ) {
				continue;
			}
			for ( IDescription desc : descriptions ) {
				fieldsMap.put(desc.getName(), desc);
			}
		}
		List<IDescription> descs = new GamaList(fieldsMap.values());
		return descs;
	}

	protected IDescription desc(final String keyword, final IDescription superDesc,
		final IChildrenProvider children, final String ... facets) {
		return DescriptionFactory.create(keyword, superDesc, children, facets);
	}

	protected IDescription desc(final String keyword, final String ... facets) {
		return DescriptionFactory.create(keyword, facets);
	}

	protected void _action(final String methodName, final Class clazz, final PrimRun e,
		final IDescription desc) {
		((StatementDescription) desc).setHelper(e);
		add(clazz, desc);
	}

	public static boolean isBuiltIn(final String name) {
		return BUILT_IN_SPECIES.containsKey(name);
	}

	public static void registerNewFunction(final String string) {
		if ( !BINARIES.containsKey(string) ) {
			BINARIES.put(string, new HashMap());
		}
		if ( FUNCTION_SIG == null ) {
			FUNCTION_SIG = new TypePair(Types.get(IType.AGENT), Types.get(IType.MAP));
		}
		Map<TypePair, IOperator> existing = BINARIES.get(string);
		if ( !existing.containsKey(FUNCTION_SIG) ) {
			IExpressionCompiler.FUNCTIONS.add(string);
			IOperator newFunct = new PrimitiveOperator(string);
			existing.put(FUNCTION_SIG, newFunct);
		}
	}

	public static void registerFunction(final String string, final IType species) {
		registerNewFunction(string);
		IOperator newFunct = BINARIES.get(string).get(FUNCTION_SIG).copy();
		BINARIES.get(string).put(new TypePair(species, Types.get(IType.MAP)), newFunct);
	}

	public static List<IDescription> getAdditions(final Class clazz) {
		return ADDITIONS.get(clazz);
	}

	public static Map<String, TypeFieldExpression> getAllFields(final Class clazz) {
		List<Class> classes = JavaUtils.collectImplementationClasses(clazz, Collections.EMPTY_SET);
		Map<String, TypeFieldExpression> fieldsMap = new LinkedHashMap();
		for ( Class c : classes ) {
			List<TypeFieldExpression> fields = FIELDS.get(c);
			if ( fields == null ) {
				continue;
			}
			for ( TypeFieldExpression desc : fields ) {
				fieldsMap.put(desc.getName(), desc);
			}
		}
		return fieldsMap;
	}

	public static ISkill getSkillInstanceFor(final String skillName) {
		return getSkillInstanceFor(SKILL_CLASSES.get(skillName));
	}

	public static ISkill getSkillInstanceFor(final Class skillClass) {
		ISkill skill = SKILL_INSTANCES.get(skillClass);
		return skill == null ? null : skill.duplicate();
		// if ( skillClass == null ) { return null; }
		// if ( skillClass.isInterface() || Modifier.isAbstract(skillClass.getModifiers()) ) {
		// return null; }
		// ISkill skill = SKILL_INSTANCES.get(skillClass);
		// if ( skill == null ) {
		// skill = getSkillConstructor(skillClass).newInstance();
		// SKILL_INSTANCES.put(skillClass, skill);
		// }
		// return skill;
	}

	// public static IArchitecture getArchitectureInstanceFor(final String keyword) {
	// ISkillConstructor helper = ARCHI_CONSTRUCTORS.get(keyword);
	// if ( helper == null ) { return new ReflexArchitecture(); }
	// return (IArchitecture) helper.newInstance();
	// }

	static final Class[] EXECUTE_ARGS = new Class[] { IAgent.class, ISkill.class };

	public static List<IDescription> getAllChildrenOf(final Class base, final Set<Class> skills) {
		Set<Class> key = new HashSet();
		key.add(base);
		key.addAll(skills);
		List<IDescription> children = ALL_ADDITIONS.get(key);
		if ( children == null ) {
			children = new ArrayList();
			final List<Class> classes = JavaUtils.collectImplementationClasses(base, skills);
			for ( final Class c1 : classes ) {
				// GuiUtils
				// .debug("Adding implementation class " + c1.getSimpleName() + " to " + getName());
				List<IDescription> toAdd = getAdditions(c1);
				if ( toAdd != null && !toAdd.isEmpty() ) {
					children.addAll(toAdd);
				}
			}
			ALL_ADDITIONS.put(key, children);
		}
		return children;
	}

	public static Map<String, Class> getSkillClasses() {
		return SKILL_CLASSES;
	}

	public static Set<String> getSpeciesSkills(final String speciesName) {
		Set<String> skills = SPECIES_SKILLS.get(speciesName);
		if ( skills == null ) { return Collections.EMPTY_SET; }
		return skills;
	}

}
