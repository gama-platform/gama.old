package core.metamodel;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import core.metamodel.attribute.IAttribute;
import core.metamodel.entity.IEntity;
import core.metamodel.value.IValue;

public interface IQueryablePopulation<E extends IEntity<A>, A extends IAttribute<? extends IValue>> 
					extends IPopulation<E, A> {

	public E getEntityForId(String id);
	
	public Iterator<E> getEntitiesForIds(String ... ids);
	
	/**
	 * Returns the count of entities which have for this attribute 
	 * one of the given values (OR)
	 * @param attribute
	 * @param values
	 * @return
	 */
	public int getCountHavingValues(A attribute, IValue ... values);
	
	/**
	 * Returns the count of entities which have one of these values for these attributes
	 * (a AND clause of OR clause)
	 * @param attribute2values
	 * @return
	 */
	public int getCountHavingValues(Map<A,Collection<IValue>> attribute2values);
	
	public int getCountHavingCoordinate(Map<A,IValue> attribute2value);

	public Iterator<E> getEntitiesHavingValues(A attribute, IValue ... values);

	public Iterator<E> getEntitiesHavingValues(Map<A,Collection<IValue>> attribute2values);

}
