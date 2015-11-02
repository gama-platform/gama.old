/*********************************************************************************************
 * 
 *
 * 'RuleStatement.java', in plugin 'msi.gaml.architecture.simplebdi', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/

package msi.gaml.architecture.simplebdi;

import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;


@symbol(name = RuleStatement.RULE, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false)
@inside(kinds = { ISymbolKind.SPECIES })
@facets(value = {
	@facet(name = RuleStatement.BELIEF, type = PredicateType.id, optional = true, doc = @doc("The mandatory belief")),
	@facet(name = RuleStatement.DESIRE, type = PredicateType.id, optional = true, doc = @doc("The mandatory desire")),
	@facet(name = RuleStatement.NEW_DESIRE , type = PredicateType.id, optional = true, doc = @doc("The desire that will be added")),
	@facet(name = RuleStatement.NEW_BELIEF, type = PredicateType.id, optional = true, doc = @doc("The belief that will be added")),
	@facet(name = IKeyword.WHEN, type = IType.BOOL, optional = true, doc = @doc(" ")),
	@facet(name = RuleStatement.PRIORITY, type = {IType.FLOAT,IType.INT}, optional = true, doc = @doc("The priority of the predicate added as a desire")),
	@facet(name = IKeyword.NAME, type = IType.ID, optional = true, doc = @doc("The name of the rule"))}
,omissible = IKeyword.NAME)
@doc( value = "enables to add a desire or a belief if the agent gets the belief or/and desire or/and condition mentioned.",
		examples={@example("rule belief: new_predicate(\"test\") when: flip(0.5) desire: new_predicate(\"test\")")})
public class RuleStatement extends AbstractStatement{

	public static final String RULE = "rule";
	public static final String BELIEF = "belief";
	public static final String DESIRE = "desire";
	public static final String NEW_DESIRE = "new_desire";
	public static final String NEW_BELIEF = "new_belief";
	public static final String PRIORITY = "priority";
	
	final IExpression when;
	final IExpression belief;
	final IExpression desire;
	final IExpression newBelief;
	final IExpression newDesire;
	final IExpression priority;
	
	public RuleStatement(IDescription desc) {
		super(desc);
		when = getFacet(IKeyword.WHEN);
		belief = getFacet(RuleStatement.BELIEF);
		desire = getFacet(RuleStatement.DESIRE);
		newBelief = getFacet(RuleStatement.NEW_BELIEF);
		newDesire = getFacet(RuleStatement.NEW_DESIRE);
		priority = getFacet(RuleStatement.PRIORITY);
	}

	@Override
	protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {
		if (newBelief == null && newDesire == null) return null;
		if ( when == null || Cast.asBool(scope, when.value(scope)) ){
			if( belief == null || SimpleBdiArchitecture.hasBelief(scope, (Predicate)(belief.value(scope)))) {
				if( desire == null || SimpleBdiArchitecture.hasDesire(scope, (Predicate)(desire.value(scope)))) {
					if (newDesire != null) {
						Predicate newDes = ((Predicate)(newDesire.value(scope)));
						if(newDes.getValues()==null){
							SimpleBdiArchitecture.addDesire(scope, null, ((Predicate)(newDesire.value(scope))));
						}else{
							//Il faut copier la liste des valeurs.
							newDes.setValues((Map<String, Object>) GamaMapFactory.createWithoutCasting(newDes.getType().getKeyType(), newDes.getType().getContentType(), ((Predicate)(newDesire.value(scope))).getValues()));
							if(priority!=null){
								newDes.setPriority(Cast.asFloat(scope, priority.value(scope)));
							}
							SimpleBdiArchitecture.addDesire(scope, null, newDes);
						}
					}
					if (newBelief != null) {
						Predicate newBel = ((Predicate)(newBelief.value(scope)));
						if(newBel.getValues()==null){
							SimpleBdiArchitecture.addBelief(scope, ((Predicate)(newBelief.value(scope))));
						}else{
							//Il faut copier la liste des valeurs.
							newBel.setValues((Map<String, Object>) GamaMapFactory.createWithoutCasting(newBel.getType().getKeyType(), newBel.getType().getContentType(), ((Predicate)(newBelief.value(scope))).getValues()));
							SimpleBdiArchitecture.addBelief(scope, newBel);
						}
					}
				}
			}
		}
		return null;
	}

}
