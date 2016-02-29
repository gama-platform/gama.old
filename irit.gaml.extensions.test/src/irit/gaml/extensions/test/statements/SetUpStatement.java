/*********************************************************************************************
 * 
 *
 * 'SetUpStatement.java', in plugin 'irit.gaml.extensions.test', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package irit.gaml.extensions.test.statements;

import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.statements.AbstractStatementSequence;

@symbol(name = { "setup" }, kind = ISymbolKind.SEQUENCE_STATEMENT, with_sequence = true)
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.EXPERIMENT, ISymbolKind.MODEL })
@doc(value="The setup statement is used to define the set of instructions that will be executed before every [#test test].", usages={ 
	@usage(value="As every test should be independent from the others, the setup will mainly contain initialization of variables that will be used in each test.", examples ={
		@example(value="species Tester {", isExecutable=false),
		@example(value="    int val_to_test;", isExecutable=false),
		@example(value="", isExecutable=false),		
		@example(value="    setup {", isExecutable=false),
		@example(value="        val_to_test <- 0;", isExecutable=false),		
		@example(value="    }", isExecutable=false),	
		@example(value="", isExecutable=false),	
		@example(value="    test t1 {", isExecutable=false),			
		@example(value="       // [set of instructions, including asserts]", isExecutable=false),	
		@example(value="    }", isExecutable=false),
		@example(value="}", isExecutable=false)})}, see = {"test", "assert"})
public class SetUpStatement extends AbstractStatementSequence {
	public SetUpStatement(final IDescription desc) {
		super(desc);
		setName("setup");
	}

    @Override
    public Object executeOn(final IScope scope) throws GamaRuntimeException {
        // does nothing when called « normally »		
    	return null;
    }

    public Object setup(final IScope scope) throws GamaRuntimeException {
            // calls the « normal » execution defined in the superclass
            return super.executeOn(scope);
    }	
	
//	@Override
//	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
//		return super.privateExecuteIn(scope); 
//	}
}
