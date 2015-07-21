/*********************************************************************************************
 * 
 *
 * 'FocusStatement.java', in plugin 'msi.gaml.architecture.simplebdi', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/

package msi.gaml.architecture.simplebdi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.statements.RemoteSequence;
import msi.gaml.types.IType;

@symbol(name = FocusStatement.FOCUS, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false, remote_context=true)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets(value = {
	@facet(name = IKeyword.VAR, type = /*{IType.FLOAT,IType.INT,IType.STRING,IType.BOOL}*/IType.NONE, doc = @doc("the variable of the perceived agent you want to add to your beliefs")),
	@facet(name = IKeyword.AGENT, type = IType.AGENT)}
,omissible = IKeyword.VAR)
@doc( value = "enables to directly add a belief from the variable of a perceived specie.",
		examples={@example("focus speed /*where speed is a variable from a species that is being perceived*/")})
public class FocusStatement extends AbstractStatement {

	public static final String FOCUS = "focus";
	
	final IExpression variable;
	final IExpression agentMyself;
	
	private RemoteSequence sequence = null;
	
	public FocusStatement(IDescription desc) {
		super(desc);
		variable = getFacet(IKeyword.VAR);
		agentMyself = getFacet(IKeyword.AGENT);
	}
	
	@Override
	public void setChildren(final List<? extends ISymbol> com) {
		sequence = new RemoteSequence(description);
		sequence.setName("commands of " + getName());
		sequence.setChildren(com);
	}

	@Override
	protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {
//		final IAgent mySelfAgent = sequence.getMyself();
		final IAgent mySelfAgent = (IAgent) agentMyself.value(scope);
		IScope scopeMySelf = null;
		if(mySelfAgent!=null){
			scopeMySelf = mySelfAgent.getScope().copy();
//Lorsqu'on touche à un scope, ça touche à tous les scoop pour le moment. Regarder d'où ça vient. Il faut en faire une copy du scope et pas assigner le même.
			scopeMySelf.push(mySelfAgent);
		}
//		final SimpleBdiArchitecture archi = null;
		final Predicate tempPred;
		//Je vais déja m'occuper de transformer la variable en prédicat. 
		//Pour ça, si la variable est une liste, j'en fait un cas particulier, sinon, je ne rajoute qu'un seul terme
		//Dans un premier temps, je vais déja faire le cas où il n'y a pas de liste. 1 seule variable par focus.
		if(variable!=null){
			String namePred = variable.getName()+"_"+scope.getAgentScope().getSpeciesName();
			String nameVar = variable.getName();
			Map<String,Object> tempValues = new HashMap<String,Object>();
			//Pour le moment, à chaque variable, la valeur associée s'appelera variable_value.
			tempValues.put(nameVar + "_value", variable.value(scope));
			tempPred = new Predicate(namePred,tempValues);
			SimpleBdiArchitecture.addBelief(scopeMySelf, tempPred);
		}
				
		return null;
	}

}
