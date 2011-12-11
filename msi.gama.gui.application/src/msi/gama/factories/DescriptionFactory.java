/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.factories;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamlException;
import msi.gama.util.GamaList;

/**
 * Written by drogoul Modified on 7 janv. 2011
 * 
 * @todo Description
 * 
 */
public class DescriptionFactory {

	public synchronized static IDescription createDescription(final ISymbolFactory factory,
		final String keyword, final IDescription superDesc, final List<IDescription> children,
		final String ... facets) throws GamlException {
		List<String> ff = new GamaList(facets);
		ff.add(0, keyword);
		return factory.createDescription(superDesc, children, ff.toArray(new String[ff.size()]));
	}

	public synchronized static IDescription createDescription(final String keyword,
		final IDescription superDesc, final List<IDescription> children, final String ... facets)
		throws GamlException {
		return createDescription(getModelFactory(), keyword, superDesc, children, facets);
	}

	public synchronized static IDescription createDescription(final String keyword,
		final IDescription superDescription, final String ... facets) throws GamlException {
		return createDescription(keyword, superDescription, Collections.EMPTY_LIST, facets);
	}

	public synchronized static IDescription createDescription(final String keyword,
		final String ... facets) throws GamlException {
		return createDescription(keyword, null, facets);
	}

	public synchronized static IDescription createOutputDescription(final String keyword,
		final String ... facets) throws GamlException {
		return createDescription(getOutputFactory(), keyword, null, Collections.EMPTY_LIST, facets);
	}

	private static Class<ISymbolFactory>	FACTORY_CLASS;
	private volatile static ISymbolFactory	modelFactory;

	public static ISymbolFactory getOutputFactory() {
		return getModelFactory().chooseFactoryFor(ISymbol.OUTPUT);
	}

	public static ISymbolFactory getModelFactory() {
		if ( modelFactory == null ) {
			try {
				modelFactory = getFactoryClass().newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return modelFactory;
	}

	public static void setFactoryClass(final Class<ISymbolFactory> fACTORY_CLASS) {
		FACTORY_CLASS = fACTORY_CLASS;
	}

	public static Class<ISymbolFactory> getFactoryClass() {
		return FACTORY_CLASS;
	}

}
