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

import java.util.*;
import msi.gama.agents.AbstractSpecies;
import msi.gama.interfaces.*;
import msi.gama.internal.descriptions.*;
import msi.gama.internal.expressions.*;
import msi.gama.java.JavaSpecies;
import msi.gama.kernel.exceptions.*;
import msi.gama.lang.utils.ISyntacticElement;
import msi.gama.precompiler.GamlAnnotations.handles;
import msi.gama.precompiler.GamlAnnotations.uses;
import msi.gama.precompiler.*;

/**
 * SpeciesFactory.
 * 
 * @author drogoul 25 oct. 07
 */
@handles({ ISymbolKind.SPECIES })
@uses({ ISymbolKind.BEHAVIOR, ISymbolKind.ACTION, ISymbolKind.VARIABLE })
public class SpeciesFactory extends SymbolFactory {

	public static class SpeciesStructure {

		private final ISyntacticElement			node;

		private final List<SpeciesStructure>	microSpecies;

		private boolean							isGrid	= false;

		public SpeciesStructure(final ISyntacticElement node) throws GamlException {
			if ( node == null ) { throw new GamlException("Species element is null!"); }

			this.node = node;
			microSpecies = new ArrayList<SpeciesStructure>();
			isGrid = node.getName().equals(ISymbol.GRID);
		}

		public boolean isGrid() {
			return isGrid;
		}

		public void addMicroSpecies(final SpeciesStructure species) {
			microSpecies.add(species);
		}

		public List<SpeciesStructure> getMicroSpecies() {
			return microSpecies;
		}

		public ISyntacticElement getNode() {
			return node;
		}

		public String getName() {
			return node.getAttribute(ISymbol.NAME);
		}

		@Override
		public String toString() {
			return "Species " + getName();
		}
	}

	public static AbstractSpecies createSpecies(final String id, final Class base,
		final boolean grid, final IModel model) throws GamaRuntimeException {
		try {
			IDescription desc =
				DescriptionFactory.createDescription(ISymbol.SPECIES, model.getDescription(),
					ISymbol.NAME, id, ISpecies.BASE, base.getCanonicalName(), ISymbol.GRID, grid
						? "true" : "false");
			return new JavaSpecies(desc);
		} catch (GamlException e) {
			throw new GamaRuntimeException(e);
		}
	}

	public static AbstractSpecies createSpecies(final Class base, final boolean grid,
		final IModel model) throws GamaRuntimeException {
		return createSpecies(base.getSimpleName(), base, grid, model);
	}

	public static AbstractSpecies createSpecies(final Class base, final IModel model)
		throws GamaRuntimeException {
		return createSpecies(base.getSimpleName(), base, false, model);
	}

	@Override
	protected SpeciesDescription buildDescription(final ISyntacticElement source,
		final String keyword, final List<IDescription> children, final Facets facets,
		final IDescription superDesc, final SymbolMetaDescription md) throws GamlException {
		String name = facets.getString(ISymbol.NAME);
		Class base = md.getBaseClass();
		String secondBase = facets.getString(ISpecies.BASE);

		String firstBase =
			superDesc.getModelDescription().getSpeciesDescription(name) != null ? superDesc
				.getModelDescription().getSpeciesDescription(name).getFacets()
				.getString(ISpecies.BASE) : null;

		if ( secondBase == null && firstBase != null ) {
			facets.putAsLabel(ISpecies.BASE, firstBase);
		}
		if ( facets.containsKey(ISpecies.BASE) ) {
			try {
				base = Class.forName(facets.getString(ISpecies.BASE));
			} catch (ClassNotFoundException e) {
				throw new GamlException("Impossible to instantiate '" + keyword + "' because: " +
					e.getMessage(), source);
			}
		}
		if ( facets.containsKey(ISpecies.SKILLS) ) {
			facets.putAsLabel(ISpecies.SKILLS, facets.getString(ISpecies.SKILLS) + "," + keyword);
		} else {
			facets.putAsLabel(ISpecies.SKILLS, keyword);
		}
		SpeciesDescription sd =
			new SpeciesDescription(keyword, superDesc, facets, children, base, source);

		return sd;

	}

	@Override
	protected void privateCompileChildren(final IDescription sd, final ISymbol cs,
		final IExpressionFactory factory) throws GamlException, GamaRuntimeException {
		List<ISymbol> lce = new ArrayList();
		SpeciesDescription desc = sd.getSpeciesContext();
		// we first compile the variables in the right order
		for ( String s : desc.getVarNames() ) {
			lce.add(compileDescription(desc.getVariable(s), factory));
		}
		// then the rest
		for ( IDescription s : sd.getChildren() ) {
			if ( !(s instanceof VariableDescription) ) {
				lce.add(compileDescription(s, factory));
			}
		}
		cs.setChildren(lce);

	}

}
