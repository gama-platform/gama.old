package msi.gaml.compilation;

import static msi.gama.common.interfaces.IKeyword.*;
import static msi.gaml.expressions.IExpressionCompiler.*;
import java.lang.reflect.*;
import java.util.*;
import msi.gama.common.util.JavaUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.base;
import msi.gama.precompiler.GamlAnnotations.combination;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.handles;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.no_scope;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.remote_context;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.GamlAnnotations.with_args;
import msi.gama.precompiler.GamlAnnotations.with_sequence;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.architecture.reflex.ReflexArchitecture;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;
import msi.gaml.factories.DescriptionFactory;
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

	private final static Map<Integer, Class> FACTORIES_BY_KIND = new HashMap();
	private final static Map<Integer, List<Class>> SYMBOLS_BY_KIND = new HashMap();
	private final static Map<String, Class> SKILL_CLASSES = new HashMap();
	private final static Map<String, Class> SPECIES_CLASSES = new HashMap();
	private final static GamlProperties SPECIES_SKILLS = new GamlProperties();
	private final static Map<Class, Map<String, SymbolMetaDescription>> SYMBOL_META = new HashMap();
	private final static Map<Class, List<String>> SYMBOL_NAMES = new HashMap();
	private final static Map<Class, Map<String, PrimitiveExecuter>> ACTION_HELPERS = new HashMap();
	private final static Map<String, Map<Class, IOperatorExecuter>> UNARY_HELPERS = new HashMap();
	private final static Map<String, Map<ClassPair, IOperatorExecuter>> BINARY_HELPERS =
		new HashMap();
	private final static Map<Class, Map<String, IVarGetter>> GETTER_HELPERS = new HashMap();
	private final static Map<Class, Map<String, IFieldGetter>> FIELD_HELPERS = new HashMap();
	private final static Map<Class, Map<String, IVarSetter>> SETTER_HELPERS = new HashMap();
	private final static Map<Class, IAgentConstructor> AGENT_CONSTRUCTORS = new HashMap();
	private final static Map<Class, ISymbolConstructor> SYMBOL_CONSTRUCTORS = new HashMap();
	private final static Map<Class, ISkillConstructor> SKILL_CONSTRUCTORS = new HashMap();
	private final static Map<Class, ISkill> SKILL_INSTANCES = new HashMap();
	private final static Map<Class, List<String>> SKILL_METHODS = new HashMap();
	private final static Map<Class, List<IDescription>> VAR_DESCRIPTIONS = new HashMap();
	private final static Map<Class, List<IDescription>> ACTION_DESCRIPTIONS = new HashMap();
	private static IExpressionFactory EXPRESSION_FACTORY;
	static final TypePair FUNCTION_SIG = new TypePair(Types.get(IType.AGENT), Types.get(IType.MAP));
	private final static Map<Set<Class>, List<Class>> IMPLEMENTATION_CLASSES = new HashMap();

	static class ClassPair {

		Class left;
		Class right;

		ClassPair(final Class l, final Class r) {
			left = l;
			right = r;
		}

		@Override
		public boolean equals(final Object o) {
			if ( o == null ) { return false; }
			if ( !(o instanceof ClassPair) ) { return false; }
			return left.equals(((ClassPair) o).left) && right.equals(((ClassPair) o).right);
		}

		@Override
		public int hashCode() {
			return left.hashCode() + right.hashCode() * 2;
		}
	}

	@Override
	protected void finalize() {

	}

	public void speciesAdd(final String name, final Class clazz, final String ... skills) {
		for ( String s : skills ) {
			SPECIES_SKILLS.put(name, s);
		}
		SPECIES_CLASSES.put(name, clazz);
	}

	protected void typesAdd(final Class ... classes) {
		for ( Class clazz : classes ) {
			Types.initType(clazz);
		}
	}

	protected void skillsAdd(final String name, final Class clazz, final String ... species) {
		for ( String spec : species ) {
			SPECIES_SKILLS.put(spec, name);
		}
		SKILL_CLASSES.put(name, clazz);
	}

	protected void symbolsAdd(final Class ... classes) {
		for ( Class clazz : classes ) {
			symbol sym = (symbol) clazz.getAnnotation(symbol.class);
			Integer i = sym.kind();
			if ( !SYMBOLS_BY_KIND.containsKey(i) ) {
				SYMBOLS_BY_KIND.put(i, new ArrayList());
			}
			SYMBOLS_BY_KIND.get(i).add(clazz);
			SYMBOL_NAMES.put(clazz, Arrays.asList(sym.name()));
		}
		for ( Map.Entry<Integer, List<Class>> entry : SYMBOLS_BY_KIND.entrySet() ) {
			Integer i = entry.getKey();
			for ( Class clazz : entry.getValue() ) {
				createMetaDescription(i, clazz);
			}
		}
	}

	protected void factoriesAdd(final Class ... classes) {
		for ( Class clazz : classes ) {
			handles han = (handles) clazz.getAnnotation(handles.class);
			for ( Integer i : han.value() ) {
				FACTORIES_BY_KIND.put(i, clazz);
			}
		}
	}

	protected void operatorsAdd(final Class ... classes) {
		for ( Class clazz : classes ) {
			scanOperators(clazz);
		}
	}

	private void createMetaDescription(final Integer sKind, final Class c) {
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
		Set<String> keywords = new HashSet(SYMBOL_NAMES.get(c));
		Set<String> contexts = new HashSet();
		facets ff = (facets) c.getAnnotation(facets.class);
		List<facet> facets = ff != null ? Arrays.asList(ff.value()) : Collections.EMPTY_LIST;
		List<combination> combinations =
			ff != null ? Arrays.asList(ff.combinations()) : Collections.EMPTY_LIST;
		omissible = ff != null ? ff.omissible() : null;
		inside parents = (inside) c.getAnnotation(inside.class);
		if ( parents != null ) {
			for ( String p : parents.symbols() ) {
				contexts.add(p);
			}
			for ( int kind : parents.kinds() ) {
				List<Class> classes = SYMBOLS_BY_KIND.get(kind);
				for ( Class clazz : classes ) {
					contexts.addAll(SYMBOL_NAMES.get(clazz));
				}
			}
		}
		contexts = new HashSet(contexts);
		// if the symbol is a variable
		if ( ISymbolKind.Variable.KINDS.contains(sKind) ) {
			Set<String> additonal = Types.keywordsToVariableType.get(sKind);
			if ( additonal != null ) {
				keywords.addAll(additonal);
			}
			// Special trick and workaround for compiling species rather than variables
			keywords.remove(SPECIES);
		}
		Map<String, SymbolMetaDescription> map = new HashMap();
		SYMBOL_META.put(c, map);
		for ( String k : keywords ) {
			if ( !ISymbolKind.Variable.KINDS.contains(sKind) ) {
				SymbolMetaDescription.nonVariableStatements.add(k);
			}
			map.put(k, new SymbolMetaDescription(c, base, k, canHaveSequence, canHaveArgs, sKind,
				doesNotHaveScope, facets, omissible, combinations, contexts, isRemoteContext));
		}

	}

	protected void addSymbolConstructor(final Class clazz, final ISymbolConstructor sc) {
		SYMBOL_CONSTRUCTORS.put(clazz, sc);
	}

	protected void addAgentConstructor(final Class clazz, final IAgentConstructor ac) {
		AGENT_CONSTRUCTORS.put(clazz, ac);
	}

	protected void addSkillConstructor(final Class clazz, final ISkillConstructor sc) {
		SKILL_CONSTRUCTORS.put(clazz, sc);
	}

	protected void addOperatorExecuter(final String name, final Class left, final Class right,
		final IOperatorExecuter e) {
		boolean isUnary = right == null;
		if ( isUnary ) {
			if ( !UNARY_HELPERS.containsKey(name) ) {
				UNARY_HELPERS.put(name, new HashMap());
			}
			UNARY_HELPERS.get(name).put(unaryKey(left), e);
		} else {
			if ( !BINARY_HELPERS.containsKey(name) ) {
				BINARY_HELPERS.put(name, new HashMap());
			}
			BINARY_HELPERS.get(name).put(binaryKey(left, right), e);
		}
	}

	protected void addGetterExecuter(final String name, final Class clazz, final IVarGetter e) {
		addHelper(GETTER_HELPERS, name, clazz, e);
	}

	protected void addSetterExecuter(final String name, final Class clazz, final IVarSetter e) {
		addHelper(SETTER_HELPERS, name, clazz, e);
	}

	protected void addFieldGetterExecuter(final String name, final Class clazz, final IFieldGetter e) {
		addHelper(FIELD_HELPERS, name, clazz, e);
	}

	protected void addActionExecuter(final String name, final Class clazz, final PrimitiveExecuter e) {
		addHelper(ACTION_HELPERS, name, clazz, e);
	}

	final void addHelper(final Map map, final String name, final Class clazz, final Object helper) {
		Map<String, Object> intern = (Map<String, Object>) map.get(clazz);
		if ( intern == null ) {
			intern = new HashMap<String, Object>();
			map.put(clazz, intern);
		}
		intern.put(name, helper);
	}

	protected void scanOperators(final Class c) {
		for ( final Method m : c.getDeclaredMethods() ) {
			operator op = m.getAnnotation(operator.class);
			if ( op == null ) {
				continue;
			}
			boolean isStatic = Modifier.isStatic(m.getModifiers());
			Class[] args = m.getParameterTypes();
			String m1 = m.getName();
			Class retClass = m.getReturnType();
			boolean contextual = args.length > 0 && args[0] == IScope.class;
			boolean canBeConst = op.can_be_const();
			short type = op.type();
			short contentType = op.content_type();
			boolean isUnary = isUnary(args, isStatic);
			for ( String keyword : op.value() ) {
				if ( isUnary ) {
					registerUnaryOperator(keyword, m1, c, retClass, args, contextual, isStatic,
						canBeConst, type, contentType);
				} else {
					int l = args.length;
					boolean lazy = l > 1 && IExpression.class.isAssignableFrom(args[l - 1]);
					boolean iterator = op.iterator();
					registerBinaryOperator(keyword, m1, c, retClass, args, contextual, lazy,
						iterator, isStatic, op.priority(), canBeConst, type, contentType);
				}
			}
		}
	}

	protected boolean isUnary(final Class[] args, final boolean isStatic) {
		if ( args.length == 0 && !isStatic ) { return true; }
		if ( args.length == 1 && !isStatic && args[0] == IScope.class ) { return true; }
		if ( args.length == 1 && isStatic ) { return true; }
		if ( args.length == 2 && isStatic && args[0] == IScope.class ) { return true; }
		return false;
	}

	public static List<Class> collectImplementationClasses(final Class baseClass,
		final Set<Class> skillClasses) {
		Set<Class> classes = new HashSet();
		classes.add(baseClass);
		classes.addAll(skillClasses);
		Set<Class> key = new HashSet(classes);
		if ( IMPLEMENTATION_CLASSES.containsKey(key) ) { return IMPLEMENTATION_CLASSES.get(key); }
		classes.addAll(JavaUtils.allInterfacesOf(baseClass));
		for ( final Class classi : new ArrayList<Class>(classes) ) {
			classes.addAll(JavaUtils.allSuperclassesOf(classi));
		}
		classes.remove(ISkill.class);
		classes.remove(IScope.class);
		final ArrayList<Class> classes2 = new ArrayList();
		for ( final Class c : classes ) {
			if ( !classes2.contains(c.getSuperclass()) ) {
				classes2.add(0, c);
			} else {
				classes2.add(c);
			}
		}
		AbstractGamlAdditions.IMPLEMENTATION_CLASSES.put(key, classes2);
		return classes2;
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

	public static void registerBinaryOperator(final String keyword, final String mName,
		final Class declClass, final Class retClass, final Class[] args, final boolean contextual,
		final boolean lazy, final boolean iterator, final boolean isStatic, final short priority,
		final boolean canBeConst, final short type, final short contentType) {
		IOperatorExecuter helper;
		Class leftClass;
		Class rightClass;
		String methodName = mName;

		if ( isStatic ) {
			leftClass = contextual ? args[1] : args[0];
			rightClass = contextual ? args[2] : args[1];
			methodName = declClass.getCanonicalName() + _DOT + methodName;
		} else {
			leftClass = declClass;
			rightClass = contextual ? args[1] : args[0];
		}
		helper = getBinaryOperatorExecuter(leftClass, rightClass, methodName);
		if ( helper == null ) { return; }

		IType leftType = Types.get(leftClass);
		IType rightType = Types.get(rightClass);
		IType returnType = Types.get(retClass);
		if ( !BINARIES.containsKey(keyword) ) {
			BINARIES.put(keyword, new GamaMap());
		}
		Map<TypePair, IOperator> map = BINARIES.get(keyword);
		TypePair signature = new TypePair(leftType, rightType);
		if ( !map.containsKey(signature) ) {
			IOperator exp =
				getExpressionFactory().createOperator(keyword, true,
					keyword.equals(OF) || keyword.equals(_DOT), returnType, helper, canBeConst,
					type, contentType, lazy);
			// simulation will be set after
			exp.setName(keyword);
			map.put(signature, exp);
		}

		if ( iterator ) {
			IExpressionCompiler.ITERATORS.add(keyword);
		}
		IExpressionCompiler.BINARY_PRIORITIES.put(keyword, priority);
	}

	public static void registerUnaryOperator(final String keyword, final String mName,
		final Class declClass, final Class retClass, final Class[] args, final boolean contextual,
		final boolean isStatic, final boolean canBeConst, final short type, final short contentType) {
		IOperator result;
		IOperatorExecuter helper;
		Class childClass;
		String methodName = mName;

		if ( isStatic ) {
			childClass = contextual ? args[1] : args[0];
			methodName = declClass.getCanonicalName() + _DOT + methodName;
		} else {
			childClass = declClass;
		}
		helper = getUnaryOperatorExecuter(childClass, methodName);
		if ( helper == null ) { return; }
		IType childType = Types.get(childClass);
		IType returnType = Types.get(retClass);
		if ( !(UNARIES.containsKey(keyword) && UNARIES.get(keyword).containsKey(childType)) ) {
			result =
				getExpressionFactory().createOperator(keyword, false, false, returnType, helper,
					canBeConst, type, contentType, false);
			// simulation will be set after
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
		if ( !SKILL_METHODS.containsKey(clazz) ) {
			SKILL_METHODS.put(clazz, new ArrayList());
			collectBuiltInAttributes(clazz);
		}
		return SKILL_METHODS.get(clazz);
	}

	public static List<IDescription> getVarDescriptions(final Class clazz) {
		if ( !VAR_DESCRIPTIONS.containsKey(clazz) ) {
			collectBuiltInAttributes(clazz);
		}
		return VAR_DESCRIPTIONS.get(clazz);
	}

	public static List<IDescription> getFieldDescriptions(final Class clazz) {
		List<Class> classes = collectImplementationClasses(clazz, Collections.EMPTY_SET);
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

	static void collectBuiltInAttributes(final Class c) {
		final Map<String, IDescription> varList = new HashMap();
		vars va = null;
		getter vp = null;
		setter vs = null;
		va = (vars) c.getAnnotation(vars.class);
		if ( va == null ) { return; /* no getter and setters */}
		for ( final var s : va.value() ) {
			List<String> ff =
				new GamaList(new String[] { TYPE, s.type(), NAME, s.name(), CONST,
					s.constant() ? TRUE : FALSE });
			String depends = "";
			String[] dependencies = s.depends_on();
			if ( dependencies.length > 0 ) {
				for ( String string : dependencies ) {
					depends += string + " ";
				}
				depends = depends.trim();
				ff.add(DEPENDS_ON);
				ff.add(depends);
			}
			String of = s.of();
			if ( !"".equals(of) ) {
				ff.add(OF);
				ff.add(of);
			}
			String init = s.init();
			if ( !"".equals(init) ) {
				ff.add(INIT);
				ff.add(init);
			}
			String[] facetArray = new String[ff.size()];
			facetArray = ff.toArray(facetArray);
			IDescription vd = DescriptionFactory.create(s.type(), (IDescription) null, facetArray);
			if ( vd != null ) {
				varList.put(s.name(), vd);
			}
		}
		for ( final Method m : c.getDeclaredMethods() ) {
			String varName = null;
			vp = m.getAnnotation(getter.class);
			if ( vp != null ) {
				varName = vp.var();
				final IDescription var = varList.get(varName);
				if ( var != null ) {
					var.getFacets().putAsLabel(GETTER, m.getName());
					if ( vp.initializer() ) {
						var.getFacets().putAsLabel(INITER, m.getName());
					}
					addSkillMethod(c, m.getName());
					final Class r = m.getReturnType();
					var.getFacets().putAsLabel(TYPE, Types.get(r).toString());
				}
			}
			vs = m.getAnnotation(setter.class);
			if ( vs != null ) {
				final IDescription var = varList.get(vs.value());
				if ( var != null ) {
					var.getFacets().putAsLabel(SETTER, m.getName());
					addSkillMethod(c, m.getName());
				}
			}
		}
		VAR_DESCRIPTIONS.put(c, new GamaList(varList.values()));
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

	public static void initFieldGetters(final IType t) {
		List<IDescription> vars = getFieldDescriptions(t.toClass());
		if ( vars != null ) {
			for ( IDescription v : vars ) {
				String n = v.getName();
				IFieldGetter g = getFieldGetter(t.toClass(), v.getFacets().getLabel(GETTER));
				t.addFieldGetter(n, new TypeFieldExpression(n, v.getType(), v.getContentType(), g));
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

	public static IPrimitiveExecuter getPrimitive(final Class originalClass, final String methodName) {
		Class methodClass = originalClass;
		Map<String, PrimitiveExecuter> classExecuters = ACTION_HELPERS.get(methodClass);
		if ( !originalClass.isInterface() &&
			(classExecuters == null || !classExecuters.containsKey(methodName)) ) {
			try {
				while (!methodClass.isInterface() &&
					methodClass.getSuperclass().getMethod(methodName, new Class[] { IScope.class }) != null) {
					methodClass = methodClass.getSuperclass();
				}
			} catch (final Exception e) {}
		}
		if ( methodClass != originalClass ) {
			classExecuters = ACTION_HELPERS.get(methodClass);
		}
		if ( classExecuters == null ) {
			classExecuters = new HashMap();
			ACTION_HELPERS.put(methodClass, classExecuters);
		}
		PrimitiveExecuter methodExecuter = classExecuters.get(methodName);
		IType type = methodExecuter.getReturnType();
		if ( type == null ) {
			try {
				Class returnClass =
					methodClass.getMethod(methodName, new Class[] { IScope.class }).getReturnType();
				IType returnType = Types.get(returnClass);
				methodExecuter.setReturnType(returnType);
			} catch (Exception e) {
				methodExecuter.setReturnType(Types.NO_TYPE);
			}
		}
		if ( methodClass != originalClass ) {
			classExecuters = ACTION_HELPERS.get(originalClass);
			if ( classExecuters == null ) {
				classExecuters = new HashMap();
				ACTION_HELPERS.put(originalClass, classExecuters);
			}
			classExecuters.put(methodName, methodExecuter);

		}
		return methodExecuter;
	}

	static Class findCompatibleClass(final Map<Class, ?> executers, final Class original,
		final String method) {
		if ( executers.containsKey(original) ) {
			Map<String, ?> val = (Map<String, ?>) executers.get(original);
			if ( val.containsKey(method) ) { return original; }
		}
		for ( Map.Entry<Class, ?> entry : executers.entrySet() ) {
			Class key = entry.getKey();
			Map<String, ?> val = (Map<String, ?>) entry.getValue();
			if ( key.isAssignableFrom(original) && val.containsKey(method) ) { return key; }
		}
		return null;
	}

	public static IVarGetter getGetter(final Class original, final String methodName) {
		Class actual = findCompatibleClass(GETTER_HELPERS, original, methodName);
		if ( actual == null ) {
			final IFieldGetter fg = getFieldGetter(original, methodName);
			IVarGetter helper = new IVarGetter() {

				@Override
				public Object execute(final IAgent agent, final ISkill skill)
					throws GamaRuntimeException {
					return fg.value(agent); // ?
				}

			};
			Map<String, IVarGetter> methods = GETTER_HELPERS.get(original);
			if ( methods == null ) {
				methods = new HashMap();
				GETTER_HELPERS.put(original, methods);
			}
			methods.put(methodName, helper);
			return helper;
		}
		Map<String, IVarGetter> methods = GETTER_HELPERS.get(actual);
		IVarGetter helper = methods.get(methodName);
		if ( actual != original ) {
			methods = GETTER_HELPERS.get(original);
			if ( methods == null ) {
				methods = new HashMap();
				GETTER_HELPERS.put(original, methods);
			}
			methods.put(methodName, helper);
		}
		return helper;
	}

	public static IFieldGetter getFieldGetter(final Class original, final String methodName) {
		Class actual = findCompatibleClass(FIELD_HELPERS, original, methodName);
		Map<String, IFieldGetter> classExecuters = FIELD_HELPERS.get(actual);
		IFieldGetter helper = classExecuters.get(methodName);
		if ( actual != original ) {
			classExecuters = FIELD_HELPERS.get(original);
			if ( classExecuters == null ) {
				classExecuters = new HashMap();
				FIELD_HELPERS.put(original, classExecuters);
			}
			classExecuters.put(methodName, helper);
		}
		return helper;
	}

	public static IVarSetter getSetter(final Class original, final String methodName,
		final Class paramClass) {
		Class actual = findCompatibleClass(SETTER_HELPERS, original, methodName);
		Map<String, IVarSetter> classExecuters = SETTER_HELPERS.get(actual);
		IVarSetter helper = classExecuters.get(methodName);
		if ( actual != original ) {
			classExecuters = SETTER_HELPERS.get(original);
			if ( classExecuters == null ) {
				classExecuters = new HashMap();
				SETTER_HELPERS.put(original, classExecuters);
			}
			classExecuters.put(methodName, helper);

		}
		return helper;

	}

	static ClassPair binaryKey(final Class leftClass, final Class rightClass) {
		return new ClassPair(leftClass, rightClass);
	}

	static Class unaryKey(final Class childClass) {
		return childClass;
	}

	public static IOperatorExecuter getBinaryOperatorExecuter(final Class leftClass,
		final Class rightClass, final String methodName) {
		return BINARY_HELPERS.get(methodName).get(binaryKey(leftClass, rightClass));
	}

	public static IOperatorExecuter getUnaryOperatorExecuter(final Class childClass,
		final String methodName) {
		return UNARY_HELPERS.get(methodName).get(unaryKey(childClass));
	}

	public static Map<String, Class> getBuiltInSpeciesClasses() {
		return SPECIES_CLASSES;
	}

	public static Map<String, Class> getSkillClasses() {
		return SKILL_CLASSES;
	}

	public static Class getFactoryForKind(final int kind) {
		return FACTORIES_BY_KIND.get(kind);
	}

	public static Map<Integer, List<Class>> getClassesByKind() {
		return SYMBOLS_BY_KIND;
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

	public static Map<Class, Map<String, SymbolMetaDescription>> getSymbolMetas() {
		return SYMBOL_META;
	}

	private static IExpressionFactory getExpressionFactory() {
		if ( EXPRESSION_FACTORY == null ) {
			EXPRESSION_FACTORY = new GamlExpressionFactory();
		}
		return EXPRESSION_FACTORY;
	}

}
