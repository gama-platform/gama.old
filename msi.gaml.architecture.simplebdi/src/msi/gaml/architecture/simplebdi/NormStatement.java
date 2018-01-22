package msi.gaml.architecture.simplebdi;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;

@symbol(name = { NormStatement.NORM }, kind = ISymbolKind.BEHAVIOR, with_sequence = true, concept = {
		IConcept.BDI })
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.MODEL })
@facets(value = { @facet(name = IKeyword.WHEN, type = IType.BOOL, optional = true),
		@facet(name = SimpleBdiArchitecture.FINISHEDWHEN, type = IType.BOOL, optional = true),
		@facet(name = SimpleBdiArchitecture.PRIORITY, type = IType.FLOAT, optional = true),
		@facet(name = IKeyword.NAME, type = IType.ID, optional = true),
		@facet(name = NormStatement.INTENTION, type = PredicateType.id, optional = true),
		@facet(name = NormStatement.OBLIGATION, type = PredicateType.id, optional = true),
		@facet(name = NormStatement.THRESHOLD, type = IType.FLOAT, optional = true),
		@facet(name = SimpleBdiArchitecture.INSTANTANEAOUS, type = IType.BOOL, optional = true) }, omissible = IKeyword.NAME)


//Statement ppour définir une norme ssur le même principe qu'un plan
public class NormStatement extends AbstractStatementSequence{

	public static final String NORM = "norm";
	public static final String INTENTION = "intention";
	public static final String OBLIGATION = "obligation";
	public static final String THRESHOLD = "threshold";
	
	final IExpression _when;
	final IExpression _priority;
	final IExpression _executedwhen;
	final IExpression _instantaneous;
	final IExpression _intention;
	final IExpression _obligation;
	final IExpression _threshold;
	
	public NormStatement(IDescription desc) {
		super(desc);
		_when = getFacet(IKeyword.WHEN);
		_priority = getFacet(SimpleBdiArchitecture.PRIORITY);
		_executedwhen = getFacet(SimpleBdiArchitecture.FINISHEDWHEN);
		_instantaneous = getFacet(SimpleBdiArchitecture.INSTANTANEAOUS);
		_intention = getFacet(NormStatement.INTENTION);
		_obligation = getFacet(NormStatement.OBLIGATION);
		_threshold = getFacet(NormStatement.THRESHOLD);
		setName(desc.getName());
	}

}
