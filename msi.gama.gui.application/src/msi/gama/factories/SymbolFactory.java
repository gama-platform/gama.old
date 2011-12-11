/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.factories;

import java.lang.reflect.Constructor;
import java.util.*;
import msi.gama.gui.application.Activator;
import msi.gama.interfaces.*;
import msi.gama.internal.compilation.ISymbolConstructor;
import msi.gama.internal.descriptions.*;
import msi.gama.internal.expressions.*;
import msi.gama.kernel.exceptions.*;
import msi.gama.lang.utils.ISyntacticElement;
import msi.gama.precompiler.*;
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

/**
 * Written by Alexis Drogoul Modified on 11 mai 2010
 * 
 * @todo Description
 * 
 */
@handles({ ISymbolKind.ENVIRONMENT })
public class SymbolFactory implements ISymbolFactory {

	public static Map<Integer, List<Class>> CLASSES_BY_KIND = new HashMap();
	public static Map<Integer, Class> FACTORIES_BY_KIND = new HashMap();
	public static Map<String, String> KEYWORD_TAGS = new HashMap();

	static {
		MultiProperties mp = new MultiProperties();
		try {
			mp = Activator.getGamaProperties(GamaProcessor.KINDS);
		} catch (GamlException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		}
		for ( String ks : mp.keySet() ) {
			Integer i = Integer.decode(ks);
			CLASSES_BY_KIND.put(i, new ArrayList());
			for ( String className : mp.get(ks) ) {
				try {
					CLASSES_BY_KIND.get(i).add(Class.forName(className));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			mp = Activator.getGamaProperties(GamaProcessor.FACTORIES);
		} catch (GamlException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		}
		for ( String ks : mp.keySet() ) {
			Integer i = Integer.decode(ks);
			String factory_name = new ArrayList<String>(mp.get(ks)).get(0);
			try {
				FACTORIES_BY_KIND.put(i, Class.forName(factory_name));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		// KEYWORD_TAGS.put(ISymbol.SPECIES, ISpecies.CONTROL);
		KEYWORD_TAGS.put(ISymbol.VAR, ISymbol.TYPE);
		KEYWORD_TAGS.put(ISymbol.METHOD, ISymbol.NAME);
	}

	protected Map<String, SymbolMetaDescription> registeredSymbols = new HashMap();

	// private final IExpressionFactory defaultExpressionFactory = new GamlExpressionFactory(
	// new GamlExpressionParser(), null);

	@Override
	public IExpressionFactory getDefaultExpressionFactory() {
		return (IExpressionFactory) chooseFactoryFor(ISymbol.GAML);
	}

	protected final Map<ISymbolFactory, Set<String>> registeredFactories = new HashMap();

	public SymbolFactory() {
		// GUI.debug(getClass().getSimpleName() + " initialized");
		registerAnnotatedFactories();
		registerAnnotatedSymbols();
	}

	private void registerAnnotatedFactories() {
		uses annot = getClass().getAnnotation(uses.class);
		if ( annot == null ) { return; }
		for ( int kind : annot.value() ) {
			Class c = FACTORIES_BY_KIND.get(kind);
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
			List<Class> classes = CLASSES_BY_KIND.get(c);
			for ( Class clazz : classes ) {
				register(clazz);
			}
		}
	}

	@Override
	public Set<String> getKeywords() {
		return registeredSymbols.keySet();
	}

	public void registerFactory(final ISymbolFactory f) {
		registeredFactories.put(f, f.getKeywords());

		// OutputManager.debug(this.getClass().getSimpleName() + " registers factory " +
		// f.getClass().getSimpleName() + " for keywords " + f.getKeywords());

	}

	public void register(final Class c) {
		Class baseClass = null;
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
		}
		if ( c.getAnnotation(inside.class) != null ) {
			inside parents = (inside) c.getAnnotation(inside.class);
			for ( String p : parents.symbols() ) {
				contexts.add(p);
			}
			for ( int kind : parents.kinds() ) {
				List<Class> classes = CLASSES_BY_KIND.get(kind);
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
			try {
				registeredSymbols.put(k, new SymbolMetaDescription(c, baseClass, k,
					canHaveSequence, canHaveArgs, isTopLevel, doesNotHaveScope, facets,
					combinations, contexts, isRemoteContext));
			} catch (GamlException e) {
				e.addContext("In compiling the meta description of: " + k);
				e.printStackTrace();
			}
		}
	}

	@Override
	public SymbolMetaDescription getMetaDescriptionFor(final String keyword) throws GamlException {
		SymbolMetaDescription md = registeredSymbols.get(keyword);
		if ( md == null ) {
			ISymbolFactory f = chooseFactoryFor(keyword, null);
			if ( f == null ) { throw new GamlException("Unknown symbol " + keyword); }
			return f.getMetaDescriptionFor(keyword);
		}
		return md;
	}

	protected String getKeyword(final IDescription desc) {
		return desc.getKeyword();
	}

	protected String getKeyword(final ISyntacticElement cur) {
		return cur.getName();
	}

	public IDescription createSymbolDescription(final ISyntacticElement cur,
		final IDescription superDesc, final String ... additionalFacets) throws GamlException {
		int n = additionalFacets.length;
		for ( int i = 0; i < n; i += 2 ) {
			cur.setAttribute(additionalFacets[i], additionalFacets[i + 1]);
		}
		return createDescription(cur, superDesc);
	}

	@Override
	public IDescription createDescription(final ISyntacticElement cur, final IDescription superDesc)
		throws GamlException {
		if ( cur == null ) { return null; }
		String keyword = getKeyword(cur);

		String context = null;
		if ( superDesc != null ) {
			context = superDesc.getKeyword();
		}

		ISymbolFactory f = chooseFactoryFor(keyword, context);
		if ( f == null ) { throw new GamlException("Impossible to parse keyword " + keyword); }
		if ( f != this ) { return f.createDescription(cur, superDesc); }

		ISyntacticElement source = cur;

		try {
			SymbolMetaDescription md = getMetaDescriptionFor(keyword);
			Facets facets = new Facets();
			Map<String, String> attributes = cur.getAttributes();
			for ( Map.Entry<String, String> a : attributes.entrySet() ) {
				facets.put(a.getKey(), a.getValue().trim());
			}
			md.verifyFacets(facets);
			List<IDescription> commands = new ArrayList();

			for ( ISyntacticElement e : cur.getChildren() ) {
				// Instead of having to consider this specific case, find a better solution.

				if ( !cur.getName().equals(ISymbol.SPECIES) ) {
					commands.add(createDescription(e, superDesc));
				} else if ( cur.hasParent(ISymbol.DISPLAY) ) { // "species" declared in "display"
																// section
					commands.add(createDescription(e, superDesc));
				}
			}

			return buildDescription(source, keyword, commands, facets, superDesc, md);
		} catch (GamlException e) {
			e.addSource(source);
			throw e;
		}
	}

	@Override
	public ISymbolFactory chooseFactoryFor(final String keyword) {
		if ( registeredSymbols.containsKey(keyword) ) { return this; }
		for ( ISymbolFactory f : registeredFactories.keySet() ) {
			if ( f.getKeywords().contains(keyword) ) { return f; }
		}
		for ( ISymbolFactory f : registeredFactories.keySet() ) {
			ISymbolFactory f2 = f.chooseFactoryFor(keyword);
			if ( f2 != null ) { return f2; }
		}
		return null;
	}

	protected ISymbolFactory chooseFactoryFor(final String keyword, final String context) {
		try {
			Set<String> factoryNames = new HashSet();
			for ( ISymbolFactory s : registeredFactories.keySet() ) {
				factoryNames.add(s.getClass().getSimpleName());
			}

			ISymbolFactory contextFactory = context != null ? chooseFactoryFor(context) : this;
			if ( contextFactory == null ) {
				contextFactory = this;
			}
			return contextFactory.chooseFactoryFor(keyword);
		} catch (StackOverflowError soe) {
			soe.printStackTrace();
			throw soe;
		}
	}

	@Override
	public IDescription createDescription(final IDescription superDesc,
		final List<IDescription> children, final String ... strings) throws GamlException {
		String keyword = strings[0];
		ISymbolFactory f = chooseFactoryFor(keyword, null);
		if ( f == null ) { throw new GamlException("Impossible to parse keyword " + keyword); }
		if ( f != this ) { return f.createDescription(superDesc, children, strings); }
		List<IDescription> commandList;
		commandList = children == null ? new ArrayList() : children;
		SymbolMetaDescription md = getMetaDescriptionFor(keyword);
		Facets facets = new Facets();
		facets.addAll(strings);
		md.verifyFacets(facets);
		return buildDescription(null, keyword, commandList, facets, superDesc, md);
	}

	protected IDescription buildDescription(final ISyntacticElement source, final String keyword,
		final List<IDescription> commands, final Facets facets, final IDescription superDesc,
		final SymbolMetaDescription md) throws GamlException {
		return new SymbolDescription(keyword, superDesc, facets, commands, source);
	}

	@Override
	public ISymbol compileDescription(final IDescription desc, final IExpressionFactory factory)
		throws GamlException, GamaRuntimeException {
		IDescription superDesc = desc.getSuperDescription();
		ISymbolFactory f =
			chooseFactoryFor(desc.getKeyword(), superDesc == null ? null : superDesc.getKeyword());
		if ( f == null ) { throw new GamlException("Impossible to compile keyword " +
			desc.getKeyword(), desc.getSourceInformation()); }
		if ( f != this ) { return f.compileDescription(desc, factory); }
		SymbolMetaDescription md = getMetaDescriptionFor(desc.getKeyword());
		try {
			return privateCompile(desc, md, factory);
		} catch (GamlException e) {
			e.addContext("In compiling " + desc.getKeyword() + " " + desc.getName());
			e.addSource(desc.getSourceInformation());
			throw e;
		}
	}

	protected Facets compileFacets(final IDescription sd, final SymbolMetaDescription md,
		final IExpressionFactory factory) throws GamlException, GamaRuntimeException {
		Facets rawFacets = sd.getFacets();
		// Addition of a facet to keep track of the keyword
		rawFacets.putAsLabel(ISymbol.KEYWORD, sd.getKeyword());

		for ( String s : new ArrayList<String>(rawFacets.keySet()) ) {
			IExpression e = compileFacet(s, sd, md, factory);
			rawFacets.put(s, e);
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
		final IExpressionFactory factory) throws GamlException, GamaRuntimeException {

		try {

			compileFacets(desc, md, factory);
			ISymbol cs = compileSymbol(desc, md.getConstructor());
			if ( md.hasSequence() ) {
				if ( md.isRemoteContext() ) {
					desc.copyTempsAbove();
				}
				privateCompileChildren(desc, cs, factory);
			}
			return cs;

		} catch (GamlException e) {
			e.addContext("Unable to compile  " + desc.getKeyword());
			throw e;
		}
	}

	protected ISymbol compileSymbol(final IDescription desc, final ISymbolConstructor c)
		throws GamlException, GamaRuntimeException {
		ISymbol cs = c.create(desc);
		return cs;
	}

	protected void privateCompileChildren(final IDescription desc, final ISymbol cs,
		final IExpressionFactory factory) throws GamlException, GamaRuntimeException {
		List<ISymbol> lce = new ArrayList();
		for ( IDescription sd : desc.getChildren() ) {
			lce.add(compileDescription(sd, factory));
		}
		cs.setChildren(lce);
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
	public ISymbol compileFile(final String path) throws GamlException, GamaRuntimeException,
		InterruptedException {
		return null;
	}

}
