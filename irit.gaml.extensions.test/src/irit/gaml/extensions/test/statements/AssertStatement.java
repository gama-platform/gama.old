package irit.gaml.extensions.test.statements;

import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.combination;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.*;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;

@symbol(name = { "assert" }, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = true)
@facets(value = {
	@facet(name = IKeyword.VALUE, type = IType.NONE, optional = false),
	@facet(name = IKeyword.EQUALS, type = IType.NONE, optional = true),
	@facet(name = IKeyword.ISNOT, type = IType.NONE, optional = true),
	@facet(name = IKeyword.RAISES, type = IType.ID, optional = true)},
	combinations = { 
		@combination({ IKeyword.VALUE, IKeyword.EQUALS }),
		@combination({ IKeyword.VALUE, IKeyword.ISNOT }),
		@combination({ IKeyword.VALUE, IKeyword.RAISES })},
	omissible = IKeyword.VALUE)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT, ISymbolKind.SPECIES, ISymbolKind.MODEL })
public class AssertStatement extends AbstractStatement {
	StatementDescription setUpStatement;
	IExpression value;
	IExpression equals;
	IExpression isnot;
	IExpression raises;

	public AssertStatement(final IDescription desc) {
		super(desc);
		setName("assert");
		
		List<IDescription> statements = desc.getSpeciesContext().getChildren();
		for ( IDescription s : statements ) {
			if ( s.getName().equals("setUp") ) {
				setUpStatement = (StatementDescription) s;
			}
		}
		
		value = getFacet(IKeyword.VALUE);
		if ( getFacet(IKeyword.EQUALS) != null ) {
			equals = getFacet(IKeyword.EQUALS);
		}
		if ( getFacet(IKeyword.ISNOT) != null ) {
			isnot = getFacet(IKeyword.ISNOT);
		}
		if ( getFacet(IKeyword.RAISES) != null ) {
			raises = getFacet(IKeyword.RAISES);
		}
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		
		if ( getFacet(IKeyword.EQUALS) != null ) {
			if(!value.value(scope).equals(equals.value(scope))){
				throw GamaRuntimeException.error("Assert equals ERROR : " + value.toGaml() + " is not equals to " + equals.value(scope));
			}
			return null;
		}

		if ( getFacet(IKeyword.ISNOT) != null ) {
			if(value.value(scope).equals(isnot.value(scope))){
				throw GamaRuntimeException.error("Assert is_not ERROR: " + value.toGaml() + " is equals to " + isnot.value(scope));
			}
			return null;
		}

		if ( getFacet(IKeyword.RAISES) != null ) {
			// System.out.println(raises.value(scope));
			try {
				value.value(scope);
			} catch(GamaRuntimeException e){
				if(e.isWarning() && IKeyword.ERROR.equals(raises.getName())){
					throw GamaRuntimeException.error("Assert raises ERROR: " + value.toGaml() + " does not raise an error. It raises a warning.");					
				}
				if(!e.isWarning() && IKeyword.WARNING_TEST.equals(raises.getName())){
					throw GamaRuntimeException.error("Assert raises ERROR: " + value.toGaml() + " does not raise a warning. It raises an error.");					
				}
				if(!IKeyword.ERROR.equals(raises.getName()) && ! IKeyword.WARNING_TEST.equals(raises.getName())){
					throw GamaRuntimeException.error("Assert raises ERROR: " + value.toGaml() + " raises " + (e.isWarning()? "a warning.":"an error.") );	
				}
				System.out.println("Toto OK" + raises.getName());
				return null;
			} catch(Exception e){
				if(IKeyword.WARNING_TEST.equals(raises.getName())){
					throw GamaRuntimeException.error("Assert raises ERROR: " + value.toGaml() + " does not raise a warning. It raises an error.");
				}
				if(!IKeyword.ERROR.equals(raises.getName()) ){
					throw GamaRuntimeException.error("Assert raises ERROR: " + value.toGaml() + " raises an error." );	
				}
				System.out.println("error weel raised");
				return null;
			}
			if(IKeyword.ERROR.equals(raises.getName()) || IKeyword.WARNING_TEST.equals(raises.getName())){
				throw GamaRuntimeException.error("Assert raises ERROR: " + value.toGaml() + " does not raise anything.");	
			}
			return null;
		}
		
		// Case where there no equals, is_not or raises
		// the value is thus evaluated as a boolean and tested
		if(! Cast.asBool(scope, getFacet(IKeyword.VALUE).value(scope))){
			throw GamaRuntimeException.error("Assert ERROR: " + value.toGaml() + " is false");		
		}
		return null;
	}

}
