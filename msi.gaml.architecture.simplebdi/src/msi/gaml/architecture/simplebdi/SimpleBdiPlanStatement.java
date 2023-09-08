/*******************************************************************************************************
 *
 * SimpleBdiPlanStatement.java, in msi.gaml.architecture.simplebdi, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.architecture.simplebdi;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.architecture.simplebdi.SimpleBdiPlanStatement.SimpleBdiPlanValidator;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.compilation.annotations.validator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.SkillDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;

/**
 * The Class SimpleBdiPlanStatement.
 */
@symbol(name = { SimpleBdiArchitecture.PLAN }, kind = ISymbolKind.BEHAVIOR, with_sequence = true, concept = {
		IConcept.BDI })
@inside(kinds = { ISymbolKind.SPECIES, ISymbolKind.MODEL })
@facets(value = { @facet(name = IKeyword.WHEN, type = IType.BOOL, optional = true),
		@facet(name = SimpleBdiArchitecture.FINISHEDWHEN, type = IType.BOOL, optional = true),
		@facet(name = SimpleBdiArchitecture.PRIORITY, type = IType.FLOAT, optional = true),
		@facet(name = IKeyword.NAME, type = IType.ID, optional = true),
		@facet(name = SimpleBdiPlanStatement.INTENTION, type = PredicateType.id, optional = true),
		@facet(name = SimpleBdiPlanStatement.EMOTION, type = EmotionType.EMOTIONTYPE_ID, optional = true),
		@facet(name = SimpleBdiPlanStatement.THRESHOLD, type = IType.FLOAT, optional = true),
		@facet(name = SimpleBdiArchitecture.INSTANTANEAOUS, type = IType.BOOL, optional = true) }, omissible = IKeyword.NAME)
@validator(SimpleBdiPlanValidator.class)
@doc("define an action plan performed by an agent using the BDI engine")
public class SimpleBdiPlanStatement extends AbstractStatementSequence {

	/**
	 * The Class SimpleBdiPlanValidator.
	 */
	public static class SimpleBdiPlanValidator implements IDescriptionValidator<StatementDescription> {

		/**
		 * Method validate()
		 * 
		 * @see msi.gaml.compilation.IDescriptionValidator#validate(msi.gaml.descriptions.IDescription)
		 */
		@Override
		public void validate(final StatementDescription description) {
			// Verify that the state is inside a species with fsm control
			final SpeciesDescription species = description.getSpeciesContext();
			final SkillDescription control = species.getControl();
			if (!SimpleBdiArchitecture.class.isAssignableFrom(control.getJavaBase())) {
				description.error("A plan can only be defined in a simple_bdi architecture species",
						IGamlIssue.WRONG_CONTEXT);
				return;
			}
		}
	}

	/** The Constant INTENTION. */
	public static final String INTENTION = "intention";
	
	/** The Constant EMOTION. */
	public static final String EMOTION = "emotion";
	
	/** The Constant THRESHOLD. */
	public static final String THRESHOLD = "threshold";

	/** The when. */
	final IExpression _when;
	
	/** The priority. */
	final IExpression _priority;
	
	/** The executedwhen. */
	final IExpression _executedwhen;
	
	/** The instantaneous. */
	final IExpression _instantaneous;
	
	/** The intention. */
	final IExpression _intention;
	
	/** The emotion. */
	final IExpression _emotion;
	
	/** The threshold. */
	final IExpression _threshold;

	/**
	 * Gets the priority expression.
	 *
	 * @return the priority expression
	 */
	public IExpression getPriorityExpression() {
		return _priority;
	}

	/**
	 * Gets the context expression.
	 *
	 * @return the context expression
	 */
	public IExpression getContextExpression() {
		return _when;
	}

	/**
	 * Gets the executed expression.
	 *
	 * @return the executed expression
	 */
	public IExpression getExecutedExpression() {
		return _executedwhen;
	}

	/**
	 * Gets the instantaneous expression.
	 *
	 * @return the instantaneous expression
	 */
	public IExpression getInstantaneousExpression() {
		return _instantaneous;
	}

	/**
	 * Gets the intention expression.
	 *
	 * @return the intention expression
	 */
	public IExpression getIntentionExpression() {
		return _intention;
	}

	/**
	 * Gets the emotion expression.
	 *
	 * @return the emotion expression
	 */
	public IExpression getEmotionExpression() {
		return _emotion;
	}

	/**
	 * Gets the threshold.
	 *
	 * @return the threshold
	 */
	public IExpression getThreshold() {
		return _threshold;
	}

	/**
	 * Instantiates a new simple bdi plan statement.
	 *
	 * @param desc the desc
	 */
	public SimpleBdiPlanStatement(final IDescription desc) {
		super(desc);
		_when = getFacet(IKeyword.WHEN);
		_priority = getFacet(SimpleBdiArchitecture.PRIORITY);
		_executedwhen = getFacet(SimpleBdiArchitecture.FINISHEDWHEN);
		_instantaneous = getFacet(SimpleBdiArchitecture.INSTANTANEAOUS);
		_intention = getFacet(SimpleBdiPlanStatement.INTENTION);
		_emotion = getFacet(SimpleBdiPlanStatement.EMOTION);
		_threshold = getFacet(SimpleBdiPlanStatement.THRESHOLD);
		setName(desc.getName());
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		if (_when == null || Cast.asBool(scope, _when.value(scope))) {
			return super.privateExecuteIn(scope);
		}
		return null;
	}

	/**
	 * Compute priority.
	 *
	 * @param scope the scope
	 * @return the double
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public Double computePriority(final IScope scope) throws GamaRuntimeException {
		return Cast.asFloat(scope, _priority.value(scope));
	}
}
