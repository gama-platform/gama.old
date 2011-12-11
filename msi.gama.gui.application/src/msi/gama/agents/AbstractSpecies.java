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
package msi.gama.agents;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.internal.descriptions.SpeciesDescription;
import msi.gama.internal.types.Types;
import msi.gama.kernel.exceptions.GamlException;
import msi.gama.util.*;

/**
 * Written by drogoul Modified on 29 déc. 2010
 * 
 * @todo Description
 * 
 */
public abstract class AbstractSpecies extends ExecutionContext implements ISpecies {

	protected boolean isGrid;

	protected Map<String, ISpecies> microSpecies;

	private boolean isCopy = false;

	protected ISpecies parentSpecies;

	private List<ISpecies> peerSpecies;

	private Map<String, ISpecies> visibleSpecies;

	public AbstractSpecies(final IDescription description) {
		super(description);

		setName(description.getName());
		isGrid = description.getFacets().equals(ISymbol.KEYWORD, ISymbol.GRID);
		isCopy = ((SpeciesDescription) description).isCopy();
	}

	@Override
	protected void initFields() {
		super.initFields();
		microSpecies = new HashMap<String, ISpecies>();
	}

	@Override
	public IType type() {
		return Types.get(IType.SPECIES);
	}

	@Override
	public GamaList listValue(final IScope scope) {
		IAgent a = scope.getAgentScope();
		if ( a == null ) { return GamaList.EMPTY_LIST; }
		IPopulation p = a.getPopulationFor(this);
		if ( p == null ) { return GamaList.EMPTY_LIST; }
		return p.getAgentsList();
	}

	@Override
	public String stringValue() {
		return name;
	}

	@Override
	public GamaMap mapValue(final IScope scope) {
		List<IAgent> agents;
		try {
			agents = scope.getAgentScope().getPopulationFor(this).getAgentsList();
		} catch (NullPointerException npe) {
			agents = GamaList.EMPTY_LIST;
		}

		// Default behavior : Returns a map containing the names of agents as keys and the
		// agents themselves as values
		final GamaMap result = new GamaMap();
		for ( IAgent agent : agents ) {
			result.put(agent.getName(), agent);
		}
		return result;
	}

	@Override
	public boolean isGrid() {
		return isGrid;
	}

	@Override
	public boolean isCopy() {
		return isCopy;
	}

	@Override
	public String toJava() {
		return "SimulationManager.getFrontmostSimulation().getSpecies(" + name + ")";
	}

	@Override
	public String toGaml() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public ISpecies copy() {
		return this;
		// Species are immutable
	}

	private void addOneMicroSpecies(final ISpecies oneMicroSpecies) {
		microSpecies.put(oneMicroSpecies.getName(), oneMicroSpecies);
	}

	@Override
	public void addChild(final ISymbol s) {
		if ( s instanceof ISpecies ) {
			addOneMicroSpecies((ISpecies) s);
		} else {
			super.addChild(s);
		}
	}

	/**
	 * @return allPureMicroSpecies + delegations.
	 */
	@Override
	public List<ISpecies> getMicroSpecies() {
		List<ISpecies> retVal = new GamaList<ISpecies>();
		retVal.addAll(microSpecies.values());
		return retVal;
	}

	@Override
	public List<String> getMicroSpeciesNames() {
		List<String> retVal = new GamaList<String>();
		retVal.addAll(microSpecies.keySet());
		return retVal;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) throws GamlException {
		super.setChildren(commands);

		for ( ISymbol c : commands ) {
			if ( c instanceof ISpecies ) {
				((ISpecies) c).setMacroSpecies(this);
			}
		}
	}

	/**
	 * Returns a micro-species with the specified name or null otherwise.
	 * 
	 * @param microSpeciesName
	 * @return a species or null
	 */
	@Override
	public ISpecies getMicroSpecies(final String microSpeciesName) {
		for ( ISpecies microSpec : microSpecies.values() ) {
			if ( microSpec.getName().equals(microSpeciesName) ) { return microSpec; }
		}

		return null;
	}

	/**
	 * @see ISpecies
	 */
	@Override
	public boolean containMicroSpecies(final ISpecies species) {
		return microSpecies.values().contains(species);
	}

	@Override
	public boolean hasMicroSpecies() {
		return !microSpecies.isEmpty();
	}

	@Override
	public SpeciesDescription getDescription() {
		return (SpeciesDescription) description;
	}

	@Override
	public List<ISpecies> getPeersSpecies() {
		if ( macroSpecies == null ) { return GamaList.EMPTY_LIST; }

		if ( peerSpecies == null ) {
			peerSpecies = new GamaList<ISpecies>();
			for ( ISpecies microSpec : macroSpecies.getMicroSpecies() ) {
				if ( !microSpec.equals(this) ) {
					peerSpecies.add(microSpec);
				}
			}
		}

		return peerSpecies;
	}

	/**
	 * Finds the corresponding ISpecies of a SpeciesDescription.
	 * 
	 * This method is invoked only by the "getParent()" method to find the parent species of this
	 * species.
	 * A species can be a sub species of
	 * either its peer species
	 * or one species situated higher in the species tree.
	 * 
	 * @param speciesDesc
	 * @return
	 */
	private ISpecies findParentSpecies(final SpeciesDescription speciesDesc) {
		getPeersSpecies();

		// Verify the peer species.
		for ( ISpecies p : peerSpecies ) {
			if ( p.getDescription().equals(speciesDesc) ) { return p; }
		}

		ISpecies macroSpecLevel = macroSpecies;
		while (macroSpecLevel != null) {
			// Verify the macro-species
			if ( macroSpecLevel.getDescription().equals(speciesDesc) ) { return macroSpecLevel; }

			// Verify the peers of macro-species
			List<ISpecies> macroLevelPeers = macroSpecLevel.getPeersSpecies();
			for ( ISpecies p : macroLevelPeers ) {
				if ( p.getDescription().equals(speciesDesc) ) { return p; }
			}

			macroSpecLevel = macroSpecLevel.getMacroSpecies();
		}

		return null;
	}

	@Override
	public ISpecies getParentSpecies() {
		if ( parentSpecies != null ) { return parentSpecies; }

		SpeciesDescription parentSpecDesc = getDescription().getParentSpecies();
		if ( parentSpecDesc == null ) { return null; }

		parentSpecies = findParentSpecies(parentSpecDesc);
		return parentSpecies;
	}

	@Override
	public ISpecies getPeerSpecies(final String peerName) {
		for ( ISpecies p : getPeersSpecies() ) {
			if ( p.getName().equals(peerName) ) { return p; }
		}

		return null;
	}

	@Override
	public Collection<ISpecies> getVisibleSpecies() {
		if ( visibleSpecies == null ) {
			visibleSpecies = new HashMap<String, ISpecies>();

			ISpecies currentSpecies = this;
			while (!currentSpecies.getName().equals(ISymbol.WORLD_SPECIES_NAME)) {
				for ( ISpecies microSpec : currentSpecies.getMicroSpecies() ) {
					visibleSpecies.put(microSpec.getName(), microSpec);
				}

				currentSpecies = currentSpecies.getMacroSpecies();
			}

			visibleSpecies.put(currentSpecies.getName(), currentSpecies);
		}

		return visibleSpecies.values();
	}

	@Override
	public ISpecies getVisibleSpecies(final String speciesName) {
		;
		getVisibleSpecies();
		return visibleSpecies.get(speciesName);
	}

	@Override
	public int getLevel() {
		return this.getDescription().getLevel();
	}

	@Override
	public boolean equals(final Object other) {
		/**
		 * species name is unique
		 */
		if ( other instanceof ISpecies ) { return this.getName().equals(
			((ISpecies) other).getName()); }

		return false;
	}

	@Override
	public void dispose() {
		super.dispose();

		// TODO dispose micro_species first???
		microSpecies.clear();
		microSpecies = null;
	}
}