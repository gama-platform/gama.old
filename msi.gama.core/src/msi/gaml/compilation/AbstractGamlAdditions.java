/*********************************************************************************************
 *
 * 'AbstractGamlAdditions.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.compilation;

import static msi.gama.common.interfaces.IKeyword.OF;
import static msi.gama.common.interfaces.IKeyword.SPECIES;
import static msi.gama.common.interfaces.IKeyword._DOT;
import static msi.gaml.expressions.IExpressionCompiler.OPERATORS;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import msi.gama.common.interfaces.IDisplayCreator;
import msi.gama.common.interfaces.IDisplayCreator.DisplayDescription;
import msi.gama.common.interfaces.IExperimentAgentCreator;
import msi.gama.common.interfaces.IExperimentAgentCreator.ExperimentAgentDescription;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.ISkill;
import msi.gama.common.util.JavaUtils;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.util.TOrderedHashMap;
import msi.gama.util.file.IGamaFile;
import msi.gaml.compilation.kernel.GamaBundleLoader;
import msi.gaml.compilation.kernel.GamaMetaModel;
import msi.gaml.compilation.kernel.GamaSkillRegistry;
import msi.gaml.descriptions.FacetProto;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IDescription.DescriptionVisitor;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.descriptions.PrimitiveDescription;
import msi.gaml.descriptions.SkillDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.descriptions.SymbolProto;
import msi.gaml.descriptions.SymbolSerializer;
import msi.gaml.descriptions.TypeDescription;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.IExpressionCompiler;
import msi.gaml.extensions.genstar.IGamaPopulationsLinker;
import msi.gaml.extensions.genstar.IGamaPopulationsLinkerConstructor;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.factories.SymbolFactory;
import msi.gaml.types.GamaFileType;
import msi.gaml.types.IType;
import msi.gaml.types.ParametricFileType;
import msi.gaml.types.Signature;
import msi.gaml.types.Types;

/**
 *
 * The class AbstractGamlAdditions. Default base implementation for plugins' gaml additions.
 *
 * @author drogoul
 * @since 17 mai 2012
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public abstract class AbstractGamlAdditions implements IGamlAdditions {

	public static final Set<String> CONSTANTS = new HashSet();
	final static Multimap<Class, IDescription> ADDITIONS = HashMultimap.create();
	private static Function<Class, Collection<IDescription>> INTO_DESCRIPTIONS = input -> ADDITIONS.get(input);
	private final static Multimap<Class, OperatorProto> FIELDS = HashMultimap.create();
	public final static Multimap<Integer, String> VARTYPE2KEYWORDS = HashMultimap.create();
	public final static Map<String, IGamaPopulationsLinker> POPULATIONS_LINKERS =
			new THashMap<String, IGamaPopulationsLinker>();
	public final static Map<String, String> TEMPORARY_BUILT_IN_VARS_DOCUMENTATION = new THashMap<>();

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

	protected static IType<?> T(final Class<?> c) {
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

	public void _display(final String string, final Class class1, final IDisplayCreator d) {
		CONSTANTS.add(string);
		final DisplayDescription dd = new DisplayDescription(d, string, GamaBundleLoader.CURRENT_PLUGIN_NAME);
		IGui.DISPLAYS.put(string, dd);
	}

	public void _experiment(final String string, final Class class1, final IExperimentAgentCreator d) {
		CONSTANTS.add(string);
		final ExperimentAgentDescription ed =
				new ExperimentAgentDescription(d, string, GamaBundleLoader.CURRENT_PLUGIN_NAME);
		GamaMetaModel.INSTANCE.addExperimentAgentCreator(string, ed);
	}

	public void _species(final String name, final Class clazz, final IAgentConstructor helper, final String... skills) {
		GamaMetaModel.INSTANCE.addSpecies(name, clazz, helper, skills);
		DescriptionFactory.addSpeciesNameAsType(name);
	}

	protected void _type(final String keyword, final IType typeInstance, final int id, final int varKind,
			final Class... wraps) {
		initType(keyword, typeInstance, id, varKind, wraps);
	}

	protected void _file(final String string, final Class clazz, final GamaHelper<IGamaFile<?, ?, ?, ?>> helper,
			final int innerType, final int keyType, final int contentType, final String[] s) {
		helper.setSkillClass(clazz);
		GamaFileType.addFileTypeDefinition(string, Types.get(innerType), Types.get(keyType), Types.get(contentType),
				clazz, helper, s);
		VARTYPE2KEYWORDS.put(ISymbolKind.Variable.CONTAINER, string + "_file");
	}

	protected void _skill(final String name, final Class clazz, final String... species) {
		final SkillDescription sd = GamaSkillRegistry.INSTANCE.register(name, clazz,
				GamaBundleLoader.CURRENT_PLUGIN_NAME, ADDITIONS.get(clazz), species);
	}

	protected void _factories(final SymbolFactory... factories) {
		for (final SymbolFactory f : factories) {
			DescriptionFactory.addFactory(f);
		}
	}

	// doc missing
	protected void _symbol(final Class c, /* final int docIndex, */final IDescriptionValidator validator,
			final SymbolSerializer serializer, final int sKind, final boolean remote, final boolean args,
			final boolean scope, final boolean sequence, final boolean unique, final boolean name_unique,
			final String[] contextKeywords, final int[] contextKinds, final FacetProto[] fmd, final String omissible,
			final ISymbolConstructor sc, final String... names) {

		final Collection<String> keywords;
		if (ISymbolKind.Variable.KINDS.contains(sKind)) {
			keywords = VARTYPE2KEYWORDS.get(sKind);
			keywords.remove(SPECIES);
		} else {
			keywords = Arrays.asList(names);
		}

		final SymbolProto md = new SymbolProto(c, sequence, args, sKind, !scope, fmd, omissible, contextKeywords,
				contextKinds, remote, unique, name_unique, sc, validator, serializer,
				names == null || names.length == 0 ? "variable declaration" : names[0],
				GamaBundleLoader.CURRENT_PLUGIN_NAME);
		DescriptionFactory.addProto(md, keywords);
	}

	public void _iterator(final String[] keywords, final Method method, final Class[] classes,
			final int[] expectedContentTypes, final Class ret, final boolean c, final int t, final int content,
			final int index, final GamaHelper helper) {
		IExpressionCompiler.ITERATORS.addAll(Arrays.asList(keywords));
		_operator(keywords, method, classes, expectedContentTypes, ret, c, t, content, index, helper);
	}

	public void _operator(final String[] keywords, final AccessibleObject method, final Class[] classes,
			final int[] expectedContentTypes, final Object returnClassOrType, final boolean c, final int t,
			final int content, final int index, final GamaHelper helper) {
		final Signature signature = new Signature(classes);
		final String plugin = GamaBundleLoader.CURRENT_PLUGIN_NAME;
		for (final String keyword : keywords) {
			final String kw = keyword;
			if (!OPERATORS.containsKey(kw)) {
				OPERATORS.put(kw, new THashMap<>());
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
		if (POPULATIONS_LINKERS.get(name) != null) {} // TODO inform duplication
		POPULATIONS_LINKERS.put(name, linker);
	}

	private void add(final Class clazz, final IDescription desc) {
		ADDITIONS.put(clazz, desc);

		// final mettre documentation ?
	}

	protected void _var(final Class clazz, final String doc, final IDescription desc, final GamaHelper get,
			final GamaHelper init, final GamaHelper set) {
		add(clazz, desc);
		((VariableDescription) desc).addHelpers(get, init, set);
		TEMPORARY_BUILT_IN_VARS_DOCUMENTATION.put(desc.getName(), doc);
		((VariableDescription) desc).setDefiningPlugin(GamaBundleLoader.CURRENT_PLUGIN_NAME);
	}

	protected void _field(final Class clazz, final String doc, final OperatorProto getter) {
		FIELDS.put(clazz, getter);
	}

	protected IDescription desc(final String keyword, final IDescription superDesc, final ChildrenProvider children,
			final String... facets) {
		return DescriptionFactory.create(keyword, superDesc, children.getChildren(), facets);
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

	public static void initType(final String keyword, final IType<?> typeInstance, final int id, final int varKind,
			final Class... wraps) {
		final IType<?> type = Types.builtInTypes.initType(keyword, typeInstance, id, varKind, wraps[0]);
		for (final Class cc : wraps) {
			Types.CLASSES_TYPES_CORRESPONDANCE.put(cc, type.getName());
		}
		type.setDefiningPlugin(GamaBundleLoader.CURRENT_PLUGIN_NAME);
		Types.cache(id, typeInstance);
		VARTYPE2KEYWORDS.put(varKind, keyword);
	}

	public static Collection<IDescription> getAdditions(final Class clazz) {
		return ADDITIONS.get(clazz);
	}

	public static Map<String, OperatorProto> getAllFields(final Class clazz) {
		final List<Class> classes =
				JavaUtils.collectImplementationClasses(clazz, Collections.EMPTY_SET, FIELDS.keySet());
		final Map<String, OperatorProto> fieldsMap = new TOrderedHashMap();
		for (final Class c : classes) {
			for (final OperatorProto desc : FIELDS.get(c)) {
				fieldsMap.put(desc.getName(), desc);
			}
		}
		return fieldsMap;
	}

	public static Iterable<IDescription> getAllChildrenOf(final Class base,
			final Iterable<Class<? extends ISkill>> skills) {
		final List<Class> classes = JavaUtils.collectImplementationClasses(base, skills, ADDITIONS.keySet());
		return Iterables.concat(Iterables.transform(classes, INTO_DESCRIPTIONS));
	}

	public static Collection<OperatorProto> getAllFields() {
		return FIELDS.values();
	}

	public static Collection<IDescription> getAllVars() {
		final THashSet<IDescription> result = new THashSet<>();

		final DescriptionVisitor varVisitor = new DescriptionVisitor<VariableDescription>() {

			@Override
			public boolean visit(final VariableDescription desc) {
				result.add(desc);
				return true;
			}

		};

		final DescriptionVisitor actionVisitor = new DescriptionVisitor<StatementDescription>() {

			@Override
			public boolean visit(final StatementDescription desc) {
				Iterables.addAll(result, desc.getFormalArgs());
				return true;
			}

		};

		for (final TypeDescription desc : Types.getBuiltInSpecies()) {
			desc.visitOwnAttributes(varVisitor);
			desc.visitOwnActions(actionVisitor);

		}
		GamaSkillRegistry.INSTANCE.visitSkills(new DescriptionVisitor<SkillDescription>() {

			@Override
			public boolean visit(final SkillDescription desc) {
				desc.visitOwnAttributes(varVisitor);
				desc.visitOwnActions(actionVisitor);
				return true;
			}

		});

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

	public static Collection<IDescription> getAllActions() {
		final THashMap<String, IDescription> result = new THashMap<>();

		final DescriptionVisitor visitor = new DescriptionVisitor<StatementDescription>() {

			@Override
			public boolean visit(final StatementDescription desc) {
				result.putIfAbsent(desc.getName(), desc);
				return true;
			}

		};

		for (final TypeDescription s : Types.getBuiltInSpecies()) {
			s.visitOwnActions(visitor);
		}
		GamaSkillRegistry.INSTANCE.visitSkills(new DescriptionVisitor<SkillDescription>() {

			@Override
			public boolean visit(final SkillDescription desc) {
				desc.visitOwnActions(visitor);
				return true;
			}

		});
		return result.values();
	}

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
		if (!OPERATORS.containsKey(name)) { return false; }
		final Map<Signature, OperatorProto> map = OPERATORS.get(name);
		for (final Signature s : map.keySet()) {
			if (s.isUnary()) { return true; }
		}
		return false;
	}

}
