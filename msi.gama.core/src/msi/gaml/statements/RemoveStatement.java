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

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gaml.descriptions.IDescription;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 6 févr. 2010
 * 
 * @todo Description
 * 
 */

@facets(value = {
	@facet(name = IKeyword.ITEM, type = IType.NONE, optional = true, doc = @doc("any expression to remove from the container")),
	@facet(name = IKeyword.FROM, type = { IType.CONTAINER, IType.SPECIES, IType.AGENT, IType.GEOMETRY }, optional = false, doc = { @doc("an expression that evaluates to a container") }),
	@facet(name = IKeyword.INDEX, type = IType.NONE, optional = true, doc = @doc("any expression, the position in the container of removed element")),
	@facet(name = IKeyword.EDGE, type = IType.NONE, optional = true, doc = @doc("any expression, the edge to be removed from a graph")),
	@facet(name = IKeyword.VERTEX, type = IType.NONE, optional = true, doc = { @doc(deprecated = "Use 'node' instead") }),
	@facet(name = IKeyword.NODE, type = IType.NONE, optional = true, doc = @doc("any expression, the node to be removed from a graph")),
	@facet(name = IKeyword.KEY, type = IType.NONE, optional = true, doc = @doc("any expression, the key of the pair to be removed from a pair")),
	@facet(name = IKeyword.ALL, type = IType.NONE, optional = true, doc = @doc("any expression, ?????? ")) }, omissible = IKeyword.ITEM)
@symbol(name = IKeyword.REMOVE, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT }, symbols = IKeyword.CHART)
@doc(value="Allows the agent to remove an element from a container (a list, matrix, map...).",usages = {
	@usage(value="This statement should be used in the following ways, depending on the kind of container used and the expected action on it:", examples = {
		@example(value="remove expr from: expr_container;",isExecutable=false),@example(value="remove index: expr from: expr_container;",isExecutable=false),@example(value="remove key: expr from: expr_container;",isExecutable=false),@example(value="remove all: expr from: expr_container;",isExecutable=false)}),
	@usage(value="In the case of list, the facet `item:` is used to remove the first occurence of a given expression, whereas `all` is used to remove all the occurences of the given expression.", examples = {
		@example("list<int> removeList <- [3,2,1,2,3];"),@example(value="remove 2 from: removeList;", var="removeList", equals="[3,1,2,3]", returnType="null"),@example(value="remove 3 all: true from: removeList;", var="removeList", equals="[1,2]", returnType="null"),@example(value="remove index: 1 from: removeList;", var="removeList", equals="[1]", returnType="null")}),
	@usage(value="In the case of map, the facet `key:` is used to remove the pair identified by the given key.", examples = {
		@example("map<string,int> removeMap <- [\"x\"::5, \"y\"::7, \"z\"::7];"),@example(value="remove key: \"x\" from: removeMap;", var="removeMap", equals="[\"y\"::7, \"z\"::7]", returnType="null"),@example(value="remove 7 all: true from: removeMap;", var="removeMap", equals="map([])", returnType="null")}),	
	@usage(value="In addition, a map a be managed as a list with pair key as index. Given that, facets item:, all: and index: can be used in the same way:", examples = {
		@example("map<string,int> removeMapList <- [\"x\"::5, \"y\"::7, \"z\"::7, \"t\"::5];"),@example(value="remove 7 from: removeMapList;", var="removeMapList", equals="[\"x\"::5, \"z\"::7, \"t\"::5]", returnType="null"),@example(value="remove [5,7] all: true from: removeMapList;", var="removeMapList", equals="[\"t\"::5]", returnType="null"),@example(value="remove index: \"t\" from: removeMapList;", var="removeMapList", equals="map([])", returnType="null")}),	
	@usage(value="In the case of a graph, both edges and nodes can be removes using node: and edge facets. If a node is removed, all edges to and from this node are also removed.", examples = {
		@example("graph removeGraph <- graph([{1,2}::{3,4},{3,4}::{5,6},{3,4},{5,6}]);"),@example(value="remove node: {1,2} from: removeGraph;", var="removeGraph", equals="graph([{3,4}::{5,6},{3,4},{5,6}])", returnType="null"),@example(value="remove edge: {3,4}::{5,6} from: removeGraph;", var="removeGraph", equals="graph([{3,4},{5,6}])", returnType="null")}),	
//	@usage(value="In the case of a geometry, a point can be removed from the geometry.", examples = {
//		@example("geometry removeGeom <- polyline([{1,2},{3,4},{5,6}]);"),@example(value="remove {3,4} from: removeGeom;", var="removeGeom", equals="polyline([{1,2},{5,6}]);", returnType="null")}),	
	@usage(value="This statement can not be used on *matrix*.")
}, see={"add","put"})
public class RemoveStatement extends AbstractContainerStatement {

	public RemoveStatement(final IDescription desc) {
		super(desc);
		setName("remove from " + list.toGaml());
	}

	@Override
	protected void apply(final IScope scope, final Object object, final Object position,
		final IContainer.Modifiable container) throws GamaRuntimeException {
		// if ( position != null ) {
		// if ( !container.checkBounds(position, false) ) { throw GamaRuntimeException.warning("Index " + position +
		// " out of bounds of " + item.toGaml()); }
		// }

		if ( position == null ) {
			// If key/at/index/node is not mentioned
			if ( asAll ) {
				// if we "remove all"
				if ( asAllValues ) {
					// if a container is passed
					container.removeValues(scope, (IContainer) object);
				} else {
					// otherwise if it is a simple value
					container.removeAllOccurencesOfValue(scope, object);
				}
			} else {
				// if it is a simple remove
				container.removeValue(scope, object);
			}
		} else {
			if ( asAllIndexes ) {
				container.removeIndexes(scope, (IContainer) position);
			} else {
				// If a key/index/at/node is mentioned
				// simply remove the index.
				container.removeIndex(scope, position);
			}
		}

	}

	@Override
	protected Object buildValue(final IScope scope, final IContainer.Modifiable container) {
		return this.item.value(scope);
	}

	@Override
	protected Object buildIndex(final IScope scope, final IContainer.Modifiable container) {
		Object o = this.index.value(scope);
		// if ( asAllIndexes && o instanceof IContainer ) { return ((IContainer) o).reverse(scope); }
		return o;
	}
}
