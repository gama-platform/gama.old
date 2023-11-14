/*******************************************************************************************************
 *
 * AbstractGamlAdditions.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.compilation;

import static msi.gama.common.interfaces.IKeyword.OF;
import static msi.gama.common.interfaces.IKeyword.SPECIES;
import static msi.gama.common.interfaces.IKeyword._DOT;
import static msi.gaml.compilation.kernel.GamaBundleLoader.CURRENT_PLUGIN_NAME;
import static msi.gaml.types.Types.builtInTypes;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import msi.gama.common.interfaces.IDisplayCreator;
import msi.gama.common.interfaces.IDisplayCreator.DisplayDescription;
import msi.gama.common.interfaces.IExperimentAgentCreator;
import msi.gama.common.interfaces.IExperimentAgentCreator.ExperimentAgentDescription;
import msi.gama.common.interfaces.IGui;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.file.IGamaFile;
import msi.gaml.compilation.kernel.GamaBundleLoader;
import msi.gaml.compilation.kernel.GamaMetaModel;
import msi.gaml.compilation.kernel.GamaSkillRegistry;
import msi.gaml.descriptions.FacetProto;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.descriptions.PrimitiveDescription;
import msi.gaml.descriptions.SymbolProto;
import msi.gaml.descriptions.VariableDescription;
import msi.gaml.factories.DescriptionFactory;
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

	/**
	 * Display.
	 *
	 * @param string
	 *            the string
	 * @param d
	 *            the d
	 */
	public void _display(final String string, final IDisplayCreator d) {
		GAML.CONSTANTS.add(string);
		IGui.DISPLAYS.put(string, new DisplayDescription(d, string, CURRENT_PLUGIN_NAME));
		GamaBundleLoader.addDisplayPlugin(CURRENT_PLUGIN_NAME);
	}

	/**
	 * Experiment.
	 *
	 * @param string
	 *            the string
	 * @param d
	 *            the d
	 */
	public void _experiment(final String string, final IExperimentAgentCreator d,
			final Class<? extends IExperimentAgent> clazz) {
		GAML.CONSTANTS.add(string);
		GamaMetaModel.INSTANCE.addExperimentAgentCreator(string,
				new ExperimentAgentDescription(d, clazz, string, CURRENT_PLUGIN_NAME));
	}

	/**
	 * Species.
	 *
	 * @param name
	 *            the name
	 * @param clazz
	 *            the clazz
	 * @param helper
	 *            the helper
	 * @param skills
	 *            the skills
	 */
	public void _species(final String name, final Class clazz, final IAgentConstructor helper, final String... skills) {
		GamaMetaModel.INSTANCE.addSpecies(name, clazz, helper, skills);
	}

	/**
	 * Type.
	 *
	 * @param keyword
	 *            the keyword
	 * @param typeInstance
	 *            the type instance
	 * @param id
	 *            the id
	 * @param varKind
	 *            the var kind
	 * @param wraps
	 *            the wraps
	 */
	protected void _type(final String keyword, final IType typeInstance, final int id, final int varKind,
			final Class... wraps) {
		final IType<?> type = builtInTypes.initType(keyword, typeInstance, id, varKind, wraps[0], CURRENT_PLUGIN_NAME);
		for (final Class cc : wraps) { Types.CLASSES_TYPES_CORRESPONDANCE.put(cc, type.getName()); }
		Types.cache(id, typeInstance);
		GAML.VARTYPE2KEYWORDS.put(varKind, keyword);
	}

	/**
	 * File.
	 *
	 * @param string
	 *            the string
	 * @param clazz
	 *            the clazz
	 * @param helper
	 *            the helper
	 * @param innerType
	 *            the inner type
	 * @param keyType
	 *            the key type
	 * @param contentType
	 *            the content type
	 * @param s
	 *            the s
	 */
	protected void _file(final String string, final Class clazz, final GamaGetter<IGamaFile<?, ?>> helper,
			final int innerType, final int keyType, final int contentType, final String[] s) {
		GamaFileType.addFileTypeDefinition(string, Types.get(innerType), Types.get(keyType), Types.get(contentType),
				clazz, helper, s, CURRENT_PLUGIN_NAME);
		GAML.VARTYPE2KEYWORDS.put(ISymbolKind.Variable.CONTAINER, string + "_file");
	}

	/**
	 * Skill.
	 *
	 * @param name
	 *            the name
	 * @param clazz
	 *            the clazz
	 * @param species
	 *            the species
	 */
	protected void _skill(final String name, final Class clazz, final String... species) {
		GamaSkillRegistry.INSTANCE.register(name, clazz, CURRENT_PLUGIN_NAME, GAML.ADDITIONS.get(clazz), species);
	}

	/**
	 * Symbol.
	 *
	 * @param names
	 *            the names
	 * @param c
	 *            the c
	 * @param sKind
	 *            the s kind
	 * @param isRemoteContext
	 *            the remote
	 * @param hasArguments
	 *            the args
	 * @param scope
	 *            the scope
	 * @param isSequence
	 *            the sequence
	 * @param isUnique
	 *            the unique
	 * @param name_unique
	 *            the name unique
	 * @param contextKeywords
	 *            the context keywords
	 * @param contextKinds
	 *            the context kinds
	 * @param fmd
	 *            the fmd
	 * @param omissible
	 *            the omissible
	 * @param sc
	 *            the sc
	 */
	protected void _symbol(final String[] names, final Class c, final int sKind, final boolean isBreakable,
			final boolean isContinuable, final boolean isRemoteContext, final boolean hasArguments, final boolean scope,
			final boolean isSequence, final boolean isUnique, final boolean name_unique, final String[] contextKeywords,
			final int[] contextKinds, final FacetProto[] fmd, final String omissible, final ISymbolConstructor sc) {
		final Collection<String> keywords;
		if (ISymbolKind.Variable.KINDS.contains(sKind)) {
			keywords = GAML.VARTYPE2KEYWORDS.get(sKind);
			keywords.remove(SPECIES);
		} else {
			keywords = Arrays.asList(names);
		}
		final SymbolProto md = new SymbolProto(c, isBreakable, isContinuable, isSequence, hasArguments, sKind, !scope,
				fmd, omissible, contextKeywords, contextKinds, isRemoteContext, isUnique, name_unique, sc,
				names == null || names.length == 0 ? "variable declaration" : names[0], CURRENT_PLUGIN_NAME);
		DescriptionFactory.addProto(md, keywords);
	}

	/**
	 * Operator.
	 *
	 * @param keywords
	 *            the keywords
	 * @param method
	 *            the method
	 * @param expectedContentTypes
	 *            the expected content types
	 * @param returnClassOrType
	 *            the return class or type
	 * @param c
	 *            the c
	 * @param t
	 *            the t
	 * @param content
	 *            the content
	 * @param index
	 *            the index
	 * @param contentContentType
	 *            the content content type
	 * @param helper
	 *            the helper
	 */
	public void _operator(final String[] keywords, final Executable method, final int[] expectedContentTypes,
			final Object returnClassOrType, final boolean c, final int t, final int content, final int index,
			final int contentContentType, final GamaGetter helper, final boolean isIterator) {
		if (isIterator) { Collections.addAll(GAML.ITERATORS, keywords); }
		final Signature signature = new Signature(method);
		int nbParameters = signature.size();
		final String plugin = GamaBundleLoader.CURRENT_PLUGIN_NAME;
		final IType rt;
		if (returnClassOrType instanceof Class) {
			rt = Types.get((Class) returnClassOrType);
		} else {
			rt = (IType) returnClassOrType;
		}
		for (final String keyword : keywords) {
			final String kw = keyword;
			if (!GAML.OPERATORS.containsKey(kw)) { GAML.OPERATORS.put(kw, GamaMapFactory.createUnordered()); }
			final Map<Signature, OperatorProto> map = GAML.OPERATORS.get(kw);
			if (!map.containsKey(signature)) {
				OperatorProto proto;
				if (nbParameters == 2 && (OF.equals(kw) || _DOT.equals(kw)) && signature.get(0).isAgentType()) {
					proto = new OperatorProto(kw, method, helper, c, true, rt, signature, t, content, index,
							contentContentType, expectedContentTypes, plugin);
				} else {
					proto = new OperatorProto(kw, method, helper, c, false, rt, signature, t, content, index,
							contentContentType, expectedContentTypes, plugin);
				}
				map.put(signature, proto);
			}
		}

	}

	/**
	 * Listener.
	 *
	 * @param varName
	 *            the var name
	 * @param clazz
	 *            the clazz
	 * @param helper
	 *            the helper
	 */
	public void _listener(final String varName, final Class clazz, final IGamaHelper helper) {
		GamaHelper gh = new GamaHelper(varName, clazz, helper);
		GAML.LISTENERS_BY_CLASS.put(clazz, gh);
		GAML.LISTENERS_BY_NAME.put(varName, clazz);
	}

	/**
	 * Operator.
	 *
	 * @param keywords
	 *            the keywords
	 * @param method
	 *            the method
	 * @param classes
	 *            the classes
	 * @param expectedContentTypes
	 *            the expected content types
	 * @param ret
	 *            the ret
	 * @param c
	 *            the c
	 * @param typeAlias
	 *            the type alias
	 * @param helper
	 *            the helper
	 */
	// For files
	public void _operator(final String[] keywords, final Executable method, final int content,
			final int[] expectedContentTypes, final Class ret, final boolean c, final String typeAlias,
			final GamaGetter helper) {
		final ParametricFileType fileType = GamaFileType.getTypeFromAlias(typeAlias);
		this._operator(keywords, method, expectedContentTypes, fileType, c, ITypeProvider.NONE, content,
				ITypeProvider.NONE, ITypeProvider.NONE, helper, false);
	}

	/**
	 * Var.
	 *
	 * @param clazz
	 *            the clazz
	 * @param desc
	 *            the desc
	 * @param get
	 *            the get
	 * @param init
	 *            the init
	 * @param set
	 *            the set
	 */
	protected void _var(final Class clazz, final IDescription desc, final IGamaHelper get, final IGamaHelper init,
			final IGamaHelper set) {
		GAML.ADDITIONS.put(clazz, desc);
		VariableDescription vd = (VariableDescription) desc;
		vd.addHelpers(clazz, get, init, set);
		vd.setDefiningPlugin(GamaBundleLoader.CURRENT_PLUGIN_NAME);
		vd.setDefinitionClass(clazz);
	}

	/**
	 * Facet.
	 *
	 * @param name
	 *            the name
	 * @param types
	 *            the types
	 * @param ct
	 *            the ct
	 * @param kt
	 *            the kt
	 * @param values
	 *            the values
	 * @param optional
	 *            the optional
	 * @param internal
	 *            the internal
	 * @param isRemote
	 *            the is remote
	 * @return the facet proto
	 */
	protected FacetProto _facet(final String name, final int[] types, final int ct, final int kt, final String[] values,
			final boolean optional, final boolean internal, final boolean isRemote) {
		return new FacetProto(name, types, ct, kt, values, optional, internal, isRemote);
	}

	/**
	 * Field.
	 *
	 * @param clazz
	 *            the clazz
	 * @param getter
	 *            the getter
	 */
	protected void _field(final Class clazz, final String name, final GamaGetter helper, final int returnType,
			final Class signature, final int typeProvider, final int contentTypeProvider, final int keyTypeProvider) {
		GAML.FIELDS.put(clazz, new OperatorProto(name, null, helper, false, true, returnType, signature, typeProvider,
				contentTypeProvider, keyTypeProvider, AI));
	}

	/**
	 * Constants.
	 *
	 * @param strings
	 *            the strings
	 */
	public static void _constants(final String[]... strings) {
		for (final String[] s : strings) { Collections.addAll(GAML.CONSTANTS, s); }
	}

	/**
	 * Arg.
	 *
	 * @param name
	 *            the name
	 * @param type
	 *            the type
	 * @param optional
	 *            the optional
	 * @return the i description
	 */
	public IDescription _arg(final String name, final int type, final boolean optional) {
		// For IDs and labels, we add a specific facet (ID) -- see #3627
		if (type <= IType.LABEL) return desc(IKeyword.ARG, IKeyword.NAME, name, IKeyword.ID, "true", IKeyword.TYPE,
				String.valueOf(type), IKeyword.OPTIONAL, optional ? "true" : "false");
		return desc(IKeyword.ARG, IKeyword.NAME, name, IKeyword.TYPE, String.valueOf(type), IKeyword.OPTIONAL,
				optional ? "true" : "false");
	}

	/**
	 * Action.
	 *
	 * @param e
	 *            the e
	 * @param desc
	 *            the desc
	 * @param method
	 *            the method
	 */
	protected void _action(final IGamaHelper e, final IDescription desc, final Method method) {
		((PrimitiveDescription) desc).setHelper(e, method);
		((PrimitiveDescription) desc).setDefiningPlugin(GamaBundleLoader.CURRENT_PLUGIN_NAME);
		GAML.ADDITIONS.put(method.getDeclaringClass(), desc);
	}

}
