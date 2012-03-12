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

import java.lang.reflect.Constructor;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.ErrorCollector;
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
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.commands.Facets;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.*;

/**
 * Written by Alexis Drogoul Modified on 11 mai 2010
 * 
 * @todo Description
 * 
 */
@handles({ ISymbolKind.ENVIRONMENT, ISymbolKind.ABSTRACT_SECTION })
public class SymbolFactory implements ISymbolFactory {

	protected Map<String, SymbolMetaDescription> registeredSymbols = new HashMap();

	@Override
	public IExpressionFactory getDefaultExpressionFactory() {
		return (IExpressionFactory) chooseFactoryFor(IKeyword.GAML);
	}

	protected final Set<ISymbolFactory> registeredFactories = new HashSet();

	public SymbolFactory() {
		registerAnnotatedFactories();
		registerAnnotatedSymbols();
	}

	private void registerAnnotatedFactories() {
		uses annot = getClass().getAnnotation(uses.class);
		if ( annot == null ) { return; }
		for ( int kind : annot.value() ) {
			Class c = GamlCompiler.getFactoriesByKind(kind);
			Constructor cc = null;
			try {
				cc = c.getConstructor();
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			if ( cc == null ) { return; }
			SymbolFactory fact;
			try {
				fact = (SymbolFactory) cc.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			registerFactory(fact);
		}
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

	public void registerFactory(final ISymbolFactory f) {
		registeredFactories.add(f);

		// OutputManager.debug(this.getClass().getSimpleName() + " registers factory " +
		// f.getClass().getSimpleName() + " for keywords " + f.getKeywords());

	}

	public void register(final Class c) {
		Class baseClass = null;
		String omissible = null;
		// GUI.debug(c.getSimpleName() + " registered in " + getClass().getSimpleName());
		boolean isTopLevel =
			((symbol) c.getAnnotation(symbol.class)).kind() == ISymbolKind.BEHAVIOR;
		boolean canHaveArgs = c.getAnnotation(with_args.class) != null;
		boolean canHaveSequence = c.getAnnotation(with_sequence.class) != null;
		boolean doesNotHaveScope = c.getAnnotation(no_scope.class) != null;
		boolean isRemoteContext = c.getAnnotation(remote_context.class) != null;
		if ( c.getAnnotation(base.class) != null ) {
			baseClass = ((base) c.getAnnotation(base.class)).value();
		}
		List<String> keywords = Arrays.asList(((symbol) c.getAnnotation(symbol.class)).name());
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

		for ( String k : keywords ) {
			// try {
			registeredSymbols.put(k, new SymbolMetaDescription(c, baseClass, k, canHaveSequence,
				canHaveArgs, isTopLevel, doesNotHaveScope, facets, omissible, combinations,
				contexts, isRemoteContext));
			// } catch (GamlException e) {
			// e.addContext("In compiling the meta description of: " + k);
			// e.printStackTrace();
			// }
		}
	}

	@Override
	public SymbolMetaDescription getMetaDescriptionFor(final IDescription desc, final String keyword) {
		SymbolMetaDescription md = registeredSymbols.get(keyword);
		if ( md == null ) {
			ISymbolFactory f = chooseFactoryFor(keyword, getKeyword(desc));
			if ( f == null ) {
				if ( desc != null ) {
					desc.flagError(new GamlException("Unknown symbol " + keyword, desc
						.getSourceInformation()));
				}
				return null;
			}
			return f.getMetaDescriptionFor(desc, keyword);
		}
		return md;
	}

	@Override
	public String getOmissibleFacetForSymbol(final ISyntacticElement e, final String keyword) {
		SymbolMetaDescription md;
		md = getMetaDescriptionFor(null, keyword);
		return md == null ? IKeyword.NAME /* by default */: md.getOmissible();

	}

	protected String getKeyword(final IDescription desc) {
		if ( desc == null ) { return null; }
		return desc.getKeyword();
	}

	protected String getKeyword(final ISyntacticElement cur) {
		return cur.getName();
	}

	public IDescription createSymbolDescription(final ISyntacticElement cur,
		final IDescription superDesc, final String ... additionalFacets) {
		int n = additionalFacets.length;
		for ( int i = 0; i < n; i += 2 ) {
			cur.setAttribute(additionalFacets[i], additionalFacets[i + 1], null);
		}
		return createDescription(cur, superDesc);
	}

	@Override
	public IDescription createDescription(final ISyntacticElement cur, final IDescription superDesc) {
		if ( cur == null ) { return null; }
		String keyword = getKeyword(cur);

		String context = null;
		if ( superDesc != null ) {
			context = superDesc.getKeyword();
		}

		ISymbolFactory f = chooseFactoryFor(keyword, context);
		if ( f == null ) {
			if ( superDesc != null ) {
				superDesc
					.flagError(new GamlException("Impossible to parse keyword " + keyword, cur));
			}
			return null;
		}
		if ( f != this ) { return f.createDescription(cur, superDesc); }

		ISyntacticElement source = cur;

		SymbolMetaDescription md = getMetaDescriptionFor(superDesc, keyword);
		Facets facets = new Facets();
		Map<String, String> attributes = cur.getAttributes();
		for ( Map.Entry<String, String> a : attributes.entrySet() ) {
			facets.put(a.getKey(), a.getValue());
		}
		if ( md != null ) {
			try {
				md.verifyFacets(source, facets);
			} catch (GamlException e1) {
				if ( superDesc != null ) {
					superDesc.flagError(e1);
				}
				return null;
			}
		}
		List<IDescription> commands = new ArrayList();

		for ( ISyntacticElement e : cur.getChildren() ) {
			// Instead of having to consider this specific case, find a better solution.
			
			if (IKeyword.SPECIES.equals(e.getName())) {
				System.out.println();
			}

			if ( !cur.getName().equals(IKeyword.SPECIES) ) {
				commands.add(createDescription(e, superDesc));
			} else if ( cur.hasParent(IKeyword.DISPLAY) || cur.hasParent(IKeyword.SPECIES) ) { // "species" declared in "display" or "species" section
				commands.add(createDescription(e, superDesc));
			}
		}

		return buildDescription(source, keyword, commands, facets, superDesc, md);
	}

	@Override
	public boolean handlesKeyword(final String keyword) {
		return registeredSymbols.containsKey(keyword);
	}

	@Override
	public ISymbolFactory chooseFactoryFor(final String keyword) {
		if ( handlesKeyword(keyword) ) { return this; }
		for ( ISymbolFactory f : registeredFactories ) {
			if ( f.handlesKeyword(keyword) ) { return f; }
		}
		for ( ISymbolFactory f : registeredFactories ) {
			ISymbolFactory f2 = f.chooseFactoryFor(keyword);
			if ( f2 != null ) { return f2; }
		}
		return null;
	}

	//
	// @Override
	// public ISymbolFactory chooseFactoryFor(final String keyword) {
	// if ( registeredSymbols.containsKey(keyword) ) { return this; }
	// for ( ISymbolFactory f : registeredFactories.keySet() ) {
	// if ( registeredFactories.get(f).contains(keyword) ) { return f; }
	// }
	// for ( ISymbolFactory f : registeredFactories.keySet() ) {
	// ISymbolFactory f2 = f.chooseFactoryFor(keyword);
	// if ( f2 != null ) { return f2; }
	// }
	// return null;
	// }

	protected ISymbolFactory chooseFactoryFor(final String keyword, final String context) {
		ISymbolFactory contextFactory = context != null ? chooseFactoryFor(context) : this;
		if ( contextFactory == null ) {
			contextFactory = this;
		}
		return contextFactory.chooseFactoryFor(keyword);
	}

	@Override
	public IDescription createDescription(final ISyntacticElement cur,
		final IDescription superDesc, final List<IDescription> children, final String ... strings) {
		String keyword = strings[0];
		ISymbolFactory f = chooseFactoryFor(keyword, null);
		if ( f == null ) {
			superDesc.flagError(new GamlException("Impossible to parse keyword " + keyword,
				superDesc.getSourceInformation()));
			return null;
		}
		if ( f != this ) { return f.createDescription(cur, superDesc, children, strings); }
		List<IDescription> commandList;
		commandList = children == null ? new ArrayList() : children;
		Facets facets = new Facets();
		SymbolMetaDescription md;
		try {
			md = getMetaDescriptionFor(superDesc, keyword);
			facets.addAll(strings);
			md.verifyFacets(null, facets);
		} catch (GamlException e) {
			superDesc.flagError(e);
			return null;
		}
		return buildDescription(cur, keyword, commandList, facets, superDesc, md);
	}

	protected IDescription buildDescription(final ISyntacticElement source, final String keyword,
		final List<IDescription> commands, final Facets facets, final IDescription superDesc,
		final SymbolMetaDescription md) {
		return new SymbolDescription(keyword, superDesc, facets, commands, source, md);
	}

	@Override
	public ISymbol compileDescription(final IDescription desc, final IExpressionFactory factory) {
		IDescription superDesc = desc.getSuperDescription();
		ISymbolFactory f =
			chooseFactoryFor(desc.getKeyword(), superDesc == null ? null : superDesc.getKeyword());
		if ( f == null ) {
			desc.flagError(new GamlException("Impossible to compile keyword " + desc.getKeyword(),
				desc.getSourceInformation()));
			return null;
		}
		if ( f != this ) { return f.compileDescription(desc, factory); }
		SymbolMetaDescription md = getMetaDescriptionFor(desc, desc.getKeyword());
		return privateCompile(desc, md, factory);
	}

	protected Facets compileFacets(final IDescription sd, final SymbolMetaDescription md,
		final IExpressionFactory factory) {
		Facets rawFacets = sd.getFacets();
		// Addition of a facet to keep track of the keyword
		rawFacets.putAsLabel(IKeyword.KEYWORD, sd.getKeyword());

		for ( String s : new ArrayList<String>(rawFacets.keySet()) ) {
			IExpression e;
			try {
				e = compileFacet(s, sd, md, factory);
				rawFacets.put(s, e);
			} catch (GamlException e1) {
				sd.flagError(e1);
			}

		}
		return rawFacets;
	}

	protected IExpression compileFacet(final String tag, final IDescription sd,
		final SymbolMetaDescription md, final IExpressionFactory factory) throws GamlException,
		GamaRuntimeException {
		if ( md.isLabel(tag) ) { return sd.getFacets().compileAsLabel(tag); }
		return sd.getFacets().compile(tag, sd, factory);
	}

	protected ISymbol privateCompile(final IDescription desc, final SymbolMetaDescription md,
		final IExpressionFactory factory) {
		if ( md == null ) { return null; }
		compileFacets(desc, md, factory);
		ISymbol cs;
		try {
			cs = compileSymbol(desc, md.getConstructor());
		} catch (GamlException e) {
			desc.flagError(e);
			return null;
		}
		if ( md.hasSequence() ) {
			if ( md.isRemoteContext() ) {
				desc.copyTempsAbove();
			}
			privateCompileChildren(desc, cs, factory);
		}
		return cs;

	}

	protected ISymbol compileSymbol(final IDescription desc, final ISymbolConstructor c)
		throws GamlException, GamaRuntimeException {
		ISymbol cs = c.create(desc);
		return cs;
	}

	protected void privateCompileChildren(final IDescription desc, final ISymbol cs,
		final IExpressionFactory factory) {
		List<ISymbol> lce = new ArrayList();
		for ( IDescription sd : desc.getChildren() ) {
			ISymbol s = compileDescription(sd, factory);
			if ( s != null ) {
				lce.add(s);
			}
		}
		try {
			cs.setChildren(lce);
		} catch (GamlException e) {
			desc.flagError(e);
		}
	}

	protected String[] convertTags(final ISyntacticElement e) {
		Map<String, String> attributes = e.getAttributes();
		String[] result = new String[(attributes.size() << 1) + 1];
		result[0] = getKeyword(e);
		int i = 1;
		for ( Map.Entry<String, String> a : attributes.entrySet() ) {
			result[i] = a.getKey();
			result[i + 1] = a.getValue();
			i += 2;
		}
		return result;
	}

	@Override
	public ISymbol compile(final ModelStructure struct, final ErrorCollector collect)
		throws InterruptedException {
		return null;
	}

}
