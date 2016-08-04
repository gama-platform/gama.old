/*********************************************************************************************
 * 
 *
 * 'TestStatement.java', in plugin 'irit.gaml.extensions.test', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package irit.gaml.extensions.test.statements;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;

@symbol(name = { "test" }, kind = ISymbolKind.BEHAVIOR, with_sequence = true, unique_name = true, concept = { IConcept.TEST })
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT, ISymbolKind.MODEL })
@facets(value = { @facet(name = IKeyword.NAME, type = IType.ID, optional = true, doc = @doc("identifier of the test")) }, omissible = IKeyword.NAME)
@doc(value="The test statement allows modeler to define a set of assertions that will be tested. Before the execution of the embedded set of instructions, if a setup is defined in the species, model or experiment, it is executed. In a test, if one assertion fails, the evaluation of other assertions continue (if GAMA is configured in the preferences that the program does not stop at the first exception).", usages={ 
		@usage(value="An example of use:", examples ={
				@example(value="species Tester {", isExecutable=false),
				@example(value="    // set of attributes that will be used in test", isExecutable=false),
				@example(value="", isExecutable=false),		
				@example(value="    setup {", isExecutable=false),
				@example(value="        // [set of instructions... in particular initializations]", isExecutable=false),		
				@example(value="    }", isExecutable=false),	
				@example(value="", isExecutable=false),	
				@example(value="    test t1 {", isExecutable=false),			
				@example(value="       // [set of instructions, including asserts]", isExecutable=false),	
				@example(value="    }", isExecutable=false),
				@example(value="}", isExecutable=false)})}, see={"setup","assert"})
public class TestStatement extends AbstractStatementSequence {

	// We keep the setup in memory to avoid looking for it every time step
	SetUpStatement setup = null;
 
	// true if the setup has already been looked for, false otherwise
	boolean setupLookedFor = false;

	public TestStatement(final IDescription desc) {
		super(desc);
		if ( hasFacet(IKeyword.NAME) ) {
			setName("test " + getLiteral(IKeyword.NAME));
		}
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		if ( setup == null && !setupLookedFor ) {
			setupLookedFor = true;
			setup = scope.getAgent().getSpecies().getStatement(SetUpStatement.class, null);
		}

		if ( setup != null ) {
			setup.setup(scope);
		}
		return super.privateExecuteIn(scope);
	}

}
