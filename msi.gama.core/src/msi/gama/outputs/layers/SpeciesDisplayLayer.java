/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
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
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.commands.IAspect;
import msi.gaml.compilation.*;
import msi.gama.precompiler.GamlAnnotations.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 9 nov. 2009
 * 
 * @todo Description
 * 
 */
@symbol(name = IKeyword.SPECIES, kind = ISymbolKind.LAYER)
@inside(symbols = { IKeyword.DISPLAY, IKeyword.SPECIES })
@facets({ @facet(name = IKeyword.POSITION, type = IType.POINT_STR, optional = true),
	@facet(name = IKeyword.SIZE, type = IType.POINT_STR, optional = true),
	@facet(name = IKeyword.TRANSPARENCY, type = IType.FLOAT_STR, optional = true),
	@facet(name = IKeyword.NAME, type = IType.ID, optional = false),
	@facet(name = IKeyword.ASPECT, type = IType.ID, optional = true) })
@with_sequence
public class SpeciesDisplayLayer extends AgentDisplayLayer {

	private IAspect aspect;

	protected ISpecies hostSpecies;
	protected ISpecies species;
	protected List<SpeciesDisplayLayer> microSpeciesLayers;
	protected List<GridDisplayLayer> gridLayers;
	protected List<AbstractDisplayLayer> subLayers;

	public SpeciesDisplayLayer(/* final ISymbol context, */final IDescription desc)
		throws GamaRuntimeException {
		super(/* context, */desc);
		microSpeciesLayers = new GamaList<SpeciesDisplayLayer>();
		gridLayers = new GamaList<GridDisplayLayer>();
	}

	@Override
	public void prepare(final IDisplayOutput out, final IScope sim) throws GamaRuntimeException {
		// TODO search for hostSpecies; may have several hostSpecies incase of micro-species are
		// inherited

		// top level species layer is a direct micro-species of "world_species" for sure
		if ( hostSpecies == null ) {
			hostSpecies = sim.getWorldScope().getSpecies();
		}

		species = hostSpecies.getMicroSpecies(getName());
		if ( species == null ) { throw new GamaRuntimeException(
			"not a suitable species to display: " + getName()); }
		super.prepare(out, sim);

		for ( SpeciesDisplayLayer microLayer : microSpeciesLayers ) {
			microLayer.setHostSpecies(species);
			microLayer.prepare(out, sim);
		}
	}

	@Override
	public void compute(final IScope scope, final long cycle) throws GamaRuntimeException {
		super.compute(scope, cycle);

		for ( SpeciesDisplayLayer microLayer : microSpeciesLayers ) {
			microLayer.compute(scope, cycle);
		}
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
		return IDisplay.SPECIES;
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
		List<SpeciesDisplayLayer> microL = new GamaList<SpeciesDisplayLayer>();
		List<GridDisplayLayer> gridL = new GamaList<GridDisplayLayer>();

		for ( ISymbol c : commands ) {
			if ( c instanceof SpeciesDisplayLayer ) {
				microL.add((SpeciesDisplayLayer) c);
			} else if ( c instanceof GridDisplayLayer ) {
				gridL.add((GridDisplayLayer) c);
			}
		}

		setMicroSpeciesLayers(microL);
		setGridLayers(gridL);
	}

	private void setMicroSpeciesLayers(final List<SpeciesDisplayLayer> layers) {
		if ( layers == null ) { return; }

		microSpeciesLayers.clear();
		microSpeciesLayers.addAll(layers);
	}

	private void setGridLayers(final List<GridDisplayLayer> layers) {
		if ( layers == null ) { return; }

		gridLayers.clear();
		gridLayers.addAll(layers);
	}

	/**
	 * Returns a list of micro-species layers declared as sub-layers.
	 * 
	 * @return
	 */
	public List<SpeciesDisplayLayer> getMicroSpeciesLayers() {
		return microSpeciesLayers;
	}

	/**
	 * Returns a list of grid layers declared as sub-layers.
	 * 
	 * @return
	 */
	public List<GridDisplayLayer> getGridLayers() {
		return gridLayers;
	}

	/**
	 * Returns a list of grid and micro-species layers declared as sub-layers.
	 * The grid layers are put ahead in the returned list.
	 * 
	 * @return
	 */
	public List<AbstractDisplayLayer> getSubLayers() {
		if ( subLayers == null ) {
			subLayers = new GamaList<AbstractDisplayLayer>();
			subLayers.addAll(gridLayers);
			subLayers.addAll(microSpeciesLayers);
		}

		return subLayers;
	}

	@Override
	public String toString() {
		// StringBuffer sb = new StringBuffer();
		return "SpeciesDisplayLayer species: " + this.getFacet(IKeyword.NAME).literalValue();
	}
}
