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
import msi.gama.precompiler.*;
import msi.gama.precompiler.GamlAnnotations.usages;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 6 févr. 2010
 * 
 * @todo Description
 * 
 */

@facets(value = {
	@facet(name = IKeyword.TO, type = { IType.CONTAINER, IType.SPECIES, IType.AGENT,
		IType.GEOMETRY }, optional = false, doc = {@doc("an expression that evaluates to a container")}),
	@facet(name = IKeyword.ITEM, type = IType.NONE, optional = true, doc = {@doc("any expression to add in the container")}),
	@facet(name = IKeyword.EDGE, type = IType.NONE, optional = true),
	@facet(name = IKeyword.VERTEX, type = IType.NONE, optional = true),
	@facet(name = IKeyword.AT, type = IType.NONE, optional = true, doc = {@doc("position in the container of element added")}),
	@facet(name = IKeyword.ALL, type = IType.NONE, optional = true),
	@facet(name = IKeyword.WEIGHT, type = IType.FLOAT, optional = true) }, omissible = IKeyword.ITEM)
@doc(value = "Allows to add, i.e. to insert, a new element in a container (a list, matrix, map, ...).", usages = {
	@usages(value = "The new element can be added either at the end of the container or at a particular position.",
			examples = {"add expr to: expr_container;    // Add at the end",
						"add expr at: expr to: expr_container;   // Add at position expr"}),
	@usages(value = "Case of a list, the expression in the attribute at: should be an integer.", 
			examples = {"let emptyList type: list <- [];",
						"add 0 at: 0 to: emptyList ;    // emptyList now equals [0]",
						"add 10 at: 0 to: emptyList ;   // emptyList now equals [10,0]",
						"add 25 at: 2 to: emptyList ;   // emptyList now equals [10,0,20]",
						"add 50 to: emptyList;          // emptyList now equals [10,0,20,50]"}),
	@usages(value = "Case of a matrix: this statement can not be used on matrix. Please refer to the statement put."),
	@usages(value = "Case of a map: As a map is basically a list of pairs key::value, we can also use the add statement on it. " +
			"It is important to note that the behavior of the statement is slightly different, in particular in the use of the at attribute.", 
			examples = {"let emptyMap type: map <- [];",
						"add \"val1\" at: \"x\" to: emptyMap;   // emptyList now equals [x::val1]"}),
	@usages(value = "If the at: attribute is ommitted, a pair null::expr_item will be added to the map. " +
			"An important exception is the case where the is a pair expression: in this case the pair is added.",
			examples = {"add \"val2\" to: emptyMap;      // emptyList now equals [null::val2, x::val1]",
						"add 5::\"val4\" to: emptyMap;   // emptyList now equals [null::val2, 5::val4, x::val1]"}),
	@usages(value = "Notice that, as the key should be unique, the addition of an item at an existing position (i.e. existing key) " +
			"will only modify the value associated with the given key.",
			examples = {"add \"val3\" at: \"x\" to: emptyMap;   // emptyList now equals [null::value2, 5::val4, x::val3]"})
	})
@symbol(name = IKeyword.ADD, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT }, symbols = IKeyword.CHART)
public class AddStatement extends AbstractContainerStatement {

	private final IExpression weight;

	public AddStatement(final IDescription desc) {
		super(desc);
		weight = getFacet(IKeyword.WEIGHT);
		setName("add to " + list.toGaml());
	}

	@Override
	protected void apply(final IScope scope, final Object toAdd, final Object position,
		final Boolean whole, final IContainer container) throws GamaRuntimeException {
		// AD 29/02/13 : Normally taken in charge by the parser, now.
		// if ( container.isFixedLength() ) { throw new GamaRuntimeException("Cannot add to " +
		// list.toGaml(), true); }
		Object param = weight == null ? null : weight.value(scope);
		if ( position != null && !container.checkBounds(position, true) ) { throw GamaRuntimeException.warning("Index " + position + " out of bounds of " + list.toGaml()); }
		container.add(scope, position, toAdd, param, whole, true);

	}

}
