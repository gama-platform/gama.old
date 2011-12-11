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

import msi.gama.interfaces.*;
import msi.gama.lang.utils.ISyntacticElement;
import msi.gama.precompiler.GamlAnnotations.handles;
import msi.gama.precompiler.*;
import msi.gaml.batch.IBatch;

/**
 * Written by drogoul Modified on 17 mai 2010
 * 
 * @todo Description
 * 
 */
@handles({ ISymbolKind.BATCH_METHOD })
public class BatchFactory extends SymbolFactory {

	public BatchFactory() {
		super();
		for ( Class c : IBatch.CLASSES ) {
			this.register(c);
		}
	}

	@Override
	protected String getKeyword(final ISyntacticElement cur) {
		if ( cur.getName().equals(ISymbol.METHOD) ) { return cur.getAttribute(ISymbol.NAME); }
		return super.getKeyword(cur);
	}

	@Override
	protected String getKeyword(final IDescription desc) {
		if ( desc.getKeyword().equals(ISymbol.METHOD) ) { return desc.getName(); }
		return super.getKeyword(desc);
	}

}
