package core.metamodel.entity.comparator.function;

import java.util.Comparator;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import core.configuration.jackson.entity.EntityComparatorSerializer;
import core.metamodel.value.IValue;
import core.util.data.GSEnumDataType;

/**
 * To implement one custom comparator function one must:
 * <p>
 * 1) Add String name variable starting with {@link #CUSTOM_TAG} and returned by method {@link #getName()}<br>
 * 2) Register the new class into as a sub type of this class: @JsonSubTypes.Type(value = the_new_class.class)<br>
 * 3) Must be initialized with default constructor and must not contains any class variables 
 * 
 * @author kevinchapuis
 *
 * @param <V>
 */
@JsonTypeInfo(
	      use = JsonTypeInfo.Id.NAME,
	      include = JsonTypeInfo.As.PROPERTY,
	      property = IComparatorFunction.TYPE_NAME
	      )
@JsonSubTypes({
	        @JsonSubTypes.Type(value = DefaultBooleanFunction.class),
	        @JsonSubTypes.Type(value = DefaultIntegerFunction.class),
	        @JsonSubTypes.Type(value = DefaultDoubleFunction.class),
	        @JsonSubTypes.Type(value = DefaultNominalFunction.class),
	        @JsonSubTypes.Type(value = DefaultOrderedValueFunction.class),
	        @JsonSubTypes.Type(value = DefaultRangeFunction.class)
	    })
@JsonSerialize(using = EntityComparatorSerializer.class)
public interface IComparatorFunction<V extends IValue> {

	public static final String DEFAULT_TAG = "DEFAULT - ";
	public static final String CUSTOM_TAG = "CUSTOM - ";
	
	public static final String TYPE_NAME = "TYPE NAME";
	
	public static final int EMPTY = 9999;
	
	/**
	 * Comparison function to implement: same behavior than in java {@link Comparator}
	 * BUT must not be consistent with equality
	 * 
	 * @param v1
	 * @param v2
	 * @param empty
	 * @return
	 */
	public int compare(IValue v1, IValue v2, IValue empty);

	/**
	 * Type of value to be handled
	 * @return
	 */
	public GSEnumDataType getType();
	
	@JsonProperty(TYPE_NAME)
	public String getName();
	
	default int testEmpty(IValue v1, IValue v2, IValue empty) {
		boolean emptyOne = v1.equals(empty);
		boolean emptyTwo = v2.equals(empty);
		return emptyOne && emptyTwo ? IComparatorFunction.EMPTY : 
			emptyOne && !emptyTwo ? 1 :
				!emptyOne && emptyTwo ? -1 : 0;
	}
	
	public static IComparatorFunction<? extends IValue> getDefaultFunction(GSEnumDataType type){
		switch (type) {
		case Boolean: return new DefaultBooleanFunction();
		case Integer: return new DefaultIntegerFunction();
		case Continue: return new DefaultDoubleFunction();
		case Nominal: return new DefaultNominalFunction();
		case Order: return new DefaultOrderedValueFunction();
		case Range: return new DefaultRangeFunction();
		default:
			throw new IllegalAccessError("Unkown data type "+type);
		}
	}
	
}
