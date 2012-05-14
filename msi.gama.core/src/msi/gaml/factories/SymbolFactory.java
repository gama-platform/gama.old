/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.factories;

import static msi.gama.common.interfaces.IKeyword.*;
import static msi.gaml.factories.DescriptionValidator.verifyFacetsType;
import java.lang.reflect.Constructor;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.base;
import msi.gama.precompiler.GamlAnnotations.combination;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.handles;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.no_scope;
import msi.gama.precompiler.GamlAnnotations.remote_context;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.uses;
import msi.gama.precompiler.GamlAnnotations.with_args;
import msi.gama.precompiler.GamlAnnotations.with_sequence;
import msi.gama.precompiler.*;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.commands.*;
import msi.gaml.commands.Facets.Facet;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpressionFactory;

/**
 * Written by Alexis Drogoul Modified on 11 mai 2010
 * 
 * @todo Description
 * 
 */
@handles({ ISymbolKind.ENVIRONMENT, ISymbolKind.ABSTRACT_SECTION })
public class SymbolFactory implements ISymbolFactory {

	protected final Map<String, SymbolMetaDescription> registeredSymbols = new LinkedHashMap();
	protected final Map<String, ISymbolFactory> registeredFactories = new LinkedHashMap();
	protected String name;

	public SymbolFactory(final ISymbolFactory superFactory) {
		name =
			superFactory == null ? getClass().getSimpleName() : superFactory.getName() + ">" +
				getClass().getSimpleName();
		registerAnnotatedFactories();
		registerAnnotatedSymbols();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IExpressionFactory getExpressionFactory() {
		return GAMA.getExpressionFactory();
	}

	private void registerAnnotatedFactories() {
		uses annot = getClass().getAnnotation(uses.class);
		if ( annot == null ) { return; }
		for ( int kind : annot.value() ) {
			Class c = GamlCompiler.getFactoriesByKind(kind);
			if ( canRegisterFactory(c) ) {
				Constructor cc = null;
				try {
					cc = c.getConstructor(ISymbolFactory.class);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				if ( cc == null ) { return; }
				SymbolFactory fact;
				try {
					fact = (SymbolFactory) cc.newInstance(this);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				registerFactory(fact);
			}
		}
	}

	/**
	 * @param c
	 * @return
	 */
	private boolean canRegisterFactory(final Class c) {
		for ( ISymbolFactory sf : registeredFactories.values() ) {
			if ( sf.getClass() == c ) { return false; }
		}
		return true;
	}

	protected void registerAnnotatedSymbols() {
		handles annot = getClass().getAnnotation(handles.class);
		if ( annot == null ) { return; }
		for ( int c : annot.value() ) {
			List<Class> classes = GamlCompiler.getClassesByKind().get(c);
			for ( Class clazz : classes ) {
				register(clazz);
			}
		}
	}

	@Override
	public Set<String> getKeywords() {
		return new HashSet(registeredSymbols.keySet());
		// Necessary to copy, since this list can be modified dynamically (esp. by SpeciesFactory)
	}

	public boolean registerFactory(final ISymbolFactory f) {
		for ( ISymbolFactory sf : registeredFactories.values() ) {
			if ( sf.getClass() == f.getClass() ) { return false; }
		}
		// availableFactories.add(f);
		// Does a pre-registration of the keywords
		for ( String s : f.getKeywords() ) {
			registeredFactories.put(s, f);
		}
		return true;
	}

	static final Set<Integer> varKinds = new HashSet(Arrays.asList(ISymbolKind.Variable.CONTAINER,
		ISymbolKind.Variable.NUMBER, ISymbolKind.Variable.REGULAR, ISymbolKind.Variable.SIGNAL));

	public void register(final Class c) {
		Class baseClass = null;
		String omissible = null;
		symbol sym = (symbol) c.getAnnotation(symbol.class);
		int sKind = sym.kind();
		// boolean isVariable = sym.kind() == ISymbolKind.VARIABLE;
		boolean canHaveArgs = c.getAnnotation(with_args.class) != null;
		boolean canHaveSequence = c.getAnnotation(with_sequence.class) != null;
		boolean doesNotHaveScope = c.getAnnotation(no_scope.class) != null;
		boolean isRemoteContext = c.getAnnotation(remote_context.class) != null;
		if ( c.getAnnotation(base.class) != null ) {
			baseClass = ((base) c.getAnnotation(base.class)).value();
		}
		List<String> keywords = new ArrayList(Arrays.asList(sym.name()));
		List<facet> facets = new ArrayList();
		List<combination> combinations = new ArrayList();
		List<String> contexts = new ArrayList();
		facets ff = (facets) c.getAnnotation(facets.class);
		if ( ff != null ) {
			facets = Arrays.asList(ff.value());
			combinations = Arrays.asList(ff.combinations());
			omissible = ff.omissible();

		}
		if ( c.getAnnotation(inside.class) != null ) {
			inside parents = (inside) c.getAnnotation(inside.class);
			for ( String p : parents.symbols() ) {
				contexts.add(p);
			}
			for ( int kind : parents.kinds() ) {
				List<Class> classes = GamlCompiler.getClassesByKind().get(kind);
				for ( Class clazz : classes ) {
					symbol names = (symbol) clazz.getAnnotation(symbol.class);
					if ( names != null ) {
						for ( String name : names.name() ) {
							contexts.add(name);
						}
					}
				}
			}
		}
		contexts = new ArrayList(new HashSet(contexts));
		if ( varKinds.contains(sKind) ) {
			GamlProperties gp = GamlProperties.loadFrom(GamlProperties.TYPES);
			Set<String> supplementary_keywords = new HashSet();
			for ( Map.Entry<String, LinkedHashSet<String>> entry : gp.entrySet() ) {
				List<String> values = new ArrayList(entry.getValue());
				if ( String.valueOf(sKind).equals(values.get(0)) ) {
					supplementary_keywords.add(values.get(1));
				}
			}
			keywords.addAll(supplementary_keywords);
			// Special trick and workaround for compiling species rather than variables
			keywords.remove(IKeyword.SPECIES);
		}
		for ( String k : keywords ) {
			// try {
			if ( !varKinds.contains(sKind) ) {
				SymbolMetaDescription.nonVariableStatements.add(k);
			}
			registeredSymbols.put(k, new SymbolMetaDescription(c, baseClass, k, canHaveSequence,
				canHaveArgs, sKind, doesNotHaveScope, facets, omissible, combinations, contexts,
				isRemoteContext));

		}
	}

	@Override
	public final SymbolMetaDescription getMetaDescriptionFor(final IDescription context,
		final String keyword) {
		SymbolMetaDescription md = registeredSymbols.get(keyword);
		if ( md == null ) {
			String upper = context == null ? null : context.getKeyword();
			ISymbolFactory f = chooseFactoryFor(keyword, upper);
			if ( f == null ) {
				if ( context != null ) {
					context.flagError("Unknown symbol " + keyword, IGamlIssue.UNKNOWN_KEYWORD,
						null, keyword, upper);
				}
				return null;
			}
			return f.getMetaDescriptionFor(context, keyword);
		}
		return md;
	}

	@Override
	public String getOmissibleFacetForSymbol(final String keyword) {
		SymbolMetaDescription md = getMetaDescriptionFor(null, keyword);
		return md == null ? IKeyword.NAME /* by default */: md.getOmissible();

	}

	protected String getKeyword(final ISyntacticElement cur) {
		return cur.getKeyword();
	}

	/**
	 * Creates a semantic description based on a source element, a super-description, and a --
	 * possibly null -- list of children. In this method, the children of the source element are not
	 * considered, so if "children" is null or empty, the description is created without children.
	 * @see msi.gaml.factories.ISymbolFactory#createDescription(msi.gama.common.interfaces.ISyntacticElement,
	 *      msi.gaml.descriptions.IDescription, java.util.List)
	 */

	@Override
	public final IDescription createDescription(final ISyntacticElement source,
		final IDescription superDesc, final List<IDescription> children) {
		String keyword = getKeyword(source);
		SymbolFactory f = chooseFactoryFor(keyword, null);
		if ( f == null ) {
			superDesc.flagError("Impossible to parse keyword " + keyword + " in this context",
				IGamlIssue.UNKNOWN_KEYWORD, source, keyword);
			return null;
		}
		SymbolMetaDescription md = f.getMetaDescriptionFor(superDesc, keyword);
		return f.createDescriptionInternal(keyword, source, superDesc, children, md);
	}

	private IDescription createDescriptionInternal(final String keyword,
		final ISyntacticElement source, final IDescription superDesc,
		final List<IDescription> children, final SymbolMetaDescription md) {
		List<IDescription> commandList = children == null ? new ArrayList() : children;
		md.verifyFacets(source, source.getFacets(), superDesc);
		IDescription desc = buildDescription(source, keyword, commandList, superDesc, md);
		desc.getSourceInformation().setDescription(desc);
		return desc;
	}

	/**
	 * Creates a semantic description based on a source element and a super-description. The
	 * children of the source element are used as a basis for building, recursively, the semantic
	 * tree.
	 * @see msi.gaml.factories.ISymbolFactory#createDescriptionRecursively(msi.gama.common.interfaces.ISyntacticElement,
	 *      msi.gaml.descriptions.IDescription)
	 */
	@Override
	public final IDescription createDescriptionRecursively(final ISyntacticElement source,
		final IDescription superDesc) {
		if ( source == null ) { return null; }
		String keyword = getKeyword(source);
		String context = superDesc == null ? null : superDesc.getKeyword();
		SymbolFactory f = chooseFactoryFor(keyword, context);
		if ( f == null ) {
			if ( superDesc != null ) {
				superDesc.flagError("Impossible to parse keyword " + keyword,
					IGamlIssue.UNKNOWN_KEYWORD, source, keyword, context);
			}
			return null;
		}
		SymbolMetaDescription md = f.getMetaDescriptionFor(superDesc, keyword);
		return f.createDescriptionRecursivelyInternal(keyword, source, superDesc, md);
	}

	private IDescription createDescriptionRecursivelyInternal(final String keyword,
		final ISyntacticElement source, final IDescription superDesc, final SymbolMetaDescription md) {
		Facets facets = source.getFacets();
		md.verifyFacets(source, facets, superDesc);
		List<IDescription> children = new ArrayList();

		for ( ISyntacticElement e : source.getChildren() ) {
			// Instead of having to consider this specific case, find a better solution.
			if ( !source.getKeyword().equals(SPECIES) ) {
				children.add(createDescriptionRecursively(e, superDesc));
			} else if ( source.hasParent(DISPLAY) || source.hasParent(SPECIES) ) {
				// "species" declared in "display" or "species" section
				children.add(createDescriptionRecursively(e, superDesc));
			}
		}

		IDescription desc = buildDescription(source, keyword, children, superDesc, md);
		desc.getSourceInformation().setDescription(desc);
		return desc;
	}

	@Override
	public SymbolFactory chooseFactoryFor(final String keyword) {
		if ( registeredSymbols.containsKey(keyword) ) { return this; }
		SymbolFactory fact = (SymbolFactory) registeredFactories.get(keyword);
		if ( fact == null ) {
			for ( ISymbolFactory f : registeredFactories.values() ) {
				SymbolFactory f2 = (SymbolFactory) f.chooseFactoryFor(keyword);
				if ( f2 != null ) { return f2; }
			}
		}
		return fact;
	}

	protected SymbolFactory chooseFactoryFor(final String keyword, final String context) {
		SymbolFactory contextFactory = context != null ? chooseFactoryFor(context) : this;
		if ( contextFactory == null ) {
			contextFactory = this;
		}
		return contextFactory.chooseFactoryFor(keyword);
	}

	protected IDescription buildDescription(final ISyntacticElement source, final String keyword,
		final List<IDescription> children, final IDescription superDesc,
		final SymbolMetaDescription md) {
		return new SymbolDescription(keyword, superDesc, children, source, md);
	}

	@Override
	public final ISymbol compileDescription(final IDescription desc) {
		IDescription superDesc = desc.getSuperDescription();
		SymbolFactory f =
			chooseFactoryFor(desc.getKeyword(), superDesc == null ? null : superDesc.getKeyword());
		if ( f == null ) {
			desc.flagError("Impossible to compile keyword " + desc.getKeyword(),
				IGamlIssue.UNKNOWN_KEYWORD, null, desc.getKeyword());
			return null;
		}
		return f.privateCompile(desc);
	}

	@Override
	public final void validateDescription(final IDescription desc) {
		IDescription superDesc = desc.getSuperDescription();
		SymbolFactory f =
			chooseFactoryFor(desc.getKeyword(), superDesc == null ? null : superDesc.getKeyword());
		if ( f == null ) {
			desc.flagError("Impossible to validate keyword " + desc.getKeyword(),
				IGamlIssue.UNKNOWN_KEYWORD, null, desc.getKeyword());
			return;
		}
		f.privateValidate(desc);
	}

	protected void privateValidate(final IDescription desc) {
		SymbolMetaDescription md = desc.getMeta();
		if ( md == null ) { return; }
		Facets rawFacets = desc.getFacets();
		// Validation of the facets (through their compilation)
		rawFacets.putAsLabel(KEYWORD, desc.getKeyword());
		for ( Facet f : rawFacets.entrySet() ) {
			if ( f == null ) {
				continue;
			}
			compileFacet(f.getKey(), desc);
		}
		verifyFacetsType(desc);
		if ( md.hasSequence() && !desc.getKeyword().equals(PRIMITIVE) ) {
			if ( md.isRemoteContext() ) {
				desc.copyTempsAbove();
			}
			privateValidateChildren(desc);
		}
	}

	protected void privateValidateChildren(final IDescription desc) {
		for ( IDescription sd : desc.getChildren() ) {
			validateDescription(sd);
		}
	}

	protected void compileFacet(final String tag, final IDescription sd) {
		try {
			IExpressionDescription ed = sd.getFacets().get(tag);
			if ( ed == null ) { return; }
			ed.compile(sd);
		} catch (GamaRuntimeException e) {
			e.printStackTrace();
		}
	}

	protected final ISymbol privateCompile(final IDescription desc) {
		SymbolMetaDescription md = desc.getMeta();
		if ( md == null ) { return null; }
		Facets rawFacets = desc.getFacets();
		// Addition of a facet to keep track of the keyword
		rawFacets.putAsLabel(KEYWORD, desc.getKeyword());
		for ( Facet f : rawFacets.entrySet() ) {
			if ( f != null ) {
				compileFacet(f.getKey(), desc);
			}
		}
		ISymbol cs = md.getConstructor().create(desc);
		if ( cs == null ) { return null; }
		if ( md.hasArgs() ) {
			((ICommand.WithArgs) cs).setFormalArgs(privateCompileArgs((CommandDescription) desc));
		}
		if ( md.hasSequence() && !desc.getKeyword().equals(PRIMITIVE) ) {
			if ( md.isRemoteContext() ) {
				desc.copyTempsAbove();
			}
			cs.setChildren(privateCompileChildren(desc));
		}
		return cs;

	}

	/**
	 * @param desc
	 * @return
	 */
	protected Arguments privateCompileArgs(final CommandDescription desc) {
		return new Arguments();
	}

	protected List<ISymbol> privateCompileChildren(final IDescription desc) {
		List<ISymbol> lce = new ArrayList();
		for ( IDescription sd : desc.getChildren() ) {
			ISymbol s = compileDescription(sd);
			if ( s != null ) {
				lce.add(s);
			}
		}
		return lce;
	}

}
