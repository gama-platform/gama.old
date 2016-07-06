/*********************************************************************************************
 *
 *
 * 'Types.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.descriptions.TypeDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.ListExpression;
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

	public static IType INT, FLOAT, BOOL, COLOR, STRING, POINT, GEOMETRY, TOPOLOGY, AGENT, PATH, FONT, SKILL, DATE, MATERIAL;
	public static IContainerType LIST, MATRIX, MAP, GRAPH, FILE, PAIR, CONTAINER, SPECIES;

	public static void cache(final int id, final IType instance) {
		switch (id) {
		case IType.INT:
			INT = instance;
			break;
		case IType.FLOAT:
			FLOAT = instance;
			break;
		case IType.BOOL:
			BOOL = instance;
			break;
		case IType.COLOR:
			COLOR = instance;
			break;
		case IType.DATE:
			DATE = instance;
			break;
		case IType.MATERIAL:
			MATERIAL = instance;
			break;
		case IType.STRING:
			STRING = instance;
			break;
		case IType.POINT:
			POINT = instance;
			break;
		case IType.GEOMETRY:
			GEOMETRY = instance;
			break;
		case IType.TOPOLOGY:
			TOPOLOGY = instance;
			break;
		case IType.LIST:
			LIST = (IContainerType) instance;
			break;
		case IType.MAP:
			MAP = (IContainerType) instance;
			break;
		case IType.GRAPH:
			GRAPH = (IContainerType) instance;
			break;
		case IType.FILE:
			FILE = (IContainerType) instance;
			break;
		case IType.PAIR:
			PAIR = (IContainerType) instance;
			break;
		case IType.AGENT:
			AGENT = instance;
			break;
		case IType.PATH:
			PATH = instance;
			break;
		case IType.MATRIX:
			MATRIX = (IContainerType) instance;
			break;
		case IType.CONTAINER:
			CONTAINER = (IContainerType) instance;
			break;
		case IType.SPECIES:
			SPECIES = (IContainerType) instance;
			break;
		case IType.FONT:
			FONT = instance;
			break;
		case IType.SKILL:
			SKILL = instance;
			break;
		default:
		}
	}

	public static IType get(final int type) {
		// use cache first
		switch (type) {
		case IType.INT:
			return INT;
		case IType.FLOAT:
			return FLOAT;
		case IType.BOOL:
			return BOOL;
		case IType.COLOR:
			return COLOR;
		case IType.DATE:
			return DATE;
		case IType.STRING:
			return STRING;
		case IType.POINT:
			return POINT;
		case IType.GEOMETRY:
			return GEOMETRY;
		case IType.TOPOLOGY:
			return TOPOLOGY;
		case IType.LIST:
			return LIST;
		case IType.MAP:
			return MAP;
		case IType.GRAPH:
			return GRAPH;
		case IType.FILE:
			return FILE;
		case IType.PAIR:
			return PAIR;
		case IType.AGENT:
			return AGENT;
		case IType.PATH:
			return PATH;
		case IType.MATRIX:
			return MATRIX;
		case IType.CONTAINER:
			return CONTAINER;
		case IType.SPECIES:
			return SPECIES;
		case IType.SKILL:
			return SKILL;
		case IType.MATERIAL:
			return MATERIAL;
		}
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
		final TypeTree<IType> hierarchy = buildHierarchy();
		for (final TypeNode<IType> node : hierarchy.build(Order.PRE_ORDER)) {
			final IType type = node.getData();
			DescriptionFactory.addNewTypeName(type.toString(), type.getVarKind());
			final Map<String, OperatorProto> vars = AbstractGamlAdditions.getAllFields(type.toClass());
			type.setFieldGetters(vars);
			type.setParent(node.getParent() == null ? null : node.getParent().getData());
		}
		// System.out.println("Hierarchy" + hierarchy.toStringWithDepth());
	}

	private static TypeTree<IType> buildHierarchy() {
		final TypeNode<IType> root = new TypeNode(NO_TYPE);
		final TypeTree<IType> hierarchy = new TypeTree();
		hierarchy.setRoot(root);
		final List<IType>[] depths = typesWithDepths();
		for (int i = 1; i < 10; i++) {
			final List<IType> types = depths[i];
			for (final IType t : types) {
				place(t, hierarchy);
			}
		}
		return hierarchy;
	}

	private static List<IType>[] typesWithDepths() {
		final List<IType>[] depths = new ArrayList[10];
		for (int i = 0; i < 10; i++) {
			depths[i] = new ArrayList();
		}
		final Set<IType> list = new LinkedHashSet(builtInTypes.getAllTypes());
		for (final IType t : list) {
			// System.out.println("Type: " + t);

			int depth = 0;
			for (final IType other : list) {
				if (other.isAssignableFrom(t) && other != t) {
					depth++;
				}
			}
			depths[depth].add(t);
		}
		return depths;
	}

	private static void place(final IType t, final TypeTree<IType> hierarchy) {
		final Map<TypeNode<IType>, Integer> map = hierarchy.buildWithDepth(Order.PRE_ORDER);
		int max = 0;
		TypeNode<IType> parent = hierarchy.getRoot();
		for (final TypeNode<IType> current : map.keySet()) {
			if (current.getData().isAssignableFrom(t) && map.get(current) > max) {
				max = map.get(current);
				parent = current;
			}
		}
		parent.addChild(new TypeNode(t));
	}

	public static Collection<TypeDescription> getBuiltInSpecies() {
		Collection<TypeDescription> result;
		final ModelDescription root = ModelDescription.ROOT;

		final TypesManager tm = root.getTypesManager();
		result = tm.getAllSpecies();
		return result;
	}

	/**
	 * @param matchType
	 * @param switchType
	 * @return
	 */
	public static boolean intFloatCase(final IType t1, final IType t2) {
		return t1 == FLOAT && t2 == INT || t2 == FLOAT && t1 == INT;
	}

	/**
	 * @param receiverType
	 * @param assignedType
	 * @param expr2
	 * @return
	 */
	public static boolean mapListCase(final IType receiverType, final IType assignedType, final IExpression expr2) {
		if (receiverType.getType() == MAP && assignedType.getType() == LIST) {
			if (expr2 instanceof ListExpression) {
				return ((ListExpression) expr2).isEmpty();
			}
			if (expr2.isConst() && ((List) expr2.value(null)).isEmpty()) {
				return true;
			}
		}
		if (receiverType.getType() == MAP && assignedType.getType() == MAP) {
			if (expr2.isConst() && ((Map) expr2.value(null)).isEmpty()) {
				return true;
			}
		}
		return false;
	}
}