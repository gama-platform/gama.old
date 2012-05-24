package msi.gaml.factories;

import java.util.*;
import msi.gama.precompiler.ISymbolKind;
import msi.gaml.descriptions.SymbolMetaDescription;

public abstract class AbstractFactory implements ISymbolFactory {

	protected static final Map<ISymbolFactory, Set<ISymbolFactory>> HIERARCHY = new HashMap();

	protected final Map<String, SymbolMetaDescription> registeredSymbols = new LinkedHashMap();
	protected final Map<String, ISymbolFactory> registeredFactories = new HashMap();
	protected final Set<ISymbolFactory> childFactories = new HashSet();
	protected final Set<Integer> kindsHandled;
	protected final Set<Integer> kindsUsed;

	public AbstractFactory(final List<Integer> handles, final List<Integer> uses) {
		kindsHandled = new HashSet(handles);
		kindsUsed = uses == null ? Collections.EMPTY_SET : new HashSet(uses);
	}

	@Override
	public Set<Integer> getHandles() {
		return kindsHandled;
	}

	@Override
	public boolean handles(final String keyword) {
		return registeredSymbols.containsKey(keyword);
	}

	@Override
	public void registerSymbol(final SymbolMetaDescription md, final List<String> names) {
		int kind = md.getKind();
		if ( handles(kind) ) {
			if ( !ISymbolKind.Variable.KINDS.contains(kind) ) {
				SymbolMetaDescription.nonVariableStatements.addAll(names);
			} // FIXME DONE SEVERAL TIMES. MAYBE UNNECESSARY
			for ( String s : names ) {
				if ( registeredSymbols.containsKey(s) ) { return; }
				registeredSymbols.put(s, md);
			}
		} else {
			for ( ISymbolFactory f : HIERARCHY.get(this) ) {
				f.registerSymbol(md, names);
			}
		}
	}

	private boolean handles(final int kind) {
		return kindsHandled.contains(kind);
	}

	@Override
	public void assembleWith(final Map<Integer, ISymbolFactory> factories) {
		if ( !HIERARCHY.containsKey(this) ) {
			HIERARCHY.put(this, new HashSet());
		}
		for ( Integer i : kindsUsed ) {

			HIERARCHY.get(this).add(factories.get(i));
		}
	}

	@Override
	public void addSpeciesNameAsType(final String name) {
		for ( ISymbolFactory sf : HIERARCHY.get(this) ) {
			sf.addSpeciesNameAsType(name);
		}
	}

	// FIXME : TOO COMPLEX !!!!!!
	@Override
	public ISymbolFactory chooseFactoryFor(final String keyword) {
		if ( handles(keyword) ) { return this; }
		ISymbolFactory f = registeredFactories.get(keyword);
		if ( f != null ) { return f; }
		for ( ISymbolFactory fact : HIERARCHY.get(this) ) {
			if ( fact.handles(keyword) ) {
				registeredFactories.put(keyword, fact);
				return fact;
			}
		}
		for ( ISymbolFactory fact : HIERARCHY.get(this) ) {
			SymbolFactory f2 = (SymbolFactory) fact.chooseFactoryFor(keyword);
			if ( f2 != null ) {
				// registeredFactories.put(keyword, fact);
				return f2;
			}
		}
		return null;
	}

	protected ISymbolFactory chooseFactoryFor(final String keyword, final String context) {
		ISymbolFactory contextFactory = context != null ? chooseFactoryFor(context) : this;
		if ( contextFactory == null ) {
			contextFactory = this;
		}
		return contextFactory.chooseFactoryFor(keyword);
	}

}
