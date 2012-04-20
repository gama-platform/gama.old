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

import java.util.List;
import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.handles;
import msi.gama.precompiler.GamlAnnotations.uses;
import msi.gama.precompiler.*;
import msi.gaml.descriptions.*;

/**
 * The Class EnvironmentFactory.
 * 
 * @author drogoul
 */
@handles({ ISymbolKind.EXPERIMENT })
@uses({ ISymbolKind.OUTPUT, ISymbolKind.PARAMETER, ISymbolKind.BATCH_METHOD, ISymbolKind.ACTION,
	ISymbolKind.BEHAVIOR })
public class ExperimentFactory extends SpeciesFactory {

	/**
	 * @param superFactory
	 */
	public ExperimentFactory(final ISymbolFactory superFactory) {
		super(superFactory);
	}

	@Override
	protected String getKeyword(final ISyntacticElement cur) {
		if ( !cur.getKeyword().equals(IKeyword.EXPERIMENT) ) { return super.getKeyword(cur); }
		String type = cur.getLabel(IKeyword.TYPE);
		if ( type == null ) { return super.getKeyword(cur); }
		return type;
	}

	// A lot to do here, probably (handle the batch, outputs, parameters, scheduler, remote and
	// random specifications)

	@Override
	protected ExperimentDescription buildDescription(final ISyntacticElement source,
		final String keyword, final List<IDescription> commands, final IDescription superDesc,
		final SymbolMetaDescription md) {
		return new ExperimentDescription(keyword, superDesc, commands, source, md);
	}
}
