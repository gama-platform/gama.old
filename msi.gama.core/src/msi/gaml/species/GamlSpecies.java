/*********************************************************************************************
 * 
 * 
 * 'GamlSpecies.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.species;

import java.util.Collection;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.util.IContainer;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.species.GamlSpecies.SpeciesValidator;
import msi.gaml.types.IType;

/**
 * The Class GamlSpecies. A species specified by GAML attributes
 * 
 * @author drogoul
 */
@symbol(name = { IKeyword.SPECIES, IKeyword.GLOBAL, IKeyword.GRID }, kind = ISymbolKind.SPECIES, with_sequence = true)
@inside(kinds = { ISymbolKind.MODEL, ISymbolKind.ENVIRONMENT, ISymbolKind.SPECIES })
@facets(value = {
	@facet(name = IKeyword.WIDTH, type = IType.INT, optional = true),
	@facet(name = IKeyword.HEIGHT, type = IType.INT, optional = true),
	@facet(name = IKeyword.NEIGHBOURS, type = IType.INT, optional = true),
	@facet(name = "use_individual_shapes",
		type = IType.BOOL,
		optional = true,
		doc = { @doc(value = "True by default. Allows to specify whether or not the agents of the grid will have distinct geometries. If set to false, they will all have simpler proxy geometries",
			see = "use_regular_agents",
			comment = "This facet, when set to true, allows to save memory by generating only one reference geometry and proxy geometries for the agents") }),
	@facet(name = "use_regular_agents",
		type = IType.BOOL,
		optional = true,
		doc = { @doc(value = "True by default. Allows to specify if the agents of the grid are regular agents (like those of any other species) or minimal ones (which can't have sub-populations, can't inherit from a regular species, etc.)") }),
	@facet(name = "use_neighbours_cache",
		type = IType.BOOL,
		optional = true,
		doc = { @doc(value = "True by default. Allows to turn on or off the use of the neighbours cache used for grids. Note that if a diffusion of variable occurs, GAMA will emit a warning and automatically switch to a caching version") }),
	@facet(name = IKeyword.FILE, type = IType.FILE, optional = true),
	@facet(name = IKeyword.TORUS, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.NAME, type = IType.ID, optional = false),
	@facet(name = IKeyword.PARENT, type = IType.ID, optional = true),
	@facet(name = IKeyword.EDGE_SPECIES, type = IType.ID, optional = true),
	@facet(name = IKeyword.SKILLS, type = IType.LIST, optional = true),
	@facet(name = IKeyword.MIRRORS, type = { IType.LIST, IType.SPECIES }, optional = true),
	@facet(name = IKeyword.CONTROL, type = IType.LABEL, /* values = { ISpecies.EMF, IKeyword.FSM }, */optional = true),
	@facet(name = "compile", type = IType.BOOL, optional = true),
	@facet(name = IKeyword.FREQUENCY, type = IType.INT, optional = true),
	@facet(name = IKeyword.SCHEDULES, type = IType.CONTAINER, optional = true),
	@facet(name = IKeyword.TOPOLOGY, type = IType.TOPOLOGY, optional = true) },
	omissible = IKeyword.NAME)
@validator(SpeciesValidator.class)
public class GamlSpecies extends AbstractSpecies {

	public static class SpeciesValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription desc) {
			// If torus is declared on a species other than "global", emit a warning
			IExpression torus = desc.getFacets().getExpr(TORUS);
			if ( torus != null ) {
				if ( desc.getKeyword().equals(IKeyword.SPECIES) || desc.getKeyword().equals(IKeyword.GRID) ) {
					desc.warning("'torus' property can only be specified for the model topology (i.e. in 'global')",
						IGamlIssue.WRONG_CONTEXT, TORUS);
				}
			}
			String name = desc.getName();
			if ( AbstractGamlAdditions.isUnaryOperator(name) ) {
				desc.error("The name '" + name + "' cannot be used for naming this " + desc.getKeyword() +
					", as the derived casting operator (" + name +
					"(...)) would conflict with an existing unary operator");
			}
		}
	}

	public GamlSpecies(final IDescription desc) {
		super(desc);
	}

	@Override
	public String getArchitectureName() {
		return getLiteral(IKeyword.CONTROL);
	}

	@Override
	public IExpression getFrequency() {
		return this.getFacet(IKeyword.FREQUENCY);
	}

	@Override
	public IExpression getSchedule() {
		return this.getFacet(IKeyword.SCHEDULES);
	}

	/**
	 * Method getSpecies()
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#getSpecies()
	 */
	@Override
	public ISpecies getSpecies() {
		return this;
	}

	/**
	 * Method getAgents()
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#getAgents()
	 */
	@Override
	public IContainer<?, ? extends IShape> getAgents() {
		return this;
	}

	/**
	 * Method accept()
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#accept(msi.gama.runtime.IScope, msi.gama.metamodel.shape.IShape, msi.gama.metamodel.shape.IShape)
	 */
	@Override
	public boolean accept(final IScope scope, final IShape source, final IShape a) {
		return getPopulation(scope).accept(scope, source, a);
	}

	/**
	 * Method filter()
	 * @see msi.gama.metamodel.topology.filter.IAgentFilter#filter(msi.gama.runtime.IScope, msi.gama.metamodel.shape.IShape, java.util.Collection)
	 */
	@Override
	public void filter(final IScope scope, final IShape source, final Collection<? extends IShape> results) {
		getPopulation(scope).filter(scope, source, results);
	}

	// @Override
	// public IPopulation getPopulation(final IScope scope, final String name) {
	// return getName().equals(name) ? getPopulation(scope) : null;
	// }

}
