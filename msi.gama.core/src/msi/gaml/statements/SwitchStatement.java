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

import static msi.gama.common.interfaces.IKeyword.MATCH;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.*;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.SwitchStatement.SwitchValidator;
import msi.gaml.types.*;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * IfPrototype.
 * 
 * @author drogoul 14 nov. 07
 */
@symbol(name = IKeyword.SWITCH, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.LAYER })
@facets(value = { @facet(name = IKeyword.VALUE, type = IType.NONE, optional = false, doc = @doc("an expression")) }, omissible = IKeyword.VALUE)
@doc(value="The \"switch... match\" statement is a powerful replacement for imbricated \"if ... else ...\" constructs. All the blocks that match are executed in the order they are defined. The block prefixed by default is executed only if none have matched (otherwise it is not).", usages = {
	@usage(value="The prototypical syntax is as follows:",examples ={
		@example(value="switch an_expression {",isExecutable=false), @example(value="        match value1 {...}",isExecutable=false), @example(value="        match_one [value1, value2, value3] {...}",isExecutable=false), @example(value="        match_between [value1, value2] {...}",isExecutable=false), @example(value="        default {...}",isExecutable=false), @example(value="}",isExecutable=false)
	}),
	@usage(value="Example:",examples ={
		@example(value="switch 3 {",test=false), @example(value="   match 1 {write \"Match 1\"; }",test=false), @example(value="   match 2 {write \"Match 2\"; }",test=false), @example(value="   match 3 {write \"Match 3\"; }",test=false), @example(value="   match_one [4,4,6,3,7]  {write \"Match one_of\"; }",test=false), @example(value="   match_between [2, 4] {write \"Match between\"; }",test=false), @example(value="   default {write \"Match Default\"; }",test=false), @example(value="}",test=false),			
		@example(value="string val1 <- \"\";",test=false,isTestOnly=true),@example(value="switch 1 {",test=false,isTestOnly=true), @example(value="   match 1 {val1 <- val1 + \"1\"; }",test=false,isTestOnly=true), @example(value="   match 2 {val1 <- val1 + \"2\"; }",test=false,isTestOnly=true), @example(value="   match_one [1,1,6,4,7]  {val1 <- val1 + \"One_of\"; }",test=false,isTestOnly=true), @example(value="   match_between [2, 4] {val1 <- val1 + \"Between\"; }",test=false,isTestOnly=true), @example(value="   default {val1 <- val1 + \"Default\"; }",test=false,isTestOnly=true), @example(value="}",test=false,isTestOnly=true), @example(var="val1",equals="'1One_of'",isTestOnly=true),			
		@example(value="string val2 <- \"\";",test=false,isTestOnly=true),@example(value="switch 2 {",test=false,isTestOnly=true), @example(value="   match 1 {val2 <- val2 + \"1\"; }",test=false,isTestOnly=true), @example(value="   match 2 {val2 <- val2 + \"2\"; }",test=false,isTestOnly=true), @example(value="   match_one [1,1,6,4,7]  {val2 <- val2 + \"One_of\"; }",test=false,isTestOnly=true), @example(value="   match_between [2, 4] {val2 <- val2 + \"Between\"; }",test=false,isTestOnly=true), @example(value="   default {val2 <- val2 + \"Default\"; }",test=false,isTestOnly=true), @example(value="}",test=false,isTestOnly=true), @example(var="val2",equals="'2Between'",isTestOnly=true),			
		@example(value="string val10 <- \"\";",test=false,isTestOnly=true),@example(value="switch 10 {",test=false,isTestOnly=true), @example(value="   match 1 {val10 <- val10 + \"1\"; }",test=false,isTestOnly=true), @example(value="   match 2 {val10 <- val10 + \"2\"; }",test=false,isTestOnly=true), @example(value="   match_one [1,1,6,4,7]  {val10 <- val10 + \"One_of\"; }",test=false,isTestOnly=true), @example(value="   match_between [2, 4] {val10 <- val10 + \"Between\"; }",test=false,isTestOnly=true), @example(value="   default {val10 <- val10 + \"Default\"; }",test=false,isTestOnly=true), @example(value="}",test=false,isTestOnly=true), @example(var="val10",equals="'Default'",isTestOnly=true)			
	})
})
@validator(SwitchValidator.class)
public class SwitchStatement extends AbstractStatementSequence {

	

	public static Predicate allMatches = new Predicate<IDescription>() {

		@Override
		public boolean apply(final IDescription input) {
			return input.getKeyword().equals(MATCH);
		}
	};

	public static class SwitchValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription desc) {

			// FIXME This assertion only verifies the case of "match" (not match_one or match_between)
			List<IDescription> children = desc.getChildren();
			Iterable<IDescription> matches = Iterables.filter(children, allMatches);
			IExpression switchValue = desc.getFacets().getExpr(VALUE);
			if ( switchValue == null ) { return; }
			IType switchType = switchValue.getType();
			if ( switchType.equals(Types.NO_TYPE) ) { return; }
			for ( IDescription match : matches ) {
				IExpression value = match.getFacets().getExpr(VALUE);
				if ( value == null ) {
					continue;
				}
				IType matchType = value.getType();
				// AD : special case introduced for ints and floats (a warning is emitted)
				if ( Types.intFloatCase(matchType, switchType) ) {
					match.warning("The value " + value.toGaml() + " of type " + matchType +
						" is compared to a value of type " + switchType + ", which will never match ",
						IGamlIssue.SHOULD_CAST, IKeyword.VALUE, switchType.toString());
					continue;
				}

				if ( matchType.isTranslatableInto(switchType) ) {
					continue;
				}
				match.warning("The value " + value.toGaml() + " of type " + matchType +
					" is compared to a value of type " + switchType + ", which will never match ",
					IGamlIssue.SHOULD_CAST, IKeyword.VALUE, switchType.toString());
			}

		}

	}

	public MatchStatement[] matches;
	public MatchStatement defaultMatch;
	final IExpression value;

	/**
	 * The Constructor.
	 * 
	 * @param sim the sim
	 */
	public SwitchStatement(final IDescription desc) {
		super(desc);
		value = getFacet(IKeyword.VALUE);
		setName("switch" + value.toGaml());

	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		final List<MatchStatement> cases = new ArrayList();
		for ( final ISymbol c : commands ) {
			if ( c instanceof MatchStatement ) {
				if ( ((MatchStatement) c).getLiteral(IKeyword.KEYWORD).equals(IKeyword.DEFAULT) ) {
					defaultMatch = (MatchStatement) c;
				} else {
					cases.add((MatchStatement) c);
				}
			}
		}
		commands.removeAll(cases);
		commands.remove(defaultMatch);
		matches = cases.toArray(new MatchStatement[0]);
		super.setChildren(commands);
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		boolean hasMatched = false;
		final Object switchValue = value.value(scope);
		Object lastResult = null;
		for ( int i = 0; i < matches.length; i++ ) {
			if ( scope.interrupted() ) { return lastResult; }
			if ( matches[i].matches(scope, switchValue) ) {
				lastResult = matches[i].executeOn(scope);
				hasMatched = true;
			}
		}
		if ( !hasMatched && defaultMatch != null ) { return defaultMatch.executeOn(scope); }
		return lastResult;
	}

	@Override
	public void leaveScope(final IScope scope) {
		// Clears any _loop_halted status
		scope.popLoop();
		super.leaveScope(scope);
	}
}