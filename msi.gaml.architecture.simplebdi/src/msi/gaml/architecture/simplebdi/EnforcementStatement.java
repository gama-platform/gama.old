package msi.gaml.architecture.simplebdi;

import msi.gama.common.interfaces.IKeyword;
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
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;

@symbol(name = EnforcementStatement.ENFORCEMENT, kind = ISymbolKind.SINGLE_STATEMENT, with_sequence = false, concept = {
		IConcept.BDI })
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets(value = {
		@facet(name = IKeyword.NAME, type = IType.ID, optional = true, doc = @doc("the identifier of the enforcement")),
		@facet(name = IKeyword.WHEN, type = IType.BOOL, optional = true, doc = @doc("A boolean value to enforce only with a certain condition")),
		@facet (
				name = EnforcementStatement.NORM,
				type = NormType.id,
				optional = true,
				doc = @doc ("The norm to enforce")),
		@facet (
				name = EnforcementStatement.SANCTION,
				type = SanctionType.id,
				optional = true,
				doc = @doc ("The sanction to apply if the norm is violated")),
		@facet (
				name = EnforcementStatement.REWARD,
				type = SanctionType.id,
				optional = true,
				doc = @doc ("The positive sanction to apply if the norm has been followed"))}
		)
@doc(value = "enables to directly add a belief from the variable of a perceived specie.", examples = {
		@example("focus var:speed /*where speed is a variable from a species that is being perceived*/") })

//statement servant à controler les normes pour appliquer des sanctions, sur le moodèle du focus
public class EnforcementStatement extends AbstractStatement{

	public static final String ENFORCEMENT = "enforcement";
	public static final String NORM = "norm";
	public static final String SANCTION = "sanction";
	public static final String REWARD = "reward";

	final IExpression name;
	final IExpression when;
	final IExpression norm;
	final IExpression sanction;
	final IExpression reward;
	
	public EnforcementStatement(IDescription desc) {
	super(desc);
	name = getFacet(IKeyword.NAME);
	when = getFacet(IKeyword.WHEN);
	norm = getFacet(EnforcementStatement.NORM);
	sanction = getFacet(EnforcementStatement.SANCTION);
	reward = getFacet(EnforcementStatement.REWARD);
	}

	@Override
	protected Object privateExecuteIn(IScope scope) throws GamaRuntimeException {
		// TODO Auto-generated method stub
		return null;
	}

}
