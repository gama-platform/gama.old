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
package msi.gama.kernel;

import java.io.File;
import java.util.*;
import msi.gama.factories.ModelFactory.ModelStructure;
import msi.gama.factories.SpeciesFactory.SpeciesStructure;
import msi.gama.gui.application.GUI;
import msi.gama.interfaces.ISymbol;
import msi.gama.internal.types.GamaFileType;
import msi.gama.kernel.exceptions.GamlException;
import msi.gama.lang.gaml.ui.GamlResourceSet;
import msi.gama.lang.utils.*;
import msi.gama.util.GamaList;
import org.eclipse.core.resources.IFile;

/**
 * Written by drogoul Modified on 18 janv. 2009
 * 
 * @todo Description
 */
public class ModelFileManager {

	private static ModelFileManager instance = new ModelFileManager();
	private Map<String, ISyntacticElement> documents;
	public final static List<String> SPECIES_NODES = Arrays.asList(ISymbol.SPECIES, ISymbol.GRID);
	private static final List<String> GLOBAL_NODES = Arrays.asList(ISymbol.GLOBAL);
	private static final List<String> NON_RECURSIVE_NODES = Arrays.asList(ISymbol.OUTPUT,
		ISymbol.BATCH, ISymbol.GLOBAL, ISymbol.SPECIES, ISymbol.GRID);
	private static final List<String> NODES_TO_REMOVE = Arrays.asList(ISymbol.INCLUDE,
		ISymbol.GLOBAL, ISymbol.SPECIES, ISymbol.GRID);
	private static final List<String> NODES_TO_EXPAND = Arrays.asList(ISymbol.ENTITIES);

	/**
	 * Instantiates a new model file manager.
	 */
	private ModelFileManager() {
		documents = new HashMap<String, ISyntacticElement>();
	}

	public static ModelFileManager getInstance() {
		return instance;
	}

	public ISyntacticElement parseDoc(final String fileName) throws GamlException {
		// if the document is already parsed (.gaml)
		ISyntacticElement doc = documents.get(fileName);
		if ( doc == null ) {
			try {
				doc = Convert.parseXml(new File(fileName));
			} catch (final Exception e) {
				throw new GamlException(fileName + ": " + e.getMessage());
			}
		}
		return doc;
	}

	public List<String> buildFileList(final String fileName,
		final Map<String, ISyntacticElement> currentDocs) throws GamlException {
		List<String> filesToParse = new ArrayList<String>();
		buildFileList(filesToParse, fileName, currentDocs);
		// OutputManager.debug("Order of files to parse:" + filesToParse);
		return filesToParse;
	}

	private Map<String, List<ISyntacticElement>> getNodesFrom(final String fileName)
		throws GamlException {
		ISyntacticElement root = null;
		List<String> filesToParse;
		Map<String, ISyntacticElement> currentDocs = new HashMap();
		filesToParse = buildFileList(fileName, currentDocs);
		List<ISyntacticElement> allNodes = new ArrayList();
		List<ISyntacticElement> speciesNodes = new ArrayList();
		List<ISyntacticElement> globalNodes = new ArrayList();
		for ( final String fn : filesToParse ) {
			ISyntacticElement doc;
			if ( currentDocs.containsKey(fn) ) {
				doc = currentDocs.get(fn);
			} else {
				doc = parseDoc(fn);
			}
			if ( root == null ) {
				root = doc;
				if ( root.getAttribute(ISymbol.NAME) == null ) {
					root.setAttribute(ISymbol.NAME, root.getName());
				}
			}
			List<ISyntacticElement> listOfNodes = doc.getChildren();
			allNodes.addAll(/* 0, */listOfNodes);
		}

		expandNodes(allNodes, NODES_TO_EXPAND);
		speciesNodes.addAll(accumulateNodes(allNodes, SPECIES_NODES));
		globalNodes.addAll(accumulateNodes(allNodes, GLOBAL_NODES));
		removeUselessNodes(allNodes, NODES_TO_REMOVE);
		Map<String, List<ISyntacticElement>> result = new HashMap();
		result.put(ISymbol.NAME, GamaList.with(root));
		result.put(ISymbol.SPECIES, speciesNodes);
		result.put(ISymbol.GLOBAL, globalNodes);
		result.put(ISymbol.MODEL, allNodes);
		return result;
	}

	/**
	 * Recursively build species structure from a species node.
	 * 
	 * @param speciesNode
	 * @return
	 * @throws GamlException
	 */
	private SpeciesStructure buildSpeciesStructure(final ISyntacticElement speciesNode)
		throws GamlException {
		if ( speciesNode == null ) { throw new GamlException("Species element is null!"); }

		SpeciesStructure species = new SpeciesStructure(speciesNode);

		// recursively accumulate micro-species
		List<ISyntacticElement> microSpecies = new GamaList<ISyntacticElement>();
		microSpecies.addAll(speciesNode.getChildren(ISymbol.SPECIES));
		microSpecies.addAll(speciesNode.getChildren(ISymbol.GRID));
		for ( ISyntacticElement microSpeciesNode : microSpecies ) {
			species.addMicroSpecies(buildSpeciesStructure(microSpeciesNode));
		}

		return species;
	}

	public ModelStructure getModelStructureFrom(final String fileName) throws GamlException {
		ModelStructure model = new ModelStructure();

		Map<String, List<ISyntacticElement>> nodes = getNodesFrom(fileName);

		model.setName(nodes.get(ISymbol.NAME).get(0).getAttribute(ISymbol.NAME));
		for ( ISyntacticElement speciesNode : nodes.get(ISymbol.SPECIES) ) {
			model.addSpecies(buildSpeciesStructure(speciesNode));
		}
		model.setGlobalNodes(nodes.get(ISymbol.GLOBAL));
		model.setModelNodes(nodes.get(ISymbol.MODEL));

		return model;
	}

	private void expandNodes(final List<ISyntacticElement> allNodes,
		final List<String> nodesToExpand) {
		List<ISyntacticElement> copy = new GamaList();
		for ( int i = 0, n = allNodes.size(); i < n; i++ ) {
			ISyntacticElement e = allNodes.get(i);
			if ( nodesToExpand.contains(e.getName()) ) {
				List<ISyntacticElement> children = e.getChildren();
				copy.addAll(children);
			} else {
				copy.add(e);
			}
		}
		allNodes.clear();
		allNodes.addAll(copy);
	}

	private List<ISyntacticElement> accumulateNodes(final List<ISyntacticElement> nodes,
		final List<String> names) {
		final List result = new GamaList();
		for ( final ISyntacticElement e : nodes ) {
			final String name = e.getName();
			if ( names.contains(name) ) {
				result.add(e);
			}
			if ( !NON_RECURSIVE_NODES.contains(name) ) {
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
			if ( !NON_RECURSIVE_NODES.contains(name) ) {
				removeUselessNodes(e.getChildren(), names);
			}
		}
	}

	private void buildFileList(final List<String> filesToParse, final String fileName,
		final Map<String, ISyntacticElement> currentDocs) throws GamlException {
		if ( filesToParse.contains(fileName) ) { return; }
		filesToParse.add(0, fileName);
		ISyntacticElement doc;
		if ( currentDocs.containsKey(fileName) ) {
			doc = currentDocs.get(fileName);
		} else {
			doc = parseDoc(fileName);
			currentDocs.put(fileName, doc);
		}
		final ISyntacticElement XMLRoot = doc;
		ISyntacticElement currentElement = null;
		try {
			for ( final ISyntacticElement e : XMLRoot.getChildren("include") ) {
				currentElement = e;
				String s = currentElement.getAttribute("file");
				if ( s != null ) {
					s = GamaFileType.constructAbsoluteFilePath(s, fileName, true);
					buildFileList(filesToParse, s, currentDocs);
				}
			}
		} catch (final GamlException ex) {
			GUI.raise(ex);
			final GamlException ge = new GamlException("Error parsing the included file");
			// if ( currentElement != null ) {
			ge.addSource(currentElement);
			ge.addContext("in the directive " + currentElement.getName());
			// }
			throw ge;
		}
	}

	/**
	 * inject all docs associated to a specific file (.gaml cheat)
	 * @param docs The parsed documents, map< file-path -> DOM >
	 * @see Map#putAll(Map)
	 * @see Map#put(Object, Object)
	 */
	public void addDocs(final Map<String, ISyntacticElement> docs) {
		documents.putAll(docs);
	}

	public void clearDocs() {
		documents.clear();
	}

	public void process(final IFile gamlFile) throws Exception {
		documents = Convert.getDocMap(gamlFile, GamlResourceSet.get(gamlFile.getProject()));
	}
}
