package spll.localizer.distribution;

import java.util.List;

import core.metamodel.attribute.Attribute;
import core.metamodel.entity.AGeoEntity;
import core.metamodel.entity.IEntity;
import core.metamodel.value.IValue;

/**
 * Define the higher order concept to define and to draw a spatial entity from a discret distribution of spatial candidates
 * 
 * @author kevinchapuis
 *
 */
public interface ISpatialDistribution<E extends IEntity<Attribute<? extends IValue>>> {

	/**
	 * Draw a spatial entity from a list of candidates, given that it will be link to the provided population entity 
	 * @param entity
	 * @param candidates
	 * @return
	 */
	public AGeoEntity<? extends IValue> getCandidate(E entity, List<? extends AGeoEntity<? extends IValue>> candidates);
	
	/**
	 * Draw a spatial entity from a pre-determined set of candidate to be bind with given population entity
	 * @param entity
	 * @return
	 */
	public AGeoEntity<? extends IValue> getCandidate(E entity);
	
	/**
	 * 
	 * @param candidates
	 */
	public void setCandidate(List<? extends AGeoEntity<? extends IValue>> candidates);
	
	/**
	 * 
	 * @return
	 */
	public List<? extends AGeoEntity<? extends IValue>> getCandidates();

}
