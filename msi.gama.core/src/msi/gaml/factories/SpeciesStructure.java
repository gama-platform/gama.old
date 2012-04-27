/**
 * Created by drogoul, 20 déc. 2011
 * 
 */
package msi.gaml.factories;

import java.util.*;
import msi.gama.common.interfaces.*;

public class SpeciesStructure {

	private final ISyntacticElement node;

	private final List<SpeciesStructure> microSpecies = new ArrayList<SpeciesStructure>();

	private boolean isGrid = false;

	public SpeciesStructure(final ISyntacticElement node) {
		this.node = node;
		isGrid = node.getKeyword().equals(IKeyword.GRID);
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
		return node.getLabel(IKeyword.NAME);
	}

	@Override
	public String toString() {
		return "Species " + getName();
	}
}