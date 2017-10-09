/*********************************************************************************************
 * 
 *
 * 'TestStatement.java', in plugin 'irit.gaml.extensions.test', is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.statements.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import msi.gama.runtime.exceptions.GamaAssertException;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.operators.Strings;
import msi.gaml.species.GamlSpecies;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;

@symbol (
		name = { "test" },
		kind = ISymbolKind.BEHAVIOR,
		with_sequence = true,
		unique_name = true,
		concept = { IConcept.TEST })
@inside (
		kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT, ISymbolKind.MODEL })
@facets (
		value = { @facet (
				name = IKeyword.NAME,
				type = IType.ID,
				optional = true,
				doc = @doc ("identifier of the test")) },
		omissible = IKeyword.NAME)
@doc (
		value = "The test statement allows modeler to define a set of assertions that will be tested. Before the execution of the embedded set of instructions, if a setup is defined in the species, model or experiment, it is executed. In a test, if one assertion fails, the evaluation of other assertions continue.",
		usages = { @usage (
				value = "An example of use:",
				examples = { @example (
						value = "species Tester {",
						isExecutable = false),
						@example (
								value = "    // set of attributes that will be used in test",
								isExecutable = false),
						@example (
								value = "",
								isExecutable = false),
						@example (
								value = "    setup {",
								isExecutable = false),
						@example (
								value = "        // [set of instructions... in particular initializations]",
								isExecutable = false),
						@example (
								value = "    }",
								isExecutable = false),
						@example (
								value = "",
								isExecutable = false),
						@example (
								value = "    test t1 {",
								isExecutable = false),
						@example (
								value = "       // [set of instructions, including asserts]",
								isExecutable = false),
						@example (
								value = "    }",
								isExecutable = false),
						@example (
								value = "}",
								isExecutable = false) }) },
		see = { "setup", "assert" })
public class TestStatement extends AbstractStatementSequence {

	public static enum State {
		FAILED("failed"), PASSED("passed"), NOT_RUN("not run"), WARNING("warning"), ABORTED("aborted");
		private final String name;

		State(final String s) {
			name = s;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	SetUpStatement setup = null;
	// Assertions contained in the test. True means they are verified, false they are not, null they have emitted a
	// warning
	List<AssertStatement> assertions = new ArrayList<>();
	boolean aborted = false;

	public TestStatement(final IDescription desc) {
		super(desc);
		if (hasFacet(IKeyword.NAME)) {
			setName("test " + getLiteral(IKeyword.NAME));
		}
	}

	@Override
	public void setEnclosing(final ISymbol enclosing) {
		super.setEnclosing(enclosing);
		setup = (SetUpStatement) ((GamlSpecies) enclosing).getBehaviors().stream()
				.filter(p -> p instanceof SetUpStatement).findAny().orElse(null);
	}

	@Override
	public void setChildren(final Iterable<? extends ISymbol> commands) {
		super.setChildren(commands);
		commands.forEach(s -> {
			if (s instanceof AssertStatement)
				assertions.add((AssertStatement) s);
		});
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		aborted = false;
		if (setup != null) {
			setup.setup(scope);
		}
		Object lastResult = null;
		scope.enableTryMode();
		for (final IStatement statement : commands) {
			try {
				lastResult = statement.executeOn(scope);
			} catch (final GamaAssertException e) {
				// Does nothing
			} catch (final GamaRuntimeException e2) {
				// Other exceptions abort the test
				aborted = true;
				break;
			}
		}
		scope.disableTryMode();
		return lastResult;

	}

	public State getState() {
		if (aborted)
			return State.ABORTED;
		State state = State.NOT_RUN;
		for (final AssertStatement s : assertions) {
			switch (s.getState()) {
				case NOT_RUN:
					break;
				case FAILED:
					state = State.FAILED;
					break;
				case PASSED:
					if (state.equals(State.NOT_RUN))
						state = State.PASSED;
					break;
				case WARNING:
					if (state.equals(State.PASSED) || state.equals(State.NOT_RUN))
						state = State.WARNING;
					break;
				default:
			}
		}
		return state;
	}

	public Collection<AssertStatement> getAssertions() {
		return assertions;
	}

	public String getSummary() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Test ").append(getName()).append(" ").append(getState()).append(Strings.LN);
		for (final AssertStatement assertion : assertions) {
			sb.append(Strings.TAB).append("Assertion ").append(assertion.getFacet(IKeyword.VALUE).serialize(true))
					.append(" ").append(assertion.getState()).append(Strings.LN);
		}
		return sb.toString();
	}

	public void reset() {
		aborted = false;
		assertions.forEach(a -> a.state = State.NOT_RUN);
	}

}
