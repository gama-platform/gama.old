package spll;

import java.util.HashMap;
import java.util.Map;

import org.locationtech.jts.geom.Point;

import core.metamodel.entity.ADemoEntity;
import core.metamodel.entity.AGeoEntity;
import core.metamodel.value.IValue;

/**
 * Represents a spatialized entity which can have attributes, be located at a point (represents
 * where it is) and a Nest (representing where it "lives"). The linkedPlaces represents the list 
 * of places linked to the entity (key: type of place, value: corresponding place).
 * Such an entity might be generated.
 * 
 * @author Kevin Chapuis
 */
public class SpllEntity extends ADemoEntity {
	
	protected Point location = null;
	protected AGeoEntity<? extends IValue> nest = null;

	protected Map<String,AGeoEntity<? extends IValue>> linkedPlaces = null;
	

	public SpllEntity(ADemoEntity entity) {
		super(entity);
	}
	
	public SpllEntity(SpllEntity entity) {
		super(entity);
		this.location = entity.location;
		this.nest = entity.nest;
	}
	
	@Override
	public SpllEntity clone() {
		SpllEntity spe = new SpllEntity(this);
		spe.setLocation(this.location);
		spe.setNest(this.nest);
		return spe;
	}
	
	/**
	 * Retrieve the location of the agent as a point
	 * 
	 * @return a point of type {@link Point}
	 */
	public Point getLocation() {
		return location;
	}

	/**
	 * Change the location of the entity
	 * 
	 * @param location
	 */
	public void setLocation(Point location){
		this.location = location;
	}


	/**
	 * Retrieve the most significant enclosing geographical entity this
	 * entity is situated. It represents 'home's entity 
	 * 
	 * @return
	 */
	public AGeoEntity<? extends IValue> getNest(){
		return nest;
	}
	
	/**
	 * Change the nest of the entity
	 * 
	 * @param entity
	 */
	public void setNest(AGeoEntity<? extends IValue> nest){
		this.nest = nest;
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, AGeoEntity<? extends IValue>> getLinkedPlaces() {
		return linkedPlaces;
	}
	
	/**
	 * 
	 * @param key
	 * @param place
	 */
	public void addLinkedPlaces(String key, AGeoEntity<? extends IValue> place) {
		if (linkedPlaces == null) linkedPlaces = new HashMap<String, AGeoEntity<? extends IValue>>();
		linkedPlaces.put(key, place);
	}
	
}
