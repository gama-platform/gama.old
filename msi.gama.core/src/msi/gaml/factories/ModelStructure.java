/**
 * Created by drogoul, 20 déc. 2011
 * 
 */
package msi.gaml.factories;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.util.GamaList;
import msi.gaml.descriptions.LabelExpressionDescription;

/**
 * 
 * The class ModelStructure. Creates a structure where all the syntactic nodes coming from different
 * models are put together
 * 
 * @author drogoul
 * @since 24 avr. 2012
 * 
 */
public class ModelStructure implements IKeyword {

	public final static Set<String> SPECIES_NODES = new HashSet(Arrays.asList(SPECIES, GRID, GUI_,
		BATCH, REMOTE, HEADLESS_UI));
	static final Set<String> GLOBAL_NODES = new HashSet(Arrays.asList(GLOBAL));
	static final Set<String> NON_RECURSIVE = new HashSet(Arrays.asList(OUTPUT, BATCH, GLOBAL,
		SPECIES, GRID, GUI_, REMOTE, HEADLESS_UI));
	static final Set<String> NODES_TO_REMOVE = new HashSet(Arrays.asList(INCLUDE, GLOBAL, SPECIES,
		GRID, BATCH, GUI_, REMOTE, HEADLESS_UI));
	static final List<String> NODES_TO_EXPAND = Arrays.asList(ENTITIES);
	private String name = "";
	private String modelPath = "";
	private final String projectPath;
	private final List<SpeciesStructure> species = new ArrayList();
	private final List<ISyntacticElement> globalNodes = new ArrayList();
	private List<ISyntacticElement> modelNodes = new ArrayList();
	private ISyntacticElement source;

	public ModelStructure(final String projectPath, final String modelPath,
		final List<ISyntacticElement> models) {
		this.projectPath = projectPath;
		this.modelPath = modelPath;
		for ( ISyntacticElement speciesNode : buildNodes(models) ) {
			addSpecies(buildSpeciesStructure(speciesNode));
		}
	}

	public ISyntacticElement getSource() {
		return source;
	}

	public String getPath() {
		return modelPath;
	}

	public String getProjectPath() {
		return projectPath;
	}

	private SpeciesStructure buildSpeciesStructure(final ISyntacticElement speciesNode) {
		SpeciesStructure species = new SpeciesStructure(speciesNode);
		// recursively accumulate micro-species
		List<ISyntacticElement> microSpecies = new GamaList<ISyntacticElement>();
		for ( String s : SPECIES_NODES ) {
			microSpecies.addAll(speciesNode.getChildren(s));
		}
		for ( ISyntacticElement microSpeciesNode : microSpecies ) {
			species.addMicroSpecies(buildSpeciesStructure(microSpeciesNode));
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

	public List<ISyntacticElement> getGlobalNodes() {
		return globalNodes;
	}

	public List<ISyntacticElement> getModelNodes() {
		return modelNodes;
	}

	@Override
	public String toString() {
		return "model " + name;
	}

	List<ISyntacticElement> buildNodes(final List<ISyntacticElement> documents) {
		List<ISyntacticElement> speciesNodes = new ArrayList();
		Collections.reverse(documents);
		for ( ISyntacticElement e : documents ) {
			if ( source == null ) {
				source = e;
				if ( source.getFacet(NAME) == null ) {
					source.setFacet(NAME, LabelExpressionDescription.create(source.getKeyword()));
				}
				setName(source.getLabel(NAME));
			}
			modelNodes.addAll(e.getChildren());
		}

		// EXPAND
		List<ISyntacticElement> expanded = new ArrayList();
		for ( int i = 0, n = modelNodes.size(); i < n; i++ ) {
			ISyntacticElement e = modelNodes.get(i);
			if ( NODES_TO_EXPAND.contains(e.getKeyword()) ) {
				List<ISyntacticElement> children = e.getChildren();
				expanded.addAll(children);
			} else {
				expanded.add(e);
			}
		}
		modelNodes = expanded;
		//
		speciesNodes.addAll(accumulateNodes(modelNodes, SPECIES_NODES));
		globalNodes.addAll(accumulateNodes(modelNodes, GLOBAL_NODES));
		removeUselessNodes(modelNodes, NODES_TO_REMOVE);
		return speciesNodes;
	}

	private List<ISyntacticElement> accumulateNodes(final List<ISyntacticElement> nodes,
		final Set<String> names) {
		final List result = new ArrayList();
		for ( final ISyntacticElement e : nodes ) {
			final String name = e.getKeyword();
			if ( names.contains(name) ) {
				result.add(e);
			}
			if ( !NON_RECURSIVE.contains(name) ) {
				result.addAll(accumulateNodes(e.getChildren(), names));
			}
		}
		return result;
	}

	private void removeUselessNodes(final List<ISyntacticElement> nodes, final Set<String> names) {
		Iterator<ISyntacticElement> i = nodes.iterator();
		while (i.hasNext()) {
			ISyntacticElement e = i.next();
			final String name = e.getKeyword();
			if ( names.contains(name) ) {
				i.remove();
			}
			if ( !NON_RECURSIVE.contains(name) ) {
				removeUselessNodes(e.getChildren(), names);
			}
		}
	}

}