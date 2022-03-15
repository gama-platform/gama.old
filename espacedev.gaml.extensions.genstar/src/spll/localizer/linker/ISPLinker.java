package spll.localizer.linker;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import core.metamodel.attribute.Attribute;
import core.metamodel.entity.AGeoEntity;
import core.metamodel.entity.IEntity;
import core.metamodel.value.IValue;
import spll.localizer.constraint.ISpatialConstraint;
import spll.localizer.distribution.ISpatialDistribution;

/**
 * Encapsulate the process that binds an entity of a population to a spatial
 * entity
 * <p>
 * Basically made of a spatial distribution that define probability of each
 * spatial entity candidate to be chosen to be bind with the entity, and spatial
 * constraints that will filter candidates to only keep acceptable ones
 * 
 * @author kevinchapuis
 *
 * @param <SD>
 */
public interface ISPLinker<E extends IEntity<Attribute<? extends IValue>>> {

	public enum ConstraintsReleaseRule {
		PRIORITY, LINEAR;
	}
	
	/**
	 * Main method to link an entity to one candidate draw from a collection. There is two aspect involve:
	 * <ul>
	 *  <li> a {@link ISpatialDistribution} to draw spatial entities from
	 *  <li> a collection of {@link ISpatialConstraint} to prior filter candidates spatial entities
	 * </ul>
	 * 
	 * @param entity
	 * @param candidates
	 * @return
	 */
	public Optional<AGeoEntity<? extends IValue>> getCandidate(E entity,
			Collection<? extends AGeoEntity<? extends IValue>> candidates);
	
	/**
	 * Main method to link a set of synthetic entities with a chosen spatial entity candidate drawn from a collection.
	 * This involves the same element as in {@link #getCandidate(IEntity, Collection)} except that filtering is made once
	 * 
	 * @param entity
	 * @param candidates
	 * @return
	 */
	public Map<E, Optional<AGeoEntity<? extends IValue>>>  getCandidates(Collection<E> entities,
			Collection<? extends AGeoEntity<? extends IValue>> candidates);

	/**
	 * Set the distribution to be used to sort linked places
	 * 
	 * @param distribution
	 */
	public void setDistribution(ISpatialDistribution<E> distribution);
	
	/**
	 * The distribution to be used
	 * 
	 * @return {@link ISpatialDistribution} the spatial distribution used to draw a candidate
	 */
	public ISpatialDistribution<E> getDistribution();

	/**
	 * Filter the collection of given candidate to fit {@link ISPLinker} constraints requirement without releasing them
	 * 
	 * @param candidates
	 * @return the collection of fitting spatial entities that are acceptable candidates
	 */
	public Collection<AGeoEntity<? extends IValue>> filter(
			Collection<? extends AGeoEntity<? extends IValue>> candidates);
	
	/**
	 * Filter the collection of given candidate with constraint release
	 * 
	 * @param candidates
	 * @return the collection of fitting spatial entities that are acceptable candidates
	 */
	public Collection<AGeoEntity<? extends IValue>> filterWithRelease(
			Collection<? extends AGeoEntity<? extends IValue>> candidates);
	
	/**
	 * Set a new list of constraints
	 * 
	 * @param constraints
	 */
	public void setConstraints(List<ISpatialConstraint> constraints);
	
	/**
	 * Add constraints to filter candidates with
	 * 
	 * @param constraints
	 */
	public void addConstraints(ISpatialConstraint... constraints);

	/**
	 * Get back the collection of constaints
	 * 
	 * @return {@link List} The list of spatial constraints to filter candidates for a link
	 */
	public List<ISpatialConstraint> getConstraints();
	
	/**
	 * The rule to release constraints
	 * 
	 * @return {@link ConstraintsReleaseRule}
	 */
	public ConstraintsReleaseRule getConstraintsReleaseRule();

	/**
	 * Set the rule to be used to release constraints
	 * 
	 * @param rule
	 */
	public void setConstraintsReleaseRule(ConstraintsReleaseRule rule);
	
}
