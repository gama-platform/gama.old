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
package msi.gama.outputs.layers;

import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IAspect;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 9 nov. 2009
 * 
 * @todo Description
 * 
 */
@symbol(name = IKeyword.POPULATION, kind = ISymbolKind.LAYER, with_sequence = true)
@inside(symbols = { IKeyword.DISPLAY, IKeyword.POPULATION })
@facets(value = { @facet(name = IKeyword.POSITION, type = IType.POINT, optional = true),
	@facet(name = IKeyword.SIZE, type = IType.POINT, optional = true),
	@facet(name = IKeyword.TRANSPARENCY, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.SPECIES, type = IType.SPECIES, optional = false),
	@facet(name = IKeyword.ASPECT, type = IType.ID, optional = true),
	@facet(name = IKeyword.Z, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.REFRESH, type = IType.BOOL, optional = true) }, omissible = IKeyword.SPECIES)
public class SpeciesLayerStatement extends AgentLayerStatement {

	private IAspect aspect;

	protected ISpecies hostSpecies;
	protected ISpecies species;
	protected List<SpeciesLayerStatement> microSpeciesLayers;
	protected List<GridLayerStatement> gridLayers;
	protected List<AbstractLayerStatement> subLayers;

	public SpeciesLayerStatement(final IDescription desc) throws GamaRuntimeException {
		super(desc);
		setName(getFacet(IKeyword.SPECIES).literalValue());
		microSpeciesLayers = new GamaList<SpeciesLayerStatement>();
		gridLayers = new GamaList<GridLayerStatement>();
	}

	@Override
	public boolean _init(final IScope scope) throws GamaRuntimeException {
		// top level species layer is a direct micro-species of "world_species" for sure
		if ( hostSpecies == null ) {
			hostSpecies = scope.getSimulationScope().getSpecies();
		}

		species = hostSpecies.getMicroSpecies(getName());
		if ( species == null ) { throw GamaRuntimeException.error("not a suitable species to display: " + getName()); }
		if ( super._init(scope) ) {
			for ( final SpeciesLayerStatement microLayer : microSpeciesLayers ) {
				microLayer.setHostSpecies(species);
				if ( !scope.init(microLayer) ) { return false; }
			}
		}
		return true;
	}

	@Override
	public boolean _step(final IScope scope) throws GamaRuntimeException {
		if ( super._step(scope) ) {
			for ( final SpeciesLayerStatement microLayer : microSpeciesLayers ) {
				scope.step(microLayer);
				// if ( !scope.step(microLayer) ) { return false; }
			}
		} else {
			return false;
		}
		return true;
	}

	@Override
	public boolean agentsHaveChanged() {
		return true;
		// return population.populationHasChanged();
	}

	@Override
	public List<? extends IAgent> computeAgents(final IScope sim) {
		return GamaList.EMPTY_LIST;
	}

	@Override
	public short getType() {
		return ILayerStatement.SPECIES;
	}

	public List<String> getAspects() {
		return species.getAspectNames();
	}

	@Override
	public void setAspect(final String currentAspect) {
		super.setAspect(currentAspect);
	}

	@Override
	public void computeAspectName(final IScope sim) throws GamaRuntimeException {
		super.computeAspectName(sim);
		aspect = species.getAspect(constantAspectName);
	}

	public IAspect getAspect() {
		return aspect;
	}

	public void setHostSpecies(final ISpecies hostSpecies) {
		this.hostSpecies = hostSpecies;
	}

	public ISpecies getSpecies() {
		return species;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		final List<SpeciesLayerStatement> microL = new GamaList<SpeciesLayerStatement>();
		final List<GridLayerStatement> gridL = new GamaList<GridLayerStatement>();

		for ( final ISymbol c : commands ) {
			if ( c instanceof SpeciesLayerStatement ) {
				microL.add((SpeciesLayerStatement) c);
			} else if ( c instanceof GridLayerStatement ) {
				gridL.add((GridLayerStatement) c);
			}
		}

		setMicroSpeciesLayers(microL);
		setGridLayers(gridL);
	}

	private void setMicroSpeciesLayers(final List<SpeciesLayerStatement> layers) {
		if ( layers == null ) { return; }

		microSpeciesLayers.clear();
		microSpeciesLayers.addAll(layers);
	}

	private void setGridLayers(final List<GridLayerStatement> layers) {
		if ( layers == null ) { return; }

		gridLayers.clear();
		gridLayers.addAll(layers);
	}

	/**
	 * Returns a list of micro-species layers declared as sub-layers.
	 * 
	 * @return
	 */
	public List<SpeciesLayerStatement> getMicroSpeciesLayers() {
		return microSpeciesLayers;
	}

	/**
	 * Returns a list of grid layers declared as sub-layers.
	 * 
	 * @return
	 */
	public List<GridLayerStatement> getGridLayers() {
		return gridLayers;
	}

	/**
	 * Returns a list of grid and micro-species layers declared as sub-layers.
	 * The grid layers are put ahead in the returned list.
	 * 
	 * @return
	 */
	public List<AbstractLayerStatement> getSubLayers() {
		if ( subLayers == null ) {
			subLayers = new GamaList<AbstractLayerStatement>();
			subLayers.addAll(gridLayers);
			subLayers.addAll(microSpeciesLayers);
		}

		return subLayers;
	}

	@Override
	public String toString() {
		// StringBuffer sb = new StringBuffer();
		return "SpeciesDisplayLayer species: " + getName();
	}
}
