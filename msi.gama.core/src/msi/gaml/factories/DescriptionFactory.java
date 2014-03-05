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
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.*;
import msi.gama.common.interfaces.*;
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

	static TIntObjectHashMap<SymbolFactory> FACTORIES = new TIntObjectHashMap(10, 0.5f, Integer.MAX_VALUE);

	static Map<String, SymbolProto> KEYWORDS_PROTOS = new HashMap();

	static TIntObjectHashMap<SymbolProto> KINDS_PROTOS = new TIntObjectHashMap(10, 0.5f, Integer.MAX_VALUE);

	public static void addFactory(final SymbolFactory factory) {
		for ( final int i : factory.getHandles() ) {
			FACTORIES.put(i, factory);
		}
	}

	public final static SymbolProto getProto(final String keyword) {
		return KEYWORDS_PROTOS.get(keyword);
	}

	public final static Map<String, SymbolProto> getProtos() {
		return KEYWORDS_PROTOS;
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
			// GuiUtils.debug("DescriptionFactory.addProto " + s);
			if ( KEYWORDS_PROTOS.containsKey(s) ) { return; }
			KEYWORDS_PROTOS.put(s, md);
		}
		KINDS_PROTOS.put(kind, md);
	}

	public static void addNewTypeName(final String s, final int kind) {
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
		object.eAdapters().add(getGamlDocumentation(description));

	}

	public static Documentation getGamlDocumentation(final IGamlDescription o) {
		return new Documentation(o);
	}

	// To be called once the validation has been done
	public static void document(final IDescription desc) {
		if ( desc == null ) { return; }
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

	public synchronized static IDescription create(final SymbolFactory factory, final String keyword,
		final IDescription superDesc, final ChildrenProvider children, final Facets facets) {
		// TODO Verify this
		final IDescription result =
			create(SyntacticFactory.create(keyword, facets, !children.getChildren().isEmpty()), superDesc, children);
		return result;
	}

	public synchronized static IDescription create(final String keyword, final IDescription superDesc,
		final ChildrenProvider children, final Facets facets) {
		return create(getFactory(keyword), keyword, superDesc, children, facets);
	}

	public synchronized static IDescription create(final String keyword, final IDescription superDesc,
		final ChildrenProvider children, final String ... facets) {
		return create(getFactory(keyword), keyword, superDesc, children, new Facets(facets));
	}

	public synchronized static IDescription create(final String keyword, final IDescription superDescription,
		final String ... facets) {
		return create(keyword, superDescription, ChildrenProvider.NONE, facets);
	}

	public synchronized static IDescription create(final String keyword, final String ... facets) {
		return create(keyword, GAML.getModelContext(), facets);
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

	public static final IDescription create(final ISyntacticElement source, final IDescription superDesc,
		final ChildrenProvider cp) {
		if ( source == null ) { return null; }
		String keyword = source.getKeyword();
		final SymbolProto md = DescriptionFactory.getProto(keyword);
		if ( md == null ) {
			superDesc.error("Unknown statement " + keyword, IGamlIssue.UNKNOWN_KEYWORD, source.getElement(), keyword);
			return null;
		}
		ChildrenProvider children = cp;
		if ( children == null ) {
			final List<IDescription> children_list = new ArrayList();
			for ( final ISyntacticElement e : source.getChildren() ) {
				IDescription desc = create(e, superDesc, null);
				if ( desc != null ) {
					children_list.add(desc);
				}
			}
			children = new ChildrenProvider(children_list);
		}
		Facets facets = source.copyFacets(md);
		EObject element = source.getElement();
		final IDescription desc = md.getFactory().buildDescription(keyword, facets, element, children, superDesc, md);
		return desc;

	}

}
