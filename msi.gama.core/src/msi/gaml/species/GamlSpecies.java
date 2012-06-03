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
package msi.gaml.species;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.*;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

/**
 * The Class Species.
 * 
 * @author drogoul
 */
@symbol(name = { IKeyword.SPECIES, IKeyword.GLOBAL, IKeyword.GRID }, kind = ISymbolKind.SPECIES, with_sequence = true)
@inside(kinds = { ISymbolKind.MODEL, ISymbolKind.ENVIRONMENT, ISymbolKind.SPECIES }, symbols = { IKeyword.ENTITIES })
@facets(value = {
	@facet(name = IKeyword.WIDTH, type = IType.INT_STR, optional = true),
	@facet(name = IKeyword.HEIGHT, type = IType.INT_STR, optional = true),
	@facet(name = IKeyword.NEIGHBOURS, type = IType.INT_STR, optional = true),
	@facet(name = IKeyword.TORUS, type = IType.BOOL_STR, optional = true),
	@facet(name = IKeyword.NAME, type = IType.ID, optional = false),
	@facet(name = IKeyword.PARENT, type = IType.ID, optional = true),
	@facet(name = IKeyword.SKILLS, type = IType.LABEL, optional = true),
	// FIXME Build a list of control architectures dynamically at startup and populate the values
	// attribute
	@facet(name = IKeyword.CONTROL, type = IType.ID, /* values = { ISpecies.EMF, IKeyword.FSM }, */optional = true),
	@facet(name = "compile", type = IType.BOOL_STR, optional = true),
	@facet(name = IKeyword.FREQUENCY, type = IType.INT_STR, optional = true),
	@facet(name = IKeyword.SCHEDULES, type = IType.CONTAINER_STR, optional = true),
	@facet(name = IKeyword.TOPOLOGY, type = IType.TOPOLOGY_STR, optional = true) }, omissible = IKeyword.NAME)
@vars({ @var(name = IKeyword.NAME, type = IType.STRING_STR) })
public class GamlSpecies extends AbstractSpecies {

	public GamlSpecies(final IDescription desc) {
		super(desc);
	}

	@Override
	public boolean isGlobal() {
		return getName().equals(IKeyword.WORLD_SPECIES);
	}

	@Override
	public String getParentName() {
		return getDescription().getParentName();
	}

	@Override
	public String getArchitectureName() {
		return getLiteral(IKeyword.CONTROL);
	}

	@Override
	@getter("name")
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
		return this.getFacet(IKeyword.FREQUENCY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see msi.gama.interfaces.ISpecies#getSchedule()
	 */
	@Override
	public IExpression getSchedule() {
		return this.getFacet(IKeyword.SCHEDULES);
	}
}
