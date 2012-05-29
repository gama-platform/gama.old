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

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gaml.compilation.SyntheticStatement;
import msi.gaml.descriptions.*;
import msi.gaml.statements.Facets;

/**
 * Written by drogoul Modified on 7 janv. 2011
 * 
 * @todo Description
 * 
 */
public class DescriptionFactory {

	public synchronized static IDescription create(final ISymbolFactory factory,
		final String keyword, final IDescription superDesc, final IChildrenProvider children,
		final Facets facets) {
		facets.putAsLabel(IKeyword.KEYWORD, keyword);
		ISyntacticElement element = new SyntheticStatement(keyword, facets);
		return factory.createDescription(element, superDesc, children);
	}

	public synchronized static IDescription create(final String keyword,
		final IDescription superDesc, final IChildrenProvider children, final Facets facets) {
		return create(getModelFactory(), keyword, superDesc, children, facets);
	}

	public synchronized static IDescription create(final String keyword,
		final IDescription superDesc, final IChildrenProvider children, final String ... facets) {
		return create(getModelFactory(), keyword, superDesc, children, new Facets(facets));
	}

	public synchronized static IDescription create(final String keyword,
		final IDescription superDescription, final String ... facets) {
		return create(keyword, superDescription, IChildrenProvider.NONE, facets);
	}

	public synchronized static IDescription create(final String keyword, final String ... facets) {
		return create(keyword, null, facets);
	}

	public synchronized static IDescription createOutputDescription(final String keyword,
		final String ... facets) {
		return create(getOutputFactory(), keyword, null, IChildrenProvider.NONE, new Facets(facets));
	}

	private volatile static ISymbolFactory.Model modelFactory;

	public static ISymbolFactory getOutputFactory() {
		return getModelFactory().chooseFactoryFor(IKeyword.OUTPUT);
	}

	public static ISymbolFactory.Model getModelFactory() {
		return modelFactory;
	}

	public static Set<String> getAllowedFacetsFor(final String key) {
		if ( key == null ) { return Collections.EMPTY_SET; }
		SymbolMetaDescription md = null;
		md = getModelFactory().getMetaDescriptionFor(null, key);
		Set<String> result = md == null ? null : md.getPossibleFacets().keySet();
		return result == null ? Collections.EMPTY_SET : result;
	}

	public static void setModelFactory(final ISymbolFactory iSymbolFactory) {
		modelFactory = (ModelFactory) iSymbolFactory;
	}

}
