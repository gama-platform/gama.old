/*******************************************************************************************************
 *
 * Types.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.types;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Iterables;

import msi.gama.util.Collector;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.ICollector;
import msi.gama.util.IMap;
import msi.gaml.compilation.GAML;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.OperatorProto;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.data.ListExpression;
import msi.gaml.expressions.data.MapExpression;
import msi.gaml.factories.DescriptionFactory;
import one.util.streamex.StreamEx;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Written by drogoul Modified on 9 juin 2010
 *
 * @todo Description
 *
 */

/**
 * The Class Types.
 */

/**
 * The Class Types.
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class Types {

	static {
		DEBUG.ON();
	}

	/** The Constant builtInTypes. */
	public final static ITypesManager builtInTypes = new TypesManager(null);

	/** The built in species map. */
	private static volatile Map<String, SpeciesDescription> builtInSpeciesMap;

	/** The Constant NO_TYPE. */
	public final static IType NO_TYPE = new GamaNoType();

	/** The type. */
	public static IType AGENT, PATH, FONT, SKILL, DATE, MATERIAL, ACTION, TYPE;

	/** The int. */
	public static GamaIntegerType INT;

	/** The float. */
	public static GamaFloatType FLOAT;

	/** The color. */
	public static GamaColorType COLOR;

	/** The bool. */
	public static GamaBoolType BOOL;

	/** The string. */
	public static GamaStringType STRING;

	/** The point. */
	public static GamaPointType POINT;

	/** The geometry. */
	public static GamaGeometryType GEOMETRY;

	/** The topology. */
	public static GamaTopologyType TOPOLOGY;

	/** The field. */
	public static GamaFieldType FIELD;

	/** The species. */
	public static IContainerType LIST, MATRIX, MAP, GRAPH, FILE, PAIR, CONTAINER, SPECIES;

	/** The Constant CLASSES_TYPES_CORRESPONDANCE. */
	public static final IMap<Class, String> CLASSES_TYPES_CORRESPONDANCE = GamaMapFactory.createUnordered();

	/**
	 * Cache.
	 *
	 * @param id
	 *            the id
	 * @param instance
	 *            the instance
	 */
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
				break;
			default:
		}
	}

	/**
	 * Gets the.
	 *
	 * @param type
	 *            the type
	 * @return the i type
	 */
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

	/**
	 * Gets the.
	 *
	 * @param type
	 *            the type
	 * @return the i type
	 */
	public static IType get(final String type) {
		return builtInTypes.get(type);
	}

	/**
	 * Gets the.
	 *
	 * @param <T>
	 *            the generic type
	 * @param type
	 *            the type
	 * @return the i type
	 */
	public static <T> IType<T> get(final Class<T> type) {
		final IType<T> t = internalGet(type);
		return t == null ? Types.NO_TYPE : t;
	}

	/**
	 * Internal get.
	 *
	 * @param <T>
	 *            the generic type
	 * @param type
	 *            the type
	 * @return the i type
	 */
	private static <T> IType<T> internalGet(final Class<T> type) {
		final IType<T>[] t = new IType[] { builtInTypes.get(Types.CLASSES_TYPES_CORRESPONDANCE.get(type)) };
		boolean newEntry = false;
		if (t[0] == Types.NO_TYPE && !type.isInterface()) {
			newEntry = !Types.CLASSES_TYPES_CORRESPONDANCE.forEachPair((support, id) -> {
				if (support != Object.class && support.isAssignableFrom(type)) {
					t[0] = (IType<T>) builtInTypes.get(id);
					return false;
				}
				return true;
			});

		}
		if (newEntry) { Types.CLASSES_TYPES_CORRESPONDANCE.put(type, t[0].toString()); }
		return t[0];
	}

	/**
	 * Gets the type names.
	 *
	 * @return the type names
	 */
	public static Iterable<String> getTypeNames() {
		return Iterables.transform(builtInTypes.getAllTypes(), IType::getName);
	}

	/**
	 * Inits the types hierarchy of built-in types
	 */
	public static void init() {
		// We build a graph-type multimap structure
		Map<IType<?>, ICollector<IType<?>>> outgoing = new HashMap(), incoming = new HashMap();
		Set<IType<?>> types = builtInTypes.getAllTypes();
		for (IType t : types) {
			outgoing.put(t, Collector.getSet());
			incoming.put(t, Collector.getSet());
		}
		for (IType t1 : types) {
			for (IType t2 : types) {
				if (t1 != t2 && t1.toClass().isAssignableFrom(t2.toClass())) {
					outgoing.get(t1).add(t2);
					incoming.get(t2).add(t1);
				}
			}
		}

		// We traverse the hierarchy beginning with NO_TYPE and browsing through its children to determine which ones
		// are only its children and which ones are sub-subtypes. The only children of a type are parented and
		// processed, the others left for further iterations
		Deque<IType<?>> toProcess = new ArrayDeque<>();
		toProcess.push(NO_TYPE);
		while (!toProcess.isEmpty()) {
			IType parent = toProcess.pop();
			for (IType t : outgoing.get(parent)) {
				incoming.get(t).remove(parent);
				if (incoming.get(t).isEmpty()) {
					toProcess.push(t);
					//DEBUG.OUT("Parenting " + t.getName() + " with " + parent.getName());
					t.setParent(parent);
					DescriptionFactory.addNewTypeName(t.toString(), t.getVarKind());
					t.setFieldGetters(GAML.getAllFields(t.toClass()));
				}
			}
		}
	}

	/**
	 * Gets the built in species.
	 *
	 * @return the built in species
	 */
	public static Map<String, ? extends SpeciesDescription> getBuiltInSpecies() {
		if (builtInSpeciesMap != null) return builtInSpeciesMap;
		final ModelDescription root = ModelDescription.ROOT;
		List<SpeciesDescription> result = new ArrayList();
		root.getAllSpecies(result);
		builtInSpeciesMap = StreamEx.of(result).toMap(SpeciesDescription::getName, sd -> sd);
		return builtInSpeciesMap;
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

	/**
	 * Checks if is empty.
	 *
	 * @param expr2
	 *            the expr 2
	 * @return true, if is empty
	 */
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

	/**
	 * Gets the all fields.
	 *
	 * @return the all fields
	 */
	public static Iterable<OperatorProto> getAllFields() {
		return concat(transform(builtInTypes.getAllTypes(), each -> each.getFieldGetters().values()));
	}

}