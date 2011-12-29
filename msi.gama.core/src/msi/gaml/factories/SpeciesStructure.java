/**
 * Created by drogoul, 20 déc. 2011
 * 
 */
package msi.gaml.factories;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gaml.compilation.GamlException;

public class SpeciesStructure {

	private final ISyntacticElement node;

	private final List<SpeciesStructure> microSpecies;

	private boolean isGrid = false;

	public SpeciesStructure(final ISyntacticElement node) throws GamlException {
		if ( node == null ) { throw new GamlException("Species element is null!"); }

		this.node = node;
		microSpecies = new ArrayList<SpeciesStructure>();
		isGrid = node.getName().equals(IKeyword.GRID);
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
		return node.getAttribute(IKeyword.NAME);
	}

	@Override
	public String toString() {
		return "Species " + getName();
	}
}