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

import static msi.gama.precompiler.ISymbolKind.*;
import java.util.List;
import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.handles;
import msi.gama.precompiler.GamlAnnotations.uses;
import msi.gama.precompiler.ISymbolKind.Variable;
import msi.gaml.compilation.GamaClassLoader;
import msi.gaml.descriptions.*;
import msi.gaml.statements.Facets;

/**
 * SpeciesFactory.
 * 
 * @author drogoul 25 oct. 07
 */
@handles({ SPECIES })
@uses({ BEHAVIOR, ACTION, Variable.NUMBER, Variable.CONTAINER, Variable.REGULAR, Variable.SIGNAL })
public class SpeciesFactory extends SymbolFactory {

	public SpeciesFactory(final List<Integer> handles, final List<Integer> uses) {
		super(handles, uses);
	}

	@Override
	protected SpeciesDescription buildDescription(final ISyntacticElement source,
		final String keyword, final List<IDescription> children, final IDescription sd,
		final SymbolMetaDescription md) {
		Facets facets = source.getFacets();
		String name = facets.getLabel(IKeyword.NAME);
		addSpeciesNameAsType(name);
		Class base = md.getBaseClass();
		String secondBase = facets.getLabel(IKeyword.BASE);
		SpeciesDescription previous = sd.getSpeciesDescription(name);
		String firstBase = previous != null ? previous.getFacets().getLabel(IKeyword.BASE) : null;
		if ( secondBase == null && firstBase != null ) {
			facets.putAsLabel(IKeyword.BASE, firstBase);
		}
		if ( facets.containsKey(IKeyword.BASE) ) {
			try {
				base = GamaClassLoader.getInstance().loadClass(facets.getLabel(IKeyword.BASE));
			} catch (Exception e) {
				sd.flagError("Error loading '" + keyword + "': " + e.getMessage(),
					IGamlIssue.GENERAL);
			}
		}
		return new SpeciesDescription(keyword, sd, facets, children, source, base, md);

	}

	@Override
	protected void privateValidateChildren(final IDescription sd) {
		SpeciesDescription desc = sd.getSpeciesContext();
		// we first validate the variables in the right order
		// Necessary to make content assist work correctly
		for ( String s : desc.getVarNames() ) {
			validateDescription(desc.getVariable(s));
		}
		// then the rest
		for ( IDescription s : sd.getChildren() ) {
			if ( !(s instanceof VariableDescription) ) {
				validateDescription(s);
			}
		}
	}

}
