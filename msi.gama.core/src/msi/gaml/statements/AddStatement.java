/*******************************************************************************************************
 *
 * msi.gaml.statements.AddStatement.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.statements;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gaml.compilation.annotations.serializer;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.descriptions.SymbolDescription;
import msi.gaml.descriptions.SymbolSerializer;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.AddStatement.AddSerializer;
import msi.gaml.statements.AddStatement.AddValidator;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 6 févr. 2010
 *
 * @todo Description
 *
 */

@facets (
		value = { @facet (
				name = IKeyword.TO,
				type = { IType.CONTAINER, IType.SPECIES, IType.AGENT, IType.GEOMETRY },
				optional = false,
				doc = { @doc ("an expression that evaluates to a container") }),
				@facet (
						name = IKeyword.ITEM,
						type = IType.NONE,
						optional = true,
						doc = { @doc ("any expression to add in the container") }),
				@facet (
						name = IKeyword.EDGE,
						type = IType.NONE,
						optional = true,
						doc = { @doc (
								deprecated = "Use 'add edge(...)' instead",
								value = "a pair that will be added to a graph as an edge (if nodes do not exist, they are also added)") }),
				@facet (
						name = IKeyword.VERTEX,
						type = IType.NONE,
						optional = true,
						doc = { @doc (
								deprecated = "Use 'add node(...)' instead") }),
				@facet (
						name = IKeyword.NODE,
						type = IType.NONE,
						optional = true,
						doc = { @doc (
								deprecated = "Use 'add node(...)' instead",
								value = "an expression that will be added to a graph as a node.") }),
				@facet (
						name = IKeyword.AT,
						type = IType.NONE,
						optional = true,
						doc = { @doc ("position in the container of added element") }),
				@facet (
						name = IKeyword.ALL,
						type = IType.NONE,
						optional = true,
						doc = @doc ("Allows to either pass a container so as to add all its element, or 'true', if the item to add is already a container.")),
				@facet (
						name = IKeyword.WEIGHT,
						type = IType.FLOAT,
						optional = true,
						doc = @doc (
								deprecated = "use the 'edge' or 'node' operators with a weight parameter",
								value = "An optional float value representing the weight to attach to this element in case the container is a graph")) },
		omissible = IKeyword.ITEM)
@doc (
		value = "Allows to add, i.e. to insert, a new element in a container (a list, matrix, map, ...).Incorrect use: The addition of a new element at a position out of the bounds of the container will produce a warning and let the container unmodified. If all: is specified, it has no effect if its argument is not a container, or if its argument is 'true' and the item to add is not a container. In that latter case",
		usages = { @usage (
				value = "The new element can be added either at the end of the container or at a particular position.",
				examples = { @example (
						value = "add expr to: expr_container;    // Add at the end",
						isExecutable = false),
						@example (
								value = "add expr at: expr to: expr_container;   // Add at position expr",
								isExecutable = false) }),
				@usage (
						value = "Case of a list, the expression in the facet at: should be an integer.",
						examples = { @example ("list<int> workingList <- [];"), @example (
								value = "add 0 at: 0 to: workingList ;",
								var = "workingList",
								equals = "[0]",
								returnType = "null"), // workingList
														// now
														// equals
														// [0]
								@example (
										value = "add 10 at: 0 to: workingList ;",
										var = "workingList",
										equals = "[10,0]",
										returnType = "null"), // workingList
																// now
																// equals
																// [10,0]
								@example (
										value = "add 20 at: 2 to: workingList ;",
										var = "workingList",
										equals = "[10,0,20]",
										returnType = "null"), // workingList
																// now
																// equals
																// [10,0,20]
								@example (
										value = "add 50 to: workingList;",
										var = "workingList",
										equals = "[10,0,20,50]",
										returnType = "null"), // workingList
																// now
																// equals
																// [10,0,20,50]
								@example (
										value = "add [60,70] all: true to: workingList;",
										var = "workingList",
										equals = "[10,0,20,50,60,70]",
										returnType = "null") }), // workingList
																	// now
																	// equals
																	// [10,0,20,50,60,70]
				@usage (
						value = "Case of a matrix: this statement can not be used on matrix. Please refer to the statement put."),
				@usage (
						value = "Case of a map: As a map is basically a list of pairs key::value, we can also use the add statement on it. "
								+ "It is important to note that the behavior of the statement is slightly different, in particular in the use of the at facet, which denotes the key of the pair.",
						examples = { @example ("map<string,string> workingMap <- [];"), @example (
								value = "add \"val1\" at: \"x\" to: workingMap;",
								var = "workingMap",
								equals = "[\"x\"::\"val1\"]",
								returnType = "null") }), // workingMap
															// now
															// equals
															// [x::val1]";
				@usage (
						value = "If the at facet is omitted, a pair expr_item::expr_item will be added to the map. "
								+ "An important exception is the case where the expr_item is a pair: in this case the pair is added.",
						examples = { @example (
								value = "add \"val2\" to: workingMap;",
								var = "workingMap",
								equals = "[\"x\"::\"val1\", \"val2\"::\"val2\"]",
								returnType = "null"), // workingMap
														// now
														// equals
														// [val2::val2,
														// x::val1]
								@example (
										value = "add \"5\"::\"val4\" to: workingMap; ",
										var = "workingMap",
										equals = "[\"x\"::\"val1\", \"val2\"::\"val2\", \"5\"::\"val4\"]",
										returnType = "null") }), // workingMap
																	// now
																	// equals
																	// [val2::val2,
																	// 5::val4,
																	// x::val1]
				@usage (
						value = "Notice that, as the key should be unique, the addition of an item at an existing position (i.e. existing key) "
								+ "will only modify the value associated with the given key.",
						examples = { @example (
								value = "add \"val3\" at: \"x\" to: workingMap;",
								var = "workingMap",
								equals = "[\"x\"::\"val3\", \"val2\"::\"val2\", \"5\"::\"val4\"]",
								returnType = "null") }), // workingMap
															// now
															// equals
															// [x::val3,
															// val2::value2,
															// 5::val4]
				@usage (
						value = "On a map, the all facet will add all value of a container  in the map (so as pair val_cont::val_cont)",
						examples = { @example (
								value = "add [\"val4\",\"val5\"] all: true at: \"x\" to: workingMap;",
								var = "workingMap",
								equals = "[\"x\"::\"val3\", \"val2\"::\"val2\", \"5\"::\"val4\",\"val4\"::\"val4\",\"val5\"::\"val5\"]",
								returnType = "null") }), // workingMap
															// now
															// equals
															// [x::val3,
															// val2::value2,
															// 5::val4,
															// val4::value4,
															// val5::value5]

				@usage (
						value = "In case of a graph, we can use the facets `node`, `edge` and `weight` to add a node, an edge or weights to the graph. However, these facets are now considered as deprecated, and it is advised to use the various edge(), node(), edges(), nodes() operators, which can build the correct objects to add to the graph ",
						examples = { @example (
								value = "graph g <- as_edge_graph([{1,5}::{12,45}]);"),
								@example (
										value = "add edge: {1,5}::{2,3} to: g;"),
								@example (
										value = "g.vertices",
										returnType = IKeyword.LIST,
										equals = "[{1,5},{12,45},{2,3}]"),
								@example (
										value = "g.edges",
										returnType = IKeyword.LIST,
										equals = "[polyline({1.0,5.0}::{12.0,45.0}),polyline({1.0,5.0}::{2.0,3.0})]"),
								@example (
										value = "add node: {5,5} to: g;"),
								@example (
										value = "g.vertices",
										returnType = IKeyword.LIST,
										equals = "[{1.0,5.0},{12.0,45.0},{2.0,3.0},{5.0,5.0}]"),
								@example (
										value = "g.edges",
										returnType = IKeyword.LIST,
										equals = "[polyline({1.0,5.0}::{12.0,45.0}),polyline({1.0,5.0}::{2.0,3.0})]") }) },
		see = { "put", "remove" })
@symbol (
		name = IKeyword.ADD,
		kind = ISymbolKind.SINGLE_STATEMENT,
		with_sequence = false,
		concept = { IConcept.CONTAINER, IConcept.GRAPH, IConcept.NODE, IConcept.EDGE, IConcept.MAP, IConcept.MATRIX,
				IConcept.LIST })
@inside (
		kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER },
		symbols = IKeyword.CHART)
@validator (AddValidator.class)
@serializer (AddSerializer.class)
public class AddStatement extends AbstractContainerStatement {

	public static class AddSerializer extends SymbolSerializer<StatementDescription> {

		@Override
		protected void serialize(final SymbolDescription cd, final StringBuilder sb, final boolean includingBuiltIn) {
			final IExpression item = cd.getFacetExpr(ITEM);
			final IExpression list = cd.getFacetExpr(TO);
			final IExpression allFacet = cd.getFacetExpr(ALL);
			final IExpression at = cd.getFacetExpr(AT);
			final boolean isAll = allFacet != null && allFacet.isConst() && "true".equals(allFacet.literalValue());
			sb.append(list.serialize(false));
			if (at != null) {
				sb.append('[').append(at.serialize(includingBuiltIn)).append(']');
			}
			sb.append(isAll ? " <<+ " : " <+ ");
			sb.append(item.serialize(includingBuiltIn)).append(';');
		}
	}

	@SuppressWarnings ({ "rawtypes" })
	public static class AddValidator extends ContainerValidator {

		@Override
		public void validateIndexAndContentTypes(final String keyword, final IDescription cd, final boolean all) {
			final IExpression item = cd.getFacetExpr(ITEM);
			final IExpression list = cd.getFacetExpr(TO);
			final IExpression allFacet = cd.getFacetExpr(ALL);
			if (item == null) { return; }
			if (allFacet != null && allFacet.isConst() && "true".equals(allFacet.literalValue())) {
				if (!item.getGamlType().isContainer()) {
					cd.warning(
							"The use of 'all' will have no effect here, as " + item.serialize(false)
									+ " is not a container. Only this value will be added to " + list.serialize(false),
							IGamlIssue.WRONG_CONTEXT, ALL);
					cd.removeFacets(ALL);
				}
			}
			if (list.getGamlType().id() == IType.MAP && item.getGamlType().id() == IType.PAIR) {
				final IType<?> contentType = list.getGamlType().getContentType();
				final IType<?> valueType = item.getGamlType().getContentType();
				final IType<?> mapKeyType = list.getGamlType().getKeyType();
				final IType<?> pairKeyType = item.getGamlType().getKeyType();
				if (contentType != Types.NO_TYPE && !valueType.isTranslatableInto(contentType)) {
					cd.warning(
							"The type of the contents of " + list.serialize(false) + " (" + contentType
									+ ") does not match with the type of the value of " + item.serialize(false),
							IGamlIssue.SHOULD_CAST, IKeyword.ITEM, contentType.toString());
				}
				if (mapKeyType != Types.NO_TYPE && !mapKeyType.isTranslatableInto(pairKeyType)) {
					cd.warning("The type of the index of " + list.serialize(false) + " (" + mapKeyType
							+ ") does not match with that of the key of " + item.serialize(false) + " (" + pairKeyType
							+ ")", IGamlIssue.SHOULD_CAST, IKeyword.ITEM, mapKeyType.toString());
				}
			} else {
				super.validateIndexAndContentTypes(keyword, cd, all);
			}
		}
	}

	public AddStatement(final IDescription desc) {
		super(desc);
		setName("add to " + list.serialize(false));
	}

	// @Override
	// protected Object buildValue(final IScope scope, final
	// IContainer.Modifiable container) {
	// // AD: Added to fix issue 1043: a "add" + an index on a map is equivalent
	// to a "put", so the same operation
	// // is applied when building the value (see PutStatement#buildValue()).
	// // 01/02/14: Not useful anymore
	// // if ( this.list.getType().id() == IType.MAP && index != null ) { return
	// container.buildValue(scope,
	// // new GamaPair(null, this.item.value(scope), Types.NO_TYPE,
	// Types.NO_TYPE)); }
	// return super.buildValue(scope, container);
	// }

	@Override
	protected void apply(final IScope scope, final Object object, final Object position,
			final IContainer.Modifiable container) throws GamaRuntimeException {
		if (position != null && !container.checkBounds(scope, position, true)) {
			throw GamaRuntimeException.warning("Index " + position + " out of bounds of " + list.serialize(false),
					scope);
		}
		if (!asAll) {
			if (position == null) {
				container.addValue(scope, object);
			} else {
				container.addValueAtIndex(scope, position, object);
			}
		} else {
			if (object instanceof IContainer) {
				// AD July 2020: Addition of the position (see #2985)
				container.addValues(scope, position, (IContainer<?, ?>) object);
			}
		}
	}

}
