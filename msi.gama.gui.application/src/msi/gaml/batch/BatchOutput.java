/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
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
package msi.gaml.batch;

import java.util.List;
import msi.gama.interfaces.*;
import msi.gama.kernel.Symbol;
import msi.gama.kernel.exceptions.GamlException;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;

@symbol(name = ISymbol.SAVE, kind = ISymbolKind.BATCH_METHOD)
@inside(kinds = { ISymbolKind.EXPERIMENT })
@facets({ @facet(name = ISymbol.TO, type = IType.LABEL, optional = false),
	@facet(name = ISymbol.REWRITE, type = IType.BOOL_STR, optional = true),
	@facet(name = ISymbol.DATA, type = IType.NONE_STR, optional = true) })
public class BatchOutput extends Symbol {

	// A placeholder for a file output
	// TODO To be replaced by a proper "save" command, now that it accepts
	// new file types.

	public BatchOutput(/* final ISymbol context, */final IDescription desc) {
		super(desc);
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) throws GamlException {}

}