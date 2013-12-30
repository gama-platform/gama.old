package msi.gaml.compilation;

import static msi.gama.common.interfaces.IKeyword.*;
import static msi.gaml.expressions.IExpressionCompiler.OPERATORS;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.JavaUtils;
import msi.gama.precompiler.*;
import msi.gama.util.*;
import msi.gama.util.file.*;
import msi.gaml.architecture.reflex.AbstractArchitecture;
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

	public final static List<String> ARCHITECTURES = new ArrayList();
	private final static Map<Set<Class>, Set<IDescription>> ALL_ADDITIONS = new HashMap();
	private final static Map<String, Class> SKILL_CLASSES = new HashMap();
	private final static GamlProperties SPECIES_SKILLS = new GamlProperties();
	private final static Map<Class, ISkill> SKILL_INSTANCES = new HashMap();
	private final static Map<Class, List<IDescription>> ADDITIONS = new HashMap();
	private final static Map<Class, List<TypeFieldExpression>> FIELDS = new HashMap();

	protected static String[] S(final String ... strings) {
		return strings;
	}

	protected static int[] I(final int ... integers) {
		return integers;
	}

	protected static FacetProto[] P(final FacetProto ... protos) {
		return protos;
	}

	protected static Class[] C(final Class ... classes) {
		return classes;
	}

	protected static IType T(final Class c) {
		return Types.get(c);
	}

	protected static IType T(final String c) {
		return Types.get(c);
	}

	protected static IType T(final int c) {
		return Types.get(c);
	}

	public final static Map<Integer, Set<String>> VARTYPE2KEYWORDS = new LinkedHashMap();

	public void _display(final String string, final Class class1, final IDisplayCreator d) {
		IGui.displays.put(string, d);
	}

	public void _species(final String name, final Class clazz, final IAgentConstructor helper, final String ... skills) {
		SpeciesProto proto = new SpeciesProto(name, clazz, helper, skills);
		tempSpecies.put(name, proto);

	}

	public static void buildMetaModel() {

		// We first build "agent" as the root of all other species (incl. "simulation")
		SpeciesProto ap = tempSpecies.remove(AGENT);
		// "agent" has no super-species yet
		SpeciesDescription agent = buildSpecies(ap, null, null, false);
		((GamaGenericAgentType) Types.builtInTypes.get(IKeyword.AGENT)).setSpecies(agent);

		// We then build "model", sub-species of "agent"
		SpeciesProto wp = tempSpecies.remove(MODEL);
		ModelDescription model = (ModelDescription) buildSpecies(wp, null, agent, true);

		// We close the first loop by putting agent "inside" model
		agent.setEnclosingDescription(model);
		model.addChild(agent);
		// // We create the type "agent"
		// initType("agent", new GamaGenericAgentType(), 11, 104, IA);

		// We create "experiment" as the root of all experiments, sub-species of "agent"
		SpeciesProto ep = tempSpecies.remove(EXPERIMENT);
		SpeciesDescription experiment = buildSpecies(ep, null, agent, false);
		model.addSpeciesType(experiment);

		// We now can attach "model" as a micro-species of "experiment"
		model.setEnclosingDescription(experiment);

		// We then create all other built-in species and attach them to "model"
		for ( SpeciesProto proto : tempSpecies.values() ) {
			model.addChild(buildSpecies(proto, model, agent, false));
		}
		model.buildTypes();
		model.finalizeDescription();
	}

	public static SpeciesDescription buildSpecies(final SpeciesProto proto, final SpeciesDescription macro,
		final SpeciesDescription parent, final boolean isGlobal) {
		Class clazz = proto.clazz;
		String name = proto.name;
		IAgentConstructor helper = proto.helper;
		String[] skills = proto.skills;
		Set<String> allSkills = new HashSet(Arrays.asList(skills));
		Set<String> builtInSkills = SPECIES_SKILLS.get(name);
		if ( builtInSkills != null ) {
			allSkills.addAll(builtInSkills);
		}
		SpeciesDescription desc;
		if ( !isGlobal ) {
			desc = DescriptionFactory.createBuiltInSpeciesDescription(name, clazz, macro, parent, helper, allSkills);
		} else {
			desc = DescriptionFactory.createRootModelDescription(name, clazz, macro, parent);
			Types.builtInTypes.addSpeciesType(desc);
		}

		// desc.setGlobal(isGlobal);
		List<IDescription> additions = getAdditions(clazz);
		if ( additions == null ) {
			additions = new GamaList();
		}
		for ( Class c : JavaUtils.collectImplementationClasses(clazz, Collections.EMPTY_SET, ADDITIONS.keySet()) ) {
			List<IDescription> add = getAdditions(c);
			if ( add != null ) {
				additions.addAll(add);
			}
		}
		for ( IDescription d : additions ) {
			d.setOriginName("built-in species " + name);
		}
		desc.copyJavaAdditions();
		desc.inheritFromParent();
		return desc;
	}

	private static class SpeciesProto {

		String name;
		Class clazz;
		IAgentConstructor helper;
		String[] skills;

		public SpeciesProto(final String name, final Class clazz, final IAgentConstructor helper, final String[] skills) {
			this.name = name;
			this.clazz = clazz;
			this.helper = helper;
			this.skills = skills;
		}
	}

	final static Map<String, SpeciesProto> tempSpecies = new LinkedHashMap();

	protected void _type(final String keyword, final IType typeInstance, final int id, final int varKind,
		final Class ... wraps) {
		initType(keyword, typeInstance, id, varKind, wraps);
	}

	protected void _file(final String string, final Class clazz, final GamaHelper<IGamaFile> helper, final String[] s) {
		GamaFileFactory.addFileTypeDefinition(string, clazz, helper, s);
	}

	protected void _skill(final String name, final Class clazz, final ISkillConstructor helper,
		final String ... species) {
		ISkill skill = helper.newInstance();
		if ( skill instanceof AbstractArchitecture ) {
			ARCHITECTURES.add(name);
		}
		skill.setName(name);
		skill.setDuplicator(helper);
		SKILL_INSTANCES.put(clazz, skill);
		for ( String spec : species ) {
			SPECIES_SKILLS.put(spec, name);
		}
		SKILL_CLASSES.put(name, clazz);
		List<IDescription> additions = getAdditions(clazz);
		if ( additions != null ) {
			for ( IDescription desc : additions ) {
				desc.setOriginName("skill " + name);
			}
		}
	}

	protected void _factories(final SymbolFactory ... factories) {
		for ( SymbolFactory f : factories ) {
			DescriptionFactory.addFactory(f);
		}
	}

	// combinations and doc missing
	protected void _symbol(final Class c, final IDescriptionValidator validator, final int sKind, final boolean remote,
		final boolean args, final boolean scope, final boolean sequence, final boolean unique,
		final boolean name_unique, final String[] parentSymbols, final int[] parentKinds, final FacetProto[] fmd,
		final String omissible, final String[][] combinations, final ISymbolConstructor sc, final String ... names) {

		Set<String> contextKeywords = new HashSet();
		Set<Integer> contextKinds = new HashSet();
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
				contextKinds.add(p);
			}
		}
		List<String> keywords = new ArrayList(Arrays.asList(names));
		// if the symbol is a variable
		if ( ISymbolKind.Variable.KINDS.contains(sKind) ) {
			Set<String> additonal = AbstractGamlAdditions.VARTYPE2KEYWORDS.get(sKind);
			if ( additonal != null ) {
				keywords.addAll(additonal);
			}
			// Special trick and workaround for compiling species and experiments rather than variables
			keywords.remove(SPECIES);
			keywords.remove(EXPERIMENT);
		}

		//

		// if ( validator != null ) {
		// GuiUtils.debug("## Individual validator found for " + c.getSimpleName());
		// }

		SymbolProto md =
			new SymbolProto(sequence, args, sKind, !scope, facets, omissible, combinations, contextKeywords,
				contextKinds, remote, unique, name_unique, sc, validator);
		DescriptionFactory.addProto(md, keywords);
	}

	public void _iterator(final String[] keywords, final Class[] classes, final int[] expectedContentTypes,
		final Class ret, final boolean c, final int t, final int content, final int index, final GamaHelper helper,
		final GamlElementDocumentation doc) {
		IExpressionCompiler.ITERATORS.addAll(Arrays.asList(keywords));
		_operator(keywords, classes, expectedContentTypes, ret, c, t, content, index, helper, doc);
	}

	public void _operator(final String[] keywords, final Class[] classes, final int[] expectedContentTypes,
		final Class ret, final boolean c, final int t, final int content, final int index, final GamaHelper helper,
		final GamlElementDocumentation doc) {
		Signature signature = new Signature(classes);
		for ( int i = 0; i < keywords.length; i++ ) {
			String kw = keywords[i];

			if ( !OPERATORS.containsKey(kw) ) {
				OPERATORS.put(kw, new GamaMap());
			}
			Map<Signature, IOperator> map = OPERATORS.get(kw);
			if ( !map.containsKey(signature) ) {
				IOperator exp;
				IType rt = Types.get(ret);
				if ( classes.length == 1 ) { // unary
					exp =
						new UnaryOperator(rt, helper, c, t, content, index, expectedContentTypes,
							IExpression.class.equals(classes[0]), signature);
				} else if ( classes.length == 2 ) { // binary
					if ( (kw.equals(OF) || kw.equals(_DOT)) && signature.get(0).isSpeciesType() ) {
						exp =
							new BinaryVarOperator(rt, helper, c, t, content, index,
								IExpression.class.equals(classes[1]), expectedContentTypes, signature);
					} else {
						exp =
							new BinaryOperator(rt, helper, c, t, content, index, IExpression.class.equals(classes[1]),
								expectedContentTypes, signature);
					}
				} else {
					exp =
						new NAryOperator(rt, helper, c, t, content, index, IExpression.class.equals(classes[1]),
							expectedContentTypes, signature);
					// FIXME The lazy attribute is completely wrong here
				}
				// FIXME Need to create an operator description or prototype rather than copying
				exp.setName(kw);
				exp.setDoc(doc);
				map.put(signature, exp);
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

	protected void _var(final Class clazz, final IDescription desc, final GamaHelper get, final GamaHelper init,
		final GamaHelper set) {
		add(clazz, desc);
		((VariableDescription) desc).addHelpers(get, init, set);
	}

	protected void _field(final Class clazz, final TypeFieldExpression getter) {
		add(clazz, getter);
	}

	// public static List<IDescription> getFieldDescriptions(final Class clazz) {
	// List<Class> classes = JavaUtils.collectImplementationClasses(clazz, Collections.EMPTY_SET, ADDITIONS.keySet());
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
		final String ... facets) {
		return DescriptionFactory.create(keyword, superDesc, children, facets);
	}

	protected IDescription desc(final String keyword, final String ... facets) {
		return DescriptionFactory.create(keyword, facets);
	}

	protected IDescription desc(final int keyword, final String ... facets) {
		return desc(Types.get(keyword).toString(), facets);
	}

	protected void _action(final String methodName, final Class clazz, final GamaHelper e, final IDescription desc) {
		((PrimitiveDescription) desc).setHelper(e);
		add(clazz, desc);
	}

	public static void initType(final String keyword, final IType typeInstance, final int id, final int varKind,
		final Class ... wraps) {
		Types.builtInTypes.initType(keyword, typeInstance, id, varKind, wraps);
		if ( !VARTYPE2KEYWORDS.containsKey(varKind) ) {
			VARTYPE2KEYWORDS.put(varKind, new HashSet());
		}
		VARTYPE2KEYWORDS.get(varKind).add(keyword);
	}

	public static List<IDescription> getAdditions(final Class clazz) {
		return ADDITIONS.get(clazz);
	}

	public static Map<String, TypeFieldExpression> getAllFields(final Class clazz) {
		List<Class> classes = JavaUtils.collectImplementationClasses(clazz, Collections.EMPTY_SET, FIELDS.keySet());
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
		return getSkillInstanceFor(getSkillClassFor(skillName));
	}

	public static Class getSkillClassFor(final String skillName) {
		return SKILL_CLASSES.get(skillName);
	}

	public static String getSkillNameFor(final Class skillClass) {
		for ( Map.Entry<String, Class> entry : SKILL_CLASSES.entrySet() ) {
			if ( skillClass == entry.getValue() ) { return entry.getKey(); }
		}
		return null;
	}

	public static ISkill getSkillInstanceFor(final Class skillClass) {
		ISkill skill = SKILL_INSTANCES.get(skillClass);
		// FIXME Replace this with an ISkillCreator that simply returns a new instance
		// Generates this ISkillCreator in GamaProcessor/JavaWriter
		return skill == null ? null : skill.duplicate();
	}

	public static Set<IDescription> getAllChildrenOf(final Class base, final Set<Class> skills) {
		Set<Class> key = new HashSet();
		key.add(base);
		key.addAll(skills);
		Set<IDescription> children = ALL_ADDITIONS.get(key);
		if ( children == null ) {
			children = new LinkedHashSet();
			final List<Class> classes = JavaUtils.collectImplementationClasses(base, skills, ADDITIONS.keySet());
			// GuiUtils.debug("#### Adding implementation classes " + classes);
			for ( final Class c1 : classes ) {
				List<IDescription> toAdd = getAdditions(c1);
				// GuiUtils.debug("    #### " + c1.getSimpleName() + ": " + toAdd);
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

	public static Collection<TypeFieldExpression> getAllFields() {
		Set<TypeFieldExpression> result = new HashSet();
		for ( List<TypeFieldExpression> list : FIELDS.values() ) {
			result.addAll(list);
		}
		return result;
	}

	public static Collection<IDescription> getAllVars() {
		Set<IDescription> result = new HashSet();
		for ( TypeDescription s : Types.getBuiltInSpecies() ) {
			result.addAll(s.getVariables().values());
			for ( String a : s.getActionNames() ) {
				StatementDescription action = s.getAction(a);
				result.addAll(action.getArgs());
			}
		}
		for ( Class c : SKILL_CLASSES.values() ) {
			List<IDescription> descs = ADDITIONS.get(c);
			if ( descs != null ) {
				for ( IDescription desc : descs ) {
					if ( desc instanceof VariableDescription ) {
						result.add(desc);
					} else if ( desc instanceof StatementDescription ) {
						result.addAll(((StatementDescription) desc).getArgs());
					}
				}
			}
		}
		return result;
	}

	public static Collection<String> getAllAspects() {
		Set<String> result = new HashSet();
		for ( TypeDescription s : Types.getBuiltInSpecies() ) {
			result.addAll(((SpeciesDescription) s).getAspectNames());
		}
		return result;
	}

	public static Collection<String> getAllSkills() {
		return SKILL_CLASSES.keySet();
	}

	public static Collection<IDescription> getAllActions() {
		Set<IDescription> result = new HashSet();
		for ( TypeDescription s : Types.getBuiltInSpecies() ) {
			result.addAll(s.getActions());
		}
		for ( Class c : SKILL_CLASSES.values() ) {
			List<IDescription> descs = ADDITIONS.get(c);
			if ( descs != null ) {
				for ( IDescription desc : descs ) {
					if ( !(desc instanceof VariableDescription) ) {
						result.add(desc);
					}
				}
			}
		}

		return result;
	}

	public static final Set<String> CONSTANTS = new HashSet();

	public static void _constants(final String[] ... strings) {
		for ( String[] s : strings ) {
			for ( String s2 : s ) {
				CONSTANTS.add(s2);
			}
		}
	}

}
