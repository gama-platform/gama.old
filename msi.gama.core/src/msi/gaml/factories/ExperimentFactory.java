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

import static msi.gama.common.interfaces.IKeyword.*;
import java.util.List;
import msi.gama.common.interfaces.ISyntacticElement;
import msi.gama.precompiler.GamlAnnotations.factory;
import msi.gama.precompiler.*;
import msi.gaml.descriptions.*;

/**
 * The Class EnvironmentFactory.
 * 
 * @author drogoul
 */
@factory(handles = { ISymbolKind.EXPERIMENT })
public class ExperimentFactory extends SpeciesFactory {

	public ExperimentFactory(final List<Integer> handles) {
		super(handles);
	}

	@Override
	protected ExperimentDescription buildDescription(final ISyntacticElement se,
		final IChildrenProvider cp, final IDescription sd, final SymbolProto md) {
		return new ExperimentDescription(se.getKeyword(), sd, cp, se.getElement(), se.getFacets());
	}

	@Override
	protected void privateValidateChildren(final IDescription sd) {
		for ( IDescription s : sd.getChildren() ) {
			if ( s.getFacets().equals(KEYWORD, PARAMETER) ) {
				validate(s);
			}
		}
		super.privateValidateChildren(sd);
	}

}
