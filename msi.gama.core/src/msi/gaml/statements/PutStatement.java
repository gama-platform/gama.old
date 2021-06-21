/*******************************************************************************************************
 *
 * msi.gaml.statements.PutStatement.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
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
import msi.gama.util.GamaPair;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gama.util.graph.IGraph;
import msi.gaml.compilation.annotations.serializer;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.SymbolDescription;
import msi.gaml.descriptions.SymbolSerializer;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.PutStatement.PutSerializer;
import msi.gaml.statements.PutStatement.PutValidator;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 6 févr. 2010
 *
 * @todo Description
 *
 */

@facets (
		value = { @facet (
				name = IKeyword.AT,
				type = IType.NONE,
				optional = true,
				doc = @doc ("any expression")),
				@facet (
						name = IKeyword.KEY,
						type = IType.NONE,
						optional = true,
						doc = @doc ("any expression")),
				@facet (
						name = IKeyword.ALL,
						type = IType.NONE,
						optional = true,
						doc = @doc ("any expression")),
				@facet (
						name = IKeyword.ITEM,
						type = IType.NONE,
						optional = true,
						doc = @doc ("any expression")),
				@facet (
						name = IKeyword.EDGE,
						type = IType.NONE,
						optional = true,
						doc = @doc (
								deprecated = "use the 'edge' operator instead (e.g. 'put edge(...) in: g')",
								value = "Indicates that the item to put should be considered as an edge of the receiving graph. Soon to be deprecated, use 'put edge(item)...' instead")),
				@facet (
						name = IKeyword.WEIGHT,
						type = IType.FLOAT,
						optional = true,
						doc = @doc (
								deprecated = "use the 'edge' or 'node' operators with a weight parameter",
								value = "an expression that evaluates to a float")),
				@facet (
						name = IKeyword.IN,
						type = { IType.CONTAINER, IType.SPECIES, IType.AGENT, IType.GEOMETRY },
						optional = false,
						doc = @doc ("an expression that evaluates to a container")) },
		omissible = IKeyword.ITEM)
@symbol (
		name = IKeyword.PUT,
		kind = ISymbolKind.SINGLE_STATEMENT,
		with_sequence = false,
		concept = { IConcept.CONTAINER, IConcept.MAP, IConcept.MATRIX, IConcept.LIST })
@inside (
		kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER },
		symbols = IKeyword.CHART)
@validator (PutValidator.class)
@doc (
		value = "Allows the agent to replace a value in a container at a given position (in a list or a map) or for a given key (in a map). Note that the behavior and the type of the attributes depends on the specific kind of container.",
		usages = { @usage (
				value = "The allowed parameters configurations are the following ones:",
				examples = { @example (
						value = "put expr at: expr in: expr_container;",
						isExecutable = false),
						@example (
								value = "put all: expr in: expr_container;",
								isExecutable = false) }),
				@usage (
						value = "In the case of a list, the position should an integer in the bound of the list. The facet all: is used to replace all the elements of the list by the given value.",
						examples = { @example (
								var = "putList",
								value = "[1,2,3,4,5]",
								returnType = "list<int>",
								equals = "[1,2,3,4,5]"),
								@example (
										value = "put -10 at: 1 in: putList;",
										var = "putList",
										equals = "[1,-10,3,4,5]"),
								@example (
										value = "put 10 all: true in: putList;",
										var = "putList",
										equals = "[10,10,10,10,10]") }), // ,@example(value="put
																			// 11
																			// at:7
																			// in:
																			// putList;",raises="error",
																			// isTestOnly=true)}),
				@usage (
						value = "In the case of a matrix, the position should be a point in the bound of the matrix. The facet all: is used to replace all the elements of the matrix by the given value.",
						examples = { @example (
								var = "putMatrix",
								value = "matrix([[0,1],[2,3]])",
								returnType = "matrix<int>",
								equals = "matrix([[0,1],[2,3]])"),
								@example (
										value = "put -10 at: {1,1} in: putMatrix;",
										var = "putMatrix",
										equals = "matrix([[0,1],[2,-10]])"),
								@example (
										value = "put 10 all: true in: putMatrix;",
										var = "putMatrix",
										equals = "matrix([[10,10],[10,10]])") }),
				@usage (
						value = "In the case of a map, the position should be one of the key values of the map. Notice that if the given key value does not exist in the map, the given pair key::value will be added to the map. The facet all is used to replace the value of all the pairs of the map.",
						examples = { @example (
								var = "putMap",
								value = "[\"x\"::4,\"y\"::7]",
								returnType = "map<string,int>",
								equals = "[\"x\"::4,\"y\"::7]"),
								@example (
										value = "put -10 key: \"y\" in: putMap;",
										var = "putMap",
										equals = "[\"x\"::4,\"y\"::-10]"),
								@example (
										value = "put -20 key: \"z\" in: putMap;",
										var = "putMap",
										equals = "[\"x\"::4,\"y\"::-10, \"z\"::-20]"),
								@example (
										value = "put -30 all: true in: putMap;",
										var = "putMap",
										equals = "[\"x\"::-30,\"y\"::-30, \"z\"::-30]") }) })
@serializer (PutSerializer.class)
public class PutStatement extends AddStatement {

	public static class PutSerializer extends SymbolSerializer<SymbolDescription> {

		@Override
		protected void serialize(final SymbolDescription cd, final StringBuilder sb, final boolean includingBuiltIn) {
			final IExpression item = cd.getFacetExpr(ITEM);
			final IExpression list = cd.getFacetExpr(TO);
			// IExpression allFacet = f.getExpr(ALL);
			final IExpression at = cd.getFacetExpr(AT);
			sb.append(list.serialize(includingBuiltIn));
			sb.append('[');
			if (at != null) { sb.append(at.serialize(includingBuiltIn)); }
			sb.append(']');
			sb.append(" <- ");
			sb.append(item.serialize(includingBuiltIn)).append(';');
		}
	}

	public static class PutValidator extends ContainerValidator {

		@Override
		public void validate(final IDescription cd) {
			final IExpression index = cd.getFacetExpr(AT, KEY);
			final IExpression whole = cd.getFacetExpr(ALL);
			if (whole != null && whole.getGamlType().id() != IType.BOOL) {
				cd.error("Put cannot be used to add several values", IGamlIssue.CONFLICTING_FACETS, ALL);
				return;
			}
			final boolean all = whole == null ? false : !whole.literalValue().equals(FALSE);
			if (!all && index == null) {
				cd.error("Put needs a valid index (facets 'at:' or 'key:') ", IGamlIssue.MISSING_FACET,
						cd.getUnderlyingElement(), AT, "0");
			} else {
				super.validate(cd);
			}
		}

	}

	public PutStatement(final IDescription desc) {
		super(desc);
		setName("put in " + list.serialize(false));
	}

	@Override
	protected Object buildValue(final IScope scope, final IGraph container) {
		// if ( asAllValues ) { return container.buildValues(scope, (IContainer)
		// this.item.value(scope), containerType);
		// }
		// AD: Added to fix issue 1043: the value computed by maps is a pair
		// (whose key is never used afterwards). However,
		// when casting an existing pair to the key type/content type of the
		// map, this would produce wrong values for the
		// contents of the pair (or the list with 2 elements).
		// O1/02/14: Not useful anymore
		// if ( this.list.getType().id() == IType.MAP ) { return
		// container.buildValue(scope,
		// new GamaPair(null, this.item.value(scope))); }
		return container.buildValue(scope, this.item.value(scope));
	}

	@Override
	protected void apply(final IScope scope, final Object object, final Object position,
			final IContainer.Modifiable container) throws GamaRuntimeException {
		if (!asAll) {
			if (!container.checkBounds(scope, position, false)) throw GamaRuntimeException
					.error("Index " + position + " out of bounds of " + list.serialize(false), scope);
			// Issue #3099
			if (container instanceof IList && position instanceof GamaPair) {
				((IList<Object>) container).replaceRange(scope, (GamaPair) position, object);
			} else {
				container.setValueAtIndex(scope, position, object);
			}
		} else {
			container.setAllValues(scope, object);
		}
	}
}
