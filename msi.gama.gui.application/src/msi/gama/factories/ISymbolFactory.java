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