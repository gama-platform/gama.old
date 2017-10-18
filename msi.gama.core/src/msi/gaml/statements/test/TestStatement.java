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
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.jgrapht.alg.util.Pair;

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
import msi.gama.util.GamaColor;
import msi.gama.util.TOrderedHashMap;
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

	public static class TestSummary {

		public static TestSummary FINISHED = new TestSummary();
		public static TestSummary INDIVIDUAL_TEST_FINISHED = new TestSummary();
		public static TestSummary BEGINNING = new TestSummary();

		private static int COUNT = 0;

		public final URI uri;
		public final String modelName;
		public final String testName;
		public final Map<String, State> asserts;
		boolean aborted;
		public final int number = COUNT++;

		private TestSummary() {
			uri = null;
			modelName = null;
			testName = null;
			asserts = null;
		}

		TestSummary(final TestStatement test) {
			final EObject object = test.getDescription().getUnderlyingElement(null);
			uri = object == null ? null : EcoreUtil.getURI(object);
			modelName = test.getDescription().getModelDescription().getName();
			testName = test.getName();
			asserts = new TOrderedHashMap<>();
			for (final AssertStatement assertion : test.getAssertions()) {
				asserts.put(assertion.getAssertion(), State.NOT_RUN);
			}
		}

		@Override
		public boolean equals(final Object o) {
			if (o instanceof TestSummary) { return ((TestSummary) o).testName.equals(testName)
					&& ((TestSummary) o).modelName.equals(modelName); }
			return false;
		}

		public void reset() {
			aborted = false;
			for (final String key : asserts.keySet()) {
				asserts.put(key, State.NOT_RUN);
			}
		}

		void abort() {
			aborted = true;
		}

		void addAssertResult(final AssertStatement a, final State s) {
			final String key = a.getAssertion();
			asserts.put(key, s);
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append(getState()).append(": ").append(testName).append(" ").append(Strings.LN);
			for (final String assertion : asserts.keySet()) {
				sb.append(Strings.TAB).append(asserts.get(assertion)).append(": ").append(assertion).append(" ")
						.append(Strings.LN);
			}
			return sb.toString();

		}

		public State getState() {
			if (aborted)
				return State.ABORTED;
			State state = State.NOT_RUN;
			for (final State s : asserts.values()) {
				switch (s) {
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

		public Collection<Pair<String, State>> getAssertions() {
			final List<Pair<String, State>> result = new ArrayList<>();
			asserts.forEach((n, s) -> result.add(new Pair<String, State>(n, s)));
			return result;
		}

	}

	public static enum State {
		ABORTED("aborted"), FAILED("failed"), WARNING("warning"), PASSED("passed"), NOT_RUN("not run");
		private final String name;

		State(final String s) {
			name = s;
		}

		@Override
		public String toString() {
			return name;
		}

		public GamaColor getColor() {
			switch (this) {
				case FAILED:
					return GamaColor.getNamed("gamared");
				case NOT_RUN:
					return GamaColor.getNamed("gamablue");
				case WARNING:
					return GamaColor.getNamed("gamaorange");
				case PASSED:
					return GamaColor.getNamed("gamagreen");
				default:
					return new GamaColor(83, 95, 107); // GamaColors.toGamaColor(IGamaColors.NEUTRAL.color());
			}
		}
	}

	SetUpStatement setup = null;
	// Assertions contained in the test.
	List<AssertStatement> assertions = new ArrayList<>();
	TestSummary summary;

	public TestStatement(final IDescription desc) {
		super(desc);
		if (hasFacet(IKeyword.NAME)) {
			setName("test " + getLiteral(IKeyword.NAME));
		}
	}

	public TestSummary getSummary() {
		if (summary == null) {
			summary = new TestSummary(this);
		}
		return summary;
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
		getSummary().reset();
		if (setup != null) {
			setup.setup(scope);
		}
		Object lastResult = null;
		try {
			scope.enableTryMode();
			for (final IStatement statement : commands) {
				AssertStatement a = assertions.contains(statement) ? (AssertStatement) statement : null;
				try {
					lastResult = statement.executeOn(scope);
				} catch (final GamaAssertException e) {
					if (a != null) {
						final State s = e.isWarning() ? State.WARNING : State.FAILED;
						getSummary().addAssertResult(a, s);
						a = null;
					}
				} catch (final GamaRuntimeException e2) {
					// Other exceptions abort the test
					getSummary().abort();
					break;
				}
				if (a != null) {
					getSummary().addAssertResult(a, State.PASSED);
				}
			}
		} finally {
			scope.disableTryMode();
		}
		return lastResult;

	}

	public Collection<AssertStatement> getAssertions() {
		return assertions;
	}

}
