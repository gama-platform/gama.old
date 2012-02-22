/**
 * Created by drogoul, 20 déc. 2011
 * 
 */
package msi.gaml.factories;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.ErrorCollector;
import msi.gama.util.GamaList;
import msi.gaml.compilation.GamlException;

public class ModelStructure {

	private String name = "";
	private String path = "";
	private final List<SpeciesStructure> species = new ArrayList();
	private List<ISyntacticElement> globalNodes;
	private List<ISyntacticElement> modelNodes;

	public ModelStructure(final String fileName, final Map<String, ISyntacticElement> nodes,
		final ErrorCollector collect) {
		path = fileName;
		init(new ModelElements(nodes).getNodesFrom(fileName, collect), collect);
	}

	private void init(final Map<String, List<ISyntacticElement>> nodes, final ErrorCollector collect) {
		setName(nodes.get(IKeyword.NAME).get(0).getAttribute(IKeyword.NAME));
		setGlobalNodes(nodes.get(IKeyword.GLOBAL));
		setModelNodes(nodes.get(IKeyword.MODEL));
		for ( ISyntacticElement speciesNode : nodes.get(IKeyword.SPECIES) ) {
			addSpecies(buildSpeciesStructure(speciesNode, collect));
		}
	}

	public String getPath() {
		return path;
	}

	private SpeciesStructure buildSpeciesStructure(final ISyntacticElement speciesNode,
		final ErrorCollector collect) {
		if ( speciesNode == null ) {
			collect.add(new GamlException("Species element is null!", (Throwable) null));
			return null;
		}

		SpeciesStructure species = new SpeciesStructure(speciesNode, collect);

		// recursively accumulate micro-species
		List<ISyntacticElement> microSpecies = new GamaList<ISyntacticElement>();
		microSpecies.addAll(speciesNode.getChildren(IKeyword.SPECIES));
		microSpecies.addAll(speciesNode.getChildren(IKeyword.GRID));
		for ( ISyntacticElement microSpeciesNode : microSpecies ) {
			species.addMicroSpecies(buildSpeciesStructure(microSpeciesNode, collect));
		}

		return species;
	}

	public void setName(final String name) {
		if ( name == null ) { return; }

		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addSpecies(final SpeciesStructure s) {
		if ( s == null ) { return; }
		this.species.add(s);
	}

	public List<SpeciesStructure> getSpecies() {
		return species;
	}

	public void setGlobalNodes(final List<ISyntacticElement> globalNodes) {
		this.globalNodes = globalNodes;
	}

	public List<ISyntacticElement> getGlobalNodes() {
		return globalNodes;
	}

	public void setModelNodes(final List<ISyntacticElement> modelNodes) {
		this.modelNodes = modelNodes;
	}

	public List<ISyntacticElement> getModelNodes() {
		return modelNodes;
	}

	@Override
	public String toString() {
		return "model " + name;
	}
}