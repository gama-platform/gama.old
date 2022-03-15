package core.util.data;

import java.util.HashMap;
import java.util.Map;

import core.metamodel.value.IValue;
import core.metamodel.value.binary.BooleanValue;
import core.metamodel.value.categoric.NominalValue;
import core.metamodel.value.categoric.OrderedValue;
import core.metamodel.value.numeric.ContinuousValue;
import core.metamodel.value.numeric.IntegerValue;
import core.metamodel.value.numeric.RangeValue;

/**
 * Define all possible value type that can define attribute instance value:
 * <p><ul>
 * <li> {@link GSEnumDataType#Continue} : double value - {@link Double}
 * <li> {@link GSEnumDataType#Integer} : integer value - {@link Integer}
 * <li> {@link GSEnumDataType#Range} : couple of numeric values - {@link Number}
 * <li> {@link GSEnumDataType#Boolean} : boolean value - {@link Boolean}
 * <li> {@link GSEnumDataType#Order} : ordered nominal value - {@link String}
 * <li> {@link GSEnumDataType#Nominal} : nominal value - {@link String}
 * </ul><p>
 * 
 * @author kevinchapuis
 * @author Vo Duc An
 *
 */
public enum GSEnumDataType {

	Continue (Double.class, ContinuousValue.class),
	Integer (Integer.class, IntegerValue.class),
	Range (Number.class, RangeValue.class),
	Boolean (Boolean.class, BooleanValue.class),
	Order (String.class, OrderedValue.class),
	Nominal (String.class, NominalValue.class);

	private Class<? extends IValue> wrapperClass;
	private Class<?> concretClass;
	
	private GSEnumDataType(Class<?> concretClass,
			Class<? extends IValue> wrapperClass){
		this.wrapperClass = wrapperClass;
		this.concretClass = concretClass;
	}
	
	/**
	 * Whether this {@link GSEnumDataType} is numerical or not
	 * 
	 * @return
	 */
	public boolean isNumericValue() {
		return concretClass.getSuperclass().equals(Number.class);
	}
	
	/**
	 * Return the inner type this data type encapsulate
	 * 
	 * @return
	 */
	public Class<?> getInnerType(){
		return concretClass;
	}
	
	/**
	 * Returns wrapper Gen* class
	 * 
	 * @see IValue
	 * 
	 * @return
	 */
	public Class<? extends IValue> getGenstarType(){
		return wrapperClass;
	}
	
	private static Map<Class<?>,GSEnumDataType> wrapperClass2enum;

	/**
	 * Returns a datatype for a IValue type
	 * @param clazz
	 * @return
	 */
	public static GSEnumDataType getType(Class<? extends IValue> clazz) {
		
		if (wrapperClass2enum == null) {
			wrapperClass2enum = new HashMap<>();
			for (GSEnumDataType dt: GSEnumDataType.values()) {
				wrapperClass2enum.put(dt.getGenstarType(), dt);
			}
		}
		
		GSEnumDataType res = wrapperClass2enum.get(clazz);
		if (res != null)
			return res;
		throw new IllegalArgumentException(clazz.getCanonicalName()+" is not linked to any "
				+GSEnumDataType.class.getCanonicalName());
		
	}
	
	private static Map<Class<?>,GSEnumDataType> concretClass2enum;

	public static GSEnumDataType getTypeForJavaType(Class<?> clazz) {
		
		if (concretClass2enum == null) {
			concretClass2enum = new HashMap<>();
			for (GSEnumDataType dt: GSEnumDataType.values()) {
				concretClass2enum.put(dt.getInnerType(), dt);
			}
		}
		
		// hardcoded :-(
		// Long can be decoded as Int !
		if (clazz.equals(Long.class))
			return GSEnumDataType.Integer;
		
		GSEnumDataType res = concretClass2enum.get(clazz);
		if (res != null)
			return res;
		throw new IllegalArgumentException(clazz.getCanonicalName()+" is not linked to any "
				+GSEnumDataType.class.getCanonicalName());
		
	}
	
	
}
