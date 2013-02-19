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

import static msi.gama.precompiler.ISymbolKind.SPECIES;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.GuiUtils;
import msi.gama.precompiler.GamlAnnotations.factory;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.*;
import msi.gaml.statements.Facets;

/**
 * SpeciesFactory.
 * 
 * @author drogoul 25 oct. 07
 */

@factory(handles = { SPECIES })
public class SpeciesFactory extends SymbolFactory {

	public SpeciesFactory(final List<Integer> handles) {
		super(handles);
	}

	@Override
	protected SpeciesDescription buildDescription(final ISyntacticElement source,
		final IChildrenProvider cp, final IDescription sd, final SymbolProto md) {
		Facets facets = source.getFacets();
		String name = facets.getLabel(IKeyword.NAME);
		if ( name == null ) { return null; }
		DescriptionFactory.addSpeciesNameAsType(name);
		return new SpeciesDescription(source.getKeyword(), sd, facets, cp, source);
	}

	public SpeciesDescription createSpeciesDescription(final String name, final Class clazz,
		final IDescription superDesc, final IAgentConstructor helper, final Set<String> skills,
		final Facets userSkills) {
		DescriptionFactory.addSpeciesNameAsType(name);
		return new SpeciesDescription(name, clazz, superDesc, helper, skills,
			DescriptionFactory.getProto(IKeyword.SPECIES), userSkills);
	}

	@Override
	protected void privateValidateChildren(final IDescription sd) {
		SpeciesDescription desc = sd.getSpeciesContext();
		// we first validate the variables in the right order
		// Necessary to make content assist work correctly
		for ( String s : desc.getVarNames() ) {
			validate(desc.getVariable(s));
		}
		// then the rest
		for ( IDescription s : sd.getChildren() ) {
			if ( !(s instanceof VariableDescription) ) {
				validate(s);
			}
		}
	}

	@Override
	ISymbol privateCompile(final IDescription desc) {
		GuiUtils.debug("Compiling " + desc.getTitle() + "as species");
		return super.privateCompile(desc);
	}

}
