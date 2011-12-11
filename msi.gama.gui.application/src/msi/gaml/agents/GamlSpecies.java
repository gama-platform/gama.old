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
package msi.gaml.agents;

import msi.gama.agents.AbstractSpecies;
import msi.gama.environment.ITopology;
import msi.gama.interfaces.*;
import msi.gama.internal.descriptions.SpeciesDescription;
import msi.gama.precompiler.GamlAnnotations.base;
import msi.gama.precompiler.GamlAnnotations.commands;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.GamlAnnotations.with_sequence;
import msi.gama.precompiler.*;
import msi.gama.skills.GridSkill;
import msi.gaml.commands.*;
import msi.gaml.control.ReflexCommand;

/**
 * The Class Species.
 * 
 * @author drogoul
 */
@with_sequence
@base(GamlAgent.class)
@symbol(name = { ISymbol.SPECIES, ISymbol.GLOBAL, ISymbol.GRID }, kind = ISymbolKind.SPECIES)
// @inside(kinds = { ISymbolKind.MODEL, ISymbolKind.ENVIRONMENT_SECTION }, symbols =
// ISymbol.ENTITIES)
@inside(kinds = { ISymbolKind.MODEL, ISymbolKind.ENVIRONMENT, ISymbolKind.SPECIES }, symbols = { ISymbol.ENTITIES })
@commands({ AspectCommand.class, ActionCommand.class, PrimitiveCommand.class, ReflexCommand.class })
@facets({
	@facet(name = ITopology.WIDTH, type = IType.INT_STR, optional = true),
	@facet(name = ITopology.HEIGHT, type = IType.INT_STR, optional = true),
	@facet(name = GridSkill.NEIGHBOURS, type = IType.INT_STR, optional = true),
	@facet(name = ITopology.TORUS, type = IType.BOOL_STR, optional = true),
	@facet(name = ISymbol.NAME, type = IType.ID, optional = false),
	@facet(name = ISpecies.PARENT, type = IType.ID, optional = true),
	@facet(name = ISpecies.SKILLS, type = IType.LABEL, optional = true),
	@facet(name = ISpecies.CONTROL, type = IType.ID, values = { ISpecies.EMF, ISpecies.FSM }, optional = true),
	@facet(name = ISpecies.BASE, type = IType.LABEL, optional = true),
	@facet(name = ISpecies.FREQUENCY, type = IType.INT_STR, optional = true),
	@facet(name = ISpecies.SCHEDULES, type = IType.LIST_STR, optional = true),
	@facet(name = ISpecies.TOPOLOGY, type = IType.TOPOLOGY_STR, optional = true)})
@vars({ @var(name = ISymbol.NAME, type = IType.STRING_STR) })
public class GamlSpecies extends AbstractSpecies {

	public GamlSpecies(final IDescription desc) {
		super(desc);
	}

	@Override
	public boolean isGlobal() {
		return getName().equals(ISymbol.WORLD_SPECIES_NAME);
	}

	@Override
	public String getParentName() {
		return getDescription().getParentName();
	}

	@Override
	public String getControlName() {
		return getLiteral(ISpecies.CONTROL);
	}

	@Override
	@getter(var = "name")
	public String getName() {
		return super.getName();
	}

	@Override
	public SpeciesDescription getDescription() {
		return (SpeciesDescription) description;
	}

	@Override
	public boolean extendsSpecies(final ISpecies s) {
		return getDescription().getSelfWithParents().contains(s.getDescription());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.ISpecies#getFrequency()
	 */
	@Override
	public IExpression getFrequency() {
		return this.getFacet(ISpecies.FREQUENCY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.ISpecies#getSchedule()
	 */
	@Override
	public IExpression getSchedule() {
		return this.getFacet(ISpecies.SCHEDULES);
	}

}
