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

import static msi.gaml.types.IType.AGENT;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.expressions.TypeFieldExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.types.TypeTree.Order;

/**
 * Written by drogoul Modified on 9 juin 2010
 * 
 * @todo Description
 * 
 */

// AJOUTER LES ESPECES PREDEFINIES DANS LES TYPES
// SUPPRIMER LES ESPECES DANS LE MODEL POUR LE METTRE DANS LE TYPEMANAGER
// Faire de cette classe un TypesManager "normal"

public class Types {

	public static int CURRENT_INDEX = IType.SPECIES_TYPES;
	public final static Map<Integer, Set<String>> VARTYPE2KEYWORDS = new LinkedHashMap();
	public final static Map<Integer, IType> ID2ITYPE = new LinkedHashMap();
	public final static Map<String, IType> STRING2ITYPE = new LinkedHashMap();
	public final static Map<Class, IType> CLASS2ITYPE = new LinkedHashMap();
	final static Map<String, SpeciesDescription> BUILT_IN_SPECIES = new LinkedHashMap();

	public final static IType NO_TYPE = new GamaNoType();

	public static void addSpeciesType(final SpeciesDescription species) {
		String name = species.getName();
		BUILT_IN_SPECIES.put(name, species);
		if ( !name.equals(IKeyword.AGENT) ) {
			// "agent" is already a built-in type. No need to recreate it
			Class base = species.getJavaBase();
			if ( STRING2ITYPE.containsKey(name) ) {
				species.error("Species " + name + " already declared. Species name must be unique",
					IGamlIssue.DUPLICATE_NAME, species.getUnderlyingElement(null), name);
			}
			int newId = ++CURRENT_INDEX;
			IType newType = new GamaAgentType(name, newId, base);
			ID2ITYPE.put(newId, newType);
			STRING2ITYPE.put(name, newType);
			CLASS2ITYPE.put(base == null ? get(AGENT).toClass() : base, newType);
		}
	}

	public static void initType(final String keyword, IType typeInstance, final int id, final int varKind,
		final Class ... wraps) {
		if ( keyword.equals(IKeyword.UNKNOWN) ) {
			typeInstance = NO_TYPE;
		}
		typeInstance.init(varKind, id, keyword, wraps);
		ID2ITYPE.put(id, typeInstance);
		STRING2ITYPE.put(keyword, typeInstance);
		// Hack to allow types to be declared with their id as string
		STRING2ITYPE.put(String.valueOf(id), typeInstance);
		for ( Class cc : wraps ) {
			CLASS2ITYPE.put(cc, typeInstance);
		}
		if ( !VARTYPE2KEYWORDS.containsKey(varKind) ) {
			VARTYPE2KEYWORDS.put(varKind, new HashSet());
		}
		VARTYPE2KEYWORDS.get(varKind).add(keyword);
	}

	public static Object coerce(final IScope scope, final Object value, final IType type, final Object param)
		throws GamaRuntimeException {
		return type.cast(scope, value, param);
	}

	public static IType get(final int type) {
		IType t = ID2ITYPE.get(type);
		return t == null ? Types.NO_TYPE : t;
	}

	public static IType get(final String type) {
		IType t = STRING2ITYPE.get(type);
		return t == null ? Types.NO_TYPE : t;
	}

	public static <T> IType<T> get(final Class<T> type) {
		IType t = CLASS2ITYPE.get(type);
		if ( t != null ) { return t; }
		// Set<Class> classes = classToIType.keySet();
		if ( !type.isInterface() ) {
			for ( Map.Entry<Class, IType> c : CLASS2ITYPE.entrySet() ) {
				if ( c.getKey() != Object.class && c.getKey().isAssignableFrom(type) ) {
					t = c.getValue();
					// classToIType.put(type, t);
					return t;
				}
			}
		}
		if ( !type.isInterface() ) {
			CLASS2ITYPE.put(type, Types.NO_TYPE);
		}
		return Types.NO_TYPE;
	}

	public static boolean isBuiltIn(String name) {
		IType t = STRING2ITYPE.get(name);
		return t != null && t.isSpeciesType();
	}

	public static void initFieldGetters(final IType t) {
		Map<String, TypeFieldExpression> vars = AbstractGamlAdditions.getAllFields(t.toClass());
		t.setFieldGetters(vars);
	}

	public static void init() {
		TypeTree<IType> hierarchy = buildHierarchy();
		for ( TypeNode<IType> node : hierarchy.build(Order.PRE_ORDER) ) {
			IType type = node.getData();
			DescriptionFactory.addNewTypeName(type.toString(), type.getVarKind());
			initFieldGetters(type);
			type.setParent(node.getParent() == null ? null : node.getParent().getData());
		}
	}

	private static TypeTree<IType> buildHierarchy() {
		TypeNode<IType> root = new TypeNode(NO_TYPE);
		TypeTree<IType> hierarchy = new TypeTree();
		hierarchy.setRoot(root);
		List<IType>[] depths = typesWithDepths();
		for ( int i = 1; i < 10; i++ ) {
			List<IType> types = depths[i];
			for ( IType t : types ) {
				place(t, hierarchy);
			}
		}
		return hierarchy;
	}

	private static List<IType>[] typesWithDepths() {
		List<IType>[] depths = new ArrayList[10];
		for ( int i = 0; i < 10; i++ ) {
			depths[i] = new ArrayList();
		}
		Set<IType> list = new HashSet(CLASS2ITYPE.values());
		for ( IType t : list ) {
			int depth = 0;
			for ( IType other : list ) {
				if ( other.isAssignableFrom(t) && other != t ) {
					depth++;
				}
			}
			depths[depth].add(t);
		}
		return depths;
	}

	private static void place(final IType t, final TypeTree<IType> hierarchy) {
		Map<TypeNode<IType>, Integer> map = hierarchy.buildWithDepth(Order.PRE_ORDER);
		int max = 0;
		TypeNode<IType> parent = hierarchy.getRoot();
		for ( TypeNode<IType> current : map.keySet() ) {
			if ( current.getData().isAssignableFrom(t) && map.get(current) > max ) {
				max = map.get(current);
				parent = current;
			}
		}
		parent.addChild(new TypeNode(t));
	}

	public static Collection<SpeciesDescription> getBuiltInSpecies() {
		return BUILT_IN_SPECIES.values();
	}

	public static SpeciesDescription getSpecies(String string) {
		return BUILT_IN_SPECIES.get(string);
	}

}