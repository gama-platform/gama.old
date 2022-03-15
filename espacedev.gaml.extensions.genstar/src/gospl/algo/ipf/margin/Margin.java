package gospl.algo.ipf.margin;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import core.metamodel.attribute.Attribute;
import core.metamodel.value.IValue;
import gospl.algo.ipf.AGosplIPF;
import gospl.distribution.matrix.control.AControl;

/**
 * Represent a marginal descriptor for a dimension of an abstract {@link AGosplIPF}
 * <p>
 * <ul>
 *  <li> Stores marginal descriptors binding control to seed coordinate
 *  <li> Stores marginal control, i.e. control total associated to a specific control marginal descriptor
 * </ul>
 * <p>
 * 
 * @author kevinchapuis
 *
 */
public class Margin<T extends Number> implements IMargin<Attribute<? extends IValue>, IValue, T> {

	private Attribute<? extends IValue> controlAttribute;
	private Attribute<? extends IValue> seedAttribute;
	
	protected Map<MarginDescriptor, AControl<T>> marginalControl;
	
	/*
	 * protected constructor to unsure safe initialization
	 */
	protected Margin(Attribute<? extends IValue> controlAttribute, 
			Attribute<? extends IValue> seedAttribute){
		this.controlAttribute = controlAttribute;
		this.seedAttribute = seedAttribute;
		this.marginalControl = new HashMap<>();
	}
	
	@Override
	public Collection<AControl<T>> getControls(){
		return Collections.unmodifiableCollection(marginalControl.values());
	}
	
	@Override
	public AControl<T> getControl(MarginDescriptor descriptor) {
		return marginalControl.get(descriptor);
	}
	
	@Override
	public Collection<MarginDescriptor> getMarginDescriptors() {
		return marginalControl.keySet();
	}
	
	@Override
	public Attribute<? extends IValue> getControlDimension() {
		return controlAttribute;
	}
	
	@Override
	public Attribute<? extends IValue> getSeedDimension() {
		return seedAttribute;
	}

	@Override
	public int size() {
		return marginalControl.size();
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName()+" "
				+controlAttribute.getAttributeName() +
				(controlAttribute.equals(seedAttribute) ? "" :
				" ("+seedAttribute.getAttributeName()+")");
	}
	
	// --------------------------
	
	/*
	 * Protected setter to unsure safe construction
	 */
	protected void addMargin(MarginDescriptor marginDescriptor, AControl<T> control){
		marginalControl.put(marginDescriptor, control);
	}

}
