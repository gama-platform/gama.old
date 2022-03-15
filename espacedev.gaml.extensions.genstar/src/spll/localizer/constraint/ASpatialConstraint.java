package spll.localizer.constraint;

import java.util.Collection;

import core.metamodel.entity.AGeoEntity;
import core.metamodel.value.IValue;

/**
 * Abstract numerical representation of a spatial constraint: it deals with priority (int) 
 * among different constraints and relaxation of constraint rules
 * 
 * @author kevinchapuis
 *
 */
public abstract class ASpatialConstraint implements ISpatialConstraint {

	protected int priority = 1;
	protected double maxIncrease = 0.0;
	protected double increaseStep = 0.0;
	protected double currentValue = 0.0;
	protected int nbIncrements = 0;
	protected boolean constraintLimitReach;
	
	@Override
	public int getPriority() {
		return priority;
	}
	
	@Override
	public void relaxConstraint(Collection<AGeoEntity<? extends IValue>> nests) {
		if (currentValue < maxIncrease) {
			currentValue = Math.min(currentValue + increaseStep, maxIncrease);
			constraintLimitReach = false;
			nbIncrements++;
			relaxConstraintOp(nests);
		} else {
			constraintLimitReach = true;
		}
	}
	

	@Override
	public boolean isConstraintLimitReach() {
		return constraintLimitReach;
	}

	@Override
	public double getCurrentValue() {
		return currentValue;
	}
	
	/**
	 * Update the priority of the current constraint
	 * @param priority
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * The maximal number associated with the constraint
	 * @return
	 */
	public double getMaxIncrease() {
		return maxIncrease;
	}

	/**
	 * Set the maximum number for the constraint
	 * @param maxIncrease
	 */
	public void setMaxIncrease(double maxIncrease) {
		this.maxIncrease = maxIncrease;
	}

	/**
	 * The step of relaxation
	 * @return
	 */
	public double getIncreaseStep() {
		return increaseStep;
	}

	/**
	 * Set the relaxation step
	 * @param increaseStep
	 */
	public void setIncreaseStep(double increaseStep) {
		this.increaseStep = increaseStep;
	}
	
	/**
	 * Define how the relaxation should be made
	 * 
	 * @param nests
	 */
	public abstract void relaxConstraintOp(Collection<AGeoEntity<? extends IValue>> nests);
	
}
