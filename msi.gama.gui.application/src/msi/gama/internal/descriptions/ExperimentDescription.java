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
package msi.gama.internal.descriptions;

import java.util.List;
import msi.gama.interfaces.*;
import msi.gama.internal.expressions.Facets;
import msi.gama.kernel.exceptions.GamlException;
import msi.gama.lang.utils.ISyntacticElement;

public class ExperimentDescription extends SymbolDescription {

	public ExperimentDescription(final String keyword, final IDescription superDesc,
		final Facets facets, final List<IDescription> children, final ISyntacticElement source)
		throws GamlException {
		super(keyword, superDesc, facets, children, source);
	}

	@Override
	public String getKeyword() {
		return facets.getString(ISymbol.TYPE);
		// The type of experiment determines the class to be instantiated
	}

}
