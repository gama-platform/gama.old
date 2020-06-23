/*******************************************************************************************************
 *
 * msi.gaml.statements.test.TestStatement.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling
 * and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
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
public class TestStatement extends AbstractStatementSequence implements WithTestSummary<IndividualTestSummary> {

	SetUpStatement setup = null;
	// Assertions contained in the test.
	List<AssertStatement> assertions = new ArrayList<>();
	IndividualTestSummary summary;

	public TestStatement(final IDescription desc) {
		super(desc);
		if (hasFacet(IKeyword.NAME)) {
			setName("test " + getLiteral(IKeyword.NAME));
		}
	}

	@Override
	public IndividualTestSummary getSummary() {
		if (summary == null) {
			summary = new IndividualTestSummary(this);
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
			if (s instanceof AssertStatement) {
				assertions.add((AssertStatement) s);
			}
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
				try {
					// TODO Verify this call (wrt IScope.execute())
					lastResult = statement.executeOn(scope);
				} catch (final GamaAssertException e) {} catch (final GamaRuntimeException e) {
					if (statement instanceof AssertStatement) {} else {
						getSummary().setState(TestState.ABORTED);
						getSummary().setError(e.getMessage());
					}
				}
			}
		} finally {
			scope.disableTryMode();
		}
		return lastResult;

	}

	@Override
	public String getTitleForSummary() {
		return "Test " + getName();
	}

	@Override
	public Collection<? extends WithTestSummary<?>> getSubElements() {
		return assertions;
	}

}
