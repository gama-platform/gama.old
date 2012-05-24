package msi.gaml.compilation;

import static msi.gama.common.interfaces.IKeyword.*;
import static msi.gaml.expressions.IExpressionCompiler.*;
import java.lang.reflect.Method;
import java.util.*;
import msi.gama.common.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.base;
import msi.gama.precompiler.GamlAnnotations.combination;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.no_scope;
import msi.gama.precompiler.GamlAnnotations.remote_context;
import msi.gama.precompiler.GamlAnnotations.with_args;
import msi.gama.precompiler.GamlAnnotations.with_sequence;
import msi.gama.precompiler.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.architecture.reflex.ReflexArchitecture;
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

	public static class HelperProvider<T> {

		Map<Class, Map<String, T>> cache = new HashMap();

		void put(final Class clazz, final String key, final T helper) {
			if ( helper == null ) {
				GuiUtils.debug("HelperProvider.put null");
			}
			Map<String, T> map = cache.get(clazz);
			if ( map == null ) {
				map = new HashMap();
				cache.put(clazz, map);
			}
			map.put(key, helper);
		}

		boolean has(final Class clazz, final String key) {
			Map<String, T> map = cache.get(clazz);
			return map != null && map.containsKey(key);
		}

		T get(final Class clazz, final String key) {
			Map<String, T> map = cache.get(clazz);
			return map == null ? null : map.get(key);
		}

		T getCompatible(final Class clazz, final String key) {
			T helper = get(clazz, key);
			if ( helper == null ) {
				// We look for an alternative in the assignable classes
				for ( Map.Entry<Class, Map<String, T>> entry : cache.entrySet() ) {
					Class altern = entry.getKey();
					if ( altern.isAssignableFrom(clazz) && has(altern, key) ) {
						helper = get(altern, key);
						// We cache the newly found helper in the new class
						put(clazz, key, helper);
						break;
					}
				}
			}
			return helper;
		}

	}

	private final static Map<String, Class> SKILL_CLASSES = new HashMap();
	private final static Map<String, Class> SPECIES_CLASSES = new HashMap();
	private final static GamlProperties SPECIES_SKILLS = new GamlProperties();
	private final static HelperProvider<PrimitiveExecuter> ACTION_HELPERS = new HelperProvider();
	private final static HelperProvider<IVarGetter> GETTER_HELPERS = new HelperProvider();
	private final static HelperProvider<IFieldGetter> FIELD_HELPERS = new HelperProvider();
	private final static HelperProvider<IVarSetter> SETTER_HELPERS = new HelperProvider();
	private final static Map<Class, IAgentConstructor> AGENT_CONSTRUCTORS = new HashMap();
	private final static Map<Class, ISymbolConstructor> SYMBOL_CONSTRUCTORS = new HashMap();
	private final static Map<Class, ISkillConstructor> SKILL_CONSTRUCTORS = new HashMap();
	private final static Map<Class, ISkill> SKILL_INSTANCES = new HashMap();
	private final static Map<Class, List<String>> SKILL_METHODS = new HashMap();
	private final static Map<Class, List<IDescription>> VAR_DESCRIPTIONS = new HashMap();
	private final static Map<Class, List<IDescription>> ACTION_DESCRIPTIONS = new HashMap();
	static final TypePair FUNCTION_SIG = new TypePair(Types.get(IType.AGENT), Types.get(IType.MAP));

	@Override
	protected void finalize() {

	}

	public void speciesAdd(final String name, final Class clazz, final String ... skills) {
		for ( String s : skills ) {
			SPECIES_SKILLS.put(name, s);
		}
		SPECIES_CLASSES.put(name, clazz);
	}

	protected void addType(final String keyword, final IType typeInstance, final short id,
		final int varKind, final Class ... wraps) {
		Types.initType(keyword, typeInstance, id, varKind, wraps);
	}

	protected void skillsAdd(final String name, final Class clazz, final String ... species) {
		for ( String spec : species ) {
			SPECIES_SKILLS.put(spec, name);
		}
		SKILL_CLASSES.put(name, clazz);
	}

	protected void addFactories(final ISymbolFactory ... factories) {
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

	protected void addSymbol(final Class c, final int sKind, final ISymbolConstructor sc,
		final String ... names) {
		Class base = null;
		String omissible = null;
		boolean canHaveArgs = c.getAnnotation(with_args.class) != null;
		boolean canHaveSequence = c.getAnnotation(with_sequence.class) != null;
		boolean doesNotHaveScope = c.getAnnotation(no_scope.class) != null;
		boolean isRemoteContext = c.getAnnotation(remote_context.class) != null;
		base b = (base) c.getAnnotation(base.class);
		if ( b != null ) {
			base = b.value();
		}
		Set<String> contextKeywords = new HashSet();
		Set<Short> contextKinds = new HashSet();
		facets ff = (facets) c.getAnnotation(facets.class);
		List<facet> facets = ff != null ? Arrays.asList(ff.value()) : Collections.EMPTY_LIST;
		List<combination> combinations =
			ff != null ? Arrays.asList(ff.combinations()) : Collections.EMPTY_LIST;
		omissible = ff != null ? ff.omissible() : null;
		inside parents = (inside) c.getAnnotation(inside.class);
		if ( parents != null ) {
			for ( String p : parents.symbols() ) {
				contextKeywords.add(p);
			}
			for ( int p : parents.kinds() ) {
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

		SymbolMetaDescription md =
			new SymbolMetaDescription(base, canHaveSequence, canHaveArgs, sKind, doesNotHaveScope,
				facets, omissible, combinations, contextKeywords, contextKinds, isRemoteContext, sc);
		DescriptionFactory.getModelFactory().registerSymbol(md, keywords);

	}

	protected void addAgentConstructor(final Class clazz, final IAgentConstructor ac) {
		AGENT_CONSTRUCTORS.put(clazz, ac);
	}

	protected void addSkillConstructor(final Class clazz, final ISkillConstructor sc) {
		SKILL_CONSTRUCTORS.put(clazz, sc);
	}

	protected void addGetterExecuter(final String name, final Class clazz, final IVarGetter e) {
		GETTER_HELPERS.put(clazz, name, e);
	}

	protected void addSetterExecuter(final String name, final Class clazz, final IVarSetter e) {
		SETTER_HELPERS.put(clazz, name, e);
	}

	protected void addFieldGetterExecuter(final String name, final Class clazz, final IFieldGetter e) {
		FIELD_HELPERS.put(clazz, name, e);
	}

	protected void addActionExecuter(final String name, final Class clazz, final PrimitiveExecuter e) {
		ACTION_HELPERS.put(clazz, name, e);
	}

	public static void registerNewFunction(final String string) {
		if ( !BINARIES.containsKey(string) ) {
			BINARIES.put(string, new HashMap());
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

	public static void addBinary(final String kw, final Class left, final Class right,
		final Class ret, final boolean iterator, final short p, final boolean c, final short t,
		final short content, final IOperatorExecuter helper) {
		if ( !BINARIES.containsKey(kw) ) {
			BINARIES.put(kw, new GamaMap());
		}
		if ( iterator ) {
			IExpressionCompiler.ITERATORS.add(kw);
		}

		IExpressionCompiler.BINARY_PRIORITIES.put(kw, p);
		Map<TypePair, IOperator> map = BINARIES.get(kw);
		TypePair signature = new TypePair(Types.get(left), Types.get(right));
		if ( !map.containsKey(signature) ) {
			IOperator exp;
			if ( kw.equals(OF) || kw.equals(_DOT) ) {
				exp =
					new BinaryVarOperator(Types.get(ret), helper, c, t, content,
						IExpression.class.equals(right));
			} else {
				exp =
					new BinaryOperator(Types.get(ret), helper, c, t, content,
						IExpression.class.equals(right));
			}
			// simulation will be set after
			exp.setName(kw);
			map.put(signature, exp);
		}

	}

	public static void addUnary(final String keyword, final Class childClass, final Class retClass,
		final boolean canBeConst, final short type, final short contentType,
		final IOperatorExecuter helper) {
		IType childType = Types.get(childClass);
		IType returnType = Types.get(retClass);
		if ( !(UNARIES.containsKey(keyword) && UNARIES.get(keyword).containsKey(childType)) ) {
			IOperator result = new UnaryOperator(returnType, helper, canBeConst, type, contentType);
			result.setName(keyword);
			if ( !UNARIES.containsKey(keyword) ) {
				UNARIES.put(keyword, new HashMap<IType, IOperator>());
			}
			UNARIES.get(keyword).put(childType, result);
		}
	}

	private static void addSkillMethod(final Class c, final String name) {
		List<String> names = SKILL_METHODS.get(c);
		if ( names == null ) {
			names = new ArrayList();
			SKILL_METHODS.put(c, names);
		}
		names.add(name);
	}

	public static List<String> getSkillMethods(final Class clazz) {
		return SKILL_METHODS.get(clazz);
	}

	public static List<IDescription> getVarDescriptions(final Class clazz) {
		return VAR_DESCRIPTIONS.get(clazz);
	}

	protected static void addVarDescription(final Class clazz, final IDescription desc) {
		if ( !VAR_DESCRIPTIONS.containsKey(clazz) ) {
			VAR_DESCRIPTIONS.put(clazz, new ArrayList());
		}
		VAR_DESCRIPTIONS.get(clazz).add(desc);
		String m = desc.getFacets().getLabel(GETTER);
		if ( m != null ) {
			addSkillMethod(clazz, m);
		}
		m = desc.getFacets().getLabel(SETTER);
		if ( m != null ) {
			addSkillMethod(clazz, m);
		}
	}

	public static List<IDescription> getFieldDescriptions(final Class clazz) {
		List<Class> classes = JavaUtils.collectImplementationClasses(clazz, Collections.EMPTY_SET);
		Map<String, IDescription> fieldsMap = new LinkedHashMap();
		for ( Class c : classes ) {
			List<IDescription> descriptions = getVarDescriptions(c);
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

	public static List<IDescription> getActionsDescriptions(final Class clazz) {
		if ( !ACTION_DESCRIPTIONS.containsKey(clazz) ) {
			collectBuiltInActions(clazz);
		}
		return ACTION_DESCRIPTIONS.get(clazz);
	}

	static void collectBuiltInActions(final Class c) {
		final HashMap<String, String> names = new HashMap();
		final HashMap<String, IType> types = new HashMap();
		final HashMap<String, List<String>> arguments = new HashMap();
		List<IDescription> commands = new ArrayList();
		ACTION_DESCRIPTIONS.put(c, commands);
		for ( final Method m : c.getDeclaredMethods() ) {
			action vp = m.getAnnotation(action.class);
			if ( vp != null ) {
				String name = vp.value();
				names.put(name, m.getName());
				types.put(name, Types.get(m.getReturnType()));
				args va = m.getAnnotation(args.class);
				arguments
					.put(name, va == null ? Collections.EMPTY_LIST : Arrays.asList(va.value()));
			}
		}

		for ( final Map.Entry<String, String> entry : names.entrySet() ) {
			String n = entry.getKey();
			addSkillMethod(c, entry.getValue());
			List<String> argNames = arguments.get(n);
			List<IDescription> args = Collections.EMPTY_LIST;
			if ( argNames != null ) {
				args = new ArrayList();
				for ( String s : argNames ) {
					IDescription arg = DescriptionFactory.create(ARG, (IDescription) null, NAME, s);
					if ( arg != null ) {
						args.add(arg);
					}
				}
			}

			IDescription prim =
				DescriptionFactory.create(PRIMITIVE, null, args, NAME, n, TYPE, types.get(n)
					.toString(), JAVA, names.get(n));
			if ( prim != null ) {
				commands.add(prim);
			}
		}
	}

	public static ISkill getSkillInstanceFor(final Class skillClass) {
		ISkill skill = SKILL_INSTANCES.get(skillClass);
		if ( skill == null ) {
			skill = getSkillConstructor(skillClass).newInstance();
			SKILL_INSTANCES.put(skillClass, skill);
		}
		return skill;
	}

	public static IArchitecture getArchitectureInstanceFor(final String keyword) {
		Class clazz = getSkillClasses().get(keyword);
		if ( clazz == null ) { return new ReflexArchitecture(); }
		return (IArchitecture) getSkillConstructor(clazz).newInstance();
	}

	static final Class[] EXECUTE_ARGS = new Class[] { IAgent.class, ISkill.class };

	public static IPrimitiveExecuter getPrimitive(final Class originalClass, final String methodName) {
		PrimitiveExecuter helper = ACTION_HELPERS.getCompatible(originalClass, methodName);
		if ( helper == null ) { return null; }
		IType type = helper.getReturnType();
		if ( type == null ) {
			try {
				Class returnClass =
					helper.getClass().getMethod("execute", EXECUTE_ARGS).getReturnType();
				IType returnType = Types.get(returnClass);
				helper.setReturnType(returnType);
			} catch (Exception e) {
				helper.setReturnType(Types.NO_TYPE);
			}
		}
		return helper;
	}

	public static IVarGetter getGetter(final Class original, final String methodName) {
		IVarGetter helper = GETTER_HELPERS.getCompatible(original, methodName);
		if ( helper == null ) {
			final IFieldGetter fg = getFieldGetter(original, methodName);
			if ( fg == null ) {
				GuiUtils.debug("getGetter");
			}
			helper = new IVarGetter() {

				@Override
				public Object execute(final IAgent agent, final ISkill skill)
					throws GamaRuntimeException {
					return fg.value(agent); // ?
				}
			};
			GETTER_HELPERS.put(original, methodName, helper);
		}
		return helper;
	}

	public static IFieldGetter getFieldGetter(final Class original, final String methodName) {
		return FIELD_HELPERS.getCompatible(original, methodName);
	}

	public static IVarSetter getSetter(final Class original, final String methodName) {
		return SETTER_HELPERS.getCompatible(original, methodName);
	}

	public static Map<String, Class> getBuiltInSpeciesClasses() {
		return SPECIES_CLASSES;
	}

	public static Map<String, Class> getSkillClasses() {
		return SKILL_CLASSES;
	}

	public static IAgentConstructor getAgentConstructor(final Class javaBase) {
		IAgentConstructor constructor = AGENT_CONSTRUCTORS.get(javaBase);
		return constructor;
	}

	public static ISkillConstructor getSkillConstructor(final Class skillClass) {
		ISkillConstructor constructor = SKILL_CONSTRUCTORS.get(skillClass);
		return constructor;
	}

	public static ISymbolConstructor getSymbolConstructor(final Class instantiationClass) {
		ISymbolConstructor constructor = SYMBOL_CONSTRUCTORS.get(instantiationClass);
		return constructor;
	}

	public static GamlProperties getSpeciesSkills() {
		return SPECIES_SKILLS;
	}

}
