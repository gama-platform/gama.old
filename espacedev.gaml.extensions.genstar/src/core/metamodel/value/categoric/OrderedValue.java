package core.metamodel.value.categoric;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import core.metamodel.value.IValue;
import core.metamodel.value.IValueSpace;
import core.util.data.GSEnumDataType;

/**
 * Encapsulate a {@link String} value that is inherently ordered
 * <p>
 * Two {@link OrderedValue} can be ordered if, and only if, they pertain to the same {@link IValueSpace}. In fact,
 * two ordered value cannot be compared outside of a specific {@link OrderedSpace} using {@link OrderedSpace#compare(OrderedValue, OrderedValue)}
 * method
 * 
 * @author kevinchapuis
 *
 */
public class OrderedValue implements IValue {

	private String value;

	private Number order;

	@JsonManagedReference
	private OrderedSpace sv;
	
	protected OrderedValue(OrderedSpace sv, String value, Number order){
		this.sv =sv;
		this.value = value;
		this.order = order;
	}
	
	@Override
	public GSEnumDataType getType() {
		return GSEnumDataType.Order;
	}

	@JsonIgnore
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getActualValue() {
		return (T) this.value;
	}
	
	@Override
	public String getStringValue() {
		return value;
	}
	
	@Override
	public OrderedSpace getValueSpace() {
		return sv;
	}

	protected int compareTo(OrderedValue o) {
		return order.doubleValue() < o.getOrder().doubleValue() ? -1 : 
			order.doubleValue() > o.getOrder().doubleValue() ? 1 : 0;
	}

	protected void setOrder(int order){
		this.order = order;
	}
	
	public Number getOrder() {
		return order;
	}
	
	// ------------------------------------------------------ //

	/**
	 * {@inheritDoc}
	 * <p>
	 * WARNING: Does not take into account order
	 */
	@Override
	public int hashCode() {
		return this.getHashCode();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * WARNING: Does not take into account order
	 */
	@Override
	public boolean equals(Object obj) {
		return this.isEquals(obj);
	}
	
	@Override
	public String toString() {
		return this.getStringValue();
	}

	@Override
	public <T> void setActualValue(T v) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("cannot change the actual value of a Ordered attribute");
	}

	
	
}
