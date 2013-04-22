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
package msi.gaml.types;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gaml.descriptions.*;

public class TypesManager {

	private int current_index = Types.CURRENT_INDEX;
	private final Map<String, TypeDescription> modelSpecies = new LinkedHashMap(Types.BUILT_IN_SPECIES);
	private final Map<Integer, IType> idToIType = new LinkedHashMap(Types.ID2ITYPE);
	private final Map<String, IType> stringToIType = new LinkedHashMap(Types.STRING2ITYPE);
	private final Map<Class, IType> classToIType = new LinkedHashMap(Types.CLASS2ITYPE);
	private final TypeTree<IType> hierarchy = new TypeTree();

	/**
	 * 
	 * @param name species name
	 * @param base
	 * @return
	 */
	public IType addSpeciesType(final TypeDescription species) {
		String name = species.getName();
		Class base = species.getJavaBase();
		if ( stringToIType.containsKey(name) ) {
			// if ( name.equals(IKeyword.AGENT) ) { return stringToIType.get(IKeyword.AGENT); }
			species.error("Species " + name + " already declared. Species name must be unique",
				IGamlIssue.DUPLICATE_NAME, species.getUnderlyingElement(null), name);
		}
		modelSpecies.put(name, species);
		int newId = ++current_index;
		return addType(new GamaAgentType(name, newId, base));
		// idToIType.put(newId, newType);
		// stringToIType.put(name, newType);
		// classToIType.put(base == null ? Types.get(AGENT).toClass() : base, newType);
		// return newType;
	}

	public IType addType(IType t) {
		idToIType.put(t.id(), t);
		stringToIType.put(t.toString(), t);
		classToIType.put(t.toClass(), t);
		return t;
	}

	public void init() {
		for ( Map.Entry<String, TypeDescription> entry : modelSpecies.entrySet() ) {
			IType type = get(entry.getKey());
			if ( type.isParented() ) {
				continue;
			}
			Class clazz = entry.getValue().getJavaBase();
			type.setSupport(clazz);
			TypeDescription parent = entry.getValue().getParent();
			type.setParent(parent == null ? get(IKeyword.AGENT) : get(parent.getName()));
		}
		buildHierarchy();
	}

	public TypeDescription getSpecies(String name) {
		return modelSpecies.get(name);
	}

	public boolean containsSpecies(String name) {
		return modelSpecies.containsKey(name);
	}

	public List<String> getTypeNames() {
		return new ArrayList(stringToIType.keySet());
	}

	public IType get(final int type) {
		IType t = idToIType.get(type);

		return t == null ? Types.NO_TYPE : t;
	}

	public IType get(final String type) {
		IType t = stringToIType.get(type);

		return t == null ? Types.NO_TYPE : t;
	}

	public IType get(final Class type) {
		IType t = classToIType.get(type);

		return t == null ? Types.NO_TYPE : t;
	}

	public void dispose() {
		idToIType.clear();
		stringToIType.clear();
		classToIType.clear();
	}

	public Set<IType> getDirectSubTypes(IType t) {
		if ( t == null ) { return Collections.EMPTY_SET; }
		Set<IType> types = new LinkedHashSet();
		for ( IType st : idToIType.values() ) {
			if ( t.equals(st.getParent()) ) {
				types.add(st);
			}
		}
		return types;
	}

	private void buildHierarchy() {
		buildHierarchy(hierarchy.setRoot(Types.NO_TYPE));
	}

	private void buildHierarchy(TypeNode<IType> currentNode) {
		Set<IType> subs = getDirectSubTypes(currentNode.getData());
		// GuiUtils.debug("Sub types of " + currentNode.getData() + " = " + subs);
		if ( !subs.isEmpty() ) {
			currentNode.addChildren(subs);
			for ( TypeNode<IType> node : currentNode.getChildren() ) {
				buildHierarchy(node);
			}
		}
	}

	public TypeTree<IType> getTypeHierarchy() {
		return hierarchy;
	}

	public TypeTree<IType> getTypeHierarchyFrom(IType t) {
		return new TypeTree(hierarchy.find(t));
	}

	public TypeTree<SpeciesDescription> getSpeciesHierarchy() {
		return new TypeTree(createSpeciesNodesFrom(hierarchy.find(get(IType.AGENT))));
	}

	private TypeNode<SpeciesDescription> createSpeciesNodesFrom(TypeNode<IType> type) {
		TypeNode<SpeciesDescription> node = new TypeNode(getSpecies(type.getData().getSpeciesName()));
		for ( TypeNode<IType> t : type.getChildren() ) {
			node.addChild(createSpeciesNodesFrom(t));
		}
		return node;
	}

}
