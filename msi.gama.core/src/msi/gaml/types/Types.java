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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.types;

import java.util.*;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.TypeFieldExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.types.TypeTree.Order;

/**
 * Written by drogoul Modified on 9 juin 2010
 * 
 * @todo Description
 * 
 */

public class Types {

	public final static TypesManager builtInTypes = new TypesManager(null);

	public final static IType NO_TYPE = new GamaNoType();

	public static IType get(final int type) {
		return builtInTypes.get(String.valueOf(type));
	}

	public static IType get(final String type) {
		return builtInTypes.get(type);
	}

	public static <T> IType<T> get(final Class<T> type) {
		return builtInTypes.get(type);
	}

	public static Collection<String> getTypeNames() {
		return builtInTypes.getTypeNames();
	}

	public static void init() {
		TypeTree<IType> hierarchy = buildHierarchy();
		for ( TypeNode<IType> node : hierarchy.build(Order.PRE_ORDER) ) {
			IType type = node.getData();
			DescriptionFactory.addNewTypeName(type.toString(), type.getVarKind());
			Map<String, TypeFieldExpression> vars = AbstractGamlAdditions.getAllFields(type.toClass());
			type.setFieldGetters(vars);
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
		Set<IType> list = new HashSet(builtInTypes.getAllTypes());
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

	public static Collection<TypeDescription> getBuiltInSpecies() {
		Collection<TypeDescription> result = ModelDescription.ROOT.getTypesManager().getAllSpecies();
		return result;
	}

	/**
	 * @param matchType
	 * @param switchType
	 * @return
	 */
	public static boolean intFloatCase(final IType t1, final IType t2) {
		return t1.id() == IType.FLOAT && t2.id() == IType.INT || t2.id() == IType.FLOAT && t1.id() == IType.INT;
	}
}