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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.factories;

import java.util.*;
import msi.gama.common.interfaces.ISyntacticElement;
import msi.gama.common.util.ErrorCollector;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpressionFactory;

/**
 * Written by drogoul Modified on 28 déc. 2010
 * 
 * @todo Description
 * 
 */
public interface ISymbolFactory {

	public abstract IDescription createDescription(ISyntacticElement cur,
		final IDescription superDescription, final List<IDescription> children,
		final String ... facets);

	public abstract IDescription createDescription(ISyntacticElement cur, IDescription superDesc);

	public abstract ISymbol compileDescription(final IDescription desc,
		final IExpressionFactory factory);

	public abstract ISymbolFactory chooseFactoryFor(String keyword);

	public abstract Set<String> getKeywords();

	public abstract IExpressionFactory getDefaultExpressionFactory();

	public abstract String getOmissibleFacetForSymbol(ISyntacticElement elmt, String symbol);

	/**
	 * @param structure
	 * @return
	 * @throws GamlException
	 * @throws GamaRuntimeException
	 * @throws InterruptedException
	 */
	ISymbol compile(ModelStructure structure, ErrorCollector collect) throws InterruptedException;

	/**
	 * @param desc
	 * @param keyword
	 * @return
	 * @throws GamlException
	 */
	SymbolMetaDescription getMetaDescriptionFor(IDescription desc, String keyword);

	/**
	 * @param keyword
	 * @return
	 */
	public abstract boolean handlesKeyword(String keyword);

}