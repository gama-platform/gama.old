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

import java.util.List;
import msi.gama.interfaces.*;
import msi.gama.internal.descriptions.*;
import msi.gama.internal.expressions.Facets;
import msi.gama.kernel.exceptions.GamlException;
import msi.gama.lang.utils.ISyntacticElement;
import msi.gama.precompiler.GamlAnnotations.handles;
import msi.gama.precompiler.GamlAnnotations.uses;
import msi.gama.precompiler.*;

/**
 * The Class EnvironmentFactory.
 * 
 * @author drogoul
 */
@handles({ ISymbolKind.EXPERIMENT })
@uses({ ISymbolKind.OUTPUT, ISymbolKind.VARIABLE, ISymbolKind.BATCH_METHOD })
public class ExperimentFactory extends SymbolFactory {

	@Override
	protected String getKeyword(final ISyntacticElement cur) {
		if ( !cur.getName().equals(ISymbol.EXPERIMENT) ) { return super.getKeyword(cur); }
		String type = cur.getAttribute(ISymbol.TYPE);
		if ( type == null ) { return super.getKeyword(cur); }
		return type;
	}

	// A lot to do here, probably (handle the batch, outputs, parameters, scheduler, remote and
	// random specifications)

	@Override
	protected IDescription buildDescription(final ISyntacticElement source, final String keyword,
		final List<IDescription> commands, final Facets facets, final IDescription superDesc,
		final SymbolMetaDescription md) throws GamlException {
		return new ExperimentDescription(keyword, superDesc, facets, commands, source);
	}
}
