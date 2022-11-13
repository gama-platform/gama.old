package core.metamodel.attribute;

import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import core.metamodel.attribute.emergent.IGSValueFunction;
import core.metamodel.attribute.emergent.filter.IGSEntitySelector;
import core.metamodel.entity.IEntity;
import core.metamodel.value.IValue;

/**
 * Attribute that can retrieve value based on emergent properties.
 * 
 * @see EmergentAttribute
 * 
 * @author kevinchapuis
 *
 * @param <V> the value type of the referent attribute
 * @param <U> The type of sub-entity to deal with (i.g. emergence and imergence of super-attribute)
 * @param <F> the type of predicate this attributes needs to make values emerge
 * 
 */
@JsonTypeName(EmergentAttribute.SELF_LABEL)
//@JsonSerialize(using = EmergentAttributeSerializer.class)
public class EmergentAttribute<V extends IValue, U, F> 
	extends Attribute<V> {

	public static final String SELF_LABEL = "EMERGENT ATTRIBUTE";
	public static final String FUNCTION_LABEL = "EMERGENT FUNCTION";
	public static final String TRANSPOSER_LABEL = "EMERGENT FILTER";
	
	@JsonProperty(FUNCTION_LABEL)
	private IGSValueFunction<U, V> function;
	@JsonProperty(TRANSPOSER_LABEL)
	private IGSEntitySelector<U, F> transposer;

	protected EmergentAttribute(String name) {
		super(name);
	}
	
	@Override
	public boolean isEmergent() {
		return true;
	}
	
	/**
	 * The main method that can retrieve the value of an attribute based on
	 * any child properties
	 * 
	 * @param entity
	 * @param transposer
	 * @return
	 */
	@JsonIgnore
	public V getEmergentValue(IEntity<? extends IAttribute<? extends IValue>> entity) {
		return function.apply(this.transposer.apply(entity));
	}
	
	/**
	 * The main method that gives a set of matching values from child entities
	 * @param value
	 * @return
	 */
	@JsonIgnore
	public Collection<Map<IAttribute<? extends IValue>,IValue>> getImeregentValues(V value){
		return function.reverse(this.transposer.reverse(this, value));
	}
		
	/**
	 * The main function that will look at sub-entities property to asses super entity attribute value 
	 * 
	 * @return
	 */
	@JsonProperty(FUNCTION_LABEL)
	public IGSValueFunction<U, V> getFunction(){
		return this.function;
	}
	
	/**
	 * Defines the main function to make attribute value emerge from sub entities properties
	 * 
	 * @param function
	 */
	@JsonProperty(FUNCTION_LABEL)
	public void setFunction(IGSValueFunction<U, V> function){
		this.function = function;
	}
	
	/**
	 * The function that will transpose the Entity linked to this attribute to 
	 * any U predicate that will be transpose to value V
	 * 
	 * @return
	 */
	@JsonProperty(TRANSPOSER_LABEL)
	public IGSEntitySelector<U, F> getTransposer(){
		return this.transposer;
	}
	
	/**
	 * The new transposer to be set
	 * 
	 * @param transposer
	 */
	@JsonProperty(TRANSPOSER_LABEL)
	public void setTransposer(IGSEntitySelector<U, F> transposer) {
		this.transposer = transposer;
	}

}
