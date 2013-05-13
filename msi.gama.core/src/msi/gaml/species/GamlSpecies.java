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

import java.util.Iterator;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

/**
 * The Class Species.
 * 
 * @author drogoul
 */
@symbol(name = { IKeyword.SPECIES, IKeyword.GLOBAL, IKeyword.GRID }, kind = ISymbolKind.SPECIES, with_sequence = true)
@inside(kinds = { ISymbolKind.MODEL, ISymbolKind.ENVIRONMENT, ISymbolKind.SPECIES })
@facets(value = { @facet(name = IKeyword.WIDTH, type = IType.INT, optional = true),
	@facet(name = IKeyword.HEIGHT, type = IType.INT, optional = true),
	@facet(name = IKeyword.NEIGHBOURS, type = IType.INT, optional = true),
	@facet(name = IKeyword.FILE, type = IType.FILE, optional = true),
	@facet(name = IKeyword.TORUS, type = IType.BOOL, optional = true),
	@facet(name = IKeyword.NAME, type = IType.ID, optional = false),
	@facet(name = IKeyword.PARENT, type = IType.ID, optional = true),
	@facet(name = IKeyword.EDGE_SPECIES, type = IType.ID, optional = true),
	@facet(name = IKeyword.SKILLS, type = IType.LIST, optional = true),
	@facet(name = "mirrors", type = IType.LIST, optional = true),
	@facet(name = IKeyword.CONTROL, type = IType.ID, /* values = { ISpecies.EMF, IKeyword.FSM }, */optional = true),
	@facet(name = "compile", type = IType.BOOL, optional = true),
	@facet(name = IKeyword.FREQUENCY, type = IType.INT, optional = true),
	@facet(name = IKeyword.SCHEDULES, type = IType.CONTAINER, optional = true),
	@facet(name = IKeyword.TOPOLOGY, type = IType.TOPOLOGY, optional = true) }, omissible = IKeyword.NAME)
@vars({ @var(name = IKeyword.NAME, type = IType.STRING) })
// FIXME Build a list of control architectures dynamically at startup and populate the values
// attribute
public class GamlSpecies extends AbstractSpecies {

	public GamlSpecies(final IDescription desc) {
		super(desc);
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
	public boolean extendsSpecies(final ISpecies s) {
		return s.getDescription().getType().isAssignableFrom(getDescription().getType());
	}

	@Override
	public IExpression getFrequency() {
		return this.getFacet(IKeyword.FREQUENCY);
	}

	@Override
	public IExpression getSchedule() {
		return this.getFacet(IKeyword.SCHEDULES);
	}

	@Override
	public IAgent get(final IScope scope, final Integer index) throws GamaRuntimeException {
		return getPopulation(scope).get(scope, index);
	}

	@Override
	public boolean contains(final IScope scope, final Object o) throws GamaRuntimeException {
		return getPopulation(scope).contains(scope, o);
	}

	@Override
	public IAgent first(final IScope scope) throws GamaRuntimeException {
		return getPopulation(scope).first(scope);
	}

	@Override
	public IAgent last(final IScope scope) throws GamaRuntimeException {
		return getPopulation(scope).last(scope);
	}

	@Override
	public int length(final IScope scope) {
		return getPopulation(scope).length(scope);
	}

	@Override
	public boolean isEmpty(final IScope scope) {
		return getPopulation(scope).isEmpty(scope);
	}

	@Override
	public IContainer<Integer, IAgent> reverse(final IScope scope) throws GamaRuntimeException {
		return getPopulation(scope).reverse(scope);
	}

	@Override
	public IAgent any(final IScope scope) {
		return getPopulation(scope).any(scope);
	}

	@Override
	public boolean checkBounds(final Integer index, final boolean forAdding) {
		return false;
	}

	@Override
	public void add(IScope scope, final Integer index, final Object value, final Object param, boolean all, boolean add)
		throws GamaRuntimeException {
		// NOT ALLOWED
	}

	@Override
	public void remove(IScope scope, Object index, final Object value, boolean all) throws GamaRuntimeException {
		// NOT ALLOWED
	}

	@Override
	public IMatrix matrixValue(final IScope scope) throws GamaRuntimeException {
		return getPopulation(scope).matrixValue(scope);
	}

	@Override
	public IMatrix matrixValue(final IScope scope, final ILocation preferredSize) throws GamaRuntimeException {
		return getPopulation(scope).matrixValue(scope, preferredSize);
	}

	@Override
	public Iterator<IAgent> iterator() {
		IScope scope = GAMA.obtainNewScope();
		if ( scope == null ) { return GamaList.EMPTY_LIST.iterator(); }
		Iterator<IAgent> result = scope.getSimulationScope().getPopulationFor(this).iterator();
		GAMA.releaseScope(scope);
		return result;
	}

	@Override
	public IAgent getFromIndicesList(final IScope scope, final IList indices) throws GamaRuntimeException {
		return (IAgent) getPopulation(scope).getFromIndicesList(scope, indices);
	}

	@Override
	public boolean isMirror() {
		return getDescription().isMirror();
	}

}
