package core.metamodel;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import core.metamodel.attribute.IAttribute;
import core.metamodel.entity.IEntity;
import core.metamodel.value.IValue;

/**
 * A population which can contain several different entity types 
 * (such as Households and Individuals).
 * 
 * It can be used as one unique population; in this case the differences 
 * between the agents inside the population will not be visible.
 * 
 * @author Samuel Thiriot
 *
 * @param <E>
 * @param <A>
 * @param <V>
 */
public interface IMultitypePopulation<E extends IEntity<A>, A extends IAttribute<? extends IValue>> 
								extends IPopulation<E,A> {

	/**
	 * Returns the set of the types of agents which might be stored into this 
	 * populations
	 * @return
	 */
	public Set<String> getEntityTypes();
	
	public int getEntityLevel(String type);
	
	/**
	 * Returns the set of the types of agents which might be stored into this 
	 * populations
	 * @return
	 */
	public List<Integer> getEntityLevel();
	
	public String getEntityType(int level);
	
	/**
	 * Adds the entity type if it is not declared already.
	 * @param novelType
	 */
	public void addEntityType(String novelType);
	
	/**
	 * Returns as a IPopulation the subpopulation of agents of the given type.
	 * @param entityType
	 * @return
	 */
	public IPopulation<E,A> getSubPopulation(String entityType);
	
	/**
	 * Returns as a IPopulation the subpopulation of agents of the given level.
	 * @param entityType
	 * @return
	 */
	public IPopulation<E,A> getSubPopulation(int entityLevel);
	
	/**
	 * Returns an Iterator on the given subpopulation 
	 * @param entityType
	 * @return
	 */
	public Iterator<E> iterateSubPopulation(String entityType);
	
	/** 
	 * Adds an agent of a given type, and sets this type for this agent
	 * @param type
	 * @param e
	 * @return
	 */
	public boolean add(String type, E e);
	
	/** 
	 * Adds agents of a given type, and sets this type for this agent
	 * @param type
	 * @param e
	 * @return
	 */
	public boolean addAll(String type, Collection<? extends E> c);
	
	/**
	 * Clears the agents of a given type
	 * @param type
	 */
	public void clear(String type);
	
}
