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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
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
