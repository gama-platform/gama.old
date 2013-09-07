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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.factories;

import static msi.gama.common.interfaces.IKeyword.AGENT;
import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.GuiUtils;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.util.GAML;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.statements.Facets;
import org.eclipse.emf.common.notify.*;
import org.eclipse.emf.ecore.EObject;

/**
 * Written by drogoul Modified on 7 janv. 2011
 * 
 * @todo Description
 * 
 */
public class DescriptionFactory {

	public static class Documentation implements Adapter.Internal {

		final String doc;
		final String title;

		Documentation(final IGamlDescription desc) {
			doc = desc.getDocumentation();
			title = desc.getTitle();
		}

		public String getDocumentation() {
			return doc;
		}

		public String getTitle() {
			return title;
		}

		@Override
		public void notifyChanged(final Notification notification) {}

		@Override
		public Notifier getTarget() {
			return null;
		}

		@Override
		public void setTarget(final Notifier newTarget) {}

		@Override
		public boolean isAdapterForType(final Object type) {
			return false;
		}

		@Override
		public void unsetTarget(final Notifier oldTarget) {}

	}

	static Map<Integer, SymbolFactory> FACTORIES = new HashMap();

	static Map<String, SymbolProto> KEYWORDS_PROTOS = new HashMap();

	static Map<Integer, SymbolProto> KINDS_PROTOS = new HashMap();

	public static void addFactory(final SymbolFactory factory) {
		for ( final int i : factory.getHandles() ) {
			FACTORIES.put(i, factory);
		}
	}

	public final static SymbolProto getProto(final String keyword) {
		return KEYWORDS_PROTOS.get(keyword);
	}

	public static SymbolFactory getFactory(final int kind) {
		return FACTORIES.get(kind);
	}

	public static String getOmissibleFacetForSymbol(final String keyword) {
		final SymbolProto md = getProto(keyword);
		if ( md == null ) { return IKeyword.NAME; }
		return md.getOmissible();
	}

	public static void addProto(final SymbolProto md, final List<String> names) {
		final int kind = md.getKind();
		if ( !ISymbolKind.Variable.KINDS.contains(kind) ) {
			SymbolProto.nonTypeStatements.addAll(names);
		}
		for ( final String s : names ) {
			if ( KEYWORDS_PROTOS.containsKey(s) ) { return; }
			KEYWORDS_PROTOS.put(s, md);
		}
		KINDS_PROTOS.put(kind, md);
	}

	public static void addNewTypeName(final String s, final int kind) {
		if ( s.equals(IKeyword.EXPERIMENT) ) {
			GuiUtils.debug("DescriptionFactory.addNewTypeName");
		}
		if ( KEYWORDS_PROTOS.containsKey(s) ) { return; }
		final SymbolProto p = KINDS_PROTOS.get(kind);
		if ( p != null ) {
			KEYWORDS_PROTOS.put(s, p);
		}
	}

	public static SymbolFactory getFactory(final String keyword) {
		final SymbolProto p = KEYWORDS_PROTOS.get(keyword);
		if ( p != null ) { return p.getFactory(); }
		return null;
	}

	public static void addSpeciesNameAsType(final String name) {
		if ( !name.equals(AGENT) && !name.equals(IKeyword.EXPERIMENT) ) {
			KEYWORDS_PROTOS.put(name, KEYWORDS_PROTOS.get(AGENT));
		}
	}

	public static void setGamlDocumentation(final EObject object, final IGamlDescription description) {
		if ( description == null || object == null ) { return; }
		Documentation existing = getGamlDocumentation(object);
		if ( existing != null ) {
			object.eAdapters().remove(existing);
		}
		object.eAdapters().add(new Documentation(description));
		//
		// final IGamlDescription existing = getGamlDescription(object, description.getClass());

	}

	// To be called once the validation has been done
	public static void document(final IDescription desc) {
		setGamlDocumentation(desc.getUnderlyingElement(null), desc);
		for ( IDescription d : desc.getChildren() ) {
			document(d);
		}
	}

	public static Documentation getGamlDocumentation(final EObject object) {
		if ( object == null ) { return null; }
		for ( int i = 0, n = object.eAdapters().size(); i < n; i++ ) {
			final Adapter a = object.eAdapters().get(i);
			if ( a.getClass() == Documentation.class ) { return (Documentation) a; }

		}
		return null;
	}

	// public static <T> T getGamlDescription(final EObject object, final Class<T> preciseClass) {
	// if ( object == null ) { return null; }
	// for ( int i = 0, n = object.eAdapters().size(); i < n; i++ ) {
	// final Adapter a = object.eAdapters().get(i);
	// if ( preciseClass.isAssignableFrom(a.getClass()) ) { return (T) a; }
	//
	// }
	// return null;
	// }

	// public static IGamlDescription getGamlDescription(final EObject object) {
	// if ( object == null ) { return null; }
	// for ( final Adapter o : object.eAdapters() ) {
	// if ( o instanceof IGamlDescription ) { return (IGamlDescription) o; }
	// }
	// return null;
	// }

	// public static void unsetGamlDescription(final EObject object, final IGamlDescription description) {
	// if ( object == null ) { return; }
	// object.eAdapters().remove(description);
	// }

	// -----

	public synchronized static IDescription create(final SymbolFactory factory, final String keyword,
		final IDescription superDesc, final IChildrenProvider children, final Facets facets) {
		final IDescription result = factory.create(new SyntacticElement(keyword, facets), superDesc, children);
		// factory.validate(result);
		return result;
	}

	// public synchronized static IDescription create(final String keyword, final IDescription superDesc,
	// final IChildrenProvider children, final EObject element, final Facets facets) {
	// final IDescription result =
	// getFactory(keyword).create(new SyntacticElement(keyword, facets, element), superDesc, children);
	// return result;
	// }

	public synchronized static IDescription create(final String keyword, final IDescription superDesc,
		final IChildrenProvider children, final Facets facets) {
		return create(getFactory(keyword), keyword, superDesc, children, facets);
	}

	public synchronized static IDescription create(final String keyword, final IDescription superDesc,
		final IChildrenProvider children, final String ... facets) {
		return create(getFactory(keyword), keyword, superDesc, children, new Facets(facets));
	}

	public synchronized static IDescription create(final String keyword, final IDescription superDescription,
		final String ... facets) {
		return create(keyword, superDescription, IChildrenProvider.NONE, facets);
	}

	public synchronized static IDescription create(final String keyword, final String ... facets) {
		return create(keyword, GAML.getModelContext(), facets);
	}

	public synchronized static ISymbol compile(final IDescription desc) {
		return getFactory(desc.getKeyword()).compile(desc);
	}

	public synchronized static IDescription validate(final IDescription desc) {
		return getFactory(desc.getKeyword()).validate(desc);
	}

	public static ModelFactory getModelFactory() {
		return (ModelFactory) getFactory(ISymbolKind.MODEL);
	}

	public static Set<String> getAllowedFacetsFor(final String key) {
		if ( key == null ) { return Collections.EMPTY_SET; }
		final SymbolProto md = getProto(key);
		return md == null ? Collections.EMPTY_SET : md.getPossibleFacets().keySet();
	}

	public static SpeciesDescription createBuiltInSpeciesDescription(final String name, final Class clazz,
		final IDescription superDesc, final SpeciesDescription parent, final IAgentConstructor helper,
		final Set<String> skills) {
		return ((SpeciesFactory) getFactory(ISymbolKind.SPECIES)).createBuiltInSpeciesDescription(name, clazz,
			superDesc, parent, helper, skills, new Facets());
	}

	public static ModelDescription createRootModelDescription(final String name, final Class clazz,
		final SpeciesDescription macro, final SpeciesDescription parent) {
		return ((ModelFactory) getFactory(ISymbolKind.MODEL)).createRootModel(name, clazz, macro, parent);
	}

}
