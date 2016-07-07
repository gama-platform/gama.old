/*********************************************************************************************
 *
 *
 * 'AbstractGamlAdditions.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.compilation;

import static msi.gama.common.interfaces.IKeyword.AGENT;
import static msi.gama.common.interfaces.IKeyword.EXPERIMENT;
import static msi.gama.common.interfaces.IKeyword.MODEL;
import static msi.gama.common.interfaces.IKeyword.OF;
import static msi.gama.common.interfaces.IKeyword.SPECIES;
import static msi.gama.common.interfaces.IKeyword._DOT;
import static msi.gaml.expressions.IExpressionCompiler.OPERATORS;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import gnu.trove.set.hash.TIntHashSet;
import msi.gama.common.interfaces.IDisplayCreator;
import msi.gama.common.interfaces.IDisplayCreator.DisplayDescription;
import msi.gama.common.interfaces.IExperimentAgentCreator;
import msi.gama.common.interfaces.IExperimentAgentCreator.ExperimentAgentDescription;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ISkill;
import msi.gama.common.util.ExperimentManager;
import msi.gama.common.util.JavaUtils;
import msi.gama.precompiler.GamlProperties;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.util.TOrderedHashMap;
import msi.gama.util.file.IGamaFile;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.architecture.reflex.AbstractArchitecture;
import msi.gaml.descriptions.FacetProto;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.descriptions.PrimitiveDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.descriptions.SymbolProto;
import msi.gaml.descriptions.SymbolSerializer;
import msi.gaml.descriptions.TypeDescription;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IExpressionCompiler;
import msi.gaml.extensions.genstar.IGamaPopulationsLinker;
import msi.gaml.extensions.genstar.IGamaPopulationsLinkerConstructor;
import msi.gaml.factories.ChildrenProvider;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.factories.SymbolFactory;
import msi.gaml.skills.Skill;
import msi.gaml.types.GamaFileType;
import msi.gaml.types.GamaGenericAgentType;
import msi.gaml.types.IType;
import msi.gaml.types.ParametricFileType;
import msi.gaml.types.Signature;
import msi.gaml.types.Types;

/**
 *
 * The class AbstractGamlAdditions. Default base implementation for plugins'
 * gaml additions.
 *
 * @author drogoul
 * @since 17 mai 2012
 *
 */
public abstract class AbstractGamlAdditions implements IGamlAdditions {

	public final static List<String> ARCHITECTURES = new ArrayList();
	private final static Map<Set<Class>, Set<IDescription>> ALL_ADDITIONS = new THashMap();
	private final static Map<String, Class> SKILL_CLASSES = new THashMap();
	private final static GamlProperties SPECIES_SKILLS = new GamlProperties();
	private final static Map<Class, ISkill> SKILL_INSTANCES = new THashMap();
	private final static Map<Class, List<IDescription>> ADDITIONS = new THashMap();
	private final static Map<Class, List<OperatorProto>> FIELDS = new THashMap();
	public final static Map<String, IGamaPopulationsLinker> POPULATIONS_LINKERS = new THashMap<String, IGamaPopulationsLinker>();

	protected static String[] S(final String... strings) {
		return strings;
	}

	protected static int[] I(final int... integers) {
		return integers;
	}

	protected static FacetProto[] P(final FacetProto... protos) {
		return protos;
	}

	protected static Class[] C(final Class... classes) {
		return classes;
	}

	protected static IType T(final Class c) {
		return Types.get(c);
	}

	protected static String Ti(final Class c) {
		return String.valueOf(Types.get(c).id());
	}

	protected static String Ts(final Class c) {
		return Types.get(c).toString();
	}

	protected static IType T(final String c) {
		return Types.get(c);
	}

	protected static IType T(final int c) {
		return Types.get(c);
	}

	public final static Map<Integer, Set<String>> VARTYPE2KEYWORDS = new TOrderedHashMap();

	public void _display(final String string, final Class class1, final IDisplayCreator d) {
		CONSTANTS.add(string);
		final DisplayDescription dd = new DisplayDescription(d, string, GamaBundleLoader.CURRENT_PLUGIN_NAME);
		IGui.DISPLAYS.put(string, dd);
	}
	
	public void _experiment(final String string, final Class class1, final IExperimentAgentCreator d) {
		CONSTANTS.add(string);
		final ExperimentAgentDescription ed = new ExperimentAgentDescription(d, string, GamaBundleLoader.CURRENT_PLUGIN_NAME);
		ExperimentManager.EXPERIMENTS.put(string, ed);
	}	

	public void _species(final String name, final Class clazz, final IAgentConstructor helper, final String... skills) {
		final SpeciesProto proto = new SpeciesProto(name, clazz, helper, skills);
		DescriptionFactory.addSpeciesNameAsType(name);
		tempSpecies.put(name, proto);

	}

	public static void buildMetaModel() {

		// We first build "agent" as the root of all other species (incl.
		// "model")
		final SpeciesProto ap = tempSpecies.remove(AGENT);
		// "agent" has no super-species yet
		final SpeciesDescription agent = buildSpecies(ap, null, null, false);
		((GamaGenericAgentType) Types.builtInTypes.get(IKeyword.AGENT)).setSpecies(agent);

		// We then build "model", sub-species of "agent"
		final SpeciesProto wp = tempSpecies.remove(MODEL);
		final ModelDescription model = (ModelDescription) buildSpecies(wp, null, agent, true);

		// We close the first loop by putting agent "inside" model
		agent.setEnclosingDescription(model);
		model.addChild(agent);

		// We create "experiment" as the root of all experiments, sub-species of
		// "agent"
		final SpeciesProto ep = tempSpecies.remove(EXPERIMENT);
		final SpeciesDescription experiment = buildSpecies(ep, null, agent, false);
		model.addSpeciesType(experiment);

		// We now can attach "model" as a micro-species of "experiment"
		model.setEnclosingDescription(experiment);

		// We then create all other built-in species and attach them to "model"
		for (final SpeciesProto proto : tempSpecies.values()) {
			model.addChild(buildSpecies(proto, model, agent, false));
		}
		model.buildTypes();
		model.finalizeDescription();
	}

	public static SpeciesDescription buildSpecies(final SpeciesProto proto, final SpeciesDescription macro,
			final SpeciesDescription parent, final boolean isGlobal) {
		final Class clazz = proto.clazz;
		final String name = proto.name;
		final IAgentConstructor helper = proto.helper;
		final String[] skills = proto.skills;
		final String plugin = proto.plugin;
		final Set<String> allSkills = new HashSet(Arrays.asList(skills));
		final Set<String> builtInSkills = SPECIES_SKILLS.get(name);
		if (builtInSkills != null) {
			allSkills.addAll(builtInSkills);
		}
		SpeciesDescription desc;
		if (!isGlobal) {
			desc = DescriptionFactory.createBuiltInSpeciesDescription(name, clazz, macro, parent, helper, allSkills,
					plugin);
		} else {
			desc = DescriptionFactory.createRootModelDescription(name, clazz, macro, parent);
			Types.builtInTypes.addSpeciesType(desc);
		}

		// desc.setGlobal(isGlobal);
		List<IDescription> additions = getAdditions(clazz);
		if (additions == null) {
			additions = new ArrayList();
		}
		for (final Object c : JavaUtils.collectImplementationClasses(clazz, Collections.EMPTY_SET, ADDITIONS.keySet())) {
			final List<IDescription> add = getAdditions((Class) c);
			if (add != null) {
				additions.addAll(add);
			}
		}
		for (final IDescription d : additions) {
			// d.resetOriginName();
			d.setOriginName("built-in species " + name);
		}
		desc.copyJavaAdditions();
		desc.inheritFromParent();
		return desc;
	}

	private static class SpeciesProto {

		final String name;
		final String plugin;
		final Class clazz;
		final IAgentConstructor helper;
		final String[] skills;

		public SpeciesProto(final String name, final Class clazz, final IAgentConstructor helper,
				final String[] skills) {
			plugin = GamaBundleLoader.CURRENT_PLUGIN_NAME;
			this.name = name;
			this.clazz = clazz;
			this.helper = helper;
			this.skills = skills;
		}
	}

	final static Map<String, SpeciesProto> tempSpecies = new TOrderedHashMap();

	protected void _type(final String keyword, final IType typeInstance, final int id, final int varKind,
			final Class... wraps) {
		initType(keyword, typeInstance, id, varKind, wraps);
	}

	protected void _file(final String string, final Class clazz, final GamaHelper<IGamaFile> helper,
			final int innerType, final int keyType, final int contentType, final String[] s) {
		helper.setSkillClass(clazz);
		GamaFileType.addFileTypeDefinition(string, Types.get(innerType), Types.get(keyType), Types.get(contentType),
				clazz, helper, s);
		if (!VARTYPE2KEYWORDS.containsKey(ISymbolKind.Variable.CONTAINER)) {
			VARTYPE2KEYWORDS.put(ISymbolKind.Variable.CONTAINER, new HashSet());
		}
		VARTYPE2KEYWORDS.get(ISymbolKind.Variable.CONTAINER).add(string + "_file");
	}

	protected void _skill(final String name, final Class clazz, final String... species) {
		final ISkill skill = Skill.Factory.create(name, clazz, GamaBundleLoader.CURRENT_PLUGIN_NAME);
		// ISkill skill = helper.newInstance();
		if (skill instanceof AbstractArchitecture) {
			ARCHITECTURES.add(name);
		}
		// skill.setName(name);
		// skill.setDuplicator(helper);
		SKILL_INSTANCES.put(clazz, skill);
		for (final String spec : species) {
			SPECIES_SKILLS.put(spec, name);
		}
		SKILL_CLASSES.put(name, clazz);
		final List<IDescription> additions = getAdditions(clazz);
		if (additions != null) {
			for (final IDescription desc : additions) {
				desc.setOriginName("skill " + name);
				desc.setDefiningPlugin(GamaBundleLoader.CURRENT_PLUGIN_NAME);
			}
		}
	}

	protected void _factories(final SymbolFactory... factories) {
		for (final SymbolFactory f : factories) {
			DescriptionFactory.addFactory(f);
		}
	}

	// combinations and doc missing
	protected void _symbol(final Class c, /* final int docIndex, */final IDescriptionValidator validator,
			final SymbolSerializer serializer, final int sKind, final boolean remote, final boolean args,
			final boolean scope, final boolean sequence, final boolean unique, final boolean name_unique,
			final String[] parentSymbols, final int[] parentKinds, final FacetProto[] fmd, final String omissible,
			/* final String[][] combinations, */final ISymbolConstructor sc, final String... names) {

		final Set<String> contextKeywords = new THashSet();
		final TIntHashSet contextKinds = new TIntHashSet();
		final Map<String, FacetProto> facets = new THashMap();
		if (fmd != null) {
			for (final FacetProto f : fmd) {
				facets.put(f.name, f);
			}
		}

		if (parentSymbols != null) {
			for (final String p : parentSymbols) {
				contextKeywords.add(p);
			}
		}
		if (parentKinds != null) {
			for (final int p : parentKinds) {
				contextKinds.add(p);
			}
		}
		final List<String> keywords = names == null ? new ArrayList() : new ArrayList(Arrays.asList(names));
		// if the symbol is a variable
		if (ISymbolKind.Variable.KINDS.contains(sKind)) {
			final Set<String> additonal = AbstractGamlAdditions.VARTYPE2KEYWORDS.get(sKind);
			if (additonal != null) {
				keywords.addAll(additonal);
			}
			// Special trick and workaround for compiling species and
			// experiments rather than variables
			keywords.remove(SPECIES);
			keywords.remove(EXPERIMENT);
		}

		//

		// if ( validator != null ) {
		// scope.getGui().debug("## Individual validator found for " +
		// c.getSimpleName());
		// }

		final SymbolProto md = new SymbolProto(c, sequence, args, sKind, !scope, facets, omissible,
				/* combinations, */contextKeywords, contextKinds, remote, unique, name_unique, sc, validator,
				serializer, names == null || names.length == 0 ? "variable declaration" : names[0],
				GamaBundleLoader.CURRENT_PLUGIN_NAME);
		// if ( names == null || names.length == 0 ) {
		// md.setName("variable declaration");
		// } else {
		// md.setName(names[0]);
		// }
		DescriptionFactory.addProto(md, keywords);
	}

	public void _iterator(final String[] keywords, final Method method, final Class[] classes,
			final int[] expectedContentTypes, final Class ret, final boolean c, final int t, final int content,
			final int index, final GamaHelper helper/* , final int doc */) {
		IExpressionCompiler.ITERATORS.addAll(Arrays.asList(keywords));
		_operator(keywords, method, classes, expectedContentTypes, ret, c, t, content, index,
				helper/* , doc */);
	}

	public void _operator(final String[] keywords, final AccessibleObject method, final Class[] classes,
			final int[] expectedContentTypes, final Object returnClassOrType, final boolean c, final int t,
			final int content, final int index, final GamaHelper helper) {
		final Signature signature = new Signature(classes);
		final String plugin = GamaBundleLoader.CURRENT_PLUGIN_NAME;
		for (final String keyword : keywords) {
			final String kw = keyword;
			if (!OPERATORS.containsKey(kw)) {
				OPERATORS.put(kw, new THashMap());
			}
			final Map<Signature, OperatorProto> map = OPERATORS.get(kw);
			if (!map.containsKey(signature)) {
				OperatorProto proto;
				IType rt;
				if (returnClassOrType instanceof Class) {
					rt = Types.get((Class) returnClassOrType);
				} else {
					rt = (IType) returnClassOrType;
				}
				if (classes.length == 1) { // unary
					proto = new OperatorProto(kw, method, helper, c, false, rt, signature,
							IExpression.class.equals(classes[0]), t, content, index, expectedContentTypes, plugin);
				} else if (classes.length == 2) { // binary
					if ((kw.equals(OF) || kw.equals(_DOT)) && signature.get(0).isAgentType()) {
						proto = new OperatorProto(kw, method, helper, c, true, rt, signature,
								IExpression.class.equals(classes[1]), t, content, index, expectedContentTypes, plugin);
					} else {
						proto = new OperatorProto(kw, method, helper, c, false, rt, signature,
								IExpression.class.equals(classes[1]), t, content, index, expectedContentTypes, plugin);
					}
				} else {
					proto = new OperatorProto(kw, method, helper, c, false, rt, signature,
							IExpression.class.equals(classes[classes.length - 1]), t, content, index,
							expectedContentTypes, plugin);
				}
				map.put(signature, proto);
			}
		}

	}

	// For files
	public void _operator(final String[] keywords, final AccessibleObject method, final Class[] classes,
			final int[] expectedContentTypes, final Class ret, final boolean c, final String typeAlias,
			final GamaHelper helper/* , final int doc */) {
		final ParametricFileType fileType = GamaFileType.getTypeFromAlias(typeAlias);
		this._operator(keywords, method, classes, expectedContentTypes, fileType, c, ITypeProvider.NONE,
				ITypeProvider.NONE, ITypeProvider.NONE, helper);
	}

	protected void _populationsLinker(final String name, final Class clazz,
			final IGamaPopulationsLinkerConstructor helper) {
		final IGamaPopulationsLinker linker = helper.newInstance();
		if (POPULATIONS_LINKERS.get(name) != null) {
		} // TODO inform duplication
		POPULATIONS_LINKERS.put(name, linker);
	}

	private void add(final Class clazz, final IDescription desc) {
		if (!ADDITIONS.containsKey(clazz)) {
			ADDITIONS.put(clazz, new ArrayList());
		}
		ADDITIONS.get(clazz).add(desc);
	}

	protected void _var(final Class clazz, final IDescription desc, final GamaHelper get, final GamaHelper init,
			final GamaHelper set) {
		add(clazz, desc);
		((VariableDescription) desc).addHelpers(get, init, set);
		((VariableDescription) desc).setDefiningPlugin(GamaBundleLoader.CURRENT_PLUGIN_NAME);
	}

	protected void _field(final Class clazz, final OperatorProto getter) {
		if (!FIELDS.containsKey(clazz)) {
			FIELDS.put(clazz, new ArrayList());
		}
		FIELDS.get(clazz).add(getter);
	}

	// public static List<IDescription> getFieldDescriptions(final Class clazz)
	// {
	// List<Class> classes = JavaUtils.collectImplementationClasses(clazz,
	// Collections.EMPTY_SET, ADDITIONS.keySet());
	// Map<String, IDescription> fieldsMap = new LinkedHashMap();
	// for ( Class c : classes ) {
	// List<IDescription> descriptions = getAdditions(c);
	// if ( descriptions == null ) {
	// continue;
	// }
	// for ( IDescription desc : descriptions ) {
	// fieldsMap.put(desc.getName(), desc);
	// }
	// }
	// List<IDescription> descs = new GamaList(fieldsMap.values());
	// return descs;
	// }

	protected IDescription desc(final String keyword, final IDescription superDesc, final ChildrenProvider children,
			final String... facets) {
		return DescriptionFactory.create(keyword, superDesc, children, facets);
	}

	protected IDescription desc(final String keyword, final String... facets) {
		return DescriptionFactory.create(keyword, facets);
	}

	protected IDescription desc(final int keyword, final String... facets) {
		return desc(Types.get(keyword).toString(), facets);
	}

	protected void _action(final String methodName, final Class clazz, final GamaHelper e, final IDescription desc,
			final AccessibleObject method) {
		((PrimitiveDescription) desc).setHelper(e, method);
		((PrimitiveDescription) desc).setDefiningPlugin(GamaBundleLoader.CURRENT_PLUGIN_NAME);
		add(clazz, desc);
	}

	public static void initType(final String keyword, final IType typeInstance, final int id, final int varKind,
			final Class... wraps) {
		final IType type = Types.builtInTypes.initType(keyword, typeInstance, id, varKind, wraps);
		type.setDefiningPlugin(GamaBundleLoader.CURRENT_PLUGIN_NAME);
		Types.cache(id, typeInstance);
		if (!VARTYPE2KEYWORDS.containsKey(varKind)) {
			VARTYPE2KEYWORDS.put(varKind, new HashSet());
		}
		VARTYPE2KEYWORDS.get(varKind).add(keyword);
	}

	public static List<IDescription> getAdditions(final Class clazz) {
		return ADDITIONS.get(clazz);
	}

	public static Map<String, OperatorProto> getAllFields(final Class clazz) {
		final List<Class> classes = JavaUtils.collectImplementationClasses(clazz, Collections.EMPTY_SET,
				FIELDS.keySet());
		final Map<String, OperatorProto> fieldsMap = new TOrderedHashMap();
		for (final Class c : classes) {
			final List<OperatorProto> fields = FIELDS.get(c);
			if (fields == null) {
				continue;
			}
			for (final OperatorProto desc : fields) {
				fieldsMap.put(desc.getName(), desc);
			}
		}
		return fieldsMap;
	}

	public static ISkill getSkillInstanceFor(final String skillName) {
		return getSkillInstanceFor(getSkillClassFor(skillName));
	}

	public static Class<? extends ISkill> getSkillClassFor(final String skillName) {
		return SKILL_CLASSES.get(skillName);
	}

	public static String getSkillNameFor(final Class skillClass) {
		for (final Map.Entry<String, Class> entry : SKILL_CLASSES.entrySet()) {
			if (skillClass == entry.getValue()) {
				return entry.getKey();
			}
		}
		return null;
	}

	public static ISkill getSkillInstanceFor(final Class skillClass) {
		final ISkill skill = SKILL_INSTANCES.get(skillClass);
		return skill == null ? null : skill.duplicate();
	}

	public static Set<IDescription> getAllChildrenOf(final Class base, final Set<Class<? extends ISkill>> skills) {
		final Set<Class> key = new HashSet();
		if (base != null) {
			key.add(base);
		}
		key.addAll(skills);
		Set<IDescription> children = ALL_ADDITIONS.get(key);
		if (children == null) {
			children = new LinkedHashSet();
			final List<Class> classes = JavaUtils.collectImplementationClasses(base, skills, ADDITIONS.keySet());
			// scope.getGui().debug("#### Adding implementation classes " +
			// classes);
			for (final Class c1 : classes) {
				final List<IDescription> toAdd = getAdditions(c1);
				// scope.getGui().debug(" #### " + c1.getSimpleName() + ": " +
				// toAdd);
				if (toAdd != null && !toAdd.isEmpty()) {
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
		final Set<String> skills = SPECIES_SKILLS.get(speciesName);
		if (skills == null) {
			return Collections.EMPTY_SET;
		}
		return skills;
	}

	public static Collection<OperatorProto> getAllFields() {
		final Set<OperatorProto> result = new HashSet();
		for (final List<OperatorProto> list : FIELDS.values()) {
			result.addAll(list);
		}
		return result;
	}

	public static Collection<IDescription> getAllVars() {
		final Set<IDescription> result = new HashSet();
		for (final TypeDescription s : Types.getBuiltInSpecies()) {
			result.addAll(s.getVariables().values());
			for (final String a : s.getActionNames()) {
				final StatementDescription action = s.getAction(a);
				result.addAll(action.getArgs());
			}
		}
		for (final Class c : SKILL_CLASSES.values()) {
			final List<IDescription> descs = ADDITIONS.get(c);
			if (descs != null) {
				for (final IDescription desc : descs) {
					if (desc instanceof VariableDescription) {
						result.add(desc);
					} else if (desc instanceof StatementDescription) {
						result.addAll(((StatementDescription) desc).getArgs());
					}
				}
			}
		}
		return result;
	}

	public static Collection<IDescription> getVariablesForSkill(final String s) {
		final Set<IDescription> result = new LinkedHashSet();
		final List<IDescription> descs = ADDITIONS.get(SKILL_CLASSES.get(s));
		if (descs != null) {

			for (final IDescription desc : descs) {
				if (desc instanceof VariableDescription) {
					result.add(desc);
				}
			}

		}
		return result;
	}

	public static Collection<IDescription> getActionsForSkill(final String s) {
		final Set<IDescription> result = new LinkedHashSet();
		final List<IDescription> descs = ADDITIONS.get(SKILL_CLASSES.get(s));
		if (descs != null) {

			for (final IDescription desc : descs) {
				if (desc instanceof PrimitiveDescription) {
					result.add(desc);
				}
			}

		}
		return result;
	}

	public static Collection<SymbolProto> getStatementsForSkill(final String s) {
		final Set<SymbolProto> result = new LinkedHashSet();
		for (final String p : DescriptionFactory.getStatementProtoNames()) {
			final SymbolProto proto = DescriptionFactory.getStatementProto(p);
			if (proto.shouldBeDefinedIn(s)) {
				result.add(proto);
			}
		}
		return result;
	}

	public static Collection<String> getAllAspects() {
		final Set<String> result = new HashSet();
		for (final TypeDescription s : Types.getBuiltInSpecies()) {
			result.addAll(((SpeciesDescription) s).getAspectNames());
		}
		return result;
	}

	public static Collection<String> getAllSkills() {
		return SKILL_CLASSES.keySet();
	}

	public static Collection<String> getSkills() {
		final Set<String> result = new LinkedHashSet();
		for (final String s : getAllSkills()) {
			final Class c = SKILL_CLASSES.get(s);
			if (!IArchitecture.class.isAssignableFrom(c)) {
				result.add(s);
			}
		}
		return result;
	}

	public static Collection<String> getControls() {
		return ARCHITECTURES;
	}

	public static Collection<IDescription> getAllActions() {
		final Map<String, IDescription> result = new HashMap();
		for (final TypeDescription s : Types.getBuiltInSpecies()) {
			for (final StatementDescription sd : s.getActions()) {
				if (!result.containsKey(sd.getName()))
					result.put(sd.getName(), sd);
			}
		}
		for (final Class c : SKILL_CLASSES.values()) {
			final List<IDescription> descs = ADDITIONS.get(c);
			if (descs != null) {
				for (final IDescription desc : descs) {
					if (!(desc instanceof VariableDescription) && !result.containsKey(desc.getName())) {
						result.put(desc.getName(), desc);
					}
				}
			}
		}

		return result.values();
	}

	public static final Set<String> CONSTANTS = new HashSet();

	public static void _constants(final String[]... strings) {
		for (final String[] s : strings) {
			for (final String s2 : s) {
				CONSTANTS.add(s2);
			}
		}
	}

	/**
	 * @param name
	 * @return
	 */
	public static boolean isUnaryOperator(final String name) {
		if (!OPERATORS.containsKey(name)) {
			return false;
		}
		final Map<Signature, OperatorProto> map = OPERATORS.get(name);
		for (final Signature s : map.keySet()) {
			if (s.isUnary()) {
				return true;
			}
		}
		return false;
	}

}
