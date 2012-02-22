/**
 * Created by drogoul, 20 déc. 2011
 * 
 */
package msi.gaml.factories;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.ErrorCollector;
import msi.gaml.compilation.GamlException;

public class SpeciesStructure {

	private final ISyntacticElement node;

	private final List<SpeciesStructure> microSpecies;

	private boolean isGrid = false;

	public SpeciesStructure(final ISyntacticElement node, final ErrorCollector collect) {
		microSpecies = new ArrayList<SpeciesStructure>();
		if ( node == null ) {
			collect.add(new GamlException("Species element is null!", node));
			this.node = null;
			return;
		}
		this.node = node;
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