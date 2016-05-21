/*********************************************************************************************
 * 
 *
, * 'SimpleBdiPlanStatement.java', in plugin 'msi.gaml.architecture.simplebdi', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.architecture.simplebdi;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;
import msi.gaml.architecture.simplebdi.SimpleBdiPlanStatement.SimpleBdiPlanValidator;

@symbol(name = { SimpleBdiArchitecture.PLAN}, kind = ISymbolKind.BEHAVIOR, with_sequence = true,
concept = { IConcept.BDI })
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.MODEL })
@facets(value = { @facet(name = IKeyword.WHEN, type = IType.BOOL, optional = true),
		@facet(name = SimpleBdiArchitecture.FINISHEDWHEN, type = IType.BOOL, optional = true),
	@facet(name = SimpleBdiArchitecture.PRIORITY, type = IType.FLOAT, optional = true),
	@facet(name = IKeyword.NAME, type = IType.ID, optional = true),
	@facet(name = SimpleBdiPlanStatement.INTENTION, type = PredicateType.id, optional = true),
	@facet(name = SimpleBdiPlanStatement.EMOTION, type = EmotionType.id, optional = true),
	@facet(name = SimpleBdiPlanStatement.THRESHOLD, type = IType.FLOAT, optional = true),
	@facet(name = SimpleBdiArchitecture.INSTANTANEAOUS, type = IType.BOOL, optional = true)}, omissible = IKeyword.NAME)
@validator(SimpleBdiPlanValidator.class)
public class SimpleBdiPlanStatement extends AbstractStatementSequence {
	 
	public static class SimpleBdiPlanValidator implements IDescriptionValidator {

		/**
		 * Method validate()
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final IDescription description) {
			// Verify that the state is inside a species with fsm control
			SpeciesDescription species = description.getSpeciesContext();
			IArchitecture control = species.getControl();
			if ( !(control instanceof SimpleBdiArchitecture) ) {
				description.error("A plan can only be defined in a simple_bdi architecture species",
						IGamlIssue.WRONG_CONTEXT);
				return; 
			}
		}
	}
	public static final String INTENTION = "intention";
	public static final String EMOTION = "emotion";
	public static final String THRESHOLD = "threshold";

	 final IExpression _when;
	 final IExpression _priority;
	 final IExpression _executedwhen;
	 final IExpression _instantaneous;
	 final IExpression _intention;
	 final IExpression _emotion;
	 final  IExpression _threshold;

	public IExpression getPriorityExpression() {
		return _priority;
	}

	public IExpression getContextExpression() {
		return _when;
	}

	public IExpression getExecutedExpression() {
		return _executedwhen;
	}

	public IExpression getInstantaneousExpression() {
		return _instantaneous;
	}

	public IExpression getIntentionExpression(){
		return _intention;
	}
	
	public IExpression getEmotionExpression(){
		return _emotion;
	}
	
	public IExpression getThreshold(){
		return _threshold;
	}
	
	public SimpleBdiPlanStatement(final IDescription desc) {
		super(desc);
		_when = getFacet(IKeyword.WHEN);
		_priority = getFacet(SimpleBdiArchitecture.PRIORITY);
		_executedwhen = getFacet(SimpleBdiArchitecture.FINISHEDWHEN);
		_instantaneous = getFacet(SimpleBdiArchitecture.INSTANTANEAOUS);
		_intention = getFacet(SimpleBdiPlanStatement.INTENTION);
		_emotion = getFacet(SimpleBdiPlanStatement.EMOTION);
		_threshold = getFacet(SimpleBdiPlanStatement.THRESHOLD);
		if ( hasFacet(IKeyword.NAME) ) {
			setName(getLiteral(IKeyword.NAME));
		}
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		if ( _when == null || Cast.asBool(scope, _when.value(scope)) ) { return super.privateExecuteIn(scope); }
		return null;
	}

	public Double computePriority(final IScope scope) throws GamaRuntimeException {
		return Cast.asFloat(scope, _priority.value(scope));
	}
}

