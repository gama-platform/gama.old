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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.factories;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.internal.compilation.*;
import msi.gama.internal.descriptions.SymbolMetaDescription;
import msi.gama.internal.expressions.IExpressionFactory;
import msi.gama.kernel.exceptions.*;
import msi.gama.lang.utils.ISyntacticElement;

/**
 * Written by drogoul Modified on 28 déc. 2010
 * 
 * @todo Description
 * 
 */
public interface ISymbolFactory {

	public abstract IDescription createDescription(final IDescription superDescription,
		final List<IDescription> children, final String ... facets) throws GamlException;

	public abstract IDescription createDescription(ISyntacticElement cur, IDescription superDesc)
		throws GamlException;

	public abstract ISymbol compileDescription(final IDescription desc,
		final IExpressionFactory factory) throws GamlException, GamaRuntimeException;

	public abstract ISymbolFactory chooseFactoryFor(String keyword);

	public abstract SymbolMetaDescription getMetaDescriptionFor(String keyword)
		throws GamlException;

	public abstract Set<String> getKeywords();

	public abstract IExpressionFactory getDefaultExpressionFactory();

	public abstract ISymbol compileFile(String path) throws GamlException, GamaRuntimeException,
		InterruptedException;

}