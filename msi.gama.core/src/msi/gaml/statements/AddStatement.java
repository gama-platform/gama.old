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
package msi.gaml.statements;

import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.*;

import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.AddStatement.AddValidator;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 6 févr. 2010
 * 
 * @todo Description
 * 
 */

@facets(value = {
	@facet(name = IKeyword.TO, type = { IType.CONTAINER, IType.SPECIES, IType.AGENT, IType.GEOMETRY }, optional = false, doc = { @doc("an expression that evaluates to a container") }),
	@facet(name = IKeyword.ITEM, type = IType.NONE, optional = true, doc = { @doc("any expression to add in the container") }),
	@facet(name = IKeyword.EDGE, type = IType.NONE, optional = true, doc = { @doc("a pair that will be added to a graph as an edge (if nodes do not exist, they are also added)") }),
	@facet(name = IKeyword.VERTEX, type = IType.NONE, optional = true, doc = { @doc(deprecated = "Use 'node' instead") }),
	@facet(name = IKeyword.NODE, type = IType.NONE, optional = true, doc = { @doc("an expression that will be added to a graph as a node") }),
	@facet(name = IKeyword.AT, type = IType.NONE, optional = true, doc = { @doc("position in the container of added element") }),
	@facet(name = IKeyword.ALL, type = IType.NONE, optional = true),
	@facet(name = IKeyword.WEIGHT, type = IType.FLOAT, optional = true) }, omissible = IKeyword.ITEM)
@doc(value = "Allows to add, i.e. to insert, a new element in a container (a list, matrix, map, ...).\\ Incorrect use: The addition of a new element at a position out of the bounds of the container will produce a warning and let the container unmodified.", usages = {
	@usage(value = "The new element can be added either at the end of the container or at a particular position.",
		examples = {@example(value="add expr to: expr_container;    // Add at the end", isExecutable=false),
					@example(value="add expr at: expr to: expr_container;   // Add at position expr", isExecutable=false)}),
	@usage(value = "Case of a list, the expression in the facet at: should be an integer.", 
		examples = {@example("list<int> workingList <- [];"),
					@example(value="add 0 at: 0 to: workingList ;", var="workingList", equals="[0]", returnType="null"),      // workingList now equals [0]
					@example(value="add 10 at: 0 to: workingList ;", var="workingList", equals="[10,0]", returnType="null"),   // workingList now equals [10,0]
					@example(value="add 20 at: 2 to: workingList ;", var="workingList", equals="[10,0,20]", returnType="null"),   // workingList now equals [10,0,20]
					@example(value="add 50 to: workingList;", var="workingList", equals="[10,0,20,50]", returnType="null")}),          // workingList now equals [10,0,20,50]
	@usage(value = "Case of a matrix: this statement can not be used on matrix. Please refer to the statement put."),
	@usage(value = "Case of a map: As a map is basically a list of pairs key::value, we can also use the add statement on it. " +
			"It is important to note that the behavior of the statement is slightly different, in particular in the use of the at facet, which denotes the key of the pair.", 
		examples = {@example("map<string,string> workingMap <- [];"),
					@example(value="add \"val1\" at: \"x\" to: workingMap;", var="workingMap", equals="[\"x\"::\"val1\"]", returnType="null")}),  // workingMap now equals [x::val1]";
	@usage(value = "If the at facet is ommitted, a pair expr_item::expr_item will be added to the map. " +
			"An important exception is the case where the is a pair expression: in this case the pair is added.",
		examples = {@example(value="add \"val2\" to: workingMap;", var="workingMap", equals= "[\"x\"::\"val1\", \"val2\"::\"val2\"]", returnType="null"),	// workingMap now equals [nil::val2, x::val1]
					@example(value="add \"5\"::\"val4\" to: workingMap; ", var="workingMap", equals= "[\"x\"::\"val1\", \"val2\"::\"val2\", \"5\"::\"val4\"]", returnType="null")}),   // workingMap now equals [nil::val2, 5::val4, x::val1]
	@usage(value = "Notice that, as the key should be unique, the addition of an item at an existing position (i.e. existing key) " +
			"will only modify the value associated with the given key.",
		examples = {@example(value="add \"val3\" at: \"x\" to: workingMap;", var="workingMap", equals="[\"x\"::\"val3\", \"val2\"::\"val2\", \"5\"::\"val4\"]", returnType="null")}),   // workingMap now equals [null::value2, 5::val4, x::val3]
	@usage(value = "In case of a graph, we can use the facets `node`, `edge` and `weight` to add a node, an edge or weights to the graph", examples= {
		@example(value="graph g <- as_edge_graph([{1,5}::{12,45}]);"),
		@example(value="add edge: {1,5}::{2,3} to: g;",returnType="null",var="g", equals="as_edge_graph([{1,5}::{12,45},{1,5}::{2,3}])"),
		@example(value="add node: {5,5} to: g;"),
		@example(value="g.vertices",returnType=IKeyword.LIST,equals="[{1.0,5.0},{12.0,45.0},{2.0,3.0},{5.0,5.0}]"),
		@example(value="g.edges",returnType=IKeyword.LIST,equals="[polyline({1.0,5.0}::{12.0,45.0}),polyline({2.0,3.0}::{5.0,5.0})]")})}, see={"put","remove"})
@symbol(name = IKeyword.ADD, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT }, symbols = IKeyword.CHART)
@validator(AddValidator.class)
public class AddStatement extends AbstractContainerStatement {

	public static class AddValidator extends ContainerValidator {

		@Override
		public void validateIndexAndContentTypes(final String keyword, final IDescription cd, final boolean all) {
			IExpression item = cd.getFacets().getExpr(ITEM);
			IExpression list = cd.getFacets().getExpr(TO);
			IExpression whole = cd.getFacets().getExpr(ALL);
			IExpression index = cd.getFacets().getExpr(AT);
			if ( list.getType().id() == IType.MAP && item.getType().id() == IType.PAIR ) {
				final IType contentType = list.getType().getContentType();
				final IType valueType = item.getType().getContentType();
				final IType mapKeyType = list.getType().getKeyType();
				final IType pairKeyType = item.getType().getKeyType();
				if ( contentType != Types.NO_TYPE && !valueType.isTranslatableInto(contentType) ) {
					cd.warning("The type of the contents of " + list.toGaml() + " (" + contentType +
						") does not match with the type of the value of " + item.toGaml(), IGamlIssue.SHOULD_CAST,
						IKeyword.ITEM, contentType.toString());
				}
				if ( mapKeyType != Types.NO_TYPE && !mapKeyType.isTranslatableInto(pairKeyType) ) {
					cd.warning("The type of the index of " + list.toGaml() + " (" + mapKeyType +
						") does not match with that of the key of " + item.toGaml() + " (" + pairKeyType + ")",
						IGamlIssue.SHOULD_CAST, IKeyword.ITEM, mapKeyType.toString());
				}
			} else {
				super.validateIndexAndContentTypes(keyword, cd, all);
			}
		}

	}

	// private final IExpression weight;

	public AddStatement(final IDescription desc) {
		super(desc);
		// weight = getFacet(IKeyword.WEIGHT);
		setName("add to " + list.toGaml());
	}

	@Override
	protected void apply(final IScope scope, final Object object, final Object position,
		final IContainer.Modifiable container) throws GamaRuntimeException {
		if ( position != null && !container.checkBounds(scope, position, true) ) { throw GamaRuntimeException
			.warning("Index " + position + " out of bounds of " + list.toGaml()); }
		if ( !asAll ) {
			// Object toAdd = container.buildValue(scope, object, containerType);
			if ( position == null ) {
				container.addValue(scope, object);
			} else {
				container.addValueAtIndex(scope, position, object);
			}
		} else {
			if ( object instanceof IContainer ) {
				// IContainer toAdd = container.buildValues(scope, (IContainer) object, containerType);
				container.addVallues(scope, (IContainer) object);
			} else {
				// Object toAdd = container.buildValue(scope, object, containerType);
				container.setAllValues(scope, object);
			}
		}
	}

	/**
	 * @param object
	 * @param position
	 * @param container
	 * @return
	 */
	// protected Object buildObjectToAdd(final IScope scope, final Object object, final Object position,
	// final Modifiable container) {
	// // Case of list and matrix: the object is casted to the content type of the container
	// // if ( container instanceof IList || container instanceof IMatrix ) { return castToContentType(scope, object);
	// // }
	// // if ( container instanceof GamaMap ) {
	// // if ( position == null ) {
	// // if ( object instanceof GamaPair ) { return new GamaPair(castToKeyType(scope, ((GamaPair) object).key),
	// // castToContentType(scope, ((GamaPair) object).value)); }
	// // Object value = castToContentType(scope, object);
	// // Object key = castToKeyType(scope, value);
	// // return new GamaPair(key, value);
	// // } else {
	// // Object value = castToContentType(scope, object);
	// // Object key = castToKeyType(scope, position);
	// // return new GamaPair(key, value);
	// // }
	// // }
	// if ( container instanceof IGraph ) {
	// if ( object instanceof NodeToAdd ) { return new NodeToAdd(
	// castToKeyType(scope, ((NodeToAdd) object).object), ((NodeToAdd) object).weight); }
	// if ( object instanceof EdgeToAdd ) { return new EdgeToAdd(
	// castToKeyType(scope, ((EdgeToAdd) object).source), castToKeyType(scope, ((EdgeToAdd) object).target),
	// castToContentType(scope, ((EdgeToAdd) object).object), ((EdgeToAdd) object).weight); }
	// }
	// Double weight = this.weight == null ? null : Cast.asFloat(scope, this.weight.value(scope));
	// boolean isNode = getFacet(IKeyword.VERTEX) != null;
	// if ( isNode ) { return new NodeToAdd(castToKeyType(scope, object), weight); }
	// if ( object instanceof GamaPair ) { return new EdgeToAdd(castToKeyType(scope, ((GamaPair) object).key),
	// castToKeyType(scope, ((GamaPair) object).value), null, weight); }
	// return new EdgeToAdd(null, null, castToContentType(scope, object), weight);
	// }

}
