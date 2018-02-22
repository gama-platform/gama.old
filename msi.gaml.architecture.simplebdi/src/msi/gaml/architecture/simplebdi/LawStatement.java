package msi.gaml.architecture.simplebdi;

import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;

//Définition des lois pour créer des obligations sur le modèle des rêgles d'inférences avec en supplément un seuil d'obéissance

@symbol (
		name = LawStatement.LAW,
		kind = ISymbolKind.SINGLE_STATEMENT,
		with_sequence = false,
		concept = { IConcept.BDI })
@inside (
		kinds = { ISymbolKind.SPECIES, ISymbolKind.MODEL })
@facets (
		value = { @facet (
				name = LawStatement.BELIEF,
				type = PredicateType.id,
				optional = true,
				doc = @doc ("The mandatory belief")),
				@facet (
						name = LawStatement.BELIEFS,
						type = IType.LIST,
						of = PredicateType.id,
						optional = true,
						doc = @doc ("The mandatory beliefs")),
				@facet (
						name = LawStatement.NEW_OBLIGATION,
						type = PredicateType.id,
						optional = true,
						doc = @doc ("The predicate that will be added as an obligation")),
				@facet (
						name = LawStatement.NEW_OBLIGATIONS,
						type = IType.LIST,
						of = PredicateType.id,
						optional = true,
						doc = @doc ("The list of predicates that will be added as obligations")),
				@facet (
						name = IKeyword.WHEN,
						type = IType.BOOL,
						optional = true,
						doc = @doc (" ")),
				
				@facet (
						name = IKeyword.PARALLEL,
						type = { IType.BOOL, IType.INT },
						optional = true,
						doc = @doc ("setting this facet to 'true' will allow 'perceive' to use concurrency with a parallel_bdi architecture; setting it to an integer will set the threshold under which they will be run sequentially (the default is initially 20, but can be fixed in the preferences). This facet is true by default.")),
				@facet (
						name = LawStatement.STRENGTH,
						type = { IType.FLOAT, IType.INT },
						optional = true,
						doc = @doc ("The stregth of the mental state created")),
				@facet (
						name = LawStatement.LIFETIME,
						type = IType.INT,
						optional = true,
						doc = @doc ("the lifetime value of the mental state created")),
				@facet (
						name = RuleStatement.THRESHOLD,
						type = IType.FLOAT,
						optional = true,
						doc = @doc ("Threshold linked to the obedience value.")),
				
				@facet (
						name = IKeyword.NAME,
						type = IType.ID,
						optional = true,
						doc = @doc ("The name of the law")) },
		omissible = IKeyword.NAME)
@doc (
		value = "enables to add a desire or a belief or to remove a belief, a desire or an intention if the agent gets the belief or/and desire or/and condition mentioned.",
		examples = {
				@example ("rule belief: new_predicate(\"test\") when: flip(0.5) new_desire: new_predicate(\"test\")") })


public class LawStatement extends AbstractStatement{

	public static final String LAW = "law";
	public static final String BELIEF = "belief";
	public static final String BELIEFS = "beliefs";
	public static final String NEW_OBLIGATION = "new_obligation";
	public static final String NEW_OBLIGATIONS = "new_obligations";
	public static final String STRENGTH = "strength";
	public static final String LIFETIME = "lifetime";
	public static final String THRESHOLD = "threshold";
	

	final IExpression when;
	final IExpression parallel;
	final IExpression belief;
	final IExpression beliefs;
	final IExpression newObligation;
	final IExpression newObligations;
	final IExpression strength;
	final IExpression lifetime;
	final IExpression threshold;
	
	public IExpression getContextExpression() {
		return when;
	}
	
	public IExpression getBeliefExpression() {
		return belief;
	}
	
	public IExpression getObligationExpression() {
		return newObligation;
	}
	
	public LawStatement(IDescription desc) {
		super(desc);
		when = getFacet(IKeyword.WHEN);
		belief = getFacet(LawStatement.BELIEF);
		beliefs = getFacet(LawStatement.BELIEFS);
		newObligation = getFacet(LawStatement.NEW_OBLIGATION);
		newObligations = getFacet(LawStatement.NEW_OBLIGATIONS);
		strength = getFacet(LawStatement.STRENGTH);
		lifetime = getFacet("lifetime");
		threshold = getFacet(LawStatement.THRESHOLD);
		parallel = getFacet(IKeyword.PARALLEL);
		setName(desc.getName());
	}


	@Override
	protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {
		if (newObligation == null && newObligations == null)
			return null;
		if (when == null || Cast.asBool(scope, when.value(scope))) {
			final MentalState tempBelief = new MentalState("Belief");
			Double obedienceValue = (Double) scope.getAgent().getAttribute("obedience");
			if (belief != null) {
				tempBelief.setPredicate((Predicate) belief.value(scope));
			}
			if (belief == null || SimpleBdiArchitecture.hasBelief(scope, tempBelief)) {				
				if (beliefs == null || hasBeliefs(scope, (List<Predicate>) beliefs.value(scope))) {
					if (threshold == null || obedienceValue>= (Double) threshold.value(scope)) {
						if (newObligation != null) {
							final Predicate newObl = (Predicate) newObligation.value(scope);
							final MentalState tempNewObligation = new MentalState("Obligation", newObl);
							if (strength != null) {
								tempNewObligation.setStrength(
								Cast.asFloat(scope, strength.value(scope)));
							}
							if (lifetime != null) {
								tempNewObligation
									.setLifeTime(Cast.asInt(scope, lifetime.value(scope)));
							}
							SimpleBdiArchitecture.addObligation(scope, tempNewObligation);
							SimpleBdiArchitecture.clearIntention(scope);
							final IAgent agent = scope.getAgent();
							agent.setAttribute(SimpleBdiArchitecture.CURRENT_PLAN, null);
						}
						if (newObligations != null) {
							final List<Predicate> newObls =
								(List<Predicate>) newObligations.value(scope);
							for (final Predicate newDes : newObls) {
								final MentalState tempDesires =
									new MentalState("Obligation", newDes);
								if (strength != null) {
									tempDesires.setStrength(
										Cast.asFloat(scope, strength.value(scope)));
								}
								if (lifetime != null) {
									tempDesires.setLifeTime(
											Cast.asInt(scope, lifetime.value(scope)));
								}
								SimpleBdiArchitecture.addObligation(scope, tempDesires);
								SimpleBdiArchitecture.clearIntention(scope);
								final IAgent agent = scope.getAgent();
								agent.setAttribute(SimpleBdiArchitecture.CURRENT_PLAN, null);
							}
						}
					}
				}
			}
		}	
		return null;
	}


private boolean hasBeliefs(final IScope scope, final List<Predicate> predicates) {
	for (final Predicate p : predicates) {
		final MentalState temp = new MentalState("Belief", p);
		if (!SimpleBdiArchitecture.hasBelief(scope, temp))
			return false;
	}
	return true;
}

}
