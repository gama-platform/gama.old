/**
 * Created by drogoul, 20 déc. 2011
 * 
 */
package msi.gaml.factories;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gaml.compilation.GamlException;

/**
 * The class ModelFilesFactory.
 * 
 * @author drogoul
 * @since 20 déc. 2011
 * 
 */
public class ModelElements implements IKeyword {

	private final Map<String, ISyntacticElement> documents;
	static final List<String> GLOBAL_NODES = Arrays.asList(GLOBAL);
	static final List<String> NON_RECURSIVE = Arrays.asList(OUTPUT, BATCH, GLOBAL, SPECIES, GRID);
	static final List<String> NODES_TO_REMOVE = Arrays.asList(INCLUDE, GLOBAL, SPECIES, GRID);
	static final List<String> NODES_TO_EXPAND = Arrays.asList(ENTITIES);

	public ModelElements(final Map<String, ISyntacticElement> nodes) {
		documents = nodes;
	}

	private List<String> buildFileList(final String fileName, final ErrorCollector collect) {
		List<String> filesToParse = new ArrayList<String>();
		buildFileList(filesToParse, fileName, collect);
		return filesToParse;
	}

	Map<String, List<ISyntacticElement>> getNodesFrom(final String fileName,
		final ErrorCollector collect) {
		ISyntacticElement root = null;
		List<String> filesToParse;
		filesToParse = buildFileList(fileName, collect);
		List<ISyntacticElement> allNodes = new ArrayList();
		List<ISyntacticElement> speciesNodes = new ArrayList();
		List<ISyntacticElement> globalNodes = new ArrayList();
		for ( final String fn : filesToParse ) {
			ISyntacticElement doc = documents.get(fn);
			if ( doc == null ) {
				continue;
			}
			if ( root == null ) {
				root = doc;
				if ( root.getAttribute(NAME) == null ) {
					root.setAttribute(NAME, root.getName(), null);
				}
			}
			List<ISyntacticElement> listOfNodes = doc.getChildren();
			allNodes.addAll(/* 0, */listOfNodes);
		}
		// EXPAND
		List<ISyntacticElement> expanded = new ArrayList();
		for ( int i = 0, n = allNodes.size(); i < n; i++ ) {
			ISyntacticElement e = allNodes.get(i);
			if ( NODES_TO_EXPAND.contains(e.getName()) ) {
				List<ISyntacticElement> children = e.getChildren();
				expanded.addAll(children);
			} else {
				expanded.add(e);
			}
		}
		allNodes = expanded;
		//
		speciesNodes.addAll(accumulateNodes(allNodes, ModelFactory.SPECIES_NODES));
		globalNodes.addAll(accumulateNodes(allNodes, GLOBAL_NODES));
		removeUselessNodes(allNodes, NODES_TO_REMOVE);
		Map<String, List<ISyntacticElement>> result = new HashMap();
		result.put(NAME, Arrays.asList(root));
		result.put(SPECIES, speciesNodes);
		result.put(GLOBAL, globalNodes);
		result.put(MODEL, allNodes);
		return result;
	}

	// public ModelStructure getModelStructureFrom(final String fileName) throws GamlException {
	// return new ModelStructure(fileName, getNodesFrom(fileName));
	// }

	private List<ISyntacticElement> accumulateNodes(final List<ISyntacticElement> nodes,
		final List<String> names) {
		final List result = new ArrayList();
		for ( final ISyntacticElement e : nodes ) {
			final String name = e.getName();
			if ( names.contains(name) ) {
				result.add(e);
			}
			if ( !NON_RECURSIVE.contains(name) ) {
				result.addAll(accumulateNodes(e.getChildren(), names));
			}
		}
		return result;
	}

	private void removeUselessNodes(final List<ISyntacticElement> nodes, final List<String> names) {
		Iterator<ISyntacticElement> i = nodes.iterator();
		while (i.hasNext()) {
			ISyntacticElement e = i.next();
			final String name = e.getName();
			if ( names.contains(name) ) {
				i.remove();
			}
			if ( !NON_RECURSIVE.contains(name) ) {
				removeUselessNodes(e.getChildren(), names);
			}
		}
	}

	private void buildFileList(final List<String> filesToParse, final String fileName,
		final ErrorCollector collect) {
		if ( filesToParse.contains(fileName) ) { return; }
		filesToParse.add(0, fileName);
		ISyntacticElement doc = documents.get(fileName);
		if ( doc == null ) { return; }
		for ( final ISyntacticElement e : doc.getChildren("include") ) {
			String s = e.getAttribute("file");
			if ( s != null ) {
				try {
					s = FileUtils.constructAbsoluteFilePath(s, fileName, true);
				} catch (GamlException e1) {
					collect.add(e1);
					continue;
				}
				buildFileList(filesToParse, s, collect);
			}
		}
	}

}
