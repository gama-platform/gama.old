/*********************************************************************************************
 * 
 * 
 * 'PutStatement.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.statements;

import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.combination;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IContainer;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.PutStatement.PutValidator;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 6 févr. 2010
 * 
 * @todo Description
 * 
 */

@facets(value = {
	@facet(name = IKeyword.AT, type = IType.NONE, optional = true, doc = @doc("any expression")),
	@facet(name = IKeyword.KEY, type = IType.NONE, optional = true, doc = @doc("any expression")),
	@facet(name = IKeyword.ALL, type = IType.NONE, optional = true, doc = @doc("any expression")),
	@facet(name = IKeyword.ITEM, type = IType.NONE, optional = true, doc = @doc("any expression")),
	@facet(name = IKeyword.EDGE,
		type = IType.NONE,
		optional = true,
		doc = @doc("Indicates that the item to put should be considered as an edge of the receiving graph. Soon to be deprecated, use 'put edge(item)...' instead")),
	@facet(name = IKeyword.WEIGHT,
		type = IType.FLOAT,
		optional = true,
		doc = @doc("an expression that evaluates to a float")),
	@facet(name = IKeyword.IN,
		type = { IType.CONTAINER, IType.SPECIES, IType.AGENT, IType.GEOMETRY },
		optional = false,
		doc = @doc("an expression that evaluates to a container")) },
	combinations = { @combination({ IKeyword.AT, IKeyword.ITEM, IKeyword.IN }),
		@combination({ IKeyword.ALL, IKeyword.IN }) },
	omissible = IKeyword.ITEM)
@symbol(name = IKeyword.PUT, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT }, symbols = IKeyword.CHART)
@validator(PutValidator.class)
@doc(value = "Allows the agent to replace a value in a container at a given position (in a list or a map) or for a given key (in a map). Note that the behavior and the type of the attributes depends on the specific kind of container.",
	usages = {
		@usage(value = "The allowed parameters configurations are the following ones:", examples = {
			@example(value = "put expr at: expr in: expr_container;", isExecutable = false),
			@example(value = "put all: expr in: expr_container;", isExecutable = false) }),
		@usage(value = "In the case of a list, the position should an integer in the bound of the list. The facet all: is used to replace all the elements of the list by the given value.",
			examples = {
				@example(var = "putList", value = "[1,2,3,4,5]", returnType = "list<int>", equals = "[1,2,3,4,5]"),
				@example(value = "put -10 at: 1 in: putList;", var = "putList", equals = "[1,-10,3,4,5]"),
				@example(value = "put 10 all: true in: putList;", var = "putList", equals = "[10,10,10,10,10]") }),// ,@example(value="put 11 at:7 in: putList;",raises="error", isTestOnly=true)}),
		@usage(value = "In the case of a matrix, the position should be a point in the bound of the matrix. The facet all: is used to replace all the elements of the matrix by the given value.",
			examples = {
				@example(var = "putMatrix",
					value = "matrix([[0,1],[2,3]])",
					returnType = "matrix<int>",
					equals = "matrix([[0,1],[2,3]])"),
				@example(value = "put -10 at: {1,1} in: putMatrix;",
					var = "putMatrix",
					equals = "matrix([[0,1],[2,-10]])"),
				@example(value = "put 10 all: true in: putMatrix;",
					var = "putMatrix",
					equals = "matrix([[10,10],[10,10]])") }),
		@usage(value = "In the case of a map, the position should be one of the key values of the map. Notice that if the given key value does not exist in the map, the given pair key::value will be added to the map. The facet all is used to replace the value of all the pairs of the map.",
			examples = {
				@example(var = "putMap",
					value = "[\"x\"::4,\"y\"::7]",
					returnType = "map<string,int>",
					equals = "[\"x\"::4,\"y\"::7]"),
				@example(value = "put -10 key: \"y\" in: putMap;", var = "putMap", equals = "[\"x\"::4,\"y\"::-10]"),
				@example(value = "put -20 key: \"z\" in: putMap;",
					var = "putMap",
					equals = "[\"x\"::4,\"y\"::-10, \"z\"::-20]"),
				@example(value = "put -30 all: true in: putMap;",
					var = "putMap",
					equals = "[\"x\"::-30,\"y\"::-30, \"z\"::-30]") }) })
public class PutStatement extends AddStatement {

	public static class PutValidator extends ContainerValidator {

		@Override
		public void validate(final IDescription cd) {
			Facets f = cd.getFacets();
			final IExpression index = f.getExpr(AT, KEY);
			final IExpression whole = f.getExpr(ALL);
			if ( whole != null && whole.getType().id() != IType.BOOL ) {
				cd.error("Put cannot be used to add several values", IGamlIssue.MISSING_FACET, ALL);
				return;
			}
			final boolean all = whole == null ? false : !whole.literalValue().equals(FALSE);
			if ( !all && index == null ) {
				cd.error("Put needs a valid index (facets 'at:' or 'key:') ", IGamlIssue.MISSING_FACET, AT);
			} else {
				super.validate(cd);
			}
		}

	}

	public PutStatement(final IDescription desc) {
		super(desc);
		setName("put in " + list.toGaml());
	}

	@Override
	protected Object buildValue(final IScope scope, final IContainer.Modifiable container) {
		// if ( asAllValues ) { return container.buildValues(scope, (IContainer) this.item.value(scope), containerType);
		// }
		return container.buildValue(scope, this.item.value(scope), containerType);
	}

	@Override
	protected void apply(final IScope scope, final Object object, final Object position,
		final IContainer.Modifiable container) throws GamaRuntimeException {
		// Object toPut = container.buildValue(scope, object, containerType);
		if ( !asAll ) {
			if ( !container.checkBounds(scope, position, false) ) { throw GamaRuntimeException.error("Index " +
				position + " out of bounds of " + list.toGaml(), scope); }
			container.setValueAtIndex(scope, position, object);
		} else {
			container.setAllValues(scope, object);
		}
	}
}
