/*********************************************************************************************
 * 
 * 
 * 'TypesManager.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.types;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.descriptions.*;
import msi.gaml.operators.Strings;

public class TypesManager {

	public static int CURRENT_INDEX = IType.SPECIES_TYPES;

	private final TypesManager parent;

	// private int current_index = IType.CURRENT_INDEX;
	private final Map<String, TypeDescription> modelSpecies = new TOrderedHashMap(/* Types.BUILT_IN_SPECIES */);
	private final Map<Integer, IType> idToIType = new TOrderedHashMap(/* Types.ID2ITYPE */);
	private final Map<String, IType> stringToIType = new TOrderedHashMap(/* Types.STRING2ITYPE */);
	private final Map<Class, IType> classToIType = new TOrderedHashMap(/* Types.CLASS2ITYPE */);
	private final TypeTree<IType> hierarchy = new TypeTree();

	public TypesManager(final TypesManager parent) {
		this.parent = parent;
		// current_index = parent == null ? msi.gaml.types.IType.SPECIES_TYPES : parent.current_index;
	}

	public void alias(final String existingTypeName, final String otherTypeName) {
		TypeDescription td = modelSpecies.get(existingTypeName);
		if ( td != null ) {
			modelSpecies.put(otherTypeName, td);
		}
		IType t = stringToIType.get(existingTypeName);
		if ( t != null ) {
			stringToIType.put(otherTypeName, t);
		}
	}

	public IType addSpeciesType(final TypeDescription species) {
		String name = species.getName();
		modelSpecies.put(name, species);
		if ( !name.equals(IKeyword.AGENT) ) {
			if ( get(name) != Types.NO_TYPE ) {
				// if ( name.equals(IKeyword.AGENT) ) { return stringToIType.get(IKeyword.AGENT); }
				species.error("Species " + name + " already declared. Species name must be unique",
					IGamlIssue.DUPLICATE_NAME, species.getUnderlyingElement(null), name);
			}
			return addType(
				new GamaAgentType((SpeciesDescription) species, species.getName(), ++CURRENT_INDEX,
					species.getJavaBase()), species.getJavaBase());
		}
		return get(IKeyword.AGENT);
	}

	public IType initType(final String keyword, IType typeInstance, final int id, final int varKind,
		final Class ... wraps) {
		if ( keyword.equals(IKeyword.UNKNOWN) ) {
			typeInstance = Types.NO_TYPE;
		}
		typeInstance.init(varKind, id, keyword, wraps);
		return addType(typeInstance, wraps);
	}

	private IType addType(final IType t, final Class ... wraps) {
		idToIType.put(t.id(), t);
		stringToIType.put(t.toString(), t);
		// Hack to allow types to be declared with their id as string
		stringToIType.put(String.valueOf(t.id()), t);
		for ( Class cc : wraps ) {
			classToIType.put(cc, t);
		}
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
			// Takes care of invalid species (see Issue 711)
			// TODO Verify this test
			type.setParent(parent == null /* || parent == entry */? get(IKeyword.AGENT) : get(parent.getName()));
		}
		buildHierarchy(hierarchy.setRoot(Types.NO_TYPE));
	}

	public TypeDescription getSpecies(final String name) {
		if ( name == null ) { return null; }
		TypeDescription td = modelSpecies.get(name);
		if ( td == null ) {
			td = parent == null ? null : parent.getSpecies(name);
		}
		return td;
	}

	//
	// public boolean containsSpecies(final String name) {
	// if ( modelSpecies.containsKey(name) ) { return true; }
	// if ( parent != null ) { return parent.containsSpecies(name); }
	// return false;
	// }

	public boolean containsType(final String s) {
		IType t = stringToIType.get(s);
		if ( t != null ) { return true; }
		if ( parent == null ) { return false; }
		return parent.containsType(s);
	}

	public List<String> getTypeNames() {
		List<String> result = parent == null ? new ArrayList() : parent.getTypeNames();
		for ( String s : stringToIType.keySet() ) {
			if ( !Strings.isGamaNumber(s) ) {
				result.add(s);
			}
		}
		return result;
	}

	Set<IType> getAllTypes() {
		Set<IType> types = new LinkedHashSet(parent == null ? Collections.EMPTY_SET : parent.getAllTypes());
		types.addAll(idToIType.values());
		return types;
	}

	public Collection<TypeDescription> getAllSpecies() {
		Set<TypeDescription> species =
			new LinkedHashSet(parent == null ? Collections.EMPTY_SET : parent.getAllSpecies());
		species.addAll(modelSpecies.values());
		return species;

	}

	public IType get(final int type) {
		IType t = idToIType.get(type);
		return t == null ? parent != null ? parent.get(type) : Types.NO_TYPE : t;
	}

	public IType get(final String type) {
		IType t = stringToIType.get(type);
		return t == null ? parent != null ? parent.get(type) : Types.NO_TYPE : t;
	}

	public <T> IType<T> get(final Class<T> type) {
		IType<T> t = internalGet(type);
		return t == null ? Types.NO_TYPE : t;
	}

	private <T> IType<T> internalGet(final Class<T> type) {
		IType t = classToIType.get(type);
		if ( t == null ) {
			if ( parent != null ) {
				t = parent.internalGet(type);
			}
			if ( t == null && !type.isInterface() ) {
				for ( Map.Entry<Class, IType> c : classToIType.entrySet() ) {
					Class support = c.getKey();
					if ( support != Object.class && support.isAssignableFrom(type) ) { return c.getValue(); }
				}
			}
		}
		return t;
	}

	public void dispose() {
		idToIType.clear();
		stringToIType.clear();
		classToIType.clear();
		if ( hierarchy != null ) {
			hierarchy.dispose();
		}
	}

	private Set<IType> getDirectSubTypes(final IType t) {
		if ( t == null ) { return Collections.EMPTY_SET; }
		Set<IType> types = new LinkedHashSet();
		for ( IType st : getAllTypes() ) {
			if ( t.equals(st.getParent()) ) {
				types.add(st);
			}
		}
		return types;
	}

	private void buildHierarchy(final TypeNode<IType> currentNode) {
		Set<IType> subs = getDirectSubTypes(currentNode.getData());
		// scope.getGui().debug("Sub types of " + currentNode.getData() + " = " + subs);
		if ( !subs.isEmpty() ) {
			currentNode.addChildren(subs);
			for ( TypeNode<IType> node : currentNode.getChildren() ) {
				buildHierarchy(node);
			}
		}
	}

	public TypeTree<SpeciesDescription> getSpeciesHierarchy() {
		TypeTree<SpeciesDescription> result =
			new TypeTree(createSpeciesNodesFrom(hierarchy.find(get(msi.gaml.types.IType.AGENT))));
		System.out.println(result.toStringWithDepth());
		return result;
	}

	private TypeNode<SpeciesDescription> createSpeciesNodesFrom(final TypeNode<IType> type) {
		TypeNode<SpeciesDescription> node = new TypeNode(getSpecies(type.getData().getSpeciesName()));
		for ( TypeNode<IType> t : type.getChildren() ) {
			node.addChild(createSpeciesNodesFrom(t));
		}
		return node;
	}

}
