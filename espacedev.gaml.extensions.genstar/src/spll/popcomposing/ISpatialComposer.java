package spll.popcomposing;

import core.metamodel.IMultitypePopulation;
import core.metamodel.attribute.Attribute;
import core.metamodel.value.IValue;
import spll.SpllEntity;
import spll.SpllPopulation;

/**
 * A ISpatialMatcher is an algorithm which takes two populations of entities as an inputs,
 * and returns a {@link IMultitypePopulation} as a result in which the entities have parents
 * and children based on spatiality (for instance, two entities which intersect might become
 * parent and child).
 * 
 * @author Samuel Thiriot
 *
 */
public interface ISpatialComposer<E extends SpllEntity> {


	/**
	 * Add a new spatial constraint to this localizer
	 * 
	 * @see ISpatialConstraint
	 * 
	 * @param constraint
	 */
	//public boolean addConstraint(ISpatialConstraint constraint);
	
	/**
	 * Set the constraint all in a row
	 * 
	 * @param constraints
	 */
	//public void setConstraints(List<ISpatialConstraint> constraints);
	
	/**
	 * Returns all set constraints
	 * 
	 * @return
	 */
	//public List<ISpatialConstraint> getConstraints();
	
	public void setPopulationOfParentCandidates(SpllPopulation populationOfParentCandidates, String parentType);
	public SpllPopulation getPopulationOfParentCandidates();

	public void setPopulationOfChildrenCandidates(SpllPopulation populationOfChildrenCandidates, String childrenType);
	public SpllPopulation getPopulationOfChildrenCandidates();

	public void matchParentsAndChildren();
	
	public int getCountEmptyParents();
	public int getCountOrphanChildren();
	
	public IMultitypePopulation<E, Attribute<? extends IValue>> getMatchedPopulation();
	
	public void clearMatchedPopulation();
	
}
