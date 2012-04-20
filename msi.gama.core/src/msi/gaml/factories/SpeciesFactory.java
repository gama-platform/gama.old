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

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.handles;
import msi.gama.precompiler.GamlAnnotations.uses;
import msi.gama.precompiler.*;
import msi.gaml.commands.Facets;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;

/**
 * SpeciesFactory.
 * 
 * @author drogoul 25 oct. 07
 */
@handles({ ISymbolKind.SPECIES })
@uses({ ISymbolKind.BEHAVIOR, ISymbolKind.ACTION, ISymbolKind.Variable.NUMBER,
	ISymbolKind.Variable.CONTAINER, ISymbolKind.Variable.REGULAR, ISymbolKind.Variable.SIGNAL })
public class SpeciesFactory extends SymbolFactory {

	/**
	 * @param superFactory
	 */
	public SpeciesFactory(final ISymbolFactory superFactory) {
		super(superFactory);
	}

	private VariableFactory varFactory;

	@Override
	protected SpeciesDescription buildDescription(final ISyntacticElement source,
		final String keyword, final List<IDescription> children, final IDescription superDesc,
		final SymbolMetaDescription md) {
		Facets facets = source.getFacets();
		String name = facets.getLabel(IKeyword.NAME);
		varFactory.addSpeciesNameAsType(name);
		Class base = md.getBaseClass();
		String secondBase = facets.getLabel(IKeyword.BASE);

		String firstBase =
			superDesc.getSpeciesDescription(name) != null ? superDesc.getSpeciesDescription(name)
				.getFacets().getLabel(IKeyword.BASE) : null;

		if ( secondBase == null && firstBase != null ) {
			facets.putAsLabel(IKeyword.BASE, firstBase);
		}
		if ( facets.containsKey(IKeyword.BASE) ) {
			try {
				base = GamaClassLoader.getInstance().loadClass(facets.getLabel(IKeyword.BASE));
			} catch (ClassNotFoundException e) {
				superDesc.flagError("Impossible to instantiate '" + keyword + "' because: " +
					e.getMessage());
			}
		}
		SpeciesDescription sd =
			new SpeciesDescription(keyword, superDesc, facets, children, base, source, md);

		return sd;

	}

	@Override
	public boolean registerFactory(final ISymbolFactory f) {
		if ( super.registerFactory(f) ) {
			if ( f instanceof VariableFactory ) {
				varFactory = (VariableFactory) f;
				return true;
			}
		}
		return false;
	}

	@Override
	protected List<ISymbol> privateCompileChildren(final IDescription sd) {
		List<ISymbol> lce = new ArrayList();
		SpeciesDescription desc = sd.getSpeciesContext();
		// we first compile the variables in the right order
		// long time = System.currentTimeMillis();
		for ( String s : desc.getVarNames() ) {
			lce.add(compileDescription(desc.getVariable(s)));
		}
		// then the rest
		for ( IDescription s : sd.getChildren() ) {
			if ( !(s instanceof VariableDescription) ) {
				lce.add(compileDescription(s));
			}
		}
		return lce;
	}

}
