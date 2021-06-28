/*******************************************************************************************************
 *
 * msi.gaml.types.Types.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and simulation
 * platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;
import msi.gama.util.tree.GamaNode;
import msi.gama.util.tree.GamaTree;
import msi.gama.util.tree.GamaTree.Order;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.ListExpression;
import msi.gaml.expressions.MapExpression;
import msi.gaml.factories.DescriptionFactory;

/**
 * Written by drogoul Modified on 9 juin 2010
 *
 * @todo Description
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class Types {

	public final static ITypesManager builtInTypes = new TypesManager(null);

	public final static IType NO_TYPE = new GamaNoType();

	public static IType AGENT, PATH, FONT, SKILL, DATE, MATERIAL, ACTION, TYPE;
	public static GamaIntegerType INT;
	public static GamaFloatType FLOAT;
	public static GamaColorType COLOR;
	public static GamaBoolType BOOL;
	public static GamaStringType STRING;
	public static GamaPointType POINT;
	public static GamaGeometryType GEOMETRY;
	public static GamaTopologyType TOPOLOGY;
	public static GamaFieldType FIELD;
	public static IContainerType LIST, MATRIX, MAP, GRAPH, FILE, PAIR, CONTAINER, SPECIES;

	public static final IMap<Class, String> CLASSES_TYPES_CORRESPONDANCE = GamaMapFactory.createUnordered();

	public static void cache(final int id, final IType instance) {
		switch (id) {
			case IType.INT:
				INT = (GamaIntegerType) instance;
				break;
			case IType.FLOAT:
				FLOAT = (GamaFloatType) instance;
				break;
			case IType.BOOL:
				BOOL = (GamaBoolType) instance;
				break;
			case IType.COLOR:
				COLOR = (GamaColorType) instance;
				break;
			case IType.DATE:
				DATE = instance;
				break;
			case IType.MATERIAL:
				MATERIAL = instance;
				break;
			case IType.STRING:
				STRING = (GamaStringType) instance;
				break;
			case IType.POINT:
				POINT = (GamaPointType) instance;
				break;
			case IType.GEOMETRY:
				GEOMETRY = (GamaGeometryType) instance;
				break;
			case IType.TOPOLOGY:
				TOPOLOGY = (GamaTopologyType) instance;
				break;
			case IType.LIST:
				LIST = (IContainerType) instance;
				break;
			case IType.MAP:
				MAP = (GamaMapType) instance;
				break;
			case IType.GRAPH:
				GRAPH = (IContainerType) instance;
				break;
			case IType.FILE:
				FILE = (IContainerType) instance;
				break;
			case IType.PAIR:
				PAIR = (GamaPairType) instance;
				break;
			case IType.AGENT:
				AGENT = instance;
				break;
			case IType.PATH:
				PATH = instance;
				break;
			case IType.MATRIX:
				MATRIX = (GamaMatrixType) instance;
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
			case IType.TYPE:
				TYPE = instance;
				break;
			case IType.ACTION:
				ACTION = instance;
				break;
			case IType.FIELD:
				FIELD = (GamaFieldType) instance;
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
			case IType.ACTION:
				return ACTION;
			case IType.TYPE:
				return TYPE;
		}
		return builtInTypes.get(String.valueOf(type));
	}

	public static IType get(final String type) {
		return builtInTypes.get(type);
	}

	public static <T> IType<T> get(final Class<T> type) {
		final IType<T> t = internalGet(type);
		return t == null ? Types.NO_TYPE : t;
	}

	private static <T> IType<T> internalGet(final Class<T> type) {
		final IType<T>[] t = new IType[] { builtInTypes.get(Types.CLASSES_TYPES_CORRESPONDANCE.get(type)) };
		boolean newEntry = false;
		if (t[0] == Types.NO_TYPE) {
			if (!type.isInterface()) {
				newEntry = !Types.CLASSES_TYPES_CORRESPONDANCE.forEachPair((support, id) -> {
					if (support != Object.class && support.isAssignableFrom(type)) {
						t[0] = (IType<T>) builtInTypes.get(id);
						return false;
					}
					return true;
				});

			}
		}
		if (newEntry) { Types.CLASSES_TYPES_CORRESPONDANCE.put(type, t[0].toString()); }
		return t[0];
	}

	public static Iterable<String> getTypeNames() {
		return Iterables.transform(builtInTypes.getAllTypes(), each -> each.getName());
	}

	public static void init() {
		final GamaTree<IType> hierarchy = buildHierarchy();
		for (final GamaNode<IType> node : hierarchy.list(Order.PRE_ORDER)) {
			final IType type = node.getData();
			DescriptionFactory.addNewTypeName(type.toString(), type.getVarKind());
			final Map<String, OperatorProto> vars = AbstractGamlAdditions.getAllFields(type.toClass());
			type.setFieldGetters(vars);
			type.setParent(node.getParent() == null ? null : node.getParent().getData());
		}
		// DEBUG.LOG("Hierarchy" + hierarchy.toStringWithDepth());
	}

	private static GamaTree<IType> buildHierarchy() {
		final GamaNode<IType> root = new GamaNode(NO_TYPE);
		final GamaTree<IType> hierarchy = new GamaTree();
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
			depths[i] = new ArrayList<>();
		}
		final Set<IType> list = Sets.newLinkedHashSet(builtInTypes.getAllTypes());
		for (final IType t : list) {
			// DEBUG.LOG("Type computing depth: " + t);

			int depth = 0;
			for (final IType other : list) {
				// DEBUG.LOG("\tComparing with: " + other);
				if (other != t && other.isAssignableFrom(t)) {

					depth++;
				}
			}
			depths[depth].add(t);
		}
		return depths;
	}

	private static void place(final IType t, final GamaTree<IType> hierarchy) {
		final Map<GamaNode<IType>, Integer> map = hierarchy.mapByDepth(Order.PRE_ORDER);
		int max = 0;
		GamaNode<IType> parent = hierarchy.getRoot();
		for (final GamaNode<IType> current : map.keySet()) {
			if (current.getData().isAssignableFrom(t) && map.get(current) > max) {
				max = map.get(current);
				parent = current;
			}
		}
		parent.addChild(t);
	}

	private static List<SpeciesDescription> builtInSpecies;

	public static Collection<? extends SpeciesDescription> getBuiltInSpecies() {
		if (builtInSpecies != null) return builtInSpecies;
		final ModelDescription root = ModelDescription.ROOT;
		final List<SpeciesDescription> result = new ArrayList<>();
		root.getAllSpecies(result);
		builtInSpecies = result;
		return builtInSpecies;

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
	 * Tests whether constant list expressions can still be compatible with a receiver even if their actual types differ
	 *
	 * @param receiverType
	 * @param assignedType
	 * @param expr2
	 * @return
	 */
	public static boolean isEmptyContainerCase(final IType receiverType, final IExpression expr2) {
		final IType receiver = receiverType.getGamlType();
		final boolean result = (receiver == MAP || receiver == LIST) && isEmpty(expr2);
		if (result) return true;

		// One last chance if receiverType is a list of lists/maps and expr2 is a list expression containing empty
		// lists. This case is treated recursively in case of complex data structures
		if (expr2 instanceof ListExpression) {
			for (final IExpression subExpr : ((ListExpression) expr2).getElements()) {
				if (!isEmptyContainerCase(receiverType.getContentType(), subExpr)) return false;
			}
			return true;
		}
		return false;

	}

	public static boolean isEmpty(final IExpression expr2) {
		switch (expr2.getGamlType().getGamlType().id()) {
			case IType.LIST:
				if (expr2 instanceof ListExpression) return ((ListExpression) expr2).isEmpty();
				// if (expr2.isConst()) {
				// final Object o = expr2.getConstValue();
				// return ((List) o).isEmpty();
				// }
				break;
			case IType.MAP:
				if (expr2 instanceof MapExpression) return ((MapExpression) expr2).isEmpty();
		}
		return false;
	}

	public static Iterable<OperatorProto> getAllFields() {
		return Iterables
				.concat(Iterables.transform(builtInTypes.getAllTypes(), (each) -> each.getFieldGetters().values()));
	}

}